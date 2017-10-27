#!/usr/bin/env bash

[[ -n "${_DOCKER_SETTINGS_SH:+_}" ]] && return || readonly _DOCKER_SETTINGS_SH=1

source ${SCRIPT_DIR}/settings/common.sh
source ${SCRIPT_DIR}/utilities/rpi-utilities.sh
source ${SCRIPT_DIR}/utilities/version-file-utilities.sh
source ${SCRIPT_DIR}/utilities/job-utilities.sh

##
# Environment variables that control how images are named/deployed/built etc.
##
: ${LOCAL_IMAGE_VERSION_TAG:="current"}
: ${DOCKER_REGISTRY_URL:=""}
: ${DOCKER_REGISTRY_ORG:="jnorwood"}
: ${ADDITIONAL_DOCKER_BUILD_ARGS:=""}
: ${ADDITIONAL_DOCKER_PUSH_ARGS:=""}
: ${DOCKER_PUSH_KEEP_TAGGED:="false"}


##
# List here all of the images built/deployed/used by the project
##
readonly _RPI_MAVEN_BUILD_IMAGE="rpi_maven_build"
readonly _X86_MAVEN_BUILD_IMAGE="maven_build"
readonly _RPI_HASHBASH_IMAGE="rpi_server"
readonly _X86_HASHBASH_IMAGE="server"
readonly _RPI_HASHBASH_CONSUMERS_IMAGE="rpi_consumers"
readonly _X86_HASHBASH_CONSUMERS_IMAGE="consumers"
readonly _RPI_NGINX_IMAGE="rpi_nginx"
readonly _X86_NGINX_IMAGE="nginx"

readonly MAVEN_BUILD_IMAGE=$(is_running_on_raspberry_pi && echo "${_RPI_MAVEN_BUILD_IMAGE}" || echo "${_X86_MAVEN_BUILD_IMAGE}")
readonly HASHBASH_IMAGE=$(is_running_on_raspberry_pi && echo "${_RPI_HASHBASH_IMAGE}" || echo "${_X86_HASHBASH_IMAGE}")
readonly HASHBASH_CONSUMERS_IMAGE=$(is_running_on_raspberry_pi && echo "${_RPI_HASHBASH_CONSUMERS_IMAGE}" || echo "${_X86_HASHBASH_CONSUMERS_IMAGE}")
readonly NGINX_IMAGE=$(is_running_on_raspberry_pi && echo "${_RPI_NGINX_IMAGE}" || echo "${_X86_NGINX_IMAGE}")
readonly UTILITIES_IMAGE="utilities"
readonly WEBPACK_BUILDER_IMAGE="webpack_builder"

readonly _DOCKER_CONFIG=$(cat <<EOF
{
    "buildImages": [
        "${MAVEN_BUILD_IMAGE}",
        "${NGINX_IMAGE}",
        $(is_running_on_raspberry_pi || echo "\"${UTILITIES_IMAGE}\",")
        $(is_running_on_raspberry_pi || echo "\"${WEBPACK_BUILDER_IMAGE}\",")
        "${HASHBASH_IMAGE}",
        "${HASHBASH_CONSUMERS_IMAGE}"
    ],
    "deployImages": [
        "${HASHBASH_IMAGE}",
        "${HASHBASH_CONSUMERS_IMAGE}",
        "${NGINX_IMAGE}"
    ],
    "imageDependencies": {
        "${HASHBASH_IMAGE}": ["${MAVEN_BUILD_IMAGE}"],
        "${HASHBASH_CONSUMERS_IMAGE}": ["${MAVEN_BUILD_IMAGE}"]
    }
}
EOF
)


##
# Command line setup
##
function print_additional_options_usage_list {
    print_job_options_usage_list
}

function handle_additional_options {
    use_additional_options_helpers <(echo 'handle_job_options') ${@}
}


##
# Settings for which images should be built/deployed by these scripts, as well as how to build and deploy them
##
function print_build_images_usage_list {
    bulleted_list <(get_images_to_build)
}

function print_deploy_images_usage_list {
    bulleted_list <(get_images_to_deploy)
}

function get_images_to_build {
    jq -r ".buildImages[]" <<< "${_DOCKER_CONFIG}"
}

function get_images_to_deploy {
    jq -r ".deployImages[]" <<< "${_DOCKER_CONFIG}"
}

function get_image_dependencies_for_image {
    local image=${1}
    jq -r ".imageDependencies.${image} | if . == null then \"\" else .[] end" <<< "${_DOCKER_CONFIG}"
}

function get_image_name {
    local image=${1}
    echo "${PROJECT_NAME}-${image}"
}

function get_dockerfile_path_for_image {
    local image=${1}
    local folder_name=$(is_running_on_raspberry_pi && echo "rpi" || echo "x86")
    echo "docker/${folder_name}/Dockerfile-${image}"
}

function get_docker_build_context_path_for_image {
    local image=${1}
    echo "."
}

function get_additional_docker_build_args {
    local image=${1}

    if [[ "${image}" == "${HASHBASH_IMAGE}" || "${image}" == "${HASHBASH_CONSUMERS_IMAGE}" ]]; then
        echo "--build-arg VERSION=$(get_image_version "${image}")"
    fi
}

function _get_maven_version {
    grep -A2 "<groupId>com.johnmalcolmnorwood.hashbash</groupId>" server/pom.xml \
        | grep "version" \
        | sed "s|.*<version>\(.*\)</version>|\1|"
}

function get_image_version {
    local image=${1}
    _get_maven_version
 }

function get_docker_registry_name {
    if [[ -z "${DOCKER_REGISTRY_URL:+_}" ]]; then
        echo "DockerHub"
    else
        echo "${DOCKER_REGISTRY_URL:+_}"
    fi
}

##
# Hooks around building and deploying images
##
function pre_build_image_hook {
    local image=${1}
    log_debug "Pre Build Image Hook for image ${image}"

    if [[ "${image}" == "${NGINX_IMAGE}" ]]; then
        generate_version_info_json > "web/src/_version.json"
    fi
}

function post_build_image_hook {
    local image=${1}
    log_debug "Post Build Image Hook for image ${image}"
}

function pre_deploy_image_hook {
    local image=${1}
    log_debug "Pre Deploy Image Hook for image ${image}"
}

function post_deploy_image_hook {
    local image=${1}
    log_debug "Post Deploy Image Hook for image ${image}"
}
