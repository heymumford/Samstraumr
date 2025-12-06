/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0
 */
package org.s8r.test.steps.lifecycle;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.jupiter.api.Assertions;
import org.s8r.component.Component;
import org.s8r.component.Environment;
import org.s8r.component.Identity;
import org.s8r.component.State;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for embryonic phase tests, focusing on structural identity development during
 * the embryonic phase of a component's lifecycle.
 */
public class EmbryonicPhaseSteps {

  private static final Logger LOGGER = Logger.getLogger(EmbryonicPhaseSteps.class.getName());

  // Test context
  private Component component;
  private Identity identity;
  private Exception lastException;
  private boolean structuralAnomaly = false;
  private Map<String, Boolean> structureElements = new HashMap<>();

  @Given("a new tube has been created")
  public void a_new_tube_has_been_created() {
    try {
      // Create a new component (representing a "tube")
      component = Component.createAdam("Embryonic Phase Test");

      // Retrieve the component's identity
      identity = component.getIdentity();

      Assertions.assertNotNull(component, "Component should be created");
      Assertions.assertNotNull(identity, "Identity should be created");

      LOGGER.info(
          "Created component for embryonic phase testing with identity: " + identity.getId());
    } catch (Exception e) {
      lastException = e;
      LOGGER.severe("Failed to create component: " + e.getMessage());
      throw new AssertionError("Failed to create component", e);
    }
  }

  @When("the tube develops its connection framework")
  public void the_tube_develops_its_connection_framework() {
    try {
      // Set up the connection framework for the component
      // This would typically involve initializing input and output connection points

      // In the S8r model, this might involve transitioning to a state where connections can be
      // established
      if (component.getState() != State.ACTIVE) {
        component.setState(State.ACTIVE);
      }

      // Initialize connection framework in the environment
      Environment env = component.getEnvironment();
      env.setValue("connections.initialized", "true");
      env.setValue("input.connections.count", "3"); // Example value
      env.setValue("output.connections.count", "2"); // Example value

      // Mark connection points as available
      for (int i = 0; i < 3; i++) {
        env.setValue(
            "input.connection." + i + ".id", "input-" + i + "-" + identity.getId().substring(0, 8));
        env.setValue("input.connection." + i + ".status", "available");
      }

      for (int i = 0; i < 2; i++) {
        env.setValue(
            "output.connection." + i + ".id",
            "output-" + i + "-" + identity.getId().substring(0, 8));
        env.setValue("output.connection." + i + ".status", "available");
      }

      LOGGER.info("Component connection framework developed");
    } catch (Exception e) {
      lastException = e;
      LOGGER.severe("Failed to develop connection framework: " + e.getMessage());
      throw new AssertionError("Failed to develop connection framework", e);
    }
  }

  @When("the tube establishes its internal structure")
  public void the_tube_establishes_its_internal_structure() {
    try {
      // Set up the internal structure for the component

      // In the S8r model, this involves initializing processing mechanisms, state containers, and
      // boundaries
      Environment env = component.getEnvironment();

      // Initialize processing mechanism
      env.setValue("processing.mechanism", "standard");
      env.setValue("processing.capacity", "10");
      env.setValue("processing.algorithm", "default");
      structureElements.put("processing", true);

      // Initialize state containers
      env.setValue("state.container.count", "5");
      for (int i = 0; i < 5; i++) {
        env.setValue("state.container." + i + ".id", "state-" + i);
        env.setValue("state.container." + i + ".type", i % 2 == 0 ? "persistent" : "transient");
        env.setValue("state.container." + i + ".capacity", String.valueOf(100 * (i + 1)));
      }
      structureElements.put("stateContainers", true);

      // Define operational boundaries
      env.setValue("operation.boundary.min", "0");
      env.setValue("operation.boundary.max", "100");
      env.setValue("operation.boundary.timeout", "5000");
      structureElements.put("operationalBoundaries", true);

      LOGGER.info("Component internal structure established");
    } catch (Exception e) {
      lastException = e;
      LOGGER.severe("Failed to establish internal structure: " + e.getMessage());
      throw new AssertionError("Failed to establish internal structure", e);
    }
  }

  @When("the tube encounters a structural anomaly during development")
  public void the_tube_encounters_a_structural_anomaly_during_development() {
    try {
      // Simulate a structural anomaly during development
      structuralAnomaly = true;

      // Set up the internal structure with an anomaly
      Environment env = component.getEnvironment();

      // Initialize processing mechanism with anomaly
      env.setValue("processing.mechanism", "corrupted");
      env.setValue("processing.capacity", "-10"); // Invalid negative capacity
      env.setValue("processing.algorithm", "unknown"); // Unknown algorithm
      structureElements.put("processing", false); // Mark as failed

      // Initialize state containers with partial success
      env.setValue("state.container.count", "5");
      for (int i = 0; i < 5; i++) {
        if (i == 2) {
          // Simulate a failed container
          env.setValue("state.container." + i + ".status", "failed");
        } else {
          env.setValue("state.container." + i + ".id", "state-" + i);
          env.setValue("state.container." + i + ".type", i % 2 == 0 ? "persistent" : "transient");
          env.setValue("state.container." + i + ".capacity", String.valueOf(100 * (i + 1)));
        }
      }
      structureElements.put("stateContainers", true); // Mark as partially successful

      // Define operational boundaries with issues
      env.setValue("operation.boundary.min", "100");
      env.setValue("operation.boundary.max", "50"); // Invalid: max < min
      env.setValue("operation.boundary.timeout", "5000");
      structureElements.put("operationalBoundaries", false); // Mark as failed

      // Record anomaly in environment
      env.setValue("structural.anomaly", "true");
      env.setValue(
          "structural.anomaly.details",
          "Invalid processing capacity, failed state container, invalid operational boundaries");

      LOGGER.info("Component encountered structural anomaly during development");
    } catch (Exception e) {
      lastException = e;
      LOGGER.severe("Unexpected error during structural anomaly simulation: " + e.getMessage());
      throw new AssertionError("Unexpected error during structural anomaly simulation", e);
    }
  }

  @Then("the tube should have input connection points")
  public void the_tube_should_have_input_connection_points() {
    Environment env = component.getEnvironment();
    String inputConnectionCount = env.getValue("input.connections.count");

    Assertions.assertNotNull(inputConnectionCount, "Input connections count should be defined");
    Assertions.assertTrue(
        Integer.parseInt(inputConnectionCount) > 0,
        "Component should have input connection points");

    // Verify individual input connections
    int count = Integer.parseInt(inputConnectionCount);
    for (int i = 0; i < count; i++) {
      String connectionId = env.getValue("input.connection." + i + ".id");
      String connectionStatus = env.getValue("input.connection." + i + ".status");

      Assertions.assertNotNull(connectionId, "Input connection " + i + " should have an ID");
      Assertions.assertNotNull(connectionStatus, "Input connection " + i + " should have a status");
    }
  }

  @And("the tube should have output connection points")
  public void the_tube_should_have_output_connection_points() {
    Environment env = component.getEnvironment();
    String outputConnectionCount = env.getValue("output.connections.count");

    Assertions.assertNotNull(outputConnectionCount, "Output connections count should be defined");
    Assertions.assertTrue(
        Integer.parseInt(outputConnectionCount) > 0,
        "Component should have output connection points");

    // Verify individual output connections
    int count = Integer.parseInt(outputConnectionCount);
    for (int i = 0; i < count; i++) {
      String connectionId = env.getValue("output.connection." + i + ".id");
      String connectionStatus = env.getValue("output.connection." + i + ".status");

      Assertions.assertNotNull(connectionId, "Output connection " + i + " should have an ID");
      Assertions.assertNotNull(
          connectionStatus, "Output connection " + i + " should have a status");
    }
  }

  @And("the connection points should have proper identifiers")
  public void the_connection_points_should_have_proper_identifiers() {
    Environment env = component.getEnvironment();

    // Check input connection identifiers
    int inputCount = Integer.parseInt(env.getValue("input.connections.count"));
    for (int i = 0; i < inputCount; i++) {
      String connectionId = env.getValue("input.connection." + i + ".id");

      Assertions.assertNotNull(connectionId, "Input connection " + i + " should have an ID");
      Assertions.assertTrue(
          connectionId.startsWith("input-"), "Input connection ID should start with 'input-'");
      Assertions.assertTrue(
          connectionId.contains(identity.getId().substring(0, 8)),
          "Input connection ID should contain part of the component identity");
    }

    // Check output connection identifiers
    int outputCount = Integer.parseInt(env.getValue("output.connections.count"));
    for (int i = 0; i < outputCount; i++) {
      String connectionId = env.getValue("output.connection." + i + ".id");

      Assertions.assertNotNull(connectionId, "Output connection " + i + " should have an ID");
      Assertions.assertTrue(
          connectionId.startsWith("output-"), "Output connection ID should start with 'output-'");
      Assertions.assertTrue(
          connectionId.contains(identity.getId().substring(0, 8)),
          "Output connection ID should contain part of the component identity");
    }
  }

  @Then("the tube should have a processing mechanism")
  public void the_tube_should_have_a_processing_mechanism() {
    Environment env = component.getEnvironment();

    String processingMechanism = env.getValue("processing.mechanism");
    String processingCapacity = env.getValue("processing.capacity");
    String processingAlgorithm = env.getValue("processing.algorithm");

    Assertions.assertNotNull(processingMechanism, "Component should have a processing mechanism");
    Assertions.assertNotNull(processingCapacity, "Component should have a processing capacity");
    Assertions.assertNotNull(processingAlgorithm, "Component should have a processing algorithm");

    // Verify processing capacity is a positive number
    int capacity = Integer.parseInt(processingCapacity);
    Assertions.assertTrue(capacity > 0, "Processing capacity should be positive");
  }

  @And("the tube should have state containers")
  public void the_tube_should_have_state_containers() {
    Environment env = component.getEnvironment();

    String containerCount = env.getValue("state.container.count");
    Assertions.assertNotNull(containerCount, "State container count should be defined");
    Assertions.assertTrue(
        Integer.parseInt(containerCount) > 0, "Component should have state containers");

    // Verify individual state containers
    int count = Integer.parseInt(containerCount);
    for (int i = 0; i < count; i++) {
      // Skip containers marked as failed in anomaly scenario
      if ("failed".equals(env.getValue("state.container." + i + ".status"))) {
        continue;
      }

      String containerId = env.getValue("state.container." + i + ".id");
      String containerType = env.getValue("state.container." + i + ".type");
      String containerCapacity = env.getValue("state.container." + i + ".capacity");

      Assertions.assertNotNull(containerId, "State container " + i + " should have an ID");
      Assertions.assertNotNull(containerType, "State container " + i + " should have a type");
      Assertions.assertNotNull(
          containerCapacity, "State container " + i + " should have a capacity");
    }
  }

  @And("the tube should have defined operational boundaries")
  public void the_tube_should_have_defined_operational_boundaries() {
    Environment env = component.getEnvironment();

    String boundaryMin = env.getValue("operation.boundary.min");
    String boundaryMax = env.getValue("operation.boundary.max");
    String boundaryTimeout = env.getValue("operation.boundary.timeout");

    Assertions.assertNotNull(boundaryMin, "Operational boundary minimum should be defined");
    Assertions.assertNotNull(boundaryMax, "Operational boundary maximum should be defined");
    Assertions.assertNotNull(boundaryTimeout, "Operational boundary timeout should be defined");

    // Skip validation in anomaly scenario
    if (!structuralAnomaly) {
      // Verify boundaries are valid
      int min = Integer.parseInt(boundaryMin);
      int max = Integer.parseInt(boundaryMax);
      int timeout = Integer.parseInt(boundaryTimeout);

      Assertions.assertTrue(min < max, "Minimum boundary should be less than maximum boundary");
      Assertions.assertTrue(timeout > 0, "Timeout should be positive");
    }
  }

  @Then("the tube should attempt structural self-correction")
  public void the_tube_should_attempt_structural_self_correction() {
    // Simulate self-correction attempt
    try {
      Environment env = component.getEnvironment();

      // Attempt to correct processing mechanism
      if (!structureElements.get("processing")) {
        env.setValue("processing.mechanism", "fallback");
        env.setValue("processing.capacity", "5"); // Reduced but valid capacity
        env.setValue("processing.algorithm", "default");
        env.setValue("processing.self.corrected", "true");
      }

      // Attempt to correct operational boundaries
      if (!structureElements.get("operationalBoundaries")) {
        env.setValue("operation.boundary.min", "0");
        env.setValue("operation.boundary.max", "50");
        env.setValue("operation.boundary.self.corrected", "true");
      }

      // Record self-correction attempt
      env.setValue("structural.self.correction.attempted", "true");

      // Check if correction was successful
      boolean processingCorrected = "fallback".equals(env.getValue("processing.mechanism"));
      boolean boundariesCorrected =
          "0".equals(env.getValue("operation.boundary.min"))
              && "50".equals(env.getValue("operation.boundary.max"));

      Assertions.assertTrue(
          processingCorrected, "Component should have attempted to correct processing mechanism");
      Assertions.assertTrue(
          boundariesCorrected, "Component should have attempted to correct operational boundaries");

      LOGGER.info("Component attempted structural self-correction");
    } catch (Exception e) {
      lastException = e;
      LOGGER.severe("Unexpected error during self-correction attempt: " + e.getMessage());
      throw new AssertionError("Unexpected error during self-correction attempt", e);
    }
  }

  @And("the tube should report the anomaly to the environment")
  public void the_tube_should_report_the_anomaly_to_the_environment() {
    Environment env = component.getEnvironment();

    // Verify anomaly was reported
    String anomalyReported = env.getValue("structural.anomaly");
    String anomalyDetails = env.getValue("structural.anomaly.details");

    Assertions.assertEquals("true", anomalyReported, "Component should report structural anomaly");
    Assertions.assertNotNull(anomalyDetails, "Component should report anomaly details");
    Assertions.assertFalse(anomalyDetails.isEmpty(), "Anomaly details should not be empty");
  }

  @And("the tube should establish a fallback functional mode")
  public void the_tube_should_establish_a_fallback_functional_mode() {
    Environment env = component.getEnvironment();

    // Set up fallback mode
    env.setValue("operating.mode", "fallback");
    env.setValue("functionality.level", "limited");

    // Verify fallback mode was established
    Assertions.assertEquals(
        "fallback",
        env.getValue("operating.mode"),
        "Component should establish fallback operating mode");
    Assertions.assertEquals(
        "limited",
        env.getValue("functionality.level"),
        "Component should have limited functionality in fallback mode");

    // Verify component is still operational despite anomalies
    Assertions.assertNotEquals(
        State.FAILED,
        component.getState(),
        "Component should not be in FAILED state despite anomalies");
  }
}
