#!/usr/bin/env bash

SCRIPT_DIR="$(cd `dirname ${BASH_SOURCE[0]}` && pwd -P)"
source ${SCRIPT_DIR}/settings/run-local-settings.sh


function usage {
    echo "Usage:"
    echo "  ${BASH_SOURCE[0]} [options] <command> <app>"
    echo
    echo "This script can be used to start, stop, and list running instances of the apps built by this project, on"
    echo "a local machine"
    echo
    echo "Options:"
    print_options_usage_list
    echo
    echo "Commands:"
    echo "  start  Run the local server for development"
    echo "  stop   Kill the local development server, stops and removes the containers if they're running in"
    echo "  ps     See which containers are currently running"
    echo "  logs   Show the logs of the running containers"
    echo
    echo "Applications:"
    print_run_local_applications_usage_list
}

function _run_docker_compose_for_app {
    local command=${1}
    local app=${2}

    local docker_compose_file=`get_local_docker_compose_path_for_app ${app}`
    log_info "Running docker-compose command ${command} for ${app}"

    docker-compose \
        -p "${PROJECT_NAME}-${app}" \
        -f ${docker_compose_file} \
        ${command} \
        `[[ ${command} = 'up' && ${DAEMON} = 'true' ]] && echo '-d'`
}

function run_local_start {
    local app=${1}
    log_info "Starting '${app}' server..."

    log_info "Ensuring all images exist to run app ${app}"
    local images=$(get_images_necessary_to_run_app "${app}")
    for i in ${images}; do
        check_current_image_exists "${i}"
    done

    pre_run_local_hook "${app}"
    _run_docker_compose_for_app 'up' ${app}
    post_run_local_hook "${app}"
}

function run_local_stop {
    local app=${1}

    log_info "Stopping '${app}' server..."
    _run_docker_compose_for_app 'down' ${app}
}

function run_local_ps {
    local app=${1}

    log_info 'Listing running servers...'
    _run_docker_compose_for_app 'ps' ${app}
}

function run_local_logs {
    local app=${1}

    log_info "Retrieving logs for app: ${app}..."
    _run_docker_compose_for_app 'logs' ${app}
}

function main {
    local command=${1:-''}
    check_named_argument 'command' "${command}" <(echo 'start stop ps logs')

    local app=${2:-''}
    check_named_argument 'app' "${app}" <(get_apps_to_run_locally)

    "run_local_${command}" "${app}"
}

handle_options_and_pass_arguments_to_main ${@}
