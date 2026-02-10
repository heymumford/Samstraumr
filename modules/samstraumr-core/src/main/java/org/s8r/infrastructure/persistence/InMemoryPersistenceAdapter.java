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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.PersistencePort;

/**
 * In-memory implementation of the PersistencePort interface.
 *
 * <p>This adapter provides persistence operations using in-memory storage. It is suitable for
 * testing and development environments.
 */
public class InMemoryPersistenceAdapter implements PersistencePort {

  private final LoggerPort logger;
  private final Map<String, Map<String, Map<String, Object>>> storage;
  private boolean initialized;

  /**
   * Constructs a new InMemoryPersistenceAdapter.
   *
   * @param logger The logger to use
   */
  public InMemoryPersistenceAdapter(LoggerPort logger) {
    this.logger = logger;
    this.storage = new ConcurrentHashMap<>();
    this.initialized = false;

    // Initialize the storage system
    initialize();

    logger.debug("InMemoryPersistenceAdapter created");
  }

  @Override
  public PersistenceResult save(String entityType, String entityId, Map<String, Object> data) {
    logger.debug("Saving entity of type {} with ID {}", entityType, entityId);

    // Check initialization
    if (!initialized) {
      return PersistenceResult.failure("Persistence system not initialized");
    }

    // Validate parameters
    if (entityType == null || entityType.trim().isEmpty()) {
      return PersistenceResult.failure("Entity type cannot be null or empty");
    }

    if (entityId == null || entityId.trim().isEmpty()) {
      return PersistenceResult.failure("Entity ID cannot be null or empty");
    }

    if (data == null) {
      return PersistenceResult.failure("Entity data cannot be null");
    }

    try {
      // Ensure type storage exists
      storage.computeIfAbsent(entityType, k -> new ConcurrentHashMap<>());

      // Check if entity already exists
      if (storage.get(entityType).containsKey(entityId)) {
        return PersistenceResult.failure("Entity already exists");
      }

      // Save entity
      storage.get(entityType).put(entityId, new HashMap<>(data));
      logger.info("Entity saved: {}/{}", entityType, entityId);

      return PersistenceResult.success(entityId);
    } catch (Exception e) {
      logger.error("Error saving entity: {}", e.getMessage(), e);
      return PersistenceResult.failure("Error saving entity: " + e.getMessage());
    }
  }

  @Override
  public PersistenceResult update(String entityType, String entityId, Map<String, Object> data) {
    logger.debug("Updating entity of type {} with ID {}", entityType, entityId);

    // Check initialization
    if (!initialized) {
      return PersistenceResult.failure("Persistence system not initialized");
    }

    // Validate parameters
    if (entityType == null || entityType.trim().isEmpty()) {
      return PersistenceResult.failure("Entity type cannot be null or empty");
    }

    if (entityId == null || entityId.trim().isEmpty()) {
      return PersistenceResult.failure("Entity ID cannot be null or empty");
    }

    if (data == null) {
      return PersistenceResult.failure("Entity data cannot be null");
    }

    try {
      // Check if type storage exists
      if (!storage.containsKey(entityType)) {
        return PersistenceResult.failure("Entity type not found");
      }

      // Check if entity exists
      if (!storage.get(entityType).containsKey(entityId)) {
        return PersistenceResult.failure("Entity not found");
      }

      // Update entity
      storage.get(entityType).put(entityId, new HashMap<>(data));
      logger.info("Entity updated: {}/{}", entityType, entityId);

      return PersistenceResult.success(entityId);
    } catch (Exception e) {
      logger.error("Error updating entity: {}", e.getMessage(), e);
      return PersistenceResult.failure("Error updating entity: " + e.getMessage());
    }
  }

  @Override
  public PersistenceResult delete(String entityType, String entityId) {
    logger.debug("Deleting entity of type {} with ID {}", entityType, entityId);

    // Check initialization
    if (!initialized) {
      return PersistenceResult.failure("Persistence system not initialized");
    }

    // Validate parameters
    if (entityType == null || entityType.trim().isEmpty()) {
      return PersistenceResult.failure("Entity type cannot be null or empty");
    }

    if (entityId == null || entityId.trim().isEmpty()) {
      return PersistenceResult.failure("Entity ID cannot be null or empty");
    }

    try {
      // Check if type storage exists
      if (!storage.containsKey(entityType)) {
        return PersistenceResult.failure("Entity type not found");
      }

      // Check if entity exists
      if (!storage.get(entityType).containsKey(entityId)) {
        return PersistenceResult.failure("Entity not found");
      }

      // Delete entity
      storage.get(entityType).remove(entityId);
      logger.info("Entity deleted: {}/{}", entityType, entityId);

      return PersistenceResult.success(entityId);
    } catch (Exception e) {
      logger.error("Error deleting entity: {}", e.getMessage(), e);
      return PersistenceResult.failure("Error deleting entity: " + e.getMessage());
    }
  }

  @Override
  public Optional<Map<String, Object>> findById(String entityType, String entityId) {
    logger.debug("Finding entity of type {} with ID {}", entityType, entityId);

    // Check initialization
    if (!initialized) {
      logger.warn("Persistence system not initialized");
      return Optional.empty();
    }

    // Validate parameters
    if (entityType == null || entityType.trim().isEmpty()) {
      logger.warn("Entity type cannot be null or empty");
      return Optional.empty();
    }

    if (entityId == null || entityId.trim().isEmpty()) {
      logger.warn("Entity ID cannot be null or empty");
      return Optional.empty();
    }

    // Check if type storage exists
    if (!storage.containsKey(entityType)) {
      logger.debug("Entity type not found: {}", entityType);
      return Optional.empty();
    }

    // Check if entity exists
    if (!storage.get(entityType).containsKey(entityId)) {
      logger.debug("Entity not found: {}/{}", entityType, entityId);
      return Optional.empty();
    }

    // Return entity data (copy to prevent modification)
    return Optional.of(new HashMap<>(storage.get(entityType).get(entityId)));
  }

  @Override
  public List<Map<String, Object>> findByType(String entityType) {
    logger.debug("Finding entities of type {}", entityType);

    // Check initialization
    if (!initialized) {
      logger.warn("Persistence system not initialized");
      return new ArrayList<>();
    }

    // Validate parameters
    if (entityType == null || entityType.trim().isEmpty()) {
      logger.warn("Entity type cannot be null or empty");
      return new ArrayList<>();
    }

    // Check if type storage exists
    if (!storage.containsKey(entityType)) {
      logger.debug("Entity type not found: {}", entityType);
      return new ArrayList<>();
    }

    // Return all entities of the type (copy to prevent modification)
    return storage.get(entityType).values().stream().map(HashMap::new).collect(Collectors.toList());
  }

  @Override
  public List<Map<String, Object>> findByCriteria(String entityType, Map<String, Object> criteria) {
    logger.debug("Finding entities of type {} by criteria", entityType);

    // Check initialization
    if (!initialized) {
      logger.warn("Persistence system not initialized");
      return new ArrayList<>();
    }

    // Validate parameters
    if (entityType == null || entityType.trim().isEmpty()) {
      logger.warn("Entity type cannot be null or empty");
      return new ArrayList<>();
    }

    if (criteria == null || criteria.isEmpty()) {
      logger.warn("Criteria cannot be null or empty");
      return findByType(entityType);
    }

    // Check if type storage exists
    if (!storage.containsKey(entityType)) {
      logger.debug("Entity type not found: {}", entityType);
      return new ArrayList<>();
    }

    // Find entities that match all criteria
    return storage.get(entityType).values().stream()
        .filter(entity -> matchesCriteria(entity, criteria))
        .map(HashMap::new)
        .collect(Collectors.toList());
  }

  /**
   * Checks if an entity matches the given criteria.
   *
   * @param entity The entity data
   * @param criteria The criteria to match
   * @return true if the entity matches all criteria, false otherwise
   */
  private boolean matchesCriteria(Map<String, Object> entity, Map<String, Object> criteria) {
    for (Map.Entry<String, Object> entry : criteria.entrySet()) {
      if (!entity.containsKey(entry.getKey())
          || !entity.get(entry.getKey()).equals(entry.getValue())) {
        return false;
      }
    }

    return true;
  }

  @Override
  public boolean exists(String entityType, String entityId) {
    logger.debug("Checking if entity exists: {}/{}", entityType, entityId);

    // Check initialization
    if (!initialized) {
      logger.warn("Persistence system not initialized");
      return false;
    }

    // Validate parameters
    if (entityType == null || entityType.trim().isEmpty()) {
      logger.warn("Entity type cannot be null or empty");
      return false;
    }

    if (entityId == null || entityId.trim().isEmpty()) {
      logger.warn("Entity ID cannot be null or empty");
      return false;
    }

    // Check if type storage exists
    if (!storage.containsKey(entityType)) {
      return false;
    }

    // Check if entity exists
    return storage.get(entityType).containsKey(entityId);
  }

  @Override
  public int count(String entityType) {
    logger.debug("Counting entities of type {}", entityType);

    // Check initialization
    if (!initialized) {
      logger.warn("Persistence system not initialized");
      return 0;
    }

    // Validate parameters
    if (entityType == null || entityType.trim().isEmpty()) {
      logger.warn("Entity type cannot be null or empty");
      return 0;
    }

    // Check if type storage exists
    if (!storage.containsKey(entityType)) {
      return 0;
    }

    // Return the count
    return storage.get(entityType).size();
  }

  @Override
  public StorageType getStorageType() {
    return StorageType.MEMORY;
  }

  @Override
  public PersistenceResult clearAll(String entityType) {
    logger.debug("Clearing all entities of type {}", entityType);

    // Check initialization
    if (!initialized) {
      return PersistenceResult.failure("Persistence system not initialized");
    }

    // Validate parameters
    if (entityType == null || entityType.trim().isEmpty()) {
      return PersistenceResult.failure("Entity type cannot be null or empty");
    }

    try {
      // Check if type storage exists
      if (!storage.containsKey(entityType)) {
        return PersistenceResult.failure("Entity type not found");
      }

      // Clear all entities
      int count = storage.get(entityType).size();
      storage.get(entityType).clear();
      logger.info("Cleared {} entities of type {}", count, entityType);

      return new PersistenceResult(true, "Cleared " + count + " entities", null);
    } catch (Exception e) {
      logger.error("Error clearing entities: {}", e.getMessage(), e);
      return PersistenceResult.failure("Error clearing entities: " + e.getMessage());
    }
  }

  @Override
  public boolean initialize() {
    logger.debug("Initializing persistence system");

    if (initialized) {
      logger.warn("Persistence system already initialized");
      return true;
    }

    try {
      // Clear any existing data
      storage.clear();

      // Set initialized flag
      initialized = true;
      logger.info("Persistence system initialized");

      return true;
    } catch (Exception e) {
      logger.error("Error initializing persistence system: {}", e.getMessage(), e);
      return false;
    }
  }

  @Override
  public boolean shutdown() {
    logger.debug("Shutting down persistence system");

    if (!initialized) {
      logger.warn("Persistence system not initialized");
      return true;
    }

    try {
      // Clear data
      storage.clear();

      // Set initialized flag
      initialized = false;
      logger.info("Persistence system shut down");

      return true;
    } catch (Exception e) {
      logger.error("Error shutting down persistence system: {}", e.getMessage(), e);
      return false;
    }
  }

  /**
   * Dumps all stored data (for debugging purposes).
   *
   * @return A map of all stored data
   */
  public Map<String, Map<String, Map<String, Object>>> dumpStorageContent() {
    Map<String, Map<String, Map<String, Object>>> result = new HashMap<>();

    for (String type : storage.keySet()) {
      result.put(type, new HashMap<>());

      for (String id : storage.get(type).keySet()) {
        result.get(type).put(id, new HashMap<>(storage.get(type).get(id)));
      }
    }

    return result;
  }
}
