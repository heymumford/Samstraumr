@L2_Integration @Functional @PortInterface @Cache
Feature: Cache Port Interface
  As a system developer
  I want to use the CachePort interface for temporary data storage
  So that I can keep my application core independent of caching implementations

  Background:
    Given a clean system environment
    And the CachePort interface is properly initialized

  Scenario: Storing and retrieving a value in the cache
    When I store a string value "test value" with key "test-key" in the cache
    Then the value should be successfully stored
    And I should be able to retrieve the value using the key
    And the retrieved value should match the original value

  Scenario: Storing with expiration and checking expiration status
    When I store a value with key "expiring-key" and expiration time of 2 seconds
    Then the value should be available immediately
    When I wait for 3 seconds
    Then the key should be expired
    And retrieving the expired key should return null

  Scenario: Updating an existing cached value
    When I store a value "initial value" with key "update-key" in the cache
    And I update the value to "updated value" for the same key
    Then the cached value should be updated
    And retrieving the key should return the updated value

  Scenario: Removing a value from the cache
    When I store a value "removable" with key "remove-key" in the cache
    And I remove the value with key "remove-key" from the cache
    Then the key should be removed from the cache
    And retrieving the removed key should return null

  Scenario: Storing and retrieving complex objects
    When I store a complex object in the cache with key "complex-key"
    Then the object should be successfully stored
    And I should be able to retrieve the complex object
    And the retrieved object should match the original object

  Scenario: Cache eviction based on size limits
    When I store multiple values exceeding the cache size limit
    Then older values should be evicted from the cache
    And newer values should be available in the cache

  Scenario: Batch operations with the cache
    When I store multiple key-value pairs in a single batch operation:
      | key         | value           |
      | batch-key1  | batch value 1   |
      | batch-key2  | batch value 2   |
      | batch-key3  | batch value 3   |
    Then all batch values should be successfully stored
    And I should be able to retrieve all batch values
    And retrieving multiple keys in a batch should return all values

  Scenario: Cache statistics and monitoring
    When I perform various cache operations
    Then cache hit rate statistics should be tracked
    And cache size statistics should be tracked
    And cache eviction statistics should be tracked