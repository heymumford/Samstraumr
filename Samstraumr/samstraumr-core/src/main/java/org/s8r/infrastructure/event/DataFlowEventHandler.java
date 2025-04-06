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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.s8r.application.port.DataFlowEventPort;
import org.s8r.application.port.EventDispatcher.EventHandler;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.event.ComponentDataEvent;
import org.s8r.domain.identity.ComponentId;

/**
 * Event handler for data flow between components.
 *
 * <p>This handler implements a publish-subscribe mechanism for component data events, allowing
 * components to communicate data without direct dependencies.
 */
public class DataFlowEventHandler implements DataFlowEventPort {
  private final LoggerPort logger;

  // Maps data channels to subscribers
  private final Map<String, Map<ComponentId, Consumer<ComponentDataEvent>>> channelSubscribers;

  // Maps component IDs to the channels they're subscribed to
  private final Map<ComponentId, Set<String>> componentSubscriptions;

  /**
   * Creates a new data flow event handler.
   *
   * @param logger The logger to use
   */
  public DataFlowEventHandler(LoggerPort logger) {
    this.logger = logger;
    this.channelSubscribers = new ConcurrentHashMap<>();
    this.componentSubscriptions = new ConcurrentHashMap<>();
  }

  /**
   * Creates a handler for ComponentDataEvent events.
   *
   * @return The event handler
   */
  public EventHandler<ComponentDataEvent> dataEventHandler() {
    return event -> {
      String channel = event.getDataChannel();
      ComponentId sourceId = event.getSourceId();

      logger.debug(
          "Data event on channel [{}] from component [{}]", channel, sourceId.getShortId());

      Map<ComponentId, Consumer<ComponentDataEvent>> subscribers = channelSubscribers.get(channel);
      if (subscribers == null || subscribers.isEmpty()) {
        logger.debug("No subscribers for channel [{}]", channel);
        return;
      }

      // Notify all subscribers
      subscribers.forEach(
          (subscriberId, consumer) -> {
            // Don't send data back to the source component
            if (!subscriberId.equals(sourceId)) {
              try {
                consumer.accept(event);
                logger.debug(
                    "Delivered data from [{}] to subscriber [{}] on channel [{}]",
                    sourceId.getShortId(),
                    subscriberId.getShortId(),
                    channel);
              } catch (Exception e) {
                logger.error(
                    "Error delivering data to subscriber [{}] on channel [{}]: {}",
                    subscriberId.getShortId(),
                    channel,
                    e.getMessage(),
                    e);
              }
            }
          });
    };
  }

  /**
   * Subscribes a component to a data channel.
   *
   * @param componentId The subscribing component's ID
   * @param channel The channel to subscribe to
   * @param handler The handler to process data events
   */
  public void subscribe(
      ComponentId componentId, String channel, Consumer<ComponentDataEvent> handler) {
    logger.info("Component [{}] subscribing to channel [{}]", componentId.getShortId(), channel);

    // Add to channel subscribers
    Map<ComponentId, Consumer<ComponentDataEvent>> subscribers =
        channelSubscribers.computeIfAbsent(channel, k -> new ConcurrentHashMap<>());
    subscribers.put(componentId, handler);

    // Record component subscription
    Set<String> subscriptions =
        componentSubscriptions.computeIfAbsent(componentId, k -> new HashSet<>());
    subscriptions.add(channel);
  }

  /**
   * Unsubscribes a component from a data channel.
   *
   * @param componentId The component's ID
   * @param channel The channel to unsubscribe from
   */
  public void unsubscribe(ComponentId componentId, String channel) {
    logger.info(
        "Component [{}] unsubscribing from channel [{}]", componentId.getShortId(), channel);

    // Remove from channel subscribers
    Map<ComponentId, Consumer<ComponentDataEvent>> subscribers = channelSubscribers.get(channel);
    if (subscribers != null) {
      subscribers.remove(componentId);
      if (subscribers.isEmpty()) {
        channelSubscribers.remove(channel);
      }
    }

    // Update component subscriptions
    Set<String> subscriptions = componentSubscriptions.get(componentId);
    if (subscriptions != null) {
      subscriptions.remove(channel);
      if (subscriptions.isEmpty()) {
        componentSubscriptions.remove(componentId);
      }
    }
  }

  /**
   * Unsubscribes a component from all data channels.
   *
   * @param componentId The component's ID
   */
  public void unsubscribeAll(ComponentId componentId) {
    logger.info("Component [{}] unsubscribing from all channels", componentId.getShortId());

    Set<String> subscriptions = componentSubscriptions.get(componentId);
    if (subscriptions != null) {
      // Copy to avoid concurrent modification
      Set<String> channels = new HashSet<>(subscriptions);
      channels.forEach(channel -> unsubscribe(componentId, channel));
    }
  }

  /**
   * Gets all channels available in the system.
   *
   * @return A set of all data channels
   */
  public Set<String> getAvailableChannels() {
    return channelSubscribers.keySet();
  }

  /**
   * Gets all channels a component is subscribed to.
   *
   * @param componentId The component's ID
   * @return A set of channel names
   */
  public Set<String> getComponentSubscriptions(ComponentId componentId) {
    Set<String> subscriptions = componentSubscriptions.get(componentId);
    return subscriptions != null ? Set.copyOf(subscriptions) : Set.of();
  }
}
