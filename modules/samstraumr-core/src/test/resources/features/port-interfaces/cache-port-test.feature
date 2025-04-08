# Copyright (c) 2025
# All rights reserved.

@port @L1 
Feature: Cache Port Interface
  As an application developer
  I want to use a standardized Cache Port interface
  So that I can implement and switch different cache providers

  Background:
    Given a cache port implementation is available

  @smoke
  Scenario: Initialize a cache instance
    When I initialize the cache with name "test-cache"
    Then the cache should be initialized successfully
    And the cache should be empty

  Scenario: Store and retrieve data from cache
    Given the cache is initialized with name "test-cache"
    When I put an item with key "user-123" and value "John Doe" in the cache
    Then the cache should contain an item with key "user-123"
    And the value for key "user-123" should be "John Doe"

  Scenario: Remove data from cache
    Given the cache is initialized with name "test-cache"
    And the cache contains an item with key "user-123" and value "John Doe"
    When I remove the item with key "user-123" from the cache
    Then the cache should not contain an item with key "user-123"

  Scenario: Clear all data from cache
    Given the cache is initialized with name "test-cache"
    And the cache contains the following items:
      | key     | value       |
      | key1    | value1      |
      | key2    | value2      |
      | key3    | value3      |
    When I clear the cache
    Then the cache should be empty

  Scenario: Check if key exists in cache
    Given the cache is initialized with name "test-cache"
    And the cache contains an item with key "user-123" and value "John Doe"
    When I check if key "user-123" exists in the cache
    Then the key should exist
    When I check if key "non-existent" exists in the cache
    Then the key should not exist

  Scenario: Store and retrieve complex objects
    Given the cache is initialized with name "test-cache"
    When I store a complex object in the cache with key "user-object"
    Then I should be able to retrieve the complex object with key "user-object"
    And the retrieved object should match the original object