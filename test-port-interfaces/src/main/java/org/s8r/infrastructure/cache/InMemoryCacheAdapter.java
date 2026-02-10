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

import org.s8r.application.port.CachePort;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
    
    // Statistics
    private long hits;
    private long misses;
    private long puts;
    private long removes;

    /**
     * Creates a new InMemoryCacheAdapter instance.
     */
    public InMemoryCacheAdapter() {
        this.cache = new ConcurrentHashMap<>();
        this.hits = 0;
        this.misses = 0;
        this.puts = 0;
        this.removes = 0;
    }

    @Override
    public void initialize(String cacheName) {
        this.cacheName = cacheName;
        this.cache.clear();
        this.hits = 0;
        this.misses = 0;
        this.puts = 0;
        this.removes = 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void put(String key, T value) {
        cache.put(key, value);
        puts++;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key) {
        T value = (T) cache.get(key);
        
        if (value != null) {
            hits++;
        } else {
            misses++;
        }
        
        return Optional.ofNullable(value);
    }

    @Override
    public boolean remove(String key) {
        boolean removed = cache.remove(key) != null;
        if (removed) {
            removes++;
        }
        return removed;
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
            
            return CacheResult.success("Retrieved keys successfully", attributes);
        } catch (Exception e) {
            return CacheResult.failure("Error getting keys", e.getMessage());
        }
    }

    @Override
    public void clear() {
        cache.clear();
    }
    
    @Override
    public CacheResult getStatistics() {
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
        
        return CacheResult.success("Statistics retrieved successfully", stats);
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