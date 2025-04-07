# ---------------------------------------------------------------------------------------
# MachineStateTest.feature - State Management Tests for Machines
#
# This feature file contains tests for machine state management. These tests validate 
# that machines properly maintain and transition between states, and propagate state
# changes to their component bundles and tubes.
# ---------------------------------------------------------------------------------------

@L2_Integration @State
Feature: Machine State Management
  # This feature validates that machines properly manage state across bundles and tubes

@Functional @L2_Machine @Init @State
  Scenario: Machine initializes with proper state hierarchy
    # Purpose: Confirm that a machine properly initializes its internal state
    Given a machine with multiple bundles is instantiated
    When the machine completes initialization
    Then each bundle should have its own state
    And the machine should have a unified state view
    And the state hierarchy should be correctly established

@Functional @L2_Machine @Runtime @State @Monitoring
  Scenario: Machine propagates state changes to components
    # Purpose: Ensure that state changes propagate correctly through the machine
    Given a machine is running with normal state
    When a critical state change occurs in one component
    Then the state change should be detected by the machine
    And the state should be propagated to affected components
    And components should adapt their behavior based on the new state

@ErrorHandling @L2_Machine @Runtime @State @CircuitBreaker
  Scenario: Machine prevents invalid state transitions
    # Purpose: Test that machines enforce valid state transition rules
    Given a machine with defined state transition constraints
    When an invalid state transition is attempted
    Then the transition should be rejected
    And an appropriate error should be logged
    And the machine should maintain its previous valid state

@ErrorHandling @L2_Machine @Termination @State
  Scenario: Machine gracefully transitions to shutdown state
    # Purpose: Validate proper state management during shutdown
    Given a machine is actively processing data
    When a shutdown signal is received
    Then the machine should transition to a shutdown state
    And all bundles should complete current processing
    And resources should be properly released
    And the final state should be logged