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

package org.s8r.test.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.s8r.application.port.PersistencePort.PersistenceResult;
import org.s8r.application.port.ValidationPort.ValidationResult;
import org.s8r.test.context.ValidationPersistenceIntegrationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step definitions for testing the integration between Validation and Persistence ports.
 */
public class ValidationPersistenceIntegrationSteps {

    private final ValidationPersistenceIntegrationContext context;

    /**
     * Constructs a new ValidationPersistenceIntegrationSteps.
     *
     * @param context The test context
     */
    public ValidationPersistenceIntegrationSteps(ValidationPersistenceIntegrationContext context) {
        this.context = context;
    }

    @Given("a validation service is initialized")
    public void aValidationServiceIsInitialized() {
        context.clear();
        context.getLoggerAdapter().info("Validation service initialized");
    }

    @Given("a persistence service is initialized")
    public void aPersistenceServiceIsInitialized() {
        boolean result = context.getPersistenceAdapter().initialize();
        assertTrue(result, "Persistence service should initialize successfully");
        context.getLoggerAdapter().info("Persistence service initialized");
    }

    @Given("I have a valid data object")
    public void iHaveAValidDataObject() {
        Map<String, Object> validObject = new HashMap<>();
        validObject.put("id", "user-123");
        validObject.put("name", "John Doe");
        validObject.put("email", "john@example.com");
        validObject.put("age", 30);
        
        context.setCurrentObject(validObject);
    }

    @Given("I have an invalid data object")
    public void iHaveAnInvalidDataObject() {
        Map<String, Object> invalidObject = new HashMap<>();
        invalidObject.put("id", "user-invalid");
        invalidObject.put("name", "John Doe");
        invalidObject.put("email", "not-an-email");  // Invalid email format
        invalidObject.put("age", 15);  // Age below minimum
        
        context.setCurrentObject(invalidObject);
    }

    @Given("I have the following data objects:")
    public void iHaveTheFollowingDataObjects(DataTable dataTable) {
        List<Map<String, Object>> objects = new ArrayList<>();
        
        for (Map<String, String> row : dataTable.asMaps()) {
            Map<String, Object> object = new HashMap<>();
            
            for (Map.Entry<String, String> entry : row.entrySet()) {
                if (entry.getKey().equals("valid")) {
                    // Skip the valid flag as it's not part of the actual object
                    continue;
                }
                
                String key = entry.getKey();
                String stringValue = entry.getValue();
                Object value = stringValue;
                
                // Try to convert numeric values
                if (key.equals("age")) {
                    try {
                        value = Integer.parseInt(stringValue);
                    } catch (NumberFormatException e) {
                        // Keep as string
                    }
                }
                
                object.put(key, value);
            }
            
            objects.add(object);
        }
        
        context.setDataObjects(objects);
    }

    @When("I validate and persist the object")
    public void iValidateAndPersistTheObject() {
        Map<String, Object> object = context.getCurrentObject();
        String entityType = "user";
        String entityId = (String) object.get("id");
        
        // Validate the object
        ValidationResult validationResult = context.getValidationAdapter().validateEntity(entityType, object);
        context.setValidationResult(validationResult);
        context.setObjectValidated(true);
        
        // Only persist if validation passed
        if (validationResult.isValid()) {
            PersistenceResult persistenceResult = context.getPersistenceAdapter().save(entityType, entityId, object);
            context.setPersistenceResult(persistenceResult);
            context.setObjectPersisted(persistenceResult.isSuccess());
        } else {
            context.setObjectPersisted(false);
        }
    }

    @When("I validate and persist each object")
    public void iValidateAndPersistEachObject() {
        List<Map<String, Object>> objects = context.getDataObjects();
        String entityType = "user";
        
        for (Map<String, Object> object : objects) {
            String entityId = (String) object.get("id");
            
            // Validate the object
            ValidationResult validationResult = context.getValidationAdapter().validateEntity(entityType, object);
            context.addValidationResult(entityId, validationResult);
            
            // Only persist if validation passed
            if (validationResult.isValid()) {
                PersistenceResult persistenceResult = context.getPersistenceAdapter().save(entityType, entityId, object);
                context.addPersistenceResult(entityId, persistenceResult);
            }
        }
    }

    @Then("the validation should pass")
    public void theValidationShouldPass() {
        ValidationResult result = context.getValidationResult();
        assertNotNull(result, "Validation result should not be null");
        assertTrue(result.isValid(), "Validation should pass");
        assertTrue(result.getErrors().isEmpty(), "There should be no validation errors");
    }

    @Then("the object should be persisted successfully")
    public void theObjectShouldBePersistedSuccessfully() {
        assertTrue(context.isObjectPersisted(), "Object should be persisted");
        
        PersistenceResult result = context.getPersistenceResult();
        assertNotNull(result, "Persistence result should not be null");
        assertTrue(result.isSuccess(), "Persistence should be successful: " + result.getMessage());
        
        Map<String, Object> object = context.getCurrentObject();
        String entityType = "user";
        String entityId = (String) object.get("id");
        
        boolean exists = context.getPersistenceAdapter().exists(entityType, entityId);
        assertTrue(exists, "Entity should exist in storage");
    }

    @Then("I should be able to retrieve the object")
    public void iShouldBeAbleToRetrieveTheObject() {
        Map<String, Object> object = context.getCurrentObject();
        String entityType = "user";
        String entityId = (String) object.get("id");
        
        Optional<Map<String, Object>> retrieved = context.getPersistenceAdapter().findById(entityType, entityId);
        context.setRetrievedObject(retrieved);
        
        assertTrue(retrieved.isPresent(), "Retrieved object should be present");
        assertEquals(object, retrieved.get(), "Retrieved object should match the original");
    }

    @Then("the validation should fail")
    public void theValidationShouldFail() {
        ValidationResult result = context.getValidationResult();
        assertNotNull(result, "Validation result should not be null");
        assertFalse(result.isValid(), "Validation should fail");
        assertFalse(result.getErrors().isEmpty(), "There should be validation errors");
    }

    @Then("the object should not be persisted")
    public void theObjectShouldNotBePersisted() {
        assertFalse(context.isObjectPersisted(), "Object should not be persisted");
        
        Map<String, Object> object = context.getCurrentObject();
        String entityType = "user";
        String entityId = (String) object.get("id");
        
        boolean exists = context.getPersistenceAdapter().exists(entityType, entityId);
        assertFalse(exists, "Entity should not exist in storage");
    }

    @Then("I should receive validation errors")
    public void iShouldReceiveValidationErrors() {
        ValidationResult result = context.getValidationResult();
        assertNotNull(result, "Validation result should not be null");
        assertFalse(result.getErrors().isEmpty(), "There should be validation errors");
        
        // Log the errors for visibility
        for (String error : result.getErrors()) {
            context.getLoggerAdapter().info("Validation error: {}", error);
        }
    }

    @Then("only valid objects should be persisted")
    public void onlyValidObjectsShouldBePersisted() {
        List<Map<String, Object>> objects = context.getDataObjects();
        String entityType = "user";
        
        for (Map<String, Object> object : objects) {
            String entityId = (String) object.get("id");
            ValidationResult validationResult = context.getValidationResults().get(entityId);
            
            assertNotNull(validationResult, "Validation result should not be null for " + entityId);
            
            boolean exists = context.getPersistenceAdapter().exists(entityType, entityId);
            
            if (validationResult.isValid()) {
                assertTrue(exists, "Valid entity should exist in storage: " + entityId);
                
                // Check if it was saved with the correct data
                Optional<Map<String, Object>> retrieved = context.getPersistenceAdapter().findById(entityType, entityId);
                assertTrue(retrieved.isPresent(), "Valid entity should be retrievable: " + entityId);
                
                // Compare only the fields that were in the original object
                for (Map.Entry<String, Object> entry : object.entrySet()) {
                    assertEquals(entry.getValue(), retrieved.get().get(entry.getKey()), 
                        "Field " + entry.getKey() + " should match for entity " + entityId);
                }
            } else {
                assertFalse(exists, "Invalid entity should not exist in storage: " + entityId);
            }
        }
    }

    @Then("I should receive validation errors for invalid objects")
    public void iShouldReceiveValidationErrorsForInvalidObjects() {
        List<Map<String, Object>> objects = context.getDataObjects();
        
        for (Map<String, Object> object : objects) {
            String entityId = (String) object.get("id");
            ValidationResult validationResult = context.getValidationResults().get(entityId);
            
            assertNotNull(validationResult, "Validation result should not be null for " + entityId);
            
            // Check if the validation result matches the expected validity
            boolean expectedValid = true;
            if (object.containsKey("valid")) {
                expectedValid = Boolean.parseBoolean(object.get("valid").toString());
            }
            
            assertEquals(expectedValid, validationResult.isValid(), 
                "Validation result for " + entityId + " should match expected validity");
            
            if (!expectedValid) {
                assertFalse(validationResult.getErrors().isEmpty(), 
                    "There should be validation errors for invalid entity: " + entityId);
                
                // Log the errors for visibility
                for (String error : validationResult.getErrors()) {
                    context.getLoggerAdapter().info("Validation error for {}: {}", entityId, error);
                }
            }
        }
    }
}