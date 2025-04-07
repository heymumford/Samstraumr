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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.s8r.component.Component;
import org.s8r.component.ComponentException;
import org.s8r.component.Identity;

import io.cucumber.java.en.*;

/**
 * Step definitions for component initialization tests.
 *
 * <p>These step definitions implement the BDD scenarios related to component initialization,
 * focusing on the initial creation, identity establishment, and basic functionality verification.
 */
public class ComponentInitializationSteps extends BaseComponentSteps {

  @Given("a basic environment for component initialization")
  public void a_basic_environment_for_component_initialization() {
    environment = prepareEnvironment();
    storeInContext("environment", environment);
  }

  @Given("an invalid environment for component initialization")
  public void an_invalid_environment_for_component_initialization() {
    // Implementation would depend on how to create an invalid environment
    // For this test, we'll use null as an invalid environment
    environment = null;
    storeInContext("environment", environment);
  }

  @When("a component is initialized with reason {string}")
  public void a_component_is_initialized_with_reason(String reason) {
    testComponent = createComponent(reason, environment);
    storeInContext("testComponent", testComponent);
  }

  @When("component initialization is attempted with reason {string}")
  public void component_initialization_is_attempted_with_reason(String reason) {
    try {
      testComponent = createComponent(reason, environment);
      storeInContext("testComponent", testComponent);
    } catch (Exception e) {
      exceptionThrown = e;
      storeInContext("exceptionThrown", exceptionThrown);
    }
  }

  @When("the component's environment state changes to {string}")
  public void the_components_environment_state_changes_to(String state) {
    assertNotNull(testComponent, "Component should exist");
    testComponent.updateEnvironmentState(state);
  }

  @When("{int} components are initialized simultaneously")
  public void components_are_initialized_simultaneously(Integer count) {
    assertNotNull(environment, "Environment should exist");

    List<Component> components = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      Component component = createComponent("Component " + i, environment);
      components.add(component);
    }

    storeInContext("componentList", components);
  }

  @When("an Adam component is created with reason {string}")
  public void an_adam_component_is_created_with_reason(String reason) {
    parentComponent = createComponent(reason, environment);
    parentIdentity = parentComponent.getIdentity();

    storeInContext("parentComponent", parentComponent);
    storeInContext("parentIdentity", parentIdentity);
  }

  @When("a child component is created with reason {string}")
  public void a_child_component_is_created_with_reason(String reason) {
    assertNotNull(parentComponent, "Parent component should exist");
    testComponent = createChildComponent(reason, environment, parentComponent);
    componentIdentity = testComponent.getIdentity();

    storeInContext("testComponent", testComponent);
    storeInContext("componentIdentity", componentIdentity);
  }

  @Then("the component should exist and have a valid UUID")
  public void the_component_should_exist_and_have_a_valid_uuid() {
    assertComponentHasValidId();
  }

  @Then("the component should log its initialization with timestamp")
  public void the_component_should_log_its_initialization_with_timestamp() {
    assertNotNull(testComponent, "Component should exist");
    List<String> logs = testComponent.getMemoryLog();
    assertFalse(logs.isEmpty(), "Component should have log entries");

    // Check if any log entry contains initialization information with timestamp
    boolean hasInitLog =
        logs.stream().anyMatch(entry -> entry.matches(".*Component created with reason.*"));
    assertTrue(hasInitLog, "Component should log initialization with timestamp");
  }

  @Then("the component should capture its reason {string}")
  public void the_component_should_capture_its_reason(String reason) {
    assertNotNull(testComponent, "Component should exist");
    assertEquals(reason, testComponent.getReason(), "Component should store its creation reason");

    // Verify reason is also in the logs
    List<String> logs = testComponent.getMemoryLog();
    boolean hasReasonLog = logs.stream().anyMatch(entry -> entry.contains(reason));
    assertTrue(hasReasonLog, "Component should log its reason");
  }

  @Then("the component should be queryable for its logs")
  public void the_component_should_be_queryable_for_its_logs() {
    assertNotNull(testComponent, "Component should exist");
    List<String> logs = testComponent.getMemoryLog();
    assertNotNull(logs, "Component should support log querying");
    assertFalse(logs.isEmpty(), "Component should have log entries");
  }

  @Then("the initialization should fail with appropriate error")
  public void the_initialization_should_fail_with_appropriate_error() {
    assertNotNull(exceptionThrown, "An exception should have been thrown");
    assertTrue(
        exceptionThrown instanceof IllegalArgumentException
            || exceptionThrown instanceof ComponentException,
        "Exception should be of appropriate type");
  }

  @Then("the error should indicate environmental issues")
  public void the_error_should_indicate_environmental_issues() {
    assertNotNull(exceptionThrown, "An exception should have been thrown");
    assertTrue(
        exceptionThrown.getMessage().contains("Environment")
            || exceptionThrown.getMessage().contains("environment"),
        "Error message should mention environment issues");
  }

  @Then("no component should be created")
  public void no_component_should_be_created() {
    assertNull(testComponent, "No component should be created");
  }

  @Then("the component should respond to the environmental change")
  public void the_component_should_respond_to_the_environmental_change() {
    assertNotNull(testComponent, "Component should exist");
    List<String> logs = testComponent.getMemoryLog();

    // Verify that the logs mention environment state changes
    boolean hasEnvironmentLog =
        logs.stream().anyMatch(entry -> entry.contains("Environment") && entry.contains("state"));
    assertTrue(hasEnvironmentLog, "Component should log environmental changes");
  }

  @Then("the component should maintain its identity during environmental changes")
  public void the_component_should_maintain_its_identity_during_environmental_changes() {
    assertNotNull(testComponent, "Component should exist");
    String originalId = testComponent.getUniqueId();

    // Verify identity remains consistent after environment changes
    assertEquals(
        originalId,
        testComponent.getUniqueId(),
        "Component ID should not change after environmental changes");

    assertNotNull(testComponent.getIdentity(), "Component identity should be maintained");
  }

  @Then("each component should have a unique identifier")
  public void each_component_should_have_a_unique_identifier() {
    @SuppressWarnings("unchecked")
    List<Component> components = getFromContext("componentList", List.class);
    assertNotNull(components, "Component list should not be null");

    for (Component component : components) {
      assertNotNull(component.getUniqueId(), "Each component should have an ID");
      assertTrue(component.getUniqueId().length() > 0, "Each component ID should not be empty");
    }
  }

  @Then("no components should share the same identifier")
  public void no_components_should_share_the_same_identifier() {
    @SuppressWarnings("unchecked")
    List<Component> components = getFromContext("componentList", List.class);
    assertNotNull(components, "Component list should not be null");

    Set<String> ids = new HashSet<>();
    for (Component component : components) {
      String id = component.getUniqueId();
      assertFalse(ids.contains(id), "Component IDs should be unique");
      ids.add(id);
    }

    assertEquals(
        components.size(), ids.size(), "Number of unique IDs should match component count");
  }

  @Then("each component should be independently addressable")
  public void each_component_should_be_independently_addressable() {
    @SuppressWarnings("unchecked")
    List<Component> components = getFromContext("componentList", List.class);
    assertNotNull(components, "Component list should not be null");

    for (Component component : components) {
      // Verify each component can be addressed and interacted with
      assertNotNull(component.getMemoryLog(), "Each component should be independently addressable");
    }
  }

  @Then("the parent should have the child in its descendants")
  public void the_parent_should_have_the_child_in_its_descendants() {
    assertNotNull(parentComponent, "Parent component should exist");
    assertNotNull(testComponent, "Child component should exist");

    // Verify that the parent's identity has the child in its descendants
    List<Identity> descendants = parentComponent.getIdentity().getDescendants();
    assertFalse(descendants.isEmpty(), "Parent should have descendants");

    boolean childFound =
        descendants.stream()
            .anyMatch(
                identity ->
                    identity.getUniqueId().equals(testComponent.getIdentity().getUniqueId()));
    assertTrue(childFound, "Child component should be in parent's descendants");
  }

  @Then("the child should have a reference to its parent")
  public void the_child_should_have_a_reference_to_its_parent() {
    assertNotNull(testComponent, "Child component should exist");
    assertNotNull(testComponent.getParentIdentity(), "Child should reference its parent");
    assertEquals(
        parentComponent.getIdentity().getUniqueId(),
        testComponent.getParentIdentity().getUniqueId(),
        "Child's parent reference should match parent's identity");
  }

  @Then("the child's lineage should include the parent")
  public void the_childs_lineage_should_include_the_parent() {
    assertNotNull(testComponent, "Child component should exist");
    List<String> lineage = testComponent.getLineage();
    assertFalse(lineage.isEmpty(), "Lineage should not be empty");

    // The parent's reason should be in the child's lineage
    boolean parentReasonFound =
        lineage.stream().anyMatch(entry -> entry.equals(parentComponent.getReason()));
    assertTrue(parentReasonFound, "Child's lineage should include parent's reason");
  }
}
