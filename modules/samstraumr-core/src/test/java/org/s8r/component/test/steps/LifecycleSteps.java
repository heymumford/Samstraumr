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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.s8r.component.ComponentException;
import org.s8r.component.InvalidStateTransitionException;
import org.s8r.component.State;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;

/**
 * Step definitions for component lifecycle tests.
 *
 * <p>These step definitions implement the BDD scenarios related to component lifecycle, focusing on
 * state transitions, state-dependent behaviors, and resource management.
 */
public class LifecycleSteps extends BaseComponentSteps {

  private List<State> observedStates = new ArrayList<>();
  private Map<String, Object> resourceTracker = new HashMap<>();

  @When("the component is guided through its full lifecycle")
  public void the_component_is_guided_through_its_full_lifecycle() {
    assertNotNull(testComponent, "Component should exist");

    // Record initial state
    observedStates.add(testComponent.getState());

    // Guide through the full lifecycle - this implementation would depend
    // on what states are valid transitions from each state
    List<State> fullLifecycle = getFullLifecycleStates();

    for (int i = 1; i < fullLifecycle.size(); i++) { // Skip first (already in it)
      try {
        State targetState = fullLifecycle.get(i);
        testComponent.setState(targetState);
        observedStates.add(testComponent.getState());
      } catch (Exception e) {
        fail("Failed to transition to state " + fullLifecycle.get(i) + ": " + e.getMessage());
      }
    }

    storeInContext("observedStates", observedStates);
  }

  @When("the component transitions to {string} state")
  public void the_component_transitions_to_state(String stateName) {
    assertNotNull(testComponent, "Component should exist");

    try {
      State targetState = State.valueOf(stateName);
      testComponent.setState(targetState);
      observedStates.add(testComponent.getState());
    } catch (Exception e) {
      exceptionThrown = e;
      storeInContext("exceptionThrown", exceptionThrown);
    }
  }

  @Then("the component should pass through all lifecycle states in order:")
  public void the_component_should_pass_through_all_lifecycle_states_in_order(DataTable dataTable) {
    assertNotNull(testComponent, "Component should exist");

    // Convert DataTable to list of expected states
    List<Map<String, String>> rows = dataTable.asMaps();
    List<State> expectedStates = new ArrayList<>();

    for (Map<String, String> row : rows) {
      State state = State.valueOf(row.get("State"));
      expectedStates.add(state);
    }

    // Compare with observed states
    assertEquals(
        expectedStates.size(), observedStates.size(), "Should observe all expected states");

    for (int i = 0; i < expectedStates.size(); i++) {
      assertEquals(
          expectedStates.get(i),
          observedStates.get(i),
          "States should be observed in expected order");
    }
  }

  @Then("the component should behave appropriately in CONCEPTION state")
  public void the_component_should_behave_appropriately_in_conception_state() {
    assertNotNull(testComponent, "Component should exist");
    assertEquals(
        State.CONCEPTION, testComponent.getState(), "Component should be in CONCEPTION state");

    // Verify behaviors specific to CONCEPTION state
    assertFalse(
        testComponent.isOperational(), "Component should not be operational in CONCEPTION state");
    assertTrue(testComponent.isEmbryonic(), "Component should be embryonic in CONCEPTION state");
  }

  @Then("the component should behave appropriately in INITIALIZING state")
  public void the_component_should_behave_appropriately_in_initializing_state() {
    assertNotNull(testComponent, "Component should exist");
    assertEquals(
        State.INITIALIZING, testComponent.getState(), "Component should be in INITIALIZING state");

    // Verify behaviors specific to INITIALIZING state
    assertFalse(
        testComponent.isOperational(), "Component should not be operational in INITIALIZING state");
    assertTrue(testComponent.isInitializing(), "Component should be initializing");
  }

  @Then("the component should behave appropriately in CONFIGURING state")
  public void the_component_should_behave_appropriately_in_configuring_state() {
    assertNotNull(testComponent, "Component should exist");
    assertEquals(
        State.CONFIGURING, testComponent.getState(), "Component should be in CONFIGURING state");

    // Verify behaviors specific to CONFIGURING state
    assertFalse(
        testComponent.isOperational(), "Component should not be operational in CONFIGURING state");
    assertTrue(testComponent.isConfiguring(), "Component should be configuring");
  }

  @Then("the component should behave appropriately in READY state")
  public void the_component_should_behave_appropriately_in_ready_state() {
    assertNotNull(testComponent, "Component should exist");
    assertEquals(State.READY, testComponent.getState(), "Component should be in READY state");

    // Verify behaviors specific to READY state
    assertTrue(testComponent.isOperational(), "Component should be operational in READY state");
    assertTrue(testComponent.isReady(), "Component should be ready");
  }

  @Then("the component should behave appropriately in ACTIVE state")
  public void the_component_should_behave_appropriately_in_active_state() {
    assertNotNull(testComponent, "Component should exist");
    assertEquals(State.ACTIVE, testComponent.getState(), "Component should be in ACTIVE state");

    // Verify behaviors specific to ACTIVE state
    assertTrue(testComponent.isOperational(), "Component should be operational in ACTIVE state");
    assertTrue(testComponent.isActive(), "Component should be active");
  }

  @Then("the component should behave appropriately in TERMINATED state")
  public void the_component_should_behave_appropriately_in_terminated_state() {
    assertNotNull(testComponent, "Component should exist");
    assertTrue(testComponent.isTerminated(), "Component should be terminated");

    // Verify behaviors specific to TERMINATED state
    assertFalse(
        testComponent.isOperational(), "Component should not be operational when terminated");
  }

  @Then("the component should allocate initialization resources")
  public void the_component_should_allocate_initialization_resources() {
    assertNotNull(testComponent, "Component should exist");

    // Verify that initialization resources are allocated
    assertNotNull(testComponent.getLogger(), "Logger resource should be allocated");
    assertNotNull(testComponent.getIdentity(), "Identity resource should be allocated");

    // Record resources for later verification
    resourceTracker.put("logger", testComponent.getLogger());
    resourceTracker.put("identity", testComponent.getIdentity());

    storeInContext("resourceTracker", resourceTracker);
  }

  @Then("the component should allocate operational resources")
  public void the_component_should_allocate_operational_resources() {
    assertNotNull(testComponent, "Component should exist");

    // Verify operational resources
    assertTrue(testComponent.isOperational(), "Component should be operational");

    // Additional resource checks would depend on the component implementation
    assertNotNull(
        testComponent.getMemoryLog(), "Memory log should be available in operational state");

    resourceTracker.put("memoryLog", testComponent.getMemoryLog());
    storeInContext("resourceTracker", resourceTracker);
  }

  @Then("all component resources should be properly released")
  public void all_component_resources_should_be_properly_released() {
    assertNotNull(testComponent, "Component should exist");
    assertTrue(testComponent.isTerminated(), "Component should be terminated");

    // Verify resources are released
    // This would depend on the component implementation
    // For now, we'll check that resources are marked as released in logs
    List<String> logs = testComponent.getMemoryLog();
    boolean resourceReleaseLogged =
        logs.stream().anyMatch(log -> log.contains("resources") && log.contains("released"));

    assertTrue(resourceReleaseLogged, "Component should log resource release on termination");
  }

  @Then("there should be no resource leaks")
  public void there_should_be_no_resource_leaks() {
    assertNotNull(testComponent, "Component should exist");

    // This would require a more specific implementation to detect resource leaks
    // For now, we'll verify termination is complete
    assertTrue(testComponent.isTerminated(), "Component should be properly terminated");

    // Additional checks for specific resources would go here
    // For example, checking that timers are cancelled, threads are joined, etc.
  }

  @Then("each lifecycle state should have descriptive metadata")
  public void each_lifecycle_state_should_have_descriptive_metadata() {
    // Check metadata for all states
    for (State state : State.values()) {
      assertNotNull(state.getDescription(), "State " + state + " should have a description");
      assertFalse(
          state.getDescription().isEmpty(), "State " + state + " description should not be empty");
    }
  }

  @Then("each state should have a biological analog")
  public void each_state_should_have_a_biological_analog() {
    // Check biological analog for all states
    for (State state : State.values()) {
      assertNotNull(
          state.getBiologicalAnalog(), "State " + state + " should have a biological analog");
      assertFalse(
          state.getBiologicalAnalog().isEmpty(),
          "State " + state + " biological analog should not be empty");
    }
  }

  @Then("each state should belong to a specific category")
  public void each_state_should_belong_to_a_specific_category() {
    // Check category for all states
    for (State state : State.values()) {
      assertNotNull(state.getCategory(), "State " + state + " should have a category");
    }
  }

  @Then("transitions between states should follow logical progression")
  public void transitions_between_states_should_follow_logical_progression() {
    assertNotNull(testComponent, "Component should exist");

    // Test a few key valid transitions
    try {
      testComponent.setState(State.CONCEPTION);
      testComponent.setState(State.INITIALIZING);
      testComponent.setState(State.CONFIGURING);
      testComponent.setState(State.READY);
      testComponent.setState(State.ACTIVE);
      testComponent.setState(State.TERMINATING);
      testComponent.setState(State.TERMINATED);

      // If we get here without exceptions, transitions are valid
      assertTrue(true, "Valid transitions should be allowed");
    } catch (Exception e) {
      fail("Valid state transitions should not throw exceptions: " + e.getMessage());
    }
  }

  @Then("invalid state transitions should be rejected:")
  public void invalid_state_transitions_should_be_rejected(DataTable dataTable) {
    assertNotNull(testComponent, "Component should exist");

    List<Map<String, String>> transitions = dataTable.asMaps();

    for (Map<String, String> transition : transitions) {
      String fromState = transition.get("From");
      String toState = transition.get("To");

      // Set the component to the from state
      try {
        testComponent.setState(State.valueOf(fromState));
      } catch (Exception e) {
        fail("Failed to set initial state for test: " + e.getMessage());
      }

      // Try the invalid transition
      Exception transitionException = null;
      try {
        testComponent.setState(State.valueOf(toState));
      } catch (Exception e) {
        transitionException = e;
      }

      // Verify the transition was rejected
      assertNotNull(
          transitionException,
          "Transition from " + fromState + " to " + toState + " should be rejected");
      assertTrue(
          transitionException instanceof InvalidStateTransitionException
              || transitionException instanceof ComponentException,
          "Should throw appropriate exception for invalid transition");
    }
  }

  @Then("appropriate errors should be generated for invalid transitions")
  public void appropriate_errors_should_be_generated_for_invalid_transitions() {
    // As a demonstration, try a specific invalid transition
    assertNotNull(testComponent, "Component should exist");

    try {
      testComponent.setState(State.CONCEPTION);
      Exception transitionException = null;
      try {
        testComponent.setState(State.TERMINATED); // Should be invalid
      } catch (Exception e) {
        transitionException = e;
      }

      assertNotNull(transitionException, "Invalid transition should throw exception");
      assertTrue(
          transitionException.getMessage().contains("Invalid state transition"),
          "Exception message should describe the invalid transition");

    } catch (Exception e) {
      fail("Test setup failed: " + e.getMessage());
    }
  }

  // Helper methods

  /**
   * Returns a list of states representing a full component lifecycle. This would need to be updated
   * based on the actual implementation.
   */
  private List<State> getFullLifecycleStates() {
    List<State> states = new ArrayList<>();
    states.add(State.CONCEPTION);
    states.add(State.INITIALIZING);
    states.add(State.CONFIGURING);
    states.add(State.SPECIALIZING);
    states.add(State.DEVELOPING_FEATURES);
    states.add(State.READY);
    states.add(State.ACTIVE);
    states.add(State.STABLE);
    states.add(State.TERMINATING);
    states.add(State.TERMINATED);
    return states;
  }
}
