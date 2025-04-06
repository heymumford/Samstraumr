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
import java.util.function.Consumer;

import org.s8r.domain.component.pattern.DataFlowPort;
import org.s8r.domain.component.Component;
import org.s8r.domain.event.ComponentDataEvent;

/**
 * Factory for creating and setting up monitoring components.
 *
 * <p>This factory simplifies the creation and configuration of monitoring components like metrics
 * collectors and health monitors.
 */
public class MonitoringFactory {
  private final DataFlowPort dataFlowPort;

  /**
   * Creates a new monitoring factory.
   *
   * @param dataFlowPort The data flow port to use for component communication
   */
  public MonitoringFactory(DataFlowPort dataFlowPort) {
    this.dataFlowPort = dataFlowPort;
  }

  /**
   * Creates a metrics collector component and subscribes it to system-wide metrics.
   *
   * @param reason The reason for creating the component
   * @param publishInterval The interval at which to publish metrics
   * @return The created metrics collector
   */
  public MetricsCollector createMetricsCollector(String reason, Duration publishInterval) {
    MetricsCollector collector = MetricsCollector.create(reason, publishInterval);

    // Subscribe to metrics channel to receive metrics from other components
    dataFlowPort.subscribe(
        collector.getId(),
        MetricsCollector.getMetricsChannel(),
        event -> {
          // Just receiving metrics - no need to process
        });

    return collector;
  }

  /**
   * Creates a health monitor component and subscribes it to system-wide health and alerts.
   *
   * @param reason The reason for creating the component
   * @param publishInterval The interval at which to publish health status
   * @param timeoutThreshold The threshold after which a component is considered unresponsive
   * @return The created health monitor
   */
  public HealthMonitor createHealthMonitor(
      String reason, Duration publishInterval, Duration timeoutThreshold) {
    HealthMonitor monitor = HealthMonitor.create(reason, publishInterval, timeoutThreshold);

    // Subscribe to health channel to receive health updates from other components
    dataFlowPort.subscribe(
        monitor.getId(),
        HealthMonitor.getHealthChannel(),
        event -> {
          // Just receiving health updates - no need to process
        });

    // Subscribe to alert channel to receive alerts from other components
    dataFlowPort.subscribe(
        monitor.getId(),
        HealthMonitor.getAlertChannel(),
        event -> {
          // Just receiving alerts - no need to process
        });

    return monitor;
  }

  /**
   * Adds a JVM resource monitor to a health monitor.
   *
   * @param monitor The health monitor
   */
  public void addJvmResourceMonitors(HealthMonitor monitor) {
    // Add memory monitor
    monitor.addResourceMonitor(
        "jvm.memory",
        () -> {
          Runtime runtime = Runtime.getRuntime();
          long maxMemory = runtime.maxMemory();
          long allocatedMemory = runtime.totalMemory();
          long freeMemory = runtime.freeMemory();

          // Calculate used memory and usage ratio
          long usedMemory = allocatedMemory - freeMemory;
          return (double) usedMemory / maxMemory;
        },
        0.95,
        0.80);

    // Add CPU monitor (simple approximation using thread count)
    monitor.addResourceMonitor(
        "jvm.threads",
        () -> {
          ThreadGroup rootThreadGroup = Thread.currentThread().getThreadGroup();
          while (rootThreadGroup.getParent() != null) {
            rootThreadGroup = rootThreadGroup.getParent();
          }

          int activeThreadCount = rootThreadGroup.activeCount();

          // Very rough estimation: assume more than 100 threads is a problem
          return Math.min(activeThreadCount / 100.0, 1.0);
        },
        0.9,
        0.7);
  }

  /**
   * Creates a component that is monitored with both metrics and health.
   *
   * @param component The component to monitor
   * @param channelName The channel name
   * @param nextHandler The next handler in the chain
   * @param metricsCollector The metrics collector
   * @param healthMonitor The health monitor
   * @return A consumer that collects metrics and monitors health
   */
  public Consumer<ComponentDataEvent> createMonitoredComponent(
      Component component,
      String channelName,
      Consumer<ComponentDataEvent> nextHandler,
      MetricsCollector metricsCollector,
      HealthMonitor healthMonitor) {

    // Register the component and channel with the health monitor
    healthMonitor.registerComponent(component.getId(), component.getClass().getSimpleName());
    healthMonitor.registerChannel(
        channelName, "Channel for " + component.getClass().getSimpleName());

    // Create a health-monitoring wrapper
    Consumer<ComponentDataEvent> healthConsumer =
        healthMonitor.createHealthConsumer(channelName, component.getId(), nextHandler);

    // Create a metrics-collecting wrapper around the health consumer
    Consumer<ComponentDataEvent> metricsConsumer =
        metricsCollector.createMetricsConsumer(channelName, component.getId(), healthConsumer);

    // Subscribe the wrapped component to the channel
    dataFlowPort.subscribe(component.getId(), channelName, metricsConsumer);

    return metricsConsumer;
  }

  /**
   * Creates a system monitor that combines metrics and health monitoring.
   *
   * @param reason The reason for creating the monitor
   * @param metricsInterval The interval at which to publish metrics
   * @param healthInterval The interval at which to publish health status
   * @param timeoutThreshold The threshold after which a component is considered unresponsive
   * @return A tuple containing the metrics collector and health monitor
   */
  public SystemMonitor createSystemMonitor(
      String reason, Duration metricsInterval, Duration healthInterval, Duration timeoutThreshold) {

    // Create the metrics collector
    MetricsCollector metricsCollector =
        createMetricsCollector(reason + "-metrics", metricsInterval);

    // Create the health monitor
    HealthMonitor healthMonitor =
        createHealthMonitor(reason + "-health", healthInterval, timeoutThreshold);

    // Add JVM resource monitors
    addJvmResourceMonitors(healthMonitor);

    // Register the metrics collector with the health monitor
    healthMonitor.registerComponent(metricsCollector.getId(), "Metrics Collector");

    // Register the health monitor with itself
    healthMonitor.registerComponent(healthMonitor.getId(), "Health Monitor");

    return new SystemMonitor(metricsCollector, healthMonitor);
  }

  /** Tuple containing a metrics collector and health monitor. */
  public static class SystemMonitor {
    private final MetricsCollector metricsCollector;
    private final HealthMonitor healthMonitor;

    /**
     * Creates a new system monitor.
     *
     * @param metricsCollector The metrics collector
     * @param healthMonitor The health monitor
     */
    public SystemMonitor(MetricsCollector metricsCollector, HealthMonitor healthMonitor) {
      this.metricsCollector = metricsCollector;
      this.healthMonitor = healthMonitor;
    }

    /**
     * Gets the metrics collector.
     *
     * @return The metrics collector
     */
    public MetricsCollector getMetricsCollector() {
      return metricsCollector;
    }

    /**
     * Gets the health monitor.
     *
     * @return The health monitor
     */
    public HealthMonitor getHealthMonitor() {
      return healthMonitor;
    }

    /** Publishes current metrics and health status. */
    public void publishStatus() {
      metricsCollector.publishMetrics();
      healthMonitor.publishHealth();
    }

    /**
     * Creates a monitored component.
     *
     * @param component The component to monitor
     * @param channelName The channel name
     * @param handler The event handler
     * @return A consumer that monitors the component
     */
    public Consumer<ComponentDataEvent> monitorComponent(
        Component component, String channelName, Consumer<ComponentDataEvent> handler) {
      // Use instance method directly instead of static reference
      MetricsCollector metrics = this.metricsCollector;
      HealthMonitor health = this.healthMonitor;

      // Create a chain of monitors
      Consumer<ComponentDataEvent> healthConsumer =
          health.createHealthConsumer(channelName, component.getId(), handler);

      return metrics.createMetricsConsumer(channelName, component.getId(), healthConsumer);
    }
  }
}
