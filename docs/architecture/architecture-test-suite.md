# Architecture Test Suite

This document describes the Samstraumr Architecture Test Suite, which validates compliance with Architecture Decision Records (ADRs) through automated tests.

## Purpose

The Architecture Test Suite ensures that:

1. The codebase complies with architectural decisions documented in ADRs
2. Architectural integrity is maintained as the codebase evolves
3. Violations of architectural principles are detected early
4. Documentation of architecture is kept up-to-date with implementation

## Test Suite Organization

The test suite is organized by architectural principles:

| Test Class | ADR | Purpose |
|------------|-----|---------|
| ComponentBasedArchitectureTest | ADR-0007 | Validates component-based architecture implementation |
| EventDrivenCommunicationTest | ADR-0010 | Validates event-driven communication model |
| HierarchicalIdentitySystemTest | ADR-0008 | Validates hierarchical identity system |
| AcyclicDependencyTest | ADR-0012 | Validates absence of circular dependencies |
| CleanArchitectureComplianceTest | ADR-0003, ADR-0005 | Validates clean architecture implementation |
| StandardizedErrorHandlingTest | ADR-0011 | Validates error handling strategy |

## Running the Tests

The architecture tests can be run using:

```bash
# Architecture Test Suite
mvn test -Dtest=RunArchitectureTests -pl Samstraumr/samstraumr-core

# Architecture Test Suite
mvn test -Dtest=ComponentBasedArchitectureTest -pl Samstraumr/samstraumr-core
```

## Test Details

### Componentbasedarchitecturetest

Validates ADR-0007 (Component-Based Architecture for System Modularity) by ensuring:

- Components implement proper lifecycle management
- Components can be composed into composites
- Composites can connect components
- Machines can manage components
- Component properties can be injected

### Eventdrivencommunicationtest

Validates ADR-0010 (Event-Driven Communication Model) by ensuring:

- Components can publish events
- Components can subscribe to events
- Events are delivered to appropriate handlers
- Event hierarchy is respected (parent event handlers can receive child events)
- Machine state changes trigger appropriate events

### Hierarchicalidentitysystemtest

Validates ADR-0008 (Hierarchical Identity System) by ensuring:

- Components have unique identities
- Component identities can be hierarchical
- Parent-child relationships work correctly
- Component lookups work with hierarchical addressing
- Component paths are correctly generated

### Acyclicdependencytest

Validates ADR-0012 (Enforce Acyclic Dependencies) by ensuring:

- No circular dependencies exist between packages
- Clean Architecture dependency rules are followed
- Test code has no circular dependencies
- Module dependencies (future) will be acyclic

See [Acyclic Dependency Enforcement](acyclic-dependency-enforcement.md) for details on the approach.

### Cleanarchitecturecompliancetest

Validates ADR-0003 (Clean Architecture for System Design) and ADR-0005 (Package Structure Alignment with Clean Architecture) by ensuring:

- Package structure follows Clean Architecture layers
- Dependencies between layers follow the dependency rule
- Domain layer has no external dependencies
- Infrastructure implementations are hidden behind interfaces

### Standardizederrorhandlingtest

Validates ADR-0011 (Standardized Error Handling Strategy) by ensuring:

- Exceptions follow a consistent hierarchy
- Error codes are used appropriately
- Recovery information is available
- Error details are preserved for logging and diagnostics

## Test Utilities

The test suite includes several utilities to assist with architectural analysis:

- **ArchitectureAnalyzer**: Analyzes package dependencies and validates Clean Architecture rules
- **TestComponentFactory**: Creates test components with appropriate configuration
- **TestMachineFactory**: Creates test machines for validating orchestration

## Future Enhancements

Planned improvements to the architecture test suite:

1. **Visual Documentation**: Generate architecture diagrams from code
2. **Architecture Metrics**: Calculate metrics like instability, abstractness, and distance from main sequence
3. **Custom Architecture DSL**: Define architecture rules in a domain-specific language
4. **Integration with CI/CD**: Validate architecture on every pull request

## Conclusion

