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
package org.s8r.application.port;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Port interface for cache operations in the application layer.
 * 
 * <p>This interface defines the operations that can be performed on a cache,
 * following the ports and adapters pattern from Clean Architecture.
 */
public interface CachePort {

    /**
     * Initializes the cache.
     *
     * @return A CacheResult indicating the outcome of the operation
     */
    default CacheResult initialize() {
        try {
            initialize("defaultCache");
            return CacheResult.success("Cache initialized successfully");
        } catch (Exception e) {
            return CacheResult.failure("Failed to initialize cache", e.getMessage());
        }
    }
    
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
     * Puts a value in the cache with the specified key and region.
     *
     * @param region The cache region
     * @param key The key to associate with the value
     * @param value The value to store in the cache
     * @param ttl Optional time-to-live duration
     * @param <T> The type of the value
     * @return A CacheResult indicating the outcome of the operation
     */
    default <T> CacheResult put(CacheRegion region, String key, T value, Duration ttl) {
        try {
            String regionKey = region.name() + ":" + key;
            put(regionKey, value);
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("region", region);
            attributes.put("key", key);
            return CacheResult.success("Value cached successfully", attributes);
        } catch (Exception e) {
            return CacheResult.failure("Failed to put value in cache", e.getMessage());
        }
    }
    
    /**
     * Retrieves a value from the cache by key.
     *
     * @param key The key to look up
     * @param <T> The expected type of the value
     * @return An Optional containing the value if found, or empty if not found
     */
    <T> Optional<T> get(String key);
    
    /**
     * Retrieves a value from the cache by key and region.
     *
     * @param region The cache region
     * @param key The key to look up
     * @param type The expected class of the value
     * @param <T> The expected type of the value
     * @return A CacheResult indicating the outcome of the operation
     */
    default <T> CacheResult get(CacheRegion region, String key, Class<T> type) {
        try {
            String regionKey = region.name() + ":" + key;
            Optional<T> value = get(regionKey);
            
            if (value.isPresent()) {
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("value", value.get());
                attributes.put("region", region);
                attributes.put("key", key);
                return CacheResult.success("Cache hit", attributes);
            } else {
                return CacheResult.failure("Cache miss", "No value found for key");
            }
        } catch (Exception e) {
            return CacheResult.failure("Failed to get value from cache", e.getMessage());
        }
    }
    
    /**
     * Gets a value from the cache, computing it if not present.
     *
     * @param region The cache region
     * @param key The cache key
     * @param type The class of the value
     * @param provider The function to compute the value if not in cache
     * @param ttl Optional time-to-live duration
     * @param <T> The type of the value
     * @return A CacheResult indicating the outcome of the operation
     */
    default <T> CacheResult getOrCompute(
            CacheRegion region, String key, Class<T> type, Function<String, T> provider, Duration ttl) {
        CacheResult getResult = get(region, key, type);
        
        if (getResult.isSuccessful()) {
            return getResult;
        }
        
        try {
            T computedValue = provider.apply(key);
            if (computedValue == null) {
                return CacheResult.failure("Computation error", "Provider returned null");
            }
            
            CacheResult putResult = put(region, key, computedValue, ttl);
            if (putResult.isSuccessful()) {
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("value", computedValue);
                attributes.put("computed", true);
                attributes.put("region", region);
                attributes.put("key", key);
                return CacheResult.success("Value computed and cached", attributes);
            } else {
                return putResult;
            }
        } catch (Exception e) {
            return CacheResult.failure("Computation error", e.getMessage());
        }
    }
    
    /**
     * Removes a value from the cache by key.
     *
     * @param key The key to remove
     * @return true if the key was found and removed, false otherwise
     */
    boolean remove(String key);
    
    /**
     * Removes a value from the cache by key and region.
     *
     * @param region The cache region
     * @param key The key to remove
     * @return A CacheResult indicating the outcome of the operation
     */
    default CacheResult remove(CacheRegion region, String key) {
        try {
            String regionKey = region.name() + ":" + key;
            boolean removed = remove(regionKey);
            
            if (removed) {
                return CacheResult.success("Value removed successfully");
            } else {
                return CacheResult.failure("Key not found", "No value found for key");
            }
        } catch (Exception e) {
            return CacheResult.failure("Failed to remove value from cache", e.getMessage());
        }
    }
    
    /**
     * Checks if the cache contains a key.
     *
     * @param key The key to check
     * @return true if the key exists in the cache, false otherwise
     */
    boolean containsKey(String key);
    
    /**
     * Checks if the cache contains a key in the specified region.
     *
     * @param region The cache region
     * @param key The key to check
     * @return true if the key exists in the cache region, false otherwise
     */
    default boolean containsKey(CacheRegion region, String key) {
        String regionKey = region.name() + ":" + key;
        return containsKey(regionKey);
    }
    
    /**
     * Gets all keys in a cache region.
     *
     * @param region The cache region
     * @return A CacheResult containing the set of keys in the region
     */
    default CacheResult getKeys(CacheRegion region) {
        try {
            // This is a simplified implementation that may need to be enhanced
            // by specific adapters based on their underlying cache technology
            return CacheResult.success("Retrieved keys successfully", 
                    Map.of("keys", Collections.emptySet()));
        } catch (Exception e) {
            return CacheResult.failure("Error getting keys", e.getMessage());
        }
    }
    
    /**
     * Clears all entries from the cache.
     */
    void clear();
    
    /**
     * Clears a specific cache region.
     *
     * @param region The cache region to clear
     * @return A CacheResult indicating the outcome of the operation
     */
    default CacheResult clearRegion(CacheRegion region) {
        try {
            // This is a simplified implementation that would need to be enhanced
            // in real implementations to only clear specific regions
            clear();
            return CacheResult.success("Region cleared successfully");
        } catch (Exception e) {
            return CacheResult.failure("Error clearing region", e.getMessage());
        }
    }
    
    /**
     * Clears all cache regions.
     *
     * @return A CacheResult indicating the outcome of the operation
     */
    default CacheResult clearAll() {
        try {
            clear();
            return CacheResult.success("All regions cleared successfully");
        } catch (Exception e) {
            return CacheResult.failure("Error clearing all regions", e.getMessage());
        }
    }
    
    /**
     * Gets statistics about the cache.
     *
     * @return A CacheResult containing cache statistics
     */
    default CacheResult getStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("hitRatio", 0.0);
            stats.put("regionSizes", Map.of());
            return CacheResult.success("Statistics retrieved successfully", stats);
        } catch (Exception e) {
            return CacheResult.failure("Error retrieving statistics", e.getMessage());
        }
    }
    
    /**
     * Shuts down the cache.
     *
     * @return A CacheResult indicating the outcome of the operation
     */
    default CacheResult shutdown() {
        try {
            clear();
            return CacheResult.success("Cache shutdown successfully");
        } catch (Exception e) {
            return CacheResult.failure("Shutdown failed", e.getMessage());
        }
    }
    
    /**
     * Enumeration of cache regions for organizing cached data.
     */
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
        SYSTEM
    }
    
    /**
     * Interface for cache operation results.
     */
    interface CacheResult {
        /**
         * Indicates whether the operation was successful.
         *
         * @return true if the operation was successful, false otherwise
         */
        boolean isSuccessful();
        
        /**
         * Gets the message describing the result.
         *
         * @return The result message
         */
        String getMessage();
        
        /**
         * Gets the reason for the result, if applicable.
         *
         * @return An Optional containing the reason, or empty if not applicable
         */
        Optional<String> getReason();
        
        /**
         * Gets additional attributes associated with the result.
         *
         * @return A map of attribute names to values
         */
        default Map<String, Object> getAttributes() {
            return Collections.emptyMap();
        }
        
        /**
         * Creates a successful result with the given message.
         *
         * @param message The success message
         * @return A new CacheResult indicating success
         */
        static CacheResult success(String message) {
            return new SimpleCacheResult(true, message, Optional.empty(), Collections.emptyMap());
        }
        
        /**
         * Creates a successful result with the given message and attributes.
         *
         * @param message The success message
         * @param attributes Additional attributes to include in the result
         * @return A new CacheResult indicating success
         */
        static CacheResult success(String message, Map<String, Object> attributes) {
            return new SimpleCacheResult(true, message, Optional.empty(), attributes);
        }
        
        /**
         * Creates a failure result with the given message and reason.
         *
         * @param message The failure message
         * @param reason The reason for the failure
         * @return A new CacheResult indicating failure
         */
        static CacheResult failure(String message, String reason) {
            return new SimpleCacheResult(false, message, Optional.of(reason), Collections.emptyMap());
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
            return new SimpleCacheResult(false, message, Optional.of(reason), attributes);
        }
    }
    
    /**
     * Simple implementation of the CacheResult interface.
     */
    class SimpleCacheResult implements CacheResult {
        private final boolean successful;
        private final String message;
        private final Optional<String> reason;
        private final Map<String, Object> attributes;
        
        /**
         * Creates a new SimpleCacheResult with the specified parameters.
         *
         * @param successful Whether the operation was successful
         * @param message The message describing the result
         * @param reason The reason for the result, if applicable
         * @param attributes Additional attributes associated with the result
         */
        SimpleCacheResult(boolean successful, String message, Optional<String> reason, Map<String, Object> attributes) {
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
            return reason;
        }
        
        @Override
        public Map<String, Object> getAttributes() {
            return attributes;
        }
    }
}