/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Simple console implementation of LoggerPort for the S8r framework
 */

package org.s8r.infrastructure.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.s8r.application.port.LoggerPort;

/**
 * Simple console implementation of the LoggerPort interface.
 *
 * <p>This adapter provides logging to the console, following Clean Architecture principles by
 * implementing an application layer port. It's primarily used for simple applications or when SLF4J
 * is not available.
 */
public class ConsoleLogger implements LoggerPort {
  private static final DateTimeFormatter TIMESTAMP_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

  private final String name;
  private LogLevel minLevel = LogLevel.INFO;

  /**
   * Creates a new ConsoleLogger with the specified name.
   *
   * @param name The logger name
   */
  public ConsoleLogger(String name) {
    this.name = name;
  }

  /**
   * Creates a new ConsoleLogger for the specified class.
   *
   * @param clazz The class to log for
   */
  public ConsoleLogger(Class<?> clazz) {
    this.name = clazz.getSimpleName();
  }

  /**
   * Sets the minimum log level.
   *
   * @param level The minimum log level
   */
  public void setMinLevel(LogLevel level) {
    this.minLevel = level;
  }

  @Override
  public void debug(String message) {
    if (isLevelEnabled(LogLevel.DEBUG)) {
      log(LogLevel.DEBUG, message, null);
    }
  }

  @Override
  public void debug(String message, Throwable e) {
    if (isLevelEnabled(LogLevel.DEBUG)) {
      log(LogLevel.DEBUG, message, e);
    }
  }

  @Override
  public void info(String message) {
    if (isLevelEnabled(LogLevel.INFO)) {
      log(LogLevel.INFO, message, null);
    }
  }

  @Override
  public void info(String message, Throwable e) {
    if (isLevelEnabled(LogLevel.INFO)) {
      log(LogLevel.INFO, message, e);
    }
  }

  @Override
  public void warn(String message) {
    if (isLevelEnabled(LogLevel.WARN)) {
      log(LogLevel.WARN, message, null);
    }
  }

  @Override
  public void warn(String message, Throwable e) {
    if (isLevelEnabled(LogLevel.WARN)) {
      log(LogLevel.WARN, message, e);
    }
  }

  @Override
  public void error(String message) {
    if (isLevelEnabled(LogLevel.ERROR)) {
      log(LogLevel.ERROR, message, null);
    }
  }

  @Override
  public void error(String message, Throwable e) {
    if (isLevelEnabled(LogLevel.ERROR)) {
      log(LogLevel.ERROR, message, e);
    }
  }

  /**
   * Checks if the given log level is enabled.
   *
   * @param level The log level to check
   * @return true if the level is enabled, false otherwise
   */
  private boolean isLevelEnabled(LogLevel level) {
    return level.ordinal() >= minLevel.ordinal();
  }

  /**
   * Logs a message and optional exception at the specified level.
   *
   * @param level The log level
   * @param message The message to log
   * @param e The exception to log, or null if none
   */
  private void log(LogLevel level, String message, Throwable e) {
    StringBuilder builder = new StringBuilder();

    // Add timestamp
    builder.append(LocalDateTime.now().format(TIMESTAMP_FORMATTER));
    builder.append(" ");

    // Add level
    builder.append("[").append(level.name()).append("] ");

    // Add name
    builder.append("[").append(name).append("] ");

    // Add message
    builder.append(message);

    // Print to appropriate stream
    if (level == LogLevel.ERROR || level == LogLevel.WARN) {
      System.err.println(builder.toString());
      // Print exception if present
      if (e != null) {
        e.printStackTrace(System.err);
      }
    } else {
      System.out.println(builder.toString());
      // Print exception if present
      if (e != null) {
        e.printStackTrace(System.out);
      }
    }
  }

  /** Log levels for the ConsoleLogger. */
  public enum LogLevel {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR
  }

  /**
   * Gets a logger for the specified class.
   *
   * @param clazz The class to get a logger for
   * @return A new ConsoleLogger
   */
  public static ConsoleLogger getLogger(Class<?> clazz) {
    return new ConsoleLogger(clazz);
  }

  /**
   * Gets a logger with the specified name.
   *
   * @param name The logger name
   * @return A new ConsoleLogger
   */
  public static ConsoleLogger getLogger(String name) {
    return new ConsoleLogger(name);
  }
}
