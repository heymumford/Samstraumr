#!/bin/bash
#==============================================================================
# Filename: run-atl-tests.sh
# Description: Run Above-The-Line (ATL) tests - critical tests that must pass
# Author: Original author (refactored by Claude)
# Created: 2025-04-03
# Updated: 2025-04-03
#==============================================================================
# Usage: ./run-atl-tests.sh [options]
#
# Options:
#   -h, --help         Display this help message
#   -v, --verbose      Enable verbose output
#   -o, --output <file> Write test output to file
#
# Examples:
#   ./run-atl-tests.sh            # Run ATL tests
#   ./run-atl-tests.sh -v         # Run ATL tests with verbose output
#   ./run-atl-tests.sh -o log.txt # Run ATL tests and save output to log.txt
#==============================================================================

# Determine script directory and load libraries
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../../../" && pwd)"

# Source shared libraries
source "${PROJECT_ROOT}/util/lib/common.sh"
source "${PROJECT_ROOT}/util/lib/test-lib.sh"

#------------------------------------------------------------------------------
# Functions
#------------------------------------------------------------------------------

function show_help() {
  local script_name="${BASH_SOURCE[0]}"
  local description="Run Above-The-Line (ATL) tests - critical tests that must pass"
  
  local options=$(cat <<EOF
  -h, --help            Display this help message
  -v, --verbose         Enable verbose output
  -o, --output <file>   Write test output to file
EOF
)

  local examples=$(cat <<EOF
  ./run-atl-tests.sh            # Run ATL tests
  ./run-atl-tests.sh -v         # Run ATL tests with verbose output
  ./run-atl-tests.sh -o log.txt # Run ATL tests and save output to log.txt
EOF
)

  show_help_template "$script_name" "$description" "$options" "$examples"
}

function parse_arguments() {
  # Default values
  OUTPUT_FILE=""
  
  # Parse common arguments first
  parse_common_args "$@"
  if [ $? -eq 1 ]; then
    show_help
    exit 0
  fi
  
  # Parse remaining arguments
  while [[ $# -gt 0 ]]; do
    case "$1" in
      -h|--help)
        show_help
        exit 0
        ;;
      -v|--verbose)
        # Already handled by parse_common_args
        shift
        ;;
      -o|--output)
        OUTPUT_FILE="$2"
        shift 2
        ;;
      *)
        # Ignore unexpected arguments
        print_warning "Ignoring unexpected argument: $1"
        shift
        ;;
    esac
  done
  
  # Debug output
  if [ "$VERBOSE" = "true" ]; then
    print_debug "OUTPUT_FILE: $OUTPUT_FILE"
  fi
}

function run_atl_tests() {
  local output_file="$1"
  
  print_header "Running Above-The-Line (ATL) Tests"
  print_info "These are the critical tests that must pass for the build to be considered valid."
  
  # Run the tests with the ATL profile
  local tag_expression=$(get_cucumber_tag_expression "atl")
  run_cucumber_tests "atl" "${SAMSTRAUMR_ATL_PROFILE}" "$tag_expression" "$output_file"
  
  return $?
}

function main() {
  # Parse command line arguments
  parse_arguments "$@"
  
  # Run the ATL tests
  run_atl_tests "$OUTPUT_FILE"
  
  # Check result
  local result=$?
  if [ $result -eq 0 ]; then
    print_success "ATL tests passed successfully"
    print_info "View the Cucumber report at: ${SAMSTRAUMR_CUCUMBER_REPORT}"
    return 0
  else
    print_error "ATL tests failed"
    print_error "Please fix the failing tests before proceeding."
    return 1
  fi
}

#------------------------------------------------------------------------------
# Main
#------------------------------------------------------------------------------
main "$@"
exit $?