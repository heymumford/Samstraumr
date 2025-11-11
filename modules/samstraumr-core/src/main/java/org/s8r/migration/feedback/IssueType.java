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

package org.s8r.migration.feedback;

/** Enumeration of migration issue types. */
public enum IssueType {
  /** Issue with type mismatches between legacy and S8r components. */
  TYPE_MISMATCH("Type Mismatch", "Incompatible types between S8r and Samstraumr components"),

  /** Issue with missing properties in either legacy or S8r components. */
  MISSING_PROPERTY("Missing Property", "Property exists in one system but not the other"),

  /** Issue with invalid state transitions. */
  STATE_TRANSITION("State Transition", "Invalid or incompatible state transition paths"),

  /** Issue with reflection access during migration. */
  REFLECTION_ERROR("Reflection Error", "Error accessing component via reflection"),

  /** Issue with method signature mismatches. */
  METHOD_MAPPING("Method Mapping", "Method signature mismatch between legacy and S8r components"),

  /** Issue with structural differences between component hierarchies. */
  STRUCTURAL_DIFFERENCE("Structural Difference", "Component hierarchy or relationship differences"),

  /** Issue with configuration incompatibilities. */
  CONFIGURATION("Configuration", "Configuration or property format differences"),

  /** Issue with event handling and propagation. */
  EVENT_HANDLING("Event Handling", "Event routing or handling incompatibilities"),

  /** Issue with serialization or data conversion. */
  SERIALIZATION("Serialization", "Data serialization or format conversion issues"),

  /** Issue with security or access control. */
  SECURITY("Security", "Security model or access control differences"),

  /** Issue with lifecycle management. */
  LIFECYCLE("Lifecycle", "Component lifecycle differences or initialization issues"),

  /** Issue with dependency injection or component wiring. */
  DEPENDENCY("Dependency", "Component dependency or wiring issues"),

  /** Issue with error handling or exception mapping. */
  ERROR_HANDLING("Error Handling", "Exception or error handling differences"),

  /** Issue with resource management. */
  RESOURCE("Resource", "Resource allocation or management issues"),

  /** Issue with performance or scalability. */
  PERFORMANCE("Performance", "Performance or scalability concerns during migration"),

  /** Unknown issue type. */
  UNKNOWN("Unknown", "Unclassified migration issue");

  private final String displayName;
  private final String description;

  IssueType(String displayName, String description) {
    this.displayName = displayName;
    this.description = description;
  }

  /**
   * Gets the display name of this issue type.
   *
   * @return The display name
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Gets the description of this issue type.
   *
   * @return The description
   */
  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return displayName;
  }
}
