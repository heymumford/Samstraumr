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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.s8r.application.port.ConfigurationPort;
import org.s8r.application.port.LoggerPort;
import org.s8r.infrastructure.logging.S8rLoggerFactory;

/**
 * Adapter implementation of the ConfigurationPort interface.
 *
 * <p>This adapter bridges between the application's ConfigurationPort interface and the
 * infrastructure's Configuration implementation. It provides access to configuration settings while
 * maintaining the clean architecture separation of concerns.
 */
public class ConfigurationAdapter implements ConfigurationPort {

  private final Configuration configuration;
  private final LoggerPort logger;

  /**
   * Constructs a new ConfigurationAdapter.
   *
   * <p>Uses the singleton Configuration instance and creates a logger.
   */
  public ConfigurationAdapter() {
    this.configuration = Configuration.getInstance();
    this.logger = S8rLoggerFactory.getInstance().getLogger(ConfigurationAdapter.class);
  }

  /**
   * Constructs a new ConfigurationAdapter with a specific Configuration.
   *
   * <p>This constructor is primarily used for testing purposes.
   *
   * @param configuration The Configuration instance to use
   * @param logger The logger to use
   */
  public ConfigurationAdapter(Configuration configuration, LoggerPort logger) {
    this.configuration = configuration;
    this.logger = logger;
  }

  @Override
  public Optional<String> getString(String key) {
    String value = configuration.get(key);
    return Optional.ofNullable(value);
  }

  @Override
  public String getString(String key, String defaultValue) {
    return configuration.get(key, defaultValue);
  }

  @Override
  public Optional<Integer> getInt(String key) {
    String value = configuration.get(key);
    if (value == null) {
      return Optional.empty();
    }

    try {
      return Optional.of(Integer.parseInt(value));
    } catch (NumberFormatException e) {
      logger.warn("Invalid integer value for key '{}': {}", key, value);
      return Optional.empty();
    }
  }

  @Override
  public int getInt(String key, int defaultValue) {
    return configuration.getInt(key, defaultValue);
  }

  @Override
  public Optional<Boolean> getBoolean(String key) {
    String value = configuration.get(key);
    if (value == null) {
      return Optional.empty();
    }

    return Optional.of(
        "true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || "1".equals(value));
  }

  @Override
  public boolean getBoolean(String key, boolean defaultValue) {
    return configuration.getBoolean(key, defaultValue);
  }

  @Override
  public Optional<List<String>> getStringList(String key) {
    String value = configuration.get(key);
    if (value == null) {
      return Optional.empty();
    }

    List<String> result = parseStringList(value);
    return Optional.of(result);
  }

  @Override
  public List<String> getStringList(String key, List<String> defaultValue) {
    String value = configuration.get(key);
    if (value == null) {
      return defaultValue;
    }

    return parseStringList(value);
  }

  @Override
  public Map<String, String> getSubset(String prefix) {
    Map<String, String> result = new HashMap<>();
    Map<String, String> allConfig = configuration.getAll();

    for (Map.Entry<String, String> entry : allConfig.entrySet()) {
      if (entry.getKey().startsWith(prefix)) {
        result.put(entry.getKey(), entry.getValue());
      }
    }

    return result;
  }

  @Override
  public boolean hasKey(String key) {
    return configuration.hasKey(key);
  }

  /**
   * Parses a comma-separated string into a list of strings.
   *
   * @param value The comma-separated string
   * @return A list of trimmed string values
   */
  private List<String> parseStringList(String value) {
    if (value == null || value.trim().isEmpty()) {
      return new ArrayList<>();
    }

    return Arrays.stream(value.split(","))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .collect(Collectors.toList());
  }
}
