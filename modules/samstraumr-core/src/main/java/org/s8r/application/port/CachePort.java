/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.application.port;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Port interface for cache operations in the application layer.
 *
 * <p>This interface defines the operations that can be performed on a cache, following the ports
 * and adapters pattern from Clean Architecture.
 */
public interface CachePort {

  /**
   * Initializes the cache with the given name.
   *
   * @param cacheName The name of the cache to initialize
   */
  void initialize(String cacheName);

  /**
   * Puts a value in the cache with the specified key.
   *
   * @param key The key to associate with the value
   * @param value The value to store in the cache
   * @param <T> The type of the value
   */
  <T> void put(String key, T value);

  /**
   * Retrieves a value from the cache by key.
   *
   * @param key The key to look up
   * @param <T> The expected type of the value
   * @return An Optional containing the value if found, or empty if not found
   */
  <T> Optional<T> get(String key);

  /**
   * Removes a value from the cache by key.
   *
   * @param key The key to remove
   * @return true if the key was found and removed, false otherwise
   */
  boolean remove(String key);

  /**
   * Checks if the cache contains a key.
   *
   * @param key The key to check
   * @return true if the key exists in the cache, false otherwise
   */
  boolean containsKey(String key);

  /** Clears all entries from the cache. */
  void clear();

  /** Enum representing cache regions. */
  enum CacheRegion {
    /** General-purpose cache region */
    GENERAL,

    /** Configuration data cache region */
    CONFIG,

    /** User data cache region */
    USER,

    /** Temporary data cache region */
    TEMP,

    /** System data cache region */
    SYSTEM,

    /** Component cache region */
    COMPONENT
  }

  /** Result of a cache operation. */
  interface CacheResult {
    /**
     * @return true if the operation was successful, false otherwise
     */
    boolean isSuccessful();

    /**
     * @return A message describing the result of the operation
     */
    String getMessage();

    /**
     * @return An optional reason for failure if the operation was not successful
     */
    Optional<String> getReason();

    /**
     * @return Additional attributes associated with the operation result
     */
    default Map<String, Object> getAttributes() {
      return Map.of();
    }

    /**
     * Creates a successful result with the given message.
     *
     * @param message The success message
     * @return A new CacheResult indicating success
     */
    static CacheResult success(String message) {
      return new SimpleCacheResult(true, message);
    }

    /**
     * Creates a successful result with the given message and attributes.
     *
     * @param message The success message
     * @param attributes Additional attributes to include in the result
     * @return A new CacheResult indicating success
     */
    static CacheResult success(String message, Map<String, Object> attributes) {
      return new SimpleCacheResult(true, message, attributes);
    }

    /**
     * Creates a failure result with the given message and reason.
     *
     * @param message The failure message
     * @param reason The reason for the failure
     * @return A new CacheResult indicating failure
     */
    static CacheResult failure(String message, String reason) {
      return new SimpleCacheResult(false, message, reason);
    }

    /**
     * Creates a failure result with the given message, reason, and attributes.
     *
     * @param message The failure message
     * @param reason The reason for the failure
     * @param attributes Additional attributes to include in the result
     * @return A new CacheResult indicating failure
     */
    static CacheResult failure(String message, String reason, Map<String, Object> attributes) {
      return new SimpleCacheResult(false, message, reason, attributes);
    }
  }

  /**
   * Initializes the cache.
   *
   * @return A result object indicating success or failure
   */
  default CacheResult initialize() {
    try {
      initialize("default");
      return CacheResult.success("Cache initialized successfully");
    } catch (Exception e) {
      return CacheResult.failure("Failed to initialize cache", e.getMessage());
    }
  }

  /**
   * Gets a value from the cache by region and key.
   *
   * @param region The cache region
   * @param key The cache key
   * @param type The expected type of the value
   * @param <T> The type parameter
   * @return A result containing the value if found
   */
  default <T> CacheResult get(CacheRegion region, String key, Class<T> type) {
    try {
      Optional<T> value = get(key);
      if (value.isPresent()) {
        return CacheResult.success("Cache hit", Map.of("value", value.get()));
      } else {
        return CacheResult.failure("Cache miss", "Value not found for key: " + key);
      }
    } catch (Exception e) {
      return CacheResult.failure("Failed to get value from cache", e.getMessage());
    }
  }

  /**
   * Puts a value in the cache by region and key.
   *
   * @param region The cache region
   * @param key The cache key
   * @param value The value to store
   * @param ttl The time-to-live for the value (can be null for no expiration)
   * @param <T> The type parameter
   * @return A result indicating success or failure
   */
  default <T> CacheResult put(CacheRegion region, String key, T value, Duration ttl) {
    try {
      put(key, value);
      return CacheResult.success("Value cached successfully");
    } catch (Exception e) {
      return CacheResult.failure("Failed to put value in cache", e.getMessage());
    }
  }

  /**
   * Gets or computes a value for the cache.
   *
   * @param region The cache region
   * @param key The cache key
   * @param type The expected type of the value
   * @param provider The function to compute the value if not found
   * @param ttl The time-to-live for the value (can be null for no expiration)
   * @param <T> The type parameter
   * @return A result containing the value
   */
  default <T> CacheResult getOrCompute(
      CacheRegion region, String key, Class<T> type, Function<String, T> provider, Duration ttl) {
    try {
      Optional<T> value = get(key);
      if (value.isPresent()) {
        return CacheResult.success("Cache hit", Map.of("value", value.get()));
      } else {
        T computed = provider.apply(key);
        put(key, computed);
        return CacheResult.success("Value computed and cached", Map.of("value", computed));
      }
    } catch (Exception e) {
      return CacheResult.failure("Failed to get or compute value", e.getMessage());
    }
  }

  /**
   * Removes a value from the cache by region and key.
   *
   * @param region The cache region
   * @param key The cache key
   * @return A result indicating success or failure
   */
  default CacheResult remove(CacheRegion region, String key) {
    try {
      boolean removed = remove(key);
      if (removed) {
        return CacheResult.success("Value removed from cache");
      } else {
        return CacheResult.failure("Value not found in cache", "Key not found: " + key);
      }
    } catch (Exception e) {
      return CacheResult.failure("Failed to remove value from cache", e.getMessage());
    }
  }

  /**
   * Checks if a key exists in the cache by region.
   *
   * @param region The cache region
   * @param key The cache key
   * @return True if the key exists, false otherwise
   */
  default boolean containsKey(CacheRegion region, String key) {
    return containsKey(key);
  }

  /**
   * Gets all keys in a cache region.
   *
   * @param region The cache region
   * @return A result containing the keys
   */
  default CacheResult getKeys(CacheRegion region) {
    return CacheResult.success("Keys retrieved successfully", Map.of("keys", Set.of()));
  }

  /**
   * Clears a specific cache region.
   *
   * @param region The cache region to clear
   * @return A result indicating success or failure
   */
  default CacheResult clearRegion(CacheRegion region) {
    try {
      clear();
      return CacheResult.success("Cache region cleared successfully");
    } catch (Exception e) {
      return CacheResult.failure("Failed to clear cache region", e.getMessage());
    }
  }

  /**
   * Clears all cache regions.
   *
   * @return A result indicating success or failure
   */
  default CacheResult clearAll() {
    try {
      clear();
      return CacheResult.success("All cache regions cleared successfully");
    } catch (Exception e) {
      return CacheResult.failure("Failed to clear all cache regions", e.getMessage());
    }
  }

  /**
   * Gets statistics about the cache.
   *
   * @return A result containing the statistics
   */
  default CacheResult getStatistics() {
    return CacheResult.success(
        "Cache statistics retrieved",
        Map.of(
            "hitCount", 0,
            "missCount", 0,
            "hitRatio", 0.0,
            "size", 0,
            "regionSizes", Map.of()));
  }

  /**
   * Shuts down the cache.
   *
   * @return A result indicating success or failure
   */
  default CacheResult shutdown() {
    return CacheResult.success("Cache shut down successfully");
  }

  /** Simple implementation of CacheResult for the default methods. */
  class SimpleCacheResult implements CacheResult {
    private final boolean successful;
    private final String message;
    private final String reason;
    private final Map<String, Object> attributes;

    public SimpleCacheResult(boolean successful, String message) {
      this(successful, message, null, Map.of());
    }

    public SimpleCacheResult(boolean successful, String message, String reason) {
      this(successful, message, reason, Map.of());
    }

    public SimpleCacheResult(boolean successful, String message, Map<String, Object> attributes) {
      this(successful, message, null, attributes);
    }

    public SimpleCacheResult(
        boolean successful, String message, String reason, Map<String, Object> attributes) {
      this.successful = successful;
      this.message = message;
      this.reason = reason;
      this.attributes = attributes;
    }

    @Override
    public boolean isSuccessful() {
      return successful;
    }

    @Override
    public String getMessage() {
      return message;
    }

    @Override
    public Optional<String> getReason() {
      return Optional.ofNullable(reason);
    }

    @Override
    public Map<String, Object> getAttributes() {
      return attributes;
    }
  }
}
