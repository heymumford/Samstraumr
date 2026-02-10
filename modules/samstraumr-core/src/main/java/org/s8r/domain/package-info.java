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
 * Domain layer of the Samstraumr framework following Clean Architecture principles.
 *
 * <p>This package and its subpackages contain the domain model that forms the core business logic
 * of the S8r framework. The domain layer is independent of external frameworks, databases, and user
 * interfaces.
 *
 * <h2>Subpackages</h2>
 *
 * <ul>
 *   <li>{@link org.s8r.domain.component} - Component domain model
 *   <li>{@link org.s8r.domain.event} - Domain events
 *   <li>{@link org.s8r.domain.identity} - Identity domain model
 *   <li>{@link org.s8r.domain.machine} - Machine domain model
 *   <li>{@link org.s8r.domain.lifecycle} - Lifecycle management
 *   <li>{@link org.s8r.domain.exception} - Domain exceptions
 * </ul>
 *
 * <h2>Clean Architecture</h2>
 *
 * <p>This layer follows Clean Architecture principles:
 *
 * <ul>
 *   <li>Framework Independence: No dependencies on external frameworks
 *   <li>Testability: Business rules can be tested without UI, database, etc.
 *   <li>UI Independence: UI can change without changing business rules
 *   <li>Database Independence: Business rules not bound to a specific database
 *   <li>Independence from External Agencies: Business rules don't know about interfaces to the
 *       outside world
 * </ul>
 *
 * <h2>Related Packages</h2>
 *
 * <ul>
 *   <li>{@link org.s8r.application.service} - Application services that use domain model
 *   <li>{@link org.s8r.application.port} - Ports to interact with the domain model
 *   <li>{@link org.s8r.infrastructure.persistence} - Repository implementations
 * </ul>
 *
 * <p>For detailed information about the domain layer organization and design principles, see the
 * README.md file in this package directory.
 */
package org.s8r.domain;
