#!/bin/bash -e

#
## Configuration settings for the project, setting up the name of the project, the locations of various important files,
## and the process used to version and build the various docker images the project builds and uses
#

# The name of the project. This value will prefix all built docker image names generated so that an image named 'image'
# will end up having a tag of ${PROJECT_NAME}-image:version
PROJECT_NAME='hashbash'
LOCAL_IMAGE_VERSION_TAG='current'

# Docker deploy configuration. If the DOCKER_REGISTRY_URL is empty, will be pushed to docker hub. The user is the
# organization that the image will be pushed to, so the full push url will be DOCKER_REGISTRY_URL/DOCKER_REGISTRY_ORG/image:tag
DOCKER_REGISTRY_URL=
DOCKER_REGISTRY_ORG='jnorwood'


#
## Script Usage Configuration
#
function print_build_usage_images_list {
    echo "  all      Build all assets and docker images needed to run the application"
    echo "  web      Build the hashbash web server image"
    echo "  rpi_web  Build the hashbash web server image for raspberry pi"
}

function print_deploy_usage_images_list {
    echo "  all      Deploy all assets and docker images needed to run the application"
    echo "  web      Deploy the hashbash web server image"
    echo "  rpi_web  Deploy the hashbash web server image for raspberry pi"
}


function print_run_local_usage_services_list {
    echo "  hasbash  Run the hashbash web server"
}

#
## Build Configuration
#

# List of Docker Images that this project builds
HASHBASH_WEB_IMAGE_NAME='web'
HASHBASH_RPI_WEB_IMAGE_NAME='rpi_web'


function get_images_for_build {
    printf "${HASHBASH_WEB_IMAGE_NAME} ${HASHBASH_RPI_WEB_IMAGE_NAME}"
}

function get_images_for_deploy {
    printf ${HASHBASH_RPI_WEB_IMAGE_NAME}
}

function get_services_for_deploy {
    printf ${PROJECT_NAME}
}

function get_images_for_service {
    local service=${1}
    if is_running_on_raspberry_pi; then
        printf ${HASHBASH_RPI_WEB_IMAGE_NAME}
    else
        printf ${HASHBASH_WEB_IMAGE_NAME}
    fi
}

function get_dockerfile_path_for_image {
    local image=${1}
    printf "docker/Dockerfile-${image}"
}

function get_docker_build_context_path_for_image {
    local image=${1}
    printf '.'
}

function get_docker_build_args_for_image {
    local image=${1}
    printf "VERSION=`get_maven_version`"
}

function get_local_docker_compose_path_for_service {
    local service=${1}

    if is_running_on_raspberry_pi; then
        printf "docker/docker-compose-rpi.yaml"
    else
        printf "docker/docker-compose.yaml"
    fi
}

function get_image_version {
    get_maven_version | sed 's|SNAPSHOT|dev|'
}


#
## Hooks
#

function pre_build_hook {
    local image_name=${1}
    log_block "Pre build hook for image ${image_name}"
}

function post_build_hook {
    local image_name=${1}
    log_block "Post build hook for image ${image_name}"
}

function pre_run_local_hook {
    local service=${1}
    log_block "Pre run local hook for service ${service}"
}

function post_run_local_hook {
    local service=${1}
    log_block "Post run local hook for service ${service}"
}

#
## Implementations
#

function get_maven_version {
    local name=${1}
    grep -A2 '<groupId>com.johnmalcolmnorwood.hashbash</groupId>' pom.xml \
        | grep 'version' \
        | sed 's|.*<version>\(.*\)</version>|\1|'
}

function is_running_on_raspberry_pi {
    uname -a | grep 'arm' &> /dev/null
}
