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
 * Configuration and dependency injection for the Samstraumr framework.
 *
 * <p>This package contains classes for configuring the framework and managing dependencies through
 * a simple dependency injection container. It provides centralized configuration management and
 * dependency resolution.
 *
 * <p>Key responsibilities:
 *
 * <ul>
 *   <li>Provide configuration management
 *   <li>Register and resolve dependencies
 *   <li>Wire components together according to Clean Architecture principles
 *   <li>Support factory patterns for legacy code adapters
 * </ul>
 *
 * <p>Key classes:
 *
 * <ul>
 *   <li>{@link org.s8r.infrastructure.config.Configuration} - Manages framework configuration
 *   <li>{@link org.s8r.infrastructure.config.DependencyContainer} - Simple dependency injection
 *       container
 *   <li>{@link org.s8r.infrastructure.config.LegacyAdapterFactory} - Factory for legacy code
 *       adapters
 * </ul>
 *
 * <p>The dependency injection container follows these principles:
 *
 * <ul>
 *   <li>Singleton pattern for central access
 *   <li>Type-based registration and resolution
 *   <li>Constructor injection for dependencies
 *   <li>Layer-based initialization (logger first, then event system, etc.)
 * </ul>
 *
 * <p>The container also implements the ServiceFactory interface from the application layer,
 * providing a clean abstraction for accessing framework services while maintaining proper
 * architectural layering.
 */
package org.s8r.infrastructure.config;
