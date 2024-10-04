Feature: Unique Identity and Evolving Purpose

  Scenario: Tube Creation with Unique Identity
    Given an environment with specific parameters
    When a new Tube is created with the reason "Initial Reason"
    Then the Tube should have a unique identifier derived from the environment
    And the Tube's reason should be "Initial Reason"

  Scenario: Tube Evolves Its Purpose
    Given an existing Tube with reason "Initial Reason"
    And the Tube detects misalignment with its environment
    When the Tube evolves its purpose to "New Reason"
    Then the Tube's reason should be updated to "New Reason"
    And the Tube should record the transformation in its lineage

