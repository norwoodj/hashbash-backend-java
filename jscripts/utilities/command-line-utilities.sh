#!/usr/bin/env bash

[[ -n "${_COMMAND_LINE_UTILITIES_SH:+_}" ]] && return || readonly _COMMAND_LINE_UTILITIES_SH=1

source ${SCRIPT_DIR}/utilities/logging-utilities.sh


function invalid_flag {
    log_error "Invalid flag '${1}'!"
    usage
    exit 1
}

function print_additional_options_usage_list {
    :
}

function handle_additional_options {
    return 1
}

function print_options_usage_list {
    print_default_options_usage_list
    print_additional_options_usage_list
}

function handle_option_flag {
    local option=${1}

    for handler in handle_default_options handle_additional_options; do
        "${handler}" ${@}

        local result=${?}

        if [[ "${result}" != 1 ]]; then
            return ${result}
        fi
    done

    invalid_flag ${option}
}

function use_additional_options_helpers {
    local options_helpers_file=${1}
    local options_helpers=$(cat ${options_helpers_file})
    local options_args=${@:2}

    for o in ${options_helpers}; do
        "${o}" ${options_args}

        local result=${?}

        if [[ "${result}" != 1 ]]; then
            return "${result}"
        fi
    done

    return 1
}

function handle_options_and_pass_arguments_to_main {
    pre_handle_options_hook

    while [[ ${1:-''} == -* ]]; do
        handle_option_flag ${@}

        if [[ ${?} == 2 ]]; then
            shift
        fi

        shift
    done

    post_handle_options_hook
    main ${@}
}

function check_named_argument {
    local argument_name=${1}
    local argument=${2}
    local possible_argument_file=${3}

    if [[ -z ${argument} ]]; then
        log_error "'${argument_name}' must be provided!"
        usage
        exit 1
    fi

    if ! is_element_of ${argument} <(cat ${possible_argument_file}); then
        log_error "Invalid ${argument_name} '${argument}' provided!"
        usage
        exit 1
    fi
}

function check_named_multi_argument {
    local argument_name=${1}

    local argument_file=${2}
    local arguments=$(cat ${argument_file})

    local possible_argument_file=${3}
    local possible_arguments=$(cat ${possible_argument_file})

    if [[ -z "${arguments}" ]]; then
        log_error "'${argument_name}' must be provided!"
        usage
        exit 1
    fi

    for a in ${arguments}; do
        if ! is_element_of ${a}  <(echo ${possible_arguments}); then
            log_error "Invalid ${argument_name} '${a}' provided!"
            usage
            exit 1
        fi
    done
}
