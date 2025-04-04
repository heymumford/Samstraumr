/*
 * Implementation of the LoggerInfo concept in the S8r framework
 *
 * This class implements the core functionality for component logging in the S8r
 * framework. It provides the essential infrastructure for tracking and identifying
 * log entries from components.
 */

package org.s8r.core.tube.logging;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Contains metadata for component logging, including identifiers and visual cues.
 *
 * <p>This class is part of the simplified package structure, replacing the more specific
 * TubeLoggerInfo with a more general LoggerInfo class in the s8r.core.tube.logging package.
 *
 * <p>It maintains component identifiers, sequence numbers for log entries, and visual
 * representations of the component identity to aid in log analysis and visualization.
 */
public class LoggerInfo {
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
  public LoggerInfo(String componentId, String compositeId, String machineId) {
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
   * <p>This method creates a consistent color representation based on the component ID, allowing
   * for quick visual identification of log entries from the same component.
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
  public long getNextSequenceNumber() {
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
