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

/**
 * Adapter package for bridging between different architectural layers and legacy code.
 *
 * <p>This package provides adapters that facilitate communication between:
 *
 * <ul>
 *   <li>Domain layer and application layer using port interfaces
 *   <li>Application layer and infrastructure layer implementations
 *   <li>New Clean Architecture components and legacy code during migration
 * </ul>
 *
 * <p>Following the Clean Architecture pattern and the Dependency Inversion Principle, these
 * adapters allow higher-level modules (like domain and application layers) to remain independent of
 * lower-level modules (like infrastructure or legacy implementations).
 *
 * <p>Port Interface Adapters:
 *
 * <ul>
 *   <li>ComponentAdapter - Converts between Component domain entities and ComponentPort interfaces
 *   <li>MachineAdapter - Converts between Machine domain entities and MachinePort interfaces
 *   <li>CompositeAdapter - Converts between Composite domain entities and CompositeComponentPort
 *       interfaces
 * </ul>
 *
 * These adapters implement the Adapter pattern to allow client code to depend on abstractions
 * (ports) rather than concrete implementations, enabling better testability and flexibility.
 *
 * <p>Legacy Migration Adapters:
 *
 * <ul>
 *   <li>IdentityAdapter - Converts between new and legacy Identity objects
 *   <li>ReflectiveIdentityConverter - Reflection-based converters for legacy types
 *   <li>ReflectiveEnvironmentConverter - Reflection-based environment converters
 *   <li>ReflectiveAdapterFactory - Factory for creating adapters with proper reflection
 *   <li>TubeComponentAdapter - Specific adapter for Tube to Component conversion
 *   <li>TubeComponentWrapper - Wrapper implementing Component while delegating to Tube
 *   <li>TubeLegacyIdentityConverter - Direct converter between TubeIdentity and ComponentId
 *   <li>TubeLegacyEnvironmentConverter - Direct converter between Environment types
 *   <li>S8rMigrationFactory - Factory for creating migration utilities
 * </ul>
 *
 * <p>Migration Utilities:
 *
 * <ul>
 *   <li>Reflective Approach: Uses reflection to interact with legacy code without direct
 *       dependencies, suitable for cases where exact legacy types are unknown or may vary.
 *   <li>Direct Approach: Provides explicit converters for known legacy types like Tube and
 *       TubeIdentity, offering better performance and type safety for the most common migration
 *       scenarios.
 * </ul>
 *
 * <p>The adapters in this package follow several design patterns:
 *
 * <ul>
 *   <li>Adapter Pattern - Converts interfaces between different components
 *   <li>Factory Pattern - Creates adapter instances as needed
 *   <li>Bridge Pattern - Decouples abstraction from implementation
 *   <li>Decorator Pattern - Extends functionality while maintaining compatibility
 * </ul>
 *
 * <p>All adapters encapsulate conversion logic, preventing coupling between layers and maintaining
 * Clean Architecture principles during both normal operation and the migration period.
 */
package org.s8r.adapter;
