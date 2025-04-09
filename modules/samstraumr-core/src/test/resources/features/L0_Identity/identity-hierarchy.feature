# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

@L0_Unit @L0_Identity @Identity @Functional @ATL
Feature: Identity Hierarchy Management
  As a framework developer
  I want to manage hierarchical relationships between component identities
  So that I can track relationships and ownership within the system

  Background:
    Given the S8r framework is initialized

  @smoke
  Scenario: Extract parent identity from child identity
    Given an Adam component exists with reason "Hierarchy Parent"
    And a child component exists with reason "Hierarchy Child"
    When I extract the parent identity from the child
    Then the extracted parent identity should match the original parent identity
    And using the extracted parent identity should resolve to the parent component

  Scenario: Determine depth in identity hierarchy
    Given an Adam component exists with identity depth 0
    And a child component exists with identity depth 1
    And a grandchild component exists with identity depth 2
    And a great-grandchild component exists with identity depth 3
    When I check the hierarchy depth of each component identity
    Then the Adam component should have depth 0
    And the child component should have depth 1
    And the grandchild component should have depth 2
    And the great-grandchild component should have depth 3

  Scenario: Find common ancestor in component hierarchy
    Given two components with a common ancestor
    When I find the common ancestor identity
    Then the common ancestor identity should be correct
    And the common ancestor should be resolvable to a valid component

  Scenario: Find all descendants of a component
    Given an Adam component exists with reason "Descendant Test Parent"
    And the component has 3 direct children
    And each child has 2 of its own children
    When I find all descendants of the Adam component
    Then I should find 9 total descendants (3 children + 6 grandchildren)
    And each descendant should have the Adam component in its ancestry chain

  Scenario: Find siblings of a component
    Given an Adam component exists with reason "Sibling Test Parent"
    And the parent has 5 children
    When I find siblings of the middle child
    Then I should find 4 siblings
    And all siblings should share the same parent identity

  @ErrorHandling @BTL
  Scenario: Handle invalid parent-child relationships
    When I attempt to create a child component with an invalid parent identity
    Then the creation should fail with exception containing "invalid parent identity"
    And the exception should be of type "ComponentInitializationException"

  @BoundaryValue
  Scenario: Handle maximum allowed hierarchy depth
    When I create a component hierarchy at the maximum allowed depth
    Then the creation should succeed
    And I should be able to traverse from leaf to root
    When I attempt to add a child to the leaf component
    Then the creation should fail with exception containing "maximum hierarchy depth exceeded"