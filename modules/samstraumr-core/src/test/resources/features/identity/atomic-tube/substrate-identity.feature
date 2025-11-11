@Tube @Identity @AtomicTube
Feature: Atomic Tube Identity Substrate
  As a developer using the Samstraumr Framework
  I want to ensure tubes maintain proper identity throughout their lifecycle
  So that they can persist, adapt, and collaborate effectively

  Background:
    Given the Samstraumr Framework is initialized
    And the testing environment is prepared

  #############################################
  # PHASE 1: BASIC IDENTITY INITIALIZATION
  #############################################

  @SubstrateIdentity @Phase1 @Positive @L0_Unit
  Scenario: Create tube with required identity components
    When I create a new tube with reason "Data Processing"
    Then the tube should have a valid UUID
    And the tube should have a conception timestamp
    And the tube should have "Data Processing" as its reason for existence

  @SubstrateIdentity @Phase1 @Positive @L0_Unit
  Scenario: Create tube with optional name
    When I create a new tube with reason "Data Processing" and name "DataProcessor1"
    Then the tube should have "DataProcessor1" as its name
    And the tube should have a valid UUID
    And the tube should have a conception timestamp

  @SubstrateIdentity @Phase1 @Positive @L0_Unit
  Scenario: UUID uniqueness across multiple tube instances
    When I create 1000 tubes with reason "Test Tube"
    Then each tube should have a unique UUID

  @SubstrateIdentity @Phase1 @Positive @L1_Integration
  Scenario: Parent-child relationship establishment
    Given an existing tube "ParentTube" with reason "Parent Process"
    When I create a child tube from "ParentTube" with reason "Child Process"
    Then the child tube should have "ParentTube" in its parent lineage

  @SubstrateIdentity @Phase1 @Positive @L0_Unit
  Scenario: Support for hierarchical identity notation for atomic tube
    When I create a new tube with reason "Process Data"
    Then the tube should have an identity notation matching pattern "T<UUID>"

  @SubstrateIdentity @Phase1 @Positive @L1_Integration
  Scenario: Support for hierarchical identity notation for composite tube
    Given a composite "DataComposite" exists
    When I create a new tube within "DataComposite" with reason "Process Data"
    Then the tube should have an identity notation matching pattern "B<ID>.T<UUID>"

  @SubstrateIdentity @Phase1 @Positive @L2_System
  Scenario: Support for hierarchical identity notation for machine tube
    Given a machine "DataMachine" exists
    And a composite "ProcessComposite" exists within "DataMachine"
    When I create a new tube within "ProcessComposite" with reason "Process Data"
    Then the tube should have an identity notation matching pattern "M<ID>.B<ID>.T<UUID>"

  @SubstrateIdentity @Phase1 @Negative @L0_Unit
  Scenario: Attempt to create tube without required reason
    When I attempt to create a new tube without a reason
    Then an exception should be thrown
    And the exception message should contain "Reason for existence is required"

  @SubstrateIdentity @Phase1 @Negative @L0_Unit
  Scenario: Attempt to manually set UUID
    When I attempt to create a new tube with a predetermined UUID
    Then an exception should be thrown
    And the exception message should contain "UUID cannot be manually set"

  @SubstrateIdentity @Phase1 @Negative @L0_Unit
  Scenario: Attempt to manually set conception timestamp
    When I attempt to create a new tube with a predetermined conception timestamp
    Then an exception should be thrown
    And the exception message should contain "Conception timestamp cannot be manually set"

  #############################################
  # PHASE 2: INCORPORATING ENVIRONMENTAL DATA
  #############################################

  @SubstrateIdentity @Phase2 @Positive @L1_Integration
  Scenario: Capture CPU architecture in tube identity
    When I create a new tube with reason "System Monitor"
    Then the tube's identity should include CPU architecture information

  @SubstrateIdentity @Phase2 @Positive @L1_Integration
  Scenario: Capture machine type in tube identity
    When I create a new tube with reason "System Monitor"
    Then the tube's identity should include machine type information

  @SubstrateIdentity @Phase2 @Positive @L1_Integration
  Scenario: Capture operating system information in tube identity
    When I create a new tube with reason "System Monitor"
    Then the tube's identity should include operating system type
    And the tube's identity should include operating system version

  @SubstrateIdentity @Phase2 @Positive @L1_Integration
  Scenario: Capture processor information in tube identity
    When I create a new tube with reason "System Monitor"
    Then the tube's identity should include processor count
    And the tube's identity should include available thread count

  @SubstrateIdentity @Phase2 @Positive @L1_Integration
  Scenario: Capture memory information in tube identity
    When I create a new tube with reason "System Monitor"
    Then the tube's identity should include total system memory
    And the tube's identity should include available memory at inception

  @SubstrateIdentity @Phase2 @Positive @L1_Integration
  Scenario: Capture runtime context in tube identity
    When I create a new tube with reason "System Monitor"
    Then the tube's identity should include Java runtime version
    And the tube's identity should include JVM implementation details

  @SubstrateIdentity @Phase2 @Positive @L1_Integration
  Scenario: Capture network information in tube identity
    When I create a new tube with reason "Network Monitor"
    Then the tube's identity should include host name
    And the tube's identity should include IP address information

  @SubstrateIdentity @Phase2 @Positive @L2_Functional
  Scenario: Environment data impacts tube behavior
    Given a tube "MemoryAdapter" with reason "Memory Optimization"
    When the tube detects low available memory in its environment
    Then the tube should adapt its behavior to use less memory

  @SubstrateIdentity @Phase2 @Positive @L2_Functional
  Scenario: Environment data impacts tube state transitions
    Given a tube "NetworkMonitor" with reason "Monitor Network"
    When the tube detects network unavailability
    Then the tube should transition to BLOCKED state

  @SubstrateIdentity @Phase2 @Negative @L2_Functional
  Scenario: Handle unavailable environmental data gracefully
    Given the operating system information access is restricted
    When I create a new tube with reason "System Monitor"
    Then the tube should be created successfully
    And the tube's operating system information should be marked as "Unavailable"

  @SubstrateIdentity @Phase2 @Negative @L2_Functional
  Scenario: Simulator environment detection
    When I create a tube in a simulated environment
    Then the tube should detect it is running in a simulator
    And the tube's identity should mark the environment as "Simulated"