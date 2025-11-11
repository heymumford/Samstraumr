<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# README

This document describes the implementation of Clean Architecture principles in the Samstraumr framework.

## Overview

The Samstraumr framework has been refactored to follow the Clean Architecture principles defined by Robert C. Martin. This architecture organizes the system into concentric layers with a strict dependency rule: dependencies only point inward. This ensures that inner layers are independent of outer layers, making the system more maintainable, testable, and adaptable to changing requirements.

## Architecture Layers

Samstraumr's implementation of Clean Architecture consists of the following layers:

### 1. domain layer

The innermost layer containing business entities, value objects, and domain logic. This layer is independent of any frameworks, libraries, or external concerns.

**Key Components:**
- `Component` - Core domain entity representing a processing unit
- `ComponentId` - Value object for component identity
- `LifecycleState` - Enumeration of component lifecycle states
- `DomainEvent` - Base class for domain events
- Domain exceptions for handling business rule violations

**Package Structure:**

```
org.samstraumr.domain
├── component
│   ├── Component.java
│   ├── composite
│   │   ├── ComponentConnection.java
│   │   ├── CompositeComponent.java
│   │   ├── CompositeFactory.java
│   │   ├── CompositeType.java
│   │   ├── ConnectionException.java
│   │   └── ConnectionType.java
│   ├── monitoring
│   │   ├── HealthMonitor.java
│   │   ├── MetricsCollector.java
│   │   └── MonitoringFactory.java
│   └── pattern
│       ├── AggregatorComponent.java
│       ├── FilterComponent.java
│       ├── PatternFactory.java
│       ├── RouterComponent.java
│       └── TransformerComponent.java
├── event
│   ├── ComponentConnectionEvent.java
│   ├── ComponentCreatedEvent.java
│   ├── ComponentDataEvent.java
│   ├── ComponentStateChangedEvent.java
│   ├── DomainEvent.java
│   └── MachineStateChangedEvent.java
├── exception
│   ├── ComponentException.java
│   ├── ComponentInitializationException.java
│   ├── ComponentNotFoundException.java
│   ├── DuplicateComponentException.java
│   ├── InvalidOperationException.java
│   └── InvalidStateTransitionException.java
├── identity
│   ├── ComponentHierarchy.java
│   └── ComponentId.java
├── lifecycle
│   └── LifecycleState.java
└── machine
    ├── Machine.java
    ├── MachineFactory.java
    ├── MachineState.java
    └── MachineType.java
```

### 2. application layer

Contains application-specific business rules and use cases. This layer defines interfaces (ports) for external services required by the domain.

**Key Components:**
- Service classes implementing use cases
- DTOs for data transfer across boundaries
- Port interfaces for external services

**Package Structure:**

```
org.samstraumr.application
├── dto
│   ├── ComponentDto.java
│   ├── CompositeComponentDto.java
│   ├── ConnectionDto.java
│   └── MachineDto.java
├── port
│   ├── ComponentRepository.java
│   ├── EventDispatcher.java
│   ├── LoggerPort.java
│   └── MachineRepository.java
└── service
    ├── ComponentService.java
    ├── DataFlowService.java
    └── MachineService.java
```

### 3. infrastructure layer

Implements the interfaces defined in the application layer, providing concrete implementations for external services.

**Key Components:**
- Repository implementations
- Logging implementations
- Configuration and dependency management
- Event dispatching

**Package Structure:**

```
org.samstraumr.infrastructure
├── config
│   ├── Configuration.java
│   └── DependencyContainer.java
├── event
│   ├── DataFlowEventHandler.java
│   ├── InMemoryEventDispatcher.java
│   └── LoggingEventHandler.java
├── logging
│   ├── ConsoleLogger.java
│   ├── LoggerFactory.java
│   └── Slf4jLogger.java
└── persistence
    └── InMemoryMachineRepository.java
```

### 4. interface adapters layer

Connects the application to external agents, converting data between the application and external formats.

**Key Components:**
- Repository adapters
- User interface adapters
- API adapters
- Controller classes

**Package Structure:**

```
org.samstraumr.adapter
├── in
│   └── cli
│       └── ComponentCliAdapter.java
└── out
    └── InMemoryComponentRepository.java
```

### 5. frameworks and drivers layer

Contains frameworks, tools, and delivery mechanisms.

**Key Components:**
- Main application entry point
- Framework configurations
- CLI/GUI implementations

**Package Structure:**

```
org.samstraumr
├── Samstraumr.java
└── app
    └── CliApplication.java
```

## Key Design Principles

### Dependency rule

The most fundamental rule in Clean Architecture is the Dependency Rule: dependencies only point inward. This means that inner layers know nothing about outer layers. This is achieved through:

1. **Abstractions**: Inner layers define interfaces that outer layers implement
2. **Dependency injection**: Dependencies are provided from outside
3. **DTO objects**: For transferring data between layers

### Separation of concerns

Each layer has a distinct responsibility:
- Domain Layer: Business rules
- Application Layer: Use cases and application flow
- Infrastructure Layer: External service implementations
- Interface Adapters: Conversion between formats
- Frameworks Layer: External tools and frameworks

### Domain-centered design

The domain model is at the center of the application. Business rules are centralized in the domain layer, making them easy to understand and modify.

### Use case-driven

The application behavior is defined by use cases in the application service layer. Each service method typically corresponds to a user action or system behavior.

### Interface adapters

External systems are accessed through adapters that implement domain-defined interfaces (ports). This allows the domain to remain pure while still interacting with external systems.

## Event-Driven Architecture

The Samstraumr framework leverages an event-driven architecture for component communication, which aligns well with Clean Architecture principles:

1. Domain entities raise events when important things happen
2. The application layer dispatches these events
3. Handlers in various layers respond to events
4. This allows for loose coupling between components

Further details on the event-driven architecture can be found in the [Event-Driven Architecture](../event/readme.md) document.

## Benefits Achieved

The transition to Clean Architecture has provided several benefits:

1. **Maintainability**: Clear separation of concerns makes the code easier to understand and modify
2. **Testability**: Domain logic can be tested independently of external concerns
3. **Flexibility**: The system can adapt to changing requirements or external services
4. **Independence**: Core business logic is independent of frameworks and libraries
5. **Clarity**: The architecture clearly communicates the intent and structure of the system

## Resources

- [The Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Clean Architecture: A Craftsman's Guide to Software Structure and Design](https://www.amazon.com/Clean-Architecture-Craftsmans-Software-Structure/dp/0134494164)
