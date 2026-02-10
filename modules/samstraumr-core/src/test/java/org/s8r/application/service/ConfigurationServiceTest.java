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

package org.s8r.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.s8r.application.port.ConfigurationPort;
import org.s8r.application.port.LoggerPort;
import org.s8r.test.annotation.UnitTest;

/** Unit tests for the ConfigurationService class. */
@UnitTest
public class ConfigurationServiceTest {

  private ConfigurationPort mockConfigurationPort;
  private LoggerPort mockLogger;
  private ConfigurationService service;

  @BeforeEach
  void setUp() {
    mockConfigurationPort = mock(ConfigurationPort.class);
    mockLogger = mock(LoggerPort.class);
    service = new ConfigurationService(mockConfigurationPort, mockLogger);
  }

  @Test
  void testGetConfigValue() {
    // Setup
    when(mockConfigurationPort.getString("existingKey")).thenReturn(Optional.of("value"));
    when(mockConfigurationPort.getString("missingKey")).thenReturn(Optional.empty());

    // Test existing key
    Optional<String> result = service.getConfigValue("existingKey");
    assertTrue(result.isPresent());
    assertEquals("value", result.get());

    // Test missing key
    Optional<String> missingResult = service.getConfigValue("missingKey");
    assertFalse(missingResult.isPresent());

    // Verify logging
    verify(mockLogger, times(2)).debug(anyString(), anyString());
  }

  @Test
  void testGetConfigValueWithDefault() {
    // Setup
    when(mockConfigurationPort.getString("existingKey", "default")).thenReturn("value");
    when(mockConfigurationPort.getString("missingKey", "default")).thenReturn("default");

    // Test existing key
    String result = service.getConfigValue("existingKey", "default");
    assertEquals("value", result);

    // Test missing key
    String missingResult = service.getConfigValue("missingKey", "default");
    assertEquals("default", missingResult);

    // Verify logging for existing key (not using default)
    verify(mockLogger).debug("Found config value for key {}: {}", "existingKey", "value");

    // Verify logging for missing key (using default)
    verify(mockLogger).debug("Using default value for config key {}: {}", "missingKey", "default");
  }

  @Test
  void testGetSystemConfiguration() {
    // Setup
    Map<String, String> configValues = new HashMap<>();
    configValues.put("system.name", "Samstraumr");
    configValues.put("system.version", "1.0.0");
    when(mockConfigurationPort.getSubset("system.")).thenReturn(configValues);

    // Test
    Map<String, String> result = service.getSystemConfiguration("system.");

    // Verify
    assertEquals(2, result.size());
    assertEquals("Samstraumr", result.get("system.name"));
    assertEquals("1.0.0", result.get("system.version"));
    verify(mockLogger).debug("Getting system configuration with prefix: {}", "system.");
  }

  @Test
  void testIsLoggingEnabled() {
    // Setup - default is true
    when(mockConfigurationPort.getBoolean("component.test-component.logging.enabled", true))
        .thenReturn(true);

    when(mockConfigurationPort.getBoolean("component.disabled-component.logging.enabled", true))
        .thenReturn(false);

    // Test enabled component
    boolean enabledResult = service.isLoggingEnabled("test-component");
    assertTrue(enabledResult);

    // Test disabled component
    boolean disabledResult = service.isLoggingEnabled("disabled-component");
    assertFalse(disabledResult);
  }

  @Test
  void testGetLogLevel() {
    // Setup - default is INFO
    when(mockConfigurationPort.getString("component.test-component.logging.level", "INFO"))
        .thenReturn("DEBUG");

    when(mockConfigurationPort.getString("component.default-component.logging.level", "INFO"))
        .thenReturn("INFO");

    // Test component with custom level
    String customResult = service.getLogLevel("test-component");
    assertEquals("DEBUG", customResult);

    // Test component with default level
    String defaultResult = service.getLogLevel("default-component");
    assertEquals("INFO", defaultResult);
  }

  @Test
  void testGetFeatureFlags() {
    // Setup
    Map<String, String> featuresConfig = new HashMap<>();
    featuresConfig.put("feature.test-feature", "true");
    featuresConfig.put("feature.another-feature", "false");
    featuresConfig.put("feature.yes-feature", "yes");
    featuresConfig.put("feature.number-feature", "1");
    when(mockConfigurationPort.getSubset("feature.")).thenReturn(featuresConfig);

    // Test
    Map<String, Boolean> result = service.getFeatureFlags();

    // Verify
    assertEquals(4, result.size());
    assertTrue(result.get("test-feature"));
    assertFalse(result.get("another-feature"));
    assertTrue(result.get("yes-feature"));
    assertTrue(result.get("number-feature"));
    verify(mockLogger).debug("Retrieved {} feature flags", 4);
  }

  @Test
  void testGetEnabledExtensions() {
    // Setup
    List<String> extensions = List.of("ext1", "ext2", "ext3");
    when(mockConfigurationPort.getStringList("extensions.enabled", List.of()))
        .thenReturn(extensions);

    // Test
    List<String> result = service.getEnabledExtensions();

    // Verify
    assertEquals(3, result.size());
    assertEquals(extensions, result);
  }

  @Test
  void testIsFeatureEnabled() {
    // Setup - default is false
    when(mockConfigurationPort.getBoolean("feature.enabled-feature", false)).thenReturn(true);

    when(mockConfigurationPort.getBoolean("feature.disabled-feature", false)).thenReturn(false);

    // Test enabled feature
    boolean enabledResult = service.isFeatureEnabled("enabled-feature");
    assertTrue(enabledResult);

    // Test disabled feature
    boolean disabledResult = service.isFeatureEnabled("disabled-feature");
    assertFalse(disabledResult);
  }
}
