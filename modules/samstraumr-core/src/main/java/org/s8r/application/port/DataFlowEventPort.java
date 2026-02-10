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

package org.s8r.application.port;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.s8r.domain.event.ComponentDataEvent;
import org.s8r.domain.identity.ComponentId;

/**
 * Port for data flow event handling services.
 *
 * <p>This port defines operations for subscribing components to data channels and managing the flow
 * of data between components.
 */
public interface DataFlowEventPort {

  /**
   * Subscribes a component to a data channel.
   *
   * @param componentId The subscribing component's ID
   * @param channel The channel to subscribe to
   * @param handler The handler to process data events
   */
  void subscribe(ComponentId componentId, String channel, Consumer<ComponentDataEvent> handler);

  /**
   * Unsubscribes a component from a data channel.
   *
   * @param componentId The component's ID
   * @param channel The channel to unsubscribe from
   */
  void unsubscribe(ComponentId componentId, String channel);

  /**
   * Unsubscribes a component from all data channels.
   *
   * @param componentId The component's ID
   */
  void unsubscribeAll(ComponentId componentId);

  /**
   * Gets all channels available in the system.
   *
   * @return A set of all data channels
   */
  Set<String> getAvailableChannels();

  /**
   * Gets all channels a component is subscribed to.
   *
   * @param componentId The component's ID
   * @return A set of channel names
   */
  Set<String> getComponentSubscriptions(ComponentId componentId);

  /**
   * Publishes data from a component to a specific channel.
   *
   * @param componentId The source component's ID
   * @param channel The channel to publish to
   * @param data The data to publish
   */
  void publishData(ComponentId componentId, String channel, Map<String, Object> data);
}
