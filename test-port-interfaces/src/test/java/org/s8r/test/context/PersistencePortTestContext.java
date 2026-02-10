/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

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
import org.s8r.test.mock.MockLoggerAdapter;
import org.s8r.test.mock.MockPersistenceAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Test context for persistence port tests.
 *
 * <p>This class manages the state of persistence tests, including the adapter instance,
 * test data, and operation results.
 */
public class PersistencePortTestContext {

    private final MockLoggerAdapter loggerAdapter;
    private final MockPersistenceAdapter persistenceAdapter;
    
    private String currentEntityType;
    private String currentEntityId;
    private Map<String, Object> currentEntityData;
    private PersistenceResult lastResult;
    private Optional<Map<String, Object>> retrievalResult;
    private List<Map<String, Object>> retrievalResults;
    private Map<String, Object> searchCriteria;
    private int entityCount;
    private boolean initialized;

    /**
     * Constructs a new PersistencePortTestContext.
     */
    public PersistencePortTestContext() {
        this.loggerAdapter = new MockLoggerAdapter();
        this.persistenceAdapter = new MockPersistenceAdapter(loggerAdapter);
        this.currentEntityData = new HashMap<>();
        this.retrievalResults = new ArrayList<>();
        this.searchCriteria = new HashMap<>();
        this.initialized = false;
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
     * Sets the current entity type.
     *
     * @param entityType The entity type
     */
    public void setCurrentEntityType(String entityType) {
        this.currentEntityType = entityType;
    }

    /**
     * Gets the current entity type.
     *
     * @return The current entity type
     */
    public String getCurrentEntityType() {
        return currentEntityType;
    }

    /**
     * Sets the current entity ID.
     *
     * @param entityId The entity ID
     */
    public void setCurrentEntityId(String entityId) {
        this.currentEntityId = entityId;
    }

    /**
     * Gets the current entity ID.
     *
     * @return The current entity ID
     */
    public String getCurrentEntityId() {
        return currentEntityId;
    }

    /**
     * Sets the current entity data.
     *
     * @param data The entity data
     */
    public void setCurrentEntityData(Map<String, Object> data) {
        this.currentEntityData = new HashMap<>(data);
    }

    /**
     * Gets the current entity data.
     *
     * @return The current entity data
     */
    public Map<String, Object> getCurrentEntityData() {
        return currentEntityData;
    }

    /**
     * Sets the last operation result.
     *
     * @param result The operation result
     */
    public void setLastResult(PersistenceResult result) {
        this.lastResult = result;
    }

    /**
     * Gets the last operation result.
     *
     * @return The last operation result
     */
    public PersistenceResult getLastResult() {
        return lastResult;
    }

    /**
     * Sets the retrieval result.
     *
     * @param result The retrieval result
     */
    public void setRetrievalResult(Optional<Map<String, Object>> result) {
        this.retrievalResult = result;
    }

    /**
     * Gets the retrieval result.
     *
     * @return The retrieval result
     */
    public Optional<Map<String, Object>> getRetrievalResult() {
        return retrievalResult;
    }

    /**
     * Sets the retrieval results.
     *
     * @param results The retrieval results
     */
    public void setRetrievalResults(List<Map<String, Object>> results) {
        this.retrievalResults = new ArrayList<>(results);
    }

    /**
     * Gets the retrieval results.
     *
     * @return The retrieval results
     */
    public List<Map<String, Object>> getRetrievalResults() {
        return retrievalResults;
    }

    /**
     * Sets the search criteria.
     *
     * @param criteria The search criteria
     */
    public void setSearchCriteria(Map<String, Object> criteria) {
        this.searchCriteria = new HashMap<>(criteria);
    }

    /**
     * Gets the search criteria.
     *
     * @return The search criteria
     */
    public Map<String, Object> getSearchCriteria() {
        return searchCriteria;
    }

    /**
     * Sets the entity count.
     *
     * @param count The entity count
     */
    public void setEntityCount(int count) {
        this.entityCount = count;
    }

    /**
     * Gets the entity count.
     *
     * @return The entity count
     */
    public int getEntityCount() {
        return entityCount;
    }

    /**
     * Sets the initialized flag.
     *
     * @param initialized The initialized flag
     */
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * Checks if the system is initialized.
     *
     * @return true if initialized, false otherwise
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Clears all test data.
     */
    public void clear() {
        currentEntityType = null;
        currentEntityId = null;
        currentEntityData.clear();
        lastResult = null;
        retrievalResult = null;
        retrievalResults.clear();
        searchCriteria.clear();
        entityCount = 0;
        
        // Reset the persistence adapter
        if (persistenceAdapter != null) {
            persistenceAdapter.shutdown();
            persistenceAdapter.initialize();
        }
    }
}