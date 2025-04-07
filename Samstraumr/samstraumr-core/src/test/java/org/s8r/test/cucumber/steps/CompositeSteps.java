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

import org.s8r.core.composite.Composite;
import org.s8r.core.composite.CompositeFactory;
import org.s8r.core.composite.CompositeType;
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
 * Step definitions for testing S8r Composites in Cucumber BDD scenarios.
 * 
 * <p>This class provides step definitions for creating, manipulating, and verifying
 * the behavior of S8r Composites (the new implementation in org.s8r.core.composite).
 * It uses a TestContext to maintain state between steps.
 */
public class CompositeSteps {
    private final TestContext context;
    
    /**
     * Constructs CompositeSteps with a test context.
     * 
     * @param context The test context to use
     */
    public CompositeSteps(TestContext context) {
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
        // Cleanup happens in TestContext
    }
    
    /**
     * Creates a new composite with the given name and type.
     * 
     * @param compositeId The ID to reference the composite by
     * @param typeStr The type of composite
     */
    @Given("a composite with ID {string} of type {string}")
    public void aCompositeWithIdOfType(String compositeId, String typeStr) {
        Environment env = context.getEnvironment();
        CompositeType type = CompositeType.valueOf(typeStr.toUpperCase());
        Composite composite = CompositeFactory.createComposite(compositeId, type, env);
        context.addComposite(compositeId, composite);
    }
    
    /**
     * Creates a standard composite with the given name.
     * 
     * @param compositeId The ID to reference the composite by
     */
    @Given("a composite with ID {string}")
    public void aCompositeWithId(String compositeId) {
        Environment env = context.getEnvironment();
        Composite composite = CompositeFactory.createComposite(compositeId, CompositeType.OBSERVER, env);
        context.addComposite(compositeId, composite);
    }
    
    /**
     * Adds a component to a composite.
     * 
     * @param componentId The ID of the component
     * @param compositeId The ID of the composite
     * @throws InitializationException if component creation fails
     */
    @Given("component {string} added to composite {string}")
    public void componentAddedToComposite(String componentId, String compositeId) throws InitializationException {
        // Get or create the composite
        Composite composite = context.getComposite(compositeId)
            .orElseThrow(() -> new IllegalArgumentException("Composite not found: " + compositeId));
        
        // Get or create the component
        Component component;
        if (context.getComponent(componentId).isPresent()) {
            component = context.getComponent(componentId).get();
        } else {
            Environment env = context.getEnvironment();
            component = Component.create(componentId, env);
            context.addComponent(componentId, component);
        }
        
        // Add the component to the composite
        composite.addComponent(component);
    }
    
    /**
     * Connects two components within a composite.
     * 
     * @param sourceId The ID of the source component
     * @param targetId The ID of the target component
     * @param compositeId The ID of the composite
     */
    @Given("components {string} and {string} are connected in composite {string}")
    public void componentsAreConnectedInComposite(String sourceId, String targetId, String compositeId) {
        Composite composite = context.getComposite(compositeId)
            .orElseThrow(() -> new IllegalArgumentException("Composite not found: " + compositeId));
        
        composite.connectComponents(sourceId, targetId, "standard");
    }
    
    /**
     * Verifies that a composite exists with the specified ID.
     * 
     * @param compositeId The composite ID to check
     */
    @Then("composite {string} should exist")
    public void compositeShouldExist(String compositeId) {
        assertTrue(
            context.getComposite(compositeId).isPresent(),
            "Composite with ID " + compositeId + " should exist"
        );
    }
    
    /**
     * Activates a composite.
     * 
     * @param compositeId The ID of the composite
     */
    @When("composite {string} is activated")
    public void compositeIsActivated(String compositeId) {
        Composite composite = context.getComposite(compositeId)
            .orElseThrow(() -> new IllegalArgumentException("Composite not found: " + compositeId));
        
        composite.activate();
    }
    
    /**
     * Sets a composite to waiting state.
     * 
     * @param compositeId The ID of the composite
     */
    @When("composite {string} is set to waiting")
    public void compositeIsSetToWaiting(String compositeId) {
        Composite composite = context.getComposite(compositeId)
            .orElseThrow(() -> new IllegalArgumentException("Composite not found: " + compositeId));
        
        composite.setWaiting();
    }
    
    /**
     * Terminates a composite.
     * 
     * @param compositeId The ID of the composite
     */
    @When("composite {string} is terminated")
    public void compositeIsTerminated(String compositeId) {
        Composite composite = context.getComposite(compositeId)
            .orElseThrow(() -> new IllegalArgumentException("Composite not found: " + compositeId));
        
        composite.terminate();
    }
    
    /**
     * Verifies a composite's lifecycle state.
     * 
     * @param compositeId The composite ID
     * @param expectedState The expected lifecycle state
     */
    @Then("composite {string} should be in {string} state")
    public void compositeShouldBeInState(String compositeId, String expectedState) {
        Composite composite = context.getComposite(compositeId)
            .orElseThrow(() -> new IllegalArgumentException("Composite not found: " + compositeId));
        
        LifecycleState expected = LifecycleState.valueOf(expectedState.toUpperCase());
        assertEquals(expected, composite.getLifecycleState(),
            "Composite should be in " + expectedState + " state");
    }
    
    /**
     * Verifies a composite's status.
     * 
     * @param compositeId The composite ID
     * @param expectedStatus The expected status
     */
    @Then("composite {string} should have status {string}")
    public void compositeShouldHaveStatus(String compositeId, String expectedStatus) {
        Composite composite = context.getComposite(compositeId)
            .orElseThrow(() -> new IllegalArgumentException("Composite not found: " + compositeId));
        
        Status expected = Status.valueOf(expectedStatus.toUpperCase());
        assertEquals(expected, composite.getStatus(),
            "Composite should have status " + expectedStatus);
    }
    
    /**
     * Verifies that a composite contains a component.
     * 
     * @param compositeId The composite ID
     * @param componentId The component ID
     */
    @Then("composite {string} should contain component {string}")
    public void compositeShouldContainComponent(String compositeId, String componentId) {
        Composite composite = context.getComposite(compositeId)
            .orElseThrow(() -> new IllegalArgumentException("Composite not found: " + compositeId));
        
        Component component = composite.getComponent(componentId);
        assertNotNull(component, "Component should exist in composite");
        assertEquals(componentId, component.getReason(), "Component should have the correct reason/name");
    }
    
    /**
     * Verifies the type of a composite.
     * 
     * @param compositeId The composite ID
     * @param expectedType The expected composite type
     */
    @Then("composite {string} should be of type {string}")
    public void compositeShouldBeOfType(String compositeId, String expectedType) {
        Composite composite = context.getComposite(compositeId)
            .orElseThrow(() -> new IllegalArgumentException("Composite not found: " + compositeId));
        
        CompositeType expected = CompositeType.valueOf(expectedType.toUpperCase());
        assertEquals(expected, composite.getType(),
            "Composite should be of type " + expectedType);
    }
}