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

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.PersistencePort;
import org.s8r.application.port.PersistencePort.PersistenceResult;
import org.s8r.application.port.ValidationPort;
import org.s8r.application.port.ValidationPort.ValidationResult;
import org.s8r.application.service.PersistenceService;
import org.s8r.application.service.ValidationService;
import org.s8r.infrastructure.logging.ConsoleLogger;
import org.s8r.infrastructure.persistence.InMemoryPersistenceAdapter;
import org.s8r.infrastructure.validation.ValidationAdapter;
import org.s8r.test.annotation.IntegrationTest;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for testing the integration between ValidationPort and PersistencePort.
 */
@IntegrationTest
public class ValidationPersistenceIntegrationSteps {

    private ValidationPort validationPort;
    private PersistencePort persistencePort;
    private ValidationService validationService;
    private PersistenceService persistenceService;
    private LoggerPort logger;
    private Map<String, Object> testContext;
    private List<String> logMessages;

    @Before
    public void setup() {
        testContext = new HashMap<>();
        logMessages = new ArrayList<>();
        
        // Create a test logger that captures log messages
        logger = new ConsoleLogger() {
            @Override
            public void info(String message) {
                super.info(message);
                logMessages.add(message);
            }
            
            @Override
            public void info(String message, Object... args) {
                super.info(message, args);
                logMessages.add(String.format(message.replace("{}", "%s"), args));
            }
            
            @Override
            public void debug(String message) {
                super.debug(message);
                logMessages.add(message);
            }
            
            @Override
            public void debug(String message, Object... args) {
                super.debug(message, args);
                logMessages.add(String.format(message.replace("{}", "%s"), args));
            }
        };
        
        // Initialize ports
        validationPort = new ValidationAdapter(logger);
        persistencePort = new InMemoryPersistenceAdapter(logger);
        
        // Initialize services
        validationService = new ValidationService(validationPort, logger);
        persistenceService = new PersistenceService(persistencePort, validationService, logger);
        
        // Reset log messages between scenarios
        logMessages.clear();
    }
    
    @After
    public void cleanup() {
        testContext.clear();
        logMessages.clear();
    }

    @Given("a clean system environment")
    public void aCleanSystemEnvironment() {
        // Already set up in the @Before method
        assertNotNull(validationPort, "ValidationPort should be initialized");
        assertNotNull(persistencePort, "PersistencePort should be initialized");
        assertNotNull(validationService, "ValidationService should be initialized");
        assertNotNull(persistenceService, "PersistenceService should be initialized");
    }

    @Given("both validation and persistence ports are properly initialized")
    public void bothValidationAndPersistencePortsAreProperlyInitialized() {
        // Ensure validation rules are initialized
        boolean validationInitialized = validationService.isInitialized();
        assertTrue(validationInitialized, "ValidationService should be initialized");
        
        // Ensure persistence store is initialized
        boolean persistenceInitialized = persistenceService.isInitialized();
        assertTrue(persistenceInitialized, "PersistenceService should be initialized");
    }

    @Given("standard entity validation rules are configured")
    public void standardEntityValidationRulesAreConfigured() {
        // Configure standard validation rules
        validationService.configureStandardRules();
        
        // Verify standard rules exists
        assertTrue(validationService.hasRule("email"), "Email validation rule should exist");
        assertTrue(validationService.hasRule("positive-number"), "Positive number validation rule should exist");
        assertTrue(validationService.hasRule("non-empty-string"), "Non-empty string validation rule should exist");
    }

    @Given("I have a valid {string} entity with the following data:")
    public void iHaveAValidEntityWithTheFollowingData(String entityType, DataTable dataTable) {
        Map<String, String> data = dataTable.asMap();
        
        // Create entity from data table
        Map<String, Object> entity = new HashMap<>();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            // Convert number values appropriately
            if (entry.getKey().equals("age")) {
                entity.put(entry.getKey(), Integer.parseInt(entry.getValue()));
            } else {
                entity.put(entry.getKey(), entry.getValue());
            }
        }
        
        // Store the entity
        testContext.put("entityType", entityType);
        testContext.put("entity", entity);
        testContext.put("entityData", data);
    }

    @Given("I have an invalid {string} entity with the following data:")
    public void iHaveAnInvalidEntityWithTheFollowingData(String entityType, DataTable dataTable) {
        Map<String, String> data = dataTable.asMap();
        
        // Create entity from data table
        Map<String, Object> entity = new HashMap<>();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (entry.getKey().equals("age")) {
                // Try to convert to number, default to 0 if empty or invalid
                try {
                    entity.put(entry.getKey(), Integer.parseInt(entry.getValue()));
                } catch (NumberFormatException e) {
                    entity.put(entry.getKey(), entry.getValue()); // Store as string to cause validation error
                }
            } else {
                entity.put(entry.getKey(), entry.getValue());
            }
        }
        
        // Store the entity
        testContext.put("entityType", entityType);
        testContext.put("entity", entity);
        testContext.put("entityData", data);
    }

    @Given("I have a stored {string} entity with the following data:")
    public void iHaveAStoredEntityWithTheFollowingData(String entityType, DataTable dataTable) {
        Map<String, String> data = dataTable.asMap();
        
        // Create entity from data table
        Map<String, Object> entity = new HashMap<>();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (entry.getKey().equals("age")) {
                entity.put(entry.getKey(), Integer.parseInt(entry.getValue()));
            } else {
                entity.put(entry.getKey(), entry.getValue());
            }
        }
        
        // Save the entity to persistence
        String entityId = (String) entity.get("id");
        PersistenceResult saveResult = persistencePort.save(entityType, entityId, entity);
        assertTrue(saveResult.isSuccessful(), "Entity should be saved successfully");
        
        // Store for later reference
        testContext.put("entityType", entityType);
        testContext.put("entityId", entityId);
        testContext.put("originalEntity", entity);
    }

    @Given("the following {string} entities are stored:")
    public void theFollowingEntitiesAreStored(String entityType, DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        List<String> savedIds = new ArrayList<>();
        
        for (Map<String, String> row : rows) {
            // Create entity from row data
            Map<String, Object> entity = new HashMap<>();
            for (Map.Entry<String, String> entry : row.entrySet()) {
                if (entry.getKey().equals("age")) {
                    entity.put(entry.getKey(), Integer.parseInt(entry.getValue()));
                } else {
                    entity.put(entry.getKey(), entry.getValue());
                }
            }
            
            // Save the entity
            String entityId = (String) entity.get("id");
            PersistenceResult saveResult = persistencePort.save(entityType, entityId, entity);
            assertTrue(saveResult.isSuccessful(), "Entity should be saved successfully: " + entityId);
            savedIds.add(entityId);
        }
        
        // Store for later reference
        testContext.put("entityType", entityType);
        testContext.put("savedIds", savedIds);
        testContext.put("savedCount", savedIds.size());
    }

    @Given("I have the following entities to process:")
    public void iHaveTheFollowingEntitiesToProcess(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        List<Map<String, Object>> entities = new ArrayList<>();
        Map<String, Boolean> validityMap = new HashMap<>();
        
        for (Map<String, String> row : rows) {
            // Create entity from row data
            Map<String, Object> entity = new HashMap<>();
            for (Map.Entry<String, String> entry : row.entrySet()) {
                if (entry.getKey().equals("age")) {
                    try {
                        entity.put(entry.getKey(), Integer.parseInt(entry.getValue()));
                    } catch (NumberFormatException e) {
                        entity.put(entry.getKey(), entry.getValue()); // Will cause validation error
                    }
                } else if (entry.getKey().equals("valid")) {
                    // Skip this field, it's for test tracking
                    continue;
                } else {
                    entity.put(entry.getKey(), entry.getValue());
                }
            }
            
            // Store validity flag
            String entityId = (String) entity.get("id");
            boolean isValid = Boolean.parseBoolean(row.get("valid"));
            validityMap.put(entityId, isValid);
            
            entities.add(entity);
        }
        
        // Store for batch processing
        testContext.put("entityType", "user");
        testContext.put("batchEntities", entities);
        testContext.put("validityMap", validityMap);
    }

    @When("I validate and store the entity")
    public void iValidateAndStoreTheEntity() {
        String entityType = (String) testContext.get("entityType");
        @SuppressWarnings("unchecked")
        Map<String, Object> entity = (Map<String, Object>) testContext.get("entity");
        
        // Validate the entity
        ValidationResult validationResult = validationService.validateEntity(entityType, entity);
        testContext.put("validationResult", validationResult);
        
        // If validation passes, store the entity
        if (validationResult.isValid()) {
            String entityId = (String) entity.get("id");
            PersistenceResult saveResult = persistenceService.save(entityType, entityId, entity);
            testContext.put("saveResult", saveResult);
            testContext.put("entitySaved", saveResult.isSuccessful());
        } else {
            testContext.put("entitySaved", false);
        }
    }

    @When("I update the entity with valid changes:")
    public void iUpdateTheEntityWithValidChanges(DataTable dataTable) {
        String entityType = (String) testContext.get("entityType");
        String entityId = (String) testContext.get("entityId");
        
        // Get the current entity
        PersistenceResult getResult = persistencePort.findById(entityType, entityId);
        assertTrue(getResult.isSuccessful(), "Entity should be found");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> currentEntity = (Map<String, Object>) getResult.getValue();
        Map<String, String> updates = dataTable.asMap();
        
        // Create updated entity
        Map<String, Object> updatedEntity = new HashMap<>(currentEntity);
        for (Map.Entry<String, String> entry : updates.entrySet()) {
            if (entry.getKey().equals("age")) {
                updatedEntity.put(entry.getKey(), Integer.parseInt(entry.getValue()));
            } else {
                updatedEntity.put(entry.getKey(), entry.getValue());
            }
        }
        
        // Validate the updated entity
        ValidationResult validationResult = validationService.validateEntity(entityType, updatedEntity);
        testContext.put("validationResult", validationResult);
        
        // If validation passes, update the entity
        if (validationResult.isValid()) {
            PersistenceResult updateResult = persistenceService.update(entityType, entityId, updatedEntity);
            testContext.put("updateResult", updateResult);
            testContext.put("entityUpdated", updateResult.isSuccessful());
            testContext.put("updatedEntity", updatedEntity);
        } else {
            testContext.put("entityUpdated", false);
        }
    }

    @When("I update the entity with invalid changes:")
    public void iUpdateTheEntityWithInvalidChanges(DataTable dataTable) {
        String entityType = (String) testContext.get("entityType");
        String entityId = (String) testContext.get("entityId");
        
        // Get the current entity
        PersistenceResult getResult = persistencePort.findById(entityType, entityId);
        assertTrue(getResult.isSuccessful(), "Entity should be found");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> currentEntity = (Map<String, Object>) getResult.getValue();
        Map<String, String> updates = dataTable.asMap();
        
        // Create updated entity
        Map<String, Object> updatedEntity = new HashMap<>(currentEntity);
        for (Map.Entry<String, String> entry : updates.entrySet()) {
            updatedEntity.put(entry.getKey(), entry.getValue());
        }
        
        // Validate the updated entity
        ValidationResult validationResult = validationService.validateEntity(entityType, updatedEntity);
        testContext.put("validationResult", validationResult);
        
        // If validation passes, update the entity
        if (validationResult.isValid()) {
            PersistenceResult updateResult = persistenceService.update(entityType, entityId, updatedEntity);
            testContext.put("updateResult", updateResult);
            testContext.put("entityUpdated", updateResult.isSuccessful());
        } else {
            testContext.put("entityUpdated", false);
        }
    }

    @When("I search for entities with the following criteria:")
    public void iSearchForEntitiesWithTheFollowingCriteria(DataTable dataTable) {
        String entityType = (String) testContext.get("entityType");
        Map<String, String> criteria = dataTable.asMap();
        
        // Convert criteria to appropriate types
        Map<String, Object> searchCriteria = new HashMap<>();
        for (Map.Entry<String, String> entry : criteria.entrySet()) {
            if (entry.getKey().equals("minAge")) {
                searchCriteria.put("minAge", Integer.parseInt(entry.getValue()));
            } else {
                searchCriteria.put(entry.getKey(), entry.getValue());
            }
        }
        
        // Perform search
        PersistenceResult searchResult = persistenceService.findByCriteria(entityType, searchCriteria);
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> results = searchResult.isSuccessful() ? 
            (List<Map<String, Object>>) searchResult.getValue() : new ArrayList<>();
        
        // Store results
        testContext.put("searchCriteria", searchCriteria);
        testContext.put("searchResults", results);
    }

    @When("I batch validate and persist the entities")
    public void iBatchValidateAndPersistTheEntities() {
        String entityType = (String) testContext.get("entityType");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> entities = (List<Map<String, Object>>) testContext.get("batchEntities");
        
        List<Map<String, Object>> validEntities = new ArrayList<>();
        List<Map<String, Object>> invalidEntities = new ArrayList<>();
        List<String> persistedIds = new ArrayList<>();
        
        for (Map<String, Object> entity : entities) {
            // Validate the entity
            ValidationResult validationResult = validationService.validateEntity(entityType, entity);
            
            if (validationResult.isValid()) {
                validEntities.add(entity);
                String entityId = (String) entity.get("id");
                PersistenceResult saveResult = persistenceService.save(entityType, entityId, entity);
                if (saveResult.isSuccessful()) {
                    persistedIds.add(entityId);
                }
            } else {
                invalidEntities.add(entity);
            }
        }
        
        // Store results
        testContext.put("validEntities", validEntities);
        testContext.put("invalidEntities", invalidEntities);
        testContext.put("persistedIds", persistedIds);
        testContext.put("successCount", persistedIds.size());
        testContext.put("failureCount", invalidEntities.size());
    }

    @Then("the validation should pass")
    public void theValidationShouldPass() {
        ValidationResult validationResult = (ValidationResult) testContext.get("validationResult");
        assertNotNull(validationResult, "Validation result should be in test context");
        
        assertTrue(validationResult.isValid(), "Validation should pass");
        assertTrue(validationResult.getErrors().isEmpty(), "Validation should have no errors");
    }

    @Then("the entity should be successfully saved")
    public void theEntityShouldBeSuccessfullySaved() {
        Boolean entitySaved = (Boolean) testContext.get("entitySaved");
        assertNotNull(entitySaved, "Entity saved flag should be in test context");
        
        assertTrue(entitySaved, "Entity should be saved successfully");
        
        PersistenceResult saveResult = (PersistenceResult) testContext.get("saveResult");
        assertNotNull(saveResult, "Save result should be in test context");
        assertTrue(saveResult.isSuccessful(), "Save operation should be successful");
    }

    @Then("I should be able to retrieve the entity by its ID")
    public void iShouldBeAbleToRetrieveTheEntityByItsID() {
        String entityType = (String) testContext.get("entityType");
        @SuppressWarnings("unchecked")
        Map<String, Object> entity = (Map<String, Object>) testContext.get("entity");
        String entityId = (String) entity.get("id");
        
        // Retrieve the entity
        PersistenceResult result = persistencePort.findById(entityType, entityId);
        
        assertTrue(result.isSuccessful(), "Entity should be retrieved successfully");
        assertNotNull(result.getValue(), "Retrieved entity should not be null");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> retrievedEntity = (Map<String, Object>) result.getValue();
        assertEquals(entityId, retrievedEntity.get("id"), "Retrieved entity should have correct ID");
        
        // Check that all fields match
        for (Map.Entry<String, Object> entry : entity.entrySet()) {
            assertEquals(entry.getValue(), retrievedEntity.get(entry.getKey()), 
                    "Field " + entry.getKey() + " should match");
        }
    }

    @Then("the validation should fail with appropriate error messages")
    public void theValidationShouldFailWithAppropriateErrorMessages() {
        ValidationResult validationResult = (ValidationResult) testContext.get("validationResult");
        assertNotNull(validationResult, "Validation result should be in test context");
        
        assertFalse(validationResult.isValid(), "Validation should fail");
        assertFalse(validationResult.getErrors().isEmpty(), "Validation should have errors");
        
        // Print errors for debugging
        List<String> errors = validationResult.getErrors();
        logger.debug("Validation errors: {}", String.join(", ", errors));
    }

    @Then("the entity should not be saved")
    public void theEntityShouldNotBeSaved() {
        Boolean entitySaved = (Boolean) testContext.get("entitySaved");
        assertNotNull(entitySaved, "Entity saved flag should be in test context");
        
        assertFalse(entitySaved, "Entity should not be saved");
        
        // Verify entity doesn't exist in persistence
        String entityType = (String) testContext.get("entityType");
        @SuppressWarnings("unchecked")
        Map<String, Object> entity = (Map<String, Object>) testContext.get("entity");
        String entityId = (String) entity.get("id");
        
        PersistenceResult result = persistencePort.findById(entityType, entityId);
        assertFalse(result.isSuccessful(), "Entity should not be found in persistence");
    }

    @Then("the entity should be successfully updated")
    public void theEntityShouldBeSuccessfullyUpdated() {
        Boolean entityUpdated = (Boolean) testContext.get("entityUpdated");
        assertNotNull(entityUpdated, "Entity updated flag should be in test context");
        
        assertTrue(entityUpdated, "Entity should be updated successfully");
        
        PersistenceResult updateResult = (PersistenceResult) testContext.get("updateResult");
        assertNotNull(updateResult, "Update result should be in test context");
        assertTrue(updateResult.isSuccessful(), "Update operation should be successful");
    }

    @Then("the retrieved entity should contain the updated values")
    public void theRetrievedEntityShouldContainTheUpdatedValues() {
        String entityType = (String) testContext.get("entityType");
        String entityId = (String) testContext.get("entityId");
        @SuppressWarnings("unchecked")
        Map<String, Object> updatedEntity = (Map<String, Object>) testContext.get("updatedEntity");
        
        // Retrieve the entity
        PersistenceResult result = persistencePort.findById(entityType, entityId);
        
        assertTrue(result.isSuccessful(), "Entity should be retrieved successfully");
        assertNotNull(result.getValue(), "Retrieved entity should not be null");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> retrievedEntity = (Map<String, Object>) result.getValue();
        
        // Check that updated fields match
        for (Map.Entry<String, Object> entry : updatedEntity.entrySet()) {
            assertEquals(entry.getValue(), retrievedEntity.get(entry.getKey()), 
                    "Field " + entry.getKey() + " should be updated");
        }
    }

    @Then("the entity should not be updated")
    public void theEntityShouldNotBeUpdated() {
        Boolean entityUpdated = (Boolean) testContext.get("entityUpdated");
        assertNotNull(entityUpdated, "Entity updated flag should be in test context");
        
        assertFalse(entityUpdated, "Entity should not be updated");
        
        // Verify entity wasn't changed
        String entityType = (String) testContext.get("entityType");
        String entityId = (String) testContext.get("entityId");
        @SuppressWarnings("unchecked")
        Map<String, Object> originalEntity = (Map<String, Object>) testContext.get("originalEntity");
        
        PersistenceResult result = persistencePort.findById(entityType, entityId);
        assertTrue(result.isSuccessful(), "Entity should still exist in persistence");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> retrievedEntity = (Map<String, Object>) result.getValue();
        
        // Check that all fields match original
        for (Map.Entry<String, Object> entry : originalEntity.entrySet()) {
            assertEquals(entry.getValue(), retrievedEntity.get(entry.getKey()), 
                    "Field " + entry.getKey() + " should match original");
        }
    }

    @Then("the search should return {int} entities")
    public void theSearchShouldReturnEntities(int expectedCount) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> results = (List<Map<String, Object>>) testContext.get("searchResults");
        assertNotNull(results, "Search results should be in test context");
        
        assertEquals(expectedCount, results.size(), "Search should return expected number of entities");
    }

    @Then("all returned entities should be valid according to validation rules")
    public void allReturnedEntitiesShouldBeValidAccordingToValidationRules() {
        String entityType = (String) testContext.get("entityType");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> results = (List<Map<String, Object>>) testContext.get("searchResults");
        assertNotNull(results, "Search results should be in test context");
        
        for (Map<String, Object> entity : results) {
            ValidationResult validationResult = validationService.validateEntity(entityType, entity);
            assertTrue(validationResult.isValid(), "All returned entities should be valid: " + entity.get("id"));
        }
    }

    @Then("only valid entities should be persisted")
    public void onlyValidEntitiesShouldBePersisted() {
        String entityType = (String) testContext.get("entityType");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> validEntities = (List<Map<String, Object>>) testContext.get("validEntities");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> invalidEntities = (List<Map<String, Object>>) testContext.get("invalidEntities");
        @SuppressWarnings("unchecked")
        List<String> persistedIds = (List<String>) testContext.get("persistedIds");
        
        // Check that all valid entities were persisted
        assertEquals(validEntities.size(), persistedIds.size(), 
                "All valid entities should be persisted");
        
        // Check that invalid entities weren't persisted
        for (Map<String, Object> invalidEntity : invalidEntities) {
            String invalidId = (String) invalidEntity.get("id");
            PersistenceResult result = persistencePort.findById(entityType, invalidId);
            assertFalse(result.isSuccessful(), "Invalid entity should not be persisted: " + invalidId);
        }
    }

    @Then("the system should report {int} successful and {int} failed operations")
    public void theSystemShouldReportSuccessfulAndFailedOperations(int successCount, int failureCount) {
        int actualSuccessCount = (int) testContext.get("successCount");
        int actualFailureCount = (int) testContext.get("failureCount");
        
        assertEquals(successCount, actualSuccessCount, "Success count should match expected");
        assertEquals(failureCount, actualFailureCount, "Failure count should match expected");
    }
    
    /**
     * Test implementation of the InMemoryPersistenceAdapter for validation-persistence integration testing.
     */
    private class InMemoryPersistenceAdapter implements PersistencePort {
        private final LoggerPort logger;
        private final Map<String, Map<String, Map<String, Object>>> storage;
        
        public InMemoryPersistenceAdapter(LoggerPort logger) {
            this.logger = logger;
            this.storage = new HashMap<>();
        }
        
        @Override
        public PersistenceResult save(String entityType, String id, Map<String, Object> entity) {
            storage.computeIfAbsent(entityType, k -> new HashMap<>());
            Map<String, Map<String, Object>> entityStore = storage.get(entityType);
            
            // Store a copy to avoid side effects
            Map<String, Object> entityCopy = new HashMap<>(entity);
            entityStore.put(id, entityCopy);
            
            logger.debug("Entity saved: {}/{}", entityType, id);
            return PersistenceResult.success(id, "Entity saved successfully");
        }
        
        @Override
        public PersistenceResult update(String entityType, String id, Map<String, Object> entity) {
            Map<String, Map<String, Object>> entityStore = storage.get(entityType);
            if (entityStore == null || !entityStore.containsKey(id)) {
                logger.debug("Entity not found for update: {}/{}", entityType, id);
                return PersistenceResult.failure("Entity not found", "not_found");
            }
            
            // Store a copy to avoid side effects
            Map<String, Object> entityCopy = new HashMap<>(entity);
            entityStore.put(id, entityCopy);
            
            logger.debug("Entity updated: {}/{}", entityType, id);
            return PersistenceResult.success(id, "Entity updated successfully");
        }
        
        @Override
        public PersistenceResult delete(String entityType, String id) {
            Map<String, Map<String, Object>> entityStore = storage.get(entityType);
            if (entityStore == null || !entityStore.containsKey(id)) {
                logger.debug("Entity not found for deletion: {}/{}", entityType, id);
                return PersistenceResult.failure("Entity not found", "not_found");
            }
            
            entityStore.remove(id);
            logger.debug("Entity deleted: {}/{}", entityType, id);
            return PersistenceResult.success(id, "Entity deleted successfully");
        }
        
        @Override
        public PersistenceResult findById(String entityType, String id) {
            Map<String, Map<String, Object>> entityStore = storage.get(entityType);
            if (entityStore == null || !entityStore.containsKey(id)) {
                logger.debug("Entity not found: {}/{}", entityType, id);
                return PersistenceResult.failure("Entity not found", "not_found");
            }
            
            // Return a copy to avoid side effects
            Map<String, Object> entity = new HashMap<>(entityStore.get(id));
            logger.debug("Entity found: {}/{}", entityType, id);
            return PersistenceResult.success(entity, "Entity found");
        }
        
        @Override
        public PersistenceResult findAll(String entityType) {
            Map<String, Map<String, Object>> entityStore = storage.get(entityType);
            if (entityStore == null) {
                logger.debug("No entities found for type: {}", entityType);
                return PersistenceResult.success(new ArrayList<>(), "No entities found");
            }
            
            // Return copies to avoid side effects
            List<Map<String, Object>> entities = entityStore.values().stream()
                    .map(HashMap::new)
                    .collect(Collectors.toList());
            
            logger.debug("Found {} entities of type: {}", entities.size(), entityType);
            return PersistenceResult.success(entities, "Entities found");
        }
        
        @Override
        public PersistenceResult findByCriteria(String entityType, Map<String, Object> criteria) {
            Map<String, Map<String, Object>> entityStore = storage.get(entityType);
            if (entityStore == null) {
                logger.debug("No entities found for type: {}", entityType);
                return PersistenceResult.success(new ArrayList<>(), "No entities found");
            }
            
            // Filter entities based on criteria
            List<Map<String, Object>> matchingEntities = entityStore.values().stream()
                    .filter(entity -> matchesCriteria(entity, criteria))
                    .map(HashMap::new) // Return copies to avoid side effects
                    .collect(Collectors.toList());
            
            logger.debug("Found {} entities matching criteria for type: {}", matchingEntities.size(), entityType);
            return PersistenceResult.success(matchingEntities, "Matching entities found");
        }
        
        @Override
        public boolean exists(String entityType, String id) {
            Map<String, Map<String, Object>> entityStore = storage.get(entityType);
            return entityStore != null && entityStore.containsKey(id);
        }
        
        @Override
        public StorageType getStorageType() {
            return StorageType.MEMORY;
        }
        
        /**
         * Check if an entity matches the given criteria.
         */
        private boolean matchesCriteria(Map<String, Object> entity, Map<String, Object> criteria) {
            for (Map.Entry<String, Object> entry : criteria.entrySet()) {
                // Special case for minAge
                if (entry.getKey().equals("minAge")) {
                    if (!entity.containsKey("age") || !(entity.get("age") instanceof Integer)) {
                        return false;
                    }
                    int age = (Integer) entity.get("age");
                    int minAge = (Integer) entry.getValue();
                    if (age < minAge) {
                        return false;
                    }
                    continue;
                }
                
                // Regular equality check
                if (!entity.containsKey(entry.getKey()) || 
                        !Optional.ofNullable(entity.get(entry.getKey()))
                               .equals(Optional.ofNullable(entry.getValue()))) {
                    return false;
                }
            }
            return true;
        }
    }
}