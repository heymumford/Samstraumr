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

package org.s8r.test.steps;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.ValidationPort;
import org.s8r.application.port.ValidationPort.ValidationResult;
import org.s8r.infrastructure.logging.ConsoleLogger;
import org.s8r.infrastructure.validation.ValidationAdapter;
import org.s8r.test.annotation.IntegrationTest;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for testing the ValidationPort interface.
 */
@IntegrationTest
public class ValidationPortSteps {

    private ValidationPort validationPort;
    private LoggerPort logger;
    private Map<String, Object> testContext;
    private List<String> logMessages;

    @Before
    public void setup() {
        testContext = new HashMap<>();
        logMessages = new ArrayList<>();
        
        // Create a test logger that captures log messages
        logger = new ConsoleLogger("ValidationPortTest") {
            @Override
            public void info(String message) {
                super.info(message);
                logMessages.add(message);
            }
            
            @Override
            public void info(String message, Object... args) {
                super.info(message);
                String formattedMessage = String.format(message.replace("{}", "%s"), args);
                logMessages.add(formattedMessage);
            }
            
            @Override
            public void debug(String message) {
                super.debug(message);
                logMessages.add(message);
            }
            
            @Override
            public void debug(String message, Object... args) {
                super.debug(message);
                String formattedMessage = String.format(message.replace("{}", "%s"), args);
                logMessages.add(formattedMessage);
            }
        };
        
        // Initialize the validation port
        validationPort = new ValidationAdapter(logger);
        
        // Reset log messages between scenarios
        logMessages.clear();
    }
    
    @After
    public void cleanup() {
        testContext.clear();
        logMessages.clear();
    }

    @Given("a clean system environment")
    public void aCleanSystemEnvironment() {
        // Already set up in the @Before method
        assertNotNull(validationPort, "ValidationPort should be initialized");
    }

    @Given("the ValidationPort interface is properly initialized")
    public void theValidationPortInterfaceIsProperlyInitialized() {
        // Verify the validation port is properly initialized by checking for standard rules
        assertTrue(validationPort.getRule("non-empty").isPresent(), 
                "Standard rule 'non-empty' should be available");
        assertTrue(validationPort.getRule("email").isPresent(), 
                "Standard rule 'email' should be available");
        assertTrue(validationPort.getRule("positive").isPresent(), 
                "Standard rule 'positive' should be available");
    }

    @Given("I have registered a validation rule set {string} with rules:")
    public void iHaveRegisteredAValidationRuleSetWithRules(String ruleSetName, DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        Map<String, Object> ruleSet = new HashMap<>();
        
        for (Map<String, String> row : rows) {
            String fieldName = row.get("fieldName");
            String ruleName = row.get("ruleName");
            ruleSet.put(fieldName, ruleName);
        }
        
        validationPort.registerRuleSet(ruleSetName, ruleSet);
        testContext.put("ruleSetName", ruleSetName);
    }

    @When("I validate the string {string} against the {string} rule")
    public void iValidateTheStringAgainstTheRule(String value, String ruleName) {
        ValidationResult result = validationPort.validateString(ruleName, value);
        
        assertNotNull(result, "Validation result should not be null");
        testContext.put("validationResult", result);
        testContext.put("validatedValue", value);
        testContext.put("ruleName", ruleName);
    }

    @When("I validate the number {int} against the {string} rule")
    public void iValidateTheNumberAgainstTheRule(Integer value, String ruleName) {
        ValidationResult result = validationPort.validateNumber(ruleName, value);
        
        assertNotNull(result, "Validation result should not be null");
        testContext.put("validationResult", result);
        testContext.put("validatedValue", value);
        testContext.put("ruleName", ruleName);
    }

    @When("I validate a field {string} with value {string} as required")
    public void iValidateAFieldWithValueAsRequired(String fieldName, String value) {
        ValidationResult result = validationPort.validateRequired(fieldName, value);
        
        assertNotNull(result, "Validation result should not be null");
        testContext.put("validationResult", result);
        testContext.put("fieldName", fieldName);
        testContext.put("validatedValue", value);
    }

    @When("I validate a field {string} with null value as required")
    public void iValidateAFieldWithNullValueAsRequired(String fieldName) {
        ValidationResult result = validationPort.validateRequired(fieldName, null);
        
        assertNotNull(result, "Validation result should not be null");
        testContext.put("validationResult", result);
        testContext.put("fieldName", fieldName);
        testContext.put("validatedValue", null);
    }

    @When("I validate a field {string} with value {string} for length between {int} and {int}")
    public void iValidateAFieldWithValueForLengthBetweenAnd(
            String fieldName, String value, int minLength, int maxLength) {
        
        ValidationResult result = validationPort.validateLength(fieldName, value, minLength, maxLength);
        
        assertNotNull(result, "Validation result should not be null");
        testContext.put("validationResult", result);
        testContext.put("fieldName", fieldName);
        testContext.put("validatedValue", value);
        testContext.put("minLength", minLength);
        testContext.put("maxLength", maxLength);
    }

    @When("I validate a field {string} with value {int} for range between {int} and {int}")
    public void iValidateAFieldWithValueForRangeBetweenAnd(
            String fieldName, Integer value, Integer min, Integer max) {
        
        ValidationResult result = validationPort.validateRange(fieldName, value, min, max);
        
        assertNotNull(result, "Validation result should not be null");
        testContext.put("validationResult", result);
        testContext.put("fieldName", fieldName);
        testContext.put("validatedValue", value);
        testContext.put("min", min);
        testContext.put("max", max);
    }

    @When("I validate a field {string} with value {string} against pattern {string}")
    public void iValidateAFieldWithValueAgainstPattern(String fieldName, String value, String pattern) {
        ValidationResult result = validationPort.validatePattern(fieldName, value, pattern);
        
        assertNotNull(result, "Validation result should not be null");
        testContext.put("validationResult", result);
        testContext.put("fieldName", fieldName);
        testContext.put("validatedValue", value);
        testContext.put("pattern", pattern);
    }

    @When("I validate a field {string} with value {string} against allowed values:")
    public void iValidateAFieldWithValueAgainstAllowedValues(String fieldName, String value, DataTable dataTable) {
        List<String> allowedValues = dataTable.asList();
        List<Object> allowedObjects = new ArrayList<>(allowedValues);
        
        ValidationResult result = validationPort.validateAllowedValues(fieldName, value, allowedObjects);
        
        assertNotNull(result, "Validation result should not be null");
        testContext.put("validationResult", result);
        testContext.put("fieldName", fieldName);
        testContext.put("validatedValue", value);
        testContext.put("allowedValues", allowedValues);
    }

    @When("I validate a map with the following values against the {string} rule set:")
    public void iValidateAMapWithTheFollowingValuesAgainstTheRuleSet(String ruleSetName, DataTable dataTable) {
        Map<String, String> rawValues = dataTable.asMap();
        Map<String, Object> values = new HashMap<>();
        
        // Convert string values to appropriate types
        for (Map.Entry<String, String> entry : rawValues.entrySet()) {
            String key = entry.getKey();
            String rawValue = entry.getValue();
            
            // Try to convert numeric values
            if (rawValue.matches("^-?\\d+$")) {
                values.put(key, Integer.parseInt(rawValue));
            } else {
                values.put(key, rawValue);
            }
        }
        
        ValidationResult result = validationPort.validateMap(ruleSetName, values);
        
        assertNotNull(result, "Validation result should not be null");
        testContext.put("validationResult", result);
        testContext.put("ruleSetName", ruleSetName);
        testContext.put("validatedMap", values);
    }

    @When("I validate the following component entity data:")
    public void iValidateTheFollowingComponentEntityData(DataTable dataTable) {
        Map<String, String> rawValues = dataTable.asMap();
        Map<String, Object> entityData = new HashMap<>();
        
        // Convert string values to appropriate types
        for (Map.Entry<String, String> entry : rawValues.entrySet()) {
            String key = entry.getKey();
            String rawValue = entry.getValue();
            
            // Try to convert numeric values
            if (rawValue.matches("^-?\\d+$")) {
                entityData.put(key, Integer.parseInt(rawValue));
            } else {
                entityData.put(key, rawValue);
            }
        }
        
        ValidationResult result = validationPort.validateEntity("component", entityData);
        
        assertNotNull(result, "Validation result should not be null");
        testContext.put("validationResult", result);
        testContext.put("entityType", "component");
        testContext.put("entityData", entityData);
    }

    @When("I register a custom validation rule {string} with pattern {string}")
    public void iRegisterACustomValidationRuleWithPattern(String ruleName, String pattern) {
        Pattern compiledPattern = Pattern.compile(pattern);
        validationPort.registerRule(ruleName, compiledPattern);
        
        testContext.put("customRuleName", ruleName);
        testContext.put("customPattern", pattern);
    }

    @Then("the validation should succeed")
    public void theValidationShouldSucceed() {
        ValidationResult result = (ValidationResult) testContext.get("validationResult");
        assertNotNull(result, "Validation result should be in the test context");
        
        assertTrue(result.isValid(), "Validation should succeed");
        assertTrue(result.getErrors().isEmpty(), "Validation errors should be empty");
    }

    @Then("the validation should fail with an appropriate error message")
    public void theValidationShouldFailWithAnAppropriateErrorMessage() {
        ValidationResult result = (ValidationResult) testContext.get("validationResult");
        assertNotNull(result, "Validation result should be in the test context");
        
        assertFalse(result.isValid(), "Validation should fail");
        assertFalse(result.getErrors().isEmpty(), "Validation errors should not be empty");
        
        // Get the first error message
        String errorMessage = result.getErrors().get(0);
        
        // Check that the error message is informative
        assertNotNull(errorMessage, "Error message should not be null");
        assertFalse(errorMessage.isEmpty(), "Error message should not be empty");
        
        // Print the error message for debugging
        System.out.println("Validation error message: " + errorMessage);
    }

    @Then("the validation should fail with appropriate error messages")
    public void theValidationShouldFailWithAppropriateErrorMessages() {
        ValidationResult result = (ValidationResult) testContext.get("validationResult");
        assertNotNull(result, "Validation result should be in the test context");
        
        assertFalse(result.isValid(), "Validation should fail");
        assertFalse(result.getErrors().isEmpty(), "Validation errors should not be empty");
        
        // Check that there are multiple error messages
        assertTrue(result.getErrors().size() > 0, "There should be at least one error message");
        
        // Print the error messages for debugging
        for (String error : result.getErrors()) {
            System.out.println("Validation error message: " + error);
        }
    }

    @Then("the error message should contain validation failure for field {string}")
    public void theErrorMessageShouldContainValidationFailureForField(String fieldName) {
        ValidationResult result = (ValidationResult) testContext.get("validationResult");
        assertNotNull(result, "Validation result should be in the test context");
        
        // Check that at least one error message contains the field name
        boolean fieldErrorFound = result.getErrors().stream()
                .anyMatch(error -> error.contains(fieldName));
        
        assertTrue(fieldErrorFound, "Error messages should contain validation failure for field: " + fieldName);
    }
}