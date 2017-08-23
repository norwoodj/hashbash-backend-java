#!/usr/bin/env bash

SCRIPT_DIR="$(cd `dirname ${BASH_SOURCE[0]}` && pwd -P)"
source ${SCRIPT_DIR}/settings/docker-settings.sh
source ${SCRIPT_DIR}/utilities/command-line-utilities.sh
source ${SCRIPT_DIR}/utilities/docker-utilities.sh
source ${SCRIPT_DIR}/utilities/job-utilities.sh


function usage {
    echo "Usage:"
    echo "  ${BASH_SOURCE[0]} [options] <image> [<image> [<image>]]"
    echo
    echo "This script is used to deploy the docker images needed to run the services contained in this repository in a"
    echo "deployed environment"
    echo
    echo "Options:"
    print_options_usage_list
    echo
    echo "Images:"
    print_deploy_images_usage_list
}

function main {
    local images=${@}
    check_named_multi_argument 'image' <(echo ${images}) <(echo "all $(get_images_to_deploy)")

    for i in ${images}; do
        if [[ ${i} = 'all' ]]; then
            local images=$(get_images_to_deploy)
            break
        fi
    done

    log_info "Deploying images: ($(join ', ' ${images}))"
    for i in ${images}; do
        deploy_image_with_hooks_job "${i}"
    done

    await_all_currently_running_jobs
}

handle_options_and_pass_arguments_to_main ${@}
