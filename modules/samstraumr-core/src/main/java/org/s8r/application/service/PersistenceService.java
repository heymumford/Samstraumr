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

package org.s8r.application.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.PersistencePort;
import org.s8r.application.port.PersistencePort.PersistenceResult;
import org.s8r.application.port.ValidationPort;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.component.port.MachinePort;
import org.s8r.domain.machine.MachineType;

/**
 * Application service for persistence operations.
 *
 * <p>This service provides high-level persistence operations for the application, using the
 * PersistencePort interface to abstract the actual persistence mechanism.
 */
public class PersistenceService {

  private final PersistencePort persistencePort;
  private final ValidationPort validationPort;
  private final LoggerPort logger;

  // Entity type constants
  private static final String ENTITY_TYPE_COMPONENT = "component";
  private static final String ENTITY_TYPE_MACHINE = "machine";
  private static final String ENTITY_TYPE_CONFIGURATION = "configuration";

  /**
   * Constructs a new PersistenceService.
   *
   * @param persistencePort The persistence port to use
   * @param validationPort The validation port to use
   * @param logger The logger to use
   */
  public PersistenceService(
      PersistencePort persistencePort, ValidationPort validationPort, LoggerPort logger) {
    this.persistencePort = persistencePort;
    this.validationPort = validationPort;
    this.logger = logger;

    logger.debug("PersistenceService created");
  }

  /**
   * Saves a component.
   *
   * @param component The component to save
   * @return The persistence result
   */
  public PersistenceResult saveComponent(ComponentPort component) {
    logger.debug("Saving component: {}", component.getId());

    // Validate component
    ValidationPort.ValidationResult validationResult =
        validationPort.validateEntity("component", createComponentData(component));

    if (!validationResult.isValid()) {
      logger.warn("Component validation failed: {}", validationResult.getErrors());
      return PersistenceResult.failure(
          "Validation failed: " + String.join(", ", validationResult.getErrors()));
    }

    // Save component
    return persistencePort.save(
        ENTITY_TYPE_COMPONENT, component.getId().getShortId(), createComponentData(component));
  }

  /**
   * Finds a component by ID.
   *
   * @param componentId The component ID
   * @return The component data if found
   */
  public Optional<Map<String, Object>> findComponentById(String componentId) {
    logger.debug("Finding component by ID: {}", componentId);

    return persistencePort.findById(ENTITY_TYPE_COMPONENT, componentId);
  }

  /**
   * Finds components by criteria.
   *
   * @param criteria The search criteria
   * @return A list of component data
   */
  public List<Map<String, Object>> findComponentsByCriteria(Map<String, Object> criteria) {
    logger.debug("Finding components by criteria");

    return persistencePort.findByCriteria(ENTITY_TYPE_COMPONENT, criteria);
  }

  /**
   * Deletes a component.
   *
   * @param componentId The component ID
   * @return The persistence result
   */
  public PersistenceResult deleteComponent(String componentId) {
    logger.debug("Deleting component: {}", componentId);

    return persistencePort.delete(ENTITY_TYPE_COMPONENT, componentId);
  }

  /**
   * Saves a machine.
   *
   * @param machine The machine to save
   * @return The persistence result
   */
  public PersistenceResult saveMachine(MachinePort machine) {
    logger.debug("Saving machine: {}", machine.getId());

    // Validate machine
    ValidationPort.ValidationResult validationResult =
        validationPort.validateEntity("machine", createMachineData(machine));

    if (!validationResult.isValid()) {
      logger.warn("Machine validation failed: {}", validationResult.getErrors());
      return PersistenceResult.failure(
          "Validation failed: " + String.join(", ", validationResult.getErrors()));
    }

    // Save machine
    return persistencePort.save(
        ENTITY_TYPE_MACHINE, machine.getId().getShortId(), createMachineData(machine));
  }

  /**
   * Updates a machine.
   *
   * @param machine The machine to update
   * @return The persistence result
   */
  public PersistenceResult updateMachine(MachinePort machine) {
    logger.debug("Updating machine: {}", machine.getId());

    // Validate machine
    ValidationPort.ValidationResult validationResult =
        validationPort.validateEntity("machine", createMachineData(machine));

    if (!validationResult.isValid()) {
      logger.warn("Machine validation failed: {}", validationResult.getErrors());
      return PersistenceResult.failure(
          "Validation failed: " + String.join(", ", validationResult.getErrors()));
    }

    // Update machine
    return persistencePort.update(
        ENTITY_TYPE_MACHINE, machine.getId().getShortId(), createMachineData(machine));
  }

  /**
   * Finds a machine by ID.
   *
   * @param machineId The machine ID
   * @return The machine data if found
   */
  public Optional<Map<String, Object>> findMachineById(String machineId) {
    logger.debug("Finding machine by ID: {}", machineId);

    return persistencePort.findById(ENTITY_TYPE_MACHINE, machineId);
  }

  /**
   * Finds machines by type.
   *
   * @param machineType The machine type
   * @return A list of machine data
   */
  public List<Map<String, Object>> findMachinesByType(MachineType machineType) {
    logger.debug("Finding machines by type: {}", machineType);

    Map<String, Object> criteria = new HashMap<>();
    criteria.put("type", machineType.toString());

    return persistencePort.findByCriteria(ENTITY_TYPE_MACHINE, criteria);
  }

  /**
   * Deletes a machine.
   *
   * @param machineId The machine ID
   * @return The persistence result
   */
  public PersistenceResult deleteMachine(String machineId) {
    logger.debug("Deleting machine: {}", machineId);

    return persistencePort.delete(ENTITY_TYPE_MACHINE, machineId);
  }

  /**
   * Saves a configuration entry.
   *
   * @param key The configuration key
   * @param value The configuration value
   * @return The persistence result
   */
  public PersistenceResult saveConfiguration(String key, String value) {
    logger.debug("Saving configuration: {}", key);

    Map<String, Object> data = new HashMap<>();
    data.put("key", key);
    data.put("value", value);
    data.put("timestamp", System.currentTimeMillis());

    return persistencePort.save(ENTITY_TYPE_CONFIGURATION, key, data);
  }

  /**
   * Updates a configuration entry.
   *
   * @param key The configuration key
   * @param value The configuration value
   * @return The persistence result
   */
  public PersistenceResult updateConfiguration(String key, String value) {
    logger.debug("Updating configuration: {}", key);

    if (!persistencePort.exists(ENTITY_TYPE_CONFIGURATION, key)) {
      return saveConfiguration(key, value);
    }

    Map<String, Object> data = new HashMap<>();
    data.put("key", key);
    data.put("value", value);
    data.put("timestamp", System.currentTimeMillis());

    return persistencePort.update(ENTITY_TYPE_CONFIGURATION, key, data);
  }

  /**
   * Gets a configuration value.
   *
   * @param key The configuration key
   * @return The configuration value if found
   */
  public Optional<String> getConfigurationValue(String key) {
    logger.debug("Getting configuration value: {}", key);

    Optional<Map<String, Object>> data = persistencePort.findById(ENTITY_TYPE_CONFIGURATION, key);

    return data.map(d -> (String) d.get("value"));
  }

  /**
   * Gets all configuration entries.
   *
   * @return A map of configuration entries
   */
  public Map<String, String> getAllConfigurationEntries() {
    logger.debug("Getting all configuration entries");

    List<Map<String, Object>> entries = persistencePort.findByType(ENTITY_TYPE_CONFIGURATION);

    Map<String, String> result = new HashMap<>();

    for (Map<String, Object> entry : entries) {
      result.put((String) entry.get("key"), (String) entry.get("value"));
    }

    return result;
  }

  /**
   * Deletes a configuration entry.
   *
   * @param key The configuration key
   * @return The persistence result
   */
  public PersistenceResult deleteConfiguration(String key) {
    logger.debug("Deleting configuration: {}", key);

    return persistencePort.delete(ENTITY_TYPE_CONFIGURATION, key);
  }

  /**
   * Gets the component count.
   *
   * @return The component count
   */
  public int getComponentCount() {
    return persistencePort.count(ENTITY_TYPE_COMPONENT);
  }

  /**
   * Gets the machine count.
   *
   * @return The machine count
   */
  public int getMachineCount() {
    return persistencePort.count(ENTITY_TYPE_MACHINE);
  }

  /**
   * Gets the storage type.
   *
   * @return The storage type
   */
  public PersistencePort.StorageType getStorageType() {
    return persistencePort.getStorageType();
  }

  /**
   * Initializes the persistence system.
   *
   * @return true if initialization was successful, false otherwise
   */
  public boolean initialize() {
    logger.info("Initializing persistence system");
    return persistencePort.initialize();
  }

  /**
   * Shuts down the persistence system.
   *
   * @return true if shutdown was successful, false otherwise
   */
  public boolean shutdown() {
    logger.info("Shutting down persistence system");
    return persistencePort.shutdown();
  }

  /**
   * Generates a unique ID.
   *
   * @return A unique ID
   */
  public String generateUniqueId() {
    return UUID.randomUUID().toString().substring(0, 8);
  }

  /**
   * Creates a map of component data.
   *
   * @param component The component
   * @return The component data
   */
  private Map<String, Object> createComponentData(ComponentPort component) {
    Map<String, Object> data = new HashMap<>();

    data.put("id", component.getId().getShortId());
    data.put("name", component.getName());
    data.put("type", component.getType());
    data.put("state", component.getState().toString());

    return data;
  }

  /**
   * Creates a map of machine data.
   *
   * @param machine The machine
   * @return The machine data
   */
  private Map<String, Object> createMachineData(MachinePort machine) {
    Map<String, Object> data = new HashMap<>();

    data.put("id", machine.getId().getShortId());
    data.put("name", machine.getName());
    data.put("type", machine.getType().toString());
    data.put("state", machine.getState().toString());
    data.put("active", machine.isActive());

    return data;
  }
}
