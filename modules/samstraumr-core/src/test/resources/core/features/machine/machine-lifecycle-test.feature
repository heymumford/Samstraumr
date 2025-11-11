Feature: Machine Lifecycle
  As a system developer
  I want machines to progress through their lifecycle states correctly
  So that they maintain integrity and proper operation during their lifetime

  Background:
    Given a machine with ID "testMachine" of type "DATA_PROCESSOR"

  Scenario: Machine creation and initialization
    Then machine "testMachine" should exist
    And machine "testMachine" should be in "CREATED" state
    And machine "testMachine" should have a valid machine ID
    And machine "testMachine" should be of type "DATA_PROCESSOR"
    
    When machine "testMachine" is initialized
    Then machine "testMachine" should be in "READY" state

  Scenario: Machine startup
    Given machine "testMachine" is initialized
    When machine "testMachine" is started
    Then machine "testMachine" should be in "RUNNING" state
    And machine "testMachine" should be active

  Scenario: Machine stop
    Given machine "testMachine" is initialized
    And machine "testMachine" is started
    When machine "testMachine" is stopped
    Then machine "testMachine" should be in "STOPPED" state

  Scenario: Machine pause
    Given machine "testMachine" is initialized
    And machine "testMachine" is started
    When machine "testMachine" is paused
    Then machine "testMachine" should be in "PAUSED" state

  Scenario: Machine destruction
    Given machine "testMachine" is initialized
    When machine "testMachine" is destroyed
    Then machine "testMachine" should be in "DESTROYED" state

  Scenario: Creating different machine types
    Given a machine with ID "processor" of type "DATA_PROCESSOR"
    And a machine with ID "analytics" of type "ANALYTICS"
    And a machine with ID "monitoring" of type "MONITORING"
    Then machine "processor" should be of type "DATA_PROCESSOR"
    And machine "analytics" should be of type "ANALYTICS"
    And machine "monitoring" should be of type "MONITORING"

  Scenario: Creating a specialized machine
    Given a data processor machine with ID "specialProcessor"
    Then machine "specialProcessor" should exist
    And machine "specialProcessor" should be of type "DATA_PROCESSOR"