/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.infrastructure.cache;

import org.s8r.application.port.CachePort;
import org.s8r.application.port.LoggerPort;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the CachePort interface.
 * 
 * <p>This adapter provides a simple in-memory cache implementation using a ConcurrentHashMap.
 * It is primarily intended for testing and development environments.
 */
public class InMemoryCacheAdapter implements CachePort {

    private final Map<String, Object> cache;
    private String cacheName;
    private final LoggerPort logger;
    
    // Statistics
    private long hits;
    private long misses;
    private long puts;
    private long removes;

    /**
     * Creates a new InMemoryCacheAdapter instance.
     */
    public InMemoryCacheAdapter() {
        this(null);
    }
    
    /**
     * Creates a new InMemoryCacheAdapter instance with the specified logger.
     *
     * @param logger The logger to use for logging, or null if no logging is required
     */
    public InMemoryCacheAdapter(LoggerPort logger) {
        this.cache = new ConcurrentHashMap<>();
        this.logger = logger;
        this.hits = 0;
        this.misses = 0;
        this.puts = 0;
        this.removes = 0;
    }

    @Override
    public CacheResult initialize() {
        return CachePort.super.initialize();
    }

    @Override
    public void initialize(String cacheName) {
        this.cacheName = cacheName;
        this.cache.clear();
        this.hits = 0;
        this.misses = 0;
        this.puts = 0;
        this.removes = 0;
        
        if (logger != null) {
            logger.info("Initialized in-memory cache: {}", cacheName);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void put(String key, T value) {
        cache.put(key, value);
        puts++;
        
        if (logger != null) {
            logger.debug("Added value to cache with key: {}", key);
        }
    }
    
    @Override
    public <T> CacheResult put(CacheRegion region, String key, T value, Duration ttl) {
        try {
            String regionKey = region.name() + ":" + key;
            put(regionKey, value);
            
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("region", region);
            attributes.put("key", key);
            
            if (logger != null) {
                logger.debug("Added value to cache region {} with key: {}", region, key);
            }
            
            return CacheResult.success("Value cached successfully", attributes);
        } catch (Exception e) {
            if (logger != null) {
                logger.error("Failed to put value in cache region {} with key: {}", region, key, e);
            }
            return CacheResult.failure("Failed to put value in cache", e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key) {
        T value = (T) cache.get(key);
        
        if (value != null) {
            hits++;
            if (logger != null) {
                logger.debug("Cache hit for key: {}", key);
            }
        } else {
            misses++;
            if (logger != null) {
                logger.debug("Cache miss for key: {}", key);
            }
        }
        
        return Optional.ofNullable(value);
    }
    
    @Override
    public <T> CacheResult get(CacheRegion region, String key, Class<T> type) {
        try {
            String regionKey = region.name() + ":" + key;
            Optional<T> value = get(regionKey);
            
            if (value.isPresent()) {
                if (type.isInstance(value.get())) {
                    Map<String, Object> attributes = new HashMap<>();
                    attributes.put("value", value.get());
                    attributes.put("region", region);
                    attributes.put("key", key);
                    return CacheResult.success("Cache hit", attributes);
                } else {
                    if (logger != null) {
                        logger.warn("Type mismatch for cache key: {}, expected: {}, found: {}", 
                                key, type.getName(), value.get().getClass().getName());
                    }
                    return CacheResult.failure("Type mismatch", 
                            "Value is not of expected type " + type.getName());
                }
            } else {
                return CacheResult.failure("Cache miss", "No value found for key");
            }
        } catch (Exception e) {
            if (logger != null) {
                logger.error("Failed to get value from cache region {} with key: {}", region, key, e);
            }
            return CacheResult.failure("Failed to get value from cache", e.getMessage());
        }
    }
    
    @Override
    public <T> CacheResult getOrCompute(
            CacheRegion region, String key, Class<T> type, Function<String, T> provider, Duration ttl) {
        
        // First try to get from cache
        CacheResult getResult = get(region, key, type);
        if (getResult.isSuccessful()) {
            return getResult;
        }
        
        // If not found or error, compute
        try {
            if (logger != null) {
                logger.debug("Computing value for cache key: {} in region: {}", key, region);
            }
            
            T computedValue = provider.apply(key);
            if (computedValue == null) {
                if (logger != null) {
                    logger.warn("Provider returned null for key: {} in region: {}", key, region);
                }
                return CacheResult.failure("Computation error", "Provider returned null");
            }
            
            // Cache the computed value
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
            if (logger != null) {
                logger.error("Error computing value for cache key: {} in region: {}", key, region, e);
            }
            return CacheResult.failure("Computation error", e.getMessage());
        }
    }

    @Override
    public boolean remove(String key) {
        boolean removed = cache.remove(key) != null;
        if (removed) {
            removes++;
            if (logger != null) {
                logger.debug("Removed value from cache with key: {}", key);
            }
        }
        return removed;
    }
    
    @Override
    public CacheResult remove(CacheRegion region, String key) {
        try {
            String regionKey = region.name() + ":" + key;
            boolean removed = remove(regionKey);
            
            if (removed) {
                return CacheResult.success("Value removed successfully");
            } else {
                if (logger != null) {
                    logger.debug("No value found for removal with key: {} in region: {}", key, region);
                }
                return CacheResult.failure("Key not found", "No value found for key");
            }
        } catch (Exception e) {
            if (logger != null) {
                logger.error("Failed to remove value from cache region {} with key: {}", region, key, e);
            }
            return CacheResult.failure("Failed to remove value from cache", e.getMessage());
        }
    }

    @Override
    public boolean containsKey(String key) {
        return cache.containsKey(key);
    }
    
    @Override
    public CacheResult getKeys(CacheRegion region) {
        try {
            String prefix = region.name() + ":";
            Set<String> regionKeys = cache.keySet().stream()
                .filter(key -> key.startsWith(prefix))
                .map(key -> key.substring(prefix.length()))
                .collect(Collectors.toSet());
            
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("keys", regionKeys);
            
            if (logger != null) {
                logger.debug("Retrieved {} keys from cache region: {}", regionKeys.size(), region);
            }
            
            return CacheResult.success("Retrieved keys successfully", attributes);
        } catch (Exception e) {
            if (logger != null) {
                logger.error("Error getting keys for cache region: {}", region, e);
            }
            return CacheResult.failure("Error getting keys", e.getMessage());
        }
    }

    @Override
    public void clear() {
        int size = cache.size();
        cache.clear();
        
        if (logger != null) {
            logger.info("Cleared {} entries from cache: {}", size, cacheName);
        }
    }
    
    @Override
    public CacheResult clearRegion(CacheRegion region) {
        try {
            String prefix = region.name() + ":";
            int count = 0;
            
            Set<String> keysToRemove = new HashSet<>();
            for (String key : cache.keySet()) {
                if (key.startsWith(prefix)) {
                    keysToRemove.add(key);
                }
            }
            
            for (String key : keysToRemove) {
                cache.remove(key);
                count++;
            }
            
            if (logger != null) {
                logger.info("Cleared {} entries from cache region: {}", count, region);
            }
            
            return CacheResult.success("Region cleared successfully", Map.of("count", count));
        } catch (Exception e) {
            if (logger != null) {
                logger.error("Error clearing cache region: {}", region, e);
            }
            return CacheResult.failure("Error clearing region", e.getMessage());
        }
    }
    
    @Override
    public CacheResult clearAll() {
        try {
            int size = cache.size();
            clear();
            return CacheResult.success("All regions cleared successfully", Map.of("count", size));
        } catch (Exception e) {
            if (logger != null) {
                logger.error("Error clearing all cache regions", e);
            }
            return CacheResult.failure("Error clearing all regions", e.getMessage());
        }
    }
    
    @Override
    public CacheResult getStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            long totalOperations = hits + misses;
            double hitRatio = totalOperations > 0 ? (double) hits / totalOperations : 0.0;
            
            stats.put("hits", hits);
            stats.put("misses", misses);
            stats.put("puts", puts);
            stats.put("removes", removes);
            stats.put("hitRatio", hitRatio);
            
            // Get counts per region
            Map<String, Integer> regionSizes = new HashMap<>();
            for (CacheRegion region : CacheRegion.values()) {
                String prefix = region.name() + ":";
                int count = (int) cache.keySet().stream()
                    .filter(key -> key.startsWith(prefix))
                    .count();
                regionSizes.put(region.name(), count);
            }
            stats.put("regionSizes", regionSizes);
            stats.put("totalSize", cache.size());
            
            if (logger != null) {
                logger.debug("Retrieved cache statistics: hit ratio={}, total entries={}", hitRatio, cache.size());
            }
            
            return CacheResult.success("Statistics retrieved successfully", stats);
        } catch (Exception e) {
            if (logger != null) {
                logger.error("Error retrieving cache statistics", e);
            }
            return CacheResult.failure("Error retrieving statistics", e.getMessage());
        }
    }
    
    @Override
    public CacheResult shutdown() {
        try {
            if (logger != null) {
                logger.info("Shutting down in-memory cache: {}", cacheName);
            }
            clear();
            return CacheResult.success("Cache shutdown successfully");
        } catch (Exception e) {
            if (logger != null) {
                logger.error("Error shutting down cache", e);
            }
            return CacheResult.failure("Shutdown failed", e.getMessage());
        }
    }
    
    /**
     * Gets the name of this cache.
     *
     * @return The cache name
     */
    public String getCacheName() {
        return cacheName;
    }
}