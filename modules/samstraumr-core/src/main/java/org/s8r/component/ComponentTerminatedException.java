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
 * Licensed under the Mozilla Public License 2.0
 */
package org.s8r.component;

/**
 * Exception thrown when attempting to perform operations on a terminated component.
 *
 * <p>This exception includes detailed information about the terminated component such as the
 * component ID, the reason for termination, and the timestamp when termination occurred.
 */
public class ComponentTerminatedException extends RuntimeException {
  private final String componentId;
  private final String terminationReason;
  private final String terminationTimestamp;

  /**
   * Constructs a new ComponentTerminatedException with the specified details.
   *
   * @param message The detailed error message
   * @param componentId The ID of the terminated component
   * @param terminationReason The reason the component was terminated
   * @param terminationTimestamp The timestamp when termination occurred
   */
  public ComponentTerminatedException(
      String message, String componentId, String terminationReason, String terminationTimestamp) {
    super(message);
    this.componentId = componentId;
    this.terminationReason = terminationReason;
    this.terminationTimestamp = terminationTimestamp;
  }

  /**
   * Constructs a new ComponentTerminatedException with a default message.
   *
   * @param componentId The ID of the terminated component
   * @param terminationReason The reason the component was terminated
   * @param terminationTimestamp The timestamp when termination occurred
   */
  public ComponentTerminatedException(
      String componentId, String terminationReason, String terminationTimestamp) {
    this(
        "Operation failed: Component is terminated",
        componentId,
        terminationReason,
        terminationTimestamp);
  }

  /**
   * Gets the ID of the terminated component.
   *
   * @return The component ID
   */
  public String getComponentId() {
    return componentId;
  }

  /**
   * Gets the reason the component was terminated.
   *
   * @return The termination reason
   */
  public String getTerminationReason() {
    return terminationReason;
  }

  /**
   * Gets the timestamp when termination occurred.
   *
   * @return The termination timestamp
   */
  public String getTerminationTimestamp() {
    return terminationTimestamp;
  }
}
