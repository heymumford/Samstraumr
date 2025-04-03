#!/bin/bash

# Script to run tests with support for both industry-standard and Samstraumr-specific terminology

function show_help() {
  echo "Usage: $0 [options] <test-type>"
  echo ""
  echo "Run tests using either industry-standard or Samstraumr-specific terminology."
  echo ""
  echo "Options:"
  echo "  -h, --help              Show this help message"
  echo "  -b, --both              Include tests with equivalent tags in the other terminology"
  echo "  -o, --output <file>     Write test output to file"
  echo "  -p, --profile <profile> Use specific Maven profile (default: atl-tests)"
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
  echo "  $0 --profile btl-tests integration # Run integration tests with BTL profile"
  echo ""
}

# Default values
INCLUDE_EQUIVALENT=false
OUTPUT_FILE=""
MAVEN_PROFILE="atl-tests"

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
if [ "$INCLUDE_EQUIVALENT" = true ]; then
  CMD="mvn test -P $MAVEN_PROFILE -Dcucumber.filter.tags=\"@$TEST_TYPE or @$(./map-test-type.sh $TEST_TYPE)\""
else
  CMD="mvn test -P $MAVEN_PROFILE -Dcucumber.filter.tags=\"@$TEST_TYPE\""
fi

# Display the command
echo "Running command: $CMD"

# Execute the command
if [ -n "$OUTPUT_FILE" ]; then
  eval "$CMD" | tee "$OUTPUT_FILE"
else
  eval "$CMD"
fi