Feature: Tube Initialization and Logging

  Scenario: Tube initializes with a unique ID and environment details
    Given the operating environment is ready
    When a new Tube is instantiated with reason "Test Initialization"
    Then the Tube should initialize with a unique UUID
    And the Tube should log its environment details
    And the Tube should log the reason "Test Initialization"
    And the Tube's log should be queryable

