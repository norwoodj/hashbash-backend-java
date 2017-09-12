#!/usr/bin/env bash

[[ -n "${_APPLICATION_SETTINGS_SH:+_}" ]] && return || readonly _APPLICATION_SETTINGS_SH=1

##
# This is where you should define the names of every application that is built by this project, an application being some
# collection of docker containers that should run as an atomic unit. These names will be used in scripts throughout this
# project, for instance in the 'run-locally.sh' script, where you will specify one of these applications to be run
##
readonly HASHBASH_APP_NAME="${PROJECT_NAME}"

readonly _APPLICATION_CONFIG=$(cat <<EOF
{
    "applications": ["${HASHBASH_APP_NAME}"],
    "imagesToRunApp": {
        "${HASHBASH_APP_NAME}": ["${WEB_IMAGE_NAME}"]
    }
}
EOF
)


function get_app_list {
    jq -r ".applications[]" <<< "${_APPLICATION_CONFIG}"
}

function print_app_usage_list {
    echo "  ${HASHBASH_APP_NAME}  Hashbash application"
}

function get_images_necessary_to_run_app {
    local app=${1}
    jq -r ".imagesToRunApp.${app}[]" <<< "${_APPLICATION_CONFIG}"
}

function _get_maven_version {
    grep -A2 '<groupId>com.johnmalcolmnorwood.hashbash</groupId>' pom.xml \
        | grep 'version' \
        | sed 's|.*<version>\(.*\)</version>|\1|'
}

function get_current_application_version {
    _get_maven_version
}
