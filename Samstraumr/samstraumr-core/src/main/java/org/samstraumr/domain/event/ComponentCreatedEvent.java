/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Component created event for the S8r framework
 */

package org.samstraumr.domain.event;

import org.samstraumr.domain.identity.ComponentId;

/** Event raised when a new component is created. */
public class ComponentCreatedEvent extends DomainEvent {
  private final ComponentId componentId;
  private final String componentType;

  /** Creates a new component created event. */
  public ComponentCreatedEvent(ComponentId componentId, String componentType) {
    this.componentId = componentId;
    this.componentType = componentType;
  }

  // Getters
  public ComponentId getComponentId() { return componentId; }
  public String getComponentType() { return componentType; }
}
