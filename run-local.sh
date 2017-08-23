#!/bin/bash -e

source common.sh


function usage {
    echo "${0} <command> <service> [ options ]"
    echo
    echo "This script can be used to start, stop, and list running instances of the services built by this project, on"
    echo "a local machine"
    echo
    echo "Commands:"
    echo "  start  Run the local server for development"
    echo "  stop   Kill the local development server, stops and removes the containers if they're running in"
    echo "  ps     See which containers are currently running"
    echo "  logs   Show the logs of the running containers"
    echo
    echo "Services:"
    print_run_local_usage_services_list
    echo
    echo "Options:"
    echo "  --help, -h    Print this usage and exit"
    echo "  --daemon, -d  Starts the local server as a daemon"
}

function run_docker_compose_for_service {
    local command=${1}
    local service=${2}
    local daemon=${3}

    local docker_compose_file=`get_local_docker_compose_path_for_service ${service}`
    log_line "Running docker-compose command ${command} for ${service}"

    docker-compose \
        -p "${PROJECT_NAME}-${service}" \
        -f ${docker_compose_file} \
        ${command} \
        `[[ ${command} = 'up' && ${daemon} = 'true' ]] && echo '-d'`
}

function start {
    local service=${1}
    shift

    local daemon='false'

    while [[ ${1} == -* ]]; do
        case ${1} in
            --daemon | -d)
                daemon='true'
            ;;
            -h | --help)
                usage
                exit 0
            ;;
            -*)
                log_block "Invalid flag '${1}'!"
                echo
                usage
                exit 1
            ;;
        esac
        shift
    done

    log_block "Starting '${service}' server..."

    log_line "Ensuring all images exist to run service ${service}"
    images=`get_images_for_service ${service}`
    for i in ${images}; do
        check_current_image_exists ${i}
    done

    pre_run_local_hook ${service}
    run_docker_compose_for_service 'up' ${service} ${daemon}
    post_run_local_hook ${service}
}

function stop {
    local service=${1}

    log_block "Stopping '${service}' server..."
    run_docker_compose_for_service 'down' ${service}
}

function ps {
    local service=${1}

    log_block 'Listing running servers...'
    run_docker_compose_for_service 'ps' ${service}
}

function logs {
    local service=${1}

    log_block "Retrieving logs for service: ${service}..."
    run_docker_compose_for_service 'logs' ${service}
}

function main {
    while [[ ${1} == -* ]]; do
        case ${1} in
            -h | --help)
                usage
                exit 0
            ;;
            -*)
                log_block "Invalid flag '${1}'!"
                echo
                usage
                exit 1
            ;;
        esac
        shift
    done

    local command=${1}
    check_command_argument "${command}" 'start' 'stop' 'ps' 'logs'
    shift

    local service=${1}
    check_service_argument ${service}
    shift

    ${command} ${service} ${@}
}


main ${@}
