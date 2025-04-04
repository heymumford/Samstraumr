/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Domain exception for component not found in the S8r framework
 */

package org.samstraumr.domain.exception;

import org.samstraumr.domain.identity.ComponentId;

/** Exception thrown when a component cannot be found. */
public class ComponentNotFoundException extends ComponentException {
  private static final long serialVersionUID = 1L;
  private final ComponentId componentId;

  /** Creates a new ComponentNotFoundException with the specified component ID. */
  public ComponentNotFoundException(ComponentId componentId) {
    super(String.format("Component not found: %s", componentId));
    this.componentId = componentId;
  }

  /** Creates a new ComponentNotFoundException with a custom message. */
  public ComponentNotFoundException(String message, ComponentId componentId) {
    super(message);
    this.componentId = componentId;
  }

  /** Creates a new ComponentNotFoundException with a cause. */
  public ComponentNotFoundException(ComponentId componentId, Throwable cause) {
    super(String.format("Component not found: %s", componentId), cause);
    this.componentId = componentId;
  }

  /** Creates a new ComponentNotFoundException with a message and cause. */
  public ComponentNotFoundException(String message, ComponentId componentId, Throwable cause) {
    super(message, cause);
    this.componentId = componentId;
  }

  /** Gets the ID of the component that could not be found. */
  public ComponentId getComponentId() { return componentId; }
}
