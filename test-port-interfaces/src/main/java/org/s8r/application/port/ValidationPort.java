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
 * Port interface for validation operations.
 *
 * <p>This interface defines standard operations for validating data in the application. Following
 * Clean Architecture principles, it allows the application core to remain independent of specific
 * validation implementation details.
 */
public interface ValidationPort {

  /** ValidationResult class to encapsulate validation results. */
  class ValidationResult {
    private final boolean valid;
    private final List<String> errors;

    /**
     * Constructs a new ValidationResult.
     *
     * @param valid Whether the validation passed
     * @param errors The validation error messages, if any
     */
    public ValidationResult(boolean valid, List<String> errors) {
      this.valid = valid;
      this.errors = errors;
    }

    /**
     * Checks if the validation passed.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
      return valid;
    }

    /**
     * Gets the validation error messages.
     *
     * @return The list of error messages
     */
    public List<String> getErrors() {
      return errors;
    }

    /**
     * Creates a valid result with no errors.
     *
     * @return A valid ValidationResult
     */
    public static ValidationResult valid() {
      return new ValidationResult(true, List.of());
    }

    /**
     * Creates an invalid result with the specified errors.
     *
     * @param errors The validation error messages
     * @return An invalid ValidationResult
     */
    public static ValidationResult invalid(List<String> errors) {
      return new ValidationResult(false, errors);
    }

    /**
     * Creates an invalid result with a single error.
     *
     * @param error The validation error message
     * @return An invalid ValidationResult
     */
    public static ValidationResult invalid(String error) {
      return new ValidationResult(false, List.of(error));
    }
  }

  /**
   * Validates a string value against a named validation rule.
   *
   * @param ruleName The name of the validation rule
   * @param value The value to validate
   * @return The validation result
   */
  ValidationResult validateString(String ruleName, String value);

  /**
   * Validates a numeric value against a named validation rule.
   *
   * @param ruleName The name of the validation rule
   * @param value The value to validate
   * @return The validation result
   */
  ValidationResult validateNumber(String ruleName, Number value);

  /**
   * Validates a map of values against a named validation rule set.
   *
   * @param ruleSetName The name of the validation rule set
   * @param values The map of values to validate
   * @return The validation result
   */
  ValidationResult validateMap(String ruleSetName, Map<String, Object> values);

  /**
   * Validates an entity represented as a map of values.
   *
   * @param entityType The type of entity to validate
   * @param entityData The map of entity data to validate
   * @return The validation result
   */
  ValidationResult validateEntity(String entityType, Map<String, Object> entityData);

  /**
   * Validates that a required field is present and not empty.
   *
   * @param fieldName The name of the field
   * @param value The value to validate
   * @return The validation result
   */
  ValidationResult validateRequired(String fieldName, String value);

  /**
   * Validates that a numeric value is within the specified range.
   *
   * @param fieldName The name of the field
   * @param value The value to validate
   * @param min The minimum allowed value
   * @param max The maximum allowed value
   * @return The validation result
   */
  ValidationResult validateRange(String fieldName, Number value, Number min, Number max);

  /**
   * Validates that a string value has a length within the specified range.
   *
   * @param fieldName The name of the field
   * @param value The value to validate
   * @param minLength The minimum allowed length
   * @param maxLength The maximum allowed length
   * @return The validation result
   */
  ValidationResult validateLength(String fieldName, String value, int minLength, int maxLength);

  /**
   * Validates that a string value matches a regular expression pattern.
   *
   * @param fieldName The name of the field
   * @param value The value to validate
   * @param pattern The regular expression pattern
   * @return The validation result
   */
  ValidationResult validatePattern(String fieldName, String value, String pattern);

  /**
   * Validates that a value is one of a set of allowed values.
   *
   * @param fieldName The name of the field
   * @param value The value to validate
   * @param allowedValues The set of allowed values
   * @return The validation result
   */
  ValidationResult validateAllowedValues(
      String fieldName, Object value, List<Object> allowedValues);

  /**
   * Gets a validation rule by name.
   *
   * @param ruleName The name of the validation rule
   * @return The validation rule if found
   */
  Optional<Object> getRule(String ruleName);

  /**
   * Gets a validation rule set by name.
   *
   * @param ruleSetName The name of the validation rule set
   * @return The validation rule set if found
   */
  Optional<Map<String, Object>> getRuleSet(String ruleSetName);

  /**
   * Registers a custom validation rule.
   *
   * @param ruleName The name of the validation rule
   * @param ruleDefinition The validation rule definition
   */
  void registerRule(String ruleName, Object ruleDefinition);

  /**
   * Registers a custom validation rule set.
   *
   * @param ruleSetName The name of the validation rule set
   * @param ruleSetDefinition The validation rule set definition
   */
  void registerRuleSet(String ruleSetName, Map<String, Object> ruleSetDefinition);
}