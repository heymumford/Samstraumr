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
 * Domain events in the Samstraumr framework.
 * 
 * <p>This package contains domain events that represent significant
 * occurrences within the system. Domain events are used to communicate
 * between different parts of the system without creating direct dependencies.</p>
 * 
 * <h2>Key Classes</h2>
 * <ul>
 *   <li>{@link org.s8r.domain.event.DomainEvent} - Base event class</li>
 *   <li>{@link org.s8r.domain.event.ComponentCreatedEvent} - Component creation events</li>
 *   <li>{@link org.s8r.domain.event.ComponentStateChangedEvent} - Component state changes</li>
 *   <li>{@link org.s8r.domain.event.MachineStateChangedEvent} - Machine state changes</li>
 * </ul>
 * 
 * <h2>Related Packages</h2>
 * <ul>
 *   <li>{@link org.s8r.domain.component} - Component domain model that generates events</li>
 *   <li>{@link org.s8r.domain.machine} - Machine domain model that generates events</li>
 *   <li>{@link org.s8r.application.port.EventDispatcher} - Port for dispatching events</li>
 *   <li>{@link org.s8r.infrastructure.event} - Event handling infrastructure</li>
 * </ul>
 */
package org.s8r.domain.event;
