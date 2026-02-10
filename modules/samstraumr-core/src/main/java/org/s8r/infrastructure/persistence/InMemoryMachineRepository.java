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

package org.s8r.infrastructure.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.MachineRepository;
import org.s8r.domain.component.port.CompositeComponentPort;
import org.s8r.domain.component.port.MachinePort;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.machine.MachineType;

/**
 * In-memory implementation of the MachineRepository interface.
 *
 * <p>This adapter provides a simple in-memory storage for machines, primarily for testing or simple
 * applications. It follows Clean Architecture principles by implementing an application layer port.
 */
public class InMemoryMachineRepository implements MachineRepository {
  // Using a map to store machine ports with their IDs as keys
  private final Map<String, MachinePort> machineStore = new HashMap<>();
  private final LoggerPort logger;

  /**
   * Creates a new InMemoryMachineRepository.
   *
   * @param logger Logger for recording operations
   */
  public InMemoryMachineRepository(LoggerPort logger) {
    this.logger = logger;
    logger.debug("Initialized InMemoryMachineRepository");
  }

  @Override
  public void save(MachinePort machine) {
    machineStore.put(machine.getId().getIdString(), machine);
    logger.debug("Saved machine: {}", machine.getId().getIdString());
  }

  @Override
  public Optional<MachinePort> findById(ComponentId id) {
    return Optional.ofNullable(machineStore.get(id.getIdString()));
  }

  @Override
  public List<MachinePort> findAll() {
    return new ArrayList<>(machineStore.values());
  }

  @Override
  public List<MachinePort> findByType(MachineType type) {
    return machineStore.values().stream()
        .filter(machine -> machine.getMachineType() == type)
        .collect(Collectors.toList());
  }

  @Override
  public List<MachinePort> findByName(String name) {
    if (name == null || name.isEmpty()) {
      return new ArrayList<>();
    }

    String searchName = name.toLowerCase();
    return machineStore.values().stream()
        .filter(machine -> machine.getMachineId().toLowerCase().contains(searchName))
        .collect(Collectors.toList());
  }

  @Override
  public List<MachinePort> findContainingComponent(ComponentId componentId) {
    if (componentId == null) {
      return new ArrayList<>();
    }

    String componentIdStr = componentId.getIdString();
    return machineStore.values().stream()
        .filter(machine -> containsComponent(machine, componentIdStr))
        .collect(Collectors.toList());
  }

  @Override
  public void delete(ComponentId id) {
    machineStore.remove(id.getIdString());
    logger.debug("Deleted machine: {}", id.getIdString());
  }

  /**
   * Checks if a machine contains a component with the specified ID.
   *
   * @param machine The machine port to check
   * @param componentIdStr The component ID string
   * @return true if the machine contains the component, false otherwise
   */
  private boolean containsComponent(MachinePort machine, String componentIdStr) {
    Map<String, CompositeComponentPort> components = machine.getComposites();

    // Check if any component matches the ID
    for (CompositeComponentPort component : components.values()) {
      if (component.getId().getIdString().equals(componentIdStr)) {
        return true;
      }
    }

    return false;
  }

  /** Clears all machines from the repository. */
  public void clear() {
    machineStore.clear();
    logger.debug("Cleared all machines");
  }

  /**
   * Gets the number of machines in the repository.
   *
   * @return The number of machines
   */
  public int size() {
    return machineStore.size();
  }
}
