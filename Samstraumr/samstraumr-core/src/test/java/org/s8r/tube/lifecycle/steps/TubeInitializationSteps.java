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

package org.s8r.tube.lifecycle.steps;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import io.cucumber.java.en.*;

/**
 * Step definitions for low-level tube initialization tests.
 *
 * <p>⚠️ DEPRECATED: This class is being replaced by the more specialized biological lifecycle step
 * definitions. Please use the following step classes instead:
 *
 * <ul>
 *   <li>{@link ConceptionPhaseSteps} - For initial tube creation
 *   <li>{@link EmbryonicPhaseSteps} - For structural formation
 * </ul>
 *
 * This class will be maintained only for backward compatibility and may be removed in a future
 * release.
 *
 * <p>These step definitions test the fundamental initialization behaviors of tubes, focusing on the
 * first two phases of the biological lifecycle model:
 *
 * <ul>
 *   <li>Creation Phase (Fertilization/Zygote Analog)
 *   <li>Initialization Phase (Cleavage/Early Cell Division Analog)
 * </ul>
 *
 * @deprecated Use {@link ConceptionPhaseSteps} and {@link EmbryonicPhaseSteps} instead
 */
@Deprecated
public class TubeInitializationSteps extends BaseLifecycleSteps {

  @Given("a basic environment for initialization")
  public void a_basic_environment_for_initialization() {
    environment = prepareEnvironment();
    storeInContext("environment", environment);
  }

  @When("a tube is initialized with reason {string}")
  public void a_tube_is_initialized_with_reason(String reason) {
    testTube = createTube(reason, environment);
    storeInContext("testTube", testTube);
  }

  @Then("the tube should exist and have a valid UUID")
  public void the_tube_should_exist_and_have_a_valid_uuid() {
    assertTubeHasValidId();
  }

  @Then("the tube should log its initialization with timestamp")
  public void the_tube_should_log_its_initialization_with_timestamp() {
    assertNotNull(testTube, "Tube should exist");
    List<String> logs = testTube.queryMimirLog();
    assertFalse(logs.isEmpty(), "Tube should have log entries");

    // Check if any log entry contains the initialization message with timestamp
    boolean hasInitLog =
        logs.stream()
            .anyMatch(entry -> entry.matches("^\\d{4}-\\d{2}-\\d{2}T.*Tube initialized.*"));
    assertTrue(hasInitLog, "Tube should log initialization with timestamp");
  }

  @Then("the tube should capture its reason {string}")
  public void the_tube_should_capture_its_reason(String reason) {
    assertNotNull(testTube, "Tube should exist");
    assertEquals(reason, testTube.getReason(), "Tube should store its creation reason");

    // Verify reason is also in the logs
    List<String> logs = testTube.queryMimirLog();
    boolean hasReasonLog = logs.stream().anyMatch(entry -> entry.contains(reason));
    assertTrue(hasReasonLog, "Tube should log its reason");
  }

  @Then("the tube should be queryable for its logs")
  public void the_tube_should_be_queryable_for_its_logs() {
    assertNotNull(testTube, "Tube should exist");
    List<String> logs = testTube.queryMimirLog();
    assertNotNull(logs, "Tube should support log querying");
    assertFalse(logs.isEmpty(), "Tube should have log entries");
  }

  @When("the tube's environment state changes to {string}")
  public void the_tubes_environment_state_changes_to(String state) {
    assertNotNull(testTube, "Tube should exist");
    testTube.updateEnvironmentState(state);
  }

  @Then("the tube should respond to the environmental change")
  public void the_tube_should_respond_to_the_environmental_change() {
    assertNotNull(testTube, "Tube should exist");
    List<String> logs = testTube.queryMimirLog();

    // Verify that the latest log entries mention environment state changes
    boolean hasEnvironmentLog = logs.stream().anyMatch(entry -> entry.contains("Environment"));
    assertTrue(hasEnvironmentLog, "Tube should log environmental changes");
  }
}
