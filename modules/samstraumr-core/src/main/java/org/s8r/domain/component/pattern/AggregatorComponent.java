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

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;

import org.s8r.domain.component.Component;
import org.s8r.domain.event.ComponentDataEvent;
import org.s8r.domain.identity.ComponentId;

/**
 * A specialized component that aggregates data over time or by count.
 *
 * <p>AggregatorComponent collects data from input channels, aggregates it according to defined
 * strategies, and publishes aggregated results to output channels periodically or after reaching a
 * count threshold.
 */
public class AggregatorComponent extends Component {
  private final String inputChannel;
  private final String outputChannel;
  private final Map<String, List<Object>> aggregationBuffer;
  private final Map<String, BiFunction<Object, Object, Object>> aggregators;

  private final AggregationStrategy strategy;
  private final int countThreshold;
  private final Duration timeThreshold;
  private final Map<String, Instant> lastAggregationTime;

  /** Strategy for when to publish aggregated data. */
  public enum AggregationStrategy {
    /** Aggregate by count, publishing after reaching a threshold. */
    COUNT,

    /** Aggregate by time, publishing after a time threshold. */
    TIME,

    /** Aggregate by both count and time, publishing when either threshold is reached. */
    HYBRID
  }

  /**
   * Creates a new aggregator component with count-based aggregation.
   *
   * @param id The component ID
   * @param inputChannel The channel to subscribe to for input data
   * @param outputChannel The channel to publish aggregated data to
   * @param countThreshold The count threshold for publishing aggregated data
   */
  private AggregatorComponent(
      ComponentId id, String inputChannel, String outputChannel, int countThreshold) {
    super(id);
    this.inputChannel = inputChannel;
    this.outputChannel = outputChannel;
    this.aggregationBuffer = new ConcurrentHashMap<>();
    this.aggregators = new HashMap<>();
    this.strategy = AggregationStrategy.COUNT;
    this.countThreshold = countThreshold;
    this.timeThreshold = null;
    this.lastAggregationTime = new ConcurrentHashMap<>();

    logActivity(
        "Created count-based aggregator from channel ["
            + inputChannel
            + "] to channel ["
            + outputChannel
            + "] with threshold "
            + countThreshold);
  }

  /**
   * Creates a new aggregator component with time-based aggregation.
   *
   * @param id The component ID
   * @param inputChannel The channel to subscribe to for input data
   * @param outputChannel The channel to publish aggregated data to
   * @param timeThreshold The time threshold for publishing aggregated data
   */
  private AggregatorComponent(
      ComponentId id, String inputChannel, String outputChannel, Duration timeThreshold) {
    super(id);
    this.inputChannel = inputChannel;
    this.outputChannel = outputChannel;
    this.aggregationBuffer = new ConcurrentHashMap<>();
    this.aggregators = new HashMap<>();
    this.strategy = AggregationStrategy.TIME;
    this.countThreshold = 0;
    this.timeThreshold = timeThreshold;
    this.lastAggregationTime = new ConcurrentHashMap<>();

    logActivity(
        "Created time-based aggregator from channel ["
            + inputChannel
            + "] to channel ["
            + outputChannel
            + "] with threshold "
            + timeThreshold);
  }

  /**
   * Creates a new aggregator component with hybrid aggregation.
   *
   * @param id The component ID
   * @param inputChannel The channel to subscribe to for input data
   * @param outputChannel The channel to publish aggregated data to
   * @param countThreshold The count threshold for publishing aggregated data
   * @param timeThreshold The time threshold for publishing aggregated data
   */
  private AggregatorComponent(
      ComponentId id,
      String inputChannel,
      String outputChannel,
      int countThreshold,
      Duration timeThreshold) {
    super(id);
    this.inputChannel = inputChannel;
    this.outputChannel = outputChannel;
    this.aggregationBuffer = new ConcurrentHashMap<>();
    this.aggregators = new HashMap<>();
    this.strategy = AggregationStrategy.HYBRID;
    this.countThreshold = countThreshold;
    this.timeThreshold = timeThreshold;
    this.lastAggregationTime = new ConcurrentHashMap<>();

    logActivity(
        "Created hybrid aggregator from channel ["
            + inputChannel
            + "] to channel ["
            + outputChannel
            + "] with count threshold "
            + countThreshold
            + " and time threshold "
            + timeThreshold);
  }

  /**
   * Creates a new count-based aggregator component.
   *
   * @param reason The reason for creating this component
   * @param inputChannel The channel to subscribe to for input data
   * @param outputChannel The channel to publish aggregated data to
   * @param countThreshold The count threshold for publishing aggregated data
   * @return A new aggregator component
   */
  public static AggregatorComponent createCountBased(
      String reason, String inputChannel, String outputChannel, int countThreshold) {
    ComponentId id = ComponentId.create(reason);
    return new AggregatorComponent(id, inputChannel, outputChannel, countThreshold);
  }

  /**
   * Creates a new time-based aggregator component.
   *
   * @param reason The reason for creating this component
   * @param inputChannel The channel to subscribe to for input data
   * @param outputChannel The channel to publish aggregated data to
   * @param timeThreshold The time threshold for publishing aggregated data
   * @return A new aggregator component
   */
  public static AggregatorComponent createTimeBased(
      String reason, String inputChannel, String outputChannel, Duration timeThreshold) {
    ComponentId id = ComponentId.create(reason);
    return new AggregatorComponent(id, inputChannel, outputChannel, timeThreshold);
  }

  /**
   * Creates a new hybrid aggregator component.
   *
   * @param reason The reason for creating this component
   * @param inputChannel The channel to subscribe to for input data
   * @param outputChannel The channel to publish aggregated data to
   * @param countThreshold The count threshold for publishing aggregated data
   * @param timeThreshold The time threshold for publishing aggregated data
   * @return A new aggregator component
   */
  public static AggregatorComponent createHybrid(
      String reason,
      String inputChannel,
      String outputChannel,
      int countThreshold,
      Duration timeThreshold) {
    ComponentId id = ComponentId.create(reason);
    return new AggregatorComponent(id, inputChannel, outputChannel, countThreshold, timeThreshold);
  }

  /**
   * Adds an aggregator function for a specific data key.
   *
   * @param key The data key to aggregate
   * @param aggregator The aggregator function that takes the current aggregate and a new value
   * @return This aggregator for method chaining
   */
  public AggregatorComponent addAggregator(
      String key, BiFunction<Object, Object, Object> aggregator) {
    aggregators.put(key, aggregator);
    logActivity("Added aggregator for key: " + key);
    return this;
  }

  /**
   * Processes input data, buffering it for aggregation.
   *
   * @param event The data event to process
   */
  public void processData(ComponentDataEvent event) {
    logActivity("Processing data from channel: " + event.getDataChannel());

    // Add data to the buffer for each key
    for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();

      // Initialize buffer for this key if it doesn't exist and atomically add the value
      aggregationBuffer.compute(
          key,
          (k, existingList) -> {
            if (existingList == null) {
              existingList = new CopyOnWriteArrayList<>();
            }
            existingList.add(value);
            return existingList;
          });

      // Initialize last aggregation time for this key if it doesn't exist
      if (strategy == AggregationStrategy.TIME || strategy == AggregationStrategy.HYBRID) {
        lastAggregationTime.computeIfAbsent(key, k -> Instant.now());
      }
    }

    // Check if we should publish aggregated data
    boolean shouldPublish = false;

    if (strategy == AggregationStrategy.COUNT || strategy == AggregationStrategy.HYBRID) {
      // Check if any key has reached the count threshold
      for (List<Object> buffer : aggregationBuffer.values()) {
        if (buffer.size() >= countThreshold) {
          shouldPublish = true;
          break;
        }
      }
    }

    if (!shouldPublish
        && (strategy == AggregationStrategy.TIME || strategy == AggregationStrategy.HYBRID)) {
      // Check if any key has reached the time threshold
      Instant now = Instant.now();
      for (Map.Entry<String, Instant> entry : lastAggregationTime.entrySet()) {
        if (Duration.between(entry.getValue(), now).compareTo(timeThreshold) >= 0) {
          shouldPublish = true;
          break;
        }
      }
    }

    if (shouldPublish) {
      publishAggregatedData();
    }
  }

  /** Publishes the aggregated data and resets the buffers. */
  private void publishAggregatedData() {
    logActivity("Publishing aggregated data to channel: " + outputChannel);

    // Create a map for the aggregated data
    Map<String, Object> aggregatedData = new HashMap<>();

    // Aggregate data for each key
    for (Map.Entry<String, List<Object>> entry : aggregationBuffer.entrySet()) {
      String key = entry.getKey();
      List<Object> values = entry.getValue();

      if (values.isEmpty()) {
        continue;
      }

      BiFunction<Object, Object, Object> aggregator = aggregators.get(key);
      if (aggregator != null) {
        // Apply custom aggregator
        Object result = values.get(0);
        for (int i = 1; i < values.size(); i++) {
          result = aggregator.apply(result, values.get(i));
        }
        aggregatedData.put(key, result);
        logActivity(
            "Aggregated "
                + values.size()
                + " values for key ["
                + key
                + "] using custom aggregator");
      } else {
        // Default behavior: use the last value
        aggregatedData.put(key, values.get(values.size() - 1));
        logActivity(
            "No aggregator defined for key ["
                + key
                + "], using last value from "
                + values.size()
                + " values");
      }
    }

    // Publish the aggregated data
    publishData(outputChannel, aggregatedData);

    // Reset buffers and timestamps
    aggregationBuffer.clear();
    Instant now = Instant.now();
    lastAggregationTime.replaceAll((k, v) -> now);

    logActivity(
        "Published aggregated data with "
            + aggregatedData.size()
            + " keys to channel: "
            + outputChannel);
  }

  /** Manually triggers publication of aggregated data. */
  public void triggerAggregation() {
    logActivity("Manually triggering aggregation");
    publishAggregatedData();
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
   * Gets the output channel.
   *
   * @return The output channel
   */
  public String getOutputChannel() {
    return outputChannel;
  }

  /**
   * Gets the aggregation strategy.
   *
   * @return The aggregation strategy
   */
  public AggregationStrategy getStrategy() {
    return strategy;
  }
}
