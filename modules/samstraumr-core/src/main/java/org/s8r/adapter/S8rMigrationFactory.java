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

package org.s8r.adapter;

import org.s8r.application.port.LoggerPort;
import org.s8r.component.Composite;
import org.s8r.component.Machine;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.component.port.MachinePort;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.identity.LegacyComponentAdapterPort;
import org.s8r.domain.identity.LegacyEnvironmentConverter;
import org.s8r.domain.identity.LegacyIdentityConverter;
import org.s8r.infrastructure.logging.ConsoleLogger;
import org.s8r.tube.Environment;
import org.s8r.tube.Tube;
import org.s8r.tube.TubeIdentity;

/**
 * Factory for creating migration utilities to assist with migrating from Samstraumr to S8r.
 *
 * <p>This factory provides easy access to the conversion utilities needed for a smooth migration
 * path from legacy Tube-based code to new Component-based code. It supports the Strangler Fig
 * pattern by allowing gradual replacement of legacy code.
 */
public class S8rMigrationFactory {

  private final LoggerPort logger;
  private final TubeLegacyIdentityConverter identityConverter;
  private final TubeLegacyEnvironmentConverter environmentConverter;
  private final TubeComponentAdapter componentAdapter;
  private final CompositeAdapter compositeAdapter;
  private final MachineAdapter machineAdapter;

  /**
   * Creates a new S8rMigrationFactory with the specified logger.
   *
   * @param logger The logger to use for migration operations
   */
  public S8rMigrationFactory(LoggerPort logger) {
    this.logger = logger;
    this.identityConverter = new TubeLegacyIdentityConverter(logger);
    this.environmentConverter = new TubeLegacyEnvironmentConverter(logger);
    this.componentAdapter =
        new TubeComponentAdapter(logger, identityConverter, environmentConverter);
    this.compositeAdapter = new CompositeAdapter(logger, componentAdapter, environmentConverter);
    this.machineAdapter = new MachineAdapter(logger, compositeAdapter, environmentConverter);

    logger.debug("S8rMigrationFactory initialized");
  }

  /** Creates a new S8rMigrationFactory with a default console logger. */
  public S8rMigrationFactory() {
    this(new ConsoleLogger("S8rMigration"));
  }

  /**
   * Gets the identity converter for Tube identities.
   *
   * @return The identity converter
   */
  public LegacyIdentityConverter getIdentityConverter() {
    return identityConverter;
  }

  /**
   * Gets the environment converter for Tube environments.
   *
   * @return The environment converter
   */
  public LegacyEnvironmentConverter getEnvironmentConverter() {
    return environmentConverter;
  }

  /**
   * Gets the component adapter for Tube components.
   *
   * @return The component adapter
   */
  public LegacyComponentAdapterPort getComponentAdapter() {
    return componentAdapter;
  }

  /**
   * Gets the composite adapter for Tube composites.
   *
   * @return The composite adapter
   */
  public CompositeAdapter getCompositeAdapter() {
    return compositeAdapter;
  }

  /**
   * Gets the machine adapter for Tube machines.
   *
   * @return The machine adapter
   */
  public MachineAdapter getMachineAdapter() {
    return machineAdapter;
  }

  // =================== Identity Conversion ===================

  /**
   * Converts a TubeIdentity to a ComponentId.
   *
   * @param tubeIdentity The TubeIdentity to convert
   * @return A new ComponentId
   */
  public ComponentId tubeIdentityToComponentId(TubeIdentity tubeIdentity) {
    logger.debug("Converting TubeIdentity to ComponentId: {}", tubeIdentity.getUniqueId());
    return identityConverter.toComponentId(
        tubeIdentity.getUniqueId(), tubeIdentity.getReason(), tubeIdentity.getLineage());
  }

  // =================== Environment Conversion ===================

  /**
   * Converts a Tube's Environment to an S8r component Environment.
   *
   * @param tubeEnvironment The Tube Environment to convert
   * @return A new S8r component Environment
   */
  public org.s8r.component.Environment tubeEnvironmentToS8rEnvironment(
      Environment tubeEnvironment) {
    logger.debug("Converting Tube Environment to S8r Environment");
    return environmentConverter.fromLegacyEnvironment(tubeEnvironment);
  }

  /**
   * Converts a Tube's Environment to an S8r core Environment.
   *
   * @param tubeEnvironment The Tube Environment to convert
   * @return A new S8r core Environment
   */
  public org.s8r.component.core.Environment tubeEnvironmentToS8rCoreEnvironment(
      Environment tubeEnvironment) {
    logger.debug("Converting Tube Environment to S8r Core Environment");
    return environmentConverter.fromLegacyEnvironmentToCore(tubeEnvironment);
  }

  /**
   * Converts an S8r component Environment to a Tube Environment.
   *
   * @param s8rEnvironment The S8r component Environment to convert
   * @return A new Tube Environment
   */
  public Environment s8rEnvironmentToTubeEnvironment(org.s8r.component.Environment s8rEnvironment) {
    logger.debug("Converting S8r Environment to Tube Environment");
    return environmentConverter.toDomainEnvironment(s8rEnvironment);
  }

  /**
   * Converts an S8r core Environment to a Tube Environment.
   *
   * @param s8rCoreEnvironment The S8r core Environment to convert
   * @return A new Tube Environment
   */
  public Environment s8rCoreEnvironmentToTubeEnvironment(
      org.s8r.component.core.Environment s8rCoreEnvironment) {
    logger.debug("Converting S8r Core Environment to Tube Environment");
    return environmentConverter.toDomainEnvironment(s8rCoreEnvironment);
  }

  // =================== Component Conversion ===================

  /**
   * Wraps a Tube as a ComponentPort, allowing it to be used with new Component-based code.
   *
   * @param tube The Tube to wrap
   * @return A ComponentPort that delegates to the Tube
   */
  public ComponentPort tubeToComponent(Tube tube) {
    logger.debug("Converting Tube to ComponentPort: {}", tube.getUniqueId());
    return componentAdapter.wrapLegacyComponent(tube);
  }

  /**
   * Creates a new Tube and wraps it as a ComponentPort.
   *
   * @param reason The reason for creating the tube
   * @param environment The environment for the tube
   * @return A ComponentPort that delegates to a new Tube
   */
  public ComponentPort createTubeComponent(String reason, Environment environment) {
    logger.debug("Creating new Tube ComponentPort with reason: {}", reason);
    Tube tube = Tube.create(reason, environment);
    return tubeToComponent(tube);
  }

  /**
   * Creates a child Tube from a parent Tube and wraps it as a ComponentPort.
   *
   * @param reason The reason for creating the child
   * @param parentTube The parent tube
   * @return A ComponentPort that delegates to the child Tube
   */
  public ComponentPort createChildTubeComponent(String reason, Tube parentTube) {
    logger.debug("Creating child Tube ComponentPort with reason: {}", reason);
    return ((TubeComponentAdapter) componentAdapter).createChildComponent(reason, parentTube);
  }

  /**
   * Extracts the wrapped Tube from a TubeComponentWrapper.
   *
   * @param componentPort The component port to extract from
   * @return The wrapped Tube, or null if not a TubeComponentWrapper
   */
  public Tube extractTube(ComponentPort componentPort) {
    if (componentPort instanceof TubeComponentWrapper) {
      logger.debug("Extracting Tube from TubeComponentWrapper");
      return ((TubeComponentWrapper) componentPort).getTube();
    }

    logger.warn("ComponentPort is not a TubeComponentWrapper, cannot extract Tube");
    return null;
  }

  // =================== Composite Conversion ===================

  /**
   * Converts a Tube composite to a Component composite.
   *
   * @param tubeComposite The Tube composite to convert
   * @return A new Component composite with the same structure
   */
  public Composite tubeCompositeToComponentComposite(
      org.s8r.tube.composite.Composite tubeComposite) {
    logger.debug(
        "Converting Tube composite to Component composite: {}", tubeComposite.getCompositeId());
    return compositeAdapter.tubeCompositeToComponentComposite(tubeComposite);
  }

  /**
   * Creates a wrapper around a Tube composite, allowing it to be used with Component APIs.
   *
   * @param tubeComposite The Tube composite to wrap
   * @return A Component composite that delegates to the Tube composite
   */
  public Composite wrapTubeComposite(org.s8r.tube.composite.Composite tubeComposite) {
    logger.debug("Creating wrapper for Tube composite: {}", tubeComposite.getCompositeId());
    return compositeAdapter.wrapTubeComposite(tubeComposite);
  }

  /**
   * Creates a hybrid composite that can contain both Tubes and Components.
   *
   * @param compositeId The ID for the new composite
   * @param tubeEnvironment The environment for the composite
   * @return A new Component composite
   */
  public Composite createHybridComposite(String compositeId, Environment tubeEnvironment) {
    logger.debug("Creating hybrid composite: {}", compositeId);
    return compositeAdapter.createHybridComposite(compositeId, tubeEnvironment);
  }

  /**
   * Adds a Tube from a legacy composite to a new Component composite.
   *
   * @param tubeComposite The source Tube composite
   * @param tubeName The name of the Tube to add
   * @param componentComposite The target Component composite
   * @param componentName The name to use for the Component in the new composite
   */
  public void addTubeToComponentComposite(
      org.s8r.tube.composite.Composite tubeComposite,
      String tubeName,
      Composite componentComposite,
      String componentName) {
    logger.debug(
        "Adding Tube {} from composite to Component composite as {}", tubeName, componentName);
    compositeAdapter.addTubeToComponentComposite(
        tubeComposite, tubeName, componentComposite, componentName);
  }

  // =================== Machine Conversion ===================

  /**
   * Converts a Tube machine to a Component machine.
   *
   * @param tubeMachine The Tube machine to convert
   * @return A new Component machine with the same structure
   */
  public Machine tubeMachineToComponentMachine(org.s8r.tube.machine.Machine tubeMachine) {
    logger.debug("Converting Tube machine to Component machine: {}", tubeMachine.getMachineId());
    return machineAdapter.tubeMachineToComponentMachine(tubeMachine);
  }

  /**
   * Converts a Tube machine to a MachinePort interface. This follows the Clean Architecture pattern
   * by returning the port interface rather than the concrete implementation.
   *
   * @param tubeMachine The Tube machine to convert
   * @return A MachinePort interface that mirrors the legacy machine
   */
  public MachinePort tubeMachineToMachinePort(org.s8r.tube.machine.Machine tubeMachine) {
    logger.debug("Converting Tube machine to MachinePort: {}", tubeMachine.getMachineId());
    return machineAdapter.tubeMachineToMachinePort(tubeMachine);
  }

  /**
   * Creates a wrapper around a Tube machine, allowing it to be used with Component machine APIs.
   *
   * @param tubeMachine The Tube machine to wrap
   * @return A Component machine that delegates to the Tube machine
   */
  public Machine wrapTubeMachine(org.s8r.tube.machine.Machine tubeMachine) {
    logger.debug("Creating wrapper for Tube machine: {}", tubeMachine.getMachineId());
    return machineAdapter.wrapTubeMachine(tubeMachine);
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
    logger.debug("Creating MachinePort wrapper for Tube machine: {}", tubeMachine.getMachineId());
    return machineAdapter.wrapTubeMachineAsPort(tubeMachine);
  }
}
