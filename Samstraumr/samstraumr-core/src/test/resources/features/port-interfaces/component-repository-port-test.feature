@L2_Integration @Functional @PortInterface @Component
Feature: Component Repository Port Interface
  As a system developer
  I want to use the ComponentRepository interface for component persistence
  So that I can keep my application core independent of specific storage implementations

  Background:
    Given a clean system environment
    And the ComponentRepository interface is properly initialized

  Scenario: Saving and retrieving a component
    When I create a new component with ID "comp-123" and name "Test Component"
    And I save the component to the repository
    Then the component should be successfully saved
    And I should be able to retrieve the component by its ID
    And the retrieved component should have the correct ID and name

  Scenario: Retrieving a non-existent component
    When I try to find a component with ID "non-existent"
    Then the result should be empty

  Scenario: Retrieving all components
    Given I have saved the following components:
      | id        | name            |
      | comp-001  | Component 1     |
      | comp-002  | Component 2     |
      | comp-003  | Component 3     |
    When I request all components
    Then I should get 3 components
    And the components should include all saved components

  Scenario: Finding child components
    Given I have saved the following component hierarchy:
      | id        | name          | parent_id    |
      | comp-001  | Parent        |              |
      | comp-002  | Child 1       | comp-001     |
      | comp-003  | Child 2       | comp-001     |
      | comp-004  | Grandchild    | comp-002     |
      | comp-005  | Unrelated     |              |
    When I search for children of component "comp-001"
    Then I should get 2 components
    And the components should include "comp-002" and "comp-003"
    When I search for children of component "comp-002"
    Then I should get 1 component
    And the components should include "comp-004"

  Scenario: Deleting a component
    Given I have saved a component with ID "comp-123" and name "Test Component"
    When I delete the component with ID "comp-123"
    Then the component should no longer exist in the repository
    And searching for the deleted component should return empty