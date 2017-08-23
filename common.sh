#!/bin/bash -e

#
## Common utilities used by all the tooling scripts
#

source project-settings.sh

#
## Logging Utilities
#

function log_block {
    echo
    echo "==> ${@}"
}

function log_line {
    echo "  + ${@}"
}

function log_border {
    echo '======================================================================'
}


#
## Docker utilities
#

function image_tag_exists {
    local image_tag=${1}
    docker history -q ${image_tag} &> /dev/null
}

function check_current_image_exists {
    local image=${1}
    local image_tag=`get_current_image_tag ${image}`
    log_line "Ensuring '${image_tag}' image exists locally"

    if ! image_tag_exists ${image_tag}; then
        log_line "Image ${image_tag} doesn't exist, build first: './build.sh ${image}'"
        exit 1
    fi
}

function build_image_if_not_exists {
    local image_name=${1}
    local image_tag=`get_current_image_tag ${image_name}`

    set +e
    log_line "Ensuring ${image_tag} image exists locally, building if not"
    if ! image_tag_exists ${image_tag}; then
        set -e
        build_image ${image_name}
    else
        set -e
    fi
}

function get_image_name {
    local image_name=${1}
    printf "${PROJECT_NAME}-${image_name}"
}

function get_image_tag_without_repository {
    local image_name=${1}
    local version=${2}
    local full_image_name=`get_image_name ${image_name}`
    printf "${full_image_name}:${version}"
}

function get_image_tag {
    local image_name=${1}
    local version=${2}
    local image_tag_without_repository=`get_image_tag_without_repository ${image_name} ${version}`

    if [ ! -z ${DOCKER_REGISTRY_URL} ]; then
        printf "${DOCKER_REGISTRY_URL}/"
    fi

    if [[ ! -z ${DOCKER_REGISTRY_ORG} ]]; then
        printf "${DOCKER_REGISTRY_ORG}/"
    fi

    printf ${image_tag_without_repository}
}

function get_current_image_tag {
    local image_name=${1}
    get_image_tag_without_repository ${image_name} ${LOCAL_IMAGE_VERSION_TAG}
}


function build_image {
    local image_name=${1}

    log_block "Building ${image_name} image"

    local image_tag=`get_current_image_tag ${image_name}`
    log_line "Building '${image_name}' docker image, using tag '${image_tag}'"

    local dockerfile_path=`get_dockerfile_path_for_image ${image_name}`
    local docker_build_context_path=`get_docker_build_context_path_for_image ${image_name}`
    local docker_build_args=`get_docker_build_args_for_image ${image_name}`

    docker build \
        -t ${image_tag} \
        -f ${dockerfile_path} \
        `for b in ${docker_build_args}; do echo "--build-arg ${b}"; done` \
        ${docker_build_context_path}
}

function build_image_with_hooks {
    local image_name=${1}

    pre_build_hook ${image_name}
    build_image ${image_name}
    post_build_hook ${image_name}
}


#
## Various other utilities
#

function prompt_yes_or_no {
    local prompt=${1}
    while true; do
        read -r -n 1 -p "${prompt} [y/n]: " REPLY
        case $REPLY in
            [yY]) echo ; return 0 ;;
            [nN]) echo ; return 1 ;;
            *) printf " \033[31m %s \n\033[0m" "invalid input"
        esac
    done
}

function join {
    local delimiter=${1}
    shift

    if [[ ! -z ${1} ]]; then
        printf ${1}
        shift
    fi

    for arg in ${@}; do
        printf "${delimiter}${arg}"
    done
}

function check_argument_in {
    local argument=${1}
    shift

    local pattern=`join '|' ${@}`
    [[ ${argument} =~ ${pattern} ]]
}


#
## Argument checkers
#

function check_environment_argument {
    local environment=${1}

    if [[ -z ${environment} ]]; then
        log_block 'Environment must be provided!'
        echo
        usage
        exit 1
    fi

    if ! check_argument_in ${environment} `get_deploy_environments`; then
        log_block "Invalid environment '${environment}' provided!"
        echo
        usage
        exit 1
    fi
}

function check_images_argument {
    local image_list_function=${1}
    shift
    local images=${@}

    if [[ -z ${images} ]]; then
        log_block 'No images provided!'
        echo
        usage
        exit 1
    fi

    for i in ${images}; do
        if ! check_argument_in ${i} 'all' `${image_list_function}`; then
            log_block "Invalid image '${i}'!"
            echo
            usage
            exit 1
        fi
    done
}

function check_service_argument {
    local service=${1}

    if [[ -z ${service} ]]; then
        log_block 'No service provided!'
        echo
        usage
        exit 1
    fi

    if ! check_argument_in ${service} `get_services_for_deploy`; then
        log_block "Invalid service '${service}'!"
        echo
        usage
        exit 1
    fi
}

function check_command_argument {
    local command=${1}
    shift

    local possible_commands=${@}

    if [[ -z ${command} ]]; then
        log_block 'No command provided!'
        echo
        usage
        exit 1
    fi

    if ! check_argument_in ${command} ${possible_commands}; then
        log_block "Invalid command '${command}'!"
        echo
        usage
        exit 1
    fi
}
