#!/usr/bin/env bash

[[ -n "${_RELEASE_SETTINGS_SH:+_}" ]] && return || readonly _RELEASE_SETTINGS_SH=1

source ${SCRIPT_DIR}/settings/application-settings.sh
source ${SCRIPT_DIR}/utilities/command-line-utilities.sh
source ${SCRIPT_DIR}/utilities/git-utilities.sh


function handle_release_options {
    local flag=${1}

    case "${flag}" in
        --hotfix | -f)   HOTFIX_NUMBER=${2}; return 2 ;;
        --version | -v)  OVERRIDE_VERSION=${2}; return 2 ;;
        -*)              return 1 ;;
    esac
}

function print_release_options_usage_list {
    echo "  --hotfix, -f <number>        Supply the hotfix number for this release"
    echo "  --version, -v <version>      Supply the complete version, overriding the default"
}

function print_additional_options_usage_list {
    print_release_options_usage_list
    print_git_options_usage_list
}

function handle_additional_options {
    use_additional_options_helpers <(echo "handle_release_options handle_git_options") ${@}
}

function get_release_version {
    local release_version=$(date -u "+%y.%m%d")

    if [[ -n "${OVERRIDE_VERSION:+_}" ]]; then
        echo "${OVERRIDE_VERSION}"
    elif [[ -n "${HOTFIX_NUMBER:+_}" ]]; then
        echo "${release_version}.${HOTFIX_NUMBER}"
    else
        echo "${release_version}"
    fi
}

function get_next_dev_version {
    local release_version=$(date -u -v+1d "+%y.%m%d")
    echo "${release_version}-dev"
}

function get_versioned_files {
    echo charts/hashbash/Chart.yaml server/pom.xml server/*/pom.xml
}

function get_release_version_commit_message {
    local current_dev_version=${1}
    local release_version=${2}

    echo "[RELEASE] Bumping versions to release version ${release_version}"
}

function get_dev_version_commit_message {
    local release_version=${1}
    local next_dev_version=${2}

    echo "[RELEASE] Bumping versions to next development version ${next_dev_version}"
}


##
# Hooks
##
function pre_cut_release_branch_hook {
    local release_version=${1}
    log_debug "Pre cut release branch hook for release version ${release_version}"
}

function post_cut_release_branch_hook {
    local release_version=${1}
    log_debug "Post cut release branch hook for release version ${release_version}"
}

function pre_commit_release_changes_hook {
    local release_version=${1}
    log_debug "Pre commit release changes hook for release version ${release_version}"
}

function post_commit_release_changes_hook {
    local release_version=${1}
    log_debug "Post commit release changes hook for release version ${release_version}"
}

function pre_commit_dev_changes_hook {
    local next_dev_version=${1}
    log_debug "Pre commit dev version changes hook for next development version ${next_dev_version}"
}

function post_commit_dev_changes_hook {
    local next_dev_version=${1}
    log_debug "Post commit dev version changes hook for next development version ${next_dev_version}"
}

function pre_merge_release_branch_hook {
    local release_version=${1}
    log_debug "Pre merge release branch hook for release version ${release_version}"
}

function post_merge_release_branch_hook {
    local release_version=${1}
    log_debug "Post merge release branch hook for release version ${release_version}"
}

function pre_rebase_stable_branch_hook {
    local release_version=${1}
    log_debug "Pre rebase stable branch hook for release version ${release_version}"
}

function post_rebase_stable_branch_hook {
    local release_version=${1}
    log_debug "Post rebase stable branch hook for release version ${release_version}"
}
