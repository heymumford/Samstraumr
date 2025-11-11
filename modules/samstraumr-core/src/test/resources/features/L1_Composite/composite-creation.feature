# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

@L1_Component @L1_Composite @Functional @ATL @CompositeTest
Feature: Composite Component Creation
  As a framework developer
  I want to create composite components with proper initialization
  So that I can build hierarchical component structures

  Background:
    Given the S8r framework is initialized

  @smoke @Lifecycle
  Scenario: Create a basic composite with valid parameters
    When I create a composite component with reason "Basic Composite"
    Then the composite should be created successfully
    And the composite should have a valid identity
    And the composite should be in "ACTIVE" state
    And the composite should have no child components initially
    And the composite log should contain an entry for "creation"

  Scenario: Create a composite with child components
    Given a composite component exists with reason "Parent Composite"
    When I add 3 child components to the composite
    Then the composite should contain exactly 3 child components
    And each child component should have the composite as its parent
    And all child components should have unique identities
    And all child components should be accessible via the composite

  Scenario: Create a nested composite hierarchy
    Given a composite component exists with reason "Root Composite"
    When I add a sub-composite with 2 components
    And I add another sub-composite with 3 components
    Then the root composite should contain 2 sub-composites
    And the first sub-composite should contain 2 components
    And the second sub-composite should contain 3 components
    And all components should have proper parent-child relationships
    And the composite hierarchy should be navigable in both directions

  Scenario: Create a composite with initial configuration
    When I create a composite with the following configuration:
      | property        | value         |
      | maxComponents   | 10            |
      | autoTerminate   | true          |
      | stateSync       | bidirectional |
      | logLevel        | detailed      |
    Then the composite should be created successfully
    And the composite should have the specified configuration values
    And the composite behavior should respect these configuration settings

  Scenario: Create composite with different component types
    When I create a composite with the following component types:
      | componentType   | count |
      | Observer        | 2     |
      | Transformer     | 1     |
      | Validator       | 3     |
    Then the composite should contain all specified components
    And each component should be of the correct type
    And the composite should manage type-specific behavior appropriately

  @ErrorHandling @BTL
  Scenario: Attempt to create composite with invalid parameters
    When I attempt to create a composite with null reason
    Then composite creation should fail with exception containing "reason cannot be null"
    When I attempt to create a composite with empty reason ""
    Then composite creation should fail with exception containing "reason cannot be empty"
    When I attempt to create a composite with maximum child capacity of -1
    Then composite creation should fail with exception containing "capacity must be positive"

  @ErrorHandling @BTL
  Scenario: Attempt to add too many child components
    Given a composite component exists with maximum capacity of 3
    When I add 3 child components to the composite
    And I attempt to add another child component
    Then the addition should fail with exception containing "maximum capacity reached"
    And the composite should still contain exactly 3 child components
    And the rejected component should not be partially added