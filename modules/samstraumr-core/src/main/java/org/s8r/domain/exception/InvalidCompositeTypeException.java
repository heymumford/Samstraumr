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

package org.s8r.domain.exception;

import org.s8r.domain.identity.ComponentId;

/**
 * Exception thrown when a component is expected to be a composite component but is not.
 *
 * <p>This exception is typically thrown when adding a non-composite component to a machine, which
 * requires all components to be composites.
 */
public class InvalidCompositeTypeException extends ComponentException {
  private static final long serialVersionUID = 1L;

  private final ComponentId machineId;
  private final String actualType;

  /**
   * Creates a new InvalidCompositeTypeException.
   *
   * @param machineId The ID of the machine that requires a composite component
   * @param actualType The actual type of the component
   */
  public InvalidCompositeTypeException(ComponentId machineId, String actualType) {
    super(
        String.format(
            "Invalid component type for machine %s: Expected CompositeComponent but got %s",
            machineId.getShortId(), actualType));
    this.machineId = machineId;
    this.actualType = actualType;
  }

  /**
   * Creates a new InvalidCompositeTypeException with a custom message.
   *
   * @param message The custom error message
   * @param machineId The ID of the machine that requires a composite component
   * @param actualType The actual type of the component
   */
  public InvalidCompositeTypeException(String message, ComponentId machineId, String actualType) {
    super(message);
    this.machineId = machineId;
    this.actualType = actualType;
  }

  /**
   * Creates a new InvalidCompositeTypeException with a cause.
   *
   * @param machineId The ID of the machine that requires a composite component
   * @param actualType The actual type of the component
   * @param cause The cause of the exception
   */
  public InvalidCompositeTypeException(ComponentId machineId, String actualType, Throwable cause) {
    super(
        String.format(
            "Invalid component type for machine %s: Expected CompositeComponent but got %s",
            machineId.getShortId(), actualType),
        cause);
    this.machineId = machineId;
    this.actualType = actualType;
  }

  /**
   * Gets the ID of the machine that requires a composite component.
   *
   * @return The machine's ID
   */
  public ComponentId getMachineId() {
    return machineId;
  }

  /**
   * Gets the actual type of the component.
   *
   * @return The component's actual type
   */
  public String getActualType() {
    return actualType;
  }
}
