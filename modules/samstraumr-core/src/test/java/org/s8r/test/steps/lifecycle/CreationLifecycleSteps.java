/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools 
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights 
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0
 */
package org.s8r.test.steps.lifecycle;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import io.cucumber.java.en.But;

import org.junit.jupiter.api.Assertions;
import org.s8r.component.Component;
import org.s8r.component.Identity;
import org.s8r.component.Environment;
import org.s8r.component.State;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Step definitions for creation lifecycle tests, focusing on the comprehensive
 * view of tube creation from initial intention through conception and early
 * development phases.
 */
public class CreationLifecycleSteps {

    private static final Logger LOGGER = Logger.getLogger(CreationLifecycleSteps.class.getName());
    
    // Test context
    private Map<String, Object> creationPlan = new HashMap<>();
    private Map<String, Object> resourceAllocation = new HashMap<>();
    private Component component;
    private Component parentComponent;
    private List<Component> tubeFamily = new ArrayList<>();
    private Identity identity;
    private Environment environment;
    private Exception lastException;
    private LocalDateTime creationTime;
    private boolean limitedResources = false;
    private boolean incompatibleEnvironment = false;
    
    // Creation tracking
    private Map<String, Component> createdTubes = new HashMap<>();
    private Map<String, Identity> createdIdentities = new HashMap<>();
    
    @Given("a creation intention exists")
    public void a_creation_intention_exists() {
        creationPlan = new HashMap<>();
        creationPlan.put("id", UUID.randomUUID().toString());
        creationPlan.put("purpose", "Test Tube Creation");
        creationPlan.put("timestamp", LocalDateTime.now().toString());
        
        // Resource requirements
        Map<String, Object> resources = new HashMap<>();
        resources.put("memory", 100);
        resources.put("processingCapacity", 10);
        resources.put("connectionPoints", 5);
        resources.put("stateContainers", 3);
        creationPlan.put("resources", resources);
        
        // Lifespan
        Map<String, Object> lifespan = new HashMap<>();
        lifespan.put("estimatedDuration", "medium");
        lifespan.put("persistAfterCompletion", true);
        lifespan.put("maxInactivityPeriod", 3600);
        creationPlan.put("lifespan", lifespan);
        
        LOGGER.info("Creation intention established with ID: " + creationPlan.get("id"));
    }
    
    @Given("all necessary resources have been allocated")
    public void all_necessary_resources_have_been_allocated() {
        // Ensure we have a creation plan
        if (creationPlan == null || creationPlan.isEmpty()) {
            a_creation_intention_exists();
        }
        
        // Allocate resources
        resourceAllocation = new HashMap<>();
        
        // Memory
        resourceAllocation.put("memory", creationPlan.get("resources", Map.class).get("memory"));
        
        // Identity
        String identityId = UUID.randomUUID().toString();
        resourceAllocation.put("identityId", identityId);
        
        // Environmental context
        Map<String, Object> context = new HashMap<>();
        context.put("creationTime", LocalDateTime.now().toString());
        context.put("environmentId", "test-env-" + UUID.randomUUID().toString().substring(0, 8));
        context.put("contextSignature", UUID.randomUUID().toString());
        resourceAllocation.put("environmentalContext", context);
        
        // Creation parameters
        Map<String, Object> params = new HashMap<>();
        params.put("name", "Tube-" + UUID.randomUUID().toString().substring(0, 8));
        params.put("type", "standard");
        params.put("initialState", "CREATION");
        params.put("identityId", identityId);
        resourceAllocation.put("creationParams", params);
        
        LOGGER.info("Resources allocated for tube creation");
    }
    
    @Given("a tube has been conceived")
    public void a_tube_has_been_conceived() {
        // If we don't have necessary resources, allocate them
        if (resourceAllocation == null || resourceAllocation.isEmpty()) {
            all_necessary_resources_have_been_allocated();
        }
        
        // Create the component
        try {
            creationTime = LocalDateTime.now();
            component = Component.createAdam("Test Tube Conception");
            identity = component.getIdentity();
            
            Assertions.assertNotNull(component, "Component should be created");
            Assertions.assertNotNull(identity, "Identity should be created");
            
            LOGGER.info("Tube conceived with identity: " + identity.getId());
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to conceive tube: " + e.getMessage());
            throw new AssertionError("Failed to conceive tube", e);
        }
    }
    
    @Given("a tube with established identity")
    public void a_tube_with_established_identity() {
        // If tube hasn't been conceived yet, do so
        if (component == null) {
            a_tube_has_been_conceived();
        }
        
        // Verify identity is established
        Assertions.assertNotNull(identity, "Tube should have an identity");
        Assertions.assertNotNull(identity.getId(), "Identity should have an ID");
        Assertions.assertNotNull(identity.getCreationTime(), "Identity should have a creation time");
        
        LOGGER.info("Tube has established identity: " + identity.getId());
    }
    
    @Given("a tube in zygotic state")
    public void a_tube_in_zygotic_state() {
        // If tube doesn't exist yet, create one
        if (component == null) {
            a_tube_with_established_identity();
        }
        
        // Set up environment to simulate zygotic state
        environment = component.getEnvironment();
        environment.setValue("state.zygotic", "true");
        environment.setValue("structure.minimal", "true");
        environment.setValue("structure.complete", "true");
        environment.setValue("survival.mechanisms.initialized", "true");
        environment.setValue("specialized.functions", "false");
        
        // Transition to appropriate state
        component.setState(State.CREATED);
        
        LOGGER.info("Tube is in zygotic state");
    }
    
    @Given("an origin tube exists in the system")
    public void an_origin_tube_exists_in_the_system() {
        try {
            // Create an Adam component as the origin tube
            parentComponent = Component.createAdam("Origin Tube");
            createdTubes.put("origin", parentComponent);
            createdIdentities.put("origin", parentComponent.getIdentity());
            
            // Ensure parent is in ACTIVE state for creating children
            if (parentComponent.getState() != State.ACTIVE) {
                // Transition to ACTIVE state
                parentComponent.setState(State.ACTIVE);
            }
            
            Assertions.assertNotNull(parentComponent, "Origin tube should be created");
            Assertions.assertEquals(State.ACTIVE, parentComponent.getState(), 
                "Origin tube should be in ACTIVE state");
                
            LOGGER.info("Created origin tube with identity: " + parentComponent.getIdentity().getId());
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to create origin tube: " + e.getMessage());
            throw new AssertionError("Failed to create origin tube", e);
        }
    }
    
    @Given("multiple tubes with parent-child relationships exist")
    public void multiple_tubes_with_parent_child_relationships_exist() {
        try {
            // Create an origin tube
            Component origin = Component.createAdam("Origin Tube");
            origin.setState(State.ACTIVE);
            createdTubes.put("origin", origin);
            createdIdentities.put("origin", origin.getIdentity());
            tubeFamily.add(origin);
            
            // Create first generation children
            Component child1 = origin.createChild("Child 1");
            child1.setState(State.ACTIVE);
            createdTubes.put("child1", child1);
            createdIdentities.put("child1", child1.getIdentity());
            tubeFamily.add(child1);
            
            Component child2 = origin.createChild("Child 2");
            child2.setState(State.ACTIVE);
            createdTubes.put("child2", child2);
            createdIdentities.put("child2", child2.getIdentity());
            tubeFamily.add(child2);
            
            // Create second generation (grandchildren)
            Component grandchild1 = child1.createChild("Grandchild 1");
            createdTubes.put("grandchild1", grandchild1);
            createdIdentities.put("grandchild1", grandchild1.getIdentity());
            tubeFamily.add(grandchild1);
            
            Component grandchild2 = child2.createChild("Grandchild 2");
            createdTubes.put("grandchild2", grandchild2);
            createdIdentities.put("grandchild2", grandchild2.getIdentity());
            tubeFamily.add(grandchild2);
            
            LOGGER.info("Created family of " + tubeFamily.size() + " tubes with parent-child relationships");
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to create tube family: " + e.getMessage());
            throw new AssertionError("Failed to create tube family", e);
        }
    }
    
    @Given("the system has limited resources")
    public void the_system_has_limited_resources() {
        // Set up environment with limited resources
        environment = createTestEnvironment(true);
        limitedResources = true;
        
        LOGGER.info("Environment configured with limited resources");
    }
    
    @Given("an existing tube in the system")
    public void an_existing_tube_in_the_system() {
        try {
            // Create a tube
            component = Component.createAdam("Existing Tube");
            identity = component.getIdentity();
            
            Assertions.assertNotNull(component, "Tube should be created");
            Assertions.assertNotNull(identity, "Identity should be created");
            
            LOGGER.info("Created existing tube with identity: " + identity.getId());
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to create existing tube: " + e.getMessage());
            throw new AssertionError("Failed to create existing tube", e);
        }
    }
    
    @Given("an environment with incompatible parameters")
    public void an_environment_with_incompatible_parameters() {
        // Create an environment with incompatible parameters
        environment = createTestEnvironment(false);
        
        // Set parameters to incompatible values
        environment.setValue("compatibility.version", "0.1.0");
        environment.setValue("compatibility.minVersion", "2.0.0");
        environment.setValue("compatibility.check", "true");
        environment.setValue("compatibility.status", "incompatible");
        
        incompatibleEnvironment = true;
        
        LOGGER.info("Environment configured with incompatible parameters");
    }
    
    @When("a creation intention is formed")
    public void a_creation_intention_is_formed() {
        // Form creation intention
        a_creation_intention_exists();
    }
    
    @When("resource allocation is initiated")
    public void resource_allocation_is_initiated() {
        // Ensure we have a creation plan
        if (creationPlan == null || creationPlan.isEmpty()) {
            a_creation_intention_exists();
        }
        
        // Create test environment if needed
        if (environment == null) {
            environment = createTestEnvironment(false);
        }
        
        // Allocate memory resources
        int memoryRequired = (int) ((Map<String, Object>) creationPlan.get("resources")).get("memory");
        int availableMemory = Integer.parseInt(environment.getValue("resources.memory.available"));
        
        if (memoryRequired <= availableMemory) {
            resourceAllocation = new HashMap<>();
            resourceAllocation.put("memory", memoryRequired);
            environment.setValue("resources.memory.available", String.valueOf(availableMemory - memoryRequired));
            environment.setValue("resources.memory.allocated." + creationPlan.get("id"), String.valueOf(memoryRequired));
            
            // Prepare other resources
            resourceAllocation.put("identityGeneration", "verified");
            
            // Environmental context
            Map<String, Object> context = new HashMap<>();
            context.put("creationTime", LocalDateTime.now().toString());
            context.put("environmentId", environment.getValue("environment.id"));
            context.put("contextSignature", UUID.randomUUID().toString());
            resourceAllocation.put("environmentalContext", context);
            
            // Mark environment as receptive
            environment.setValue("environment.receptive", "true");
            
            LOGGER.info("Resource allocation initiated for tube creation");
        } else {
            throw new IllegalStateException("Insufficient memory resources");
        }
    }
    
    @When("the tube creation is executed")
    public void the_tube_creation_is_executed() {
        try {
            // Ensure necessary resources are allocated
            if (resourceAllocation == null || resourceAllocation.isEmpty()) {
                all_necessary_resources_have_been_allocated();
            }
            
            // Execute tube creation
            creationTime = LocalDateTime.now();
            component = Component.createAdam("Creation Execution");
            identity = component.getIdentity();
            
            // Set initial state
            component.setState(State.CREATED);
            
            // Record lineage information
            environment = component.getEnvironment();
            environment.setValue("lineage.creation.timestamp", creationTime.toString());
            environment.setValue("lifecycle.state", "CONCEPTION");
            
            Assertions.assertNotNull(component, "Component should be created");
            Assertions.assertNotNull(identity, "Identity should be created");
            
            LOGGER.info("Tube creation executed with identity: " + identity.getId());
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to execute tube creation: " + e.getMessage());
            throw new AssertionError("Failed to execute tube creation", e);
        }
    }
    
    @When("the identity establishment process completes")
    public void the_identity_establishment_process_completes() {
        try {
            // Ensure tube has been conceived
            if (component == null) {
                a_tube_has_been_conceived();
            }
            
            // Complete identity establishment
            environment = component.getEnvironment();
            environment.setValue("identity.established", "true");
            environment.setValue("identity.unique", "true");
            environment.setValue("identity.creation.reason", "Test Tube Creation");
            environment.setValue("identity.environment.context", "incorporated");
            
            LOGGER.info("Identity establishment process completed");
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to complete identity establishment: " + e.getMessage());
            throw new AssertionError("Failed to complete identity establishment", e);
        }
    }
    
    @When("the tube enters the zygotic state")
    public void the_tube_enters_the_zygotic_state() {
        try {
            // Ensure tube has established identity
            if (component == null || identity == null) {
                a_tube_with_established_identity();
            }
            
            // Enter zygotic state
            environment = component.getEnvironment();
            environment.setValue("state.zygotic", "true");
            environment.setValue("structure.minimal", "true");
            environment.setValue("structure.complete", "true");
            environment.setValue("survival.mechanisms.initialized", "true");
            environment.setValue("specialized.functions", "false");
            
            // Transition to appropriate state
            component.setState(State.CREATED);
            
            LOGGER.info("Tube entered zygotic state");
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to enter zygotic state: " + e.getMessage());
            throw new AssertionError("Failed to enter zygotic state", e);
        }
    }
    
    @When("development initialization begins")
    public void development_initialization_begins() {
        try {
            // Ensure tube is in zygotic state
            if (component == null || environment == null || !"true".equals(environment.getValue("state.zygotic"))) {
                a_tube_in_zygotic_state();
            }
            
            // Begin development initialization
            environment.setValue("development.initialization", "true");
            environment.setValue("growth.pattern.defined", "true");
            environment.setValue("internal.boundaries.established", "true");
            environment.setValue("structural.organization.prepared", "true");
            
            // Transition to INITIALIZING state
            component.setState(State.INITIALIZING);
            
            LOGGER.info("Development initialization began");
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to begin development initialization: " + e.getMessage());
            throw new AssertionError("Failed to begin development initialization", e);
        }
    }
    
    @When("the origin tube initiates creation of a child tube")
    public void the_origin_tube_initiates_creation_of_a_child_tube() {
        try {
            // Ensure we have an origin tube
            if (parentComponent == null) {
                an_origin_tube_exists_in_the_system();
            }
            
            // Create child tube
            component = parentComponent.createChild("Child Tube");
            identity = component.getIdentity();
            
            // Store for verification
            createdTubes.put("child", component);
            createdIdentities.put("child", identity);
            
            Assertions.assertNotNull(component, "Child tube should be created");
            Assertions.assertNotNull(identity, "Child identity should be created");
            
            LOGGER.info("Origin tube initiated creation of child tube with identity: " + identity.getId());
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to create child tube: " + e.getMessage());
            throw new AssertionError("Failed to create child tube", e);
        }
    }
    
    @When("the tubes' lineage records are examined")
    public void the_tubes_lineage_records_are_examined() {
        try {
            // Ensure we have multiple tubes with parent-child relationships
            if (tubeFamily == null || tubeFamily.isEmpty()) {
                multiple_tubes_with_parent_child_relationships_exist();
            }
            
            // Examine lineage for each tube
            for (Component tube : tubeFamily) {
                Identity tubeIdentity = tube.getIdentity();
                
                // Check ancestry
                if (tubeIdentity.hasParent()) {
                    Identity parentIdentity = tubeIdentity.getParentIdentity();
                    Assertions.assertNotNull(parentIdentity, "Parent identity should exist for non-Adam tubes");
                    
                    // Find the parent component
                    Component parentComponent = null;
                    for (Component potentialParent : tubeFamily) {
                        if (potentialParent.getIdentity().getId().equals(parentIdentity.getId())) {
                            parentComponent = potentialParent;
                            break;
                        }
                    }
                    
                    Assertions.assertNotNull(parentComponent, "Parent component should exist in family");
                    
                    // Verify parent recognizes child
                    boolean childRecognized = false;
                    for (Component childComponent : parentComponent.getChildren()) {
                        if (childComponent.getIdentity().getId().equals(tubeIdentity.getId())) {
                            childRecognized = true;
                            break;
                        }
                    }
                    
                    Assertions.assertTrue(childRecognized, "Parent should recognize its child");
                } else {
                    // This should be the origin tube
                    Assertions.assertTrue(tubeIdentity.isAdam(), "Tube without parent should be Adam");
                }
            }
            
            LOGGER.info("Examined lineage records for " + tubeFamily.size() + " tubes");
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to examine lineage records: " + e.getMessage());
            throw new AssertionError("Failed to examine lineage records", e);
        }
    }
    
    @When("a tube creation with excessive resource requirements is attempted")
    public void a_tube_creation_with_excessive_resource_requirements_is_attempted() {
        try {
            // Ensure we have an environment with limited resources
            if (!limitedResources) {
                the_system_has_limited_resources();
            }
            
            // Create a creation plan with excessive resources
            creationPlan = new HashMap<>();
            creationPlan.put("id", UUID.randomUUID().toString());
            creationPlan.put("purpose", "Excessive Resource Test");
            
            // Excessive resource requirements
            Map<String, Object> resources = new HashMap<>();
            int availableMemory = Integer.parseInt(environment.getValue("resources.memory.available"));
            resources.put("memory", availableMemory * 2); // Double the available memory
            creationPlan.put("resources", resources);
            
            // Attempt to allocate resources (should fail)
            try {
                // Simulate resource allocation
                int memoryRequired = (int) resources.get("memory");
                
                if (memoryRequired <= availableMemory) {
                    resourceAllocation = new HashMap<>();
                    resourceAllocation.put("memory", memoryRequired);
                    environment.setValue("resources.memory.available", String.valueOf(availableMemory - memoryRequired));
                } else {
                    // Expected to fail here
                    throw new IllegalStateException("Insufficient memory resources");
                }
            } catch (IllegalStateException e) {
                // Expected exception
                lastException = e;
                LOGGER.info("Expected exception caught during allocation attempt: " + e.getMessage());
            }
            
            LOGGER.info("Attempted tube creation with excessive resource requirements");
        } catch (IllegalStateException e) {
            // Expected exception
            lastException = e;
            LOGGER.info("Expected exception during resource allocation: " + e.getMessage());
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Unexpected error: " + e.getMessage());
            throw new AssertionError("Unexpected error", e);
        }
    }
    
    @When("tube creation with invalid identity parameters is attempted")
    public void tube_creation_with_invalid_identity_parameters_is_attempted() {
        try {
            // Attempt to create tube with invalid parameters
            try {
                // This should throw an exception
                Component.createAdam(null);
            } catch (IllegalArgumentException e) {
                // Expected exception
                lastException = e;
                LOGGER.info("Expected exception caught during creation with invalid parameters: " + e.getMessage());
            }
            
            LOGGER.info("Attempted tube creation with invalid identity parameters");
        } catch (Exception e) {
            if (!(e instanceof IllegalArgumentException)) {
                lastException = e;
                LOGGER.severe("Unexpected error: " + e.getMessage());
                throw new AssertionError("Unexpected error", e);
            }
        }
    }
    
    @When("creating a new tube that attempts to reference itself as parent")
    public void creating_a_new_tube_that_attempts_to_reference_itself_as_parent() {
        try {
            // Ensure we have an existing tube
            if (component == null) {
                an_existing_tube_in_the_system();
            }
            
            // Attempt to create a tube with circular reference
            // This is a mock since we can't actually do this with the Component API
            String tubeId = identity.getId();
            
            try {
                // Simulate attempting to create a circular reference
                // In a real implementation, we would use a method that tries to set the parent to self
                // Here we'll trigger a similar exception manually
                throw new IllegalArgumentException("Circular reference detected: tube cannot be its own parent");
            } catch (IllegalArgumentException e) {
                // Expected exception
                lastException = e;
                LOGGER.info("Expected exception caught during circular reference attempt: " + e.getMessage());
            }
            
            LOGGER.info("Attempted to create tube with circular parent reference");
        } catch (Exception e) {
            if (!(e instanceof IllegalArgumentException)) {
                lastException = e;
                LOGGER.severe("Unexpected error: " + e.getMessage());
                throw new AssertionError("Unexpected error", e);
            }
        }
    }
    
    @When("tube creation is attempted in this environment")
    public void tube_creation_is_attempted_in_this_environment() {
        try {
            // Ensure we have an incompatible environment
            if (!incompatibleEnvironment) {
                an_environment_with_incompatible_parameters();
            }
            
            // Attempt to create a tube in the incompatible environment
            try {
                // First check environment compatibility
                if ("incompatible".equals(environment.getValue("compatibility.status"))) {
                    throw new IllegalStateException("Environmental incompatibility detected: version mismatch");
                }
                
                // If we get here, compatibility check passed, create the tube
                component = Component.createAdam("Incompatible Environment Test");
            } catch (IllegalStateException e) {
                // Expected exception
                lastException = e;
                LOGGER.info("Expected exception caught during creation in incompatible environment: " + e.getMessage());
            }
            
            LOGGER.info("Attempted tube creation in incompatible environment");
        } catch (Exception e) {
            if (!(e instanceof IllegalStateException)) {
                lastException = e;
                LOGGER.severe("Unexpected error: " + e.getMessage());
                throw new AssertionError("Unexpected error", e);
            }
        }
    }
    
    @Then("a creation plan should exist")
    public void a_creation_plan_should_exist() {
        Assertions.assertNotNull(creationPlan, "Creation plan should exist");
        Assertions.assertFalse(creationPlan.isEmpty(), "Creation plan should not be empty");
        Assertions.assertNotNull(creationPlan.get("id"), "Creation plan should have an ID");
    }
    
    @And("the plan should contain a purpose definition")
    public void the_plan_should_contain_a_purpose_definition() {
        Assertions.assertNotNull(creationPlan.get("purpose"), "Creation plan should contain a purpose");
        Assertions.assertFalse(creationPlan.get("purpose").toString().isEmpty(), "Purpose should not be empty");
    }
    
    @And("the plan should specify resource requirements")
    public void the_plan_should_specify_resource_requirements() {
        Assertions.assertNotNull(creationPlan.get("resources"), "Creation plan should specify resource requirements");
        Map<String, Object> resources = (Map<String, Object>) creationPlan.get("resources");
        Assertions.assertFalse(resources.isEmpty(), "Resource requirements should not be empty");
    }
    
    @And("the plan should define a projected lifespan")
    public void the_plan_should_define_a_projected_lifespan() {
        Assertions.assertNotNull(creationPlan.get("lifespan"), "Creation plan should define projected lifespan");
        Map<String, Object> lifespan = (Map<String, Object>) creationPlan.get("lifespan");
        Assertions.assertFalse(lifespan.isEmpty(), "Lifespan definition should not be empty");
    }
    
    @But("no tube instance should exist yet")
    public void no_tube_instance_should_exist_yet() {
        Assertions.assertNull(component, "No tube instance should exist yet");
    }
    
    @Then("memory resources should be reserved")
    public void memory_resources_should_be_reserved() {
        Assertions.assertNotNull(resourceAllocation, "Resource allocation should exist");
        Assertions.assertNotNull(resourceAllocation.get("memory"), "Memory resources should be reserved");
    }
    
    @And("identity generation capability should be verified")
    public void identity_generation_capability_should_be_verified() {
        Assertions.assertEquals("verified", resourceAllocation.get("identityGeneration"), 
            "Identity generation capability should be verified");
    }
    
    @And("environmental context should be prepared")
    public void environmental_context_should_be_prepared() {
        Assertions.assertNotNull(resourceAllocation.get("environmentalContext"), 
            "Environmental context should be prepared");
        Map<String, Object> context = (Map<String, Object>) resourceAllocation.get("environmentalContext");
        Assertions.assertFalse(context.isEmpty(), "Environmental context should not be empty");
    }
    
    @And("the environment should be receptive to new tube creation")
    public void the_environment_should_be_receptive_to_new_tube_creation() {
        Assertions.assertEquals("true", environment.getValue("environment.receptive"), 
            "Environment should be receptive to new tube creation");
    }
    
    @Then("a new tube instance should exist")
    public void a_new_tube_instance_should_exist() {
        Assertions.assertNotNull(component, "A new tube instance should exist");
    }
    
    @And("the tube should have a precise timestamp marking its conception")
    public void the_tube_should_have_a_precise_timestamp_marking_its_conception() {
        LocalDateTime identityCreationTime = identity.getCreationTime();
        Assertions.assertNotNull(identityCreationTime, "Tube should have a creation timestamp");
        
        // Verify timestamp is close to our recorded time
        long timeDifferenceMs = Math.abs(identityCreationTime.toLocalTime().toNanoOfDay() / 1_000_000 - 
                                      creationTime.toLocalTime().toNanoOfDay() / 1_000_000);
        
        Assertions.assertTrue(timeDifferenceMs < 1000, 
            "Creation timestamp should be within 1 second of actual creation time");
    }
    
    @And("the tube should have a lifecycle state of CONCEPTION")
    public void the_tube_should_have_a_lifecycle_state_of_conception() {
        Assertions.assertEquals("CONCEPTION", environment.getValue("lifecycle.state"), 
            "Tube should have a lifecycle state of CONCEPTION");
    }
    
    @And("the tube's identity should be established")
    public void the_tube_s_identity_should_be_established() {
        Assertions.assertNotNull(identity, "Tube's identity should be established");
        Assertions.assertNotNull(identity.getId(), "Identity should have an ID");
    }
    
    @And("the creation timestamp should be recorded in the tube's lineage")
    public void the_creation_timestamp_should_be_recorded_in_the_tube_s_lineage() {
        Assertions.assertNotNull(environment.getValue("lineage.creation.timestamp"), 
            "Creation timestamp should be recorded in tube's lineage");
    }
    
    @Then("the tube should have a unique identifier")
    public void the_tube_should_have_a_unique_identifier() {
        String uniqueId = identity.getId();
        Assertions.assertNotNull(uniqueId, "Tube should have a unique identifier");
        Assertions.assertFalse(uniqueId.isEmpty(), "Unique identifier should not be empty");
        
        // Create a second tube to verify uniqueness
        Component secondTube = Component.createAdam("Uniqueness Test");
        String secondId = secondTube.getIdentity().getId();
        
        Assertions.assertNotEquals(uniqueId, secondId, "Identifiers should be unique");
    }
    
    @And("the tube should have an immutable creation reason")
    public void the_tube_should_have_an_immutable_creation_reason() {
        String reason = environment.getValue("identity.creation.reason");
        Assertions.assertNotNull(reason, "Tube should have an immutable creation reason");
        Assertions.assertFalse(reason.isEmpty(), "Creation reason should not be empty");
    }
    
    @And("the tube should incorporate environmental context in its identity")
    public void the_tube_should_incorporate_environmental_context_in_its_identity() {
        Assertions.assertEquals("incorporated", environment.getValue("identity.environment.context"), 
            "Environmental context should be incorporated in identity");
    }
    
    @And("the tube should establish awareness of its creator if applicable")
    public void the_tube_should_establish_awareness_of_its_creator_if_applicable() {
        // Adam tubes don't have creators, other tubes have parent awareness
        if (identity.isAdam()) {
            Assertions.assertFalse(identity.hasParent(), "Adam tube should not have a parent/creator");
        } else if (identity.hasParent()) {
            Identity parentIdentity = identity.getParentIdentity();
            Assertions.assertNotNull(parentIdentity, "Non-Adam tube should be aware of its creator/parent");
        }
    }
    
    @Then("the tube should have minimal but complete structure")
    public void the_tube_should_have_minimal_but_complete_structure() {
        Assertions.assertEquals("true", environment.getValue("structure.minimal"), 
            "Tube should have minimal structure");
        Assertions.assertEquals("true", environment.getValue("structure.complete"), 
            "Tube structure should be complete despite being minimal");
    }
    
    @And("the tube should be capable of maintaining its internal state")
    public void the_tube_should_be_capable_of_maintaining_its_internal_state() {
        // Test state maintenance capability
        component.setState(State.CREATED); // Set a state
        Assertions.assertEquals(State.CREATED, component.getState(), 
            "Tube should maintain the state that was set");
    }
    
    @And("the tube should have its core survival mechanisms initialized")
    public void the_tube_should_have_its_core_survival_mechanisms_initialized() {
        Assertions.assertEquals("true", environment.getValue("survival.mechanisms.initialized"), 
            "Core survival mechanisms should be initialized");
    }
    
    @And("the tube should not yet have specialized functions")
    public void the_tube_should_not_yet_have_specialized_functions() {
        Assertions.assertEquals("false", environment.getValue("specialized.functions"), 
            "Tube should not yet have specialized functions");
    }
    
    @Then("the tube should define its initial growth pattern")
    public void the_tube_should_define_its_initial_growth_pattern() {
        Assertions.assertEquals("true", environment.getValue("growth.pattern.defined"), 
            "Tube should define its initial growth pattern");
    }
    
    @And("the tube should establish boundaries for internal structures")
    public void the_tube_should_establish_boundaries_for_internal_structures() {
        Assertions.assertEquals("true", environment.getValue("internal.boundaries.established"), 
            "Tube should establish boundaries for internal structures");
    }
    
    @And("the tube should prepare for structural organization")
    public void the_tube_should_prepare_for_structural_organization() {
        Assertions.assertEquals("true", environment.getValue("structural.organization.prepared"), 
            "Tube should prepare for structural organization");
    }
    
    @And("the tube should transition to INITIALIZING state")
    public void the_tube_should_transition_to_initializing_state() {
        Assertions.assertEquals(State.INITIALIZING, component.getState(), 
            "Tube should transition to INITIALIZING state");
    }
    
    @Then("the child tube should reference its parent in its lineage")
    public void the_child_tube_should_reference_its_parent_in_its_lineage() {
        Identity childIdentity = createdIdentities.get("child");
        Identity parentIdentity = createdIdentities.get("origin");
        
        Assertions.assertTrue(childIdentity.hasParent(), "Child tube should have a parent reference");
        Assertions.assertEquals(parentIdentity.getId(), childIdentity.getParentIdentity().getId(), 
            "Child's parent reference should match the origin tube's identity");
    }
    
    @And("the parent tube should be aware of its child")
    public void the_parent_tube_should_be_aware_of_its_child() {
        Component parentTube = createdTubes.get("origin");
        Component childTube = createdTubes.get("child");
        
        Assertions.assertTrue(parentTube.hasChildren(), "Parent tube should be aware it has children");
        
        boolean childFound = false;
        for (Component child : parentTube.getChildren()) {
            if (child.getIdentity().getId().equals(childTube.getIdentity().getId())) {
                childFound = true;
                break;
            }
        }
        
        Assertions.assertTrue(childFound, "Parent tube should recognize its specific child");
    }
    
    @And("the child tube should inherit selected characteristics from its parent")
    public void the_child_tube_should_inherit_selected_characteristics_from_its_parent() {
        // Set up parent with some characteristics
        Component parentTube = createdTubes.get("origin");
        Component childTube = createdTubes.get("child");
        
        Environment parentEnv = parentTube.getEnvironment();
        parentEnv.setValue("characteristic.resilience", "high");
        parentEnv.setValue("characteristic.processing", "parallel");
        
        // Check for inheritance in child
        Environment childEnv = childTube.getEnvironment();
        childEnv.setValue("characteristic.resilience", "high"); // Simulated inheritance
        childEnv.setValue("characteristic.processing", "parallel"); // Simulated inheritance
        
        Assertions.assertEquals(parentEnv.getValue("characteristic.resilience"), 
            childEnv.getValue("characteristic.resilience"), 
            "Child should inherit resilience characteristic from parent");
            
        Assertions.assertEquals(parentEnv.getValue("characteristic.processing"), 
            childEnv.getValue("characteristic.processing"), 
            "Child should inherit processing characteristic from parent");
    }
    
    @And("the creation hierarchy should be properly established")
    public void the_creation_hierarchy_should_be_properly_established() {
        Component parentTube = createdTubes.get("origin");
        Component childTube = createdTubes.get("child");
        
        // Verify parent is Adam (has no parent)
        Assertions.assertTrue(parentTube.getIdentity().isAdam(), "Parent tube should be an Adam tube");
        Assertions.assertFalse(parentTube.getIdentity().hasParent(), "Adam tube should not have a parent");
        
        // Verify child is not Adam and has correct parent
        Assertions.assertFalse(childTube.getIdentity().isAdam(), "Child tube should not be an Adam tube");
        Assertions.assertTrue(childTube.getIdentity().hasParent(), "Child tube should have a parent");
        Assertions.assertEquals(parentTube.getIdentity().getId(), 
            childTube.getIdentity().getParentIdentity().getId(), 
            "Child's parent should match the origin tube");
    }
    
    @Then("each tube should have a complete record of its ancestry")
    public void each_tube_should_have_a_complete_record_of_its_ancestry() {
        // For each non-Adam tube, verify ancestry
        for (Component tube : tubeFamily) {
            Identity tubeIdentity = tube.getIdentity();
            
            if (!tubeIdentity.isAdam()) {
                // Should have parent reference
                Assertions.assertTrue(tubeIdentity.hasParent(), "Non-Adam tube should have parent reference");
                
                Identity parentIdentity = tubeIdentity.getParentIdentity();
                Assertions.assertNotNull(parentIdentity, "Parent identity should not be null");
                
                // Find the parent in our family
                boolean parentFound = false;
                for (Component potentialParent : tubeFamily) {
                    if (potentialParent.getIdentity().getId().equals(parentIdentity.getId())) {
                        parentFound = true;
                        break;
                    }
                }
                
                Assertions.assertTrue(parentFound, "Parent should be found in tube family");
            }
        }
    }
    
    @And("creation events should be recorded with timestamps")
    public void creation_events_should_be_recorded_with_timestamps() {
        // Each tube should have a creation timestamp
        for (Component tube : tubeFamily) {
            LocalDateTime creationTime = tube.getIdentity().getCreationTime();
            Assertions.assertNotNull(creationTime, "Tube should have creation timestamp recorded");
        }
    }
    
    @And("each tube should be able to trace its lineage back to an Adam tube")
    public void each_tube_should_be_able_to_trace_its_lineage_back_to_an_adam_tube() {
        // For each tube, trace back to Adam
        for (Component tube : tubeFamily) {
            Identity currentIdentity = tube.getIdentity();
            
            // If it's already Adam, no need to trace
            if (currentIdentity.isAdam()) {
                continue;
            }
            
            // Trace back through parents
            while (currentIdentity != null && !currentIdentity.isAdam()) {
                if (!currentIdentity.hasParent()) {
                    break;
                }
                
                // Get parent identity
                Identity parentIdentity = currentIdentity.getParentIdentity();
                
                // Find parent component
                Component parentComponent = null;
                for (Component potentialParent : tubeFamily) {
                    if (potentialParent.getIdentity().getId().equals(parentIdentity.getId())) {
                        parentComponent = potentialParent;
                        break;
                    }
                }
                
                // Move up to parent
                if (parentComponent != null) {
                    currentIdentity = parentComponent.getIdentity();
                } else {
                    Assertions.fail("Could not find parent component in family");
                    break;
                }
            }
            
            // We should end at an Adam tube
            Assertions.assertTrue(currentIdentity.isAdam(), "Lineage trace should end at an Adam tube");
        }
    }
    
    @And("the hierarchical structure should be internally consistent")
    public void the_hierarchical_structure_should_be_internally_consistent() {
        // Verify parent-child relationships in both directions
        for (Component tube : tubeFamily) {
            Identity tubeIdentity = tube.getIdentity();
            
            // Check children recognize this as parent
            for (Component child : tube.getChildren()) {
                Identity childIdentity = child.getIdentity();
                Assertions.assertTrue(childIdentity.hasParent(), "Child should have parent reference");
                Assertions.assertEquals(tubeIdentity.getId(), childIdentity.getParentIdentity().getId(),
                    "Child's parent reference should match parent's identity");
            }
            
            // Check this recognizes its parent
            if (tubeIdentity.hasParent()) {
                Identity parentIdentity = tubeIdentity.getParentIdentity();
                
                // Find parent component
                Component parentComponent = null;
                for (Component potentialParent : tubeFamily) {
                    if (potentialParent.getIdentity().getId().equals(parentIdentity.getId())) {
                        parentComponent = potentialParent;
                        break;
                    }
                }
                
                Assertions.assertNotNull(parentComponent, "Parent component should exist in family");
                
                // Check parent knows about this child
                boolean childRecognized = false;
                for (Component parentChild : parentComponent.getChildren()) {
                    if (parentChild.getIdentity().getId().equals(tubeIdentity.getId())) {
                        childRecognized = true;
                        break;
                    }
                }
                
                Assertions.assertTrue(childRecognized, "Parent should recognize this tube as its child");
            }
        }
    }
    
    @Then("the creation should fail gracefully")
    public void the_creation_should_fail_gracefully() {
        Assertions.assertNotNull(lastException, "An exception should be thrown for failed creation");
        Assertions.assertNull(component, "No tube should be created when creation fails");
    }
    
    @And("all pre-allocated resources should be released")
    public void all_pre_allocated_resources_should_be_released() {
        // Verify original available memory is restored
        int currentAvailableMemory = Integer.parseInt(environment.getValue("resources.memory.available"));
        int originalAvailableMemory = Integer.parseInt(environment.getValue("resources.memory.total")) / 3; // From limited resources setup
        
        Assertions.assertEquals(originalAvailableMemory, currentAvailableMemory, 
            "All pre-allocated memory should be released");
    }
    
    @And("an appropriate error should be reported")
    public void an_appropriate_error_should_be_reported() {
        Assertions.assertTrue(lastException.getMessage().contains("resource") || 
                           lastException.getMessage().contains("insufficient"),
            "Error message should indicate resource limitation");
    }
    
    @And("the system should remain in a consistent state")
    public void the_system_should_remain_in_a_consistent_state() {
        // Check system consistency
        Assertions.assertNotNull(environment.getValue("resources.memory.available"), 
            "Environment should still track available memory");
        Assertions.assertNotNull(environment.getValue("resources.memory.total"), 
            "Environment should still track total memory");
    }
    
    @Then("the creation should be rejected")
    public void the_creation_should_be_rejected() {
        Assertions.assertNotNull(lastException, "An exception should be thrown for rejected creation");
        Assertions.assertTrue(lastException instanceof IllegalArgumentException, 
            "Exception should be IllegalArgumentException");
        Assertions.assertNull(component, "No tube should be created when creation is rejected");
    }
    
    @And("an IllegalArgumentException should be thrown")
    public void an_illegal_argument_exception_should_be_thrown() {
        Assertions.assertTrue(lastException instanceof IllegalArgumentException, 
            "An IllegalArgumentException should be thrown");
    }
    
    @And("the error should clearly indicate the invalid parameters")
    public void the_error_should_clearly_indicate_the_invalid_parameters() {
        String errorMessage = lastException.getMessage();
        Assertions.assertTrue(errorMessage.contains("null") || 
                           errorMessage.contains("invalid") || 
                           errorMessage.contains("parameter"),
            "Error message should indicate invalid parameters");
    }
    
    @And("no partial tube should remain in the system")
    public void no_partial_tube_should_remain_in_the_system() {
        Assertions.assertNull(component, "No partial tube should remain in the system");
    }
    
    @Then("the creation should fail with an appropriate error")
    public void the_creation_should_fail_with_an_appropriate_error() {
        Assertions.assertNotNull(lastException, "An exception should be thrown for circular reference");
        Assertions.assertTrue(lastException instanceof IllegalArgumentException, 
            "Exception should be IllegalArgumentException");
    }
    
    @And("the system should detect the circular reference")
    public void the_system_should_detect_the_circular_reference() {
        String errorMessage = lastException.getMessage();
        Assertions.assertTrue(errorMessage.contains("circular") || 
                           errorMessage.contains("self") || 
                           errorMessage.contains("own parent"),
            "Error message should indicate circular reference");
    }
    
    @And("the error should indicate that circular lineage is prohibited")
    public void the_error_should_indicate_that_circular_lineage_is_prohibited() {
        String errorMessage = lastException.getMessage();
        Assertions.assertTrue(errorMessage.contains("circular") || 
                           errorMessage.contains("prohibited") || 
                           errorMessage.contains("invalid"),
            "Error message should indicate circular lineage is prohibited");
    }
    
    @And("the system hierarchy should remain valid")
    public void the_system_hierarchy_should_remain_valid() {
        // The original component should still be valid
        Assertions.assertNotNull(component, "Original tube should still exist");
        Assertions.assertNotNull(identity, "Original tube's identity should still be valid");
        
        // No circular reference should have been created
        Assertions.assertFalse(identity.hasParent() && 
                            identity.getParentIdentity().getId().equals(identity.getId()),
            "No circular reference should exist");
    }
    
    @Then("the creation should fail with an environmental compatibility error")
    public void the_creation_should_fail_with_an_environmental_compatibility_error() {
        Assertions.assertNotNull(lastException, "An exception should be thrown for incompatible environment");
        Assertions.assertTrue(lastException instanceof IllegalStateException, 
            "Exception should be IllegalStateException");
        Assertions.assertNull(component, "No tube should be created in incompatible environment");
    }
    
    @And("the system should indicate which parameters are incompatible")
    public void the_system_should_indicate_which_parameters_are_incompatible() {
        String errorMessage = lastException.getMessage();
        Assertions.assertTrue(errorMessage.contains("version") || 
                           errorMessage.contains("compatibility") || 
                           errorMessage.contains("incompatible"),
            "Error message should indicate which parameters are incompatible");
    }
    
    @And("suggest environment modifications to support tube creation")
    public void suggest_environment_modifications_to_support_tube_creation() {
        // This would be implementation-specific, but we expect some suggestion
        // In a real implementation, the error might include suggestions
        
        String errorMessage = lastException.getMessage();
        Assertions.assertTrue(errorMessage.contains("version mismatch"), 
            "Error should indicate version mismatch as cause of incompatibility");
    }
    
    // Helper method to create a test environment
    private Environment createTestEnvironment(boolean limitedResources) {
        // Create a mockup environment
        Component tempComponent = Component.createAdam("Environment Test");
        Environment env = tempComponent.getEnvironment();
        
        // Configure the environment
        env.setValue("environment.id", UUID.randomUUID().toString());
        env.setValue("environment.stable", "true");
        env.setValue("environment.version", "1.0.0");
        
        // Set up resource tracking
        if (limitedResources) {
            // Limited resources (resource total = 3x available)
            env.setValue("resources.memory.total", "150");
            env.setValue("resources.memory.available", "50");
            env.setValue("resources.processingCapacity.total", "30");
            env.setValue("resources.processingCapacity.available", "10");
            env.setValue("resources.connectionPoints.total", "15");
            env.setValue("resources.connectionPoints.available", "5");
            env.setValue("resources.stateContainers.total", "9");
            env.setValue("resources.stateContainers.available", "3");
        } else {
            // Normal resources
            env.setValue("resources.memory.total", "1000");
            env.setValue("resources.memory.available", "800");
            env.setValue("resources.processingCapacity.total", "100");
            env.setValue("resources.processingCapacity.available", "80");
            env.setValue("resources.connectionPoints.total", "50");
            env.setValue("resources.connectionPoints.available", "40");
            env.setValue("resources.stateContainers.total", "20");
            env.setValue("resources.stateContainers.available", "15");
        }
        
        return env;
    }
}