# Domain Layer

This directory contains the domain model that forms the core business logic of the S8r framework, following Clean Architecture principles.

## Package Organization

The domain layer uses a standard package structure that organizes classes by domain concept:

- `org.s8r.domain.component` - Component domain model
  - `Component.java` - Domain component entity
  - `.composite` - Composite component domain entities
  - `.pattern` - Pattern-based domain entities

- `org.s8r.domain.event` - Domain events
  - `DomainEvent.java` - Base event class
  - `ComponentCreatedEvent.java` - Component creation events
  - `ComponentStateChangedEvent.java` - Component state changes

- `org.s8r.domain.identity` - Identity domain model
  - `ComponentId.java` - Component identity value object
  - `ComponentHierarchy.java` - Component hierarchy management

- `org.s8r.domain.machine` - Machine domain model
  - `Machine.java` - Domain machine entity
  - `MachineFactory.java` - Factory for machine creation
  - `MachineState.java` - Machine states

- `org.s8r.domain.lifecycle` - Lifecycle management
  - `LifecycleState.java` - Component lifecycle states

- `org.s8r.domain.exception` - Domain exceptions
  - `DomainException.java` - Base exception class
  - `ComponentException.java` - Component-specific exceptions

## Domain Concepts

The domain layer implements core business logic following Domain-Driven Design concepts:

- **Entities**: Business objects with identity (Component, Machine)
- **Value Objects**: Immutable objects defined by their attributes (Identity)
- **Aggregates**: Clusters of entities and value objects with consistency boundaries
- **Domain Events**: Record of something significant that occurred in the domain
- **Services**: Operations that don't belong to any entity
- **Repositories**: Interfaces for persistence operations (defined as ports)

## Clean Architecture

This layer follows Clean Architecture principles:

1. **Framework Independence**: No dependencies on external frameworks
2. **Testability**: Business rules can be tested without UI, database, etc.
3. **UI Independence**: UI can change without changing business rules
4. **Database Independence**: Business rules not bound to a specific database
5. **Independence from External Agencies**: Business rules don't know about interfaces to the outside world

## Related Packages

- `org.s8r.application.service` - Application services that use domain model
- `org.s8r.application.port` - Ports to interact with the domain model
- `org.s8r.infrastructure.persistence` - Repository implementations
- `org.s8r.component` - Component layer (more abstract, fundamental interfaces)
