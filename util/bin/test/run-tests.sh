#!/bin/bash
#==============================================================================
# Filename: run-tests.sh
# Description: Run tests for Samstraumr
#==============================================================================
# Usage: ./run-tests.sh <test-type> [options]
#
# Options:
#   --verbose                 Enable verbose output
#   --output-file <file>      Write test output to file
#   --profile <profile>       Use specific Maven profile
#   --clean                   Clean project before running tests
#   --skip-quality            Skip quality checks
#   --both                    Include equivalent tests from other terminology
#
# Test types:
#   Industry Standard      |  Samstraumr Equivalent
#   --------------------   |  ----------------------
#   smoke                  |  orchestration
#   unit                   |  tube
#   component              |  composite
#   integration            |  flow
#   api                    |  machine
#   system                 |  stream
#   endtoend               |  acceptance
#   property               |  adaptation
#
#   Special test types: all, atl, btl, adam
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

# Check that test type is provided
if [ $# -lt 1 ]; then
  echo "Error: Missing test type argument"
  echo "Usage: $0 <test-type> [options]"
  exit 1
fi

# Extract test type from arguments
TEST_TYPE="$1"
shift

# Set default options
VERBOSE=false
CLEAN=false
SKIP_QUALITY=false
INCLUDE_BOTH=false
OUTPUT_FILE=""
PROFILE=""

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
    --both)
      INCLUDE_BOTH=true
      shift
      ;;
    *)
      echo "Unknown option: $1"
      exit 1
      ;;
  esac
done

# Map test type to profile
case "$TEST_TYPE" in
  unit|tube)
    if [ -z "$PROFILE" ]; then
      if [ "$TEST_TYPE" = "unit" ]; then
        PROFILE="unit-tests"
      else
        PROFILE="tube-tests"
      fi
      
      # Include both if requested
      if $INCLUDE_BOTH; then
        PROFILE="$PROFILE,tube-tests,unit-tests"
      fi
    fi
    ;;
  component|composite)
    if [ -z "$PROFILE" ]; then
      if [ "$TEST_TYPE" = "component" ]; then
        PROFILE="component-tests"
      else
        PROFILE="composite-tests"
      fi
      
      # Include both if requested
      if $INCLUDE_BOTH; then
        PROFILE="$PROFILE,composite-tests,component-tests"
      fi
    fi
    ;;
  integration|flow)
    if [ -z "$PROFILE" ]; then
      if [ "$TEST_TYPE" = "integration" ]; then
        PROFILE="integration-tests"
      else
        PROFILE="flow-tests"
      fi
      
      # Include both if requested
      if $INCLUDE_BOTH; then
        PROFILE="$PROFILE,flow-tests,integration-tests"
      fi
    fi
    ;;
  api|machine)
    if [ -z "$PROFILE" ]; then
      if [ "$TEST_TYPE" = "api" ]; then
        PROFILE="api-tests"
      else
        PROFILE="machine-tests"
      fi
      
      # Include both if requested
      if $INCLUDE_BOTH; then
        PROFILE="$PROFILE,machine-tests,api-tests"
      fi
    fi
    ;;
  system|stream)
    if [ -z "$PROFILE" ]; then
      if [ "$TEST_TYPE" = "system" ]; then
        PROFILE="system-tests"
      else
        PROFILE="stream-tests"
      fi
      
      # Include both if requested
      if $INCLUDE_BOTH; then
        PROFILE="$PROFILE,stream-tests,system-tests"
      fi
    fi
    ;;
  endtoend|acceptance)
    if [ -z "$PROFILE" ]; then
      if [ "$TEST_TYPE" = "endtoend" ]; then
        PROFILE="endtoend-tests"
      else
        PROFILE="acceptance-tests"
      fi
      
      # Include both if requested
      if $INCLUDE_BOTH; then
        PROFILE="$PROFILE,acceptance-tests,endtoend-tests"
      fi
    fi
    ;;
  smoke|orchestration)
    if [ -z "$PROFILE" ]; then
      if [ "$TEST_TYPE" = "smoke" ]; then
        PROFILE="smoke-tests"
      else
        PROFILE="orchestration-tests"
      fi
      
      # Include both if requested
      if $INCLUDE_BOTH; then
        PROFILE="$PROFILE,orchestration-tests,smoke-tests"
      fi
    fi
    ;;
  property|adaptation)
    if [ -z "$PROFILE" ]; then
      if [ "$TEST_TYPE" = "property" ]; then
        PROFILE="property-tests"
      else
        PROFILE="adaptation-tests"
      fi
      
      # Include both if requested
      if $INCLUDE_BOTH; then
        PROFILE="$PROFILE,adaptation-tests,property-tests"
      fi
    fi
    ;;
  atl)
    if [ -z "$PROFILE" ]; then
      PROFILE="atl-tests"
    fi
    ;;
  btl)
    if [ -z "$PROFILE" ]; then
      PROFILE="btl-tests"
    fi
    ;;
  adam)
    if [ -z "$PROFILE" ]; then
      PROFILE="adam-tube-tests"
    fi
    ;;
  all)
    if [ -z "$PROFILE" ]; then
      PROFILE="all-tests"
    fi
    ;;
  *)
    echo "Error: Unknown test type: $TEST_TYPE"
    exit 1
    ;;
esac

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
echo "Running tests: $TEST_TYPE with profile: $PROFILE"
if [ -n "$OUTPUT_FILE" ]; then
  echo "Writing output to: $OUTPUT_FILE"
  $MAVEN_CMD | tee "$OUTPUT_FILE"
else
  $MAVEN_CMD
fi

# Check result
if [ ${PIPESTATUS[0]} -eq 0 ]; then
  echo -e "\033[0;32mTests completed successfully!\033[0m"
  exit 0
else
  echo -e "\033[0;31mTests failed\033[0m"
  exit 1
fi
