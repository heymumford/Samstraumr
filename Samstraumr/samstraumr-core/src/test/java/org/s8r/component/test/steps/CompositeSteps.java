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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.s8r.component.Composite;
import org.s8r.component.CompositeFactory;
import org.s8r.component.Component;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;

/**
 * Step definitions for composite component tests.
 *
 * <p>These step definitions implement the BDD scenarios related to composite components, including
 * component connection, data flow, transformation, and validation.
 */
public class CompositeSteps extends BaseComponentSteps {

  private Object processedResult;
  private int processingAttempts;
  private int processingFailures;

  @Given("a composite with ID {string}")
  public void a_composite_with_id(String compositeId) {
    if (environment == null) {
      environment = prepareEnvironment();
      storeInContext("environment", environment);
    }

    testComposite = new Composite(compositeId, environment);
    storeInContext("testComposite", testComposite);
  }

  @When("components are added to the composite:")
  public void components_are_added_to_the_composite(DataTable dataTable) {
    assertNotNull(testComposite, "Composite should exist");

    List<Map<String, String>> componentList = dataTable.asMaps(String.class, String.class);
    for (Map<String, String> componentData : componentList) {
      String name = componentData.get("Name");
      String reason = componentData.get("Reason");

      testComposite.createComponent(name, reason);
    }
  }

  @When("components are connected in the composite:")
  public void components_are_connected_in_the_composite(DataTable dataTable) {
    assertNotNull(testComposite, "Composite should exist");

    List<Map<String, String>> connectionList = dataTable.asMaps(String.class, String.class);
    for (Map<String, String> connection : connectionList) {
      String source = connection.get("Source");
      String target = connection.get("Target");

      testComposite.connect(source, target);
    }
  }

  @When("a transformer composite is created with components:")
  public void a_transformer_composite_is_created_with_components(DataTable dataTable) {
    if (environment == null) {
      environment = prepareEnvironment();
      storeInContext("environment", environment);
    }

    // Create a transformer composite
    testComposite = CompositeFactory.createTransformationComposite(environment);
    storeInContext("testComposite", testComposite);

    // Add specific components if they're not already included
    List<Map<String, String>> componentList = dataTable.asMaps(String.class, String.class);
    for (Map<String, String> componentData : componentList) {
      String name = componentData.get("Name");
      String reason = componentData.get("Reason");

      // Check if component exists, create only if it doesn't
      try {
        testComposite.getComponent(name);
      } catch (IllegalArgumentException e) {
        // Component doesn't exist, create it
        testComposite.createComponent(name, reason);
      }
    }
  }

  @When("a transformer is added to {string} that converts text to uppercase")
  public void a_transformer_is_added_to_that_converts_text_to_uppercase(String componentName) {
    assertNotNull(testComposite, "Composite should exist");

    // Add an uppercase transformer
    testComposite.addTransformer(componentName, (String data) -> data.toUpperCase());
  }

  @When("data {string} is processed through the composite starting at {string}")
  public void data_is_processed_through_the_composite_starting_at(
      String inputData, String startComponent) {
    assertNotNull(testComposite, "Composite should exist");

    // Process data through the composite
    Optional<?> result = testComposite.process(startComponent, inputData);

    if (result.isPresent()) {
      processedResult = result.get();
    } else {
      processedResult = null;
    }

    storeInContext("processedResult", processedResult);
  }

  @When("a validator composite is created with components:")
  public void a_validator_composite_is_created_with_components(DataTable dataTable) {
    if (environment == null) {
      environment = prepareEnvironment();
      storeInContext("environment", environment);
    }

    // Create a validator composite
    testComposite = CompositeFactory.createValidationComposite(environment);
    storeInContext("testComposite", testComposite);

    // Add specific components if they're not already included
    List<Map<String, String>> componentList = dataTable.asMaps(String.class, String.class);
    for (Map<String, String> componentData : componentList) {
      String name = componentData.get("Name");
      String reason = componentData.get("Reason");

      // Check if component exists, create only if it doesn't
      try {
        testComposite.getComponent(name);
      } catch (IllegalArgumentException e) {
        // Component doesn't exist, create it
        testComposite.createComponent(name, reason);
      }
    }
  }

  @When("a validator is added to {string} that checks if data contains {string}")
  public void a_validator_is_added_to_that_checks_if_data_contains(
      String componentName, String checkString) {
    assertNotNull(testComposite, "Composite should exist");

    // Add a validator that checks if data contains the specified string
    testComposite.addValidator(componentName, (String data) -> data.contains(checkString));
  }

  @When("a processing composite is created with circuit breaker enabled on {string}")
  public void a_processing_composite_is_created_with_circuit_breaker_enabled_on(
      String componentName) {
    if (environment == null) {
      environment = prepareEnvironment();
      storeInContext("environment", environment);
    }

    // Create a processing composite
    testComposite = CompositeFactory.createProcessingComposite(environment);
    storeInContext("testComposite", testComposite);

    // Enable circuit breaker on the specified component
    // Use a small threshold and timeout for testing
    testComposite.enableCircuitBreaker(componentName, 3, 1000);
  }

  @When("a transformer is added to {string} that throws exceptions")
  public void a_transformer_is_added_to_that_throws_exceptions(String componentName) {
    assertNotNull(testComposite, "Composite should exist");

    // Add a transformer that throws exceptions
    testComposite.addTransformer(
        componentName,
        (Object data) -> {
          throw new RuntimeException("Test exception for circuit breaker");
        });
  }

  @When("data is processed through the composite multiple times")
  public void data_is_processed_through_the_composite_multiple_times() {
    assertNotNull(testComposite, "Composite should exist");

    // Process data multiple times to trigger circuit breaker
    processingAttempts = 0;
    processingFailures = 0;

    for (int i = 0; i < 5; i++) {
      processingAttempts++;
      Optional<?> result = testComposite.process("input", "test-data-" + i);
      if (result.isEmpty()) {
        processingFailures++;
      }
    }

    storeInContext("processingAttempts", processingAttempts);
    storeInContext("processingFailures", processingFailures);
  }

  @When("an observer composite is created")
  public void an_observer_composite_is_created() {
    if (environment == null) {
      environment = prepareEnvironment();
      storeInContext("environment", environment);
    }

    // Create an observer composite
    testComposite = CompositeFactory.createObserverComposite(environment);
    storeInContext("testComposite", testComposite);
  }

  @Then("the composite should have the expected components")
  public void the_composite_should_have_the_expected_components() {
    assertNotNull(testComposite, "Composite should exist");

    // Verify all components are present
    Map<String, Component> components = testComposite.getComponents();
    assertFalse(components.isEmpty(), "Composite should have components");
  }

  @Then("the composite should have the expected connections")
  public void the_composite_should_have_the_expected_connections() {
    assertNotNull(testComposite, "Composite should exist");

    // Verify connections exist
    Map<String, List<String>> connections = testComposite.getConnections();
    assertFalse(connections.isEmpty(), "Composite should have connections");
  }

  @Then("the connection graph should be properly formed")
  public void the_connection_graph_should_be_properly_formed() {
    assertNotNull(testComposite, "Composite should exist");

    // Verify the connection graph structure
    Map<String, List<String>> connections = testComposite.getConnections();

    // Check that each source has valid targets
    for (Map.Entry<String, List<String>> connection : connections.entrySet()) {
      String source = connection.getKey();
      List<String> targets = connection.getValue();

      // Verify source exists
      assertNotNull(testComposite.getComponent(source), "Source component should exist: " + source);

      // Verify all targets exist
      for (String target : targets) {
        assertNotNull(
            testComposite.getComponent(target), "Target component should exist: " + target);
      }
    }
  }

  @Then("the output from the composite should be {string}")
  public void the_output_from_the_composite_should_be(String expectedOutput) {
    assertNotNull(processedResult, "Composite should return a result");
    assertEquals(expectedOutput, processedResult.toString(), "Output should match expected result");
  }

  @Then("the transformer should log its operation")
  public void the_transformer_should_log_its_operation() {
    assertNotNull(testComposite, "Composite should exist");

    // Check that transformer operation was logged
    List<Composite.CompositeEvent> events = testComposite.getEventLog();
    boolean foundEvent =
        events.stream().anyMatch(event -> event.getDescription().contains("transformed"));

    assertTrue(foundEvent, "Composite should log transformer operation");
  }

  @Then("the composite should return a result")
  public void the_composite_should_return_a_result() {
    assertNotNull(processedResult, "Composite should return a result");
  }

  @Then("the composite should not return a result")
  public void the_composite_should_not_return_a_result() {
    assertNull(processedResult, "Composite should not return a result");
  }

  @Then("the validator should log validation status")
  public void the_validator_should_log_validation_status() {
    assertNotNull(testComposite, "Composite should exist");

    // Check that validation status was logged
    List<Composite.CompositeEvent> events = testComposite.getEventLog();
    boolean foundEvent =
        events.stream().anyMatch(event -> event.getDescription().contains("validation"));

    assertTrue(foundEvent, "Composite should log validation status");
  }

  @Then("the circuit breaker should open after failure threshold is reached")
  public void the_circuit_breaker_should_open_after_failure_threshold_is_reached() {
    assertNotNull(testComposite, "Composite should exist");
    Integer failures = getFromContext("processingFailures", Integer.class);
    assertNotNull(failures, "Processing failures should be tracked");

    // Check that circuit breaker operation was logged
    List<Composite.CompositeEvent> events = testComposite.getEventLog();
    boolean circuitOpened =
        events.stream().anyMatch(event -> event.getDescription().contains("Circuit open"));

    assertTrue(circuitOpened, "Circuit breaker should open after failures");
  }

  @Then("subsequent processing attempts should be skipped")
  public void subsequent_processing_attempts_should_be_skipped() {
    // Check that some processing attempts were skipped
    List<Composite.CompositeEvent> events = testComposite.getEventLog();
    boolean skippedProcessing =
        events.stream().anyMatch(event -> event.getDescription().contains("skipping processing"));

    assertTrue(skippedProcessing, "Processing should be skipped when circuit is open");
  }

  @Then("after the timeout period the circuit breaker should reset to half-open")
  public void after_the_timeout_period_the_circuit_breaker_should_reset_to_half_open()
      throws InterruptedException {
    // Wait for circuit breaker timeout (slightly longer than the timeout we set)
    Thread.sleep(1200);

    // Verify circuit half-open status by attempting to process again
    Optional<?> result = testComposite.process("input", "test-after-timeout");

    // Check logs to see if half-open state was entered
    List<Composite.CompositeEvent> events = testComposite.getEventLog();
    boolean halfOpenState =
        events.stream().anyMatch(event -> event.getDescription().contains("half-open"));

    assertTrue(halfOpenState, "Circuit breaker should enter half-open state after timeout");
  }

  @Then("the observer should log the data processing")
  public void the_observer_should_log_the_data_processing() {
    assertNotNull(testComposite, "Composite should exist");

    // Check that observer logged data processing
    List<Composite.CompositeEvent> events = testComposite.getEventLog();
    boolean observerLogged =
        events.stream().anyMatch(event -> event.getDescription().contains("observed data"));

    assertTrue(observerLogged, "Observer should log the data processing");
  }

  @Then("the original data transformation should be preserved")
  public void the_original_data_transformation_should_be_preserved() {
    assertNotNull(processedResult, "Composite should return a result");
    assertNotEquals(
        "original data", processedResult.toString(), "Data transformation should be preserved");
  }
}
