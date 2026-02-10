/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.infrastructure.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import org.s8r.application.port.CachePort;
import org.s8r.application.port.CachePort.CacheRegion;
import org.s8r.application.port.CachePort.CacheResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Tests for the InMemoryCacheAdapter.
 */
class InMemoryCacheAdapterTest {

    private InMemoryCacheAdapter cacheAdapter;
    
    @BeforeEach
    void setUp() {
        cacheAdapter = new InMemoryCacheAdapter();
        cacheAdapter.initialize("test-cache");
    }
    
    @Test
    @DisplayName("Cache should be initialized with the given name")
    void testInitialize() {
        assertEquals("test-cache", cacheAdapter.getCacheName());
        
        // Re-initialize with a different name
        cacheAdapter.initialize("another-cache");
        assertEquals("another-cache", cacheAdapter.getCacheName());
    }
    
    @Test
    @DisplayName("Should put and get values from the cache")
    void testPutAndGet() {
        // Test with string value
        cacheAdapter.put("key1", "value1");
        Optional<String> value1 = cacheAdapter.get("key1");
        assertTrue(value1.isPresent());
        assertEquals("value1", value1.get());
        
        // Test with integer value
        cacheAdapter.put("key2", 42);
        Optional<Integer> value2 = cacheAdapter.get("key2");
        assertTrue(value2.isPresent());
        assertEquals(42, value2.get());
        
        // Test with complex object
        TestObject object = new TestObject("Test", 100);
        cacheAdapter.put("key3", object);
        Optional<TestObject> value3 = cacheAdapter.get("key3");
        assertTrue(value3.isPresent());
        assertEquals("Test", value3.get().getName());
        assertEquals(100, value3.get().getValue());
    }
    
    @Test
    @DisplayName("Should return empty Optional for non-existent keys")
    void testGetNonExistentKey() {
        Optional<String> value = cacheAdapter.get("non-existent-key");
        assertFalse(value.isPresent());
    }
    
    @Test
    @DisplayName("Should check if keys exist in the cache")
    void testContainsKey() {
        cacheAdapter.put("existing-key", "value");
        
        assertTrue(cacheAdapter.containsKey("existing-key"));
        assertFalse(cacheAdapter.containsKey("non-existent-key"));
    }
    
    @Test
    @DisplayName("Should remove values from the cache")
    void testRemove() {
        // Add value to remove
        cacheAdapter.put("key-to-remove", "value");
        assertTrue(cacheAdapter.containsKey("key-to-remove"));
        
        // Remove existing key
        boolean removed = cacheAdapter.remove("key-to-remove");
        assertTrue(removed);
        assertFalse(cacheAdapter.containsKey("key-to-remove"));
        
        // Remove non-existent key
        boolean removedNonExistent = cacheAdapter.remove("non-existent-key");
        assertFalse(removedNonExistent);
    }
    
    @Test
    @DisplayName("Should clear all values from the cache")
    void testClear() {
        // Add some values
        cacheAdapter.put("key1", "value1");
        cacheAdapter.put("key2", "value2");
        assertTrue(cacheAdapter.containsKey("key1"));
        assertTrue(cacheAdapter.containsKey("key2"));
        
        // Clear cache
        cacheAdapter.clear();
        assertFalse(cacheAdapter.containsKey("key1"));
        assertFalse(cacheAdapter.containsKey("key2"));
    }
    
    @Test
    @DisplayName("Should handle region-specific operations")
    void testRegionOperations() {
        // Put values in different regions
        cacheAdapter.put(CacheRegion.CONFIG + ":app.name", "TestApp");
        cacheAdapter.put(CacheRegion.CONFIG + ":app.version", "1.0.0");
        cacheAdapter.put(CacheRegion.USER + ":user1", "John Doe");
        
        // Get keys for CONFIG region
        CacheResult configKeys = cacheAdapter.getKeys(CacheRegion.CONFIG);
        assertTrue(configKeys.isSuccessful());
        
        @SuppressWarnings("unchecked")
        Set<String> keys = (Set<String>) configKeys.getAttributes().get("keys");
        assertNotNull(keys);
        assertEquals(2, keys.size());
        assertTrue(keys.contains("app.name"));
        assertTrue(keys.contains("app.version"));
        
        // Get keys for USER region
        CacheResult userKeys = cacheAdapter.getKeys(CacheRegion.USER);
        assertTrue(userKeys.isSuccessful());
        
        @SuppressWarnings("unchecked")
        Set<String> userKeySet = (Set<String>) userKeys.getAttributes().get("keys");
        assertNotNull(userKeySet);
        assertEquals(1, userKeySet.size());
        assertTrue(userKeySet.contains("user1"));
        
        // Get keys for TEMP region (empty)
        CacheResult tempKeys = cacheAdapter.getKeys(CacheRegion.TEMP);
        assertTrue(tempKeys.isSuccessful());
        
        @SuppressWarnings("unchecked")
        Set<String> tempKeySet = (Set<String>) tempKeys.getAttributes().get("keys");
        assertNotNull(tempKeySet);
        assertTrue(tempKeySet.isEmpty());
    }
    
    @Test
    @DisplayName("Should track and report statistics")
    void testStatistics() {
        // Put some values
        cacheAdapter.put("key1", "value1");
        cacheAdapter.put("key2", "value2");
        
        // Get with hits
        cacheAdapter.get("key1");
        cacheAdapter.get("key2");
        
        // Get with miss
        cacheAdapter.get("non-existent-key");
        
        // Remove value
        cacheAdapter.remove("key1");
        
        // Get statistics
        CacheResult statsResult = cacheAdapter.getStatistics();
        assertTrue(statsResult.isSuccessful());
        
        Map<String, Object> stats = statsResult.getAttributes();
        assertEquals(2L, stats.get("hits"));
        assertEquals(1L, stats.get("misses"));
        assertEquals(2L, stats.get("puts"));
        assertEquals(1L, stats.get("removes"));
        assertEquals(2.0/3.0, (double) stats.get("hitRatio"), 0.001);
        assertEquals(1, stats.get("totalSize"));
        
        @SuppressWarnings("unchecked")
        Map<String, Integer> regionSizes = (Map<String, Integer>) stats.get("regionSizes");
        assertNotNull(regionSizes);
        assertEquals(0, regionSizes.get("CONFIG").intValue());
        assertEquals(0, regionSizes.get("USER").intValue());
        assertEquals(0, regionSizes.get("TEMP").intValue());
        assertEquals(0, regionSizes.get("SYSTEM").intValue());
        assertEquals(0, regionSizes.get("GENERAL").intValue());
    }
    
    /**
     * Test object for complex object caching tests.
     */
    static class TestObject {
        private final String name;
        private final int value;
        
        TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }
        
        String getName() {
            return name;
        }
        
        int getValue() {
            return value;
        }
    }
}