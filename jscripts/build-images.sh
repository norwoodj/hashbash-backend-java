#!/usr/bin/env bash

SCRIPT_DIR="$(cd `dirname ${BASH_SOURCE[0]}` && pwd -P)"
source ${SCRIPT_DIR}/settings/build-images-settings.sh


function usage {
    echo "Usage:"
    echo "  ${BASH_SOURCE[0]} [options] <image> [<image> [<image>]]"
    echo
    echo "This script is used to build the various docker images needed to run the services from this project"
    echo
    echo "Options:"
    print_options_usage_list
    echo
    echo "Images:"
    print_build_images_usage_list
}

function main {
    local images=${@}
    check_named_multi_argument 'image' <(echo ${images}) <(echo "all $(get_images_to_build)")

    for i in ${images}; do
        if [[ "${i}" = 'all' ]]; then
            local images=$(get_images_to_build)
            break
        fi
    done

    log_info "Building images: ($(join ', ' ${images}))"
    for i in ${images}; do
        ADDITIONAL_DOCKER_BUILD_ARGS=$(get_additional_docker_build_args_for_image "${i}") build_image_with_hooks_job "${i}"
    done

    await_all_currently_running_jobs
}

handle_options_and_pass_arguments_to_main ${@}
