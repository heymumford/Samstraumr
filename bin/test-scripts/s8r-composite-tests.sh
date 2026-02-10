#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# s8r-composite-tests: Composite-level tests
# Tests for interactions between components
#==============================================================================

# Initialize test layer
LAYER="COMPOSITE"

#------------------------------------------------------------------------------
# Composite Tests for Component-Composite Integration
#------------------------------------------------------------------------------
run_composite_component_integration_tests() {
    echo -e "${BOLD}Running Composite Tests - Component-Composite Integration${RESET}"
    
    # Create a test component, add it to a composite, then verify using info commands
    
    # Step 1: Create a component for testing
    test_start "$LAYER" "create test component"
    local output=$(run_command "./s8r component create --type transformer TestIntegrationComponent")
    assert_contains "$output" "created successfully" "$LAYER" "create test component success"
    assert_success $LAST_EXIT_CODE "$LAYER" "create test component exit code"
    
    # Step 2: Create a composite for testing
    test_start "$LAYER" "create test composite"
    local output=$(run_command "./s8r composite create --type processing TestIntegrationComposite")
    assert_contains "$output" "created successfully" "$LAYER" "create test composite success"
    assert_success $LAST_EXIT_CODE "$LAYER" "create test composite exit code"
    
    # Step 3: Add component to composite
    test_start "$LAYER" "add component to composite"
    local output=$(run_command "./s8r composite add --component TestIntegrationComponent --composite TestIntegrationComposite")
    assert_contains "$output" "added" "$LAYER" "add component to composite success"
    assert_success $LAST_EXIT_CODE "$LAYER" "add component to composite exit code"
    
    # Step 4: Verify component info shows it's in the composite
    test_start "$LAYER" "verify component info"
    local output=$(run_command "./s8r component info TestIntegrationComponent")
    assert_contains "$output" "Component: TestIntegrationComponent" "$LAYER" "component info shows component name"
    assert_contains "$output" "transformer" "$LAYER" "component info shows correct type"
    assert_success $LAST_EXIT_CODE "$LAYER" "verify component info exit code"
    
    # Step 5: Verify composite info shows the component
    test_start "$LAYER" "verify composite info"
    local output=$(run_command "./s8r composite info TestIntegrationComposite")
    assert_contains "$output" "Composite: TestIntegrationComposite" "$LAYER" "composite info shows composite name"
    assert_contains "$output" "processing" "$LAYER" "composite info shows correct type"
    assert_success $LAST_EXIT_CODE "$LAYER" "verify composite info exit code"
    
    # Step 6: Create a second component and connect them
    test_start "$LAYER" "create second test component"
    local output=$(run_command "./s8r component create --type validator TestIntegrationComponent2")
    assert_contains "$output" "created successfully" "$LAYER" "create second test component success"
    assert_success $LAST_EXIT_CODE "$LAYER" "create second test component exit code"
    
    # Step 7: Add second component to composite
    test_start "$LAYER" "add second component to composite"
    local output=$(run_command "./s8r composite add --component TestIntegrationComponent2 --composite TestIntegrationComposite")
    assert_contains "$output" "added" "$LAYER" "add second component to composite success"
    assert_success $LAST_EXIT_CODE "$LAYER" "add second component to composite exit code"
    
    # Step 8: Connect the components
    test_start "$LAYER" "connect components in composite"
    local output=$(run_command "./s8r composite connect --from TestIntegrationComponent --to TestIntegrationComponent2 --composite TestIntegrationComposite")
    assert_contains "$output" "Connected" "$LAYER" "connect components success"
    assert_success $LAST_EXIT_CODE "$LAYER" "connect components exit code"
    
    # Step 9: Cleanup - Delete the composite
    test_start "$LAYER" "delete test composite"
    local output=$(run_command "./s8r composite delete --force TestIntegrationComposite")
    assert_contains "$output" "deleted successfully" "$LAYER" "delete test composite success"
    assert_success $LAST_EXIT_CODE "$LAYER" "delete test composite exit code"
}

#------------------------------------------------------------------------------
# Composite Tests for Composite-Machine Integration
#------------------------------------------------------------------------------
run_composite_machine_integration_tests() {
    echo -e "${BOLD}Running Composite Tests - Composite-Machine Integration${RESET}"
    
    # Create composites, add them to a machine, then verify using info commands
    
    # Step 1: Create first composite for testing
    test_start "$LAYER" "create first test composite"
    local output=$(run_command "./s8r composite create --type processing TestMachineComposite1")
    assert_contains "$output" "created successfully" "$LAYER" "create first test composite success"
    assert_success $LAST_EXIT_CODE "$LAYER" "create first test composite exit code"
    
    # Step 2: Create second composite for testing
    test_start "$LAYER" "create second test composite"
    local output=$(run_command "./s8r composite create --type storage TestMachineComposite2")
    assert_contains "$output" "created successfully" "$LAYER" "create second test composite success"
    assert_success $LAST_EXIT_CODE "$LAYER" "create second test composite exit code"
    
    # Step 3: Create a machine
    test_start "$LAYER" "create test machine"
    local output=$(run_command "./s8r machine create --template flow TestIntegrationMachine")
    assert_contains "$output" "created successfully" "$LAYER" "create test machine success"
    assert_success $LAST_EXIT_CODE "$LAYER" "create test machine exit code"
    
    # Step 4: Add first composite to machine
    test_start "$LAYER" "add first composite to machine"
    local output=$(run_command "./s8r machine add --composite TestMachineComposite1 --machine TestIntegrationMachine")
    assert_contains "$output" "added" "$LAYER" "add first composite to machine success"
    assert_success $LAST_EXIT_CODE "$LAYER" "add first composite to machine exit code"
    
    # Step 5: Add second composite to machine
    test_start "$LAYER" "add second composite to machine"
    local output=$(run_command "./s8r machine add --composite TestMachineComposite2 --machine TestIntegrationMachine")
    assert_contains "$output" "added" "$LAYER" "add second composite to machine success"
    assert_success $LAST_EXIT_CODE "$LAYER" "add second composite to machine exit code"
    
    # Step 6: Connect composites in machine
    test_start "$LAYER" "connect composites in machine"
    local output=$(run_command "./s8r machine connect --from TestMachineComposite1 --to TestMachineComposite2 --machine TestIntegrationMachine")
    assert_contains "$output" "Connected" "$LAYER" "connect composites in machine success"
    assert_success $LAST_EXIT_CODE "$LAYER" "connect composites in machine exit code"
    
    # Step 7: Verify machine info shows both composites
    test_start "$LAYER" "verify machine info"
    local output=$(run_command "./s8r machine info TestIntegrationMachine")
    assert_contains "$output" "Machine: TestIntegrationMachine" "$LAYER" "machine info shows machine name"
    assert_success $LAST_EXIT_CODE "$LAYER" "verify machine info exit code"
    
    # Step 8: Start the machine
    test_start "$LAYER" "start machine"
    local output=$(run_command "./s8r machine start TestIntegrationMachine")
    assert_contains "$output" "started successfully" "$LAYER" "start machine success"
    assert_success $LAST_EXIT_CODE "$LAYER" "start machine exit code"
    
    # Step 9: Stop the machine
    test_start "$LAYER" "stop machine"
    local output=$(run_command "./s8r machine stop TestIntegrationMachine")
    assert_contains "$output" "stopped successfully" "$LAYER" "stop machine success"
    assert_success $LAST_EXIT_CODE "$LAYER" "stop machine exit code"
    
    # Step 10: Cleanup - Delete the machine
    test_start "$LAYER" "delete test machine"
    local output=$(run_command "./s8r machine delete --force TestIntegrationMachine")
    assert_contains "$output" "deleted successfully" "$LAYER" "delete test machine success"
    assert_success $LAST_EXIT_CODE "$LAYER" "delete test machine exit code"
}

#------------------------------------------------------------------------------
# Run all composite tests
#------------------------------------------------------------------------------
run_composite_tests() {
    echo -e "${BOLD}${MAGENTA}Running Composite Tests${RESET}"
    echo -e "${MAGENTA}=======================${RESET}\n"
    
    run_composite_component_integration_tests
    run_composite_machine_integration_tests
    
    echo -e "\n${MAGENTA}Composite Tests Complete${RESET}"
}