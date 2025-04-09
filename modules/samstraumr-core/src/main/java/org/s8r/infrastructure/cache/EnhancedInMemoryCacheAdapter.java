/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools 
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights 
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0
 */
package org.s8r.infrastructure.cache;

import org.s8r.application.port.CachePort;
import org.s8r.application.port.LoggerPort;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.function.Function;

/**
 * Enhanced in-memory implementation of the CachePort interface.
 * 
 * <p>This adapter provides an optimized in-memory cache implementation with:
 * - Time-based expiration
 * - LRU (Least Recently Used) eviction policy
 * - Concurrent access optimization
 * - Region-based operation optimization
 * - Statistics tracking
 */
public class EnhancedInMemoryCacheAdapter implements CachePort {

    private static final int DEFAULT_MAX_ENTRIES = 10000;
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(30);
    
    private final Map<String, CacheEntry<?>> cache;
    private final ReadWriteLock cacheLock;
    private final Map<CacheRegion, ReadWriteLock> regionLocks;
    private final int maxEntries;
    private final LoggerPort logger;
    private String cacheName;
    
    // Statistics
    private long hits;
    private long misses;
    private long puts;
    private long removes;
    private long evictions;
    private final Map<CacheRegion, CacheRegionStats> regionStats;

    /**
     * Creates a new EnhancedInMemoryCacheAdapter instance with default settings.
     */
    public EnhancedInMemoryCacheAdapter() {
        this(null, DEFAULT_MAX_ENTRIES);
    }
    
    /**
     * Creates a new EnhancedInMemoryCacheAdapter with a logger and default settings.
     *
     * @param logger The logger to use for logging
     */
    public EnhancedInMemoryCacheAdapter(LoggerPort logger) {
        this(logger, DEFAULT_MAX_ENTRIES);
    }
    
    /**
     * Creates a new EnhancedInMemoryCacheAdapter with the specified maximum entries.
     *
     * @param logger The logger to use for logging
     * @param maxEntries The maximum number of entries to store in the cache
     */
    public EnhancedInMemoryCacheAdapter(LoggerPort logger, int maxEntries) {
        this.cache = new ConcurrentHashMap<>();
        this.cacheLock = new ReentrantReadWriteLock();
        this.regionLocks = new ConcurrentHashMap<>();
        this.maxEntries = maxEntries;
        this.logger = logger;
        this.hits = 0;
        this.misses = 0;
        this.puts = 0;
        this.removes = 0;
        this.evictions = 0;
        this.regionStats = new ConcurrentHashMap<>();
        
        // Initialize region locks and stats for all regions
        for (CacheRegion region : CacheRegion.values()) {
            regionLocks.put(region, new ReentrantReadWriteLock());
            regionStats.put(region, new CacheRegionStats());
        }
    }

    @Override
    public void initialize(String cacheName) {
        this.cacheName = cacheName;
        
        cacheLock.writeLock().lock();
        try {
            this.cache.clear();
            this.hits = 0;
            this.misses = 0;
            this.puts = 0;
            this.removes = 0;
            this.evictions = 0;
            
            // Reset region stats
            for (CacheRegion region : CacheRegion.values()) {
                regionStats.put(region, new CacheRegionStats());
            }
            
            if (logger != null) {
                logger.info("Initialized cache: " + cacheName);
            }
        } finally {
            cacheLock.writeLock().unlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void put(String key, T value) {
        // Create a cache entry with the default TTL
        CacheEntry<T> entry = new CacheEntry<>(value, Instant.now().plus(DEFAULT_TTL));
        
        cacheLock.writeLock().lock();
        try {
            // Check if we need to evict entries before adding a new one
            if (cache.size() >= maxEntries && !cache.containsKey(key)) {
                evictLRUEntry();
            }
            
            cache.put(key, entry);
            puts++;
            
            // Update region stats if this is a region key
            for (CacheRegion region : CacheRegion.values()) {
                String prefix = region.name() + ":";
                if (key.startsWith(prefix)) {
                    CacheRegionStats stats = regionStats.get(region);
                    stats.puts++;
                    break;
                }
            }
            
            if (logger != null) {
                logger.debug("Cache put: " + key);
            }
        } finally {
            cacheLock.writeLock().unlock();
        }
    }
    
    @Override
    public <T> CacheResult put(CacheRegion region, String key, T value, Duration ttl) {
        try {
            String regionKey = region.name() + ":" + key;
            
            // Create a cache entry with the specified TTL
            Instant expiration = ttl != null ? Instant.now().plus(ttl) : Instant.now().plus(DEFAULT_TTL);
            CacheEntry<T> entry = new CacheEntry<>(value, expiration);
            
            // Get region-specific lock for better concurrency
            ReadWriteLock regionLock = regionLocks.get(region);
            regionLock.writeLock().lock();
            try {
                // Check if we need to evict entries before adding a new one
                if (cache.size() >= maxEntries && !cache.containsKey(regionKey)) {
                    // Fallback to global eviction if needed
                    cacheLock.writeLock().lock();
                    try {
                        if (cache.size() >= maxEntries) {
                            evictLRUEntry();
                        }
                    } finally {
                        cacheLock.writeLock().unlock();
                    }
                }
                
                cache.put(regionKey, entry);
                puts++;
                
                // Update region stats
                CacheRegionStats stats = regionStats.get(region);
                stats.puts++;
                
                if (logger != null) {
                    logger.debug("Cache put in region " + region + ": " + key);
                }
            } finally {
                regionLock.writeLock().unlock();
            }
            
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("region", region);
            attributes.put("key", key);
            attributes.put("expiration", expiration);
            return CacheResult.success("Value cached successfully", attributes);
        } catch (Exception e) {
            if (logger != null) {
                logger.error("Error in cache put: " + e.getMessage(), e);
            }
            return CacheResult.failure("Failed to put value in cache", e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key) {
        cacheLock.readLock().lock();
        try {
            CacheEntry<T> entry = (CacheEntry<T>) cache.get(key);
            
            if (entry == null) {
                misses++;
                
                // Update region stats if this is a region key
                for (CacheRegion region : CacheRegion.values()) {
                    String prefix = region.name() + ":";
                    if (key.startsWith(prefix)) {
                        CacheRegionStats stats = regionStats.get(region);
                        stats.misses++;
                        break;
                    }
                }
                
                if (logger != null) {
                    logger.debug("Cache miss: " + key);
                }
                
                return Optional.empty();
            }
            
            // Check if the entry has expired
            if (entry.isExpired()) {
                // Need to upgrade to write lock to remove the expired entry
                cacheLock.readLock().unlock();
                cacheLock.writeLock().lock();
                try {
                    // Check again in case another thread already removed it
                    entry = (CacheEntry<T>) cache.get(key);
                    if (entry != null && entry.isExpired()) {
                        cache.remove(key);
                        misses++;
                        
                        // Update region stats if this is a region key
                        for (CacheRegion region : CacheRegion.values()) {
                            String prefix = region.name() + ":";
                            if (key.startsWith(prefix)) {
                                CacheRegionStats stats = regionStats.get(region);
                                stats.misses++;
                                break;
                            }
                        }
                        
                        if (logger != null) {
                            logger.debug("Cache entry expired: " + key);
                        }
                        
                        return Optional.empty();
                    }
                    // Reacquire read lock before continuing
                    cacheLock.readLock().lock();
                } finally {
                    cacheLock.writeLock().unlock();
                }
            }
            
            // Update the access time
            entry.updateAccessTime();
            hits++;
            
            // Update region stats if this is a region key
            for (CacheRegion region : CacheRegion.values()) {
                String prefix = region.name() + ":";
                if (key.startsWith(prefix)) {
                    CacheRegionStats stats = regionStats.get(region);
                    stats.hits++;
                    break;
                }
            }
            
            if (logger != null) {
                logger.debug("Cache hit: " + key);
            }
            
            return Optional.of(entry.getValue());
        } finally {
            cacheLock.readLock().unlock();
        }
    }
    
    @Override
    public <T> CacheResult get(CacheRegion region, String key, Class<T> type) {
        String regionKey = region.name() + ":" + key;
        ReadWriteLock regionLock = regionLocks.get(region);
        
        regionLock.readLock().lock();
        try {
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
            if (logger != null) {
                logger.error("Error in cache get: " + e.getMessage(), e);
            }
            return CacheResult.failure("Failed to get value from cache", e.getMessage());
        } finally {
            regionLock.readLock().unlock();
        }
    }
    
    @Override
    public <T> CacheResult getOrCompute(
            CacheRegion region, String key, Class<T> type, Function<String, T> provider, Duration ttl) {
        
        // First try to get from cache with region-specific lock
        ReadWriteLock regionLock = regionLocks.get(region);
        String regionKey = region.name() + ":" + key;
        
        regionLock.readLock().lock();
        try {
            Optional<T> cachedValue = get(regionKey);
            if (cachedValue.isPresent()) {
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("value", cachedValue.get());
                attributes.put("computed", false);
                attributes.put("region", region);
                attributes.put("key", key);
                
                CacheRegionStats stats = regionStats.get(region);
                stats.cacheHitsForCompute++;
                
                return CacheResult.success("Value retrieved from cache", attributes);
            }
        } finally {
            regionLock.readLock().unlock();
        }
        
        // Value wasn't in cache, compute it
        try {
            T computedValue = provider.apply(key);
            if (computedValue == null) {
                CacheRegionStats stats = regionStats.get(region);
                stats.computeFailures++;
                
                if (logger != null) {
                    logger.warn("Compute provider returned null for key: " + key);
                }
                
                return CacheResult.failure("Computation error", "Provider returned null");
            }
            
            // Put the computed value in the cache
            CacheResult putResult;
            if (ttl != null) {
                putResult = put(region, key, computedValue, ttl);
            } else {
                regionLock.writeLock().lock();
                try {
                    put(regionKey, computedValue);
                    putResult = CacheResult.success("Value cached successfully");
                } finally {
                    regionLock.writeLock().unlock();
                }
            }
            
            CacheRegionStats stats = regionStats.get(region);
            stats.computeOperations++;
            
            if (logger != null) {
                logger.debug("Computed value for key: " + key);
            }
            
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
            CacheRegionStats stats = regionStats.get(region);
            stats.computeFailures++;
            
            if (logger != null) {
                logger.error("Error computing value: " + e.getMessage(), e);
            }
            
            return CacheResult.failure("Computation error", e.getMessage());
        }
    }

    @Override
    public boolean remove(String key) {
        cacheLock.writeLock().lock();
        try {
            boolean removed = cache.remove(key) != null;
            if (removed) {
                removes++;
                
                // Update region stats if this is a region key
                for (CacheRegion region : CacheRegion.values()) {
                    String prefix = region.name() + ":";
                    if (key.startsWith(prefix)) {
                        CacheRegionStats stats = regionStats.get(region);
                        stats.removes++;
                        break;
                    }
                }
                
                if (logger != null) {
                    logger.debug("Cache remove: " + key);
                }
            }
            return removed;
        } finally {
            cacheLock.writeLock().unlock();
        }
    }
    
    @Override
    public CacheResult remove(CacheRegion region, String key) {
        String regionKey = region.name() + ":" + key;
        ReadWriteLock regionLock = regionLocks.get(region);
        
        regionLock.writeLock().lock();
        try {
            boolean removed = remove(regionKey);
            
            if (removed) {
                return CacheResult.success("Value removed successfully");
            } else {
                return CacheResult.failure("Key not found", "No value found for key");
            }
        } catch (Exception e) {
            if (logger != null) {
                logger.error("Error removing from cache: " + e.getMessage(), e);
            }
            return CacheResult.failure("Failed to remove value from cache", e.getMessage());
        } finally {
            regionLock.writeLock().unlock();
        }
    }

    @Override
    public boolean containsKey(String key) {
        cacheLock.readLock().lock();
        try {
            CacheEntry<?> entry = cache.get(key);
            if (entry != null && !entry.isExpired()) {
                return true;
            }
            
            // Check if the entry exists but has expired
            if (entry != null && entry.isExpired()) {
                // Need to upgrade to write lock to remove the expired entry
                cacheLock.readLock().unlock();
                cacheLock.writeLock().lock();
                try {
                    // Check again in case another thread already removed it
                    entry = cache.get(key);
                    if (entry != null && entry.isExpired()) {
                        cache.remove(key);
                        
                        if (logger != null) {
                            logger.debug("Removed expired entry: " + key);
                        }
                    }
                    return false;
                } finally {
                    cacheLock.writeLock().unlock();
                }
            }
            
            return false;
        } finally {
            cacheLock.readLock().unlock();
        }
    }
    
    @Override
    public boolean containsKey(CacheRegion region, String key) {
        String regionKey = region.name() + ":" + key;
        ReadWriteLock regionLock = regionLocks.get(region);
        
        regionLock.readLock().lock();
        try {
            return containsKey(regionKey);
        } finally {
            regionLock.readLock().unlock();
        }
    }
    
    @Override
    public CacheResult getKeys(CacheRegion region) {
        ReadWriteLock regionLock = regionLocks.get(region);
        
        regionLock.readLock().lock();
        try {
            String prefix = region.name() + ":";
            
            Set<String> regionKeys = cache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(prefix) && !entry.getValue().isExpired())
                .map(entry -> entry.getKey().substring(prefix.length()))
                .collect(Collectors.toSet());
            
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("keys", regionKeys);
            attributes.put("count", regionKeys.size());
            
            return CacheResult.success("Retrieved keys successfully", attributes);
        } catch (Exception e) {
            if (logger != null) {
                logger.error("Error getting keys: " + e.getMessage(), e);
            }
            return CacheResult.failure("Error getting keys", e.getMessage());
        } finally {
            regionLock.readLock().unlock();
        }
    }

    @Override
    public void clear() {
        cacheLock.writeLock().lock();
        try {
            cache.clear();
            
            // Reset region stats
            for (CacheRegion region : CacheRegion.values()) {
                regionStats.put(region, new CacheRegionStats());
            }
            
            if (logger != null) {
                logger.info("Cache cleared");
            }
        } finally {
            cacheLock.writeLock().unlock();
        }
    }
    
    @Override
    public CacheResult clearRegion(CacheRegion region) {
        ReadWriteLock regionLock = regionLocks.get(region);
        
        regionLock.writeLock().lock();
        try {
            String prefix = region.name() + ":";
            
            cacheLock.writeLock().lock();
            try {
                // Remove all entries for this region
                Set<String> keysToRemove = cache.keySet().stream()
                    .filter(key -> key.startsWith(prefix))
                    .collect(Collectors.toSet());
                
                for (String key : keysToRemove) {
                    cache.remove(key);
                }
                
                // Reset region stats
                regionStats.put(region, new CacheRegionStats());
                
                if (logger != null) {
                    logger.info("Cleared cache region: " + region + " (" + keysToRemove.size() + " entries)");
                }
                
                return CacheResult.success("Region cleared successfully", Map.of("keysRemoved", keysToRemove.size()));
            } finally {
                cacheLock.writeLock().unlock();
            }
        } catch (Exception e) {
            if (logger != null) {
                logger.error("Error clearing region: " + e.getMessage(), e);
            }
            return CacheResult.failure("Error clearing region", e.getMessage());
        } finally {
            regionLock.writeLock().unlock();
        }
    }
    
    @Override
    public CacheResult getStatistics() {
        cacheLock.readLock().lock();
        try {
            Map<String, Object> stats = new HashMap<>();
            
            long totalOperations = hits + misses;
            double hitRatio = totalOperations > 0 ? (double) hits / totalOperations : 0.0;
            
            stats.put("hits", hits);
            stats.put("misses", misses);
            stats.put("puts", puts);
            stats.put("removes", removes);
            stats.put("evictions", evictions);
            stats.put("hitRatio", hitRatio);
            stats.put("totalSize", cache.size());
            
            // Get counts and stats per region
            Map<String, Map<String, Object>> regionMetrics = new HashMap<>();
            for (CacheRegion region : CacheRegion.values()) {
                String prefix = region.name() + ":";
                CacheRegionStats regionStat = regionStats.get(region);
                
                // Count entries in this region
                int count = (int) cache.keySet().stream()
                    .filter(key -> key.startsWith(prefix))
                    .count();
                
                Map<String, Object> regionData = new HashMap<>();
                regionData.put("entryCount", count);
                regionData.put("hits", regionStat.hits);
                regionData.put("misses", regionStat.misses);
                regionData.put("puts", regionStat.puts);
                regionData.put("removes", regionStat.removes);
                regionData.put("computeOperations", regionStat.computeOperations);
                regionData.put("cacheHitsForCompute", regionStat.cacheHitsForCompute);
                regionData.put("computeFailures", regionStat.computeFailures);
                
                long regionOps = regionStat.hits + regionStat.misses;
                double regionHitRatio = regionOps > 0 ? (double) regionStat.hits / regionOps : 0.0;
                regionData.put("hitRatio", regionHitRatio);
                
                regionMetrics.put(region.name(), regionData);
            }
            stats.put("regionMetrics", regionMetrics);
            
            // Count expired entries (for diagnostic purposes)
            long expiredCount = cache.values().stream()
                .filter(CacheEntry::isExpired)
                .count();
            stats.put("expiredEntries", expiredCount);
            
            return CacheResult.success("Statistics retrieved successfully", stats);
        } finally {
            cacheLock.readLock().unlock();
        }
    }
    
    /**
     * Removes the least recently used entry from the cache.
     * This method must be called with the write lock held.
     */
    private void evictLRUEntry() {
        if (cache.isEmpty()) {
            return;
        }
        
        // Find the entry with the oldest access time
        String oldestKey = null;
        Instant oldestAccess = Instant.MAX;
        
        for (Map.Entry<String, CacheEntry<?>> entry : cache.entrySet()) {
            Instant accessTime = entry.getValue().getLastAccessTime();
            if (accessTime.isBefore(oldestAccess)) {
                oldestAccess = accessTime;
                oldestKey = entry.getKey();
            }
        }
        
        if (oldestKey != null) {
            cache.remove(oldestKey);
            evictions++;
            
            if (logger != null) {
                logger.debug("Evicted LRU entry: " + oldestKey);
            }
            
            // Update region stats if this was a region key
            for (CacheRegion region : CacheRegion.values()) {
                String prefix = region.name() + ":";
                if (oldestKey.startsWith(prefix)) {
                    CacheRegionStats stats = regionStats.get(region);
                    stats.evictions++;
                    break;
                }
            }
        }
    }
    
    /**
     * Gets the number of entries in the cache.
     *
     * @return The number of entries in the cache
     */
    public int size() {
        cacheLock.readLock().lock();
        try {
            return cache.size();
        } finally {
            cacheLock.readLock().unlock();
        }
    }
    
    /**
     * Gets the maximum number of entries allowed in the cache.
     *
     * @return The maximum number of entries
     */
    public int getMaxEntries() {
        return maxEntries;
    }
    
    /**
     * Gets the name of this cache.
     *
     * @return The cache name
     */
    public String getCacheName() {
        return cacheName;
    }
    
    /**
     * Resets the cache statistics.
     */
    public void resetStatistics() {
        cacheLock.writeLock().lock();
        try {
            this.hits = 0;
            this.misses = 0;
            this.puts = 0;
            this.removes = 0;
            this.evictions = 0;
            
            // Reset region stats
            for (CacheRegion region : CacheRegion.values()) {
                regionStats.put(region, new CacheRegionStats());
            }
            
            if (logger != null) {
                logger.info("Cache statistics reset");
            }
        } finally {
            cacheLock.writeLock().unlock();
        }
    }
    
    /**
     * Class representing a cache entry with expiration and LRU tracking.
     */
    private static class CacheEntry<T> {
        private final T value;
        private final Instant expirationTime;
        private Instant lastAccessTime;
        
        /**
         * Creates a new cache entry with the specified value and expiration time.
         *
         * @param value The value to store
         * @param expirationTime The time when this entry expires
         */
        CacheEntry(T value, Instant expirationTime) {
            this.value = value;
            this.expirationTime = expirationTime;
            this.lastAccessTime = Instant.now();
        }
        
        /**
         * Gets the value stored in this entry.
         *
         * @return The stored value
         */
        T getValue() {
            return value;
        }
        
        /**
         * Checks if this entry has expired.
         *
         * @return true if the entry has expired, false otherwise
         */
        boolean isExpired() {
            return Instant.now().isAfter(expirationTime);
        }
        
        /**
         * Gets the last access time of this entry.
         *
         * @return The last access time
         */
        Instant getLastAccessTime() {
            return lastAccessTime;
        }
        
        /**
         * Updates the last access time to the current time.
         */
        void updateAccessTime() {
            this.lastAccessTime = Instant.now();
        }
    }
    
    /**
     * Class for tracking statistics for a cache region.
     */
    private static class CacheRegionStats {
        private long hits;
        private long misses;
        private long puts;
        private long removes;
        private long evictions;
        private long computeOperations;
        private long cacheHitsForCompute;
        private long computeFailures;
        
        CacheRegionStats() {
            this.hits = 0;
            this.misses = 0;
            this.puts = 0;
            this.removes = 0;
            this.evictions = 0;
            this.computeOperations = 0;
            this.cacheHitsForCompute = 0;
            this.computeFailures = 0;
        }
    }
}