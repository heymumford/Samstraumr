/*
Filename: machine-orchestration-tests.feature
Purpose: Validates the orchestration capabilities of Machines
Goals:
  - Verify composite management within machines
  - Test composite connectivity through the machine
  - Validate machine lifecycle and state management
Dependencies:
  - BDD step definitions in org.s8r.component.test.steps package
  - Machine, Composite and Component implementations in org.s8r.component
*/

@L2_Integration @Functional
Feature: Machine Orchestration
  Machines should properly orchestrate multiple composites and manage system-level
  operations and state.

@Functional  @L2_Machine @Orchestration @Creation
  Scenario: Basic machine creation and initialization
    Given a standard environment
    When a machine is created with ID "basic-machine-test"
    Then the machine should be initialized
    And the machine should have an empty composite set
    And the machine should be in "INITIALIZING" state
    And the machine should have a valid identity

@Functional  @L2_Machine @Orchestration @CompositeManagement
  Scenario: Composite management in machine
    Given a standard environment
    When a machine is created with ID "composite-management-test"
    And composites are added to the machine:
      | Name     | Purpose                  |
      | input    | Input Processing         |
      | transform| Data Transformation      |
      | output   | Output Handling          |
    Then the machine should contain all the expected composites
    And each composite should be accessible by name
    And the machine should log composite addition events

@Functional  @L2_Machine @Orchestration @CompositeConnectivity
  Scenario: Composite connectivity in machine
    Given a standard environment
    When a machine is created with ID "composite-connectivity-test"
    And composites are added to the machine:
      | Name     | Purpose                  |
      | input    | Input Processing         |
      | process  | Data Processing          |
      | output   | Output Handling          |
    And composites are connected in the machine:
      | Source   | Target   |
      | input    | process  |
      | process  | output   |
    Then the machine should have the expected connections
    And the machine should maintain connection information
    And the machine should log connection events

@Functional  @L2_Machine @Orchestration @StateManagement
  Scenario: Machine state management
    Given a standard environment
    When a machine is created with ID "state-management-test"
    Then the machine should be active initially
    When the machine is deactivated
    Then the machine should be inactive
    When the machine is activated
    Then the machine should be active again
    When the machine state is updated with key "status" and value "TESTING"
    Then the machine should have "status" with value "TESTING"
    And the machine should log state change events

@Functional  @L2_Machine @Orchestration @ShutdownSequence
  Scenario: Machine shutdown sequence
    Given a standard environment
    When a data processing machine is created
    And the machine is populated with test composites
    Then the machine should be active initially
    When the machine shutdown process is initiated
    Then the machine should proceed through shutdown states
    And all composites should be deactivated
    And the machine should log shutdown events
    And the machine state should be "TERMINATED"
    And the machine should record the termination time