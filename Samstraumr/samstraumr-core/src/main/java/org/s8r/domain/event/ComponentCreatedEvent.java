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

package org.s8r.domain.event;

import org.s8r.domain.identity.ComponentId;

/** Event raised when a new component is created. */
public class ComponentCreatedEvent extends ComponentEvent {
  private final String componentType;

  /** Creates a new component created event. */
  public ComponentCreatedEvent(ComponentId componentId, String componentType) {
    super(componentId);
    this.componentType = componentType;
  }

  /**
   * Gets the type of the component that was created.
   *
   * @return The component type
   */
  public String getComponentType() {
    return componentType;
  }
}
