#!/usr/bin/env bash

[[ -n "${_BUILD_IMAGES_SETTINGS_SH:+_}" ]] && return || readonly _BUILD_IMAGES_SETTINGS_SH=1

source ${SCRIPT_DIR}/utilities/command-line-utilities.sh
source ${SCRIPT_DIR}/utilities/docker-utilities.sh

##
# Command line setup
##
function print_additional_options_usage_list {
    print_job_options_usage_list
}

function handle_additional_options {
    use_additional_options_helpers <(echo handle_job_options) ${@}
}
