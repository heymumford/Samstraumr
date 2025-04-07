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

package org.s8r.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.PersistencePort.PersistenceResult;
import org.s8r.application.port.PersistencePort.StorageType;
import org.s8r.test.annotation.UnitTest;

/**
 * Unit tests for the InMemoryPersistenceAdapter class.
 */
@UnitTest
public class InMemoryPersistenceAdapterTest {

    private LoggerPort mockLogger;
    private InMemoryPersistenceAdapter adapter;
    
    @BeforeEach
    void setUp() {
        mockLogger = mock(LoggerPort.class);
        adapter = new InMemoryPersistenceAdapter(mockLogger);
    }
    
    @Test
    void testSaveEntity() {
        // Create test data
        String entityType = "test-entity";
        String entityId = "test-id";
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test Entity");
        data.put("value", 42);
        
        // Save entity
        PersistenceResult result = adapter.save(entityType, entityId, data);
        
        // Verify result
        assertTrue(result.isSuccess());
        assertEquals(entityId, result.getId());
        
        // Verify entity was saved
        assertTrue(adapter.exists(entityType, entityId));
        
        verify(mockLogger, atLeastOnce()).debug(anyString(), anyString(), anyString());
        verify(mockLogger, atLeastOnce()).info(anyString(), anyString(), anyString());
    }
    
    @Test
    void testSaveEntityWithInvalidParameters() {
        // Test with null entity type
        PersistenceResult result1 = adapter.save(null, "test-id", new HashMap<>());
        assertFalse(result1.isSuccess());
        assertTrue(result1.getMessage().contains("Entity type cannot be null"));
        
        // Test with empty entity type
        PersistenceResult result2 = adapter.save("", "test-id", new HashMap<>());
        assertFalse(result2.isSuccess());
        assertTrue(result2.getMessage().contains("Entity type cannot be null"));
        
        // Test with null entity ID
        PersistenceResult result3 = adapter.save("test-entity", null, new HashMap<>());
        assertFalse(result3.isSuccess());
        assertTrue(result3.getMessage().contains("Entity ID cannot be null"));
        
        // Test with empty entity ID
        PersistenceResult result4 = adapter.save("test-entity", "", new HashMap<>());
        assertFalse(result4.isSuccess());
        assertTrue(result4.getMessage().contains("Entity ID cannot be null"));
        
        // Test with null data
        PersistenceResult result5 = adapter.save("test-entity", "test-id", null);
        assertFalse(result5.isSuccess());
        assertTrue(result5.getMessage().contains("Entity data cannot be null"));
        
        verify(mockLogger, atLeastOnce()).debug(anyString(), anyString(), anyString());
    }
    
    @Test
    void testSaveDuplicateEntity() {
        // Create test data
        String entityType = "test-entity";
        String entityId = "test-id";
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test Entity");
        
        // Save entity
        PersistenceResult result1 = adapter.save(entityType, entityId, data);
        assertTrue(result1.isSuccess());
        
        // Try to save duplicate entity
        PersistenceResult result2 = adapter.save(entityType, entityId, data);
        assertFalse(result2.isSuccess());
        assertTrue(result2.getMessage().contains("Entity already exists"));
        
        verify(mockLogger, atLeastOnce()).debug(anyString(), anyString(), anyString());
        verify(mockLogger, atLeastOnce()).info(anyString(), anyString(), anyString());
    }
    
    @Test
    void testUpdateEntity() {
        // Create test data
        String entityType = "test-entity";
        String entityId = "test-id";
        Map<String, Object> data1 = new HashMap<>();
        data1.put("name", "Test Entity");
        data1.put("value", 42);
        
        // Save entity
        PersistenceResult saveResult = adapter.save(entityType, entityId, data1);
        assertTrue(saveResult.isSuccess());
        
        // Update entity
        Map<String, Object> data2 = new HashMap<>();
        data2.put("name", "Updated Entity");
        data2.put("value", 43);
        
        PersistenceResult updateResult = adapter.update(entityType, entityId, data2);
        
        // Verify result
        assertTrue(updateResult.isSuccess());
        assertEquals(entityId, updateResult.getId());
        
        // Verify entity was updated
        Optional<Map<String, Object>> retrievedData = adapter.findById(entityType, entityId);
        assertTrue(retrievedData.isPresent());
        assertEquals("Updated Entity", retrievedData.get().get("name"));
        assertEquals(43, retrievedData.get().get("value"));
        
        verify(mockLogger, atLeastOnce()).debug(anyString(), anyString(), anyString());
        verify(mockLogger, atLeastOnce()).info(anyString(), anyString(), anyString());
    }
    
    @Test
    void testUpdateNonExistentEntity() {
        // Create test data
        String entityType = "test-entity";
        String entityId = "non-existent-id";
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test Entity");
        
        // Try to update non-existent entity
        PersistenceResult result = adapter.update(entityType, entityId, data);
        
        // Verify result
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Entity not found"));
        
        verify(mockLogger, atLeastOnce()).debug(anyString(), anyString(), anyString());
    }
    
    @Test
    void testDeleteEntity() {
        // Create test data
        String entityType = "test-entity";
        String entityId = "test-id";
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test Entity");
        
        // Save entity
        PersistenceResult saveResult = adapter.save(entityType, entityId, data);
        assertTrue(saveResult.isSuccess());
        
        // Delete entity
        PersistenceResult deleteResult = adapter.delete(entityType, entityId);
        
        // Verify result
        assertTrue(deleteResult.isSuccess());
        assertEquals(entityId, deleteResult.getId());
        
        // Verify entity was deleted
        assertFalse(adapter.exists(entityType, entityId));
        
        verify(mockLogger, atLeastOnce()).debug(anyString(), anyString(), anyString());
        verify(mockLogger, atLeastOnce()).info(anyString(), anyString(), anyString());
    }
    
    @Test
    void testDeleteNonExistentEntity() {
        // Create test data
        String entityType = "test-entity";
        String entityId = "non-existent-id";
        
        // Try to delete non-existent entity
        PersistenceResult result = adapter.delete(entityType, entityId);
        
        // Verify result
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Entity not found"));
        
        verify(mockLogger, atLeastOnce()).debug(anyString(), anyString(), anyString());
    }
    
    @Test
    void testFindById() {
        // Create test data
        String entityType = "test-entity";
        String entityId = "test-id";
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test Entity");
        data.put("value", 42);
        
        // Save entity
        PersistenceResult saveResult = adapter.save(entityType, entityId, data);
        assertTrue(saveResult.isSuccess());
        
        // Find entity by ID
        Optional<Map<String, Object>> result = adapter.findById(entityType, entityId);
        
        // Verify result
        assertTrue(result.isPresent());
        assertEquals("Test Entity", result.get().get("name"));
        assertEquals(42, result.get().get("value"));
        
        verify(mockLogger, atLeastOnce()).debug(anyString(), anyString(), anyString());
    }
    
    @Test
    void testFindByIdWithNonExistentEntity() {
        // Create test data
        String entityType = "test-entity";
        String entityId = "non-existent-id";
        
        // Try to find non-existent entity
        Optional<Map<String, Object>> result = adapter.findById(entityType, entityId);
        
        // Verify result
        assertFalse(result.isPresent());
        
        verify(mockLogger, atLeastOnce()).debug(anyString(), anyString(), anyString());
    }
    
    @Test
    void testFindByType() {
        // Create test data
        String entityType = "test-entity";
        
        Map<String, Object> data1 = new HashMap<>();
        data1.put("name", "Entity 1");
        data1.put("value", 1);
        
        Map<String, Object> data2 = new HashMap<>();
        data2.put("name", "Entity 2");
        data2.put("value", 2);
        
        // Save entities
        adapter.save(entityType, "id-1", data1);
        adapter.save(entityType, "id-2", data2);
        
        // Find entities by type
        List<Map<String, Object>> result = adapter.findByType(entityType);
        
        // Verify result
        assertEquals(2, result.size());
        
        // Verify the entities are returned (order may vary)
        boolean foundEntity1 = false;
        boolean foundEntity2 = false;
        
        for (Map<String, Object> entity : result) {
            if ("Entity 1".equals(entity.get("name")) && entity.get("value").equals(1)) {
                foundEntity1 = true;
            } else if ("Entity 2".equals(entity.get("name")) && entity.get("value").equals(2)) {
                foundEntity2 = true;
            }
        }
        
        assertTrue(foundEntity1);
        assertTrue(foundEntity2);
        
        verify(mockLogger, atLeastOnce()).debug(anyString(), anyString());
    }
    
    @Test
    void testFindByTypeWithNonExistentType() {
        // Create test data
        String entityType = "non-existent-type";
        
        // Try to find entities of non-existent type
        List<Map<String, Object>> result = adapter.findByType(entityType);
        
        // Verify result
        assertTrue(result.isEmpty());
        
        verify(mockLogger, atLeastOnce()).debug(anyString(), anyString());
    }
    
    @Test
    void testFindByCriteria() {
        // Create test data
        String entityType = "test-entity";
        
        Map<String, Object> data1 = new HashMap<>();
        data1.put("name", "Entity 1");
        data1.put("category", "A");
        data1.put("value", 10);
        
        Map<String, Object> data2 = new HashMap<>();
        data2.put("name", "Entity 2");
        data2.put("category", "A");
        data2.put("value", 20);
        
        Map<String, Object> data3 = new HashMap<>();
        data3.put("name", "Entity 3");
        data3.put("category", "B");
        data3.put("value", 30);
        
        // Save entities
        adapter.save(entityType, "id-1", data1);
        adapter.save(entityType, "id-2", data2);
        adapter.save(entityType, "id-3", data3);
        
        // Find entities by criteria
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("category", "A");
        
        List<Map<String, Object>> result = adapter.findByCriteria(entityType, criteria);
        
        // Verify result
        assertEquals(2, result.size());
        
        // Verify the entities are returned (order may vary)
        boolean foundEntity1 = false;
        boolean foundEntity2 = false;
        
        for (Map<String, Object> entity : result) {
            if ("Entity 1".equals(entity.get("name")) && entity.get("value").equals(10)) {
                foundEntity1 = true;
            } else if ("Entity 2".equals(entity.get("name")) && entity.get("value").equals(20)) {
                foundEntity2 = true;
            }
        }
        
        assertTrue(foundEntity1);
        assertTrue(foundEntity2);
        
        verify(mockLogger, atLeastOnce()).debug(anyString(), anyString());
    }
    
    @Test
    void testExists() {
        // Create test data
        String entityType = "test-entity";
        String entityId = "test-id";
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test Entity");
        
        // Save entity
        adapter.save(entityType, entityId, data);
        
        // Check if entity exists
        boolean exists = adapter.exists(entityType, entityId);
        
        // Verify result
        assertTrue(exists);
        
        // Check if non-existent entity exists
        boolean nonExistentExists = adapter.exists(entityType, "non-existent-id");
        
        // Verify result
        assertFalse(nonExistentExists);
        
        verify(mockLogger, atLeastOnce()).debug(anyString(), anyString(), anyString());
    }
    
    @Test
    void testCount() {
        // Create test data
        String entityType = "test-entity";
        
        Map<String, Object> data1 = new HashMap<>();
        data1.put("name", "Entity 1");
        
        Map<String, Object> data2 = new HashMap<>();
        data2.put("name", "Entity 2");
        
        Map<String, Object> data3 = new HashMap<>();
        data3.put("name", "Entity 3");
        
        // Save entities
        adapter.save(entityType, "id-1", data1);
        adapter.save(entityType, "id-2", data2);
        adapter.save(entityType, "id-3", data3);
        
        // Count entities
        int count = adapter.count(entityType);
        
        // Verify result
        assertEquals(3, count);
        
        // Count entities of non-existent type
        int nonExistentCount = adapter.count("non-existent-type");
        
        // Verify result
        assertEquals(0, nonExistentCount);
        
        verify(mockLogger, atLeastOnce()).debug(anyString(), anyString());
    }
    
    @Test
    void testGetStorageType() {
        // Get storage type
        StorageType storageType = adapter.getStorageType();
        
        // Verify result
        assertEquals(StorageType.MEMORY, storageType);
    }
    
    @Test
    void testClearAll() {
        // Create test data
        String entityType = "test-entity";
        
        Map<String, Object> data1 = new HashMap<>();
        data1.put("name", "Entity 1");
        
        Map<String, Object> data2 = new HashMap<>();
        data2.put("name", "Entity 2");
        
        // Save entities
        adapter.save(entityType, "id-1", data1);
        adapter.save(entityType, "id-2", data2);
        
        // Verify entities were saved
        assertEquals(2, adapter.count(entityType));
        
        // Clear all entities
        PersistenceResult result = adapter.clearAll(entityType);
        
        // Verify result
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("Cleared 2 entities"));
        
        // Verify entities were cleared
        assertEquals(0, adapter.count(entityType));
        
        verify(mockLogger, atLeastOnce()).debug(anyString(), anyString());
        verify(mockLogger, atLeastOnce()).info(anyString(), anyString(), anyInt(), anyString());
    }
    
    @Test
    void testInitializeAndShutdown() {
        // Test initialization
        boolean initialized = adapter.initialize();
        assertTrue(initialized);
        
        // Test shutdown
        boolean shutdown = adapter.shutdown();
        assertTrue(shutdown);
        
        verify(mockLogger, atLeastOnce()).debug(anyString());
        verify(mockLogger, atLeastOnce()).info(anyString());
    }
    
    @Test
    void testDumpStorageContent() {
        // Create test data
        String entityType1 = "type-1";
        String entityType2 = "type-2";
        
        Map<String, Object> data1 = new HashMap<>();
        data1.put("name", "Entity 1");
        
        Map<String, Object> data2 = new HashMap<>();
        data2.put("name", "Entity 2");
        
        // Save entities
        adapter.save(entityType1, "id-1", data1);
        adapter.save(entityType2, "id-2", data2);
        
        // Dump storage content
        Map<String, Map<String, Map<String, Object>>> content = adapter.dumpStorageContent();
        
        // Verify result
        assertEquals(2, content.size());
        assertTrue(content.containsKey(entityType1));
        assertTrue(content.containsKey(entityType2));
        assertEquals(1, content.get(entityType1).size());
        assertEquals(1, content.get(entityType2).size());
        assertTrue(content.get(entityType1).containsKey("id-1"));
        assertTrue(content.get(entityType2).containsKey("id-2"));
        assertEquals("Entity 1", content.get(entityType1).get("id-1").get("name"));
        assertEquals("Entity 2", content.get(entityType2).get("id-2").get("name"));
    }
}