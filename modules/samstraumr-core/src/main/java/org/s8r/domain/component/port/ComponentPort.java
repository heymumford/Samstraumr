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

package org.s8r.domain.component.port;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.s8r.domain.event.DomainEvent;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;

/**
 * Port interface for the Component domain entity.
 *
 * <p>This interface defines the contract for components in the domain layer, allowing adapter
 * implementations to bridge between legacy implementations and the clean architecture domain model.
 */
public interface ComponentPort {

  /**
   * Gets the component's unique identifier.
   *
   * @return The component ID
   */
  ComponentId getId();

  /**
   * Gets the component's name.
   *
   * @return The component name
   */
  default String getName() {
    return getId().getShortId();
  }

  /**
   * Gets the component's type.
   *
   * @return The component type
   */
  default String getType() {
    return "Component";
  }

  /**
   * Gets the component's state.
   *
   * @return The component state
   */
  default String getState() {
    return getLifecycleState().name();
  }

  /**
   * Gets the component's current lifecycle state.
   *
   * @return The current lifecycle state
   */
  LifecycleState getLifecycleState();

  /**
   * Gets the component's lineage (ancestry chain).
   *
   * @return An unmodifiable list containing lineage entries
   */
  List<String> getLineage();

  /**
   * Gets the component's activity log.
   *
   * @return An unmodifiable list containing activity log entries
   */
  List<String> getActivityLog();

  /**
   * Gets the component's creation time.
   *
   * @return The instant when the component was created
   */
  Instant getCreationTime();

  /**
   * Gets the domain events raised by this component.
   *
   * @return An unmodifiable list of domain events
   */
  List<DomainEvent> getDomainEvents();

  /**
   * Adds an entry to the component's lineage.
   *
   * @param entry The lineage entry to add
   */
  void addToLineage(String entry);

  /** Clears all accumulated domain events. */
  void clearEvents();

  /**
   * Publishes data to a specified channel.
   *
   * @param channel The channel to publish to
   * @param data The data to publish
   */
  void publishData(String channel, Map<String, Object> data);

  /**
   * Publishes a single key-value pair to a specified channel.
   *
   * @param channel The channel to publish to
   * @param key The data key
   * @param value The data value
   */
  void publishData(String channel, String key, Object value);

  /**
   * Transitions the component to a new lifecycle state.
   *
   * @param newState The new state to transition to
   * @throws org.s8r.domain.exception.InvalidStateTransitionException if the transition is not
   *     allowed
   */
  void transitionTo(LifecycleState newState);

  /**
   * Activates the component.
   *
   * @throws org.s8r.domain.exception.InvalidOperationException if the component is not in READY
   *     state
   */
  void activate();

  /**
   * Deactivates the component.
   *
   * @throws org.s8r.domain.exception.InvalidOperationException if the component is not in ACTIVE
   *     state
   */
  void deactivate();

  /** Terminates the component, releasing its resources. */
  void terminate();
}
