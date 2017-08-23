#!/usr/bin/env bash

[[ -n "${_RPI_UTILITIES_SH:+_}" ]] && return || readonly _RPI_UTILITIES_SH=1


function is_running_on_raspberry_pi {
    uname -a | grep 'arm' &> /dev/null
}