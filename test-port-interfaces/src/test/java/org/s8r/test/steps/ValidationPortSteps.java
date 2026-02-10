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
 * Licensed under the Mozilla Public License 2.0
 */

package org.s8r.test.steps;

import io.cucumber.java.Before;
import io.cucumber.java.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.s8r.application.port.ValidationPort;
import org.s8r.test.context.ValidationPortTestContext;
import org.s8r.test.mock.MockLoggerAdapter;
import org.s8r.test.mock.MockValidationAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Step definitions for ValidationPort tests.
 */
public class ValidationPortSteps {

    private final ValidationPortTestContext context = new ValidationPortTestContext();
    private final MockLoggerAdapter logger = new MockLoggerAdapter();
    private final MockValidationAdapter mockValidationAdapter = new MockValidationAdapter(logger);
    
    @Before
    public void setup() {
        context.setValidationPort(mockValidationAdapter);
    }
    
    @Given("a clean system environment")
    public void a_clean_system_environment() {
        // Nothing to do, as the mock adapter is reset for each test
    }
    
    @Given("the ValidationPort interface is properly initialized")
    public void the_validation_port_interface_is_properly_initialized() {
        assertNotNull("ValidationPort should be initialized", context.getValidationPort());
    }
    
    @When("I validate the string {string} against the {string} rule")
    public void i_validate_the_string_against_the_rule(String value, String ruleName) {
        context.setCurrentRuleName(ruleName);
        context.setCurrentValue(value);
        
        ValidationPort.ValidationResult result = context.getValidationPort().validateString(ruleName, value);
        context.setLastResult(result);
    }
    
    @When("I validate the number {int} against the {string} rule")
    public void i_validate_the_number_against_the_rule(Integer value, String ruleName) {
        context.setCurrentRuleName(ruleName);
        context.setCurrentValue(value);
        
        ValidationPort.ValidationResult result = context.getValidationPort().validateNumber(ruleName, value);
        context.setLastResult(result);
    }
    
    @When("I validate a field {string} with value {string} as required")
    public void i_validate_a_field_with_value_as_required(String fieldName, String value) {
        context.setCurrentFieldName(fieldName);
        context.setCurrentValue(value);
        
        ValidationPort.ValidationResult result = context.getValidationPort().validateRequired(fieldName, value);
        context.setLastResult(result);
    }
    
    @When("I validate a field {string} with null value as required")
    public void i_validate_a_field_with_null_value_as_required(String fieldName) {
        context.setCurrentFieldName(fieldName);
        context.setCurrentValue(null);
        
        ValidationPort.ValidationResult result = context.getValidationPort().validateRequired(fieldName, null);
        context.setLastResult(result);
    }
    
    @When("I validate a field {string} with value {string} for length between {int} and {int}")
    public void i_validate_a_field_with_value_for_length_between_and(
            String fieldName, String value, Integer minLength, Integer maxLength) {
        context.setCurrentFieldName(fieldName);
        context.setCurrentValue(value);
        
        ValidationPort.ValidationResult result = 
                context.getValidationPort().validateLength(fieldName, value, minLength, maxLength);
        context.setLastResult(result);
    }
    
    @When("I validate a field {string} with value {int} for range between {int} and {int}")
    public void i_validate_a_field_with_value_for_range_between_and(
            String fieldName, Integer value, Integer min, Integer max) {
        context.setCurrentFieldName(fieldName);
        context.setCurrentValue(value);
        
        ValidationPort.ValidationResult result = 
                context.getValidationPort().validateRange(fieldName, value, min, max);
        context.setLastResult(result);
    }
    
    @When("I validate a field {string} with value {string} against pattern {string}")
    public void i_validate_a_field_with_value_against_pattern(
            String fieldName, String value, String pattern) {
        context.setCurrentFieldName(fieldName);
        context.setCurrentValue(value);
        
        ValidationPort.ValidationResult result = 
                context.getValidationPort().validatePattern(fieldName, value, pattern);
        context.setLastResult(result);
    }
    
    @When("I validate a field {string} with value {string} against allowed values:")
    public void i_validate_a_field_with_value_against_allowed_values(
            String fieldName, String value, List<String> allowedValues) {
        context.setCurrentFieldName(fieldName);
        context.setCurrentValue(value);
        context.clearAllowedValues();
        
        List<Object> values = new ArrayList<>(allowedValues);
        context.setCurrentAllowedValues(values);
        
        ValidationPort.ValidationResult result = 
                context.getValidationPort().validateAllowedValues(fieldName, value, values);
        context.setLastResult(result);
    }
    
    @Given("I have registered a validation rule set {string} with rules:")
    public void i_have_registered_a_validation_rule_set_with_rules(String ruleSetName, DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, Object> ruleSetDefinition = new HashMap<>();
        
        for (Map<String, String> row : rows) {
            String fieldName = row.get("fieldName");
            String ruleName = row.get("ruleName");
            ruleSetDefinition.put(fieldName, ruleName);
        }
        
        context.getValidationPort().registerRuleSet(ruleSetName, ruleSetDefinition);
        context.addRegisteredRuleSet(ruleSetName, ruleSetDefinition);
    }
    
    @When("I validate a map with the following values against the {string} rule set:")
    public void i_validate_a_map_with_the_following_values_against_the_rule_set(
            String ruleSetName, DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, Object> values = new HashMap<>();
        
        for (Map<String, String> row : rows) {
            String key = row.keySet().iterator().next();
            String value = row.get(key);
            
            // Try to convert numeric values
            try {
                int intValue = Integer.parseInt(value);
                values.put(key, intValue);
            } catch (NumberFormatException e) {
                values.put(key, value);
            }
        }
        
        context.setCurrentMap(values);
        ValidationPort.ValidationResult result = 
                context.getValidationPort().validateMap(ruleSetName, values);
        context.setLastResult(result);
    }
    
    @When("I validate the following component entity data:")
    public void i_validate_the_following_component_entity_data(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, Object> entityData = new HashMap<>();
        
        for (Map<String, String> row : rows) {
            String key = row.keySet().iterator().next();
            String value = row.get(key);
            entityData.put(key, value);
        }
        
        context.setLastEntityType("component");
        context.setLastEntityData(entityData);
        
        ValidationPort.ValidationResult result = 
                context.getValidationPort().validateEntity("component", entityData);
        context.setLastResult(result);
    }
    
    @When("I register a custom validation rule {string} with pattern {string}")
    public void i_register_a_custom_validation_rule_with_pattern(String ruleName, String pattern) {
        context.getValidationPort().registerRule(ruleName, pattern);
        context.addRegisteredRule(ruleName, pattern);
    }
    
    @Then("the validation should succeed")
    public void the_validation_should_succeed() {
        ValidationPort.ValidationResult result = context.getLastResult();
        
        assertNotNull("Validation result should not be null", result);
        assertTrue("Validation should succeed", result.isValid());
        assertTrue("Validation errors should be empty", result.getErrors().isEmpty());
    }
    
    @Then("the validation should fail with an appropriate error message")
    public void the_validation_should_fail_with_an_appropriate_error_message() {
        ValidationPort.ValidationResult result = context.getLastResult();
        
        assertNotNull("Validation result should not be null", result);
        assertFalse("Validation should fail", result.isValid());
        assertFalse("Validation errors should not be empty", result.getErrors().isEmpty());
    }
    
    @Then("the validation should fail with appropriate error messages")
    public void the_validation_should_fail_with_appropriate_error_messages() {
        ValidationPort.ValidationResult result = context.getLastResult();
        
        assertNotNull("Validation result should not be null", result);
        assertFalse("Validation should fail", result.isValid());
        assertFalse("Validation errors should not be empty", result.getErrors().isEmpty());
    }
    
    @Then("the error message should contain validation failure for field {string}")
    public void the_error_message_should_contain_validation_failure_for_field(String fieldName) {
        ValidationPort.ValidationResult result = context.getLastResult();
        
        assertNotNull("Validation result should not be null", result);
        assertFalse("Validation should fail", result.isValid());
        
        boolean containsField = false;
        for (String error : result.getErrors()) {
            if (error.toLowerCase().contains(fieldName.toLowerCase())) {
                containsField = true;
                break;
            }
        }
        
        assertTrue("Error message should contain failure for field: " + fieldName, containsField);
    }
}