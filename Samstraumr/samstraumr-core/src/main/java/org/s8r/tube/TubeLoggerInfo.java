/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Implementation of the TubeLoggerInfo concept in the Samstraumr framework
 *
 * This class implements the core functionality for TubeLoggerInfo in the Samstraumr
 * tube-based processing framework. It provides the essential infrastructure for
 * the tube ecosystem to maintain its hierarchical design and data processing capabilities.
 *
 * Key features:
 * - Implementation of the TubeLoggerInfo concept
 * - Integration with the tube substrate model
 * - Support for hierarchical tube organization
 */

package org.s8r.tube;

// Standard Java imports
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class TubeLoggerInfo {
  private final String tubeId;
  private final String compositeId;
  private final String machineId;
  private final AtomicLong sequenceNumber;
  private final String visualHash;

  public TubeLoggerInfo(String tubeId, String compositeId, String machineId) {
    this.tubeId = tubeId;
    this.compositeId = compositeId;
    this.machineId = machineId;
    this.sequenceNumber = new AtomicLong(0);
    this.visualHash = generateVisualHash(tubeId);
  }

  public Map<String, Object> assembleLogInfo(
      String message, String level, String[] tags, Map<String, Object> additionalContext) {
    Map<String, Object> logInfo = new HashMap<>();
    logInfo.put("tubeId", tubeId);
    logInfo.put("compositeId", compositeId);
    logInfo.put("machineId", machineId);
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

  private String generateVisualHash(String id) {
    // Using a positive long value to avoid Integer.MIN_VALUE issue when taking absolute value
    long hash = id.hashCode() & 0xFFFFFFFFL;
    Color color =
        new Color((int) (hash % 255), (int) ((hash >> 8) % 255), (int) ((hash >> 16) % 255));
    return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
  }

  public String getTubeId() {
    return tubeId;
  }

  public String getCompositeId() {
    return compositeId;
  }

  public String getMachineId() {
    return machineId;
  }

  public long getNextSequenceNumber() {
    return sequenceNumber.get();
  }

  public String getVisualHash() {
    return visualHash;
  }
}
