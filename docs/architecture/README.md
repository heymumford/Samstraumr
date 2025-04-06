<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Samstraumr Architecture Documentation

This directory contains the high-level architecture documentation for the Samstraumr (S8r) framework.

## Core Documents

- [Strategy](strategy.md) - Strategic goals and architectural principles
- [Implementation](implementation.md) - Implementation details and code structure
- [Testing Strategy](testing-strategy.md) - Testing approach and implementation
- [Circular Dependency Prevention](circular-dependency-prevention.md) - Approach to preventing circular dependencies
- [Clean Architecture Overview](clean/README.md) - Clean Architecture implementation details
- [Event-Driven Architecture](event/README.md) - Event system design and implementation
- [Integration Patterns](patterns/README.md) - Common integration patterns implemented in the framework
- [Monitoring and Management](monitoring/README.md) - System monitoring and management capabilities

## C4 Model Architecture Diagrams

The architecture is documented using the [C4 model](https://c4model.com/) approach, which provides multiple levels of abstraction:

1. **Context Diagram**: Shows the system and its relationships with users and other systems
2. **Container Diagram**: Shows the high-level technical components (applications, data stores, etc.)
3. **Component Diagram**: Shows how containers are composed of components
4. **Code Diagram**: Shows how components are implemented as code

These diagrams are automatically generated during the build process and stored in the [diagrams](../diagrams) directory.

### Viewing the Latest Diagrams

The latest generated diagrams can be found in the [diagrams directory](../diagrams/README.md).

### Generating Diagrams

The C4 model diagrams are automatically generated during the Maven build process. To manually generate them, run:

```bash
./bin/generate-diagrams.sh
```

For asynchronous generation (useful during development):

```bash
./bin/generate-diagrams.sh --async
```

For more options:

```bash
./bin/generate-diagrams.sh --help
```

## Architecture Decision Records

Important architectural decisions are documented as Architecture Decision Records (ADRs) in the [decisions](./decisions) directory.

To create a new ADR, use the `new-adr` script:

```bash
./bin/new-adr "Title of the decision"
```

## Architecture Overview

Samstraumr is built on Clean Architecture principles as defined by Robert C. Martin (Uncle Bob). The architecture is organized into concentric layers with dependencies pointing inward, ensuring a clear separation of concerns and making the system more maintainable and testable.

### Architectural Layers

1. **Domain Layer** - The innermost layer that contains business entities, value objects, and domain logic.
2. **Application Layer** - Contains application-specific business rules, use cases, and ports (interfaces) for external services.
3. **Infrastructure Layer** - Implements the interfaces defined in the application layer, providing concrete implementations for external services.
4. **Presentation Layer** - Handles user interactions, whether through CLI, GUI, or API endpoints.

### Key Architectural Principles

- **Dependency Rule**: Dependencies only point inward. Inner layers know nothing about outer layers.
- **Domain-Centered**: The domain model is at the core, immune to external changes.
- **Use Case-Driven**: Application behavior is defined by use cases.
- **Interface Adapters**: External systems are accessed through adapters implementing domain-defined ports.
- **Frameworks Independence**: The core logic doesn't depend on frameworks or libraries.
- **No Circular Dependencies**: Package and module dependencies must be acyclic. All circular dependencies are detected and prevented.

## Implementation Status

The Samstraumr framework has successfully implemented Clean Architecture:

1. ✅ Core component model implementation
2. ✅ Identity and lifecycle framework
3. ✅ Logging infrastructure
4. ✅ Component testing infrastructure
5. ✅ Composite pattern implementation
6. ✅ Machine abstractions
7. ✅ Clean Architecture implementation
8. ✅ Event-driven communication
9. ✅ Integration patterns (Transformer, Filter, Aggregator, Router)
10. ✅ Monitoring and management capabilities
11. ✅ Circular dependency detection and prevention
12. ✅ Comprehensive architecture validation testing

## Key Design Patterns

Samstraumr employs several design patterns to achieve its architectural goals:

- **Factory Pattern**: Used for creating complex objects (Components, Machines)
- **Observer Pattern**: Core of the event-driven architecture
- **Adapter Pattern**: Connects different parts of the system without creating direct dependencies
- **Composite Pattern**: Allows components to be composed into complex structures
- **Strategy Pattern**: Used for pluggable behaviors
- **Decorator Pattern**: Used for component wrappers like monitoring
- **Command Pattern**: Used in event processing

## Related Documentation

- [Main Documentation](../../README.md) - Return to main Samstraumr documentation
- [Core Concepts](../concepts/core-concepts.md) - Fundamental building blocks and principles
- [Getting Started](../guides/getting-started.md) - Practical guide to using the architecture
