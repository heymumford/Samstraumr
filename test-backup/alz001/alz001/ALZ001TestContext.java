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

package org.s8r.test.steps.alz001;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe test context for the ALZ001 test suite.
 * 
 * <p>This class provides a centralized store for sharing data between step definition
 * classes and across test steps. It uses a concurrent hash map to ensure thread safety
 * when tests are run in parallel.
 */
public class ALZ001TestContext {
    
    private final Map<String, Object> dataStore = new ConcurrentHashMap<>();
    
    /**
     * Stores a value in the context with the specified key.
     *
     * @param <T> The type of the value
     * @param key The key to associate with the value
     * @param value The value to store
     */
    public <T> void setData(String key, T value) {
        dataStore.put(key, value);
    }
    
    /**
     * Retrieves a value from the context by key.
     *
     * @param <T> The expected type of the value
     * @param key The key associated with the value
     * @return The value, or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T> T getData(String key) {
        return (T) dataStore.get(key);
    }
    
    /**
     * Retrieves a value from the context by key with type safety.
     *
     * @param <T> The expected type of the value
     * @param key The key associated with the value
     * @param type The Class object representing the expected type
     * @return The value, or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T> T getData(String key, Class<T> type) {
        Object value = dataStore.get(key);
        if (value != null && type.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        return null;
    }
    
    /**
     * Checks if the context contains a value for the specified key.
     *
     * @param key The key to check
     * @return true if the key exists in the context, false otherwise
     */
    public boolean containsKey(String key) {
        return dataStore.containsKey(key);
    }
    
    /**
     * Removes a value from the context.
     *
     * @param key The key of the value to remove
     * @return The removed value, or null if the key was not found
     */
    public Object removeData(String key) {
        return dataStore.remove(key);
    }
    
    /**
     * Clears all data from the context.
     */
    public void clear() {
        dataStore.clear();
    }
    
    /**
     * Gets a copy of all data in the context.
     *
     * @return A map containing all data in the context
     */
    public Map<String, Object> getAllData() {
        return new HashMap<>(dataStore);
    }
}