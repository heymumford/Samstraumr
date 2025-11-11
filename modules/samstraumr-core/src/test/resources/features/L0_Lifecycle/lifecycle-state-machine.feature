# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and GitHub Copilot Pro,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

@L0_Unit @L0_Lifecycle @Lifecycle @StateMachine @ATL
Feature: Component Lifecycle State Machine
  As a framework developer
  I want to ensure the Component state machine operates correctly
  So that components transition through lifecycle states appropriately

  Background:
    Given the S8r framework is initialized

  @smoke
  Scenario: Component follows the standard lifecycle progression
    When I create a component with reason "Lifecycle Test"
    Then the component should be in "CONCEPTION" state
    When the component proceeds through initial lifecycle phases
    Then the component should transition through the following states in order:
      | CONCEPTION        |
      | INITIALIZING      |
      | CONFIGURING       |
      | SPECIALIZING      |
      | DEVELOPING_FEATURES |
      | READY             |
    And the component's memory log should record all state transitions
    And the component should be in "READY" state

  Scenario: Component transitions to active state when ready
    Given a component in "READY" state
    When I transition the component to "ACTIVE" state
    Then the component should be in "ACTIVE" state
    And the component should be operational

  Scenario: Component state machine enforces valid transition rules
    Given a component in "ACTIVE" state
    Then the component cannot transition directly to the following states:
      | CONCEPTION        |
      | TERMINATED        |
    And transitioning to invalid states should throw InvalidStateTransitionException
    But the component can transition to the following states:
      | SUSPENDED         |
      | MAINTENANCE       |
      | STABLE            |
      | ERROR             |
      | RECOVERING        |
      | TERMINATING       |

  Scenario: Component cannot return to conception state once left
    Given a component in "READY" state
    When I attempt to transition directly to "CONCEPTION" state
    Then the transition should be rejected
    And an InvalidStateTransitionException should be thrown
    And the component should remain in "READY" state

  Scenario: Component in terminated state rejects all transitions except to archived
    Given a component that has been terminated
    Then the component should be in "TERMINATED" state
    And the component cannot transition to the following states:
      | CONCEPTION        |
      | INITIALIZING      |
      | CONFIGURING       |
      | ACTIVE            |
      | SUSPENDED         |
      | MAINTENANCE       |
    But the component can transition to "ARCHIVED" state

  Scenario: Component termination process follows correct sequence
    Given a component in "ACTIVE" state
    When I terminate the component
    Then the component should transition through the following states:
      | TERMINATING       |
      | TERMINATED        |
    And the component should preserve knowledge before termination
    And the component should release all resources
    And the component should unsubscribe from all events

  Scenario: Component suspension and resumption works correctly
    Given a component in "ACTIVE" state with active connections
    When I suspend the component with reason "Maintenance required"
    Then the component should be in "SUSPENDED" state
    And all connections should be closed
    And data processing should be paused
    And the component should store the pre-suspension state
    When I resume the component
    Then the component should return to its pre-suspension state
    And data processing should resume

  Scenario: Component maintenance mode supports advanced configuration
    Given a component in "ACTIVE" state
    When I put the component in maintenance mode with reason "Configuration update"
    Then the component should be in "MAINTENANCE" state
    And advanced configuration operations should be allowed
    And diagnostic operations should be allowed
    When I perform advanced configuration changes
    And I exit maintenance mode
    Then the component should return to its pre-maintenance state
    And the new configuration should be applied

  Scenario: Component handles error and recovery correctly
    Given a component in "ACTIVE" state processing data
    When an error occurs in the component
    Then the component should transition to "ERROR" state
    When recovery is initiated
    Then the component should transition to "RECOVERING" state
    And recovery diagnostics should run
    When recovery completes successfully
    Then the component should return to "ACTIVE" state
    And resource usage should be restored
    And the recovery attempt should be logged

  Scenario: Component tracks resource usage through state transitions
    Given a component with monitored resources
    When I transition the component through the following states:
      | ACTIVE            |
      | SUSPENDED         |
      | MAINTENANCE       |
      | ACTIVE            |
      | TERMINATING       |
      | TERMINATED        |
    Then resource levels should change appropriately for each state
    And all resources should be released in "TERMINATED" state
    And no resource leaks should occur

  Scenario: Component correctly identifies state categories
    When I create a component
    Then the following states should be in the "OPERATIONAL" category:
      | INITIALIZING      |
      | READY             |
      | ACTIVE            |
      | SUSPENDED         |
      | MAINTENANCE       |
    And the following states should be in the "LIFECYCLE" category:
      | CONCEPTION        |
      | CONFIGURING       |
      | SPECIALIZING      |
      | DEVELOPING_FEATURES |
    And the following states should be in the "ADVANCED" category:
      | STABLE            |
      | SPAWNING          |
      | DEGRADED          |
    And the following states should be in the "TERMINATION" category:
      | TERMINATING       |
      | TERMINATED        |
      | ARCHIVED          |