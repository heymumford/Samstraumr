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

import java.util.Map;
import java.util.Optional;

import org.s8r.core.composite.Composite;
import org.s8r.core.composite.CompositeFactory;
import org.s8r.core.composite.CompositeType;
import org.s8r.core.env.Environment;
import org.s8r.core.machine.Machine;
import org.s8r.core.machine.MachineFactory;
import org.s8r.core.machine.MachineState;
import org.s8r.core.machine.MachineType;
import org.s8r.test.cucumber.context.TestContext;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for testing S8r Machines in Cucumber BDD scenarios.
 * 
 * <p>This class provides step definitions for creating, manipulating, and verifying
 * the behavior of S8r Machines (the new implementation in org.s8r.core.machine).
 * It uses a TestContext to maintain state between steps.
 */
public class MachineSteps {
    private final TestContext context;
    
    /**
     * Constructs MachineSteps with a test context.
     * 
     * @param context The test context to use
     */
    public MachineSteps(TestContext context) {
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
     * Creates a new machine with the given name and type.
     * 
     * @param machineId The ID to reference the machine by
     * @param typeStr The type of machine
     */
    @Given("a machine with ID {string} of type {string}")
    public void aMachineWithIdOfType(String machineId, String typeStr) {
        Environment env = context.getEnvironment();
        MachineType type = MachineType.valueOf(typeStr.toUpperCase());
        Machine machine = MachineFactory.createMachine(type, machineId, "Test machine: " + machineId, env);
        context.addMachine(machineId, machine);
    }
    
    /**
     * Creates a data processor machine with the given ID.
     * 
     * @param machineId The ID to reference the machine by
     */
    @Given("a data processor machine with ID {string}")
    public void aDataProcessorMachineWithId(String machineId) {
        Environment env = context.getEnvironment();
        Machine machine = MachineFactory.createDataProcessor(machineId, "Test data processor: " + machineId, env);
        context.addMachine(machineId, machine);
    }
    
    /**
     * Creates a monitoring machine with the given ID.
     * 
     * @param machineId The ID to reference the machine by
     */
    @Given("a monitoring machine with ID {string}")
    public void aMonitoringMachineWithId(String machineId) {
        Environment env = context.getEnvironment();
        Machine machine = MachineFactory.createMonitoring(machineId, "Test monitoring: " + machineId, env);
        context.addMachine(machineId, machine);
    }
    
    /**
     * Adds a composite to a machine.
     * 
     * @param compositeId The ID of the composite
     * @param machineId The ID of the machine
     */
    @Given("composite {string} added to machine {string} as {string}")
    public void compositeAddedToMachine(String compositeId, String machineId, String referenceName) {
        // Get the machine
        Machine machine = context.getMachine(machineId)
            .orElseThrow(() -> new IllegalArgumentException("Machine not found: " + machineId));
        
        // Get or create the composite
        Composite composite;
        Optional<Composite> existingComposite = context.getComposite(compositeId);
        if (existingComposite.isPresent()) {
            composite = existingComposite.get();
        } else {
            Environment env = context.getEnvironment();
            composite = CompositeFactory.createComposite(compositeId, CompositeType.OBSERVER, env);
            context.addComposite(compositeId, composite);
        }
        
        // Add the composite to the machine
        machine.addComposite(referenceName, composite);
    }
    
    /**
     * Connects two composites in a machine.
     * 
     * @param sourceRef The reference name of the source composite
     * @param targetRef The reference name of the target composite
     * @param machineId The ID of the machine
     */
    @Given("composites {string} and {string} are connected in machine {string}")
    public void compositesAreConnectedInMachine(String sourceRef, String targetRef, String machineId) {
        Machine machine = context.getMachine(machineId)
            .orElseThrow(() -> new IllegalArgumentException("Machine not found: " + machineId));
        
        machine.connect(sourceRef, targetRef);
    }
    
    /**
     * Verifies that a machine exists with the specified ID.
     * 
     * @param machineId The machine ID to check
     */
    @Then("machine {string} should exist")
    public void machineShouldExist(String machineId) {
        assertTrue(
            context.getMachine(machineId).isPresent(),
            "Machine with ID " + machineId + " should exist"
        );
    }
    
    /**
     * Initializes a machine.
     * 
     * @param machineId The ID of the machine
     */
    @When("machine {string} is initialized")
    public void machineIsInitialized(String machineId) {
        Machine machine = context.getMachine(machineId)
            .orElseThrow(() -> new IllegalArgumentException("Machine not found: " + machineId));
        
        machine.initialize();
    }
    
    /**
     * Starts a machine.
     * 
     * @param machineId The ID of the machine
     */
    @When("machine {string} is started")
    public void machineIsStarted(String machineId) {
        Machine machine = context.getMachine(machineId)
            .orElseThrow(() -> new IllegalArgumentException("Machine not found: " + machineId));
        
        machine.start();
    }
    
    /**
     * Stops a machine.
     * 
     * @param machineId The ID of the machine
     */
    @When("machine {string} is stopped")
    public void machineIsStopped(String machineId) {
        Machine machine = context.getMachine(machineId)
            .orElseThrow(() -> new IllegalArgumentException("Machine not found: " + machineId));
        
        machine.stop();
    }
    
    /**
     * Pauses a machine.
     * 
     * @param machineId The ID of the machine
     */
    @When("machine {string} is paused")
    public void machineIsPaused(String machineId) {
        Machine machine = context.getMachine(machineId)
            .orElseThrow(() -> new IllegalArgumentException("Machine not found: " + machineId));
        
        machine.pause();
    }
    
    /**
     * Destroys a machine.
     * 
     * @param machineId The ID of the machine
     */
    @When("machine {string} is destroyed")
    public void machineIsDestroyed(String machineId) {
        Machine machine = context.getMachine(machineId)
            .orElseThrow(() -> new IllegalArgumentException("Machine not found: " + machineId));
        
        machine.destroy();
    }
    
    /**
     * Verifies a machine's state.
     * 
     * @param machineId The machine ID
     * @param expectedState The expected machine state
     */
    @Then("machine {string} should be in {string} state")
    public void machineShouldBeInState(String machineId, String expectedState) {
        Machine machine = context.getMachine(machineId)
            .orElseThrow(() -> new IllegalArgumentException("Machine not found: " + machineId));
        
        MachineState expected = MachineState.valueOf(expectedState.toUpperCase());
        assertEquals(expected, machine.getMachineState(),
            "Machine should be in " + expectedState + " state");
    }
    
    /**
     * Verifies that a machine contains a composite.
     * 
     * @param machineId The machine ID
     * @param compositeName The composite reference name
     */
    @Then("machine {string} should contain composite {string}")
    public void machineShouldContainComposite(String machineId, String compositeName) {
        Machine machine = context.getMachine(machineId)
            .orElseThrow(() -> new IllegalArgumentException("Machine not found: " + machineId));
        
        Optional<Composite> composite = machine.getComposite(compositeName);
        assertTrue(composite.isPresent(), 
            "Machine should contain composite with reference name: " + compositeName);
    }
    
    /**
     * Verifies the machine's ID.
     * 
     * @param machineId The reference ID
     */
    @Then("machine {string} should have a valid machine ID")
    public void machineShouldHaveValidMachineId(String machineId) {
        Machine machine = context.getMachine(machineId)
            .orElseThrow(() -> new IllegalArgumentException("Machine not found: " + machineId));
        
        String id = machine.getMachineId();
        assertNotNull(id, "Machine should have an ID");
        assertTrue(id.length() > 0, "Machine ID should not be empty");
    }
    
    /**
     * Verifies the type of a machine.
     * 
     * @param machineId The machine ID
     * @param expectedType The expected machine type
     */
    @Then("machine {string} should be of type {string}")
    public void machineShouldBeOfType(String machineId, String expectedType) {
        Machine machine = context.getMachine(machineId)
            .orElseThrow(() -> new IllegalArgumentException("Machine not found: " + machineId));
        
        MachineType expected = MachineType.valueOf(expectedType.toUpperCase());
        assertEquals(expected, machine.getType(),
            "Machine should be of type " + expectedType);
    }
    
    /**
     * Verifies that a machine is active.
     * 
     * @param machineId The machine ID
     */
    @Then("machine {string} should be active")
    public void machineShouldBeActive(String machineId) {
        Machine machine = context.getMachine(machineId)
            .orElseThrow(() -> new IllegalArgumentException("Machine not found: " + machineId));
        
        assertTrue(machine.isActive(), "Machine should be active");
    }
    
    /**
     * Verifies that a machine has connections between composites.
     * 
     * @param machineId The machine ID
     * @param sourceRef The source composite reference
     * @param targetRef The target composite reference
     */
    @Then("machine {string} should have connection from {string} to {string}")
    public void machineShouldHaveConnection(String machineId, String sourceRef, String targetRef) {
        Machine machine = context.getMachine(machineId)
            .orElseThrow(() -> new IllegalArgumentException("Machine not found: " + machineId));
        
        Map<String, List<String>> connections = machine.getConnections();
        assertTrue(connections.containsKey(sourceRef), 
            "Machine should have source composite: " + sourceRef);
        assertTrue(connections.get(sourceRef).contains(targetRef), 
            "Connection should exist from " + sourceRef + " to " + targetRef);
    }
}