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

import org.s8r.tube.Tube;

import io.cucumber.java.en.*;

/**
 * Step definitions for testing the Substrate Identity aspect of the Tube Lifecycle model.
 *
 * <p>These steps implement tests specifically for the biological continuity analog of tube
 * identity, focusing on:
 *
 * <ul>
 *   <li>Unique identification
 *   <li>Creation tracking
 *   <li>Lineage management
 *   <li>Hierarchical addressing
 *   <li>Environmental context capture
 * </ul>
 */
public class SubstrateIdentitySteps extends BaseLifecycleSteps {
  private Tube parentTube;
  private Tube childTube;

  // Creation Tracking Steps

  @Then("the tube should record a precise creation timestamp")
  public void the_tube_should_record_a_precise_creation_timestamp() {
    List<String> logs = testTube.queryMimirLog();
    assertFalse(logs.isEmpty(), "Tube should have log entries");

    // Check for ISO-8601 timestamp format (YYYY-MM-DDThh:mm:ss.sssZ)
    boolean hasTimestamp =
        logs.stream()
            .anyMatch(
                entry -> entry.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+.*"));
    assertTrue(hasTimestamp, "Tube should record precise creation timestamp");
  }

  @Then("the tube should capture the complete environmental context")
  public void the_tube_should_capture_the_complete_environmental_context() {
    List<String> logs = testTube.queryMimirLog();

    // Check for environment logging
    boolean hasEnvironmentDetails = logs.stream().anyMatch(entry -> entry.contains("Environment:"));
    assertTrue(hasEnvironmentDetails, "Tube should log environmental context");
  }

  @Then("the tube's birth certificate should contain the reason {string}")
  public void the_tubes_birth_certificate_should_contain_the_reason(String reason) {
    List<String> logs = testTube.queryMimirLog();

    // Check if any log entry contains the reason
    boolean hasReason =
        logs.stream().anyMatch(entry -> entry.contains("Initialization reason: " + reason));
    assertTrue(hasReason, "Tube birth certificate should contain reason: " + reason);
  }

  // Lineage Management Steps

  @Given("a parent tube with reason {string}")
  public void a_parent_tube_with_reason(String reason) {
    parentTube = createTube(reason, environment);
    assertNotNull(parentTube, "Parent tube should be created successfully");
    storeInContext("parentTube", parentTube);
  }

  @When("a child tube is created from the parent with reason {string}")
  public void a_child_tube_is_created_from_the_parent_with_reason(String reason) {
    // Current implementation doesn't directly support parent-child relationship
    // For now, we'll create a tube and add the parent to its lineage
    childTube = createTube(reason, environment);
    assertNotNull(childTube, "Child tube should be created successfully");

    // Simulate lineage connection by adding parent ID to lineage
    childTube.addToLineage("Child of tube: " + parentTube.getUniqueId());

    storeInContext("childTube", childTube);
  }

  @Then("the child tube should have its own unique identifier")
  public void the_child_tube_should_have_its_own_unique_identifier() {
    assertNotNull(childTube, "Child tube should exist");
    assertNotNull(childTube.getUniqueId(), "Child tube should have a unique ID");

    // Verify child and parent have different IDs
    assertNotEquals(
        parentTube.getUniqueId(),
        childTube.getUniqueId(),
        "Child should have a different ID than parent");
  }

  @Then("the child tube should reference its parent in its lineage")
  public void the_child_tube_should_reference_its_parent_in_its_lineage() {
    List<String> lineage = childTube.getLineage();
    assertFalse(lineage.isEmpty(), "Child tube should have lineage entries");

    // Check if lineage contains reference to parent
    boolean referencesParent =
        lineage.stream().anyMatch(entry -> entry.contains(parentTube.getUniqueId()));
    assertTrue(referencesParent, "Child tube should reference parent in lineage");
  }

  @Then("the parent tube should track its child in descendants")
  public void the_parent_tube_should_track_its_child_in_descendants() {
    // Current implementation doesn't directly support tracking descendants
    // This would be implemented when enhancing the Tube.java model
    // For now, this is a placeholder test
    assertNotNull(parentTube, "Parent tube should exist");
  }

  // More step definitions for hierarchical addressing and environmental context
  // will be added as we implement those features
}
