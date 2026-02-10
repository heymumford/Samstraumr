#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# Shorter version of the CLI acceptance tests for Samstraumr
#==============================================================================
set -e

# Determine script directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
export PROJECT_ROOT="${SCRIPT_DIR}"

# Terminal colors
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
CYAN='\033[0;36m'
BOLD='\033[1m'
RESET='\033[0m'

# Test counters
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# Test utilities
function test_start() {
  local test_name="$1"
  echo -e "${BLUE}Starting test:${RESET} ${BOLD}$test_name${RESET}"
  TOTAL_TESTS=$((TOTAL_TESTS + 1))
}

function test_pass() {
  local test_name="$1"
  echo -e "  ${GREEN}✓ PASS:${RESET} $test_name"
  PASSED_TESTS=$((PASSED_TESTS + 1))
}

function test_fail() {
  local test_name="$1"
  local error_msg="$2"
  echo -e "  ${RED}✗ FAIL:${RESET} $test_name"
  echo -e "  ${RED}      $error_msg${RESET}"
  FAILED_TESTS=$((FAILED_TESTS + 1))
}

function assert_contains() {
  local output="$1"
  local expected="$2"
  local test_name="$3"
  
  if [[ "$output" == *"$expected"* ]]; then
    test_pass "$test_name - contains '$expected'"
  else
    test_fail "$test_name" "Expected output to contain '$expected', but it didn't"
    echo -e "  ${YELLOW}Actual output:${RESET}"
    echo "$output" | sed 's/^/      /'
  fi
}

function run_command() {
  local command="$1"
  local output
  local exit_code=0
  
  # Run the command and capture both output and exit code
  # We use a temporary file to avoid issues with command substitution
  local tmp_file=$(mktemp)
  
  # Run command and capture exit code
  # Use the command directly from the current directory
  cd "$PROJECT_ROOT" && ./$command > "$tmp_file" 2>&1 || exit_code=$?
  
  # Read output from temp file
  output=$(cat "$tmp_file")
  rm -f "$tmp_file"
  
  # Return the output (for capture by the caller)
  echo "$output"
  
  # Store exit code for later retrieval
  export LAST_EXIT_CODE=$exit_code
  
  # Return success so we don't break the test flow
  return 0
}

# Simple test of the component help command
function test_component_help() {
  test_start "component - Help command"
  local output
  output=$(run_command "s8r component --help")
  assert_contains "$output" "Component Commands" "Help output shows title"
  assert_contains "$output" "create" "Help output lists create subcommand"
  assert_contains "$output" "list" "Help output lists list subcommand"
}

# Simple test of the composite help command
function test_composite_help() {
  test_start "composite - Help command"
  local output
  output=$(run_command "s8r composite --help")
  assert_contains "$output" "Composite Commands" "Help output shows title"
  assert_contains "$output" "create" "Help output lists create subcommand"
  assert_contains "$output" "add" "Help output lists add subcommand"
}

# Simple test of the machine help command
function test_machine_help() {
  test_start "machine - Help command"
  local output
  output=$(run_command "s8r machine --help")
  assert_contains "$output" "Machine Commands" "Help output shows title"
  assert_contains "$output" "create" "Help output lists create subcommand"
  assert_contains "$output" "start" "Help output lists start subcommand"
}

# Run test suites
echo -e "${BOLD}${CYAN}S8r CLI Acceptance Tests (Short Version)${RESET}"
echo -e "${CYAN}================================${RESET}"
echo

# Run tests
test_component_help
test_composite_help
test_machine_help

# Print test summary
echo
echo -e "${CYAN}================================${RESET}"
echo -e "${BOLD}Test Summary:${RESET}"
echo -e "  Total tests:  ${TOTAL_TESTS}"
echo -e "  ${GREEN}Passed tests: ${PASSED_TESTS}${RESET}"
if [ $FAILED_TESTS -gt 0 ]; then
  echo -e "  ${RED}Failed tests: ${FAILED_TESTS}${RESET}"
  exit 1
else
  echo -e "  Failed tests: 0"
  echo -e "${GREEN}All tests passed!${RESET}"
  exit 0
fi