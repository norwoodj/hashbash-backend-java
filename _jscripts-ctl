#!/usr/bin/env bash

: ${JSCRIPTS_PATH:="$(cd && pwd)/.jscripts"}
: ${JSCRIPTS_LOCAL_PATH:="$(pwd -P)/jscripts"}


function usage {
    echo "Usage:"
    echo "  ${BASH_SOURCE[0]} <command>"
    echo
    echo "This script is used to bootstrap the jscripts, as well as updating its version, etc"
    echo
    echo "Options:"
    echo "  --help, -h               Print this usage and exit"
    echo "  --jscripts, -j <path>    The path to the jscripts installation on disk (Default: '~/.jscripts')"
    echo
    echo "Commands:"
    echo "  install - Install the jscripts to the provided project"
    echo "  version - Print the version of the currently configured jscripts"
}

function jsc_version {
    if [[ ! -f "${JSCRIPTS_PATH}/version.txt" ]]; then
        echo "No version.txt file found at configured jscripts path '${JSCRIPTS_PATH}'"
        return 1
    fi

    cat "${JSCRIPTS_PATH}/version.txt"
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

    echo "Copying README over"
    cp "${JSCRIPTS_PATH}/README.md" "${JSCRIPTS_LOCAL_PATH}"
}

function main {
    local command=${1}

    if [[ -z "${command}" ]]; then
        usage
        exit 1
    fi

    "jsc_${command}"
}

function handle_options_and_pass_arguments_to_main {
    while [[ "${1}" == -* ]]; do
        case "${1}" in
            --help | -h)     usage; exit ;;
            --jscripts | -j) JSCRIPTS_PATH=${2}; shift ;;
            -*)              echo "Invalid Option '${1}'"; exit 1;;
        esac

        shift
    done

    main ${@}
}

handle_options_and_pass_arguments_to_main ${@}
