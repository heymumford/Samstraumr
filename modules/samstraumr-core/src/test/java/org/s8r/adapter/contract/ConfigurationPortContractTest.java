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

package org.s8r.adapter.contract;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.s8r.application.port.ConfigurationPort;
import org.s8r.application.port.LoggerPort;

/**
 * Contract tests for the ConfigurationPort interface.
 *
 * <p>This test class verifies that any implementation of the ConfigurationPort interface adheres to
 * the contract defined by the interface. It tests the behavior expected by the application core
 * regardless of the specific adapter implementation.
 *
 * <p>The tests cover operations for retrieving configuration values of various types, including
 * default values and string lists.
 */
public class ConfigurationPortContractTest extends PortContractTest<ConfigurationPort> {

  private MockConfiguration mockConfiguration;

  @Override
  protected ConfigurationPort createPortImplementation() {
    mockConfiguration = new MockConfiguration();
    // Create a custom adapter that uses our mock configuration
    return new MockConfigAdapter(mockConfiguration, logger);
  }

  /** Mock adapter implementation that works with our MockConfiguration */
  private static class MockConfigAdapter implements ConfigurationPort {
    private final MockConfiguration configuration;
    private final LoggerPort logger;

    public MockConfigAdapter(MockConfiguration configuration, LoggerPort logger) {
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
      if (key == null) {
        return Optional.empty();
      }

      String value = configuration.get(key);
      if (value == null) {
        return Optional.empty();
      }

      try {
        return Optional.of(Integer.parseInt(value));
      } catch (NumberFormatException e) {
        logger.debug("Failed to parse integer value for key: {}", key);
        return Optional.empty();
      }
    }

    @Override
    public int getInt(String key, int defaultValue) {
      return configuration.getInt(key, defaultValue);
    }

    @Override
    public Optional<Boolean> getBoolean(String key) {
      if (key == null) {
        return Optional.empty();
      }

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
      if (key == null) {
        return Optional.empty();
      }

      String value = configuration.get(key);
      if (value == null) {
        return Optional.empty();
      }

      if (value.trim().isEmpty()) {
        return Optional.of(new ArrayList<>());
      }

      return Optional.of(
          Arrays.stream(value.split(",")).map(String::trim).collect(Collectors.toList()));
    }

    @Override
    public List<String> getStringList(String key, List<String> defaultValue) {
      return getStringList(key).orElse(defaultValue);
    }

    @Override
    public Map<String, String> getSubset(String prefix) {
      if (prefix == null) {
        return new HashMap<>();
      }

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
  }

  @BeforeEach
  public void setUpTestConfiguration() {
    // Set up some test configuration values
    mockConfiguration.set("string.key", "string value");
    mockConfiguration.set("int.key", "42");
    mockConfiguration.set("bool.key.true", "true");
    mockConfiguration.set("bool.key.false", "false");
    mockConfiguration.set("list.key", "item1, item2, item3");
    mockConfiguration.set("empty.list.key", "");
    mockConfiguration.set("prefix.key1", "value1");
    mockConfiguration.set("prefix.key2", "value2");
  }

  @Override
  protected void verifyNullInputHandling() {
    // This is tested in nullInputHandlingTests()
  }

  @Override
  protected void verifyRequiredMethods() {
    // This is tested across multiple method-specific tests
  }

  /** Verifies that the ConfigurationPort implementation handles null inputs correctly. */
  @Test
  @DisplayName("Should handle null inputs gracefully")
  public void nullInputHandlingTests() {
    // Test null key in getString
    Optional<String> nullStringResult = portUnderTest.getString(null);
    assertTrue(nullStringResult.isEmpty(), "getString(null) should return empty Optional");

    // Test null key in getString with default
    String nullStringDefaultResult = portUnderTest.getString(null, "default");
    assertEquals(
        "default", nullStringDefaultResult, "getString(null, default) should return default value");

    // Test null key in getInt
    Optional<Integer> nullIntResult = portUnderTest.getInt(null);
    assertTrue(nullIntResult.isEmpty(), "getInt(null) should return empty Optional");

    // Test null key in getInt with default
    int nullIntDefaultResult = portUnderTest.getInt(null, 123);
    assertEquals(123, nullIntDefaultResult, "getInt(null, default) should return default value");

    // Test null key in getBoolean
    Optional<Boolean> nullBoolResult = portUnderTest.getBoolean(null);
    assertTrue(nullBoolResult.isEmpty(), "getBoolean(null) should return empty Optional");

    // Test null key in getBoolean with default
    boolean nullBoolDefaultResult = portUnderTest.getBoolean(null, true);
    assertTrue(nullBoolDefaultResult, "getBoolean(null, default) should return default value");

    // Test null key in getStringList
    Optional<List<String>> nullListResult = portUnderTest.getStringList(null);
    assertTrue(nullListResult.isEmpty(), "getStringList(null) should return empty Optional");

    // Test null key in getStringList with default
    List<String> defaultList = Arrays.asList("default1", "default2");
    List<String> nullListDefaultResult = portUnderTest.getStringList(null, defaultList);
    assertEquals(
        defaultList,
        nullListDefaultResult,
        "getStringList(null, default) should return default value");

    // Test null key in getSubset
    Map<String, String> nullSubsetResult = portUnderTest.getSubset(null);
    assertTrue(nullSubsetResult.isEmpty(), "getSubset(null) should return empty map");

    // Test null key in hasKey
    assertFalse(portUnderTest.hasKey(null), "hasKey(null) should return false");
  }

  /** Tests string configuration value retrieval. */
  @Test
  @DisplayName("Should retrieve string values correctly")
  public void stringValueTests() {
    // Test getting an existing string value
    Optional<String> existingValue = portUnderTest.getString("string.key");
    assertTrue(
        existingValue.isPresent(), "getString() should return present Optional for existing key");
    assertEquals("string value", existingValue.get(), "getString() should return correct value");

    // Test getting a non-existent string value
    Optional<String> nonExistentValue = portUnderTest.getString("nonexistent.key");
    assertTrue(
        nonExistentValue.isEmpty(),
        "getString() should return empty Optional for non-existent key");

    // Test getting an existing string value with default
    String existingWithDefault = portUnderTest.getString("string.key", "default");
    assertEquals(
        "string value",
        existingWithDefault,
        "getString() with default should return actual value when key exists");

    // Test getting a non-existent string value with default
    String nonExistentWithDefault = portUnderTest.getString("nonexistent.key", "default");
    assertEquals(
        "default",
        nonExistentWithDefault,
        "getString() with default should return default value when key doesn't exist");
  }

  /** Tests integer configuration value retrieval. */
  @Test
  @DisplayName("Should retrieve integer values correctly")
  public void integerValueTests() {
    // Test getting an existing integer value
    Optional<Integer> existingValue = portUnderTest.getInt("int.key");
    assertTrue(
        existingValue.isPresent(), "getInt() should return present Optional for existing key");
    assertEquals(42, existingValue.get(), "getInt() should return correct value");

    // Test getting a non-existent integer value
    Optional<Integer> nonExistentValue = portUnderTest.getInt("nonexistent.key");
    assertTrue(
        nonExistentValue.isEmpty(), "getInt() should return empty Optional for non-existent key");

    // Test getting an invalid integer value
    Optional<Integer> invalidValue = portUnderTest.getInt("string.key");
    assertTrue(
        invalidValue.isEmpty(), "getInt() should return empty Optional for invalid integer format");

    // Test getting an existing integer value with default
    int existingWithDefault = portUnderTest.getInt("int.key", 100);
    assertEquals(
        42,
        existingWithDefault,
        "getInt() with default should return actual value when key exists");

    // Test getting a non-existent integer value with default
    int nonExistentWithDefault = portUnderTest.getInt("nonexistent.key", 100);
    assertEquals(
        100,
        nonExistentWithDefault,
        "getInt() with default should return default value when key doesn't exist");
  }

  /** Tests boolean configuration value retrieval. */
  @Test
  @DisplayName("Should retrieve boolean values correctly")
  public void booleanValueTests() {
    // Test getting an existing boolean value (true)
    Optional<Boolean> trueValue = portUnderTest.getBoolean("bool.key.true");
    assertTrue(
        trueValue.isPresent(), "getBoolean() should return present Optional for existing key");
    assertTrue(trueValue.get(), "getBoolean() should return true for 'true' value");

    // Test getting an existing boolean value (false)
    Optional<Boolean> falseValue = portUnderTest.getBoolean("bool.key.false");
    assertTrue(
        falseValue.isPresent(), "getBoolean() should return present Optional for existing key");
    assertFalse(falseValue.get(), "getBoolean() should return false for 'false' value");

    // Test getting a non-existent boolean value
    Optional<Boolean> nonExistentValue = portUnderTest.getBoolean("nonexistent.key");
    assertTrue(
        nonExistentValue.isEmpty(),
        "getBoolean() should return empty Optional for non-existent key");

    // Test getting an existing boolean value with default
    boolean trueWithDefault = portUnderTest.getBoolean("bool.key.true", false);
    assertTrue(
        trueWithDefault, "getBoolean() with default should return actual value when key exists");

    // Test getting a non-existent boolean value with default
    boolean nonExistentWithDefault = portUnderTest.getBoolean("nonexistent.key", true);
    assertTrue(
        nonExistentWithDefault,
        "getBoolean() with default should return default value when key doesn't exist");
  }

  /** Tests string list configuration value retrieval. */
  @Test
  @DisplayName("Should retrieve string lists correctly")
  public void stringListTests() {
    // Test getting an existing string list
    Optional<List<String>> existingList = portUnderTest.getStringList("list.key");
    assertTrue(
        existingList.isPresent(),
        "getStringList() should return present Optional for existing key");
    assertEquals(
        3, existingList.get().size(), "getStringList() should return correct number of items");
    assertEquals(
        "item1", existingList.get().get(0), "getStringList() should return correct first item");
    assertEquals(
        "item2", existingList.get().get(1), "getStringList() should return correct second item");
    assertEquals(
        "item3", existingList.get().get(2), "getStringList() should return correct third item");

    // Test getting an empty string list
    Optional<List<String>> emptyList = portUnderTest.getStringList("empty.list.key");
    assertTrue(
        emptyList.isPresent(), "getStringList() should return present Optional for empty list");
    assertTrue(
        emptyList.get().isEmpty(), "getStringList() should return empty list for empty value");

    // Test getting a non-existent string list
    Optional<List<String>> nonExistentList = portUnderTest.getStringList("nonexistent.key");
    assertTrue(
        nonExistentList.isEmpty(),
        "getStringList() should return empty Optional for non-existent key");

    // Test getting an existing string list with default
    List<String> defaultList = Arrays.asList("default1", "default2");
    List<String> existingWithDefault = portUnderTest.getStringList("list.key", defaultList);
    assertEquals(
        3,
        existingWithDefault.size(),
        "getStringList() with default should return actual value when key exists");

    // Test getting a non-existent string list with default
    List<String> nonExistentWithDefault =
        portUnderTest.getStringList("nonexistent.key", defaultList);
    assertEquals(
        defaultList,
        nonExistentWithDefault,
        "getStringList() with default should return default value when key doesn't exist");
  }

  /** Tests configuration subset retrieval. */
  @Test
  @DisplayName("Should retrieve configuration subsets correctly")
  public void subsetTests() {
    // Test getting a configuration subset
    Map<String, String> subset = portUnderTest.getSubset("prefix.");
    assertFalse(subset.isEmpty(), "getSubset() should return non-empty map for existing prefix");
    assertEquals(2, subset.size(), "getSubset() should return correct number of entries");
    assertEquals(
        "value1",
        subset.get("prefix.key1"),
        "getSubset() should return correct value for first key");
    assertEquals(
        "value2",
        subset.get("prefix.key2"),
        "getSubset() should return correct value for second key");

    // Test getting a non-existent configuration subset
    Map<String, String> nonExistentSubset = portUnderTest.getSubset("nonexistent.");
    assertTrue(
        nonExistentSubset.isEmpty(), "getSubset() should return empty map for non-existent prefix");
  }

  /** Tests configuration key existence checking. */
  @Test
  @DisplayName("Should check key existence correctly")
  public void keyExistenceTests() {
    // Test checking existence of an existing key
    assertTrue(portUnderTest.hasKey("string.key"), "hasKey() should return true for existing key");

    // Test checking existence of a non-existent key
    assertFalse(
        portUnderTest.hasKey("nonexistent.key"),
        "hasKey() should return false for non-existent key");
  }

  /** Mock implementation of Configuration for testing. */
  private static class MockConfiguration {
    private final Map<String, String> config = new HashMap<>();

    public void set(String key, String value) {
      if (key != null) {
        config.put(key, value);
      }
    }

    public String get(String key) {
      return key != null ? config.get(key) : null;
    }

    public String get(String key, String defaultValue) {
      return key != null && config.containsKey(key) ? config.get(key) : defaultValue;
    }

    public int getInt(String key, int defaultValue) {
      if (key == null || !config.containsKey(key)) {
        return defaultValue;
      }

      try {
        return Integer.parseInt(config.get(key));
      } catch (NumberFormatException e) {
        return defaultValue;
      }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
      if (key == null || !config.containsKey(key)) {
        return defaultValue;
      }

      String value = config.get(key);
      return "true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || "1".equals(value);
    }

    public boolean hasKey(String key) {
      return key != null && config.containsKey(key);
    }

    public Map<String, String> getAll() {
      return new HashMap<>(config);
    }
  }
}
