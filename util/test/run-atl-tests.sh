#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

# Filename: run-atl-tests.sh
# Purpose: Run Above-The-Line (ATL) tests - critical tests that must pass
# Location: util/test/

# Determine script directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"

# Define color codes for terminal output
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RESET='\033[0m' # No Color

# Configuration variables
CORE_MODULE="modules/samstraumr-core"
ATL_PROFILE="atl-tests"
CUCUMBER_REPORT="target/atl-reports/cucumber.html"

echo -e "${BLUE}Running Above-The-Line (ATL) tests${RESET}"
echo "These are the critical tests that must pass for the build to be considered valid."

# Run the tests with the ATL profile
cd "${PROJECT_ROOT}" && mvn test -f "${CORE_MODULE}/pom.xml" -P "${ATL_PROFILE}" -Dtest=RunATLTests -DskipTests=false

# Check the result
if [ $? -eq 0 ]; then
  echo -e "${GREEN}ATL tests passed successfully${RESET}"
  echo "View the Cucumber report at: ${CUCUMBER_REPORT}"
else
  echo -e "${RED}ATL tests failed${RESET}"
  echo "Please fix the failing tests before proceeding."
  exit 1
fi