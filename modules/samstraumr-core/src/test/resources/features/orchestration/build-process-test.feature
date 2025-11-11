@L0_Orchestration @ATL @Build
Feature: Samstraumr Build Process Validation
  As a Samstraumr developer
  I want to verify that the build process works correctly with different options
  So that I can ensure the build tooling is reliable and functional

  Background:
    Given a clean project environment
    And the S8r build tool is available

  @Fast @Smoke
  Scenario: Fast build mode skips tests and quality checks
    When I execute the build with "fast" mode
    Then the build should complete successfully
    And tests should be skipped
    And quality checks should be skipped
    And only compilation should be performed

  @Test
  Scenario: Test build mode runs tests but skips quality checks
    When I execute the build with "test" mode
    Then the build should complete successfully
    And tests should be executed
    And quality checks should be skipped

  @Package
  Scenario: Package build mode creates JAR artifacts
    When I execute the build with "package" mode
    Then the build should complete successfully
    And tests should be executed
    And JAR files should be created in the target directory

  @Install
  Scenario: Install build mode installs artifacts to local repository
    When I execute the build with "install" mode
    Then the build should complete successfully
    And artifacts should be installed to the local Maven repository

  @Clean
  Scenario: Clean option removes build artifacts before building
    Given previous build artifacts exist
    When I execute the build with "fast" mode and "--clean" option
    Then the build should complete successfully
    And all build artifacts should be removed before building
    And new build artifacts should be created

  @Parallel
  Scenario: Parallel build option builds modules in parallel
    When I execute the build with "fast" mode and "--parallel" option
    Then the build should complete successfully
    And the build should use parallel execution

  @Full
  Scenario: Full build mode runs all verification steps
    When I execute the build with "full" mode
    Then the build should complete successfully
    And tests should be executed
    And quality checks should be executed
    And code coverage should be measured

  @CI
  Scenario: CI option runs CI checks after build
    When I execute the build with "fast" mode and "--ci" option
    Then the build should complete successfully
    And CI checks should be executed

  @Verbose
  Scenario: Verbose option shows detailed build information
    When I execute the build with "fast" mode and "--verbose" option
    Then the build should complete successfully
    And verbose build output should be displayed

  @Error
  Scenario: Build reports errors correctly when build fails
    Given a project with compilation errors
    When I execute the build with "fast" mode
    Then the build should fail
    And error messages should be displayed

  @MultiOption
  Scenario: Build with multiple options combines their effects
    When I execute the build with "test" mode and "--clean --parallel" options
    Then the build should complete successfully
    And all build artifacts should be removed before building
    And the build should use parallel execution
    And tests should be executed