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

package org.s8r.test.tube;

import java.util.HashMap;
import java.util.Map;

/**
 * Shared test context for storing and retrieving test objects between step definitions.
 *
 * <p>This class provides a simple key-value store for sharing objects between different step
 * definitions in Cucumber tests. It helps maintain state between steps without requiring direct
 * field access.
 */
public class TubeTestContext {
  private Map<String, Object> contextMap = new HashMap<>();

  /**
   * Stores an object in the context.
   *
   * @param key The key to store the object under
   * @param value The object to store
   */
  public void store(String key, Object value) {
    contextMap.put(key, value);
  }

  /**
   * Retrieves an object from the context.
   *
   * @param key The key to retrieve
   * @param <T> The type to cast the result to
   * @return The stored object, or null if not found
   */
  @SuppressWarnings("unchecked")
  public <T> T get(String key) {
    return (T) contextMap.get(key);
  }

  /**
   * Checks if the context contains a key.
   *
   * @param key The key to check
   * @return true if the key exists in the context, false otherwise
   */
  public boolean contains(String key) {
    return contextMap.containsKey(key);
  }

  /**
   * Removes an object from the context.
   *
   * @param key The key to remove
   * @return The removed object, or null if not found
   */
  public Object remove(String key) {
    return contextMap.remove(key);
  }

  /** Clears all objects from the context. */
  public void clear() {
    contextMap.clear();
  }
}
