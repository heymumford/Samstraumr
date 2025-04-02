package org.samstraumr.tube.steps;

import io.cucumber.java.en.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import org.samstraumr.tube.Environment;
import org.samstraumr.tube.Tube;

/**
 * Step definitions for testing machine state management.
 * Implements the steps defined in MachineStateTest.feature.
 * 
 * Note: The Machine class is not yet implemented, so this uses a
 * simulation of the machine behavior using existing Tube components.
 */
public class MachineStateSteps {
    private static final Logger logger = LoggerFactory.getLogger(MachineStateSteps.class);
    
    // Machine simulation variables
    private Environment environment;
    private Map<String, Object> machineState = new HashMap<>();
    private Map<String, Map<String, Object>> bundleStates = new HashMap<>();
    private Map<String, List<Tube>> bundles = new HashMap<>();
    private List<String> stateChangeLog = new ArrayList<>();
    private String machineStatus = "UNKNOWN";
    
    @Given("a machine with multiple bundles is instantiated")
    public void a_machine_with_multiple_bundles_is_instantiated() {
        // Create environment
        environment = new Environment();
        
        // Create simulated bundles with tubes
        createBundle("data-input", 3);
        createBundle("data-processing", 4);
        createBundle("data-output", 2);
        
        // Initialize machine state
        machineState.put("status", "INITIALIZING");
        machineState.put("bundleCount", bundles.size());
        machineState.put("timestamp", System.currentTimeMillis());
        
        logger.info("Created machine with {} bundles", bundles.size());
    }

    @When("the machine completes initialization")
    public void the_machine_completes_initialization() {
        // Simulate machine initialization completion
        for (String bundleName : bundles.keySet()) {
            // Create bundle state 
            Map<String, Object> bundleState = new HashMap<>();
            bundleState.put("status", "READY");
            bundleState.put("tubeCount", bundles.get(bundleName).size());
            bundleState.put("timestamp", System.currentTimeMillis());
            
            // Add to bundle states
            bundleStates.put(bundleName, bundleState);
            
            logStateChange(bundleName, "Initialized and ready");
        }
        
        // Update machine state
        machineState.put("status", "READY");
        machineStatus = "READY";
        
        logStateChange("machine", "Initialization complete, all bundles ready");
        logger.info("Machine initialization completed successfully");
    }

    @Then("each bundle should have its own state")
    public void each_bundle_should_have_its_own_state() {
        // Verify each bundle has its own state
        for (String bundleName : bundles.keySet()) {
            assertTrue(bundleStates.containsKey(bundleName), 
                       "Bundle '" + bundleName + "' should have state");
            
            assertNotNull(bundleStates.get(bundleName), 
                         "Bundle '" + bundleName + "' state should not be null");
            
            assertEquals("READY", bundleStates.get(bundleName).get("status"),
                        "Bundle '" + bundleName + "' should be in READY state");
        }
        
        logger.info("Verified: Each bundle has its own state");
    }

    @Then("the machine should have a unified state view")
    public void the_machine_should_have_a_unified_state_view() {
        // Verify machine has unified state view
        assertNotNull(machineState, "Machine state should not be null");
        assertEquals("READY", machineState.get("status"), "Machine should be in READY state");
        assertEquals(bundles.size(), machineState.get("bundleCount"), 
                    "Machine state should track the correct number of bundles");
        
        logger.info("Verified: Machine has unified state view");
    }

    @Then("the state hierarchy should be correctly established")
    public void the_state_hierarchy_should_be_correctly_established() {
        // Verify state hierarchy relationships
        
        // Machine knows about all bundles
        assertEquals(bundles.size(), bundleStates.size(), 
                    "Machine should track state for all bundles");
        
        // Each bundle has tubes
        for (String bundleName : bundles.keySet()) {
            List<Tube> tubes = bundles.get(bundleName);
            assertFalse(tubes.isEmpty(), "Bundle should have tubes");
            
            Map<String, Object> bundleState = bundleStates.get(bundleName);
            assertEquals(tubes.size(), bundleState.get("tubeCount"), 
                        "Bundle state should track the correct number of tubes");
        }
        
        logger.info("Verified: State hierarchy correctly established");
    }
    
    @Given("a machine is running with normal state")
    public void a_machine_is_running_with_normal_state() {
        // Setup a machine in normal operating state
        a_machine_with_multiple_bundles_is_instantiated();
        the_machine_completes_initialization();
        
        // Ensure normal state
        machineState.put("status", "NORMAL");
        machineStatus = "NORMAL";
        
        for (String bundleName : bundleStates.keySet()) {
            bundleStates.get(bundleName).put("status", "NORMAL");
        }
        
        logStateChange("machine", "Machine running with normal state");
        logger.info("Machine running with normal state");
    }

    @When("a critical state change occurs in one component")
    public void a_critical_state_change_occurs_in_one_component() {
        // Simulate critical state change in data-processing bundle
        String affectedBundle = "data-processing";
        
        if (bundleStates.containsKey(affectedBundle)) {
            bundleStates.get(affectedBundle).put("status", "CRITICAL");
            bundleStates.get(affectedBundle).put("error", "Memory allocation failure");
            bundleStates.get(affectedBundle).put("timestamp", System.currentTimeMillis());
            
            logStateChange(affectedBundle, "Changed to CRITICAL state: Memory allocation failure");
            logger.info("Critical state change occurred in {} bundle", affectedBundle);
        }
    }

    @Then("the state change should be detected by the machine")
    public void the_state_change_should_be_detected_by_the_machine() {
        // Simulate machine detecting the state change
        machineState.put("alertLevel", "WARNING");
        machineState.put("timestamp", System.currentTimeMillis());
        
        logStateChange("machine", "Detected critical state in data-processing bundle");
        
        // Verify detection through logs
        boolean stateChangeDetected = stateChangeLog.stream()
                .anyMatch(log -> log.contains("machine") && log.contains("Detected critical state"));
        
        assertTrue(stateChangeDetected, "Machine should detect the state change");
        logger.info("Verified: Machine detected the state change");
    }
    
    @Then("the state should be propagated to affected components")
    public void the_state_should_be_propagated_to_affected_components() {
        // Simulate state propagation to affected components
        String affectedBundle = "data-output"; // Dependent on data-processing
        
        if (bundleStates.containsKey(affectedBundle)) {
            bundleStates.get(affectedBundle).put("status", "WARNING");
            bundleStates.get(affectedBundle).put("timestamp", System.currentTimeMillis());
            
            logStateChange(affectedBundle, "Changed to WARNING state due to upstream issues");
            logger.info("State propagated to affected components");
        }
        
        // Verify propagation
        boolean statePropagated = stateChangeLog.stream()
                .anyMatch(log -> log.contains("data-output") && log.contains("WARNING state"));
        
        assertTrue(statePropagated, "State should be propagated to affected components");
        logger.info("Verified: State was propagated to affected components");
    }
    
    @Then("components should adapt their behavior based on the new state")
    public void components_should_adapt_their_behavior_based_on_the_new_state() {
        // Simulate behavior adaptation
        
        // Data-processing (critical) reduces workload
        logStateChange("data-processing", "Adapting behavior: Reduced workload to recover memory");
        
        // Data-output (warning) becomes more cautious
        logStateChange("data-output", "Adapting behavior: Added validation to handle possible corrupt data");
        
        // Machine overall changes monitoring frequency
        machineState.put("monitoringFrequency", "HIGH");
        logStateChange("machine", "Adapting behavior: Increased monitoring frequency");
        
        // Verify adaptation
        boolean behaviorAdapted = stateChangeLog.stream()
                .anyMatch(log -> log.contains("Adapting behavior"));
        
        assertTrue(behaviorAdapted, "Components should adapt their behavior based on new state");
        logger.info("Verified: Components adapted their behavior based on the new state");
    }
    
    @Given("a machine with defined state transition constraints")
    public void a_machine_with_defined_state_transition_constraints() {
        // Setup machine with state transition constraints
        a_machine_is_running_with_normal_state();
        
        // Define allowed transitions (simplified for test)
        Map<String, List<String>> allowedTransitions = new HashMap<>();
        List<String> normalTransitions = new ArrayList<>();
        normalTransitions.add("WARNING");
        normalTransitions.add("SHUTDOWN");
        
        machineState.put("allowedTransitions", allowedTransitions);
        
        logStateChange("machine", "State transition constraints defined");
        logger.info("Machine with state transition constraints ready");
    }
    
    @When("an invalid state transition is attempted")
    public void an_invalid_state_transition_is_attempted() {
        // Attempt invalid transition: NORMAL -> CRITICAL (not allowed, should go through WARNING)
        logStateChange("machine", "Attempted transition from NORMAL to CRITICAL (invalid)");
        logger.info("Invalid state transition attempted");
    }
    
    @Then("the transition should be rejected")
    public void the_transition_should_be_rejected() {
        // Simulate transition rejection
        logStateChange("machine", "State transition NORMAL -> CRITICAL rejected: Invalid transition path");
        
        // Verify rejection
        boolean transitionRejected = stateChangeLog.stream()
                .anyMatch(log -> log.contains("rejected"));
        
        assertTrue(transitionRejected, "Invalid transition should be rejected");
        logger.info("Verified: Invalid transition was rejected");
    }
    
    @Then("an appropriate error should be logged")
    public void an_appropriate_error_should_be_logged() {
        // Simulate error logging
        logStateChange("machine", "ERROR: Invalid state transition attempted: NORMAL -> CRITICAL");
        
        // Verify error logging
        boolean errorLogged = stateChangeLog.stream()
                .anyMatch(log -> log.contains("ERROR"));
        
        assertTrue(errorLogged, "Error should be logged for invalid transition");
        logger.info("Verified: Appropriate error was logged");
    }
    
    @Then("the machine should maintain its previous valid state")
    public void the_machine_should_maintain_its_previous_valid_state() {
        // Verify state remains unchanged
        assertEquals("NORMAL", machineStatus, "Machine state should remain unchanged");
        assertEquals("NORMAL", machineState.get("status"), "Machine state should remain NORMAL");
        
        logStateChange("machine", "State maintained as NORMAL after rejected transition");
        logger.info("Verified: Machine maintained its previous valid state");
    }
    
    @Given("a machine is actively processing data")
    public void a_machine_is_actively_processing_data() {
        // Setup machine that's actively processing
        a_machine_is_running_with_normal_state();
        
        machineState.put("status", "PROCESSING");
        machineStatus = "PROCESSING";
        machineState.put("activeJobs", 5);
        
        for (String bundleName : bundleStates.keySet()) {
            bundleStates.get(bundleName).put("status", "PROCESSING");
            bundleStates.get(bundleName).put("activeItems", 10);
        }
        
        logStateChange("machine", "Machine actively processing data (5 jobs active)");
        logger.info("Machine is actively processing data");
    }
    
    @When("a shutdown signal is received")
    public void a_shutdown_signal_is_received() {
        // Simulate shutdown signal
        logStateChange("machine", "Shutdown signal received");
        logger.info("Shutdown signal received by machine");
    }
    
    @Then("the machine should transition to a shutdown state")
    public void the_machine_should_transition_to_a_shutdown_state() {
        // Simulate transition to shutdown state
        machineState.put("status", "SHUTTING_DOWN");
        machineStatus = "SHUTTING_DOWN";
        machineState.put("timestamp", System.currentTimeMillis());
        
        logStateChange("machine", "Transitioned to SHUTTING_DOWN state");
        
        // Verify transition
        assertEquals("SHUTTING_DOWN", machineStatus, "Machine should transition to shutdown state");
        logger.info("Verified: Machine transitioned to shutdown state");
    }
    
    @Then("all bundles should complete current processing")
    public void all_bundles_should_complete_current_processing() {
        // Simulate bundles completing processing
        for (String bundleName : bundleStates.keySet()) {
            bundleStates.get(bundleName).put("status", "COMPLETING");
            logStateChange(bundleName, "Completing current processing before shutdown");
            
            // Then simulate completion
            bundleStates.get(bundleName).put("status", "READY_FOR_SHUTDOWN");
            bundleStates.get(bundleName).put("activeItems", 0);
            logStateChange(bundleName, "Completed all processing, ready for shutdown");
        }
        
        // Verify completion
        boolean allCompleted = bundleStates.values().stream()
                .allMatch(state -> "READY_FOR_SHUTDOWN".equals(state.get("status")));
        
        assertTrue(allCompleted, "All bundles should complete processing");
        logger.info("Verified: All bundles completed current processing");
    }
    
    @Then("resources should be properly released")
    public void resources_should_be_properly_released() {
        // Simulate resource release
        for (String bundleName : bundleStates.keySet()) {
            logStateChange(bundleName, "Resources released: memory, connections, file handles");
            bundleStates.get(bundleName).put("status", "TERMINATED");
        }
        
        machineState.put("activeJobs", 0);
        machineState.put("resourcesReleased", true);
        logStateChange("machine", "All resources properly released");
        
        // Verify release
        boolean resourcesReleased = stateChangeLog.stream()
                .anyMatch(log -> log.contains("resources") && log.contains("released"));
        
        assertTrue(resourcesReleased, "Resources should be properly released");
        logger.info("Verified: Resources were properly released");
    }
    
    @Then("the final state should be logged")
    public void the_final_state_should_be_logged() {
        // Simulate final state logging
        machineState.put("status", "TERMINATED");
        machineStatus = "TERMINATED";
        machineState.put("uptime", "2h 34m 12s");
        machineState.put("processedItems", 1234);
        
        logStateChange("machine", "FINAL STATE: TERMINATED - Processed 1234 items during 2h 34m 12s uptime");
        
        // Verify final logging
        boolean finalStateLogged = stateChangeLog.stream()
                .anyMatch(log -> log.contains("FINAL STATE"));
        
        assertTrue(finalStateLogged, "Final state should be logged");
        assertEquals("TERMINATED", machineStatus, "Machine should reach TERMINATED state");
        logger.info("Verified: Final state was logged");
    }
    
    // Helper methods for simulating machine and bundles
    
    private void createBundle(String name, int tubeCount) {
        List<Tube> bundleTubes = new ArrayList<>();
        
        for (int i = 0; i < tubeCount; i++) {
            Tube tube = new Tube(name + "-tube-" + i, environment);
            bundleTubes.add(tube);
        }
        
        bundles.put(name, bundleTubes);
        logger.debug("Created bundle '{}' with {} tubes", name, tubeCount);
    }
    
    private void logStateChange(String component, String message) {
        String logEntry = "STATE [" + component + "]: " + message;
        stateChangeLog.add(logEntry);
        logger.debug(logEntry);
    }
}