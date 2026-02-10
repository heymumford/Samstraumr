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

package org.s8r.test.context;

import org.s8r.application.port.PersistencePort;
import org.s8r.application.port.PersistencePort.PersistenceResult;
import org.s8r.application.port.ValidationPort;
import org.s8r.application.port.ValidationPort.ValidationResult;
import org.s8r.test.mock.MockLoggerAdapter;
import org.s8r.test.mock.MockPersistenceAdapter;
import org.s8r.test.mock.MockValidationAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Test context for validation-persistence integration tests.
 *
 * <p>This class manages the shared state between validation and persistence
 * integrations tests, including both adapter instances and test data.
 */
public class ValidationPersistenceIntegrationContext {

    private final MockLoggerAdapter loggerAdapter;
    private final MockValidationAdapter validationAdapter;
    private final MockPersistenceAdapter persistenceAdapter;

    private Map<String, Object> currentObject;
    private ValidationResult validationResult;
    private PersistenceResult persistenceResult;
    private boolean objectValidated;
    private boolean objectPersisted;
    private List<Map<String, Object>> dataObjects;
    private Map<String, ValidationResult> validationResults;
    private Map<String, PersistenceResult> persistenceResults;
    private Optional<Map<String, Object>> retrievedObject;

    /**
     * Constructs a new ValidationPersistenceIntegrationContext.
     */
    public ValidationPersistenceIntegrationContext() {
        this.loggerAdapter = new MockLoggerAdapter();
        this.validationAdapter = new MockValidationAdapter(loggerAdapter);
        this.persistenceAdapter = new MockPersistenceAdapter(loggerAdapter);
        this.currentObject = new HashMap<>();
        this.dataObjects = new ArrayList<>();
        this.validationResults = new HashMap<>();
        this.persistenceResults = new HashMap<>();
    }

    /**
     * Gets the validation adapter.
     *
     * @return The validation adapter
     */
    public ValidationPort getValidationAdapter() {
        return validationAdapter;
    }

    /**
     * Gets the persistence adapter.
     *
     * @return The persistence adapter
     */
    public PersistencePort getPersistenceAdapter() {
        return persistenceAdapter;
    }

    /**
     * Gets the logger adapter.
     *
     * @return The logger adapter
     */
    public MockLoggerAdapter getLoggerAdapter() {
        return loggerAdapter;
    }

    /**
     * Sets the current object.
     *
     * @param object The current object
     */
    public void setCurrentObject(Map<String, Object> object) {
        this.currentObject = new HashMap<>(object);
    }

    /**
     * Gets the current object.
     *
     * @return The current object
     */
    public Map<String, Object> getCurrentObject() {
        return currentObject;
    }

    /**
     * Sets the validation result.
     *
     * @param result The validation result
     */
    public void setValidationResult(ValidationResult result) {
        this.validationResult = result;
    }

    /**
     * Gets the validation result.
     *
     * @return The validation result
     */
    public ValidationResult getValidationResult() {
        return validationResult;
    }

    /**
     * Sets the persistence result.
     *
     * @param result The persistence result
     */
    public void setPersistenceResult(PersistenceResult result) {
        this.persistenceResult = result;
    }

    /**
     * Gets the persistence result.
     *
     * @return The persistence result
     */
    public PersistenceResult getPersistenceResult() {
        return persistenceResult;
    }

    /**
     * Sets whether the object has been validated.
     *
     * @param validated Whether the object has been validated
     */
    public void setObjectValidated(boolean validated) {
        this.objectValidated = validated;
    }

    /**
     * Checks if the object has been validated.
     *
     * @return true if the object has been validated, false otherwise
     */
    public boolean isObjectValidated() {
        return objectValidated;
    }

    /**
     * Sets whether the object has been persisted.
     *
     * @param persisted Whether the object has been persisted
     */
    public void setObjectPersisted(boolean persisted) {
        this.objectPersisted = persisted;
    }

    /**
     * Checks if the object has been persisted.
     *
     * @return true if the object has been persisted, false otherwise
     */
    public boolean isObjectPersisted() {
        return objectPersisted;
    }

    /**
     * Adds a data object to the list.
     *
     * @param object The data object to add
     */
    public void addDataObject(Map<String, Object> object) {
        this.dataObjects.add(new HashMap<>(object));
    }

    /**
     * Sets the data objects.
     *
     * @param objects The data objects
     */
    public void setDataObjects(List<Map<String, Object>> objects) {
        this.dataObjects = new ArrayList<>();
        for (Map<String, Object> object : objects) {
            this.dataObjects.add(new HashMap<>(object));
        }
    }

    /**
     * Gets the data objects.
     *
     * @return The data objects
     */
    public List<Map<String, Object>> getDataObjects() {
        return dataObjects;
    }

    /**
     * Adds a validation result.
     *
     * @param id The ID of the object
     * @param result The validation result
     */
    public void addValidationResult(String id, ValidationResult result) {
        this.validationResults.put(id, result);
    }

    /**
     * Gets the validation results.
     *
     * @return The validation results
     */
    public Map<String, ValidationResult> getValidationResults() {
        return validationResults;
    }

    /**
     * Adds a persistence result.
     *
     * @param id The ID of the object
     * @param result The persistence result
     */
    public void addPersistenceResult(String id, PersistenceResult result) {
        this.persistenceResults.put(id, result);
    }

    /**
     * Gets the persistence results.
     *
     * @return The persistence results
     */
    public Map<String, PersistenceResult> getPersistenceResults() {
        return persistenceResults;
    }

    /**
     * Sets the retrieved object.
     *
     * @param object The retrieved object
     */
    public void setRetrievedObject(Optional<Map<String, Object>> object) {
        this.retrievedObject = object;
    }

    /**
     * Gets the retrieved object.
     *
     * @return The retrieved object
     */
    public Optional<Map<String, Object>> getRetrievedObject() {
        return retrievedObject;
    }

    /**
     * Clears all test data.
     */
    public void clear() {
        currentObject.clear();
        validationResult = null;
        persistenceResult = null;
        objectValidated = false;
        objectPersisted = false;
        dataObjects.clear();
        validationResults.clear();
        persistenceResults.clear();
        retrievedObject = null;

        // Reset adapters
        validationAdapter.clearRules();
        persistenceAdapter.shutdown();
        persistenceAdapter.initialize();

        // Set up standard validation rules
        setupStandardValidationRules();
    }

    /**
     * Sets up standard validation rules.
     */
    public void setupStandardValidationRules() {
        // Set up standard rules for user objects
        validationAdapter.registerRule("user", "name", ValidationPort.ValidationRule.REQUIRED, "Name is required");
        validationAdapter.registerRule("user", "email", ValidationPort.ValidationRule.REQUIRED, "Email is required");
        validationAdapter.registerRule("user", "email", ValidationPort.ValidationRule.PATTERN, "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", "Invalid email format");
        validationAdapter.registerRule("user", "age", ValidationPort.ValidationRule.REQUIRED, "Age is required");
        validationAdapter.registerRule("user", "age", ValidationPort.ValidationRule.MIN_VALUE, "18", "Age must be at least 18");
    }
}