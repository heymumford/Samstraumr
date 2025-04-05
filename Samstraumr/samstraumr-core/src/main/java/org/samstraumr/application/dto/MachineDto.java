/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * DTO for Machine in the S8r framework
 */

package org.samstraumr.application.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.samstraumr.domain.machine.Machine;
import org.samstraumr.domain.machine.MachineState;
import org.samstraumr.domain.machine.MachineType;

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
