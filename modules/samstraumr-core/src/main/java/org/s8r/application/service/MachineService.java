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

package org.s8r.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.s8r.application.dto.MachineDto;
import org.s8r.application.port.ComponentRepository;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.MachineRepository;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.component.port.CompositeComponentPort;
import org.s8r.domain.component.port.MachineFactoryPort;
import org.s8r.domain.component.port.MachinePort;
import org.s8r.domain.exception.ComponentNotFoundException;
import org.s8r.domain.exception.InvalidOperationException;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.machine.MachineType;

/**
 * Application service for managing machines.
 *
 * <p>This service orchestrates operations on machines, following Clean Architecture principles by
 * depending only on domain entities and application layer ports (interfaces).
 */
public class MachineService {
  private final MachineRepository machineRepository;
  private final ComponentRepository componentRepository;
  private final LoggerPort logger;
  private final MachineFactoryPort machineFactory;

  /**
   * Creates a new MachineService.
   *
   * @param machineRepository The machine repository
   * @param componentRepository The component repository
   * @param machineFactory The machine factory
   * @param logger The logger
   */
  public MachineService(
      MachineRepository machineRepository,
      ComponentRepository componentRepository,
      MachineFactoryPort machineFactory,
      LoggerPort logger) {
    this.machineRepository = machineRepository;
    this.componentRepository = componentRepository;
    this.machineFactory = machineFactory;
    this.logger = logger;
  }

  /**
   * Creates a new machine.
   *
   * @param type The machine type
   * @param name The machine name
   * @param description The machine description
   * @param version The machine version
   * @return A DTO of the created machine
   */
  public MachineDto createMachine(
      MachineType type, String name, String description, String version) {
    logger.info("Creating machine: " + name + " (" + type + ")");

    // Create configuration for the machine
    java.util.Map<String, Object> config = new java.util.HashMap<>();
    config.put("name", name);
    config.put("description", description);
    config.put("version", version);

    // Use the machine factory port to create the machine
    MachinePort machinePort = machineFactory.createMachine(type, name, config);

    // Save the machine port
    machineRepository.save(machinePort);

    logger.info("Machine created successfully: " + machinePort.getId().getShortId());

    // Create the DTO using the port interface
    return new MachineDto(machinePort);
  }

  /**
   * Creates a predefined type of machine using the factory.
   *
   * @param typeName The name of the machine type (must match a MachineType enum value)
   * @param name The machine name
   * @param description The machine description
   * @return A DTO of the created machine
   * @throws IllegalArgumentException if the machine type name is invalid
   */
  public MachineDto createMachineByType(String typeName, String name, String description) {
    try {
      MachineType type = MachineType.valueOf(typeName.toUpperCase());
      return createMachine(type, name, description, "1.0.0");
    } catch (IllegalArgumentException e) {
      logger.error("Invalid machine type: " + typeName);
      throw new IllegalArgumentException(
          "Invalid machine type: "
              + typeName
              + ". Valid types are: "
              + String.join(", ", getValidMachineTypes()));
    }
  }

  /**
   * Gets a list of valid machine type names.
   *
   * @return An array of valid machine type names
   */
  private String[] getValidMachineTypes() {
    MachineType[] types = MachineType.values();
    String[] typeNames = new String[types.length];

    for (int i = 0; i < types.length; i++) {
      typeNames[i] = types[i].name();
    }

    return typeNames;
  }

  /**
   * Adds a component to a machine.
   *
   * @param machineId The machine ID
   * @param componentId The component ID
   * @throws ComponentNotFoundException if either component is not found
   * @throws org.s8r.domain.exception.NonExistentComponentReferenceException if the referenced
   *     component doesn't exist
   * @throws InvalidOperationException if the machine is not in a valid state for adding components
   * @throws IllegalArgumentException if the component is not a composite component
   */
  public void addComponentToMachine(ComponentId machineId, ComponentId componentId) {
    logger.info(
        "Adding component " + componentId.getShortId() + " to machine " + machineId.getShortId());

    // Get the machine port
    MachinePort machinePort = getMachineOrThrow(machineId);

    // Validate that the referenced component exists
    org.s8r.domain.validation.ComponentReferenceValidator.validateComponentReference(
        "addComponentToMachine",
        machineId,
        componentId,
        id -> componentRepository.findById(id).isPresent());

    // Get the component port
    ComponentPort componentPort =
        componentRepository
            .findById(componentId)
            .orElseThrow(() -> new ComponentNotFoundException(componentId));

    // Ensure the component is a composite component port
    if (!(componentPort instanceof CompositeComponentPort)) {
      throw new IllegalArgumentException(
          "Component " + componentId.getShortId() + " is not a composite component");
    }

    // Add the component to the machine
    CompositeComponentPort compositeComponentPort = (CompositeComponentPort) componentPort;
    boolean success =
        machinePort.addComposite(compositeComponentPort.getCompositeId(), compositeComponentPort);

    if (!success) {
      throw new InvalidOperationException("Failed to add component to machine");
    }

    // Save the updated machine
    machineRepository.save(machinePort);
    logger.info("Component added to machine successfully");
  }

  /**
   * Removes a component from a machine.
   *
   * @param machineId The machine ID
   * @param componentId The component ID
   * @throws ComponentNotFoundException if either component is not found
   * @throws org.s8r.domain.exception.NonExistentComponentReferenceException if the referenced
   *     component doesn't exist
   * @throws InvalidOperationException if the machine is not in a valid state for removing
   *     components
   */
  public void removeComponentFromMachine(ComponentId machineId, ComponentId componentId) {
    logger.info(
        "Removing component "
            + componentId.getShortId()
            + " from machine "
            + machineId.getShortId());

    // Get the machine port
    MachinePort machinePort = getMachineOrThrow(machineId);

    // Validate that the referenced component exists
    org.s8r.domain.validation.ComponentReferenceValidator.validateComponentReference(
        "removeComponentFromMachine",
        machineId,
        componentId,
        id -> componentRepository.findById(id).isPresent());

    // Try to find the component by ID to get its name
    // This is needed because MachinePort.removeComposite uses the name, not the ID
    ComponentPort componentPort =
        componentRepository
            .findById(componentId)
            .orElseThrow(() -> new ComponentNotFoundException(componentId));

    if (!(componentPort instanceof CompositeComponentPort)) {
      throw new IllegalArgumentException(
          "Component " + componentId.getShortId() + " is not a composite component");
    }

    CompositeComponentPort compositeComponentPort = (CompositeComponentPort) componentPort;
    String compositeName = compositeComponentPort.getCompositeId();

    // Remove the component from the machine
    java.util.Optional<CompositeComponentPort> removed = machinePort.removeComposite(compositeName);
    if (!removed.isPresent()) {
      throw new ComponentNotFoundException(
          ComponentId.create("Component not found in machine: " + compositeName));
    }

    // Save the updated machine
    machineRepository.save(machinePort);
    logger.info("Component removed from machine successfully");
  }

  /**
   * Initializes a machine.
   *
   * @param machineId The machine ID
   * @throws ComponentNotFoundException if the machine is not found
   * @throws InvalidOperationException if the machine is not in a valid state for initialization
   */
  public void initializeMachine(ComponentId machineId) {
    logger.info("Initializing machine: " + machineId.getShortId());

    MachinePort machinePort = getMachineOrThrow(machineId);

    // In MachinePort, initialization might be handled differently
    // We use the start method as it's the closest equivalent
    if (!machinePort.start()) {
      throw new InvalidOperationException("Failed to initialize machine " + machineId.getShortId());
    }

    machineRepository.save(machinePort);
    logger.info("Machine initialized successfully");
  }

  /**
   * Starts a machine.
   *
   * @param machineId The machine ID
   * @throws ComponentNotFoundException if the machine is not found
   * @throws InvalidOperationException if the machine is not in a valid state for starting
   */
  public void startMachine(ComponentId machineId) {
    logger.info("Starting machine: " + machineId.getShortId());

    MachinePort machinePort = getMachineOrThrow(machineId);
    if (!machinePort.start()) {
      throw new InvalidOperationException("Failed to start machine " + machineId.getShortId());
    }

    machineRepository.save(machinePort);
    logger.info("Machine started successfully");
  }

  /**
   * Stops a machine.
   *
   * @param machineId The machine ID
   * @throws ComponentNotFoundException if the machine is not found
   * @throws InvalidOperationException if the machine is not in a valid state for stopping
   */
  public void stopMachine(ComponentId machineId) {
    logger.info("Stopping machine: " + machineId.getShortId());

    MachinePort machinePort = getMachineOrThrow(machineId);
    if (!machinePort.stop()) {
      throw new InvalidOperationException("Failed to stop machine " + machineId.getShortId());
    }

    machineRepository.save(machinePort);
    logger.info("Machine stopped successfully");
  }

  /**
   * Destroys a machine.
   *
   * @param machineId The machine ID
   * @throws ComponentNotFoundException if the machine is not found
   */
  public void destroyMachine(ComponentId machineId) {
    logger.info("Destroying machine: " + machineId.getShortId());

    MachinePort machinePort = getMachineOrThrow(machineId);
    // In MachinePort, we use terminate as the closest equivalent to destroy
    machinePort.terminate();

    machineRepository.save(machinePort);
    logger.info("Machine destroyed successfully");
  }

  /**
   * Gets a machine by ID.
   *
   * @param machineId The machine ID
   * @return A DTO of the machine if found
   * @throws ComponentNotFoundException if the machine is not found
   */
  public MachineDto getMachine(ComponentId machineId) {
    logger.debug("Getting machine: " + machineId.getShortId());

    MachinePort machinePort = getMachineOrThrow(machineId);
    return new MachineDto(machinePort);
  }

  /**
   * Gets all machines.
   *
   * @return A list of machine DTOs
   */
  public List<MachineDto> getAllMachines() {
    logger.debug("Getting all machines");

    // Use the port interface
    return machineRepository.findAll().stream().map(MachineDto::new).collect(Collectors.toList());
  }

  /**
   * Gets machines by type.
   *
   * @param type The machine type
   * @return A list of machine DTOs
   */
  public List<MachineDto> getMachinesByType(MachineType type) {
    logger.debug("Getting machines by type: " + type);

    // Use the port interface
    return machineRepository.findByType(type).stream()
        .map(MachineDto::new)
        .collect(Collectors.toList());
  }

  /**
   * Gets machines by name.
   *
   * @param name The machine name
   * @return A list of machine DTOs
   */
  public List<MachineDto> getMachinesByName(String name) {
    logger.debug("Getting machines by name: " + name);

    // Use the port interface
    return machineRepository.findByName(name).stream()
        .map(MachineDto::new)
        .collect(Collectors.toList());
  }

  /**
   * Gets machines containing a specific component.
   *
   * @param componentId The component ID
   * @return A list of machine DTOs
   */
  public List<MachineDto> getMachinesContainingComponent(ComponentId componentId) {
    logger.debug("Getting machines containing component: " + componentId.getShortId());

    // Use the port interface
    return machineRepository.findContainingComponent(componentId).stream()
        .map(MachineDto::new)
        .collect(Collectors.toList());
  }

  /**
   * Updates the version of a machine.
   *
   * @param machineId The machine ID
   * @param version The new version
   * @throws ComponentNotFoundException if the machine is not found
   * @throws InvalidOperationException if the machine is not in a valid state for updating the
   *     version
   * @throws UnsupportedOperationException if the machine port does not support version updates
   */
  public void updateMachineVersion(ComponentId machineId, String version) {
    logger.info("Updating version of machine " + machineId.getShortId() + " to " + version);

    MachinePort machinePort = getMachineOrThrow(machineId);

    // MachinePort interface doesn't directly support version updates
    // This would typically be handled by a specific version management service
    // We'll throw an exception for now, but in a real implementation,
    // we might use a MachineVersionManager service or similar
    throw new UnsupportedOperationException(
        "Version management is not supported through the MachinePort interface");
  }

  /**
   * Deletes a machine.
   *
   * @param machineId The machine ID
   * @throws ComponentNotFoundException if the machine is not found
   */
  public void deleteMachine(ComponentId machineId) {
    logger.info("Deleting machine: " + machineId.getShortId());

    // Check if the machine exists
    if (!machineRepository.findById(machineId).isPresent()) {
      throw new ComponentNotFoundException(machineId);
    }

    machineRepository.delete(machineId);
    logger.info("Machine deleted successfully");
  }

  /**
   * Helper method to get a machine or throw a ComponentNotFoundException.
   *
   * @param machineId The machine ID
   * @return The machine port
   * @throws ComponentNotFoundException if the machine is not found
   */
  private MachinePort getMachineOrThrow(ComponentId machineId) {
    return machineRepository
        .findById(machineId)
        .orElseThrow(() -> new ComponentNotFoundException(machineId));
  }
}
