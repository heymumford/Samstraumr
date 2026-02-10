#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#
# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

# Script to run all test types to verify the testing pyramid

set -e

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Define directory paths
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
MODULES_DIR="${PROJECT_ROOT}/modules"
TEST_RESULTS_DIR="${PROJECT_ROOT}/test-results"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
TEST_LOG="${TEST_RESULTS_DIR}/test_results_${TIMESTAMP}.txt"

# Ensure test results directory exists
mkdir -p "$TEST_RESULTS_DIR"

# Print functions
function print_header() {
  echo -e "${BLUE}====== $1 ======${NC}"
}

function print_success() {
  echo -e "${GREEN}✓ SUCCESS: $1${NC}"
}

function print_failure() {
  echo -e "${RED}✗ FAILED: $1${NC}"
}

function print_info() {
  echo -e "${YELLOW}ℹ INFO: $1${NC}"
}

# Function to run tests with Maven
function run_tests() {
  local test_type="$1"
  local tag_expression="$2"
  local test_class="$3"
  
  print_header "Running $test_type tests"
  echo "Testing with tags: $tag_expression"
  echo "Test class: $test_class"
  
  cd "$PROJECT_ROOT"
  
  # Construct Maven command
  local cmd="mvn -f modules/pom.xml test -DskipTests=false -Dtest=$test_class -Dcucumber.filter.tags=\"$tag_expression\""
  
  echo "Running: $cmd"
  echo "Results will be logged to: $TEST_LOG"
  
  # Run tests and capture output
  if eval "$cmd" | tee -a "$TEST_LOG"; then
    print_success "$test_type tests passed"
    return 0
  else
    print_failure "$test_type tests failed"
    return 1
  fi
}

# Function to count tests by type
function count_tests() {
  local test_type="$1"
  local pattern="$2"
  
  local count=0
  
  case "$test_type" in
    "Java Tests")
      count=$(find "$MODULES_DIR" -name "*Test.java" | grep -i "$pattern" | wc -l)
      ;;
    "BDD Scenarios")
      count=$(grep -r "Scenario:" --include="*.feature" "$MODULES_DIR" | grep -i "$pattern" | wc -l)
      ;;
    "Test Methods")
      count=$(grep -r "@Test" --include="*Test.java" "$MODULES_DIR" | grep -i "$pattern" | wc -l)
      ;;
    *)
      count="Unknown"
      ;;
  esac
  
  echo "$count"
}

# Run a test of the testing system
print_header "Testing Pyramid Analysis"

# Count different types of tests
UNIT_TEST_COUNT=$(count_tests "Java Tests" "unit\\|component")
COMPONENT_TEST_COUNT=$(count_tests "Java Tests" "composite")
INTEGRATION_TEST_COUNT=$(count_tests "Java Tests" "integration")
SYSTEM_TEST_COUNT=$(count_tests "Java Tests" "system")

BDD_UNIT_COUNT=$(count_tests "BDD Scenarios" "component\\|unit")
BDD_COMPONENT_COUNT=$(count_tests "BDD Scenarios" "composite")
BDD_INTEGRATION_COUNT=$(count_tests "BDD Scenarios" "integration\\|machine")
BDD_SYSTEM_COUNT=$(count_tests "BDD Scenarios" "system\\|acceptance")

TEST_METHOD_COUNT=$(count_tests "Test Methods" "")

# Display test counts
echo "===== Test Count Analysis ====="
echo "Java Unit Tests: $UNIT_TEST_COUNT"
echo "Java Component Tests: $COMPONENT_TEST_COUNT"
echo "Java Integration Tests: $INTEGRATION_TEST_COUNT"
echo "Java System Tests: $SYSTEM_TEST_COUNT"
echo ""
echo "BDD Unit/Component Level Scenarios: $BDD_UNIT_COUNT"
echo "BDD Composite Scenarios: $BDD_COMPONENT_COUNT"
echo "BDD Integration/Machine Scenarios: $BDD_INTEGRATION_COUNT"
echo "BDD System/Acceptance Scenarios: $BDD_SYSTEM_COUNT"
echo ""
echo "Total @Test methods: $TEST_METHOD_COUNT"
echo ""

# Check if test pyramid is balanced
print_header "Testing Pyramid Balance"

# Calculate percentages
TOTAL_TESTS=$((UNIT_TEST_COUNT + COMPONENT_TEST_COUNT + INTEGRATION_TEST_COUNT + SYSTEM_TEST_COUNT))
TOTAL_BDD=$((BDD_UNIT_COUNT + BDD_COMPONENT_COUNT + BDD_INTEGRATION_COUNT + BDD_SYSTEM_COUNT))

if [ $TOTAL_TESTS -eq 0 ]; then
  echo "No tests found. Unable to analyze test pyramid."
  exit 1
fi

UNIT_PCT=$((UNIT_TEST_COUNT * 100 / (TOTAL_TESTS + 1)))
COMPONENT_PCT=$((COMPONENT_TEST_COUNT * 100 / (TOTAL_TESTS + 1)))
INTEGRATION_PCT=$((INTEGRATION_TEST_COUNT * 100 / (TOTAL_TESTS + 1)))
SYSTEM_PCT=$((SYSTEM_TEST_COUNT * 100 / (TOTAL_TESTS + 1)))

echo "===== Test Pyramid Analysis ====="
echo "Unit Tests: $UNIT_PCT%"
echo "Component Tests: $COMPONENT_PCT%"
echo "Integration Tests: $INTEGRATION_PCT%"
echo "System Tests: $SYSTEM_PCT%"
echo ""

# Check if pyramid is inverted
if [ $UNIT_PCT -lt $SYSTEM_PCT ]; then
  print_failure "Your testing pyramid is inverted! You have more system tests than unit tests."
  print_info "Consider adding more unit tests to improve test coverage and performance."
elif [ $UNIT_PCT -lt 50 ]; then
  print_failure "Your testing pyramid is unbalanced. Unit tests should be at least 50% of your test suite."
  print_info "Consider adding more unit tests for better coverage and faster test runs."
else
  print_success "Your testing pyramid has a good shape with more unit tests than system tests."
fi

# Run unit tests
run_tests "Unit" "@UnitTest or @ComponentTest or @L0_Component" "org.s8r.test.runner.UnitTestRunner"

# Run component tests
run_tests "Component" "@CompositeTest or @L1_Composite" "org.s8r.test.runner.CompositeTestRunner"

# Run integration tests 
run_tests "Integration" "@IntegrationTest or @L2_Machine" "org.s8r.test.runner.IntegrationTestRunner"

# Run architecture tests
run_tests "Architecture" "@Architecture" "org.s8r.architecture.RunArchitectureTests"

print_header "Test Run Summary"
echo "All tests have been executed."
echo "Test results are available in: $TEST_LOG"

exit 0