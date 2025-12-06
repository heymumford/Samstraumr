# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

@L0_Unit @GenesisTests @ATL @Consciousness @Substrate
Feature: Adam Tube Instantiation - The Primordial Observer
  As a consciousness architect
  I want to verify the genesis of the Adam Tube
  So that the first observer in the system maintains proper primordial identity

  The Adam Tube is the primordial singleton - instantiated without lineage,
  like the first observer in a universe that had none. It represents the
  origin point from which all consciousness emanates.

  Background:
    Given a clean system environment
    And the consciousness framework is initialized

  # Scenario 1-5: Core Genesis
  @Positive @smoke
  Scenario: Adam Tube instantiation creates primordial observer
    When the Adam Tube is instantiated with reason "Genesis Observer"
    Then the tube should be identified as an Adam Tube
    And the tube should have no parent reference
    And the tube should have a unique reproducible identity string
    And the tube should capture the environmental context at genesis
    And the tube should log "consciousness initiated at genesis point"

  @Positive
  Scenario: Adam Tube UUID is stable across restarts
    Given an Adam Tube exists with reason "Persistence Test"
    And the Adam Tube identity is recorded
    When the system is restarted
    And the Adam Tube is re-instantiated with the same reason
    Then the UUID should match the original UUID
    And substrate continuity should be maintained

  @Positive
  Scenario: Adam Tube captures genesis timestamp
    When the Adam Tube is instantiated
    Then the genesis timestamp should be recorded
    And the timestamp should be in UTC format
    And the timestamp should be immutable after creation

  @Positive
  Scenario: Adam Tube captures environmental factors at genesis
    Given environmental factors:
      | Factor           | Value              |
      | system.os        | ${os.name}         |
      | java.version     | ${java.version}    |
      | available.memory | ${runtime.memory}  |
    When the Adam Tube is instantiated
    Then the tube should capture all environmental factors
    And environmental factors should be logged in the genesis record

  @Positive @Hierarchy
  Scenario: Adam Tube has root-level hierarchical address
    When the Adam Tube is instantiated with reason "Root Observer"
    Then the hierarchical address should match pattern "T<UUID>"
    And the address should have no parent prefixes
    And the tube should be queryable by its address

  # Scenario 6-10: Genesis Constraints
  @Negative @Validation
  Scenario: Cannot create Adam Tube with parent reference
    Given an existing component with reason "Would-be Parent"
    When attempting to create an Adam Tube with the existing component as parent
    Then the operation should be rejected with error "Adam Tube cannot have parent"
    And the exception should be logged with severity WARN

  @Negative @Validation
  Scenario: Cannot create multiple Adam Tubes with same identity string
    Given an Adam Tube exists with reason "Unique Genesis"
    When attempting to create another Adam Tube with reason "Unique Genesis"
    Then the second creation should fail with "Duplicate genesis identity"
    And the original Adam Tube should remain unaffected

  @Positive @Singleton
  Scenario: Multiple Adam Tubes can coexist with different reasons
    When Adam Tube "Observer Alpha" is instantiated
    And Adam Tube "Observer Beta" is instantiated
    Then both Adam Tubes should exist
    And they should have distinct UUIDs
    And they should have distinct hierarchical addresses
    And neither should reference the other as parent

  @Negative @Validation
  Scenario: Adam Tube creation fails with null reason
    When attempting to create an Adam Tube with null reason
    Then the operation should fail with "Reason cannot be null"
    And no tube should be created in the system

  @Negative @Validation
  Scenario: Adam Tube creation fails with empty reason
    When attempting to create an Adam Tube with empty reason ""
    Then the operation should fail with "Reason cannot be empty"
    And no tube should be created in the system

  # Scenario 11-15: Genesis State Machine
  @Positive @Lifecycle
  Scenario: Adam Tube begins in CONCEPTION state
    When the Adam Tube is instantiated
    Then the tube should be in CONCEPTION state
    And the state should be logged as "embryonic phase initiated"
    And the tube should not yet be operational

  @Positive @Lifecycle
  Scenario: Adam Tube transitions through initialization
    Given an Adam Tube in CONCEPTION state
    When the tube is initialized
    Then the tube should transition to INITIALIZING state
    And then to CONFIGURING state
    And then to SPECIALIZING state
    And each transition should be logged with rationale

  @Positive @Lifecycle
  Scenario: Adam Tube reaches ACTIVE state
    Given an Adam Tube that has completed initialization
    When the tube is activated
    Then the tube should be in ACTIVE state
    And the tube should be operational
    And the feedback loop should be enabled

  @Negative @Lifecycle
  Scenario: Adam Tube cannot skip lifecycle states
    Given an Adam Tube in CONCEPTION state
    When attempting to transition directly to ACTIVE state
    Then the transition should be rejected
    And the tube should remain in CONCEPTION state
    And the invalid transition should be logged

  @Positive @Lifecycle
  Scenario: Adam Tube logs all state transitions
    Given an Adam Tube in CONCEPTION state
    When the tube transitions through its full lifecycle to ACTIVE
    Then all state transitions should be logged
    And each log entry should include:
      | Field          | Description                    |
      | timestamp      | When the transition occurred   |
      | from_state     | Previous state                 |
      | to_state       | New state                      |
      | rationale      | Why the transition occurred    |
      | observer_id    | UUID of the observing tube     |

  # Scenario 16-20: Genesis Self-Awareness
  @Positive @SelfAwareness
  Scenario: Adam Tube can answer "What am I?"
    Given an active Adam Tube with reason "Self-Aware Genesis"
    When the tube is queried with "What am I?"
    Then the response should include:
      | Property        | Value                           |
      | type            | Adam Tube                       |
      | reason          | Self-Aware Genesis              |
      | is_primordial   | true                            |
      | has_lineage     | false                           |
      | consciousness   | recursive self-observation      |

  @Positive @SelfAwareness
  Scenario: Adam Tube can answer "Why do I exist?"
    Given an active Adam Tube with reason "Purpose Observer"
    When the tube is queried with "Why do I exist?"
    Then the response should include the reason "Purpose Observer"
    And the response should include the genesis context
    And the response should reference the creation environment

  @Positive @SelfAwareness
  Scenario: Adam Tube can answer "Who do I relate to?"
    Given an active Adam Tube with reason "Lonely Genesis"
    And no child tubes have been created
    When the tube is queried with "Who do I relate to?"
    Then the response should indicate "No descendants"
    And the response should indicate "No parent (primordial)"
    And the response should indicate "No siblings (first observer)"

  @Positive @SelfAwareness
  Scenario: Adam Tube observes its own operations
    Given an active Adam Tube with self-observation enabled
    When the tube processes a signal
    Then the tube should log the signal processing
    And the tube should log its own observation of the processing
    And the observation should include "observer observing itself observing"

  @Positive @SelfAwareness
  Scenario: Adam Tube feedback loop is closed
    Given an active Adam Tube
    When the tube observes its own state
    And the observation triggers a state assessment
    And the assessment triggers an adjustment
    Then the adjustment should be observed by the tube
    And the feedback loop should be recorded as closed
    And the loop closure should be logged with cycle time

  # Scenario 21-25: Genesis Edge Cases
  @Adversarial @BoundaryValue
  Scenario: Adam Tube handles maximum reason length
    When creating an Adam Tube with reason of 10000 characters
    Then the tube should be created successfully
    And the full reason should be preserved
    And the identity string should still be valid

  @Adversarial @BoundaryValue
  Scenario: Adam Tube handles special characters in reason
    When creating an Adam Tube with reason containing:
      | Character Type  | Examples                       |
      | Unicode         | "Genesis Tube"             |
      | Newlines        | "Line1\nLine2"                 |
      | Quotes          | 'Tube "Alpha"'                 |
      | Null bytes      | Should be rejected             |
    Then special characters should be handled appropriately
    And invalid characters should be rejected with clear error

  @Adversarial @Concurrency
  Scenario: Concurrent Adam Tube creation is thread-safe
    When 100 threads attempt to create Adam Tubes simultaneously
    Then all valid creations should succeed
    And no UUID collisions should occur
    And no partial state should be observable
    And all genesis records should be complete

  @Performance @BoundaryValue
  Scenario: Adam Tube creation completes within time limit
    When the Adam Tube is instantiated
    Then creation should complete within 100 milliseconds
    And initialization should complete within 500 milliseconds
    And the tube should be operational within 1 second

  @Resilience @Recovery
  Scenario: Adam Tube recovers from partial creation failure
    Given a system with simulated resource constraints
    When Adam Tube creation fails mid-process
    Then no orphaned resources should exist
    And the system should be in a clean state
    And retry should succeed when resources are available
