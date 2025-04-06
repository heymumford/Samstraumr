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

package org.s8r.infrastructure.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.s8r.application.port.LoggerPort;
import org.s8r.infrastructure.logging.LoggerFactory;

/**
 * Configuration system for the S8r framework.
 *
 * <p>This class provides a centralized configuration system that can load settings from various
 * sources, including properties files, environment variables, and system properties.
 */
public class Configuration {
  private static final String DEFAULT_CONFIG_FILE = "samstraumr.properties";
  private static final LoggerPort logger = LoggerFactory.getLogger(Configuration.class);

  private final Map<String, String> configValues = new HashMap<>();
  private static final Configuration instance = new Configuration();

  /** Private constructor to enforce singleton pattern. */
  private Configuration() {
    // Load default configuration
    loadDefaults();

    // Load from properties file
    loadFromPropertiesFile(DEFAULT_CONFIG_FILE);

    // Override with environment variables
    loadFromEnvironment();

    // Override with system properties
    loadFromSystemProperties();
  }

  /**
   * Gets the singleton instance.
   *
   * @return The Configuration instance
   */
  public static Configuration getInstance() {
    return instance;
  }

  /** Loads default configuration values. */
  private void loadDefaults() {
    configValues.put("log.level", "INFO");
    configValues.put("log.implementation", "SLF4J");
    configValues.put("repository.implementation", "IN_MEMORY");
    configValues.put("machine.default.version", "1.0.0");
  }

  /**
   * Loads configuration from a properties file.
   *
   * @param filename The properties file name
   */
  public void loadFromPropertiesFile(String filename) {
    Path path = Paths.get(filename);

    if (!Files.exists(path)) {
      logger.debug("Properties file not found: " + filename);
      return;
    }

    try (InputStream input = new FileInputStream(path.toFile())) {
      Properties properties = new Properties();
      properties.load(input);

      for (String name : properties.stringPropertyNames()) {
        configValues.put(name, properties.getProperty(name));
      }

      logger.debug("Loaded configuration from " + filename);
    } catch (IOException e) {
      logger.error("Error loading properties file: " + filename, e);
    }
  }

  /** Loads configuration from environment variables. */
  private void loadFromEnvironment() {
    for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
      String key = entry.getKey();

      // Only process S8R_ prefixed environment variables
      if (key.startsWith("S8R_")) {
        // Convert S8R_LOG_LEVEL to log.level
        String configKey = key.substring(4).toLowerCase().replace('_', '.');
        configValues.put(configKey, entry.getValue());
      }
    }
  }

  /** Loads configuration from system properties. */
  private void loadFromSystemProperties() {
    for (String key : System.getProperties().stringPropertyNames()) {
      if (key.startsWith("s8r.")) {
        configValues.put(key, System.getProperty(key));
      }
    }
  }

  /**
   * Gets a configuration value.
   *
   * @param key The configuration key
   * @return The configuration value, or null if not found
   */
  public String get(String key) {
    return configValues.get(key);
  }

  /**
   * Gets a configuration value with a default.
   *
   * @param key The configuration key
   * @param defaultValue The default value to return if the key is not found
   * @return The configuration value, or defaultValue if not found
   */
  public String get(String key, String defaultValue) {
    return configValues.getOrDefault(key, defaultValue);
  }

  /**
   * Gets a configuration value as an integer.
   *
   * @param key The configuration key
   * @param defaultValue The default value to return if the key is not found or not a valid integer
   * @return The configuration value as an integer, or defaultValue if not found or not a valid
   *     integer
   */
  public int getInt(String key, int defaultValue) {
    String value = get(key);

    if (value == null) {
      return defaultValue;
    }

    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      logger.warn("Invalid integer value for " + key + ": " + value);
      return defaultValue;
    }
  }

  /**
   * Gets a configuration value as a boolean.
   *
   * @param key The configuration key
   * @param defaultValue The default value to return if the key is not found
   * @return The configuration value as a boolean, or defaultValue if not found
   */
  public boolean getBoolean(String key, boolean defaultValue) {
    String value = get(key);

    if (value == null) {
      return defaultValue;
    }

    return "true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || "1".equals(value);
  }

  /**
   * Sets a configuration value.
   *
   * @param key The configuration key
   * @param value The configuration value
   */
  public void set(String key, String value) {
    configValues.put(key, value);
  }

  /**
   * Checks if a configuration key exists.
   *
   * @param key The configuration key
   * @return true if the key exists, false otherwise
   */
  public boolean hasKey(String key) {
    return configValues.containsKey(key);
  }

  /**
   * Gets all configuration values.
   *
   * @return A map of all configuration values
   */
  public Map<String, String> getAll() {
    return new HashMap<>(configValues);
  }
}
