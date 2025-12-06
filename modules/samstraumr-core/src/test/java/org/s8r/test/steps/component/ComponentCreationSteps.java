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
package org.s8r.test.steps.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.s8r.component.Component;
import org.s8r.component.Environment;
import org.s8r.component.State;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/** Step definitions for component creation feature tests. */
public class ComponentCreationSteps {

  // Test context to share between steps
  private Component adamComponent;
  private Component childComponent;
  private List<Component> components = new ArrayList<>();
  private Exception lastException;
  private static final int MAX_REASON_LENGTH = 255; // Assumed max length

  @Given("the S8r framework is initialized")
  public void the_s8r_framework_is_initialized() {
    // This step could involve initializing any framework services,
    // or it might be a no-op if the framework initializes on demand
    // For now, we'll simply make sure state is cleared
    adamComponent = null;
    childComponent = null;
    components.clear();
    lastException = null;
  }

  @Given("an Adam component exists with reason {string}")
  public void an_adam_component_exists_with_reason(String reason) {
    try {
      // Create an Adam component
      adamComponent = Component.createAdam(reason);
      components.add(adamComponent);

      // Verify creation
      Assertions.assertNotNull(adamComponent, "Adam component should be created");
      Assertions.assertEquals(
          reason, adamComponent.getCreationReason(), "Creation reason should match");
    } catch (Exception e) {
      lastException = e;
      throw e; // Rethrow to fail the test
    }
  }

  @When("I create an Adam component with reason {string}")
  public void i_create_an_adam_component_with_reason(String reason) {
    try {
      // Create an Adam component
      adamComponent = Component.createAdam(reason);
      components.add(adamComponent);
    } catch (Exception e) {
      lastException = e;
    }
  }

  @When("I create a child component with parent and reason {string}")
  public void i_create_a_child_component_with_parent_and_reason(String reason) {
    try {
      // Create a child component
      Assertions.assertNotNull(adamComponent, "Parent component must exist");
      childComponent = Component.createChild(adamComponent, reason);
      components.add(childComponent);
    } catch (Exception e) {
      lastException = e;
    }
  }

  @When("I create {int} child components from the same parent")
  public void i_create_n_child_components_from_the_same_parent(int count) {
    try {
      Assertions.assertNotNull(adamComponent, "Parent component must exist");

      for (int i = 0; i < count; i++) {
        Component child = Component.createChild(adamComponent, "Child " + (i + 1));
        components.add(child);
      }
    } catch (Exception e) {
      lastException = e;
    }
  }

  @When("I create an Adam component with reason of maximum allowed length")
  public void i_create_an_adam_component_with_maximum_reason_length() {
    try {
      // Create a reason string of max length
      StringBuilder reason = new StringBuilder();
      for (int i = 0; i < MAX_REASON_LENGTH; i++) {
        reason.append('X');
      }
      adamComponent = Component.createAdam(reason.toString());
      components.add(adamComponent);
    } catch (Exception e) {
      lastException = e;
    }
  }

  @When("I create an Adam component with the following environment:")
  public void i_create_an_adam_component_with_the_following_environment(DataTable dataTable) {
    try {
      // Convert DataTable to environment values
      List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
      Map<String, String> envValues = new HashMap<>();

      for (Map<String, String> row : rows) {
        envValues.put(row.get("key"), row.get("value"));
      }

      // Create component with environment
      Environment env = new Environment(envValues);
      adamComponent = Component.createAdam("Custom Environment Test", env);
      components.add(adamComponent);
    } catch (Exception e) {
      lastException = e;
    }
  }

  @When("I attempt to create an Adam component with null reason")
  public void i_attempt_to_create_an_adam_component_with_null_reason() {
    try {
      adamComponent = Component.createAdam(null);
      components.add(adamComponent);
    } catch (Exception e) {
      lastException = e;
    }
  }

  @When("I attempt to create a child component with null parent")
  public void i_attempt_to_create_a_child_component_with_null_parent() {
    try {
      childComponent = Component.createChild(null, "Child with null parent");
      components.add(childComponent);
    } catch (Exception e) {
      lastException = e;
    }
  }

  @Then("the component should be created successfully")
  public void the_component_should_be_created_successfully() {
    Component component = adamComponent != null ? adamComponent : childComponent;
    Assertions.assertNotNull(component, "Component should be created");
    Assertions.assertNull(lastException, "No exception should be thrown");
  }

  @Then("the component should have a valid identity")
  public void the_component_should_have_a_valid_identity() {
    Component component = adamComponent != null ? adamComponent : childComponent;
    Assertions.assertNotNull(component, "Component should be created");
    Assertions.assertNotNull(component.getIdentity(), "Component should have identity");

    // Further validation could check UUID format, etc.
    String identity = component.getIdentity().toString();
    Assertions.assertTrue(
        identity.contains("adam/") || identity.contains("/child/"),
        "Identity should have proper format");
  }

  @Then("the component should be in {string} state")
  public void the_component_should_be_in_state(String stateName) {
    Component component = adamComponent != null ? adamComponent : childComponent;
    Assertions.assertNotNull(component, "Component should be created");

    State expectedState = State.valueOf(stateName);
    Assertions.assertEquals(
        expectedState, component.getState(), "Component should be in " + stateName + " state");
  }

  @Then("the component log should contain an entry for {string}")
  public void the_component_log_should_contain_an_entry_for(String eventType) {
    Component component = adamComponent != null ? adamComponent : childComponent;
    Assertions.assertNotNull(component, "Component should be created");

    // This assumes there's a method to access log entries
    // Implementation would depend on actual logging system
    boolean hasEntry = component.getLog().stream().anyMatch(entry -> entry.contains(eventType));

    Assertions.assertTrue(hasEntry, "Log should contain entry for " + eventType);
  }

  @Then("the component's parent should be the Adam component")
  public void the_components_parent_should_be_the_adam_component() {
    Assertions.assertNotNull(childComponent, "Child component should be created");
    Assertions.assertNotNull(adamComponent, "Parent component should be created");

    Assertions.assertEquals(
        adamComponent.getIdentity(),
        childComponent.getParentIdentity(),
        "Child's parent identity should match Adam component's identity");
  }

  @Then("all {int} components should be created successfully")
  public void all_n_components_should_be_created_successfully(int count) {
    Assertions.assertEquals(
        count + 1, components.size(), "Expected parent + " + count + " children");

    for (Component component : components) {
      Assertions.assertNotNull(
          component.getIdentity(), "Each component should have valid identity");
    }
  }

  @Then("all {int} components should have the same parent")
  public void all_n_components_should_have_the_same_parent(int count) {
    // Skip the first component (parent)
    for (int i = 1; i <= count; i++) {
      Component child = components.get(i);
      Assertions.assertEquals(
          adamComponent.getIdentity(),
          child.getParentIdentity(),
          "Child component should have parent identity matching Adam component");
    }
  }

  @Then("all {int} components should have unique identities")
  public void all_n_components_should_have_unique_identities(int count) {
    // Map to store identities for uniqueness check
    Map<String, Boolean> identities = new HashMap<>();

    // Skip the first component (parent) in the count
    for (int i = 1; i <= count; i++) {
      Component child = components.get(i);
      String identity = child.getIdentity().toString();

      Assertions.assertFalse(
          identities.containsKey(identity), "Component identities should be unique");

      identities.put(identity, true);
    }
  }

  @Then("all {int} components should be in {string} state")
  public void all_n_components_should_be_in_state(int count, String stateName) {
    State expectedState = State.valueOf(stateName);

    // Skip the first component (parent) in the count
    for (int i = 1; i <= count; i++) {
      Component child = components.get(i);
      Assertions.assertEquals(
          expectedState, child.getState(), "Child component should be in " + stateName + " state");
    }
  }

  @Then("the component environment should contain key {string} with value {string}")
  public void the_component_environment_should_contain_key_with_value(String key, String value) {
    Component component = adamComponent != null ? adamComponent : childComponent;
    Assertions.assertNotNull(component, "Component should be created");

    Environment env = component.getEnvironment();
    Assertions.assertNotNull(env, "Environment should not be null");

    String actualValue = env.getValue(key);
    Assertions.assertEquals(
        value, actualValue, "Environment should contain key " + key + " with value " + value);
  }

  @Then("component creation should fail with exception containing {string}")
  public void component_creation_should_fail_with_exception_containing(String errorText) {
    Assertions.assertNotNull(lastException, "Exception should be thrown");
    Assertions.assertTrue(
        lastException.getMessage().contains(errorText),
        "Exception message should contain: " + errorText);
  }

  @Then("the exception should be of type {string}")
  public void the_exception_should_be_of_type(String exceptionType) {
    Assertions.assertNotNull(lastException, "Exception should be thrown");
    String actualType = lastException.getClass().getSimpleName();
    Assertions.assertEquals(
        exceptionType, actualType, "Exception should be of type " + exceptionType);
  }

  @Then("no component resources should be allocated")
  public void no_component_resources_should_be_allocated() {
    // This step would verify that no resources were allocated
    // Implementation depends on how resources are tracked
    // For now, we'll just assert that components weren't created
    if (lastException != null) {
      Assertions.assertNull(adamComponent, "Adam component should not be created on error");
      Assertions.assertNull(childComponent, "Child component should not be created on error");
    }
  }
}
