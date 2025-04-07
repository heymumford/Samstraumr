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
import java.util.Optional;

import org.s8r.domain.component.port.MachinePort;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.machine.Machine;
import org.s8r.domain.machine.MachineType;

/**
 * Port interface for machine persistence operations.
 *
 * <p>This interface defines the contract for storing and retrieving Machine entities. Following the
 * ports and adapters pattern, this is an output port in the application layer, which will be
 * implemented by adapters in the infrastructure layer.
 */
public interface MachineRepository {

  /**
   * Saves a machine.
   *
   * @param machine The machine to save
   */
  void save(MachinePort machine);

  /**
   * Finds a machine by its ID.
   *
   * @param id The machine ID to find
   * @return An Optional containing the machine if found, or empty if not found
   */
  Optional<MachinePort> findById(ComponentId id);

  /**
   * Finds all machines.
   *
   * @return A list of all machines
   */
  List<MachinePort> findAll();

  /**
   * Finds machines by type.
   *
   * @param type The machine type to filter by
   * @return A list of machines of the specified type
   */
  List<MachinePort> findByType(MachineType type);

  /**
   * Finds machines by name (partial match).
   *
   * @param name The machine name (or part of it) to search for
   * @return A list of machines with names containing the search string
   */
  List<MachinePort> findByName(String name);

  /**
   * Finds machines containing a specific component.
   *
   * @param componentId The component ID to search for
   * @return A list of machines containing the specified component
   */
  List<MachinePort> findContainingComponent(ComponentId componentId);

  /**
   * Deletes a machine.
   *
   * @param id The ID of the machine to delete
   */
  void delete(ComponentId id);
}
