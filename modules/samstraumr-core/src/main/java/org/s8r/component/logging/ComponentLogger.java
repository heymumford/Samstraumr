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

package org.s8r.component.logging;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.s8r.component.Logger;

/**
 * Manages logging for a component, providing both structured logging and in-memory log retention.
 *
 * <p>This class handles all logging concerns for a component, including:
 *
 * <ul>
 *   <li>Structured logging with tags (via Logger)
 *   <li>In-memory log retention for introspection
 *   <li>Timestamped log entries
 * </ul>
 *
 * <p><b>Refactoring Note:</b> Extracted from Component.java as part of Phase 2 God Class
 * decomposition (Martin Fowler refactoring).
 */
public class ComponentLogger {
  private final String componentId;
  private final Logger logger;
  private final List<String> memoryLog;

  /**
   * Creates a new component logger.
   *
   * @param componentId The unique identifier of the component (used for log context)
   */
  public ComponentLogger(String componentId) {
    this.componentId = Objects.requireNonNull(componentId, "Component ID cannot be null");
    this.logger = new Logger(componentId);
    this.memoryLog = Collections.synchronizedList(new LinkedList<>());
  }

  /**
   * Logs an informational message.
   *
   * @param message The message to log
   * @param tags Optional tags for categorization
   */
  public void info(String message, String... tags) {
    logger.info(message, tags);
    logToMemory(message);
  }

  /**
   * Logs a debug message.
   *
   * @param message The message to log
   * @param tags Optional tags for categorization
   */
  public void debug(String message, String... tags) {
    logger.debug(message, tags);
    logToMemory(message);
  }

  /**
   * Logs an error message.
   *
   * @param message The message to log
   * @param tags Optional tags for categorization
   */
  public void error(String message, String... tags) {
    logger.error(message, tags);
    logToMemory(message);
  }

  /**
   * Logs a warning message.
   *
   * @param message The message to log
   * @param tags Optional tags for categorization
   */
  public void warn(String message, String... tags) {
    logger.warn(message, tags);
    logToMemory(message);
  }

  /**
   * Adds a message to the in-memory log with timestamp.
   *
   * @param message The message to log
   */
  private void logToMemory(String message) {
    String logEntry = Instant.now() + " - " + message;
    memoryLog.add(logEntry);
  }

  /**
   * Gets the in-memory log entries.
   *
   * @return An unmodifiable view of the memory log
   */
  public List<String> getMemoryLog() {
    return Collections.unmodifiableList(memoryLog);
  }

  /**
   * Gets the size of the memory log.
   *
   * @return The number of log entries
   */
  public int getMemoryLogSize() {
    return memoryLog.size();
  }

  /**
   * Gets the underlying Logger instance.
   *
   * @return The Logger
   */
  public Logger getLogger() {
    return logger;
  }

  /** Clears the in-memory log. */
  public void clearMemoryLog() {
    memoryLog.clear();
  }
}
