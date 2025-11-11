@L0_Unit # Copyright (c) 2025
# All rights reserved.

@integration @L1 @ports
Feature: Cache and FileSystem Integration
  As an application developer
  I want to ensure the Cache and FileSystem ports work together correctly
  So that I can implement persistent caching functionality

  Background:
    Given a cache system is initialized with name "test-cache"
    And a file system adapter is available

  @smoke
  Scenario: Cache data is persisted to the file system
    When I put data "test-value" with key "test-key" in the cache
    And I persist the cache to file "cache-data.json"
    And I clear the cache for integration test
    Then the cache should not contain integration key "test-key"
    When I load the cache from file "cache-data.json"
    Then the cache should contain integration key "test-key"
    And the integration value for key "test-key" should be "test-value"

  Scenario: Multiple cache entries are persisted and loaded correctly
    When I put the following data in the cache:
      | key    | value       |
      | key1   | value1      |
      | key2   | value2      |
      | key3   | value3      |
    And I persist the cache to file "multi-cache-data.json"
    And I clear the cache for integration test
    When I load the cache from file "multi-cache-data.json"
    Then the cache should contain the following entries:
      | key    | value       |
      | key1   | value1      |
      | key2   | value2      |
      | key3   | value3      |

  Scenario: Cache persists and loads complex data structures
    When I put a complex object in integration cache with key "complex-key"
    And I persist the cache to file "complex-cache-data.json"
    And I clear the cache for integration test
    When I load the cache from file "complex-cache-data.json"
    Then the cache should contain integration key "complex-key"
    And the complex object should be retrieved correctly