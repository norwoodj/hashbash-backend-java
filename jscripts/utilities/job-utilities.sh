#!/usr/bin/env bash

[[ -n "${_JOB_UTILITIES_SH:+_}" ]] && return || readonly _JOB_UTILITIES_SH=1

source ${SCRIPT_DIR}/utilities/common.sh
source ${SCRIPT_DIR}/utilities/logging-utilities.sh

: ${JOBS_IN_PARALLEL:='true'}
declare -A _JOB_NAME_TO_PID=()


function handle_job_options {
    local flag=${1}

    case "${flag}" in
        --no-parallel | -n) JOBS_IN_PARALLEL='false' ;;
        -*)                 return 1 ;;
    esac
}

function print_job_options_usage_list {
    echo "  --no-parallel, -n            Run all jobs in serial, whether they specify to be run in parallel or not"
}

function _get_pid_for_job_name {
    local job_name=${1}
    echo ${_JOB_NAME_TO_PID[${job_name}]:-''}
}

function _set_pid_for_job_name {
    local job_name=${1}
    local pid=${2}

    _JOB_NAME_TO_PID[${job_name}]=${pid}
}

function _set_job_completed {
    local job_name=${1}
    unset _JOB_NAME_TO_PID[${job_name}]
}

function _get_oldest_job {
    for j in ${!_JOB_NAME_TO_PID[@]}; do
        echo "${j}"
        return
    done
}

function wait_for_job {
    local job=${1}
    local exit_on_dep_fail=${2:-'false'}

    if job_is_completed "${job}"; then
        return
    elif ! wait $(_get_pid_for_job_name "${job}"); then
        _set_job_completed "${job}"

        if [[ ${exit_on_dep_fail} != 'false' ]]; then
            log_error "A job was waiting on job '${job}', which failed, aborting execution..."
            return 1
        else
            log_warn "A job was waiting on job '${job}', which failed, exit on dependency fail is false, so continuing..."
        fi
    else
        _set_job_completed "${job}"
    fi
}

function _await_all_jobs {
    local job_name=${1}
    local wait_file=${2:-/dev/null}
    local exit_on_dep_fail=${3:-'true'}
    local wait_jobs=$(cat ${wait_file})

    if [[ -n "${wait_jobs:+x}" ]]; then
        local csv_pids=$(join ", " `map _get_pid_for_job_name <(echo ${wait_jobs})`)
        log_debug "Job '${job_name}' waiting on job completion of ($(join ", " ${wait_jobs})), pids: (${csv_pids})"

        for w in ${wait_jobs}; do
            wait_for_job "${w}" "${exit_on_dep_fail}"
        done

        log_debug "Dependencies for job '${job_name}' ($(join ", " ${wait_jobs})), pids: (${csv_pids}) COMPLETED, starting..."
    else
        log_debug "No Dependencies for job '${job_name}', starting..."
    fi
}

function _wait_if_at_job_capacity {
    local job_name=${1}
    local max_job_processes=${2}

    while [[ $(get_num_running_jobs) -ge ${max_job_processes} ]]; do
        local oldest_job=$(_get_oldest_job)
        log_debug "Currently $(get_num_running_jobs) jobs running, >= allowed maximum of ${max_job_processes} waiting for job '${oldest_job}' to complete before running '${job_name}'"
        wait_for_job "${oldest_job}"
    done
}

function _run_job_in_background {
    local job_name=${1}
    local job_callback=${2}
    local job_args_file=${3}
    local max_job_processes=${4}

    _wait_if_at_job_capacity "${job_name}" "${max_job_processes}"

    log_debug "Starting job '${job_name}' as a background process"

    ${job_callback} $(cat "${job_args_file}") &

    local job_pid=${!}
    _set_pid_for_job_name "${job_name}" "${job_pid}"
}

function _submit_job {
    local job_name=${1}
    local job_callback=${2}
    local job_args_file=${3:-/dev/null}
    local background=${4:-'false'}
    local max_job_processes=${5:-'16'}

    if [[ "${background}" == 'true' && "${JOBS_IN_PARALLEL}" == 'true' ]]; then
        _run_job_in_background \
            "${job_name}" \
            "${job_callback}" \
            "${job_args_file}" \
            "${max_job_processes}"
    else
        log_debug "Starting job '${job_name}' in this process"
        "${job_callback}" $(cat "${job_args_file}")
    fi
}

function job_is_completed {
    local job_name=${1}
    local job_pid=$(_get_pid_for_job_name ${job_name})
    [[ -z "${job_pid}" ]]
}

function get_num_running_jobs {
    echo "${#_JOB_NAME_TO_PID[@]}"
}

function await_all_currently_running_jobs {
    _await_all_jobs \
        'WAIT_ALL' \
        <(for s in ${!_JOB_NAME_TO_PID[@]}; do echo ${s}; done) \
        'false'
}

function await_dependencies_for_and_submit_job {
    local job_name=${1}
    local job_callback=${2}
    local job_args_file=${3:-/dev/null}
    local background=${4:-'false'}
    local wait_file=${5:-/dev/null}
    local exit_on_dep_fail=${6:-'true'}

    _await_all_jobs \
        "${job_name}" \
        "${wait_file}" \
        "${exit_on_dep_fail}"

    _submit_job \
        "${job_name}" \
        "${job_callback}" \
        "${job_args_file}" \
        "${background}"
}
