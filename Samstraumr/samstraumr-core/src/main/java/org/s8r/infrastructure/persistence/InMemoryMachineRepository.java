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

import org.s8r.application.port.MachineRepository;
import org.s8r.domain.component.composite.CompositeComponent;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.machine.Machine;
import org.s8r.domain.machine.MachineType;

/**
 * In-memory implementation of the MachineRepository interface.
 *
 * <p>This adapter provides a simple in-memory storage for machines, primarily for testing or simple
 * applications. It follows Clean Architecture principles by implementing an application layer port.
 */
public class InMemoryMachineRepository implements MachineRepository {
  // Using a map to store machines with their IDs as keys
  private final Map<String, Machine> machineStore = new HashMap<>();

  @Override
  public void save(Machine machine) {
    machineStore.put(machine.getId().getIdString(), machine);
  }

  @Override
  public Optional<Machine> findById(ComponentId id) {
    return Optional.ofNullable(machineStore.get(id.getIdString()));
  }

  @Override
  public List<Machine> findAll() {
    return new ArrayList<>(machineStore.values());
  }

  @Override
  public List<Machine> findByType(MachineType type) {
    return machineStore.values().stream()
        .filter(machine -> machine.getType() == type)
        .collect(Collectors.toList());
  }

  @Override
  public List<Machine> findByName(String name) {
    if (name == null || name.isEmpty()) {
      return new ArrayList<>();
    }

    String searchName = name.toLowerCase();
    return machineStore.values().stream()
        .filter(machine -> machine.getName().toLowerCase().contains(searchName))
        .collect(Collectors.toList());
  }

  @Override
  public List<Machine> findContainingComponent(ComponentId componentId) {
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
  }

  /**
   * Checks if a machine contains a component with the specified ID.
   *
   * @param machine The machine to check
   * @param componentIdStr The component ID string
   * @return true if the machine contains the component, false otherwise
   */
  private boolean containsComponent(Machine machine, String componentIdStr) {
    List<CompositeComponent> components = machine.getComponents();

    // Check if any component matches the ID
    for (CompositeComponent component : components) {
      if (component.getId().getIdString().equals(componentIdStr)) {
        return true;
      }
    }

    return false;
  }

  /** Clears all machines from the repository. */
  public void clear() {
    machineStore.clear();
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
