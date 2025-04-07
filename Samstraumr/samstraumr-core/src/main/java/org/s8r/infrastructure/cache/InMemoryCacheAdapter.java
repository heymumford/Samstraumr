/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.infrastructure.cache;

import org.s8r.application.port.CachePort;
import org.s8r.application.port.LoggerPort;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * In-memory implementation of the CachePort interface.
 * <p>
 * This adapter provides a thread-safe in-memory cache with support for expiration
 * and statistics. It's suitable for development, testing, and lightweight production
 * use cases.
 */
public class InMemoryCacheAdapter implements CachePort {

    private final LoggerPort logger;
    private final Map<CacheRegion, Map<String, CacheEntry<?>>> regionCaches;
    private final ScheduledExecutorService cleanupExecutor;
    private final Duration defaultCleanupInterval;
    private final AtomicLong hits;
    private final AtomicLong misses;
    private final AtomicLong expirations;
    private boolean initialized;

    /**
     * Internal class representing a cache entry with optional expiration.
     */
    private static class CacheEntry<T> {
        private final T value;
        private final Instant expirationTime;
        private final Class<T> type;

        private CacheEntry(T value, Class<T> type, Duration ttl) {
            this.value = value;
            this.type = type;
            this.expirationTime = ttl != null 
                    ? Instant.now().plus(ttl) 
                    : null;
        }

        @SuppressWarnings("unchecked")
        private CacheEntry(T value, Duration ttl) {
            this.value = value;
            this.type = (Class<T>) (value != null ? value.getClass() : Object.class);
            this.expirationTime = ttl != null
                    ? Instant.now().plus(ttl)
                    : null;
        }

        public T getValue() {
            return value;
        }

        public Class<T> getType() {
            return type;
        }

        public boolean isExpired() {
            return expirationTime != null && Instant.now().isAfter(expirationTime);
        }

        public Optional<Instant> getExpirationTime() {
            return Optional.ofNullable(expirationTime);
        }
    }

    /**
     * Creates a new InMemoryCacheAdapter with the specified logger.
     *
     * @param logger The logger to use for logging
     */
    public InMemoryCacheAdapter(LoggerPort logger) {
        this.logger = logger;
        this.regionCaches = new ConcurrentHashMap<>();
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "cache-cleanup-thread");
            t.setDaemon(true);
            return t;
        });
        this.defaultCleanupInterval = Duration.ofMinutes(5);
        this.hits = new AtomicLong(0);
        this.misses = new AtomicLong(0);
        this.expirations = new AtomicLong(0);
        this.initialized = false;
    }

    @Override
    public CacheResult initialize() {
        if (initialized) {
            return CacheResult.success("Cache already initialized");
        }

        try {
            // Initialize cache regions
            for (CacheRegion region : CacheRegion.values()) {
                regionCaches.put(region, new ConcurrentHashMap<>());
            }

            // Schedule cleanup task
            cleanupExecutor.scheduleAtFixedRate(
                    this::cleanupExpiredEntries,
                    defaultCleanupInterval.toMillis(),
                    defaultCleanupInterval.toMillis(),
                    TimeUnit.MILLISECONDS
            );

            initialized = true;
            logger.info("In-memory cache initialized successfully with {} regions", regionCaches.size());
            return CacheResult.success("Cache initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize cache: {}", e.getMessage(), e);
            return CacheResult.failure("Failed to initialize cache", e.getMessage());
        }
    }

    @Override
    public <T> CacheResult get(CacheRegion region, String key, Class<T> type) {
        if (!initialized) {
            return CacheResult.failure("Cache not initialized", "Initialize the cache before use");
        }

        if (region == null || key == null || type == null) {
            return CacheResult.failure("Invalid parameters", "Region, key, and type cannot be null");
        }

        try {
            Map<String, CacheEntry<?>> regionCache = regionCaches.get(region);
            CacheEntry<?> entry = regionCache.get(key);

            if (entry == null) {
                misses.incrementAndGet();
                return CacheResult.failure("Cache miss", "No value found for key: " + key);
            }

            if (entry.isExpired()) {
                regionCache.remove(key);
                expirations.incrementAndGet();
                return CacheResult.failure("Cache miss", "Value expired for key: " + key);
            }

            if (!type.isAssignableFrom(entry.getType())) {
                return CacheResult.failure("Type mismatch", 
                        "Cached value type " + entry.getType().getName() + 
                        " is not compatible with requested type " + type.getName());
            }

            hits.incrementAndGet();
            @SuppressWarnings("unchecked")
            T value = (T) entry.getValue();
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("value", value);
            entry.getExpirationTime().ifPresent(time -> attributes.put("expiresAt", time));

            return CacheResult.success("Cache hit for key: " + key, attributes);
        } catch (Exception e) {
            logger.error("Error retrieving from cache: {}", e.getMessage(), e);
            return CacheResult.failure("Error retrieving from cache", e.getMessage());
        }
    }

    @Override
    public <T> CacheResult put(CacheRegion region, String key, T value) {
        return put(region, key, value, null);
    }

    @Override
    public <T> CacheResult put(CacheRegion region, String key, T value, Duration ttl) {
        if (!initialized) {
            return CacheResult.failure("Cache not initialized", "Initialize the cache before use");
        }

        if (region == null || key == null) {
            return CacheResult.failure("Invalid parameters", "Region and key cannot be null");
        }

        try {
            Map<String, CacheEntry<?>> regionCache = regionCaches.get(region);
            
            if (value == null) {
                regionCache.remove(key);
                return CacheResult.success("Removed null value for key: " + key);
            }

            @SuppressWarnings("unchecked")
            CacheEntry<T> entry = new CacheEntry<>(value, ttl);
            regionCache.put(key, entry);

            Map<String, Object> attributes = new HashMap<>();
            entry.getExpirationTime().ifPresent(time -> attributes.put("expiresAt", time));

            logger.debug("Value cached for key: {} in region: {}", key, region);
            return CacheResult.success("Value cached successfully", attributes);
        } catch (Exception e) {
            logger.error("Error caching value: {}", e.getMessage(), e);
            return CacheResult.failure("Error caching value", e.getMessage());
        }
    }

    @Override
    public <T> CacheResult getOrCompute(CacheRegion region, String key, Class<T> type, Function<String, T> provider) {
        return getOrCompute(region, key, type, provider, null);
    }

    @Override
    public <T> CacheResult getOrCompute(CacheRegion region, String key, Class<T> type, Function<String, T> provider, Duration ttl) {
        if (!initialized) {
            return CacheResult.failure("Cache not initialized", "Initialize the cache before use");
        }

        if (region == null || key == null || type == null || provider == null) {
            return CacheResult.failure("Invalid parameters", "Region, key, type, and provider cannot be null");
        }

        try {
            CacheResult getResult = get(region, key, type);
            
            if (getResult.isSuccessful()) {
                return getResult;
            }

            // Cache miss - compute the value
            T computedValue = provider.apply(key);
            
            if (computedValue == null) {
                return CacheResult.failure("Computation returned null", "Provider returned null for key: " + key);
            }

            // Cache the computed value
            CacheResult putResult = put(region, key, computedValue, ttl);
            
            if (!putResult.isSuccessful()) {
                return putResult;
            }

            // Return the computed value
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("value", computedValue);
            attributes.put("computed", true);
            ttl = ttl != null ? ttl : null;
            if (ttl != null) {
                attributes.put("expiresAt", Instant.now().plus(ttl));
            }

            return CacheResult.success("Value computed and cached", attributes);
        } catch (Exception e) {
            logger.error("Error in getOrCompute: {}", e.getMessage(), e);
            return CacheResult.failure("Error computing value", e.getMessage());
        }
    }

    @Override
    public CacheResult remove(CacheRegion region, String key) {
        if (!initialized) {
            return CacheResult.failure("Cache not initialized", "Initialize the cache before use");
        }

        if (region == null || key == null) {
            return CacheResult.failure("Invalid parameters", "Region and key cannot be null");
        }

        try {
            Map<String, CacheEntry<?>> regionCache = regionCaches.get(region);
            CacheEntry<?> removedEntry = regionCache.remove(key);

            if (removedEntry == null) {
                return CacheResult.failure("Key not found", "No value found for key: " + key);
            }

            logger.debug("Removed value for key: {} from region: {}", key, region);
            return CacheResult.success("Value removed successfully");
        } catch (Exception e) {
            logger.error("Error removing from cache: {}", e.getMessage(), e);
            return CacheResult.failure("Error removing from cache", e.getMessage());
        }
    }

    @Override
    public boolean containsKey(CacheRegion region, String key) {
        if (!initialized || region == null || key == null) {
            return false;
        }

        try {
            Map<String, CacheEntry<?>> regionCache = regionCaches.get(region);
            CacheEntry<?> entry = regionCache.get(key);

            if (entry == null) {
                return false;
            }

            if (entry.isExpired()) {
                regionCache.remove(key);
                expirations.incrementAndGet();
                return false;
            }

            return true;
        } catch (Exception e) {
            logger.warn("Error checking cache key: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public CacheResult getKeys(CacheRegion region) {
        if (!initialized) {
            return CacheResult.failure("Cache not initialized", "Initialize the cache before use");
        }

        if (region == null) {
            return CacheResult.failure("Invalid parameters", "Region cannot be null");
        }

        try {
            Map<String, CacheEntry<?>> regionCache = regionCaches.get(region);
            
            // Filter out expired entries
            Set<String> validKeys = new HashSet<>();
            for (Map.Entry<String, CacheEntry<?>> entry : regionCache.entrySet()) {
                if (!entry.getValue().isExpired()) {
                    validKeys.add(entry.getKey());
                } else {
                    regionCache.remove(entry.getKey());
                    expirations.incrementAndGet();
                }
            }

            return CacheResult.success("Retrieved keys successfully", 
                    Map.of("keys", Collections.unmodifiableSet(validKeys)));
        } catch (Exception e) {
            logger.error("Error getting cache keys: {}", e.getMessage(), e);
            return CacheResult.failure("Error getting cache keys", e.getMessage());
        }
    }

    @Override
    public CacheResult clearRegion(CacheRegion region) {
        if (!initialized) {
            return CacheResult.failure("Cache not initialized", "Initialize the cache before use");
        }

        if (region == null) {
            return CacheResult.failure("Invalid parameters", "Region cannot be null");
        }

        try {
            Map<String, CacheEntry<?>> regionCache = regionCaches.get(region);
            int count = regionCache.size();
            regionCache.clear();

            logger.info("Cleared {} entries from cache region: {}", count, region);
            return CacheResult.success("Cache region cleared successfully", 
                    Map.of("clearedEntries", count));
        } catch (Exception e) {
            logger.error("Error clearing cache region: {}", e.getMessage(), e);
            return CacheResult.failure("Error clearing cache region", e.getMessage());
        }
    }

    @Override
    public CacheResult clearAll() {
        if (!initialized) {
            return CacheResult.failure("Cache not initialized", "Initialize the cache before use");
        }

        try {
            int totalCount = 0;
            for (CacheRegion region : CacheRegion.values()) {
                Map<String, CacheEntry<?>> regionCache = regionCaches.get(region);
                totalCount += regionCache.size();
                regionCache.clear();
            }

            logger.info("Cleared all cache regions, total entries: {}", totalCount);
            return CacheResult.success("All cache regions cleared successfully", 
                    Map.of("clearedEntries", totalCount));
        } catch (Exception e) {
            logger.error("Error clearing all cache regions: {}", e.getMessage(), e);
            return CacheResult.failure("Error clearing all cache regions", e.getMessage());
        }
    }

    @Override
    public CacheResult getStatistics() {
        if (!initialized) {
            return CacheResult.failure("Cache not initialized", "Initialize the cache before use");
        }

        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("hits", hits.get());
            stats.put("misses", misses.get());
            stats.put("expirations", expirations.get());
            
            long totalEntries = 0;
            Map<String, Integer> regionSizes = new HashMap<>();
            
            for (CacheRegion region : CacheRegion.values()) {
                Map<String, CacheEntry<?>> regionCache = regionCaches.get(region);
                int size = regionCache.size();
                totalEntries += size;
                regionSizes.put(region.name(), size);
            }
            
            stats.put("totalEntries", totalEntries);
            stats.put("regionSizes", regionSizes);
            
            double hitRatio = totalEntries > 0 
                    ? (double) hits.get() / (hits.get() + misses.get()) 
                    : 0.0;
            stats.put("hitRatio", hitRatio);

            return CacheResult.success("Statistics retrieved successfully", stats);
        } catch (Exception e) {
            logger.error("Error retrieving cache statistics: {}", e.getMessage(), e);
            return CacheResult.failure("Error retrieving cache statistics", e.getMessage());
        }
    }

    @Override
    public CacheResult shutdown() {
        if (!initialized) {
            return CacheResult.success("Cache not initialized");
        }

        try {
            cleanupExecutor.shutdown();
            if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                cleanupExecutor.shutdownNow();
            }

            for (CacheRegion region : CacheRegion.values()) {
                regionCaches.get(region).clear();
            }
            regionCaches.clear();
            
            initialized = false;
            logger.info("Cache shutdown successfully");
            return CacheResult.success("Cache shutdown successfully");
        } catch (Exception e) {
            logger.error("Error during cache shutdown: {}", e.getMessage(), e);
            return CacheResult.failure("Error during cache shutdown", e.getMessage());
        }
    }

    /**
     * Cleans up expired entries from all cache regions.
     */
    private void cleanupExpiredEntries() {
        try {
            int expiredCount = 0;
            
            for (CacheRegion region : CacheRegion.values()) {
                Map<String, CacheEntry<?>> regionCache = regionCaches.get(region);
                Set<String> keysToRemove = new HashSet<>();
                
                for (Map.Entry<String, CacheEntry<?>> entry : regionCache.entrySet()) {
                    if (entry.getValue().isExpired()) {
                        keysToRemove.add(entry.getKey());
                    }
                }
                
                for (String key : keysToRemove) {
                    regionCache.remove(key);
                    expiredCount++;
                }
            }
            
            if (expiredCount > 0) {
                expirations.addAndGet(expiredCount);
                logger.debug("Cleaned up {} expired cache entries", expiredCount);
            }
        } catch (Exception e) {
            logger.error("Error during cache cleanup: {}", e.getMessage(), e);
        }
    }
}