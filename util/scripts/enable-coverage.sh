#!/bin/bash

# Enable JaCoCo code coverage for Samstraumr
# This script runs tests with JaCoCo code coverage enabled and generates reports

set -e

# Get script directory
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." &> /dev/null && pwd 2> /dev/null)"

# Set colored output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
CYAN='\033[0;36m'
RESET='\033[0m'

echo -e "${CYAN}===== Enabling JaCoCo Code Coverage =====${RESET}"

# Change to project root directory
cd "$PROJECT_ROOT"

# Parse command line arguments
TEST_TYPE="atl-tests"
CUSTOM_ARGS=""

function show_help() {
  echo "Usage: $0 [OPTIONS]"
  echo ""
  echo "Options:"
  echo "  --atl                Run Above-The-Line tests (default)"
  echo "  --btl                Run Below-The-Line tests"
  echo "  --all                Run all tests"
  echo "  --type=TYPE          Specify test type (atl-tests, btl-tests, all)"
  echo "  --custom='ARGS'      Pass custom arguments to Maven"
  echo "  --help               Show this help message"
  echo ""
  echo "Examples:"
  echo "  $0 --btl"
  echo "  $0 --type=all"
  echo "  $0 --custom='-Dmaven.test.failure.ignore=true'"
}

# Process arguments
for arg in "$@"; do
  case $arg in
    --atl)
      TEST_TYPE="atl-tests"
      shift
      ;;
    --btl)
      TEST_TYPE="btl-tests"
      shift
      ;;
    --all)
      TEST_TYPE="all"
      shift
      ;;
    --type=*)
      TEST_TYPE="${arg#*=}"
      shift
      ;;
    --custom=*)
      CUSTOM_ARGS="${arg#*=}"
      shift
      ;;
    --help)
      show_help
      exit 0
      ;;
  esac
done

# Test profile selection
if [ "$TEST_TYPE" == "all" ]; then
  TEST_PROFILE=""
  echo -e "${YELLOW}Running ALL tests with code coverage${RESET}"
else
  TEST_PROFILE="-P $TEST_TYPE"
  echo -e "${YELLOW}Running $TEST_TYPE with code coverage${RESET}"
fi

# Ensure JaCoCo is enabled by setting property
MAVEN_PROPS="-Djacoco.skip=false"

# Verify Maven installation
if ! command -v mvn &> /dev/null; then
  echo -e "${RED}Maven is not installed or not in the PATH${RESET}"
  exit 1
fi

# First run clean to ensure fresh start
echo -e "${YELLOW}Cleaning project...${RESET}"
mvn clean

# Run tests with JaCoCo enabled
echo -e "${YELLOW}Running tests with JaCoCo coverage...${RESET}"
mvn test $TEST_PROFILE $MAVEN_PROPS $CUSTOM_ARGS

# Generate JaCoCo report (even if tests failed)
echo -e "${YELLOW}Generating JaCoCo coverage report...${RESET}"
mvn jacoco:report $MAVEN_PROPS $CUSTOM_ARGS

# Report paths
CORE_REPORT="$PROJECT_ROOT/modules/samstraumr-core/target/site/jacoco/index.html"
ROOT_REPORT="$PROJECT_ROOT/target/site/jacoco/index.html"
MODULE_REPORT="$PROJECT_ROOT/modules/target/site/jacoco/index.html"

# Display report paths
echo -e "${CYAN}==== JaCoCo Coverage Reports ====${RESET}"
if [ -f "$CORE_REPORT" ]; then
  echo -e "${GREEN}Core module report: ${CYAN}$CORE_REPORT${RESET}"
fi
if [ -f "$MODULE_REPORT" ]; then
  echo -e "${GREEN}Modules report: ${CYAN}$MODULE_REPORT${RESET}"
fi
if [ -f "$ROOT_REPORT" ]; then
  echo -e "${GREEN}Root report: ${CYAN}$ROOT_REPORT${RESET}"
fi

echo -e "${GREEN}✓ JaCoCo coverage analysis complete${RESET}"