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

/**
 * Domain identity management for components.
 * 
 * <p>This package contains classes for managing component identity in the Samstraumr framework.
 * Identity is a core concept in the framework, providing unique identification and lineage
 * tracking for components.
 * 
 * <p>Key responsibilities of identity management:
 * <ul>
 *   <li>Generating and managing unique identifiers for components</li>
 *   <li>Tracking component lineage (parent-child relationships)</li>
 *   <li>Supporting hierarchical component relationships</li>
 *   <li>Converting between different identity representations</li>
 * </ul>
 * 
 * <p>Key classes:
 * <ul>
 *   <li>{@link org.s8r.domain.identity.ComponentId} - Value object representing a component's identity</li>
 *   <li>{@link org.s8r.domain.identity.ComponentHierarchy} - Utility for managing hierarchical relationships</li>
 *   <li>{@link org.s8r.domain.identity.IdentityConverter} - Interface for converting to/from legacy identity formats</li>
 *   <li>{@link org.s8r.domain.identity.LegacyIdentityConverter} - Extended interface for legacy identity conversion</li>
 *   <li>{@link org.s8r.domain.identity.LegacyEnvironmentConverter} - Interface for legacy environment conversion</li>
 * </ul>
 * 
 * <p>Identity in the framework follows the principles of:
 * <ul>
 *   <li>Immutability - Component IDs are immutable value objects</li>
 *   <li>Uniqueness - Each component has a globally unique identifier</li>
 *   <li>Traceability - Lineage information tracks component relationships</li>
 *   <li>Hierarchical structure - Components can form parent-child relationships</li>
 * </ul>
 */
package org.s8r.domain.identity;