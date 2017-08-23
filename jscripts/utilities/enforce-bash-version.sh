#!/usr/bin/env bash

[[ -n "${_ENFORCE_BASH_VERSION_UTILITIES_SH:+_}" ]] && return || readonly _ENFORCE_BASH_VERSION_UTILITIES_SH=1


function enforce_bash_version {
    local expected_version=${1}
    local version=$((${BASH_VERSION%%[^0-9]*}))

    if [ $version -lt ${expected_version} ]; then
        echo -e "\e[31mHost machine is currently running an unsupported version of bash: ${BASH_VERSION}. The minimum required version is: ${expected_version}"
        exit 1
    fi
}

enforce_bash_version 4
