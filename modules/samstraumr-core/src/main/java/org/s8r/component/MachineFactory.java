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

package org.s8r.component;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for creating and managing Machine instances.
 *
 * <p>This class provides methods to create preconfigured machines for common processing patterns.
 * It maintains a global registry of all machines created through the factory, allowing for
 * centralized lifecycle management.
 *
 * <p>The factory offers specialized machine configurations for different use cases such as:
 *
 * <ul>
 *   <li>Data processing with input, transformation, and output stages
 *   <li>Monitoring systems with observers and validators
 *   <li>Custom machines with specialized composite arrangements
 * </ul>
 */
public class MachineFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(MachineFactory.class);
  private static final Map<String, Machine> MACHINE_REGISTRY = new ConcurrentHashMap<>();

  private MachineFactory() {
    // Private constructor to prevent instantiation
  }

  /**
   * Creates a new Machine with a generated ID.
   *
   * @param environment The environment for the machine
   * @return The created machine
   */
  public static Machine createMachine(Environment environment) {
    String machineId = generateMachineId();
    return createMachine(machineId, environment);
  }

  /**
   * Creates a new Machine with the specified ID.
   *
   * @param machineId The ID for the machine
   * @param environment The environment for the machine
   * @return The created machine
   */
  public static Machine createMachine(String machineId, Environment environment) {
    Machine machine = new Machine(machineId, environment);
    MACHINE_REGISTRY.put(machineId, machine);
    LOGGER.info("Created and registered machine: {}", machineId);
    return machine;
  }

  /**
   * Creates a data processing machine with input, processing, and output composites.
   *
   * @param environment The environment for the machine
   * @return The created data processing machine
   */
  public static Machine createDataProcessingMachine(Environment environment) {
    Machine machine = createMachine("data-processor-" + generateMachineId(), environment);

    // Create and add composites
    Composite inputComposite = CompositeFactory.createComposite("input-handler", environment);
    Composite processingComposite = CompositeFactory.createProcessingComposite(environment);
    Composite outputComposite = CompositeFactory.createComposite("output-handler", environment);

    // Add composites to machine
    machine
        .addComposite("input", inputComposite)
        .addComposite("processor", processingComposite)
        .addComposite("output", outputComposite);

    // Connect composites
    machine.connect("input", "processor").connect("processor", "output");

    // Update machine state
    machine.updateState("type", "DATA_PROCESSOR");
    machine.updateState("status", "READY");

    LOGGER.info("Created data processing machine: {}", machine.getMachineId());
    return machine;
  }

  /**
   * Creates a monitoring machine for system observation.
   *
   * @param environment The environment for the machine
   * @return The created monitoring machine
   */
  public static Machine createMonitoringMachine(Environment environment) {
    Machine machine = createMachine("monitor-" + generateMachineId(), environment);

    // Create and add composites
    Composite observerComposite = CompositeFactory.createObserverComposite(environment);
    Composite validationComposite = CompositeFactory.createValidationComposite(environment);

    // Add composites to machine
    machine
        .addComposite("observer", observerComposite)
        .addComposite("validator", validationComposite);

    // Connect composites
    machine.connect("observer", "validator");

    // Update machine state
    machine.updateState("type", "MONITORING");
    machine.updateState("status", "READY");

    LOGGER.info("Created monitoring machine: {}", machine.getMachineId());
    return machine;
  }

  /**
   * Creates a transformation pipeline machine with sequential processing stages.
   *
   * @param environment The environment for the machine
   * @return The created transformation pipeline machine
   */
  public static Machine createTransformationPipelineMachine(Environment environment) {
    Machine machine = createMachine("transform-pipeline-" + generateMachineId(), environment);

    // Create transformation composites
    Composite inputStage = CompositeFactory.createComposite("input-stage", environment);
    Composite validationStage = CompositeFactory.createValidationComposite(environment);
    Composite transformStage = CompositeFactory.createTransformationComposite(environment);
    Composite outputStage = CompositeFactory.createComposite("output-stage", environment);

    // Add composites to machine
    machine
        .addComposite("input", inputStage)
        .addComposite("validation", validationStage)
        .addComposite("transformation", transformStage)
        .addComposite("output", outputStage);

    // Connect composites in pipeline sequence
    machine
        .connect("input", "validation")
        .connect("validation", "transformation")
        .connect("transformation", "output");

    // Update machine state
    machine.updateState("type", "TRANSFORMATION_PIPELINE");
    machine.updateState("status", "READY");

    LOGGER.info("Created transformation pipeline machine: {}", machine.getMachineId());
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
      machine.deactivate();
      LOGGER.info("Removed machine from registry: {}", machineId);
      return true;
    }
    return false;
  }

  /**
   * Shuts down all registered machines.
   *
   * <p>This method initiates an orderly shutdown of all machines in the registry, allowing them to
   * clean up resources and preserve their final state before being removed from the registry.
   */
  public static void shutdownAllMachines() {
    LOGGER.info("Shutting down all {} registered machines", MACHINE_REGISTRY.size());
    for (Machine machine : MACHINE_REGISTRY.values()) {
      machine.shutdown();
    }
    MACHINE_REGISTRY.clear();
    LOGGER.info("All machines shut down and registry cleared");
  }

  /**
   * Generates a unique machine ID.
   *
   * @return A unique machine ID
   */
  private static String generateMachineId() {
    return UUID.randomUUID().toString().substring(0, 8);
  }
}
