/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.infrastructure.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.s8r.application.port.DataFlowEventPort;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.event.ComponentDataEvent;
import org.s8r.domain.identity.ComponentId;

/**
 * Adapter implementation of the DataFlowEventPort interface.
 *
 * <p>This adapter manages data flow subscriptions and publications between components.
 */
public class DataFlowEventAdapter implements DataFlowEventPort {

  private final LoggerPort logger;
  private final Map<String, Map<ComponentId, Consumer<ComponentDataEvent>>> channelSubscriptions;
  private final Map<ComponentId, Set<String>> componentSubscriptions;
  private final DataFlowEventHandler dataFlowHandler;

  /**
   * Creates a new DataFlowEventAdapter.
   *
   * @param logger The logger to use
   */
  public DataFlowEventAdapter(LoggerPort logger) {
    this.logger = logger;
    this.channelSubscriptions = new ConcurrentHashMap<>();
    this.componentSubscriptions = new ConcurrentHashMap<>();

    // Initialize the data flow handler
    String[] eventTypes = new String[] {"data.flow", "data.update", "data.request"};
    this.dataFlowHandler = new DataFlowEventHandler(logger, eventTypes, this::processDataEvent);
  }

  private void processDataEvent(Map<String, Object> data) {
    // Logic to process data events and route them to subscribers
    String channel = (String) data.getOrDefault("channel", "default");
    String sourceId = (String) data.getOrDefault("sourceId", "unknown");
    ComponentId sourceComponentId;
    try {
      sourceComponentId = ComponentId.fromString(sourceId, "Data flow event");
    } catch (IllegalArgumentException e) {
      logger.warn("Invalid component ID format in data event: {}", sourceId);
      sourceComponentId = ComponentId.create("Data flow event from unknown source");
    }

    // Create a component data event
    ComponentDataEvent event = new ComponentDataEvent(sourceComponentId, channel, data);

    // Deliver to subscribers
    if (channelSubscriptions.containsKey(channel)) {
      channelSubscriptions
          .get(channel)
          .forEach(
              (componentId, handler) -> {
                try {
                  handler.accept(event);
                } catch (Exception e) {
                  logger.error(
                      "Error delivering data event to component {}: {}",
                      componentId,
                      e.getMessage(),
                      e);
                }
              });
    }
  }

  @Override
  public void subscribe(
      ComponentId componentId, String channel, Consumer<ComponentDataEvent> handler) {
    // Add to channel subscriptions
    channelSubscriptions
        .computeIfAbsent(channel, k -> new ConcurrentHashMap<>())
        .put(componentId, handler);

    // Track component's subscriptions
    componentSubscriptions.computeIfAbsent(componentId, k -> new HashSet<>()).add(channel);

    logger.debug("Component {} subscribed to channel {}", componentId, channel);
  }

  @Override
  public void unsubscribe(ComponentId componentId, String channel) {
    // Remove from channel subscriptions
    if (channelSubscriptions.containsKey(channel)) {
      channelSubscriptions.get(channel).remove(componentId);
      if (channelSubscriptions.get(channel).isEmpty()) {
        channelSubscriptions.remove(channel);
      }
    }

    // Update component's subscriptions
    if (componentSubscriptions.containsKey(componentId)) {
      componentSubscriptions.get(componentId).remove(channel);
      if (componentSubscriptions.get(componentId).isEmpty()) {
        componentSubscriptions.remove(componentId);
      }
    }

    logger.debug("Component {} unsubscribed from channel {}", componentId, channel);
  }

  @Override
  public void unsubscribeAll(ComponentId componentId) {
    // Get all channels the component is subscribed to
    Set<String> channels = new HashSet<>(getComponentSubscriptions(componentId));

    // Unsubscribe from each channel
    for (String channel : channels) {
      unsubscribe(componentId, channel);
    }

    // Ensure component is removed from tracking
    componentSubscriptions.remove(componentId);

    logger.debug("Component {} unsubscribed from all channels", componentId);
  }

  @Override
  public Set<String> getAvailableChannels() {
    return new HashSet<>(channelSubscriptions.keySet());
  }

  @Override
  public Set<String> getComponentSubscriptions(ComponentId componentId) {
    return new HashSet<>(componentSubscriptions.getOrDefault(componentId, new HashSet<>()));
  }

  @Override
  public void publishData(ComponentId componentId, String channel, Map<String, Object> data) {
    // Add metadata to the data event
    Map<String, Object> eventData = new HashMap<>(data);
    eventData.put("sourceId", componentId.toString());
    eventData.put("channel", channel);
    eventData.put("timestamp", System.currentTimeMillis());

    // Process the data directly
    processDataEvent(eventData);

    logger.debug("Component {} published data to channel {}", componentId, channel);
  }

  /**
   * Gets the data flow handler.
   *
   * @return The data flow handler
   */
  public DataFlowEventHandler getDataFlowHandler() {
    return dataFlowHandler;
  }
}
