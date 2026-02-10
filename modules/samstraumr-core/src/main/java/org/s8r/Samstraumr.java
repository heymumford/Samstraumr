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

package org.s8r;

import java.util.List;

import org.s8r.application.ServiceLocator;
import org.s8r.application.dto.ComponentDto;
import org.s8r.application.dto.MachineDto;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.S8rFacade;
import org.s8r.application.service.ComponentService;
import org.s8r.application.service.MachineService;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.machine.MachineType;
import org.s8r.infrastructure.config.Configuration;
import org.s8r.infrastructure.config.DependencyContainer;
import org.s8r.infrastructure.logging.S8rLoggerFactory;

/**
 * Facade for the S8r framework.
 *
 * <p>This class provides a simplified interface to the framework's functionality, hiding the
 * complexity of the underlying implementation. It follows the Facade design pattern to provide a
 * high-level interface to the framework.
 */
public class Samstraumr implements S8rFacade {
  private static final Samstraumr instance = new Samstraumr();
  private final DependencyContainer container;
  private final LoggerPort logger;

  /** Private constructor to enforce singleton pattern. */
  private Samstraumr() {
    this.container = DependencyContainer.getInstance();
    this.logger = S8rLoggerFactory.getInstance().getLogger(Samstraumr.class);

    // Initialize the ServiceLocator with the DependencyContainer
    // This breaks the circular dependency by using the Service Locator pattern
    ServiceLocator.setServiceFactory(container);

    // Register this instance as the S8rFacade implementation
    // This is done after ServiceLocator is initialized to prevent circular dependencies
    container.registerFramework(this);

    logger.info("Samstraumr framework initialized");
  }

  /**
   * Gets the singleton instance.
   *
   * @return The Samstraumr instance
   */
  public static Samstraumr getInstance() {
    return instance;
  }

  /**
   * Gets the dependency container.
   *
   * @return The dependency container
   */
  public DependencyContainer getContainer() {
    return container;
  }

  /**
   * Gets the configuration.
   *
   * @return The configuration
   */
  public Configuration getConfiguration() {
    return Configuration.getInstance();
  }

  // Component methods

  /**
   * Creates a new component.
   *
   * <p>Internal method that returns the actual ComponentId.
   *
   * @param reason The reason for creating the component
   * @return The ID of the created component
   */
  private ComponentId createComponentInternal(String reason) {
    ComponentService service = container.get(ComponentService.class);
    return service.createComponent(reason);
  }

  @Override
  public String createComponent(String reason) {
    ComponentId id = createComponentInternal(reason);
    return id.getIdString();
  }

  /**
   * Gets a component by ID.
   *
   * <p>Internal method that accepts an actual ComponentId.
   *
   * @param componentId The component ID
   * @return The component DTO
   */
  private ComponentDto getComponentInternal(ComponentId componentId) {
    ComponentService service = container.get(ComponentService.class);
    return ComponentDto.fromDomain(service.getComponent(componentId).orElse(null));
  }

  @Override
  public ComponentDto getComponent(String componentId) {
    try {
      ComponentId id = ComponentId.fromString(componentId, "Get Component");
      return getComponentInternal(id);
    } catch (IllegalArgumentException e) {
      logger.error("Invalid component ID format: " + componentId, e);
      return null;
    }
  }

  @Override
  public List<ComponentDto> getAllComponents() {
    ComponentService service = container.get(ComponentService.class);
    return service.getAllComponents().stream().map(ComponentDto::new).toList();
  }

  // Machine methods

  /**
   * Creates a new machine.
   *
   * @param type The machine type
   * @param name The machine name
   * @param description The machine description
   * @return The created machine DTO
   */
  private MachineDto createMachineInternal(MachineType type, String name, String description) {
    MachineService service = container.get(MachineService.class);
    return service.createMachine(
        type, name, description, getConfiguration().get("machine.default.version", "1.0.0"));
  }

  @Override
  public MachineDto createMachine(String typeName, String name, String description) {
    MachineService service = container.get(MachineService.class);
    return service.createMachineByType(typeName, name, description);
  }

  /**
   * Gets a machine by ID (internal method).
   *
   * @param machineId The machine ID
   * @return The machine DTO
   */
  private MachineDto getMachineInternal(ComponentId machineId) {
    MachineService service = container.get(MachineService.class);
    return service.getMachine(machineId);
  }

  @Override
  public MachineDto getMachine(String machineId) {
    try {
      ComponentId id = ComponentId.fromString(machineId, "Get Machine");
      return getMachineInternal(id);
    } catch (IllegalArgumentException e) {
      logger.error("Invalid machine ID format: " + machineId, e);
      return null;
    }
  }

  @Override
  public List<MachineDto> getAllMachines() {
    MachineService service = container.get(MachineService.class);
    return service.getAllMachines();
  }

  /**
   * Starts a machine (internal method).
   *
   * @param machineId The machine ID
   */
  private void startMachineInternal(ComponentId machineId) {
    MachineService service = container.get(MachineService.class);
    service.startMachine(machineId);
  }

  @Override
  public void startMachine(String machineId) {
    try {
      ComponentId id = ComponentId.fromString(machineId, "Start Machine");
      startMachineInternal(id);
    } catch (IllegalArgumentException e) {
      logger.error("Invalid machine ID format: " + machineId, e);
    }
  }

  /**
   * Stops a machine (internal method).
   *
   * @param machineId The machine ID
   */
  private void stopMachineInternal(ComponentId machineId) {
    MachineService service = container.get(MachineService.class);
    service.stopMachine(machineId);
  }

  @Override
  public void stopMachine(String machineId) {
    try {
      ComponentId id = ComponentId.fromString(machineId, "Stop Machine");
      stopMachineInternal(id);
    } catch (IllegalArgumentException e) {
      logger.error("Invalid machine ID format: " + machineId, e);
    }
  }
}
