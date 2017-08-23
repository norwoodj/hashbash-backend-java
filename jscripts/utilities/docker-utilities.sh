#!/usr/bin/env bash

source ${SCRIPT_DIR}/settings/docker-settings.sh
source ${SCRIPT_DIR}/utilities/job-utilities.sh

##
# Docker Utility Functions
##
function get_image_tag_without_repository {
    local image=${1}
    local version=${2}
    local full_image_name=$(get_image_name ${image})
    echo "${full_image_name}:${version}"
}

function get_image_tag {
    local image=${1}
    local version=${2}
    local image_tag_without_repository=$(get_image_tag_without_repository ${image} ${version})
    local docker_url=$([[ -n "${DOCKER_REGISTRY_URL:+_}" ]] && echo "${DOCKER_REGISTRY_URL}/")
    local docker_org=$([[ -n "${DOCKER_REGISTRY_ORG:+_}" ]] && echo "${DOCKER_REGISTRY_ORG}/")

    echo "${docker_url}${docker_org}${image_tag_without_repository}"
}

function get_current_image_tag {
    local image=${1}
    get_image_tag_without_repository ${image} ${LOCAL_IMAGE_VERSION_TAG}
}

function image_tag_exists {
    local image_tag=${1}
    docker history -q ${image_tag} &> /dev/null
}

function check_current_image_exists {
    local image=${1}
    local image_tag=$(get_current_image_tag ${image})
    log_debug "Ensuring '${image_tag}' image exists locally"

    if ! image_tag_exists ${image_tag}; then
        log_error "Image ${image_tag} doesn't exist, build first: './jscripts/build-images.sh ${image}'"
        exit 1
    fi
}

function build_image {
    local image=${1}
    local image_tag=$(get_current_image_tag ${image})
    log_info "Building '${image}' docker image, using tag '${image_tag}'"

    local dockerfile_path=$(get_dockerfile_path_for_image ${image})
    local docker_build_context_path=$(get_docker_build_context_path_for_image ${image})

    docker build \
        -t ${image_tag} \
        -f ${dockerfile_path} \
        ${ADDITIONAL_DOCKER_BUILD_ARGS} \
        ${docker_build_context_path}
}

function build_image_with_hooks {
    local image=${1}
    local image_dependencies=${2:-''}

    # At this point all of the images we depend on must have been built, so check to ensure that's the case
    for i in ${image_dependencies}; do
        check_current_image_exists ${i}
    done

    pre_build_image_hook ${image}
    build_image ${image}
    post_build_image_hook ${image}
}

function _job_name_for_build_image {
    local image=${1}
    echo "build_image_${image}"
}

function build_image_with_hooks_job {
    local image=${1}
    local image_dependencies=$(get_image_dependencies_for_image "${image}")

    await_dependencies_for_and_submit_job \
        $(_job_name_for_build_image "${image}") \
        build_image_with_hooks \
        <(echo "${image} ${image_dependencies}") \
        'true' \
        <(map _job_name_for_build_image <(echo "${image_dependencies}"))
}

function build_image_if_not_exists {
    local image_name=${1}
    local image_tag=$(get_current_image_tag ${image_name})

    log_debug "Ensuring ${image_tag} image exists locally, building if not"
    if ! image_tag_exists ${image_tag}; then
        build_image ${image_name}
    fi
}

function deploy_image {
    local image=${1}
    local version=$(get_image_version ${image})
    local image_tag=$(get_image_tag ${image} ${version})

    log_info "Deploying ${image_tag} image to $(get_docker_registry_name)"
    check_current_image_exists ${image}

    log_debug "Tagging $(get_current_image_tag ${image}) as ${image_tag}"
    docker tag $(get_current_image_tag ${image}) ${image_tag}

    log_debug "Pushing ${image_tag}"
    docker push ${image_tag}

    if [[ ${DOCKER_PUSH_KEEP_TAGGED} != 'true' ]]; then
        log_debug "Removing repository image tag ${image_tag}"
        docker rmi ${image_tag}
    else
        log_debug "DOCKER_PUSH_KEEP_TAGGED='true' keeping repository image tag ${image_tag}"
    fi
}

function deploy_image_with_hooks {
    local image=${1}

    pre_deploy_image_hook ${image}
    deploy_image ${image}
    post_deploy_image_hook ${image}
}

function _job_name_for_deploy_image {
    local image=${1}
    echo "build_image_${image}"
}

function deploy_image_with_hooks_job {
    local image=${1}
    local image_dependencies=$(get_image_dependencies_for_image "${image}")
    local image_job_dependencies

    await_dependencies_for_and_submit_job \
        $(_job_name_for_deploy_image "${image}") \
        deploy_image_with_hooks \
        <(echo "${image}") \
        'true' \
        <(map _job_name_for_deploy_image <(echo ${image_dependencies}))
}
