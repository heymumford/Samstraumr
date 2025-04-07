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

package org.s8r.test.cucumber.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.s8r.core.env.Environment;
import org.s8r.core.exception.InitializationException;
import org.s8r.core.tube.LifecycleState;
import org.s8r.core.tube.Status;
import org.s8r.core.tube.impl.Component;
import org.s8r.test.cucumber.context.TestContext;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for testing S8r Components in Cucumber BDD scenarios.
 * 
 * <p>This class provides step definitions for creating, manipulating, and verifying
 * the behavior of S8r Components (the new implementation in org.s8r.core.tube.impl).
 * It uses a TestContext to maintain state between steps.
 */
public class ComponentSteps {
    private final TestContext context;
    
    /**
     * Constructs ComponentSteps with a test context.
     * 
     * @param context The test context to use
     */
    public ComponentSteps(TestContext context) {
        this.context = context;
    }
    
    /**
     * Set up before each scenario.
     */
    @Before
    public void setUp() {
        // Any setup needed before each scenario
    }
    
    /**
     * Clean up after each scenario.
     */
    @After
    public void tearDown() {
        context.cleanup();
    }
    
    /**
     * Creates a new component with the given name.
     * 
     * @param componentId The ID to reference the component by
     * @throws InitializationException if component creation fails
     */
    @Given("a component with ID {string}")
    public void aComponentWithId(String componentId) throws InitializationException {
        Environment env = context.getEnvironment();
        Component component = Component.create(componentId, env);
        context.addComponent(componentId, component);
    }
    
    /**
     * Verifies that a component exists with the specified ID.
     * 
     * @param componentId The component ID to check
     */
    @Then("component {string} should exist")
    public void componentShouldExist(String componentId) {
        assertTrue(
            context.getComponent(componentId).isPresent(),
            "Component with ID " + componentId + " should exist"
        );
    }
    
    /**
     * Verifies a component's state.
     * 
     * @param componentId The component ID
     * @param expectedState The expected lifecycle state
     */
    @Then("component {string} should be in {string} state")
    public void componentShouldBeInState(String componentId, String expectedState) {
        Component component = context.getComponent(componentId)
            .orElseThrow(() -> new IllegalArgumentException("Component not found: " + componentId));
        
        LifecycleState expected = LifecycleState.valueOf(expectedState.toUpperCase());
        assertEquals(expected, component.getLifecycleState(),
            "Component should be in " + expectedState + " state");
    }
    
    /**
     * Verifies a component's status.
     * 
     * @param componentId The component ID
     * @param expectedStatus The expected status
     */
    @Then("component {string} should have status {string}")
    public void componentShouldHaveStatus(String componentId, String expectedStatus) {
        Component component = context.getComponent(componentId)
            .orElseThrow(() -> new IllegalArgumentException("Component not found: " + componentId));
        
        Status expected = Status.valueOf(expectedStatus.toUpperCase());
        assertEquals(expected, component.getStatus(),
            "Component should have status " + expectedStatus);
    }
    
    /**
     * Sets a component's state to READY.
     * 
     * @param componentId The component ID
     */
    @When("component {string} is set to ready")
    public void componentIsSetToReady(String componentId) {
        Component component = context.getComponent(componentId)
            .orElseThrow(() -> new IllegalArgumentException("Component not found: " + componentId));
        
        component.setLifecycleState(LifecycleState.READY);
        component.setStatus(Status.READY);
    }
    
    /**
     * Sets a component's state to ACTIVE.
     * 
     * @param componentId The component ID
     */
    @When("component {string} is activated")
    public void componentIsActivated(String componentId) {
        Component component = context.getComponent(componentId)
            .orElseThrow(() -> new IllegalArgumentException("Component not found: " + componentId));
        
        component.setLifecycleState(LifecycleState.ACTIVE);
        component.setStatus(Status.OPERATIONAL);
    }
    
    /**
     * Terminates a component.
     * 
     * @param componentId The component ID
     */
    @When("component {string} is terminated")
    public void componentIsTerminated(String componentId) {
        Component component = context.getComponent(componentId)
            .orElseThrow(() -> new IllegalArgumentException("Component not found: " + componentId));
        
        component.terminate();
    }
    
    /**
     * Creates a parent-child relationship between components.
     * 
     * @param childId The child component ID
     * @param parentId The parent component ID
     * @throws InitializationException if creation fails
     */
    @Given("component {string} is a child of component {string}")
    public void componentIsChildOfComponent(String childId, String parentId) throws InitializationException {
        Component parent = context.getComponent(parentId)
            .orElseThrow(() -> new IllegalArgumentException("Parent component not found: " + parentId));
        
        Environment env = context.getEnvironment();
        Component child = Component.createChild(childId, env, parent);
        context.addComponent(childId, child);
    }
    
    /**
     * Verifies the unique ID of a component.
     * 
     * @param componentId The component reference ID
     */
    @Then("component {string} should have a unique ID")
    public void componentShouldHaveUniqueId(String componentId) {
        Component component = context.getComponent(componentId)
            .orElseThrow(() -> new IllegalArgumentException("Component not found: " + componentId));
        
        String uniqueId = component.getUniqueId();
        assertNotNull(uniqueId, "Component should have a unique ID");
        assertTrue(uniqueId.length() > 0, "Component unique ID should not be empty");
    }
}