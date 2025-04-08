Feature: Machine Composite Management
  As a system developer
  I want machines to manage composites properly
  So that I can create complex systems with proper connections

  Background:
    Given a machine with ID "testMachine" of type "DATA_PROCESSOR"
    And a composite with ID "composite1"
    And a composite with ID "composite2"

  Scenario: Adding composites to a machine
    Given composite "composite1" added to machine "testMachine" as "input"
    And composite "composite2" added to machine "testMachine" as "output"
    Then machine "testMachine" should contain composite "input"
    And machine "testMachine" should contain composite "output"

  Scenario: Connecting composites within a machine
    Given composite "composite1" added to machine "testMachine" as "source"
    And composite "composite2" added to machine "testMachine" as "target"
    And composites "source" and "target" are connected in machine "testMachine"
    Then machine "testMachine" should have connection from "source" to "target"

  Scenario: Using factory-created specialized machines
    Given a data processor machine with ID "processor"
    Then machine "processor" should contain composite "input"
    And machine "processor" should contain composite "processor"
    And machine "processor" should contain composite "output"
    And machine "processor" should have connection from "input" to "processor"
    And machine "processor" should have connection from "processor" to "output"

  Scenario: Using a monitoring machine
    Given a monitoring machine with ID "monitor"
    Then machine "monitor" should contain composite "observer"
    And machine "monitor" should contain composite "validator"
    And machine "monitor" should contain composite "alerter"
    And machine "monitor" should have connection from "observer" to "validator"
    And machine "monitor" should have connection from "validator" to "alerter"