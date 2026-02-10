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
 * Domain event classes for the S8r framework.
 *
 * <p>This package contains all domain events in the system, following Domain-Driven Design
 * principles. Domain events represent facts that have occurred in the domain and help implement a
 * reactive, event-driven architecture that follows Clean Architecture principles. They're used for:
 *
 * <ul>
 *   <li>Notifying different parts of the system about state changes
 *   <li>Implementing the Observer pattern across bounded contexts
 *   <li>Supporting eventual consistency in distributed systems
 *   <li>Enabling loose coupling between components
 *   <li>Facilitating audit trails and event sourcing
 * </ul>
 *
 * <p>Domain events in this package follow these characteristics:
 *
 * <ul>
 *   <li>All events extend the abstract {@link org.s8r.domain.event.DomainEvent} base class
 *   <li>Events are immutable value objects with all necessary context
 *   <li>Events use past tense to indicate they represent something that has happened
 *   <li>Events include a unique identifier, timestamp, and source component ID
 *   <li>Some events also track if they originated from port interfaces vs. direct implementations,
 *       supporting Clean Architecture's Dependency Inversion Principle
 * </ul>
 *
 * <p>Main event categories include:
 *
 * <ul>
 *   <li>Lifecycle events:
 *       <ul>
 *         <li>{@link org.s8r.domain.event.ComponentCreated} - Component creation
 *         <li>{@link org.s8r.domain.event.ComponentCreatedEvent} - Legacy version (to be
 *             deprecated)
 *         <li>{@link org.s8r.domain.event.ComponentStateChangedEvent} - Component state changes
 *         <li>{@link org.s8r.domain.event.MachineStateChangedEvent} - Machine state changes
 *       </ul>
 *   <li>Structural events:
 *       <ul>
 *         <li>{@link org.s8r.domain.event.ComponentConnectionEvent} - Component connection changes
 *       </ul>
 *   <li>Data flow events:
 *       <ul>
 *         <li>{@link org.s8r.domain.event.ComponentDataEvent} - Component data flow
 *       </ul>
 * </ul>
 *
 * <p>Naming note: The framework is transitioning to standardized event naming without the "Event"
 * suffix, following modern domain event conventions. Both versions are currently supported, with
 * older versions gradually being replaced.
 *
 * <h2>Related Packages</h2>
 *
 * <ul>
 *   <li>{@link org.s8r.domain.component} - Component domain model that generates events
 *   <li>{@link org.s8r.domain.component.port} - Port interfaces that generate events
 *   <li>{@link org.s8r.domain.machine} - Machine domain model that generates events
 *   <li>{@link org.s8r.application.port.EventDispatcher} - Port for dispatching events
 *   <li>{@link org.s8r.infrastructure.event} - Event handling infrastructure
 * </ul>
 */
package org.s8r.domain.event;
