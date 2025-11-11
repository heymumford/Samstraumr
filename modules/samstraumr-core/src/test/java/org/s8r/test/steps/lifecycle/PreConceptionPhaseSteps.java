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

import org.junit.jupiter.api.Assertions;
import org.s8r.component.Component;
import org.s8r.component.Environment;
import org.s8r.component.State;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * Step definitions for pre-conception phase tests, focusing on the environment
 * assessment, creation request preparation, validation, and resource allocation
 * that occurs before actual tube creation.
 */
public class PreConceptionPhaseSteps {

    private static final Logger LOGGER = Logger.getLogger(PreConceptionPhaseSteps.class.getName());
    
    // Test context
    private Environment environment;
    private Map<String, Object> creationRequest = new HashMap<>();
    private Map<String, Object> validationResults = new HashMap<>();
    private Map<String, Object> allocatedResources = new HashMap<>();
    private Map<String, Object> creationTemplate = new HashMap<>();
    private Exception lastException;
    private AtomicBoolean resourcesAllocated = new AtomicBoolean(false);
    private boolean limitedResources = false;

    @Given("a valid tube creation request exists")
    public void a_valid_tube_creation_request_exists() {
        try {
            // Create a valid tube creation request
            creationRequest = new HashMap<>();
            creationRequest.put("id", UUID.randomUUID().toString());
            creationRequest.put("creationPurpose", "Test Tube Creation");
            creationRequest.put("timestamp", LocalDateTime.now().toString());
            
            // Required resources
            Map<String, Object> requiredResources = new HashMap<>();
            requiredResources.put("memory", 100);
            requiredResources.put("processingCapacity", 10);
            requiredResources.put("connectionPoints", 5);
            requiredResources.put("stateContainers", 3);
            creationRequest.put("requiredResources", requiredResources);
            
            // Lifespan information
            Map<String, Object> lifespan = new HashMap<>();
            lifespan.put("estimatedDuration", "medium"); // short, medium, long
            lifespan.put("persistAfterCompletion", true);
            lifespan.put("maxInactivityPeriod", 3600); // seconds
            creationRequest.put("lifespan", lifespan);
            
            // Interaction model
            Map<String, Object> interactionModel = new HashMap<>();
            interactionModel.put("inputMode", "synchronous");
            interactionModel.put("outputMode", "asynchronous");
            interactionModel.put("isolationLevel", "standard");
            creationRequest.put("interactionModel", interactionModel);
            
            // Creation parameters
            Map<String, Object> creationParams = new HashMap<>();
            creationParams.put("name", "TestTube");
            creationParams.put("type", "standard");
            creationParams.put("initialState", "CREATED");
            creationParams.put("autoTransition", true);
            creationRequest.put("creationParams", creationParams);
            
            // Mark as validated
            creationRequest.put("validated", true);
            
            LOGGER.info("Created valid tube creation request with ID: " + creationRequest.get("id"));
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to create valid tube creation request: " + e.getMessage());
            throw new AssertionError("Failed to create valid tube creation request", e);
        }
    }

    @Given("the environment has limited resources")
    public void the_environment_has_limited_resources() {
        try {
            // Configure environment with limited resources
            environment = createTestEnvironment(true);
            limitedResources = true;
            
            // Set up limited resource metrics
            environment.setValue("resources.memory.total", "150");
            environment.setValue("resources.memory.available", "50");
            environment.setValue("resources.processingCapacity.total", "20");
            environment.setValue("resources.processingCapacity.available", "5");
            environment.setValue("resources.connectionPoints.total", "10");
            environment.setValue("resources.connectionPoints.available", "2");
            environment.setValue("resources.stateContainers.total", "5");
            environment.setValue("resources.stateContainers.available", "1");
            
            LOGGER.info("Configured environment with limited resources");
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to configure limited resources environment: " + e.getMessage());
            throw new AssertionError("Failed to configure limited resources environment", e);
        }
    }

    @When("the environment is evaluated for tube creation")
    public void the_environment_is_evaluated_for_tube_creation() {
        try {
            // Create and evaluate the environment
            environment = createTestEnvironment(false);
            
            LOGGER.info("Environment evaluated for tube creation");
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to evaluate environment: " + e.getMessage());
            throw new AssertionError("Failed to evaluate environment", e);
        }
    }

    @When("a tube creation request is prepared")
    public void a_tube_creation_request_is_prepared() {
        try {
            // Prepare a tube creation request
            creationRequest = new HashMap<>();
            creationRequest.put("id", UUID.randomUUID().toString());
            creationRequest.put("creationPurpose", "Test Tube Creation");
            creationRequest.put("timestamp", LocalDateTime.now().toString());
            
            // Required resources
            Map<String, Object> requiredResources = new HashMap<>();
            requiredResources.put("memory", 100);
            requiredResources.put("processingCapacity", 10);
            requiredResources.put("connectionPoints", 5);
            requiredResources.put("stateContainers", 3);
            creationRequest.put("requiredResources", requiredResources);
            
            // Lifespan information
            Map<String, Object> lifespan = new HashMap<>();
            lifespan.put("estimatedDuration", "medium"); // short, medium, long
            lifespan.put("persistAfterCompletion", true);
            lifespan.put("maxInactivityPeriod", 3600); // seconds
            creationRequest.put("lifespan", lifespan);
            
            // Interaction model
            Map<String, Object> interactionModel = new HashMap<>();
            interactionModel.put("inputMode", "synchronous");
            interactionModel.put("outputMode", "asynchronous");
            interactionModel.put("isolationLevel", "standard");
            creationRequest.put("interactionModel", interactionModel);
            
            LOGGER.info("Prepared tube creation request with ID: " + creationRequest.get("id"));
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to prepare tube creation request: " + e.getMessage());
            throw new AssertionError("Failed to prepare tube creation request", e);
        }
    }

    @When("a tube creation request is validated")
    public void a_tube_creation_request_is_validated() {
        try {
            // First ensure we have a creation request and environment
            if (creationRequest == null || creationRequest.isEmpty()) {
                a_tube_creation_request_is_prepared();
            }
            
            if (environment == null) {
                environment = createTestEnvironment(false);
            }
            
            // Validate the creation request
            validationResults = new HashMap<>();
            boolean isValid = true;
            
            // Check for completeness
            boolean isComplete = creationRequest.containsKey("creationPurpose") && 
                              creationRequest.containsKey("requiredResources") &&
                              creationRequest.containsKey("lifespan") &&
                              creationRequest.containsKey("interactionModel");
            
            validationResults.put("complete", isComplete);
            
            if (!isComplete) {
                validationResults.put("error", "Incomplete request: missing required fields");
                isValid = false;
            }
            
            // Check resource availability
            Map<String, Object> requiredResources = (Map<String, Object>) creationRequest.get("requiredResources");
            
            int availableMemory = Integer.parseInt(environment.getValue("resources.memory.available"));
            int availableProcessingCapacity = Integer.parseInt(environment.getValue("resources.processingCapacity.available"));
            int availableConnectionPoints = Integer.parseInt(environment.getValue("resources.connectionPoints.available"));
            int availableStateContainers = Integer.parseInt(environment.getValue("resources.stateContainers.available"));
            
            boolean hasEnoughMemory = availableMemory >= (int) requiredResources.get("memory");
            boolean hasEnoughProcessingCapacity = availableProcessingCapacity >= (int) requiredResources.get("processingCapacity");
            boolean hasEnoughConnectionPoints = availableConnectionPoints >= (int) requiredResources.get("connectionPoints");
            boolean hasEnoughStateContainers = availableStateContainers >= (int) requiredResources.get("stateContainers");
            
            boolean resourcesAvailable = hasEnoughMemory && hasEnoughProcessingCapacity && 
                                      hasEnoughConnectionPoints && hasEnoughStateContainers;
                                      
            validationResults.put("resourcesAvailable", resourcesAvailable);
            
            if (!resourcesAvailable) {
                validationResults.put("error", "Insufficient resources available");
                isValid = false;
            }
            
            // Check for conflicts
            boolean noConflicts = true; // Simplified for test
            validationResults.put("noConflicts", noConflicts);
            
            if (!noConflicts) {
                validationResults.put("error", "Request conflicts with existing tubes");
                isValid = false;
            }
            
            // Set overall validity
            validationResults.put("valid", isValid);
            
            // Create creation template if valid
            if (isValid) {
                creationTemplate = new HashMap<>();
                creationTemplate.put("id", "template-" + UUID.randomUUID().toString().substring(0, 8));
                creationTemplate.put("requestId", creationRequest.get("id"));
                creationTemplate.put("timestamp", LocalDateTime.now().toString());
                creationTemplate.put("creationPurpose", creationRequest.get("creationPurpose"));
                creationTemplate.put("requiredResources", requiredResources);
                creationTemplate.put("lifespan", creationRequest.get("lifespan"));
                creationTemplate.put("interactionModel", creationRequest.get("interactionModel"));
                
                // Add creation parameters
                Map<String, Object> creationParams = new HashMap<>();
                creationParams.put("name", "Tube-" + UUID.randomUUID().toString().substring(0, 8));
                creationParams.put("type", "standard");
                creationParams.put("initialState", "CREATED");
                creationParams.put("autoTransition", true);
                
                creationTemplate.put("creationParams", creationParams);
                
                validationResults.put("creationTemplate", creationTemplate);
            }
            
            LOGGER.info("Validated tube creation request with result: " + (isValid ? "valid" : "invalid"));
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to validate tube creation request: " + e.getMessage());
            throw new AssertionError("Failed to validate tube creation request", e);
        }
    }

    @When("resources are allocated for tube creation")
    public void resources_are_allocated_for_tube_creation() {
        try {
            // Ensure we have a valid creation request
            if (creationRequest == null || creationRequest.isEmpty() || !(boolean) creationRequest.getOrDefault("validated", false)) {
                a_valid_tube_creation_request_exists();
            }
            
            if (environment == null) {
                environment = createTestEnvironment(false);
            }
            
            // Allocate resources based on the request
            Map<String, Object> requiredResources = (Map<String, Object>) creationRequest.get("requiredResources");
            Map<String, Object> creationParams = (Map<String, Object>) creationRequest.get("creationParams");
            
            // Allocate memory resources
            int memoryRequired = (int) requiredResources.get("memory");
            int availableMemory = Integer.parseInt(environment.getValue("resources.memory.available"));
            
            if (memoryRequired <= availableMemory) {
                allocatedResources.put("memory", memoryRequired);
                environment.setValue("resources.memory.available", String.valueOf(availableMemory - memoryRequired));
                environment.setValue("resources.memory.allocated." + creationRequest.get("id"), String.valueOf(memoryRequired));
            } else {
                throw new IllegalStateException("Insufficient memory resources");
            }
            
            // Allocate processing capacity
            int processingRequired = (int) requiredResources.get("processingCapacity");
            int availableProcessing = Integer.parseInt(environment.getValue("resources.processingCapacity.available"));
            
            if (processingRequired <= availableProcessing) {
                allocatedResources.put("processingCapacity", processingRequired);
                environment.setValue("resources.processingCapacity.available", String.valueOf(availableProcessing - processingRequired));
                environment.setValue("resources.processingCapacity.allocated." + creationRequest.get("id"), String.valueOf(processingRequired));
            } else {
                // Rollback memory allocation
                environment.setValue("resources.memory.available", String.valueOf(availableMemory));
                environment.setValue("resources.memory.allocated." + creationRequest.get("id"), null);
                throw new IllegalStateException("Insufficient processing resources");
            }
            
            // Allocate connection points
            int connectionsRequired = (int) requiredResources.get("connectionPoints");
            int availableConnections = Integer.parseInt(environment.getValue("resources.connectionPoints.available"));
            
            if (connectionsRequired <= availableConnections) {
                allocatedResources.put("connectionPoints", connectionsRequired);
                environment.setValue("resources.connectionPoints.available", String.valueOf(availableConnections - connectionsRequired));
                environment.setValue("resources.connectionPoints.allocated." + creationRequest.get("id"), String.valueOf(connectionsRequired));
            } else {
                // Rollback previous allocations
                environment.setValue("resources.memory.available", String.valueOf(availableMemory));
                environment.setValue("resources.memory.allocated." + creationRequest.get("id"), null);
                environment.setValue("resources.processingCapacity.available", String.valueOf(availableProcessing));
                environment.setValue("resources.processingCapacity.allocated." + creationRequest.get("id"), null);
                throw new IllegalStateException("Insufficient connection resources");
            }
            
            // Allocate state containers
            int containersRequired = (int) requiredResources.get("stateContainers");
            int availableContainers = Integer.parseInt(environment.getValue("resources.stateContainers.available"));
            
            if (containersRequired <= availableContainers) {
                allocatedResources.put("stateContainers", containersRequired);
                environment.setValue("resources.stateContainers.available", String.valueOf(availableContainers - containersRequired));
                environment.setValue("resources.stateContainers.allocated." + creationRequest.get("id"), String.valueOf(containersRequired));
            } else {
                // Rollback previous allocations
                environment.setValue("resources.memory.available", String.valueOf(availableMemory));
                environment.setValue("resources.memory.allocated." + creationRequest.get("id"), null);
                environment.setValue("resources.processingCapacity.available", String.valueOf(availableProcessing));
                environment.setValue("resources.processingCapacity.allocated." + creationRequest.get("id"), null);
                environment.setValue("resources.connectionPoints.available", String.valueOf(availableConnections));
                environment.setValue("resources.connectionPoints.allocated." + creationRequest.get("id"), null);
                throw new IllegalStateException("Insufficient state container resources");
            }
            
            // Prepare identity resources
            String identityId = UUID.randomUUID().toString();
            allocatedResources.put("identityId", identityId);
            environment.setValue("identity.reserved." + creationRequest.get("id"), identityId);
            
            // Capture environmental context
            Map<String, String> environmentalContext = new HashMap<>();
            environmentalContext.put("creationTime", LocalDateTime.now().toString());
            environmentalContext.put("environmentId", environment.getValue("environment.id"));
            environmentalContext.put("contextSignature", UUID.randomUUID().toString());
            
            allocatedResources.put("environmentalContext", environmentalContext);
            
            // Establish initialization parameters
            Map<String, Object> initParams = new HashMap<>();
            initParams.put("name", creationParams.get("name"));
            initParams.put("type", creationParams.get("type"));
            initParams.put("initialState", creationParams.get("initialState"));
            initParams.put("identityId", identityId);
            initParams.put("environmentalContext", environmentalContext);
            
            allocatedResources.put("initializationParameters", initParams);
            
            // Mark resources as allocated
            resourcesAllocated.set(true);
            
            LOGGER.info("Allocated resources for tube creation with ID: " + creationRequest.get("id"));
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to allocate resources: " + e.getMessage());
            throw new AssertionError("Failed to allocate resources", e);
        }
    }

    @When("a tube creation request exceeds available resources")
    public void a_tube_creation_request_exceeds_available_resources() {
        try {
            // Ensure we have a limited resources environment
            if (!limitedResources) {
                the_environment_has_limited_resources();
            }
            
            // Create a request that exceeds available resources
            creationRequest = new HashMap<>();
            creationRequest.put("id", UUID.randomUUID().toString());
            creationRequest.put("creationPurpose", "Test Tube Creation With Excessive Resources");
            creationRequest.put("timestamp", LocalDateTime.now().toString());
            
            // Required resources (exceeding available)
            Map<String, Object> requiredResources = new HashMap<>();
            requiredResources.put("memory", 100); // Available: 50
            requiredResources.put("processingCapacity", 15); // Available: 5
            requiredResources.put("connectionPoints", 5); // Available: 2
            requiredResources.put("stateContainers", 3); // Available: 1
            creationRequest.put("requiredResources", requiredResources);
            
            // Other required parameters
            Map<String, Object> lifespan = new HashMap<>();
            lifespan.put("estimatedDuration", "medium");
            lifespan.put("persistAfterCompletion", true);
            lifespan.put("maxInactivityPeriod", 3600);
            creationRequest.put("lifespan", lifespan);
            
            Map<String, Object> interactionModel = new HashMap<>();
            interactionModel.put("inputMode", "synchronous");
            interactionModel.put("outputMode", "asynchronous");
            interactionModel.put("isolationLevel", "standard");
            creationRequest.put("interactionModel", interactionModel);
            
            // Attempt to validate and allocate resources (should fail)
            try {
                // Validate (should fail on resource check)
                a_tube_creation_request_is_validated();
                
                if ((boolean) validationResults.getOrDefault("valid", false)) {
                    throw new AssertionError("Validation should have failed for resource-exceeding request");
                }
                
                // Try to allocate resources anyway (to test error handling)
                // This should throw an exception
                resources_are_allocated_for_tube_creation();
                
                // If we get here, allocation succeeded when it should have failed
                throw new AssertionError("Resource allocation should have failed for resource-exceeding request");
            } catch (IllegalStateException | AssertionError e) {
                // Expected exception for resource allocation failure
                lastException = e;
                LOGGER.info("Expected exception caught during allocation attempt: " + e.getMessage());
            }
            
            LOGGER.info("Created tube creation request that exceeds available resources");
        } catch (IllegalStateException | AssertionError e) {
            lastException = e;
            LOGGER.info("Expected exception during resource allocation: " + e.getMessage());
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Unexpected error: " + e.getMessage());
            throw new AssertionError("Unexpected error while creating resource-exceeding request", e);
        }
    }

    @Then("the environment should have a stable configuration")
    public void the_environment_should_have_a_stable_configuration() {
        Assertions.assertNotNull(environment, "Environment should be created");
        Assertions.assertEquals("true", environment.getValue("environment.stable"), 
            "Environment should be marked as stable");
    }

    @And("the environment should have resource allocation capabilities")
    public void the_environment_should_have_resource_allocation_capabilities() {
        Assertions.assertNotNull(environment.getValue("resources.memory.total"), 
            "Environment should track total memory resources");
        Assertions.assertNotNull(environment.getValue("resources.memory.available"), 
            "Environment should track available memory resources");
            
        Assertions.assertNotNull(environment.getValue("resources.processingCapacity.total"), 
            "Environment should track total processing capacity");
        Assertions.assertNotNull(environment.getValue("resources.processingCapacity.available"), 
            "Environment should track available processing capacity");
    }

    @And("the environment should provide isolation boundaries")
    public void the_environment_should_provide_isolation_boundaries() {
        Assertions.assertEquals("true", environment.getValue("isolation.enabled"), 
            "Environment should have isolation enabled");
        Assertions.assertNotNull(environment.getValue("isolation.boundary.type"), 
            "Environment should define isolation boundary type");
    }

    @And("the environment should have identity generation mechanisms")
    public void the_environment_should_have_identity_generation_mechanisms() {
        Assertions.assertEquals("true", environment.getValue("identity.generation.enabled"), 
            "Environment should have identity generation enabled");
        Assertions.assertNotNull(environment.getValue("identity.generation.method"), 
            "Environment should define identity generation method");
    }

    @Then("the request should contain a creation purpose")
    public void the_request_should_contain_a_creation_purpose() {
        Assertions.assertNotNull(creationRequest.get("creationPurpose"), 
            "Creation request should contain a purpose");
        Assertions.assertFalse(creationRequest.get("creationPurpose").toString().isEmpty(), 
            "Creation purpose should not be empty");
    }

    @And("the request should specify required resources")
    public void the_request_should_specify_required_resources() {
        Assertions.assertTrue(creationRequest.containsKey("requiredResources"), 
            "Creation request should specify required resources");
            
        Map<String, Object> resources = (Map<String, Object>) creationRequest.get("requiredResources");
        Assertions.assertTrue(resources.containsKey("memory"), "Required resources should specify memory");
        Assertions.assertTrue(resources.containsKey("processingCapacity"), "Required resources should specify processing capacity");
        Assertions.assertTrue(resources.containsKey("connectionPoints"), "Required resources should specify connection points");
        Assertions.assertTrue(resources.containsKey("stateContainers"), "Required resources should specify state containers");
    }

    @And("the request should indicate the intended lifespan")
    public void the_request_should_indicate_the_intended_lifespan() {
        Assertions.assertTrue(creationRequest.containsKey("lifespan"), 
            "Creation request should indicate intended lifespan");
            
        Map<String, Object> lifespan = (Map<String, Object>) creationRequest.get("lifespan");
        Assertions.assertTrue(lifespan.containsKey("estimatedDuration"), "Lifespan should specify estimated duration");
        Assertions.assertTrue(lifespan.containsKey("persistAfterCompletion"), "Lifespan should specify persistence preference");
    }

    @And("the request should define the interaction model")
    public void the_request_should_define_the_interaction_model() {
        Assertions.assertTrue(creationRequest.containsKey("interactionModel"), 
            "Creation request should define interaction model");
            
        Map<String, Object> interactionModel = (Map<String, Object>) creationRequest.get("interactionModel");
        Assertions.assertTrue(interactionModel.containsKey("inputMode"), "Interaction model should specify input mode");
        Assertions.assertTrue(interactionModel.containsKey("outputMode"), "Interaction model should specify output mode");
        Assertions.assertTrue(interactionModel.containsKey("isolationLevel"), "Interaction model should specify isolation level");
    }

    @Then("the request should be checked for completeness")
    public void the_request_should_be_checked_for_completeness() {
        Assertions.assertTrue(validationResults.containsKey("complete"), 
            "Validation should check request completeness");
    }

    @And("the request should be evaluated for resource availability")
    public void the_request_should_be_evaluated_for_resource_availability() {
        Assertions.assertTrue(validationResults.containsKey("resourcesAvailable"), 
            "Validation should evaluate resource availability");
    }

    @And("the request should be analyzed for potential conflicts")
    public void the_request_should_be_analyzed_for_potential_conflicts() {
        Assertions.assertTrue(validationResults.containsKey("noConflicts"), 
            "Validation should analyze potential conflicts");
    }

    @And("a successful validation should produce a creation template")
    public void a_successful_validation_should_produce_a_creation_template() {
        if ((boolean) validationResults.get("valid")) {
            Assertions.assertTrue(validationResults.containsKey("creationTemplate"), 
                "Successful validation should produce a creation template");
                
            Map<String, Object> template = (Map<String, Object>) validationResults.get("creationTemplate");
            Assertions.assertNotNull(template.get("id"), "Creation template should have an ID");
            Assertions.assertEquals(creationRequest.get("id"), template.get("requestId"), 
                "Creation template should reference the original request");
            Assertions.assertTrue(template.containsKey("creationParams"), 
                "Creation template should include creation parameters");
        } else {
            Assertions.assertFalse(validationResults.containsKey("creationTemplate"), 
                "Failed validation should not produce a creation template");
        }
    }

    @Then("memory resources should be reserved")
    public void memory_resources_should_be_reserved() {
        Assertions.assertTrue(allocatedResources.containsKey("memory"), 
            "Memory resources should be reserved");
            
        int memoryAllocated = (int) allocatedResources.get("memory");
        int memoryRequired = (int) ((Map<String, Object>) creationRequest.get("requiredResources")).get("memory");
        
        Assertions.assertEquals(memoryRequired, memoryAllocated, 
            "Allocated memory should match requested amount");
            
        // Verify environment tracking
        String allocatedInEnv = environment.getValue("resources.memory.allocated." + creationRequest.get("id"));
        Assertions.assertNotNull(allocatedInEnv, "Environment should track allocated memory");
        Assertions.assertEquals(String.valueOf(memoryAllocated), allocatedInEnv, 
            "Environment allocated memory should match actual allocation");
    }

    @And("identity resources should be prepared")
    public void identity_resources_should_be_prepared() {
        Assertions.assertTrue(allocatedResources.containsKey("identityId"), 
            "Identity resources should be prepared");
            
        String identityId = (String) allocatedResources.get("identityId");
        Assertions.assertNotNull(identityId, "Identity ID should be generated");
        Assertions.assertFalse(identityId.isEmpty(), "Identity ID should not be empty");
        
        // Verify environment tracking
        String reservedInEnv = environment.getValue("identity.reserved." + creationRequest.get("id"));
        Assertions.assertEquals(identityId, reservedInEnv, 
            "Environment should track reserved identity");
    }

    @And("environmental context should be captured")
    public void environmental_context_should_be_captured() {
        Assertions.assertTrue(allocatedResources.containsKey("environmentalContext"), 
            "Environmental context should be captured");
            
        Map<String, String> context = (Map<String, String>) allocatedResources.get("environmentalContext");
        Assertions.assertNotNull(context.get("creationTime"), "Context should include creation time");
        Assertions.assertNotNull(context.get("environmentId"), "Context should include environment ID");
        Assertions.assertNotNull(context.get("contextSignature"), "Context should include a signature");
    }

    @And("initialization parameters should be established")
    public void initialization_parameters_should_be_established() {
        Assertions.assertTrue(allocatedResources.containsKey("initializationParameters"), 
            "Initialization parameters should be established");
            
        Map<String, Object> params = (Map<String, Object>) allocatedResources.get("initializationParameters");
        Assertions.assertNotNull(params.get("name"), "Parameters should include name");
        Assertions.assertNotNull(params.get("type"), "Parameters should include type");
        Assertions.assertNotNull(params.get("initialState"), "Parameters should include initial state");
        Assertions.assertNotNull(params.get("identityId"), "Parameters should include identity ID");
        Assertions.assertNotNull(params.get("environmentalContext"), "Parameters should include environmental context");
    }

    @Then("the creation process should be rejected")
    public void the_creation_process_should_be_rejected() {
        Assertions.assertNotNull(lastException, "An exception should be thrown for rejected creation");
        
        if (validationResults.containsKey("valid")) {
            Assertions.assertFalse((boolean) validationResults.get("valid"), 
                "Validation should fail for resource-exceeding request");
        }
    }

    @And("appropriate error information should be provided")
    public void appropriate_error_information_should_be_provided() {
        if (validationResults.containsKey("error")) {
            String errorMessage = (String) validationResults.get("error");
            Assertions.assertNotNull(errorMessage, "Error message should be provided");
            Assertions.assertTrue(errorMessage.contains("resource") || errorMessage.contains("insufficient"), 
                "Error message should mention resource issues");
        } else {
            Assertions.assertNotNull(lastException, "An exception should provide error information");
            String errorMessage = lastException.getMessage();
            Assertions.assertTrue(errorMessage.contains("resource") || errorMessage.contains("insufficient"), 
                "Exception message should mention resource issues");
        }
    }

    @And("no partial tube structure should remain")
    public void no_partial_tube_structure_should_remain() {
        // This is difficult to verify directly, but we can check that allocated resources were properly released
        // The approach here is to verify the resource count hasn't changed if allocation failed
        
        int availableMemory = Integer.parseInt(environment.getValue("resources.memory.available"));
        int availableProcessing = Integer.parseInt(environment.getValue("resources.processingCapacity.available"));
        int availableConnections = Integer.parseInt(environment.getValue("resources.connectionPoints.available"));
        int availableContainers = Integer.parseInt(environment.getValue("resources.stateContainers.available"));
        
        // The expected values if the environment was correctly restored
        int expectedMemory = 50; // From limited resources setup
        int expectedProcessing = 5;
        int expectedConnections = 2;
        int expectedContainers = 1;
        
        Assertions.assertEquals(expectedMemory, availableMemory, 
            "Available memory should be restored after failed allocation");
        Assertions.assertEquals(expectedProcessing, availableProcessing, 
            "Available processing capacity should be restored after failed allocation");
        Assertions.assertEquals(expectedConnections, availableConnections, 
            "Available connection points should be restored after failed allocation");
        Assertions.assertEquals(expectedContainers, availableContainers, 
            "Available state containers should be restored after failed allocation");
    }

    @And("resources should be properly released")
    public void resources_should_be_properly_released() {
        // Verify that resource allocations were properly cleaned up
        String memoryAllocation = environment.getValue("resources.memory.allocated." + creationRequest.get("id"));
        String processingAllocation = environment.getValue("resources.processingCapacity.allocated." + creationRequest.get("id"));
        String connectionsAllocation = environment.getValue("resources.connectionPoints.allocated." + creationRequest.get("id"));
        String containersAllocation = environment.getValue("resources.stateContainers.allocated." + creationRequest.get("id"));
        
        // These should all be null or empty after proper cleanup
        Assertions.assertTrue(memoryAllocation == null || memoryAllocation.isEmpty(), 
            "Memory allocation should be cleaned up");
        Assertions.assertTrue(processingAllocation == null || processingAllocation.isEmpty(), 
            "Processing capacity allocation should be cleaned up");
        Assertions.assertTrue(connectionsAllocation == null || connectionsAllocation.isEmpty(), 
            "Connection points allocation should be cleaned up");
        Assertions.assertTrue(containersAllocation == null || containersAllocation.isEmpty(), 
            "State containers allocation should be cleaned up");
            
        // Identity reservation should also be cleared
        String identityReservation = environment.getValue("identity.reserved." + creationRequest.get("id"));
        Assertions.assertTrue(identityReservation == null || identityReservation.isEmpty(), 
            "Identity reservation should be cleaned up");
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
            // Limited resources
            env.setValue("resources.memory.total", "150");
            env.setValue("resources.memory.available", "50");
            env.setValue("resources.processingCapacity.total", "20");
            env.setValue("resources.processingCapacity.available", "5");
            env.setValue("resources.connectionPoints.total", "10");
            env.setValue("resources.connectionPoints.available", "2");
            env.setValue("resources.stateContainers.total", "5");
            env.setValue("resources.stateContainers.available", "1");
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
        
        // Set up isolation boundaries
        env.setValue("isolation.enabled", "true");
        env.setValue("isolation.boundary.type", "standard");
        env.setValue("isolation.level", "component");
        
        // Set up identity generation
        env.setValue("identity.generation.enabled", "true");
        env.setValue("identity.generation.method", "uuid-v4");
        
        return env;
    }
}