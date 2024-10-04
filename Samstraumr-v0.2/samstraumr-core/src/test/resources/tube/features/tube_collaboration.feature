Feature: Self-Awareness and Adaptive Collaboration

  Scenario: Tube Lineage Awareness
    Given a parent Tube with reason "Parent Reason"
    And a child Tube is created with reason "Child Reason" by the parent Tube
    When I check the child Tube's lineage
    Then it should include the parent Tube's reason "Parent Reason"

  Scenario: Tube Connection Awareness
    Given a Tube connected to another Tube with reason "Connected Reason"
    When I check the Tube's connections
    Then the Tube should be aware of the connected Tube and its reason

  Scenario: Reflective Learning and Purpose Alignment
    Given a Tube with operational history
    When the Tube analyzes its performance
    And detects misalignment with its purpose
    Then it should decide to evolve its purpose to "Adjusted Reason"
    And record the transformation in its lineage

