#!/usr/bin/env bash

[[ -n "${_COMMON_SETTINGS_SH:+_}" ]] && return || readonly _COMMON_SETTINGS_SH=1

source ${SCRIPT_DIR}/utilities/logging-utilities.sh

##
# The name of the project these scripts are installed to. This name is used in a number of places throughout these scripts,
# for instance in the naming of docker images. By default they are prefixed with the Project name
##
readonly PROJECT_NAME="hashbash"


##
# Various other environment variables that control output levels and other important aspects of scripts" execution
##
: ${IMAGE_DEPS:=""}
: ${LOG_LEVEL:=INFO}


##
# Script options handling and usage information
##
function print_default_options_usage_list {
    echo "  --help, -h                   Print this usage and exit"
    echo "  --log-level, -l              Set the logging level to one of (NONE, ERROR, WARNING, INFO, DEBUG) (Default: INFO)"
    echo "  --trace, -x                  Trace command execution (Default: false)"
    echo "  --pull-deps, -p              Pull Docker image dependencies (Default: false)"
}

function handle_default_options {
    local option=${1}

    case "${option}" in
        -h | --help)      usage && exit 0 ;;
        -p | --pull-deps) for i in ${IMAGE_DEPS}; do docker pull "${i}"; done ;;
        -x | --trace)     set -x ;;
        -l | --log-level) LOG_LEVEL=${2}; set_log_level "${LOG_LEVEL}" && return 2 ;;
        -*)               return 1 ;;
    esac
}

function _assert_installed {
    local command=${1}

    if ! hash "${command}" &> /dev/null; then
        log_error "${command} is required to run these scripts, please install."
        exit 1
    fi
}

##
# Hooks for before and after options are handled, useful for setting up bash modes and checking that things are installed
##
function pre_handle_options_hook {
    set_log_level "${LOG_LEVEL}"
    _assert_installed "docker"
    _assert_installed "jq"
}

function post_handle_options_hook {
    set -euo pipefail # http://redsymbol.net/articles/unofficial-bash-strict-mode/
}
