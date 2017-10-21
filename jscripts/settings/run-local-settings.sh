#!/usr/bin/env bash

[[ -n "${_RUN_LOCAL_SETTINGS_SH:+_}" ]] && return || readonly _RUN_LOCAL_SETTINGS_SH=1

source ${SCRIPT_DIR}/settings/application-settings.sh
source ${SCRIPT_DIR}/utilities/command-line-utilities.sh
source ${SCRIPT_DIR}/utilities/docker-utilities.sh

##
# Run Local Configuration
##
: ${DAEMON:="false"}
: ${NO_EXTRA_HOST:="false"}


function print_run_local_applications_usage_list {
    print_app_usage_list
}

function get_applications_to_run_locally {
    get_app_list
}

function print_additional_options_usage_list {
    echo "  --daemon, -d                 Run the service as daemon"
    echo "  --no-extra-host, -n          Don't add the extra host argument that enables nginx to pass requests to locally running hashbash"
}

function handle_additional_options {
    local option=${1}

    case ${option} in
        -d | --daemon)        DAEMON="true" ;;
        -n | --no-extra-host) NO_EXTRA_HOST="true" ;;
        -*)                   return 1 ;;
    esac
}

function get_local_docker_compose_path_for_app {
    local app=${1}
    local folder_name=$(is_running_on_raspberry_pi && echo "rpi" || echo "x86")
    echo "docker/${folder_name}/docker-compose-${app}.yaml"
}


##
# Hooks
##

# This queries your various network interfaces to find one that has LAN IP assigned, as our docker containers can reach
# localhost on that interface, but not through the 127.0.0.1 interface
function _find_local_network_private_ip_address {
    for i in {0..15}; do
        local host_ip_address=$(ifconfig "en${i}" 2> /dev/null | grep "inet " | cut -f2 -d' ')

        if [[ -n "${host_ip_address}" ]]; then
            echo "${host_ip_address}"
            return
        fi
    done
}

function pre_run_local_hook {
    local app=${1}
    log_debug "Pre Run Local hook for application ${app}"

    # Only care about assigning the extra IP for localhost if we're running the dependencies image and we need the
    # nginx docker container to be able to reach our java server running on localhost
    if [[ "${app}" != "${HASHBASH_DEPENDENCIES}" ]]; then
        return
    fi

    if [[ "${NO_EXTRA_HOST}" == "true" ]]; then
        return
    fi

    local hashbash_host_ip_address=$(_find_local_network_private_ip_address)

    if [[ -z "${hashbash_host_ip_address}" ]]; then
        log_error "No local IP address found, cannot start nginx dependency"
        exit 1
    fi

    log_debug "Exporting HASHBASH_HOST_IP_ADDRESS=${hashbash_host_ip_address}"
    export HASHBASH_HOST_IP_ADDRESS=${hashbash_host_ip_address}
}

function post_run_local_hook {
    local app=${1}
    log_debug "Pre Run Local hook for application ${app}"
}
