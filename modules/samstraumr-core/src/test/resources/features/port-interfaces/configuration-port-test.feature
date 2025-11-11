@L2_Integration @Functional @PortInterface @Configuration
Feature: Configuration Port Interface
  As a system developer
  I want to use the ConfigurationPort interface for accessing configuration
  So that I can keep my application core independent of specific configuration mechanisms

  Background:
    Given a clean system environment
    And the ConfigurationPort interface is properly initialized
    And basic configuration values are set for testing

  Scenario: Retrieving string configuration values
    When I get a string configuration value for key "string.key"
    Then the result should be the string value "test string value"
    When I get a string configuration value for a non-existent key "non.existent.key"
    Then the result should be an empty optional
    When I get a string configuration value for key "non.existent.key" with default "default value"
    Then the result should be the default string value "default value"

  Scenario: Retrieving integer configuration values
    When I get an integer configuration value for key "integer.key"
    Then the result should be the integer value 42
    When I get an integer configuration value for a non-existent key "non.existent.key"
    Then the result should be an empty optional
    When I get an integer configuration value for key "non.existent.key" with default 100
    Then the result should be the default integer value 100
    When I get an integer configuration value for an invalid key "invalid.integer.key"
    Then the result should be an empty optional due to invalid format

  Scenario: Retrieving boolean configuration values
    When I get a boolean configuration value for key "boolean.true.key"
    Then the result should be the boolean value true
    When I get a boolean configuration value for key "boolean.false.key"
    Then the result should be the boolean value false
    When I get a boolean configuration value for a non-existent key "non.existent.key"
    Then the result should be an empty optional
    When I get a boolean configuration value for key "non.existent.key" with default true
    Then the result should be the default boolean value true

  Scenario: Retrieving string list configuration values
    When I get a string list configuration value for key "list.key"
    Then the result should be a list with values:
      | item1 |
      | item2 |
      | item3 |
    When I get a string list configuration value for a non-existent key "non.existent.key"
    Then the result should be an empty optional
    When I get a string list configuration value for key "non.existent.key" with default list:
      | default1 |
      | default2 |
    Then the result should be the default list

  Scenario: Getting configuration subsets
    When I get a configuration subset with prefix "subset.prefix."
    Then the result should be a map with entries:
      | key            | value           |
      | subset.prefix.key1 | subset value 1 |
      | subset.prefix.key2 | subset value 2 |
      | subset.prefix.key3 | subset value 3 |
    When I get a configuration subset with prefix "empty.prefix."
    Then the result should be an empty map

  Scenario: Checking key existence
    When I check if key "string.key" exists
    Then the key existence check should return true
    When I check if key "non.existent.key" exists
    Then the key existence check should return false

  Scenario Outline: Different boolean value representations
    When I get a boolean configuration value for key "<key>"
    Then the result should be the boolean value <expected>

    Examples:
      | key                | expected |
      | boolean.true.key   | true     |
      | boolean.yes.key    | true     |
      | boolean.1.key      | true     |
      | boolean.false.key  | false    |
      | boolean.no.key     | false    |
      | boolean.0.key      | false    |

  Scenario: Handling empty and null values
    When I get a string configuration value for key "empty.string.key"
    Then the result should be an empty string
    When I get a string list configuration value for key "empty.list.key"
    Then the result should be an empty list
    When I get a string configuration value for null key
    Then the result should be an empty optional
    When I get an integer configuration value for null key
    Then the result should be an empty optional
    When I get a boolean configuration value for null key
    Then the result should be an empty optional
    When I get a string list configuration value for null key
    Then the result should be an empty optional