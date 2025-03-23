#!/usr/bin/env bash

# Global variables
CURRENT_PATH="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &>/dev/null && pwd)"
DC_FILE="$CURRENT_PATH/docker-compose.yml"
DC_DEV_FILE="$CURRENT_PATH/docker-compose-dev.yml"
DC_PROD_FILE="$CURRENT_PATH/docker-compose-prod.yml"
IMAGE_NAME="breizhcamp/konter:latest"
IMAGE_TRIVY="aquasec/trivy:0.18.3"

ARGS=()
HELP=0 VERBOSE=0 DEV=0 PROD=0
START=0 STOP=0 DOWN=0 BUILD=0 LINT=0 GITLEAKS=0

source "$CURRENT_PATH/libs/utils.sh"

function display_help() {
    local output=""
    output="
$(colors 'Y')Usage$(colors 'W') $(basename "$0") [OPTIONS] COMMAND
$(colors 'Y')Commands:$(colors 'N')
$(colors 'G')start$(colors 'W')               Initialize and creating containers
$(colors 'G')stop$(colors 'W')                Stopping containers
$(colors 'G')down$(colors 'W')                Stopping/removing containers, networks and volumes
$(colors 'G')build$(colors 'W')               Building docker image
$(colors 'G')lint$(colors 'W')                Lint the Dockerfile
$(colors 'G')gitleaks$(colors 'W')            Detecting secrets like passwords, API keys, and tokens in files
$(colors 'Y')Options:$(colors 'N')
$(colors 'G')-d, --dev$(colors 'W')           Starting konter container with a dev profil
$(colors 'G')-p, --prod$(colors 'W')          Starting konter container with a prod profil
$(colors 'G')-v, --verbose$(colors 'W')       Make the command more talkative
$(colors 'G')-h, --help$(colors 'W')          Display help
    "
    echo -e "$output\n"|sed '1d; $d'
    return 0
}

function start() {
    if ! [[ -f .env  ]]; then
      info "Creating an .env file"
      cp .env-sample .env
    fi
    info "Creating and starting Docker containers"
    local cmd="docker compose -f $DC_FILE up --build -d"
    if [[ $DEV -gt 0 ]]; then
        cmd="docker compose -f $DC_FILE -f $DC_DEV_FILE up --build -d"
    fi
    if [[ $PROD -gt 0 ]]; then
        cmd="docker compose -f $DC_FILE -f $DC_PROD_FILE up --build -d"
    fi
    debug "$cmd"
    ! $cmd && error "Containers cannot be started" && return 1
    return 0
}

function stop() {
    info "Stopping Docker containers"
    local cmd="docker compose -f $DC_FILE -f $DC_DEV_FILE -f $DC_PROD_FILE stop"
    debug "$cmd"
    ! $cmd && error "Containers cannot be stopped" && return 1
    return 0
}

function down() {
    info "Remove Docker containers, networks and volumes"
    local cmd="docker compose -f $DC_FILE -f $DC_DEV_FILE -f $DC_PROD_FILE down --volumes"
    debug "$cmd"
    ! $cmd && error "Containers cannot be removed" && return 1
    return 0
}

function build() {
    info "Build Docker image"
    local cmd="docker build -t $IMAGE_NAME ."
    debug "$cmd"
    ! $cmd && error "Docker image cannot be built" && return 1
    if [ -z "$(docker images -q $IMAGE_NAME 2> /dev/null)" ]; then
      error "Docker image failed to be created" && return 1
    fi
    ! scan_trivy && error "Error executing trivy scan" && return 1
    return 0
}

function scan_trivy() {
    info "Execute a scan to find vulnerabilities"
    local cmd="docker run --rm -v /var/run/docker.sock:/var/run/docker.sock $IMAGE_TRIVY --severity HIGH,CRITICAL $IMAGE_NAME"
    debug "$cmd"
    ! exec $cmd && return 1
    return 0
}

function lint_dockerfile() {
    info "Run docker build --check"
    cmd="docker build --check --debug ."
    debug "$cmd"
    ! exec $cmd < Dockerfile && error "Error executing a check" && return 1
    return 0
}

function scan_gitleaks() {
    # check if pre-commit is installed
    if ! command -v pre-commit 2>&1 >/dev/null; then
        error "pre-commit could not be found" && return 1
    fi
    info "Execute a scan with gitleaks and pre-commit to find secrets"
    local cmd="pre-commit run gitleaks --all-files -v"
    debug "$cmd"
    ! exec $cmd && return 1
    return 0
}

# Check options passed as script parameters.
# shellcheck disable=SC2034
function check_opts() {
    read -ra opts <<< "$@"
    for opt in "${opts[@]}"; do
        case "$opt" in
            start) START=1 ; ARGS+=("$opt") ;;
            stop) STOP=1 ; ARGS+=("$opt") ;;
            down) DOWN=1 ; ARGS+=("$opt") ;;
            build) BUILD=1 ; ARGS+=("$opt") ;;
            lint) LINT=1 ;;
            gitleaks) GITLEAKS=1 ;;
            --dev|-d) DEV=1 ;;
            --prod|-p) PROD=1 ;;
            --verbose|-v) VERBOSE=1 ;;
            --help) HELP=1 ;;
            *) ARGS+=("$opt") ;;
        esac
    done
    # Help is displayed if no option is passed as script parameter
    if [[ $((HELP+START+STOP+DOWN+BUILD+LINT+GITLEAKS)) -eq 0 ]]; then
        HELP=1
    fi
    return 0
}

function main() {
    # Check options passed as script parameters and execute tasks
    ! check_opts "$@" && return 1
    # Display help
    if [[ $HELP -gt 0 ]]; then
        ! display_help && return 2
        return 0
    fi
    # Starting Docker containers
    if [[ $START -gt 0 ]]; then
        ! start && return 3
    fi
    # Stopping containers
    if [[ $STOP -gt 0 ]]; then
        ! stop && return 4
    fi
    # Stopping and remove containers, networks and volumes
    if [[ $DOWN -gt 0 ]]; then
        ! down && return 5
    fi
    # Building docker image
    if [[ $BUILD -gt 0 ]]; then
        ! build && return 6
    fi
    # Lint Dockerfile
    if [[ $LINT -gt 0 ]]; then
        ! lint_dockerfile && return 7
    fi
    # Execute a scan with gitleaks and pre-commit to find secrets
    if [[ $GITLEAKS -gt 0 ]]; then
        ! scan_gitleaks && return 8
    fi
    return 0
}

main "$@"
