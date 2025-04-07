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
 * Adapter package for bridging between different architectural layers and legacy code.
 * <p>
 * This package provides adapters that facilitate communication between:
 * <ul>
 *   <li>Domain layer and application layer using port interfaces</li>
 *   <li>Application layer and infrastructure layer implementations</li>
 *   <li>New Clean Architecture components and legacy code during migration</li>
 * </ul>
 * </p>
 * <p>
 * Following the Clean Architecture pattern and the Dependency Inversion Principle, these adapters
 * allow higher-level modules (like domain and application layers) to remain independent of lower-level
 * modules (like infrastructure or legacy implementations).
 * </p>
 * <p>
 * Port Interface Adapters:
 * <ul>
 *   <li>ComponentAdapter - Converts between Component domain entities and ComponentPort interfaces</li>
 *   <li>MachineAdapter - Converts between Machine domain entities and MachinePort interfaces</li>
 *   <li>CompositeAdapter - Converts between Composite domain entities and CompositeComponentPort interfaces</li>
 * </ul>
 * These adapters implement the Adapter pattern to allow client code to depend on abstractions (ports)
 * rather than concrete implementations, enabling better testability and flexibility.
 * </p>
 * <p>
 * Legacy Migration Adapters:
 * <ul>
 *   <li>IdentityAdapter - Converts between new and legacy Identity objects</li>
 *   <li>ReflectiveIdentityConverter - Reflection-based converters for legacy types</li>
 *   <li>ReflectiveEnvironmentConverter - Reflection-based environment converters</li>
 *   <li>ReflectiveAdapterFactory - Factory for creating adapters with proper reflection</li>
 *   <li>TubeComponentAdapter - Specific adapter for Tube to Component conversion</li>
 *   <li>TubeComponentWrapper - Wrapper implementing Component while delegating to Tube</li>
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
 * The adapters in this package follow several design patterns:
 * <ul>
 *   <li>Adapter Pattern - Converts interfaces between different components</li>
 *   <li>Factory Pattern - Creates adapter instances as needed</li>
 *   <li>Bridge Pattern - Decouples abstraction from implementation</li>
 *   <li>Decorator Pattern - Extends functionality while maintaining compatibility</li>
 * </ul>
 * </p>
 * <p>
 * All adapters encapsulate conversion logic, preventing coupling between layers and maintaining
 * Clean Architecture principles during both normal operation and the migration period.
 * </p>
 */
package org.s8r.adapter;
