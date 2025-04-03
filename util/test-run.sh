#!/bin/bash
# Main test runner script

# Default to running all tests if no argument is provided
TEST_TYPE=${1:-all}

# Forward to the appropriate script based on the argument
echo -e "\033[0;34mRunning tests: ${TEST_TYPE}\033[0m"

# Use the test-run-all.sh script
./util/test-run-all.sh "$@"
