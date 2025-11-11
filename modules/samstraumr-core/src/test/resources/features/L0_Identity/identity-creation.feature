# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

@L0_Unit @L0_Identity @Identity @Functional @ATL
Feature: Identity Creation
  As a framework developer
  I want to create and validate component identities
  So that components can be uniquely identified in the system

  Background:
    Given the S8r framework is initialized

  @smoke
  Scenario: Create an Adam component identity
    When I create an Adam component with reason "Identity Test"
    Then the component should have a valid identity
    And the identity should be an Adam identity
    And the identity should have a UUID format
    And the identity should have no parent reference
    And the identity should include a creation timestamp
    And the identity should match the pattern "/adam/{uuid}"

  Scenario: Create a child component identity
    Given an Adam component exists with reason "Parent for Identity Test"
    When I create a child component with parent and reason "Child Identity Test"
    Then the component should have a valid identity
    And the identity should be a child identity
    And the identity should have a UUID format
    And the identity should include the parent's identity as prefix
    And the identity should include a creation timestamp
    And the identity should match the pattern "{parent-identity}/child/{uuid}"

  Scenario: Create multiple levels of nested identities
    Given an Adam component exists with reason "Top Level Parent"
    And a child component exists with reason "Mid Level Parent"
    When I create a child of the mid-level component with reason "Bottom Level Child"
    Then the bottom component should have a valid identity
    And the identity should be a grandchild identity
    And the identity should contain both parent and grandparent references
    And the identity should match the pattern "{grandparent-identity}/child/{parent-uuid}/child/{uuid}"

  @BoundaryValue
  Scenario: Create component with a deep hierarchy of 10 levels
    When I create a component hierarchy 10 levels deep
    Then each component should have a valid identity
    And each component's identity should contain references to all its ancestors
    And the leaf component's identity should have 9 "/child/" sections
    And all UUIDs in the hierarchy should be valid

  @Performance
  Scenario: Create 1000 components with unique identities
    When I create 1000 Adam components
    Then all 1000 components should have unique identities
    And identity creation performance should be within acceptable limits
    And identities should be correctly structured
    And each identity should be valid

  @Resilience
  Scenario: Identity persistence across serialization
    When I create an Adam component with reason "Serialization Test"
    And I serialize and then deserialize the component identity
    Then the deserialized identity should match the original identity
    And I should be able to resolve a component with the deserialized identity