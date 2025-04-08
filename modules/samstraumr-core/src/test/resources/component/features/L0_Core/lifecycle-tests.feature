/*
Filename: lifecycle-tests.feature
Purpose: Validates the lifecycle states and transitions of Components
Goals:
  - Verify the complete lifecycle progression
  - Test state-dependent behaviors
  - Validate resource management throughout the lifecycle
Dependencies:
  - BDD step definitions in org.s8r.component.test.steps package
  - Component and State implementations in org.s8r.component.core
*/

@L0_Unit
Feature: Component Lifecycle Management
  Components should follow a biological-inspired lifecycle with proper
  state transitions and appropriate behaviors at each stage.

@Functional @L0_Core @Lifecycle @Progression
  Scenario: Complete component lifecycle progression
    Given a basic environment for component initialization
    When a component is initialized with reason "Lifecycle Test"
    Then the component should have initial state "CONCEPTION"
    When the component is guided through its full lifecycle
    Then the component should pass through all lifecycle states in order:
      | State               | Category    |
      | CONCEPTION          | LIFECYCLE   |
      | INITIALIZING        | OPERATIONAL |
      | CONFIGURING         | LIFECYCLE   |
      | SPECIALIZING        | LIFECYCLE   |
      | DEVELOPING_FEATURES | LIFECYCLE   |
      | READY               | OPERATIONAL |
      | ACTIVE              | OPERATIONAL |
      | WAITING             | OPERATIONAL |
      | ADAPTING            | OPERATIONAL |
      | TRANSFORMING        | OPERATIONAL |
      | STABLE              | LIFECYCLE   |
      | SPAWNING            | LIFECYCLE   |
      | DEGRADED            | OPERATIONAL |
      | MAINTAINING         | OPERATIONAL |
      | TERMINATING         | LIFECYCLE   |
      | TERMINATED          | LIFECYCLE   |
      | ARCHIVED            | LIFECYCLE   |

@Functional @L0_Core @Lifecycle @StateBehavior
  Scenario: State-dependent component behaviors
    Given a basic environment for component initialization
    When a component is initialized with reason "State Behavior Test"
    Then the component should have initial state "CONCEPTION"
    And the component should behave appropriately in CONCEPTION state
    When the component transitions to "INITIALIZING" state
    Then the component should behave appropriately in INITIALIZING state
    When the component transitions to "CONFIGURING" state
    Then the component should behave appropriately in CONFIGURING state
    When the component transitions to "READY" state
    Then the component should behave appropriately in READY state
    When the component transitions to "ACTIVE" state
    Then the component should behave appropriately in ACTIVE state
    When the component is terminated
    Then the component should behave appropriately in TERMINATED state

@Functional @L0_Core @Lifecycle @ResourceManagement
  Scenario: Resource management throughout lifecycle
    Given a basic environment for component initialization
    When a component is initialized with reason "Resource Management Test"
    Then the component should allocate initialization resources
    When the component transitions to "READY" state
    Then the component should allocate operational resources
    When the component is terminated
    Then all component resources should be properly released
    And there should be no resource leaks

@Functional @L0_Core @Lifecycle @StateMetadata
  Scenario: Lifecycle state metadata
    Given a basic environment for component initialization
    When a component is initialized with reason "State Metadata Test"
    Then each lifecycle state should have descriptive metadata
    And each state should have a biological analog
    And each state should belong to a specific category
    And transitions between states should follow logical progression

@Functional @L0_Core @Lifecycle @InvalidTransitions
  Scenario: Invalid lifecycle state transitions
    Given a basic environment for component initialization
    When a component is initialized with reason "Invalid Transition Test"
    Then invalid state transitions should be rejected:
      | From       | To                |
      | CONCEPTION | ACTIVE            |
      | CONCEPTION | TERMINATED        |
      | READY      | CONCEPTION        |
      | TERMINATED | ACTIVE            |
    And appropriate errors should be generated for invalid transitions