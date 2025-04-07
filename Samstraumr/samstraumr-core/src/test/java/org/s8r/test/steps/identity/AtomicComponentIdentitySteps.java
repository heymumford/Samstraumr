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

package org.s8r.test.steps.identity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.s8r.component.Component;
import org.s8r.component.Environment;
import org.s8r.component.Machine;
import org.s8r.component.State;
import org.s8r.component.composite.Composite;
import org.s8r.component.core.ComponentException;
import org.s8r.component.core.CompositeException;
import org.s8r.component.identity.Identity;
import org.s8r.test.annotation.ComponentTest;
import org.s8r.test.annotation.IdentityTest;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Steps for testing the atomic component identity functionality.
 * These tests ensure that components maintain proper identity throughout their lifecycle.
 */
@ComponentTest
@IdentityTest
public class AtomicComponentIdentitySteps {

    private Map<String, Object> testContext;
    private Environment testEnvironment;
    private Exception lastException;

    @Before
    public void setup() {
        // Initialize test context and environment
        testContext = new ConcurrentHashMap<>();
        testEnvironment = new Environment();
        testEnvironment.setParameter("testMode", "true");
        
        lastException = null;
    }

    @After
    public void cleanup() {
        // Clean up components and other resources
        for (Object obj : testContext.values()) {
            if (obj instanceof Component) {
                try {
                    ((Component) obj).terminate();
                } catch (Exception e) {
                    // Ignore termination exceptions during cleanup
                }
            }
        }
        
        testContext.clear();
    }

    @Given("the S8r Framework is initialized")
    public void theS8rFrameworkIsInitialized() {
        // Verify framework is ready (no-op in this test, just for readability)
        assertNotNull(testEnvironment, "Test environment should be initialized");
    }

    @Given("the testing environment is prepared")
    public void theTestingEnvironmentIsPrepared() {
        // Set up necessary environment parameters for testing
        testEnvironment.setParameter("testRun", true);
        testEnvironment.setParameter("simulationMode", false);
        testEnvironment.setParameter("memoryConstraints", false);
    }

    @When("I create a new component with reason {string}")
    public void iCreateANewComponentWithReason(String reason) {
        // Create new component
        Component component = Component.create(reason, testEnvironment);
        testContext.put("currentComponent", component);
    }

    @When("I create a new component with reason {string} and name {string}")
    public void iCreateANewComponentWithReasonAndName(String reason, String name) {
        // Create environment with name parameter
        Environment env = new Environment();
        env.copyFrom(testEnvironment);
        env.setParameter("name", name);
        
        // Create component
        Component component = Component.create(reason, env);
        testContext.put("currentComponent", component);
        testContext.put("componentName", name);
    }

    @When("I create {int} components with reason {string}")
    public void iCreateComponentsWithReason(int count, String reason) {
        // Create multiple components and store them
        List<Component> components = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            components.add(Component.create(reason + " " + i, testEnvironment));
        }
        testContext.put("multipleComponents", components);
        testContext.put("componentCount", count);
    }

    @Given("an existing component {string} with reason {string}")
    public void anExistingComponentWithReason(String identifier, String reason) {
        // Create and store a component for later reference
        Component component = Component.create(reason, testEnvironment);
        testContext.put(identifier, component);
    }

    @When("I create a child component from {string} with reason {string}")
    public void iCreateAChildComponentFromWithReason(String parentIdentifier, String reason) {
        // Retrieve parent component and create child
        Component parent = (Component) testContext.get(parentIdentifier);
        assertNotNull(parent, "Parent component should exist");
        
        Component child = Component.createChild(reason, testEnvironment, parent);
        testContext.put("childComponent", child);
    }

    @Given("a composite {string} exists")
    public void aCompositeExists(String identifier) {
        // Create a composite for testing
        try {
            Composite composite = new Composite(identifier, testEnvironment);
            testContext.put(identifier, composite);
        } catch (CompositeException e) {
            lastException = e;
            fail("Failed to create composite: " + e.getMessage());
        }
    }

    @When("I create a new component within {string} with reason {string}")
    public void iCreateANewComponentWithinWithReason(String compositeIdentifier, String reason) {
        // Create a component within the specified composite
        Composite composite = (Composite) testContext.get(compositeIdentifier);
        assertNotNull(composite, "Composite should exist");
        
        Component component = Component.create(reason, testEnvironment);
        composite.addComponent(component.getUniqueId().toString(), component);
        testContext.put("currentComponent", component);
    }

    @Given("a machine {string} exists")
    public void aMachineExists(String identifier) {
        // Create a machine for testing
        try {
            Machine machine = new Machine(identifier, testEnvironment);
            testContext.put(identifier, machine);
        } catch (Exception e) {
            lastException = e;
            fail("Failed to create machine: " + e.getMessage());
        }
    }

    @Given("a composite {string} exists within {string}")
    public void aCompositeExistsWithin(String compositeIdentifier, String machineIdentifier) {
        // Create a composite within a machine
        try {
            Machine machine = (Machine) testContext.get(machineIdentifier);
            assertNotNull(machine, "Machine should exist");
            
            Composite composite = new Composite(compositeIdentifier, testEnvironment);
            machine.addComposite(compositeIdentifier, composite);
            testContext.put(compositeIdentifier, composite);
        } catch (Exception e) {
            lastException = e;
            fail("Failed to create composite within machine: " + e.getMessage());
        }
    }

    @When("I attempt to create a new component without a reason")
    public void iAttemptToCreateANewComponentWithoutAReason() {
        // Attempt to create component with null reason, should throw exception
        try {
            Component component = Component.create(null, testEnvironment);
            testContext.put("currentComponent", component);
        } catch (Exception e) {
            lastException = e;
        }
    }

    @When("I attempt to create a new component with a predetermined UUID")
    public void iAttemptToCreateANewComponentWithAPredeterminedUUID() {
        // Attempt to create component with manual UUID, should throw exception
        try {
            // This would need to use reflection or a special constructor to attempt setting UUID
            // Here we simulate the exception that would occur
            Environment env = new Environment();
            env.copyFrom(testEnvironment);
            env.setParameter("_forceUUID", UUID.randomUUID().toString());
            
            Component component = Component.create("Test Reason", env);
            testContext.put("currentComponent", component);
        } catch (Exception e) {
            lastException = e;
        }
    }

    @When("I attempt to create a new component with a predetermined creation timestamp")
    public void iAttemptToCreateANewComponentWithAPredeterminedCreationTimestamp() {
        // Attempt to create component with manual timestamp, should throw exception
        try {
            // This would need to use reflection or a special constructor to attempt setting timestamp
            // Here we simulate the exception that would occur
            Environment env = new Environment();
            env.copyFrom(testEnvironment);
            env.setParameter("_forceTimestamp", Instant.now().toString());
            
            Component component = Component.create("Test Reason", env);
            testContext.put("currentComponent", component);
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Given("the operating system information access is restricted")
    public void theOperatingSystemInformationAccessIsRestricted() {
        // Simulate restricted OS access
        testEnvironment.setParameter("restrictedOSAccess", true);
    }

    @When("I create a component in a simulated environment")
    public void iCreateAComponentInASimulatedEnvironment() {
        // Create component in simulated environment
        Environment simulatedEnv = new Environment();
        simulatedEnv.copyFrom(testEnvironment);
        simulatedEnv.setParameter("simulationMode", true);
        
        Component component = Component.create("Simulated Component", simulatedEnv);
        testContext.put("currentComponent", component);
    }

    @Given("a component {string} with reason {string}")
    public void aComponentWithReason(String identifier, String reason) {
        // Create component for behavior testing
        Component component = Component.create(reason, testEnvironment);
        testContext.put(identifier, component);
    }

    @When("the component detects low available memory in its environment")
    public void theComponentDetectsLowAvailableMemoryInItsEnvironment() {
        // Simulate low memory condition
        Component component = (Component) testContext.get("MemoryAdapter");
        assertNotNull(component, "Component should exist");
        
        Environment env = component.getEnvironment();
        env.setParameter("availableMemory", 128);  // Very low memory in MB
        env.setParameter("memoryConstraints", true);
        
        // Trigger component behavior update (would normally happen automatically)
        component.handleEnvironmentChange("memory");
    }

    @When("the component detects network unavailability")
    public void theComponentDetectsNetworkUnavailability() {
        // Simulate network unavailability
        Component component = (Component) testContext.get("NetworkMonitor");
        assertNotNull(component, "Component should exist");
        
        Environment env = component.getEnvironment();
        env.setParameter("networkAvailable", false);
        
        // Trigger component behavior update
        component.handleEnvironmentChange("network");
    }

    @Then("the component should have a valid UUID")
    public void theComponentShouldHaveAValidUUID() {
        Component component = (Component) testContext.get("currentComponent");
        assertNotNull(component, "Component should exist");
        
        UUID uuid = component.getUniqueId();
        assertNotNull(uuid, "Component UUID should not be null");
    }

    @Then("the component should have a creation timestamp")
    public void theComponentShouldHaveACreationTimestamp() {
        Component component = (Component) testContext.get("currentComponent");
        assertNotNull(component, "Component should exist");
        
        Instant timestamp = component.getCreationTime();
        assertNotNull(timestamp, "Creation timestamp should not be null");
        
        // Should be a recent timestamp (within last minute)
        assertTrue(timestamp.isAfter(Instant.now().minusSeconds(60)), 
                "Creation timestamp should be recent");
    }

    @Then("the component should have {string} as its reason for existence")
    public void theComponentShouldHaveAsItsReasonForExistence(String reason) {
        Component component = (Component) testContext.get("currentComponent");
        assertNotNull(component, "Component should exist");
        
        assertEquals(reason, component.getReason(), 
                "Component should have the correct reason");
    }

    @Then("the component should have {string} as its name")
    public void theComponentShouldHaveAsItsName(String name) {
        Component component = (Component) testContext.get("currentComponent");
        assertNotNull(component, "Component should exist");
        
        assertEquals(name, component.getName(), 
                "Component should have the correct name");
    }

    @Then("each component should have a unique UUID")
    public void eachComponentShouldHaveAUniqueUUID() {
        List<Component> components = (List<Component>) testContext.get("multipleComponents");
        assertNotNull(components, "Components list should exist");
        
        // Extract all UUIDs
        Set<UUID> uuids = new HashSet<>();
        for (Component component : components) {
            uuids.add(component.getUniqueId());
        }
        
        // Check uniqueness
        assertEquals(components.size(), uuids.size(), 
                "All components should have unique UUIDs");
    }

    @Then("the child component should have {string} in its parent lineage")
    public void theChildComponentShouldHaveInItsParentLineage(String parentIdentifier) {
        Component child = (Component) testContext.get("childComponent");
        Component parent = (Component) testContext.get(parentIdentifier);
        
        assertNotNull(child, "Child component should exist");
        assertNotNull(parent, "Parent component should exist");
        
        Identity childIdentity = child.getIdentity();
        UUID parentUUID = parent.getUniqueId();
        
        assertTrue(childIdentity.getLineage().contains(parentUUID.toString()), 
                "Child should have parent in lineage");
    }

    @Then("the component should have an identity notation matching pattern {string}")
    public void theComponentShouldHaveAnIdentityNotationMatchingPattern(String pattern) {
        Component component = (Component) testContext.get("currentComponent");
        assertNotNull(component, "Component should exist");
        
        // Convert pattern string to regex pattern (C<UUID> -> C[a-f0-9-]+)
        String regex = pattern
                .replace("<UUID>", "[a-f0-9\\-]+")
                .replace("<ID>", "[a-zA-Z0-9_]+");
        
        String notation = component.getIdentity().getNotation();
        assertTrue(Pattern.matches(regex, notation), 
                "Identity notation '" + notation + "' should match pattern: " + pattern);
    }

    @Then("an exception should be thrown")
    public void anExceptionShouldBeThrown() {
        assertNotNull(lastException, "An exception should have been thrown");
    }

    @Then("the exception message should contain {string}")
    public void theExceptionMessageShouldContain(String message) {
        assertNotNull(lastException, "An exception should have been thrown");
        assertTrue(lastException.getMessage().contains(message), 
                "Exception message should contain: " + message);
    }

    @Then("the component's identity should include CPU architecture information")
    public void theComponentsIdentityShouldIncludeCPUArchitectureInformation() {
        Component component = (Component) testContext.get("currentComponent");
        assertNotNull(component, "Component should exist");
        
        Identity identity = component.getIdentity();
        assertNotNull(identity.getEnvironmentalData("cpuArchitecture"), 
                "Identity should include CPU architecture information");
    }

    @Then("the component's identity should include machine type information")
    public void theComponentsIdentityShouldIncludeMachineTypeInformation() {
        Component component = (Component) testContext.get("currentComponent");
        assertNotNull(component, "Component should exist");
        
        Identity identity = component.getIdentity();
        assertNotNull(identity.getEnvironmentalData("machineType"), 
                "Identity should include machine type information");
    }

    @Then("the component's identity should include operating system type")
    public void theComponentsIdentityShouldIncludeOperatingSystemType() {
        Component component = (Component) testContext.get("currentComponent");
        assertNotNull(component, "Component should exist");
        
        Identity identity = component.getIdentity();
        assertNotNull(identity.getEnvironmentalData("osType"), 
                "Identity should include OS type information");
    }

    @And("the component's identity should include operating system version")
    public void theComponentsIdentityShouldIncludeOperatingSystemVersion() {
        Component component = (Component) testContext.get("currentComponent");
        assertNotNull(component, "Component should exist");
        
        Identity identity = component.getIdentity();
        assertNotNull(identity.getEnvironmentalData("osVersion"), 
                "Identity should include OS version information");
    }

    @Then("the component's identity should include processor count")
    public void theComponentsIdentityShouldIncludeProcessorCount() {
        Component component = (Component) testContext.get("currentComponent");
        assertNotNull(component, "Component should exist");
        
        Identity identity = component.getIdentity();
        assertNotNull(identity.getEnvironmentalData("processorCount"), 
                "Identity should include processor count information");
    }

    @And("the component's identity should include available thread count")
    public void theComponentsIdentityShouldIncludeAvailableThreadCount() {
        Component component = (Component) testContext.get("currentComponent");
        assertNotNull(component, "Component should exist");
        
        Identity identity = component.getIdentity();
        assertNotNull(identity.getEnvironmentalData("availableThreads"), 
                "Identity should include available thread count information");
    }

    @Then("the component's identity should include total system memory")
    public void theComponentsIdentityShouldIncludeTotalSystemMemory() {
        Component component = (Component) testContext.get("currentComponent");
        assertNotNull(component, "Component should exist");
        
        Identity identity = component.getIdentity();
        assertNotNull(identity.getEnvironmentalData("totalMemory"), 
                "Identity should include total memory information");
    }

    @And("the component's identity should include available memory at inception")
    public void theComponentsIdentityShouldIncludeAvailableMemoryAtInception() {
        Component component = (Component) testContext.get("currentComponent");
        assertNotNull(component, "Component should exist");
        
        Identity identity = component.getIdentity();
        assertNotNull(identity.getEnvironmentalData("availableMemory"), 
                "Identity should include available memory information");
    }

    @Then("the component's identity should include Java runtime version")
    public void theComponentsIdentityShouldIncludeJavaRuntimeVersion() {
        Component component = (Component) testContext.get("currentComponent");
        assertNotNull(component, "Component should exist");
        
        Identity identity = component.getIdentity();
        assertNotNull(identity.getEnvironmentalData("javaVersion"), 
                "Identity should include Java version information");
    }

    @And("the component's identity should include JVM implementation details")
    public void theComponentsIdentityShouldIncludeJVMImplementationDetails() {
        Component component = (Component) testContext.get("currentComponent");
        assertNotNull(component, "Component should exist");
        
        Identity identity = component.getIdentity();
        assertNotNull(identity.getEnvironmentalData("jvmImplementation"), 
                "Identity should include JVM implementation information");
    }

    @Then("the component's identity should include host name")
    public void theComponentsIdentityShouldIncludeHostName() {
        Component component = (Component) testContext.get("currentComponent");
        assertNotNull(component, "Component should exist");
        
        Identity identity = component.getIdentity();
        assertNotNull(identity.getEnvironmentalData("hostName"), 
                "Identity should include host name information");
    }

    @And("the component's identity should include IP address information")
    public void theComponentsIdentityShouldIncludeIPAddressInformation() {
        Component component = (Component) testContext.get("currentComponent");
        assertNotNull(component, "Component should exist");
        
        Identity identity = component.getIdentity();
        assertNotNull(identity.getEnvironmentalData("ipAddress"), 
                "Identity should include IP address information");
    }

    @Then("the component should adapt its behavior to use less memory")
    public void theComponentShouldAdaptItsBehaviorToUseLessMemory() {
        Component component = (Component) testContext.get("MemoryAdapter");
        assertNotNull(component, "Component should exist");
        
        // Check if component's memory optimization mode is enabled
        Map<String, Object> behaviorStatus = component.getBehaviorStatus();
        assertTrue((Boolean) behaviorStatus.getOrDefault("memoryOptimizationEnabled", false),
                "Component should enable memory optimization behavior");
        
        // Could also check for other indicators of memory optimization
    }

    @Then("the component should transition to WAITING state")
    public void theComponentShouldTransitionToWaitingState() {
        Component component = (Component) testContext.get("NetworkMonitor");
        assertNotNull(component, "Component should exist");
        
        // Check current state
        assertEquals(State.WAITING, component.getState(),
                "Component should transition to WAITING state when network is unavailable");
    }

    @Then("the component should be created successfully")
    public void theComponentShouldBeCreatedSuccessfully() {
        Component component = (Component) testContext.get("currentComponent");
        assertNotNull(component, "Component should exist despite restricted OS access");
    }

    @And("the component's operating system information should be marked as {string}")
    public void theComponentsOperatingSystemInformationShouldBeMarkedAs(String value) {
        Component component = (Component) testContext.get("currentComponent");
        assertNotNull(component, "Component should exist");
        
        Identity identity = component.getIdentity();
        assertEquals(value, identity.getEnvironmentalData("osType"), 
                "OS info should be marked as: " + value);
    }

    @Then("the component should detect it is running in a simulator")
    public void theComponentShouldDetectItIsRunningInASimulator() {
        Component component = (Component) testContext.get("currentComponent");
        assertNotNull(component, "Component should exist");
        
        // Check if component detected simulation mode
        assertTrue((Boolean) component.getEnvironment().getParameter("simulationMode"),
                "Component should detect simulation mode");
    }

    @And("the component's identity should mark the environment as {string}")
    public void theComponentsIdentityShouldMarkTheEnvironmentAs(String value) {
        Component component = (Component) testContext.get("currentComponent");
        assertNotNull(component, "Component should exist");
        
        Identity identity = component.getIdentity();
        assertEquals(value, identity.getEnvironmentalData("environmentType"), 
                "Environment type should be marked as: " + value);
    }
}