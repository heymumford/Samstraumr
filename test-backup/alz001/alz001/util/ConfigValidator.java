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

package org.s8r.test.steps.alz001.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class to validate the configuration of ALZ001 tests.
 * Ensures that configuration files exist and have required properties.
 */
public class ConfigValidator {

    private static final String CUCUMBER_PROPERTIES_PATH = 
        "/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/resources/cucumber.properties";
    
    private static final String ALZ001_PROPERTIES_PATH = 
        "/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/resources/cucumber-alz001.properties";
    
    /**
     * Validates the configuration files and prints results to console.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("ALZ001 Configuration Validator");
        System.out.println("------------------------------");
        
        // Validate the files exist
        validateFileExists(CUCUMBER_PROPERTIES_PATH, "Main Cucumber properties");
        validateFileExists(ALZ001_PROPERTIES_PATH, "ALZ001 Cucumber properties");
        
        // Validate properties content
        validateCucumberProperties();
        validateALZ001Properties();
        
        System.out.println("\nConfiguration validation complete!");
    }
    
    /**
     * Validates that a file exists at the specified path.
     * 
     * @param path The absolute path to the file
     * @param description A description of the file
     */
    private static void validateFileExists(String path, String description) {
        File file = new File(path);
        System.out.println("\nChecking " + description + ":");
        System.out.println("  Path: " + path);
        
        if (file.exists()) {
            System.out.println("  ✅ File exists");
        } else {
            System.out.println("  ❌ File does not exist");
        }
    }
    
    /**
     * Validates the content of the main Cucumber properties file.
     */
    private static void validateCucumberProperties() {
        System.out.println("\nValidating main Cucumber properties:");
        Properties props = loadProperties(CUCUMBER_PROPERTIES_PATH);
        
        if (props == null) {
            return;
        }
        
        // Check for required properties
        checkProperty(props, "cucumber.execution.parallel.enabled");
        checkProperty(props, "cucumber.execution.order");
        checkProperty(props, "cucumber.plugin");
        checkProperty(props, "cucumber.publish.quiet");
        checkProperty(props, "cucumber.filter.tags");
        checkProperty(props, "cucumber.glue");
    }
    
    /**
     * Validates the content of the ALZ001-specific Cucumber properties file.
     */
    private static void validateALZ001Properties() {
        System.out.println("\nValidating ALZ001 Cucumber properties:");
        Properties props = loadProperties(ALZ001_PROPERTIES_PATH);
        
        if (props == null) {
            return;
        }
        
        // Check for required properties
        checkProperty(props, "cucumber.execution.parallel.enabled");
        checkProperty(props, "cucumber.execution.order");
        checkProperty(props, "cucumber.plugin");
        checkProperty(props, "cucumber.filter.tags");
        checkProperty(props, "cucumber.glue");
        checkProperty(props, "cucumber.features");
        checkProperty(props, "cucumber.execution.strict");
        checkProperty(props, "cucumber.snippet-type");
    }
    
    /**
     * Loads properties from a properties file.
     * 
     * @param path The path to the properties file
     * @return The Properties object, or null if loading failed
     */
    private static Properties loadProperties(String path) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(path)) {
            properties.load(fis);
            return properties;
        } catch (IOException e) {
            System.out.println("  ❌ Failed to load properties: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Checks if a property exists in the properties object.
     * 
     * @param props The Properties object
     * @param propertyName The name of the property to check
     */
    private static void checkProperty(Properties props, String propertyName) {
        if (props.containsKey(propertyName)) {
            System.out.println("  ✅ " + propertyName + " = " + props.getProperty(propertyName));
        } else {
            System.out.println("  ❌ Missing required property: " + propertyName);
        }
    }
}