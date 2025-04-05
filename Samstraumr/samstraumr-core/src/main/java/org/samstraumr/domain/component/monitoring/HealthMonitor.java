/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Health monitor for the S8r framework
 */

package org.samstraumr.domain.component.monitoring;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.samstraumr.domain.component.Component;
import org.samstraumr.domain.event.ComponentDataEvent;
import org.samstraumr.domain.identity.ComponentId;

/**
 * Component that monitors the health of the system.
 *
 * <p>HealthMonitor tracks component availability, process heartbeats, and system resources,
 * providing health status and alerts when issues occur.
 */
public class HealthMonitor extends Component {
  private static final String HEALTH_CHANNEL = "system.health";
  private static final String ALERT_CHANNEL = "system.alerts";
  private static final org.slf4j.Logger LOGGER =
      org.slf4j.LoggerFactory.getLogger(HealthMonitor.class);

  /** Health status values. */
  public enum HealthStatus {
    /** Component is healthy and functioning normally. */
    HEALTHY,

    /** Component has minor issues but is still operational. */
    DEGRADED,

    /** Component has critical issues and is not functioning properly. */
    UNHEALTHY,

    /** Component status is unknown or hasn't reported recently. */
    UNKNOWN
  }

  /** Alert level values. */
  public enum AlertLevel {
    /** Informational alert, no action required. */
    INFO,

    /** Warning alert, may require attention. */
    WARNING,

    /** Error alert, requires attention. */
    ERROR,

    /** Critical alert, requires immediate attention. */
    CRITICAL
  }

  private final Map<ComponentId, ComponentHealth> componentHealth = new ConcurrentHashMap<>();
  private final Map<String, ChannelHealth> channelHealth = new ConcurrentHashMap<>();
  private final Map<String, ResourceMonitor> resourceMonitors = new ConcurrentHashMap<>();
  private final Duration publishInterval;
  private final Duration timeoutThreshold;
  private Instant lastPublishTime;

  /**
   * Creates a new health monitor.
   *
   * @param id The component ID
   * @param publishInterval The interval at which to publish health status
   * @param timeoutThreshold The threshold after which a component is considered unresponsive
   */
  private HealthMonitor(ComponentId id, Duration publishInterval, Duration timeoutThreshold) {
    super(id);
    this.publishInterval = publishInterval;
    this.timeoutThreshold = timeoutThreshold;
    this.lastPublishTime = Instant.now();

    logActivity(
        "Health monitor created with publish interval "
            + publishInterval
            + " and timeout threshold "
            + timeoutThreshold);
  }

  /**
   * Logs an activity related to this health monitor.
   *
   * @param message The message to log
   */
  @Override
  protected void logActivity(String message) {
    String logEntry = Instant.now() + " - " + message;
    // Add to activity log in parent class
    super.logActivity(message);
    // Also log to the SLF4J logger
    LOGGER.debug(logEntry);
  }

  /**
   * Creates a new health monitor.
   *
   * @param reason The reason for creating this component
   * @param publishInterval The interval at which to publish health status
   * @param timeoutThreshold The threshold after which a component is considered unresponsive
   * @return A new health monitor
   */
  public static HealthMonitor create(
      String reason, Duration publishInterval, Duration timeoutThreshold) {
    ComponentId id = ComponentId.create(reason);
    return new HealthMonitor(id, publishInterval, timeoutThreshold);
  }

  /**
   * Registers a component for health monitoring.
   *
   * @param componentId The component ID
   * @param description A description of the component
   */
  public void registerComponent(ComponentId componentId, String description) {
    ComponentHealth health = new ComponentHealth();
    health.description = description;
    health.status = HealthStatus.UNKNOWN;
    health.lastUpdated = Instant.now();

    componentHealth.put(componentId, health);
    logActivity("Registered component for health monitoring: " + componentId.getShortId());
  }

  /**
   * Registers a channel for health monitoring.
   *
   * @param channelName The channel name
   * @param description A description of the channel
   */
  public void registerChannel(String channelName, String description) {
    ChannelHealth health = new ChannelHealth();
    health.description = description;
    health.status = HealthStatus.UNKNOWN;
    health.lastUpdated = Instant.now();

    channelHealth.put(channelName, health);
    logActivity("Registered channel for health monitoring: " + channelName);
  }

  /**
   * Adds a resource monitor.
   *
   * @param name The resource name
   * @param supplier A supplier that returns the resource usage (0.0-1.0)
   * @param criticalThreshold The threshold above which the resource is in critical state (0.0-1.0)
   * @param warningThreshold The threshold above which the resource is in warning state (0.0-1.0)
   */
  public void addResourceMonitor(
      String name, Supplier<Double> supplier, double criticalThreshold, double warningThreshold) {
    ResourceMonitor monitor = new ResourceMonitor();
    monitor.name = name;
    monitor.supplier = supplier;
    monitor.criticalThreshold = criticalThreshold;
    monitor.warningThreshold = warningThreshold;

    resourceMonitors.put(name, monitor);
    logActivity("Added resource monitor: " + name);
  }

  /**
   * Updates the health status of a component.
   *
   * @param componentId The component ID
   * @param status The health status
   * @param details Additional details about the health status
   */
  public void updateComponentHealth(ComponentId componentId, HealthStatus status, String details) {
    ComponentHealth health =
        componentHealth.computeIfAbsent(componentId, k -> new ComponentHealth());

    // Record previous status for change detection
    HealthStatus previousStatus = health.status;

    // Update health status
    health.status = status;
    health.details = details;
    health.lastUpdated = Instant.now();

    // Check if status changed to unhealthy
    if (previousStatus != HealthStatus.UNHEALTHY && status == HealthStatus.UNHEALTHY) {
      publishAlert(
          AlertLevel.ERROR,
          "Component " + componentId.getShortId() + " is now UNHEALTHY: " + details);
    }

    // Check if status changed to degraded
    if (previousStatus == HealthStatus.HEALTHY && status == HealthStatus.DEGRADED) {
      publishAlert(
          AlertLevel.WARNING,
          "Component " + componentId.getShortId() + " is now DEGRADED: " + details);
    }

    // Check if status recovered
    if ((previousStatus == HealthStatus.UNHEALTHY || previousStatus == HealthStatus.DEGRADED)
        && status == HealthStatus.HEALTHY) {
      publishAlert(
          AlertLevel.INFO,
          "Component " + componentId.getShortId() + " has recovered to HEALTHY state");
    }

    // Check if it's time to publish health status
    checkAndPublishHealth();
  }

  /**
   * Updates the health status of a channel.
   *
   * @param channelName The channel name
   * @param status The health status
   * @param details Additional details about the health status
   */
  public void updateChannelHealth(String channelName, HealthStatus status, String details) {
    ChannelHealth health = channelHealth.computeIfAbsent(channelName, k -> new ChannelHealth());

    // Record previous status for change detection
    HealthStatus previousStatus = health.status;

    // Update health status
    health.status = status;
    health.details = details;
    health.lastUpdated = Instant.now();

    // Check if status changed to unhealthy
    if (previousStatus != HealthStatus.UNHEALTHY && status == HealthStatus.UNHEALTHY) {
      publishAlert(AlertLevel.ERROR, "Channel " + channelName + " is now UNHEALTHY: " + details);
    }

    // Check if status changed to degraded
    if (previousStatus == HealthStatus.HEALTHY && status == HealthStatus.DEGRADED) {
      publishAlert(AlertLevel.WARNING, "Channel " + channelName + " is now DEGRADED: " + details);
    }

    // Check if status recovered
    if ((previousStatus == HealthStatus.UNHEALTHY || previousStatus == HealthStatus.DEGRADED)
        && status == HealthStatus.HEALTHY) {
      publishAlert(AlertLevel.INFO, "Channel " + channelName + " has recovered to HEALTHY state");
    }

    // Check if it's time to publish health status
    checkAndPublishHealth();
  }

  /**
   * Records a heartbeat from a component to indicate it's still alive.
   *
   * @param componentId The component ID
   */
  public void recordHeartbeat(ComponentId componentId) {
    ComponentHealth health =
        componentHealth.computeIfAbsent(componentId, k -> new ComponentHealth());

    // Update last heartbeat time
    health.lastHeartbeat = Instant.now();

    // If status was unknown, set to healthy
    if (health.status == HealthStatus.UNKNOWN) {
      health.status = HealthStatus.HEALTHY;
      publishAlert(
          AlertLevel.INFO, "Component " + componentId.getShortId() + " is now online and HEALTHY");
    }
  }

  /**
   * Records activity on a channel to indicate it's still active.
   *
   * @param channelName The channel name
   */
  public void recordChannelActivity(String channelName) {
    ChannelHealth health = channelHealth.computeIfAbsent(channelName, k -> new ChannelHealth());

    // Update last activity time
    health.lastActivity = Instant.now();

    // If status was unknown, set to healthy
    if (health.status == HealthStatus.UNKNOWN) {
      health.status = HealthStatus.HEALTHY;
      publishAlert(AlertLevel.INFO, "Channel " + channelName + " is now active and HEALTHY");
    }
  }

  /** Checks if it's time to publish health status and publishes if necessary. */
  private void checkAndPublishHealth() {
    Instant now = Instant.now();
    if (Duration.between(lastPublishTime, now).compareTo(publishInterval) >= 0) {
      publishHealth();
    }
  }

  /** Publishes the current health status. */
  public void publishHealth() {
    Map<String, Object> healthData = new HashMap<>();
    Instant now = Instant.now();

    // Check for unresponsive components
    for (Map.Entry<ComponentId, ComponentHealth> entry : componentHealth.entrySet()) {
      ComponentId componentId = entry.getKey();
      ComponentHealth health = entry.getValue();

      // Check if component hasn't sent a heartbeat recently
      if (health.lastHeartbeat != null
          && Duration.between(health.lastHeartbeat, now).compareTo(timeoutThreshold) > 0) {

        // If not already marked as unhealthy, update status
        if (health.status != HealthStatus.UNHEALTHY) {
          health.status = HealthStatus.UNHEALTHY;
          health.details =
              "Component hasn't sent a heartbeat in "
                  + Duration.between(health.lastHeartbeat, now).toSeconds()
                  + " seconds";

          publishAlert(
              AlertLevel.ERROR,
              "Component "
                  + componentId.getShortId()
                  + " appears to be unresponsive: "
                  + health.details);
        }
      }
    }

    // Check for inactive channels
    for (Map.Entry<String, ChannelHealth> entry : channelHealth.entrySet()) {
      String channelName = entry.getKey();
      ChannelHealth health = entry.getValue();

      // Check if channel hasn't had activity recently
      if (health.lastActivity != null
          && Duration.between(health.lastActivity, now).compareTo(timeoutThreshold) > 0) {

        // If not already marked as unhealthy, update status
        if (health.status != HealthStatus.UNHEALTHY) {
          health.status = HealthStatus.UNHEALTHY;
          health.details =
              "Channel hasn't had activity in "
                  + Duration.between(health.lastActivity, now).toSeconds()
                  + " seconds";

          publishAlert(
              AlertLevel.ERROR,
              "Channel " + channelName + " appears to be inactive: " + health.details);
        }
      }
    }

    // Add component health data
    Map<String, Map<String, Object>> componentHealthMap = new HashMap<>();
    for (Map.Entry<ComponentId, ComponentHealth> entry : componentHealth.entrySet()) {
      ComponentId componentId = entry.getKey();
      ComponentHealth health = entry.getValue();

      Map<String, Object> healthMap = new HashMap<>();
      healthMap.put("status", health.status.name());
      healthMap.put("details", health.details);
      healthMap.put("description", health.description);
      healthMap.put("lastUpdated", health.lastUpdated);
      healthMap.put("lastHeartbeat", health.lastHeartbeat);

      componentHealthMap.put(componentId.getIdString(), healthMap);
    }
    healthData.put("components", componentHealthMap);

    // Add channel health data
    Map<String, Map<String, Object>> channelHealthMap = new HashMap<>();
    for (Map.Entry<String, ChannelHealth> entry : channelHealth.entrySet()) {
      String channelName = entry.getKey();
      ChannelHealth health = entry.getValue();

      Map<String, Object> healthMap = new HashMap<>();
      healthMap.put("status", health.status.name());
      healthMap.put("details", health.details);
      healthMap.put("description", health.description);
      healthMap.put("lastUpdated", health.lastUpdated);
      healthMap.put("lastActivity", health.lastActivity);

      channelHealthMap.put(channelName, healthMap);
    }
    healthData.put("channels", channelHealthMap);

    // Add resource health data
    Map<String, Map<String, Object>> resourceHealthMap = new HashMap<>();
    for (Map.Entry<String, ResourceMonitor> entry : resourceMonitors.entrySet()) {
      String resourceName = entry.getKey();
      ResourceMonitor monitor = entry.getValue();

      // Get current resource usage
      double usage = monitor.supplier.get();

      // Determine status based on thresholds
      HealthStatus status;
      if (usage >= monitor.criticalThreshold) {
        status = HealthStatus.UNHEALTHY;
        publishAlert(
            AlertLevel.CRITICAL,
            "Resource " + resourceName + " is in CRITICAL state: " + (usage * 100) + "% usage");
      } else if (usage >= monitor.warningThreshold) {
        status = HealthStatus.DEGRADED;
        publishAlert(
            AlertLevel.WARNING,
            "Resource " + resourceName + " is in WARNING state: " + (usage * 100) + "% usage");
      } else {
        status = HealthStatus.HEALTHY;
      }

      Map<String, Object> healthMap = new HashMap<>();
      healthMap.put("status", status.name());
      healthMap.put("usage", usage);
      healthMap.put("warningThreshold", monitor.warningThreshold);
      healthMap.put("criticalThreshold", monitor.criticalThreshold);

      resourceHealthMap.put(resourceName, healthMap);
    }
    healthData.put("resources", resourceHealthMap);

    // Add system-wide health status
    Map<String, Object> systemHealth = new HashMap<>();
    systemHealth.put("timestamp", now);

    // Determine overall system status (worst status of any component or resource)
    HealthStatus systemStatus = HealthStatus.HEALTHY;

    for (ComponentHealth health : componentHealth.values()) {
      if (health.status == HealthStatus.UNHEALTHY) {
        systemStatus = HealthStatus.UNHEALTHY;
        break;
      } else if (health.status == HealthStatus.DEGRADED && systemStatus != HealthStatus.UNHEALTHY) {
        systemStatus = HealthStatus.DEGRADED;
      }
    }

    for (ResourceMonitor monitor : resourceMonitors.values()) {
      double usage = monitor.supplier.get();
      if (usage >= monitor.criticalThreshold && systemStatus != HealthStatus.UNHEALTHY) {
        systemStatus = HealthStatus.UNHEALTHY;
        break;
      } else if (usage >= monitor.warningThreshold && systemStatus != HealthStatus.UNHEALTHY) {
        systemStatus = HealthStatus.DEGRADED;
      }
    }

    systemHealth.put("status", systemStatus.name());
    systemHealth.put("componentCount", componentHealth.size());
    systemHealth.put("channelCount", channelHealth.size());
    systemHealth.put("resourceCount", resourceMonitors.size());

    healthData.put("system", systemHealth);

    // Publish health data
    publishData(HEALTH_CHANNEL, healthData);
    logActivity("Published health status to channel: " + HEALTH_CHANNEL);

    lastPublishTime = now;
  }

  /**
   * Publishes an alert.
   *
   * @param level The alert level
   * @param message The alert message
   */
  public void publishAlert(AlertLevel level, String message) {
    Map<String, Object> alertData = new HashMap<>();
    alertData.put("level", level.name());
    alertData.put("message", message);
    alertData.put("timestamp", Instant.now());
    alertData.put("source", getId().getShortId());

    publishData(ALERT_CHANNEL, alertData);
    logActivity("Published " + level + " alert: " + message);
  }

  /**
   * Creates a consumer for checking component health via events.
   *
   * @param channelName The channel name
   * @param componentId The component ID
   * @param nextHandler The next handler in the chain
   * @return A consumer that monitors health and passes events to the next handler
   */
  public Consumer<ComponentDataEvent> createHealthConsumer(
      String channelName, ComponentId componentId, Consumer<ComponentDataEvent> nextHandler) {

    return event -> {
      try {
        // Process the event
        nextHandler.accept(event);

        // Record heartbeat for the component
        recordHeartbeat(componentId);

        // Record activity on the channel
        recordChannelActivity(channelName);

        // Component is healthy
        updateComponentHealth(componentId, HealthStatus.HEALTHY, "Processing events normally");

      } catch (Exception e) {
        // Update component health to unhealthy
        updateComponentHealth(
            componentId, HealthStatus.UNHEALTHY, "Error processing event: " + e.getMessage());

        // Re-throw the exception
        throw e;
      }
    };
  }

  /**
   * Returns the health channel name.
   *
   * @return The health channel name
   */
  public static String getHealthChannel() {
    return HEALTH_CHANNEL;
  }

  /**
   * Returns the alert channel name.
   *
   * @return The alert channel name
   */
  public static String getAlertChannel() {
    return ALERT_CHANNEL;
  }

  /** Health status for a component. */
  private static class ComponentHealth {
    private HealthStatus status = HealthStatus.UNKNOWN;
    private String details;
    private String description;
    private Instant lastUpdated;
    private Instant lastHeartbeat;
  }

  /** Health status for a channel. */
  private static class ChannelHealth {
    private HealthStatus status = HealthStatus.UNKNOWN;
    private String details;
    private String description;
    private Instant lastUpdated;
    private Instant lastActivity;
  }

  /** Monitor for a system resource. */
  private static class ResourceMonitor {
    private String name;
    private Supplier<Double> supplier;
    private double criticalThreshold;
    private double warningThreshold;
  }
}
