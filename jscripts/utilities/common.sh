#!/usr/bin/env bash

[[ -n "${_COMMON_UTILITIES_SH:+_}" ]] && return || readonly _COMMON_UTILITIES_SH=1

source ${SCRIPT_DIR}/utilities/enforce-bash-version.sh

##
# Various other utilities
#
function prompt_yes_or_no {
    local prompt=${1}
    while true; do
        read -r -n 1 -p "${prompt} [y/n]: " REPLY
        case $REPLY in
            [yY]) echo ; return 0 ;;
            [nN]) echo ; return 1 ;;
            *) echo " \033[31m %s \n\033[0m" "invalid input"
        esac
    done
}

function map {
    local func=${1}
    local input_file=${2:-/dev/null}
    local before_args=$(cat ${3:-/dev/null})
    local after_args=$(cat ${4:-/dev/null})

    for i in $(cat ${input_file}); do
        "${func}" ${before_args} ${i} ${after_args}
    done
}

function length {
    local args=${@}
    local count=0

    for a in ${args}; do
        let 'count++'
    done

    echo "${count}"
}

function join {
    local delimiter=${1}

    if [[ -n "${2:+x}" ]]; then
        echo -ne "${2}"
    fi

    for arg in ${@:3}; do
        echo -ne "${delimiter}${arg}"
    done
}

function is_element_of {
    local str=${1}
    local options_file=${2}

    for o in $(cat ${options_file}); do
        [[ "${str}" == "${o}" ]] && return 0
    done

    return 1
}

