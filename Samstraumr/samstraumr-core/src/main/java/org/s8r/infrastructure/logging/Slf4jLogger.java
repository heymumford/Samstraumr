/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Infrastructure implementation of LoggerPort using SLF4J
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
  public void debug(String message) {
    logger.debug(message);
  }

  @Override
  public void debug(String message, Throwable e) {
    logger.debug(message, e);
  }

  @Override
  public void info(String message) {
    logger.info(message);
  }

  @Override
  public void info(String message, Throwable e) {
    logger.info(message, e);
  }

  @Override
  public void warn(String message) {
    logger.warn(message);
  }

  @Override
  public void warn(String message, Throwable e) {
    logger.warn(message, e);
  }

  @Override
  public void error(String message) {
    logger.error(message);
  }

  @Override
  public void error(String message, Throwable e) {
    logger.error(message, e);
  }

  /**
   * Checks if debug logging is enabled.
   *
   * @return true if debug is enabled, false otherwise
   */
  public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  /**
   * Checks if trace logging is enabled.
   *
   * @return true if trace is enabled, false otherwise
   */
  public boolean isTraceEnabled() {
    return logger.isTraceEnabled();
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
