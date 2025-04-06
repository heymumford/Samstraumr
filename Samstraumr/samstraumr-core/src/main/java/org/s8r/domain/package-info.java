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
 * Domain layer for the Samstraumr framework.
 * 
 * <p>This package contains the core business entities, business rules, and domain logic of the
 * Samstraumr framework. As the innermost layer of the Clean Architecture, it has no dependencies
 * on other layers.
 * 
 * <p>Key responsibilities of the domain layer:
 * <ul>
 *   <li>Define core business entities (Component, Machine, etc.)</li>
 *   <li>Implement domain-specific business rules</li>
 *   <li>Define interfaces that will be implemented by outer layers</li>
 *   <li>Establish domain events and their propagation rules</li>
 * </ul>
 * 
 * <p>The domain layer is organized into several subpackages:
 * <ul>
 *   <li>component - Core component entities and related classes</li>
 *   <li>event - Domain events and event types</li>
 *   <li>exception - Domain-specific exceptions</li>
 *   <li>identity - Component identification and hierarchy management</li>
 *   <li>lifecycle - Lifecycle state management</li>
 *   <li>machine - Machine definition and orchestration</li>
 * </ul>
 * 
 * <p>This layer follows the Dependency Inversion Principle: high-level modules (like domain)
 * don't depend on low-level modules, but both depend on abstractions. Abstractions are
 * defined in this layer and implemented by outer layers.
 */
package org.s8r.domain;