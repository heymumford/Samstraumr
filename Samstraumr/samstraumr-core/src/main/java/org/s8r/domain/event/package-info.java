/**
 * Domain event package contains the event entities for domain-level events.
 * <p>
 * This package defines the events that represent significant changes or occurrences
 * within the domain model. These events are used for communication between different
 * parts of the system and for capturing changes in domain state.
 * </p>
 * <p>
 * Key concepts in this package:
 * <ul>
 *   <li>DomainEvent - Base class for all domain events</li>
 *   <li>ComponentCreatedEvent - Event representing component creation</li>
 *   <li>ComponentStateChangedEvent - Event for component state changes</li>
 *   <li>ComponentConnectionEvent - Event for component connection changes</li>
 *   <li>ComponentDataEvent - Event for data processing in components</li>
 *   <li>MachineStateChangedEvent - Event for machine state changes</li>
 * </ul>
 * </p>
 * <p>
 * This package follows the principles of Domain Events in Domain-Driven Design,
 * providing a mechanism for loose coupling between different parts of the system
 * while maintaining a record of significant domain changes.
 * </p>
 */
package org.s8r.domain.event;