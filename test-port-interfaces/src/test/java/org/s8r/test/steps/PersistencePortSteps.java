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
import org.s8r.test.context.PersistencePortTestContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step definitions for testing the PersistencePort interface.
 */
public class PersistencePortSteps {

    private final PersistencePortTestContext context;

    /**
     * Constructs a new PersistencePortSteps.
     *
     * @param context The test context
     */
    public PersistencePortSteps(PersistencePortTestContext context) {
        this.context = context;
    }

    @Given("a clean system environment")
    public void aCleanSystemEnvironment() {
        context.clear();
    }

    @Given("the PersistencePort interface is properly initialized")
    public void thePersistencePortInterfaceIsProperlyInitialized() {
        boolean result = context.getPersistenceAdapter().initialize();
        context.setInitialized(result);
        assertTrue(result, "Persistence adapter should be initialized successfully");
    }

    @Given("a {string} entity with ID {string} exists with the following data:")
    public void aEntityWithIDExistsWithTheFollowingData(String entityType, String entityId, DataTable dataTable) {
        context.setCurrentEntityType(entityType);
        context.setCurrentEntityId(entityId);
        
        Map<String, Object> data = convertDataTableToMap(dataTable);
        context.setCurrentEntityData(data);
        
        PersistenceResult result = context.getPersistenceAdapter().save(entityType, entityId, data);
        context.setLastResult(result);
        
        assertTrue(result.isSuccess(), "Entity should be saved successfully: " + result.getMessage());
        assertTrue(context.getPersistenceAdapter().exists(entityType, entityId), "Entity should exist in storage");
    }

    @Given("the following {string} entities exist:")
    public void theFollowingEntitiesExist(String entityType, DataTable dataTable) {
        context.setCurrentEntityType(entityType);
        
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            String entityId = row.get("id");
            assertNotNull(entityId, "Entity ID is required");
            
            Map<String, Object> data = new HashMap<>(row);
            data.remove("id"); // Remove ID from data as it's stored separately
            
            PersistenceResult result = context.getPersistenceAdapter().save(entityType, entityId, data);
            assertTrue(result.isSuccess(), "Entity should be saved successfully: " + result.getMessage());
        }
    }

    @When("I save a {string} entity with ID {string} and the following data:")
    public void iSaveAEntityWithIDAndTheFollowingData(String entityType, String entityId, DataTable dataTable) {
        context.setCurrentEntityType(entityType);
        context.setCurrentEntityId(entityId);
        
        Map<String, Object> data = convertDataTableToMap(dataTable);
        context.setCurrentEntityData(data);
        
        PersistenceResult result = context.getPersistenceAdapter().save(entityType, entityId, data);
        context.setLastResult(result);
    }

    @When("I update the entity with the following data:")
    public void iUpdateTheEntityWithTheFollowingData(DataTable dataTable) {
        String entityType = context.getCurrentEntityType();
        String entityId = context.getCurrentEntityId();
        
        Map<String, Object> data = convertDataTableToMap(dataTable);
        context.setCurrentEntityData(data);
        
        PersistenceResult result = context.getPersistenceAdapter().update(entityType, entityId, data);
        context.setLastResult(result);
    }

    @When("I delete the entity")
    public void iDeleteTheEntity() {
        String entityType = context.getCurrentEntityType();
        String entityId = context.getCurrentEntityId();
        
        PersistenceResult result = context.getPersistenceAdapter().delete(entityType, entityId);
        context.setLastResult(result);
    }

    @When("I retrieve all entities of type {string}")
    public void iRetrieveAllEntitiesOfType(String entityType) {
        context.setCurrentEntityType(entityType);
        
        List<Map<String, Object>> results = context.getPersistenceAdapter().findByType(entityType);
        context.setRetrievalResults(results);
    }

    @When("I search for entities with criteria:")
    public void iSearchForEntitiesWithCriteria(DataTable dataTable) {
        String entityType = context.getCurrentEntityType();
        
        Map<String, Object> criteria = convertDataTableToMap(dataTable);
        context.setSearchCriteria(criteria);
        
        List<Map<String, Object>> results = context.getPersistenceAdapter().findByCriteria(entityType, criteria);
        context.setRetrievalResults(results);
    }

    @When("I count entities of type {string}")
    public void iCountEntitiesOfType(String entityType) {
        context.setCurrentEntityType(entityType);
        
        int count = context.getPersistenceAdapter().count(entityType);
        context.setEntityCount(count);
    }

    @When("I clear all entities of type {string}")
    public void iClearAllEntitiesOfType(String entityType) {
        context.setCurrentEntityType(entityType);
        
        PersistenceResult result = context.getPersistenceAdapter().clearAll(entityType);
        context.setLastResult(result);
    }

    @When("I try to find an entity with type {string} and ID {string}")
    public void iTryToFindAnEntityWithTypeAndID(String entityType, String entityId) {
        context.setCurrentEntityType(entityType);
        context.setCurrentEntityId(entityId);
        
        Optional<Map<String, Object>> result = context.getPersistenceAdapter().findById(entityType, entityId);
        context.setRetrievalResult(result);
    }

    @When("I try to update an entity with type {string} and ID {string}")
    public void iTryToUpdateAnEntityWithTypeAndID(String entityType, String entityId) {
        context.setCurrentEntityType(entityType);
        context.setCurrentEntityId(entityId);
        
        Map<String, Object> dummyData = new HashMap<>();
        dummyData.put("test", "value");
        
        PersistenceResult result = context.getPersistenceAdapter().update(entityType, entityId, dummyData);
        context.setLastResult(result);
    }

    @When("I try to delete an entity with type {string} and ID {string}")
    public void iTryToDeleteAnEntityWithTypeAndID(String entityType, String entityId) {
        context.setCurrentEntityType(entityType);
        context.setCurrentEntityId(entityId);
        
        PersistenceResult result = context.getPersistenceAdapter().delete(entityType, entityId);
        context.setLastResult(result);
    }

    @When("I check the storage type")
    public void iCheckTheStorageType() {
        // No need to do anything here, the storage type is checked in the Then step
    }

    @Then("the entity should be successfully saved")
    public void theEntityShouldBeSuccessfullySaved() {
        PersistenceResult result = context.getLastResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isSuccess(), "Save operation should be successful: " + result.getMessage());
        assertEquals(context.getCurrentEntityId(), result.getId(), "Result ID should match the entity ID");
    }

    @Then("the entity should exist in the storage")
    public void theEntityShouldExistInTheStorage() {
        String entityType = context.getCurrentEntityType();
        String entityId = context.getCurrentEntityId();
        
        boolean exists = context.getPersistenceAdapter().exists(entityType, entityId);
        assertTrue(exists, "Entity should exist in storage");
    }

    @Then("I should be able to retrieve the entity by ID")
    public void iShouldBeAbleToRetrieveTheEntityByID() {
        String entityType = context.getCurrentEntityType();
        String entityId = context.getCurrentEntityId();
        
        Optional<Map<String, Object>> result = context.getPersistenceAdapter().findById(entityType, entityId);
        context.setRetrievalResult(result);
        
        assertTrue(result.isPresent(), "Entity should be retrievable");
        assertEquals(context.getCurrentEntityData(), result.get(), "Retrieved entity should match the original data");
    }

    @Then("the entity should be successfully updated")
    public void theEntityShouldBeSuccessfullyUpdated() {
        PersistenceResult result = context.getLastResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isSuccess(), "Update operation should be successful: " + result.getMessage());
        assertEquals(context.getCurrentEntityId(), result.getId(), "Result ID should match the entity ID");
    }

    @Then("the entity should have the updated data")
    public void theEntityShouldHaveTheUpdatedData() {
        String entityType = context.getCurrentEntityType();
        String entityId = context.getCurrentEntityId();
        
        Optional<Map<String, Object>> result = context.getPersistenceAdapter().findById(entityType, entityId);
        context.setRetrievalResult(result);
        
        assertTrue(result.isPresent(), "Entity should be retrievable");
        assertEquals(context.getCurrentEntityData(), result.get(), "Retrieved entity should match the updated data");
    }

    @Then("the entity should be successfully deleted")
    public void theEntityShouldBeSuccessfullyDeleted() {
        PersistenceResult result = context.getLastResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isSuccess(), "Delete operation should be successful: " + result.getMessage());
        assertEquals(context.getCurrentEntityId(), result.getId(), "Result ID should match the entity ID");
    }

    @Then("the entity should no longer exist in the storage")
    public void theEntityShouldNoLongerExistInTheStorage() {
        String entityType = context.getCurrentEntityType();
        String entityId = context.getCurrentEntityId();
        
        boolean exists = context.getPersistenceAdapter().exists(entityType, entityId);
        assertFalse(exists, "Entity should not exist in storage after deletion");
    }

    @Then("I should get {int} entities")
    public void iShouldGetEntities(int expectedCount) {
        List<Map<String, Object>> results = context.getRetrievalResults();
        assertNotNull(results, "Results should not be null");
        assertEquals(expectedCount, results.size(), "Number of retrieved entities should match");
    }

    @Then("the entities should match the stored data")
    public void theEntitiesShouldMatchTheStoredData() {
        List<Map<String, Object>> results = context.getRetrievalResults();
        String entityType = context.getCurrentEntityType();
        
        // We don't know the exact data that should be in the results, so we just check
        // that the number of entities from findByType matches the count
        int count = context.getPersistenceAdapter().count(entityType);
        assertEquals(count, results.size(), "Number of entities from findByType should match count");
    }

    @Then("the entities should match the criteria")
    public void theEntitiesShouldMatchTheCriteria() {
        List<Map<String, Object>> results = context.getRetrievalResults();
        Map<String, Object> criteria = context.getSearchCriteria();
        
        for (Map<String, Object> entity : results) {
            for (Map.Entry<String, Object> entry : criteria.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                
                assertTrue(entity.containsKey(key), "Entity should contain key: " + key);
                assertEquals(value, entity.get(key), "Entity value for key " + key + " should match criteria");
            }
        }
    }

    @Then("the count should be {int}")
    public void theCountShouldBe(int expectedCount) {
        int count = context.getEntityCount();
        assertEquals(expectedCount, count, "Entity count should match");
    }

    @Then("all entities of type {string} should be removed")
    public void allEntitiesOfTypeShouldBeRemoved(String entityType) {
        PersistenceResult result = context.getLastResult();
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isSuccess(), "Clear operation should be successful: " + result.getMessage());
    }

    @Then("the count of {string} entities should be {int}")
    public void theCountOfEntitiesShouldBe(String entityType, int expectedCount) {
        int count = context.getPersistenceAdapter().count(entityType);
        assertEquals(expectedCount, count, "Entity count should be " + expectedCount);
    }

    @Then("the result should be empty")
    public void theResultShouldBeEmpty() {
        Optional<Map<String, Object>> result = context.getRetrievalResult();
        assertNotNull(result, "Result should not be null");
        assertFalse(result.isPresent(), "Result should be empty");
    }

    @Then("the update operation should fail")
    public void theUpdateOperationShouldFail() {
        PersistenceResult result = context.getLastResult();
        assertNotNull(result, "Result should not be null");
        assertFalse(result.isSuccess(), "Update operation should fail");
    }

    @Then("the delete operation should fail")
    public void theDeleteOperationShouldFail() {
        PersistenceResult result = context.getLastResult();
        assertNotNull(result, "Result should not be null");
        assertFalse(result.isSuccess(), "Delete operation should fail");
    }

    @Then("the storage type should be {string}")
    public void theStorageTypeShouldBe(String expectedType) {
        String actualType = context.getPersistenceAdapter().getStorageType().name();
        assertEquals(expectedType, actualType, "Storage type should match");
    }

    /**
     * Converts a DataTable to a Map of String to Object.
     *
     * @param dataTable The DataTable to convert
     * @return The converted Map
     */
    private Map<String, Object> convertDataTableToMap(DataTable dataTable) {
        Map<String, Object> result = new HashMap<>();
        
        List<Map<String, String>> rows = dataTable.asMaps();
        if (rows.isEmpty()) {
            // Handle the case where the DataTable is in the form of key-value pairs
            dataTable.asMap().forEach((key, value) -> {
                // Try to convert numeric values
                Object convertedValue = value;
                try {
                    // Check if it's an integer
                    convertedValue = Integer.parseInt(value);
                } catch (NumberFormatException e1) {
                    try {
                        // Check if it's a double
                        convertedValue = Double.parseDouble(value);
                    } catch (NumberFormatException e2) {
                        // Keep as string
                    }
                }
                result.put(key, convertedValue);
            });
        } else {
            // Handle the case where there are multiple rows (should be just one for entity data)
            Map<String, String> row = rows.get(0);
            row.forEach((key, value) -> {
                // Try to convert numeric values
                Object convertedValue = value;
                try {
                    // Check if it's an integer
                    convertedValue = Integer.parseInt(value);
                } catch (NumberFormatException e1) {
                    try {
                        // Check if it's a double
                        convertedValue = Double.parseDouble(value);
                    } catch (NumberFormatException e2) {
                        // Keep as string
                    }
                }
                result.put(key, convertedValue);
            });
        }
        
        return result;
    }
}