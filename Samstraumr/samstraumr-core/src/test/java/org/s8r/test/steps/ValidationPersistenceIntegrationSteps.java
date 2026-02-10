/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.test.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.datatable.DataTable;

import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Step definitions for Validation and Persistence integration tests.
 */
public class ValidationPersistenceIntegrationSteps {
    
    private ValidationService validationService;
    private PersistenceService persistenceService;
    private TestDataObject currentDataObject;
    private boolean validationResult;
    private boolean persistenceResult;
    private List<String> validationErrors = new ArrayList<>();
    private List<TestDataObject> dataObjects = new ArrayList<>();
    
    // Simple classes for testing
    
    private static class TestDataObject {
        private int id;
        private String name;
        private String email;
        private int age;
        private boolean valid;
        
        public TestDataObject(int id, String name, String email, int age, boolean valid) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.age = age;
            this.valid = valid;
        }
        
        public int getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public int getAge() { return age; }
        public boolean isValid() { return valid; }
    }
    
    private static class ValidationService {
        private List<Predicate<TestDataObject>> rules = new ArrayList<>();
        private List<String> errors = new ArrayList<>();
        
        public ValidationService() {
            // Add validation rules
            rules.add(obj -> {
                if (obj.getName() == null || obj.getName().isEmpty()) {
                    errors.add("Name cannot be empty");
                    return false;
                }
                return true;
            });
            
            rules.add(obj -> {
                if (obj.getEmail() == null || !obj.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                    errors.add("Email must be a valid format");
                    return false;
                }
                return true;
            });
            
            rules.add(obj -> {
                if (obj.getAge() < 18) {
                    errors.add("Age must be 18 or older");
                    return false;
                }
                return true;
            });
        }
        
        public boolean validate(TestDataObject obj) {
            errors.clear();
            boolean valid = true;
            
            for (Predicate<TestDataObject> rule : rules) {
                boolean ruleResult = rule.test(obj);
                valid = valid && ruleResult;
            }
            
            return valid;
        }
        
        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }
    }
    
    private static class PersistenceService {
        private Map<Integer, TestDataObject> storage = new HashMap<>();
        
        public boolean save(TestDataObject obj) {
            storage.put(obj.getId(), obj);
            return true;
        }
        
        public Optional<TestDataObject> findById(int id) {
            return Optional.ofNullable(storage.get(id));
        }
        
        public List<TestDataObject> findAll() {
            return new ArrayList<>(storage.values());
        }
        
        public boolean exists(int id) {
            return storage.containsKey(id);
        }
    }
    
    @Given("a validation service is initialized")
    public void aValidationServiceIsInitialized() {
        validationService = new ValidationService();
    }
    
    @Given("a persistence service is initialized")
    public void aPersistenceServiceIsInitialized() {
        persistenceService = new PersistenceService();
    }
    
    @Given("I have a valid data object")
    public void iHaveAValidDataObject() {
        currentDataObject = new TestDataObject(1, "John Doe", "john.doe@example.com", 30, true);
    }
    
    @Given("I have an invalid data object")
    public void iHaveAnInvalidDataObject() {
        currentDataObject = new TestDataObject(2, "Invalid User", "not-an-email", 15, false);
    }
    
    @When("I validate and persist the object")
    public void iValidateAndPersistTheObject() {
        validationResult = validationService.validate(currentDataObject);
        
        if (validationResult) {
            persistenceResult = persistenceService.save(currentDataObject);
        } else {
            validationErrors = validationService.getErrors();
            persistenceResult = false;
        }
    }
    
    @Then("the validation should pass")
    public void theValidationShouldPass() {
        Assertions.assertTrue(validationResult, "Validation should pass");
    }
    
    @Then("the validation should fail")
    public void theValidationShouldFail() {
        Assertions.assertFalse(validationResult, "Validation should fail");
    }
    
    @Then("the object should be persisted successfully")
    public void theObjectShouldBePersistedSuccessfully() {
        Assertions.assertTrue(persistenceResult, "Object should be persisted successfully");
        Assertions.assertTrue(persistenceService.exists(currentDataObject.getId()), 
            "Object should exist in persistence storage");
    }
    
    @Then("I should be able to retrieve the object")
    public void iShouldBeAbleToRetrieveTheObject() {
        Optional<TestDataObject> retrieved = persistenceService.findById(currentDataObject.getId());
        Assertions.assertTrue(retrieved.isPresent(), "Object should be retrievable");
        Assertions.assertEquals(currentDataObject.getName(), retrieved.get().getName(), 
            "Retrieved object should have the same name");
    }
    
    @Then("the object should not be persisted")
    public void theObjectShouldNotBePersisted() {
        Assertions.assertFalse(persistenceResult, "Object should not be persisted");
        Assertions.assertFalse(persistenceService.exists(currentDataObject.getId()), 
            "Object should not exist in persistence storage");
    }
    
    @Then("I should receive validation errors")
    public void iShouldReceiveValidationErrors() {
        Assertions.assertFalse(validationErrors.isEmpty(), "There should be validation errors");
    }
    
    @Given("I have the following data objects:")
    public void iHaveTheFollowingDataObjects(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        
        for (Map<String, String> row : rows) {
            int id = Integer.parseInt(row.get("id"));
            String name = row.get("name");
            String email = row.get("email");
            int age = Integer.parseInt(row.get("age"));
            boolean valid = Boolean.parseBoolean(row.get("valid"));
            
            dataObjects.add(new TestDataObject(id, name, email, age, valid));
        }
    }
    
    @When("I validate and persist each object")
    public void iValidateAndPersistEachObject() {
        for (TestDataObject obj : dataObjects) {
            boolean isValid = validationService.validate(obj);
            
            if (isValid) {
                persistenceService.save(obj);
            }
        }
    }
    
    @Then("only valid objects should be persisted")
    public void onlyValidObjectsShouldBePersisted() {
        for (TestDataObject obj : dataObjects) {
            if (obj.isValid()) {
                Assertions.assertTrue(persistenceService.exists(obj.getId()), 
                    "Valid object with ID " + obj.getId() + " should be persisted");
            } else {
                Assertions.assertFalse(persistenceService.exists(obj.getId()), 
                    "Invalid object with ID " + obj.getId() + " should not be persisted");
            }
        }
    }
    
    @Then("I should receive validation errors for invalid objects")
    public void iShouldReceiveValidationErrorsForInvalidObjects() {
        // Test validation of one of the invalid objects to verify errors are generated
        TestDataObject invalidObject = dataObjects.stream()
            .filter(obj -> !obj.isValid())
            .findFirst()
            .orElseThrow(() -> new AssertionError("No invalid object found for testing"));
        
        boolean result = validationService.validate(invalidObject);
        Assertions.assertFalse(result, "Validation of invalid object should fail");
        Assertions.assertFalse(validationService.getErrors().isEmpty(), 
            "Validation should produce errors for invalid object");
    }
}