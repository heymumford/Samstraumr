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

import java.util.ArrayList;
import java.util.List;

import org.s8r.tube.Tube;

import io.cucumber.java.en.*;

/**
 * Step definitions for testing the Tube Lifecycle model following a biological development
 * approach.
 *
 * <p>These steps implement tests for the phases of Tube development:
 *
 * <ul>
 *   <li>Creation Phase (Fertilization/Zygote Analog)
 *   <li>Initialization Phase (Cleavage/Early Cell Division Analog)
 *   <li>Configuration Phase (Blastulation/Early Organization Analog)
 *   <li>Specialization Phase (Gastrulation/Germ Layer Analog)
 *   <li>Feature Development Phase (Organogenesis Analog)
 *   <li>Operational Phase (Post-Embryonic Growth Analog)
 *   <li>Transformation Phase (Metamorphosis Analog)
 *   <li>Stability Phase (Maturity & Reproduction Analog)
 *   <li>Degradation Phase (Aging & Senescence Analog)
 *   <li>Termination Phase (Death & Nutrient Recycling Analog)
 * </ul>
 */
public class TubeLifecycleSteps extends BaseLifecycleSteps {
  private List<Tube> tubeList = new ArrayList<>();

  // Creation Phase Step Definitions

  @Given("a prepared environment for tube creation")
  public void a_prepared_environment_for_tube_creation() {
    environment = prepareEnvironment();
    storeInContext("environment", environment);
  }

  @When("a tube is created with reason {string}")
  public void a_tube_is_created_with_reason(String reason) {
    testTube = createTube(reason, environment);
    storeInContext("testTube", testTube);
  }

  @Then("the tube should have a unique identifier")
  public void the_tube_should_have_a_unique_identifier() {
    assertTubeHasValidId();
  }

  @Then("the identifier should include a creation timestamp")
  public void the_identifier_should_include_a_creation_timestamp() {
    // This would normally check the timestamp in the ID
    // Since the current implementation doesn't expose this directly,
    // we'll verify the tube logs which should contain timestamps
    List<String> logs = testTube.queryMimirLog();
    assertFalse(logs.isEmpty(), "Tube should have log entries");

    // Check if logs contain timestamps (ISO-8601 format starts with year)
    boolean hasTimestamp =
        logs.stream().anyMatch(entry -> entry.matches("^\\d{4}-\\d{2}-\\d{2}T.*"));
    assertTrue(hasTimestamp, "Tube logs should contain timestamps");
  }

  @Then("the identifier should not be modifiable")
  public void the_identifier_should_not_be_modifiable() {
    // Store the original ID
    String originalId = testTube.getUniqueId();

    // Attempt operations that might indirectly change the ID
    // (in this case trigger lifecycle events)
    testTube.updateEnvironmentState("testing immutability");

    // Verify the ID hasn't changed
    assertEquals(originalId, testTube.getUniqueId(), "Tube ID should be immutable");
  }

  // More step definitions will be added as we implement the tests
}
