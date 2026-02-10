#!/usr/bin/env bash

# This script runs a single CLI test to verify functionality

# Set path variables
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
export PROJECT_ROOT="${SCRIPT_DIR}"

# Terminal colors
GREEN='\033[0;32m'
RED='\033[0;31m'
RESET='\033[0m'

# Run the s8r help command and capture output
output=$("$PROJECT_ROOT/s8r" machine --help 2>&1)
exit_code=$?

# Check if output contains expected text
if [[ "$output" == *"Machine Commands"* ]]; then
  echo -e "${GREEN}✓ Test passed - Output contains 'Machine Commands'${RESET}"
else
  echo -e "${RED}✗ Test failed - Output missing 'Machine Commands'${RESET}"
  echo "Actual output:"
  echo "$output"
  exit 1
fi

# Check exit code
if [[ $exit_code -eq 0 ]]; then
  echo -e "${GREEN}✓ Test passed - Exit code is 0${RESET}"
else
  echo -e "${RED}✗ Test failed - Exit code is $exit_code${RESET}"
  exit 1
fi

echo -e "${GREEN}All tests passed!${RESET}"
exit 0