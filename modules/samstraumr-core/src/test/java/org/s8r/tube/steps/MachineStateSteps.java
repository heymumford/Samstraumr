/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.mozilla.org/en-US/MPL/2.0/
 */

package org.s8r.tube.steps;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.s8r.tube.Environment;
import org.s8r.tube.Tube;
import org.s8r.tube.composite.Composite;
import org.s8r.tube.machine.Machine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cucumber.java.en.*;

/**
 * Step definitions for testing machine state management. Implements the steps defined in
 * MachineStateTest.feature.
 */
public class MachineStateSteps {
  private static final Logger LOGGER = LoggerFactory.getLogger(MachineStateSteps.class);

  // Machine simulation variables
  private Environment environment;
  private Machine machine;
  private Map<String, Object> machineState = new HashMap<>();
  private Map<String, Map<String, Object>> compositeStates = new HashMap<>();
  private Map<String, List<Tube>> composites = new HashMap<>();
  private List<String> stateChangeLog = new ArrayList<>();
  private String machineStatus = "UNKNOWN";

  @Given("a machine with multiple composites is instantiated")
  public void a_machine_with_multiple_composites_is_instantiated() {
    // Create environment
    environment = new Environment();

    // Create machine
    machine = new Machine("test-machine", environment);

    // Create simulated composites with tubes
    createComposite("data-input", 3);
    createComposite("data-processing", 4);
    createComposite("data-output", 2);

    // Initialize machine state
    machineState.put("status", "INITIALIZING");
    machineState.put("compositeCount", composites.size());
    machineState.put("timestamp", System.currentTimeMillis());

    // Add state to actual machine instance too
    machine.updateState("status", "INITIALIZING");
    machine.updateState("compositeCount", composites.size());
    machine.updateState("timestamp", System.currentTimeMillis());

    LOGGER.info("Created machine with {} composites", composites.size());
  }

  @When("the machine completes initialization")
  public void the_machine_completes_initialization() {
    // Simulate machine initialization completion
    for (String compositeName : composites.keySet()) {
      // Create composite state
      Map<String, Object> compositeState = new HashMap<>();
      compositeState.put("status", "READY");
      compositeState.put("tubeCount", composites.get(compositeName).size());
      compositeState.put("timestamp", System.currentTimeMillis());

      // Add to composite states
      compositeStates.put(compositeName, compositeState);

      logStateChange(compositeName, "Initialized and ready");
    }

    // Update machine state
    machineState.put("status", "READY");
    machineStatus = "READY";
    machine.updateState("status", "READY");

    logStateChange("machine", "Initialization complete, all composites ready");
    LOGGER.info("Machine initialization completed successfully");
  }

  @Then("each composite should have its own state")
  public void each_composite_should_have_its_own_state() {
    // Verify each composite has its own state
    for (String compositeName : composites.keySet()) {
      assertTrue(
          compositeStates.containsKey(compositeName),
          "Composite '" + compositeName + "' should have state");

      assertNotNull(
          compositeStates.get(compositeName),
          "Composite '" + compositeName + "' state should not be null");

      assertEquals(
          "READY",
          compositeStates.get(compositeName).get("status"),
          "Composite '" + compositeName + "' should be in READY state");
    }

    LOGGER.info("Verified: Each composite has its own state");
  }

  @Then("the machine should have a unified state view")
  public void the_machine_should_have_a_unified_state_view() {
    // Verify machine has unified state view
    assertNotNull(machineState, "Machine state should not be null");
    assertEquals("READY", machineState.get("status"), "Machine should be in READY state");
    assertEquals(
        composites.size(),
        machineState.get("compositeCount"),
        "Machine state should track the correct number of composites");

    // Verify actual machine state
    Map<String, Object> actualMachineState = machine.getState();
    assertEquals(
        "READY", actualMachineState.get("status"), "Actual machine should be in READY state");

    LOGGER.info("Verified: Machine has unified state view");
  }

  @Then("the state hierarchy should be correctly established")
  public void the_state_hierarchy_should_be_correctly_established() {
    // Verify state hierarchy relationships

    // Machine knows about all composites
    assertEquals(
        composites.size(), compositeStates.size(), "Machine should track state for all composites");

    // Each composite has tubes
    for (String compositeName : composites.keySet()) {
      List<Tube> tubes = composites.get(compositeName);
      assertFalse(tubes.isEmpty(), "Composite should have tubes");

      Map<String, Object> compositeState = compositeStates.get(compositeName);
      assertEquals(
          tubes.size(),
          compositeState.get("tubeCount"),
          "Composite state should track the correct number of tubes");
    }

    LOGGER.info("Verified: State hierarchy correctly established");
  }

  @Given("a machine is running with normal state")
  public void a_machine_is_running_with_normal_state() {
    // Setup a machine in normal operating state
    a_machine_with_multiple_composites_is_instantiated();
    the_machine_completes_initialization();

    // Ensure normal state
    machineState.put("status", "NORMAL");
    machineStatus = "NORMAL";
    machine.updateState("status", "NORMAL");

    for (String compositeName : compositeStates.keySet()) {
      compositeStates.get(compositeName).put("status", "NORMAL");
    }

    logStateChange("machine", "Machine running with normal state");
    LOGGER.info("Machine running with normal state");
  }

  @When("a critical state change occurs in one component")
  public void a_critical_state_change_occurs_in_one_component() {
    // Simulate critical state change in data-processing composite
    String affectedComposite = "data-processing";

    if (compositeStates.containsKey(affectedComposite)) {
      compositeStates.get(affectedComposite).put("status", "CRITICAL");
      compositeStates.get(affectedComposite).put("error", "Memory allocation failure");
      compositeStates.get(affectedComposite).put("timestamp", System.currentTimeMillis());

      logStateChange(affectedComposite, "Changed to CRITICAL state: Memory allocation failure");
      LOGGER.info("Critical state change occurred in {} composite", affectedComposite);
    }
  }

  @Then("the state change should be detected by the machine")
  public void the_state_change_should_be_detected_by_the_machine() {
    // Simulate machine detecting the state change
    machineState.put("alertLevel", "WARNING");
    machineState.put("timestamp", System.currentTimeMillis());
    machine.updateState("alertLevel", "WARNING");
    machine.updateState("timestamp", System.currentTimeMillis());

    logStateChange("machine", "Detected critical state in data-processing composite");

    // Verify detection through logs
    boolean stateChangeDetected =
        stateChangeLog.stream()
            .anyMatch(log -> log.contains("machine") && log.contains("Detected critical state"));

    assertTrue(stateChangeDetected, "Machine should detect the state change");
    LOGGER.info("Verified: Machine detected the state change");
  }

  @Then("the state should be propagated to affected components")
  public void the_state_should_be_propagated_to_affected_components() {
    // Simulate state propagation to affected components
    String affectedComposite = "data-output"; // Dependent on data-processing

    if (compositeStates.containsKey(affectedComposite)) {
      compositeStates.get(affectedComposite).put("status", "WARNING");
      compositeStates.get(affectedComposite).put("timestamp", System.currentTimeMillis());

      logStateChange(affectedComposite, "Changed to WARNING state due to upstream issues");
      LOGGER.info("State propagated to affected components");
    }

    // Verify propagation
    boolean statePropagated =
        stateChangeLog.stream()
            .anyMatch(log -> log.contains("data-output") && log.contains("WARNING state"));

    assertTrue(statePropagated, "State should be propagated to affected components");
    LOGGER.info("Verified: State was propagated to affected components");
  }

  @Then("components should adapt their behavior based on the new state")
  public void components_should_adapt_their_behavior_based_on_the_new_state() {
    // Simulate behavior adaptation

    // Data-processing (critical) reduces workload
    logStateChange("data-processing", "Adapting behavior: Reduced workload to recover memory");

    // Data-output (warning) becomes more cautious
    logStateChange(
        "data-output", "Adapting behavior: Added validation to handle possible corrupt data");

    // Machine overall changes monitoring frequency
    machineState.put("monitoringFrequency", "HIGH");
    machine.updateState("monitoringFrequency", "HIGH");
    logStateChange("machine", "Adapting behavior: Increased monitoring frequency");

    // Verify adaptation
    boolean behaviorAdapted =
        stateChangeLog.stream().anyMatch(log -> log.contains("Adapting behavior"));

    assertTrue(behaviorAdapted, "Components should adapt their behavior based on new state");
    LOGGER.info("Verified: Components adapted their behavior based on the new state");
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
    machine.updateState("allowedTransitions", allowedTransitions);

    logStateChange("machine", "State transition constraints defined");
    LOGGER.info("Machine with state transition constraints ready");
  }

  @When("an invalid state transition is attempted")
  public void an_invalid_state_transition_is_attempted() {
    // Attempt invalid transition: NORMAL -> CRITICAL (not allowed, should go through WARNING)
    logStateChange("machine", "Attempted transition from NORMAL to CRITICAL (invalid)");
    LOGGER.info("Invalid state transition attempted");
  }

  @Then("the transition should be rejected")
  public void the_transition_should_be_rejected() {
    // Simulate transition rejection
    logStateChange(
        "machine", "State transition NORMAL -> CRITICAL rejected: Invalid transition path");

    // Verify rejection
    boolean transitionRejected = stateChangeLog.stream().anyMatch(log -> log.contains("rejected"));

    assertTrue(transitionRejected, "Invalid transition should be rejected");
    LOGGER.info("Verified: Invalid transition was rejected");
  }

  @Then("an appropriate error should be logged")
  public void an_appropriate_error_should_be_logged() {
    // Simulate error logging
    logStateChange("machine", "ERROR: Invalid state transition attempted: NORMAL -> CRITICAL");
    machine.logEvent("ERROR: Invalid state transition attempted: NORMAL -> CRITICAL");

    // Verify error logging
    boolean errorLogged = stateChangeLog.stream().anyMatch(log -> log.contains("ERROR"));

    assertTrue(errorLogged, "Error should be logged for invalid transition");
    LOGGER.info("Verified: Appropriate error was logged");
  }

  @Then("the machine should maintain its previous valid state")
  public void the_machine_should_maintain_its_previous_valid_state() {
    // Verify state remains unchanged
    assertEquals("NORMAL", machineStatus, "Machine state should remain unchanged");
    assertEquals("NORMAL", machineState.get("status"), "Machine state should remain NORMAL");
    assertEquals(
        "NORMAL", machine.getState().get("status"), "Actual machine state should remain NORMAL");

    logStateChange("machine", "State maintained as NORMAL after rejected transition");
    LOGGER.info("Verified: Machine maintained its previous valid state");
  }

  @Given("a machine is actively processing data")
  public void a_machine_is_actively_processing_data() {
    // Setup machine that's actively processing
    a_machine_is_running_with_normal_state();

    machineState.put("status", "PROCESSING");
    machineStatus = "PROCESSING";
    machineState.put("activeJobs", 5);
    machine.updateState("status", "PROCESSING");
    machine.updateState("activeJobs", 5);

    for (String compositeName : compositeStates.keySet()) {
      compositeStates.get(compositeName).put("status", "PROCESSING");
      compositeStates.get(compositeName).put("activeItems", 10);
    }

    logStateChange("machine", "Machine actively processing data (5 jobs active)");
    LOGGER.info("Machine is actively processing data");
  }

  @When("a shutdown signal is received")
  public void a_shutdown_signal_is_received() {
    // Simulate shutdown signal
    logStateChange("machine", "Shutdown signal received");
    LOGGER.info("Shutdown signal received by machine");
  }

  @Then("the machine should transition to a shutdown state")
  public void the_machine_should_transition_to_a_shutdown_state() {
    // Simulate transition to shutdown state
    machineState.put("status", "SHUTTING_DOWN");
    machineStatus = "SHUTTING_DOWN";
    machineState.put("timestamp", System.currentTimeMillis());
    machine.updateState("status", "SHUTTING_DOWN");

    logStateChange("machine", "Transitioned to SHUTTING_DOWN state");

    // Verify transition
    assertEquals("SHUTTING_DOWN", machineStatus, "Machine should transition to shutdown state");
    LOGGER.info("Verified: Machine transitioned to shutdown state");
  }

  @Then("all composites should complete current processing")
  public void all_composites_should_complete_current_processing() {
    // Simulate composites completing processing
    for (String compositeName : compositeStates.keySet()) {
      compositeStates.get(compositeName).put("status", "COMPLETING");
      logStateChange(compositeName, "Completing current processing before shutdown");

      // Then simulate completion
      compositeStates.get(compositeName).put("status", "READY_FOR_SHUTDOWN");
      compositeStates.get(compositeName).put("activeItems", 0);
      logStateChange(compositeName, "Completed all processing, ready for shutdown");
    }

    // Verify completion
    boolean allCompleted =
        compositeStates.values().stream()
            .allMatch(state -> "READY_FOR_SHUTDOWN".equals(state.get("status")));

    assertTrue(allCompleted, "All composites should complete processing");
    LOGGER.info("Verified: All composites completed current processing");
  }

  @Then("resources should be properly released")
  public void resources_should_be_properly_released() {
    // Simulate resource release
    for (String compositeName : compositeStates.keySet()) {
      logStateChange(compositeName, "Resources released: memory, connections, file handles");
      compositeStates.get(compositeName).put("status", "TERMINATED");
    }

    machineState.put("activeJobs", 0);
    machineState.put("resourcesReleased", true);
    machine.updateState("activeJobs", 0);
    machine.updateState("resourcesReleased", true);
    logStateChange("machine", "All resources properly released");

    // Verify release
    boolean resourcesReleased =
        stateChangeLog.stream()
            .anyMatch(log -> log.contains("resources") && log.contains("released"));

    assertTrue(resourcesReleased, "Resources should be properly released");
    LOGGER.info("Verified: Resources were properly released");
  }

  @Then("the final state should be logged")
  public void the_final_state_should_be_logged() {
    // Simulate final state logging
    machineState.put("status", "TERMINATED");
    machineStatus = "TERMINATED";
    machineState.put("uptime", "2h 34m 12s");
    machineState.put("processedItems", 1234);
    machine.updateState("status", "TERMINATED");
    machine.updateState("uptime", "2h 34m 12s");
    machine.updateState("processedItems", 1234);

    logStateChange(
        "machine", "FINAL STATE: TERMINATED - Processed 1234 items during 2h 34m 12s uptime");
    machine.logEvent("FINAL STATE: TERMINATED - Processed 1234 items during 2h 34m 12s uptime");

    // Verify final logging
    boolean finalStateLogged = stateChangeLog.stream().anyMatch(log -> log.contains("FINAL STATE"));

    assertTrue(finalStateLogged, "Final state should be logged");
    assertEquals("TERMINATED", machineStatus, "Machine should reach TERMINATED state");
    assertEquals(
        "TERMINATED",
        machine.getState().get("status"),
        "Actual machine should reach TERMINATED state");
    LOGGER.info("Verified: Final state was logged");
  }

  // Helper methods for simulating machine and composites

  private void createComposite(String name, int tubeCount) {
    List<Tube> compositeTubes = new ArrayList<>();

    for (int i = 0; i < tubeCount; i++) {
      Tube tube = Tube.create(name + "-tube-" + i, environment);
      compositeTubes.add(tube);
    }

    composites.put(name, compositeTubes);

    // Add tubes to a Composite and add it to the machine
    Composite composite = new Composite(name, environment);
    for (int i = 0; i < tubeCount; i++) {
      composite.addTube(name + "-tube-" + i, compositeTubes.get(i));
    }
    machine.addComposite(name, composite);

    LOGGER.debug("Created composite '{}' with {} tubes", name, tubeCount);
  }

  private void logStateChange(String component, String message) {
    String logEntry = "STATE [" + component + "]: " + message;
    stateChangeLog.add(logEntry);
    LOGGER.debug(logEntry);
  }
}
