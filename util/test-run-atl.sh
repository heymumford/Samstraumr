#!/bin/bash
# Filename: run-atl-tests.sh
# Purpose: Runs Above The Line (ATL) tests for the Samstraumr project
# Location: util/test/
# Usage: ./run-atl-tests.sh
# 
# ATL tests are critical tests that must pass for the system to be considered functional.
# These are focused on L0_Tube tests that validate core functionality.

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Running Above The Line (ATL) tests...${NC}"
echo ""

# Set Maven options for faster builds and integrate JAVA_TOOL_OPTIONS
source "$(dirname "$0")/setup-java-env.sh"

# Add additional Maven options for faster builds
export MAVEN_OPTS="$MAVEN_OPTS -XX:+TieredCompilation -XX:TieredStopAtLevel=1"

# Apply Spotless formatting first to ensure code format is correct
echo -e "${YELLOW}Running code formatting with Spotless...${NC}"
mvn spotless:apply -q || {
    echo -e "${RED}Warning: Spotless formatting failed, proceeding with build anyway${NC}"
}

# Clean and install without running tests first
echo -e "${YELLOW}Building project without tests...${NC}"
mvn clean install -DskipTests -P skip-quality-checks

# Run only ATL tests
echo -e "${YELLOW}Running ATL tests...${NC}"
mvn clean install -Dmaven.test.skip=false -DskipTests=false -P atl-tests

# Check the exit status
EXIT_CODE=$?

if [ $EXIT_CODE -eq 0 ]; then
    echo -e "${GREEN}ATL tests PASSED!${NC}"
    exit 0
else
    echo -e "${RED}ATL tests FAILED!${NC}"
    echo ""
    echo -e "${YELLOW}Tips for fixing ATL tests:${NC}"
    echo "1. Check the test output for specific errors"
    echo "2. Common issues include:"
    echo "   - Type compatibility issues between Bundle and Composite"
    echo "   - Missing or incorrect test dependencies"
    echo "   - Incorrect import statements"
    echo "3. Use the RunTests class for debugging specific test failures"
    exit 1
fi