# Architecture Test Utilities

This package contains utility classes to support architecture validation tests.

## Purpose

These classes provide the necessary testing infrastructure to validate that our codebase correctly implements 
the architectural decisions documented in our ADRs (Architecture Decision Records).

## Contents

- **TestComponentFactory** - Creates mock component implementations for testing component-based architecture
- **TestMachineFactory** - Creates mock machine implementations for testing state machines and data flow
- **ErrorHandlingTestUtils** - Utilities for testing standardized error handling
- **ArchitectureAnalyzer** - Static analysis tool to validate clean architecture compliance

## Usage

These utilities are designed to be used by the architecture validation tests in the parent package.
Each utility provides mock implementations and helpers tailored to specific architectural aspects:

### Testing Component-Based Architecture (ADR-0007)

```java
// Create mock components
Component component = TestComponentFactory.createComponent("test-component");
CompositeComponent composite = TestComponentFactory.createCompositeComponent("test-composite");

// Add components to composite
composite.addComponent(component);

// Test component composition
assertEquals(1, composite.getComponents().size());
```

### Testing Event-Driven Communication (ADR-0010)

```java
// Create a mock event dispatcher
TestComponentFactory.MockEventDispatcher dispatcher = 
    (TestComponentFactory.MockEventDispatcher) TestComponentFactory.createEventDispatcher();

// Subscribe to events
dispatcher.subscribe(DomainEvent.class, event -> {
    // Handle event
});

// Publish an event
ComponentCreatedEvent event = new ComponentCreatedEvent(
    new ComponentId("test-component"),
    Map.of("key", "value")
);
dispatcher.publish(event);

// Verify published events
List<DomainEvent> publishedEvents = dispatcher.getPublishedEvents();
assertEquals(1, publishedEvents.size());
assertEquals("ComponentCreated", publishedEvents.get(0).getEventType());
```

### Testing Error Handling (ADR-0011)

```java
// Create a component that fails with a specific exception
Component failingComponent = ErrorHandlingTestUtils.createFailingComponent(
    "failing-component", 
    () -> new ComponentInitializationException("Test failure")
);

// Test error handling
assertThrows(ComponentInitializationException.class, () -> {
    failingComponent.initialize();
});

// Test retry logic
Supplier<String> operation = () -> {
    // Operation that might fail
    return "result";
};

String result = ErrorHandlingTestUtils.executeWithRetry(operation, 3, 100);
assertEquals("result", result);
```

### Testing Clean Architecture Compliance (ADR-0003, ADR-0005)

```java
// Check if a dependency follows clean architecture rules
boolean allowed = ArchitectureAnalyzer.isAllowedDependency(
    "org.s8r.infrastructure.persistence.InMemoryRepository",
    "org.s8r.domain.entity.Entity"
);
assertTrue(allowed);

// Analyze all dependencies in the source directory
Map<String, List<String>> violations = ArchitectureAnalyzer.analyzePackageDependencies(
    "/path/to/source/directory"
);
assertTrue(violations.isEmpty(), "There should be no Clean Architecture violations");
```

## Extending

To add new testing utilities:

1. Create a new utility class in this package
2. Implement mock objects or helper methods focused on a specific architectural aspect
3. Document the class with clear Javadoc comments
4. Update this README with information about the new utility

## Important Notes

- These utilities are meant for testing only and should not be used in production code
- The mock implementations are simplified versions of their real counterparts
- Some tests may require additional setup or context beyond what these utilities provide