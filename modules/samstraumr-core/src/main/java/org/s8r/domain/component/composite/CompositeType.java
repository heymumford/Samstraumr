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

package org.s8r.domain.component.composite;

/**
 * Defines the types of composite components.
 *
 * <p>This enum represents the different patterns that composite components can follow,
 * corresponding to common software design patterns.
 */
public enum CompositeType {
  /** Standard composite with hierarchical structure but no specific pattern. */
  STANDARD("Standard hierarchical composite"),

  /** Pipeline pattern where components process data in sequence. */
  PIPELINE("Sequential processing pipeline"),

  /** Observer pattern where components observe and react to changes. */
  OBSERVER("Observes and reacts to changes"),

  /** Transformer pattern for data transformation. */
  TRANSFORMER("Transforms input data"),

  /** Validator pattern for data validation. */
  VALIDATOR("Validates input data"),

  /** Chain of responsibility pattern for request processing. */
  CHAIN("Chain of responsibility"),

  /** Circuit breaker pattern for fault tolerance. */
  CIRCUIT_BREAKER("Prevents cascade failures"),

  /** Adapter pattern for interface adaptation. */
  ADAPTER("Adapts interfaces"),

  /** Façade pattern that simplifies complex subsystems. */
  FACADE("Simplifies complex subsystems"),

  /** Mediator pattern for coordinating component interactions. */
  MEDIATOR("Coordinates component interactions");

  private final String description;

  /**
   * Constructs a CompositeType with a description.
   *
   * @param description A description of the composite type
   */
  CompositeType(String description) {
    this.description = description;
  }

  /**
   * Gets the description of this composite type.
   *
   * @return The description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Determines if this composite type processes data.
   *
   * @return true if this type processes data, false otherwise
   */
  public boolean processesData() {
    return this == PIPELINE || this == TRANSFORMER || this == VALIDATOR;
  }

  /**
   * Determines if this composite type coordinates other components.
   *
   * @return true if this type coordinates components, false otherwise
   */
  public boolean coordinatesComponents() {
    return this == MEDIATOR || this == FACADE;
  }

  /**
   * Determines if this composite type enhances resilience.
   *
   * @return true if this type enhances resilience, false otherwise
   */
  public boolean enhancesResilience() {
    return this == CIRCUIT_BREAKER;
  }

  @Override
  public String toString() {
    return name() + " (" + description + ")";
  }
}
