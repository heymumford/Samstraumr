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

import java.lang.reflect.Method;

import org.s8r.application.port.LoggerPort;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.identity.LegacyComponentAdapterPort;
import org.s8r.domain.identity.LegacyEnvironmentConverter;
import org.s8r.domain.identity.LegacyIdentityConverter;

/**
 * Adapter that bridges between legacy component implementations and the new clean architecture.
 *
 * <p>This adapter uses reflection to interact with legacy components, completely removing direct
 * dependencies on legacy code. This follows Clean Architecture principles by properly isolating the
 * adapter implementation details. The adapter works with any legacy component type.
 *
 * <p>By returning the ComponentPort interface, this adapter decouples its clients from the concrete
 * implementation details of the wrapped components. This promotes the Dependency Inversion
 * Principle by making the clients depend on abstractions rather than concrete implementations,
 * ensuring a smooth migration path to Clean Architecture while preserving legacy functionality.
 */
public class LegacyComponentAdapter implements LegacyComponentAdapterPort {

  private final String componentClassName;
  private final LegacyIdentityConverter identityConverter;
  private final LegacyEnvironmentConverter environmentConverter;
  private final LoggerPort logger;

  // Cached reflection lookups
  private Class<?> componentClass;
  private Method getIdentityMethod;
  private Method getStateMethod;
  private Method setStateMethod;
  private Method initializeMethod;
  private Method getNameMethod;
  private Method setNameMethod;
  private Method getTypeMethod;
  private Method setTypeMethod;

  /**
   * Creates a new legacy component adapter for the specified component class.
   *
   * @param componentClassName The fully qualified class name of the legacy component
   * @param identityConverter The converter to use for legacy identities
   * @param environmentConverter The converter to use for legacy environments
   * @param logger The logger to use
   */
  public LegacyComponentAdapter(
      String componentClassName,
      LegacyIdentityConverter identityConverter,
      LegacyEnvironmentConverter environmentConverter,
      LoggerPort logger) {
    this.componentClassName = componentClassName;
    this.identityConverter = identityConverter;
    this.environmentConverter = environmentConverter;
    this.logger = logger;

    try {
      // Attempt to load the component class
      this.componentClass = Class.forName(componentClassName);

      // Find the necessary methods
      this.getIdentityMethod = findMethod("getIdentity");
      this.getStateMethod = findMethod("getState");
      this.setStateMethod = findMethod("setState", String.class);
      this.initializeMethod = findMethod("initialize");
      this.getNameMethod = findMethod("getName");
      this.setNameMethod = findMethod("setName", String.class);
      this.getTypeMethod = findMethod("getType");
      this.setTypeMethod = findMethod("setType", String.class);

      logger.debug("Successfully loaded legacy component class: {}", componentClassName);
    } catch (Exception e) {
      logger.error("Failed to initialize LegacyComponentAdapter: {}", e.getMessage());
      throw new IllegalArgumentException(
          "Could not load legacy component class: " + componentClassName, e);
    }
  }

  /**
   * Wraps a legacy component in a Clean Architecture Component.
   *
   * @param legacyComponent The legacy component to wrap
   * @return A clean architecture component that delegates to the legacy component
   */
  public ComponentPort wrapLegacyComponent(Object legacyComponent) {
    if (legacyComponent == null) {
      throw new IllegalArgumentException("Legacy component cannot be null");
    }

    if (!componentClass.isInstance(legacyComponent)) {
      throw new IllegalArgumentException(
          "Expected "
              + componentClassName
              + " object, got: "
              + legacyComponent.getClass().getName());
    }

    try {
      // Extract identity
      Object legacyIdentity = getIdentityMethod.invoke(legacyComponent);
      ComponentId componentId = createComponentIdFromLegacyIdentity(legacyIdentity);

      // Extract other attributes
      String state = (String) getStateMethod.invoke(legacyComponent);
      String name = (String) getNameMethod.invoke(legacyComponent);
      String type = (String) getTypeMethod.invoke(legacyComponent);

      // Create a wrapper that delegates to the legacy component
      return new LegacyComponentWrapper(componentId, name, type, state, legacyComponent, this);
    } catch (Exception e) {
      logger.error("Failed to wrap legacy component: {}", e.getMessage());
      throw new IllegalStateException("Error wrapping legacy component", e);
    }
  }

  /**
   * Creates a legacy component with the specified parameters.
   *
   * @param name The component name
   * @param type The component type
   * @param reason The reason for creation
   * @return A wrapped legacy component
   */
  public ComponentPort createLegacyComponent(String name, String type, String reason) {
    try {
      // Create an environment for the component
      Object legacyEnvironment = environmentConverter.createLegacyEnvironment(null);

      // Create an identity for the component
      Object legacyIdentity = identityConverter.createLegacyAdamIdentity(reason, legacyEnvironment);

      // Create component instance using reflection
      Object legacyComponent = componentClass.getDeclaredConstructor().newInstance();

      // Set component properties
      setNameMethod.invoke(legacyComponent, name);
      setTypeMethod.invoke(legacyComponent, type);
      setStateMethod.invoke(legacyComponent, "CONCEPTION");

      // Set the identity using reflection
      Method setIdentityMethod = componentClass.getMethod("setIdentity", legacyIdentity.getClass());
      setIdentityMethod.invoke(legacyComponent, legacyIdentity);

      // Initialize the component
      initializeMethod.invoke(legacyComponent);

      // Wrap and return the component
      return wrapLegacyComponent(legacyComponent);
    } catch (Exception e) {
      logger.error("Failed to create legacy component: {}", e.getMessage());
      throw new IllegalStateException("Error creating legacy component", e);
    }
  }

  /**
   * Sets the state of a legacy component.
   *
   * @param legacyComponent The legacy component
   * @param state The new state value
   */
  @Override
  public void setLegacyComponentState(Object legacyComponent, String state) {
    try {
      setStateMethod.invoke(legacyComponent, state);
      logger.debug("Set legacy component state to {}", state);
    } catch (Exception e) {
      logger.error("Failed to set legacy component state: {}", e.getMessage());
      throw new IllegalStateException("Error setting legacy component state", e);
    }
  }

  /**
   * Gets the state of a legacy component.
   *
   * @param legacyComponent The legacy component
   * @return The component state
   */
  @Override
  public String getLegacyComponentState(Object legacyComponent) {
    try {
      return (String) getStateMethod.invoke(legacyComponent);
    } catch (Exception e) {
      logger.error("Failed to get legacy component state: {}", e.getMessage());
      throw new IllegalStateException("Error getting legacy component state", e);
    }
  }

  /**
   * Initializes a legacy component.
   *
   * @param legacyComponent The legacy component to initialize
   */
  @Override
  public void initializeLegacyComponent(Object legacyComponent) {
    try {
      initializeMethod.invoke(legacyComponent);
      logger.debug("Initialized legacy component");
    } catch (Exception e) {
      logger.error("Failed to initialize legacy component: {}", e.getMessage());
      throw new IllegalStateException("Error initializing legacy component", e);
    }
  }

  /**
   * Creates a ComponentId from a legacy identity object.
   *
   * @param legacyIdentity The legacy identity object
   * @return A new ComponentId
   */
  private ComponentId createComponentIdFromLegacyIdentity(Object legacyIdentity) {
    String id = identityConverter.getLegacyIdentityId(legacyIdentity);
    String reason = identityConverter.getLegacyIdentityReason(legacyIdentity);
    var lineage = identityConverter.getLegacyIdentityLineage(legacyIdentity);

    return identityConverter.toComponentId(id, reason, lineage);
  }

  /**
   * Helper method to find a method in the component class.
   *
   * @param methodName The name of the method to find
   * @param parameterTypes The parameter types of the method
   * @return The Method object
   */
  private Method findMethod(String methodName, Class<?>... parameterTypes) {
    try {
      return componentClass.getMethod(methodName, parameterTypes);
    } catch (NoSuchMethodException e) {
      logger.error(
          "Method {} not found in legacy component class {}", methodName, componentClassName);
      throw new IllegalArgumentException("Method not found: " + methodName, e);
    }
  }
}
