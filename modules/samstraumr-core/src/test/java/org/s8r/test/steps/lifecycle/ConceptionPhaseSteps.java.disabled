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
import io.cucumber.datatable.DataTable;

import org.junit.jupiter.api.Assertions;
import org.s8r.component.Component;
import org.s8r.component.Identity;
import org.s8r.component.Environment;
import org.s8r.component.State;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Step definitions for conception phase tests, focusing on unique identifier
 * creation and tracking initialization parameters.
 */
public class ConceptionPhaseSteps {

    private static final Logger LOGGER = Logger.getLogger(ConceptionPhaseSteps.class.getName());
    
    // Test context
    private Component component;
    private Identity identity;
    private LocalDateTime creationTime;
    private Map<String, Object> customParameters = new HashMap<>();
    private Exception lastException;
    
    @Given("the system environment is properly configured")
    public void the_system_environment_is_properly_configured() {
        // This step is already implemented in EarlyConceptionPhaseSteps
        // Configure test environment with appropriate settings
        Map<String, Object> environmentContext = new HashMap<>();
        environmentContext.put("test.mode", true);
        environmentContext.put("validate.identity", true);
        environmentContext.put("conceive.components", true);
        environmentContext.put("test.timestamp", LocalDateTime.now());
        
        LOGGER.info("Test environment configured with test mode enabled");
    }
    
    @When("a new tube is created")
    public void a_new_tube_is_created() {
        try {
            // Capture the time before creation
            creationTime = LocalDateTime.now();
            
            // Create a new component (representing a "tube")
            component = Component.createAdam("Conception Phase Test");
            
            // Retrieve the component's identity
            identity = component.getIdentity();
            
            Assertions.assertNotNull(component, "Component should be created");
            Assertions.assertNotNull(identity, "Identity should be created");
            
            LOGGER.info("Created component with identity: " + identity.getId());
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to create component: " + e.getMessage());
            throw new AssertionError("Failed to create component", e);
        }
    }
    
    @When("the tube initializes with custom parameters")
    public void the_tube_initializes_with_custom_parameters() {
        try {
            // Set up custom parameters
            customParameters.put("name", "Parameterized Tube");
            customParameters.put("version", "1.0.0");
            customParameters.put("createdBy", "Conception Test");
            customParameters.put("purpose", "Parameter Testing");
            customParameters.put("timestamp", LocalDateTime.now());
            customParameters.put("uuid", UUID.randomUUID().toString());
            
            // Create a component with custom parameters
            component = Component.createAdam(customParameters.get("name").toString());
            
            // Apply custom parameters to the component's environment
            Environment env = component.getEnvironment();
            for (Map.Entry<String, Object> entry : customParameters.entrySet()) {
                env.setValue(entry.getKey(), entry.getValue().toString());
            }
            
            // Retrieve the component's identity
            identity = component.getIdentity();
            
            Assertions.assertNotNull(component, "Component should be created");
            Assertions.assertNotNull(identity, "Identity should be created");
            
            LOGGER.info("Created component with custom parameters and identity: " + identity.getId());
        } catch (Exception e) {
            lastException = e;
            LOGGER.severe("Failed to create component with custom parameters: " + e.getMessage());
            throw new AssertionError("Failed to create component with custom parameters", e);
        }
    }
    
    @When("the tube is created with invalid parameters")
    public void the_tube_is_created_with_invalid_parameters() {
        try {
            // Set up invalid parameters
            customParameters.put("name", null); // Invalid - name cannot be null
            customParameters.put("version", "-1.0"); // Invalid version format
            customParameters.put("state", "INVALID_STATE"); // Invalid state
            
            try {
                // This should throw an exception
                component = Component.createAdam((String)null);
            } catch (Exception e) {
                // Expected exception - capture for verification
                lastException = e;
                LOGGER.info("Captured expected error during invalid parameter creation: " + e.getMessage());
                return;
            }
            
            // If we get here, no exception was thrown
            throw new AssertionError("Expected exception with invalid parameters, but none was thrown");
        } catch (AssertionError e) {
            throw e; // Rethrow assertion errors
        } catch (Exception e) {
            if (lastException == null) {
                lastException = e;
            }
            LOGGER.severe("Unexpected error: " + e.getMessage());
            throw new AssertionError("Unexpected error during invalid parameter test", e);
        }
    }
    
    @Then("the tube should have a unique identifier")
    public void the_tube_should_have_a_unique_identifier() {
        Assertions.assertNotNull(identity, "Tube should have an identity");
        Assertions.assertNotNull(identity.getId(), "Tube identity should have an ID");
        Assertions.assertFalse(identity.getId().isEmpty(), "Tube identity ID should not be empty");
        
        // Create a second component to verify uniqueness
        Component secondComponent = Component.createAdam("Uniqueness Test");
        String secondId = secondComponent.getIdentity().getId();
        
        Assertions.assertNotEquals(identity.getId(), secondId, "Identity IDs should be unique");
    }
    
    @And("the tube should have a creation timestamp")
    public void the_tube_should_have_a_creation_timestamp() {
        LocalDateTime identityCreationTime = identity.getCreationTime();
        Assertions.assertNotNull(identityCreationTime, "Identity should have a creation time");
        
        // Verify the creation time is close to our recorded time (within 1 second)
        long timeDifferenceMs = Math.abs(identityCreationTime.toLocalTime().toNanoOfDay() / 1_000_000 - 
                                      creationTime.toLocalTime().toNanoOfDay() / 1_000_000);
        
        Assertions.assertTrue(timeDifferenceMs < 1000, 
            "Identity creation time should be within 1 second of the actual creation time");
    }
    
    @And("the tube should capture the environmental context")
    public void the_tube_should_capture_the_environmental_context() {
        Environment env = component.getEnvironment();
        Assertions.assertNotNull(env, "Tube should have an environment");
        
        // Check for basic environmental properties
        Assertions.assertNotNull(env.getValue("test.mode"), 
            "Environment should capture test mode setting");
    }
    
    @Then("the tube should incorporate the parameters into its identity")
    public void the_tube_should_incorporate_the_parameters_into_its_identity() {
        // Verify the identity has incorporated the custom parameters
        Environment env = component.getEnvironment();
        
        for (Map.Entry<String, Object> entry : customParameters.entrySet()) {
            String paramName = entry.getKey();
            String expectedValue = entry.getValue().toString();
            String actualValue = env.getValue(paramName);
            
            Assertions.assertNotNull(actualValue, 
                "Parameter '" + paramName + "' should be incorporated into the tube's environment");
            Assertions.assertEquals(expectedValue, actualValue,
                "Parameter '" + paramName + "' should have correct value");
        }
        
        // Verify the identity itself reflects key parameters
        Assertions.assertEquals(customParameters.get("name").toString(), component.getName(),
            "Tube name should match the specified parameter");
    }
    
    @Then("the tube creation should fail gracefully")
    public void the_tube_creation_should_fail_gracefully() {
        // Verify the component creation failed as expected
        Assertions.assertNotNull(lastException, "An exception should have been thrown");
        Assertions.assertNull(component, "No component should be created after error");
    }
    
    @And("an appropriate error message should be logged")
    public void an_appropriate_error_message_should_be_logged() {
        // Verify error information is appropriate
        Assertions.assertNotNull(lastException.getMessage(), "Error should have a message");
        Assertions.assertFalse(lastException.getMessage().isEmpty(), "Error message should not be empty");
        
        // Verify error contains expected information about parameter validation
        String errorMessage = lastException.getMessage();
        Assertions.assertTrue(errorMessage.contains("parameter") || 
                          errorMessage.contains("invalid") ||
                          errorMessage.contains("null") ||
                          errorMessage.contains("empty"),
            "Error message should reference invalid parameters");
    }
}