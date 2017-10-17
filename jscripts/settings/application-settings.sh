#!/usr/bin/env bash

[[ -n "${_APPLICATION_SETTINGS_SH:+_}" ]] && return || readonly _APPLICATION_SETTINGS_SH=1

source ${SCRIPT_DIR}/settings/docker-settings.sh

##
# This is where you should define the names of every application that is built by this project, an application being some
# collection of docker containers that should run as an atomic unit. These names will be used in scripts throughout this
# project, for instance in the "run-locally.sh" script, where you will specify one of these applications to be run
##
readonly HASHBASH_APP_NAME="${PROJECT_NAME}"
readonly HASHBASH_DEPENDENCIES="${PROJECT_NAME}-deps"

readonly _APPLICATION_CONFIG=$(cat <<EOF
{
    "applications": ["${HASHBASH_APP_NAME}", "${HASHBASH_DEPENDENCIES}"],
    "imagesToRunApp": {
        "${HASHBASH_APP_NAME}": ["${HASHBASH_IMAGE}", "${NGINX_IMAGE}", "${WEBPACK_BUILDER_IMAGE}"],
        "${HASHBASH_DEPENDENCIES}": ["${NGINX_IMAGE}", "${WEBPACK_BUILDER_IMAGE}"]
    }
}
EOF
)


function get_app_list {
    jq -r ".applications[]" <<< "${_APPLICATION_CONFIG}"
}

function print_app_usage_list {
    bulleted_list <(get_app_list)
}

function get_images_necessary_to_run_app {
    local app=${1}
    jq -r ".imagesToRunApp[\"${app}\"][]" <<< "${_APPLICATION_CONFIG}"
}

function get_current_application_version {
    _get_maven_version
}
