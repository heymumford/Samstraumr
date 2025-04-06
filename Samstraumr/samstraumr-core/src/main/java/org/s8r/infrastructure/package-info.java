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
 *     https://www.mozilla.org/MPL/2.0/
 */

/**
 * Infrastructure layer for the Samstraumr framework.
 * 
 * <p>This package contains implementations of interfaces defined in the application layer,
 * providing concrete implementations of technical concerns such as persistence, logging,
 * event handling, and configuration.
 * 
 * <p>Key responsibilities of the infrastructure layer:
 * <ul>
 *   <li>Implement application ports defined in the application layer</li>
 *   <li>Provide concrete implementations of technical concerns</li>
 *   <li>Integrate with external frameworks and libraries</li>
 *   <li>Handle cross-cutting concerns like logging, security, and transactions</li>
 * </ul>
 * 
 * <p>The infrastructure layer is organized into subpackages:
 * <ul>
 *   <li>config - Configuration and dependency injection</li>
 *   <li>event - Event handling and dispatching</li>
 *   <li>logging - Logging implementations</li>
 *   <li>persistence - Repository implementations</li>
 * </ul>
 * 
 * <p>In Clean Architecture, the infrastructure layer depends on the domain and application
 * layers, but not vice versa. This ensures that the core business logic remains independent
 * of technical implementation details.
 */
package org.s8r.infrastructure;