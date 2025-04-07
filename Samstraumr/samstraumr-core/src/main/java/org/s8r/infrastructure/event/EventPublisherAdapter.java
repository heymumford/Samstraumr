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

package org.s8r.infrastructure.event;

import java.util.List;
import org.s8r.application.port.ComponentRepository;
import org.s8r.application.port.EventDispatcher;
import org.s8r.application.port.EventPublisherPort;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.event.DomainEvent;
import org.s8r.domain.identity.ComponentId;

/**
 * Implementation of the EventPublisherPort that adapts to the EventDispatcher.
 *
 * <p>This adapter implements the output port defined in the application layer
 * and delegates to the EventDispatcher infrastructure component. This follows
 * the Clean Architecture pattern by keeping the application layer dependent only
 * on the port interfaces, not on the implementation details.
 */
public class EventPublisherAdapter implements EventPublisherPort {

  private final EventDispatcher eventDispatcher;
  private final ComponentRepository componentRepository;
  private final LoggerPort logger;

  /**
   * Creates a new EventPublisherAdapter with the specified dependencies.
   *
   * @param eventDispatcher The event dispatcher to use for publishing events
   * @param componentRepository The component repository for finding components
   * @param logger The logger to use for logging
   */
  public EventPublisherAdapter(
      EventDispatcher eventDispatcher,
      ComponentRepository componentRepository,
      LoggerPort logger) {
    this.eventDispatcher = eventDispatcher;
    this.componentRepository = componentRepository;
    this.logger = logger;
  }

  @Override
  public void publishEvent(DomainEvent event) {
    if (event == null) {
      logger.warn("Attempted to publish null event");
      return;
    }
    
    eventDispatcher.dispatch(event);
  }

  @Override
  public void publishEvents(List<DomainEvent> events) {
    if (events == null || events.isEmpty()) {
      return;
    }
    
    logger.debug("Publishing {} events", events.size());
    for (DomainEvent event : events) {
      publishEvent(event);
    }
  }

  @Override
  public int publishPendingEvents(ComponentId componentId) {
    if (componentId == null) {
      logger.warn("Attempted to publish events for null component ID");
      return 0;
    }
    
    // Find the component in the repository
    return componentRepository.findById(componentId)
        .map(this::publishAndClearComponentEvents)
        .orElseGet(() -> {
          logger.warn("Component not found for ID: {}", componentId.getIdString());
          return 0;
        });
  }
  
  /**
   * Publishes all events from a component and clears them.
   *
   * @param component The component whose events should be published
   * @return The number of events published
   */
  private int publishAndClearComponentEvents(ComponentPort component) {
    // Get the component's domain events
    List<DomainEvent> events = component.getDomainEvents();
    int eventCount = events.size();
    
    if (eventCount > 0) {
      // Publish all events
      publishEvents(events);
      
      // Clear the events from the component
      component.clearEvents();
      
      logger.debug("Published and cleared {} events from component {}", 
          eventCount, component.getId().getIdString());
    }
    
    return eventCount;
  }
}