/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Factory for creating loggers in the S8r framework
 */

package org.s8r.infrastructure.logging;

import org.s8r.application.port.LoggerPort;

/**
 * Factory for creating LoggerPort implementations.
 *
 * <p>This factory allows the system to switch between different logger implementations while
 * maintaining Clean Architecture principles by returning the application layer's LoggerPort
 * interface.
 */
public class LoggerFactory {
  private static LoggerImplementation implementation = LoggerImplementation.SLF4J;

  /** Available logger implementations. */
  public enum LoggerImplementation {
    SLF4J,
    CONSOLE
  }

  /**
   * Sets the logger implementation to use.
   *
   * @param implementation The implementation to use
   */
  public static void setImplementation(LoggerImplementation implementation) {
    LoggerFactory.implementation = implementation;
  }

  /**
   * Gets the current logger implementation.
   *
   * @return The current logger implementation
   */
  public static LoggerImplementation getImplementation() {
    return implementation;
  }

  /**
   * Gets a logger for the specified class.
   *
   * @param clazz The class to get a logger for
   * @return A LoggerPort implementation
   */
  public static LoggerPort getLogger(Class<?> clazz) {
    switch (implementation) {
      case SLF4J:
        return Slf4jLogger.getLogger(clazz);
      case CONSOLE:
        return ConsoleLogger.getLogger(clazz);
      default:
        return Slf4jLogger.getLogger(clazz);
    }
  }

  /**
   * Gets a logger with the specified name.
   *
   * @param name The logger name
   * @return A LoggerPort implementation
   */
  public static LoggerPort getLogger(String name) {
    switch (implementation) {
      case SLF4J:
        return Slf4jLogger.getLogger(name);
      case CONSOLE:
        return ConsoleLogger.getLogger(name);
      default:
        return Slf4jLogger.getLogger(name);
    }
  }
}
