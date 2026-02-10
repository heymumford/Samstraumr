/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.infrastructure.cache;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.s8r.application.port.CachePort;
import org.s8r.application.port.LoggerPort;
import org.s8r.test.annotation.UnitTest;

@UnitTest
public class InMemoryCacheAdapterTest {

  private LoggerPort mockLogger;
  private InMemoryCacheAdapter adapter;
  private static final CachePort.CacheRegion TEST_REGION = CachePort.CacheRegion.GENERAL;

  @BeforeEach
  void setUp() {
    mockLogger = mock(LoggerPort.class);
    adapter = new InMemoryCacheAdapter(mockLogger);
    adapter.initialize();
  }

  @Test
  void initialize_shouldReturnSuccessResult() {
    // Act
    CachePort.CacheResult result = adapter.initialize();

    // Assert
    assertTrue(result.isSuccessful());
    assertEquals("Cache initialized successfully", result.getMessage());
    verify(mockLogger, never()).error(anyString(), anyString(), any(Exception.class));
  }

  @Test
  void put_shouldStoreValueSuccessfully() {
    // Arrange
    String key = "testKey";
    String value = "Test Value";

    // Act
    CachePort.CacheResult result = adapter.put(TEST_REGION, key, value, null);

    // Assert
    assertTrue(result.isSuccessful());
    assertTrue(adapter.containsKey(TEST_REGION, key));
  }

  @Test
  void put_withNullValueShouldRemoveKey() {
    // Arrange
    String key = "testKey";
    String value = "Test Value";
    adapter.put(TEST_REGION, key, value, Duration.ofMinutes(10));

    // Act - Put null value
    CachePort.CacheResult result = adapter.put(TEST_REGION, key, null, Duration.ofMinutes(10));

    // Assert
    assertTrue(result.isSuccessful());
    assertFalse(adapter.containsKey(TEST_REGION, key));
  }

  @Test
  void get_shouldReturnCachedValue() {
    // Arrange
    String key = "testKey";
    String value = "Test Value";
    adapter.put(TEST_REGION, key, value, Duration.ofMinutes(10));

    // Act
    CachePort.CacheResult result = adapter.get(TEST_REGION, key, String.class);

    // Assert
    assertTrue(result.isSuccessful());
    assertEquals(value, result.getAttributes().get("value"));
  }

  @Test
  void get_shouldReturnFailureWhenKeyDoesNotExist() {
    // Act
    CachePort.CacheResult result = adapter.get(TEST_REGION, "nonExistentKey", String.class);

    // Assert
    assertFalse(result.isSuccessful());
    assertTrue(result.getReason().isPresent());
    assertTrue(result.getReason().get().contains("Cache miss"));
  }

  @Test
  void get_shouldReturnFailureWhenTypeMismatch() {
    // Arrange
    String key = "testKey";
    String value = "Test Value";
    adapter.put(TEST_REGION, key, value, Duration.ofMinutes(10));

    // Act
    CachePort.CacheResult result = adapter.get(TEST_REGION, key, Integer.class);

    // Assert
    assertFalse(result.isSuccessful());
    assertTrue(result.getReason().isPresent());
    assertTrue(result.getReason().get().contains("Type mismatch"));
  }

  @Test
  void remove_shouldRemoveExistingKey() {
    // Arrange
    String key = "testKey";
    String value = "Test Value";
    adapter.put(TEST_REGION, key, value, Duration.ofMinutes(10));

    // Act
    CachePort.CacheResult result = adapter.remove(TEST_REGION, key);

    // Assert
    assertTrue(result.isSuccessful());
    assertFalse(adapter.containsKey(TEST_REGION, key));
  }

  @Test
  void remove_shouldReturnFailureWhenKeyDoesNotExist() {
    // Act
    CachePort.CacheResult result = adapter.remove(TEST_REGION, "nonExistentKey");

    // Assert
    assertFalse(result.isSuccessful());
    assertTrue(result.getReason().isPresent());
    assertTrue(result.getReason().get().contains("Key not found"));
  }

  @Test
  void getOrCompute_shouldReturnCachedValue() {
    // Arrange
    String key = "testKey";
    String value = "Test Value";
    adapter.put(TEST_REGION, key, value, Duration.ofMinutes(10));
    Function<String, String> provider = k -> "Computed Value";

    // Act
    CachePort.CacheResult result =
        adapter.getOrCompute(TEST_REGION, key, String.class, provider, Duration.ofMinutes(10));

    // Assert
    assertTrue(result.isSuccessful());
    assertEquals(value, result.getAttributes().get("value"));
    assertFalse(result.getAttributes().containsKey("computed"));
  }

  @Test
  void getOrCompute_shouldComputeWhenKeyDoesNotExist() {
    // Arrange
    String key = "nonExistentKey";
    String computedValue = "Computed Value";
    Function<String, String> provider = k -> computedValue;

    // Act
    CachePort.CacheResult result =
        adapter.getOrCompute(TEST_REGION, key, String.class, provider, Duration.ofMinutes(10));

    // Assert
    assertTrue(result.isSuccessful());
    assertEquals(computedValue, result.getAttributes().get("value"));
    assertEquals(Boolean.TRUE, result.getAttributes().get("computed"));
    assertTrue(adapter.containsKey(TEST_REGION, key));
  }

  @Test
  void getOrCompute_shouldReturnFailureWhenProviderReturnsNull() {
    // Arrange
    String key = "testKey";
    Function<String, String> provider = k -> null;

    // Act
    CachePort.CacheResult result =
        adapter.getOrCompute(TEST_REGION, key, String.class, provider, Duration.ofMinutes(10));

    // Assert
    assertFalse(result.isSuccessful());
    assertTrue(result.getReason().isPresent());
    assertTrue(result.getReason().get().contains("Provider returned null"));
  }

  @Test
  void put_withExpirationShouldExpireValue() throws InterruptedException {
    // Arrange
    String key = "expiringKey";
    String value = "Expiring Value";
    Duration ttl = Duration.ofMillis(100); // Short TTL for testing

    // Act
    adapter.put(TEST_REGION, key, value, ttl);

    // Initially the value should be there
    assertTrue(adapter.containsKey(TEST_REGION, key));

    // Wait for expiration
    Thread.sleep(200);

    // Assert - Value should be expired
    assertFalse(adapter.containsKey(TEST_REGION, key));
  }

  @Test
  void getKeys_shouldReturnAllValidKeys() {
    // Arrange
    adapter.put(TEST_REGION, "key1", "value1", Duration.ofMinutes(10));
    adapter.put(TEST_REGION, "key2", "value2", Duration.ofMinutes(10));
    adapter.put(TEST_REGION, "key3", "value3", Duration.ofMinutes(10));

    // Act
    CachePort.CacheResult result = adapter.getKeys(TEST_REGION);

    // Assert
    assertTrue(result.isSuccessful());
    @SuppressWarnings("unchecked")
    Set<String> keys = (Set<String>) result.getAttributes().get("keys");
    assertEquals(3, keys.size());
    assertTrue(keys.contains("key1"));
    assertTrue(keys.contains("key2"));
    assertTrue(keys.contains("key3"));
  }

  @Test
  void clearRegion_shouldRemoveAllEntriesInRegion() {
    // Arrange
    adapter.put(TEST_REGION, "key1", "value1", Duration.ofMinutes(10));
    adapter.put(TEST_REGION, "key2", "value2", Duration.ofMinutes(10));
    adapter.put(CachePort.CacheRegion.CONFIG, "key3", "value3", Duration.ofMinutes(10));

    // Act
    CachePort.CacheResult result = adapter.clearRegion(TEST_REGION);

    // Assert
    assertTrue(result.isSuccessful());
    assertFalse(adapter.containsKey(TEST_REGION, "key1"));
    assertFalse(adapter.containsKey(TEST_REGION, "key2"));
    assertTrue(adapter.containsKey(CachePort.CacheRegion.CONFIG, "key3")); // Should not be affected
  }

  @Test
  void clearAll_shouldRemoveAllEntries() {
    // Arrange
    adapter.put(TEST_REGION, "key1", "value1", Duration.ofMinutes(10));
    adapter.put(CachePort.CacheRegion.CONFIG, "key2", "value2", Duration.ofMinutes(10));

    // Act
    CachePort.CacheResult result = adapter.clearAll();

    // Assert
    assertTrue(result.isSuccessful());
    assertFalse(adapter.containsKey(TEST_REGION, "key1"));
    assertFalse(adapter.containsKey(CachePort.CacheRegion.CONFIG, "key2"));
  }

  @Test
  void getStatistics_shouldReturnCorrectStatistics() {
    // Arrange
    adapter.put(TEST_REGION, "key1", "value1", Duration.ofMinutes(10));
    adapter.get(TEST_REGION, "key1", String.class); // Hit
    adapter.get(TEST_REGION, "nonExistent", String.class); // Miss

    // Act
    CachePort.CacheResult result = adapter.getStatistics();

    // Assert
    assertTrue(result.isSuccessful());
    Map<String, Object> stats = result.getAttributes();
    assertEquals(1L, stats.get("hits"));
    assertEquals(1L, stats.get("misses"));

    @SuppressWarnings("unchecked")
    Map<String, Integer> regionSizes = (Map<String, Integer>) stats.get("regionSizes");
    assertTrue(regionSizes.containsKey(TEST_REGION.name()));
    assertEquals(1, regionSizes.get(TEST_REGION.name()));
  }

  @Test
  void shutdown_shouldClearAllEntries() {
    // Arrange
    adapter.put(TEST_REGION, "key1", "value1", Duration.ofMinutes(10));

    // Act
    CachePort.CacheResult result = adapter.shutdown();

    // Assert
    assertTrue(result.isSuccessful());

    // Re-initialize to test
    adapter.initialize();
    assertFalse(adapter.containsKey(TEST_REGION, "key1"));
  }

  @Test
  void containsKey_shouldReturnFalseForNullParameters() {
    // Act & Assert
    assertFalse(adapter.containsKey(null, "key"));
    assertFalse(adapter.containsKey(TEST_REGION, null));
  }

  @Test
  void invalidParameters_shouldReturnFailureResults() {
    // Act & Assert - For put
    CachePort.CacheResult putResult = adapter.put(null, "key", "value", Duration.ofMinutes(10));
    assertFalse(putResult.isSuccessful());

    // Act & Assert - For get
    CachePort.CacheResult getResult = adapter.get(null, "key", String.class);
    assertFalse(getResult.isSuccessful());

    // Act & Assert - For remove
    CachePort.CacheResult removeResult = adapter.remove(null, "key");
    assertFalse(removeResult.isSuccessful());

    // Act & Assert - For getKeys
    CachePort.CacheResult getKeysResult = adapter.getKeys(null);
    assertFalse(getKeysResult.isSuccessful());

    // Act & Assert - For clearRegion
    CachePort.CacheResult clearRegionResult = adapter.clearRegion(null);
    assertFalse(clearRegionResult.isSuccessful());
  }
}
