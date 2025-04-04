/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Implementation of the Logger concept in the S8r framework
 */

package org.s8r.core.tube.logging;

import java.util.Map;

import org.slf4j.LoggerFactory;

/**
 * Provides enhanced logging functionality for components.
 *
 * <p>This class offers logging capabilities with component-specific contextual information and
 * categorization through tags. It's part of the simplified package structure, replacing the more
 * specific TubeLogger with a more general Logger class in the s8r.core.tube.logging package.
 */
public class Logger {
  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Logger.class);
  private final LoggerInfo loggerInfo;

  /**
   * Creates a new Logger with the specified component identifiers.
   *
   * @param componentId the unique identifier of the component
   * @param compositeId the identifier of the composite this component belongs to (optional)
   * @param machineId the identifier of the machine this component is part of (optional)
   */
  public Logger(String componentId, String compositeId, String machineId) {
    this.loggerInfo = new LoggerInfo(componentId, compositeId, machineId);
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
    return loggerInfo;
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
    sb.append("[Component:").append(loggerInfo.getComponentId()).append("]");

    if (loggerInfo.getCompositeId() != null) {
      sb.append("[Composite:").append(loggerInfo.getCompositeId()).append("]");
    }

    if (loggerInfo.getMachineId() != null) {
      sb.append("[Machine:").append(loggerInfo.getMachineId()).append("]");
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
}