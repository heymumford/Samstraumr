/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Application layer service for Machine management
 */

package org.samstraumr.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.samstraumr.application.dto.MachineDto;
import org.samstraumr.application.port.ComponentRepository;
import org.samstraumr.application.port.LoggerPort;
import org.samstraumr.application.port.MachineRepository;
import org.samstraumr.domain.component.Component;
import org.samstraumr.domain.component.composite.CompositeComponent;
import org.samstraumr.domain.exception.ComponentNotFoundException;
import org.samstraumr.domain.exception.InvalidOperationException;
import org.samstraumr.domain.identity.ComponentId;
import org.samstraumr.domain.machine.Machine;
import org.samstraumr.domain.machine.MachineFactory;
import org.samstraumr.domain.machine.MachineType;

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

  /**
   * Creates a new MachineService.
   *
   * @param machineRepository The machine repository
   * @param componentRepository The component repository
   * @param logger The logger
   */
  public MachineService(
      MachineRepository machineRepository,
      ComponentRepository componentRepository,
      LoggerPort logger) {
    this.machineRepository = machineRepository;
    this.componentRepository = componentRepository;
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

    Machine machine = MachineFactory.createMachine(type, name, description, version);
    machineRepository.save(machine);

    logger.info("Machine created successfully: " + machine.getId().getShortId());

    return new MachineDto(machine);
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
   * @throws InvalidOperationException if the machine is not in a valid state for adding components
   * @throws IllegalArgumentException if the component is not a composite component
   */
  public void addComponentToMachine(ComponentId machineId, ComponentId componentId) {
    logger.info(
        "Adding component " + componentId.getShortId() + " to machine " + machineId.getShortId());

    // Get the machine
    Machine machine = getMachineOrThrow(machineId);

    // Get the component
    Component component =
        componentRepository
            .findById(componentId)
            .orElseThrow(() -> new ComponentNotFoundException(componentId));

    // Ensure the component is a composite component
    if (!(component instanceof CompositeComponent)) {
      throw new IllegalArgumentException(
          "Component " + componentId.getShortId() + " is not a composite component");
    }

    // Add the component to the machine
    machine.addComponent((CompositeComponent) component);

    // Save the updated machine
    machineRepository.save(machine);
    logger.info("Component added to machine successfully");
  }

  /**
   * Removes a component from a machine.
   *
   * @param machineId The machine ID
   * @param componentId The component ID
   * @throws ComponentNotFoundException if either component is not found
   * @throws InvalidOperationException if the machine is not in a valid state for removing
   *     components
   */
  public void removeComponentFromMachine(ComponentId machineId, ComponentId componentId) {
    logger.info(
        "Removing component "
            + componentId.getShortId()
            + " from machine "
            + machineId.getShortId());

    // Get the machine
    Machine machine = getMachineOrThrow(machineId);

    // Remove the component from the machine
    machine.removeComponent(componentId);

    // Save the updated machine
    machineRepository.save(machine);
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

    Machine machine = getMachineOrThrow(machineId);
    machine.initialize();

    machineRepository.save(machine);
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

    Machine machine = getMachineOrThrow(machineId);
    machine.start();

    machineRepository.save(machine);
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

    Machine machine = getMachineOrThrow(machineId);
    machine.stop();

    machineRepository.save(machine);
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

    Machine machine = getMachineOrThrow(machineId);
    machine.destroy();

    machineRepository.save(machine);
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

    Machine machine = getMachineOrThrow(machineId);
    return new MachineDto(machine);
  }

  /**
   * Gets all machines.
   *
   * @return A list of machine DTOs
   */
  public List<MachineDto> getAllMachines() {
    logger.debug("Getting all machines");

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
   */
  public void updateMachineVersion(ComponentId machineId, String version) {
    logger.info("Updating version of machine " + machineId.getShortId() + " to " + version);

    Machine machine = getMachineOrThrow(machineId);
    machine.setVersion(version);

    machineRepository.save(machine);
    logger.info("Machine version updated successfully");
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
   * @return The machine
   * @throws ComponentNotFoundException if the machine is not found
   */
  private Machine getMachineOrThrow(ComponentId machineId) {
    return machineRepository
        .findById(machineId)
        .orElseThrow(() -> new ComponentNotFoundException(machineId));
  }
}
