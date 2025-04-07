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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.ValidationPort;

/**
 * Adapter implementation of the ValidationPort interface.
 * 
 * <p>This adapter provides validation operations for the application using
 * a simple rules-based validation system. It follows Clean Architecture
 * principles by implementing the port interface defined in the application layer.
 */
public class ValidationAdapter implements ValidationPort {
    
    private final LoggerPort logger;
    private final Map<String, Object> rules;
    private final Map<String, Map<String, Object>> ruleSets;
    private final Map<String, Map<String, Object>> entityValidators;
    
    /**
     * Constructs a new ValidationAdapter.
     *
     * @param logger The logger to use
     */
    public ValidationAdapter(LoggerPort logger) {
        this.logger = logger;
        this.rules = new HashMap<>();
        this.ruleSets = new HashMap<>();
        this.entityValidators = new HashMap<>();
        
        // Initialize with standard rules
        initializeStandardRules();
        
        logger.debug("ValidationAdapter initialized with standard rules");
    }
    
    /**
     * Initializes standard validation rules.
     */
    private void initializeStandardRules() {
        // String rules
        rules.put("non-empty", (Pattern) Pattern.compile(".+"));
        rules.put("email", (Pattern) Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"));
        rules.put("alphanumeric", (Pattern) Pattern.compile("^[a-zA-Z0-9]*$"));
        rules.put("numeric", (Pattern) Pattern.compile("^[0-9]*$"));
        rules.put("alphabetic", (Pattern) Pattern.compile("^[a-zA-Z]*$"));
        
        // Number rules
        rules.put("positive", (ValidationRule<Number>) value -> value.doubleValue() > 0);
        rules.put("non-negative", (ValidationRule<Number>) value -> value.doubleValue() >= 0);
        rules.put("id-format", (ValidationRule<String>) value -> 
            value != null && value.length() >= 3 && value.length() <= 50);
            
        // Entity validators
        Map<String, Object> componentValidator = new HashMap<>();
        componentValidator.put("id", "id-format");
        componentValidator.put("name", "non-empty");
        componentValidator.put("state", "non-empty");
        entityValidators.put("component", componentValidator);
        
        Map<String, Object> machineValidator = new HashMap<>();
        machineValidator.put("id", "id-format");
        machineValidator.put("name", "non-empty");
        machineValidator.put("type", "non-empty");
        entityValidators.put("machine", machineValidator);
    }
    
    @Override
    public ValidationResult validateString(String ruleName, String value) {
        logger.debug("Validating string against rule: {}", ruleName);
        
        if (!rules.containsKey(ruleName)) {
            logger.warn("Rule not found: {}", ruleName);
            return ValidationResult.invalid("Validation rule not found: " + ruleName);
        }
        
        Object rule = rules.get(ruleName);
        
        if (rule instanceof Pattern) {
            return validateStringPattern(value, (Pattern) rule, ruleName);
        } else if (rule instanceof ValidationRule) {
            @SuppressWarnings("unchecked")
            ValidationRule<String> stringRule = (ValidationRule<String>) rule;
            boolean valid = stringRule.validate(value);
            return valid ? 
                ValidationResult.valid() : 
                ValidationResult.invalid("Value does not match rule: " + ruleName);
        } else {
            logger.warn("Invalid rule type for string validation: {}", ruleName);
            return ValidationResult.invalid("Invalid rule type for string validation");
        }
    }
    
    /**
     * Validates a string against a pattern.
     *
     * @param value The value to validate
     * @param pattern The pattern to match
     * @param ruleName The name of the rule (for error messages)
     * @return The validation result
     */
    private ValidationResult validateStringPattern(String value, Pattern pattern, String ruleName) {
        if (value == null) {
            return ValidationResult.invalid("Value cannot be null for rule: " + ruleName);
        }
        
        boolean matches = pattern.matcher(value).matches();
        return matches ? 
            ValidationResult.valid() : 
            ValidationResult.invalid("Value does not match pattern for rule: " + ruleName);
    }
    
    @Override
    public ValidationResult validateNumber(String ruleName, Number value) {
        logger.debug("Validating number against rule: {}", ruleName);
        
        if (!rules.containsKey(ruleName)) {
            logger.warn("Rule not found: {}", ruleName);
            return ValidationResult.invalid("Validation rule not found: " + ruleName);
        }
        
        Object rule = rules.get(ruleName);
        
        if (rule instanceof ValidationRule) {
            @SuppressWarnings("unchecked")
            ValidationRule<Number> numberRule = (ValidationRule<Number>) rule;
            boolean valid = numberRule.validate(value);
            return valid ? 
                ValidationResult.valid() : 
                ValidationResult.invalid("Value does not match rule: " + ruleName);
        } else {
            logger.warn("Invalid rule type for number validation: {}", ruleName);
            return ValidationResult.invalid("Invalid rule type for number validation");
        }
    }
    
    @Override
    public ValidationResult validateMap(String ruleSetName, Map<String, Object> values) {
        logger.debug("Validating map against rule set: {}", ruleSetName);
        
        if (!ruleSets.containsKey(ruleSetName)) {
            logger.warn("Rule set not found: {}", ruleSetName);
            return ValidationResult.invalid("Validation rule set not found: " + ruleSetName);
        }
        
        Map<String, Object> ruleSet = ruleSets.get(ruleSetName);
        List<String> errors = new ArrayList<>();
        
        for (Map.Entry<String, Object> rule : ruleSet.entrySet()) {
            String fieldName = rule.getKey();
            String ruleName = rule.getValue().toString();
            
            if (!values.containsKey(fieldName)) {
                errors.add("Missing required field: " + fieldName);
                continue;
            }
            
            Object value = values.get(fieldName);
            
            if (value instanceof String) {
                ValidationResult result = validateString(ruleName, (String) value);
                if (!result.isValid()) {
                    errors.add(fieldName + ": " + String.join(", ", result.getErrors()));
                }
            } else if (value instanceof Number) {
                ValidationResult result = validateNumber(ruleName, (Number) value);
                if (!result.isValid()) {
                    errors.add(fieldName + ": " + String.join(", ", result.getErrors()));
                }
            } else {
                errors.add("Unsupported value type for field: " + fieldName);
            }
        }
        
        return errors.isEmpty() ? 
            ValidationResult.valid() : 
            ValidationResult.invalid(errors);
    }
    
    @Override
    public ValidationResult validateEntity(String entityType, Map<String, Object> entityData) {
        logger.debug("Validating entity of type: {}", entityType);
        
        if (!entityValidators.containsKey(entityType)) {
            logger.warn("Entity validator not found for type: {}", entityType);
            return ValidationResult.invalid("Entity validator not found for type: " + entityType);
        }
        
        Map<String, Object> validator = entityValidators.get(entityType);
        List<String> errors = new ArrayList<>();
        
        for (Map.Entry<String, Object> rule : validator.entrySet()) {
            String fieldName = rule.getKey();
            String ruleName = rule.getValue().toString();
            
            if (!entityData.containsKey(fieldName)) {
                errors.add("Missing required field: " + fieldName);
                continue;
            }
            
            Object value = entityData.get(fieldName);
            
            if (value instanceof String) {
                ValidationResult result = validateString(ruleName, (String) value);
                if (!result.isValid()) {
                    errors.add(fieldName + ": " + String.join(", ", result.getErrors()));
                }
            } else if (value instanceof Number) {
                ValidationResult result = validateNumber(ruleName, (Number) value);
                if (!result.isValid()) {
                    errors.add(fieldName + ": " + String.join(", ", result.getErrors()));
                }
            } else {
                errors.add("Unsupported value type for field: " + fieldName);
            }
        }
        
        return errors.isEmpty() ? 
            ValidationResult.valid() : 
            ValidationResult.invalid(errors);
    }
    
    @Override
    public ValidationResult validateRequired(String fieldName, String value) {
        logger.debug("Validating required field: {}", fieldName);
        
        if (value == null || value.trim().isEmpty()) {
            return ValidationResult.invalid("Field is required: " + fieldName);
        }
        
        return ValidationResult.valid();
    }
    
    @Override
    public ValidationResult validateRange(String fieldName, Number value, Number min, Number max) {
        logger.debug("Validating range for field: {}", fieldName);
        
        if (value == null) {
            return ValidationResult.invalid("Field cannot be null: " + fieldName);
        }
        
        double doubleValue = value.doubleValue();
        double doubleMin = min.doubleValue();
        double doubleMax = max.doubleValue();
        
        if (doubleValue < doubleMin || doubleValue > doubleMax) {
            return ValidationResult.invalid(
                "Field " + fieldName + " must be between " + min + " and " + max);
        }
        
        return ValidationResult.valid();
    }
    
    @Override
    public ValidationResult validateLength(String fieldName, String value, int minLength, int maxLength) {
        logger.debug("Validating length for field: {}", fieldName);
        
        if (value == null) {
            return ValidationResult.invalid("Field cannot be null: " + fieldName);
        }
        
        int length = value.length();
        
        if (length < minLength || length > maxLength) {
            return ValidationResult.invalid(
                "Field " + fieldName + " length must be between " + minLength + " and " + maxLength);
        }
        
        return ValidationResult.valid();
    }
    
    @Override
    public ValidationResult validatePattern(String fieldName, String value, String pattern) {
        logger.debug("Validating pattern for field: {}", fieldName);
        
        if (value == null) {
            return ValidationResult.invalid("Field cannot be null: " + fieldName);
        }
        
        try {
            Pattern compiledPattern = Pattern.compile(pattern);
            boolean matches = compiledPattern.matcher(value).matches();
            
            return matches ? 
                ValidationResult.valid() : 
                ValidationResult.invalid("Field " + fieldName + " does not match the required pattern");
        } catch (PatternSyntaxException e) {
            logger.error("Invalid pattern syntax: {}", pattern, e);
            return ValidationResult.invalid("Invalid pattern syntax for field: " + fieldName);
        }
    }
    
    @Override
    public ValidationResult validateAllowedValues(String fieldName, Object value, List<Object> allowedValues) {
        logger.debug("Validating allowed values for field: {}", fieldName);
        
        if (value == null) {
            return ValidationResult.invalid("Field cannot be null: " + fieldName);
        }
        
        if (allowedValues.contains(value)) {
            return ValidationResult.valid();
        }
        
        return ValidationResult.invalid(
            "Field " + fieldName + " must be one of: " + String.join(", ", 
                allowedValues.stream().map(Object::toString).toList()));
    }
    
    @Override
    public Optional<Object> getRule(String ruleName) {
        return Optional.ofNullable(rules.get(ruleName));
    }
    
    @Override
    public Optional<Map<String, Object>> getRuleSet(String ruleSetName) {
        return Optional.ofNullable(ruleSets.get(ruleSetName));
    }
    
    @Override
    public void registerRule(String ruleName, Object ruleDefinition) {
        logger.debug("Registering rule: {}", ruleName);
        rules.put(ruleName, ruleDefinition);
    }
    
    @Override
    public void registerRuleSet(String ruleSetName, Map<String, Object> ruleSetDefinition) {
        logger.debug("Registering rule set: {}", ruleSetName);
        ruleSets.put(ruleSetName, new HashMap<>(ruleSetDefinition));
    }
    
    /**
     * Registers an entity validator.
     *
     * @param entityType The entity type
     * @param validatorDefinition The validator definition
     */
    public void registerEntityValidator(String entityType, Map<String, Object> validatorDefinition) {
        logger.debug("Registering entity validator: {}", entityType);
        entityValidators.put(entityType, new HashMap<>(validatorDefinition));
    }
    
    /**
     * Functional interface for validation rules.
     *
     * @param <T> The type of value to validate
     */
    @FunctionalInterface
    public interface ValidationRule<T> {
        /**
         * Validates a value.
         *
         * @param value The value to validate
         * @return true if valid, false otherwise
         */
        boolean validate(T value);
    }
}