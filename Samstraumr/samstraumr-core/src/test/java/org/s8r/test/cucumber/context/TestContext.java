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

package org.s8r.test.cucumber.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.s8r.core.composite.Composite;
import org.s8r.core.env.Environment;
import org.s8r.core.machine.Machine;
import org.s8r.core.tube.impl.Component;

/**
 * Context for BDD tests, providing state sharing between step definitions.
 * 
 * <p>This class is used to maintain state during Cucumber scenario execution,
 * allowing different step definition classes to share data. It stores components,
 * composites, machines, and other test-related objects.
 * 
 * <p>The design follows the Context Object pattern, which is commonly used in
 * BDD testing to manage shared state between steps in a scenario.
 */
public class TestContext {
    private final Environment environment;
    private final Map<String, Component> components = new HashMap<>();
    private final Map<String, Composite> composites = new HashMap<>();
    private final Map<String, Machine> machines = new HashMap<>();
    private final Map<String, Object> testData = new HashMap<>();
    
    /**
     * Creates a new test context with a default environment.
     */
    public TestContext() {
        this.environment = Environment.create();
    }
    
    /**
     * Gets the environment for tests.
     * 
     * @return The environment
     */
    public Environment getEnvironment() {
        return environment;
    }
    
    /**
     * Adds a component to the context.
     * 
     * @param id The ID to use for referencing the component
     * @param component The component to add
     */
    public void addComponent(String id, Component component) {
        components.put(id, component);
    }
    
    /**
     * Gets a component from the context.
     * 
     * @param id The ID of the component
     * @return An Optional containing the component if found, empty otherwise
     */
    public Optional<Component> getComponent(String id) {
        return Optional.ofNullable(components.get(id));
    }
    
    /**
     * Adds a composite to the context.
     * 
     * @param id The ID to use for referencing the composite
     * @param composite The composite to add
     */
    public void addComposite(String id, Composite composite) {
        composites.put(id, composite);
    }
    
    /**
     * Gets a composite from the context.
     * 
     * @param id The ID of the composite
     * @return An Optional containing the composite if found, empty otherwise
     */
    public Optional<Composite> getComposite(String id) {
        return Optional.ofNullable(composites.get(id));
    }
    
    /**
     * Adds a machine to the context.
     * 
     * @param id The ID to use for referencing the machine
     * @param machine The machine to add
     */
    public void addMachine(String id, Machine machine) {
        machines.put(id, machine);
    }
    
    /**
     * Gets a machine from the context.
     * 
     * @param id The ID of the machine
     * @return An Optional containing the machine if found, empty otherwise
     */
    public Optional<Machine> getMachine(String id) {
        return Optional.ofNullable(machines.get(id));
    }
    
    /**
     * Sets test data.
     * 
     * @param key The key for the data
     * @param value The value to store
     */
    public void setData(String key, Object value) {
        testData.put(key, value);
    }
    
    /**
     * Gets test data.
     * 
     * @param <T> The expected type of the data
     * @param key The key for the data
     * @param type The class representing the expected type
     * @return An Optional containing the data if found and of the expected type, empty otherwise
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getData(String key, Class<T> type) {
        Object value = testData.get(key);
        if (value != null && type.isInstance(value)) {
            return Optional.of((T) value);
        }
        return Optional.empty();
    }
    
    /**
     * Cleans up resources after test execution.
     */
    public void cleanup() {
        // Terminate any active machines to release resources
        machines.values().forEach(Machine::destroy);
        
        // Clear collections
        components.clear();
        composites.clear();
        machines.clear();
        testData.clear();
    }
}