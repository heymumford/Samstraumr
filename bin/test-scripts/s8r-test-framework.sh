#!/usr/bin/env bash
#==============================================================================
# s8r-test-framework: Framework for comprehensive pairwise testing
# Executes tests at each layer of the testing pyramid
#
# TODO: Proper exit code verification
# - Currently, the CLI scripts don't consistently return non-zero exit codes for errors
# - Error tests are currently verified by checking error message content instead
# - Future CLI script improvements should ensure non-zero exit codes for errors
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
MAGENTA='\033[0;35m'
BOLD='\033[1m'
RESET='\033[0m'

# Test counters for each layer
UNIT_TOTAL=0
UNIT_PASSED=0
UNIT_FAILED=0

COMPONENT_TOTAL=0
COMPONENT_PASSED=0
COMPONENT_FAILED=0

COMPOSITE_TOTAL=0
COMPOSITE_PASSED=0
COMPOSITE_FAILED=0

MACHINE_TOTAL=0
MACHINE_PASSED=0
MACHINE_FAILED=0

SYSTEM_TOTAL=0
SYSTEM_PASSED=0
SYSTEM_FAILED=0

# Log file for detailed test output
LOG_DIR="${PROJECT_ROOT}/test-results"
mkdir -p "$LOG_DIR"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
LOG_FILE="${LOG_DIR}/test_log_${TIMESTAMP}.log"
RESULTS_FILE="${LOG_DIR}/test_results_${TIMESTAMP}.txt"

# Logging utility
log() {
    echo "$@" | tee -a "$LOG_FILE"
}

# Test start indicator
test_start() {
    local layer="$1"
    local test_name="$2"
    log -e "${BLUE}[${layer}] Starting:${RESET} ${BOLD}$test_name${RESET}"
    
    # Update test counter
    case "$layer" in
        UNIT) UNIT_TOTAL=$((UNIT_TOTAL + 1)) ;;
        COMPONENT) COMPONENT_TOTAL=$((COMPONENT_TOTAL + 1)) ;;
        COMPOSITE) COMPOSITE_TOTAL=$((COMPOSITE_TOTAL + 1)) ;;
        MACHINE) MACHINE_TOTAL=$((MACHINE_TOTAL + 1)) ;;
        SYSTEM) SYSTEM_TOTAL=$((SYSTEM_TOTAL + 1)) ;;
    esac
}

# Test pass indicator
test_pass() {
    local layer="$1"
    local test_name="$2"
    log -e "  ${GREEN}✓ PASS:${RESET} $test_name"
    
    # Update pass counter
    case "$layer" in
        UNIT) UNIT_PASSED=$((UNIT_PASSED + 1)) ;;
        COMPONENT) COMPONENT_PASSED=$((COMPONENT_PASSED + 1)) ;;
        COMPOSITE) COMPOSITE_PASSED=$((COMPOSITE_PASSED + 1)) ;;
        MACHINE) MACHINE_PASSED=$((MACHINE_PASSED + 1)) ;;
        SYSTEM) SYSTEM_PASSED=$((SYSTEM_PASSED + 1)) ;;
    esac
}

# Test fail indicator
test_fail() {
    local layer="$1"
    local test_name="$2"
    local error_msg="$3"
    log -e "  ${RED}✗ FAIL:${RESET} $test_name"
    log -e "  ${RED}      $error_msg${RESET}"
    
    # Update fail counter
    case "$layer" in
        UNIT) UNIT_FAILED=$((UNIT_FAILED + 1)) ;;
        COMPONENT) COMPONENT_FAILED=$((COMPONENT_FAILED + 1)) ;;
        COMPOSITE) COMPOSITE_FAILED=$((COMPOSITE_FAILED + 1)) ;;
        MACHINE) MACHINE_FAILED=$((MACHINE_FAILED + 1)) ;;
        SYSTEM) SYSTEM_FAILED=$((SYSTEM_FAILED + 1)) ;;
    esac
}

# Command execution utility - super simplified for reliability
run_command() {
    local command="$1"
    local output
    local exit_code
    
    # Temporarily disable errexit to capture real exit codes
    set +e
    
    # Execute command directly, capturing output and exit code
    output=$(cd "$PROJECT_ROOT" && eval "$command" 2>&1)
    exit_code=$?
    
    # Re-enable errexit
    set -e
    
    # Log execution details for debugging
    {
        echo "=================================="
        echo "DEBUG: Command executed: $command"
        echo "DEBUG: Exit code: $exit_code"
        echo "=================================="
    } >> "$LOG_FILE"
    
    # Return the output (for capture by the caller)
    echo "$output"
    
    # Store exit code for retrieval - absolutely crucial!
    export LAST_EXIT_CODE=$exit_code
    
    # Return success to avoid breaking test flow
    return 0
}

# Assert output contains expected text
assert_contains() {
    local output="$1"
    local expected="$2"
    local layer="$3"
    local test_name="$4"
    
    if [[ "$output" == *"$expected"* ]]; then
        test_pass "$layer" "$test_name - contains '$expected'"
        return 0
    else
        test_fail "$layer" "$test_name" "Expected output to contain '$expected', but it didn't"
        log -e "  ${YELLOW}Actual output:${RESET}"
        log "$output" | sed 's/^/      /'
        return 1
    fi
}

# Assert output does not contain unwanted text
assert_not_contains() {
    local output="$1"
    local unwanted="$2"
    local layer="$3"
    local test_name="$4"
    
    if [[ "$output" != *"$unwanted"* ]]; then
        test_pass "$layer" "$test_name - does not contain '$unwanted'"
        return 0
    else
        test_fail "$layer" "$test_name" "Expected output to NOT contain '$unwanted', but it did"
        log -e "  ${YELLOW}Actual output:${RESET}"
        log "$output" | sed 's/^/      /'
        return 1
    fi
}

# Assert command succeeded (exit code 0)
assert_success() {
    local exit_code="$1"
    local layer="$2"
    local test_name="$3"
    
    if [[ "$exit_code" -eq 0 ]]; then
        test_pass "$layer" "$test_name - command succeeded"
        return 0
    else
        test_fail "$layer" "$test_name" "Expected exit code 0, but got $exit_code"
        return 1
    fi
}

# Assert command failed (non-zero exit code)
assert_failure() {
    local exit_code="$1"
    local layer="$2"
    local test_name="$3"
    
    if [[ "$exit_code" -ne 0 ]]; then
        test_pass "$layer" "$test_name - command failed as expected"
        return 0
    else
        test_fail "$layer" "$test_name" "Expected non-zero exit code, but got 0"
        return 1
    fi
}

# Print test summary report
print_summary() {
    # Calculate totals
    local total_tests=$((UNIT_TOTAL + COMPONENT_TOTAL + COMPOSITE_TOTAL + MACHINE_TOTAL + SYSTEM_TOTAL))
    local total_passed=$((UNIT_PASSED + COMPONENT_PASSED + COMPOSITE_PASSED + MACHINE_PASSED + SYSTEM_PASSED))
    local total_failed=$((UNIT_FAILED + COMPONENT_FAILED + COMPOSITE_FAILED + MACHINE_FAILED + SYSTEM_FAILED))
    
    # Format the summary
    echo -e "\n${BOLD}${CYAN}Test Summary Report${RESET}"
    echo -e "${CYAN}====================${RESET}\n"
    
    printf "%-15s %-10s %-10s %-10s %-15s\n" "Layer" "Total" "Passed" "Failed" "Pass Rate"
    printf "%-15s %-10s %-10s %-10s %-15s\n" "---------------" "----------" "----------" "----------" "---------------"
    
    # Calculate and print pass rates for each layer
    if [ $UNIT_TOTAL -gt 0 ]; then
        local unit_rate=$(( (UNIT_PASSED * 100) / UNIT_TOTAL ))
        printf "%-15s %-10d ${GREEN}%-10d${RESET} ${RED}%-10d${RESET} %-15s\n" "Unit" $UNIT_TOTAL $UNIT_PASSED $UNIT_FAILED "${unit_rate}%"
    else
        printf "%-15s %-10d ${GREEN}%-10d${RESET} ${RED}%-10d${RESET} %-15s\n" "Unit" 0 0 0 "N/A"
    fi
    
    if [ $COMPONENT_TOTAL -gt 0 ]; then
        local component_rate=$(( (COMPONENT_PASSED * 100) / COMPONENT_TOTAL ))
        printf "%-15s %-10d ${GREEN}%-10d${RESET} ${RED}%-10d${RESET} %-15s\n" "Component" $COMPONENT_TOTAL $COMPONENT_PASSED $COMPONENT_FAILED "${component_rate}%"
    else
        printf "%-15s %-10d ${GREEN}%-10d${RESET} ${RED}%-10d${RESET} %-15s\n" "Component" 0 0 0 "N/A"
    fi
    
    if [ $COMPOSITE_TOTAL -gt 0 ]; then
        local composite_rate=$(( (COMPOSITE_PASSED * 100) / COMPOSITE_TOTAL ))
        printf "%-15s %-10d ${GREEN}%-10d${RESET} ${RED}%-10d${RESET} %-15s\n" "Composite" $COMPOSITE_TOTAL $COMPOSITE_PASSED $COMPOSITE_FAILED "${composite_rate}%"
    else
        printf "%-15s %-10d ${GREEN}%-10d${RESET} ${RED}%-10d${RESET} %-15s\n" "Composite" 0 0 0 "N/A"
    fi
    
    if [ $MACHINE_TOTAL -gt 0 ]; then
        local machine_rate=$(( (MACHINE_PASSED * 100) / MACHINE_TOTAL ))
        printf "%-15s %-10d ${GREEN}%-10d${RESET} ${RED}%-10d${RESET} %-15s\n" "Machine" $MACHINE_TOTAL $MACHINE_PASSED $MACHINE_FAILED "${machine_rate}%"
    else
        printf "%-15s %-10d ${GREEN}%-10d${RESET} ${RED}%-10d${RESET} %-15s\n" "Machine" 0 0 0 "N/A"
    fi
    
    if [ $SYSTEM_TOTAL -gt 0 ]; then
        local system_rate=$(( (SYSTEM_PASSED * 100) / SYSTEM_TOTAL ))
        printf "%-15s %-10d ${GREEN}%-10d${RESET} ${RED}%-10d${RESET} %-15s\n" "System" $SYSTEM_TOTAL $SYSTEM_PASSED $SYSTEM_FAILED "${system_rate}%"
    else
        printf "%-15s %-10d ${GREEN}%-10d${RESET} ${RED}%-10d${RESET} %-15s\n" "System" 0 0 0 "N/A"
    fi
    
    # Print overall totals with a separator line
    printf "%-15s %-10s %-10s %-10s %-15s\n" "---------------" "----------" "----------" "----------" "---------------"
    
    # Calculate overall pass rate
    local overall_rate=0
    if [ $total_tests -gt 0 ]; then
        overall_rate=$(( (total_passed * 100) / total_tests ))
    fi
    
    printf "%-15s %-10d ${GREEN}%-10d${RESET} ${RED}%-10d${RESET} %-15s\n" "TOTAL" $total_tests $total_passed $total_failed "${overall_rate}%"
    
    # Save results to file
    {
        echo "Test Results Summary"
        echo "===================="
        echo "Date: $(date)"
        echo ""
        echo "Layer       Total      Passed     Failed     Pass Rate"
        echo "----------- ---------- ---------- ---------- -----------"
        echo "Unit        $UNIT_TOTAL      $UNIT_PASSED      $UNIT_FAILED      ${unit_rate:-0}%"
        echo "Component   $COMPONENT_TOTAL      $COMPONENT_PASSED      $COMPONENT_FAILED      ${component_rate:-0}%"
        echo "Composite   $COMPOSITE_TOTAL      $COMPOSITE_PASSED      $COMPOSITE_FAILED      ${composite_rate:-0}%"
        echo "Machine     $MACHINE_TOTAL      $MACHINE_PASSED      $MACHINE_FAILED      ${machine_rate:-0}%"
        echo "System      $SYSTEM_TOTAL      $SYSTEM_PASSED      $SYSTEM_FAILED      ${system_rate:-0}%"
        echo "----------- ---------- ---------- ---------- -----------"
        echo "TOTAL       $total_tests      $total_passed      $total_failed      ${overall_rate}%"
    } > "$RESULTS_FILE"
    
    echo -e "\nDetailed logs saved to: ${LOG_FILE}"
    echo -e "Results summary saved to: ${RESULTS_FILE}"
    
    # Return 0 if all tests passed, 1 otherwise
    if [ $total_failed -eq 0 ]; then
        echo -e "\n${GREEN}All tests passed!${RESET}"
        return 0
    else
        echo -e "\n${RED}$total_failed tests failed.${RESET}"
        return 1
    fi
}

# Source the specific test suites
source_test_suite() {
    local test_suite="$1"
    
    if [ -f "$test_suite" ]; then
        source "$test_suite"
    else
        log -e "${RED}Error: Test suite not found: $test_suite${RESET}"
        return 1
    fi
}

# Print header
echo -e "${BOLD}${CYAN}Samstraumr Comprehensive Test Framework${RESET}"
echo -e "${CYAN}=====================================${RESET}\n"
echo "Starting tests at $(date)"
echo "Logging to $LOG_FILE"
echo