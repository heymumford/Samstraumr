/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools 
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
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
import org.s8r.component.State;
import org.s8r.component.Identity;
import org.s8r.component.Environment;
import org.s8r.test.util.ComponentTestUtil;
import org.s8r.test.util.ListenerFactory;
import org.s8r.test.util.ListenerFactory.RecordingEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Step definitions focused on component operations in different lifecycle states.
 * This class tests the operational aspects of the Component lifecycle.
 */
public class LifecycleOperationsSteps {

    // Test context to share between steps
    private Component component;
    private Map<String, Object> operationResults = new HashMap<>();
    private List<String> operationLog = new ArrayList<>();
    private RecordingEventListener eventListener;
    private Exception lastException;
    
    @Given("the S8r framework is initialized")
    public void the_s8r_framework_is_initialized() {
        // Framework initialization is simulated, no real action needed in tests
        operationLog.add("S8r framework initialized");
    }
    
    @Given("a component in {string} state")
    public void a_component_in_state(String stateName) {
        component = ComponentTestUtil.createTestComponent("Lifecycle Test Component");
        component.setState(State.valueOf(stateName));
        operationLog.add("Created component in " + stateName + " state");
    }
    
    @Given("a component in {string} state with ongoing data processing")
    public void a_component_in_state_with_ongoing_data_processing(String stateName) {
        component = ComponentTestUtil.createTestComponent("Data Processing Test");
        
        // Simulate starting data processing
        Map<String, Object> data = new HashMap<>();
        data.put("operation", "processingStart");
        data.put("timestamp", LocalDateTime.now().toString());
        component.setProperty("dataProcessing", data);
        operationLog.add("Started data processing");
        
        // Set component to desired state
        component.setState(State.valueOf(stateName));
        operationLog.add("Set component to " + stateName + " state");
    }
    
    @When("I test the component's functionality")
    public void i_test_the_components_functionality() {
        operationResults = ComponentTestUtil.testAllOperations(component);
        operationLog.add("Tested all component operations");
    }
    
    @When("I transition the component to {string} state")
    public void i_transition_the_component_to_state(String stateName) {
        try {
            State targetState = State.valueOf(stateName);
            
            // Use the appropriate method based on the state
            if (targetState == State.SUSPENDED) {
                component.suspend("Test-initiated suspension");
            } else if (targetState == State.MAINTENANCE) {
                component.enterMaintenanceMode("Test-initiated maintenance");
            } else {
                component.setState(targetState);
            }
            
            operationLog.add("Transitioned component to " + stateName + " state");
        } catch (Exception e) {
            lastException = e;
            operationLog.add("Exception during transition to " + stateName + ": " + e.getMessage());
        }
    }
    
    @When("I transition the component back to {string} state")
    public void i_transition_the_component_back_to_state(String stateName) {
        try {
            State targetState = State.valueOf(stateName);
            
            // Use the appropriate method based on the current state
            if (component.isSuspended()) {
                component.resume();
            } else if (component.isInMaintenance()) {
                component.exitMaintenanceMode();
            } else {
                component.setState(targetState);
            }
            
            operationLog.add("Transitioned component back to " + stateName + " state");
        } catch (Exception e) {
            lastException = e;
            operationLog.add("Exception during transition back to " + stateName + ": " + e.getMessage());
        }
    }
    
    @When("I make configuration changes in maintenance mode")
    public void i_make_configuration_changes_in_maintenance_mode() {
        try {
            Assertions.assertEquals(State.MAINTENANCE, component.getState(),
                "Component should be in MAINTENANCE state");
            
            // Make advanced configuration changes
            Map<String, Object> config = new HashMap<>();
            config.put("advanced.setting1", "new-value1");
            config.put("advanced.setting2", "new-value2");
            
            component.performOperation("update_config", config);
            operationLog.add("Made configuration changes in maintenance mode");
            
        } catch (Exception e) {
            lastException = e;
            operationLog.add("Exception during configuration changes: " + e.getMessage());
        }
    }
    
    @When("I create a component")
    public void i_create_a_component() {
        component = ComponentTestUtil.createTestComponent("General Test Component");
        operationLog.add("Created new component");
    }
    
    @When("I create a component with monitored resources")
    public void i_create_a_component_with_monitored_resources() {
        component = ComponentTestUtil.createResourceMonitoredComponent("Resource Test Component");
        operationLog.add("Created component with monitored resources");
    }
    
    @Given("a component that publishes and subscribes to events")
    public void a_component_that_publishes_and_subscribes_to_events() {
        component = ComponentTestUtil.createEventTestComponent("Event Test Component");
        
        // Add a specific listener for testing
        eventListener = ListenerFactory.createEventListener("*");
        component.addEventListener(eventListener, "*");
        
        operationLog.add("Created component with event capabilities");
    }
    
    @When("the component is in {string} state")
    public void the_component_is_in_state(String stateName) {
        component.setState(State.valueOf(stateName));
        operationLog.add("Set component to " + stateName + " state");
    }
    
    @When("I simulate a recoverable error")
    public void i_simulate_a_recoverable_error() {
        try {
            component.triggerRecoverableError();
            operationLog.add("Triggered recoverable error");
        } catch (Exception e) {
            lastException = e;
            operationLog.add("Exception during error simulation: " + e.getMessage());
        }
    }
    
    @When("recovery completes")
    public void recovery_completes() {
        try {
            // Wait for the simulated recovery process to complete
            Thread.sleep(2500);
            operationLog.add("Waited for recovery completion");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            operationLog.add("Interrupted while waiting for recovery");
        }
    }
    
    @Then("all operations should succeed")
    public void all_operations_should_succeed() {
        for (Map.Entry<String, Object> entry : operationResults.entrySet()) {
            if (entry.getValue() instanceof Boolean) {
                Assertions.assertTrue((Boolean)entry.getValue(), 
                    "Operation " + entry.getKey() + " should succeed");
            }
        }
        
        Assertions.assertFalse(operationResults.containsKey("exception"),
            "No exceptions should occur during operations");
    }
    
    @Then("data processing should be enabled")
    public void data_processing_should_be_enabled() {
        boolean canProcess = component.canPerformOperation("process_data");
        Assertions.assertTrue(canProcess, "Data processing should be enabled");
    }
    
    @Then("data processing should be paused")
    public void data_processing_should_be_paused() {
        boolean canProcess = component.canPerformOperation("process_data");
        Assertions.assertFalse(canProcess, "Data processing should be paused");
    }
    
    @Then("monitoring should be enabled")
    public void monitoring_should_be_enabled() {
        boolean canMonitor = component.canPerformOperation("query_status");
        Assertions.assertTrue(canMonitor, "Monitoring should be enabled");
    }
    
    @Then("monitoring should remain enabled")
    public void monitoring_should_remain_enabled() {
        monitoring_should_be_enabled();
    }
    
    @Then("configuration changes should be allowed")
    public void configuration_changes_should_be_allowed() {
        boolean canUpdateConfig = component.canPerformOperation("update_config");
        Assertions.assertTrue(canUpdateConfig, "Configuration changes should be allowed");
    }
    
    @Then("configuration changes should be restricted")
    public void configuration_changes_should_be_restricted() {
        boolean canUpdateConfig = component.canPerformOperation("update_config");
        Assertions.assertFalse(canUpdateConfig, "Configuration changes should be restricted");
    }
    
    @Then("the component should accept incoming connections")
    public void the_component_should_accept_incoming_connections() {
        boolean canEstablishConnection = component.canPerformOperation("establish_connection");
        Assertions.assertTrue(canEstablishConnection, "Should accept incoming connections");
    }
    
    @Then("pending operations should be queued")
    public void pending_operations_should_be_queued() {
        // Attempt to publish an event which should be queued in this state
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("test", "value");
        
        boolean published = component.publishEvent("test.event", eventData);
        
        // In SUSPENDED state, publishEvent() returns false if the event was queued
        Assertions.assertFalse(published, "Event should be queued, not published directly");
    }
    
    @Then("the component should reject new data processing requests")
    public void the_component_should_reject_new_data_processing_requests() {
        try {
            component.performOperation("process_data", new HashMap<>());
            Assertions.fail("Should have rejected data processing request");
        } catch (Exception e) {
            // Expected - operation should be rejected
            Assertions.assertTrue(e instanceof IllegalStateException, 
                "Should throw IllegalStateException for rejected operations");
        }
    }
    
    @Then("queued operations should resume")
    public void queued_operations_should_resume() {
        // This is tested indirectly by the state transition, as Component.resume() 
        // automatically processes queued events
        
        // Verify event listener received queued events after resuming
        Assertions.assertTrue(eventListener.getEventCount() > 0, 
            "Event listener should have received events after resuming");
    }
    
    @Then("data processing should continue")
    public void data_processing_should_continue() {
        data_processing_should_be_enabled();
    }
    
    @Then("advanced configuration changes should be allowed")
    public void advanced_configuration_changes_should_be_allowed() {
        Assertions.assertEquals(State.MAINTENANCE, component.getState(),
            "Component should be in MAINTENANCE state");
        
        try {
            // Try to make an advanced configuration change
            Map<String, Object> config = new HashMap<>();
            config.put("advanced.critical.setting", "new-value");
            component.performOperation("update_config", config);
            
            // If we got here, it succeeded
        } catch (Exception e) {
            Assertions.fail("Advanced configuration changes should be allowed: " + e.getMessage());
        }
    }
    
    @Then("diagnostic operations should be available")
    public void diagnostic_operations_should_be_available() {
        boolean canRunDiagnostics = component.canPerformOperation("run_diagnostics");
        Assertions.assertTrue(canRunDiagnostics, "Diagnostic operations should be available");
    }
    
    @Then("the new configuration should be applied")
    public void the_new_configuration_should_be_applied() {
        Environment env = component.getEnvironment();
        
        Assertions.assertEquals("new-value1", env.getValue("advanced.setting1"),
            "Setting 1 should be updated");
        Assertions.assertEquals("new-value2", env.getValue("advanced.setting2"),
            "Setting 2 should be updated");
    }
    
    @Then("data processing should resume with new configuration")
    public void data_processing_should_resume_with_new_configuration() {
        // Check if data processing is enabled
        data_processing_should_be_enabled();
        
        // Verify configuration changes were kept
        Environment env = component.getEnvironment();
        Assertions.assertEquals("new-value1", env.getValue("advanced.setting1"),
            "Setting 1 should still be applied");
    }
    
    @Then("in {string} state the following operations should be available:")
    public void in_state_the_following_operations_should_be_available(
            String stateName, DataTable dataTable) {
        // Set the component to the specified state
        component.setState(State.valueOf(stateName));
        
        // Try each operation
        for (String operation : dataTable.asList()) {
            boolean canPerform = component.canPerformOperation(operation.trim());
            Assertions.assertTrue(canPerform, 
                "Operation '" + operation + "' should be available in " + stateName + " state");
        }
    }
    
    @Then("in {string} state no operations should be available")
    public void in_state_no_operations_should_be_available(String stateName) {
        // Ensure component is in the right state
        if (!component.getState().name().equals(stateName)) {
            component.setState(State.valueOf(stateName));
        }
        
        // Test operations that should fail
        List<String> operations = List.of(
            "process_data", "query_status", "update_config", "establish_connection");
        
        for (String operation : operations) {
            try {
                boolean canPerform = component.canPerformOperation(operation);
                Assertions.assertFalse(canPerform, 
                    "Operation '" + operation + "' should not be available in " + stateName + " state");
            } catch (Exception e) {
                // Exception is also an acceptable outcome for terminated components
                Assertions.assertTrue(e.getMessage().contains("terminated"),
                    "Exception should mention terminated state");
            }
        }
    }
    
    @Then("resources should be allocated during initialization")
    public void resources_should_be_allocated_during_initialization() {
        Assertions.assertTrue(component.getResourceUsage("memory") > 0, 
            "Memory resources should be allocated");
        Assertions.assertTrue(component.getResourceUsage("threads") > 0, 
            "Thread resources should be allocated");
    }
    
    @Then("resources should be fully available in {string} state")
    public void resources_should_be_fully_available_in_state(String stateName) {
        // Set component to the specified state
        component.setState(State.valueOf(stateName));
        
        // Verify resources match expected state
        component.updateResourceUsage();
        
        Assertions.assertTrue(ComponentTestUtil.verifyResourceAvailability(component, "memory", 40), 
            "Memory resources should be fully available");
        Assertions.assertTrue(ComponentTestUtil.verifyResourceAvailability(component, "threads", 2), 
            "Thread resources should be fully available");
    }
    
    @Then("resources should be partially released in {string} state")
    public void resources_should_be_partially_released_in_state(String stateName) {
        // First set component to fully active to ensure resources are allocated
        component.setState(State.ACTIVE);
        component.updateResourceUsage();
        
        // Capture initial resource levels
        int initialConnections = component.getResourceUsage("connections");
        int initialTimers = component.getResourceUsage("timers");
        
        // Now transition to specified state
        component.setState(State.valueOf(stateName));
        component.updateResourceUsage();
        
        // Verify reduced resource usage in specified state
        int currentConnections = component.getResourceUsage("connections");
        int currentTimers = component.getResourceUsage("timers");
        
        Assertions.assertTrue(currentConnections < initialConnections, 
            "Connection resources should be reduced");
        Assertions.assertTrue(currentTimers < initialTimers, 
            "Timer resources should be reduced");
    }
    
    @Then("all resources should be released in {string} state")
    public void all_resources_should_be_released_in_state(String stateName) {
        // Terminate the component
        component.terminate("Test termination");
        
        // Verify resource release
        Assertions.assertEquals(0, component.getResourceUsage("connections"), 
            "Connection resources should be released");
        Assertions.assertEquals(0, component.getResourceUsage("timers"), 
            "Timer resources should be released");
        Assertions.assertTrue(component.getResourceUsage("threads") <= 1, 
            "Thread resources should be mostly released (except for cleanup thread)");
    }
    
    @Then("it should publish and receive events")
    public void it_should_publish_and_receive_events() {
        // Clear any previous events
        eventListener.clearEvents();
        
        // Publish test event
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("test", "value");
        boolean published = component.publishEvent("test.event", eventData);
        
        // Verify success
        Assertions.assertTrue(published, "Event should be published");
        Assertions.assertTrue(eventListener.getEventCount() > 0, 
            "Event listener should have received the event");
    }
    
    @Then("it should not publish events but still receive them")
    public void it_should_not_publish_events_but_still_receive_them() {
        // Clear any previous events
        eventListener.clearEvents();
        
        // Try to publish event
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("test", "value");
        boolean published = component.publishEvent("test.event", eventData);
        
        // Verify the event was not published directly
        Assertions.assertFalse(published, "Event should not be published in SUSPENDED state");
        
        // Create a second component to send events to the suspended one
        Component senderComponent = ComponentTestUtil.createTestComponent("Sender Component");
        
        // Add our component as a listener to the sender
        senderComponent.addEventListener((c, type, data) -> {
            // Forward the event to our component's event listeners
            for (var listener : component.getActiveListeners()) {
                listener.onEvent(component, type, data);
            }
        }, "*");
        
        // Now have the sender component publish an event
        Map<String, Object> externalEventData = new HashMap<>();
        externalEventData.put("source", "external");
        senderComponent.publishEvent("external.event", externalEventData);
        
        // Verify our listener received the external event
        Assertions.assertTrue(eventListener.getEventCount() > 0, 
            "Event listener should have received the external event");
    }
    
    @Then("it should publish diagnostic events but queue normal events")
    public void it_should_publish_diagnostic_events_but_queue_normal_events() {
        // Clear any previous events
        eventListener.clearEvents();
        
        // Try to publish normal event - should be queued
        Map<String, Object> normalEventData = new HashMap<>();
        normalEventData.put("type", "normal");
        boolean normalPublished = component.publishEvent("normal.event", normalEventData);
        
        // Verify normal event was queued (not published directly)
        Assertions.assertFalse(normalPublished, "Normal event should be queued in MAINTENANCE state");
        
        // Try to publish diagnostic event - should succeed
        Map<String, Object> diagnosticEventData = new HashMap<>();
        diagnosticEventData.put("type", "diagnostic");
        boolean diagnosticPublished = component.publishEvent("diagnostic.health", diagnosticEventData);
        
        // Verify diagnostic event was published
        Assertions.assertTrue(diagnosticPublished, "Diagnostic event should be published in MAINTENANCE state");
        Assertions.assertTrue(eventListener.getEventCount() > 0, 
            "Event listener should have received the diagnostic event");
    }
    
    @Then("it should unsubscribe from all events")
    public void it_should_unsubscribe_from_all_events() {
        // Terminate component, which should unsubscribe from all events
        component.terminate("Test termination");
        
        // Verify listener was terminated
        Assertions.assertTrue(eventListener.isTerminated(), 
            "Event listener should have been terminated");
    }
    
    @Then("it should not publish any events")
    public void it_should_not_publish_any_events() {
        // Clear any existing events
        eventListener.clearEvents();
        
        // Try to publish an event after termination
        try {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("type", "post-termination");
            component.publishEvent("test.event", eventData);
            
            Assertions.fail("Should not be able to publish events after termination");
        } catch (Exception e) {
            // Expected exception
            Assertions.assertTrue(e.getMessage().contains("terminated"),
                "Exception should mention component is terminated");
        }
        
        // Verify no events were received
        Assertions.assertEquals(0, eventListener.getEventCount(),
            "No events should be received after termination");
    }
    
    @Then("the component should enter {string} state")
    public void the_component_should_enter_state(String stateName) {
        Assertions.assertEquals(State.valueOf(stateName), component.getState(),
            "Component should enter " + stateName + " state");
    }
    
    @Then("state constraints should be maintained during recovery")
    public void state_constraints_should_be_maintained_during_recovery() {
        Assertions.assertEquals(State.RECOVERING, component.getState(),
            "Component should be in RECOVERING state");
        
        // Test operations that should be constrained in RECOVERING state
        try {
            component.performOperation("process_data", new HashMap<>());
            Assertions.fail("Should not be able to process data in RECOVERING state");
        } catch (Exception e) {
            // Expected behavior
            Assertions.assertTrue(e instanceof IllegalStateException,
                "Should throw IllegalStateException for blocked operations");
        }
    }
    
    @Then("operations appropriate for {string} state should be available")
    public void operations_appropriate_for_state_should_be_available(String stateName) {
        // Verify state
        Assertions.assertEquals(State.valueOf(stateName), component.getState(),
            "Component should be in " + stateName + " state");
        
        // Test recovery operations
        Assertions.assertTrue(component.canPerformOperation("query_status"),
            "Should be able to query status in " + stateName + " state");
        
        Assertions.assertTrue(component.canPerformOperation("run_diagnostics"),
            "Should be able to run diagnostics in " + stateName + " state");
    }
    
    @Then("normal operations should resume")
    public void normal_operations_should_resume() {
        // Verify component is in ACTIVE state
        Assertions.assertEquals(State.ACTIVE, component.getState(),
            "Component should be in ACTIVE state after recovery");
        
        // Test normal operations
        Assertions.assertTrue(component.canPerformOperation("process_data"),
            "Should be able to process data after recovery");
        
        Assertions.assertTrue(component.canPerformOperation("query_status"),
            "Should be able to query status after recovery");
        
        Assertions.assertTrue(component.canPerformOperation("update_config"),
            "Should be able to update config after recovery");
    }
    
    @Then("the component should return to {string} state")
    public void the_component_should_return_to_state(String stateName) {
        Assertions.assertEquals(State.valueOf(stateName), component.getState(),
            "Component should return to " + stateName + " state");
    }
}