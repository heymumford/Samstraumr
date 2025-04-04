#!/bin/bash
#==============================================================================
# Filename: run-atl-tests.sh
# Description: Run Above-The-Line (ATL) tests for Samstraumr
#==============================================================================
# Usage: ./run-atl-tests.sh [options]
#
# Options:
#   --verbose                 Enable verbose output
#   --output-file <file>      Write test output to file
#   --profile <profile>       Use specific Maven profile
#   --clean                   Clean project before running tests
#   --skip-quality            Skip quality checks
#
# This script runs Above-The-Line (ATL) tests, which are critical tests
# that verify core functionality.
#==============================================================================

# Determine script directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../../.." && pwd)"

# Source common functions if available
if [ -f "${PROJECT_ROOT}/util/lib/common.sh" ]; then
  source "${PROJECT_ROOT}/util/lib/common.sh"
fi

if [ -f "${PROJECT_ROOT}/util/lib/test-lib.sh" ]; then
  source "${PROJECT_ROOT}/util/lib/test-lib.sh"
fi

# Set default options
VERBOSE=false
CLEAN=false
SKIP_QUALITY=false
OUTPUT_FILE=""
PROFILE="atl-tests"

# Parse command-line arguments
while [[ $# -gt 0 ]]; do
  case "$1" in
    --verbose)
      VERBOSE=true
      shift
      ;;
    --output-file)
      OUTPUT_FILE="$2"
      shift 2
      ;;
    --profile)
      PROFILE="$2"
      shift 2
      ;;
    --clean)
      CLEAN=true
      shift
      ;;
    --skip-quality)
      SKIP_QUALITY=true
      shift
      ;;
    *)
      echo "Unknown option: $1"
      exit 1
      ;;
  esac
done

# Build Maven command
MAVEN_CMD="mvn test"

# Add profile
if [ -n "$PROFILE" ]; then
  MAVEN_CMD="$MAVEN_CMD -P $PROFILE"
fi

# Add clean if requested
if $CLEAN; then
  MAVEN_CMD="$MAVEN_CMD clean"
fi

# Add skip quality if requested
if $SKIP_QUALITY; then
  MAVEN_CMD="$MAVEN_CMD -P skip-quality-checks"
fi

# Add verbose flag for Maven
if $VERBOSE; then
  MAVEN_CMD="$MAVEN_CMD -X"
else
  MAVEN_CMD="$MAVEN_CMD -q"
fi

# Change to project directory
cd "${PROJECT_ROOT}/Samstraumr" || exit 1

# Run tests
echo "Running ATL tests with profile: $PROFILE"
if [ -n "$OUTPUT_FILE" ]; then
  echo "Writing output to: $OUTPUT_FILE"
  $MAVEN_CMD | tee "$OUTPUT_FILE"
else
  $MAVEN_CMD
fi

# Check result
if [ ${PIPESTATUS[0]} -eq 0 ]; then
  echo -e "\033[0;32mATL tests completed successfully!\033[0m"
  exit 0
else
  echo -e "\033[0;31mATL tests failed\033[0m"
  exit 1
fi
