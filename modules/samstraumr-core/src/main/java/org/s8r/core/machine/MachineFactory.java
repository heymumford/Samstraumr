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

package org.s8r.core.machine;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.s8r.core.composite.Composite;
import org.s8r.core.composite.CompositeFactory;
import org.s8r.core.composite.CompositeType;
import org.s8r.core.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for creating and managing Machine instances.
 *
 * <p>This factory provides methods to create different types of preconfigured machines and a
 * registry to track all created machines.
 */
public class MachineFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(MachineFactory.class);
  private static final Map<String, Machine> MACHINE_REGISTRY = new ConcurrentHashMap<>();

  private MachineFactory() {
    // Private constructor to prevent instantiation
  }

  /**
   * Creates a data processor machine.
   *
   * @param name The machine name
   * @param description The machine description
   * @param environment The environment
   * @return A new data processor machine
   */
  public static Machine createDataProcessor(
      String name, String description, Environment environment) {
    String machineId = generateMachineId("DP");
    Machine machine =
        Machine.create(
            machineId, MachineType.DATA_PROCESSOR, name, description, "1.0.0", environment);
    MACHINE_REGISTRY.put(machineId, machine);

    // Create and configure standard data processor components
    Composite inputHandler =
        CompositeFactory.createComposite("input-handler", CompositeType.OBSERVER, environment);
    Composite transformer =
        CompositeFactory.createComposite(
            "data-transformer", CompositeType.TRANSFORMER, environment);
    Composite outputHandler =
        CompositeFactory.createComposite("output-handler", CompositeType.OBSERVER, environment);

    // Add composites to machine
    machine
        .addComposite("input", inputHandler)
        .addComposite("transformer", transformer)
        .addComposite("output", outputHandler);

    // Connect composites
    machine.connect("input", "transformer").connect("transformer", "output");

    LOGGER.info("Created and registered data processor machine: {}", machineId);
    return machine;
  }

  /**
   * Creates an analytics machine.
   *
   * @param name The machine name
   * @param description The machine description
   * @param environment The environment
   * @return A new analytics machine
   */
  public static Machine createAnalytics(String name, String description, Environment environment) {
    String machineId = generateMachineId("AN");
    Machine machine =
        Machine.create(machineId, MachineType.ANALYTICS, name, description, "1.0.0", environment);
    MACHINE_REGISTRY.put(machineId, machine);

    // Create and configure standard analytics components
    Composite dataCollector =
        CompositeFactory.createComposite("data-collector", CompositeType.OBSERVER, environment);
    Composite analyzer =
        CompositeFactory.createComposite("analyzer", CompositeType.TRANSFORMER, environment);
    Composite reporter =
        CompositeFactory.createComposite("reporter", CompositeType.OBSERVER, environment);

    // Add composites to machine
    machine
        .addComposite("collector", dataCollector)
        .addComposite("analyzer", analyzer)
        .addComposite("reporter", reporter);

    // Connect composites
    machine.connect("collector", "analyzer").connect("analyzer", "reporter");

    LOGGER.info("Created and registered analytics machine: {}", machineId);
    return machine;
  }

  /**
   * Creates a monitoring machine.
   *
   * @param name The machine name
   * @param description The machine description
   * @param environment The environment
   * @return A new monitoring machine
   */
  public static Machine createMonitoring(String name, String description, Environment environment) {
    String machineId = generateMachineId("MON");
    Machine machine =
        Machine.create(machineId, MachineType.MONITORING, name, description, "1.0.0", environment);
    MACHINE_REGISTRY.put(machineId, machine);

    // Create and configure standard monitoring components
    Composite observer =
        CompositeFactory.createComposite("system-observer", CompositeType.OBSERVER, environment);
    Composite validator =
        CompositeFactory.createComposite("validator", CompositeType.VALIDATOR, environment);
    Composite alerter =
        CompositeFactory.createComposite("alerter", CompositeType.OBSERVER, environment);

    // Add composites to machine
    machine
        .addComposite("observer", observer)
        .addComposite("validator", validator)
        .addComposite("alerter", alerter);

    // Connect composites
    machine.connect("observer", "validator").connect("validator", "alerter");

    LOGGER.info("Created and registered monitoring machine: {}", machineId);
    return machine;
  }

  /**
   * Creates a workflow machine.
   *
   * @param name The machine name
   * @param description The machine description
   * @param environment The environment
   * @return A new workflow machine
   */
  public static Machine createWorkflow(String name, String description, Environment environment) {
    String machineId = generateMachineId("WF");
    Machine machine =
        Machine.create(machineId, MachineType.WORKFLOW, name, description, "1.0.0", environment);
    MACHINE_REGISTRY.put(machineId, machine);

    // Create and configure standard workflow components
    Composite trigger =
        CompositeFactory.createComposite("trigger", CompositeType.OBSERVER, environment);
    Composite orchestrator =
        CompositeFactory.createComposite("orchestrator", CompositeType.TRANSFORMER, environment);
    Composite executor =
        CompositeFactory.createComposite("executor", CompositeType.TRANSFORMER, environment);
    Composite reporter =
        CompositeFactory.createComposite("reporter", CompositeType.OBSERVER, environment);

    // Add composites to machine
    machine
        .addComposite("trigger", trigger)
        .addComposite("orchestrator", orchestrator)
        .addComposite("executor", executor)
        .addComposite("reporter", reporter);

    // Connect composites
    machine
        .connect("trigger", "orchestrator")
        .connect("orchestrator", "executor")
        .connect("executor", "reporter");

    LOGGER.info("Created and registered workflow machine: {}", machineId);
    return machine;
  }

  /**
   * Creates a custom machine.
   *
   * @param type The machine type
   * @param name The machine name
   * @param description The machine description
   * @param environment The environment
   * @return A new custom machine
   */
  public static Machine createMachine(
      MachineType type, String name, String description, Environment environment) {
    String machineId = generateMachineId(type.name().substring(0, 3).toUpperCase());
    Machine machine = Machine.create(machineId, type, name, description, "1.0.0", environment);
    MACHINE_REGISTRY.put(machineId, machine);
    LOGGER.info("Created and registered custom machine: {}", machineId);
    return machine;
  }

  /**
   * Gets a machine by its ID.
   *
   * @param machineId The ID of the machine to retrieve
   * @return Optional containing the machine if found, empty otherwise
   */
  public static Optional<Machine> getMachine(String machineId) {
    return Optional.ofNullable(MACHINE_REGISTRY.get(machineId));
  }

  /**
   * Gets all registered machines.
   *
   * @return An unmodifiable map of machine IDs to machines
   */
  public static Map<String, Machine> getAllMachines() {
    return Collections.unmodifiableMap(MACHINE_REGISTRY);
  }

  /**
   * Removes a machine from the registry.
   *
   * @param machineId The ID of the machine to remove
   * @return true if the machine was removed, false if it wasn't found
   */
  public static boolean removeMachine(String machineId) {
    Machine machine = MACHINE_REGISTRY.remove(machineId);
    if (machine != null) {
      try {
        machine.stop();
        machine.destroy();
        LOGGER.info("Removed machine from registry: {}", machineId);
        return true;
      } catch (MachineException e) {
        LOGGER.warn(
            "Failed to cleanly stop machine {} during removal: {}", machineId, e.getMessage());
        return true;
      }
    }
    return false;
  }

  /** Shuts down all registered machines. */
  public static void shutdownAllMachines() {
    LOGGER.info("Shutting down all {} registered machines", MACHINE_REGISTRY.size());
    for (Machine machine : MACHINE_REGISTRY.values()) {
      try {
        if (machine.isActive()) {
          machine.stop();
        }
        machine.destroy();
      } catch (MachineException e) {
        LOGGER.warn("Error shutting down machine {}: {}", machine.getMachineId(), e.getMessage());
      }
    }
    MACHINE_REGISTRY.clear();
    LOGGER.info("All machines shut down and registry cleared");
  }

  /**
   * Generates a unique machine ID with the given prefix.
   *
   * @param prefix The prefix for the machine ID
   * @return A unique machine ID
   */
  private static String generateMachineId(String prefix) {
    return prefix + "-" + UUID.randomUUID().toString().substring(0, 8);
  }
}
