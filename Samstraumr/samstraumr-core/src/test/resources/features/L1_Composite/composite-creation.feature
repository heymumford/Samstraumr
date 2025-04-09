@L1_Component @L1_Composite @Functional @ATL @CompositeTest
Feature: Composite Component Creation
  As a system architect
  I want to create composite components
  So that I can organize and structure my components hierarchically

  Background:
    Given the Samstraumr system is initialized

  @smoke @Lifecycle
  Scenario: Create a basic composite with valid parameters
    When I create a composite component with reason "Basic Composite"
    Then the composite should be created successfully
    And the composite should have a valid identity
    And the composite should be in "ACTIVE" state
    And the composite should have no child components initially
    And the composite log should contain an entry for "creation"

  @Lifecycle
  Scenario: Create a composite with child components
    Given I have created the following components:
      | name          | reason         |
      | ChildComp1    | Test Child 1   |
      | ChildComp2    | Test Child 2   |
    When I create a composite component with reason "Parent Composite"
    And I add the components to the composite
    Then the composite should have 2 child components
    And all child components should be accessible through the composite
    
  @Lifecycle @Error
  Scenario: Attempt to create a composite with invalid parameters
    When I attempt to create a composite component with an empty reason
    Then the operation should fail with an exception
    And the exception message should contain "reason cannot be empty"

  @Lifecycle @Hierarchy
  Scenario: Create nested composite hierarchy
    Given I have created a composite component "Level1" with reason "Level 1 Composite"
    When I create a composite component "Level2" with reason "Level 2 Composite"
    And I add the "Level2" composite as a child of "Level1" composite
    Then the "Level1" composite should contain the "Level2" composite
    And the "Level2" composite should have "Level1" as its parent

  @Lifecycle @State
  Scenario: Verify composite state management
    Given I have created a composite component with reason "State Test Composite"
    When I change the composite state to "SUSPENDED"
    Then the composite should be in "SUSPENDED" state
    When I change the composite state to "ACTIVE"
    Then the composite should be in "ACTIVE" state