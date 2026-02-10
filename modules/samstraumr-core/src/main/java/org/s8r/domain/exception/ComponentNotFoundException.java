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
  public ComponentId getComponentId() {
    return componentId;
  }
}
