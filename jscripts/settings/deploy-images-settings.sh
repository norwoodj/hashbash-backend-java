#!/usr/bin/env bash

[[ -n "${_BUILD_IMAGES_SETTINGS_SH:+_}" ]] && return || readonly _BUILD_IMAGES_SETTINGS_SH=1

source ${SCRIPT_DIR}/utilities/command-line-utilities.sh
source ${SCRIPT_DIR}/utilities/docker-utilities.sh

: ${KEEP_REMOTE_IMAGE_TAGS:="false"}

##
# Command line setup
##
function print_deploy_images_options_usage_list {
    echo "  --keep-remote-image-tags -k  Keep the remote tags for pushed docker images locally, rather than deleting them"
}

function handle_deploy_images_options {
    local option=${1}

    case "${option}" in
        --keep-remote-image-tags | -k) KEEP_REMOTE_IMAGE_TAGS="true" ;;
        -*)                            return 1 ;;
    esac
}

function print_additional_options_usage_list {
    print_deploy_images_options_usage_list
    print_job_options_usage_list
}

function handle_additional_options {
    use_additional_options_helpers <(echo handle_deploy_images_options handle_job_options) ${@}
}
