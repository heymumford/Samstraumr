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

import org.s8r.application.port.LegacyAdapterResolver;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.identity.LegacyEnvironmentConverter;
import org.s8r.domain.identity.LegacyIdentityConverter;

/**
 * Factory for creating reflection-based adapters that work with legacy code without direct
 * dependencies.
 *
 * <p>This factory creates adapter implementations that use reflection to interact with legacy
 * classes, removing direct dependencies on those classes. This follows Clean Architecture
 * principles by isolating implementation details from the domain layer.
 *
 * <p>Available adapters include:
 *
 * <ul>
 *   <li>Environment converters - For working with legacy environment objects
 *   <li>Identity converters - For working with legacy identity objects
 *   <li>Component adapters - For wrapping legacy components in the new Component interface
 * </ul>
 */
public class ReflectiveAdapterFactory implements LegacyAdapterResolver {

  // Constants for legacy class names
  private static final String TUBE_ENVIRONMENT_CLASS = "org.s8r.tube.Environment";
  private static final String TUBE_IDENTITY_CLASS = "org.s8r.tube.TubeIdentity";
  private static final String CORE_ENVIRONMENT_CLASS = "org.s8r.core.tube.Environment";
  private static final String CORE_IDENTITY_CLASS = "org.s8r.core.tube.identity.Identity";
  private static final String TUBE_COMPONENT_CLASS = "org.s8r.tube.Tube";
  private static final String CORE_COMPONENT_CLASS = "org.s8r.core.tube.Tube";

  private final LoggerPort logger;

  // Lazily initialized adapters
  private ReflectiveEnvironmentConverter tubeEnvironmentConverter;
  private ReflectiveEnvironmentConverter coreEnvironmentConverter;
  private ReflectiveIdentityConverter tubeIdentityConverter;
  private ReflectiveIdentityConverter coreIdentityConverter;
  private LegacyComponentAdapter tubeComponentAdapter;
  private LegacyComponentAdapter coreComponentAdapter;

  /**
   * Creates a new reflective adapter factory.
   *
   * @param logger The logger to use
   */
  public ReflectiveAdapterFactory(LoggerPort logger) {
    this.logger = logger;
  }

  @Override
  public LegacyEnvironmentConverter getTubeEnvironmentConverter() {
    if (tubeEnvironmentConverter == null) {
      try {
        tubeEnvironmentConverter =
            new ReflectiveEnvironmentConverter(TUBE_ENVIRONMENT_CLASS, logger);
        logger.debug("Created reflective tube environment converter");
      } catch (Exception e) {
        logger.error("Failed to create tube environment converter: {}", e.getMessage());
        throw new IllegalStateException("Could not create tube environment converter", e);
      }
    }
    return tubeEnvironmentConverter;
  }

  @Override
  public LegacyEnvironmentConverter getCoreEnvironmentConverter() {
    if (coreEnvironmentConverter == null) {
      try {
        coreEnvironmentConverter =
            new ReflectiveEnvironmentConverter(CORE_ENVIRONMENT_CLASS, logger);
        logger.debug("Created reflective core environment converter");
      } catch (Exception e) {
        logger.error("Failed to create core environment converter: {}", e.getMessage());
        throw new IllegalStateException("Could not create core environment converter", e);
      }
    }
    return coreEnvironmentConverter;
  }

  @Override
  public LegacyIdentityConverter getTubeIdentityConverter() {
    if (tubeIdentityConverter == null) {
      try {
        // First ensure environment converter is created
        ReflectiveEnvironmentConverter envConverter =
            (ReflectiveEnvironmentConverter) getTubeEnvironmentConverter();

        tubeIdentityConverter =
            new ReflectiveIdentityConverter(TUBE_IDENTITY_CLASS, envConverter, logger);
        logger.debug("Created reflective tube identity converter");
      } catch (Exception e) {
        logger.error("Failed to create tube identity converter: {}", e.getMessage());
        throw new IllegalStateException("Could not create tube identity converter", e);
      }
    }
    return tubeIdentityConverter;
  }

  @Override
  public LegacyIdentityConverter getCoreIdentityConverter() {
    if (coreIdentityConverter == null) {
      try {
        // First ensure environment converter is created
        ReflectiveEnvironmentConverter envConverter =
            (ReflectiveEnvironmentConverter) getCoreEnvironmentConverter();

        coreIdentityConverter =
            new ReflectiveIdentityConverter(CORE_IDENTITY_CLASS, envConverter, logger);
        logger.debug("Created reflective core identity converter");
      } catch (Exception e) {
        logger.error("Failed to create core identity converter: {}", e.getMessage());
        throw new IllegalStateException("Could not create core identity converter", e);
      }
    }
    return coreIdentityConverter;
  }

  /**
   * Gets an adapter for Tube components.
   *
   * @return A component adapter for working with Tube instances
   */
  public LegacyComponentAdapter getTubeComponentAdapter() {
    if (tubeComponentAdapter == null) {
      try {
        // First ensure the required converters are created
        LegacyIdentityConverter identityConverter = getTubeIdentityConverter();
        LegacyEnvironmentConverter environmentConverter = getTubeEnvironmentConverter();

        tubeComponentAdapter =
            new LegacyComponentAdapter(
                TUBE_COMPONENT_CLASS, identityConverter, environmentConverter, logger);
        logger.debug("Created tube component adapter");
      } catch (Exception e) {
        logger.error("Failed to create tube component adapter: {}", e.getMessage());
        throw new IllegalStateException("Could not create tube component adapter", e);
      }
    }
    return tubeComponentAdapter;
  }

  /**
   * Gets an adapter for Core Tube components.
   *
   * @return A component adapter for working with Core Tube instances
   */
  public LegacyComponentAdapter getCoreComponentAdapter() {
    if (coreComponentAdapter == null) {
      try {
        // First ensure the required converters are created
        LegacyIdentityConverter identityConverter = getCoreIdentityConverter();
        LegacyEnvironmentConverter environmentConverter = getCoreEnvironmentConverter();

        coreComponentAdapter =
            new LegacyComponentAdapter(
                CORE_COMPONENT_CLASS, identityConverter, environmentConverter, logger);
        logger.debug("Created core component adapter");
      } catch (Exception e) {
        logger.error("Failed to create core component adapter: {}", e.getMessage());
        throw new IllegalStateException("Could not create core component adapter", e);
      }
    }
    return coreComponentAdapter;
  }
}
