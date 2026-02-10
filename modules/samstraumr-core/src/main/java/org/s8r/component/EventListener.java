/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

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

package org.s8r.component;

import java.util.Map;

/**
 * Interface for listeners that want to be notified of component events.
 *
 * <p>Event listeners receive notifications about state changes, lifecycle transitions, and other
 * important occurrences within components. They enable reactive and event-driven behavior in the
 * system.
 *
 * <p>To use an event listener:
 *
 * <ol>
 *   <li>Implement this interface in your listener class
 *   <li>Register the listener with a component using component.addEventListener()
 *   <li>Handle events in the onEvent method
 * </ol>
 */
public interface EventListener {

  /**
   * Called when a component event occurs.
   *
   * @param component The component that raised the event
   * @param eventType The type of event
   * @param data The event data
   */
  void onEvent(Component component, String eventType, Map<String, Object> data);

  /**
   * Sets the event type this listener is interested in. Use "*" for receiving all events.
   *
   * @param eventType The event type to listen for
   */
  void setEventType(String eventType);

  /**
   * Gets the event type this listener is interested in.
   *
   * @return The event type
   */
  String getEventType();

  /**
   * Checks if this listener is interested in the specified event type. By default, this returns
   * true if the event type matches the one this listener is registered for, or if this listener is
   * registered for all events ("*").
   *
   * @param eventType The event type to check
   * @return true if this listener is interested in the event type
   */
  default boolean isInterestedIn(String eventType) {
    return getEventType().equals(eventType) || getEventType().equals("*");
  }

  /**
   * Called when a component is terminated and the listener is being removed. This gives the
   * listener a chance to clean up any resources.
   */
  default void onTermination() {
    // Default implementation is a no-op
  }
}
