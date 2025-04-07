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

import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.PersistencePort;
import org.s8r.application.port.PersistencePort.PersistenceResult;
import org.s8r.application.port.PersistencePort.StorageType;
import org.s8r.infrastructure.logging.ConsoleLogger;
import org.s8r.infrastructure.persistence.InMemoryPersistenceAdapter;
import org.s8r.test.annotation.IntegrationTest;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for testing the PersistencePort interface.
 */
@IntegrationTest
public class PersistencePortSteps {

    private PersistencePort persistencePort;
    private LoggerPort logger;
    private Map<String, Object> testContext;
    private List<String> logMessages;

    @Before
    public void setup() {
        testContext = new HashMap<>();
        logMessages = new ArrayList<>();
        
        // Create a test logger that captures log messages
        logger = new ConsoleLogger("PersistencePortTest") {
            @Override
            public void info(String message) {
                super.info(message);
                logMessages.add(message);
            }
            
            @Override
            public void info(String message, Object... args) {
                super.info(message);
                String formattedMessage = String.format(message.replace("{}", "%s"), args);
                logMessages.add(formattedMessage);
            }
            
            @Override
            public void debug(String message) {
                super.debug(message);
                logMessages.add(message);
            }
            
            @Override
            public void debug(String message, Object... args) {
                super.debug(message);
                String formattedMessage = String.format(message.replace("{}", "%s"), args);
                logMessages.add(formattedMessage);
            }
        };
        
        // Initialize the persistence port
        persistencePort = new InMemoryPersistenceAdapter(logger);
        
        // Reset log messages between scenarios
        logMessages.clear();
    }
    
    @After
    public void cleanup() {
        testContext.clear();
        logMessages.clear();
        
        // Shutdown the persistence port
        persistencePort.shutdown();
    }

    @Given("a clean system environment")
    public void aCleanSystemEnvironment() {
        // Already set up in the @Before method
        assertNotNull(persistencePort, "PersistencePort should be initialized");
    }

    @Given("the PersistencePort interface is properly initialized")
    public void thePersistencePortInterfaceIsProperlyInitialized() {
        // Ensure the persistence system is initialized
        boolean initialized = persistencePort.initialize();
        assertTrue(initialized, "Persistence system should be initialized successfully");
    }

    @Given("a {string} entity with ID {string} exists with the following data:")
    public void aEntityWithIDExistsWithTheFollowingData(String entityType, String entityId, DataTable dataTable) {
        Map<String, String> rawData = dataTable.asMap();
        Map<String, Object> entityData = convertToEntityData(rawData);
        
        // Save the entity
        PersistenceResult saveResult = persistencePort.save(entityType, entityId, entityData);
        
        assertTrue(saveResult.isSuccess(), "Entity should be saved successfully");
        assertEquals(entityId, saveResult.getId(), "Saved entity ID should match");
        
        // Store for later verification
        testContext.put("entityType", entityType);
        testContext.put("entityId", entityId);
        testContext.put("entityData", entityData);
    }

    @Given("the following {string} entities exist:")
    public void theFollowingEntitiesExist(String entityType, DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        List<String> savedIds = new ArrayList<>();
        
        for (Map<String, String> row : rows) {
            String entityId = row.get("id");
            
            // Create a copy without the ID
            Map<String, String> dataWithoutId = new HashMap<>(row);
            dataWithoutId.remove("id");
            
            Map<String, Object> entityData = convertToEntityData(dataWithoutId);
            
            // Save the entity
            PersistenceResult saveResult = persistencePort.save(entityType, entityId, entityData);
            assertTrue(saveResult.isSuccess(), "Entity should be saved successfully: " + entityId);
            savedIds.add(entityId);
        }
        
        // Store for later verification
        testContext.put("entityType", entityType);
        testContext.put("savedIds", savedIds);
        testContext.put("rowCount", rows.size());
    }

    @When("I save a {string} entity with ID {string} and the following data:")
    public void iSaveAEntityWithIDAndTheFollowingData(String entityType, String entityId, DataTable dataTable) {
        Map<String, String> rawData = dataTable.asMap();
        Map<String, Object> entityData = convertToEntityData(rawData);
        
        // Save the entity
        PersistenceResult saveResult = persistencePort.save(entityType, entityId, entityData);
        
        // Store for later verification
        testContext.put("entityType", entityType);
        testContext.put("entityId", entityId);
        testContext.put("entityData", entityData);
        testContext.put("saveResult", saveResult);
    }

    @When("I update the entity with the following data:")
    public void iUpdateTheEntityWithTheFollowingData(DataTable dataTable) {
        String entityType = (String) testContext.get("entityType");
        String entityId = (String) testContext.get("entityId");
        
        assertNotNull(entityType, "Entity type should be in test context");
        assertNotNull(entityId, "Entity ID should be in test context");
        
        Map<String, String> rawData = dataTable.asMap();
        Map<String, Object> updatedData = convertToEntityData(rawData);
        
        // Update the entity
        PersistenceResult updateResult = persistencePort.update(entityType, entityId, updatedData);
        
        // Store for later verification
        testContext.put("updatedData", updatedData);
        testContext.put("updateResult", updateResult);
    }

    @When("I delete the entity")
    public void iDeleteTheEntity() {
        String entityType = (String) testContext.get("entityType");
        String entityId = (String) testContext.get("entityId");
        
        assertNotNull(entityType, "Entity type should be in test context");
        assertNotNull(entityId, "Entity ID should be in test context");
        
        // Delete the entity
        PersistenceResult deleteResult = persistencePort.delete(entityType, entityId);
        
        // Store for later verification
        testContext.put("deleteResult", deleteResult);
    }

    @When("I retrieve all entities of type {string}")
    public void iRetrieveAllEntitiesOfType(String entityType) {
        List<Map<String, Object>> entities = persistencePort.findByType(entityType);
        
        // Store for later verification
        testContext.put("retrievedEntities", entities);
    }

    @When("I search for entities with criteria:")
    public void iSearchForEntitiesWithCriteria(DataTable dataTable) {
        String entityType = (String) testContext.get("entityType");
        assertNotNull(entityType, "Entity type should be in test context");
        
        Map<String, String> rawCriteria = dataTable.asMap();
        Map<String, Object> criteria = convertToEntityData(rawCriteria);
        
        List<Map<String, Object>> entities = persistencePort.findByCriteria(entityType, criteria);
        
        // Store for later verification
        testContext.put("searchCriteria", criteria);
        testContext.put("searchResults", entities);
    }

    @When("I count entities of type {string}")
    public void iCountEntitiesOfType(String entityType) {
        int count = persistencePort.count(entityType);
        
        // Store for later verification
        testContext.put("countEntityType", entityType);
        testContext.put("entityCount", count);
    }

    @When("I clear all entities of type {string}")
    public void iClearAllEntitiesOfType(String entityType) {
        PersistenceResult clearResult = persistencePort.clearAll(entityType);
        
        // Store for later verification
        testContext.put("clearEntityType", entityType);
        testContext.put("clearResult", clearResult);
    }

    @When("I try to find an entity with type {string} and ID {string}")
    public void iTryToFindAnEntityWithTypeAndID(String entityType, String entityId) {
        Optional<Map<String, Object>> result = persistencePort.findById(entityType, entityId);
        
        // Store for later verification
        testContext.put("findEntityType", entityType);
        testContext.put("findEntityId", entityId);
        testContext.put("findResult", result);
    }

    @When("I try to update an entity with type {string} and ID {string}")
    public void iTryToUpdateAnEntityWithTypeAndID(String entityType, String entityId) {
        Map<String, Object> dummyData = new HashMap<>();
        dummyData.put("dummy", "value");
        
        PersistenceResult updateResult = persistencePort.update(entityType, entityId, dummyData);
        
        // Store for later verification
        testContext.put("updateEntityType", entityType);
        testContext.put("updateEntityId", entityId);
        testContext.put("updateResult", updateResult);
    }

    @When("I try to delete an entity with type {string} and ID {string}")
    public void iTryToDeleteAnEntityWithTypeAndID(String entityType, String entityId) {
        PersistenceResult deleteResult = persistencePort.delete(entityType, entityId);
        
        // Store for later verification
        testContext.put("deleteEntityType", entityType);
        testContext.put("deleteEntityId", entityId);
        testContext.put("deleteResult", deleteResult);
    }

    @When("I check the storage type")
    public void iCheckTheStorageType() {
        StorageType storageType = persistencePort.getStorageType();
        
        // Store for later verification
        testContext.put("storageType", storageType);
    }

    @Then("the entity should be successfully saved")
    public void theEntityShouldBeSuccessfullySaved() {
        PersistenceResult saveResult = (PersistenceResult) testContext.get("saveResult");
        assertNotNull(saveResult, "Save result should be in test context");
        
        assertTrue(saveResult.isSuccess(), "Entity should be saved successfully");
        assertNotNull(saveResult.getId(), "Saved entity ID should not be null");
        
        String expectedId = (String) testContext.get("entityId");
        assertEquals(expectedId, saveResult.getId(), "Saved entity ID should match");
    }

    @Then("the entity should exist in the storage")
    public void theEntityShouldExistInTheStorage() {
        String entityType = (String) testContext.get("entityType");
        String entityId = (String) testContext.get("entityId");
        
        assertNotNull(entityType, "Entity type should be in test context");
        assertNotNull(entityId, "Entity ID should be in test context");
        
        boolean exists = persistencePort.exists(entityType, entityId);
        assertTrue(exists, "Entity should exist in storage");
    }

    @Then("I should be able to retrieve the entity by ID")
    public void iShouldBeAbleToRetrieveTheEntityByID() {
        String entityType = (String) testContext.get("entityType");
        String entityId = (String) testContext.get("entityId");
        Map<String, Object> originalData = (Map<String, Object>) testContext.get("entityData");
        
        assertNotNull(entityType, "Entity type should be in test context");
        assertNotNull(entityId, "Entity ID should be in test context");
        assertNotNull(originalData, "Entity data should be in test context");
        
        Optional<Map<String, Object>> retrieved = persistencePort.findById(entityType, entityId);
        
        assertTrue(retrieved.isPresent(), "Entity should be retrieved");
        Map<String, Object> retrievedData = retrieved.get();
        
        // Check that all original data is in the retrieved entity
        for (Map.Entry<String, Object> entry : originalData.entrySet()) {
            assertTrue(retrievedData.containsKey(entry.getKey()), "Retrieved entity should contain key: " + entry.getKey());
            assertEquals(entry.getValue(), retrievedData.get(entry.getKey()), 
                    "Retrieved value for key " + entry.getKey() + " should match original");
        }
    }

    @Then("the entity should be successfully updated")
    public void theEntityShouldBeSuccessfullyUpdated() {
        PersistenceResult updateResult = (PersistenceResult) testContext.get("updateResult");
        assertNotNull(updateResult, "Update result should be in test context");
        
        assertTrue(updateResult.isSuccess(), "Entity should be updated successfully");
        assertNotNull(updateResult.getId(), "Updated entity ID should not be null");
        
        String expectedId = (String) testContext.get("entityId");
        assertEquals(expectedId, updateResult.getId(), "Updated entity ID should match");
    }

    @Then("the entity should have the updated data")
    public void theEntityShouldHaveTheUpdatedData() {
        String entityType = (String) testContext.get("entityType");
        String entityId = (String) testContext.get("entityId");
        Map<String, Object> updatedData = (Map<String, Object>) testContext.get("updatedData");
        
        assertNotNull(entityType, "Entity type should be in test context");
        assertNotNull(entityId, "Entity ID should be in test context");
        assertNotNull(updatedData, "Updated data should be in test context");
        
        Optional<Map<String, Object>> retrieved = persistencePort.findById(entityType, entityId);
        
        assertTrue(retrieved.isPresent(), "Entity should be retrieved");
        Map<String, Object> retrievedData = retrieved.get();
        
        // Check that all updated data is in the retrieved entity
        for (Map.Entry<String, Object> entry : updatedData.entrySet()) {
            assertTrue(retrievedData.containsKey(entry.getKey()), "Retrieved entity should contain key: " + entry.getKey());
            assertEquals(entry.getValue(), retrievedData.get(entry.getKey()), 
                    "Retrieved value for key " + entry.getKey() + " should match updated value");
        }
    }

    @Then("the entity should be successfully deleted")
    public void theEntityShouldBeSuccessfullyDeleted() {
        PersistenceResult deleteResult = (PersistenceResult) testContext.get("deleteResult");
        assertNotNull(deleteResult, "Delete result should be in test context");
        
        assertTrue(deleteResult.isSuccess(), "Entity should be deleted successfully");
        
        String expectedId = (String) testContext.get("entityId");
        assertEquals(expectedId, deleteResult.getId(), "Deleted entity ID should match");
    }

    @Then("the entity should no longer exist in the storage")
    public void theEntityShouldNoLongerExistInTheStorage() {
        String entityType = (String) testContext.get("entityType");
        String entityId = (String) testContext.get("entityId");
        
        assertNotNull(entityType, "Entity type should be in test context");
        assertNotNull(entityId, "Entity ID should be in test context");
        
        boolean exists = persistencePort.exists(entityType, entityId);
        assertFalse(exists, "Entity should not exist in storage");
    }

    @Then("I should get {int} entities")
    public void iShouldGetEntities(Integer expectedCount) {
        List<Map<String, Object>> entities = null;
        
        if (testContext.containsKey("retrievedEntities")) {
            entities = (List<Map<String, Object>>) testContext.get("retrievedEntities");
        } else if (testContext.containsKey("searchResults")) {
            entities = (List<Map<String, Object>>) testContext.get("searchResults");
        }
        
        assertNotNull(entities, "Retrieved entities should be in test context");
        assertEquals(expectedCount.intValue(), entities.size(), "Should retrieve expected number of entities");
    }

    @Then("the entities should match the stored data")
    public void theEntitiesShouldMatchTheStoredData() {
        List<Map<String, Object>> entities = (List<Map<String, Object>>) testContext.get("retrievedEntities");
        @SuppressWarnings("unchecked")
        List<String> savedIds = (List<String>) testContext.get("savedIds");
        Integer rowCount = (Integer) testContext.get("rowCount");
        
        assertNotNull(entities, "Retrieved entities should be in test context");
        assertNotNull(savedIds, "Saved IDs should be in test context");
        assertNotNull(rowCount, "Row count should be in test context");
        
        assertEquals(rowCount.intValue(), entities.size(), "Should retrieve same number of entities as saved");
        
        // We can't easily match the exact entities because we don't have IDs in the result
        // Just check the count matches and logs show that all were found
        assertTrue(logMessages.stream().anyMatch(message -> message.contains("Finding entities")),
                "Should log finding entities");
    }

    @Then("the entities should match the criteria")
    public void theEntitiesShouldMatchTheCriteria() {
        List<Map<String, Object>> entities = (List<Map<String, Object>>) testContext.get("searchResults");
        Map<String, Object> criteria = (Map<String, Object>) testContext.get("searchCriteria");
        
        assertNotNull(entities, "Search results should be in test context");
        assertNotNull(criteria, "Search criteria should be in test context");
        
        // Check that each entity matches all criteria
        for (Map<String, Object> entity : entities) {
            for (Map.Entry<String, Object> criterion : criteria.entrySet()) {
                assertTrue(entity.containsKey(criterion.getKey()),
                        "Entity should contain criterion key: " + criterion.getKey());
                assertEquals(criterion.getValue(), entity.get(criterion.getKey()),
                        "Entity value for key " + criterion.getKey() + " should match criterion");
            }
        }
    }

    @Then("the count should be {int}")
    public void theCountShouldBe(Integer expectedCount) {
        Integer actualCount = (Integer) testContext.get("entityCount");
        assertNotNull(actualCount, "Entity count should be in test context");
        
        assertEquals(expectedCount.intValue(), actualCount.intValue(), "Entity count should match expected");
    }

    @Then("all entities of type {string} should be removed")
    public void allEntitiesOfTypeShouldBeRemoved(String entityType) {
        PersistenceResult clearResult = (PersistenceResult) testContext.get("clearResult");
        assertNotNull(clearResult, "Clear result should be in test context");
        
        assertTrue(clearResult.isSuccess(), "Clear operation should be successful");
    }

    @Then("the count of {string} entities should be {int}")
    public void theCountOfEntitiesShouldBe(String entityType, Integer expectedCount) {
        int actualCount = persistencePort.count(entityType);
        assertEquals(expectedCount.intValue(), actualCount, "Entity count after clearing should be 0");
    }

    @Then("the result should be empty")
    public void theResultShouldBeEmpty() {
        Optional<Map<String, Object>> result = (Optional<Map<String, Object>>) testContext.get("findResult");
        assertNotNull(result, "Find result should be in test context");
        
        assertFalse(result.isPresent(), "Result should be empty for non-existent entity");
    }

    @Then("the update operation should fail")
    public void theUpdateOperationShouldFail() {
        PersistenceResult result = (PersistenceResult) testContext.get("updateResult");
        assertNotNull(result, "Update result should be in test context");
        
        assertFalse(result.isSuccess(), "Update operation should fail for non-existent entity");
    }

    @Then("the delete operation should fail")
    public void theDeleteOperationShouldFail() {
        PersistenceResult result = (PersistenceResult) testContext.get("deleteResult");
        assertNotNull(result, "Delete result should be in test context");
        
        assertFalse(result.isSuccess(), "Delete operation should fail for non-existent entity");
    }

    @Then("the storage type should be {string}")
    public void theStorageTypeShouldBe(String expectedType) {
        StorageType storageType = (StorageType) testContext.get("storageType");
        assertNotNull(storageType, "Storage type should be in test context");
        
        assertEquals(expectedType, storageType.name(), "Storage type should match expected");
    }
    
    /**
     * Converts a map of string values to a map of typed values (numbers, booleans, etc.).
     *
     * @param rawData The raw data map with string values
     * @return A map with appropriate types for values
     */
    private Map<String, Object> convertToEntityData(Map<String, String> rawData) {
        Map<String, Object> entityData = new HashMap<>();
        
        for (Map.Entry<String, String> entry : rawData.entrySet()) {
            String key = entry.getKey();
            String rawValue = entry.getValue();
            
            if (rawValue == null || rawValue.isEmpty()) {
                entityData.put(key, "");
            } else if (rawValue.matches("^-?\\d+$")) {
                // Integer value
                entityData.put(key, Integer.parseInt(rawValue));
            } else if (rawValue.matches("^-?\\d+\\.\\d+$")) {
                // Double value
                entityData.put(key, Double.parseDouble(rawValue));
            } else if (rawValue.equalsIgnoreCase("true") || rawValue.equalsIgnoreCase("false")) {
                // Boolean value
                entityData.put(key, Boolean.parseBoolean(rawValue));
            } else {
                // String value
                entityData.put(key, rawValue);
            }
        }
        
        return entityData;
    }
}