@L0_Unit @Identity
Feature: Adam Tube Concept - Origin Point
  As a system architect
  I want to verify the "Adam Tube" concept for first-created tubes
  So that system initialization maintains proper tube hierarchy

  Background:
    Given the system environment is properly configured

@Functional @SubstrateIdentity @AdamTube @Positive @Conception @L0_Tube @Identity
  Scenario: Origin tube is correctly identified as an Adam tube
    When an origin tube is created without a parent
    Then the tube should be identified as an Adam tube
    And the tube should have a unique identifier
    And the tube should have a creation timestamp
    And the tube should capture the environmental context
    And the tube should have a root-level hierarchical address

@Functional @SubstrateIdentity @AdamTube @Positive @Conception @L0_Tube @Identity @Hierarchy
  Scenario: Adam tube can create child tubes
    Given an origin tube exists in the system
    When the origin tube creates a child tube
    Then the child tube should have the origin tube as parent
    And the child tube should not be an Adam tube
    And the child tube should have a hierarchical address derived from its parent

@Functional @SubstrateIdentity @AdamTube @Negative @Conception @L0_Tube @Identity @Validation
  Scenario: Cannot create an Adam tube with a parent
    Given an origin tube exists in the system
    When attempting to create an Adam tube with a parent reference
    Then the operation should be rejected
    And an appropriate error message should be logged

@Functional @SubstrateIdentity @AdamTube @Positive @Conception @L0_Tube @Identity @Lineage
  Scenario: Adam tube maintains proper descendant tracking
    Given an origin tube exists in the system
    When multiple child tubes are created from the origin tube
    Then the origin tube should properly track all its descendants
    And each child should have the origin tube as its parent