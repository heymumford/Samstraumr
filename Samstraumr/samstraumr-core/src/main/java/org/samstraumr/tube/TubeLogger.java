/*
Filename: TubeLogger.java
Purpose: Provides standardized logging capabilities for Tube instances with contextual information.
Goals:
  - Ensure that logging includes consistent tube-specific identifiers
  - Ensure that log levels are properly mapped to SLF4J implementation
  - Ensure that contextual information can be attached to log entries
Dependencies:
  - org.slf4j: For underlying logging implementation
  - TubeLoggerInfo: For structured logging data assembly
Assumptions:
  - SLF4J is properly configured in the runtime environment
  - Log messages follow a consistent format across the application
  - Logger instance is thread-safe for concurrent tube operations
*/

package org.samstraumr.tube;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TubeLogger {
  private static final Logger LOGGER = LoggerFactory.getLogger(TubeLogger.class);
  private final TubeLoggerInfo loggerInfo;

  public TubeLogger(String tubeId, String compositeId, String machineId) {
    this.loggerInfo = new TubeLoggerInfo(tubeId, compositeId, machineId);
  }

  public void log(String level, String message, String... tags) {
    logWithContext(level, message, null, tags);
  }

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
      default:
        LOGGER.info("{}", logEntry);
    }
  }
}
