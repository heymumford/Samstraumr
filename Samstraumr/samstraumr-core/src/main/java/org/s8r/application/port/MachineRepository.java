/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Application layer port for Machine repository
 */

package org.s8r.application.port;

import java.util.List;
import java.util.Optional;

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
  void save(Machine machine);

  /**
   * Finds a machine by its ID.
   *
   * @param id The machine ID to find
   * @return An Optional containing the machine if found, or empty if not found
   */
  Optional<Machine> findById(ComponentId id);

  /**
   * Finds all machines.
   *
   * @return A list of all machines
   */
  List<Machine> findAll();

  /**
   * Finds machines by type.
   *
   * @param type The machine type to filter by
   * @return A list of machines of the specified type
   */
  List<Machine> findByType(MachineType type);

  /**
   * Finds machines by name (partial match).
   *
   * @param name The machine name (or part of it) to search for
   * @return A list of machines with names containing the search string
   */
  List<Machine> findByName(String name);

  /**
   * Finds machines containing a specific component.
   *
   * @param componentId The component ID to search for
   * @return A list of machines containing the specified component
   */
  List<Machine> findContainingComponent(ComponentId componentId);

  /**
   * Deletes a machine.
   *
   * @param id The ID of the machine to delete
   */
  void delete(ComponentId id);
}
