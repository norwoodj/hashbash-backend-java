#!/usr/bin/env bash

source ${SCRIPT_DIR}/utilities/command-line-utilities.sh

function get_ca_cert_path {
    echo /etc/kubernetes/pki/ca.crt
}

function get_ca_key_path {
    echo /etc/kubernetes/pki/ca.key
}

function get_cert_expiry_days {
    echo 90
}

function get_full_csr_path {
    local name=${1}
    echo "${name}.csr"
}

function get_full_cert_path {
    local name=${1}
    echo "${name}.cert"
}
