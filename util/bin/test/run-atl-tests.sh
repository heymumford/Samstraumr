#!/bin/bash
#==============================================================================
# Filename: run-atl-tests.sh
# Description: Run Above-The-Line (ATL) tests for Samstraumr
#==============================================================================
# Usage: ./run-atl-tests.sh [options]
#
# Options:
#   --verbose                 Enable verbose output
#   --output-file <file>      Write test output to file
#   --profile <profile>       Use specific Maven profile
#   --clean                   Clean project before running tests
#   --skip-quality            Skip quality checks
#
# This script runs Above-The-Line (ATL) tests, which are critical tests
# that verify core functionality.
#==============================================================================

# Determine script directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../../.." && pwd)"

# Source common functions if available
if [ -f "${PROJECT_ROOT}/util/lib/common.sh" ]; then
  source "${PROJECT_ROOT}/util/lib/common.sh"
fi

if [ -f "${PROJECT_ROOT}/util/lib/test-lib.sh" ]; then
  source "${PROJECT_ROOT}/util/lib/test-lib.sh"
fi

# Set default options
VERBOSE=false
CLEAN=false
SKIP_QUALITY=false
OUTPUT_FILE=""
PROFILE="atl-tests"
CYCLE_NAME=""

# Parse command-line arguments
while [[ $# -gt 0 ]]; do
  case "$1" in
    --verbose)
      VERBOSE=true
      shift
      ;;
    --output-file)
      OUTPUT_FILE="$2"
      shift 2
      ;;
    --profile)
      PROFILE="$2"
      shift 2
      ;;
    --clean)
      CLEAN=true
      shift
      ;;
    --skip-quality)
      SKIP_QUALITY=true
      shift
      ;;
    --cyclename)
      CYCLE_NAME="$2"
      shift 2
      ;;
    *)
      echo "Unknown option: $1"
      exit 1
      ;;
  esac
done

# Build Maven command
MAVEN_CMD="mvn test"

# Add profile
if [ -n "$PROFILE" ]; then
  MAVEN_CMD="$MAVEN_CMD -P $PROFILE -DskipTests=false"
fi

# Add clean if requested (Put clean before test)
if $CLEAN; then
  MAVEN_CMD="mvn clean ${MAVEN_CMD#mvn}"
fi

# Add skip quality if requested
if $SKIP_QUALITY; then
  MAVEN_CMD="$MAVEN_CMD -P skip-quality-checks"
fi

# Add verbose flag for Maven
if $VERBOSE; then
  # We're setting our own verbose flag for test status reporting
  # Don't use -X for Maven as it's too verbose - we'll handle our own output
  # But don't quiet Maven either - we want to see test results
  :  # No-op to avoid empty if statement
else
  MAVEN_CMD="$MAVEN_CMD -q"
fi

# Change to project directory
cd "${PROJECT_ROOT}/Samstraumr" || exit 1

# Define color codes for status reporting (using different variable names to avoid conflicts)
STATUS_RED='\033[0;31m'
STATUS_GREEN='\033[0;32m'
STATUS_YELLOW='\033[0;33m'
STATUS_BLUE='\033[0;34m'
STATUS_CYAN='\033[0;36m'
STATUS_GRAY='\033[0;37m'
STATUS_BOLD='\033[1m'
STATUS_RESET='\033[0m'

# Function to print test status in verbose mode
print_test_status() {
  local test_name="$1"
  local status="$2"
  local timestamp=$(date +"%H:%M:%S")
  
  case "$status" in
    "no run")
      status_color="${STATUS_GRAY}"
      ;;
    "running")
      status_color="${STATUS_BLUE}"
      ;;
    "pass")
      status_color="${STATUS_GREEN}"
      ;;
    "fail")
      status_color="${STATUS_RED}"
      ;;
    "blocked")
      status_color="${STATUS_YELLOW}"
      ;;
    "error")
      status_color="${STATUS_RED}"
      ;;
    *)
      status_color="${STATUS_RESET}"
      ;;
  esac
  
  if [ -n "$CYCLE_NAME" ]; then
    echo -e "[${timestamp}] ${STATUS_CYAN}${CYCLE_NAME}${STATUS_RESET} - ${STATUS_BOLD}${test_name}${STATUS_RESET}: ${status_color}${status}${STATUS_RESET}"
  else
    echo -e "[${timestamp}] ${STATUS_BOLD}${test_name}${STATUS_RESET}: ${status_color}${status}${STATUS_RESET}"
  fi
}

# Run tests
echo "Running ATL tests with profile: $PROFILE"

# Print cycle name if provided
if $VERBOSE && [ -n "$CYCLE_NAME" ]; then
  echo -e "${STATUS_CYAN}${STATUS_BOLD}Test Cycle: ${CYCLE_NAME}${STATUS_RESET}"
fi

# If running in verbose mode, extract and display test execution status
if $VERBOSE; then
  # Define ATL test group
  ATL_GROUP="Above-The-Line Tests"
  
  # Mark ATL group as "no run" initially
  print_test_status "$ATL_GROUP" "no run"
  
  # Run Maven command and capture output
  if [ -n "$OUTPUT_FILE" ]; then
    echo "Writing output to: $OUTPUT_FILE"
    
    # Run the command with output processing for verbose reporting
    {
      # Mark ATL group as running
      print_test_status "$ATL_GROUP" "running"
      
      $MAVEN_CMD 2>&1 | tee "$OUTPUT_FILE" | while IFS= read -r line; do
        # Echo the line to show Maven output
        echo "$line"
        
        # Look for different test execution markers in the Maven output
        # and update the status display accordingly
        if [[ "$line" == *"Running org.samstraumr.tube."* ]]; then
          # Extract the test class name for detailed reporting
          test_name=$(echo "$line" | grep -o "Running org.samstraumr.tube.[^ ]*" | sed 's/Running //')
          print_test_status "$test_name" "running"
        elif [[ "$line" == *"Tests run:"* ]]; then
          # Extract test run statistics
          local tests_run=$(echo "$line" | grep -o "Tests run: [0-9]*" | awk '{print $3}')
          local failures=$(echo "$line" | grep -o "Failures: [0-9]*" | awk '{print $2}')
          local errors=$(echo "$line" | grep -o "Errors: [0-9]*" | awk '{print $2}')
          local skipped=$(echo "$line" | grep -o "Skipped: [0-9]*" | awk '{print $2}')
          
          if [[ "$failures" != "0" || "$errors" != "0" ]]; then
            print_test_status "$test_name" "fail"
          else
            print_test_status "$test_name" "pass"
          fi
        fi
      done
    }
    MAVEN_EXIT_CODE=${PIPESTATUS[0]}
  else
    # Run the command with output processing for verbose reporting
    {
      # Mark ATL group as running
      print_test_status "$ATL_GROUP" "running"
      
      $MAVEN_CMD 2>&1 | while IFS= read -r line; do
        # Echo the line to show Maven output
        echo "$line"
        
        # Look for different test execution markers in the Maven output
        # and update the status display accordingly
        if [[ "$line" == *"Running org.samstraumr.tube."* ]]; then
          # Extract the test class name for detailed reporting
          test_name=$(echo "$line" | grep -o "Running org.samstraumr.tube.[^ ]*" | sed 's/Running //')
          print_test_status "$test_name" "running"
        elif [[ "$line" == *"Tests run:"* ]]; then
          # Extract test run statistics
          local tests_run=$(echo "$line" | grep -o "Tests run: [0-9]*" | awk '{print $3}')
          local failures=$(echo "$line" | grep -o "Failures: [0-9]*" | awk '{print $2}')
          local errors=$(echo "$line" | grep -o "Errors: [0-9]*" | awk '{print $2}')
          local skipped=$(echo "$line" | grep -o "Skipped: [0-9]*" | awk '{print $2}')
          
          if [[ "$failures" != "0" || "$errors" != "0" ]]; then
            print_test_status "$test_name" "fail"
          else
            print_test_status "$test_name" "pass"
          fi
        fi
      done
    }
    MAVEN_EXIT_CODE=${PIPESTATUS[0]}
  fi
  
  # Update final status for ATL group
  if [ $MAVEN_EXIT_CODE -eq 0 ]; then
    print_test_status "$ATL_GROUP" "pass"
    echo -e "\n${STATUS_GREEN}ATL tests completed successfully!${STATUS_RESET}"
    exit 0
  else
    print_test_status "$ATL_GROUP" "fail"
    echo -e "\n${STATUS_RED}ATL tests failed${STATUS_RESET}"
    exit 1
  fi
else
  # Non-verbose mode - simple output
  if [ -n "$OUTPUT_FILE" ]; then
    echo "Writing output to: $OUTPUT_FILE"
    $MAVEN_CMD | tee "$OUTPUT_FILE"
    MAVEN_EXIT_CODE=${PIPESTATUS[0]}
  else
    $MAVEN_CMD
    MAVEN_EXIT_CODE=$?
  fi

  # Check result
  if [ $MAVEN_EXIT_CODE -eq 0 ]; then
    echo -e "\033[0;32mATL tests completed successfully!\033[0m"
    exit 0
  else
    echo -e "\033[0;31mATL tests failed\033[0m"
    exit 1
  fi
fi
