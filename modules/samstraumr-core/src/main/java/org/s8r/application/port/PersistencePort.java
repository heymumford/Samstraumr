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

package org.s8r.application.port;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Port interface for persistence operations.
 *
 * <p>This interface defines standard operations for persisting and retrieving data in the
 * application. Following Clean Architecture principles, it allows the application core to remain
 * independent of specific persistence mechanisms (database, file system, etc.).
 */
public interface PersistencePort {

  /** Storage types for persistence. */
  enum StorageType {
    MEMORY,
    FILE,
    DATABASE,
    CLOUD
  }

  /** Result class for persistence operations. */
  class PersistenceResult {
    private final boolean success;
    private final String message;
    private final String id;

    /**
     * Constructs a new PersistenceResult.
     *
     * @param success Whether the operation was successful
     * @param message A message describing the result
     * @param id The ID of the persisted entity
     */
    public PersistenceResult(boolean success, String message, String id) {
      this.success = success;
      this.message = message;
      this.id = id;
    }

    /**
     * Checks if the operation was successful.
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccess() {
      return success;
    }

    /**
     * Gets the result message.
     *
     * @return The result message
     */
    public String getMessage() {
      return message;
    }

    /**
     * Gets the ID of the persisted entity.
     *
     * @return The entity ID
     */
    public String getId() {
      return id;
    }

    /**
     * Creates a success result.
     *
     * @param id The ID of the persisted entity
     * @return A success result
     */
    public static PersistenceResult success(String id) {
      return new PersistenceResult(true, "Operation completed successfully", id);
    }

    /**
     * Creates a failure result.
     *
     * @param message The error message
     * @return A failure result
     */
    public static PersistenceResult failure(String message) {
      return new PersistenceResult(false, message, null);
    }
  }

  /**
   * Saves an entity.
   *
   * @param entityType The type of entity
   * @param entityId The entity ID
   * @param data The entity data
   * @return The persistence result
   */
  PersistenceResult save(String entityType, String entityId, Map<String, Object> data);

  /**
   * Updates an entity.
   *
   * @param entityType The type of entity
   * @param entityId The entity ID
   * @param data The entity data
   * @return The persistence result
   */
  PersistenceResult update(String entityType, String entityId, Map<String, Object> data);

  /**
   * Deletes an entity.
   *
   * @param entityType The type of entity
   * @param entityId The entity ID
   * @return The persistence result
   */
  PersistenceResult delete(String entityType, String entityId);

  /**
   * Finds an entity by ID.
   *
   * @param entityType The type of entity
   * @param entityId The entity ID
   * @return The entity data if found
   */
  Optional<Map<String, Object>> findById(String entityType, String entityId);

  /**
   * Finds entities by type.
   *
   * @param entityType The type of entity
   * @return A list of entity data
   */
  List<Map<String, Object>> findByType(String entityType);

  /**
   * Finds entities by criteria.
   *
   * @param entityType The type of entity
   * @param criteria The search criteria
   * @return A list of entity data
   */
  List<Map<String, Object>> findByCriteria(String entityType, Map<String, Object> criteria);

  /**
   * Checks if an entity exists.
   *
   * @param entityType The type of entity
   * @param entityId The entity ID
   * @return true if the entity exists, false otherwise
   */
  boolean exists(String entityType, String entityId);

  /**
   * Counts entities by type.
   *
   * @param entityType The type of entity
   * @return The number of entities
   */
  int count(String entityType);

  /**
   * Gets the storage type.
   *
   * @return The storage type
   */
  StorageType getStorageType();

  /**
   * Clears all data of a specific type.
   *
   * @param entityType The type of entity
   * @return The persistence result
   */
  PersistenceResult clearAll(String entityType);

  /**
   * Initializes the persistence system.
   *
   * @return true if initialization was successful, false otherwise
   */
  boolean initialize();

  /**
   * Shuts down the persistence system.
   *
   * @return true if shutdown was successful, false otherwise
   */
  boolean shutdown();
}
