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

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

import org.s8r.application.dto.ComponentDto;
import org.s8r.application.dto.MachineDto;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.ValidationPort;
import org.s8r.application.port.ValidationPort.ValidationResult;
import org.s8r.domain.machine.MachineType;

/**
 * Application service for validation operations.
 * 
 * <p>This service uses the ValidationPort to provide data validation
 * services to the application. It provides high-level validation operations
 * while relying on the port interface for the actual validation logic.
 */
public class ValidationService {

    private final ValidationPort validationPort;
    private final LoggerPort logger;
    
    /**
     * Constructs a new ValidationService.
     *
     * @param validationPort The validation port to use
     * @param logger The logger to use
     */
    public ValidationService(ValidationPort validationPort, LoggerPort logger) {
        this.validationPort = validationPort;
        this.logger = logger;
        
        logger.debug("ValidationService created");
        
        // Register additional validation rules
        registerDomainRules();
    }
    
    /**
     * Registers domain-specific validation rules.
     */
    private void registerDomainRules() {
        // Machine type validation rule set
        Map<String, Object> machineTypeRules = new HashMap<>();
        machineTypeRules.put("id", "id-format");
        machineTypeRules.put("type", "non-empty");
        machineTypeRules.put("name", "non-empty");
        machineTypeRules.put("version", "non-empty");
        validationPort.registerRuleSet("machine-type", machineTypeRules);
        
        // Component validation rule set
        Map<String, Object> componentRules = new HashMap<>();
        componentRules.put("id", "id-format");
        componentRules.put("name", "non-empty");
        componentRules.put("type", "non-empty");
        validationPort.registerRuleSet("component", componentRules);
        
        logger.debug("Domain validation rules registered");
    }
    
    /**
     * Validates a component DTO.
     *
     * @param component The component DTO to validate
     * @return The validation result
     */
    public ValidationResult validateComponent(ComponentDto component) {
        logger.debug("Validating component: {}", component.getId());
        
        Map<String, Object> componentData = new HashMap<>();
        componentData.put("id", component.getId().getShortId());
        componentData.put("name", component.getName());
        componentData.put("type", component.getType());
        
        return validationPort.validateEntity("component", componentData);
    }
    
    /**
     * Validates a machine DTO.
     *
     * @param machine The machine DTO to validate
     * @return The validation result
     */
    public ValidationResult validateMachine(MachineDto machine) {
        logger.debug("Validating machine: {}", machine.getId());
        
        Map<String, Object> machineData = new HashMap<>();
        machineData.put("id", machine.getId().getShortId());
        machineData.put("name", machine.getName());
        machineData.put("type", machine.getType().toString());
        
        return validationPort.validateEntity("machine", machineData);
    }
    
    /**
     * Validates machine creation parameters.
     *
     * @param type The machine type
     * @param name The machine name
     * @param description The machine description
     * @param version The machine version
     * @return The validation result
     */
    public ValidationResult validateMachineCreation(
            MachineType type, String name, String description, String version) {
        logger.debug("Validating machine creation parameters");
        
        Map<String, Object> machineData = new HashMap<>();
        machineData.put("type", type.toString());
        machineData.put("name", name);
        machineData.put("version", version);
        
        ValidationResult typeResult = validationPort.validateString("non-empty", type.toString());
        ValidationResult nameResult = validationPort.validateString("non-empty", name);
        ValidationResult versionResult = validationPort.validateString("non-empty", version);
        
        if (!typeResult.isValid() || !nameResult.isValid() || !versionResult.isValid()) {
            return ValidationResult.invalid(List.of(
                "Machine creation requires valid type, name, and version"
            ));
        }
        
        return validationPort.validateMap("machine-type", machineData);
    }
    
    /**
     * Validates a field value against a pattern.
     *
     * @param fieldName The field name
     * @param value The value to validate
     * @param pattern The pattern to match
     * @return The validation result
     */
    public ValidationResult validatePattern(String fieldName, String value, String pattern) {
        return validationPort.validatePattern(fieldName, value, pattern);
    }
    
    /**
     * Validates that a field contains one of the allowed values.
     *
     * @param fieldName The field name
     * @param value The value to validate
     * @param allowedValues The allowed values
     * @return The validation result
     */
    public ValidationResult validateAllowedValues(String fieldName, String value, List<String> allowedValues) {
        return validationPort.validateAllowedValues(fieldName, value, allowedValues.stream().map(v -> (Object) v).toList());
    }
    
    /**
     * Validates that a required field is present.
     *
     * @param fieldName The field name
     * @param value The value to validate
     * @return The validation result
     */
    public ValidationResult validateRequired(String fieldName, String value) {
        return validationPort.validateRequired(fieldName, value);
    }
    
    /**
     * Validates that a numeric field is within a range.
     *
     * @param fieldName The field name
     * @param value The value to validate
     * @param min The minimum value
     * @param max The maximum value
     * @return The validation result
     */
    public ValidationResult validateRange(String fieldName, Number value, Number min, Number max) {
        return validationPort.validateRange(fieldName, value, min, max);
    }
    
    /**
     * Adds a custom validation rule.
     *
     * @param ruleName The rule name
     * @param pattern The regex pattern for the rule
     * @return true if the rule was added successfully
     */
    public boolean addCustomRule(String ruleName, String pattern) {
        try {
            java.util.regex.Pattern compiledPattern = java.util.regex.Pattern.compile(pattern);
            validationPort.registerRule(ruleName, compiledPattern);
            logger.info("Added custom validation rule: {}", ruleName);
            return true;
        } catch (Exception e) {
            logger.error("Failed to add custom validation rule: {}", ruleName, e);
            return false;
        }
    }
    
    /**
     * Gets a validation rule by name.
     *
     * @param ruleName The rule name
     * @return The rule if found
     */
    public Optional<Object> getRule(String ruleName) {
        return validationPort.getRule(ruleName);
    }
}