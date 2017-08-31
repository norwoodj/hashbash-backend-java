#!/usr/bin/env bash

: ${JSCRIPTS_PATH:="$(cd && pwd)/.jscripts"}
: ${JSCRIPTS_LOCAL_PATH:="$(pwd -P)/jscripts"}


function usage {
    echo "Usage:"
    echo "  ${BASH_SOURCE[0]} <command>"
    echo
    echo "This script is used to bootstrap the jscripts, as well as updating its version, etc"
    echo
    echo "Commands:"
    echo "  install - Install the jscripts to the provided project"
}

function jsc_install {
    local jscripts_script_path="${JSCRIPTS_PATH}/jscripts"

    if [[ ! -d "${jscripts_script_path}" ]]; then
        echo "'${jscripts_script_path}' directory doesn't exist! Clone jscripts to that location first, or specify your path with JSCRIPTS_PATH=asdf ${BASH_SOURCE[0]} ..."
    fi

    mkdir -p "${JSCRIPTS_LOCAL_PATH}"

    echo "Linking scripts..."
    for s in ${jscripts_script_path}/*.sh; do
        echo "Linking ${s} -> ${JSCRIPTS_LOCAL_PATH}/$(basename ${s})"
        ln -sf "${s}" "${JSCRIPTS_LOCAL_PATH}/$(basename ${s})"
    done

    mkdir -p "${JSCRIPTS_LOCAL_PATH}/utilities"
    echo "Linking utilities..."

    for s in ${jscripts_script_path}/utilities/*.sh; do
        echo "Linking ${s} -> ${JSCRIPTS_LOCAL_PATH}/$(basename ${s})"
        ln -sf "${s}" "${JSCRIPTS_LOCAL_PATH}/utilities/$(basename ${s})"
    done

    if [[ ! -d "${JSCRIPTS_LOCAL_PATH}/settings" ]]; then
        echo "Copying base settings files over..."
        cp -R "${jscripts_script_path}/settings" "${JSCRIPTS_LOCAL_PATH}/settings"
    else
        echo "Settings directory already exists, not copying settings"
    fi

    for pattern in 'jscripts/*.sh' 'jscripts/utilities/*.sh'; do
        if ! grep -F "${pattern}" .gitignore &> /dev/null; then
            echo "${pattern}" >> .gitignore
        fi
    done
}

function main {
    local command=${1}

    if [[ -z "${command}" ]]; then
        usage
        exit 1
    fi

    "jsc_${command}"
}

main ${@}
