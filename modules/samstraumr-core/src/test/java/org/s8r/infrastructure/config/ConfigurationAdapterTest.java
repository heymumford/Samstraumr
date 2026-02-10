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

package org.s8r.infrastructure.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.s8r.application.port.LoggerPort;
import org.s8r.test.annotation.UnitTest;

/** Unit tests for the ConfigurationAdapter class. */
@UnitTest
public class ConfigurationAdapterTest {

  private Configuration mockConfiguration;
  private LoggerPort mockLogger;
  private ConfigurationAdapter adapter;

  @BeforeEach
  void setUp() {
    mockConfiguration = mock(Configuration.class);
    mockLogger = mock(LoggerPort.class);
    adapter = new ConfigurationAdapter(mockConfiguration, mockLogger);
  }

  @Test
  void testGetString() {
    // Setup
    when(mockConfiguration.get("existingKey")).thenReturn("value");
    when(mockConfiguration.get("missingKey")).thenReturn(null);

    // Test existing key
    Optional<String> result = adapter.getString("existingKey");
    assertTrue(result.isPresent());
    assertEquals("value", result.get());

    // Test missing key
    Optional<String> missingResult = adapter.getString("missingKey");
    assertFalse(missingResult.isPresent());
  }

  @Test
  void testGetStringWithDefault() {
    // Setup
    when(mockConfiguration.get("existingKey", "default")).thenReturn("value");
    when(mockConfiguration.get("missingKey", "default")).thenReturn("default");

    // Test existing key
    String result = adapter.getString("existingKey", "default");
    assertEquals("value", result);

    // Test missing key
    String missingResult = adapter.getString("missingKey", "default");
    assertEquals("default", missingResult);
  }

  @Test
  void testGetInt() {
    // Setup
    when(mockConfiguration.get("validInt")).thenReturn("42");
    when(mockConfiguration.get("invalidInt")).thenReturn("not-a-number");
    when(mockConfiguration.get("missingKey")).thenReturn(null);

    // Test valid integer
    Optional<Integer> validResult = adapter.getInt("validInt");
    assertTrue(validResult.isPresent());
    assertEquals(42, validResult.get());

    // Test invalid integer
    Optional<Integer> invalidResult = adapter.getInt("invalidInt");
    assertFalse(invalidResult.isPresent());
    verify(mockLogger).warn(anyString(), eq("invalidInt"), eq("not-a-number"));

    // Test missing key
    Optional<Integer> missingResult = adapter.getInt("missingKey");
    assertFalse(missingResult.isPresent());
  }

  @Test
  void testGetIntWithDefault() {
    // Setup
    when(mockConfiguration.getInt("validInt", 99)).thenReturn(42);
    when(mockConfiguration.getInt("missingKey", 99)).thenReturn(99);

    // Test valid integer
    int validResult = adapter.getInt("validInt", 99);
    assertEquals(42, validResult);

    // Test missing key
    int missingResult = adapter.getInt("missingKey", 99);
    assertEquals(99, missingResult);
  }

  @Test
  void testGetBoolean() {
    // Setup
    when(mockConfiguration.get("trueValue1")).thenReturn("true");
    when(mockConfiguration.get("trueValue2")).thenReturn("yes");
    when(mockConfiguration.get("trueValue3")).thenReturn("1");
    when(mockConfiguration.get("falseValue")).thenReturn("false");
    when(mockConfiguration.get("missingKey")).thenReturn(null);

    // Test true values
    assertTrue(adapter.getBoolean("trueValue1").get());
    assertTrue(adapter.getBoolean("trueValue2").get());
    assertTrue(adapter.getBoolean("trueValue3").get());

    // Test false value
    assertFalse(adapter.getBoolean("falseValue").get());

    // Test missing key
    assertFalse(adapter.getBoolean("missingKey").isPresent());
  }

  @Test
  void testGetBooleanWithDefault() {
    // Setup
    when(mockConfiguration.getBoolean("trueValue", false)).thenReturn(true);
    when(mockConfiguration.getBoolean("falseValue", true)).thenReturn(false);
    when(mockConfiguration.getBoolean("missingKey", true)).thenReturn(true);

    // Test true value
    assertTrue(adapter.getBoolean("trueValue", false));

    // Test false value
    assertFalse(adapter.getBoolean("falseValue", true));

    // Test missing key
    assertTrue(adapter.getBoolean("missingKey", true));
  }

  @Test
  void testGetStringList() {
    // Setup
    when(mockConfiguration.get("validList")).thenReturn("item1, item2,item3");
    when(mockConfiguration.get("emptyList")).thenReturn("");
    when(mockConfiguration.get("missingKey")).thenReturn(null);

    // Test valid list
    Optional<List<String>> validResult = adapter.getStringList("validList");
    assertTrue(validResult.isPresent());
    assertEquals(Arrays.asList("item1", "item2", "item3"), validResult.get());

    // Test empty list
    Optional<List<String>> emptyResult = adapter.getStringList("emptyList");
    assertTrue(emptyResult.isPresent());
    assertTrue(emptyResult.get().isEmpty());

    // Test missing key
    Optional<List<String>> missingResult = adapter.getStringList("missingKey");
    assertFalse(missingResult.isPresent());
  }

  @Test
  void testGetStringListWithDefault() {
    // Setup
    List<String> defaultList = Arrays.asList("default1", "default2");
    when(mockConfiguration.get("validList")).thenReturn("item1, item2,item3");
    when(mockConfiguration.get("emptyList")).thenReturn("");
    when(mockConfiguration.get("missingKey")).thenReturn(null);

    // Test valid list
    List<String> validResult = adapter.getStringList("validList", defaultList);
    assertEquals(Arrays.asList("item1", "item2", "item3"), validResult);

    // Test empty list
    List<String> emptyResult = adapter.getStringList("emptyList", defaultList);
    assertTrue(emptyResult.isEmpty());

    // Test missing key
    List<String> missingResult = adapter.getStringList("missingKey", defaultList);
    assertEquals(defaultList, missingResult);
  }

  @Test
  void testGetSubset() {
    // Setup
    Map<String, String> fullConfig = new HashMap<>();
    fullConfig.put("app.name", "Samstraumr");
    fullConfig.put("app.version", "1.0.0");
    fullConfig.put("log.level", "INFO");
    when(mockConfiguration.getAll()).thenReturn(fullConfig);

    // Test getting subset
    Map<String, String> appSubset = adapter.getSubset("app.");
    assertEquals(2, appSubset.size());
    assertEquals("Samstraumr", appSubset.get("app.name"));
    assertEquals("1.0.0", appSubset.get("app.version"));
    assertFalse(appSubset.containsKey("log.level"));

    // Test getting non-existent subset
    Map<String, String> nonExistentSubset = adapter.getSubset("nonexistent.");
    assertTrue(nonExistentSubset.isEmpty());
  }

  @Test
  void testHasKey() {
    // Setup
    when(mockConfiguration.hasKey("existingKey")).thenReturn(true);
    when(mockConfiguration.hasKey("missingKey")).thenReturn(false);

    // Test existing key
    assertTrue(adapter.hasKey("existingKey"));

    // Test missing key
    assertFalse(adapter.hasKey("missingKey"));
  }
}
