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

package org.s8r.application.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.component.port.MachinePort;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.machine.Machine;
import org.s8r.domain.machine.MachineState;
import org.s8r.domain.machine.MachineType;

/**
 * Data Transfer Object (DTO) for Machine entities.
 *
 * <p>This class is used to transfer machine data across architectural boundaries without exposing
 * domain entities. It follows Clean Architecture principles by keeping the domain entities isolated
 * from external concerns.
 */
public class MachineDto {
  private final String id;
  private final String shortId;
  private final String name;
  private final String description;
  private final String type;
  private final String category;
  private final String version;
  private final String state;
  private final String stateDescription;
  private final Instant creationTime;
  private final List<ComponentDto> components;
  private final List<String> activityLog;

  /**
   * Creates a new MachineDto from a domain Machine entity.
   *
   * @param machine The domain machine entity
   */
  public MachineDto(Machine machine) {
    this.id = machine.getId().getIdString();
    this.shortId = machine.getId().getShortId();
    this.name = machine.getName();
    this.description = machine.getDescription();

    MachineType type = machine.getType();
    this.type = type.name();
    this.category = type.getCategory().name();

    this.version = machine.getVersion();

    MachineState state = machine.getState();
    this.state = state.name();
    this.stateDescription = state.getDescription();

    this.creationTime = machine.getCreationTime();

    // Convert components to DTOs
    this.components =
        machine.getComponents().stream().map(ComponentDto::new).collect(Collectors.toList());

    this.activityLog = new ArrayList<>(machine.getActivityLog());
  }

  /**
   * Creates a new MachineDto from a MachinePort interface. This constructor supports Clean
   * Architecture by working with the port interface rather than the concrete implementation.
   *
   * @param machinePort The machine port interface
   */
  public MachineDto(MachinePort machinePort) {
    ComponentId componentId = machinePort.getId();
    this.id = componentId.getIdString();
    this.shortId = componentId.getShortId();
    this.name = machinePort.getMachineId(); // Using machineId as name

    // Default description if not available through the port
    this.description = "Machine " + this.shortId;

    MachineType type = machinePort.getMachineType();
    this.type = type.name();
    this.category = type.getCategory().name();

    // Default version if not available through the port
    this.version = "1.0.0";

    MachineState state = machinePort.getMachineState();
    this.state = state.name();
    this.stateDescription = state.getDescription();

    this.creationTime = machinePort.getCreationTime();

    // Convert components to DTOs
    this.components = new ArrayList<>();
    Map<String, ComponentPort> portComponents = machinePort.getComponents();
    for (Map.Entry<String, ComponentPort> entry : portComponents.entrySet()) {
      ComponentPort componentPort = entry.getValue();
      this.components.add(new ComponentDto(componentPort));
    }

    this.activityLog = machinePort.getActivityLog();
  }

  /**
   * Creates a new MachineDto with the specified values.
   *
   * @param id The machine ID
   * @param shortId The short machine ID
   * @param name The machine name
   * @param description The machine description
   * @param type The machine type
   * @param category The machine category
   * @param version The machine version
   * @param state The machine state
   * @param stateDescription The machine state description
   * @param creationTime The machine creation time
   * @param components The machine components
   * @param activityLog The machine activity log
   */
  public MachineDto(
      String id,
      String shortId,
      String name,
      String description,
      String type,
      String category,
      String version,
      String state,
      String stateDescription,
      Instant creationTime,
      List<ComponentDto> components,
      List<String> activityLog) {
    this.id = id;
    this.shortId = shortId;
    this.name = name;
    this.description = description;
    this.type = type;
    this.category = category;
    this.version = version;
    this.state = state;
    this.stateDescription = stateDescription;
    this.creationTime = creationTime;
    this.components = new ArrayList<>(components);
    this.activityLog = new ArrayList<>(activityLog);
  }

  // Getter methods

  /**
   * Gets the machine ID.
   *
   * @return The machine ID
   */
  public String getId() {
    return id;
  }

  /**
   * Gets the short machine ID.
   *
   * @return The short machine ID
   */
  public String getShortId() {
    return shortId;
  }

  /**
   * Gets the machine name.
   *
   * @return The machine name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the machine description.
   *
   * @return The machine description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the machine type.
   *
   * @return The machine type
   */
  public String getType() {
    return type;
  }

  /**
   * Gets the machine category.
   *
   * @return The machine category
   */
  public String getCategory() {
    return category;
  }

  /**
   * Gets the machine version.
   *
   * @return The machine version
   */
  public String getVersion() {
    return version;
  }

  /**
   * Gets the machine state.
   *
   * @return The machine state
   */
  public String getState() {
    return state;
  }

  /**
   * Gets the machine state description.
   *
   * @return The machine state description
   */
  public String getStateDescription() {
    return stateDescription;
  }

  /**
   * Gets the machine creation time.
   *
   * @return The creation time
   */
  public Instant getCreationTime() {
    return creationTime;
  }

  /**
   * Gets the machine components.
   *
   * @return A list of component DTOs
   */
  public List<ComponentDto> getComponents() {
    return new ArrayList<>(components);
  }

  /**
   * Gets the machine activity log.
   *
   * @return The activity log
   */
  public List<String> getActivityLog() {
    return new ArrayList<>(activityLog);
  }

  /**
   * Gets the composites in this machine.
   *
   * @return A map of composite names to composite DTOs
   */
  public Map<String, CompositeComponentDto> getComposites() {
    // This is a placeholder implementation - in a real implementation,
    // this would return actual composites from the machine
    return new HashMap<>();
  }

  /**
   * Gets the connections between components in this machine.
   *
   * @return A map of source components to lists of target components
   */
  public Map<String, List<String>> getConnections() {
    // This is a placeholder implementation - in a real implementation,
    // this would return actual connections from the machine
    return new HashMap<>();
  }

  /**
   * Checks if this machine is active.
   *
   * @return true if the machine is active, false otherwise
   */
  public boolean isActive() {
    // This is a placeholder implementation - in a real implementation,
    // this would check the machine state
    return "ACTIVE".equals(state) || "RUNNING".equals(state);
  }

  /**
   * Factory method to create a MachineDto from a domain Machine entity.
   *
   * @param machine The domain machine entity
   * @return A new MachineDto or null if the machine is null
   */
  public static MachineDto fromDomain(Machine machine) {
    if (machine == null) {
      return null;
    }
    return new MachineDto(machine);
  }

  /**
   * Factory method to create a MachineDto from a domain MachinePort interface. This supports Clean
   * Architecture by working with the port interface rather than the concrete implementation.
   *
   * @param machinePort The domain machine port interface
   * @return A new MachineDto or null if the machinePort is null
   */
  public static MachineDto fromDomain(MachinePort machinePort) {
    if (machinePort == null) {
      return null;
    }
    return new MachineDto(machinePort);
  }

  @Override
  public String toString() {
    return "MachineDto{"
        + "id='"
        + shortId
        + '\''
        + ", name='"
        + name
        + '\''
        + ", type='"
        + type
        + '\''
        + ", state='"
        + state
        + '\''
        + ", version='"
        + version
        + '\''
        + ", componentCount="
        + components.size()
        + '}';
  }
}
