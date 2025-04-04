/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Package containing logging infrastructure for components in the S8r framework.
 */

/**
 * Provides specialized logging functionality for components in the S8r framework.
 *
 * <p>This package is part of the simplified structure, replacing the original tube-specific logging
 * implementation. It offers enhanced logging capabilities with component-specific contextual
 * information, hierarchical awareness, and visual identification features.
 *
 * <p>Key features of this package include:
 *
 * <ul>
 *   <li>Component-aware logger with hierarchical context
 *   <li>Structured logging with component identifiers
 *   <li>Tagging capabilities for categorization
 *   <li>Visual identification for log analysis
 *   <li>Sequence tracking for operation order
 * </ul>
 *
 * <p>Example usage:
 *
 * <pre>
 * Logger logger = new Logger(component.getUniqueId());
 * logger.info("Component initialized successfully", "LIFECYCLE", "INIT");
 * </pre>
 */
package org.s8r.core.tube.logging;
