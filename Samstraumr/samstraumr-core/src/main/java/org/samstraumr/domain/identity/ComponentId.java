/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Domain implementation of ComponentId in the S8r framework
 */

package org.samstraumr.domain.identity;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** 
 * Immutable value object representing a Component's identity.
 * Clean Architecture principles: no infrastructure dependencies,
 * immutable properties, value-based equality.
 */
public final class ComponentId {
  private final UUID id;
  private final String reason;
  private final Instant creationTime;
  private final String shortId;

  private ComponentId(UUID id, String reason, Instant creationTime) {
    this.id = Objects.requireNonNull(id, "ID cannot be null");
    this.reason = Objects.requireNonNull(reason, "Reason cannot be null");
    this.creationTime = Objects.requireNonNull(creationTime, "Creation time cannot be null");
    this.shortId = id.toString().substring(0, 8);
  }

  /** Creates a new ComponentId with a random UUID. */
  public static ComponentId create(String reason) {
    return new ComponentId(UUID.randomUUID(), reason, Instant.now());
  }

  /** Creates a ComponentId from an existing UUID string. */
  public static ComponentId fromString(String id, String reason) {
    try {
      return new ComponentId(UUID.fromString(id), reason, Instant.now());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid UUID format: " + id, e);
    }
  }

  // Getters
  public UUID getValue() { return id; }
  public String getIdString() { return id.toString(); }
  public String getShortId() { return shortId; }
  public String getReason() { return reason; }
  public Instant getCreationTime() { return creationTime; }
  
  /** Creates a formatted address representation of this component ID. */
  public String toAddress() { return "CO<" + shortId + ">"; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    return Objects.equals(id, ((ComponentId) o).id);
  }

  @Override
  public int hashCode() { return Objects.hash(id); }

  @Override
  public String toString() {
    return "ComponentId{id=" + id + ", reason='" + reason + "', creationTime=" + creationTime + '}';
  }
}
