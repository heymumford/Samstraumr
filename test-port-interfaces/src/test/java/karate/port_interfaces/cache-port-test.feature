Feature: Cache Port Interface Tests
  As an application developer
  I want to use a standardized Cache Port interface
  So that I can implement and switch different cache providers

  Background:
    * def cacheAdapter = Java.type('org.s8r.infrastructure.cache.EnhancedInMemoryCacheAdapter').createInstance()
    * def cacheName = 'test-cache-' + Java.type('java.util.UUID').randomUUID()
    * def testObject = { id: 123, name: 'Test User', roles: ['USER', 'ADMIN'], active: true }

  Scenario: Initialize a cache instance
    When cacheAdapter.initialize(cacheName)
    Then assert cacheAdapter.getCacheName() == cacheName
    And assert cacheAdapter.size() == 0

  Scenario: Store and retrieve data from cache
    Given cacheAdapter.initialize(cacheName)
    When cacheAdapter.put('user-123', 'John Doe')
    Then assert cacheAdapter.containsKey('user-123')
    And def result = cacheAdapter.get('user-123')
    And assert result.isPresent()
    And assert result.get() == 'John Doe'

  Scenario: Remove data from cache
    Given cacheAdapter.initialize(cacheName)
    And cacheAdapter.put('user-123', 'John Doe')
    When def removed = cacheAdapter.remove('user-123')
    Then assert removed
    And assert cacheAdapter.containsKey('user-123') == false

  Scenario: Clear all data from cache
    Given cacheAdapter.initialize(cacheName)
    And cacheAdapter.put('key1', 'value1')
    And cacheAdapter.put('key2', 'value2')
    And cacheAdapter.put('key3', 'value3')
    When cacheAdapter.clear()
    Then assert cacheAdapter.size() == 0
    And assert cacheAdapter.containsKey('key1') == false
    And assert cacheAdapter.containsKey('key2') == false
    And assert cacheAdapter.containsKey('key3') == false

  Scenario: Check if key exists in cache
    Given cacheAdapter.initialize(cacheName)
    And cacheAdapter.put('user-123', 'John Doe')
    When def exists = cacheAdapter.containsKey('user-123')
    Then assert exists
    When def nonExistentKeyExists = cacheAdapter.containsKey('non-existent')
    Then assert !nonExistentKeyExists

  Scenario: Store and retrieve complex objects
    Given cacheAdapter.initialize(cacheName)
    When cacheAdapter.put('user-object', testObject)
    Then def result = cacheAdapter.get('user-object')
    And assert result.isPresent()
    And def retrievedObject = result.get()
    And assert retrievedObject.id == testObject.id
    And assert retrievedObject.name == testObject.name
    And assert retrievedObject.active == testObject.active
    And match retrievedObject.roles == testObject.roles

  Scenario: Cache region-based operations
    Given cacheAdapter.initialize(cacheName)
    And def region = Java.type('org.s8r.application.port.CachePort$CacheRegion').USER
    And def duration = Java.type('java.time.Duration').ofMinutes(5)
    When def result = cacheAdapter.put(region, 'profile', testObject, duration)
    Then assert result.isSuccessful()
    And def getResult = cacheAdapter.get(region, 'profile', 'java.lang.Object')
    And assert getResult.isSuccessful()
    And def value = getResult.getAttributes().get('value')
    And assert value.id == testObject.id
    And assert value.name == testObject.name

  Scenario: Retrieve cache statistics
    Given cacheAdapter.initialize(cacheName)
    And cacheAdapter.put('key1', 'value1')
    And def result1 = cacheAdapter.get('key1')
    And def result2 = cacheAdapter.get('non-existent')
    When def statsResult = cacheAdapter.getStatistics()
    Then assert statsResult.isSuccessful()
    And def stats = statsResult.getAttributes()
    And assert stats.hits >= 1
    And assert stats.misses >= 1
    And assert stats.puts >= 1