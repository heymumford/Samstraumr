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

/**
 * Interface for converting between different identity types.
 *
 * <p>This interface allows for backward compatibility during the transition to the simplified
 * package structure, while ensuring the domain layer doesn't depend on legacy implementation
 * details.
 */
public interface IdentityConverter {

  /**
   * Converts a legacy identity to the new component identity format.
   *
   * @param legacyId A legacy identity ID
   * @param legacyReason The reason from the legacy identity
   * @param legacyLineage The lineage from the legacy identity
   * @return A new ComponentId object with the same properties
   */
  ComponentId toComponentId(
      String legacyId, String legacyReason, java.util.List<String> legacyLineage);

  /**
   * Converts a component identity to a legacy format string representation.
   *
   * @param componentId The ComponentId to convert
   * @return A string representation of the legacy identity format
   */
  String toLegacyIdString(ComponentId componentId);
}
