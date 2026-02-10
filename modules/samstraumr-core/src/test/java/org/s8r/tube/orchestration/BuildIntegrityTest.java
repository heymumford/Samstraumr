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

package org.s8r.tube.orchestration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.s8r.tube.Environment;
import org.s8r.tube.Tube;
import org.s8r.tube.composite.Composite;
import org.s8r.tube.composite.CompositeFactory;
import org.s8r.tube.test.annotations.AboveTheLine;
import org.s8r.tube.test.annotations.OrchestrationTest;

/**
 * Basic orchestration tests to verify build integrity.
 *
 * <p>These tests verify that the essential components of Samstraumr are correctly assembled and can
 * be instantiated. They serve as the first line of defense to ensure the build process was
 * successful and core functionality is available.
 */
@OrchestrationTest
@AboveTheLine
public class BuildIntegrityTest {

  @Test
  @DisplayName("Core environment should initialize")
  void environmentShouldInitialize() {
    Environment env = new Environment();
    Map<String, Object> params = env.getParameters();
    assertNotNull(params, "Environment parameters should not be null");
    assertTrue(params.containsKey("hostname"), "Environment parameters should include hostname");
  }

  @Test
  @DisplayName("Tube creation should succeed")
  void tubeShouldBeCreatable() {
    Environment env = new Environment();
    Tube tube = Tube.create("Orchestration test tube", env);
    assertNotNull(tube, "Tube should be created successfully");
    assertNotNull(tube.getUniqueId(), "Tube should have a unique ID");
  }

  @Test
  @DisplayName("Composite creation should succeed")
  void compositeShouldBeCreatable() {
    Environment env = new Environment();
    Composite composite = CompositeFactory.createComposite(env);
    assertNotNull(composite, "Composite should be created successfully");
    assertTrue(composite.isActive(), "Newly created composite should be active");
  }

  @Test
  @DisplayName("Basic composite wiring should function")
  void compositeWiringShouldFunction() {
    Environment env = new Environment();

    // Create a simple linear composite
    Composite composite = CompositeFactory.createComposite(env);
    composite.createTube("source", "Source tube for orchestration test");
    composite.createTube("target", "Target tube for orchestration test");
    composite.connect("source", "target");

    // Verify the structure
    assertTrue(composite.getTubes().size() == 2, "Composite should have 2 tubes");
    assertTrue(composite.getConnections().containsKey("source"), "Source tube should be connected");
    assertTrue(
        composite.getConnections().get("source").contains("target"),
        "Source should be connected to target");
  }
}
