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
 * </ul>
 * </p>
 * <p>
 * This package uses reflection to interact with legacy code, eliminating direct
 * dependencies on legacy classes. This approach maintains Clean Architecture principles
 * while still providing necessary bridging functionality during the migration period.
 * </p>
 * <p>
 * The use of reflection allows the adapter layer to work with legacy types without
 * creating illegal dependencies across architectural boundaries. All legacy code access
 * is encapsulated in this package, preventing it from spreading elsewhere.
 * </p>
 */
package org.s8r.adapter;
