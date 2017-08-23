#!/bin/bash -e

source common.sh


function usage {
    echo "${0} [ options ] <image> [ <image> [ <image> ... ] ]"
    echo
    echo "This script is used to build the various docker images needed to run the services from this project"
    echo
    echo "Images:"
    print_build_usage_images_list
    echo
    echo "Options:"
    echo "  --help, -h  Print this usage and exit"
}

function build {
    if [[ ${1} = 'all' ]]; then
        local images=`get_images_for_build`

        log_block 'Building all images:'
        for i in ${images}; do
            log_line "${i}"
        done

        for i in ${images}; do
            build_image_with_hooks ${i}
        done
    else
        for i in ${@}; do
            if [[ ${i} != 'all' ]]; then
                build_image_with_hooks ${i}
            fi
        done
    fi
}

function main {
    while [[ ${1} == -* ]]; do
        case ${1} in
            -h | --help)
                usage
                exit 0
            ;;
            -*)
                log_block "Invalid flag '${1}'!"
                echo
                usage
                exit 1
            ;;
        esac
        shift
    done

    local images=${@}
    check_images_argument 'get_images_for_build' ${images}

    build ${images}
}


main ${@}
