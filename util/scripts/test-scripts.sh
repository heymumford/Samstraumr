#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# test-scripts.sh: Test suite for Samstraumr bash scripts
# Validates script functionality, performance, and correctness
#==============================================================================
set -e

# Determine script directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

# Terminal colors
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
CYAN='\033[0;36m'
BOLD='\033[1m'
RESET='\033[0m'

# Functions for prettier output
info() { echo -e "${BLUE}$1${RESET}"; }
success() { echo -e "${GREEN}$1${RESET}"; }
error() { echo -e "${RED}Error: $1${RESET}" >&2; exit 1; }
warn() { echo -e "${YELLOW}Warning: $1${RESET}" >&2; }
detail() { echo -e "${CYAN}$1${RESET}"; }

# Create test directory for temporary files
TEST_DIR="$PROJECT_ROOT/.test-scripts"
mkdir -p "$TEST_DIR"

# Track test statistics
TESTS_RUN=0
TESTS_PASSED=0
TESTS_FAILED=0
TESTS_SKIPPED=0

# Function to run a test
run_test() {
  local test_name="$1"
  local test_func="$2"
  local test_args="${@:3}"
  
  ((TESTS_RUN++))
  info "Running test: $test_name"
  
  # Create test output directory
  local test_output_dir="$TEST_DIR/${test_name//[^a-zA-Z0-9]/_}"
  mkdir -p "$test_output_dir"
  
  # Measure execution time
  local start_time=$(date +%s.%N)
  
  # Run the test function in a subshell
  (
    cd "$PROJECT_ROOT"
    if $test_func $test_args > "$test_output_dir/output.log" 2> "$test_output_dir/error.log"; then
      echo "PASS" > "$test_output_dir/result"
    else
      echo "FAIL" > "$test_output_dir/result"
    fi
  )
  
  # Calculate execution time
  local end_time=$(date +%s.%N)
  local execution_time=$(echo "$end_time - $start_time" | bc)
  
  # Check test result
  if [ "$(cat "$test_output_dir/result")" == "PASS" ]; then
    ((TESTS_PASSED++))
    success "✓ PASSED ($execution_time seconds)"
  else
    ((TESTS_FAILED++))
    echo -e "${RED}✗ FAILED ($execution_time seconds)${RESET}"
    echo
    detail "Output:"
    cat "$test_output_dir/output.log"
    echo
    detail "Errors:"
    cat "$test_output_dir/error.log"
    echo
  fi
}

# Function to skip a test
skip_test() {
  local test_name="$1"
  local reason="$2"
  
  ((TESTS_SKIPPED++))
  warn "Skipping test: $test_name - $reason"
}

# Display summary of test results
display_summary() {
  echo
  info "Test Summary"
  echo "============"
  echo "Total Tests: $TESTS_RUN"
  echo -e "${GREEN}Passed: $TESTS_PASSED${RESET}"
  if [ $TESTS_FAILED -gt 0 ]; then
    echo -e "${RED}Failed: $TESTS_FAILED${RESET}"
  else
    echo "Failed: 0"
  fi
  if [ $TESTS_SKIPPED -gt 0 ]; then
    echo -e "${YELLOW}Skipped: $TESTS_SKIPPED${RESET}"
  else 
    echo "Skipped: 0"
  fi
  echo "============"
  
  if [ $TESTS_FAILED -gt 0 ]; then
    exit 1
  fi
}

# ===== Script Tests =====

# Test s8r main script
test_s8r_main() {
  # Test that s8r script exists and is executable
  if [ ! -x "$PROJECT_ROOT/s8r" ]; then
    echo "s8r script not found or not executable"
    return 1
  fi
  
  # Check help output
  if ! "$PROJECT_ROOT/s8r" help | grep -q "Usage:"; then
    echo "s8r help command failed"
    return 1
  fi
  
  # Test version command
  if ! "$PROJECT_ROOT/s8r" version get > /dev/null; then
    echo "s8r version get command failed"
    return 1
  fi
  
  return 0
}

# Test s8r-version script
test_s8r_version() {
  # Test that s8r-version script exists and is executable
  if [ ! -x "$PROJECT_ROOT/s8r-version" ]; then
    echo "s8r-version script not found or not executable"
    return 1
  fi
  
  # Check current version
  if ! "$PROJECT_ROOT/s8r-version" get | grep -q -E "[0-9]+\.[0-9]+\.[0-9]+"; then
    echo "s8r-version get command failed to return valid version"
    return 1
  fi
  
  return 0
}

# Test s8r-test script
test_s8r_test() {
  # Test that s8r-test script exists and is executable
  if [ ! -x "$PROJECT_ROOT/s8r-test" ]; then
    echo "s8r-test script not found or not executable"
    return 1
  fi
  
  # Check help output
  if ! "$PROJECT_ROOT/s8r-test" --help | grep -q "Usage:"; then
    echo "s8r-test help command failed"
    return 1
  fi
  
  # Test command line argument parsing
  local output
  output=$("$PROJECT_ROOT/s8r-test" --help)
  if ! echo "$output" | grep -q "Test Types:"; then
    echo "s8r-test --help should show test types"
    return 1
  fi
  
  return 0
}

# Test s8r-build script
test_s8r_build() {
  # Test that s8r-build script exists and is executable
  if [ ! -x "$PROJECT_ROOT/s8r-build" ]; then
    echo "s8r-build script not found or not executable"
    return 1
  fi
  
  # Check help output
  if ! "$PROJECT_ROOT/s8r-build" --help | grep -q "Usage:"; then
    echo "s8r-build help command failed"
    return 1
  fi
  
  return 0
}

# Test s8r-ai-test script
test_s8r_ai_test() {
  # Test that s8r-ai-test script exists and is executable
  if [ ! -x "$PROJECT_ROOT/s8r-ai-test" ]; then
    echo "s8r-ai-test script not found or not executable"
    return 1
  fi
  
  # Check help output
  if ! "$PROJECT_ROOT/s8r-ai-test" --help | grep -q "Usage:"; then
    echo "s8r-ai-test help command failed"
    return 1
  fi
  
  # Test command line argument parsing for modes
  local output
  output=$("$PROJECT_ROOT/s8r-ai-test" --help)
  if ! echo "$output" | grep -q "Modes:"; then
    echo "s8r-ai-test --help should show available modes"
    return 1
  fi
  
  # Test ML tools check
  if ! "$PROJECT_ROOT/s8r-ai-test" --help | grep -q "analyze"; then
    echo "s8r-ai-test should support analyze mode"
    return 1
  fi
  
  return 0
}

# Test script performance
test_script_performance() {
  local max_startup_time=0.5  # Maximum acceptable startup time in seconds
  
  # Measure s8r startup time
  local start_time=$(date +%s.%N)
  "$PROJECT_ROOT/s8r" help > /dev/null
  local end_time=$(date +%s.%N)
  local startup_time=$(echo "$end_time - $start_time" | bc)
  
  echo "s8r startup time: $startup_time seconds"
  
  if (( $(echo "$startup_time > $max_startup_time" | bc -l) )); then
    echo "s8r startup time exceeds maximum acceptable time of $max_startup_time seconds"
    return 1
  fi
  
  # Measure s8r-version startup time
  start_time=$(date +%s.%N)
  "$PROJECT_ROOT/s8r-version" get > /dev/null
  end_time=$(date +%s.%N)
  startup_time=$(echo "$end_time - $start_time" | bc)
  
  echo "s8r-version startup time: $startup_time seconds"
  
  if (( $(echo "$startup_time > $max_startup_time" | bc -l) )); then
    echo "s8r-version startup time exceeds maximum acceptable time of $max_startup_time seconds"
    return 1
  fi
  
  return 0
}

# Test script dependencies
test_script_dependencies() {
  # Check that all required tools are available
  local dependencies=("git" "bc" "grep" "awk" "sed")
  local missing_deps=()
  
  for dep in "${dependencies[@]}"; do
    if ! command -v "$dep" > /dev/null; then
      missing_deps+=("$dep")
    fi
  done
  
  if [ ${#missing_deps[@]} -gt 0 ]; then
    echo "Missing dependencies: ${missing_deps[*]}"
    return 1
  fi
  
  # Check Python availability for AI test
  if ! command -v python3 > /dev/null; then
    warn "Python 3 not available - AI testing features will be limited"
  else
    # Check for pandas (optional dependency for AI testing)
    if ! python3 -c "import pandas" &> /dev/null; then
      warn "Python pandas not available - AI testing features will be limited"
    fi
  fi
  
  return 0
}

# Test s8r integration
test_s8r_integration() {
  # Create a test project structure
  local test_project="$TEST_DIR/test_project"
  rm -rf "$test_project"
  mkdir -p "$test_project"
  
  # Copy s8r scripts
  cp "$PROJECT_ROOT/s8r" "$test_project/"
  cp "$PROJECT_ROOT/s8r-version" "$test_project/"
  cp "$PROJECT_ROOT/s8r-test" "$test_project/"
  cp "$PROJECT_ROOT/s8r-build" "$test_project/"
  cp "$PROJECT_ROOT/s8r-ai-test" "$test_project/"
  
  # Test basic integration
  cd "$test_project"
  
  if ! ./s8r version get > /dev/null; then
    echo "s8r integration test failed: version command failed"
    return 1
  fi
  
  if ! ./s8r-test --help > /dev/null; then
    echo "s8r integration test failed: s8r-test help command failed"
    return 1
  fi
  
  if ! ./s8r-ai-test --help > /dev/null; then
    echo "s8r integration test failed: s8r-ai-test help command failed"
    return 1
  fi
  
  # Return to original directory
  cd "$PROJECT_ROOT"
  
  return 0
}

# ===== Main Execution =====

# Parse command line arguments
RUN_ALL=true
VERBOSE=false

for arg in "$@"; do
  case "$arg" in
    --verbose|-v)
      VERBOSE=true
      ;;
    --help|-h)
      echo "Usage: $0 [--verbose|-v] [test_name]"
      echo
      echo "Available tests:"
      echo "  main            Test s8r main script"
      echo "  version         Test s8r-version script"
      echo "  test            Test s8r-test script"
      echo "  build           Test s8r-build script"
      echo "  ai-test         Test s8r-ai-test script"
      echo "  performance     Test script performance"
      echo "  dependencies    Test script dependencies"
      echo "  integration     Test s8r integration"
      echo "  all             Run all tests (default)"
      exit 0
      ;;
    *)
      if [[ "$arg" =~ ^(main|version|test|build|ai-test|performance|dependencies|integration|all)$ ]]; then
        RUN_ALL=false
        eval "RUN_${arg^^}=true"
      else
        error "Unknown test: $arg"
      fi
      ;;
  esac
done

# Display test header
echo -e "${BOLD}Samstraumr Script Tests${RESET}"
echo "========================"
echo "Date: $(date)"
echo "Project Root: $PROJECT_ROOT"
echo "========================"
echo

# Run selected tests
if [ "$RUN_ALL" = true ] || [ "${RUN_MAIN:-false}" = true ]; then
  run_test "s8r main script" test_s8r_main
fi

if [ "$RUN_ALL" = true ] || [ "${RUN_VERSION:-false}" = true ]; then
  run_test "s8r-version script" test_s8r_version
fi

if [ "$RUN_ALL" = true ] || [ "${RUN_TEST:-false}" = true ]; then
  run_test "s8r-test script" test_s8r_test
fi

if [ "$RUN_ALL" = true ] || [ "${RUN_BUILD:-false}" = true ]; then
  run_test "s8r-build script" test_s8r_build
fi

if [ "$RUN_ALL" = true ] || [ "${RUN_AI_TEST:-false}" = true ]; then
  run_test "s8r-ai-test script" test_s8r_ai_test
fi

if [ "$RUN_ALL" = true ] || [ "${RUN_PERFORMANCE:-false}" = true ]; then
  run_test "Script performance" test_script_performance
fi

if [ "$RUN_ALL" = true ] || [ "${RUN_DEPENDENCIES:-false}" = true ]; then
  run_test "Script dependencies" test_script_dependencies
fi

if [ "$RUN_ALL" = true ] || [ "${RUN_INTEGRATION:-false}" = true ]; then
  run_test "s8r integration" test_s8r_integration
fi

# Display test summary
display_summary