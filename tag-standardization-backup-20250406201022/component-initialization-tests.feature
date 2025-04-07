/*
Filename: component-initialization-tests.feature
Purpose: Validates the initialization and early lifecycle stages of Components
Goals:
  - Verify basic component creation and initialization
  - Test initialization error handling
  - Validate environment awareness during initialization
Dependencies:
  - BDD step definitions in org.s8r.component.test.steps package
  - Component and Environment implementations in org.s8r.component.core
*/

@ATL @L0_Core @Lifecycle @Init
Feature: Component Initialization and Early Lifecycle
  Components should properly initialize, establish identity, and begin their lifecycle
  following the biological development model, focusing on the early embryonic stages
  of creation (fertilization) and initialization (cleavage).

  @ATL @L0_Core @Lifecycle @Init @Creation
  Scenario: Basic component initialization with reason
    Given a basic environment for component initialization
    When a component is initialized with reason "Basic Initialization Test"
    Then the component should exist and have a valid UUID
    And the component should log its initialization with timestamp
    And the component should capture its reason "Basic Initialization Test"
    And the component should be queryable for its logs

  @ATL @L0_Core @Lifecycle @Init @Error
  Scenario: Component initialization with invalid environment
    Given an invalid environment for component initialization
    When component initialization is attempted with reason "Invalid Environment Test"
    Then the initialization should fail with appropriate error
    And the error should indicate environmental issues
    And no component should be created

  @ATL @L0_Core @Lifecycle @Init @Environment
  Scenario: Component environment awareness during initialization
    Given a basic environment for component initialization
    When a component is initialized with reason "Environment Awareness Test"
    And the component's environment state changes to "test condition"
    Then the component should respond to the environmental change
    And the component should maintain its identity during environmental changes

  @ATL @L0_Core @Lifecycle @Init @MultiComponent
  Scenario: Multiple components with unique identities
    Given a basic environment for component initialization
    When 10 components are initialized simultaneously
    Then each component should have a unique identifier
    And no components should share the same identifier
    And each component should be independently addressable

  @ATL @L0_Core @Lifecycle @Init @Hierarchy
  Scenario: Parent-child component relationship
    Given a basic environment for component initialization
    When an Adam component is created with reason "Parent Component"
    And a child component is created with reason "Child Component"
    Then the parent should have the child in its descendants
    And the child should have a reference to its parent
    And the child's lineage should include the parent