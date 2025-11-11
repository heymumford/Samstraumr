# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

@L0_Unit @L0_Lifecycle @Lifecycle @ErrorHandling @BTL
Feature: Component Lifecycle Negative Path Testing
  As a framework developer
  I want components to handle exceptional conditions gracefully
  So that the system remains stable even under error conditions

  Background:
    Given the S8r framework is initialized

  @smoke
  Scenario: Invalid state transitions are rejected
    Given a component in "ACTIVE" state
    When I attempt to transition directly to "TERMINATED" state
    Then the transition should be rejected
    And an InvalidStateTransitionException should be thrown
    And the exception should contain details about allowed transitions
    And the component should remain in "ACTIVE" state

  Scenario: Operations fail appropriately for terminated components
    Given a component that has been terminated
    When I attempt the following operations on the terminated component:
      | process_data         |
      | update_configuration |
      | establish_connection |
      | set_state            |
    Then each operation should fail with ComponentTerminatedException
    And exceptions should include the component identity
    And exceptions should include the termination reason
    And exceptions should include the termination timestamp
    And no operations should partially succeed

  Scenario: Component handles initialization failures
    When I attempt to create a component with simulated initialization failure
    Then initialization should fail with ComponentInitializationException
    And partial resources should be properly cleaned up
    And the component should not enter the "ACTIVE" state
    And the failure reason should be logged
    And the exception should contain diagnostic information

  Scenario: Component handles failures during state transition
    Given a component in "ACTIVE" state
    When I transition to "MAINTENANCE" state with simulated transition failure
    Then the transition should be rolled back
    And the component should return to "ACTIVE" state
    And a StateTransitionFailedException should be thrown
    And the component should be in a consistent state
    And the failure should be logged with diagnostic information

  Scenario: Multiple invalid operations are handled consistently
    Given a component in "SUSPENDED" state
    When I attempt multiple invalid operations concurrently
    Then all operations should be rejected consistently
    And appropriate exceptions should be thrown for each operation
    And the component state should remain consistent
    And resource leaks should not occur

  Scenario: Termination during operations handles in-flight work
    Given a component with in-flight operations
    When I terminate the component immediately
    Then pending operations should be cancelled
    And in-progress operations should complete or timeout
    And resources associated with cancelled operations should be released
    And the component should reach "TERMINATED" state
    And termination should not deadlock

  Scenario: Component handles parent termination during initialization
    Given a parent component in "ACTIVE" state
    When I begin initializing a child component
    And I terminate the parent component before child initialization completes
    Then the child initialization should be cancelled
    And child resources should be released
    And appropriate exceptions should be thrown
    And parent termination should complete successfully

  @Resilience
  Scenario: Recovery from critical errors preserves data integrity
    Given a component processing critical data
    When I simulate a critical error during processing
    Then the component should preserve data integrity
    And partial results should be rolled back
    And the error should be properly logged
    And appropriate alerts should be triggered
    And the component should be in a safe state