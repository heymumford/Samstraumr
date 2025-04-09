# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

@L0_Unit @L0_Component @Functional @ATL @State @Lifecycle
Feature: Component Status and Lifecycle State Management
  As a framework developer
  I want to manage component states and status information
  So that I can track and control component lifecycle

  Background:
    Given the S8r framework is initialized

  @smoke
  Scenario: Check initial state of new component
    When I create an Adam component with reason "Status Test Component"
    Then the component should be in "ACTIVE" state
    And the component status should be "OPERATIONAL"
    And the component should have creation reason "Status Test Component"
    And the component should have creation timestamp set
    And the component should not have a termination reason
    And the component should not have a termination timestamp

  @State
  Scenario: Component state transitions through lifecycle
    Given an Adam component exists with reason "Lifecycle State Test"
    When I transition the component through the following states:
      | SUSPENDED   |
      | ACTIVE      |
      | MAINTENANCE |
      | ACTIVE      |
    Then each state transition should be recorded in the component log
    And the component state after all transitions should be "ACTIVE"
    And state change timestamps should be recorded correctly

  @State
  Scenario: Component state affects operation availability
    Given an Adam component exists with reason "Operation Availability Test"
    When I set the component state to "SUSPENDED"
    Then state-sensitive operations should be unavailable
    When I set the component state to "ACTIVE"
    Then state-sensitive operations should be available
    When I set the component state to "MAINTENANCE"
    Then maintenance operations should be available
    And standard operations should be restricted

  @State @DataFlow
  Scenario: Component status reflects data processing capability
    Given an Adam component exists with reason "Status Test"
    When I simulate data processing load at 50% capacity
    Then the component status should be "OPERATIONAL"
    When I simulate data processing load at 90% capacity
    Then the component status should be "DEGRADED"
    When I simulate data processing load at 100% capacity
    Then the component status should be "AT_CAPACITY"
    When I simulate data processing failure
    Then the component status should be "ERROR"
    When I clear the component error condition
    Then the component status should be "OPERATIONAL"

  @Monitoring @State
  Scenario: Component status history is maintained
    Given an Adam component exists with reason "Status History Test"
    When the component goes through the following status changes:
      | OPERATIONAL  |
      | DEGRADED     |
      | OPERATIONAL  |
      | AT_CAPACITY  |
      | ERROR        |
      | OPERATIONAL  |
    Then the component status history should contain 6 entries
    And the status change timestamps should be recorded correctly
    And the component's current status should be "OPERATIONAL"

  @State @ErrorHandling @BTL
  Scenario: Invalid state transitions are rejected
    Given an Adam component exists with reason "Invalid State Test"
    When I attempt to transition directly from "ACTIVE" to "TERMINATED"
    Then the transition should be rejected with exception
    And the component should remain in "ACTIVE" state
    When I terminate the component with reason "Proper termination"
    Then the component should be in "TERMINATED" state