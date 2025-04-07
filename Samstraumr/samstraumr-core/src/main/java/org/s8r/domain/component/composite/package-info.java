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
 * Composite component domain model.
 * <p>
 * This package contains the domain model for composite components, which are containers
 * that can hold and manage multiple child components. Composites form a crucial part of
 * the S8r framework's structural model, implementing the Composite design pattern.
 * </p>
 * <p>
 * Key classes in this package:
 * <ul>
 *   <li>{@link org.s8r.domain.component.composite.CompositeComponent} - Core composite entity</li>
 *   <li>{@link org.s8r.domain.component.composite.CompositeException} - Domain exceptions</li>
 *   <li>{@link org.s8r.domain.component.composite.CompositeFactory} - Factory for creating composites</li>
 * </ul>
 * </p>
 * <p>
 * Each composite supports adding, removing, and connecting components, providing 
 * a hierarchical structure that can represent complex systems. Composites implement
 * the Component interface, allowing them to be treated uniformly with simple components.
 * </p>
 * <p>
 * Integration with Clean Architecture:
 * <ul>
 *   <li>Composites are part of the domain layer, containing core business logic</li>
 *   <li>Port interfaces ({@link org.s8r.domain.component.port.CompositeComponentPort}) define
 *       the contract that allows adapter implementations to work with composites</li>
 *   <li>Adapter implementations ({@link org.s8r.adapter.ComponentAdapter}) convert between
 *       concrete CompositeComponent implementations and CompositeComponentPort interfaces</li>
 *   <li>This ensures higher-level modules (application layer) can depend on abstractions
 *       instead of concrete implementations</li>
 * </ul>
 * </p>
 * <p>
 * Related packages:
 * <ul>
 *   <li>{@link org.s8r.domain.component.port} - Port interfaces for composites</li>
 *   <li>{@link org.s8r.domain.component} - Base component model</li>
 *   <li>{@link org.s8r.domain.machine} - Higher-level structures that use composites</li>
 *   <li>{@link org.s8r.adapter} - Adapters for composite components</li>
 *   <li>{@link org.s8r.component.composite} - Legacy composite abstraction layer</li>
 * </ul>
 * </p>
 */
package org.s8r.domain.component.composite;
