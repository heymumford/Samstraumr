# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

@L0_Unit @L0_Component @Functional @ATL @Identity
Feature: Component Creation
  As a framework developer
  I want to create components with proper initialization
  So that I can ensure the component lifecycle starts correctly

  Background:
    Given the S8r framework is initialized

  @smoke @Lifecycle
  Scenario: Create an Adam component with valid parameters
    When I create an Adam component with reason "Initial Test Component"
    Then the component should be created successfully
    And the component should have a valid identity
    And the component should be in "ACTIVE" state
    And the component log should contain an entry for "creation"

  @Lifecycle
  Scenario: Create a child component with valid parent
    Given an Adam component exists with reason "Parent Component"
    When I create a child component with parent and reason "Child Component"
    Then the component should be created successfully
    And the component should have a valid identity
    And the component's parent should be the Adam component
    And the component should be in "ACTIVE" state
    And the component log should contain an entry for "creation"

  @Lifecycle @BoundaryValue
  Scenario: Create component with maximum reason length
    When I create an Adam component with reason of maximum allowed length
    Then the component should be created successfully
    And the component should be in "ACTIVE" state

  @Lifecycle
  Scenario: Create multiple child components from same parent
    Given an Adam component exists with reason "Multi-Child Parent"
    When I create 5 child components from the same parent
    Then all 5 components should be created successfully
    And all 5 components should have the same parent
    And all 5 components should have unique identities
    And all 5 components should be in "ACTIVE" state

  @Lifecycle @BoundaryValue
  Scenario: Create component with custom environment values
    When I create an Adam component with the following environment:
      | key           | value       |
      | timeout       | 30000       |
      | retryCount    | 3           |
      | logLevel      | DEBUG       |
      | maxBufferSize | 8192        |
    Then the component should be created successfully
    And the component environment should contain key "timeout" with value "30000"
    And the component environment should contain key "retryCount" with value "3"
    And the component environment should contain key "logLevel" with value "DEBUG"
    And the component environment should contain key "maxBufferSize" with value "8192"

  # Negative Paths (some overlapping with exception tests but important for creation completeness)
  @ErrorHandling @BTL
  Scenario: Attempt to create component with null reason
    When I attempt to create an Adam component with null reason
    Then component creation should fail with exception containing "reason cannot be null"

  @ErrorHandling @BTL
  Scenario: Attempt to create child component with null parent
    When I attempt to create a child component with null parent
    Then component creation should fail with exception containing "parent cannot be null"