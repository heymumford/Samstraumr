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
 * Component domain model in the Samstraumr framework - core building blocks of the system.
 * 
 * <p>This package contains the domain model for components, which are the fundamental
 * building blocks of the Samstraumr framework. Components encapsulate domain logic
 * and behavior, forming the heart of the business rules in Clean Architecture.
 * </p>
 * 
 * <p>The component domain model follows these principles:
 * <ul>
 *   <li>Rich Domain Model - Components contain both data and behavior</li>
 *   <li>Domain-Driven Design - Components map to concepts in the problem domain</li>
 *   <li>Entity Pattern - Components have identity and lifecycle</li>
 *   <li>Aggregate Pattern - Components can form hierarchical structures</li>
 *   <li>Event-Driven - Components raise domain events when significant things happen</li>
 * </ul>
 * </p>
 * 
 * <h2>Key Classes</h2>
 * <ul>
 *   <li>{@link org.s8r.domain.component.Component} - Domain component entity</li>
 * </ul>
 * 
 * <h2>Clean Architecture Integration</h2>
 * <ul>
 *   <li>Components are part of the domain layer, the innermost circle of Clean Architecture</li>
 *   <li>Port interfaces in {@link org.s8r.domain.component.port} define abstractions that 
 *       higher-level modules can depend on</li>
 *   <li>Adapter implementations in {@link org.s8r.adapter} convert between concrete
 *       implementations and port interfaces</li>
 *   <li>Domain events in {@link org.s8r.domain.event} allow components to notify other
 *       parts of the system without direct dependencies</li>
 *   <li>Application services in {@link org.s8r.application.service} orchestrate components
 *       to fulfill use cases</li>
 * </ul>
 * 
 * <h2>Subpackages</h2>
 * <ul>
 *   <li>{@link org.s8r.domain.component.composite} - Composite component domain entities</li>
 *   <li>{@link org.s8r.domain.component.pattern} - Pattern-based domain entities</li>
 *   <li>{@link org.s8r.domain.component.monitoring} - Component monitoring entities</li>
 *   <li>{@link org.s8r.domain.component.port} - Port interfaces for Clean Architecture</li>
 * </ul>
 * 
 * <h2>Related Packages</h2>
 * <ul>
 *   <li>{@link org.s8r.domain.event} - Domain events related to components</li>
 *   <li>{@link org.s8r.domain.identity} - Identity management for components</li>
 *   <li>{@link org.s8r.domain.lifecycle} - Lifecycle states for components</li>
 *   <li>{@link org.s8r.domain.machine} - Higher-level structures that use components</li>
 *   <li>{@link org.s8r.adapter} - Adapters for component interfaces</li>
 *   <li>{@link org.s8r.component} - Legacy component abstraction layer</li>
 * </ul>
 */
package org.s8r.domain.component;
