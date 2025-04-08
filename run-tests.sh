#!/bin/bash
# Script to run all tests

# Set colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Running all tests${NC}"

# Run Maven tests
echo -e "${GREEN}Running: mvn test${NC}"
mvn test

# If you want to run specific test types, uncomment one of these:
# echo -e "${GREEN}Running: mvn test -Punit-tests${NC}"
# mvn test -Punit-tests

# echo -e "${GREEN}Running: mvn test -Pcomponent-tests${NC}"
# mvn test -Pcomponent-tests

# echo -e "${GREEN}Running: mvn test -Pall-tests${NC}"
# mvn test -Pall-tests

echo -e "${YELLOW}Test run complete${NC}"