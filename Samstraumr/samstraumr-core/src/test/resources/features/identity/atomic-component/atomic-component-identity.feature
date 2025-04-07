@Component @Identity @AtomicComponent
Feature: Atomic Component Identity
  As a developer using the S8r Framework
  I want to ensure components maintain proper identity throughout their lifecycle
  So that they can persist, adapt, and collaborate effectively

  Background:
    Given the S8r Framework is initialized
    And the testing environment is prepared

  #############################################
  # PHASE 1: BASIC IDENTITY INITIALIZATION
  #############################################

  @ComponentIdentity @Phase1 @Positive @L0_Unit
  Scenario: Create component with required identity components
    When I create a new component with reason "Data Processing"
    Then the component should have a valid UUID
    And the component should have a creation timestamp
    And the component should have "Data Processing" as its reason for existence

  @ComponentIdentity @Phase1 @Positive @L0_Unit
  Scenario: Create component with optional name
    When I create a new component with reason "Data Processing" and name "DataProcessor1"
    Then the component should have "DataProcessor1" as its name
    And the component should have a valid UUID
    And the component should have a creation timestamp

  @ComponentIdentity @Phase1 @Positive @L0_Unit
  Scenario: UUID uniqueness across multiple component instances
    When I create 1000 components with reason "Test Component"
    Then each component should have a unique UUID

  @ComponentIdentity @Phase1 @Positive @L1_Integration
  Scenario: Parent-child relationship establishment
    Given an existing component "ParentComponent" with reason "Parent Process"
    When I create a child component from "ParentComponent" with reason "Child Process"
    Then the child component should have "ParentComponent" in its parent lineage

  @ComponentIdentity @Phase1 @Positive @L0_Unit
  Scenario: Support for hierarchical identity notation for atomic component
    When I create a new component with reason "Process Data"
    Then the component should have an identity notation matching pattern "C<UUID>"

  @ComponentIdentity @Phase1 @Positive @L1_Integration
  Scenario: Support for hierarchical identity notation for composite component
    Given a composite "DataComposite" exists
    When I create a new component within "DataComposite" with reason "Process Data"
    Then the component should have an identity notation matching pattern "CM<ID>.C<UUID>"

  @ComponentIdentity @Phase1 @Positive @L2_System
  Scenario: Support for hierarchical identity notation for machine component
    Given a machine "DataMachine" exists
    And a composite "ProcessComposite" exists within "DataMachine"
    When I create a new component within "ProcessComposite" with reason "Process Data"
    Then the component should have an identity notation matching pattern "M<ID>.CM<ID>.C<UUID>"

  @ComponentIdentity @Phase1 @Negative @L0_Unit
  Scenario: Attempt to create component without required reason
    When I attempt to create a new component without a reason
    Then an exception should be thrown
    And the exception message should contain "Reason for existence is required"

  @ComponentIdentity @Phase1 @Negative @L0_Unit
  Scenario: Attempt to manually set UUID
    When I attempt to create a new component with a predetermined UUID
    Then an exception should be thrown
    And the exception message should contain "UUID cannot be manually set"

  @ComponentIdentity @Phase1 @Negative @L0_Unit
  Scenario: Attempt to manually set creation timestamp
    When I attempt to create a new component with a predetermined creation timestamp
    Then an exception should be thrown
    And the exception message should contain "Creation timestamp cannot be manually set"

  #############################################
  # PHASE 2: INCORPORATING ENVIRONMENTAL DATA
  #############################################

  @ComponentIdentity @Phase2 @Positive @L1_Integration
  Scenario: Capture CPU architecture in component identity
    When I create a new component with reason "System Monitor"
    Then the component's identity should include CPU architecture information

  @ComponentIdentity @Phase2 @Positive @L1_Integration
  Scenario: Capture machine type in component identity
    When I create a new component with reason "System Monitor"
    Then the component's identity should include machine type information

  @ComponentIdentity @Phase2 @Positive @L1_Integration
  Scenario: Capture operating system information in component identity
    When I create a new component with reason "System Monitor"
    Then the component's identity should include operating system type
    And the component's identity should include operating system version

  @ComponentIdentity @Phase2 @Positive @L1_Integration
  Scenario: Capture processor information in component identity
    When I create a new component with reason "System Monitor"
    Then the component's identity should include processor count
    And the component's identity should include available thread count

  @ComponentIdentity @Phase2 @Positive @L1_Integration
  Scenario: Capture memory information in component identity
    When I create a new component with reason "System Monitor"
    Then the component's identity should include total system memory
    And the component's identity should include available memory at inception

  @ComponentIdentity @Phase2 @Positive @L1_Integration
  Scenario: Capture runtime context in component identity
    When I create a new component with reason "System Monitor"
    Then the component's identity should include Java runtime version
    And the component's identity should include JVM implementation details

  @ComponentIdentity @Phase2 @Positive @L1_Integration
  Scenario: Capture network information in component identity
    When I create a new component with reason "Network Monitor"
    Then the component's identity should include host name
    And the component's identity should include IP address information

  @ComponentIdentity @Phase2 @Positive @L2_Functional
  Scenario: Environment data impacts component behavior
    Given a component "MemoryAdapter" with reason "Memory Optimization"
    When the component detects low available memory in its environment
    Then the component should adapt its behavior to use less memory

  @ComponentIdentity @Phase2 @Positive @L2_Functional
  Scenario: Environment data impacts component state transitions
    Given a component "NetworkMonitor" with reason "Monitor Network"
    When the component detects network unavailability
    Then the component should transition to WAITING state

  @ComponentIdentity @Phase2 @Negative @L2_Functional
  Scenario: Handle unavailable environmental data gracefully
    Given the operating system information access is restricted
    When I create a new component with reason "System Monitor"
    Then the component should be created successfully
    And the component's operating system information should be marked as "Unavailable"

  @ComponentIdentity @Phase2 @Negative @L2_Functional
  Scenario: Simulator environment detection
    When I create a component in a simulated environment
    Then the component should detect it is running in a simulator
    And the component's identity should mark the environment as "Simulated"