/**
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://github.com/heymumford/Samstraumr/blob/main/LICENSE
 *
 * Implementation of the unified Logger for components in the S8r framework
 */

package org.s8r.component.logging;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.LoggerFactory;

/**
 * Provides enhanced logging functionality for components.
 *
 * <p>This class offers logging capabilities with component-specific contextual information and
 * categorization through tags. It's part of the simplified package structure, replacing the 
 * combination of TubeLogger and TubeLoggerInfo with a single, integrated class.
 */
public class Logger {
  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Logger.class);
  private final LoggerInfo info;

  /**
   * Creates a new Logger with the specified component identifiers.
   *
   * @param componentId the unique identifier of the component
   * @param compositeId the identifier of the composite this component belongs to (optional)
   * @param machineId the identifier of the machine this component is part of (optional)
   */
  public Logger(String componentId, String compositeId, String machineId) {
    this.info = new LoggerInfo(componentId, compositeId, machineId);
  }

  /**
   * Creates a new Logger with just a component identifier.
   *
   * @param componentId the unique identifier of the component
   */
  public Logger(String componentId) {
    this(componentId, null, null);
  }

  /**
   * Logs a message with the specified level and tags.
   *
   * @param level the log level ("debug", "info", "warn", "error")
   * @param message the message to log
   * @param tags optional tags for categorizing the log entry
   */
  public void log(String level, String message, String... tags) {
    String formattedMessage = formatMessage(message, tags);

    switch (level.toLowerCase()) {
      case "debug":
        LOGGER.debug(formattedMessage);
        break;
      case "info":
        LOGGER.info(formattedMessage);
        break;
      case "warn":
        LOGGER.warn(formattedMessage);
        break;
      case "error":
        LOGGER.error(formattedMessage);
        break;
      default:
        LOGGER.info(formattedMessage);
    }
  }

  /**
   * Logs a message with the specified level, context, and tags. This method provides structured
   * logging capabilities with additional context.
   *
   * @param level the log level ("debug", "info", "warn", "error")
   * @param message the message to log
   * @param context additional contextual information as key-value pairs
   * @param tags optional tags for categorizing the log entry
   */
  public void logWithContext(
      String level, String message, Map<String, Object> context, String... tags) {
    String contextString = formatContext(context);
    String formattedMessage = formatMessage(message + " " + contextString, tags);

    switch (level.toLowerCase()) {
      case "debug":
        LOGGER.debug(formattedMessage);
        break;
      case "info":
        LOGGER.info(formattedMessage);
        break;
      case "warn":
        LOGGER.warn(formattedMessage);
        break;
      case "error":
        LOGGER.error(formattedMessage);
        break;
      default:
        LOGGER.info(formattedMessage);
    }
  }

  /**
   * Logs a message at the INFO level.
   *
   * @param message the message to log
   * @param tags optional tags for categorizing the log entry
   */
  public void info(String message, String... tags) {
    log("info", message, tags);
  }

  /**
   * Logs a message at the DEBUG level.
   *
   * @param message the message to log
   * @param tags optional tags for categorizing the log entry
   */
  public void debug(String message, String... tags) {
    log("debug", message, tags);
  }

  /**
   * Logs a message at the WARN level.
   *
   * @param message the message to log
   * @param tags optional tags for categorizing the log entry
   */
  public void warn(String message, String... tags) {
    log("warn", message, tags);
  }

  /**
   * Logs a message at the ERROR level.
   *
   * @param message the message to log
   * @param tags optional tags for categorizing the log entry
   */
  public void error(String message, String... tags) {
    log("error", message, tags);
  }

  /**
   * Gets the logger info object containing component identifiers.
   *
   * @return the logger info object
   */
  public LoggerInfo getLoggerInfo() {
    return info;
  }

  /**
   * Formats a message with component identifiers and tags.
   *
   * @param message the original message
   * @param tags optional tags for categorizing the log entry
   * @return the formatted message
   */
  private String formatMessage(String message, String... tags) {
    StringBuilder sb = new StringBuilder();

    // Add component identifiers
    sb.append("[Component:").append(info.getComponentId()).append("]");

    if (info.getCompositeId() != null) {
      sb.append("[Composite:").append(info.getCompositeId()).append("]");
    }

    if (info.getMachineId() != null) {
      sb.append("[Machine:").append(info.getMachineId()).append("]");
    }

    // Add tags if present
    if (tags != null && tags.length > 0) {
      for (String tag : tags) {
        sb.append("[").append(tag).append("]");
      }
    }

    // Add the message
    sb.append(" ").append(message);

    return sb.toString();
  }

  /**
   * Formats context information as a string.
   *
   * @param context the context as key-value pairs
   * @return formatted context string
   */
  private String formatContext(Map<String, Object> context) {
    if (context == null || context.isEmpty()) {
      return "";
    }

    StringBuilder sb = new StringBuilder();
    sb.append("{");

    boolean first = true;
    for (Map.Entry<String, Object> entry : context.entrySet()) {
      if (!first) {
        sb.append(", ");
      }
      sb.append(entry.getKey()).append("=").append(entry.getValue());
      first = false;
    }

    sb.append("}");
    return sb.toString();
  }

  /**
   * Contains metadata for component logging, including identifiers and visual cues.
   * 
   * <p>This inner class integrates what was previously a separate LoggerInfo class,
   * maintaining all the same functionality while reducing the number of separate files.
   */
  public static class LoggerInfo {
    private final String componentId;
    private final String compositeId;
    private final String machineId;
    private final AtomicLong sequenceNumber;
    private final String visualHash;

    /**
     * Creates a new LoggerInfo with the specified component identifiers.
     *
     * @param componentId The unique identifier for this component
     * @param compositeId The identifier of the composite this component belongs to (optional)
     * @param machineId The identifier of the machine this component is part of (optional)
     */
    LoggerInfo(String componentId, String compositeId, String machineId) {
      this.componentId = componentId;
      this.compositeId = compositeId;
      this.machineId = machineId;
      this.sequenceNumber = new AtomicLong(0);
      this.visualHash = generateVisualHash(componentId);
    }

    /**
     * Assembles comprehensive log information including all available context.
     *
     * @param message The log message
     * @param level The log level
     * @param tags Optional categorization tags
     * @param additionalContext Additional contextual information
     * @return A map containing all log information
     */
    public Map<String, Object> assembleLogInfo(
        String message, String level, String[] tags, Map<String, Object> additionalContext) {
      Map<String, Object> logInfo = new HashMap<>();
      logInfo.put("componentId", componentId);
      
      if (compositeId != null) {
        logInfo.put("compositeId", compositeId);
      }
      
      if (machineId != null) {
        logInfo.put("machineId", machineId);
      }
      
      logInfo.put("message", message);
      logInfo.put("level", level);
      logInfo.put("sequenceNumber", sequenceNumber.getAndIncrement());
      logInfo.put("tags", tags);
      logInfo.put("visualHash", visualHash);
      logInfo.put("timestamp", System.currentTimeMillis());

      if (additionalContext != null) {
        logInfo.putAll(additionalContext);
      }

      return logInfo;
    }

    /**
     * Generates a color-based visual hash from a component ID.
     * 
     * <p>This method creates a consistent color representation based on the component ID,
     * allowing for quick visual identification of log entries from the same component.
     *
     * @param id The component ID to hash
     * @return A hex color code representing the component ID
     */
    private String generateVisualHash(String id) {
      // Using a positive long value to avoid Integer.MIN_VALUE issue when taking absolute value
      long hash = id.hashCode() & 0xFFFFFFFFL;
      Color color = 
          new Color((int) (hash % 255), (int) ((hash >> 8) % 255), (int) ((hash >> 16) % 255));
      return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Gets the component identifier.
     *
     * @return The component ID
     */
    public String getComponentId() {
      return componentId;
    }

    /**
     * Gets the composite identifier, if any.
     *
     * @return The composite ID or null
     */
    public String getCompositeId() {
      return compositeId;
    }

    /**
     * Gets the machine identifier, if any.
     *
     * @return The machine ID or null
     */
    public String getMachineId() {
      return machineId;
    }

    /**
     * Gets the current sequence number.
     *
     * @return The current sequence number
     */
    public long getSequenceNumber() {
      return sequenceNumber.get();
    }

    /**
     * Gets the visual hash representation of the component ID.
     *
     * @return A hex color code representing the component ID
     */
    public String getVisualHash() {
      return visualHash;
    }
  }
}