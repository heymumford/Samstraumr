#!/usr/bin/env bash
#==============================================================================
# s8r-acceptance-tests: Acceptance tests for Samstraumr CLI
# Tests for known issues that need to be fixed in future development
#==============================================================================
# IMPORTANT: This file contains tests that INTENTIONALLY FAIL until
# the underlying issues are fixed. These tests document known issues
# and serve as validation that they have been properly addressed when
# someone fixes them in the future.
#
# Current known issues and future requirements being tested:
# 
# EXIT CODE CONSISTENCY:
# 1. Command redirection exit codes (now fixed)
# 2. CLI errors should return non-zero exit codes consistently
#
# COMPONENT/TUBE VALIDATION:
# 3. Component duplicate detection
# 4. Non-existent component reference detection
# 5. Component name validation (allowed characters, length)
# 6. Component type validation (unknown types)
#
# COMPOSITE VALIDATION:
# 7. Composite connection validation (invalid connections)
# 8. Connection cycle detection
#
# MACHINE VALIDATION:
# 9. Machine state validation (illegal transitions)
# 10. Machine component validation
#
# CROSS-ENTITY VALIDATION:
# 11. Cross-entity name uniqueness (tube vs component vs composite vs machine)
# 
# ERROR HANDLING:
# 12. Standardized error format
# 13. Resilience to component failures
#==============================================================================

# Initialize test layer
LAYER="ACCEPTANCE"

# Source the test framework
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
export PROJECT_ROOT="${SCRIPT_DIR}"

source "${PROJECT_ROOT}/s8r-test-framework.sh"

#==============================================================================
# EXIT CODE CONSISTENCY TESTS
#==============================================================================

#------------------------------------------------------------------------------
# Acceptance Tests for Command Redirection Exit Codes
#------------------------------------------------------------------------------
run_acceptance_command_redirection_tests() {
    echo -e "${BOLD}Running Acceptance Tests - Command Redirection Exit Codes${RESET}"
    
    # Test s8r with a development command (should exit with code 1)
    test_start "$LAYER" "s8r with dev command exit code"
    
    # Run command directly to ensure correct exit code
    set +e
    cd "$PROJECT_ROOT" && ./s8r build &>/dev/null
    local actual_exit_code=$?
    set -e
    
    # Verify exit code is 1 (error)
    if [ $actual_exit_code -eq 1 ]; then
        test_pass "$LAYER" "s8r with dev command has correct exit code (1)"
    else
        test_fail "$LAYER" "s8r with dev command has incorrect exit code ($actual_exit_code, expected 1)"
        echo "NOTE: This test failure indicates that s8r does not return exit code 1 when redirecting to s8r-dev." >> "$LOG_FILE"
    fi
    
    # Test s8r-dev with a user command (should exit with code 1)
    test_start "$LAYER" "s8r-dev with user command exit code"
    
    # Run command directly to ensure correct exit code
    set +e
    cd "$PROJECT_ROOT" && ./s8r-dev component &>/dev/null
    local actual_exit_code=$?
    set -e
    
    # Verify exit code is 1 (error)
    if [ $actual_exit_code -eq 1 ]; then
        test_pass "$LAYER" "s8r-dev with user command has correct exit code (1)"
    else
        test_fail "$LAYER" "s8r-dev with user command has incorrect exit code ($actual_exit_code, expected 1)"
        echo "NOTE: This test failure indicates that s8r-dev does not return exit code 1 when redirecting to s8r." >> "$LOG_FILE"
    fi
}

#------------------------------------------------------------------------------
# Acceptance Tests for CLI Error Exit Codes
#------------------------------------------------------------------------------
run_acceptance_cli_error_exit_codes() {
    echo -e "${BOLD}Running Acceptance Tests - CLI Error Exit Codes${RESET}"
    
    # Test invalid subcommand (should exit with code 1)
    test_start "$LAYER" "CLI returns non-zero for invalid subcommand"
    
    # Run command directly to ensure correct exit code
    set +e
    cd "$PROJECT_ROOT" && ./s8r component invalid_subcommand &>/dev/null
    local actual_exit_code=$?
    set -e
    
    # Verify exit code is non-zero
    if [ $actual_exit_code -ne 0 ]; then
        test_pass "$LAYER" "Invalid subcommand returns non-zero exit code ($actual_exit_code)"
    else
        test_fail "$LAYER" "Invalid subcommand returned exit code 0 (expected non-zero)"
        echo "NOTE: This test failure indicates that CLI commands don't consistently return non-zero exit codes for errors." >> "$LOG_FILE"
    fi
    
    # Test missing required parameter (should exit with code 1)
    test_start "$LAYER" "CLI returns non-zero for missing required parameter"
    
    # Run command directly to ensure correct exit code
    set +e
    cd "$PROJECT_ROOT" && ./s8r component create &>/dev/null
    local actual_exit_code=$?
    set -e
    
    # Verify exit code is non-zero
    if [ $actual_exit_code -ne 0 ]; then
        test_pass "$LAYER" "Missing required parameter returns non-zero exit code ($actual_exit_code)"
    else
        test_fail "$LAYER" "Missing required parameter returned exit code 0 (expected non-zero)"
        echo "NOTE: This test failure indicates that CLI commands don't consistently return non-zero exit codes for missing parameters." >> "$LOG_FILE"
    fi
}

#==============================================================================
# COMPONENT/TUBE VALIDATION TESTS
#==============================================================================

#------------------------------------------------------------------------------
# Acceptance Tests for Component Duplicate Detection
#------------------------------------------------------------------------------
run_acceptance_component_duplicate_tests() {
    echo -e "${BOLD}Running Acceptance Tests - Component Duplicate Detection${RESET}"
    
    # Test creating a component with a unique name 
    test_start "$LAYER" "create component with unique name"
    local output=$(run_command "./s8r component create UniqueTestComponent")
    assert_contains "$output" "created successfully" "$LAYER" "component was created successfully"
    assert_success $LAST_EXIT_CODE "$LAYER" "command succeeded"
    
    # Test creating a component with the same name (should fail)
    test_start "$LAYER" "create duplicate component"
    
    # Run command directly to ensure correct exit code
    set +e
    local output=$(cd "$PROJECT_ROOT" && ./s8r component create UniqueTestComponent 2>&1)
    local actual_exit_code=$?
    set -e
    
    # Verify both error message and exit code
    if [[ $output == *"already exists"* && $actual_exit_code -ne 0 ]]; then
        test_pass "$LAYER" "Duplicate component detection works correctly"
    else
        test_fail "$LAYER" "Duplicate component detection failed (error message or exit code incorrect)"
        echo "NOTE: This test failure indicates that component duplicate detection does not work properly." >> "$LOG_FILE"
        echo "Expected: Error message containing 'already exists' and non-zero exit code" >> "$LOG_FILE"
        echo "Actual: Exit code $actual_exit_code, output: $output" >> "$LOG_FILE"
    fi
    
    # Clean up
    run_command "./s8r component delete --force UniqueTestComponent" > /dev/null
}

#------------------------------------------------------------------------------
# Acceptance Tests for Non-existent Component Reference
#------------------------------------------------------------------------------
run_acceptance_nonexistent_component_tests() {
    echo -e "${BOLD}Running Acceptance Tests - Non-existent Component Reference${RESET}"
    
    # Generate a random name to ensure it doesn't exist
    local random_name="NonexistentComponent_$(date +%s)"
    
    # Test getting info on non-existent component (should fail)
    test_start "$LAYER" "reference non-existent component"
    
    # Run command directly to ensure correct exit code
    set +e
    local output=$(cd "$PROJECT_ROOT" && ./s8r component info "$random_name" 2>&1)
    local actual_exit_code=$?
    set -e
    
    # Verify both error message and exit code
    if [[ $output == *"not found"* && $actual_exit_code -ne 0 ]]; then
        test_pass "$LAYER" "Non-existent component detection works correctly"
    else
        test_fail "$LAYER" "Non-existent component detection failed (error message or exit code incorrect)"
        echo "NOTE: This test failure indicates that non-existent component detection does not work properly." >> "$LOG_FILE"
        echo "Expected: Error message containing 'not found' and non-zero exit code" >> "$LOG_FILE"
        echo "Actual: Exit code $actual_exit_code, output: $output" >> "$LOG_FILE"
    fi
}

#------------------------------------------------------------------------------
# Acceptance Tests for Component Name Validation
#------------------------------------------------------------------------------
run_acceptance_component_name_validation() {
    echo -e "${BOLD}Running Acceptance Tests - Component Name Validation${RESET}"
    
    # Test component name with invalid characters (should fail)
    test_start "$LAYER" "component name with invalid characters"
    
    # Run command directly to ensure correct exit code
    set +e
    local output=$(cd "$PROJECT_ROOT" && ./s8r component create "Invalid@Component#Name" 2>&1)
    local actual_exit_code=$?
    set -e
    
    # Verify both error message and exit code
    if [[ $output == *"invalid"* && $actual_exit_code -ne 0 ]]; then
        test_pass "$LAYER" "Invalid character detection works correctly"
    else
        test_fail "$LAYER" "Invalid character detection failed (error message or exit code incorrect)"
        echo "NOTE: This test failure indicates that component name validation does not properly check for invalid characters." >> "$LOG_FILE"
        echo "Expected: Error message containing 'invalid' and non-zero exit code" >> "$LOG_FILE"
        echo "Actual: Exit code $actual_exit_code, output: $output" >> "$LOG_FILE"
        
        # Clean up if it was created despite invalid name
        run_command "./s8r component delete --force 'Invalid@Component#Name'" > /dev/null
    fi
    
    # Test component name that's too long (should fail)
    test_start "$LAYER" "component name too long"
    local long_name=$(printf '%.0s' {1..101} | tr '0-9' 'A-Za-z')
    
    # Run command directly to ensure correct exit code
    set +e
    local output=$(cd "$PROJECT_ROOT" && ./s8r component create "$long_name" 2>&1)
    local actual_exit_code=$?
    set -e
    
    # Verify both error message and exit code
    if [[ $output == *"too long"* && $actual_exit_code -ne 0 ]]; then
        test_pass "$LAYER" "Name length validation works correctly"
    else
        test_fail "$LAYER" "Name length validation failed (error message or exit code incorrect)"
        echo "NOTE: This test failure indicates that component name validation does not properly check for names that are too long." >> "$LOG_FILE"
        echo "Expected: Error message containing 'too long' and non-zero exit code" >> "$LOG_FILE"
        echo "Actual: Exit code $actual_exit_code, output: $output" >> "$LOG_FILE"
        
        # Clean up if it was created despite invalid name
        run_command "./s8r component delete --force '$long_name'" > /dev/null
    fi
}

#------------------------------------------------------------------------------
# Acceptance Tests for Component Type Validation
#------------------------------------------------------------------------------
run_acceptance_component_type_validation() {
    echo -e "${BOLD}Running Acceptance Tests - Component Type Validation${RESET}"
    
    # Test creating a component with an invalid type (should fail)
    test_start "$LAYER" "component with invalid type"
    
    # Run command directly to ensure correct exit code
    set +e
    local output=$(cd "$PROJECT_ROOT" && ./s8r component create --type nonexistenttype TypeValidationComponent 2>&1)
    local actual_exit_code=$?
    set -e
    
    # Verify both error message and exit code
    if [[ $output == *"invalid type"* && $actual_exit_code -ne 0 ]]; then
        test_pass "$LAYER" "Invalid type detection works correctly"
    else
        test_fail "$LAYER" "Invalid type detection failed (error message or exit code incorrect)"
        echo "NOTE: This test failure indicates that component type validation does not properly check for invalid types." >> "$LOG_FILE"
        echo "Expected: Error message containing 'invalid type' and non-zero exit code" >> "$LOG_FILE"
        echo "Actual: Exit code $actual_exit_code, output: $output" >> "$LOG_FILE"
        
        # Clean up if it was created despite invalid type
        run_command "./s8r component delete --force TypeValidationComponent" > /dev/null
    fi
}

#==============================================================================
# COMPOSITE VALIDATION TESTS
#==============================================================================

#------------------------------------------------------------------------------
# Acceptance Tests for Composite Connection Validation
#------------------------------------------------------------------------------
run_acceptance_composite_connection_validation() {
    echo -e "${BOLD}Running Acceptance Tests - Composite Connection Validation${RESET}"
    
    # Create test components for connections
    run_command "./s8r component create --type source SourceComponent" > /dev/null
    run_command "./s8r component create --type processor ProcessorComponent" > /dev/null
    run_command "./s8r component create --type sink SinkComponent" > /dev/null
    run_command "./s8r composite create TestComposite" > /dev/null
    run_command "./s8r composite add --component SourceComponent --composite TestComposite" > /dev/null
    run_command "./s8r composite add --component ProcessorComponent --composite TestComposite" > /dev/null
    
    # Test connecting components that aren't in the composite (should fail)
    test_start "$LAYER" "connect component not in composite"
    
    # Run command directly to ensure correct exit code
    set +e
    local output=$(cd "$PROJECT_ROOT" && ./s8r composite connect --from SourceComponent --to SinkComponent --composite TestComposite 2>&1)
    local actual_exit_code=$?
    set -e
    
    # Verify both error message and exit code
    if [[ $output == *"not found in composite"* && $actual_exit_code -ne 0 ]]; then
        test_pass "$LAYER" "Invalid connection detection works correctly"
    else
        test_fail "$LAYER" "Invalid connection detection failed (error message or exit code incorrect)"
        echo "NOTE: This test failure indicates that composite connection validation does not properly check if components exist in the composite." >> "$LOG_FILE"
        echo "Expected: Error message containing 'not found in composite' and non-zero exit code" >> "$LOG_FILE"
        echo "Actual: Exit code $actual_exit_code, output: $output" >> "$LOG_FILE"
    fi
    
    # Clean up
    run_command "./s8r composite delete --force TestComposite" > /dev/null
    run_command "./s8r component delete --force SourceComponent" > /dev/null
    run_command "./s8r component delete --force ProcessorComponent" > /dev/null
    run_command "./s8r component delete --force SinkComponent" > /dev/null
}

#------------------------------------------------------------------------------
# Acceptance Tests for Connection Cycle Detection
#------------------------------------------------------------------------------
run_acceptance_connection_cycle_detection() {
    echo -e "${BOLD}Running Acceptance Tests - Connection Cycle Detection${RESET}"
    
    # Create test components and composite for cycle detection
    run_command "./s8r component create --type processor ProcessorA" > /dev/null
    run_command "./s8r component create --type processor ProcessorB" > /dev/null
    run_command "./s8r component create --type processor ProcessorC" > /dev/null
    run_command "./s8r composite create CycleTestComposite" > /dev/null
    run_command "./s8r composite add --component ProcessorA --composite CycleTestComposite" > /dev/null
    run_command "./s8r composite add --component ProcessorB --composite CycleTestComposite" > /dev/null
    run_command "./s8r composite add --component ProcessorC --composite CycleTestComposite" > /dev/null
    
    # Create valid connections first
    run_command "./s8r composite connect --from ProcessorA --to ProcessorB --composite CycleTestComposite" > /dev/null
    run_command "./s8r composite connect --from ProcessorB --to ProcessorC --composite CycleTestComposite" > /dev/null
    
    # Test creating a cycle (should fail)
    test_start "$LAYER" "detect connection cycle"
    
    # Run command directly to ensure correct exit code
    set +e
    local output=$(cd "$PROJECT_ROOT" && ./s8r composite connect --from ProcessorC --to ProcessorA --composite CycleTestComposite 2>&1)
    local actual_exit_code=$?
    set -e
    
    # Verify both error message and exit code
    if [[ $output == *"cycle"* && $actual_exit_code -ne 0 ]]; then
        test_pass "$LAYER" "Cycle detection works correctly"
    else
        test_fail "$LAYER" "Cycle detection failed (error message or exit code incorrect)"
        echo "NOTE: This test failure indicates that connection cycle detection is not properly implemented." >> "$LOG_FILE"
        echo "Expected: Error message containing 'cycle' and non-zero exit code" >> "$LOG_FILE"
        echo "Actual: Exit code $actual_exit_code, output: $output" >> "$LOG_FILE"
    fi
    
    # Clean up
    run_command "./s8r composite delete --force CycleTestComposite" > /dev/null
    run_command "./s8r component delete --force ProcessorA" > /dev/null
    run_command "./s8r component delete --force ProcessorB" > /dev/null
    run_command "./s8r component delete --force ProcessorC" > /dev/null
}

#==============================================================================
# MACHINE VALIDATION TESTS
#==============================================================================

#------------------------------------------------------------------------------
# Acceptance Tests for Machine State Validation
#------------------------------------------------------------------------------
run_acceptance_machine_state_validation() {
    echo -e "${BOLD}Running Acceptance Tests - Machine State Validation${RESET}"
    
    # Create test machine
    run_command "./s8r machine create StateTestMachine" > /dev/null
    
    # Stop machine that isn't running (should fail)
    test_start "$LAYER" "stop machine that isn't running"
    
    # Run command directly to ensure correct exit code
    set +e
    local output=$(cd "$PROJECT_ROOT" && ./s8r machine stop StateTestMachine 2>&1)
    local actual_exit_code=$?
    set -e
    
    # Verify both error message and exit code
    if [[ $output == *"not running"* && $actual_exit_code -ne 0 ]]; then
        test_pass "$LAYER" "Invalid state transition detection works correctly"
    else
        test_fail "$LAYER" "Invalid state transition detection failed (error message or exit code incorrect)"
        echo "NOTE: This test failure indicates that machine state validation does not properly check for valid state transitions." >> "$LOG_FILE"
        echo "Expected: Error message containing 'not running' and non-zero exit code" >> "$LOG_FILE"
        echo "Actual: Exit code $actual_exit_code, output: $output" >> "$LOG_FILE"
    fi
    
    # Clean up
    run_command "./s8r machine delete --force StateTestMachine" > /dev/null
}

#------------------------------------------------------------------------------
# Acceptance Tests for Machine Component Validation
#------------------------------------------------------------------------------
run_acceptance_machine_component_validation() {
    echo -e "${BOLD}Running Acceptance Tests - Machine Component Validation${RESET}"
    
    # Create test machine and composite
    run_command "./s8r machine create ComponentValidationMachine" > /dev/null
    
    # Try to add non-existent composite to machine (should fail)
    test_start "$LAYER" "add non-existent composite to machine"
    
    # Run command directly to ensure correct exit code
    set +e
    local output=$(cd "$PROJECT_ROOT" && ./s8r machine add --composite NonExistentComposite --machine ComponentValidationMachine 2>&1)
    local actual_exit_code=$?
    set -e
    
    # Verify both error message and exit code
    if [[ $output == *"not found"* && $actual_exit_code -ne 0 ]]; then
        test_pass "$LAYER" "Non-existent composite validation works correctly"
    else
        test_fail "$LAYER" "Non-existent composite validation failed (error message or exit code incorrect)"
        echo "NOTE: This test failure indicates that machine component validation does not properly check for non-existent composites." >> "$LOG_FILE"
        echo "Expected: Error message containing 'not found' and non-zero exit code" >> "$LOG_FILE"
        echo "Actual: Exit code $actual_exit_code, output: $output" >> "$LOG_FILE"
    fi
    
    # Clean up
    run_command "./s8r machine delete --force ComponentValidationMachine" > /dev/null
}

#==============================================================================
# CROSS-ENTITY VALIDATION TESTS
#==============================================================================

#------------------------------------------------------------------------------
# Acceptance Tests for Cross-Entity Name Uniqueness
#------------------------------------------------------------------------------
run_acceptance_cross_entity_uniqueness() {
    echo -e "${BOLD}Running Acceptance Tests - Cross-Entity Name Uniqueness${RESET}"
    
    # Create a component with a specific name
    local test_name="CrossEntityTest"
    run_command "./s8r component create $test_name" > /dev/null
    
    # Try to create a tube with the same name (should fail)
    test_start "$LAYER" "create tube with same name as component"
    
    # Run command directly to ensure correct exit code
    set +e
    local output=$(cd "$PROJECT_ROOT" && ./s8r tube create $test_name 2>&1)
    local actual_exit_code=$?
    set -e
    
    # Verify both error message and exit code
    if [[ $output == *"already exists"* && $actual_exit_code -ne 0 ]]; then
        test_pass "$LAYER" "Cross-entity name uniqueness validation works correctly"
    else
        test_fail "$LAYER" "Cross-entity name uniqueness validation failed (error message or exit code incorrect)"
        echo "NOTE: This test failure indicates that cross-entity name uniqueness is not enforced." >> "$LOG_FILE"
        echo "Expected: Error message containing 'already exists' and non-zero exit code" >> "$LOG_FILE"
        echo "Actual: Exit code $actual_exit_code, output: $output" >> "$LOG_FILE"
        
        # Delete tube if it was created
        run_command "./s8r tube delete --force $test_name" > /dev/null
    fi
    
    # Clean up
    run_command "./s8r component delete --force $test_name" > /dev/null
}

#==============================================================================
# ERROR HANDLING TESTS
#==============================================================================

#------------------------------------------------------------------------------
# Acceptance Tests for Standardized Error Format
#------------------------------------------------------------------------------
run_acceptance_error_format() {
    echo -e "${BOLD}Running Acceptance Tests - Standardized Error Format${RESET}"
    
    # Test error message format consistency
    test_start "$LAYER" "standardized error format"
    
    # Run command that should produce an error
    set +e
    local output=$(cd "$PROJECT_ROOT" && ./s8r component create 2>&1)
    set -e
    
    # Error messages should have a consistent format - like "Error: <message>"
    if [[ $output == *"Error:"* ]]; then
        test_pass "$LAYER" "Error format is standardized"
    else
        test_fail "$LAYER" "Error format is not standardized"
        echo "NOTE: This test failure indicates that error messages don't follow a consistent format." >> "$LOG_FILE"
        echo "Expected: Error message containing 'Error:'" >> "$LOG_FILE"
        echo "Actual output: $output" >> "$LOG_FILE"
    fi
}

#------------------------------------------------------------------------------
# Acceptance Tests for Component Resilience
#------------------------------------------------------------------------------
run_acceptance_component_resilience() {
    echo -e "${BOLD}Running Acceptance Tests - Component Resilience${RESET}"
    
    # Create test machine, composite, and components for resilience testing
    run_command "./s8r component create --type source ResilienceSourceComponent" > /dev/null
    run_command "./s8r component create --type sink ResilienceSinkComponent" > /dev/null
    run_command "./s8r composite create ResilienceComposite" > /dev/null
    run_command "./s8r composite add --component ResilienceSourceComponent --composite ResilienceComposite" > /dev/null
    run_command "./s8r composite add --component ResilienceSinkComponent --composite ResilienceComposite" > /dev/null
    run_command "./s8r composite connect --from ResilienceSourceComponent --to ResilienceSinkComponent --composite ResilienceComposite" > /dev/null
    run_command "./s8r machine create ResilienceMachine" > /dev/null
    run_command "./s8r machine add --composite ResilienceComposite --machine ResilienceMachine" > /dev/null
    
    # Test machine resilience - should recover from component failures
    test_start "$LAYER" "machine recovers from component failures"
    
    # Start the machine
    run_command "./s8r machine start ResilienceMachine" > /dev/null
    
    # In a real test, we would simulate a component failure here
    # For now, we'll just check if the machine has error handling features
    
    local output=$(run_command "./s8r machine info ResilienceMachine")
    
    # Look for any mention of error handling or recovery capabilities
    if [[ $output == *"recover"* || $output == *"resilience"* || $output == *"fault tolerance"* ]]; then
        test_pass "$LAYER" "Machine has resilience capabilities"
    else
        test_fail "$LAYER" "Machine does not have documented resilience capabilities"
        echo "NOTE: This test failure indicates that machine resilience features are not implemented or documented." >> "$LOG_FILE"
        echo "Expected: Machine info containing resilience-related terms" >> "$LOG_FILE"
        echo "Actual output did not mention resilience capabilities." >> "$LOG_FILE"
    fi
    
    # Stop and clean up
    run_command "./s8r machine stop ResilienceMachine" > /dev/null
    run_command "./s8r machine delete --force ResilienceMachine" > /dev/null
    run_command "./s8r composite delete --force ResilienceComposite" > /dev/null
    run_command "./s8r component delete --force ResilienceSourceComponent" > /dev/null
    run_command "./s8r component delete --force ResilienceSinkComponent" > /dev/null
}

#------------------------------------------------------------------------------
# Run all acceptance tests
#------------------------------------------------------------------------------
run_acceptance_tests() {
    echo -e "${BOLD}${MAGENTA}Running Acceptance Tests${RESET}"
    echo -e "${MAGENTA}=======================${RESET}\n"
    
    # EXIT CODE CONSISTENCY TESTS
    run_acceptance_command_redirection_tests
    run_acceptance_cli_error_exit_codes
    
    # COMPONENT/TUBE VALIDATION TESTS
    run_acceptance_component_duplicate_tests
    run_acceptance_nonexistent_component_tests
    run_acceptance_component_name_validation
    run_acceptance_component_type_validation
    
    # COMPOSITE VALIDATION TESTS
    run_acceptance_composite_connection_validation
    run_acceptance_connection_cycle_detection
    
    # MACHINE VALIDATION TESTS
    run_acceptance_machine_state_validation
    run_acceptance_machine_component_validation
    
    # CROSS-ENTITY VALIDATION TESTS
    run_acceptance_cross_entity_uniqueness
    
    # ERROR HANDLING TESTS
    run_acceptance_error_format
    run_acceptance_component_resilience
    
    echo -e "\n${MAGENTA}Acceptance Tests Complete${RESET}"
}

# Run the acceptance tests if this script is executed directly
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    # Set up test environment
    setup_test_environment
    
    # Run the acceptance tests
    run_acceptance_tests
    
    # Report results
    report_test_results
    
    # Exit with the appropriate code
    exit $TEST_EXIT_CODE
fi