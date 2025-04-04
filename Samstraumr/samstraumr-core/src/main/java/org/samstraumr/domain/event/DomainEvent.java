/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Base domain event class for the S8r framework
 */

package org.samstraumr.domain.event;

import java.time.Instant;
import java.util.UUID;

/** 
 * Base class for all domain events, following DDD and Clean Architecture
 * principles by keeping events as part of the domain model.
 */
public abstract class DomainEvent {
  private final String eventId = UUID.randomUUID().toString();
  private final Instant occurredOn = Instant.now();
  private final String eventType = this.getClass().getSimpleName();

  // Getters
  public String getEventId() { return eventId; }
  public Instant getOccurredOn() { return occurredOn; }
  public String getEventType() { return eventType; }
}
