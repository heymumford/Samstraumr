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

package org.s8r.infrastructure.logging;

import org.s8r.application.port.LoggerFactory;
import org.s8r.application.port.LoggerPort;

/**
 * Implementation of the LoggerFactory interface for the S8r framework.
 *
 * <p>This factory allows the system to switch between different logger implementations while
 * maintaining Clean Architecture principles by implementing the application layer's LoggerFactory
 * interface.
 */
public class S8rLoggerFactory implements LoggerFactory {
  private static LoggerImplementation implementation = LoggerImplementation.SLF4J;
  private static final S8rLoggerFactory INSTANCE = new S8rLoggerFactory();

  /** Available logger implementations. */
  public enum LoggerImplementation {
    SLF4J,
    CONSOLE
  }

  /** Private constructor to enforce singleton pattern. */
  private S8rLoggerFactory() {}

  /**
   * Gets the singleton instance.
   *
   * @return The S8rLoggerFactory instance
   */
  public static S8rLoggerFactory getInstance() {
    return INSTANCE;
  }

  /**
   * Sets the logger implementation to use.
   *
   * @param implementation The implementation to use
   */
  public void setImplementation(LoggerImplementation implementation) {
    S8rLoggerFactory.implementation = implementation;
  }

  /**
   * Gets the current logger implementation.
   *
   * @return The current logger implementation
   */
  public LoggerImplementation getImplementation() {
    return implementation;
  }

  @Override
  public LoggerPort getLogger(Class<?> clazz) {
    switch (implementation) {
      case SLF4J:
        return Slf4jLogger.getLogger(clazz);
      case CONSOLE:
        return ConsoleLogger.getLogger(clazz);
      default:
        return Slf4jLogger.getLogger(clazz);
    }
  }

  @Override
  public LoggerPort getLogger(String name) {
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
