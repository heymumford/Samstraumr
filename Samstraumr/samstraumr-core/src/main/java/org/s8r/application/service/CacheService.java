/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.application.service;

import org.s8r.application.port.CachePort;
import org.s8r.application.port.LoggerPort;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Service class that provides caching functionality to the application layer.
 * <p>
 * This service uses the CachePort to interact with the cache infrastructure.
 * It provides a simplified interface for common cache operations and adds additional
 * business logic as needed.
 */
public class CacheService {

    private final CachePort cachePort;
    private final LoggerPort logger;

    /**
     * Creates a new CacheService with the specified dependencies.
     *
     * @param cachePort The cache port implementation to use
     * @param logger    The logger to use for logging
     */
    public CacheService(CachePort cachePort, LoggerPort logger) {
        this.cachePort = cachePort;
        this.logger = logger;
    }

    /**
     * Initializes the cache service.
     */
    public void initialize() {
        logger.info("Initializing cache service");
        CachePort.CacheResult result = cachePort.initialize();
        if (!result.isSuccessful()) {
            logger.error("Failed to initialize cache service: {}", result.getReason().orElse("Unknown reason"));
        }
    }

    /**
     * Gets a value from the cache.
     *
     * @param <T>    The type of the value
     * @param region The cache region
     * @param key    The cache key
     * @param type   The class of the value
     * @return An Optional containing the value, or empty if not found or an error occurred
     */
    public <T> Optional<T> get(CachePort.CacheRegion region, String key, Class<T> type) {
        CachePort.CacheResult result = cachePort.get(region, key, type);
        if (result.isSuccessful()) {
            @SuppressWarnings("unchecked")
            T value = (T) result.getAttributes().get("value");
            return Optional.ofNullable(value);
        } else {
            if (result.getReason().isPresent() && !result.getReason().get().contains("Cache miss")) {
                logger.warn("Failed to get value from cache for key {}: {}", key, result.getReason().orElse("Unknown reason"));
            }
            return Optional.empty();
        }
    }

    /**
     * Puts a value in the cache.
     *
     * @param <T>    The type of the value
     * @param region The cache region
     * @param key    The cache key
     * @param value  The value to cache
     * @return True if the value was cached successfully, false otherwise
     */
    public <T> boolean put(CachePort.CacheRegion region, String key, T value) {
        return put(region, key, value, null);
    }

    /**
     * Puts a value in the cache with an expiration time.
     *
     * @param <T>    The type of the value
     * @param region The cache region
     * @param key    The cache key
     * @param value  The value to cache
     * @param ttl    The time-to-live for the cached value
     * @return True if the value was cached successfully, false otherwise
     */
    public <T> boolean put(CachePort.CacheRegion region, String key, T value, Duration ttl) {
        CachePort.CacheResult result = cachePort.put(region, key, value, ttl);
        if (!result.isSuccessful()) {
            logger.warn("Failed to put value in cache for key {}: {}", key, result.getReason().orElse("Unknown reason"));
        }
        return result.isSuccessful();
    }

    /**
     * Gets a value from the cache, computing it if not present.
     *
     * @param <T>      The type of the value
     * @param region   The cache region
     * @param key      The cache key
     * @param type     The class of the value
     * @param provider The function to compute the value if not in cache
     * @return An Optional containing the value (either from cache or computed), or empty if an error occurred
     */
    public <T> Optional<T> getOrCompute(CachePort.CacheRegion region, String key, Class<T> type, Function<String, T> provider) {
        return getOrCompute(region, key, type, provider, null);
    }

    /**
     * Gets a value from the cache, computing it if not present, with expiration.
     *
     * @param <T>      The type of the value
     * @param region   The cache region
     * @param key      The cache key
     * @param type     The class of the value
     * @param provider The function to compute the value if not in cache
     * @param ttl      The time-to-live for the cached value
     * @return An Optional containing the value (either from cache or computed), or empty if an error occurred
     */
    public <T> Optional<T> getOrCompute(CachePort.CacheRegion region, String key, Class<T> type, Function<String, T> provider, Duration ttl) {
        CachePort.CacheResult result = cachePort.getOrCompute(region, key, type, provider, ttl);
        if (result.isSuccessful()) {
            @SuppressWarnings("unchecked")
            T value = (T) result.getAttributes().get("value");
            return Optional.ofNullable(value);
        } else {
            logger.warn("Failed to get or compute value for key {}: {}", key, result.getReason().orElse("Unknown reason"));
            return Optional.empty();
        }
    }

    /**
     * Removes a value from the cache.
     *
     * @param region The cache region
     * @param key    The cache key
     * @return True if the value was removed successfully, false otherwise
     */
    public boolean remove(CachePort.CacheRegion region, String key) {
        CachePort.CacheResult result = cachePort.remove(region, key);
        return result.isSuccessful();
    }

    /**
     * Checks if a key exists in the cache.
     *
     * @param region The cache region
     * @param key    The cache key
     * @return True if the key exists in the cache, false otherwise
     */
    public boolean containsKey(CachePort.CacheRegion region, String key) {
        return cachePort.containsKey(region, key);
    }

    /**
     * Gets all keys in a cache region.
     *
     * @param region The cache region
     * @return A set of keys, or an empty set if an error occurred
     */
    public Set<String> getKeys(CachePort.CacheRegion region) {
        CachePort.CacheResult result = cachePort.getKeys(region);
        if (result.isSuccessful()) {
            @SuppressWarnings("unchecked")
            Set<String> keys = (Set<String>) result.getAttributes().get("keys");
            return keys != null ? keys : Collections.emptySet();
        } else {
            logger.warn("Failed to get keys for region {}: {}", region, result.getReason().orElse("Unknown reason"));
            return Collections.emptySet();
        }
    }

    /**
     * Clears a cache region.
     *
     * @param region The cache region
     * @return True if the region was cleared successfully, false otherwise
     */
    public boolean clearRegion(CachePort.CacheRegion region) {
        CachePort.CacheResult result = cachePort.clearRegion(region);
        if (!result.isSuccessful()) {
            logger.warn("Failed to clear cache region {}: {}", region, result.getReason().orElse("Unknown reason"));
        }
        return result.isSuccessful();
    }

    /**
     * Clears all cache regions.
     *
     * @return True if all regions were cleared successfully, false otherwise
     */
    public boolean clearAll() {
        CachePort.CacheResult result = cachePort.clearAll();
        if (!result.isSuccessful()) {
            logger.warn("Failed to clear all cache regions: {}", result.getReason().orElse("Unknown reason"));
        }
        return result.isSuccessful();
    }

    /**
     * Gets statistics about the cache.
     *
     * @return A map of statistics, or an empty map if an error occurred
     */
    public Map<String, Object> getStatistics() {
        CachePort.CacheResult result = cachePort.getStatistics();
        if (result.isSuccessful()) {
            return result.getAttributes();
        } else {
            logger.warn("Failed to get cache statistics: {}", result.getReason().orElse("Unknown reason"));
            return Collections.emptyMap();
        }
    }

    /**
     * Gets the hit ratio statistic.
     *
     * @return The hit ratio as a double between 0.0 and 1.0, or 0.0 if not available
     */
    public double getHitRatio() {
        Map<String, Object> stats = getStatistics();
        if (stats.containsKey("hitRatio")) {
            return (double) stats.get("hitRatio");
        }
        return 0.0;
    }

    /**
     * Gets the number of entries in a specific region.
     *
     * @param region The cache region
     * @return The number of entries in the region, or 0 if not available
     */
    public int getRegionSize(CachePort.CacheRegion region) {
        Map<String, Object> stats = getStatistics();
        if (stats.containsKey("regionSizes")) {
            @SuppressWarnings("unchecked")
            Map<String, Integer> regionSizes = (Map<String, Integer>) stats.get("regionSizes");
            return regionSizes.getOrDefault(region.name(), 0);
        }
        return 0;
    }

    /**
     * Shuts down the cache service.
     */
    public void shutdown() {
        logger.info("Shutting down cache service");
        CachePort.CacheResult result = cachePort.shutdown();
        if (!result.isSuccessful()) {
            logger.error("Failed to shut down cache service: {}", result.getReason().orElse("Unknown reason"));
        }
    }
}