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

package org.s8r.infrastructure.security;

import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.SecurityPort;

/**
 * Adapter implementation of the SecurityPort interface.
 *
 * <p>This adapter delegates to the InMemorySecurityAdapter for testing purposes. In a production
 * environment, this adapter would connect to a proper security system.
 */
public class SecurityAdapter extends InMemorySecurityAdapter implements SecurityPort {

  /**
   * Creates a new SecurityAdapter with the specified logger.
   *
   * @param logger The logger to use for logging security events
   */
  public SecurityAdapter(LoggerPort logger) {
    super(logger);
  }
}
