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
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.s8r.component.core.State;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;

/**
 * Step definitions for component state management tests.
 *
 * <p>These step definitions implement the BDD scenarios related to component state transitions,
 * lifecycle management, and termination behaviors.
 */
public class ComponentStateSteps extends BaseComponentSteps {

  private List<State> stateTransitions = new ArrayList<>();

  @Then("the component should have initial state {string}")
  public void the_component_should_have_initial_state(String stateName) {
    assertNotNull(testComponent, "Component should exist");
    assertEquals(
        State.valueOf(stateName),
        testComponent.getState(),
        "Component should have correct initial state");
  }

  @When("the component transitions through early lifecycle")
  public void the_component_transitions_through_early_lifecycle() {
    assertNotNull(testComponent, "Component should exist");

    // Record initial state
    stateTransitions.add(testComponent.getState());

    // Proceed through early lifecycle (as implemented in Component.proceedThroughEarlyLifecycle)
    testComponent.proceedThroughEarlyLifecycle();

    // Record state transitions
    stateTransitions.add(testComponent.getState());

    storeInContext("stateTransitions", stateTransitions);
  }

  @Then("the component state should progress in order:")
  public void the_component_state_should_progress_in_order(DataTable dataTable) {
    List<Map<String, String>> stateList = dataTable.asMaps(String.class, String.class);

    // Gather the memory log which contains state transition information
    List<String> logs = testComponent.getMemoryLog();

    // Verify each state in the expected progression is found in the correct order
    for (Map<String, String> stateData : stateList) {
      String stateName = stateData.get("State");
      String stateCategory = stateData.get("Category");

      // Verify state transition was logged
      boolean stateFound =
          logs.stream().anyMatch(log -> log.contains("State changed:") && log.contains(stateName));

      assertTrue(
          stateFound,
          "Component should transition through state: "
              + stateName
              + " of category: "
              + stateCategory);
    }
  }

  @Then("the component should be in non-terminated state")
  public void the_component_should_be_in_non_terminated_state() {
    assertNotNull(testComponent, "Component should exist");
    assertFalse(testComponent.isTerminated(), "Component should not be terminated");
  }

  @When("the component is terminated")
  public void the_component_is_terminated() {
    assertNotNull(testComponent, "Component should exist");
    testComponent.terminate();
  }

  @Then("the component should be in terminated state")
  public void the_component_should_be_in_terminated_state() {
    assertNotNull(testComponent, "Component should exist");
    assertTrue(testComponent.isTerminated(), "Component should be terminated");
  }

  @Then("the component should log its termination")
  public void the_component_should_log_its_termination() {
    assertNotNull(testComponent, "Component should exist");
    List<String> logs = testComponent.getMemoryLog();

    boolean terminationLogged =
        logs.stream().anyMatch(log -> log.contains("terminated") || log.contains("termination"));

    assertTrue(terminationLogged, "Component should log its termination");
  }

  @Then("the component's resources should be released")
  public void the_components_resources_should_be_released() {
    assertNotNull(testComponent, "Component should exist");

    // Check memory log for resource release
    List<String> logs = testComponent.getMemoryLog();
    boolean resourceReleaseLogged =
        logs.stream().anyMatch(log -> log.contains("resources") && log.contains("releas"));

    assertTrue(resourceReleaseLogged, "Component should log resource release during termination");
  }

  @Then("the component state should have a description")
  public void the_component_state_should_have_a_description() {
    assertNotNull(testComponent, "Component should exist");
    State state = testComponent.getState();

    assertNotNull(state.getDescription(), "State should have a description");
    assertFalse(state.getDescription().isEmpty(), "State description should not be empty");
  }

  @Then("the component state should have a biological analog")
  public void the_component_state_should_have_a_biological_analog() {
    assertNotNull(testComponent, "Component should exist");
    State state = testComponent.getState();

    if (state.isLifecycle()) {
      assertNotNull(state.getBiologicalAnalog(), "Lifecycle state should have a biological analog");
      assertFalse(state.getBiologicalAnalog().isEmpty(), "Biological analog should not be empty");
    }
  }

  @Then("the component should be able to check its state category")
  public void the_component_should_be_able_to_check_its_state_category() {
    assertNotNull(testComponent, "Component should exist");
    State state = testComponent.getState();

    assertNotNull(state.getCategory(), "State should have a category");

    // Verify category check methods work
    if (state.getCategory() == State.Category.LIFECYCLE) {
      assertTrue(state.isLifecycle(), "isLifecycle() should return true for LIFECYCLE category");
    } else if (state.getCategory() == State.Category.OPERATIONAL) {
      assertTrue(
          state.isOperational(), "isOperational() should return true for OPERATIONAL category");
    }
  }

  @When("attempting to change component state should fail with appropriate error")
  public void attempting_to_change_component_state_should_fail_with_appropriate_error() {
    assertNotNull(testComponent, "Component should exist");

    try {
      testComponent.setState(State.ACTIVE);
      fail("Should not be able to change state of terminated component");
    } catch (Exception e) {
      exceptionThrown = e;
      storeInContext("exceptionThrown", exceptionThrown);
    }

    assertNotNull(exceptionThrown, "An exception should have been thrown");
    assertTrue(
        exceptionThrown instanceof IllegalStateException,
        "Exception should be IllegalStateException");
    assertTrue(
        exceptionThrown.getMessage().contains("terminated"),
        "Error message should mention termination");
  }

  @When("a custom termination delay of {int} milliseconds is set")
  public void a_custom_termination_delay_of_milliseconds_is_set(Integer delay) {
    assertNotNull(testComponent, "Component should exist");
    testComponent.setTerminationDelay(delay / 1000); // Convert ms to seconds

    // Store the time for verification
    storeInContext("terminationDelayMs", delay);
    storeInContext("terminationSetTime", System.currentTimeMillis());
  }

  @Then("the component should terminate after the specified delay")
  public void the_component_should_terminate_after_the_specified_delay()
      throws InterruptedException {
    assertNotNull(testComponent, "Component should exist");
    Integer delay = getFromContext("terminationDelayMs", Integer.class);
    assertNotNull(delay, "Termination delay should be stored in context");

    // Wait for termination
    CountDownLatch latch = new CountDownLatch(1);

    // Use a separate thread to check for termination
    new Thread(
            () -> {
              try {
                // Check every 100ms
                while (!testComponent.isTerminated()) {
                  Thread.sleep(100);
                }
                latch.countDown();
              } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
              }
            })
        .start();

    // Wait with a buffer (delay + 500ms)
    boolean terminated = latch.await(delay + 1000, TimeUnit.MILLISECONDS);
    assertTrue(terminated, "Component should terminate after the specified delay");

    // Verify termination
    assertTrue(testComponent.isTerminated(), "Component should be terminated");
  }
}
