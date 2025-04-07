# Samstraumr Adapter Pattern Library

This document provides a comprehensive guide to adapter implementations in the Samstraumr project following Clean Architecture principles. It serves as a reference for developers implementing new adapters or maintaining existing ones.

## Table of Contents

1. [Introduction to Adapters in Clean Architecture](#introduction-to-adapters-in-clean-architecture)
2. [Adapter Design Patterns](#adapter-design-patterns)
3. [Reference Implementations](#reference-implementations)
4. [Testing Adapters](#testing-adapters)
5. [Common Pitfalls and Solutions](#common-pitfalls-and-solutions)
6. [Best Practices](#best-practices)

## Introduction to Adapters in Clean Architecture

In Clean Architecture, adapters are responsible for converting between the interfaces and models used by the application core and the interfaces and models provided by external frameworks, libraries, or legacy systems. They serve as a bridge between the domain and infrastructure layers.

### Key Concepts

- **Ports**: Interfaces defined by the application core that specify what it needs from external systems
- **Adapters**: Implementations of port interfaces that connect to external systems
- **Domain-first design**: Adapters adapt to the domain, not the other way around
- **Dependency inversion**: The domain depends on abstractions (ports), not on concrete implementations (adapters)

## Adapter Design Patterns

The Samstraumr project employs several adapter design patterns to maintain clean architectural boundaries:

### 1. Port Adapter Pattern

Adapters implement port interfaces defined by the application core. This pattern ensures the application core remains isolated from infrastructure concerns.

**Example**:
```java
// In application layer - port interface
public interface LoggerPort {
    void debug(String message, Object... args);
    void info(String message, Object... args);
    void warn(String message, Object... args);
    void error(String message, Object... args);
}

// In infrastructure layer - adapter implementation
public class Slf4jLogger implements LoggerPort {
    private final Logger logger;
    
    public Slf4jLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }
    
    @Override
    public void debug(String message, Object... args) {
        logger.debug(message, args);
    }
    
    // Other method implementations...
}
```

### 2. Factory Adapter Pattern

Factories create and configure adapters, providing a clean way to instantiate complex adapter types.

**Example**:
```java
// Factory adapter in infrastructure layer
public class MachineFactoryAdapter {
    private final LoggerPort logger;
    
    public MachineFactoryAdapter(LoggerPort logger) {
        this.logger = logger;
    }
    
    public MachinePort createMachine(MachineType type, String name) {
        // Implementation creates a machine and returns adapter
    }
}
```

### 3. Bidirectional Adapter Pattern

Adapters that convert between domain models and infrastructure models in both directions, ensuring clean separation while enabling data flow.

**Example**:
```java
public class ComponentAdapter {
    // Convert domain component to port interface
    public static ComponentPort createComponentPort(Component domainComponent) {
        // Implementation converts domain component to port
    }
    
    // Convert component port to domain component (if needed)
    public static Component toDomainComponent(ComponentPort componentPort) {
        // Implementation converts port to domain component
    }
}
```

### 4. Event Adapter Pattern

Adapters that bridge the event systems of the domain and infrastructure layers, ensuring events flow between layers without creating dependencies.

**Example**:
```java
public class EventPublisherAdapter implements EventPublisherPort {
    private final EventDispatcher eventDispatcher;
    
    @Override
    public void publish(DomainEvent event) {
        // Convert domain event to infrastructure event if needed
        // Publish to event dispatcher
    }
}
```

## Reference Implementations

The project contains several reference implementations that serve as templates for new adapters:

### MachineAdapter

The `MachineAdapter` demonstrates how to adapt between domain and component machine types, with proper handling of lifecycle states, identities, and operations.

**Key Features**:
- Factory methods for creating adapters
- Bidirectional conversion between domain and component models
- Proper implementation of all port methods
- Error handling for type conversions
- Default method implementations where appropriate

[View MachineAdapter Source](../../Samstraumr/samstraumr-core/src/main/java/org/s8r/adapter/MachineAdapter.java)

### DataFlowEventHandler

The `DataFlowEventHandler` demonstrates how to implement a port interface that manages event flow between components.

**Key Features**:
- Thread-safe implementation using concurrent collections
- Proper error handling for event delivery
- Clear logging of operations
- Null-safe operations

[View DataFlowEventHandler Source](../../Samstraumr/samstraumr-core/src/main/java/org/s8r/infrastructure/event/DataFlowEventHandler.java)

## Testing Adapters

The Samstraumr project employs a comprehensive approach to testing adapters:

### Contract Tests

Contract tests verify that adapters correctly implement their port interfaces. These tests focus on the behavior expected by the application core, not the internal implementation details.

**Example**:
```java
@Test
@DisplayName("Should manage subscriptions correctly")
public void subscriptionManagementTests() {
    // Test interface behavior, not implementation details
    portUnderTest.subscribe(sourceId, "test-channel", handler);
    
    // Verify expected behavior
    Set<String> subscriptions = portUnderTest.getComponentSubscriptions(sourceId);
    assertFalse(subscriptions.isEmpty());
    assertTrue(subscriptions.contains("test-channel"));
}
```

### Integration Tests

Integration tests verify that adapters interact correctly with their external systems and with each other.

### Unit Tests

Unit tests verify the internal implementation of adapters, focusing on specific functionality.

### Test Fixtures

The `TestFixtureFactory` provides standard implementations for use in tests, ensuring consistency across test classes.

## Common Pitfalls and Solutions

### 1. Domain Leakage

**Pitfall**: Infrastructure concerns leaking into the domain layer through adapter implementations.

**Solution**: Ensure adapters fully encapsulate all infrastructure concerns and expose only domain-compatible interfaces to the application core.

### 2. Circular Dependencies

**Pitfall**: Circular dependencies between domain and infrastructure layers.

**Solution**: Ensure the domain layer defines port interfaces and the infrastructure layer implements them, not the other way around.

### 3. Type Conversion Complexity

**Pitfall**: Complex conversions between domain and infrastructure models.

**Solution**: 
- Use factory methods to centralize conversion logic
- Create value objects or DTOs to facilitate conversions
- Consider builder pattern for complex conversions

### 4. Null Handling

**Pitfall**: Null values causing unexpected behavior or exceptions.

**Solution**:
- Validate inputs and return appropriate default values or empty collections
- Use Optional for values that may be missing
- Document null handling behavior in interface contracts

### 5. Stateful Adapters

**Pitfall**: Adapters with mutable state that can lead to concurrency issues.

**Solution**:
- Prefer immutable adapters where possible
- Use thread-safe collections for shared state
- Document threading assumptions

## Best Practices

1. **Follow the Dependency Rule**: Dependencies should always point inward, from infrastructure to application to domain.

2. **Design Domain First**: Create domain models and port interfaces before implementing adapters.

3. **Use Factory Methods**: Create factory methods or factory classes for adapter creation to centralize conversion logic.

4. **Test Interface Contracts**: Create contract tests to verify adapter implementations fulfill port interface requirements.

5. **Document Adapter Behavior**: Clearly document how adapters handle edge cases, errors, and null values.

6. **Prefer Composition Over Inheritance**: Compose adapters from smaller, focused adapters rather than creating complex inheritance hierarchies.

7. **Centralize Conversions**: Create utility methods for converting between domain and infrastructure models.

8. **Validate Inputs**: Always validate inputs and handle edge cases gracefully.

9. **Log Adapter Operations**: Log adapter operations for debugging and diagnostics.

10. **Minimize Adapter State**: Keep adapters as stateless as possible to avoid concurrency issues.

---

This pattern library will be updated as new patterns emerge and existing patterns evolve.