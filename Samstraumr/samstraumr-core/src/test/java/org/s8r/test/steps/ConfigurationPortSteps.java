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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.s8r.application.port.ConfigurationPort;
import org.s8r.application.port.LoggerPort;
import org.s8r.infrastructure.config.Configuration;
import org.s8r.infrastructure.config.ConfigurationAdapter;
import org.s8r.infrastructure.logging.ConsoleLogger;
import org.s8r.test.annotation.IntegrationTest;

/**
 * Step definitions for testing the ConfigurationPort interface in BDD scenarios.
 */
@IntegrationTest
public class ConfigurationPortSteps {

    private LoggerPort logger;
    private Configuration configuration;
    private ConfigurationPort configurationPort;
    private Map<String, Object> testContext = new HashMap<>();
    
    @Before
    public void setup() {
        logger = new ConsoleLogger();
        configuration = new TestConfiguration();
        configurationPort = new ConfigurationAdapter(configuration, logger);
    }
    
    @Given("a clean system environment")
    public void givenCleanSystemEnvironment() {
        testContext.clear();
        ((TestConfiguration) configuration).clear();
    }
    
    @Given("the ConfigurationPort interface is properly initialized")
    public void givenConfigurationPortInitialized() {
        // Initialization is handled in the setup method
        assertNotNull(configurationPort, "ConfigurationPort should be initialized");
    }
    
    @Given("basic configuration values are set for testing")
    public void givenBasicConfigurationValues() {
        // Set string values
        configuration.set("string.key", "test string value");
        configuration.set("empty.string.key", "");
        
        // Set integer values
        configuration.set("integer.key", "42");
        configuration.set("invalid.integer.key", "not an integer");
        
        // Set boolean values with different representations
        configuration.set("boolean.true.key", "true");
        configuration.set("boolean.yes.key", "yes");
        configuration.set("boolean.1.key", "1");
        configuration.set("boolean.false.key", "false");
        configuration.set("boolean.no.key", "no");
        configuration.set("boolean.0.key", "0");
        
        // Set list values
        configuration.set("list.key", "item1,item2,item3");
        configuration.set("empty.list.key", "");
        
        // Set subset values
        configuration.set("subset.prefix.key1", "subset value 1");
        configuration.set("subset.prefix.key2", "subset value 2");
        configuration.set("subset.prefix.key3", "subset value 3");
    }
    
    @When("I get a string configuration value for key {string}")
    public void whenGetStringValue(String key) {
        Optional<String> value = configurationPort.getString(key);
        testContext.put("stringResult", value);
    }
    
    @When("I get a string configuration value for key {string} with default {string}")
    public void whenGetStringValueWithDefault(String key, String defaultValue) {
        String value = configurationPort.getString(key, defaultValue);
        testContext.put("stringDefaultResult", value);
    }
    
    @When("I get an integer configuration value for key {string}")
    public void whenGetIntValue(String key) {
        Optional<Integer> value = configurationPort.getInt(key);
        testContext.put("intResult", value);
    }
    
    @When("I get an integer configuration value for key {string} with default {int}")
    public void whenGetIntValueWithDefault(String key, int defaultValue) {
        int value = configurationPort.getInt(key, defaultValue);
        testContext.put("intDefaultResult", value);
    }
    
    @When("I get a boolean configuration value for key {string}")
    public void whenGetBooleanValue(String key) {
        Optional<Boolean> value = configurationPort.getBoolean(key);
        testContext.put("booleanResult", value);
    }
    
    @When("I get a boolean configuration value for key {string} with default {}")
    public void whenGetBooleanValueWithDefault(String key, boolean defaultValue) {
        boolean value = configurationPort.getBoolean(key, defaultValue);
        testContext.put("booleanDefaultResult", value);
    }
    
    @When("I get a string list configuration value for key {string}")
    public void whenGetStringListValue(String key) {
        Optional<List<String>> value = configurationPort.getStringList(key);
        testContext.put("listResult", value);
    }
    
    @When("I get a string list configuration value for key {string} with default list:")
    public void whenGetStringListValueWithDefault(String key, DataTable dataTable) {
        List<String> defaultValues = dataTable.asList();
        List<String> value = configurationPort.getStringList(key, defaultValues);
        testContext.put("listDefaultResult", value);
    }
    
    @When("I get a configuration subset with prefix {string}")
    public void whenGetConfigSubset(String prefix) {
        Map<String, String> value = configurationPort.getSubset(prefix);
        testContext.put("subsetResult", value);
    }
    
    @When("I check if key {string} exists")
    public void whenCheckKeyExists(String key) {
        boolean value = configurationPort.hasKey(key);
        testContext.put("keyExistsResult", value);
    }
    
    @When("I get a string configuration value for null key")
    public void whenGetStringValueForNullKey() {
        Optional<String> value = configurationPort.getString(null);
        testContext.put("nullKeyStringResult", value);
    }
    
    @When("I get an integer configuration value for null key")
    public void whenGetIntValueForNullKey() {
        Optional<Integer> value = configurationPort.getInt(null);
        testContext.put("nullKeyIntResult", value);
    }
    
    @When("I get a boolean configuration value for null key")
    public void whenGetBooleanValueForNullKey() {
        Optional<Boolean> value = configurationPort.getBoolean(null);
        testContext.put("nullKeyBooleanResult", value);
    }
    
    @When("I get a string list configuration value for null key")
    public void whenGetStringListValueForNullKey() {
        Optional<List<String>> value = configurationPort.getStringList(null);
        testContext.put("nullKeyListResult", value);
    }
    
    @Then("the result should be the string value {string}")
    public void thenResultShouldBeStringValue(String expected) {
        @SuppressWarnings("unchecked")
        Optional<String> result = (Optional<String>) testContext.get("stringResult");
        
        assertTrue(result.isPresent(), "Result should be present");
        assertEquals(expected, result.get(), "String value should match expected");
    }
    
    @Then("the result should be an empty optional")
    public void thenResultShouldBeEmptyOptional() {
        // Check which result type is being verified
        if (testContext.containsKey("stringResult")) {
            @SuppressWarnings("unchecked")
            Optional<String> result = (Optional<String>) testContext.get("stringResult");
            assertFalse(result.isPresent(), "String result should be empty");
        } else if (testContext.containsKey("intResult")) {
            @SuppressWarnings("unchecked")
            Optional<Integer> result = (Optional<Integer>) testContext.get("intResult");
            assertFalse(result.isPresent(), "Integer result should be empty");
        } else if (testContext.containsKey("booleanResult")) {
            @SuppressWarnings("unchecked")
            Optional<Boolean> result = (Optional<Boolean>) testContext.get("booleanResult");
            assertFalse(result.isPresent(), "Boolean result should be empty");
        } else if (testContext.containsKey("listResult")) {
            @SuppressWarnings("unchecked")
            Optional<List<String>> result = (Optional<List<String>>) testContext.get("listResult");
            assertFalse(result.isPresent(), "List result should be empty");
        }
    }
    
    @Then("the result should be the default string value {string}")
    public void thenResultShouldBeDefaultStringValue(String expected) {
        String result = (String) testContext.get("stringDefaultResult");
        assertEquals(expected, result, "Default string value should match expected");
    }
    
    @Then("the result should be the integer value {int}")
    public void thenResultShouldBeIntValue(int expected) {
        @SuppressWarnings("unchecked")
        Optional<Integer> result = (Optional<Integer>) testContext.get("intResult");
        
        assertTrue(result.isPresent(), "Result should be present");
        assertEquals(expected, result.get(), "Integer value should match expected");
    }
    
    @Then("the result should be the default integer value {int}")
    public void thenResultShouldBeDefaultIntValue(int expected) {
        int result = (int) testContext.get("intDefaultResult");
        assertEquals(expected, result, "Default integer value should match expected");
    }
    
    @Then("the result should be the boolean value {}")
    public void thenResultShouldBeBooleanValue(boolean expected) {
        @SuppressWarnings("unchecked")
        Optional<Boolean> result = (Optional<Boolean>) testContext.get("booleanResult");
        
        assertTrue(result.isPresent(), "Result should be present");
        assertEquals(expected, result.get(), "Boolean value should match expected");
    }
    
    @Then("the result should be the default boolean value {}")
    public void thenResultShouldBeDefaultBooleanValue(boolean expected) {
        boolean result = (boolean) testContext.get("booleanDefaultResult");
        assertEquals(expected, result, "Default boolean value should match expected");
    }
    
    @Then("the result should be a list with values:")
    public void thenResultShouldBeListValues(DataTable dataTable) {
        List<String> expectedValues = dataTable.asList();
        
        @SuppressWarnings("unchecked")
        Optional<List<String>> result = (Optional<List<String>>) testContext.get("listResult");
        
        assertTrue(result.isPresent(), "Result should be present");
        assertEquals(expectedValues.size(), result.get().size(), "List size should match");
        
        for (int i = 0; i < expectedValues.size(); i++) {
            assertEquals(expectedValues.get(i), result.get().get(i), 
                    "List item at index " + i + " should match");
        }
    }
    
    @Then("the result should be the default list")
    public void thenResultShouldBeDefaultList() {
        @SuppressWarnings("unchecked")
        List<String> result = (List<String>) testContext.get("listDefaultResult");
        assertNotNull(result, "Default list result should not be null");
        assertEquals(2, result.size(), "Default list size should be 2");
        assertEquals("default1", result.get(0), "First default list item should match");
        assertEquals("default2", result.get(1), "Second default list item should match");
    }
    
    @Then("the result should be a map with entries:")
    public void thenResultShouldBeMapEntries(DataTable dataTable) {
        List<Map<String, String>> expectedEntries = dataTable.asMaps();
        Map<String, String> subsetResult = (Map<String, String>) testContext.get("subsetResult");
        
        assertNotNull(subsetResult, "Subset result should not be null");
        assertEquals(expectedEntries.size(), subsetResult.size(), "Map size should match");
        
        for (Map<String, String> entry : expectedEntries) {
            String key = entry.get("key");
            String value = entry.get("value");
            
            assertTrue(subsetResult.containsKey(key), "Map should contain key: " + key);
            assertEquals(value, subsetResult.get(key), "Value for key " + key + " should match");
        }
    }
    
    @Then("the result should be an empty map")
    public void thenResultShouldBeEmptyMap() {
        Map<String, String> subsetResult = (Map<String, String>) testContext.get("subsetResult");
        assertTrue(subsetResult.isEmpty(), "Map should be empty");
    }
    
    @Then("the key existence check should return {}")
    public void thenKeyExistenceCheckShouldReturn(boolean expected) {
        boolean result = (boolean) testContext.get("keyExistsResult");
        assertEquals(expected, result, "Key existence check should match expected");
    }
    
    @Then("the result should be an empty string")
    public void thenResultShouldBeEmptyString() {
        @SuppressWarnings("unchecked")
        Optional<String> result = (Optional<String>) testContext.get("stringResult");
        
        assertTrue(result.isPresent(), "Result should be present");
        assertEquals("", result.get(), "String value should be empty");
    }
    
    @Then("the result should be an empty list")
    public void thenResultShouldBeEmptyList() {
        @SuppressWarnings("unchecked")
        Optional<List<String>> result = (Optional<List<String>>) testContext.get("listResult");
        
        assertTrue(result.isPresent(), "Result should be present");
        assertTrue(result.get().isEmpty(), "List should be empty");
    }
    
    @Then("the result should be an empty optional due to invalid format")
    public void thenResultShouldBeEmptyOptionalDueToInvalidFormat() {
        @SuppressWarnings("unchecked")
        Optional<Integer> result = (Optional<Integer>) testContext.get("intResult");
        assertFalse(result.isPresent(), "Integer result should be empty due to invalid format");
    }
    
    /**
     * Test implementation of Configuration for BDD testing.
     */
    private static class TestConfiguration extends Configuration {
        private Map<String, String> configValues = new HashMap<>();
        
        @Override
        public void set(String key, String value) {
            if (key != null) {
                configValues.put(key, value);
            }
        }
        
        @Override
        public String get(String key) {
            return key != null ? configValues.get(key) : null;
        }
        
        @Override
        public String get(String key, String defaultValue) {
            return key != null && configValues.containsKey(key) ? configValues.get(key) : defaultValue;
        }
        
        @Override
        public int getInt(String key, int defaultValue) {
            if (key == null || !configValues.containsKey(key)) {
                return defaultValue;
            }
            
            try {
                return Integer.parseInt(configValues.get(key));
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        
        @Override
        public boolean getBoolean(String key, boolean defaultValue) {
            if (key == null || !configValues.containsKey(key)) {
                return defaultValue;
            }
            
            String value = configValues.get(key);
            return "true".equalsIgnoreCase(value) || 
                   "yes".equalsIgnoreCase(value) || 
                   "1".equals(value);
        }
        
        @Override
        public boolean hasKey(String key) {
            return key != null && configValues.containsKey(key);
        }
        
        @Override
        public Map<String, String> getAll() {
            return new HashMap<>(configValues);
        }
        
        /**
         * Clears all configuration values.
         */
        public void clear() {
            configValues.clear();
        }
    }
}