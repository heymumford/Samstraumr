#!/bin/bash
# Filename: test-run-all.sh
# Purpose: Run tests with support for both industry-standard and Samstraumr-specific terminology
# Location: util/
# Usage: ./test-run-all.sh [options] <test-type>
#
# This script provides a comprehensive interface for running different types of tests
# in the Samstraumr project, supporting both industry-standard and Samstraumr-specific
# test terminology.
#
# See the help output (--help) for detailed usage information.

# Determine script directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

# Source the configuration file
if [ -f "${PROJECT_ROOT}/.samstraumr.config" ]; then
  source "${PROJECT_ROOT}/.samstraumr.config"
else
  echo "Configuration file not found: ${PROJECT_ROOT}/.samstraumr.config"
  exit 1
fi

function show_help() {
  echo "Usage: $0 [options] <test-type>"
  echo ""
  echo "Run tests using either industry-standard or Samstraumr-specific terminology."
  echo ""
  echo "Options:"
  echo "  -h, --help              Show this help message"
  echo "  -b, --both              Include tests with equivalent tags in the other terminology"
  echo "  -o, --output <file>     Write test output to file"
  echo "  -p, --profile <profile> Use specific Maven profile (default: ${SAMSTRAUMR_ATL_PROFILE})"
  echo ""
  echo "Test Types:"
  echo "  Industry Standard       |  Samstraumr Equivalent"
  echo "  --------------------    |  ----------------------"
  echo "  smoke                   |  orchestration"
  echo "  unit                    |  tube"
  echo "  component               |  composite"
  echo "  integration             |  flow"
  echo "  api                     |  machine"
  echo "  system                  |  stream"
  echo "  endtoend                |  acceptance"
  echo "  property                |  adaptation"
  echo ""
  echo "Examples:"
  echo "  $0 unit                  # Run unit tests only"
  echo "  $0 --both unit           # Run unit tests and tube tests"
  echo "  $0 --profile ${SAMSTRAUMR_BTL_PROFILE} integration # Run integration tests with BTL profile"
  echo ""
}

# Default values
INCLUDE_EQUIVALENT=false
OUTPUT_FILE=""
MAVEN_PROFILE="${SAMSTRAUMR_ATL_PROFILE}"

# Parse command line options
while [[ $# -gt 0 ]]; do
  case $1 in
    -h|--help)
      show_help
      exit 0
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
    *)
      TEST_TYPE="$1"
      shift
      ;;
  esac
done

if [ -z "$TEST_TYPE" ]; then
  echo "Error: No test type specified"
  show_help
  exit 1
fi

# Convert test type to lowercase for case insensitivity
TEST_TYPE=$(echo "$TEST_TYPE" | tr '[:upper:]' '[:lower:]')

# Command to run the tests
MAP_SCRIPT="${SCRIPT_DIR}/test/mapping/map-test-type.sh"

# Check if mapping script exists
if [ ! -f "$MAP_SCRIPT" ]; then
  echo "Error: Mapping script not found at $MAP_SCRIPT"
  exit 1
fi

if [ "$INCLUDE_EQUIVALENT" = true ]; then
  # Get the equivalent test type
  EQUIVALENT_TYPE=$("$MAP_SCRIPT" "$TEST_TYPE")
  CMD="mvn test -f ${SAMSTRAUMR_CORE_MODULE}/pom.xml -P $MAVEN_PROFILE -Dcucumber.filter.tags=\"@$TEST_TYPE or @$EQUIVALENT_TYPE\""
else
  CMD="mvn test -f ${SAMSTRAUMR_CORE_MODULE}/pom.xml -P $MAVEN_PROFILE -Dcucumber.filter.tags=\"@$TEST_TYPE\""
fi

# Display the command
echo "Running command: $CMD"

# Execute the command
if [ -n "$OUTPUT_FILE" ]; then
  eval "$CMD" | tee "$OUTPUT_FILE"
else
  eval "$CMD"
fi