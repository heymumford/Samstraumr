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
import org.s8r.domain.lifecycle.LifecycleState;

/**
 * Domain event representing the creation of a component.
 * 
 * <p>This event follows the standardized naming convention without the "Event" suffix,
 * which is recommended for domain events in Clean Architecture. It uses the Component ID 
 * to uniquely identify the component that was created.
 * 
 * <p>This implementation is designed to work seamlessly with both direct domain entities
 * and port interfaces through the adapter layer, supporting the Dependency Inversion Principle
 * of Clean Architecture.
 */
public class ComponentCreated extends DomainEvent {
  private final ComponentId componentId;
  private final String componentType;
  private LifecycleState initialState;

  /**
   * Creates a new component created event.
   *
   * @param componentId The ID of the created component
   * @param componentType The type of the component (e.g., "Component", "CompositeComponent", "Machine")
   */
  public ComponentCreated(ComponentId componentId, String componentType) {
    super(componentId.getIdString());
    this.componentId = componentId;
    this.componentType = componentType;
    this.initialState = LifecycleState.INITIALIZED;
  }
  
  /**
   * Creates a new ComponentCreated event.
   *
   * @param componentId The ID of the created component
   * @param initialState The initial lifecycle state of the component
   * @param componentType The type of the component (e.g., "Component", "CompositeComponent", "Machine")
   */
  public ComponentCreated(ComponentId componentId, LifecycleState initialState, String componentType) {
    super(componentId.getIdString());
    this.componentId = componentId;
    this.componentType = componentType;
    this.initialState = initialState;
  }

  /**
   * Gets the ID of the created component.
   *
   * @return The component ID
   */
  public ComponentId getComponentId() {
    return componentId;
  }

  /**
   * Gets the type of the component.
   *
   * @return The component type
   */
  public String getComponentType() {
    return componentType;
  }
  
  /**
   * Gets the initial lifecycle state of the component.
   *
   * @return The initial lifecycle state
   */
  public LifecycleState getInitialState() {
    return initialState;
  }
  
  /**
   * Sets the initial lifecycle state of the component.
   *
   * @param initialState The initial lifecycle state
   */
  public void setInitialState(LifecycleState initialState) {
    this.initialState = initialState;
  }
  
  @Override
  public String toString() {
    return String.format("ComponentCreated[componentId=%s, initialState=%s, componentType=%s]",
            componentId.getIdString(), initialState, componentType);
  }
}