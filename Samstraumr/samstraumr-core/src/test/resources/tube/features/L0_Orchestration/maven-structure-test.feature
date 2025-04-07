@L0_Orchestration @ATL @MavenTest @Architecture
Feature: Maven Project Structure Validation
  As a developer
  I want to verify that the Maven project structure is correctly set up
  So that the build system works reliably and follows our architectural standards

  @Structure
  Scenario: Verify three-tier Maven structure
    Given the project has a root pom.xml file
    And the project has a module container pom.xml file
    And the project has a core module pom.xml file
    Then the root pom should contain modules section with "Samstraumr"
    And the module container pom should reference the "samstraumr-core" module
    And all pom files should have consistent versions

  @Dependencies
  Scenario: Verify dependency management
    Given the project has a root pom.xml file
    Then all dependencies should be managed in the parent pom
    And all plugin versions should be defined as properties
    And all plugins should be managed in pluginManagement section
    And no SNAPSHOT dependencies should be used in non-SNAPSHOT versions

  @Profiles
  Scenario: Verify Maven profiles for testing
    Given the project has a root pom.xml file
    Then the root pom should contain the following profiles:
      | Profile Name     |
      | atl-tests        |
      | skip-tests       |
      | tdd-development  |
      | coverage         |
      | quality-checks   |
      | security-checks  |
      | build-report     |
    And each profile should have the correct properties and plugins

  @CleanArchitecture
  Scenario: Verify architecture alignment
    Given the project has a core module pom.xml file
    Then the dependencies should respect Clean Architecture principles
    And domain layer should not depend on infrastructure implementations
    And all Java module dependencies should be correctly defined
    And test dependencies should have test scope

  @TestFramework
  Scenario: Verify test framework configuration
    Given the project has a core module pom.xml file
    Then JUnit and Cucumber dependencies should be properly configured
    And the surefire plugin should be configured for test discovery
    And the correct test resources should be filtered
    And test tag properties should be defined