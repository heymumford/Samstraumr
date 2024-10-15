# ---------------------------------------------------------------------------------------
# TubeInitializationTest.feature - Initialization and Logging Tests for Atomic Tubes
#
# This feature file contains critical Above the Line (ATL) tests for the initialization
# and logging of atomic base tubes in Samstraumr. These tests validate the integrity,
# uniqueness, and self-contained nature of a tube's identity and operations. All ATL tests
# must pass before proceeding to any further testing stages.
#
# Running Tests from the Command Line:
#
# To run all tests in this feature file:
#   mvn test -Dcucumber.filter.tags="@ATL"
#
# To run specific groups of tests:
#   Use tags to target individual scenarios or categories of features.
#
# Example: Running only the UUID-related tests:
#   mvn test -Dcucumber.filter.tags="@UUID"
#
# Available Tags:
#   @ATL           - Above the Line tests (P0 priority, must pass for further testing)
#   @Initialization - Tests related to the initialization process of Tubes
#   @UUID          - Tests concerning the generation and uniqueness of Tube UUIDs
#   @Logging       - Tests verifying the logging functionality of Tubes
#   @ErrorHandling - Tests validating the error handling mechanisms of Tubes
#   @Encapsulation - Tests ensuring Tube isolation and self-containment
#
# Best Practices for Running Tests:
#   - Always start with @ATL tests before proceeding to any Below the Line (BTL) tests.
#   - Use specific tags to focus on particular areas of concern when troubleshooting.
#   - Review logs for queryable timestamps and reasons in case of failure.
#   - Ensure your environment is properly configured and ready before running the tests.
#
# ---------------------------------------------------------------------------------------

Feature: Tube Initialization and Logging
  # This feature verifies that the atomic base tube (Tube.java) initializes correctly, ensuring that it operates
  # within its assigned environment, generates a unique identity (UUID), logs relevant details, and remains self-contained.
  # These tests are essential for validating that the Tube can function in isolation, without interacting
  # with external systems, which sets the stage for further tests involving Composite Tubes and Machines.

  @ATL @Initialization @UUID @Logging
  Scenario: Tube initializes with a unique ID and environment details
    # The core reason behind this test is to confirm that the atomic Tube can initialize correctly with its
    # environment, generate a unique identity, and log relevant information. This is a P0 test because without a
    # valid identity and environment awareness, nothing else in Tube-Based Design (TBD) would function correctly.
    Given the operating environment is ready
    When a new Tube is instantiated with reason "Test Initialization"
    Then the Tube should initialize with a unique UUID
    And the Tube should log its environment details
    And the Tube should log the reason "Test Initialization"
    And the Tube log should be queryable

  @ATL @Initialization @ErrorHandling @FaultTolerance
  Scenario: Tube initialization fails gracefully when the environment is invalid
    # This test verifies that the Tube can handle failures during initialization, such as an invalid or corrupted
    # operating environment. Handling errors gracefully is crucial to ensure the system does not crash when individual
    # tubes encounter issues. This P0 test guarantees fault tolerance at the atomic level.
    Given the operating environment is invalid
    When a new Tube is instantiated with reason "Invalid Environment Test"
    Then the Tube initialization should fail with a "EnvironmentInitializationException"
    And the Tube should log the failure with the reason "Invalid Environment Test"
    And the log should capture the specific error message "Environment initialization failed"
    And the Tube log should be queryable

  @ATL @UUID @Uniqueness @Boundary
  Scenario: Tube generates a truly unique UUID across multiple instantiations
    # This test ensures that each Tube receives a unique identity across multiple instances. This is a foundational
    # principle in Tube-Based Design. The uniqueness of each Tube ensures that systems can track, trace, and interact
    # with them without confusion or duplication. This is a priority 0 test.
    Given the operating environment is ready
    When multiple Tubes are instantiated simultaneously
    Then each Tube should have a unique UUID
    And no two Tubes should share the same UUID
    And all Tube logs should be queryable for their UUIDs

  @ATL @Logging @Querying
  Scenario: Tube logs are generated in real-time and are queryable
    # Logging is critical for visibility into Tube operations. This test verifies that logs are generated immediately
    # upon Tube initialization and are queryable. Without this, debugging and tracking a Tube's lifecycle would be
    # near impossible. Another P0 test.
    Given the operating environment is ready
    When a new Tube is instantiated with reason "Log Query Test"
    Then the Tube should log its initialization details immediately
    And the Tube logs should be queryable by timestamp
    And the logs should contain the reason "Log Query Test"

  @ATL @Encapsulation @SelfContainment
  Scenario: Tube is self-contained and does not attempt external communications
    # This test ensures that the atomic Tube does not interact with any external systems. The purpose of an atomic Tube
    # is to interface only with its host OS and any Tubes directly connected to it. This test reinforces that there are
    # no unwanted external communications, reinforcing the integrity of Tube-based isolation.
    Given the operating environment is ready
    When a new Tube is instantiated with reason "Self-Containment Test"
    Then the Tube should not establish any external network connections
    And the Tube should not log any external communication attempts
    And all Tube operations should remain confined to its internal environment

    # Feature: Tube Initialization and Logging
# This feature file covers the critical aspects of atomic Tube instantiation in memory, focusing on its uniqueness, environment awareness, resource management, and error handling.
# These Above-the-Line (ATL) tests ensure that the Tube behaves as expected in isolation.
# Tags have been used to allow selective testing of key categories (e.g., @identity, @encapsulation, @lifecycle).

  @tube @initialization @ATL
  Feature: Tube Initialization and Logging

  # Scenario: Tube initializes with a unique ID and environment details
  # This test verifies that every Tube is instantiated with a unique, immutable UUID and that it correctly logs its environment details and initialization reason.
  # Rationale: The uniqueness and traceability of the Tube are critical for system integrity.
  @identity @logging
  Scenario: Tube initializes with a unique ID and environment details
    Given the operating environment is ready
    When a new Tube is instantiated with reason "Test Initialization"
    Then the Tube should initialize with a unique UUID
    And the Tube should log its environment details
    And the Tube should log the reason "Test Initialization"
    And the Tube log should be queryable

  # Scenario: Tube encapsulation validation
  # This test checks that the Tube's internal state remains encapsulated and immutable after initialization, ensuring proper encapsulation principles are followed.
  # Rationale: Protecting the internal state of the Tube from unwanted mutation is key to system stability.
  @encapsulation
  Scenario: Tube encapsulation validation
    Given the operating environment is ready
    When a new Tube is instantiated with reason "Encapsulation Test"
    Then the Tube’s internal state should be encapsulated and not directly modifiable
    And the Tube’s identity (UUID) should remain immutable

  # Scenario Outline: Tube initialization failure handling
  # This scenario outlines potential failure cases during initialization, verifying that the Tube handles them gracefully and logs appropriate error messages.
  # Rationale: Graceful error handling ensures system resilience, especially during critical processes like initialization.
  @error_handling
  Scenario Outline: Tube initialization failure handling
    Given the operating environment is <status>
    When a new Tube is instantiated with reason "Test Failure Handling"
    Then the Tube should log an error message "<error_message>"
    And the Tube should not initialize successfully

    Examples:
      | status          | error_message                          |
      | "corrupted"     | "Environment corrupted, initialization failed" |
      | "incomplete"    | "Environment incomplete, initialization failed" |
      | "not ready"     | "Environment not ready, initialization failed" |

  # Scenario: Tube resource allocation check
  # This test verifies that the Tube allocates the correct amount of resources (CPU, memory) based on its environment and logs this information.
  # Rationale: Proper resource allocation is critical for ensuring that Tubes operate within defined limits.
  @resource_management
  Scenario: Tube resource allocation check
    Given the operating environment is ready with CPU: "4 cores" and memory: "8 GB"
    When a new Tube is instantiated with reason "Resource Allocation Test"
    Then the Tube should allocate CPU: "4 cores" and memory: "8 GB"
    And the Tube should log its resource allocation details

  # Scenario: Tube lifecycle state transition
  # This test verifies that the Tube correctly transitions through its lifecycle states (e.g., initialized, active, terminated) and logs each transition.
  # Rationale: Managing the lifecycle of a Tube is essential for ensuring consistent state and orderly shutdowns.
  @lifecycle
  Scenario: Tube lifecycle state transition
    Given the operating environment is ready
    When a new Tube is instantiated with reason "Lifecycle Test"
    Then the Tube should transition to "initialized" state
    And the Tube should transition to "active" state
    When the Tube is terminated
    Then the Tube should transition to "terminated" state
    And all state transitions should be logged in the Tube log

  # Scenario Outline: Tube initialization with different environment configurations
  # This scenario outlines how the Tube reacts to different environmental configurations and ensures that it adapts correctly and logs all configurations.
  # Rationale: The Tube must be flexible in handling various configurations while maintaining correct functionality.
  @environment_adaptation
  Scenario Outline: Tube initialization with different environment configurations
    Given the environment is configured with <cpu> and <memory>
    When a new Tube is instantiated with reason "Config Test"
    Then the Tube should log "CPU: <cpu>, Memory: <memory>" in the Tube log
    And the Tube should operate correctly with the given configuration

    Examples:
      | cpu         | memory |
      | "2 cores"   | "4 GB" |
      | "8 cores"   | "16 GB" |
      | "16 cores"  | "32 GB" |

  # Scenario: Querying the Tube log after initialization
  # This test ensures that the Tube's log is queryable and that it contains the correct initialization details for audit and review.
  # Rationale: Transparent logging is essential for debugging and system observability.
  @logging @audit
  Scenario: Querying the Tube log after initialization
    Given a Tube has been initialized with reason "Log Query Test"
    When I query the Tube log
    Then the log should contain the reason "Log Query Test"
    And the log should contain the Tube's unique UUID
    And the log should contain the environment details

  # Scenario: Tube self-awareness on environment changes
  # This test verifies that the Tube can detect changes in its environment and log any critical updates that affect its operation.
  # Rationale: The Tube’s ability to sense and adapt to environment changes is essential for its resilience and self-awareness.
  @self_awareness @environment_adaptation
  Scenario: Tube self-awareness on environment changes
    Given the environment is ready
    And the Tube is instantiated with reason "Self-Awareness Test"
    When the environment changes to "low memory"
    Then the Tube should log "Memory critically low" in the Tube log
    And the Tube should remain operational while logging resource adjustments

