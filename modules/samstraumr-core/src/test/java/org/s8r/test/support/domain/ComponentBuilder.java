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

package org.s8r.test.support.domain;

import java.util.UUID;
import org.s8r.domain.component.Component;
import org.s8r.domain.component.Environment;
import org.s8r.domain.component.Identity;
import org.s8r.domain.lifecycle.LifecycleState;

/**
 * Fluent builder for constructing Component instances in test scenarios.
 *
 * <p>Simplifies component creation by providing sensible defaults and chainable configuration
 * methods. Used across test scenarios to construct components with known state.
 *
 * <p>Example:
 *
 * <pre>
 * Component comp = ComponentBuilder.aComponent()
 *     .withIdentity("payment-processor")
 *     .inState(ACTIVE)
 *     .build();
 * </pre>
 */
public class ComponentBuilder {
  private String componentId;
  private String name;
  private Environment environment;
  private LifecycleState initialState;

  private ComponentBuilder() {
    // Use factory method
    this.componentId = UUID.randomUUID().toString();
    this.name = "test-component";
    this.initialState = LifecycleState.CONCEPTION;
  }

  /**
   * Creates new builder with default values.
   *
   * @return New builder instance
   */
  public static ComponentBuilder aComponent() {
    return new ComponentBuilder();
  }

  /**
   * Sets component identity.
   *
   * @param name Component name/identifier
   * @return This builder (for chaining)
   */
  public ComponentBuilder withIdentity(String name) {
    this.componentId = UUID.randomUUID().toString();
    this.name = name;
    return this;
  }

  /**
   * Sets component identity with specific ID.
   *
   * @param componentId Unique identifier
   * @param name Display name
   * @return This builder (for chaining)
   */
  public ComponentBuilder withIdentity(String componentId, String name) {
    this.componentId = componentId;
    this.name = name;
    return this;
  }

  /**
   * Sets environment (configuration context).
   *
   * @param environment Environment providing configuration/context
   * @return This builder (for chaining)
   */
  public ComponentBuilder withEnvironment(Environment environment) {
    this.environment = environment;
    return this;
  }

  /**
   * Sets initial lifecycle state.
   *
   * <p>Component will be created in this state.
   *
   * @param state Target lifecycle state
   * @return This builder (for chaining)
   */
  public ComponentBuilder inState(LifecycleState state) {
    this.initialState = state;
    return this;
  }

  /**
   * Builds component with current configuration.
   *
   * <p>Creates component with:
   * - Auto-generated or configured identity
   * - Configured environment (required)
   * - Initial lifecycle state (default: CONCEPTION)
   *
   * @return Constructed component
   * @throws IllegalStateException if environment not configured
   */
  public Component build() {
    if (environment == null) {
      throw new IllegalStateException(
          "Environment required. Use withEnvironment() to configure before build()");
    }

    Identity identity = Identity.create(componentId, name);
    Component component = Component.create(identity, environment);

    // Transition to initial state if not CONCEPTION
    if (initialState != LifecycleState.CONCEPTION) {
      // Use proper state machine - not all transitions may be valid
      // For test purposes, assume valid transition path exists
      component.transitionTo(initialState);
    }

    return component;
  }

  /**
   * Gets current component ID.
   *
   * @return Component ID
   */
  public String getComponentId() {
    return componentId;
  }

  /**
   * Gets current component name.
   *
   * @return Component name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets current environment.
   *
   * @return Environment or null if not set
   */
  public Environment getEnvironment() {
    return environment;
  }

  /**
   * Gets initial lifecycle state.
   *
   * @return Lifecycle state
   */
  public LifecycleState getInitialState() {
    return initialState;
  }
}
