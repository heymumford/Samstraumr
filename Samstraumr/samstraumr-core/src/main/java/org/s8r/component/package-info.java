/**
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://github.com/heymumford/Samstraumr/blob/main/LICENSE
 *
 * Component-based architecture for S8r framework
 */

/**
 * Provides a component-based architecture for building resilient, adaptive systems in the S8r framework.
 * 
 * <p>This package replaces the tube-based terminology with more standard component-based terminology
 * while maintaining the biological-inspired lifecycle model. It offers a cleaner, more intuitive
 * structure with reduced nesting depth.
 * 
 * <p>The component model is organized into the following subpackages:
 * 
 * <ul>
 *   <li>{@code core} - Core component implementation and state management</li>
 *   <li>{@code identity} - Component identity and lineage tracking</li>
 *   <li>{@code logging} - Component-aware logging infrastructure</li>
 *   <li>{@code composite} - Component composition and relationship management</li>
 *   <li>{@code machine} - Higher-level abstractions built from components</li>
 * </ul>
 * 
 * <p>This package is part of the simplified structure, replacing:
 * <ul>
 *   <li>org.samstraumr.tube.*</li>
 *   <li>org.s8r.core.tube.*</li>
 *   <li>org.tube.*</li>
 * </ul>
 */
package org.s8r.component;