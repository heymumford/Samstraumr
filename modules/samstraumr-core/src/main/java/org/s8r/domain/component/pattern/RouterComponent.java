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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import org.s8r.domain.component.Component;
import org.s8r.domain.event.ComponentDataEvent;
import org.s8r.domain.identity.ComponentId;

/**
 * A specialized component that routes data to different output channels.
 *
 * <p>RouterComponent receives data from an input channel and routes it to one or more output
 * channels based on routing rules. It can implement content-based routing, filters, splitters, or
 * multicast patterns.
 */
public class RouterComponent extends Component {
  private final String inputChannel;
  private final Map<String, Predicate<Map<String, Object>>> routingRules;
  private final Map<String, Function<Map<String, Object>, Map<String, Object>>> transformers;
  private boolean multicastMode = false;

  /**
   * Creates a new router component.
   *
   * @param id The component ID
   * @param inputChannel The channel to subscribe to for input data
   */
  private RouterComponent(ComponentId id, String inputChannel) {
    super(id);
    this.inputChannel = inputChannel;
    this.routingRules = new HashMap<>();
    this.transformers = new HashMap<>();

    logActivity("Created router for channel: " + inputChannel);
  }

  /**
   * Creates a new router component.
   *
   * @param reason The reason for creating this component
   * @param inputChannel The channel to subscribe to for input data
   * @return A new router component
   */
  public static RouterComponent create(String reason, String inputChannel) {
    ComponentId id = ComponentId.create(reason);
    return new RouterComponent(id, inputChannel);
  }

  /**
   * Sets whether to operate in multicast mode (route to all matching channels).
   *
   * @param multicast true to enable multicast mode, false for single-route mode
   * @return This router for method chaining
   */
  public RouterComponent setMulticast(boolean multicast) {
    this.multicastMode = multicast;
    logActivity("Set multicast mode to: " + multicast);
    return this;
  }

  /**
   * Adds a routing rule for an output channel.
   *
   * @param outputChannel The channel to route data to
   * @param rule The predicate that decides if data should be routed to this channel
   * @return This router for method chaining
   */
  public RouterComponent addRoute(String outputChannel, Predicate<Map<String, Object>> rule) {
    routingRules.put(outputChannel, rule);
    logActivity("Added route to channel: " + outputChannel);
    return this;
  }

  /**
   * Adds a key-based routing rule for an output channel.
   *
   * @param outputChannel The channel to route data to
   * @param key The data key to check
   * @param predicate The predicate to apply to the key's value
   * @return This router for method chaining
   */
  public RouterComponent addKeyRoute(
      String outputChannel, String key, Predicate<Object> predicate) {
    Predicate<Map<String, Object>> rule =
        data -> {
          Object value = data.get(key);
          return value != null && predicate.test(value);
        };
    return addRoute(outputChannel, rule);
  }

  /**
   * Adds a transformer for an output channel.
   *
   * @param outputChannel The channel to route data to
   * @param transformer The function to transform data before routing to this channel
   * @return This router for method chaining
   */
  public RouterComponent addTransformer(
      String outputChannel, Function<Map<String, Object>, Map<String, Object>> transformer) {
    transformers.put(outputChannel, transformer);
    logActivity("Added transformer for channel: " + outputChannel);
    return this;
  }

  /**
   * Processes input data, applying routing rules to send to output channels.
   *
   * @param event The data event to process
   */
  public void processData(ComponentDataEvent event) {
    logActivity("Processing data from channel: " + event.getDataChannel());

    Map<String, Object> data = event.getData();
    List<String> matchedChannels = new ArrayList<>();

    // Find matching routes
    for (Map.Entry<String, Predicate<Map<String, Object>>> entry : routingRules.entrySet()) {
      String channel = entry.getKey();
      Predicate<Map<String, Object>> rule = entry.getValue();

      if (rule.test(data)) {
        matchedChannels.add(channel);
        logActivity("Data matched route to channel: " + channel);

        if (!multicastMode) {
          // Break after first match in single-route mode
          break;
        }
      }
    }

    // Route data to matched channels
    for (String channel : matchedChannels) {
      // Apply transformer if exists
      Map<String, Object> transformedData = data;
      Function<Map<String, Object>, Map<String, Object>> transformer = transformers.get(channel);

      if (transformer != null) {
        transformedData = transformer.apply(data);
        logActivity("Applied transformer for channel: " + channel);
      }

      // Publish to the channel
      publishData(channel, transformedData);
      logActivity("Routed data to channel: " + channel);
    }

    if (matchedChannels.isEmpty()) {
      logActivity("No matching routes found for data");
    }
  }

  /**
   * Gets the input channel.
   *
   * @return The input channel
   */
  public String getInputChannel() {
    return inputChannel;
  }

  /**
   * Gets all output channels with routing rules.
   *
   * @return A list of output channel names
   */
  public List<String> getOutputChannels() {
    return new ArrayList<>(routingRules.keySet());
  }

  /**
   * Checks if this router is in multicast mode.
   *
   * @return true if in multicast mode, false otherwise
   */
  public boolean isMulticast() {
    return multicastMode;
  }
}
