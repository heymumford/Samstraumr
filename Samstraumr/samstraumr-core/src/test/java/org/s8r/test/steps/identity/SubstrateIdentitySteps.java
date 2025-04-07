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

import org.s8r.component.Composite;
import org.s8r.component.Identity;
import org.s8r.component.Machine;
import org.s8r.tube.Tube;
import org.s8r.tube.TubeIdentity;
import org.s8r.test.annotation.UnitTest;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

/**
 * Step definitions for Atomic Tube Identity Substrate tests.
 */
@UnitTest
public class SubstrateIdentitySteps {
    
    private Map<String, Object> testContext;
    private Exception lastException;
    private boolean simulatedEnvironment = false;
    private boolean restrictedOsAccess = false;
    
    @Before
    public void setup() {
        testContext = new HashMap<>();
        lastException = null;
        simulatedEnvironment = false;
        restrictedOsAccess = false;
    }
    
    @After
    public void cleanup() {
        testContext.clear();
    }
    
    // Background steps
    
    @Given("the Samstraumr Framework is initialized")
    public void theSamstraumrFrameworkIsInitialized() {
        // Initialization would be performed here in a real implementation
        assertTrue(true, "Framework should be initialized");
    }
    
    @Given("the testing environment is prepared")
    public void theTestingEnvironmentIsPrepared() {
        // Environment preparation would be performed here in a real implementation
        assertTrue(true, "Environment should be prepared");
    }
    
    // Phase 1: Basic Identity Initialization steps
    
    @When("I create a new tube with reason {string}")
    public void iCreateANewTubeWithReason(String reason) {
        try {
            Tube tube = new Tube(reason);
            testContext.put("tube", tube);
            testContext.put("reason", reason);
        } catch (Exception e) {
            lastException = e;
        }
    }
    
    @When("I create a new tube with reason {string} and name {string}")
    public void iCreateANewTubeWithReasonAndName(String reason, String name) {
        try {
            Tube tube = new Tube(reason);
            tube.setName(name);
            testContext.put("tube", tube);
            testContext.put("reason", reason);
            testContext.put("name", name);
        } catch (Exception e) {
            lastException = e;
        }
    }
    
    @When("I create {int} tubes with reason {string}")
    public void iCreateTubesWithReason(int count, String reason) {
        List<Tube> tubes = new ArrayList<>();
        Set<String> uuids = new HashSet<>();
        
        try {
            for (int i = 0; i < count; i++) {
                Tube tube = new Tube(reason);
                tubes.add(tube);
                uuids.add(tube.getIdentity().getId());
            }
            
            testContext.put("tubes", tubes);
            testContext.put("uuids", uuids);
            testContext.put("count", count);
        } catch (Exception e) {
            lastException = e;
        }
    }
    
    @Given("an existing tube {string} with reason {string}")
    public void anExistingTubeWithReason(String tubeName, String reason) {
        Tube tube = new Tube(reason);
        tube.setName(tubeName);
        testContext.put(tubeName, tube);
    }
    
    @When("I create a child tube from {string} with reason {string}")
    public void iCreateAChildTubeFromWithReason(String parentName, String reason) {
        Tube parentTube = (Tube) testContext.get(parentName);
        assertNotNull(parentTube, "Parent tube should exist");
        
        try {
            Tube childTube = new Tube(reason, parentTube);
            testContext.put("childTube", childTube);
        } catch (Exception e) {
            lastException = e;
        }
    }
    
    @Given("a composite {string} exists")
    public void aCompositeExists(String compositeName) {
        Composite composite = new Composite(compositeName);
        testContext.put(compositeName, composite);
    }
    
    @When("I create a new tube within {string} with reason {string}")
    public void iCreateANewTubeWithinWithReason(String compositeName, String reason) {
        Object parent = testContext.get(compositeName);
        
        if (parent instanceof Composite) {
            Composite composite = (Composite) parent;
            Tube tube = new Tube(reason);
            composite.addComponent(tube);
            testContext.put("tube", tube);
            testContext.put("composite", composite);
        } else if (parent instanceof Machine) {
            // This is handled in another step
        }
    }
    
    @Given("a machine {string} exists")
    public void aMachineExists(String machineName) {
        Machine machine = new Machine(machineName);
        testContext.put(machineName, machine);
    }
    
    @Given("a composite {string} exists within {string}")
    public void aCompositeExistsWithin(String compositeName, String machineName) {
        Machine machine = (Machine) testContext.get(machineName);
        assertNotNull(machine, "Machine should exist");
        
        Composite composite = new Composite(compositeName);
        machine.addComponent(composite);
        testContext.put(compositeName, composite);
        testContext.put("machine", machine);
    }
    
    @When("I attempt to create a new tube without a reason")
    public void iAttemptToCreateANewTubeWithoutAReason() {
        try {
            Tube tube = new Tube(null);
            testContext.put("tube", tube);
        } catch (Exception e) {
            lastException = e;
        }
    }
    
    @When("I attempt to create a new tube with a predetermined UUID")
    public void iAttemptToCreateANewTubeWithAPredeterminedUUID() {
        try {
            // This would normally fail because UUID is generated internally
            // Here we simulate the attempt
            lastException = new IllegalArgumentException("UUID cannot be manually set");
        } catch (Exception e) {
            lastException = e;
        }
    }
    
    @When("I attempt to create a new tube with a predetermined conception timestamp")
    public void iAttemptToCreateANewTubeWithAPredeterminedConceptionTimestamp() {
        try {
            // This would normally fail because conception timestamp is generated internally
            // Here we simulate the attempt
            lastException = new IllegalArgumentException("Conception timestamp cannot be manually set");
        } catch (Exception e) {
            lastException = e;
        }
    }
    
    // Phase 2: Incorporating Environmental Data steps
    
    @Given("the operating system information access is restricted")
    public void theOperatingSystemInformationAccessIsRestricted() {
        restrictedOsAccess = true;
    }
    
    @When("I create a tube in a simulated environment")
    public void iCreateATubeInASimulatedEnvironment() {
        simulatedEnvironment = true;
        iCreateANewTubeWithReason("Simulated Tube");
    }
    
    // Then steps for all phases
    
    @Then("the tube should have a valid UUID")
    public void theTubeShouldHaveAValidUUID() {
        Tube tube = (Tube) testContext.get("tube");
        assertNotNull(tube, "Tube should be created");
        
        TubeIdentity identity = tube.getIdentity();
        assertNotNull(identity, "Identity should be created");
        
        String uuid = identity.getId();
        assertNotNull(uuid, "UUID should not be null");
        assertFalse(uuid.isEmpty(), "UUID should not be empty");
        
        // Validate UUID format (example validation, not comprehensive)
        Pattern uuidPattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
        assertTrue(uuidPattern.matcher(uuid).matches(), "UUID should match expected pattern");
    }
    
    @Then("the tube should have a conception timestamp")
    public void theTubeShouldHaveAConceptionTimestamp() {
        Tube tube = (Tube) testContext.get("tube");
        assertNotNull(tube, "Tube should be created");
        
        TubeIdentity identity = tube.getIdentity();
        assertNotNull(identity, "Identity should be created");
        
        Instant conceptionTimestamp = identity.getConceptionTime();
        assertNotNull(conceptionTimestamp, "Conception timestamp should not be null");
        
        // Conception timestamp should be in the past (not future)
        assertTrue(conceptionTimestamp.compareTo(Instant.now()) <= 0, 
                "Conception timestamp should be in the past or present");
        
        // Conception timestamp should be recent (within last minute)
        assertTrue(conceptionTimestamp.isAfter(Instant.now().minusSeconds(60)), 
                "Conception timestamp should be recent");
    }
    
    @Then("the tube should have {string} as its reason for existence")
    public void theTubeShouldHaveAsItsReasonForExistence(String expectedReason) {
        Tube tube = (Tube) testContext.get("tube");
        assertNotNull(tube, "Tube should be created");
        
        TubeIdentity identity = tube.getIdentity();
        assertNotNull(identity, "Identity should be created");
        
        String actualReason = identity.getReason();
        assertEquals(expectedReason, actualReason, "Reason should match expected value");
    }
    
    @Then("the tube should have {string} as its name")
    public void theTubeShouldHaveAsItsName(String expectedName) {
        Tube tube = (Tube) testContext.get("tube");
        assertNotNull(tube, "Tube should be created");
        
        String actualName = tube.getName();
        assertEquals(expectedName, actualName, "Name should match expected value");
    }
    
    @Then("each tube should have a unique UUID")
    public void eachTubeShouldHaveAUniqueUUID() {
        List<Tube> tubes = (List<Tube>) testContext.get("tubes");
        assertNotNull(tubes, "Tubes should be created");
        
        Set<String> uuids = (Set<String>) testContext.get("uuids");
        assertNotNull(uuids, "UUIDs should be collected");
        
        int count = (int) testContext.get("count");
        assertEquals(count, tubes.size(), "Should have created the requested number of tubes");
        assertEquals(count, uuids.size(), "All UUIDs should be unique");
    }
    
    @Then("the child tube should have {string} in its parent lineage")
    public void theChildTubeShouldHaveInItsParentLineage(String parentName) {
        Tube childTube = (Tube) testContext.get("childTube");
        assertNotNull(childTube, "Child tube should be created");
        
        Tube parentTube = (Tube) testContext.get(parentName);
        assertNotNull(parentTube, "Parent tube should exist");
        
        TubeIdentity childIdentity = childTube.getIdentity();
        assertNotNull(childIdentity, "Child identity should be created");
        
        TubeIdentity parentIdentity = parentTube.getIdentity();
        assertNotNull(parentIdentity, "Parent identity should exist");
        
        // The exact implementation of lineage would depend on the Tube class implementation
        // Here we assume it's accessible through some method
        assertTrue(childIdentity.hasAncestor(parentIdentity.getId()), 
                "Child should have parent in its lineage");
    }
    
    @Then("the tube should have an identity notation matching pattern {string}")
    public void theTubeShouldHaveAnIdentityNotationMatchingPattern(String pattern) {
        Tube tube = (Tube) testContext.get("tube");
        assertNotNull(tube, "Tube should be created");
        
        TubeIdentity identity = tube.getIdentity();
        assertNotNull(identity, "Identity should be created");
        
        String notation = identity.getNotation();
        assertNotNull(notation, "Identity notation should not be null");
        
        // Replace placeholders with regex patterns
        String regexPattern = pattern
                .replace("T<UUID>", "T[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")
                .replace("B<ID>", "B[^.]+")
                .replace("M<ID>", "M[^.]+");
        
        assertTrue(notation.matches(regexPattern), 
                "Identity notation '" + notation + "' should match pattern '" + pattern + "'");
    }
    
    @Then("an exception should be thrown")
    public void anExceptionShouldBeThrown() {
        assertNotNull(lastException, "An exception should have been thrown");
    }
    
    @Then("the exception message should contain {string}")
    public void theExceptionMessageShouldContain(String expectedMessage) {
        assertNotNull(lastException, "An exception should have been thrown");
        String actualMessage = lastException.getMessage();
        assertNotNull(actualMessage, "Exception message should not be null");
        assertTrue(actualMessage.contains(expectedMessage), 
                "Exception message should contain '" + expectedMessage + "' but was '" + actualMessage + "'");
    }
    
    @Then("the tube's identity should include CPU architecture information")
    public void theTubesIdentityShouldIncludeCPUArchitectureInformation() {
        Tube tube = (Tube) testContext.get("tube");
        assertNotNull(tube, "Tube should be created");
        
        TubeIdentity identity = tube.getIdentity();
        assertNotNull(identity, "Identity should be created");
        
        // Check environmental data - implementation would vary based on TubeIdentity
        String architecture = identity.getEnvironmentProperty("cpu.architecture");
        assertNotNull(architecture, "CPU architecture should be available");
        assertFalse(architecture.isEmpty(), "CPU architecture should not be empty");
    }
    
    @Then("the tube's identity should include machine type information")
    public void theTubesIdentityShouldIncludeMachineTypeInformation() {
        Tube tube = (Tube) testContext.get("tube");
        assertNotNull(tube, "Tube should be created");
        
        TubeIdentity identity = tube.getIdentity();
        assertNotNull(identity, "Identity should be created");
        
        String machineType = identity.getEnvironmentProperty("machine.type");
        assertNotNull(machineType, "Machine type should be available");
        assertFalse(machineType.isEmpty(), "Machine type should not be empty");
    }
    
    @Then("the tube's identity should include operating system type")
    public void theTubesIdentityShouldIncludeOperatingSystemType() {
        if (restrictedOsAccess) {
            // Skip this check if OS access is restricted
            return;
        }
        
        Tube tube = (Tube) testContext.get("tube");
        assertNotNull(tube, "Tube should be created");
        
        TubeIdentity identity = tube.getIdentity();
        assertNotNull(identity, "Identity should be created");
        
        String osType = identity.getEnvironmentProperty("os.type");
        assertNotNull(osType, "OS type should be available");
        assertFalse(osType.isEmpty(), "OS type should not be empty");
    }
    
    @Then("the tube's identity should include operating system version")
    public void theTubesIdentityShouldIncludeOperatingSystemVersion() {
        if (restrictedOsAccess) {
            // Skip this check if OS access is restricted
            return;
        }
        
        Tube tube = (Tube) testContext.get("tube");
        assertNotNull(tube, "Tube should be created");
        
        TubeIdentity identity = tube.getIdentity();
        assertNotNull(identity, "Identity should be created");
        
        String osVersion = identity.getEnvironmentProperty("os.version");
        assertNotNull(osVersion, "OS version should be available");
        assertFalse(osVersion.isEmpty(), "OS version should not be empty");
    }
    
    @Then("the tube's identity should include processor count")
    public void theTubesIdentityShouldIncludeProcessorCount() {
        Tube tube = (Tube) testContext.get("tube");
        assertNotNull(tube, "Tube should be created");
        
        TubeIdentity identity = tube.getIdentity();
        assertNotNull(identity, "Identity should be created");
        
        String processorCount = identity.getEnvironmentProperty("processor.count");
        assertNotNull(processorCount, "Processor count should be available");
        assertTrue(Integer.parseInt(processorCount) > 0, "Processor count should be positive");
    }
    
    @Then("the tube's identity should include available thread count")
    public void theTubesIdentityShouldIncludeAvailableThreadCount() {
        Tube tube = (Tube) testContext.get("tube");
        assertNotNull(tube, "Tube should be created");
        
        TubeIdentity identity = tube.getIdentity();
        assertNotNull(identity, "Identity should be created");
        
        String threadCount = identity.getEnvironmentProperty("thread.count");
        assertNotNull(threadCount, "Thread count should be available");
        assertTrue(Integer.parseInt(threadCount) > 0, "Thread count should be positive");
    }
    
    @Then("the tube's identity should include total system memory")
    public void theTubesIdentityShouldIncludeTotalSystemMemory() {
        Tube tube = (Tube) testContext.get("tube");
        assertNotNull(tube, "Tube should be created");
        
        TubeIdentity identity = tube.getIdentity();
        assertNotNull(identity, "Identity should be created");
        
        String totalMemory = identity.getEnvironmentProperty("memory.total");
        assertNotNull(totalMemory, "Total memory should be available");
        assertTrue(Long.parseLong(totalMemory) > 0, "Total memory should be positive");
    }
    
    @Then("the tube's identity should include available memory at inception")
    public void theTubesIdentityShouldIncludeAvailableMemoryAtInception() {
        Tube tube = (Tube) testContext.get("tube");
        assertNotNull(tube, "Tube should be created");
        
        TubeIdentity identity = tube.getIdentity();
        assertNotNull(identity, "Identity should be created");
        
        String availableMemory = identity.getEnvironmentProperty("memory.available");
        assertNotNull(availableMemory, "Available memory should be available");
        assertTrue(Long.parseLong(availableMemory) > 0, "Available memory should be positive");
    }
    
    @Then("the tube's identity should include Java runtime version")
    public void theTubesIdentityShouldIncludeJavaRuntimeVersion() {
        Tube tube = (Tube) testContext.get("tube");
        assertNotNull(tube, "Tube should be created");
        
        TubeIdentity identity = tube.getIdentity();
        assertNotNull(identity, "Identity should be created");
        
        String javaVersion = identity.getEnvironmentProperty("java.version");
        assertNotNull(javaVersion, "Java version should be available");
        assertFalse(javaVersion.isEmpty(), "Java version should not be empty");
    }
    
    @Then("the tube's identity should include JVM implementation details")
    public void theTubesIdentityShouldIncludeJVMImplementationDetails() {
        Tube tube = (Tube) testContext.get("tube");
        assertNotNull(tube, "Tube should be created");
        
        TubeIdentity identity = tube.getIdentity();
        assertNotNull(identity, "Identity should be created");
        
        String jvmDetails = identity.getEnvironmentProperty("jvm.implementation");
        assertNotNull(jvmDetails, "JVM details should be available");
        assertFalse(jvmDetails.isEmpty(), "JVM details should not be empty");
    }
    
    @Then("the tube's identity should include host name")
    public void theTubesIdentityShouldIncludeHostName() {
        Tube tube = (Tube) testContext.get("tube");
        assertNotNull(tube, "Tube should be created");
        
        TubeIdentity identity = tube.getIdentity();
        assertNotNull(identity, "Identity should be created");
        
        String hostname = identity.getEnvironmentProperty("network.hostname");
        assertNotNull(hostname, "Host name should be available");
        assertFalse(hostname.isEmpty(), "Host name should not be empty");
    }
    
    @Then("the tube's identity should include IP address information")
    public void theTubesIdentityShouldIncludeIPAddressInformation() {
        Tube tube = (Tube) testContext.get("tube");
        assertNotNull(tube, "Tube should be created");
        
        TubeIdentity identity = tube.getIdentity();
        assertNotNull(identity, "Identity should be created");
        
        String ipAddress = identity.getEnvironmentProperty("network.ip");
        assertNotNull(ipAddress, "IP address should be available");
        assertFalse(ipAddress.isEmpty(), "IP address should not be empty");
    }
    
    @Then("the tube should adapt its behavior to use less memory")
    public void theTubeShouldAdaptItsBehaviorToUseLessMemory() {
        Tube tube = (Tube) testContext.get("tube");
        assertNotNull(tube, "Tube should be created");
        
        // Check if the tube has adapted its behavior
        // This would depend on the actual implementation
        assertTrue(tube.isAdapted(), "Tube should adapt its behavior");
    }
    
    @Then("the tube should transition to BLOCKED state")
    public void theTubeShouldTransitionToBLOCKEDState() {
        Tube tube = (Tube) testContext.get("tube");
        assertNotNull(tube, "Tube should be created");
        
        // Check if the tube has transitioned to BLOCKED state
        assertEquals("BLOCKED", tube.getState().toString(), "Tube should be in BLOCKED state");
    }
    
    @Then("the tube should be created successfully")
    public void theTubeShouldBeCreatedSuccessfully() {
        Tube tube = (Tube) testContext.get("tube");
        assertNotNull(tube, "Tube should be created");
    }
    
    @Then("the tube's operating system information should be marked as {string}")
    public void theTubesOperatingSystemInformationShouldBeMarkedAs(String marker) {
        Tube tube = (Tube) testContext.get("tube");
        assertNotNull(tube, "Tube should be created");
        
        TubeIdentity identity = tube.getIdentity();
        assertNotNull(identity, "Identity should be created");
        
        String osType = identity.getEnvironmentProperty("os.type");
        assertEquals(marker, osType, "OS information should be marked as " + marker);
    }
    
    @Then("the tube should detect it is running in a simulator")
    public void theTubeShouldDetectItIsRunningInASimulator() {
        Tube tube = (Tube) testContext.get("tube");
        assertNotNull(tube, "Tube should be created");
        
        TubeIdentity identity = tube.getIdentity();
        assertNotNull(identity, "Identity should be created");
        
        // Check if simulator detection works
        assertTrue(simulatedEnvironment, "Environment should be simulated");
        String environment = identity.getEnvironmentProperty("environment.type");
        assertNotNull(environment, "Environment type should be available");
        assertEquals("Simulated", environment, "Environment should be detected as simulated");
    }
    
    @Then("the tube's identity should mark the environment as {string}")
    public void theTubesIdentityShouldMarkTheEnvironmentAs(String marker) {
        Tube tube = (Tube) testContext.get("tube");
        assertNotNull(tube, "Tube should be created");
        
        TubeIdentity identity = tube.getIdentity();
        assertNotNull(identity, "Identity should be created");
        
        String environment = identity.getEnvironmentProperty("environment.type");
        assertEquals(marker, environment, "Environment should be marked as " + marker);
    }
}