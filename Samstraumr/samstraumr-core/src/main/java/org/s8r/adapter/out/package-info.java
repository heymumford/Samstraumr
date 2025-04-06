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
 * Output adapters for the S8r framework, connecting to external systems.
 *
 * <p>This package contains the output adapters that implement outgoing ports defined in the
 * application layer. These adapters connect the domain model to external infrastructure such as
 * databases, messaging systems, and other external services.
 *
 * <p>According to the Ports and Adapters Architecture (also known as Hexagonal Architecture), these
 * adapters convert between the application's internal domain model and external interfaces. Each
 * adapter implements a port interface defined in the application layer.
 *
 * <p>This architectural style ensures that:
 *
 * <ul>
 *   <li>The application's core logic doesn't depend on external systems
 *   <li>External systems can be changed without affecting the core
 *   <li>The system is more testable, as the core can be tested without external dependencies
 * </ul>
 */
package org.s8r.adapter.out;