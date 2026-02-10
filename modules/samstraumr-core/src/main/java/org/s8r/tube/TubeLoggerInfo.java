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
