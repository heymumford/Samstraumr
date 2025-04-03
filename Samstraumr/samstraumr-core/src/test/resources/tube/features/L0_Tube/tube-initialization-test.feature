/*
Filename: tube-initialization-test.feature
Purpose: Validates initialization, uniqueness, and logging capabilities of atomic Tube instances.
Goals:
  - Ensure that tubes initialize with unique identifiers
  - Ensure that environment details are properly captured and logged
  - Ensure that tubes maintain isolation and proper error handling
Dependencies:
  - BDD step definitions in org.samstraumr.tube.steps package
  - Tube and Environment implementations in org.samstraumr.tube
Assumptions:
  - Test environment provides stable resource conditions
  - Cucumber test runner is properly configured
  - Test tags (@ATL, @L0_Tube, etc.) are consistent with the test ontology
*/

@ATL @L0_Tube
Feature: Tube Initialization and Logging
  # This feature verifies that the atomic base tube (Tube.java) initializes correctly, ensuring that it operates
  # within its assigned environment, generates a unique identity (UUID), logs relevant details, and remains self-contained.
  # These tests are essential for validating that the Tube can function in isolation, without interacting
  # with external systems, which sets the stage for further tests involving Composite Tubes and Machines.

  @ATL @L0_Tube @Init @Identity @Awareness
  Scenario: Tube initializes with a unique ID and logs environment details
    # Purpose: This test confirms that each Tube is instantiated with a unique, immutable identity (UUID),
    # correctly logs its environment details and the reason for initialization.
    # Without this, the Tube system cannot ensure traceability or isolation, making this a critical test.
    Given the operating environment is ready
    When a new Tube is instantiated with reason "Test Initialization"
    Then the Tube should initialize with a unique UUID
    And the Tube should log its environment details
    And the Tube should log the reason "Test Initialization"
    And the Tube log should be queryable

  @ATL @L0_Tube @Init @CircuitBreaker @Resilience
  Scenario: Tube handles invalid environment initialization gracefully
    # Purpose: This test ensures that when a Tube attempts to initialize in an invalid environment, it fails
    # gracefully and provides clear error messages. Fault-tolerant systems rely on effective error logging
    # to prevent cascading failures, making this a priority test.
    Given the operating environment is invalid
    When a new Tube is instantiated with reason "Invalid Environment Test"
    Then the Tube initialization should fail with a "TubeInitializationException"
    And the Tube should log "Invalid Environment Test - initialization failed"
    And the Tube log should capture the specific error message

  @ATL @L0_Tube @Init @Identity @Scale
  Scenario: Tube generates unique UUIDs across multiple instantiations
    # Purpose: Ensuring that each Tube has a truly unique identifier is critical to avoid collisions in a distributed system.
    # This test guarantees that each instance of Tube has its own UUID.
    Given the operating environment is ready
    When 100 Tubes are instantiated simultaneously
    Then each Tube should have a unique UUID
    And no two Tubes should share the same UUID
    And all Tube logs should be queryable for their UUIDs

  @ATL @L0_Tube @Runtime @Observer @Performance
  Scenario: Tube logs are generated in real-time and are queryable
    # Purpose: Verifies that all logging operations happen in real-time, ensuring that operational details like initialization
    # and state changes are logged immediately, which is essential for debugging and tracking.
    Given the operating environment is ready
    When a new Tube is instantiated with reason "Log Query Test"
    Then the Tube should log its initialization details immediately
    And the Tube logs should be queryable by timestamp
    And the logs should contain the reason "Log Query Test"

  @ATL @L0_Tube @Runtime @Flow @Validator
  Scenario: Tube is self-contained and does not attempt external communications
    # Purpose: The atomic Tube should never interact with external systems directly. This test ensures that the Tube
    # only interacts with its internal environment and other Tubes when connected. Any external interaction should
    # be through a Composite tube, ensuring isolation and integrity of the system.
    Given the operating environment is ready
    When a new Tube is instantiated with reason "Self-Containment Test"
    Then the Tube should not establish any external network connections
    And the Tube should not log any external communication attempts
    And all Tube operations should remain confined to its internal environment

  @ATL @L0_Tube @Runtime @Awareness @Observer
  Scenario: Tube self-awareness during resource changes
    # Purpose: Tests the Tube's ability to detect and log environmental changes, such as low memory. Self-awareness
    # is key to the Tube's adaptability and resilience, ensuring that it responds to environmental shifts in a
    # predictable manner.
    Given the environment is ready with memory "8 GB" 
    When a new Tube is instantiated with reason "Self-Awareness Test"
    And the environment changes to "low memory"
    Then the Tube should log "Memory critically low" in the Tube log
    And the Tube should remain operational while logging resource adjustments