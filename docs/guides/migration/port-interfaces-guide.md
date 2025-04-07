<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Port Interfaces Guide

This document explains how to use S8r port interfaces during the migration from Samstraumr to the S8r framework, focusing on the integration between legacy code and Clean Architecture.

## Table of Contents

1. [Introduction to Port Interfaces](#introduction-to-port-interfaces)
2. [Port Interface Architecture](#port-interface-architecture)
3. [Available Port Interfaces](#available-port-interfaces)
4. [Using Port Interfaces with Legacy Code](#using-port-interfaces-with-legacy-code)
5. [Adapter Implementation](#adapter-implementation)
6. [Testing with Port Interfaces](#testing-with-port-interfaces)
7. [Migration Strategies](#migration-strategies)
8. [Best Practices](#best-practices)
9. [Common Patterns](#common-patterns)
10. [Troubleshooting](#troubleshooting)

## Introduction to Port Interfaces

Port interfaces are a key concept in Clean Architecture, allowing the application core to remain independent of external concerns by depending on abstractions rather than concrete implementations. In the S8r framework, port interfaces enable:

- **Separation of concerns**: Decoupling application logic from infrastructure details
- **Testability**: Easier testing of application logic with mock implementations
- **Flexibility**: Multiple implementations of the same port for different contexts
- **Migration path**: Gradual migration from legacy code to Clean Architecture

During the migration from Samstraumr to S8r, port interfaces serve as the bridge between legacy code and the new architecture.

## Port Interface Architecture

The S8r port interfaces follow the hexagonal architecture pattern:

```
┌─────────────────────────────────────────────────┐
│                Application Core                 │
│                                                 │
│  ┌─────────────┐        ┌───────────────────┐   │
│  │   Domain    │        │    Application     │   │
│  │   Entities  │◄──────►│     Services      │   │
│  └─────────────┘        └─────────┬─────────┘   │
│                                   │             │
│                          ┌────────▼─────────┐   │
│                          │  Port Interfaces │   │
└──────────────────────────┴────────┬─────────┴───┘
                                    │
┌──────────────────────────────────▼─────────────┐
│                   Adapters                      │
│  ┌──────────────────┐      ┌─────────────────┐ │
│  │Primary/Driving   │      │ Secondary/Driven │ │
│  │    Adapters      │      │    Adapters      │ │
│  └──────────────────┘      └─────────────────┘ │
└────────────────────────────────────────────────┘
```

In this architecture:
- **Domain entities** represent the core business objects
- **Application services** implement the business logic
- **Port interfaces** define the boundaries of the application core
- **Adapters** implement the port interfaces to connect with external systems or legacy code

## Available Port Interfaces

The S8r framework provides the following port interfaces:

### Core port interfaces

| Port Interface | Package | Purpose |
|----------------|---------|---------|
| `ComponentPort` | `org.s8r.application.port` | Interface for interacting with components |
| `MachinePort` | `org.s8r.application.port` | Interface for interacting with machines |
| `CompositePort` | `org.s8r.application.port` | Interface for interacting with composites |
| `DataFlowEventPort` | `org.s8r.application.port` | Interface for publishing and subscribing to data events |
| `LoggerPort` | `org.s8r.application.port` | Interface for logging operations |
| `ConfigurationPort` | `org.s8r.application.port` | Interface for accessing configuration |
| `ProjectInitializationPort` | `org.s8r.application.port` | Interface for project initialization |

### Extended port interfaces

| Port Interface | Package | Purpose |
|----------------|---------|---------|
| `NotificationPort` | `org.s8r.application.port` | Interface for sending notifications |
| `SecurityPort` | `org.s8r.application.port` | Interface for authentication and authorization |
| `StoragePort` | `org.s8r.application.port` | Interface for data storage |
| `FileSystemPort` | `org.s8r.application.port` | Interface for file system operations |
| `MessagingPort` | `org.s8r.application.port` | Interface for message publishing and subscription |
| `TaskExecutionPort` | `org.s8r.application.port` | Interface for asynchronous task execution |
| `CachePort` | `org.s8r.application.port` | Interface for caching operations |
| `TemplatePort` | `org.s8r.application.port` | Interface for template rendering |

## Using Port Interfaces with Legacy Code

Legacy Samstraumr code can be integrated with S8r port interfaces using adapters. These adapters implement the port interfaces while delegating to legacy code.

### Example: using componentport with legacy tube

```java
import org.s8r.application.port.ComponentPort;
import org.s8r.adapter.TubeComponentAdapter;
import org.samstraumr.tube.Tube;

// Your legacy tube
Tube legacyTube = Tube.create("test", environment);

// Adapt it to ComponentPort
ComponentPort componentPort = new TubeComponentAdapter(legacyTube);

// Now you can use it with application services
applicationService.processComponent(componentPort);
```

### Example: using datafloweventport with legacy event system

```java
import org.s8r.application.port.DataFlowEventPort;
import org.s8r.adapter.LegacyDataFlowEventAdapter;
import org.samstraumr.tube.event.EventSystem;

// Your legacy event system
EventSystem legacyEventSystem = EventSystem.getInstance();

// Adapt it to DataFlowEventPort
DataFlowEventPort dataFlowPort = new LegacyDataFlowEventAdapter(legacyEventSystem);

// Now you can use it with application services
applicationService.subscribeToEvents(dataFlowPort);
```

## Adapter Implementation

If you need to create your own adapters for legacy code, follow these guidelines:

### Basic adapter structure

```java
import org.s8r.application.port.ComponentPort;
import org.s8r.domain.component.Component;

public class CustomComponentAdapter implements ComponentPort {
    private final YourLegacyComponent legacyComponent;
    
    public CustomComponentAdapter(YourLegacyComponent legacyComponent) {
        this.legacyComponent = legacyComponent;
    }
    
    @Override
    public String getId() {
        return legacyComponent.getIdentity().toString();
    }
    
    @Override
    public String getName() {
        return legacyComponent.getName();
    }
    
    // Implement other methods...
}
```

### Bidirectional synchronization

For adapters that need to maintain bidirectional synchronization between legacy and S8r objects:

```java
import org.s8r.application.port.ComponentPort;
import org.s8r.component.core.State;

public class BidirectionalComponentAdapter implements ComponentPort {
    private final YourLegacyComponent legacyComponent;
    private final StateChangeListener stateChangeListener;
    
    public BidirectionalComponentAdapter(YourLegacyComponent legacyComponent) {
        this.legacyComponent = legacyComponent;
        this.stateChangeListener = new StateChangeListener();
        legacyComponent.addListener(stateChangeListener);
    }
    
    @Override
    public void setState(State state) {
        // Convert S8r state to legacy state
        YourLegacyState legacyState = convertState(state);
        legacyComponent.setState(legacyState);
    }
    
    private class StateChangeListener implements LegacyComponentListener {
        @Override
        public void onStateChanged(YourLegacyState newState) {
            // Update any cached state in the adapter
        }
    }
    
    // Other methods...
}
```

## Testing with Port Interfaces

Port interfaces simplify testing by allowing you to use mock implementations:

### Example: testing with mock componentport

```java
import org.s8r.application.port.ComponentPort;
import org.s8r.application.service.ComponentService;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

public class ComponentServiceTest {
    @Test
    void testProcessComponent() {
        // Create a mock ComponentPort
        ComponentPort mockComponent = mock(ComponentPort.class);
        when(mockComponent.getId()).thenReturn("test-component");
        when(mockComponent.getState()).thenReturn(State.READY);
        
        // Create the service with mock dependencies
        ComponentService service = new ComponentService(
            mockComponent, 
            mock(LoggerPort.class)
        );
        
        // Test the service
        service.processComponent(mockComponent);
        
        // Verify interactions
        verify(mockComponent).setState(State.ACTIVE);
    }
}
```

### Example: testing legacy code with port adapters

```java
import org.s8r.adapter.TubeComponentAdapter;
import org.samstraumr.tube.Tube;

public class LegacyTubeTest {
    @Test
    void testLegacyTubeWithAdapter() {
        // Create a legacy tube
        Tube legacyTube = Tube.create("test", environment);
        
        // Adapt it to ComponentPort
        ComponentPort componentPort = new TubeComponentAdapter(legacyTube);
        
        // Use it with the new service
        ComponentService service = new ComponentService(
            componentPort, 
            new ConsoleLogger()
        );
        
        // Test the service interaction with legacy code
        service.processComponent(componentPort);
        
        // Verify the effect on the legacy tube
        assertEquals(TubeStatus.ACTIVE, legacyTube.getStatus());
    }
}
```

## Migration Strategies

### Strategy 1: port first, implementation later

1. Define port interfaces for your key abstractions
2. Create adapters for legacy implementations
3. Update application code to use port interfaces
4. Gradually replace legacy implementations with new ones

### Strategy 2: start with infrastructure ports

1. Start by defining ports for infrastructure concerns (logging, storage, etc.)
2. Create adapters for legacy infrastructure
3. Build application services that use these ports
4. Finally, migrate domain logic to use domain ports

### Strategy 3: domain-driven migration

1. Start with defining domain entity ports
2. Create domain services that use these ports
3. Create adapters for legacy domain entities
4. Gradually expand to infrastructure ports

## Best Practices

### Port interface design

1. **Keep ports focused**: Each port should have a single responsibility
2. **Use domain terminology**: Port methods should use domain language
3. **Hide implementation details**: Don't expose implementation-specific concepts
4. **Consider bidirectional needs**: Design ports to support bidirectional communication if needed
5. **Document assumptions**: Clearly document expected behavior of port implementations

### Adapter implementation

1. **Prefer composition over inheritance**: Use delegation in adapters
2. **Handle state synchronization carefully**: Ensure bidirectional adapters don't cause infinite loops
3. **Cache converted objects**: Avoid repeated conversion of the same objects
4. **Handle exceptions appropriately**: Wrap or translate exceptions from legacy code
5. **Implement proper equals() and hashCode()**: Ensure adapter identity works correctly in collections

## Common Patterns

### Repository pattern

```java
// Port interface
public interface ComponentRepository {
    Component findById(String id);
    void save(Component component);
    void delete(String id);
    List<Component> findAll();
}

// Legacy adapter
public class LegacyComponentRepository implements ComponentRepository {
    private final TubeRegistry legacyRegistry;
    
    // Implementation methods...
}
```

### Factory pattern

```java
// Port interface
public interface ComponentFactory {
    Component create(String name, Environment env);
    Component createChild(String name, Environment env, Component parent);
}

// Legacy adapter
public class LegacyComponentFactory implements ComponentFactory {
    // Implementation methods...
}
```

### Observer pattern

```java
// Port interface
public interface EventListener<T> {
    void onEvent(T event);
}

// Legacy adapter
public class LegacyEventAdapter implements EventListener<ComponentEvent> {
    private final LegacyEventHandler legacyHandler;
    
    // Implementation methods...
}
```

## Troubleshooting

### Common issues

| Issue | Solution |
|-------|----------|
| Adapter method throws UnsupportedOperationException | Implement the missing functionality in the adapter |
| Bidirectional updates cause infinite loops | Add flags to prevent recursive updates |
| Legacy object state gets out of sync | Ensure all state changes go through the adapter |
| ClassCastException when using adapters | Verify the correct adapter is used for the object type |
| Performance issues with adapters | Consider caching or direct migration for performance-critical paths |

### Debugging tips

1. Enable debug logging for adapters
2. Add validation checks for state consistency
3. Use unique IDs for tracking object identity across systems
4. Test adapter behavior in isolation
