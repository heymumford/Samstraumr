/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.application.port;

/**
 * Port interface for logging operations in the application layer.
 *
 * <p>This interface defines the operations for logging at different levels, following the ports and
 * adapters pattern from Clean Architecture.
 */
public interface LoggerPort {

  /**
   * Logs a message at the TRACE level.
   *
   * @param message The message to log
   */
  void trace(String message);

  /**
   * Logs a formatted message at the TRACE level.
   *
   * @param format The format string
   * @param args The arguments
   */
  void trace(String format, Object... args);

  /**
   * Logs a message at the DEBUG level.
   *
   * @param message The message to log
   */
  void debug(String message);

  /**
   * Logs a formatted message at the DEBUG level.
   *
   * @param format The format string
   * @param args The arguments
   */
  void debug(String format, Object... args);

  /**
   * Logs a message at the INFO level.
   *
   * @param message The message to log
   */
  void info(String message);

  /**
   * Logs a formatted message at the INFO level.
   *
   * @param format The format string
   * @param args The arguments
   */
  void info(String format, Object... args);

  /**
   * Logs a message at the WARN level.
   *
   * @param message The message to log
   */
  void warn(String message);

  /**
   * Logs a formatted message at the WARN level.
   *
   * @param format The format string
   * @param args The arguments
   */
  void warn(String format, Object... args);

  /**
   * Logs a message at the ERROR level.
   *
   * @param message The message to log
   */
  void error(String message);

  /**
   * Logs a formatted message at the ERROR level.
   *
   * @param format The format string
   * @param args The arguments
   */
  void error(String format, Object... args);

  /**
   * Logs a message at the ERROR level with an exception.
   *
   * @param message The message to log
   * @param throwable The exception to log
   */
  void error(String message, Throwable throwable);

  /**
   * Checks if the TRACE level is enabled.
   *
   * @return true if the TRACE level is enabled, false otherwise
   */
  boolean isTraceEnabled();

  /**
   * Checks if the DEBUG level is enabled.
   *
   * @return true if the DEBUG level is enabled, false otherwise
   */
  boolean isDebugEnabled();

  /**
   * Gets the name of this logger.
   *
   * @return The logger name
   */
  String getName();
}
