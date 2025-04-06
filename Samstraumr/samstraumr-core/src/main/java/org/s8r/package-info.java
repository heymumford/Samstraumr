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
 * Samstraumr (S8r) - A component-based framework for building resilient systems.
 * 
 * <p>This root package contains the main framework facade and entry points. The framework
 * is built on Clean Architecture principles, with clear separation of concerns between layers:
 * 
 * <ul>
 *   <li>Domain Layer: Core business entities and logic (org.s8r.domain)</li>
 *   <li>Application Layer: Use cases and application-specific rules (org.s8r.application)</li>
 *   <li>Infrastructure Layer: External service implementations (org.s8r.infrastructure)</li>
 *   <li>Interface Adapters Layer: Controllers, gateways, presenters (org.s8r.adapter)</li>
 *   <li>Frameworks Layer: External frameworks and tools (org.s8r.app)</li>
 * </ul>
 * 
 * <p>The framework implements dependency inversion through interfaces (ports) defined in the
 * application layer and implemented in the infrastructure layer. This ensures that inner layers
 * don't depend on outer layers, maintaining a clean, testable architecture.
 * 
 * <p>The {@link org.s8r.Samstraumr} class serves as the main facade for the framework, providing
 * a simplified API for clients to use the framework's functionality.
 */
package org.s8r;