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
 * Domain exceptions in the Samstraumr framework.
 *
 * <p>This package contains exception classes that represent errors and exceptional conditions
 * within the domain layer.
 *
 * <h2>Key Classes</h2>
 *
 * <ul>
 *   <li>{@link org.s8r.domain.exception.DomainException} - Base exception class
 *   <li>{@link org.s8r.domain.exception.ComponentException} - Component-specific exceptions
 *   <li>{@link org.s8r.domain.exception.ComponentNotFoundException} - Exception for missing
 *       components
 *   <li>{@link org.s8r.domain.exception.InvalidStateTransitionException} - Invalid state
 *       transitions
 * </ul>
 *
 * <h2>Related Packages</h2>
 *
 * <ul>
 *   <li>{@link org.s8r.domain.component} - Component domain model that throws these exceptions
 *   <li>{@link org.s8r.domain.machine} - Machine domain model that throws these exceptions
 * </ul>
 */
package org.s8r.domain.exception;
