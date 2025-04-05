<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Event-Driven Architecture in Samstraumr

This document describes the event-driven architecture implemented in the Samstraumr framework.

## Overview

Samstraumr implements a comprehensive event-driven architecture that enables components to communicate without direct dependencies. This architecture provides loose coupling, making the system more maintainable, testable, and adaptable to changing requirements.

The event system follows Clean Architecture principles, with domain events defined in the domain layer, event dispatching in the application layer, and concrete event handling in the infrastructure and adapter layers.

## Core Components

### Domain Events

Domain events represent significant occurrences within the domain model. They are defined in the domain layer and are immutable value objects.

**Base Domain Event:**

```java
public abstract class DomainEvent {
    private final String eventId;
    private final Instant occurredOn;
    private final String eventType;
    
    protected DomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = Instant.now();
        this.eventType = this.getClass().getSimpleName();
    }
    
    // Getters...
}
```

**Key Domain Events:**
- `ComponentCreatedEvent`: Raised when a new component is created
- `ComponentStateChangedEvent`: Raised when a component's lifecycle state changes
- `ComponentConnectionEvent`: Raised when components are connected
- `MachineStateChangedEvent`: Raised when a machine's state changes
- `ComponentDataEvent`: Raised when a component publishes data for other components

### Event Dispatcher

The event dispatcher is defined as a port (interface) in the application layer and implemented in the infrastructure layer. It allows components to register handlers for specific event types and dispatches events to the appropriate handlers.

**Event Dispatcher Interface:**

```java
public interface EventDispatcher {
    void dispatch(DomainEvent event);
    <T extends DomainEvent> void registerHandler(Class<T> eventType, EventHandler<T> handler);
    <T extends DomainEvent> void unregisterHandler(Class<T> eventType, EventHandler<T> handler);
    
    @FunctionalInterface
    interface EventHandler<T extends DomainEvent> {
        void handle(T event);
    }
}
```

**Memory Implementation:**

```java
public class InMemoryEventDispatcher implements EventDispatcher {
    private final Map<Class<? extends DomainEvent>, List<EventHandler<? extends DomainEvent>>> handlers;
    private final LoggerPort logger;
    
    // Implementation...
}
```

### Data Flow Service

The `DataFlowService` provides a facade for the event system, simplifying the publication and subscription of data events.

```java
public class DataFlowService {
    private final ComponentRepository componentRepository;
    private final DataFlowEventHandler dataFlowHandler;
    private final EventDispatcher eventDispatcher;
    private final LoggerPort logger;
    
    // Methods for publishing and subscribing...
}
```

## Event Types and Flows

### System Events

System events represent significant changes to the system state, such as component creation, state changes, and component connections.

- When a component is created, a `ComponentCreatedEvent` is raised
- When a component changes state, a `ComponentStateChangedEvent` is raised
- When components are connected, a `ComponentConnectionEvent` is raised
- When a machine changes state, a `MachineStateChangedEvent` is raised

These events are primarily used for system monitoring, logging, and maintaining consistency across the system.

### Data Events

Data events (`ComponentDataEvent`) represent data flowing between components. These events carry a payload of data from one component to another, enabling communication without direct dependencies.

Data events include:
- Source component ID (which component produced the data)
- Data channel (logical topic for the data)
- Data payload (map of key-value pairs)

### Event Channels

Event channels provide logical segmentation of event flows. Components can subscribe to specific channels to receive only events they are interested in.

Key system channels include:
- `system.metrics`: For system-wide metrics
- `system.health`: For health status information
- `system.alerts`: For system alerts

## Publishing Events

Components can publish events in two ways:

1. **Domain Events**: Components raise domain events like `ComponentStateChangedEvent` when their state changes.

```java
protected void raiseEvent(DomainEvent event) {
    domainEvents.add(event);
}
```

2. **Data Events**: Components publish data events explicitly to share data with other components.

```java
public void publishData(String channel, Map<String, Object> data) {
    ComponentDataEvent event = new ComponentDataEvent(id, channel, data);
    raiseEvent(event);
}
```

The application layer collects these events and dispatches them using the `EventDispatcher`.

## Handling Events

Components can handle events by subscribing to specific event types or channels.

```java
// Subscribe to a data channel
dataFlowService.subscribe(componentId, channelName, event -> {
    // Handle the event
    // ...
});
```

Event handlers can be chained to create processing pipelines:

```java
// Create a chain of handlers
Consumer<ComponentDataEvent> handler = event -> {
    // First handler
    // ...
    
    // Pass to next handler
    nextHandler.accept(event);
};
```

## Integration Patterns

The event system supports various integration patterns through specialized components:

- **Transformer Pattern**: Transforming data between formats
- **Filter Pattern**: Filtering data based on conditions
- **Aggregator Pattern**: Collecting and aggregating data over time
- **Router Pattern**: Routing data to different destinations based on content

See the [Integration Patterns](../patterns/README.md) document for details on these patterns.

## Monitoring

The event system includes built-in monitoring capabilities:

- **Metrics Collection**: Tracking message counts, processing times, and error rates
- **Health Monitoring**: Monitoring component health and system resources
- **Alerting**: Generating alerts for critical conditions

See the [Monitoring and Management](../monitoring/README.md) document for details.

## Benefits

The event-driven architecture provides several benefits:

1. **Loose Coupling**: Components communicate without direct dependencies
2. **Scalability**: Components can be scaled independently
3. **Extensibility**: New components can be added without modifying existing ones
4. **Resilience**: Failures are isolated to specific components
5. **Testability**: Components can be tested in isolation
6. **Flexibility**: Different processing patterns can be implemented without modifying the core architecture

## Best Practices

1. **Use Meaningful Event Names**: Event names should clearly describe what happened
2. **Keep Events Immutable**: Events should not be modified after creation
3. **Include Necessary Context**: Events should include all the information needed to handle them
4. **Separate Command and Query**: Use different channels for commands and queries
5. **Handle Failures Gracefully**: Implement error handling in event handlers
6. **Monitor Event Flows**: Use metrics and health monitoring to track event flows
7. **Document Event Schemas**: Document the structure and meaning of events

## Example: Data Processing Pipeline

Here's an example of creating a data processing pipeline using the event system:

```java
// Create components
TransformerComponent transformer = patternFactory.createTransformer(
    "Transform raw data", "raw.data", "transformed.data");
transformer.addTransformation("value", value -> ((Integer) value) * 2);

FilterComponent filter = patternFactory.createFilter(
    "Filter values > 100", "transformed.data", "filtered.data");
filter.addFilter("value", value -> ((Integer) value) > 100);

AggregatorComponent aggregator = patternFactory.createCountAggregator(
    "Aggregate results", "filtered.data", "aggregated.data", 10);
aggregator.addAggregator("value", (current, next) -> 
    ((Integer) current) + ((Integer) next));

// Publish raw data
Map<String, Object> data = new HashMap<>();
data.put("value", 75);
dataFlowService.publishData(sourceId, "raw.data", data);
```

## Conclusion

The event-driven architecture in Samstraumr provides a flexible and powerful way to build loosely coupled, scalable systems. By following Clean Architecture principles, the event system maintains a clear separation of concerns while enabling complex component interactions.
