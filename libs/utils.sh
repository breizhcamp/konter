#!/usr/bin/env bash

# Shell coloring
function colors() {
    case $1 in
        'R') echo -e '\033[0;31m' ;; # RED
        'G') echo -e '\033[0;32m' ;; # GREEN
        'B') echo -e '\033[0;34m' ;; # BLUE
        'Y') echo -e '\033[0;33m' ;; # YELLOW
        'W') echo -e '\033[0;37m' ;; # WHITE
        'N') echo -e '\033[0;0m' ;; # NOCOLOR
    esac
}

function info() {
    echo -e "$(colors 'W')$*$(colors 'N')"
    return 0
}

function debug() {
    if [[ $((VERBOSE)) -gt 0 ]]; then
        echo -e "$(colors 'G')$*$(colors 'N')"
    fi
    return 0
}

function error() {
    echo -e "$(colors 'R')$*$(colors 'N')"
    return 0
}
