#!/bin/bash
#==============================================================================
# Filename: unified-test-runner.sh
# Description: Unified test execution script for Samstraumr
#
# This script provides a single entry point for running all types of tests
# in the Samstraumr project. It replaces the separate run-tests.sh and 
# run-atl-tests.sh scripts with a more flexible and maintainable approach.
#
# USAGE:
#   ./unified-test-runner.sh <test-type> [options]
#
# OPTIONS:
#   -h, --help                    Show help message
#   -v, --verbose                 Enable verbose output
#   -b, --both                    Include equivalent tests from other terminology
#   -c, --clean                   Clean before running tests
#   -p, --profile <profile>       Use specific Maven profile
#   -o, --output <file>           Write test output to file
#   --skip-quality                Skip quality checks
#   --cyclename <name>            Specify test cycle name for reporting
#
# TEST TYPES:
#   See test-lib.sh for details on available test types
#
# EXAMPLES:
#   ./unified-test-runner.sh unit              # Run unit tests
#   ./unified-test-runner.sh -v --both tube    # Run tube tests and equivalent unit tests
#   ./unified-test-runner.sh -c -p atl-tests all # Run all tests with ATL profile, cleaning first
#==============================================================================

# Load library dependencies
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../../.." && pwd)"

# Source utilities
source "${PROJECT_ROOT}/util/lib/common.sh"
source "${PROJECT_ROOT}/util/lib/test-lib.sh"

#------------------------------------------------------------------------------
# Help and Usage Functions
#------------------------------------------------------------------------------

# Show help information
function show_help() {
  local script_name=$(basename "$0")
  
  print_header "${script_name} - Unified Test Runner"
  echo ""
  print_bold "USAGE:"
  echo "  ${script_name} [options] <test-type>"
  echo ""
  
  print_bold "OPTIONS:"
  echo "  -h, --help                    Display this help message"
  echo "  -v, --verbose                 Enable verbose output with test status reporting"
  echo "  -b, --both                    Include equivalent tests from other terminology"
  echo "  -c, --clean                   Clean before running tests" 
  echo "  -o, --output <file>           Write test output to file"
  echo "  -p, --profile <profile>       Use specific Maven profile"
  echo "  --skip-quality                Skip quality checks"
  echo "  --cyclename <name>            Specify test cycle name for reporting"
  echo ""
  
  show_test_types_help
  
  print_bold "EXAMPLES:"
  echo "  ${script_name} unit                            # Run unit tests only"
  echo "  ${script_name} --both unit                     # Run unit tests and equivalent tube tests"
  echo "  ${script_name} -p btl-tests flow               # Run flow tests with BTL profile"
  echo "  ${script_name} atl                             # Run all Above-The-Line tests"
  echo "  ${script_name} -v tube                         # Run tube tests with verbose status reporting"
  echo "  ${script_name} -v --cyclename \"Daily Build\" atl # Run ATL tests in a specific test cycle"
  echo ""
}

#------------------------------------------------------------------------------
# Main Test Execution
#------------------------------------------------------------------------------

function main() {
  # Initialize configuration with defaults
  declare -A config
  config[verbose]=false
  config[both]=false
  config[clean]=false
  config[profile]=""
  config[output]=""
  config[skip_quality]=false
  config[cyclename]="Tests"
  config[help]=false
  config[positional]=""
  
  # Parse command line arguments
  parse_args config "$@"
  
  # Print help if requested or no test type provided
  if [[ "${config[help]}" == "true" || -z "${config[positional]}" ]]; then
    show_help
    return 1
  fi
  
  # Extract test type from positional arguments
  local test_type="${config[positional]}"
  
  # Set VERBOSE for the rest of the script
  VERBOSE="${config[verbose]}"
  
  # Print banner
  if [[ "${config[verbose]}" == "true" ]]; then
    print_header "Samstraumr Test Runner"
    echo ""
    print_info "Running tests: $test_type with profile: ${config[profile]}"
    
    if [[ "${config[cyclename]}" != "Tests" ]]; then
      print_bold "${config[cyclename]}"
    fi
  fi
  
  # Special handling for ATL tests which require a specific test runner
  if [[ "$test_type" == "atl" || "$test_type" == "above-the-line" ]]; then
    print_info "Using specialized ATL test runner for Above-The-Line tests"
    
    # Build the arguments for the ATL test runner script
    local atl_args=""
    
    # Map our config to ATL runner args
    if [[ "${config[verbose]}" == "true" ]]; then
      atl_args="$atl_args --verbose"
    fi
    
    if [[ "${config[clean]}" == "true" ]]; then
      atl_args="$atl_args --clean"
    fi
    
    if [[ -n "${config[profile]}" ]]; then
      atl_args="$atl_args --profile ${config[profile]}"
    fi
    
    if [[ -n "${config[output]}" ]]; then
      atl_args="$atl_args --output-file ${config[output]}"
    fi
    
    if [[ "${config[skip_quality]}" == "true" ]]; then
      atl_args="$atl_args --skip-quality"
    fi
    
    if [[ -n "${config[cyclename]}" ]]; then
      atl_args="$atl_args --cyclename ${config[cyclename]}"
    fi
    
    # Run the ATL test runner script directly
    print_debug "Executing ATL test runner: ${SCRIPT_DIR}/run-atl-tests.sh $atl_args"
    
    # Execute the ATL test runner script
    cd "${PROJECT_ROOT}" && "${SCRIPT_DIR}/run-atl-tests.sh" $atl_args
    local result=$?
  else
    # Run regular tests using the unified run_test function
    run_test "$test_type" config
    local result=$?
  fi
  
  # Print summary
  if [[ "$result" -eq 0 ]]; then
    if [[ "${config[verbose]}" == "true" ]]; then
      print_success "Tests passed"
    fi
    return 0
  else
    if [[ "${config[verbose]}" == "true" ]]; then
      print_error "Tests failed"
    fi
    return 1
  fi
}

# Execute main function with all command-line arguments
main "$@"
exit $?