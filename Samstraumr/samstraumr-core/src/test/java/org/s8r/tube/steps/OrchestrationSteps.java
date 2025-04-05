/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

package org.s8r.tube.steps;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.s8r.tube.Environment;
import org.s8r.tube.Tube;
import org.s8r.tube.composite.Composite;
import org.s8r.tube.composite.CompositeFactory;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for orchestration tests.
 *
 * <p>These steps validate the most basic functionality of Samstraumr to ensure build integrity and
 * core component wiring.
 */
public class OrchestrationSteps {

  private Environment environment;
  private Tube tube;
  private Composite composite;
  private String sourceTubeName;
  private String targetTubeName;

  @Given("a new Environment is created")
  public void createEnvironment() {
    environment = new Environment();
    assertNotNull(environment, "Environment should be created successfully");
  }

  @Then("the environment should have valid parameters")
  public void environmentShouldHaveValidParameters() {
    Map<String, Object> params = environment.getParameters();
    assertNotNull(params, "Environment parameters should not be null");
    assertFalse(params.isEmpty(), "Environment parameters should not be empty");
  }

  @Then("the environment should contain runtime information")
  public void environmentShouldContainRuntimeInformation() {
    Map<String, Object> params = environment.getParameters();
    assertTrue(params.containsKey("hostname"), "Environment parameters should include hostname");
    assertTrue(
        params.containsKey("cpu") || params.containsKey("processor"),
        "Environment parameters should include CPU information");
  }

  @When("a new Tube is created with reason {string}")
  public void createTube(String reason) {
    tube = Tube.create(reason, environment);
    assertNotNull(tube, "Tube should be created successfully");
  }

  @Then("the tube should have a valid unique ID")
  public void tubeShouldHaveValidUniqueId() {
    String uniqueId = tube.getUniqueId();
    assertNotNull(uniqueId, "Tube unique ID should not be null");
    assertFalse(uniqueId.isEmpty(), "Tube unique ID should not be empty");
  }

  @Then("the tube should log its creation in Mimir")
  public void tubeShouldLogCreationInMimir() {
    assertFalse(tube.queryMimirLog().isEmpty(), "Tube should have logged events in Mimir");
  }

  @When("a new Composite is created")
  public void createComposite() {
    composite = CompositeFactory.createComposite(environment);
    assertNotNull(composite, "Composite should be created successfully");
  }

  @Then("the composite should be active")
  public void compositeShouldBeActive() {
    assertTrue(composite.isActive(), "Newly created composite should be active");
  }

  @Then("the composite should have no tubes initially")
  public void compositeShouldHaveNoTubesInitially() {
    assertTrue(composite.getTubes().isEmpty(), "Newly created composite should have no tubes");
  }

  @When("a source tube is added to the composite")
  public void addSourceTubeToComposite() {
    sourceTubeName = "source";
    composite.createTube(sourceTubeName, "Source tube for orchestration test");
  }

  @When("a target tube is added to the composite")
  public void addTargetTubeToComposite() {
    targetTubeName = "target";
    composite.createTube(targetTubeName, "Target tube for orchestration test");
  }

  @When("the source tube is connected to the target tube")
  public void connectSourceToTargetTube() {
    composite.connect(sourceTubeName, targetTubeName);
  }

  @Then("the composite should have {int} tubes")
  public void compositeShouldHaveTubes(int count) {
    assertTrue(composite.getTubes().size() == count, "Composite should have " + count + " tubes");
  }

  @Then("the source tube should be connected to the target tube")
  public void sourceShouldBeConnectedToTarget() {
    assertTrue(
        composite.getConnections().containsKey(sourceTubeName),
        "Source tube should be in connections map");
    assertTrue(
        composite.getConnections().get(sourceTubeName).contains(targetTubeName),
        "Source should be connected to target");
  }
}
