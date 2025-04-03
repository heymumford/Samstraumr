#!/bin/bash

# This script fixes the logger variable in PatternSteps.java
# by replacing all instances of the variable 'logger' with 'LOGGER'

PATTERN_STEPS_FILE="Samstraumr/samstraumr-core/src/test/java/org/samstraumr/tube/steps/PatternSteps.java"

echo "Fixing logger variables in $PATTERN_STEPS_FILE..."

# Use sed to replace all 'logger.' with 'LOGGER.'
sed -i 's/logger\./LOGGER\./g' "$PATTERN_STEPS_FILE"

echo "Logger variables fixed successfully."
echo "Running tests to verify the fix..."

# Run tests with quality checks skipped
mvn test -P skip-quality-checks -Dspotless.check.skip=true -Dpmd.skip=true -Dcheckstyle.skip=true -Dspotbugs.skip=true -Djacoco.skip=true -Dmaven.test.skip=false