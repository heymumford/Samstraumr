/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/**
 * Infrastructure event package provides concrete implementations for event handling.
 *
 * <p>This package contains infrastructure-level implementations of the event handling mechanisms
 * defined in the application layer. It provides concrete implementations for dispatching,
 * subscribing to, and processing events within the system.
 *
 * <p>Key components in this package:
 *
 * <ul>
 *   <li>InMemoryEventDispatcher - Memory-based implementation of event dispatcher
 *   <li>LoggingEventHandler - Event handler that logs events
 *   <li>DataFlowEventHandler - Handler for data flow related events
 * </ul>
 *
 * <p>This package is part of the infrastructure layer in Clean Architecture, providing concrete
 * implementations of interfaces defined in the application layer. These implementations are
 * technology-specific and may depend on external frameworks or libraries.
 */
package org.s8r.infrastructure.event;
