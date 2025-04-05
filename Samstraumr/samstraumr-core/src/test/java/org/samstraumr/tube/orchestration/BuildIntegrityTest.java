/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

package org.samstraumr.tube.orchestration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.samstraumr.tube.Environment;
import org.samstraumr.tube.Tube;
import org.samstraumr.tube.composite.Composite;
import org.samstraumr.tube.composite.CompositeFactory;
import org.samstraumr.tube.test.annotations.AboveTheLine;
import org.samstraumr.tube.test.annotations.OrchestrationTest;

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
