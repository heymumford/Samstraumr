#!/bin/bash
#==============================================================================
# Filename: run-tests.sh
# Description: Run tests for Samstraumr
#==============================================================================
# Usage: ./run-tests.sh <test-type> [options]
#
# Options:
#   --verbose                 Enable verbose output
#   --output-file <file>      Write test output to file
#   --profile <profile>       Use specific Maven profile
#   --clean                   Clean project before running tests
#   --skip-quality            Skip quality checks
#   --both                    Include equivalent tests from other terminology
#
# Test types:
#   Industry Standard      |  Samstraumr Equivalent
#   --------------------   |  ----------------------
#   smoke                  |  orchestration
#   unit                   |  tube
#   component              |  composite
#   integration            |  flow
#   api                    |  machine
#   system                 |  stream
#   endtoend               |  acceptance
#   property               |  adaptation
#
#   Special test types: all, atl, btl, adam
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

# Check that test type is provided
if [ $# -lt 1 ]; then
  echo "Error: Missing test type argument"
  echo "Usage: $0 <test-type> [options]"
  exit 1
fi

# Extract test type from arguments
TEST_TYPE="$1"
shift

# Set default options
VERBOSE=false
CLEAN=false
SKIP_QUALITY=false
INCLUDE_BOTH=false
OUTPUT_FILE=""
PROFILE=""
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
    --both)
      INCLUDE_BOTH=true
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

# Map test type to profile
case "$TEST_TYPE" in
  unit|tube)
    if [ -z "$PROFILE" ]; then
      if [ "$TEST_TYPE" = "unit" ]; then
        PROFILE="unit-tests"
      else
        PROFILE="tube-tests"
      fi
      
      # Include both if requested
      if $INCLUDE_BOTH; then
        PROFILE="$PROFILE,tube-tests,unit-tests"
      fi
    fi
    ;;
  component|composite)
    if [ -z "$PROFILE" ]; then
      if [ "$TEST_TYPE" = "component" ]; then
        PROFILE="component-tests"
      else
        PROFILE="composite-tests"
      fi
      
      # Include both if requested
      if $INCLUDE_BOTH; then
        PROFILE="$PROFILE,composite-tests,component-tests"
      fi
    fi
    ;;
  integration|flow)
    if [ -z "$PROFILE" ]; then
      if [ "$TEST_TYPE" = "integration" ]; then
        PROFILE="integration-tests"
      else
        PROFILE="flow-tests"
      fi
      
      # Include both if requested
      if $INCLUDE_BOTH; then
        PROFILE="$PROFILE,flow-tests,integration-tests"
      fi
    fi
    ;;
  api|machine)
    if [ -z "$PROFILE" ]; then
      if [ "$TEST_TYPE" = "api" ]; then
        PROFILE="api-tests"
      else
        PROFILE="machine-tests"
      fi
      
      # Include both if requested
      if $INCLUDE_BOTH; then
        PROFILE="$PROFILE,machine-tests,api-tests"
      fi
    fi
    ;;
  system|stream)
    if [ -z "$PROFILE" ]; then
      if [ "$TEST_TYPE" = "system" ]; then
        PROFILE="system-tests"
      else
        PROFILE="stream-tests"
      fi
      
      # Include both if requested
      if $INCLUDE_BOTH; then
        PROFILE="$PROFILE,stream-tests,system-tests"
      fi
    fi
    ;;
  endtoend|acceptance)
    if [ -z "$PROFILE" ]; then
      if [ "$TEST_TYPE" = "endtoend" ]; then
        PROFILE="endtoend-tests"
      else
        PROFILE="acceptance-tests"
      fi
      
      # Include both if requested
      if $INCLUDE_BOTH; then
        PROFILE="$PROFILE,acceptance-tests,endtoend-tests"
      fi
    fi
    ;;
  smoke|orchestration)
    if [ -z "$PROFILE" ]; then
      if [ "$TEST_TYPE" = "smoke" ]; then
        PROFILE="smoke-tests"
      else
        PROFILE="orchestration-tests"
      fi
      
      # Include both if requested
      if $INCLUDE_BOTH; then
        PROFILE="$PROFILE,orchestration-tests,smoke-tests"
      fi
    fi
    ;;
  property|adaptation)
    if [ -z "$PROFILE" ]; then
      if [ "$TEST_TYPE" = "property" ]; then
        PROFILE="property-tests"
      else
        PROFILE="adaptation-tests"
      fi
      
      # Include both if requested
      if $INCLUDE_BOTH; then
        PROFILE="$PROFILE,adaptation-tests,property-tests"
      fi
    fi
    ;;
  atl)
    if [ -z "$PROFILE" ]; then
      PROFILE="atl-tests"
    fi
    ;;
  btl)
    if [ -z "$PROFILE" ]; then
      PROFILE="btl-tests"
    fi
    ;;
  adam)
    if [ -z "$PROFILE" ]; then
      PROFILE="adam-tube-tests"
    fi
    ;;
  all)
    if [ -z "$PROFILE" ]; then
      PROFILE="all-tests"
    fi
    ;;
  *)
    echo "Error: Unknown test type: $TEST_TYPE"
    exit 1
    ;;
esac

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
echo "Running tests: $TEST_TYPE with profile: $PROFILE"

# Print cycle name if provided
if $VERBOSE && [ -n "$CYCLE_NAME" ]; then
  echo -e "${STATUS_CYAN}${STATUS_BOLD}Test Cycle: ${CYCLE_NAME}${STATUS_RESET}"
fi

# If running in verbose mode, extract and display test execution status
if $VERBOSE; then
  # Define test groups based on the test type
  declare -a TEST_GROUPS
  
  case "$TEST_TYPE" in
    unit|tube)
      TEST_GROUPS=("Unit Tests" "Tube Tests")
      ;;
    component|composite)
      TEST_GROUPS=("Component Tests" "Composite Tests")
      ;;
    integration|flow)
      TEST_GROUPS=("Integration Tests" "Flow Tests")
      ;;
    api|machine)
      TEST_GROUPS=("API Tests" "Machine Tests")
      ;;
    system|stream)
      TEST_GROUPS=("System Tests" "Stream Tests")
      ;;
    endtoend|acceptance)
      TEST_GROUPS=("End-to-End Tests" "Acceptance Tests")
      ;;
    property|adaptation)
      TEST_GROUPS=("Property Tests" "Adaptation Tests")
      ;;
    smoke|orchestration)
      TEST_GROUPS=("Smoke Tests" "Orchestration Tests")
      ;;
    atl)
      TEST_GROUPS=("Above-The-Line Tests")
      ;;
    btl)
      TEST_GROUPS=("Below-The-Line Tests")
      ;;
    adam)
      TEST_GROUPS=("Adam Tube Tests")
      ;;
    all)
      TEST_GROUPS=("All Tests")
      ;;
    *)
      TEST_GROUPS=("Tests")
      ;;
  esac
  
  # Mark all test groups as "no run" initially
  for group in "${TEST_GROUPS[@]}"; do
    print_test_status "$group" "no run"
  done
  
  # Run Maven command and capture output
  if [ -n "$OUTPUT_FILE" ]; then
    echo "Writing output to: $OUTPUT_FILE"
    
    # Run the command with output processing for verbose reporting
    {
      # Mark the first test group as running
      print_test_status "${TEST_GROUPS[0]}" "running"
      
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
      # Mark the first test group as running
      print_test_status "${TEST_GROUPS[0]}" "running"
      
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
  
  # Update final status for test groups
  if [ $MAVEN_EXIT_CODE -eq 0 ]; then
    for group in "${TEST_GROUPS[@]}"; do
      print_test_status "$group" "pass"
    done
    echo -e "\n${STATUS_GREEN}Tests completed successfully!${STATUS_RESET}"
    exit 0
  else
    for group in "${TEST_GROUPS[@]}"; do
      print_test_status "$group" "fail"
    done
    echo -e "\n${STATUS_RED}Tests failed${STATUS_RESET}"
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
    echo -e "\033[0;32mTests completed successfully!\033[0m"
    exit 0
  else
    echo -e "\033[0;31mTests failed\033[0m"
    exit 1
  fi
fi
