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

package org.s8r.application.port;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Port interface for configuration operations.
 * 
 * <p>This interface defines the standard operations for accessing configuration
 * settings, allowing the application core to remain independent of the specific
 * configuration implementation (file-based, database, environment variables, etc.).
 */
public interface ConfigurationPort {

  /**
   * Gets a string configuration value.
   *
   * @param key The configuration key
   * @return The string value if found
   */
  Optional<String> getString(String key);
  
  /**
   * Gets a string configuration value with a default fallback.
   *
   * @param key The configuration key
   * @param defaultValue The default value to return if the key is not found
   * @return The string value or default
   */
  String getString(String key, String defaultValue);
  
  /**
   * Gets an integer configuration value.
   *
   * @param key The configuration key
   * @return The integer value if found
   */
  Optional<Integer> getInt(String key);
  
  /**
   * Gets an integer configuration value with a default fallback.
   *
   * @param key The configuration key
   * @param defaultValue The default value to return if the key is not found
   * @return The integer value or default
   */
  int getInt(String key, int defaultValue);
  
  /**
   * Gets a boolean configuration value.
   *
   * @param key The configuration key
   * @return The boolean value if found
   */
  Optional<Boolean> getBoolean(String key);
  
  /**
   * Gets a boolean configuration value with a default fallback.
   *
   * @param key The configuration key
   * @param defaultValue The default value to return if the key is not found
   * @return The boolean value or default
   */
  boolean getBoolean(String key, boolean defaultValue);
  
  /**
   * Gets a list of string values.
   *
   * @param key The configuration key
   * @return The list of string values if found
   */
  Optional<List<String>> getStringList(String key);
  
  /**
   * Gets a list of string values with a default fallback.
   *
   * @param key The configuration key
   * @param defaultValue The default value to return if the key is not found
   * @return The list of string values or default
   */
  List<String> getStringList(String key, List<String> defaultValue);
  
  /**
   * Gets a configuration subset as a map.
   *
   * @param prefix The prefix for the configuration keys
   * @return A map of key-value pairs for the subset
   */
  Map<String, String> getSubset(String prefix);
  
  /**
   * Checks if a configuration key exists.
   *
   * @param key The configuration key
   * @return True if the key exists
   */
  boolean hasKey(String key);
}