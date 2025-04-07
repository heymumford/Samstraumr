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
 * CachePort defines the interface for caching operations in the S8r framework.
 * <p>
 * This port provides caching capabilities to the application, allowing for temporary storage
 * of frequently accessed data to improve performance. It follows Clean Architecture principles 
 * by defining a boundary between the application and infrastructure layers.
 */
public interface CachePort {

    /**
     * Represents the outcome of a cache operation with detailed information.
     */
    final class CacheResult {
        private final boolean successful;
        private final String message;
        private final String reason;
        private final Map<String, Object> attributes;

        private CacheResult(boolean successful, String message, String reason, Map<String, Object> attributes) {
            this.successful = successful;
            this.message = message;
            this.reason = reason;
            this.attributes = attributes;
        }

        /**
         * Creates a successful result.
         *
         * @param message A message describing the successful operation
         * @return A new CacheResult instance indicating success
         */
        public static CacheResult success(String message) {
            return new CacheResult(true, message, null, Map.of());
        }

        /**
         * Creates a successful result with additional attributes.
         *
         * @param message    A message describing the successful operation
         * @param attributes Additional information about the operation
         * @return A new CacheResult instance indicating success
         */
        public static CacheResult success(String message, Map<String, Object> attributes) {
            return new CacheResult(true, message, null, attributes);
        }

        /**
         * Creates a failed result.
         *
         * @param message A message describing the failed operation
         * @param reason  The reason for the failure
         * @return A new CacheResult instance indicating failure
         */
        public static CacheResult failure(String message, String reason) {
            return new CacheResult(false, message, reason, Map.of());
        }

        /**
         * Creates a failed result with additional attributes.
         *
         * @param message    A message describing the failed operation
         * @param reason     The reason for the failure
         * @param attributes Additional information about the operation
         * @return A new CacheResult instance indicating failure
         */
        public static CacheResult failure(String message, String reason, Map<String, Object> attributes) {
            return new CacheResult(false, message, reason, attributes);
        }

        /**
         * Checks if the operation was successful.
         *
         * @return True if the operation was successful, false otherwise
         */
        public boolean isSuccessful() {
            return successful;
        }

        /**
         * Gets the message associated with the operation result.
         *
         * @return The message describing the operation outcome
         */
        public String getMessage() {
            return message;
        }

        /**
         * Gets the reason for a failed operation.
         *
         * @return The reason for the failure, or empty if the operation was successful
         */
        public Optional<String> getReason() {
            return Optional.ofNullable(reason);
        }

        /**
         * Gets the additional attributes associated with the operation result.
         *
         * @return A map of attributes providing additional information
         */
        public Map<String, Object> getAttributes() {
            return attributes;
        }
    }

    /**
     * Storage type for cache operations.
     */
    enum StorageType {
        /**
         * In-memory storage (e.g., HashMap)
         */
        MEMORY,
        
        /**
         * Local disk storage (e.g., file-based)
         */
        LOCAL_DISK,
        
        /**
         * Distributed cache (e.g., Redis, Memcached)
         */
        DISTRIBUTED,
        
        /**
         * Database storage (e.g., SQL, NoSQL)
         */
        DATABASE
    }
    
    /**
     * Cache region/namespace to use for segmenting the cache.
     */
    enum CacheRegion {
        /**
         * For application configuration values
         */
        CONFIGURATION,
        
        /**
         * For component data
         */
        COMPONENT,
        
        /**
         * For machine data
         */
        MACHINE,
        
        /**
         * For computed values (calculations, transformations)
         */
        COMPUTED,
        
        /**
         * For session data (user sessions, auth tokens)
         */
        SESSION,
        
        /**
         * For general-purpose caching
         */
        GENERAL
    }

    /**
     * Initializes the cache.
     *
     * @return A CacheResult indicating success or failure
     */
    CacheResult initialize();

    /**
     * Gets a value from the cache.
     *
     * @param <T>    The type of the value
     * @param region The cache region
     * @param key    The cache key
     * @param type   The class of the value
     * @return A CacheResult containing the value if present
     */
    <T> CacheResult get(CacheRegion region, String key, Class<T> type);

    /**
     * Puts a value in the cache.
     *
     * @param <T>    The type of the value
     * @param region The cache region
     * @param key    The cache key
     * @param value  The value to cache
     * @return A CacheResult indicating success or failure
     */
    <T> CacheResult put(CacheRegion region, String key, T value);

    /**
     * Puts a value in the cache with an expiration time.
     *
     * @param <T>       The type of the value
     * @param region    The cache region
     * @param key       The cache key
     * @param value     The value to cache
     * @param ttl       The time-to-live for the cached value
     * @return A CacheResult indicating success or failure
     */
    <T> CacheResult put(CacheRegion region, String key, T value, Duration ttl);

    /**
     * Gets a value from the cache, computing it if not present.
     *
     * @param <T>       The type of the value
     * @param region    The cache region
     * @param key       The cache key
     * @param type      The class of the value
     * @param provider  The function to compute the value if not in cache
     * @return A CacheResult containing the value (either from cache or computed)
     */
    <T> CacheResult getOrCompute(CacheRegion region, String key, Class<T> type, Function<String, T> provider);

    /**
     * Gets a value from the cache, computing it if not present, with expiration.
     *
     * @param <T>       The type of the value
     * @param region    The cache region
     * @param key       The cache key
     * @param type      The class of the value
     * @param provider  The function to compute the value if not in cache
     * @param ttl       The time-to-live for the cached value
     * @return A CacheResult containing the value (either from cache or computed)
     */
    <T> CacheResult getOrCompute(CacheRegion region, String key, Class<T> type, Function<String, T> provider, Duration ttl);

    /**
     * Removes a value from the cache.
     *
     * @param region The cache region
     * @param key    The cache key
     * @return A CacheResult indicating success or failure
     */
    CacheResult remove(CacheRegion region, String key);

    /**
     * Checks if a key exists in the cache.
     *
     * @param region The cache region
     * @param key    The cache key
     * @return True if the key exists in the cache, false otherwise
     */
    boolean containsKey(CacheRegion region, String key);

    /**
     * Gets all keys in a cache region.
     *
     * @param region The cache region
     * @return A CacheResult containing the set of keys
     */
    CacheResult getKeys(CacheRegion region);

    /**
     * Clears a cache region.
     *
     * @param region The cache region
     * @return A CacheResult indicating success or failure
     */
    CacheResult clearRegion(CacheRegion region);

    /**
     * Clears all cache regions.
     *
     * @return A CacheResult indicating success or failure
     */
    CacheResult clearAll();

    /**
     * Gets statistics about the cache.
     *
     * @return A CacheResult containing statistics
     */
    CacheResult getStatistics();

    /**
     * Shuts down the cache.
     *
     * @return A CacheResult indicating success or failure
     */
    CacheResult shutdown();
    
    /**
     * Gets the storage type used by this cache implementation.
     *
     * @return The storage type
     */
    StorageType getStorageType();
}