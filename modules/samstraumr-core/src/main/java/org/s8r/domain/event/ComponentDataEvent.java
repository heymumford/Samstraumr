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

import java.util.HashMap;
import java.util.Map;

import org.s8r.domain.identity.ComponentId;

/**
 * Event raised when a component produces data to be shared with other components. Enables
 * event-driven data flow between components without direct dependencies.
 */
public class ComponentDataEvent extends DomainEvent {
  private final ComponentId sourceId;
  private final String dataChannel;
  private final Map<String, Object> data;

  /** Creates a new component data event. */
  public ComponentDataEvent(ComponentId sourceId, String dataChannel, Map<String, Object> data) {
    this.sourceId = sourceId;
    this.dataChannel = dataChannel;
    this.data = new HashMap<>(data); // Defensive copy
  }

  /** Creates a component data event with a single data value. */
  public static ComponentDataEvent createSingleValue(
      ComponentId sourceId, String dataChannel, String key, Object value) {
    Map<String, Object> data = new HashMap<>();
    data.put(key, value);
    return new ComponentDataEvent(sourceId, dataChannel, data);
  }

  // Getters
  public ComponentId getSourceId() {
    return sourceId;
  }

  public String getDataChannel() {
    return dataChannel;
  }

  public Map<String, Object> getData() {
    return Map.copyOf(data);
  }

  public Object getValue(String key) {
    return data.get(key);
  }
}
