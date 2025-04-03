#!/bin/bash

# Script to run Above The Line (ATL) tests for the Samstraumr project

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Running Above The Line (ATL) tests...${NC}"
echo ""

# Set Maven options for faster builds
export MAVEN_OPTS="-Xmx1g -XX:+TieredCompilation -XX:TieredStopAtLevel=1"

# Clean and install without running tests first
echo -e "${YELLOW}Building project without tests...${NC}"
mvn clean install -DskipTests -P skip-quality-checks

# Run only ATL tests
echo -e "${YELLOW}Running ATL tests...${NC}"
mvn clean install -Dmaven.test.skip=false -DskipTests=false -P atl-tests

# Check the exit status
if [ $? -eq 0 ]; then
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