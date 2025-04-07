Feature: Component Lifecycle
  As a system developer
  I want components to progress through their lifecycle states correctly
  So that they maintain integrity and proper operation during their lifetime

  Background:
    Given a component with ID "testComponent"

  Scenario: Component initialization
    Then component "testComponent" should exist
    And component "testComponent" should be in "CONCEPTION" state
    And component "testComponent" should have status "INITIALIZING"
    And component "testComponent" should have a unique ID

  Scenario: Component activation
    When component "testComponent" is set to ready
    Then component "testComponent" should be in "READY" state
    And component "testComponent" should have status "READY"
    
    When component "testComponent" is activated
    Then component "testComponent" should be in "ACTIVE" state
    And component "testComponent" should have status "OPERATIONAL"

  Scenario: Component termination
    When component "testComponent" is terminated
    Then component "testComponent" should be in "TERMINATED" state
    And component "testComponent" should have status "TERMINATED"

  Scenario: Component parent-child relationship
    Given a component with ID "parentComponent"
    And component "childComponent" is a child of component "parentComponent"
    Then component "childComponent" should exist
    And component "parentComponent" should exist