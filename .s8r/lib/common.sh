#!/usr/bin/env bash
#==============================================================================
# S8r Common Library Functions
# Shared code for the Samstraumr build system
#==============================================================================

# Find repository root
PROJECT_ROOT="${PROJECT_ROOT:-$(git rev-parse --show-toplevel)}"

# Terminal colors
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
BOLD='\033[1m'
RESET='\033[0m'

# Functions for prettier output
info() { echo -e "${BLUE}$1${RESET}"; }
success() { echo -e "${GREEN}$1${RESET}"; }
error() { echo -e "${RED}Error: $1${RESET}" >&2; exit 1; }
warn() { echo -e "${YELLOW}Warning: $1${RESET}" >&2; }
highlight() { echo -e "${CYAN}$1${RESET}"; }
important() { echo -e "${MAGENTA}$1${RESET}"; }

# Determine Maven settings
setup_maven_opts() {
  MVN_OPTS=""
  if [ -n "$MAVEN_MEMORY_OPTS" ]; then
    MVN_OPTS="$MAVEN_MEMORY_OPTS"
  else
    MVN_OPTS="-Xmx1g"
  fi
  export MAVEN_OPTS="$MVN_OPTS"
}

# Execute with timing information
time_exec() {
  local start_time=$(date +%s)
  "$@"
  local exit_code=$?
  local end_time=$(date +%s)
  local duration=$((end_time - start_time))
  
  if [ $duration -gt 60 ]; then
    local minutes=$((duration / 60))
    local seconds=$((duration % 60))
    echo -e "${YELLOW}Execution time: ${minutes}m ${seconds}s${RESET}"
  else
    echo -e "${YELLOW}Execution time: ${duration}s${RESET}"
  fi
  
  return $exit_code
}

# Check if a command exists
command_exists() {
  command -v "$1" >/dev/null 2>&1
}

# Check for required tools
check_requirements() {
  local missing=0
  
  for cmd in "$@"; do
    if ! command_exists "$cmd"; then
      echo -e "${RED}Required command not found: ${cmd}${RESET}" >&2
      missing=1
    fi
  done
  
  if [ $missing -eq 1 ]; then
    echo -e "${RED}Please install the missing requirements and try again.${RESET}" >&2
    exit 1
  fi
}

# Run Maven command with appropriate arguments
run_maven() {
  local cmd=("mvn")
  
  # Add arguments
  for arg in "$@"; do
    cmd+=("$arg")
  done
  
  # Display command
  echo -e "${BLUE}Running: ${cmd[*]}${RESET}"
  
  # Execute command
  if "${cmd[@]}"; then
    return 0
  else
    return 1
  fi
}

# Initialize common settings
setup_maven_opts