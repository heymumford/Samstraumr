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

package org.s8r.domain.component.pattern;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.s8r.domain.event.ComponentDataEvent;

/**
 * Port interface for data flow operations specific to a component.
 *
 * <p>This interface extends the base data flow capabilities with component-specific operations,
 * allowing a component to manage its own data flow subscriptions and publications without needing
 * to specify its ID for each operation.
 *
 * <p>This follows the Clean Architecture port pattern for the domain layer, defining capabilities
 * that components can use without depending on implementation details.
 */
public interface DataFlowComponentPort {

  /**
   * Subscribes to a data channel.
   *
   * @param channel The channel to subscribe to
   * @param handler The handler to process data events
   * @return True if subscription was successful
   */
  boolean subscribe(String channel, Consumer<ComponentDataEvent> handler);

  /**
   * Unsubscribes from a data channel.
   *
   * @param channel The channel to unsubscribe from
   * @return True if unsubscription was successful
   */
  boolean unsubscribe(String channel);

  /**
   * Unsubscribes from all data channels.
   *
   * @return True if unsubscription was successful
   */
  boolean unsubscribeAll();

  /**
   * Publishes data to a channel.
   *
   * @param channel The channel to publish to
   * @param data The data to publish
   * @return True if publishing was successful
   */
  boolean publish(String channel, Map<String, Object> data);

  /**
   * Gets all channels this component is subscribed to.
   *
   * @return A set of channel names
   */
  Set<String> getSubscriptions();

  /**
   * Gets the number of subscriptions for this component.
   *
   * @return The subscription count
   */
  default int getSubscriptionCount() {
    return getSubscriptions().size();
  }

  /**
   * Checks if this component is subscribed to a specific channel.
   *
   * @param channel The channel to check
   * @return True if subscribed
   */
  default boolean isSubscribedTo(String channel) {
    return getSubscriptions().contains(channel);
  }
}
