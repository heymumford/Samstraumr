/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

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

package org.s8r.domain.logging;

/**
 * Domain-layer logging interface providing a clean abstraction for logging within the domain model.
 *
 * <p>This interface enables domain logic to emit log messages without depending on external logging
 * frameworks (SLF4J, Log4j, etc.). Implementations in the infrastructure layer will bridge to
 * concrete logging systems.
 *
 * <p>This is a pure domain interface with no dependencies on outer architectural layers, complying
 * with the Dependency Inversion Principle of Clean Architecture.
 *
 * @see <a href="https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html">Clean
 *     Architecture</a>
 */
public interface DomainLogger {

  /**
   * Logs a debug-level message with optional arguments.
   *
   * <p>Arguments are substituted into the message template using '{}' placeholders. This follows
   * the SLF4J convention for message formatting.
   *
   * @param message the log message template
   * @param args optional arguments to substitute into the message template
   */
  void debug(String message, Object... args);

  /**
   * Logs an info-level message with optional arguments.
   *
   * @param message the log message template
   * @param args optional arguments to substitute into the message template
   */
  void info(String message, Object... args);

  /**
   * Logs a warning-level message with optional arguments.
   *
   * @param message the log message template
   * @param args optional arguments to substitute into the message template
   */
  void warn(String message, Object... args);

  /**
   * Logs an error-level message with optional arguments.
   *
   * @param message the log message template
   * @param args optional arguments to substitute into the message template
   */
  void error(String message, Object... args);

  /**
   * Logs an error-level message with an exception and optional arguments.
   *
   * @param message the log message template
   * @param throwable the exception to log
   * @param args optional arguments to substitute into the message template
   */
  void error(String message, Throwable throwable, Object... args);
}
