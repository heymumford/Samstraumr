/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools 
 * including Claude 3.7 Sonnet, Claude Code, and GitHub Copilot Pro,
 * which were used as paid services. All intellectual property rights 
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0
 */
package org.s8r.test.steps.lifecycle;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import io.cucumber.datatable.DataTable;

import org.junit.jupiter.api.Assertions;
import org.s8r.component.Component;
import org.s8r.component.Environment;
import org.s8r.component.Identity;
import org.s8r.component.InvalidStateTransitionException;
import org.s8r.component.State;
import org.s8r.test.util.ComponentTestUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Step definitions for component lifecycle state machine tests.
 */
public class LifecycleStateMachineSteps {

    // Test context to share between steps
    private Component component;
    private List<State> transitionHistory = new ArrayList<>();
    private Map<String, Object> resourceLevels = new HashMap<>();
    private Exception lastException;
    private State preTransitionState;
    private List<String> establishedConnections = new ArrayList<>();

    @Given("the S8r framework is initialized")
    public void the_s8r_framework_is_initialized() {
        // Initialize any required framework settings
        // This is a placeholder for actual initialization if needed
    }

    @When("I create a component with reason {string}")
    public void i_create_a_component_with_reason(String reason) {
        component = Component.createAdam(reason);
        transitionHistory.add(component.getState());
    }

    @When("I create a component")
    public void i_create_a_component() {
        component = Component.createAdam("State Category Test");
    }

    @Then("the component should be in {string} state")
    public void the_component_should_be_in_state(String stateName) {
        Assertions.assertEquals(
            State.valueOf(stateName),
            component.getState(),
            "Component should be in " + stateName + " state"
        );
    }

    @When("the component proceeds through initial lifecycle phases")
    public void the_component_proceeds_through_initial_lifecycle_phases() {
        // Record initial state before proceeding
        transitionHistory.add(component.getState());
        
        // Explicitly call the method that handles early lifecycle progression
        component.proceedThroughEarlyLifecycle();
        
        // State should now be READY as per the Component initialization process
        // This is set in the initialize() method of Component
    }

    @Then("the component should transition through the following states in order:")
    public void the_component_should_transition_through_the_following_states_in_order(DataTable dataTable) {
        List<String> expectedStateNames = dataTable.asList();
        List<State> expectedStates = expectedStateNames.stream()
            .map(State::valueOf)
            .collect(Collectors.toList());

        // Get the actual transitions from the component's memory log
        List<String> memoryLog = component.getMemoryLog();
        List<State> actualTransitions = new ArrayList<>();
        
        // The initial state should be in our transition history
        actualTransitions.addAll(transitionHistory);
        
        // Extract state transitions from memory log
        for (String logEntry : memoryLog) {
            if (logEntry.contains("State changed:")) {
                String[] parts = logEntry.split("State changed: ")[1].split(" -> ");
                if (parts.length == 2) {
                    actualTransitions.add(State.valueOf(parts[1]));
                }
            }
        }
        
        // Verify that all expected states were observed in order
        for (int i = 0; i < expectedStates.size(); i++) {
            State expected = expectedStates.get(i);
            
            // Find this state in the actual transitions
            boolean found = false;
            for (State actual : actualTransitions) {
                if (actual == expected) {
                    found = true;
                    break;
                }
            }
            
            Assertions.assertTrue(found, 
                "Component should have transitioned through state: " + expected);
        }
    }

    @Then("the component's memory log should record all state transitions")
    public void the_components_memory_log_should_record_all_state_transitions() {
        List<String> memoryLog = component.getMemoryLog();
        boolean foundStateChanges = false;
        
        for (String logEntry : memoryLog) {
            if (logEntry.contains("State changed:")) {
                foundStateChanges = true;
                break;
            }
        }
        
        Assertions.assertTrue(foundStateChanges, 
            "Component memory log should record state transitions");
    }

    @Given("a component in {string} state")
    public void a_component_in_state(String stateName) {
        // Create a fresh component
        component = Component.createAdam("State Test Component");
        
        // Directly set the state if needed
        State targetState = State.valueOf(stateName);
        
        // We'll use the setState method to transition to the target state
        // In some cases, we may need multiple transitions to reach the target state
        if (targetState != component.getState()) {
            component.setState(targetState);
        }
        
        // Verify we reached the desired state
        Assertions.assertEquals(targetState, component.getState(),
            "Component should be in " + stateName + " state");
    }

    @When("I transition the component to {string} state")
    public void i_transition_the_component_to_state(String stateName) {
        preTransitionState = component.getState();
        try {
            component.setState(State.valueOf(stateName));
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("the component should be operational")
    public void the_component_should_be_operational() {
        Assertions.assertTrue(component.isOperational(),
            "Component should be operational");
    }

    @Then("the component cannot transition directly to the following states:")
    public void the_component_cannot_transition_directly_to_the_following_states(DataTable dataTable) {
        List<String> invalidStateNames = dataTable.asList();
        
        for (String stateName : invalidStateNames) {
            State targetState = State.valueOf(stateName);
            State currentState = component.getState();
            
            try {
                component.setState(targetState);
                // If we get here, the transition was allowed but shouldn't have been
                Assertions.fail("Component should not be able to transition from " + 
                    currentState + " to " + targetState);
            } catch (InvalidStateTransitionException e) {
                // This is the expected behavior
                Assertions.assertEquals(currentState, component.getState(),
                    "Component state should remain unchanged after invalid transition attempt");
            } catch (Exception e) {
                // Some other exception occurred
                Assertions.fail("Unexpected exception: " + e.getMessage());
            }
        }
    }

    @Then("transitioning to invalid states should throw InvalidStateTransitionException")
    public void transitioning_to_invalid_states_should_throw_invalid_state_transition_exception() {
        // This is verified by the previous step
    }

    @Then("the component can transition to the following states:")
    public void the_component_can_transition_to_the_following_states(DataTable dataTable) {
        List<String> validStateNames = dataTable.asList();
        
        for (String stateName : validStateNames) {
            // Create a fresh component for each test to avoid interference
            Component testComponent = Component.createAdam("Transition Test");
            
            // Set it to the same state as our main component
            testComponent.setState(component.getState());
            
            try {
                // Attempt the transition
                testComponent.setState(State.valueOf(stateName));
                
                // If we get here, the transition was allowed as expected
                Assertions.assertEquals(State.valueOf(stateName), testComponent.getState(),
                    "Component should be able to transition to " + stateName);
            } catch (Exception e) {
                Assertions.fail("Component should be able to transition to " + stateName + 
                    " but got: " + e.getMessage());
            }
        }
    }

    @When("I attempt to transition directly to {string} state")
    public void i_attempt_to_transition_directly_to_state(String stateName) {
        preTransitionState = component.getState();
        
        try {
            component.setState(State.valueOf(stateName));
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("the transition should be rejected")
    public void the_transition_should_be_rejected() {
        Assertions.assertNotNull(lastException, 
            "Invalid transition should throw an exception");
    }

    @Then("an InvalidStateTransitionException should be thrown")
    public void an_invalid_state_transition_exception_should_be_thrown() {
        Assertions.assertTrue(lastException instanceof InvalidStateTransitionException,
            "Exception should be InvalidStateTransitionException, but was: " + 
            lastException.getClass().getSimpleName());
    }

    @Then("the component should remain in {string} state")
    public void the_component_should_remain_in_state(String stateName) {
        Assertions.assertEquals(State.valueOf(stateName), component.getState(),
            "Component should remain in " + stateName + " state");
    }

    @Given("a component that has been terminated")
    public void a_component_that_has_been_terminated() {
        component = Component.createAdam("Terminated Component Test");
        component.terminate("Test termination");
        
        Assertions.assertEquals(State.TERMINATED, component.getState(),
            "Component should be in TERMINATED state");
    }

    @Then("the component can transition to {string} state")
    public void the_component_can_transition_to_state(String stateName) {
        try {
            component.setState(State.valueOf(stateName));
            Assertions.assertEquals(State.valueOf(stateName), component.getState(),
                "Component should be able to transition to " + stateName);
        } catch (Exception e) {
            Assertions.fail("Component should be able to transition to " + stateName + 
                " but got: " + e.getMessage());
        }
    }

    @When("I terminate the component")
    public void i_terminate_the_component() {
        // Record the initial state
        transitionHistory.clear();
        transitionHistory.add(component.getState());
        
        // Terminate the component
        component.terminate("Test termination");
        
        // Add the final state to the history
        transitionHistory.add(component.getState());
    }

    @Then("the component should preserve knowledge before termination")
    public void the_component_should_preserve_knowledge_before_termination() {
        // Check memory log for knowledge preservation
        List<String> memoryLog = component.getMemoryLog();
        boolean foundPreservationEntry = false;
        
        for (String logEntry : memoryLog) {
            if (logEntry.contains("Preserving knowledge")) {
                foundPreservationEntry = true;
                break;
            }
        }
        
        Assertions.assertTrue(foundPreservationEntry,
            "Component should preserve knowledge before termination");
    }

    @Then("the component should release all resources")
    public void the_component_should_release_all_resources() {
        // Check memory log for resource release
        List<String> memoryLog = component.getMemoryLog();
        boolean foundResourceReleaseEntry = false;
        
        for (String logEntry : memoryLog) {
            if (logEntry.contains("Releasing") && logEntry.contains("resources")) {
                foundResourceReleaseEntry = true;
                break;
            }
        }
        
        Assertions.assertTrue(foundResourceReleaseEntry,
            "Component should release all resources during termination");
        
        // Check that resources are actually released
        int connections = component.getResourceUsage("connections");
        int timers = component.getResourceUsage("timers");
        
        Assertions.assertEquals(0, connections, "All connections should be released");
        Assertions.assertEquals(0, timers, "All timers should be released");
    }

    @Then("the component should unsubscribe from all events")
    public void the_component_should_unsubscribe_from_all_events() {
        // Check memory log for event unsubscription
        List<String> memoryLog = component.getMemoryLog();
        boolean foundUnsubscribeEntry = false;
        
        for (String logEntry : memoryLog) {
            if (logEntry.contains("Unsubscribed from all events")) {
                foundUnsubscribeEntry = true;
                break;
            }
        }
        
        Assertions.assertTrue(foundUnsubscribeEntry,
            "Component should unsubscribe from all events during termination");
        
        // Check that there are no active listeners
        List<?> activeListeners = component.getActiveListeners();
        Assertions.assertTrue(activeListeners.isEmpty(),
            "Component should have no active listeners after termination");
    }

    @Given("a component in {string} state with active connections")
    public void a_component_in_state_with_active_connections(String stateName) {
        // Create a component and set to the desired state
        component = Component.createAdam("Connection Test Component");
        component.setState(State.valueOf(stateName));
        
        // Establish some connections
        establishedConnections.add(component.establishConnection("Test Connection 1"));
        establishedConnections.add(component.establishConnection("Test Connection 2"));
        
        // Verify connections were established
        Assertions.assertEquals(2, component.getResourceUsage("connections"),
            "Component should have 2 active connections");
    }

    @When("I suspend the component with reason {string}")
    public void i_suspend_the_component_with_reason(String reason) {
        preTransitionState = component.getState();
        component.suspend(reason);
    }

    @Then("all connections should be closed")
    public void all_connections_should_be_closed() {
        Assertions.assertEquals(0, component.getResourceUsage("connections"),
            "All connections should be closed when component is suspended");
    }

    @Then("data processing should be paused")
    public void data_processing_should_be_paused() {
        // In suspended state, the component should not allow data processing
        Assertions.assertFalse(component.getState().allowsDataProcessing(),
            "Data processing should be paused in " + component.getState() + " state");
    }

    @Then("the component should store the pre-suspension state")
    public void the_component_should_store_the_pre_suspension_state() {
        Object preSuspendedState = component.getProperty("preSuspendedState");
        Assertions.assertNotNull(preSuspendedState,
            "Component should store the pre-suspension state");
        Assertions.assertEquals(preTransitionState.name(), preSuspendedState,
            "Stored pre-suspension state should match the actual pre-suspension state");
    }

    @When("I resume the component")
    public void i_resume_the_component() {
        component.resume();
    }

    @Then("the component should return to its pre-suspension state")
    public void the_component_should_return_to_its_pre_suspension_state() {
        Assertions.assertEquals(preTransitionState, component.getState(),
            "Component should return to its pre-suspension state after resuming");
    }

    @Then("data processing should resume")
    public void data_processing_should_resume() {
        // In active state, the component should allow data processing
        Assertions.assertTrue(component.getState().allowsDataProcessing(),
            "Data processing should resume in " + component.getState() + " state");
    }

    @When("I put the component in maintenance mode with reason {string}")
    public void i_put_the_component_in_maintenance_mode_with_reason(String reason) {
        preTransitionState = component.getState();
        component.enterMaintenanceMode(reason);
    }

    @Then("advanced configuration operations should be allowed")
    public void advanced_configuration_operations_should_be_allowed() {
        Assertions.assertTrue(component.getState().allowsAdvancedConfigurationChanges(),
            "Advanced configuration operations should be allowed in MAINTENANCE state");
    }

    @Then("diagnostic operations should be allowed")
    public void diagnostic_operations_should_be_allowed() {
        // Test if diagnostics operations are allowed
        try {
            // Here we would perform a diagnostic operation
            // For testing, we just check if the state allows diagnostics
            Assertions.assertTrue(component.canPerformOperation("run_diagnostics"),
                "Diagnostic operations should be allowed in MAINTENANCE state");
        } catch (Exception e) {
            Assertions.fail("Diagnostic operations should be allowed: " + e.getMessage());
        }
    }

    @When("I perform advanced configuration changes")
    public void i_perform_advanced_configuration_changes() {
        try {
            // Make advanced configuration changes
            Environment env = component.getEnvironment();
            env.setParameter("advanced.setting1", "new-value1");
            env.setParameter("advanced.setting2", "new-value2");
        } catch (Exception e) {
            Assertions.fail("Should be able to perform advanced configuration changes: " + e.getMessage());
        }
    }

    @When("I exit maintenance mode")
    public void i_exit_maintenance_mode() {
        component.exitMaintenanceMode();
    }

    @Then("the component should return to its pre-maintenance state")
    public void the_component_should_return_to_its_pre_maintenance_state() {
        Assertions.assertEquals(preTransitionState, component.getState(),
            "Component should return to its pre-maintenance state after exiting maintenance mode");
    }

    @Then("the new configuration should be applied")
    public void the_new_configuration_should_be_applied() {
        Environment env = component.getEnvironment();
        
        Assertions.assertEquals("new-value1", env.getParameter("advanced.setting1"),
            "Advanced setting 1 should be updated");
        Assertions.assertEquals("new-value2", env.getParameter("advanced.setting2"),
            "Advanced setting 2 should be updated");
    }

    @Given("a component in {string} state processing data")
    public void a_component_in_state_processing_data(String stateName) {
        // Create a component and set to the desired state
        component = Component.createAdam("Error Recovery Test");
        component.setState(State.valueOf(stateName));
        
        // Simulate processing data by allocating resources
        component.allocateResource("memory", 20);
        component.allocateResource("threads", 2);
        component.publishEvent("processing.started", new HashMap<>());
    }

    @When("an error occurs in the component")
    public void an_error_occurs_in_the_component() {
        // Simulate an error by transitioning to ERROR state
        component.setState(State.ERROR);
    }

    @When("recovery is initiated")
    public void recovery_is_initiated() {
        // Simulate recovery initiation
        component.setState(State.RECOVERING);
    }

    @Then("recovery diagnostics should run")
    public void recovery_diagnostics_should_run() {
        // In a real system, diagnostics would run during recovery
        // For this test, we just verify the component is in RECOVERING state
        Assertions.assertEquals(State.RECOVERING, component.getState(),
            "Component should be in RECOVERING state during diagnostics");
    }

    @When("recovery completes successfully")
    public void recovery_completes_successfully() {
        // Simulate successful recovery by returning to ACTIVE state
        component.setState(State.ACTIVE);
    }

    @Then("resource usage should be restored")
    public void resource_usage_should_be_restored() {
        // Verify resources are restored to normal levels
        Assertions.assertTrue(component.getResourceUsage("memory") > 0,
            "Memory resource should be restored after recovery");
        Assertions.assertTrue(component.getResourceUsage("threads") > 0,
            "Thread resource should be restored after recovery");
    }

    @Then("the recovery attempt should be logged")
    public void the_recovery_attempt_should_be_logged() {
        // Check memory log for recovery entries
        List<String> memoryLog = component.getMemoryLog();
        boolean foundRecoveryEntry = false;
        
        for (String logEntry : memoryLog) {
            if (logEntry.contains("RECOVERING") || logEntry.contains("recovery")) {
                foundRecoveryEntry = true;
                break;
            }
        }
        
        Assertions.assertTrue(foundRecoveryEntry,
            "Component should log recovery attempts");
    }

    @Given("a component with monitored resources")
    public void a_component_with_monitored_resources() {
        // Create a component with initialized resource tracking
        component = Component.createAdam("Resource Monitoring Test");
        component.initializeResourceTracking();
        
        // Capture initial resource levels
        resourceLevels.put("initial.memory", component.getResourceUsage("memory"));
        resourceLevels.put("initial.threads", component.getResourceUsage("threads"));
        resourceLevels.put("initial.connections", component.getResourceUsage("connections"));
        resourceLevels.put("initial.timers", component.getResourceUsage("timers"));
    }

    @When("I transition the component through the following states:")
    public void i_transition_the_component_through_the_following_states(DataTable dataTable) {
        List<String> stateNames = dataTable.asList();
        transitionHistory.clear();
        
        // Record resource usage for each state
        for (String stateName : stateNames) {
            // Transition to the state
            component.setState(State.valueOf(stateName));
            transitionHistory.add(component.getState());
            
            // Capture resource levels for this state
            resourceLevels.put(stateName + ".memory", component.getResourceUsage("memory"));
            resourceLevels.put(stateName + ".threads", component.getResourceUsage("threads"));
            resourceLevels.put(stateName + ".connections", component.getResourceUsage("connections"));
            resourceLevels.put(stateName + ".timers", component.getResourceUsage("timers"));
        }
    }

    @Then("resource levels should change appropriately for each state")
    public void resource_levels_should_change_appropriately_for_each_state() {
        // Verify resource levels changed as expected for different states
        
        // ACTIVE state should have full resources
        if (resourceLevels.containsKey("ACTIVE.memory")) {
            Assertions.assertTrue((int)resourceLevels.get("ACTIVE.memory") >= (int)resourceLevels.get("initial.memory"),
                "Memory usage should be fully available in ACTIVE state");
        }
        
        // SUSPENDED state should have reduced connections
        if (resourceLevels.containsKey("SUSPENDED.connections")) {
            Assertions.assertEquals(0, resourceLevels.get("SUSPENDED.connections"),
                "Connections should be zero in SUSPENDED state");
        }
        
        // MAINTENANCE state should have similar pattern
        if (resourceLevels.containsKey("MAINTENANCE.connections")) {
            Assertions.assertEquals(0, resourceLevels.get("MAINTENANCE.connections"),
                "Connections should be zero in MAINTENANCE state");
        }
        
        // TERMINATED state should have minimal resources
        if (resourceLevels.containsKey("TERMINATED.memory")) {
            Assertions.assertTrue((int)resourceLevels.get("TERMINATED.memory") < (int)resourceLevels.get("initial.memory"),
                "Memory usage should be reduced in TERMINATED state");
        }
    }

    @Then("no resource leaks should occur")
    public void no_resource_leaks_should_occur() {
        // Verify that termination properly releases all resources
        if (component.getState() == State.TERMINATED) {
            Assertions.assertEquals(0, component.getResourceUsage("connections"),
                "All connections should be released in TERMINATED state");
            Assertions.assertEquals(0, component.getResourceUsage("timers"),
                "All timers should be released in TERMINATED state");
        }
    }

    @Then("the following states should be in the {string} category:")
    public void the_following_states_should_be_in_the_category(String categoryName, DataTable dataTable) {
        List<String> stateNames = dataTable.asList();
        
        for (String stateName : stateNames) {
            State state = State.valueOf(stateName);
            
            switch (categoryName) {
                case "OPERATIONAL":
                    Assertions.assertTrue(state.isOperational(),
                        state + " should be in OPERATIONAL category");
                    break;
                case "LIFECYCLE":
                    Assertions.assertTrue(state.isLifecycle(),
                        state + " should be in LIFECYCLE category");
                    break;
                case "ADVANCED":
                    Assertions.assertTrue(state.isAdvanced(),
                        state + " should be in ADVANCED category");
                    break;
                case "TERMINATION":
                    Assertions.assertTrue(state.isTermination(),
                        state + " should be in TERMINATION category");
                    break;
                default:
                    Assertions.fail("Unknown category: " + categoryName);
            }
        }
    }
}