#!/bin/bash
#==============================================================================
# Filename: run-tests.sh
# Description: Main test runner with support for different test types and profiles
#==============================================================================
# Usage: ./run-tests.sh [options] <test-type>
#
# Options:
#   -h, --help                    Display this help message
#   -v, --verbose                 Enable verbose output
#   -b, --both                    Include equivalent tests from other terminology
#   -o, --output <file>           Write test output to file
#   -p, --profile <profile>       Use specific Maven profile
#   --skip-quality                Skip quality checks
#
# Test Types:
#   - Industry Standard: smoke, unit, component, integration, api, system, endtoend, property
#   - Samstraumr Specific: orchestration, tube, composite, flow, machine, stream, acceptance, adaptation
#   - Special: all, atl, btl, adam
#
# Examples:
#   ./run-tests.sh unit                # Run unit tests only
#   ./run-tests.sh --both unit         # Run unit tests and equivalent tube tests
#   ./run-tests.sh -p btl-tests flow   # Run flow tests with BTL profile
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
  local description="Main test runner with support for different test types and profiles"
  
  local options=$(cat <<EOF
  -h, --help                    Display this help message
  -v, --verbose                 Enable verbose output
  -b, --both                    Include equivalent tests from other terminology
  -o, --output <file>           Write test output to file
  -p, --profile <profile>       Use specific Maven profile
  --skip-quality                Skip quality checks
EOF
)

  local examples=$(cat <<EOF
  ./run-tests.sh unit                # Run unit tests only
  ./run-tests.sh --both unit         # Run unit tests and equivalent tube tests
  ./run-tests.sh -p btl-tests flow   # Run flow tests with BTL profile
EOF
)

  show_help_template "$script_name" "$description" "$options" "$examples"
  
  # Show test types
  echo -e "${COLOR_BOLD}Test Types:${COLOR_RESET}"
  show_test_types_help
}

function parse_arguments() {
  # Default values
  TEST_TYPE="all"
  INCLUDE_EQUIVALENT=false
  OUTPUT_FILE=""
  MAVEN_PROFILE="${SAMSTRAUMR_ATL_PROFILE}"
  SKIP_QUALITY=false
  
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
      -b|--both)
        INCLUDE_EQUIVALENT=true
        shift
        ;;
      -o|--output)
        OUTPUT_FILE="$2"
        shift 2
        ;;
      -p|--profile)
        MAVEN_PROFILE="$2"
        shift 2
        ;;
      --skip-quality)
        SKIP_QUALITY=true
        shift
        ;;
      *)
        # Assume this is the test type
        TEST_TYPE="$1"
        shift
        ;;
    esac
  done
  
  # Debug output
  if [ "$VERBOSE" = "true" ]; then
    print_debug "TEST_TYPE: $TEST_TYPE"
    print_debug "INCLUDE_EQUIVALENT: $INCLUDE_EQUIVALENT"
    print_debug "OUTPUT_FILE: $OUTPUT_FILE"
    print_debug "MAVEN_PROFILE: $MAVEN_PROFILE"
    print_debug "SKIP_QUALITY: $SKIP_QUALITY"
  fi
}

function run_tests() {
  local test_type="$1"
  local profile="$2"
  local include_equivalent="$3"
  local output_file="$4"
  
  # Special case for adam tube tests
  test_type_lower=$(to_lowercase "$test_type")
  if [[ "$test_type_lower" == "adam" || "$test_type_lower" == "adamtube" ]]; then
    print_header "Running Adam Tube Tests"
    run_adam_tube_tests "${SAMSTRAUMR_ADAM_TUBE_PROFILE}" "$output_file"
    return $?
  elif [[ "$test_type_lower" == "adam-atl" || "$test_type_lower" == "adamtube-atl" ]]; then
    print_header "Running Adam Tube ATL Tests"
    run_adam_tube_tests "${SAMSTRAUMR_ADAM_TUBE_ATL_PROFILE}" "$output_file"
    return $?
  fi
  
  # For other test types
  print_header "Running ${test_type} Tests"
  
  # Get tag expression for the test type
  local tag_expression=$(get_cucumber_tag_expression "$test_type" "$include_equivalent")
  
  # Run the tests
  run_cucumber_tests "$test_type" "$profile" "$tag_expression" "$output_file"
  
  return $?
}

function main() {
  # Parse command line arguments
  parse_arguments "$@"
  
  # Validate arguments
  if [ -z "$TEST_TYPE" ]; then
    print_error "No test type specified"
    show_help
    exit 1
  fi
  
  # Default to ATL profile if not specified
  if [ -z "$MAVEN_PROFILE" ]; then
    MAVEN_PROFILE="${SAMSTRAUMR_ATL_PROFILE}"
  fi
  
  # Run the tests
  run_tests "$TEST_TYPE" "$MAVEN_PROFILE" "$INCLUDE_EQUIVALENT" "$OUTPUT_FILE"
  
  # Check result
  local result=$?
  if [ $result -eq 0 ]; then
    print_test_summary "true" "$TEST_TYPE"
  else
    print_test_summary "false" "$TEST_TYPE"
  fi
  
  return $result
}

#------------------------------------------------------------------------------
# Main
#------------------------------------------------------------------------------
main "$@"
exit $?