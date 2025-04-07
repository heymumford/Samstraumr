/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools 
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights 
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.mozilla.org/en-US/MPL/2.0/
 */

package org.s8r.test.steps;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.s8r.application.port.CachePort;
import org.s8r.application.port.CachePort.CacheRegion;
import org.s8r.application.port.CachePort.CacheResult;
import org.s8r.application.port.LoggerPort;
import org.s8r.infrastructure.logging.ConsoleLogger;
import org.s8r.test.annotation.IntegrationTest;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for testing the CachePort interface.
 */
@IntegrationTest
public class CachePortSteps {

    private CachePort cachePort;
    private LoggerPort logger;
    private Map<String, Object> testContext;
    private List<String> logMessages;

    @Before
    public void setup() {
        testContext = new HashMap<>();
        logMessages = new ArrayList<>();
        
        // Create a test logger that captures log messages
        logger = new ConsoleLogger() {
            @Override
            public void info(String message) {
                super.info(message);
                logMessages.add(message);
            }
            
            @Override
            public void info(String message, Object... args) {
                super.info(message, args);
                logMessages.add(String.format(message.replace("{}", "%s"), args));
            }
        };
        
        // Initialize the cache port with test implementation
        cachePort = new TestCacheAdapter(logger);
        
        // Initialize the cache
        CacheResult initResult = cachePort.initialize();
        assertTrue(initResult.isSuccessful(), "Cache initialization should succeed");
        
        // Reset log messages between scenarios
        logMessages.clear();
    }
    
    @After
    public void cleanup() {
        testContext.clear();
        logMessages.clear();
        
        // Shutdown the cache
        cachePort.shutdown();
    }

    @Given("a clean system environment")
    public void aCleanSystemEnvironment() {
        // Already set up in the @Before method
        assertNotNull(cachePort, "CachePort should be initialized");
    }

    @Given("the CachePort interface is properly initialized")
    public void theCachePortInterfaceIsProperlyInitialized() {
        // Verify the cache port is properly initialized
        assertNotNull(cachePort, "CachePort should be initialized");
        
        // Clear all cache data to ensure a clean state
        CacheResult clearResult = cachePort.clearAll();
        assertTrue(clearResult.isSuccessful(), "Cache clearing should succeed");
    }

    @When("I store a string value {string} with key {string} in the cache")
    public void iStoreAStringValueWithKeyInTheCache(String value, String key) {
        CacheResult result = cachePort.put(CacheRegion.GENERAL, key, value);
        
        assertNotNull(result, "Cache put result should not be null");
        testContext.put("putResult", result);
        testContext.put("key", key);
        testContext.put("value", value);
    }

    @When("I store a value with key {string} and expiration time of {int} seconds")
    public void iStoreAValueWithKeyAndExpirationTimeOfSeconds(String key, int seconds) {
        String value = "expiring value";
        CacheResult result = cachePort.put(CacheRegion.GENERAL, key, value, Duration.ofSeconds(seconds));
        
        assertNotNull(result, "Cache put result should not be null");
        testContext.put("putResult", result);
        testContext.put("expiringKey", key);
        testContext.put("expiringValue", value);
        testContext.put("expirationSeconds", seconds);
    }

    @When("I wait for {int} seconds")
    public void iWaitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Thread was interrupted during wait: " + e.getMessage());
        }
    }

    @When("I update the value to {string} for the same key")
    public void iUpdateTheValueToForTheSameKey(String updatedValue) {
        String key = (String) testContext.get("key");
        assertNotNull(key, "Key should be in the test context");
        
        CacheResult result = cachePort.put(CacheRegion.GENERAL, key, updatedValue);
        
        assertNotNull(result, "Cache update result should not be null");
        testContext.put("updateResult", result);
        testContext.put("updatedValue", updatedValue);
    }

    @When("I remove the value with key {string} from the cache")
    public void iRemoveTheValueWithKeyFromTheCache(String key) {
        CacheResult result = cachePort.remove(CacheRegion.GENERAL, key);
        
        assertNotNull(result, "Cache remove result should not be null");
        testContext.put("removeResult", result);
        testContext.put("removedKey", key);
    }

    @When("I store a complex object in the cache with key {string}")
    public void iStoreAComplexObjectInTheCacheWithKey(String key) {
        // Create a complex test object (using a map for simplicity)
        Map<String, Object> complexObject = new HashMap<>();
        complexObject.put("name", "Test Object");
        complexObject.put("id", 12345);
        complexObject.put("active", true);
        complexObject.put("tags", List.of("test", "complex", "object"));
        
        CacheResult result = cachePort.put(CacheRegion.GENERAL, key, complexObject);
        
        assertNotNull(result, "Cache put result should not be null");
        testContext.put("complexKey", key);
        testContext.put("complexObject", complexObject);
        testContext.put("complexPutResult", result);
    }

    @When("I store multiple values exceeding the cache size limit")
    public void iStoreMultipleValuesExceedingTheCacheSizeLimit() {
        // Store many values to trigger eviction (assuming a small test cache size)
        List<String> storedKeys = new ArrayList<>();
        
        for (int i = 0; i < 100; i++) {
            String key = "eviction-key-" + i;
            String value = "value-" + i;
            
            cachePort.put(CacheRegion.GENERAL, key, value);
            storedKeys.add(key);
        }
        
        testContext.put("evictionKeys", storedKeys);
    }

    @When("I store multiple key-value pairs in a single batch operation:")
    public void iStoreMultipleKeyValuePairsInASingleBatchOperation(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        Map<String, String> batchValues = new HashMap<>();
        
        // Store values from the data table
        for (Map<String, String> row : rows) {
            String key = row.get("key");
            String value = row.get("value");
            
            batchValues.put(key, value);
            cachePort.put(CacheRegion.GENERAL, key, value);
        }
        
        testContext.put("batchValues", batchValues);
    }

    @When("I perform various cache operations")
    public void iPerformVariousCacheOperations() {
        // Perform various operations to generate statistics
        
        // Some successful gets
        cachePort.put(CacheRegion.GENERAL, "stats-key-1", "value1");
        cachePort.put(CacheRegion.GENERAL, "stats-key-2", "value2");
        
        // Hits
        cachePort.get(CacheRegion.GENERAL, "stats-key-1", String.class);
        cachePort.get(CacheRegion.GENERAL, "stats-key-2", String.class);
        cachePort.get(CacheRegion.GENERAL, "stats-key-1", String.class);
        
        // Misses
        cachePort.get(CacheRegion.GENERAL, "missing-key-1", String.class);
        cachePort.get(CacheRegion.GENERAL, "missing-key-2", String.class);
        
        // Updates
        cachePort.put(CacheRegion.GENERAL, "stats-key-1", "updated-value-1");
        
        // Removals
        cachePort.remove(CacheRegion.GENERAL, "stats-key-2");
        
        // Evictions (simulate by adding many entries assuming a small test cache)
        for (int i = 0; i < 50; i++) {
            cachePort.put(CacheRegion.GENERAL, "evict-key-" + i, "value-" + i);
        }
        
        // Get the statistics
        CacheResult statsResult = cachePort.getStatistics();
        testContext.put("statistics", statsResult);
    }

    @Then("the value should be successfully stored")
    public void theValueShouldBeSuccessfullyStored() {
        CacheResult result = (CacheResult) testContext.get("putResult");
        assertNotNull(result, "Put result should be in the test context");
        
        assertTrue(result.isSuccessful(), "Cache put operation should be successful");
    }

    @Then("I should be able to retrieve the value using the key")
    public void iShouldBeAbleToRetrieveTheValueUsingTheKey() {
        String key = (String) testContext.get("key");
        assertNotNull(key, "Key should be in the test context");
        
        CacheResult getResult = cachePort.get(CacheRegion.GENERAL, key, String.class);
        
        assertNotNull(getResult, "Cache get result should not be null");
        assertTrue(getResult.isSuccessful(), "Cache get operation should be successful");
        
        testContext.put("getResult", getResult);
    }

    @Then("the retrieved value should match the original value")
    public void theRetrievedValueShouldMatchTheOriginalValue() {
        String originalValue = (String) testContext.get("value");
        CacheResult getResult = (CacheResult) testContext.get("getResult");
        
        assertNotNull(originalValue, "Original value should be in the test context");
        assertNotNull(getResult, "Get result should be in the test context");
        
        Object retrievedValue = getResult.getAttributes().get("value");
        assertEquals(originalValue, retrievedValue, "Retrieved value should match original value");
    }

    @Then("the value should be available immediately")
    public void theValueShouldBeAvailableImmediately() {
        String key = (String) testContext.get("expiringKey");
        String expectedValue = (String) testContext.get("expiringValue");
        
        assertNotNull(key, "Expiring key should be in the test context");
        assertNotNull(expectedValue, "Expiring value should be in the test context");
        
        CacheResult getResult = cachePort.get(CacheRegion.GENERAL, key, String.class);
        
        assertNotNull(getResult, "Cache get result should not be null");
        assertTrue(getResult.isSuccessful(), "Cache get operation should be successful");
        
        Object retrievedValue = getResult.getAttributes().get("value");
        assertEquals(expectedValue, retrievedValue, "Retrieved value should match expected value");
    }

    @Then("the key should be expired")
    public void theKeyShouldBeExpired() {
        String key = (String) testContext.get("expiringKey");
        assertNotNull(key, "Expiring key should be in the test context");
        
        boolean keyExists = cachePort.containsKey(CacheRegion.GENERAL, key);
        assertFalse(keyExists, "Key should be expired and not exist in the cache");
    }

    @Then("retrieving the expired key should return null")
    public void retrievingTheExpiredKeyShouldReturnNull() {
        String key = (String) testContext.get("expiringKey");
        assertNotNull(key, "Expiring key should be in the test context");
        
        CacheResult getResult = cachePort.get(CacheRegion.GENERAL, key, String.class);
        
        assertFalse(getResult.isSuccessful(), "Cache get operation should not be successful for expired key");
        assertTrue(getResult.getReason().isPresent(), "Get result should have a reason for failure");
        assertTrue(getResult.getReason().get().contains("expired") 
                || getResult.getReason().get().contains("not found"), 
                "Reason should indicate the key is expired or not found");
    }

    @Then("the cached value should be updated")
    public void theCachedValueShouldBeUpdated() {
        CacheResult result = (CacheResult) testContext.get("updateResult");
        assertNotNull(result, "Update result should be in the test context");
        
        assertTrue(result.isSuccessful(), "Cache update operation should be successful");
    }

    @Then("retrieving the key should return the updated value")
    public void retrievingTheKeyShouldReturnTheUpdatedValue() {
        String key = (String) testContext.get("key");
        String updatedValue = (String) testContext.get("updatedValue");
        
        assertNotNull(key, "Key should be in the test context");
        assertNotNull(updatedValue, "Updated value should be in the test context");
        
        CacheResult getResult = cachePort.get(CacheRegion.GENERAL, key, String.class);
        
        assertNotNull(getResult, "Cache get result should not be null");
        assertTrue(getResult.isSuccessful(), "Cache get operation should be successful");
        
        Object retrievedValue = getResult.getAttributes().get("value");
        assertEquals(updatedValue, retrievedValue, "Retrieved value should match updated value");
    }

    @Then("the key should be removed from the cache")
    public void theKeyShouldBeRemovedFromTheCache() {
        String key = (String) testContext.get("removedKey");
        assertNotNull(key, "Removed key should be in the test context");
        
        boolean keyExists = cachePort.containsKey(CacheRegion.GENERAL, key);
        assertFalse(keyExists, "Key should not exist in the cache after removal");
    }

    @Then("retrieving the removed key should return null")
    public void retrievingTheRemovedKeyShouldReturnNull() {
        String key = (String) testContext.get("removedKey");
        assertNotNull(key, "Removed key should be in the test context");
        
        CacheResult getResult = cachePort.get(CacheRegion.GENERAL, key, String.class);
        
        assertFalse(getResult.isSuccessful(), "Cache get operation should not be successful for removed key");
        assertTrue(getResult.getReason().isPresent(), "Get result should have a reason for failure");
        assertTrue(getResult.getReason().get().contains("not found"), 
                "Reason should indicate the key is not found");
    }

    @Then("the object should be successfully stored")
    public void theObjectShouldBeSuccessfullyStored() {
        CacheResult result = (CacheResult) testContext.get("complexPutResult");
        assertNotNull(result, "Complex put result should be in the test context");
        
        assertTrue(result.isSuccessful(), "Cache put operation should be successful for complex object");
    }

    @Then("I should be able to retrieve the complex object")
    public void iShouldBeAbleToRetrieveTheComplexObject() {
        String key = (String) testContext.get("complexKey");
        assertNotNull(key, "Complex key should be in the test context");
        
        // Use Map.class as the type for the complex object
        CacheResult getResult = cachePort.get(CacheRegion.GENERAL, key, Map.class);
        
        assertNotNull(getResult, "Cache get result should not be null");
        assertTrue(getResult.isSuccessful(), "Cache get operation should be successful");
        
        testContext.put("complexGetResult", getResult);
    }

    @Then("the retrieved object should match the original object")
    public void theRetrievedObjectShouldMatchTheOriginalObject() {
        @SuppressWarnings("unchecked")
        Map<String, Object> originalObject = (Map<String, Object>) testContext.get("complexObject");
        CacheResult getResult = (CacheResult) testContext.get("complexGetResult");
        
        assertNotNull(originalObject, "Original complex object should be in the test context");
        assertNotNull(getResult, "Complex get result should be in the test context");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> retrievedObject = (Map<String, Object>) getResult.getAttributes().get("value");
        
        assertNotNull(retrievedObject, "Retrieved object should not be null");
        assertEquals(originalObject.size(), retrievedObject.size(), "Retrieved object should have same size");
        
        // Check key-value pairs
        for (Map.Entry<String, Object> entry : originalObject.entrySet()) {
            assertTrue(retrievedObject.containsKey(entry.getKey()), 
                    "Retrieved object should contain key: " + entry.getKey());
            
            // For lists, compare contents rather than reference
            if (entry.getValue() instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> originalList = (List<Object>) entry.getValue();
                @SuppressWarnings("unchecked")
                List<Object> retrievedList = (List<Object>) retrievedObject.get(entry.getKey());
                
                assertEquals(originalList.size(), retrievedList.size(), 
                        "Retrieved list should have same size");
                
                for (int i = 0; i < originalList.size(); i++) {
                    assertEquals(originalList.get(i), retrievedList.get(i), 
                            "List elements should match");
                }
            } else {
                assertEquals(entry.getValue(), retrievedObject.get(entry.getKey()), 
                        "Values for key " + entry.getKey() + " should match");
            }
        }
    }

    @Then("older values should be evicted from the cache")
    public void olderValuesShouldBeEvictedFromTheCache() {
        @SuppressWarnings("unchecked")
        List<String> storedKeys = (List<String>) testContext.get("evictionKeys");
        assertNotNull(storedKeys, "Stored keys should be in the test context");
        
        // Check that early entries were evicted
        boolean someEvicted = false;
        for (int i = 0; i < 20; i++) {  // Check first 20 entries
            String key = storedKeys.get(i);
            if (!cachePort.containsKey(CacheRegion.GENERAL, key)) {
                someEvicted = true;
                break;
            }
        }
        
        assertTrue(someEvicted, "Some older entries should be evicted");
    }

    @Then("newer values should be available in the cache")
    public void newerValuesShouldBeAvailableInTheCache() {
        @SuppressWarnings("unchecked")
        List<String> storedKeys = (List<String>) testContext.get("evictionKeys");
        assertNotNull(storedKeys, "Stored keys should be in the test context");
        
        // Check that more recent entries are still in the cache
        boolean someRetained = false;
        for (int i = 80; i < 100; i++) {  // Check last 20 entries
            String key = storedKeys.get(i);
            if (cachePort.containsKey(CacheRegion.GENERAL, key)) {
                someRetained = true;
                break;
            }
        }
        
        assertTrue(someRetained, "Some newer entries should be retained");
    }

    @Then("all batch values should be successfully stored")
    public void allBatchValuesShouldBeSuccessfullyStored() {
        @SuppressWarnings("unchecked")
        Map<String, String> batchValues = (Map<String, String>) testContext.get("batchValues");
        assertNotNull(batchValues, "Batch values should be in the test context");
        
        // Check that all keys exist in the cache
        for (String key : batchValues.keySet()) {
            assertTrue(cachePort.containsKey(CacheRegion.GENERAL, key), 
                    "Key should exist in the cache: " + key);
        }
    }

    @Then("I should be able to retrieve all batch values")
    public void iShouldBeAbleToRetrieveAllBatchValues() {
        @SuppressWarnings("unchecked")
        Map<String, String> batchValues = (Map<String, String>) testContext.get("batchValues");
        assertNotNull(batchValues, "Batch values should be in the test context");
        
        // Check that all values can be retrieved
        for (Map.Entry<String, String> entry : batchValues.entrySet()) {
            CacheResult getResult = cachePort.get(CacheRegion.GENERAL, entry.getKey(), String.class);
            
            assertTrue(getResult.isSuccessful(), 
                    "Cache get operation should be successful for key: " + entry.getKey());
            
            Object retrievedValue = getResult.getAttributes().get("value");
            assertEquals(entry.getValue(), retrievedValue, 
                    "Retrieved value should match expected for key: " + entry.getKey());
        }
    }

    @Then("retrieving multiple keys in a batch should return all values")
    public void retrievingMultipleKeysInABatchShouldReturnAllValues() {
        // This would ideally test a batch get operation, but since our interface
        // doesn't have a batch get, we'll verify individual gets
        
        @SuppressWarnings("unchecked")
        Map<String, String> batchValues = (Map<String, String>) testContext.get("batchValues");
        assertNotNull(batchValues, "Batch values should be in the test context");
        
        for (Map.Entry<String, String> entry : batchValues.entrySet()) {
            CacheResult getResult = cachePort.get(CacheRegion.GENERAL, entry.getKey(), String.class);
            
            assertTrue(getResult.isSuccessful(), 
                    "Cache get operation should be successful for key: " + entry.getKey());
            
            Object retrievedValue = getResult.getAttributes().get("value");
            assertEquals(entry.getValue(), retrievedValue, 
                    "Retrieved value should match expected for key: " + entry.getKey());
        }
    }

    @Then("cache hit rate statistics should be tracked")
    public void cacheHitRateStatisticsShouldBeTracked() {
        CacheResult statsResult = (CacheResult) testContext.get("statistics");
        assertNotNull(statsResult, "Statistics result should be in the test context");
        
        assertTrue(statsResult.isSuccessful(), "Statistics retrieval should be successful");
        
        // Check for hit rate in statistics
        Map<String, Object> attributes = statsResult.getAttributes();
        assertTrue(attributes.containsKey("hitRate") || attributes.containsKey("hits") || 
                attributes.containsKey("misses"), "Statistics should include hit rate information");
    }

    @Then("cache size statistics should be tracked")
    public void cacheSizeStatisticsShouldBeTracked() {
        CacheResult statsResult = (CacheResult) testContext.get("statistics");
        assertNotNull(statsResult, "Statistics result should be in the test context");
        
        assertTrue(statsResult.isSuccessful(), "Statistics retrieval should be successful");
        
        // Check for size information in statistics
        Map<String, Object> attributes = statsResult.getAttributes();
        assertTrue(attributes.containsKey("size") || attributes.containsKey("count") || 
                attributes.containsKey("entries"), "Statistics should include size information");
    }

    @Then("cache eviction statistics should be tracked")
    public void cacheEvictionStatisticsShouldBeTracked() {
        CacheResult statsResult = (CacheResult) testContext.get("statistics");
        assertNotNull(statsResult, "Statistics result should be in the test context");
        
        assertTrue(statsResult.isSuccessful(), "Statistics retrieval should be successful");
        
        // Check for eviction information in statistics
        Map<String, Object> attributes = statsResult.getAttributes();
        assertTrue(attributes.containsKey("evictions") || attributes.containsKey("replacements") || 
                attributes.containsKey("expirations"), "Statistics should include eviction information");
    }
    
    /**
     * A test implementation of CachePort that provides the necessary functionality
     * for the BDD tests. This is a simple in-memory cache implementation.
     */
    private static class TestCacheAdapter implements CachePort {
        private final LoggerPort logger;
        private final Map<CacheRegion, Map<String, CacheEntry>> cache;
        private boolean initialized;
        private int maxEntries = 50;  // Small size to test eviction
        private int hits;
        private int misses;
        private int evictions;
        
        public TestCacheAdapter(LoggerPort logger) {
            this.logger = logger;
            this.cache = new HashMap<>();
            for (CacheRegion region : CacheRegion.values()) {
                cache.put(region, new HashMap<>());
            }
            this.initialized = false;
            this.hits = 0;
            this.misses = 0;
            this.evictions = 0;
        }
        
        @Override
        public CacheResult initialize() {
            if (initialized) {
                return CacheResult.failure("Cache already initialized", "Already initialized");
            }
            
            initialized = true;
            logger.info("Cache initialized");
            
            return CacheResult.success("Cache initialized successfully");
        }
        
        @Override
        public <T> CacheResult get(CacheRegion region, String key, Class<T> type) {
            if (!initialized) {
                return CacheResult.failure("Cache not initialized", "Not initialized");
            }
            
            Map<String, CacheEntry> regionCache = cache.get(region);
            CacheEntry entry = regionCache.get(key);
            
            if (entry == null) {
                misses++;
                logger.info("Cache miss for key: {} in region: {}", key, region);
                return CacheResult.failure("Key not found", "not_found", Map.of("key", key, "region", region));
            }
            
            // Check if expired
            if (entry.isExpired()) {
                regionCache.remove(key);
                misses++;
                logger.info("Cache entry expired: {} in region: {}", key, region);
                return CacheResult.failure("Key expired", "expired", Map.of("key", key, "region", region));
            }
            
            // Verify type compatibility
            if (!type.isInstance(entry.getValue())) {
                misses++;
                logger.info("Cache type mismatch for key: {} in region: {}", key, region);
                return CacheResult.failure("Type mismatch", "type_mismatch", 
                        Map.of("key", key, "region", region, "expectedType", type.getName(), 
                              "actualType", entry.getValue().getClass().getName()));
            }
            
            hits++;
            logger.info("Cache hit for key: {} in region: {}", key, region);
            
            @SuppressWarnings("unchecked")
            T value = (T) entry.getValue();
            return CacheResult.success("Value retrieved successfully", 
                    Map.of("key", key, "region", region, "value", value));
        }
        
        @Override
        public <T> CacheResult put(CacheRegion region, String key, T value) {
            if (!initialized) {
                return CacheResult.failure("Cache not initialized", "Not initialized");
            }
            
            Map<String, CacheEntry> regionCache = cache.get(region);
            
            // Check if we need to evict
            if (!regionCache.containsKey(key) && regionCache.size() >= maxEntries) {
                evictOldest(region);
            }
            
            CacheEntry entry = new CacheEntry(value);
            regionCache.put(key, entry);
            
            logger.info("Value stored in cache: key={}, region={}", key, region);
            
            return CacheResult.success("Value stored successfully", 
                    Map.of("key", key, "region", region));
        }
        
        @Override
        public <T> CacheResult put(CacheRegion region, String key, T value, Duration ttl) {
            if (!initialized) {
                return CacheResult.failure("Cache not initialized", "Not initialized");
            }
            
            Map<String, CacheEntry> regionCache = cache.get(region);
            
            // Check if we need to evict
            if (!regionCache.containsKey(key) && regionCache.size() >= maxEntries) {
                evictOldest(region);
            }
            
            CacheEntry entry = new CacheEntry(value, ttl);
            regionCache.put(key, entry);
            
            logger.info("Value stored in cache with TTL: key={}, region={}, ttl={}s", 
                    key, region, ttl.getSeconds());
            
            return CacheResult.success("Value stored successfully with TTL", 
                    Map.of("key", key, "region", region, "ttl", ttl.toMillis()));
        }
        
        @Override
        public <T> CacheResult getOrCompute(CacheRegion region, String key, Class<T> type, 
                java.util.function.Function<String, T> provider) {
            
            if (!initialized) {
                return CacheResult.failure("Cache not initialized", "Not initialized");
            }
            
            CacheResult getResult = get(region, key, type);
            if (getResult.isSuccessful()) {
                return getResult;
            }
            
            // Compute the value
            T value = provider.apply(key);
            if (value == null) {
                return CacheResult.failure("Provider returned null value", "null_value", 
                        Map.of("key", key, "region", region));
            }
            
            // Store in cache
            CacheResult putResult = put(region, key, value);
            if (!putResult.isSuccessful()) {
                return putResult;
            }
            
            logger.info("Value computed and stored: key={}, region={}", key, region);
            
            return CacheResult.success("Value computed and stored successfully", 
                    Map.of("key", key, "region", region, "value", value, "computed", true));
        }
        
        @Override
        public <T> CacheResult getOrCompute(CacheRegion region, String key, Class<T> type, 
                java.util.function.Function<String, T> provider, Duration ttl) {
            
            if (!initialized) {
                return CacheResult.failure("Cache not initialized", "Not initialized");
            }
            
            CacheResult getResult = get(region, key, type);
            if (getResult.isSuccessful()) {
                return getResult;
            }
            
            // Compute the value
            T value = provider.apply(key);
            if (value == null) {
                return CacheResult.failure("Provider returned null value", "null_value", 
                        Map.of("key", key, "region", region));
            }
            
            // Store in cache with TTL
            CacheResult putResult = put(region, key, value, ttl);
            if (!putResult.isSuccessful()) {
                return putResult;
            }
            
            logger.info("Value computed and stored with TTL: key={}, region={}, ttl={}s", 
                    key, region, ttl.getSeconds());
            
            return CacheResult.success("Value computed and stored successfully with TTL", 
                    Map.of("key", key, "region", region, "value", value, 
                          "computed", true, "ttl", ttl.toMillis()));
        }
        
        @Override
        public CacheResult remove(CacheRegion region, String key) {
            if (!initialized) {
                return CacheResult.failure("Cache not initialized", "Not initialized");
            }
            
            Map<String, CacheEntry> regionCache = cache.get(region);
            if (regionCache.remove(key) != null) {
                logger.info("Value removed from cache: key={}, region={}", key, region);
                return CacheResult.success("Value removed successfully", 
                        Map.of("key", key, "region", region));
            } else {
                logger.info("Key not found for removal: key={}, region={}", key, region);
                return CacheResult.failure("Key not found", "not_found", 
                        Map.of("key", key, "region", region));
            }
        }
        
        @Override
        public boolean containsKey(CacheRegion region, String key) {
            if (!initialized) {
                return false;
            }
            
            Map<String, CacheEntry> regionCache = cache.get(region);
            CacheEntry entry = regionCache.get(key);
            
            if (entry == null) {
                return false;
            }
            
            // Check if expired
            if (entry.isExpired()) {
                regionCache.remove(key);
                return false;
            }
            
            return true;
        }
        
        @Override
        public CacheResult getKeys(CacheRegion region) {
            if (!initialized) {
                return CacheResult.failure("Cache not initialized", "Not initialized");
            }
            
            Map<String, CacheEntry> regionCache = cache.get(region);
            
            // Remove expired entries
            regionCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
            
            return CacheResult.success("Keys retrieved successfully", 
                    Map.of("keys", regionCache.keySet(), "region", region, "count", regionCache.size()));
        }
        
        @Override
        public CacheResult clearRegion(CacheRegion region) {
            if (!initialized) {
                return CacheResult.failure("Cache not initialized", "Not initialized");
            }
            
            Map<String, CacheEntry> regionCache = cache.get(region);
            int count = regionCache.size();
            regionCache.clear();
            
            logger.info("Cache region cleared: region={}, entries={}", region, count);
            
            return CacheResult.success("Cache region cleared successfully", 
                    Map.of("region", region, "cleared", count));
        }
        
        @Override
        public CacheResult clearAll() {
            if (!initialized) {
                return CacheResult.failure("Cache not initialized", "Not initialized");
            }
            
            int totalCount = 0;
            for (CacheRegion region : CacheRegion.values()) {
                Map<String, CacheEntry> regionCache = cache.get(region);
                totalCount += regionCache.size();
                regionCache.clear();
            }
            
            logger.info("All cache regions cleared: total entries={}", totalCount);
            
            return CacheResult.success("All cache regions cleared successfully", 
                    Map.of("cleared", totalCount));
        }
        
        @Override
        public CacheResult getStatistics() {
            if (!initialized) {
                return CacheResult.failure("Cache not initialized", "Not initialized");
            }
            
            int totalEntries = 0;
            Map<String, Integer> regionCounts = new HashMap<>();
            
            for (CacheRegion region : CacheRegion.values()) {
                Map<String, CacheEntry> regionCache = cache.get(region);
                regionCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
                int count = regionCache.size();
                totalEntries += count;
                regionCounts.put(region.name(), count);
            }
            
            double hitRate = hits + misses > 0 ? (double) hits / (hits + misses) : 0.0;
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("entries", totalEntries);
            stats.put("regionCounts", regionCounts);
            stats.put("hits", hits);
            stats.put("misses", misses);
            stats.put("hitRate", hitRate);
            stats.put("evictions", evictions);
            
            logger.info("Cache statistics: entries={}, hits={}, misses={}, hitRate={}, evictions={}", 
                    totalEntries, hits, misses, hitRate, evictions);
            
            return CacheResult.success("Statistics retrieved successfully", stats);
        }
        
        @Override
        public CacheResult shutdown() {
            if (!initialized) {
                return CacheResult.failure("Cache not initialized", "Not initialized");
            }
            
            for (CacheRegion region : CacheRegion.values()) {
                cache.get(region).clear();
            }
            
            initialized = false;
            logger.info("Cache shutdown complete");
            
            return CacheResult.success("Cache shutdown successfully");
        }
        
        /**
         * Evicts the oldest entry from a cache region.
         */
        private void evictOldest(CacheRegion region) {
            Map<String, CacheEntry> regionCache = cache.get(region);
            if (regionCache.isEmpty()) {
                return;
            }
            
            // Find oldest entry
            String oldestKey = null;
            long oldestTime = Long.MAX_VALUE;
            
            for (Map.Entry<String, CacheEntry> entry : regionCache.entrySet()) {
                long creationTime = entry.getValue().getCreationTime();
                if (creationTime < oldestTime) {
                    oldestTime = creationTime;
                    oldestKey = entry.getKey();
                }
            }
            
            if (oldestKey != null) {
                regionCache.remove(oldestKey);
                evictions++;
                logger.info("Evicted oldest entry from cache: key={}, region={}", oldestKey, region);
            }
        }
        
        /**
         * Represents a cache entry with TTL support.
         */
        private static class CacheEntry {
            private final Object value;
            private final long creationTime;
            private final Long expirationTime;  // null means no expiration
            
            public CacheEntry(Object value) {
                this.value = value;
                this.creationTime = System.currentTimeMillis();
                this.expirationTime = null;
            }
            
            public CacheEntry(Object value, Duration ttl) {
                this.value = value;
                this.creationTime = System.currentTimeMillis();
                this.expirationTime = ttl != null ? 
                        System.currentTimeMillis() + ttl.toMillis() : null;
            }
            
            public Object getValue() {
                return value;
            }
            
            public long getCreationTime() {
                return creationTime;
            }
            
            public boolean isExpired() {
                return expirationTime != null && System.currentTimeMillis() > expirationTime;
            }
        }
    }
}