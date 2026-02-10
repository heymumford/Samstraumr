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

import org.s8r.domain.identity.LegacyComponentAdapterPort;
import org.s8r.domain.identity.LegacyEnvironmentConverter;
import org.s8r.domain.identity.LegacyIdentityConverter;

/**
 * Defines an interface for resolving legacy adapter implementations.
 *
 * <p>This interface provides a way for application layer code to obtain instances of legacy
 * converters without depending directly on specific adapter implementations.
 */
public interface LegacyAdapterResolver {

  /**
   * Gets a converter for core environment objects.
   *
   * @return The core environment converter
   */
  LegacyEnvironmentConverter getCoreEnvironmentConverter();

  /**
   * Gets a converter for tube environment objects.
   *
   * @return The tube environment converter
   */
  LegacyEnvironmentConverter getTubeEnvironmentConverter();

  /**
   * Gets a converter for core identity objects.
   *
   * @return The core identity converter
   */
  LegacyIdentityConverter getCoreIdentityConverter();

  /**
   * Gets a converter for tube identity objects.
   *
   * @return The tube identity converter
   */
  LegacyIdentityConverter getTubeIdentityConverter();

  /**
   * Gets an adapter for core components.
   *
   * @return The core component adapter
   */
  LegacyComponentAdapterPort getCoreComponentAdapter();

  /**
   * Gets an adapter for tube components.
   *
   * @return The tube component adapter
   */
  LegacyComponentAdapterPort getTubeComponentAdapter();
}
