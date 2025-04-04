/**
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://github.com/heymumford/Samstraumr/blob/main/LICENSE
 *
 * Package containing logging infrastructure for components in the S8r framework
 */

/**
 * Provides specialized logging functionality for components in the S8r framework.
 *
 * <p>This package is part of the simplified structure, replacing the original tube-specific
 * logging implementation. It offers enhanced logging capabilities with component-specific 
 * contextual information, hierarchical awareness, and visual identification features.
 *
 * <p>Key features of this package include:
 * <ul>
 *   <li>Component-aware logger with hierarchical context</li>
 *   <li>Structured logging with component identifiers</li>
 *   <li>Tagging capabilities for categorization</li>
 *   <li>Visual identification for log analysis</li>
 *   <li>Sequence tracking for operation order</li>
 * </ul>
 *
 * <p>The design of this package emphasizes simplicity and integration by combining
 * what were previously separate classes (Logger and LoggerInfo) into a single
 * class with nested components.
 *
 * <p>Example usage:
 * <pre>
 * Logger logger = new Logger(component.getUniqueId());
 * logger.info("Component initialized successfully", "LIFECYCLE", "INIT");
 * </pre>
 */
package org.s8r.component.logging;