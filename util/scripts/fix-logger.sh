#!/bin/bash
# This script fixes the logger variable in PatternSteps.java
# by replacing all instances of the variable 'logger' with 'LOGGER'

# Determine script directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"

# Source the configuration file
if [ -f "${PROJECT_ROOT}/.samstraumr.config" ]; then
  source "${PROJECT_ROOT}/.samstraumr.config"
else
  echo "Configuration file not found: ${PROJECT_ROOT}/.samstraumr.config"
  exit 1
fi

# Construct file path using configuration variables
PATTERN_STEPS_FILE="${SAMSTRAUMR_JAVA_TEST}/$(path_for_package "${SAMSTRAUMR_TEST_PACKAGE}")/steps/PatternSteps.java"

echo "Fixing logger variables in $PATTERN_STEPS_FILE..."

# Use sed to replace all 'logger.' with 'LOGGER.'
sed -i 's/logger\./LOGGER\./g' "$PATTERN_STEPS_FILE"

echo "Logger variables fixed successfully."
echo "Running tests to verify the fix..."

# Run tests with quality checks skipped
mvn -f "${SAMSTRAUMR_CORE_MODULE}/pom.xml" test -P "${SAMSTRAUMR_SKIP_QUALITY_PROFILE}" ${SAMSTRAUMR_SKIP_QUALITY} ${SAMSTRAUMR_RUN_TESTS}