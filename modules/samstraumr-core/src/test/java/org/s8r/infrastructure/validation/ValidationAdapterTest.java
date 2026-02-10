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

package org.s8r.infrastructure.validation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.ValidationPort.ValidationResult;
import org.s8r.test.annotation.UnitTest;

/** Unit tests for the ValidationAdapter class. */
@UnitTest
public class ValidationAdapterTest {

  private LoggerPort mockLogger;
  private ValidationAdapter adapter;

  @BeforeEach
  void setUp() {
    mockLogger = mock(LoggerPort.class);
    adapter = new ValidationAdapter(mockLogger);
  }

  @Test
  void testValidateStringWithPattern() {
    // Test valid value
    ValidationResult validResult = adapter.validateString("non-empty", "test");
    assertTrue(validResult.isValid());

    // Test invalid value
    ValidationResult invalidResult = adapter.validateString("non-empty", "");
    assertFalse(invalidResult.isValid());
    assertEquals(1, invalidResult.getErrors().size());

    // Test null value
    ValidationResult nullResult = adapter.validateString("non-empty", null);
    assertFalse(nullResult.isValid());
    assertEquals(1, nullResult.getErrors().size());

    // Test non-existent rule
    ValidationResult nonExistentResult = adapter.validateString("non-existent", "test");
    assertFalse(nonExistentResult.isValid());
    assertEquals(1, nonExistentResult.getErrors().size());

    verify(mockLogger, times(4)).debug(anyString(), anyString());
  }

  @Test
  void testValidateNumber() {
    // Test valid value
    ValidationResult validResult = adapter.validateNumber("positive", 5);
    assertTrue(validResult.isValid());

    // Test invalid value
    ValidationResult invalidResult = adapter.validateNumber("positive", -5);
    assertFalse(invalidResult.isValid());
    assertEquals(1, invalidResult.getErrors().size());

    // Test non-existent rule
    ValidationResult nonExistentResult = adapter.validateNumber("non-existent", 5);
    assertFalse(nonExistentResult.isValid());
    assertEquals(1, nonExistentResult.getErrors().size());

    verify(mockLogger, times(3)).debug(anyString(), anyString());
  }

  @Test
  void testValidateMap() {
    // Create a rule set
    Map<String, Object> ruleSet = new HashMap<>();
    ruleSet.put("name", "non-empty");
    ruleSet.put("age", "positive");
    adapter.registerRuleSet("person", ruleSet);

    // Test valid values
    Map<String, Object> validValues = new HashMap<>();
    validValues.put("name", "John");
    validValues.put("age", 30);

    ValidationResult validResult = adapter.validateMap("person", validValues);
    assertTrue(validResult.isValid());

    // Test invalid values
    Map<String, Object> invalidValues = new HashMap<>();
    invalidValues.put("name", "");
    invalidValues.put("age", -5);

    ValidationResult invalidResult = adapter.validateMap("person", invalidValues);
    assertFalse(invalidResult.isValid());
    assertEquals(2, invalidResult.getErrors().size());

    // Test missing fields
    Map<String, Object> missingValues = new HashMap<>();
    missingValues.put("name", "John");

    ValidationResult missingResult = adapter.validateMap("person", missingValues);
    assertFalse(missingResult.isValid());
    assertEquals(1, missingResult.getErrors().size());

    verify(mockLogger, times(4)).debug(anyString(), anyString());
  }

  @Test
  void testValidateEntity() {
    // Create test data
    Map<String, Object> validComponent = new HashMap<>();
    validComponent.put("id", "test-123");
    validComponent.put("name", "Test Component");
    validComponent.put("state", "ACTIVE");

    ValidationResult validResult = adapter.validateEntity("component", validComponent);
    assertTrue(validResult.isValid());

    // Test invalid component
    Map<String, Object> invalidComponent = new HashMap<>();
    invalidComponent.put("id", "t"); // Too short
    invalidComponent.put("name", "");
    invalidComponent.put("state", "ACTIVE");

    ValidationResult invalidResult = adapter.validateEntity("component", invalidComponent);
    assertFalse(invalidResult.isValid());
    assertEquals(2, invalidResult.getErrors().size());

    // Test non-existent entity type
    ValidationResult nonExistentResult = adapter.validateEntity("non-existent", validComponent);
    assertFalse(nonExistentResult.isValid());
    assertEquals(1, nonExistentResult.getErrors().size());

    verify(mockLogger, times(3)).debug(anyString(), anyString());
  }

  @Test
  void testValidateRequired() {
    // Test valid value
    ValidationResult validResult = adapter.validateRequired("test", "value");
    assertTrue(validResult.isValid());

    // Test empty value
    ValidationResult emptyResult = adapter.validateRequired("test", "");
    assertFalse(emptyResult.isValid());
    assertEquals(1, emptyResult.getErrors().size());

    // Test null value
    ValidationResult nullResult = adapter.validateRequired("test", null);
    assertFalse(nullResult.isValid());
    assertEquals(1, nullResult.getErrors().size());

    verify(mockLogger, times(3)).debug(anyString(), anyString());
  }

  @Test
  void testValidateRange() {
    // Test valid value
    ValidationResult validResult = adapter.validateRange("test", 5, 0, 10);
    assertTrue(validResult.isValid());

    // Test value at lower boundary
    ValidationResult lowerResult = adapter.validateRange("test", 0, 0, 10);
    assertTrue(lowerResult.isValid());

    // Test value at upper boundary
    ValidationResult upperResult = adapter.validateRange("test", 10, 0, 10);
    assertTrue(upperResult.isValid());

    // Test value below range
    ValidationResult belowResult = adapter.validateRange("test", -1, 0, 10);
    assertFalse(belowResult.isValid());
    assertEquals(1, belowResult.getErrors().size());

    // Test value above range
    ValidationResult aboveResult = adapter.validateRange("test", 11, 0, 10);
    assertFalse(aboveResult.isValid());
    assertEquals(1, aboveResult.getErrors().size());

    // Test null value
    ValidationResult nullResult = adapter.validateRange("test", null, 0, 10);
    assertFalse(nullResult.isValid());
    assertEquals(1, nullResult.getErrors().size());

    verify(mockLogger, times(6)).debug(anyString(), anyString());
  }

  @Test
  void testValidateLength() {
    // Test valid value
    ValidationResult validResult = adapter.validateLength("test", "test", 1, 10);
    assertTrue(validResult.isValid());

    // Test value at lower boundary
    ValidationResult lowerResult = adapter.validateLength("test", "a", 1, 10);
    assertTrue(lowerResult.isValid());

    // Test value at upper boundary
    ValidationResult upperResult = adapter.validateLength("test", "abcdefghij", 1, 10);
    assertTrue(upperResult.isValid());

    // Test value below range
    ValidationResult belowResult = adapter.validateLength("test", "", 1, 10);
    assertFalse(belowResult.isValid());
    assertEquals(1, belowResult.getErrors().size());

    // Test value above range
    ValidationResult aboveResult = adapter.validateLength("test", "abcdefghijk", 1, 10);
    assertFalse(aboveResult.isValid());
    assertEquals(1, aboveResult.getErrors().size());

    // Test null value
    ValidationResult nullResult = adapter.validateLength("test", null, 1, 10);
    assertFalse(nullResult.isValid());
    assertEquals(1, nullResult.getErrors().size());

    verify(mockLogger, times(6)).debug(anyString(), anyString());
  }

  @Test
  void testValidatePattern() {
    // Test valid value
    ValidationResult validResult = adapter.validatePattern("test", "abc123", "^[a-z0-9]+$");
    assertTrue(validResult.isValid());

    // Test invalid value
    ValidationResult invalidResult = adapter.validatePattern("test", "ABC123", "^[a-z0-9]+$");
    assertFalse(invalidResult.isValid());
    assertEquals(1, invalidResult.getErrors().size());

    // Test null value
    ValidationResult nullResult = adapter.validatePattern("test", null, "^[a-z0-9]+$");
    assertFalse(nullResult.isValid());
    assertEquals(1, nullResult.getErrors().size());

    // Test invalid pattern
    ValidationResult invalidPatternResult = adapter.validatePattern("test", "abc123", "[");
    assertFalse(invalidPatternResult.isValid());
    assertEquals(1, invalidPatternResult.getErrors().size());

    verify(mockLogger, times(4)).debug(anyString(), anyString());
    verify(mockLogger, times(1)).error(anyString(), anyString(), any(Exception.class));
  }

  @Test
  void testValidateAllowedValues() {
    // Test valid value
    ValidationResult validResult =
        adapter.validateAllowedValues("test", "a", List.of("a", "b", "c"));
    assertTrue(validResult.isValid());

    // Test invalid value
    ValidationResult invalidResult =
        adapter.validateAllowedValues("test", "d", List.of("a", "b", "c"));
    assertFalse(invalidResult.isValid());
    assertEquals(1, invalidResult.getErrors().size());

    // Test null value
    ValidationResult nullResult =
        adapter.validateAllowedValues("test", null, List.of("a", "b", "c"));
    assertFalse(nullResult.isValid());
    assertEquals(1, nullResult.getErrors().size());

    verify(mockLogger, times(3)).debug(anyString(), anyString());
  }

  @Test
  void testRegisterRule() {
    // Register a new rule
    Pattern pattern = Pattern.compile("^[A-Z]+$");
    adapter.registerRule("uppercase", pattern);

    // Test using the new rule
    ValidationResult validResult = adapter.validateString("uppercase", "ABC");
    assertTrue(validResult.isValid());

    ValidationResult invalidResult = adapter.validateString("uppercase", "abc");
    assertFalse(invalidResult.isValid());

    verify(mockLogger, times(1)).debug(anyString(), anyString());
  }

  @Test
  void testRegisterRuleSet() {
    // Register a new rule set
    Map<String, Object> userRules = new HashMap<>();
    userRules.put("username", "alphanumeric");
    userRules.put("age", "positive");
    adapter.registerRuleSet("user", userRules);

    // Test using the new rule set
    Map<String, Object> validUser = new HashMap<>();
    validUser.put("username", "john123");
    validUser.put("age", 25);

    ValidationResult validResult = adapter.validateMap("user", validUser);
    assertTrue(validResult.isValid());

    Map<String, Object> invalidUser = new HashMap<>();
    invalidUser.put("username", "john@123");
    invalidUser.put("age", -5);

    ValidationResult invalidResult = adapter.validateMap("user", invalidUser);
    assertFalse(invalidResult.isValid());
    assertEquals(2, invalidResult.getErrors().size());

    verify(mockLogger, times(3)).debug(anyString(), anyString());
  }

  @Test
  void testRegisterEntityValidator() {
    // Register a new entity validator
    Map<String, Object> userValidator = new HashMap<>();
    userValidator.put("username", "alphanumeric");
    userValidator.put("email", "email");
    adapter.registerEntityValidator("user", userValidator);

    // Test using the new entity validator
    Map<String, Object> validUser = new HashMap<>();
    validUser.put("username", "john123");
    validUser.put("email", "john@example.com");

    ValidationResult validResult = adapter.validateEntity("user", validUser);
    assertTrue(validResult.isValid());

    Map<String, Object> invalidUser = new HashMap<>();
    invalidUser.put("username", "john@123");
    invalidUser.put("email", "notanemail");

    ValidationResult invalidResult = adapter.validateEntity("user", invalidUser);
    assertFalse(invalidResult.isValid());
    assertEquals(2, invalidResult.getErrors().size());

    verify(mockLogger, times(3)).debug(anyString(), anyString());
  }
}
