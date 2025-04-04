/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Implementation of the TubeLogger concept in the Samstraumr framework
 *
 * This class implements the core functionality for TubeLogger in the Samstraumr
 * tube-based processing framework. It provides the essential infrastructure for
 * the tube ecosystem to maintain its hierarchical design and data processing capabilities.
 *
 * Key features:
 * - Implementation of the TubeLogger concept
 * - Integration with the tube substrate model
 * - Support for hierarchical tube organization
 */

package org.s8r.tube.legacy.core;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Core logger implementation for the tube-based processing framework. Provides structured logging
 * capabilities with contextual information.
 */
public class TubeLogger {
  private static final Logger LOGGER = LoggerFactory.getLogger(TubeLogger.class);
  private final TubeLoggerInfo loggerInfo;

  public TubeLogger(String tubeId, String compositeId, String machineId) {
    this.loggerInfo = new TubeLoggerInfo(tubeId, compositeId, machineId);
  }

  /**
   * Logs a message with the specified level and tags.
   *
   * @param level the log level ("debug", "info", "warn", "error")
   * @param message the message to log
   * @param tags optional tags for categorizing the log entry
   */
  public void log(String level, String message, String... tags) {
    logWithContext(level, message, null, tags);
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
    Map<String, Object> logEntry = loggerInfo.assembleLogInfo(message, level, tags, context);

    switch (level.toLowerCase()) {
      case "debug":
        LOGGER.debug("{}", logEntry);
        break;
      case "info":
        LOGGER.info("{}", logEntry);
        break;
      case "warn":
        LOGGER.warn("{}", logEntry);
        break;
      case "error":
        LOGGER.error("{}", logEntry);
        break;
      case "trace":
        LOGGER.trace("{}", logEntry);
        break;
      default:
        LOGGER.info("{}", logEntry);
        LOGGER.warn("Unknown log level '{}' used, defaulting to INFO", level);
    }
  }
}
