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

package org.s8r.component.test.steps;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.s8r.component.Composite;
import org.s8r.component.Machine;
import org.s8r.component.MachineFactory;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;

/**
 * Step definitions for machine orchestration tests.
 *
 * <p>These step definitions implement the BDD scenarios related to machine orchestration, including
 * composite management, machine lifecycle, and state handling.
 */
public class MachineSteps extends BaseComponentSteps {

  @Given("a standard environment")
  public void a_standard_environment() {
    environment = prepareEnvironment();
    storeInContext("environment", environment);
  }

  @When("a machine is created with ID {string}")
  public void a_machine_is_created_with_id(String machineId) {
    assertNotNull(environment, "Environment should exist");

    // Create a machine with the specified ID
    testMachine = new Machine(machineId, environment);
    storeInContext("testMachine", testMachine);
  }

  @When("composites are added to the machine:")
  public void composites_are_added_to_the_machine(DataTable dataTable) {
    assertNotNull(testMachine, "Machine should exist");

    List<Map<String, String>> compositeList = dataTable.asMaps(String.class, String.class);
    for (Map<String, String> compositeData : compositeList) {
      String name = compositeData.get("Name");
      String purpose = compositeData.get("Purpose");

      testMachine.createComposite(name, purpose);
    }
  }

  @When("composites are connected in the machine:")
  public void composites_are_connected_in_the_machine(DataTable dataTable) {
    assertNotNull(testMachine, "Machine should exist");

    List<Map<String, String>> connectionList = dataTable.asMaps(String.class, String.class);
    for (Map<String, String> connection : connectionList) {
      String source = connection.get("Source");
      String target = connection.get("Target");

      testMachine.connect(source, target);
    }
  }

  @When("a data processing machine is created")
  public void a_data_processing_machine_is_created() {
    assertNotNull(environment, "Environment should exist");

    // Create a data processing machine
    testMachine = MachineFactory.createDataProcessingMachine(environment);
    storeInContext("testMachine", testMachine);
  }

  @When("the machine is populated with test composites")
  public void the_machine_is_populated_with_test_composites() {
    assertNotNull(testMachine, "Machine should exist");

    // The data processing machine factory already creates the necessary composites,
    // so this step is mostly a placeholder for test clarity
    Map<String, Composite> composites = testMachine.getComposites();
    assertFalse(composites.isEmpty(), "Machine should have composites");
  }

  @When("the machine shutdown process is initiated")
  public void the_machine_shutdown_process_is_initiated() {
    assertNotNull(testMachine, "Machine should exist");

    // Initiate machine shutdown
    testMachine.shutdown();
  }

  @When("the machine is deactivated")
  public void the_machine_is_deactivated() {
    assertNotNull(testMachine, "Machine should exist");

    // Deactivate the machine
    testMachine.deactivate();
  }

  @When("the machine is activated")
  public void the_machine_is_activated() {
    assertNotNull(testMachine, "Machine should exist");

    // Activate the machine
    testMachine.activate();
  }

  @When("the machine state is updated with key {string} and value {string}")
  public void the_machine_state_is_updated_with_key_and_value(String key, String value) {
    assertNotNull(testMachine, "Machine should exist");

    // Update machine state
    testMachine.updateState(key, value);
  }

  @Then("the machine should be initialized")
  public void the_machine_should_be_initialized() {
    assertNotNull(testMachine, "Machine should exist");

    // Verify machine is initialized
    assertNotNull(testMachine.getMachineId(), "Machine should have an ID");
    assertNotNull(testMachine.getEnvironment(), "Machine should have an environment");
    assertNotNull(testMachine.getMachineIdentity(), "Machine should have an identity");
  }

  @Then("the machine should have an empty composite set")
  public void the_machine_should_have_an_empty_composite_set() {
    assertNotNull(testMachine, "Machine should exist");

    // Verify machine has no composites initially
    Map<String, Composite> composites = testMachine.getComposites();
    assertTrue(composites.isEmpty(), "Machine should have empty composite set initially");
  }

  @Then("the machine should be in {string} state")
  public void the_machine_should_be_in_state(String stateName) {
    assertNotNull(testMachine, "Machine should exist");

    // Verify machine state
    Map<String, Object> state = testMachine.getState();
    assertNotNull(state, "Machine should have state");

    Object status = state.get("status");
    assertNotNull(status, "Machine should have status in state");
    assertEquals(stateName, status.toString(), "Machine should be in correct state");
  }

  @Then("the machine should have a valid identity")
  public void the_machine_should_have_a_valid_identity() {
    assertNotNull(testMachine, "Machine should exist");

    // Verify machine identity
    assertNotNull(testMachine.getMachineIdentity(), "Machine should have an identity");
    assertNotNull(
        testMachine.getMachineIdentity().getUniqueId(), "Machine identity should have a unique ID");
  }

  @Then("the machine should contain all the expected composites")
  public void the_machine_should_contain_all_the_expected_composites() {
    assertNotNull(testMachine, "Machine should exist");

    // Verify all composites are present
    Map<String, Composite> composites = testMachine.getComposites();
    assertFalse(composites.isEmpty(), "Machine should have composites");
  }

  @Then("each composite should be accessible by name")
  public void each_composite_should_be_accessible_by_name() {
    assertNotNull(testMachine, "Machine should exist");

    // Verify each composite can be accessed by name
    Map<String, Composite> composites = testMachine.getComposites();

    for (String compositeName : composites.keySet()) {
      Composite composite = testMachine.getComposite(compositeName);
      assertNotNull(composite, "Each composite should be accessible by name: " + compositeName);
    }
  }

  @Then("the machine should log composite addition events")
  public void the_machine_should_log_composite_addition_events() {
    assertNotNull(testMachine, "Machine should exist");

    // Verify composite addition is logged
    List<Machine.MachineEvent> events = testMachine.getEventLog();
    boolean foundEvent =
        events.stream().anyMatch(event -> event.getDescription().contains("added to machine"));

    assertTrue(foundEvent, "Machine should log composite addition events");
  }

  @Then("the machine should have the expected connections")
  public void the_machine_should_have_the_expected_connections() {
    assertNotNull(testMachine, "Machine should exist");

    // Verify connections exist
    Map<String, List<String>> connections = testMachine.getConnections();
    assertFalse(connections.isEmpty(), "Machine should have connections");
  }

  @Then("the machine should maintain connection information")
  public void the_machine_should_maintain_connection_information() {
    assertNotNull(testMachine, "Machine should exist");

    // Verify connection information is maintained
    Map<String, List<String>> connections = testMachine.getConnections();

    for (Map.Entry<String, List<String>> entry : connections.entrySet()) {
      String source = entry.getKey();
      List<String> targets = entry.getValue();

      assertNotNull(testMachine.getComposite(source), "Source composite should exist");
      assertFalse(targets.isEmpty(), "Connection should have targets");

      for (String target : targets) {
        assertNotNull(testMachine.getComposite(target), "Target composite should exist");
      }
    }
  }

  @Then("the machine should log connection events")
  public void the_machine_should_log_connection_events() {
    assertNotNull(testMachine, "Machine should exist");

    // Verify connection events are logged
    List<Machine.MachineEvent> events = testMachine.getEventLog();
    boolean foundEvent =
        events.stream().anyMatch(event -> event.getDescription().contains("Connected"));

    assertTrue(foundEvent, "Machine should log connection events");
  }

  @Then("the machine should be active initially")
  public void the_machine_should_be_active_initially() {
    assertNotNull(testMachine, "Machine should exist");

    // Verify machine is initially active
    assertTrue(testMachine.isActive(), "Machine should be active initially");
  }

  @Then("the machine should be inactive")
  public void the_machine_should_be_inactive() {
    assertNotNull(testMachine, "Machine should exist");

    // Verify machine is inactive
    assertFalse(testMachine.isActive(), "Machine should be inactive");
  }

  @Then("the machine should be active again")
  public void the_machine_should_be_active_again() {
    assertNotNull(testMachine, "Machine should exist");

    // Verify machine is active again
    assertTrue(testMachine.isActive(), "Machine should be active again");
  }

  @Then("the machine should have {string} with value {string}")
  public void the_machine_should_have_with_value(String key, String expectedValue) {
    assertNotNull(testMachine, "Machine should exist");

    // Verify state has expected key-value pair
    Map<String, Object> state = testMachine.getState();
    assertNotNull(state, "Machine should have state");

    Object value = state.get(key);
    assertNotNull(value, "Machine state should have key: " + key);
    assertEquals(expectedValue, value.toString(), "State value should match expected");
  }

  @Then("the machine should log state change events")
  public void the_machine_should_log_state_change_events() {
    assertNotNull(testMachine, "Machine should exist");

    // Verify state change events are logged
    List<Machine.MachineEvent> events = testMachine.getEventLog();
    boolean foundEvent =
        events.stream().anyMatch(event -> event.getDescription().contains("State updated"));

    assertTrue(foundEvent, "Machine should log state change events");
  }

  @Then("the machine should proceed through shutdown states")
  public void the_machine_should_proceed_through_shutdown_states() {
    assertNotNull(testMachine, "Machine should exist");

    // Verify machine proceeds through shutdown states
    Map<String, Object> state = testMachine.getState();
    assertNotNull(state, "Machine should have state");

    Object status = state.get("status");
    assertNotNull(status, "Machine should have status in state");
    assertTrue(
        status.equals("TERMINATED") || status.equals("SHUTTING_DOWN"),
        "Machine should be in a shutdown state");
  }

  @Then("all composites should be deactivated")
  public void all_composites_should_be_deactivated() {
    assertNotNull(testMachine, "Machine should exist");

    // Verify all composites are deactivated
    Map<String, Composite> composites = testMachine.getComposites();

    for (Composite composite : composites.values()) {
      assertFalse(composite.isActive(), "All composites should be deactivated");
    }
  }

  @Then("the machine should log shutdown events")
  public void the_machine_should_log_shutdown_events() {
    assertNotNull(testMachine, "Machine should exist");

    // Verify shutdown events are logged
    List<Machine.MachineEvent> events = testMachine.getEventLog();
    boolean foundEvent =
        events.stream().anyMatch(event -> event.getDescription().contains("shutdown"));

    assertTrue(foundEvent, "Machine should log shutdown events");
  }

  @Then("the machine should record the termination time")
  public void the_machine_should_record_the_termination_time() {
    assertNotNull(testMachine, "Machine should exist");

    // Verify termination time is recorded
    Map<String, Object> state = testMachine.getState();
    Object terminatedAt = state.get("terminatedAt");

    assertNotNull(terminatedAt, "Machine should record termination time");
    assertTrue(
        terminatedAt instanceof Instant || terminatedAt instanceof String,
        "Termination time should be a timestamp");
  }
}
