@L0_CLI @Functional
Feature: S8r CLI Commands
  As a model designer
  I want to use CLI commands to initialize and interact with S8r models
  So that I can efficiently work with the framework from the command line

  Background:
    Given a temporary directory for testing
    And the S8r CLI is available on the system path

  @Initialization @Positive
  Scenario: Initialize an empty S8r project with default settings
    When I run "s8r init" in the temporary directory
    Then the command should succeed
    And the temporary directory should contain an initialized S8r project
    And the project should have the default package "org.example"
    And the project should have an Adam component

  @Initialization @Positive
  Scenario: Initialize an S8r project with a custom package name
    When I run "s8r init --package com.example.model" in the temporary directory
    Then the command should succeed
    And the temporary directory should contain an initialized S8r project
    And the project should have the package "com.example.model"
    And the project should have an Adam component

  @Initialization @Negative
  Scenario: Attempt to initialize in a directory that already has an S8r project
    Given the temporary directory already contains an initialized S8r project
    When I run "s8r init" in the temporary directory
    Then the command should fail
    And the output should contain "Samstraumr project already exists at this location"

  @Initialization @Negative
  Scenario: Attempt to initialize without a git repository
    Given a non-git temporary directory for testing
    When I run "s8r init" in the temporary directory
    Then the command should succeed
    And a git repository should be created in the temporary directory
    And the temporary directory should contain an initialized S8r project

  @Visualization @Positive
  Scenario: List components in an empty S8r project
    Given the temporary directory already contains an initialized S8r project
    When I run "s8r list" in the temporary directory
    Then the command should succeed
    And the output should contain an ASCII visualization of the project
    And the visualization should indicate an empty model

  @Visualization @Positive
  Scenario: List components in an S8r project with a machine
    Given the temporary directory already contains an initialized S8r project
    And the project has a machine named "TestMachine" with composites:
      | name     | type      |
      | input    | standard  |
      | process  | transform |
      | output   | standard  |
    And the machine has the following connections:
      | from    | to       |
      | input   | process  |
      | process | output   |
    When I run "s8r list" in the temporary directory
    Then the command should succeed
    And the output should contain an ASCII visualization of the project
    And the visualization should show the machine "TestMachine"
    And the visualization should show the 3 composites
    And the visualization should show the connections between composites

  @Visualization @Negative
  Scenario: Attempt to list components in a non-S8r directory
    Given a clean temporary directory
    When I run "s8r list" in the temporary directory
    Then the command should fail
    And the output should contain "Not a valid S8r project"