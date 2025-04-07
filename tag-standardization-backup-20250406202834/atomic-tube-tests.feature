@L0_Unit @Functional @Identity
Feature: Atomic Tube Concept
  As a system architect
  I want to verify the "Atomic Tube" concept for standalone components
  So that basic tube functionality works correctly without dependencies

  Background:
    Given the system environment is properly configured

@Functional  @AtomicTube @Identity @Initialization @L0_Tube @Positive
  Scenario: Atomic tube is correctly created with basic properties
    When an atomic tube is created with reason "Basic Atomic Tube Test"
    Then the tube should have a valid tube identity
    And the tube should have a unique identifier
    And the tube should have a creation timestamp
    And the tube should capture the reason "Basic Atomic Tube Test"
    And the tube should have an initial lifecycle state
    And the tube should have a logger instance

@Functional  @AtomicTube @Identity @Initialization @L0_Tube @Negative
  Scenario: Atomic tube creation fails with invalid parameters
    When an atomic tube is created with null reason
    Then the tube creation should fail
    And an appropriate error message should indicate "reason cannot be null"
    
    When an atomic tube is created with empty reason
    Then the tube creation should fail
    And an appropriate error message should indicate "reason cannot be empty"
    
    When an atomic tube is created with null environment
    Then the tube creation should fail
    And an appropriate error message should indicate "environment cannot be null"

@Functional  @AtomicTube @Lifecycle @State @L0_Tube @Positive
  Scenario: Atomic tube progresses through lifecycle states
    Given an atomic tube is created with reason "Lifecycle State Test"
    Then the tube should be in CONCEPTION state
    
    When the tube progresses to the next lifecycle state
    Then the tube should be in INITIALIZING state
    And the tube should log the state transition
    
    When the tube progresses to the next lifecycle state
    Then the tube should be in CONFIGURING state
    And the tube should log the state transition
    
    When the tube progresses to READY state
    Then the tube should be in READY state
    And the tube should be operational

@Functional  @AtomicTube @Resources @Termination @L0_Tube @Positive
  Scenario: Atomic tube properly cleans up resources on termination
    Given an atomic tube is created with reason "Resource Cleanup Test"
    And the tube allocates test resources
    
    When the tube is terminated
    Then the tube should be in TERMINATED state
    And all tube resources should be properly released
    And the tube termination should be logged
    And no resource leaks should be detected

@Functional  @AtomicTube @Memory @Logging @L0_Tube @Positive
  Scenario: Atomic tube maintains internal memory log of events
    Given an atomic tube is created with reason "Memory Log Test"
    
    When the tube experiences the following lifecycle events:
      | Event       | Details                     |
      | State Change | CONCEPTION to INITIALIZING  |
      | Configuration| Setting option "test=value" |
      | Activity     | Processing test data        |
      | Warning      | Resource constraint reached |
      | State Change | INITIALIZING to READY       |
    
    Then the tube memory log should contain all lifecycle events
    And the tube memory log should maintain chronological order
    And the tube memory log should contain timestamps
    And the tube memory log should contain event details