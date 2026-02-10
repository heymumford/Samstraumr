/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025 Eric C. Mumford
 *
 * Licensed under the Mozilla Public License 2.0
 */
package org.s8r.test.steps.composite;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Base step definitions for composite component tests.
 * Contains common functionality used across different composite test scenarios.
 */
public class CompositeBaseSteps {
    
    protected static final Logger LOGGER = Logger.getLogger(CompositeBaseSteps.class.getName());
    
    // Shared test context for all composite tests
    protected static final Map<String, Object> testContext = new HashMap<>();
    
    /**
     * Initialize test environment before each scenario.
     */
    @Before
    public void setUp() {
        LOGGER.info("Setting up test environment for composite component tests");
        testContext.clear();
    }
    
    /**
     * Clean up after each scenario.
     */
    @After
    public void tearDown() {
        LOGGER.info("Cleaning up test environment after composite component tests");
        // Release any resources created during the test
    }
    
    /**
     * Initialize the Samstraumr system for testing.
     */
    @Given("the Samstraumr system is initialized")
    public void theSamstraumrSystemIsInitialized() {
        LOGGER.info("Initializing Samstraumr system for composite component tests");
        // Initialize the system - this will be implemented when core classes are available
        testContext.put("systemInitialized", true);
    }
    
    /**
     * Store an object in the test context for later use.
     * 
     * @param key The key to use for storing the object
     * @param value The object to store
     */
    protected void storeInContext(String key, Object value) {
        testContext.put(key, value);
    }
    
    /**
     * Retrieve an object from the test context.
     * 
     * @param key The key to use for retrieving the object
     * @return The stored object, or null if not found
     */
    @SuppressWarnings("unchecked")
    protected <T> T getFromContext(String key) {
        return (T) testContext.get(key);
    }
    
    /**
     * Check if a key exists in the test context.
     * 
     * @param key The key to check
     * @return true if the key exists, false otherwise
     */
    protected boolean contextContains(String key) {
        return testContext.containsKey(key);
    }
}