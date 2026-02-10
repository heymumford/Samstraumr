#\!/bin/bash
#
# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.mozilla.org/en-US/MPL/2.0/
#

# Script to run port integration tests for the Samstraumr project

# Set project root
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

# Define constants
readonly TEST_PACKAGE="org.s8r.test.runner.PortIntegrationTests"
readonly TEST_TAGS="@L3_Integration @PortIntegration"
readonly DEFAULT_RUNNER="junit"

# Functions for output formatting
print_info() {
    echo -e "\033[0;34m[INFO]\033[0m $1"
}

print_success() {
    echo -e "\033[0;32m[SUCCESS]\033[0m $1"
}

print_error() {
    echo -e "\033[0;31m[ERROR]\033[0m $1" >&2
}

print_banner() {
    local text="$1"
    local length=${#text}
    local line=$(printf '%*s' "$length" | tr ' ' '=')
    
    echo -e "\n\033[1;36m$line\033[0m"
    echo -e "\033[1;36m$text\033[0m"
    echo -e "\033[1;36m$line\033[0m\n"
}

# Parse command line arguments
parse_args() {
  RUNNER=${1:-$DEFAULT_RUNNER}
  SPECIFIC_TEST=${2:-""}
}

# Run port integration tests with JUnit
run_junit_tests() {
  print_info "Running port integration tests with JUnit..."
  
  cd "${PROJECT_ROOT}/Samstraumr"
  
  if [[ -n "$SPECIFIC_TEST" ]]; then
    print_info "Running specific test: $SPECIFIC_TEST"
    mvn test -Dtest=$SPECIFIC_TEST
  else
    print_info "Running all port integration tests"
    mvn test -Dtest=$TEST_PACKAGE
  fi
}

# Run port integration tests with Cucumber
run_cucumber_tests() {
  print_info "Running port integration tests with Cucumber..."
  
  cd "${PROJECT_ROOT}/Samstraumr"
  
  if [[ -n "$SPECIFIC_TEST" ]]; then
    print_info "Running specific feature: $SPECIFIC_TEST"
    mvn test -Dcucumber.filter.tags="$TEST_TAGS" -Dcucumber.features="src/test/resources/features/integration/$SPECIFIC_TEST.feature"
  else
    print_info "Running all port integration tests"
    mvn test -Dcucumber.filter.tags="$TEST_TAGS"
  fi
}

# Main function
main() {
  print_banner "Samstraumr Port Integration Tests"
  
  parse_args "$@"
  
  case "$RUNNER" in
    junit)
      run_junit_tests
      ;;
    cucumber)
      run_cucumber_tests
      ;;
    *)
      print_error "Unknown runner: $RUNNER. Use 'junit' or 'cucumber'."
      exit 1
      ;;
  esac
  
  print_success "Port integration test execution complete\!"
}

# Execute main function
main "$@"
