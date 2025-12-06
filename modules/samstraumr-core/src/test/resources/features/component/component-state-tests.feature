# Filename: component-state-tests.feature
# Purpose: Validates the state transitions and lifecycle phases of Components
# Goals:
# - Verify state transitions through the biological lifecycle
# - Test state management functionality
# - Validate termination handling
# Dependencies:
# - BDD step definitions in org.s8r.component.test.steps package
# - Component and State implementations in org.s8r.component.core
#


@L0_Unit @State
Feature: Component State Management
  Components should manage their states according to the biological lifecycle model,
  transitioning through the appropriate states in the correct order.

@Functional @L0_Core @Lifecycle @State @Transitions
  Scenario: Basic component state transitions
    Given a basic environment for component initialization
    When a component is initialized with reason "State Transition Test"
    Then the component should have initial state "CONCEPTION"
    When the component transitions through early lifecycle
    Then the component state should progress in order:
      | State               | Category    |
      | CONCEPTION          | LIFECYCLE   |
      | INITIALIZING        | OPERATIONAL |
      | CONFIGURING         | LIFECYCLE   |
      | SPECIALIZING        | LIFECYCLE   |
      | DEVELOPING_FEATURES | LIFECYCLE   |
      | READY               | OPERATIONAL |

@Functional @L0_Core @Lifecycle @State @Termination
  Scenario: Component termination
    Given a basic environment for component initialization
    When a component is initialized with reason "Termination Test"
    Then the component should be in non-terminated state
    When the component is terminated
    Then the component should be in terminated state
    And the component should log its termination
    And the component's resources should be released

@Functional @L0_Core @Lifecycle @State @StateQuery
  Scenario: Component state querying
    Given a basic environment for component initialization
    When a component is initialized with reason "State Query Test"
    Then the component should have initial state "CONCEPTION"
    And the component state should have a description
    And the component state should have a biological analog
    And the component should be able to check its state category

@Functional @L0_Core @Lifecycle @State @InvalidTransition
  Scenario: Invalid component state transitions
    Given a basic environment for component initialization
    When a component is initialized with reason "Invalid Transition Test"
    Then the component should have initial state "CONCEPTION"
    When the component is terminated
    Then attempting to change component state should fail with appropriate error

@Functional @L0_Core @Lifecycle @State @TerminationTimer
  Scenario: Component termination timer
    Given a basic environment for component initialization
    When a component is initialized with reason "Timer Test"
    And a custom termination delay of 500 milliseconds is set
    Then the component should terminate after the specified delay