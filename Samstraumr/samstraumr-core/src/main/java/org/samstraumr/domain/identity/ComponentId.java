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
 * A value object representing the identity of a Component.
 *
 * <p>This is a pure domain entity following Clean Architecture principles with:
 * <ul>
 *   <li>No frameworks or infrastructure dependencies
 *   <li>Immutable properties
 *   <li>Value object semantics (equality based on value, not reference)
 * </ul>
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

    /**
     * Creates a new ComponentId with a random UUID.
     *
     * @param reason The reason for creating this component
     * @return A new ComponentId instance
     */
    public static ComponentId create(String reason) {
        return new ComponentId(UUID.randomUUID(), reason, Instant.now());
    }

    /**
     * Creates a ComponentId from an existing UUID string.
     *
     * @param id The UUID string to use
     * @param reason The reason for this component
     * @return A new ComponentId instance
     * @throws IllegalArgumentException if the provided id is not a valid UUID
     */
    public static ComponentId fromString(String id, String reason) {
        try {
            return new ComponentId(UUID.fromString(id), reason, Instant.now());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format: " + id, e);
        }
    }

    /**
     * Gets the underlying UUID.
     *
     * @return The UUID
     */
    public UUID getValue() {
        return id;
    }

    /**
     * Gets the UUID as a string.
     *
     * @return The UUID string
     */
    public String getIdString() {
        return id.toString();
    }

    /**
     * Gets the shortened ID (first 8 characters).
     *
     * @return The short ID
     */
    public String getShortId() {
        return shortId;
    }

    /**
     * Gets the reason for creating this component.
     *
     * @return The creation reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * Gets the creation time of this component ID.
     *
     * @return The creation time
     */
    public Instant getCreationTime() {
        return creationTime;
    }

    /**
     * Creates a formatted address representation of this component ID.
     *
     * @return A formatted address string
     */
    public String toAddress() {
        return "CO<" + shortId + ">";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComponentId that = (ComponentId) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ComponentId{" +
                "id=" + id +
                ", reason='" + reason + '\'' +
                ", creationTime=" + creationTime +
                '}';
    }
}