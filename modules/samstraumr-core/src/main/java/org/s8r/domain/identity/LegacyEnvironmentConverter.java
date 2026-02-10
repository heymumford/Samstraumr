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

package org.s8r.domain.identity;

import java.util.Map;

/**
 * Interface for converting between different environment types.
 *
 * <p>This interface allows for backward compatibility during the transition to the simplified
 * package structure, while ensuring the domain layer doesn't depend on legacy implementation
 * details in the core or tube packages.
 */
public interface LegacyEnvironmentConverter {

  /**
   * Creates a legacy environment with the specified parameters.
   *
   * @param parameters The parameters to set in the legacy environment
   * @return An Object representing the legacy environment
   */
  Object createLegacyEnvironment(Map<String, String> parameters);

  /**
   * Extract parameters from a legacy environment.
   *
   * @param legacyEnvironment The legacy environment object
   * @return A map of parameters extracted from the legacy environment
   */
  Map<String, String> extractParametersFromLegacyEnvironment(Object legacyEnvironment);

  /**
   * Extracts the class name of the legacy environment.
   *
   * @param legacyEnvironment The legacy environment object
   * @return The class name of the legacy environment
   */
  String getLegacyEnvironmentClassName(Object legacyEnvironment);
}
