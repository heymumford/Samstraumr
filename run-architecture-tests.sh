#!/bin/bash

# Script to run architecture validation tests
# This validates that all our architectural decisions are implemented correctly

# Source common functions
source ./util/lib/common.sh

# Display header
echo "=================================="
echo " Running Architecture Tests"
echo "=================================="
echo "Validating implementation of architecture decisions in ADRs"
echo

# Ensure using the correct Java version
if command -v ./use-java21.sh &> /dev/null; then
    echo "Using Java 21 for tests..."
    export JAVA_TOOL_OPTIONS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED"
fi

# Run the tests with Maven, showing only failures and errors by default
if [ "$1" = "--verbose" ] || [ "$1" = "-v" ]; then
    echo "Running in verbose mode..."
    cd Samstraumr && mvn test -Dtest=RunArchitectureTests
else
    echo "Running tests (use --verbose to see all output)..."
    cd Samstraumr && mvn test -Dtest=RunArchitectureTests -q
fi

# Check the result
if [ $? -eq 0 ]; then
    echo -e "\n\033[32mAll architecture tests PASSED ✅\033[0m"
    echo -e "The codebase complies with all architectural decisions.\n"
    exit 0
else
    echo -e "\n\033[31mSome architecture tests FAILED ❌\033[0m"
    echo -e "Please ensure all architectural decisions are properly implemented.\n"
    exit 1
fi