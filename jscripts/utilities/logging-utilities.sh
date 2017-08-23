#!/usr/bin/env bash

[[ -n "${_LOGGING_UTILITIES_SH:+_}" ]] && return || readonly _LOGGING_UTILITIES_SH=1

# Colors for pretty printing
readonly NO_COLOR='\e[0m'
readonly GRAY='\e[1m'
readonly RED='\e[31m'
readonly YELLOW='\e[33m'
readonly GREEN='\e[32m'
readonly BLUE='\e[34m'
readonly CYAN='\e[36m'
readonly WHITE='\e[37m'

# Available log levels
readonly _LOG_LEVELS=(
    NONE
    ERROR
    WARN
    INFO
    DEBUG
)

declare -A _LOG_LEVEL_SET=()

# Other control variables that can be overridden by users of the script
: ${LOG_TEE_FILE:="/var/log/jscripts/jscripts-$(date '+%Y-%m-%d').log"}


function _get_log_date_string {
    date '+%Y-%m-%dT%H:%M:%S'
}

function _log_at_level {
    local level=${1^^}
    local color=${2}

    if [[ -n "${_LOG_LEVEL_SET[${level}]:+_}" ]]; then
        echo -e "${color}$(_get_log_date_string) [${level}] - ${@:3}${NO_COLOR}" | tee -a "${LOG_TEE_FILE}"
    fi
}

function print_with_spacing_and_color {
    local print_string=${1}
    local spacing=${2:-''}
    local color=${3:-"${NO_COLOR}"}

    printf "${color}%-${spacing}s${NO_COLOR}" "${print_string}"
}

function set_log_level {
    local level=${1^^}

    mkdir -p $(dirname "${LOG_TEE_FILE}")

    for l in ${_LOG_LEVELS[@]}; do
        unset _LOG_LEVEL_SE["${l}"]
    done

    for l in ${_LOG_LEVELS[@]}; do
        _LOG_LEVEL_SET["${l}"]=1

        if [[ "${l}" == "${level}" ]]; then
            return
        fi
    done
}

function log_error {
    _log_at_level ERROR "${RED}" ${@}
}

function log_warn {
    _log_at_level WARN "${YELLOW}" ${@}
}

function log_info {
    _log_at_level INFO "${GREEN}" ${@}
}

function log_debug {
    _log_at_level DEBUG "${BLUE}" ${@}
}
