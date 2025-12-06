#
Filename: tube-initialization-tests.feature
Purpose: Validates the initialization and early lifecycle stages of Tubes
Goals:
  - Verify basic tube creation and initialization
  - Test initialization error handling
  - Validate environment awareness during initialization
Dependencies:
  - BDD step definitions in org.s8r.tube.lifecycle.steps package
  - Tube and Environment implementations in org.s8r.tube
#

@ATL @L0_Tube @Lifecycle @Init
Feature: Tube Initialization and Early Lifecycle
  Tubes should properly initialize, establish identity, and begin their lifecycle
  following the biological development model, focusing on the early embryonic stages
  of creation (fertilization) and initialization (cleavage).

  @ATL @L0_Tube @Lifecycle @Init @Creation
  Scenario: Basic tube initialization with reason
    Given a basic environment for initialization
    When a tube is initialized with reason "Basic Initialization Test"
    Then the tube should exist and have a valid UUID
    And the tube should log its initialization with timestamp
    And the tube should capture its reason "Basic Initialization Test"
    And the tube should be queryable for its logs

  @ATL @L0_Tube @Lifecycle @Init @Error
  Scenario: Tube initialization with invalid environment
    Given an invalid environment for initialization
    When tube initialization is attempted with reason "Invalid Environment Test"
    Then the initialization should fail with appropriate error
    And the error should indicate environmental issues
    And no tube should be created

  @ATL @L0_Tube @Lifecycle @Init @Environment
  Scenario: Tube environment awareness during initialization
    Given a basic environment for initialization
    When a tube is initialized with reason "Environment Awareness Test"
    And the tube's environment state changes to "test condition"
    Then the tube should respond to the environmental change
    And the tube should maintain its identity during environmental changes

  @ATL @L0_Tube @Lifecycle @Init @MultiTube
  Scenario: Multiple tubes with unique identities
    Given a basic environment for initialization
    When 10 tubes are initialized simultaneously
    Then each tube should have a unique identifier
    And no tubes should share the same identifier
    And each tube should be independently addressable