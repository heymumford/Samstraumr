Feature: Composite Lifecycle
  As a system developer
  I want composites to progress through their lifecycle states correctly
  So that they maintain integrity and proper operation during their lifetime

  Background:
    Given a composite with ID "testComposite"

  Scenario: Composite initialization
    Then composite "testComposite" should exist
    And composite "testComposite" should be in "READY" state
    And composite "testComposite" should have status "READY"
    And composite "testComposite" should be of type "OBSERVER"

  Scenario: Composite activation
    When composite "testComposite" is activated
    Then composite "testComposite" should be in "ACTIVE" state
    And composite "testComposite" should have status "OPERATIONAL"

  Scenario: Composite waiting state
    When composite "testComposite" is activated
    And composite "testComposite" is set to waiting
    Then composite "testComposite" should be in "WAITING" state

  Scenario: Composite termination
    When composite "testComposite" is terminated
    Then composite "testComposite" should be in "TERMINATED" state
    And composite "testComposite" should have status "TERMINATED"

  Scenario: Creating different composite types
    Given a composite with ID "observer" of type "OBSERVER"
    And a composite with ID "transformer" of type "TRANSFORMER"
    And a composite with ID "validator" of type "VALIDATOR"
    Then composite "observer" should be of type "OBSERVER"
    And composite "transformer" should be of type "TRANSFORMER"
    And composite "validator" should be of type "VALIDATOR"