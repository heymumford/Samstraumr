# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

@L0_Unit @L0_Lifecycle @Lifecycle @Functional @ATL
Feature: Component Lifecycle State Transitions
  As a framework developer
  I want to manage component lifecycle state transitions
  So that components progress through their lifecycle in a controlled manner

  Background:
    Given the S8r framework is initialized

  @smoke
  Scenario: Component progresses through normal lifecycle states
    When I create a component with reason "Lifecycle Test"
    Then the component should be in "ACTIVE" state
    When I transition the component to "SUSPENDED" state
    Then the component should be in "SUSPENDED" state
    When I transition the component to "ACTIVE" state
    Then the component should be in "ACTIVE" state
    When I transition the component to "MAINTENANCE" state
    Then the component should be in "MAINTENANCE" state
    When I transition the component to "ACTIVE" state
    Then the component should be in "ACTIVE" state
    When I terminate the component with reason "Normal termination"
    Then the component should be in "TERMINATED" state

  Scenario: Component lifecycle includes initialization phase
    When I create a component with tracking initialization phases
    Then the component should progress through the following states:
      | INITIALIZING |
      | VALIDATING   |
      | CONFIGURING  |
      | CONNECTING   |
      | STARTING     |
      | ACTIVE       |
    And each state transition should be logged
    And initialization metrics should be recorded

  Scenario: Component lifecycle includes termination phase
    Given a component in "ACTIVE" state
    When I trigger graceful termination
    Then the component should progress through the following states:
      | TERMINATING    |
      | DISCONNECTING  |
      | RELEASING      |
      | TERMINATED     |
    And each termination state should be logged
    And all resources should be properly released

  Scenario: Component state affects operation availability
    Given a component in "ACTIVE" state
    Then all standard operations should be available
    When I transition the component to "SUSPENDED" state
    Then data processing operations should be unavailable
    But monitoring operations should be available
    When I transition the component to "MAINTENANCE" state
    Then maintenance operations should be available
    But data processing operations should be unavailable
    When I transition the component to "TERMINATED" state
    Then all operations should be unavailable
    And attempting any operation should raise appropriate exceptions

  Scenario: Lifecycle state transitions trigger appropriate events
    Given I have registered lifecycle state transition listeners
    When a component transitions through its lifecycle states
    Then appropriate events should be triggered for each transition
    And the events should contain the previous and new states
    And the events should include timestamps
    And the events should include the transition reason

  Scenario: Component cancels active timers during termination
    Given a component with multiple active timers
    When I terminate the component
    Then all timers should be properly cancelled
    And no timer callbacks should execute after termination
    And resource leaks should not occur

  @ErrorHandling @BTL
  Scenario: Invalid state transitions are rejected
    Given a component in "ACTIVE" state
    When I attempt the following invalid transitions:
      | TERMINATED   | Cannot transition directly to TERMINATED |
      | INITIALIZING | Cannot transition back to INITIALIZING   |
      | UNKNOWN      | Unknown state                           |
    Then each transition should be rejected with appropriate exception
    And the component should remain in "ACTIVE" state
    And the failed transitions should be logged