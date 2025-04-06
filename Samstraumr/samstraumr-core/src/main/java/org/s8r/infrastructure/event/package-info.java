/**
 * Infrastructure event package provides concrete implementations for event handling.
 * <p>
 * This package contains infrastructure-level implementations of the event handling
 * mechanisms defined in the application layer. It provides concrete implementations
 * for dispatching, subscribing to, and processing events within the system.
 * </p>
 * <p>
 * Key components in this package:
 * <ul>
 *   <li>InMemoryEventDispatcher - Memory-based implementation of event dispatcher</li>
 *   <li>LoggingEventHandler - Event handler that logs events</li>
 *   <li>DataFlowEventHandler - Handler for data flow related events</li>
 * </ul>
 * </p>
 * <p>
 * This package is part of the infrastructure layer in Clean Architecture, providing
 * concrete implementations of interfaces defined in the application layer. These
 * implementations are technology-specific and may depend on external frameworks or
 * libraries.
 * </p>
 */
package org.s8r.infrastructure.event;