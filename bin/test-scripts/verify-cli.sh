#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# verify-cli.sh - Verify that the basic CLI commands are working
#==============================================================================
set -e

# Terminal colors
GREEN='\033[0;32m'
RED='\033[0;31m'
BOLD='\033[1m'
RESET='\033[0m'

TESTS_PASSED=0
TESTS_FAILED=0

# Test a command and check output
function test_command() {
  local description="$1"
  local command="$2"
  local expected_text="$3"
  
  echo -e "${BOLD}Testing:${RESET} $description"
  
  # Run the command and capture output
  local output
  if ! output=$($command 2>&1); then
    echo -e "  ${RED}✗ FAILED:${RESET} Command exited with non-zero status"
    echo -e "  Output: $output"
    TESTS_FAILED=$((TESTS_FAILED + 1))
    return 1
  fi
  
  # Check if output contains expected text
  if echo "$output" | grep -q "$expected_text"; then
    echo -e "  ${GREEN}✓ PASSED${RESET}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
  else
    echo -e "  ${RED}✗ FAILED:${RESET} Output missing '$expected_text'"
    echo -e "  Output: $output"
    TESTS_FAILED=$((TESTS_FAILED + 1))
    return 1
  fi
  
  return 0
}

# Run basic CLI verification tests
echo -e "${BOLD}Running basic CLI verification tests...${RESET}"
echo

# Test s8r component command
test_command "Component help command" "./s8r component --help" "Component Commands"

# Test s8r composite command
test_command "Composite help command" "./s8r composite --help" "Composite Commands"

# Test s8r machine command
test_command "Machine help command" "./s8r machine --help" "Machine Commands"

# Test s8r-dev command
test_command "s8r-dev help command" "./s8r-dev --help" "Development Tools"

# Print summary
echo
echo -e "${BOLD}Test Summary:${RESET}"
echo -e "  Passed: ${GREEN}$TESTS_PASSED${RESET}"
if [ $TESTS_FAILED -gt 0 ]; then
  echo -e "  Failed: ${RED}$TESTS_FAILED${RESET}"
  exit 1
else
  echo -e "  Failed: 0"
  echo -e "${GREEN}All basic CLI verification tests PASSED!${RESET}"
  exit 0
fi