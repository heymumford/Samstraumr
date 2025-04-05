#!/bin/bash
# Filename: run-atl-tests.sh
# Purpose: Run Above-The-Line (ATL) tests - critical tests that must pass
# Location: util/test/

# Determine script directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"

# Source the configuration file
if [ -f "${PROJECT_ROOT}/.samstraumr.config" ]; then
  source "$(cd "$(dirname "${BASH_SOURCE[0]}")" source "${PROJECT_ROOT}/.samstraumr.configsource "${PROJECT_ROOT}/.samstraumr.config pwd)/../../.samstraumr/config.sh""
else
  echo "Configuration file not found: ${PROJECT_ROOT}/.samstraumr.config"
  exit 1
fi

echo -e "\033[0;34mRunning Above-The-Line (ATL) tests\033[0m"
echo "These are the critical tests that must pass for the build to be considered valid."

# Run the tests with the ATL profile
cd "${PROJECT_ROOT}" && mvn test -f "${SAMSTRAUMR_CORE_MODULE}/pom.xml" -P "${SAMSTRAUMR_ATL_PROFILE}" ${SAMSTRAUMR_RUN_TESTS}

# Check the result
if [ $? -eq 0 ]; then
  echo -e "\033[0;32mATL tests passed successfully\033[0m"
  echo "View the Cucumber report at: ${SAMSTRAUMR_CUCUMBER_REPORT}"
else
  echo -e "\033[0;31mATL tests failed\033[0m"
  echo "Please fix the failing tests before proceeding."
  exit 1
fi