<!--
Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>

Licensed under Mozilla Public License 2.0.
See LICENSE file for details.
-->




# State Management

## Table of Contents

- [Introduction](#introduction)
- [Unified State Model](#unified-state-model)
- [State at Different Levels](#state-at-different-levels)
- [State Transitions](#state-transitions)
- [Monitoring and Observability](#monitoring-and-observability)
- [Implementation Guidelines](#implementation-guidelines)
- [Advanced State Management](#advanced-state-management)
- [State Management Patterns](#state-management-patterns)
- [Common Challenges](#common-challenges)

## Introduction

S8r brings awareness to software through its unified state model complemented by a flexible properties system. This approach enables components to maintain their essential identity through state transitions while responding intelligently to changing circumstances.

## Unified State Model

### Component state lifecycle

S8r's unified State enum represents the component's position in a lifecycle:

```java
public enum State {
    // Creation Phase
    CONCEPTION, INITIALIZING,
    
    // Development Phase
    CONFIGURING, SPECIALIZING, DEVELOPING_FEATURES,
    
    // Operational Phase
    READY, ACTIVE, DEGRADED,
    
    // Termination Phase
    TERMINATING, TERMINATED, ARCHIVED
}
```

### Component properties

While State provides lifecycle and operational status, properties manage detailed, dynamic information:

```java
// Setting properties
component.setProperty("processingCount", 1250);
component.setProperty("errorRate", 0.002);

// Retrieving properties
int count = (int)component.getProperty("processingCount");
```

### State-property interplay

1. **State Provides Context**: Property interpretation depends on current state
2. **Properties Influence Transitions**: Patterns in properties can trigger state changes
3. **State Affects Property Focus**: Different states highlight different properties

## State at Different Levels

### Component state

```java
public void processItem(Item item) {
    setState(State.ACTIVE);
    
    try {
        Result result = processor.process(item);
        setProperty("lastProcessedTime", Instant.now());
        setProperty("processedCount", getProcessedCount() + 1);
        return result;
    } catch (Exception e) {
        setState(State.DEGRADED);
        setProperty("lastError", e.getMessage());
        setProperty("errorCount", getErrorCount() + 1);
        throw e;
    }
}
```

### Composite state

Composites aggregate states of their components:

```java
public void evaluateCompositeState() {
    // Count components in each state
    int readyCount = 0, activeCount = 0, degradedCount = 0;
    
    for (Component component : components) {
        switch (component.getState()) {
            case READY: readyCount++; break;
            case ACTIVE: activeCount++; break;
            case DEGRADED: degradedCount++; break;
        }
    }
    
    // Determine composite state
    if (degradedCount > criticalDegradedThreshold) {
        setState(State.DEGRADED);
    } else if (activeCount > 0 && degradedCount == 0) {
        setState(State.ACTIVE);
    } else if (readyCount == components.size()) {
        setState(State.READY);
    }
}
```

### Machine state

Machines coordinate multiple composites:

```java
public void determineMachineState() {
    // Check critical composites first
    boolean criticalCompositeImpaired = false;
    
    for (Composite composite : criticalComposites) {
        if (composite.getState() == State.DEGRADED) {
            criticalCompositeImpaired = true;
            break;
        }
    }
    
    // Determine machine state
    if (criticalCompositeImpaired) {
        setState(State.DEGRADED);
    } else if (getActiveCompositeCount() > 0) {
        setState(State.ACTIVE);
    } else {
        setState(State.READY);
    }
}
```

## State Transitions

### Intentional transitions

Explicitly requested by the component or controller:

```java
public void startProcessing() {
    if (getState() == State.READY) {
        setState(State.ACTIVE);
        startProcessingThreads();
    }
}

public void shutDown() {
    if (getState() == State.ACTIVE || getState() == State.DEGRADED) {
        setState(State.TERMINATING);
        stopProcessingThreads();
        releaseResources();
        setState(State.TERMINATED);
    }
}
```

### Reactive transitions

Occur in response to observed conditions:

```java
public void monitorHealthAndAdapt() {
    double errorRate = (double)getProperty("errorRate", 0.0);
    long queueDepth = (long)getProperty("queueDepth", 0L);
    
    if (errorRate > errorThreshold || queueDepth > queueThreshold) {
        if (getState() == State.ACTIVE) {
            setState(State.DEGRADED);
            reduceAcceptedLoad();
        }
    } else if (getState() == State.DEGRADED) {
        setState(State.ACTIVE);
        restoreFullFunctionality();
    }
}
```

### Propagated transitions

State changes in one component trigger changes in related components:

```java
public void onComponentStateChanged(Component component, State oldState, State newState) {
    // If a critical component is degraded, the composite becomes degraded
    if (criticalComponents.contains(component) && newState == State.DEGRADED) {
        setState(State.DEGRADED);
        return;
    }
    
    // Re-evaluate composite state
    evaluateCompositeState();
}
```

## Monitoring and Observability

### Health assessments

Structured evaluation of operational wellness:

```java
public HealthAssessment assessHealth() {
    HealthAssessment.Builder builder = new HealthAssessment.Builder();
    
    // Gather metrics
    double errorRate = (double)getProperty("errorRate", 0.0);
    long queueDepth = (long)getProperty("queueDepth", 0L);
    
    // Add metrics to assessment
    builder.withMetric("errorRate", errorRate)
           .withMetric("queueDepth", queueDepth);
    
    // Determine status
    if (errorRate > criticalErrorThreshold) {
        builder.withStatus("CRITICAL")
               .withWarning("Error rate exceeds critical threshold");
    } else if (queueDepth > criticalQueueThreshold) {
        builder.withStatus("CRITICAL")
               .withWarning("Queue depth exceeds critical threshold");
    } else {
        builder.withStatus("HEALTHY");
    }
    
    return builder.build();
}
```

### System-wide observability

Beyond individual components:

1. **Hierarchical Rollups**: Aggregate state information up the hierarchy
2. **Cross-Cutting Views**: Views across components by type or domain
3. **State History**: Historical state information for trend analysis
4. **Anomaly Detection**: Identifying unusual state patterns

## Implementation Guidelines

### State implementation

1. **Use an Enum for Primary State**: Type safety and clear documentation
2. **Validate State Transitions**: Only allow valid transitions
3. **Make State Changes Thread-Safe**: Use synchronization or volatile variables
4. **Log State Changes**: Maintain visibility for debugging

```java
@Override
public void setState(State newState) {
    synchronized (stateLock) {
        if (newState == state) {
            return; // No change
        }
        
        // Validate transition
        if (!isValidTransition(state, newState)) {
            throw new IllegalStateTransitionException(
                    "Invalid transition from " + state + " to " + newState);
        }
        
        State oldState = state;
        state = newState;
        
        // Log the change
        logger.info("State changed: {} -> {}", oldState, newState);
        
        // Notify listeners
        notifyStateChange(oldState, newState);
    }
}
```

### Properties implementation

1. **Use a Flexible Structure**: Accommodate various types without code changes
2. **Include Timestamps**: Attach timestamps to property changes
3. **Ensure Thread Safety**: Make property updates thread-safe

```java
public void setProperty(String key, Object value) {
    if (key == null) {
        throw new IllegalArgumentException("Property key cannot be null");
    }
    
    if (value == null) {
        properties.remove(key);
    } else {
        properties.put(key, value);
        properties.put(key + ".lastUpdated", Instant.now());
    }
}
```

## Advanced State Management

### State machines

For components with complex transition rules:

```java
public boolean transitionTo(OrderState targetState) {
    Set<OrderState> allowedTransitions = validTransitions.get(currentState);
    
    if (allowedTransitions.contains(targetState)) {
        OrderState previousState = currentState;
        currentState = targetState;
        
        // Execute state-specific actions
        executeActions(previousState, targetState);
        
        return true;
    }
    
    return false;
}
```

### State persistence

For long-running or mission-critical systems:

```java
public PersistentStateComponent(Identity identity, Environment env,
                              StateRepository stateRepository) {
    super(identity, env);
    this.stateRepository = stateRepository;
    
    // Restore state from repository if available
    Optional<ComponentState> savedState = 
        stateRepository.loadState(identity.getFullId());
    
    if (savedState.isPresent()) {
        ComponentState state = savedState.get();
        super.setState(state.getState());
        
        // Restore properties
        for (Map.Entry<String, Object> entry : state.getProperties().entrySet()) {
            setProperty(entry.getKey(), entry.getValue());
        }
    }
}
```

### Distributed state

Maintaining coherent state across multiple nodes:

```java
public void publishStateChange(String componentId, State newState) {
    // Distribute to other nodes
    distributor.distributeStateChange(componentId, newState);
}

private void onDistributedStateChange(String componentId, State newState) {
    // Notify local listeners
    Set<StateChangeListener> componentListeners = listeners.get(componentId);
    if (componentListeners != null) {
        for (StateChangeListener listener : componentListeners) {
            listener.onStateChanged(newState);
        }
    }
}
```

## State Management Patterns

### Self-healing state

Components that detect and recover from failures:

```java
public Object process(Object input) {
    try {
        // Check if we need to attempt recovery
        if (getState() == State.DEGRADED && consecutiveFailures >= healingThreshold) {
            attemptRecovery();
        }
        
        // Process normally if active
        if (getState() == State.ACTIVE) {
            Object result = super.process(input);
            consecutiveFailures = 0; // Reset on success
            return result;
        }
    } catch (Exception e) {
        // Handle failure
        consecutiveFailures++;
        setState(State.DEGRADED);
        throw e;
    }
}
```

### Degraded state

Components operating with reduced functionality:

```java
public Object process(Object input) {
    // Update current mode based on system conditions
    assessHealthAndUpdateMode();
    
    // Process based on current mode
    switch (currentMode) {
        case FULL:
            return processWithFullFunctionality(input);
        case REDUCED:
            return processWithReducedFunctionality(input);
        case MINIMAL:
            return processWithMinimalFunctionality(input);
        case OFFLINE:
            return new DegradedResult(null, "System in OFFLINE mode");
    }
}
```

### State-based routing

Using state to direct work flow:

```java
public Object process(Object input) {
    // Choose route based on component states
    String selectedRoute = selectRouteBasedOnState();
    
    // Get the destination
    Component destination = routes.get(selectedRoute);
    
    // Forward the input
    return destination.process(input);
}

private String selectRouteBasedOnState() {
    // Find the first ACTIVE route with least load
    for (Map.Entry<String, Component> entry : routes.entrySet()) {
        Component route = entry.getValue();
        if (route.getState() == State.ACTIVE) {
            return route.getIdentity().getId();
        }
    }
    
    // Find degraded route as fallback
    for (Map.Entry<String, Component> entry : routes.entrySet()) {
        if (entry.getValue().getState() == State.DEGRADED) {
            return entry.getValue().getIdentity().getId();
        }
    }
    
    throw new IllegalStateException("No available routes");
}
```

## Common Challenges

### State explosion

**Solution**: Use state abstraction and hierarchical properties

```java
// Create hierarchical property structure
Map<String, Object> processingProps = new HashMap<>();
processingProps.put("rate", 0.0);
processingProps.put("count", 0);

Map<String, Object> resourceProps = new HashMap<>();
resourceProps.put("memoryUsage", 0.0);
resourceProps.put("cpuUsage", 0.0);

// Set nested properties
setProperty("processing", processingProps);
setProperty("resources", resourceProps);
```

### State visibility delays

**Solution**: Implement eventually consistent state patterns

```java
public void setState(State newState) {
    super.setState(newState);
    
    // Publish state change asynchronously
    coordinator.publishStateChange(getId(), newState);
}

private void onRemoteStateChange(State remoteState) {
    // Update local state to match remote if appropriate
    if (shouldAcceptRemoteState(remoteState)) {
        super.setState(remoteState);
    }
}
```
