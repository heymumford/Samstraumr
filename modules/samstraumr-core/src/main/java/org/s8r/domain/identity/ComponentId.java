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

package org.s8r.domain.identity;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Immutable value object representing a Component's identity. Clean Architecture principles: no
 * infrastructure dependencies, immutable properties, value-based equality.
 */
public final class ComponentId {
  private final UUID id;
  private final String reason;
  private final Instant creationTime;
  private final String shortId;
  private final List<String> lineage;
  private final UUID parentId; // Can be null for root components

  private ComponentId(UUID id, String reason, Instant creationTime, List<String> lineage) {
    this.id = Objects.requireNonNull(id, "ID cannot be null");
    this.reason = Objects.requireNonNull(reason, "Reason cannot be null");
    this.creationTime = Objects.requireNonNull(creationTime, "Creation time cannot be null");
    this.shortId = id.toString().substring(0, 8);
    this.lineage = lineage != null ? List.copyOf(lineage) : List.of();
    this.parentId = computeParentId(this.lineage);
  }

  /**
   * Computes the parent ID from lineage.
   *
   * @param lineage The lineage list
   * @return The parent UUID if available in lineage, null otherwise
   */
  private static UUID computeParentId(List<String> lineage) {
    if (lineage == null || lineage.isEmpty()) {
      return null;
    }

    try {
      return UUID.fromString(lineage.get(lineage.size() - 1));
    } catch (IllegalArgumentException e) {
      // Invalid UUID in lineage, return null
      return null;
    }
  }

  /** Creates a new ComponentId with a random UUID. */
  public static ComponentId create(String reason) {
    return new ComponentId(UUID.randomUUID(), reason, Instant.now(), Collections.emptyList());
  }

  /** Creates a new ComponentId with the specified lineage. */
  public static ComponentId create(String reason, List<String> lineage) {
    return new ComponentId(UUID.randomUUID(), reason, Instant.now(), lineage);
  }

  /** Creates a ComponentId from an existing UUID string. */
  public static ComponentId fromString(String id, String reason) {
    try {
      return new ComponentId(UUID.fromString(id), reason, Instant.now(), Collections.emptyList());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid UUID format: " + id, e);
    }
  }

  /**
   * Creates a new ComponentId instance with the specified values.
   *
   * @param id The ID as a string
   * @param reason The reason for creation
   * @param lineage The lineage list
   * @return A new ComponentId
   */
  public static ComponentId fromValues(String id, String reason, List<String> lineage) {
    try {
      UUID uuid = UUID.fromString(id);
      return new ComponentId(uuid, reason, Instant.now(), lineage);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid UUID format: " + id, e);
    }
  }

  // Getters
  public UUID getValue() {
    return id;
  }

  public String getIdString() {
    return id.toString();
  }

  public String getShortId() {
    return shortId;
  }

  public String getReason() {
    return reason;
  }

  public Instant getCreationTime() {
    return creationTime;
  }

  /**
   * Gets the component's lineage.
   *
   * @return An unmodifiable view of the lineage list
   */
  public List<String> getLineage() {
    return Collections.unmodifiableList(lineage);
  }

  /**
   * Gets the parent ID if it exists.
   *
   * @return The parent ID, or null if this is a root component
   */
  public UUID getParentId() {
    return parentId;
  }

  /** Creates a formatted address representation of this component ID. */
  public String toAddress() {
    return "CO<" + shortId + ">";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    return Objects.equals(id, ((ComponentId) o).id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "ComponentId{id=" + id + ", reason='" + reason + "', creationTime=" + creationTime + '}';
  }
}
