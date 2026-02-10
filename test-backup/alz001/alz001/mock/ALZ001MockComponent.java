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
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.mozilla.org/en-US/MPL/2.0/
 */

package org.s8r.test.steps.alz001.mock;

import java.util.HashMap;
import java.util.Map;
import java.time.Instant;

/**
 * Base class for all mock components in the ALZ001 test suite.
 * 
 * <p>Provides common state management, validation, and metadata functionality
 * for all mock components. This class is designed to be extended by specific
 * component implementations for each capability.
 */
public abstract class ALZ001MockComponent {
    
    /**
     * The unique name of the component.
     */
    protected final String name;
    
    /**
     * The current state of the component.
     */
    protected String state;
    
    /**
     * The creation timestamp of the component.
     */
    protected final Instant createdAt;
    
    /**
     * Metadata for the component (key-value pairs).
     */
    protected final Map<String, String> metadata;
    
    /**
     * Configuration for the component (key-value pairs).
     */
    protected final Map<String, Object> configuration;
    
    /**
     * Constructs a new mock component with the specified name.
     * Initial state is set to "INITIALIZED".
     *
     * @param name The unique name of the component
     */
    public ALZ001MockComponent(String name) {
        this.name = name;
        this.state = "INITIALIZED";
        this.createdAt = Instant.now();
        this.metadata = new HashMap<>();
        this.configuration = new HashMap<>();
        
        // Set default metadata
        metadata.put("created_by", "ALZ001TestSuite");
        metadata.put("component_type", getClass().getSimpleName());
    }
    
    /**
     * Gets the name of the component.
     *
     * @return The component name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the current state of the component.
     *
     * @return The current state
     */
    public String getState() {
        return state;
    }
    
    /**
     * Sets the current state of the component.
     *
     * @param state The new state
     */
    public void setState(String state) {
        this.state = state;
    }
    
    /**
     * Validates that the component is in the expected state.
     *
     * @param expectedState The expected state
     * @return true if the current state matches the expected state, false otherwise
     */
    public boolean validateState(String expectedState) {
        return state.equals(expectedState);
    }
    
    /**
     * Gets the creation timestamp of the component.
     *
     * @return The creation timestamp
     */
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Gets a metadata value by key.
     *
     * @param key The metadata key
     * @return The metadata value, or null if not found
     */
    public String getMetadata(String key) {
        return metadata.get(key);
    }
    
    /**
     * Sets a metadata value.
     *
     * @param key The metadata key
     * @param value The metadata value
     */
    public void setMetadata(String key, String value) {
        metadata.put(key, value);
    }
    
    /**
     * Gets a configuration value by key.
     *
     * @param <T> The type of the configuration value
     * @param key The configuration key
     * @return The configuration value, cast to the expected type
     */
    @SuppressWarnings("unchecked")
    public <T> T getConfig(String key) {
        return (T) configuration.get(key);
    }
    
    /**
     * Sets a configuration value.
     *
     * @param key The configuration key
     * @param value The configuration value
     */
    public void setConfig(String key, Object value) {
        configuration.put(key, value);
    }
    
    /**
     * Configures the component with the specified configuration.
     *
     * @param config The configuration to apply
     */
    public void configure(Map<String, Object> config) {
        configuration.putAll(config);
        setState("CONFIGURED");
    }
    
    /**
     * Validates the component's configuration.
     * Subclasses should override this method to implement component-specific validation.
     *
     * @return A map of validation errors (empty if valid)
     */
    public Map<String, String> validateConfiguration() {
        return new HashMap<>();
    }
    
    /**
     * Initializes the component.
     * This method should be overridden by subclasses to implement component-specific initialization.
     */
    public void initialize() {
        if (validateConfiguration().isEmpty()) {
            setState("READY");
        } else {
            setState("ERROR");
        }
    }
    
    /**
     * Destroys the component, releasing any resources.
     * This method should be overridden by subclasses to implement component-specific cleanup.
     */
    public void destroy() {
        setState("DESTROYED");
    }
    
    /**
     * Gets a string representation of the component.
     *
     * @return A string representation
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[name=" + name + ", state=" + state + "]";
    }
}