#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

# This script fixes the logger variable in PatternSteps.java
# by replacing all instances of the variable 'logger' with 'LOGGER'

# Determine script directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"

# Source the configuration file
if [ -f "${PROJECT_ROOT}/.s8r.config" ]; then
  source "$(cd "$(dirname "${BASH_SOURCE[0]}")" source "${PROJECT_ROOT}/.s8r.configsource "${PROJECT_ROOT}/.s8r.config pwd)/../../.s8r/config.sh""
else
  echo "Configuration file not found: ${PROJECT_ROOT}/.s8r.config"
  exit 1
fi

# Construct file path using configuration variables
PATTERN_STEPS_FILE="${S8R_JAVA_TEST}/$(path_for_package "${S8R_TEST_PACKAGE}")/steps/PatternSteps.java"

echo "Fixing logger variables in $PATTERN_STEPS_FILE..."

# Use sed to replace all 'logger.' with 'LOGGER.'
sed -i 's/logger\./LOGGER\./g' "$PATTERN_STEPS_FILE"

echo "Logger variables fixed successfully."
echo "Running tests to verify the fix..."

# Run tests with quality checks skipped
mvn -f "${S8R_CORE_MODULE}/pom.xml" test -P "${S8R_SKIP_QUALITY_PROFILE}" ${S8R_SKIP_QUALITY} ${S8R_RUN_TESTS}