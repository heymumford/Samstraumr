# Using Domain Adapters

This guide explains how to use port interfaces and adapters to implement Clean Architecture principles in the Samstraumr framework.

## Introduction

The Samstraumr framework has implemented Clean Architecture to improve modularity, testability, and maintainability. A key part of this implementation is the use of port interfaces and adapters, which allow different architectural layers to communicate without direct dependencies, following the Dependency Inversion Principle.

### Recent updates (april 2025)

As of April 2025, we have completed the implementation of port interfaces in the infrastructure layer, particularly in the repository implementations:

- `InMemoryComponentRepository` and `InMemoryMachineRepository` now use port interfaces (`ComponentPort` and `MachinePort`) instead of concrete implementations
- The `DataFlowService` has been updated to work with port interfaces
- Unit tests have been added to validate that port interfaces work correctly with adapters, repositories, and services
- Documentation has been updated to reflect the current state of implementation

## Clean Architecture Overview

Clean Architecture organizes code into concentric layers:

1. **Domain Layer** (innermost) - Contains business entities and rules
2. **Application Layer** - Contains use cases that orchestrate domain entities
3. **Infrastructure Layer** (outermost) - Contains frameworks, drivers, and tools

The fundamental rule is that dependencies can only point inward. Higher-level layers can depend on lower-level layers, but not vice versa. This is achieved through interfaces and adapters.

## Port Interfaces

Port interfaces in the Samstraumr framework are defined in the domain layer and include:

- **ComponentPort**: Base interface for all components
- **CompositeComponentPort**: Interface for composite components
- **MachinePort**: Interface for machines that orchestrate composites

These interfaces define the operations that can be performed on components without specifying their implementation details.

## Adapter Components

The framework includes several adapters in the `org.s8r.adapter` package:

- **ComponentAdapter**: Converts between Component domain entities and ComponentPort interfaces
- **MachineAdapter**: Converts between Machine domain entities and MachinePort interfaces
- **PortAdapterFactory**: Factory for creating port interfaces from domain entities
- **LegacyAdapterFactory**: Factory for creating adapters for legacy code

## Basic Usage Pattern

The basic pattern for using port interfaces and adapters involves:

1. Creating a port adapter factory
2. Using the factory to create port interfaces from domain entities
3. Working with the port interfaces instead of concrete implementations

### Example: creating and using port interfaces

```java
// Create adapter factory
LoggerPort logger = DependencyContainer.getInstance().getLogger(MyClass.class);
PortAdapterFactory adapterFactory = new PortAdapterFactory(logger);

// Create a component port from a domain component
org.s8r.domain.component.Component domainComponent = createComponent();
ComponentPort componentPort = adapterFactory.createComponentPort(domainComponent);

// Work with the component through its port interface
componentPort.activate();

// No need to convert back - the adapter transparently delegates to the domain component
```

### Example: working with legacy code

```java
// Create adapter factory
LoggerPort logger = DependencyContainer.getInstance().getLogger(MyClass.class);
LegacyAdapterFactory legacyAdapterFactory = 
    (LegacyAdapterFactory)DependencyContainer.getInstance().getLegacyAdapterResolver();

// Create a machine port from a legacy tube machine
org.s8r.tube.machine.Machine tubeMachine = createTubeMachine();
MachinePort machinePort = legacyAdapterFactory.createMachinePort(tubeMachine);

// Work with the machine through its port interface
machinePort.start();
```

## Implementation Strategies

### Strategy 1: port interfaces in service layer

Use port interfaces in the service layer to depend on abstractions rather than concrete implementations:

```java
public class ComponentService {
    private final ComponentRepository repository;
    private final LoggerPort logger;
    private final EventDispatcher eventDispatcher;
    
    public ComponentService(
            ComponentRepository repository, 
            LoggerPort logger,
            EventDispatcher eventDispatcher) {
        this.repository = repository;
        this.logger = logger;
        this.eventDispatcher = eventDispatcher;
    }
    
    public void activateComponent(ComponentId componentId) {
        // Repository returns a ComponentPort (interface) rather than concrete implementation
        Optional<ComponentPort> componentOpt = repository.findById(componentId);
        
        if (componentOpt.isPresent()) {
            ComponentPort component = componentOpt.get();
            
            // Work with the component through its port interface
            component.activate();
            
            // Save the component back to the repository
            repository.save(component);
            
            // Handle domain events
            List<DomainEvent> events = component.getDomainEvents();
            for (DomainEvent event : events) {
                eventDispatcher.dispatch(event);
            }
            component.clearEvents();
            
            logger.info("Component activated: {}", componentId.getIdString());
        } else {
            throw new ComponentNotFoundException(componentId);
        }
    }
}
```

### Strategy 2: repository with port interfaces

Implement repositories that work with port interfaces instead of concrete implementations:

```java
public class InMemoryComponentRepository implements ComponentRepository {
    private final Map<String, ComponentPort> componentStore = new HashMap<>();
    private final LoggerPort logger;
    
    public InMemoryComponentRepository(LoggerPort logger) {
        this.logger = logger;
        logger.debug("Initialized InMemoryComponentRepository");
    }
    
    @Override
    public void save(ComponentPort component) {
        componentStore.put(component.getId().getIdString(), component);
        logger.debug("Saved component: {}", component.getId().getIdString());
    }
    
    @Override
    public Optional<ComponentPort> findById(ComponentId id) {
        return Optional.ofNullable(componentStore.get(id.getIdString()));
    }
    
    @Override
    public List<ComponentPort> findAll() {
        return new ArrayList<>(componentStore.values());
    }
    
    @Override
    public List<ComponentPort> findChildren(ComponentId parentId) {
        if (parentId == null) {
            return new ArrayList<>();
        }
        
        String parentIdStr = parentId.getIdString();
        return componentStore.values().stream()
            .filter(component -> 
                component.getLineage().stream()
                    .anyMatch(entry -> entry.contains(parentIdStr)))
            .collect(Collectors.toList());
    }
    
    @Override
    public void delete(ComponentId id) {
        componentStore.remove(id.getIdString());
        logger.debug("Deleted component: {}", id.getIdString());
    }
}
```

### Strategy 3: using port interfaces for new code

Implement new functionality using port interfaces to ensure compatibility with both domain entities and legacy code:

```java
// Legacy method that expects a concrete Machine implementation
public void legacyProcessMachine(org.s8r.domain.machine.Machine machine) {
    // ... legacy processing ...
}

// New method that works with MachinePort interface
public void processMachine(MachinePort machinePort) {
    // This method can accept both:
    // - Domain Machine wrapped in a port adapter
    // - Legacy Machine wrapped in a port adapter
    // - Any other implementation of MachinePort
    
    // Process the machine through its port interface
    if (!machinePort.start()) {
        logger.error("Failed to start machine: {}", machinePort.getMachineId());
        return;
    }
    
    // Access child components through the port interface
    Map<String, CompositeComponentPort> composites = machinePort.getComposites();
    for (Map.Entry<String, CompositeComponentPort> entry : composites.entrySet()) {
        String compositeName = entry.getKey();
        CompositeComponentPort composite = entry.getValue();
        logger.info("Processing composite: {}", compositeName);
        
        // Work with the composite through its port interface
        composite.activate();
    }
    
    // The method works regardless of the concrete implementation
}

// Method to support transition - calls the new method with port adapter
public void processMachine(org.s8r.domain.machine.Machine machine) {
    MachinePort machinePort = adapterFactory.createMachinePort(machine);
    processMachine(machinePort); // Call new port-based method
}
```

## Performance Considerations

Port adapters use the Proxy pattern to delegate calls to their concrete implementations. This introduces minimal overhead while providing the benefits of Clean Architecture:

1. **Lightweight Adapters**: Adapters only delegate method calls without copying data
2. **No Data Conversion**: The adapters work with the original objects, avoiding any serialization/deserialization
3. **Singleton Factory**: The adapter factory is a singleton, reducing object creation

However, for highly performance-critical sections, consider these optimizations:

1. **Batch Operations**: Use batch methods to reduce method call overhead
2. **Direct Domain Operations**: For performance-critical internal code that doesn't cross architectural boundaries, you can use domain entities directly
3. **Caching Repository Results**: Cache frequently accessed repository results

## Clean Architecture Implementation Roadmap

1. **Phase 1: Define Port Interfaces (Completed)**: Define the core port interfaces in the domain layer
2. **Phase 2: Implement Adapters (Completed)**: Create adapters that implement the port interfaces
3. **Phase 3: Update Repositories (Completed)**: Modify repositories to work with port interfaces
4. **Phase 4: Update Application Services (Current)**: Refactor application services to use port interfaces
5. **Phase 5: Update Infrastructure Components**: Ensure infrastructure components use port interfaces
6. **Phase 6: Legacy Compatibility**: Maintain legacy adapters for backward compatibility

## Troubleshooting

### Common issues

1. **Component Identity**: If components with the same ID are treated as different instances, check that you're using the port interfaces consistently and not mixing them with direct domain entity references.

2. **Event Propagation**: If domain events aren't being triggered correctly, ensure that events from port interfaces are being properly dispatched and that `clearEvents()` is called after processing.

3. **Adapter Compatibility**: When working with multiple adapter types (port adapters, legacy adapters), ensure you're using the correct adapter factory for each type of conversion.

4. **Missing Functionality**: If certain functionality is missing when using port interfaces, check that the port interface includes all required methods and that adapters properly implement them.

### Debugging

To enable debug logging for the adapters:

```java
// Configure the logger factory with debug level for adapters
LoggingConfiguration.setLogLevel("org.s8r.adapter", LogLevel.DEBUG);

// Or get a specific logger with debug level
LoggerPort logger = DependencyContainer.getInstance().getLogger(MyClass.class);
logger.setLevel(LogLevel.DEBUG);
```

## Best Practices

1. **Depend on Abstractions**: Service and application classes should depend on port interfaces, not concrete implementations.

2. **Use Factories**: Use the provided adapter factories instead of creating adapters directly.

3. **Be Consistent**: Once you start using port interfaces in a component, use them consistently throughout its lifecycle.

4. **Document Adapter Usage**: When creating adapters, document their purpose and how they map between implementations.

5. **Test Through Interfaces**: Write tests that use port interfaces rather than concrete implementations for better isolation.

6. **Handle Events Properly**: Remember to get events via `getDomainEvents()` and clear them with `clearEvents()` after processing.

7. **Preserve Identity**: Be careful when working with port interfaces that represent the same domain entity in different contexts.

8. **Use Port Interfaces in Public APIs**: Ensure that all public interfaces that cross architectural boundaries use port interfaces.

## Conclusion

Port interfaces and adapters are key to implementing Clean Architecture in the Samstraumr framework. They allow for:

- **Separation of Concerns**: Each layer has a specific responsibility
- **Dependency Inversion**: Higher-level modules depend on abstractions, not details
- **Testability**: Components can be tested in isolation
- **Flexibility**: Implementations can be changed without affecting other layers
- **Gradual Migration**: Legacy code can coexist with new Clean Architecture code

By using port interfaces and adapters consistently, you can build a more maintainable, testable, and flexible system.

For more information, see:
- [Clean Architecture ADR](../architecture/decisions/0003-adopt-clean-architecture-for-system-design.md)
- [Adapter Package Documentation](/Samstraumr/samstraumr-core/src/main/java/org/s8r/adapter/package-info.java.md)
- [Port Interfaces Documentation](/Samstraumr/samstraumr-core/src/main/java/org/s8r/domain/component/port/package-info.java.md)
- [Port Interface Tests](/Samstraumr/samstraumr-core/src/test/java/org/s8r/domain/component/port/port-interface-test.java)
- [Repository Implementations](/Samstraumr/samstraumr-core/src/main/java/org/s8r/infrastructure/persistence.persistence/)
