#!/bin/bash -e

source common.sh


function usage {
    echo "${0} [ options ] <commands>"
    echo
    echo "This script is used to deploy various pieces used to run this service in a deployed environment. The commands"
    echo "available are listed below, but they include deploying docker images to a remote registry, deploying an application"
    echo "bundle to a remote file storage service, and deploying a remote application bundle to a server to run the application."
    echo
    echo "Commands:"
    echo "  images <image> [<image> [<image> ...]]  Pushes built image to the configured docker registry"
    echo
    echo "Images:"
    print_deploy_usage_images_list
    echo
    echo "Options:"
    echo "  --help, -h  Print this usage and exit"
}

function get_docker_registry_name {
    if [[ ! -z ${DOCKER_REGISTRY_URL} ]]; then
        printf ${DOCKER_REGISTRY_URL}
    else
        printf "Docker Hub"
    fi
}

function do_deploy_image {
    local image=${1}
    local version=`get_image_version ${image}`
    local image_tag=`get_image_tag ${image} ${version}`

    log_block "Deploying ${image_tag} image to `get_docker_registry_name`"
    check_current_image_exists ${image}

    log_line "Tagging `get_current_image_tag ${image}` as ${image_tag}"
    docker tag `get_current_image_tag ${image}` ${image_tag}

    log_line "Pushing ${image_tag}"
    docker push ${image_tag}
}

function images {
    local images=${@}
    check_images_argument 'get_images_for_deploy' ${images}

    if [[ ${1} = 'all' ]]; then
        images=`get_images_for_deploy`

        log_block 'Deploying all images:'
        for i in ${images}; do
            log_line "${i}"
        done

        for i in ${images}; do
            do_deploy_image ${i}
        done
    else
        for i in ${images}; do
            if [[ ${i} != 'all' ]]; then
                do_deploy_image ${i}
            fi
        done
    fi
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
    check_command_argument "${command}" 'images'

    shift
    ${command} ${@}
}

main ${@}
