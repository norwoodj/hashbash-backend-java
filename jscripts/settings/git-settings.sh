#!/usr/bin/env bash

[[ -n "${_GIT_SETTINGS_SH:+_}" ]] && return || readonly _GIT_SETTINGS_SH=1

function get_master_branch_name {
    echo "master"
}

function get_stable_branch_name {
    echo "stable"
}

function get_release_branch_name {
    local release_version=${1}
    echo "release/${release_version}"
}

function get_release_tag_for_version {
    local release_version=${1}
    echo "${release_version}"
}

function get_remote_to_pull_from {
    echo "origin"
}

function get_remotes_to_push_to {
    echo "origin rpi"
}
