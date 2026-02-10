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

package org.s8r.component.identity;

import java.util.Map;

/**
 * Represents the identity of a component based on the biological continuity model.
 *
 * <p>This class is part of the clean architecture implementation, providing a dedicated identity
 * package for the component domain.
 *
 * <p>The Identity class encapsulates all aspects of a component's identity:
 *
 * <ul>
 *   <li>Universally Unique Identifier (UUID)
 *   <li>Creation timestamp (conception time)
 *   <li>Lineage information (parent-child relationships)
 *   <li>Environmental context at creation
 *   <li>Creation reason and metadata
 * </ul>
 *
 * <p>Once created, the core identity properties are immutable, ensuring a stable foundation for a
 * component's existence throughout its lifecycle.
 */
public class Identity extends org.s8r.component.Identity {

  /**
   * Creates a new Identity with the specified properties.
   *
   * @param uniqueId The unique identifier for this component
   * @param reason The reason for creating this component
   */
  public Identity(String uniqueId, String reason) {
    super(uniqueId, reason);
  }

  /**
   * Creates a new Identity with a randomly generated UUID.
   *
   * @param reason The reason for creating this component
   * @return A new Identity instance
   */
  public static Identity createWithRandomId(String reason) {
    return (Identity) org.s8r.component.Identity.createWithRandomId(reason);
  }

  /**
   * Creates an Adam (origin) component identity without a parent reference.
   *
   * @param reason The reason for creating this Adam component
   * @param environmentParams The environment in which to create the component
   * @return A new Adam component identity
   */
  public static Identity createAdamIdentity(String reason, Map<String, String> environmentParams) {
    return (Identity) org.s8r.component.Identity.createAdamIdentity(reason, environmentParams);
  }

  /**
   * Creates a child component identity with a parent reference.
   *
   * @param reason The reason for creating this child component
   * @param environmentParams Environmental parameters
   * @param parentIdentity The parent component's identity
   * @return A new child component identity
   */
  public static Identity createChildIdentity(
      String reason,
      Map<String, String> environmentParams,
      org.s8r.component.Identity parentIdentity) {
    return (Identity)
        org.s8r.component.Identity.createChildIdentity(reason, environmentParams, parentIdentity);
  }
}
