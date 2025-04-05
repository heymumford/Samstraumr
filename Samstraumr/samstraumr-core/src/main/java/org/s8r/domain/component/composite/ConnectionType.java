/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Domain model for connection types in the S8r framework
 */

package org.s8r.domain.component.composite;

/**
 * Defines the types of connections that can exist between components.
 *
 * <p>This enum represents the different communication and relationship patterns that can exist
 * between components in a composite structure.
 */
public enum ConnectionType {
  /** Data flow connection where data moves from source to target. */
  DATA_FLOW("Directional flow of data between components"),

  /** Control connection where the source component controls the target. */
  CONTROL("Control relationship where source directs target"),

  /** Parent-child composition relationship. */
  COMPOSITION("Hierarchical parent-child relationship"),

  /** Event notification from source to target (observer pattern). */
  EVENT("Event notification from source to target"),

  /** Bidirectional data exchange between components. */
  BIDIRECTIONAL("Two-way communication between components"),

  /** Validation relationship where target validates source output. */
  VALIDATION("Validation of source by target"),

  /** Transformation relationship where target transforms source data. */
  TRANSFORMATION("Transformation of source data by target"),

  /** Monitoring relationship where target monitors source state. */
  MONITORING("Monitoring of source state by target"),

  /** Dependency relationship where source depends on target. */
  DEPENDENCY("Source component depends on target component");

  private final String description;

  /**
   * Constructs a ConnectionType with a description.
   *
   * @param description A description of the connection type
   */
  ConnectionType(String description) {
    this.description = description;
  }

  /**
   * Gets the description of this connection type.
   *
   * @return The description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Determines if this connection type is directional.
   *
   * @return true if the connection has a clear direction, false otherwise
   */
  public boolean isDirectional() {
    return this != BIDIRECTIONAL;
  }

  /**
   * Determines if this connection type represents a control relationship.
   *
   * @return true if this is a control relationship, false otherwise
   */
  public boolean isControlRelationship() {
    return this == CONTROL || this == COMPOSITION;
  }

  /**
   * Determines if this connection type involves data transfer.
   *
   * @return true if this connection involves data transfer, false otherwise
   */
  public boolean involvesDataTransfer() {
    return this == DATA_FLOW || this == BIDIRECTIONAL || this == TRANSFORMATION;
  }

  @Override
  public String toString() {
    return name() + " (" + description + ")";
  }
}
