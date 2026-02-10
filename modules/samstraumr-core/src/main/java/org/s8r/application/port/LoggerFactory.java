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

package org.s8r.application.port;

/**
 * Port for creating logger instances.
 *
 * <p>This port defines how the application layer obtains loggers, allowing for different logging
 * implementations to be used without changing the application code.
 */
public interface LoggerFactory {

  /**
   * Gets a logger for the specified class.
   *
   * @param clazz The class requiring a logger
   * @return A logger instance
   */
  LoggerPort getLogger(Class<?> clazz);

  /**
   * Gets a logger with the specified name.
   *
   * @param name The logger name
   * @return A logger instance
   */
  LoggerPort getLogger(String name);
}
