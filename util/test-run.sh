#!/bin/bash
# Filename: test-run.sh
# Purpose: Main test runner script that forwards to the appropriate specialized test script
# Location: util/

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

# Default to running all tests if no argument is provided
TEST_TYPE=${1:-all}

# Convert to lowercase for case-insensitive matching
test_type_lower=$(echo "$TEST_TYPE" | tr '[:upper:]' '[:lower:]')

# Forward to the appropriate script based on the argument
echo -e "\033[0;34mRunning tests: ${TEST_TYPE}\033[0m"

# Special case for Adam Tube tests
if [ "$test_type_lower" == "adam" ] || [ "$test_type_lower" == "adamtube" ]; then
  echo "Running Adam Tube tests using JUnit 4 runner"
  cd "${PROJECT_ROOT}" && mvn test -f "${SAMSTRAUMR_CORE_MODULE}/pom.xml" -P "${SAMSTRAUMR_ADAM_TUBE_PROFILE}" ${SAMSTRAUMR_RUN_TESTS}
elif [ "$test_type_lower" == "adam-atl" ] || [ "$test_type_lower" == "adamtube-atl" ]; then
  echo "Running Adam Tube ATL tests using JUnit 4 runner"
  cd "${PROJECT_ROOT}" && mvn test -f "${SAMSTRAUMR_CORE_MODULE}/pom.xml" -P "${SAMSTRAUMR_ADAM_TUBE_ATL_PROFILE}" ${SAMSTRAUMR_RUN_TESTS}
else
  # Use the test-run-all.sh script for other test types
  "${SCRIPT_DIR}/test-run-all.sh" "$@"
fi
