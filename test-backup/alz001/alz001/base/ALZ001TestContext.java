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

package org.s8r.test.steps.alz001.base;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe test context for storing and sharing objects between step definitions
 * in the ALZ001 test suite. Allows type-safe access to context data with proper
 * generic handling.
 * 
 * <p>This context class is designed to be used as a central repository for all
 * test-related data, enabling data exchange between different step definition classes
 * and supporting component, composite, and machine-level tests.
 */
public class ALZ001TestContext {
    
    /**
     * The internal map that stores all context objects.
     * ConcurrentHashMap provides thread-safety for concurrent access.
     */
    private final Map<String, Object> contextMap = new ConcurrentHashMap<>();
    
    /**
     * Stores an object in the context with the specified key.
     * 
     * @param <T> The type of the value being stored
     * @param key The unique key to identify the stored object
     * @param value The object to store in the context
     */
    public <T> void store(String key, T value) {
        contextMap.put(key, value);
    }
    
    /**
     * Retrieves an object from the context by its key.
     * 
     * @param <T> The expected type of the retrieved object
     * @param key The key used to identify the object
     * @return The object from the context, cast to the expected type
     * @throws ClassCastException if the stored object is not of type T
     */
    @SuppressWarnings("unchecked")
    public <T> T retrieve(String key) {
        return (T) contextMap.get(key);
    }
    
    /**
     * Checks if the context contains an object with the specified key.
     * 
     * @param key The key to check for existence
     * @return true if the key exists in the context, false otherwise
     */
    public boolean contains(String key) {
        return contextMap.containsKey(key);
    }
    
    /**
     * Removes an object from the context by its key.
     * 
     * @param key The key of the object to remove
     * @return The removed object, or null if the key was not found
     */
    public Object remove(String key) {
        return contextMap.remove(key);
    }
    
    /**
     * Removes all objects from the context.
     * Should be called between test scenarios to prevent data leakage.
     */
    public void clear() {
        contextMap.clear();
    }
    
    /**
     * Returns the current size of the context (number of stored objects).
     * 
     * @return The number of objects in the context
     */
    public int size() {
        return contextMap.size();
    }
    
    /**
     * Retrieves an object from the context with a default value if not present.
     * 
     * @param <T> The expected type of the retrieved object
     * @param key The key used to identify the object
     * @param defaultValue The default value to return if the key is not present
     * @return The object from the context if present, or the default value
     */
    @SuppressWarnings("unchecked")
    public <T> T retrieveOrDefault(String key, T defaultValue) {
        return (T) contextMap.getOrDefault(key, defaultValue);
    }
}