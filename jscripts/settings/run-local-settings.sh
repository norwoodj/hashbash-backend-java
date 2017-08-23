#!/usr/bin/env bash

[[ -n "${_RUN_LOCAL_SETTINGS_SH:+_}" ]] && return || readonly _RUN_LOCAL_SETTINGS_SH=1

source ${SCRIPT_DIR}/settings/application-settings.sh
source ${SCRIPT_DIR}/utilities/command-line-utilities.sh
source ${SCRIPT_DIR}/utilities/docker-utilities.sh
source ${SCRIPT_DIR}/utilities/rpi-utilities.sh

##
# Run Local Configuration
##
: ${DAEMON:='false'}


function print_run_local_applications_usage_list {
    print_app_usage_list
}

function get_apps_to_run_locally {
    get_app_list
}

function print_additional_options_usage_list {
    echo "  --daemon, -d                 Run the service as daemon"
}

function handle_additional_options {
    local option=${1}

    case ${option} in
        -d | --daemon) DAEMON='true' ;;
        -*)            return 1 ;;
    esac
}

function get_local_docker_compose_path_for_app {
    local app=${1}
    local folder_name=$(is_running_on_raspberry_pi && echo "rpi" || echo "x86")
    echo "docker/${folder_name}/docker-compose.yaml"
}


##
# Hooks
##
function pre_run_local_hook {
    local app=${1}
    log_debug "Pre Run Local hook for application '${app}'"
}

function post_run_local_hook {
    local app=${1}
    log_debug "Pre Run Local hook for application '${app}'"
}
