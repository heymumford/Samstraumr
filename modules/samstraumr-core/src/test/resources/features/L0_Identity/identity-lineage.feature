# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

@L0_Unit @L0_Identity @Identity @Functional @ATL
Feature: Identity Lineage Tracking
  As a framework developer
  I want to track the lineage of component identities
  So that I can understand the full ancestry and creation history of components

  Background:
    Given the S8r framework is initialized

  @smoke
  Scenario: Track full ancestry path from leaf to root
    Given a component hierarchy with 5 levels of depth
    When I request the full ancestry path of the leaf component
    Then the ancestry path should contain 5 identities in order from root to leaf
    And each identity in the path should be valid
    And each identity should have the correct relation to its parent
    And the root should be an Adam identity

  Scenario: Record creation lineage with timestamps
    Given an Adam component created at a specific time
    And a child component created at a later time
    And a grandchild component created at a further later time
    When I retrieve the creation lineage of the grandchild
    Then the lineage should contain 3 timestamps in chronological order
    And each timestamp should match the creation time of its component
    And the lineage should include creation reasons for each component

  Scenario: Determine component age and generation
    Given an Adam component created at time T
    And a child component created at time T+10 seconds
    And a grandchild component created at time T+20 seconds
    When I calculate the age and generation of each component
    Then the Adam component should be generation 0 with the oldest age
    And the child component should be generation 1 with the middle age
    And the grandchild component should be generation 2 with the youngest age

  Scenario: Track identity across architectural boundaries
    Given an Adam component in the domain layer
    When I create adapter components in the following layers:
      | application    |
      | infrastructure |
      | presentation   |
    Then each adapter component should maintain lineage to the domain component
    And the full lineage should be traceable across architectural boundaries
    And the ancestry should be properly maintained in each adapter

  Scenario: Handle branching lineage patterns
    Given an Adam component as the common ancestor
    And two separate branches of descendants (Branch A and Branch B)
    When I compare the lineage of a leaf from Branch A with a leaf from Branch B
    Then both lineages should share the common ancestor
    And the lineages should diverge at the correct branch point
    And each branch should have its own separate path

  @Resilience
  Scenario: Maintain lineage information after component reconstruction
    Given a component hierarchy with 3 levels
    When the system is shut down and restarted
    And components are reconstructed from persistent storage
    Then all lineage information should be preserved
    And parent-child relationships should be maintained
    And creation timestamps should be preserved
    And ancestry paths should be identical to before the restart