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

package org.s8r.domain.component.monitoring;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;

import org.s8r.domain.component.Component;
import org.s8r.domain.event.ComponentDataEvent;
import org.s8r.domain.identity.ComponentId;

/**
 * Component that collects and publishes metrics about event flow and processing.
 *
 * <p>MetricsCollector monitors message counts, processing times, and error rates for multiple
 * channels, providing critical operational data for system monitoring.
 */
public class MetricsCollector extends Component {
  private static final String METRICS_CHANNEL = "system.metrics";

  private final Map<String, ChannelMetrics> channelMetrics = new ConcurrentHashMap<>();
  private final Map<ComponentId, ComponentMetrics> componentMetrics = new ConcurrentHashMap<>();
  private final Duration publishInterval;
  private Instant lastPublishTime;

  /**
   * Creates a new metrics collector.
   *
   * @param id The component ID
   * @param publishInterval The interval at which to publish metrics
   */
  private MetricsCollector(ComponentId id, Duration publishInterval) {
    super(id);
    this.publishInterval = publishInterval;
    this.lastPublishTime = Instant.now();

    logActivity("Metrics collector created with publish interval " + publishInterval);
  }

  /**
   * Creates a new metrics collector.
   *
   * @param reason The reason for creating this component
   * @param publishInterval The interval at which to publish metrics
   * @return A new metrics collector
   */
  public static MetricsCollector create(String reason, Duration publishInterval) {
    ComponentId id = ComponentId.create(reason);
    return new MetricsCollector(id, publishInterval);
  }

  /**
   * Records metrics for a message on a channel.
   *
   * @param channelName The channel name
   * @param sourceId The source component ID
   * @param processingSizeBytes The size of the message in bytes
   * @param processingTimeMs The time spent processing the message
   */
  public void recordMessageMetrics(
      String channelName, ComponentId sourceId, long processingSizeBytes, long processingTimeMs) {
    // Get or create channel metrics
    ChannelMetrics metrics = channelMetrics.computeIfAbsent(channelName, k -> new ChannelMetrics());

    // Update channel metrics
    metrics.messageCount.increment();
    metrics.byteCount.add(processingSizeBytes);
    metrics.totalProcessingTime.add(processingTimeMs);
    metrics.lastMessageTime = Instant.now();

    // Get or create component metrics
    ComponentMetrics sourceMetrics =
        componentMetrics.computeIfAbsent(sourceId, k -> new ComponentMetrics());

    // Update component metrics
    sourceMetrics.messagesSent.incrementAndGet();
    sourceMetrics.bytesSent.add(processingSizeBytes);

    // Check if it's time to publish metrics
    checkAndPublishMetrics();
  }

  /**
   * Records metrics for an error.
   *
   * @param channelName The channel name
   * @param componentId The component ID
   * @param errorMessage The error message
   */
  public void recordError(String channelName, ComponentId componentId, String errorMessage) {
    // Get or create channel metrics
    ChannelMetrics metrics = channelMetrics.computeIfAbsent(channelName, k -> new ChannelMetrics());

    // Update channel metrics
    metrics.errorCount.increment();
    metrics.lastErrorTime = Instant.now();
    metrics.lastErrorMessage = errorMessage;

    // Get or create component metrics
    ComponentMetrics compMetrics =
        componentMetrics.computeIfAbsent(componentId, k -> new ComponentMetrics());

    // Update component metrics
    compMetrics.errorCount.incrementAndGet();
    compMetrics.lastErrorTime = Instant.now();
    compMetrics.lastErrorMessage = errorMessage;

    // Errors are published immediately
    publishMetrics();
  }

  /**
   * Records component activation.
   *
   * @param componentId The component ID
   */
  public void recordComponentActivation(ComponentId componentId) {
    // Get or create component metrics
    ComponentMetrics metrics =
        componentMetrics.computeIfAbsent(componentId, k -> new ComponentMetrics());

    // Update metrics
    metrics.isActive = true;
    metrics.lastStateChangeTime = Instant.now();
  }

  /**
   * Records component deactivation.
   *
   * @param componentId The component ID
   */
  public void recordComponentDeactivation(ComponentId componentId) {
    // Get or create component metrics
    ComponentMetrics metrics =
        componentMetrics.computeIfAbsent(componentId, k -> new ComponentMetrics());

    // Update metrics
    metrics.isActive = false;
    metrics.lastStateChangeTime = Instant.now();
  }

  /** Checks if it's time to publish metrics and publishes if necessary. */
  private void checkAndPublishMetrics() {
    Instant now = Instant.now();
    if (Duration.between(lastPublishTime, now).compareTo(publishInterval) >= 0) {
      publishMetrics();
    }
  }

  /** Publishes the current metrics. */
  public void publishMetrics() {
    Map<String, Object> metricsData = new HashMap<>();

    // Add channel metrics
    Map<String, Map<String, Object>> channelMetricsMap = new HashMap<>();
    for (Map.Entry<String, ChannelMetrics> entry : channelMetrics.entrySet()) {
      String channelName = entry.getKey();
      ChannelMetrics metrics = entry.getValue();

      Map<String, Object> metricsMap = new HashMap<>();
      metricsMap.put("messageCount", metrics.messageCount.sum());
      metricsMap.put("byteCount", metrics.byteCount.sum());
      metricsMap.put("errorCount", metrics.errorCount.sum());
      metricsMap.put(
          "avgProcessingTimeMs",
          metrics.messageCount.sum() > 0
              ? metrics.totalProcessingTime.sum() / metrics.messageCount.sum()
              : 0);
      metricsMap.put("lastMessageTime", metrics.lastMessageTime);

      if (metrics.lastErrorTime != null) {
        metricsMap.put("lastErrorTime", metrics.lastErrorTime);
        metricsMap.put("lastErrorMessage", metrics.lastErrorMessage);
      }

      channelMetricsMap.put(channelName, metricsMap);
    }
    metricsData.put("channels", channelMetricsMap);

    // Add component metrics
    Map<String, Map<String, Object>> componentMetricsMap = new HashMap<>();
    for (Map.Entry<ComponentId, ComponentMetrics> entry : componentMetrics.entrySet()) {
      ComponentId componentId = entry.getKey();
      ComponentMetrics metrics = entry.getValue();

      Map<String, Object> metricsMap = new HashMap<>();
      metricsMap.put("messagesSent", metrics.messagesSent.get());
      metricsMap.put("messagesReceived", metrics.messagesReceived.get());
      metricsMap.put("bytesSent", metrics.bytesSent.sum());
      metricsMap.put("bytesReceived", metrics.bytesReceived.sum());
      metricsMap.put("errorCount", metrics.errorCount.get());
      metricsMap.put("isActive", metrics.isActive);
      metricsMap.put("lastStateChangeTime", metrics.lastStateChangeTime);

      if (metrics.lastErrorTime != null) {
        metricsMap.put("lastErrorTime", metrics.lastErrorTime);
        metricsMap.put("lastErrorMessage", metrics.lastErrorMessage);
      }

      componentMetricsMap.put(componentId.getIdString(), metricsMap);
    }
    metricsData.put("components", componentMetricsMap);

    // Add system-wide metrics
    Map<String, Object> systemMetrics = new HashMap<>();
    systemMetrics.put("timestamp", Instant.now());
    systemMetrics.put("totalChannels", channelMetrics.size());
    systemMetrics.put("totalComponents", componentMetrics.size());

    long totalMessages = 0;
    long totalBytes = 0;
    long totalErrors = 0;

    for (ChannelMetrics metrics : channelMetrics.values()) {
      totalMessages += metrics.messageCount.sum();
      totalBytes += metrics.byteCount.sum();
      totalErrors += metrics.errorCount.sum();
    }

    systemMetrics.put("totalMessages", totalMessages);
    systemMetrics.put("totalBytes", totalBytes);
    systemMetrics.put("totalErrors", totalErrors);

    metricsData.put("system", systemMetrics);

    // Publish metrics
    publishData(METRICS_CHANNEL, metricsData);
    logActivity("Published metrics to channel: " + METRICS_CHANNEL);

    lastPublishTime = Instant.now();
  }

  /**
   * Creates a consumer for processing data events and recording metrics.
   *
   * @param channelName The channel name
   * @param componentId The component ID
   * @param nextHandler The next handler in the chain
   * @return A consumer that records metrics and passes events to the next handler
   */
  public Consumer<ComponentDataEvent> createMetricsConsumer(
      String channelName, ComponentId componentId, Consumer<ComponentDataEvent> nextHandler) {

    return event -> {
      // Record the start time
      Instant startTime = Instant.now();

      try {
        // Process the event
        nextHandler.accept(event);

        // Record metrics
        Instant endTime = Instant.now();
        long processingTimeMs = Duration.between(startTime, endTime).toMillis();

        // Estimate the size of the data (a simple approximation)
        long sizeBytes = estimateDataSize(event.getData());

        // Record successful processing
        recordMessageMetrics(channelName, event.getSourceId(), sizeBytes, processingTimeMs);

        // Update component metrics for receiving component
        ComponentMetrics metrics =
            componentMetrics.computeIfAbsent(componentId, k -> new ComponentMetrics());
        metrics.messagesReceived.incrementAndGet();
        metrics.bytesReceived.add(sizeBytes);

      } catch (Exception e) {
        // Record error
        recordError(channelName, componentId, e.getMessage());
        // Re-throw the exception
        throw e;
      }
    };
  }

  /**
   * Estimates the size of a data map in bytes.
   *
   * @param data The data map
   * @return The estimated size in bytes
   */
  private long estimateDataSize(Map<String, Object> data) {
    // A very simple estimation - in a real system this would be more accurate
    long size = 0;
    for (Map.Entry<String, Object> entry : data.entrySet()) {
      // Key size (assuming UTF-8, where each char is ~2 bytes)
      size += entry.getKey().length() * 2;

      // Value size (a very rough estimate)
      Object value = entry.getValue();
      if (value == null) {
        size += 4; // Null reference
      } else if (value instanceof String) {
        size += ((String) value).length() * 2;
      } else if (value instanceof Number) {
        size += 8; // Most numbers are 8 bytes or less
      } else if (value instanceof Boolean) {
        size += 1;
      } else if (value instanceof Map) {
        size += 100; // Placeholder for nested maps
      } else {
        size += 16; // Default size for unknown types
      }
    }
    return size;
  }

  /**
   * Returns the metrics channel name.
   *
   * @return The metrics channel name
   */
  public static String getMetricsChannel() {
    return METRICS_CHANNEL;
  }

  /** Metrics for a channel. */
  private static class ChannelMetrics {
    private final LongAdder messageCount = new LongAdder();
    private final LongAdder byteCount = new LongAdder();
    private final LongAdder errorCount = new LongAdder();
    private final LongAdder totalProcessingTime = new LongAdder();
    private Instant lastMessageTime = null;
    private Instant lastErrorTime = null;
    private String lastErrorMessage = null;
  }

  /** Metrics for a component. */
  private static class ComponentMetrics {
    private final AtomicLong messagesSent = new AtomicLong(0);
    private final AtomicLong messagesReceived = new AtomicLong(0);
    private final LongAdder bytesSent = new LongAdder();
    private final LongAdder bytesReceived = new LongAdder();
    private final AtomicLong errorCount = new AtomicLong(0);
    private boolean isActive = false;
    private Instant lastStateChangeTime = null;
    private Instant lastErrorTime = null;
    private String lastErrorMessage = null;
  }
}
