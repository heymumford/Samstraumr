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

package org.s8r.adapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.s8r.application.port.LoggerPort;
import org.s8r.component.Component;
import org.s8r.component.Composite;
import org.s8r.component.Environment;
import org.s8r.component.Machine;
import org.s8r.component.State;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.component.port.CompositeComponentPort;
import org.s8r.domain.component.port.MachinePort;
import org.s8r.domain.event.DomainEvent;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.domain.machine.MachineState;
import org.s8r.domain.machine.MachineType;

// Legacy types referenced using fully qualified names

/**
 * Adapter for converting between legacy Tube-based Machines and new Component-based Machines.
 *
 * <p>This adapter facilitates migration from legacy code by allowing existing Tube machines to be
 * used with the new Component-based architecture. The adapter handles the conversion of composites,
 * connections, and state management.
 *
 * <p>This adapter implements the Dependency Inversion Principle by also providing conversions that
 * return MachinePort interfaces instead of concrete Machine implementations. This allows client
 * code to depend on abstractions rather than concrete implementations, facilitating a smooth
 * migration to Clean Architecture.
 */
public class MachineAdapter {

  private final LoggerPort logger;
  private final CompositeAdapter compositeAdapter;
  private final TubeLegacyEnvironmentConverter environmentConverter;

  /**
   * Gets the environment from a tube machine using reflection. This is a workaround for missing
   * getEnvironment() method.
   *
   * @param tubeMachine The tube machine
   * @return The environment, or null if it cannot be accessed
   */
  private org.s8r.tube.Environment getTubeMachineEnvironment(
      org.s8r.tube.machine.Machine tubeMachine) {
    if (tubeMachine == null) {
      return null;
    }

    try {
      java.lang.reflect.Field field =
          org.s8r.tube.machine.Machine.class.getDeclaredField("environment");
      field.setAccessible(true);
      return (org.s8r.tube.Environment) field.get(tubeMachine);
    } catch (Exception e) {
      logger.error("Failed to access environment field", e);
      // Create an empty environment as fallback
      return new org.s8r.tube.Environment();
    }
  }

  /**
   * Creates a new MachineAdapter.
   *
   * @param logger Logger for recording operations
   * @param compositeAdapter Adapter for converting Tube composites to Component composites
   * @param environmentConverter Converter for Environment objects
   */
  public MachineAdapter(
      LoggerPort logger,
      CompositeAdapter compositeAdapter,
      TubeLegacyEnvironmentConverter environmentConverter) {
    this.logger = logger;
    this.compositeAdapter = compositeAdapter;
    this.environmentConverter = environmentConverter;

    logger.debug("MachineAdapter initialized");
  }

  /**
   * Converts a legacy Tube machine to a new Component machine.
   *
   * @param tubeMachine The legacy machine to convert
   * @return A new Component-based machine that mirrors the legacy machine
   */
  public Machine tubeMachineToComponentMachine(org.s8r.tube.machine.Machine tubeMachine) {
    logger.debug("Converting tube machine to component machine: {}", tubeMachine.getMachineId());

    // Convert environment
    org.s8r.tube.Environment tubeEnvironment = getTubeMachineEnvironment(tubeMachine);
    org.s8r.component.Environment componentEnvironment =
        environmentConverter.fromLegacyEnvironment(tubeEnvironment);

    // Create new machine
    Machine componentMachine = new Machine(tubeMachine.getMachineId(), componentEnvironment);

    // Convert composites
    Map<String, org.s8r.tube.composite.Composite> tubeComposites = tubeMachine.getComposites();
    for (Map.Entry<String, org.s8r.tube.composite.Composite> entry : tubeComposites.entrySet()) {
      String compositeName = entry.getKey();
      org.s8r.tube.composite.Composite tubeComposite = entry.getValue();

      // Convert and add to the new machine
      Composite componentComposite =
          compositeAdapter.tubeCompositeToComponentComposite(tubeComposite);
      componentMachine.addComposite(compositeName, componentComposite);

      logger.debug("Added converted composite to machine: {}", compositeName);
    }

    // Copy connections
    Map<String, List<String>> connections = tubeMachine.getConnections();
    for (Map.Entry<String, List<String>> entry : connections.entrySet()) {
      String sourceName = entry.getKey();
      for (String targetName : entry.getValue()) {
        componentMachine.connect(sourceName, targetName);
        logger.debug("Added connection to machine: {} -> {}", sourceName, targetName);
      }
    }

    // Copy state
    Map<String, Object> tubeState = tubeMachine.getState();
    for (Map.Entry<String, Object> entry : tubeState.entrySet()) {
      componentMachine.updateState(entry.getKey(), entry.getValue());
    }

    // Copy activation status
    if (!tubeMachine.isActive()) {
      componentMachine.deactivate();
    }

    logger.debug("Completed conversion of tube machine: {}", tubeMachine.getMachineId());
    return componentMachine;
  }

  /**
   * Converts a legacy Tube machine to a MachinePort interface. This follows the Clean Architecture
   * pattern by returning the port interface rather than the concrete implementation.
   *
   * @param tubeMachine The legacy machine to convert
   * @return A MachinePort interface that mirrors the legacy machine
   */
  public MachinePort tubeMachineToMachinePort(org.s8r.tube.machine.Machine tubeMachine) {
    logger.debug("Converting tube machine to MachinePort: {}", tubeMachine.getMachineId());

    // First create a component-based machine
    Machine componentMachine = tubeMachineToComponentMachine(tubeMachine);

    // Wrap in a MachinePort adapter - using component adapter because this is a component machine
    return createMachinePortFromComponent(componentMachine);
  }

  /**
   * Creates a wrapper around a Tube machine, allowing it to be used with Component APIs.
   *
   * @param tubeMachine The Tube machine to wrap
   * @return A Component machine that delegates to the Tube machine
   */
  public Machine wrapTubeMachine(org.s8r.tube.machine.Machine tubeMachine) {
    logger.debug("Creating wrapper for tube machine: {}", tubeMachine.getMachineId());

    // Convert environment
    org.s8r.tube.Environment tubeEnvironment = getTubeMachineEnvironment(tubeMachine);
    org.s8r.component.Environment componentEnvironment =
        environmentConverter.fromLegacyEnvironment(tubeEnvironment);

    // Create wrapper machine
    return new TubeMachineWrapper(
        tubeMachine.getMachineId(), componentEnvironment, tubeMachine, compositeAdapter, logger);
  }

  /**
   * Creates a wrapper around a Tube machine that implements the MachinePort interface. This follows
   * the Clean Architecture pattern by returning the port interface rather than the concrete
   * implementation.
   *
   * @param tubeMachine The Tube machine to wrap
   * @return A MachinePort that delegates to the Tube machine
   */
  public MachinePort wrapTubeMachineAsPort(org.s8r.tube.machine.Machine tubeMachine) {
    logger.debug("Creating MachinePort wrapper for tube machine: {}", tubeMachine.getMachineId());

    // First create a component wrapper
    Machine machineWrapper = wrapTubeMachine(tubeMachine);

    // Wrap in a MachinePort adapter
    return createMachinePortFromComponent(machineWrapper);
  }

  /**
   * Creates a MachinePort interface from a domain Machine implementation. This is a factory method
   * that provides access to the adapter while keeping the adapter class itself private.
   *
   * @param machine The domain machine implementation to adapt
   * @return A MachinePort interface that delegates to the machine
   */
  public static MachinePort createMachinePort(org.s8r.domain.machine.Machine machine) {
    if (machine == null) {
      return null;
    }
    // Create an adapter that converts from domain Machine to MachinePort
    return new DomainMachinePortAdapter(machine);
  }

  // This is an overloaded method to handle component Machine instances
  public static MachinePort createMachinePort(org.s8r.component.Machine machine) {
    if (machine == null) {
      return null;
    }
    // Create an adapter that converts from component Machine to MachinePort
    return new MachineToComponentPortAdapter(machine);
  }

  /**
   * Creates a MachinePort interface from a domain Machine implementation. This method exists to
   * provide a clean interface for using domain machines with ports.
   *
   * @param machine The domain machine to adapt
   * @return A MachinePort interface that delegates to the machine
   */
  public static MachinePort createMachinePortFromDomain(org.s8r.domain.machine.Machine machine) {
    if (machine == null) {
      return null;
    }
    // Create an adapter that converts from domain Machine to MachinePort
    return new MachineToDomainPortAdapter(machine);
  }

  /**
   * Creates a MachinePort interface from a component Machine implementation. This is a utility
   * method to support both domain and component machine types.
   *
   * @param machine The component machine to adapt
   * @return A MachinePort interface that delegates to the machine
   */
  public static MachinePort createMachinePortFromComponent(org.s8r.component.Machine machine) {
    if (machine == null) {
      return null;
    }
    // Create an adapter that converts from component Machine to MachinePort
    return new MachineToComponentPortAdapter(machine);
  }
}
