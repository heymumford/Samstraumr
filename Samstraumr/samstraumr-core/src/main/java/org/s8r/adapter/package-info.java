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
 * Adapter package for bridging between new Clean Architecture components and legacy code.
 * <p>
 * This package provides adapters that facilitate communication between the new Clean
 * Architecture structure and legacy code. During the migration period, these adapters
 * allow gradual transition without breaking existing functionality.
 * </p>
 * <p>
 * Key components in this package:
 * <ul>
 *   <li>IdentityAdapter - Converts between new and legacy Identity objects</li>
 *   <li>ReflectiveIdentityConverter - Reflection-based converters for legacy types</li>
 *   <li>ReflectiveEnvironmentConverter - Reflection-based environment converters</li>
 *   <li>ReflectiveAdapterFactory - Factory for creating adapters with proper reflection</li>
 *   <li>TubeComponentAdapter - Specific adapter for Tube to Component conversion</li>
 *   <li>TubeComponentWrapper - Wrapper implementing Component while delegating to Tube</li>
 *   <li>CompositeAdapter - Adapter for converting between Tube Composites and Component Composites</li>
 *   <li>MachineAdapter - Adapter for converting between Tube Machines and Component Machines</li>
 *   <li>TubeLegacyIdentityConverter - Direct converter between TubeIdentity and ComponentId</li>
 *   <li>TubeLegacyEnvironmentConverter - Direct converter between Environment types</li>
 *   <li>S8rMigrationFactory - Factory for creating migration utilities</li>
 * </ul>
 * </p>
 * <p>
 * Migration Utilities:
 * <ul>
 *   <li>Reflective Approach: Uses reflection to interact with legacy code without direct dependencies,
 *       suitable for cases where exact legacy types are unknown or may vary.</li>
 *   <li>Direct Approach: Provides explicit converters for known legacy types like Tube and TubeIdentity,
 *       offering better performance and type safety for the most common migration scenarios.</li>
 * </ul>
 * </p>
 * <p>
 * The S8rMigrationFactory simplifies the use of these utilities for client code, providing a
 * convenient entry point to the migration functionality. It helps implement the Strangler Fig pattern
 * by allowing gradual replacement of legacy code with new Clean Architecture components.
 * </p>
 * <p>
 * All legacy code access is encapsulated in this package, preventing it from spreading elsewhere
 * and maintaining Clean Architecture principles during the migration period.
 * </p>
 */
package org.s8r.adapter;
