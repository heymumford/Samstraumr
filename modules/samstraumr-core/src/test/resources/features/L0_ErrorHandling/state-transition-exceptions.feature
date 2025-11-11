# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

@L0_Unit @L0_ErrorHandling @ErrorHandling @BTL @State
Feature: Component State Transition Exception Handling
  As a framework developer
  I want robust exception handling for invalid state transitions
  So that components maintain state integrity even under error conditions

  Background:
    Given the S8r framework is initialized

  @smoke
  Scenario: Invalid direct state transitions are rejected
    Given a component in "ACTIVE" state
    When I attempt these invalid direct state transitions:
      | Target State  | Expected Exception               | Expected Message                           |
      | TERMINATED    | InvalidStateTransitionException  | Cannot transition directly to TERMINATED   |
      | INITIALIZING  | InvalidStateTransitionException  | Cannot transition to INITIALIZING from ACTIVE |
      | UNKNOWN       | IllegalArgumentException         | Unknown state: UNKNOWN                     |
    Then each transition should fail with the expected exception
    And the component should remain in its original state
    And each exception should include the current state and attempted target state
    And each failed transition should be logged

  Scenario: State-dependent operations fail appropriately
    Given a component in "SUSPENDED" state
    When I attempt state-dependent operations:
      | Operation           | Expected Exception          | Expected Message                         |
      | processData         | IllegalStateException       | Cannot process data in SUSPENDED state   |
      | establishConnection | IllegalStateException       | Cannot establish connection in SUSPENDED state |
    Then each operation should fail with the expected exception and message
    And the component should remain in its original state
    And each exception should include the current state and required state

  Scenario: Terminated component rejects all state transitions
    Given a component has been terminated with reason "Test termination"
    When I attempt to transition to each of the following states:
      | Target State  |
      | ACTIVE        |
      | SUSPENDED     |
      | MAINTENANCE   |
    Then each transition should fail with ComponentTerminatedException
    And each exception should include the termination reason "Test termination"
    And each exception should include the termination timestamp
    And the component should remain in "TERMINATED" state

  Scenario: Component handles race conditions in state transitions
    Given a component with concurrent state transition operations
    When multiple threads attempt to change state simultaneously
    Then no invalid intermediate states should occur
    And state transitions should be thread-safe
    And the final state should be valid
    And no exceptions should be leaked to the caller
    And concurrent transition attempts should be consistently handled

  Scenario: State transition during active operations
    Given a component with active long-running operations
    When I transition the component to "SUSPENDED" state
    Then ongoing operations should either complete or be cleanly interrupted
    And new operations should be rejected with appropriate exceptions
    And the component should reach "SUSPENDED" state
    And no resources should leak
    And the transition should be logged with timing information

  Scenario: State transitions with dependent components
    Given a parent component with child components
    When I transition the parent to "SUSPENDED" state
    Then child components should receive notification of parent state change
    And child components should handle the parent state change appropriately
    And the parent state transition should complete successfully
    And dependent component exceptions should not prevent parent transition
    And exceptions in child handling should be properly logged

  Scenario Outline: State transition with specific component configurations
    Given a component in "<Initial State>" state with configuration:
      | setting           | value        |
      | strictTransitions | <Strictness> |
      | autoRecovery      | <Recovery>   |
    When I attempt to transition to an invalid next state
    Then the transition handling should follow the "<Expected Behavior>" behavior

    Examples:
      | Initial State | Strictness | Recovery | Expected Behavior                 |
      | ACTIVE        | true       | false    | Reject with exception             |
      | ACTIVE        | false      | false    | Log warning but allow transition  |
      | ACTIVE        | true       | true     | Reject with exception, auto-recover |
      | SUSPENDED     | true       | false    | Reject with exception             |
      | MAINTENANCE   | true       | true     | Reject with exception, auto-recover |

  @Resilience
  Scenario: Recovery from failed state transition
    Given a component that will experience transition failure
    When I attempt a state transition that will fail internally
    Then the component should revert to previous state
    And the component should be in a consistent state
    And an appropriate exception should be thrown
    And the exception should contain detailed diagnostic information
    And the component should be usable after the failed transition
    And the failure should be logged with complete context