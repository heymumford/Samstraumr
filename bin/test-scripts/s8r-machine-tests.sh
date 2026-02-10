#!/usr/bin/env bash
#==============================================================================
# s8r-machine-tests: Machine-level tests
# Tests for machine workflows and end-to-end functionality
#==============================================================================

# Initialize test layer
LAYER="MACHINE"

#------------------------------------------------------------------------------
# Machine Tests for End-to-End Data Flow
#------------------------------------------------------------------------------
run_machine_dataflow_tests() {
    echo -e "${BOLD}Running Machine Tests - End-to-End Data Flow${RESET}"
    
    # Create a complete data flow pipeline with components, composites, and a machine
    
    # Step 1: Create components
    test_start "$LAYER" "create data source component"
    local output=$(run_command "./s8r component create --type source DataSourceComponent")
    assert_contains "$output" "created successfully" "$LAYER" "create data source component success"
    assert_success $LAST_EXIT_CODE "$LAYER" "create data source component exit code"
    
    test_start "$LAYER" "create transformer component"
    local output=$(run_command "./s8r component create --type transformer DataTransformerComponent")
    assert_contains "$output" "created successfully" "$LAYER" "create transformer component success"
    assert_success $LAST_EXIT_CODE "$LAYER" "create transformer component exit code"
    
    test_start "$LAYER" "create validator component"
    local output=$(run_command "./s8r component create --type validator DataValidatorComponent")
    assert_contains "$output" "created successfully" "$LAYER" "create validator component success"
    assert_success $LAST_EXIT_CODE "$LAYER" "create validator component exit code"
    
    test_start "$LAYER" "create storage component"
    local output=$(run_command "./s8r component create --type sink DataStorageComponent")
    assert_contains "$output" "created successfully" "$LAYER" "create storage component success"
    assert_success $LAST_EXIT_CODE "$LAYER" "create storage component exit code"
    
    # Step 2: Create ingestion composite
    test_start "$LAYER" "create ingestion composite"
    local output=$(run_command "./s8r composite create --type ingestion IngestionComposite")
    assert_contains "$output" "created successfully" "$LAYER" "create ingestion composite success"
    assert_success $LAST_EXIT_CODE "$LAYER" "create ingestion composite exit code"
    
    # Step 3: Add source and transformer to ingestion composite
    test_start "$LAYER" "add source to ingestion composite"
    local output=$(run_command "./s8r composite add --component DataSourceComponent --composite IngestionComposite")
    assert_contains "$output" "added" "$LAYER" "add source to ingestion composite success"
    assert_success $LAST_EXIT_CODE "$LAYER" "add source to ingestion composite exit code"
    
    test_start "$LAYER" "add transformer to ingestion composite"
    local output=$(run_command "./s8r composite add --component DataTransformerComponent --composite IngestionComposite")
    assert_contains "$output" "added" "$LAYER" "add transformer to ingestion composite success"
    assert_success $LAST_EXIT_CODE "$LAYER" "add transformer to ingestion composite exit code"
    
    # Step 4: Connect components in ingestion composite
    test_start "$LAYER" "connect components in ingestion composite"
    local output=$(run_command "./s8r composite connect --from DataSourceComponent --to DataTransformerComponent --composite IngestionComposite")
    assert_contains "$output" "Connected" "$LAYER" "connect components in ingestion composite success"
    assert_success $LAST_EXIT_CODE "$LAYER" "connect components in ingestion composite exit code"
    
    # Step 5: Create storage composite
    test_start "$LAYER" "create storage composite"
    local output=$(run_command "./s8r composite create --type storage StorageComposite")
    assert_contains "$output" "created successfully" "$LAYER" "create storage composite success"
    assert_success $LAST_EXIT_CODE "$LAYER" "create storage composite exit code"
    
    # Step 6: Add validator and storage to storage composite
    test_start "$LAYER" "add validator to storage composite"
    local output=$(run_command "./s8r composite add --component DataValidatorComponent --composite StorageComposite")
    assert_contains "$output" "added" "$LAYER" "add validator to storage composite success"
    assert_success $LAST_EXIT_CODE "$LAYER" "add validator to storage composite exit code"
    
    test_start "$LAYER" "add storage to storage composite"
    local output=$(run_command "./s8r composite add --component DataStorageComponent --composite StorageComposite")
    assert_contains "$output" "added" "$LAYER" "add storage to storage composite success"
    assert_success $LAST_EXIT_CODE "$LAYER" "add storage to storage composite exit code"
    
    # Step 7: Connect components in storage composite
    test_start "$LAYER" "connect components in storage composite"
    local output=$(run_command "./s8r composite connect --from DataValidatorComponent --to DataStorageComponent --composite StorageComposite")
    assert_contains "$output" "Connected" "$LAYER" "connect components in storage composite success"
    assert_success $LAST_EXIT_CODE "$LAYER" "connect components in storage composite exit code"
    
    # Step 8: Create data pipeline machine
    test_start "$LAYER" "create data pipeline machine"
    local output=$(run_command "./s8r machine create --template flow DataPipelineMachine")
    assert_contains "$output" "created successfully" "$LAYER" "create data pipeline machine success"
    assert_success $LAST_EXIT_CODE "$LAYER" "create data pipeline machine exit code"
    
    # Step 9: Add composites to machine
    test_start "$LAYER" "add ingestion composite to machine"
    local output=$(run_command "./s8r machine add --composite IngestionComposite --machine DataPipelineMachine")
    assert_contains "$output" "added" "$LAYER" "add ingestion composite to machine success"
    assert_success $LAST_EXIT_CODE "$LAYER" "add ingestion composite to machine exit code"
    
    test_start "$LAYER" "add storage composite to machine"
    local output=$(run_command "./s8r machine add --composite StorageComposite --machine DataPipelineMachine")
    assert_contains "$output" "added" "$LAYER" "add storage composite to machine success"
    assert_success $LAST_EXIT_CODE "$LAYER" "add storage composite to machine exit code"
    
    # Step 10: Connect composites in machine
    test_start "$LAYER" "connect composites in machine"
    local output=$(run_command "./s8r machine connect --from IngestionComposite --to StorageComposite --machine DataPipelineMachine")
    assert_contains "$output" "Connected" "$LAYER" "connect composites in machine success"
    assert_success $LAST_EXIT_CODE "$LAYER" "connect composites in machine exit code"
    
    # Step 11: Start the machine
    test_start "$LAYER" "start data pipeline machine"
    local output=$(run_command "./s8r machine start DataPipelineMachine")
    assert_contains "$output" "started successfully" "$LAYER" "start data pipeline machine success"
    assert_success $LAST_EXIT_CODE "$LAYER" "start data pipeline machine exit code"
    
    # Step 12: Let it run for a moment (in a real test, we'd wait for data to flow through)
    sleep 1
    
    # Step 13: Stop the machine
    test_start "$LAYER" "stop data pipeline machine"
    local output=$(run_command "./s8r machine stop DataPipelineMachine")
    assert_contains "$output" "stopped successfully" "$LAYER" "stop data pipeline machine success"
    assert_success $LAST_EXIT_CODE "$LAYER" "stop data pipeline machine exit code"
    
    # Step 14: Cleanup - delete machine
    test_start "$LAYER" "delete data pipeline machine"
    local output=$(run_command "./s8r machine delete --force DataPipelineMachine")
    assert_contains "$output" "deleted successfully" "$LAYER" "delete data pipeline machine success"
    assert_success $LAST_EXIT_CODE "$LAYER" "delete data pipeline machine exit code"
}

#------------------------------------------------------------------------------
# Machine Tests for Multiple Machine Interaction
#------------------------------------------------------------------------------
run_machine_interaction_tests() {
    echo -e "${BOLD}Running Machine Tests - Multiple Machine Interaction${RESET}"
    
    # Create two machines that interact with each other
    
    # Step 1: Create producer machine
    test_start "$LAYER" "create producer machine"
    local output=$(run_command "./s8r machine create --template producer ProducerMachine")
    assert_contains "$output" "created successfully" "$LAYER" "create producer machine success"
    assert_success $LAST_EXIT_CODE "$LAYER" "create producer machine exit code"
    
    # Step 2: Create consumer machine
    test_start "$LAYER" "create consumer machine"
    local output=$(run_command "./s8r machine create --template consumer ConsumerMachine")
    assert_contains "$output" "created successfully" "$LAYER" "create consumer machine success"
    assert_success $LAST_EXIT_CODE "$LAYER" "create consumer machine exit code"
    
    # Step 3: Start the producer machine
    test_start "$LAYER" "start producer machine"
    local output=$(run_command "./s8r machine start ProducerMachine")
    assert_contains "$output" "started successfully" "$LAYER" "start producer machine success"
    assert_success $LAST_EXIT_CODE "$LAYER" "start producer machine exit code"
    
    # Step 4: Start the consumer machine
    test_start "$LAYER" "start consumer machine"
    local output=$(run_command "./s8r machine start ConsumerMachine")
    assert_contains "$output" "started successfully" "$LAYER" "start consumer machine success"
    assert_success $LAST_EXIT_CODE "$LAYER" "start consumer machine exit code"
    
    # Step 5: Let them run for a moment (in a real test, we'd verify data exchange)
    sleep 1
    
    # Step 6: Stop the consumer machine first
    test_start "$LAYER" "stop consumer machine"
    local output=$(run_command "./s8r machine stop ConsumerMachine")
    assert_contains "$output" "stopped successfully" "$LAYER" "stop consumer machine success"
    assert_success $LAST_EXIT_CODE "$LAYER" "stop consumer machine exit code"
    
    # Step 7: Stop the producer machine
    test_start "$LAYER" "stop producer machine"
    local output=$(run_command "./s8r machine stop ProducerMachine")
    assert_contains "$output" "stopped successfully" "$LAYER" "stop producer machine success"
    assert_success $LAST_EXIT_CODE "$LAYER" "stop producer machine exit code"
    
    # Step 8: Cleanup - delete machines
    test_start "$LAYER" "delete consumer machine"
    local output=$(run_command "./s8r machine delete --force ConsumerMachine")
    assert_contains "$output" "deleted successfully" "$LAYER" "delete consumer machine success"
    assert_success $LAST_EXIT_CODE "$LAYER" "delete consumer machine exit code"
    
    test_start "$LAYER" "delete producer machine"
    local output=$(run_command "./s8r machine delete --force ProducerMachine")
    assert_contains "$output" "deleted successfully" "$LAYER" "delete producer machine success"
    assert_success $LAST_EXIT_CODE "$LAYER" "delete producer machine exit code"
}

#------------------------------------------------------------------------------
# Run all machine tests
#------------------------------------------------------------------------------
run_machine_tests() {
    echo -e "${BOLD}${MAGENTA}Running Machine Tests${RESET}"
    echo -e "${MAGENTA}======================${RESET}\n"
    
    run_machine_dataflow_tests
    run_machine_interaction_tests
    
    echo -e "\n${MAGENTA}Machine Tests Complete${RESET}"
}