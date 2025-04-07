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

import java.time.Instant;
import java.util.UUID;

/**
 * Base class for all domain events, following DDD and Clean Architecture principles by keeping
 * events as part of the domain model.
 * 
 * <p>This class provides the core properties that all domain events must have, including a unique
 * identifier, a timestamp, a type, and a source component identifier. It represents a fact that
 * something of importance has happened in the domain.
 * 
 * <p>Domain events are immutable value objects that represent something that has already happened.
 * They can be used to trigger side effects, implement the Observer pattern, or facilitate 
 * eventual consistency across bounded contexts.
 */
public abstract class DomainEvent {
  private final String eventId = UUID.randomUUID().toString();
  private final Instant occurredOn = Instant.now();
  private final String eventType = this.getClass().getSimpleName().replaceAll("Event$", "");
  private String sourceComponentId;
  private boolean isFromPort = false;
  
  /**
   * Creates a domain event with no source component ID.
   */
  protected DomainEvent() {
    this.sourceComponentId = null;
  }
  
  /**
   * Creates a domain event with the specified source component ID.
   *
   * @param sourceComponentId The ID of the component that generated this event
   */
  protected DomainEvent(String sourceComponentId) {
    this.sourceComponentId = sourceComponentId;
  }
  
  /**
   * Sets whether this event originated from a port interface.
   * This helps adapters track events that came from port interfaces
   * vs. concrete implementations, facilitating Clean Architecture.
   *
   * @param isFromPort True if the event originated from a port interface
   */
  public void setFromPort(boolean isFromPort) {
    this.isFromPort = isFromPort;
  }
  
  /**
   * Checks if this event originated from a port interface.
   *
   * @return True if the event originated from a port interface
   */
  public boolean isFromPort() {
    return isFromPort;
  }
  
  /**
   * Sets the source component ID for this event.
   *
   * @param sourceComponentId The ID of the component that generated this event
   */
  public void setSourceComponentId(String sourceComponentId) {
    this.sourceComponentId = sourceComponentId;
  }

  /**
   * Gets the unique identifier for this event.
   *
   * @return The event ID
   */
  public String getEventId() {
    return eventId;
  }

  /**
   * Gets the timestamp when this event occurred.
   *
   * @return The event timestamp
   */
  public Instant getOccurredOn() {
    return occurredOn;
  }

  /**
   * Gets the event type, derived from the class name.
   *
   * @return The event type
   */
  public String getEventType() {
    return eventType;
  }
  
  /**
   * Gets the ID of the component that generated this event.
   *
   * @return The source component ID, or null if not set
   */
  public String getSourceComponentId() {
    return sourceComponentId;
  }
}
