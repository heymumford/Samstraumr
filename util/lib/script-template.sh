#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# Filename: script-name.sh
# Description: Brief description of what the script does
#==============================================================================
# Usage: ./script-name.sh [options] <args>
#
# Options:
#   -h, --help          Display this help message
#   -v, --verbose       Enable verbose output
#
# Examples:
#   ./script-name.sh                # Basic usage
#   ./script-name.sh -v             # With verbose output
#   ./script-name.sh --option value # With additional options
#==============================================================================

# Determine script directory and load libraries
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../../../" && pwd)"  # Adjust path as needed

# Source shared libraries
source "${PROJECT_ROOT}/util/lib/common.sh"  # Always include common.sh
# Add additional libraries as needed:
# source "${PROJECT_ROOT}/util/lib/build-lib.sh"
# source "${PROJECT_ROOT}/util/lib/test-lib.sh"
# source "${PROJECT_ROOT}/util/lib/quality-lib.sh"
# source "${PROJECT_ROOT}/util/lib/version-lib.sh"

#------------------------------------------------------------------------------
# Functions
#------------------------------------------------------------------------------

function show_help() {
  local script_name="${BASH_SOURCE[0]}"
  local description="Brief description of what the script does"
  
  local options=$(cat <<EOF
  -h, --help          Display this help message
  -v, --verbose       Enable verbose output
  # Add more options here
EOF
)

  local examples=$(cat <<EOF
  ./script-name.sh                # Basic usage
  ./script-name.sh -v             # With verbose output
  ./script-name.sh --option value # With additional options
EOF
)

  show_help_template "$script_name" "$description" "$options" "$examples"
}

function parse_arguments() {
  # Default values for script-specific arguments
  ARG1=""
  ARG2=""
  FLAG1=false
  
  # Parse common arguments first (handles -h/--help and -v/--verbose)
  parse_common_args "$@"
  if [ $? -eq 1 ]; then
    show_help
    exit 0
  fi
  
  # Parse remaining arguments
  while [[ $# -gt 0 ]]; do
    case "$1" in
      -h|--help)
        # Already handled by parse_common_args
        shift
        ;;
      -v|--verbose)
        # Already handled by parse_common_args
        shift
        ;;
      --flag1)
        FLAG1=true
        shift
        ;;
      --option1)
        ARG1="$2"
        shift 2
        ;;
      *)
        # Assume this is a positional argument
        if [ -z "$ARG2" ]; then
          ARG2="$1"
        else
          print_error "Unexpected argument: $1"
          show_help
          exit 1
        fi
        shift
        ;;
    esac
  done
  
  # Validate required arguments
  if [ -z "$ARG2" ]; then
    print_error "Missing required argument"
    show_help
    exit 1
  fi
  
  # Debug output
  if [ "$VERBOSE" = "true" ]; then
    print_debug "ARG1: $ARG1"
    print_debug "ARG2: $ARG2"
    print_debug "FLAG1: $FLAG1"
  fi
}

# Function to perform the script's primary operation
function do_operation() {
  local arg1="$1"
  local arg2="$2"
  local flag1="$3"
  
  print_header "Performing Operation"
  
  # Implement your primary logic here
  print_info "Processing with arg1=$arg1, arg2=$arg2, flag1=$flag1"
  
  # Example of conditional logic
  if [ "$flag1" = "true" ]; then
    print_info "Flag1 is enabled, doing special processing"
  else
    print_info "Standard processing"
  fi
  
  # Example of using a configuration variable
  print_info "Using core module: ${SAMSTRAUMR_CORE_MODULE}"
  
  # Return a success code
  return 0
}

function main() {
  # Parse command line arguments
  parse_arguments "$@"
  
  # Perform the main operation
  do_operation "$ARG1" "$ARG2" "$FLAG1"
  
  # Return the operation's result
  return $?
}

#------------------------------------------------------------------------------
# Main
#------------------------------------------------------------------------------
main "$@"
exit $?