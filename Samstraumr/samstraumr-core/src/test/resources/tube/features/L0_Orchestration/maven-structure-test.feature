# Copyright (c) 2025
# All rights reserved.

@maven-structure @L0 @architecture
Feature: Maven Project Structure Tests
  As a developer
  I want to ensure the Maven project structure follows conventions
  So that the project is organized and maintainable

  Background:
    Given I am in the project root directory

  @smoke
  Scenario: Verify basic Maven structure
    Then each module should have a pom.xml file
    And the core module should have the clean architecture layers

  Scenario: Verify required Maven files
    When I look for the file "pom.xml"
    Then the file should exist
    When I look for the file "Samstraumr/pom.xml"
    Then the file should exist
    When I look for the file "Samstraumr/samstraumr-core/pom.xml"
    Then the file should exist

  Scenario: Verify source directories
    When I check for the directory "Samstraumr/samstraumr-core/src/main/java"
    Then the directory should exist
    When I check for the directory "Samstraumr/samstraumr-core/src/test/java"
    Then the directory should exist
    When I check for the directory "Samstraumr/samstraumr-core/src/main/resources"
    Then the directory should exist
    When I check for the directory "Samstraumr/samstraumr-core/src/test/resources"
    Then the directory should exist

  Scenario: Verify required packages
    Then the following packages should exist:
      | domain |
      | application |
      | infrastructure |
      | component |
      | component.identity |
      | component.composite |
      | component.machine |
      | application.port |
      | application.service |

  Scenario: Verify port interface packages
    Then the following packages should exist:
      | application.port |
      | infrastructure.cache |
      | infrastructure.filesystem |
      | infrastructure.notification |
      | infrastructure.event |
      | infrastructure.security |