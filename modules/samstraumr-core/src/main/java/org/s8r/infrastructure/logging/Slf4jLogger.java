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

import org.s8r.application.port.LoggerPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SLF4J implementation of the LoggerPort interface.
 *
 * <p>This adapter connects the application layer's LoggerPort to the SLF4J logging framework,
 * following Clean Architecture principles by implementing an application layer port.
 *
 * <p>This implementation provides additional functionality beyond the LoggerPort interface, such as
 * checking if debug is enabled and static factory methods for creating loggers.
 */
public class Slf4jLogger implements LoggerPort {
  private final Logger logger;

  /**
   * Creates a new SLF4J logger for the specified class.
   *
   * @param clazz The class to create the logger for
   */
  public Slf4jLogger(Class<?> clazz) {
    this.logger = LoggerFactory.getLogger(clazz);
  }

  /**
   * Creates a new SLF4J logger with the specified name.
   *
   * @param name The logger name
   */
  public Slf4jLogger(String name) {
    this.logger = LoggerFactory.getLogger(name);
  }

  @Override
  public void trace(String message) {
    logger.trace(message);
  }
  
  @Override
  public void trace(String format, Object... args) {
    logger.trace(format, args);
  }

  @Override
  public void debug(String message) {
    logger.debug(message);
  }
  
  @Override
  public void debug(String format, Object... args) {
    logger.debug(format, args);
  }

  @Override
  public void info(String message) {
    logger.info(message);
  }
  
  @Override
  public void info(String format, Object... args) {
    logger.info(format, args);
  }

  @Override
  public void warn(String message) {
    logger.warn(message);
  }
  
  @Override
  public void warn(String format, Object... args) {
    logger.warn(format, args);
  }

  @Override
  public void error(String message) {
    logger.error(message);
  }
  
  @Override
  public void error(String format, Object... args) {
    logger.error(format, args);
  }

  @Override
  public void error(String message, Throwable e) {
    logger.error(message, e);
  }

  @Override
  public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  @Override
  public boolean isTraceEnabled() {
    return logger.isTraceEnabled();
  }
  
  @Override
  public String getName() {
    return logger.getName();
  }

  /**
   * Gets a logger for the specified class.
   *
   * @param clazz The class to get a logger for
   * @return A new Slf4jLogger
   */
  public static Slf4jLogger getLogger(Class<?> clazz) {
    return new Slf4jLogger(clazz);
  }

  /**
   * Gets a logger with the specified name.
   *
   * @param name The logger name
   * @return A new Slf4jLogger
   */
  public static Slf4jLogger getLogger(String name) {
    return new Slf4jLogger(name);
  }
}
