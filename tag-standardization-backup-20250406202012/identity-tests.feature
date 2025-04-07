/*
Filename: identity-tests.feature
Purpose: Validates the identity system for Components
Goals:
  - Verify identity creation for Adam (root) and child components
  - Test hierarchical addressing and query functionality
  - Validate lineage tracking with multiple generations
Dependencies:
  - BDD step definitions in org.s8r.component.test.steps package
  - Component and Identity implementations in org.s8r.component.identity
*/

@L0_Unit @Functional @Identity
Feature: Component Identity Management
  Components should establish and maintain unique identities with proper
  hierarchical relationships and lineage tracking.

@Functional  @L0_Core @Identity @Adam
  Scenario: Adam component identity creation
    Given a basic environment for component initialization
    When an Adam component is created with reason "Adam Identity Test"
    Then the component should have an Adam identity
    And the identity should have a unique ID
    And the identity should have the reason "Adam Identity Test"
    And the identity should have no parent
    And the identity should be in the root hierarchy

@Functional  @L0_Core @Identity @Child
  Scenario: Child component identity creation
    Given a basic environment for component initialization
    When an Adam component is created with reason "Parent Component"
    And a child component is created from the parent with reason "Child Component"
    Then the child component should have a valid identity
    And the child identity should reference the parent identity
    And the child identity should follow hierarchical naming convention
    And the child identity should include the parent in its lineage

@Functional  @L0_Core @Identity @Hierarchy
  Scenario: Multi-level hierarchy creation
    Given a basic environment for component initialization
    When a 3-level component hierarchy is created
    Then each level should properly reference its parent
    And the top level should have all descendants in its hierarchy
    And each descendant should have the correct ancestor chain
    And the hierarchical addressing should reflect the tree structure

@Functional  @L0_Core @Identity @Lineage
  Scenario: Identity lineage tracking
    Given a basic environment for component initialization
    When a component genealogy of 5 generations is created
    Then each component should have a complete lineage
    And the lineage should contain all ancestor reasons in order
    And the root component should be at the start of each lineage
    And each generation should add exactly one entry to the lineage

@Functional  @L0_Core @Identity @Query
  Scenario: Identity query operations
    Given a basic environment for component initialization
    When a complex component hierarchy is created with 10 nodes
    Then it should be possible to find any component by its unique ID
    And it should be possible to find all descendants of any component
    And it should be possible to find all siblings of any component
    And the ancestral relationship queries should work correctly