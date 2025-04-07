/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.s8r.application.port.CachePort;
import org.s8r.application.port.LoggerPort;
import org.s8r.test.annotation.UnitTest;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@UnitTest
public class CacheServiceTest {

    private CachePort mockCachePort;
    private LoggerPort mockLogger;
    private CacheService service;
    private static final CachePort.CacheRegion TEST_REGION = CachePort.CacheRegion.GENERAL;

    @BeforeEach
    void setUp() {
        mockCachePort = mock(CachePort.class);
        mockLogger = mock(LoggerPort.class);
        service = new CacheService(mockCachePort, mockLogger);
    }

    @Test
    void initialize_shouldCallPortInitialize() {
        // Arrange
        when(mockCachePort.initialize()).thenReturn(CachePort.CacheResult.success("Success"));

        // Act
        service.initialize();

        // Assert
        verify(mockCachePort).initialize();
        verify(mockLogger).info(anyString());
        verify(mockLogger, never()).error(anyString(), anyString());
    }

    @Test
    void initialize_shouldLogErrorOnFailure() {
        // Arrange
        when(mockCachePort.initialize()).thenReturn(
                CachePort.CacheResult.failure("Initialization failed", "Test error")
        );

        // Act
        service.initialize();

        // Assert
        verify(mockCachePort).initialize();
        verify(mockLogger).error(contains("Failed to initialize"), eq("Test error"));
    }

    @Test
    void get_shouldReturnValueOnSuccess() {
        // Arrange
        String key = "testKey";
        String value = "Test Value";
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("value", value);

        when(mockCachePort.get(TEST_REGION, key, String.class)).thenReturn(
                CachePort.CacheResult.success("Cache hit", attributes)
        );

        // Act
        Optional<String> result = service.get(TEST_REGION, key, String.class);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(value, result.get());
        verify(mockCachePort).get(TEST_REGION, key, String.class);
    }

    @Test
    void get_shouldReturnEmptyOnCacheMiss() {
        // Arrange
        String key = "nonExistentKey";
        when(mockCachePort.get(TEST_REGION, key, String.class)).thenReturn(
                CachePort.CacheResult.failure("Cache miss", "No value found for key")
        );

        // Act
        Optional<String> result = service.get(TEST_REGION, key, String.class);

        // Assert
        assertFalse(result.isPresent());
        verify(mockCachePort).get(TEST_REGION, key, String.class);
        verify(mockLogger, never()).warn(anyString(), anyString(), anyString());
    }

    @Test
    void get_shouldLogWarningOnNonCacheMissError() {
        // Arrange
        String key = "errorKey";
        when(mockCachePort.get(TEST_REGION, key, String.class)).thenReturn(
                CachePort.CacheResult.failure("Error", "Type mismatch")
        );

        // Act
        Optional<String> result = service.get(TEST_REGION, key, String.class);

        // Assert
        assertFalse(result.isPresent());
        verify(mockCachePort).get(TEST_REGION, key, String.class);
        verify(mockLogger).warn(anyString(), eq(key), anyString());
    }

    @Test
    void put_shouldReturnTrueOnSuccess() {
        // Arrange
        String key = "testKey";
        String value = "Test Value";
        when(mockCachePort.put(eq(TEST_REGION), eq(key), eq(value), isNull())).thenReturn(
                CachePort.CacheResult.success("Value cached successfully")
        );

        // Act
        boolean result = service.put(TEST_REGION, key, value);

        // Assert
        assertTrue(result);
        verify(mockCachePort).put(TEST_REGION, key, value, null);
    }

    @Test
    void put_shouldReturnFalseAndLogOnFailure() {
        // Arrange
        String key = "testKey";
        String value = "Test Value";
        when(mockCachePort.put(eq(TEST_REGION), eq(key), eq(value), isNull())).thenReturn(
                CachePort.CacheResult.failure("Error caching value", "Invalid region")
        );

        // Act
        boolean result = service.put(TEST_REGION, key, value);

        // Assert
        assertFalse(result);
        verify(mockCachePort).put(TEST_REGION, key, value, null);
        verify(mockLogger).warn(anyString(), eq(key), anyString());
    }

    @Test
    void put_withTtlShouldPassTtlToPort() {
        // Arrange
        String key = "testKey";
        String value = "Test Value";
        Duration ttl = Duration.ofMinutes(5);
        when(mockCachePort.put(TEST_REGION, key, value, ttl)).thenReturn(
                CachePort.CacheResult.success("Value cached successfully")
        );

        // Act
        boolean result = service.put(TEST_REGION, key, value, ttl);

        // Assert
        assertTrue(result);
        verify(mockCachePort).put(TEST_REGION, key, value, ttl);
    }

    @Test
    void getOrCompute_shouldReturnValueOnSuccess() {
        // Arrange
        String key = "testKey";
        String value = "Test Value";
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("value", value);
        Function<String, String> provider = k -> "Computed";

        when(mockCachePort.getOrCompute(eq(TEST_REGION), eq(key), eq(String.class), 
                any(Function.class), isNull())).thenReturn(
                CachePort.CacheResult.success("Cache hit", attributes)
        );

        // Act
        Optional<String> result = service.getOrCompute(TEST_REGION, key, String.class, provider);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(value, result.get());
        verify(mockCachePort).getOrCompute(eq(TEST_REGION), eq(key), eq(String.class), 
                any(Function.class), isNull());
    }

    @Test
    void getOrCompute_shouldReturnEmptyAndLogOnFailure() {
        // Arrange
        String key = "testKey";
        Function<String, String> provider = k -> null;
        when(mockCachePort.getOrCompute(eq(TEST_REGION), eq(key), eq(String.class), 
                any(Function.class), isNull())).thenReturn(
                CachePort.CacheResult.failure("Computation error", "Provider returned null")
        );

        // Act
        Optional<String> result = service.getOrCompute(TEST_REGION, key, String.class, provider);

        // Assert
        assertFalse(result.isPresent());
        verify(mockCachePort).getOrCompute(eq(TEST_REGION), eq(key), eq(String.class), 
                any(Function.class), isNull());
        verify(mockLogger).warn(anyString(), eq(key), anyString());
    }

    @Test
    void remove_shouldReturnTrueOnSuccess() {
        // Arrange
        String key = "testKey";
        when(mockCachePort.remove(TEST_REGION, key)).thenReturn(
                CachePort.CacheResult.success("Value removed successfully")
        );

        // Act
        boolean result = service.remove(TEST_REGION, key);

        // Assert
        assertTrue(result);
        verify(mockCachePort).remove(TEST_REGION, key);
    }

    @Test
    void remove_shouldReturnFalseOnFailure() {
        // Arrange
        String key = "nonExistentKey";
        when(mockCachePort.remove(TEST_REGION, key)).thenReturn(
                CachePort.CacheResult.failure("Key not found", "No value found for key")
        );

        // Act
        boolean result = service.remove(TEST_REGION, key);

        // Assert
        assertFalse(result);
        verify(mockCachePort).remove(TEST_REGION, key);
    }

    @Test
    void containsKey_shouldDelegateToPort() {
        // Arrange
        String key = "testKey";
        when(mockCachePort.containsKey(TEST_REGION, key)).thenReturn(true);

        // Act
        boolean result = service.containsKey(TEST_REGION, key);

        // Assert
        assertTrue(result);
        verify(mockCachePort).containsKey(TEST_REGION, key);
    }

    @Test
    void getKeys_shouldReturnKeysOnSuccess() {
        // Arrange
        Set<String> keys = new HashSet<>();
        keys.add("key1");
        keys.add("key2");
        
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("keys", keys);
        
        when(mockCachePort.getKeys(TEST_REGION)).thenReturn(
                CachePort.CacheResult.success("Retrieved keys successfully", attributes)
        );

        // Act
        Set<String> result = service.getKeys(TEST_REGION);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains("key1"));
        assertTrue(result.contains("key2"));
        verify(mockCachePort).getKeys(TEST_REGION);
    }

    @Test
    void getKeys_shouldReturnEmptySetAndLogOnFailure() {
        // Arrange
        when(mockCachePort.getKeys(TEST_REGION)).thenReturn(
                CachePort.CacheResult.failure("Error getting keys", "Invalid region")
        );

        // Act
        Set<String> result = service.getKeys(TEST_REGION);

        // Assert
        assertTrue(result.isEmpty());
        verify(mockCachePort).getKeys(TEST_REGION);
        verify(mockLogger).warn(anyString(), eq(TEST_REGION), anyString());
    }

    @Test
    void clearRegion_shouldReturnTrueOnSuccess() {
        // Arrange
        when(mockCachePort.clearRegion(TEST_REGION)).thenReturn(
                CachePort.CacheResult.success("Region cleared successfully")
        );

        // Act
        boolean result = service.clearRegion(TEST_REGION);

        // Assert
        assertTrue(result);
        verify(mockCachePort).clearRegion(TEST_REGION);
    }

    @Test
    void clearRegion_shouldReturnFalseAndLogOnFailure() {
        // Arrange
        when(mockCachePort.clearRegion(TEST_REGION)).thenReturn(
                CachePort.CacheResult.failure("Error clearing region", "Invalid region")
        );

        // Act
        boolean result = service.clearRegion(TEST_REGION);

        // Assert
        assertFalse(result);
        verify(mockCachePort).clearRegion(TEST_REGION);
        verify(mockLogger).warn(anyString(), eq(TEST_REGION), anyString());
    }

    @Test
    void clearAll_shouldReturnTrueOnSuccess() {
        // Arrange
        when(mockCachePort.clearAll()).thenReturn(
                CachePort.CacheResult.success("All regions cleared successfully")
        );

        // Act
        boolean result = service.clearAll();

        // Assert
        assertTrue(result);
        verify(mockCachePort).clearAll();
    }

    @Test
    void clearAll_shouldReturnFalseAndLogOnFailure() {
        // Arrange
        when(mockCachePort.clearAll()).thenReturn(
                CachePort.CacheResult.failure("Error clearing all regions", "Cache not initialized")
        );

        // Act
        boolean result = service.clearAll();

        // Assert
        assertFalse(result);
        verify(mockCachePort).clearAll();
        verify(mockLogger).warn(anyString(), anyString());
    }

    @Test
    void getStatistics_shouldReturnStatsOnSuccess() {
        // Arrange
        Map<String, Object> stats = new HashMap<>();
        stats.put("hits", 10L);
        stats.put("misses", 5L);
        stats.put("hitRatio", 0.67);
        
        when(mockCachePort.getStatistics()).thenReturn(
                CachePort.CacheResult.success("Statistics retrieved successfully", stats)
        );

        // Act
        Map<String, Object> result = service.getStatistics();

        // Assert
        assertEquals(stats, result);
        verify(mockCachePort).getStatistics();
    }

    @Test
    void getStatistics_shouldReturnEmptyMapAndLogOnFailure() {
        // Arrange
        when(mockCachePort.getStatistics()).thenReturn(
                CachePort.CacheResult.failure("Error retrieving statistics", "Cache not initialized")
        );

        // Act
        Map<String, Object> result = service.getStatistics();

        // Assert
        assertTrue(result.isEmpty());
        verify(mockCachePort).getStatistics();
        verify(mockLogger).warn(anyString(), anyString());
    }

    @Test
    void getHitRatio_shouldReturnRatioFromStatistics() {
        // Arrange
        Map<String, Object> stats = new HashMap<>();
        stats.put("hitRatio", 0.75);
        
        when(mockCachePort.getStatistics()).thenReturn(
                CachePort.CacheResult.success("Statistics retrieved successfully", stats)
        );

        // Act
        double result = service.getHitRatio();

        // Assert
        assertEquals(0.75, result, 0.001);
        verify(mockCachePort).getStatistics();
    }

    @Test
    void getHitRatio_shouldReturnZeroWhenNotAvailable() {
        // Arrange
        when(mockCachePort.getStatistics()).thenReturn(
                CachePort.CacheResult.success("Statistics retrieved successfully", Collections.emptyMap())
        );

        // Act
        double result = service.getHitRatio();

        // Assert
        assertEquals(0.0, result, 0.001);
        verify(mockCachePort).getStatistics();
    }

    @Test
    void getRegionSize_shouldReturnSizeFromStatistics() {
        // Arrange
        Map<String, Integer> regionSizes = new HashMap<>();
        regionSizes.put(TEST_REGION.name(), 42);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("regionSizes", regionSizes);
        
        when(mockCachePort.getStatistics()).thenReturn(
                CachePort.CacheResult.success("Statistics retrieved successfully", stats)
        );

        // Act
        int result = service.getRegionSize(TEST_REGION);

        // Assert
        assertEquals(42, result);
        verify(mockCachePort).getStatistics();
    }

    @Test
    void getRegionSize_shouldReturnZeroWhenNotAvailable() {
        // Arrange
        when(mockCachePort.getStatistics()).thenReturn(
                CachePort.CacheResult.success("Statistics retrieved successfully", Collections.emptyMap())
        );

        // Act
        int result = service.getRegionSize(TEST_REGION);

        // Assert
        assertEquals(0, result);
        verify(mockCachePort).getStatistics();
    }

    @Test
    void shutdown_shouldCallPortShutdown() {
        // Arrange
        when(mockCachePort.shutdown()).thenReturn(
                CachePort.CacheResult.success("Cache shutdown successfully")
        );

        // Act
        service.shutdown();

        // Assert
        verify(mockCachePort).shutdown();
        verify(mockLogger).info(anyString());
        verify(mockLogger, never()).error(anyString(), anyString());
    }

    @Test
    void shutdown_shouldLogErrorOnFailure() {
        // Arrange
        when(mockCachePort.shutdown()).thenReturn(
                CachePort.CacheResult.failure("Shutdown failed", "Test error")
        );

        // Act
        service.shutdown();

        // Assert
        verify(mockCachePort).shutdown();
        verify(mockLogger).error(contains("Failed to shut down"), eq("Test error"));
    }
}