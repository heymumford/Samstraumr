/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.mozilla.org/en-US/MPL/2.0/
 */

package org.s8r.test.mock;

import org.s8r.application.port.ConfigurationPort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Mock implementation of the ConfigurationPort for testing.
 * 
 * <p>This adapter provides an in-memory implementation of the ConfigurationPort
 * interface for use in testing.
 */
public class MockConfigurationAdapter implements ConfigurationPort {
    
    private final Map<String, String> properties;
    
    /**
     * Creates a new MockConfigurationAdapter.
     */
    public MockConfigurationAdapter() {
        this.properties = new ConcurrentHashMap<>();
    }
    
    /**
     * Resets the configuration to its initial state.
     */
    public void reset() {
        properties.clear();
    }
    
    /**
     * Sets a configuration property.
     * 
     * @param key The property key
     * @param value The property value
     */
    public void setProperty(String key, String value) {
        properties.put(key, value);
    }
    
    /**
     * Removes a configuration property.
     * 
     * @param key The property key
     */
    public void removeProperty(String key) {
        properties.remove(key);
    }
    
    @Override
    public Optional<String> getString(String key) {
        return Optional.ofNullable(properties.get(key));
    }
    
    @Override
    public String getString(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }
    
    @Override
    public Optional<Integer> getInt(String key) {
        String value = properties.get(key);
        if (value == null) {
            return Optional.empty();
        }
        
        try {
            return Optional.of(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
    
    @Override
    public int getInt(String key, int defaultValue) {
        String value = properties.get(key);
        if (value == null) {
            return defaultValue;
        }
        
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    @Override
    public Optional<Boolean> getBoolean(String key) {
        String value = properties.get(key);
        if (value == null) {
            return Optional.empty();
        }
        
        return Optional.of(Boolean.parseBoolean(value));
    }
    
    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = properties.get(key);
        if (value == null) {
            return defaultValue;
        }
        
        return Boolean.parseBoolean(value);
    }
    
    @Override
    public Optional<List<String>> getStringList(String key) {
        String value = properties.get(key);
        if (value == null) {
            return Optional.empty();
        }
        
        List<String> result = new ArrayList<>();
        for (String item : value.split(",")) {
            result.add(item.trim());
        }
        
        return Optional.of(Collections.unmodifiableList(result));
    }
    
    @Override
    public List<String> getStringList(String key, List<String> defaultValue) {
        return getStringList(key).orElse(defaultValue);
    }
    
    @Override
    public Map<String, String> getSubset(String prefix) {
        return properties.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(prefix))
            .collect(Collectors.toMap(
                entry -> entry.getKey().substring(prefix.length()),
                Map.Entry::getValue,
                (a, b) -> b,  // In case of duplicates, use the second value
                HashMap::new
            ));
    }
    
    @Override
    public boolean hasKey(String key) {
        return properties.containsKey(key);
    }
}