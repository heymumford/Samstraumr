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
 * Exception thrown when an attempt is made to create a component with an ID that already exists.
 *
 * <p>This exception is typically thrown by repositories when a component with the same ID already
 * exists in the persistence store.
 */
public class DuplicateComponentException extends ComponentException {
  private static final long serialVersionUID = 1L;

  private final ComponentId componentId;

  /**
   * Creates a new DuplicateComponentException with the specified component ID.
   *
   * @param componentId The ID that caused the duplication error
   */
  public DuplicateComponentException(ComponentId componentId) {
    super(String.format("Component with ID %s already exists", componentId));
    this.componentId = componentId;
  }

  /**
   * Creates a new DuplicateComponentException with a custom message and the specified component ID.
   *
   * @param message The error message
   * @param componentId The ID that caused the duplication error
   */
  public DuplicateComponentException(String message, ComponentId componentId) {
    super(message);
    this.componentId = componentId;
  }

  /**
   * Creates a new DuplicateComponentException with the specified component ID and a cause.
   *
   * @param componentId The ID that caused the duplication error
   * @param cause The cause of the exception
   */
  public DuplicateComponentException(ComponentId componentId, Throwable cause) {
    super(String.format("Component with ID %s already exists", componentId), cause);
    this.componentId = componentId;
  }

  /**
   * Gets the component ID that caused the duplication error.
   *
   * @return The component ID
   */
  public ComponentId getComponentId() {
    return componentId;
  }
}
