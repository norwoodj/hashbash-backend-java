#!/usr/bin/env bash

[[ -n "${_GENERATE_KUBECTL_CSR_SETTINGS_SH:+_}" ]] && return || readonly _GENERATE_KUBECTL_CSR_SETTINGS_SH=1

source ${SCRIPT_DIR}/utilities/command-line-utilities.sh


function handle_generate_csr_options {
    local flag=${1}

    case ${flag} in
        --existing-key | -e) EXISTING_KEY=${2}; return 2 ;;
        -*)                  return 1 ;;
    esac
}

function print_additional_options_usage_list {
    echo "  --existing-key, -e           Use an existing private key to generate the csr"
}

function handle_additional_options {
    use_additional_options_helpers <(echo "handle_generate_csr_options") ${@}
}

function get_csr_subject {
    local name=${1}
    echo "/CN=${name}"
}

function get_full_csr_path {
    local name=${1}
    echo "${name}.csr"
}

function get_full_key_path {
    local name=${1}
    echo "${name}.key"
}


##
# Hooks
##

function pre_generate_csr_hook {
    local name=${1}
    log_debug "Pre Generate CSR Hook for name ${name}"
}

function post_generate_csr_hook {
    local name=${1}
    log_debug "Post Generate CSR Hook for name ${name}"
}

