@Configuration @PortTest @port
Feature: Configuration Port Interface
  As a developer
  I want to have a port interface for configuration management
  So that I can load, access, and modify application configuration properties

  Background:
    Given a configuration service

  @Smoke
  Scenario: Read string configuration values
    Given a configuration service with default properties
    Then I should get string value "Samstraumr" for key "app.name"
    And I should get string value "2.7.1" for key "app.version"
    And I should get string value "DefaultValue" for key "nonexistent" with default "DefaultValue"

  @Smoke
  Scenario: Read integer configuration values
    Given a configuration service with default properties
    Then I should get integer value 100 for key "app.maxConnections"
    And I should get integer value 42 for key "nonexistent" with default 42
    
  @Smoke
  Scenario: Read boolean configuration values
    Given a configuration service with default properties
    Then I should get boolean value "true" for key "app.debug"
    And I should get boolean value "true" for key "module.cache.enabled"
    And I should get boolean value "false" for key "module.auth.enabled"
    And I should get boolean value "true" for key "nonexistent" with default "true"

  Scenario: Set and retrieve configuration properties
    When I set a string property "test.string" to "test-value"
    And I set an integer property "test.integer" to 42
    And I set a boolean property "test.boolean" to "true"
    Then I should get string value "test-value" for key "test.string"
    And I should get integer value 42 for key "test.integer"
    And I should get boolean value "true" for key "test.boolean"

  Scenario: Remove configuration properties
    Given a configuration service with default properties
    When I remove the property "app.name"
    Then the property "app.name" should not exist
    And the operation result should be "true"
    When I remove the property "nonexistent"
    Then the operation result should be "false"

  Scenario: Check if properties exist
    Given a configuration service with default properties
    When I check if property "app.name" exists
    Then the operation result should be "true"
    When I check if property "nonexistent" exists
    Then the operation result should be "false"

  Scenario: Get all configuration properties
    Given a configuration service with default properties
    When I get all properties
    Then I should get 8 properties
    And the filtered properties should contain key "app.name"
    And the filtered properties should contain key "app.version"
    And the filtered properties should not contain key "nonexistent"

  Scenario: Get properties with prefix
    Given a configuration service with default properties
    When I get properties with prefix "module."
    Then I should get 3 properties
    And the filtered properties should contain key "module.cache.enabled"
    And the filtered properties should contain key "module.cache.size"
    And the filtered properties should contain key "module.auth.enabled"
    And the filtered properties should not contain key "app.name"
    
    When I get properties with prefix "module.cache."
    Then I should get 2 properties
    And the filtered properties should contain key "module.cache.enabled"
    And the filtered properties should contain key "module.cache.size"
    And the filtered properties should not contain key "module.auth.enabled"

  Scenario: Split comma-separated values into list
    Given a configuration service with default properties
    When I get a string list for key "app.tags"
    Then the string list should contain values "java,clean-architecture,testing"

  Scenario: Load configuration from file
    Given a configuration service
    And a properties file with test data
    When I load a configuration file
    Then the configuration should be loaded successfully

  Scenario: Save configuration to file
    Given a configuration service with default properties
    When I save the configuration to a file
    Then the configuration should be saved successfully

  Scenario: Clear all configuration properties
    Given a configuration service with default properties
    When I clear all configuration properties
    Then the configuration should be empty