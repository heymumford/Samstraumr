# State Management

## Table of Contents

- [Introduction: States of Being and Becoming](#introduction-states-of-being-and-becoming)
- [The Unified State Model](#the-unified-state-model)
  - [Component State Lifecycle](#component-state-lifecycle)
  - [Component Properties](#component-properties)
  - [The Interplay Between State and Properties](#the-interplay-between-state-and-properties)
- [State at Different Levels](#state-at-different-levels)
  - [Component State](#component-state)
  - [Composite State](#composite-state)
  - [Machine State](#machine-state)
- [State Transitions](#state-transitions)
  - [Intentional Transitions](#intentional-transitions)
  - [Reactive Transitions](#reactive-transitions)
  - [Propagated Transitions](#propagated-transitions)
- [State Monitoring and Observability](#state-monitoring-and-observability)
  - [Health Assessments](#health-assessments)
  - [Vital Statistics](#vital-statistics)
  - [System-Wide Observability](#system-wide-observability)
- [Implementation Guidelines](#implementation-guidelines)
  - [State Implementation](#state-implementation)
  - [Properties Implementation](#properties-implementation)
  - [State Synchronization](#state-synchronization)
- [Advanced State Management](#advanced-state-management)
  - [State Machines](#state-machines)
  - [State Persistence](#state-persistence)
  - [Distributed State](#distributed-state)
- [State Management Patterns](#state-management-patterns)
  - [Self-Healing State](#self-healing-state)
  - [Degraded State](#degraded-state)
  - [State-Based Routing](#state-based-routing)
- [Common Challenges and Solutions](#common-challenges-and-solutions)

## Introduction: States of Being and Becoming

In nature, consciousness requires awareness—both of what something is and what it's experiencing. A river knows itself as a river (its fundamental nature) while also experiencing its current conditions (flowing swiftly after rain, frozen in winter, or trickling during drought).

S8r brings this natural awareness to software through its unified state model complemented by a flexible properties system. This approach enables components to maintain both their essential identity through state transitions and respond intelligently to changing circumstances through dynamic properties.

This document explores how state management in S8r mirrors natural systems, creating software that can sense, adapt, and maintain equilibrium even amid changing requirements and environments.

## The Unified State Model

At the heart of S8r's approach to state management lies a unified state model that captures both lifecycle phases and operational conditions, complemented by a flexible properties system for detailed runtime information.

### Component State Lifecycle

S8r's unified State enum represents the component's position in a biological-inspired lifecycle, from conception through development, operation, and eventual termination:

```java
public enum State {
    // Creation Phase
    CONCEPTION,         // Initial creation phase
    INITIALIZING,       // Setting up internal structures
    
    // Development Phase
    CONFIGURING,        // Establishing boundaries and configuration
    SPECIALIZING,       // Determining core functionality
    DEVELOPING_FEATURES,// Building specific capabilities
    
    // Operational Phase
    READY,              // Configured and ready for operation
    ACTIVE,             // Fully operational and processing
    DEGRADED,           // Operating with reduced capabilities
    
    // Termination Phase
    TERMINATING,        // In process of shutting down
    TERMINATED,         // No longer operating
    ARCHIVED            // Preserved for historical reference
}
```

This unified approach provides several advantages:
1. **Simplicity**: One state variable to manage instead of multiple
2. **Clarity**: Clear, well-defined states with intuitive transitions
3. **Consistency**: All components follow the same state model
4. **Completeness**: Captures both operational status and lifecycle position

### Component Properties

While the State enum provides the fundamental lifecycle and operational status, the properties system manages the detailed, dynamic information about a component:

```java
// Example of component properties
component.setProperty("processingCount", 1250);
component.setProperty("lastProcessedTime", Instant.now());
component.setProperty("errorRate", 0.002);
component.setProperty("currentMode", "high-throughput");

// Retrieving properties
int count = (int)component.getProperty("processingCount");
double errorRate = (double)component.getProperty("errorRate");
```

Properties offer:
1. **Flexibility**: Can store any type of data relevant to the component
2. **Extensibility**: New properties can be added without modifying the component class
3. **Detail**: Capture fine-grained information about the component's operation
4. **History**: Track changes in component metrics over time

### The Interplay Between State and Properties

The magic of S8r's approach emerges in the interplay between state and properties:

1. **State Provides Context for Properties**: The interpretation of property values depends on the current state. For example, a high queue depth during ACTIVE state might indicate a problem, but during INITIALIZING, it could be expected.

2. **Properties Influence State Transitions**: Persistent patterns in properties often trigger state transitions. For example, a sustained high error rate might trigger a transition from ACTIVE to DEGRADED.

3. **State Changes Affect Property Focus**: When state changes, the properties that matter most often change as well. A component in DEGRADED state might focus on different metrics than one in ACTIVE state.

This continuous feedback loop creates components that are both stable in their fundamental nature and highly responsive to changing conditions—much like living organisms that maintain their identity while constantly adapting to their environment.

## State at Different Levels

State management in S8r operates across all levels of the system hierarchy, with each level building upon and extending the patterns established at lower levels.

### Component State

At the component level, state management focuses on the health and operation of individual processing units:

```java
// Example of component state management
public void processItem(Item item) {
    // Update state to reflect processing
    setState(State.ACTIVE);
    
    try {
        // Process item
        Result result = processor.process(item);
        
        // Update properties with processing metrics
        setProperty("lastProcessedItem", item.getId());
        setProperty("lastProcessedTime", Instant.now());
        setProperty("processedCount", getProcessedCount() + 1);
        
        return result;
    } catch (Exception e) {
        // Update state to reflect error
        setState(State.DEGRADED);
        
        // Update properties with error information
        setProperty("lastError", e.getMessage());
        setProperty("errorCount", getErrorCount() + 1);
        setProperty("errorRate", calculateErrorRate());
        
        // Attempt recovery if possible
        if (isRecoverable(e)) {
            attemptRecovery();
        }
        
        throw e;
    }
}
```

### Composite State

Composites aggregate the states of their contained components to determine their own state:

```java
// Example of composite state determination
public void evaluateCompositeState() {
    int readyCount = 0;
    int activeCount = 0;
    int degradedCount = 0;
    int terminatingCount = 0;
    
    for (Component component : components) {
        switch (component.getState()) {
            case READY: readyCount++; break;
            case ACTIVE: activeCount++; break;
            case DEGRADED: degradedCount++; break;
            case TERMINATING: terminatingCount++; break;
        }
    }
    
    // Determine composite state based on component states
    if (degradedCount > criticalDegradedThreshold) {
        setState(State.DEGRADED);
    } else if (activeCount > 0 && degradedCount == 0) {
        setState(State.ACTIVE);
    } else if (readyCount == components.size()) {
        setState(State.READY);
    } else if (terminatingCount > 0) {
        setState(State.TERMINATING);
    }
    
    // Update composite properties
    setProperty("componentStates", collectComponentStates());
    setProperty("healthScore", calculateHealthScore());
}
```

### Machine State

Machines coordinate multiple composites and manage system-wide state:

```java
// Example of machine state determination
public void determineMachineState() {
    // Check critical composites first
    boolean criticalCompositeImpaired = false;
    
    for (Composite composite : criticalComposites) {
        if (composite.getState() == State.DEGRADED) {
            criticalCompositeImpaired = true;
            break;
        }
    }
    
    // Count composites in each state
    int readyCount = 0;
    int activeCount = 0;
    int degradedCount = 0;
    
    for (Composite composite : allComposites) {
        switch (composite.getState()) {
            case READY: readyCount++; break;
            case ACTIVE: activeCount++; break;
            case DEGRADED: degradedCount++; break;
        }
    }
    
    // Determine machine state
    if (criticalCompositeImpaired) {
        setState(State.DEGRADED);
    } else if (degradedCount > 0) {
        setState(State.ACTIVE); // Still active but with some degraded composites
    } else if (activeCount > 0) {
        setState(State.ACTIVE);
    } else {
        setState(State.READY);
    }
    
    // Update machine properties
    setProperty("compositeStates", collectCompositeStates());
    setProperty("systemUtilization", calculateSystemUtilization());
    setProperty("capability", assessSystemCapabilities());
}
```

## State Transitions

State transitions in S8r can occur in several ways, each serving a different purpose in system management.

### Intentional Transitions

Intentional transitions are explicitly requested by the component itself or an external controller:

```java
// Example of intentional state transition
public void startProcessing() {
    if (getState() == State.READY) {
        setState(State.ACTIVE);
        logger.info("Started processing");
        
        // Additional startup logic
        startProcessingThreads();
        notifyComponentStarted();
    } else {
        logger.warn("Cannot start processing from state: {}", getState());
    }
}

public void shutDown() {
    if (getState() == State.ACTIVE || getState() == State.DEGRADED) {
        setState(State.TERMINATING);
        logger.info("Shutting down");
        
        // Perform shutdown logic
        stopProcessingThreads();
        flushData();
        releaseResources();
        
        setState(State.TERMINATED);
        logger.info("Shutdown complete");
    } else {
        logger.warn("Cannot shut down from state: {}", getState());
    }
}
```

### Reactive Transitions

Reactive transitions occur when a component changes its state in response to observed conditions or events:

```java
// Example of reactive state transition
public void monitorHealthAndAdapt() {
    // Gather current metrics
    double errorRate = (double)getProperty("errorRate", 0.0);
    long queueDepth = (long)getProperty("queueDepth", 0L);
    
    // Check for degradation conditions
    if (errorRate > errorThreshold || queueDepth > queueThreshold) {
        if (getState() == State.ACTIVE) {
            // Transition to DEGRADED state
            logger.warn("Health metrics exceeding thresholds - degrading");
            setState(State.DEGRADED);
            
            // Apply degradation strategies
            reduceAcceptedLoad();
            simplifyProcessing();
            notifyDegradation();
        }
    } else if (getState() == State.DEGRADED) {
        // Recover to normal operation
        logger.info("Health metrics improved - restoring normal operation");
        setState(State.ACTIVE);
        
        // Restore normal operation
        restoreFullFunctionality();
        notifyRecovery();
    }
}
```

### Propagated Transitions

Propagated transitions occur when state changes in one component trigger state changes in related components:

```java
// Example of propagated state transition in a composite
public void onComponentStateChanged(Component component, State oldState, State newState) {
    logger.debug("Component {} state changed: {} -> {}", 
                component.getId(), oldState, newState);
    
    // If a critical component is degraded, the composite becomes degraded
    if (criticalComponents.contains(component) && newState == State.DEGRADED) {
        setState(State.DEGRADED);
        return;
    }
    
    // Re-evaluate composite state based on all components
    evaluateCompositeState();
}
```

## State Monitoring and Observability

The value of state in S8r comes not just from maintaining it, but from making it observable and actionable.

### Health Assessments

Health assessments provide a structured way to evaluate a component's operational wellness:

```java
public class HealthAssessment {
    private final Instant timestamp;
    private final String status; // e.g., "HEALTHY", "DEGRADED", "CRITICAL"
    private final Map<String, Number> metrics;
    private final List<String> warnings;
    
    // Constructor and getters
    
    public static class Builder {
        // Builder implementation
    }
}

// Example of health assessment
public HealthAssessment assessHealth() {
    HealthAssessment.Builder builder = new HealthAssessment.Builder()
        .withTimestamp(Instant.now());
    
    // Gather metrics
    double errorRate = (double)getProperty("errorRate", 0.0);
    long queueDepth = (long)getProperty("queueDepth", 0L);
    double processingRate = (double)getProperty("processingRate", 0.0);
    
    // Add metrics to assessment
    builder.withMetric("errorRate", errorRate)
           .withMetric("queueDepth", queueDepth)
           .withMetric("processingRate", processingRate);
    
    // Determine status and add warnings if needed
    if (errorRate > criticalErrorThreshold) {
        builder.withStatus("CRITICAL")
               .withWarning("Error rate exceeds critical threshold");
    } else if (errorRate > warningErrorThreshold) {
        builder.withStatus("DEGRADED")
               .withWarning("Error rate exceeds warning threshold");
    } else if (queueDepth > criticalQueueThreshold) {
        builder.withStatus("CRITICAL")
               .withWarning("Queue depth exceeds critical threshold");
    } else if (queueDepth > warningQueueThreshold) {
        builder.withStatus("DEGRADED")
               .withWarning("Queue depth exceeds warning threshold");
    } else if (processingRate < minimumProcessingRate) {
        builder.withStatus("DEGRADED")
               .withWarning("Processing rate below minimum threshold");
    } else {
        builder.withStatus("HEALTHY");
    }
    
    return builder.build();
}
```

### Vital Statistics

Vital statistics provide a focused view of a component's most important metrics, similar to how a doctor checks pulse, blood pressure, and temperature:

```java
public class VitalStats {
    private final Instant timestamp;
    private final State state;
    private final long itemsProcessed;
    private final long itemsInQueue;
    private final long errorCount;
    private final double processingRatePerSecond;
    private final double averageLatencyMs;
    
    // Constructor and getters
}

// Example of gathering vital statistics
public VitalStats gatherVitalStats() {
    return new VitalStats(
        Instant.now(),
        getState(),
        (long)getProperty("processedCount", 0L),
        (long)getProperty("queueDepth", 0L),
        (long)getProperty("errorCount", 0L),
        (double)getProperty("processingRate", 0.0),
        (double)getProperty("averageLatency", 0.0)
    );
}
```

### System-Wide Observability

Beyond individual component state, S8r emphasizes system-wide observability through:

1. **Hierarchical Rollups**: Aggregating state information up the hierarchy from components to composites to machines

2. **Cross-Cutting Views**: Providing views across components by type, domain, or other dimensions

3. **State History**: Maintaining historical state information for trend analysis

4. **Anomaly Detection**: Identifying unusual state patterns that may indicate emerging issues

```java
// Example of system-wide state snapshot
public SystemStateSnapshot captureSystemState() {
    Map<String, State> componentStates = new HashMap<>();
    Map<String, State> compositeStates = new HashMap<>();
    Map<String, Object> aggregateMetrics = new HashMap<>();
    
    // Capture component states
    for (Component component : getAllComponents()) {
        componentStates.put(component.getIdentity().getFullId(), component.getState());
    }
    
    // Capture composite states
    for (Composite composite : getAllComposites()) {
        compositeStates.put(composite.getIdentity().getFullId(),
                composite.getState());
    }
    
    // Calculate aggregate metrics
    aggregateMetrics.put("totalComponentCount", getAllComponents().size());
    aggregateMetrics.put("activeComponentCount",
            countComponentsInState(State.ACTIVE));
    aggregateMetrics.put("degradedComponentCount",
            countComponentsInState(State.DEGRADED));
    // Other metrics...
    
    return new SystemStateSnapshot(
            Instant.now(),
            machineState,
            componentStates,
            compositeStates,
            aggregateMetrics
    );
}
```

## Implementation Guidelines

Effective state management requires careful implementation to ensure consistency, performance, and clarity.

### State Implementation

1. **Use an Enum for Primary State**: Enums provide type safety and clear documentation of possible states.

2. **Validate State Transitions**: Ensure transitions only occur between valid states.

3. **Make State Changes Thread-Safe**: Use proper synchronization or volatile variables.

4. **Log State Changes**: Maintain visibility into state transitions for debugging.

5. **Use State in Decision Logic**: Make behavior dependent on current state.

```java
// Example of state implementation
public class BaseComponent implements Component {
    private volatile State state = State.CONCEPTION;
    private final Object stateLock = new Object();
    private final List<StateChangeListener> stateListeners = new CopyOnWriteArrayList<>();
    
    @Override
    public State getState() {
        return state;
    }
    
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
    
    private boolean isValidTransition(State from, State to) {
        // Implementation of state transition rules
        switch (from) {
            case CONCEPTION:
                return to == State.INITIALIZING;
            case INITIALIZING:
                return to == State.CONFIGURING || to == State.TERMINATING;
            // Additional cases...
            default:
                return false;
        }
    }
    
    private void notifyStateChange(State oldState, State newState) {
        for (StateChangeListener listener : stateListeners) {
            try {
                listener.onStateChanged(this, oldState, newState);
            } catch (Exception e) {
                logger.error("Error notifying listener", e);
            }
        }
    }
}
```

### Properties Implementation

1. **Use a Flexible Structure**: Properties should accommodate various types of information without requiring code changes.

2. **Employ Builder Pattern**: Make property updates clear and fluent with a builder approach.

3. **Include Timestamps**: Attach timestamps to property changes for temporal context.

4. **Balance Detail and Performance**: Capture enough detail to be useful without excessive overhead.

5. **Consider Thread Safety**: Ensure property updates are thread-safe.

```java
// Example of properties implementation
public class ComponentWithProperties implements Component {
    private final Map<String, Object> properties = 
        new ConcurrentHashMap<>();
    
    @Override
    public void setProperty(String key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException("Property key cannot be null");
        }
        
        if (value == null) {
            properties.remove(key);
        } else {
            properties.put(key, value);
            
            // Automatically add timestamp for when property was last updated
            properties.put(key + ".lastUpdated", Instant.now());
        }
    }
    
    @Override
    public Object getProperty(String key) {
        return properties.get(key);
    }
    
    @Override
    public Object getProperty(String key, Object defaultValue) {
        Object value = properties.get(key);
        return value != null ? value : defaultValue;
    }
    
    @Override
    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }
    
    @Override
    public Map<String, Object> getAllProperties() {
        return new HashMap<>(properties); // Defensive copy
    }
}
```

### State Synchronization

In distributed systems, state synchronization becomes a critical consideration:

```java
// Example of state synchronization
public class DistributedComponent extends BaseComponent {
    private final StateCoordinator coordinator;
    
    public DistributedComponent(Identity identity, Environment env, 
                                StateCoordinator coordinator) {
        super(identity, env);
        this.coordinator = coordinator;
        
        // Register for state updates from other nodes
        coordinator.registerStateListener(getIdentity().getFullId(), 
                                         this::onRemoteStateChange);
    }
    
    @Override
    public void setState(State newState) {
        super.setState(newState);
        
        // Propagate state change to other nodes
        coordinator.publishStateChange(getIdentity().getFullId(), newState);
    }
    
    private void onRemoteStateChange(State remoteState) {
        // Handle remote state change without triggering another propagation
        if (getState() != remoteState) {
            logger.info("Synchronizing with remote state: {}", remoteState);
            super.setState(remoteState); // Use super to avoid re-publishing
        }
    }
}
```

## Advanced State Management

As systems grow in complexity, advanced state management techniques become necessary.

### State Machines

For components with complex state transition rules, a formal state machine approach provides clarity and correctness:

```java
public class OrderStateMachine {
    private enum OrderState {
        CREATED, VALIDATED, PAYMENT_PENDING, PAID, FULFILLING,
        SHIPPED, DELIVERED, CANCELLED
    }
    
    private final Map<OrderState, Set<OrderState>> validTransitions = new HashMap<>();
    private OrderState currentState = OrderState.CREATED;
    
    public OrderStateMachine() {
        // Define valid transitions
        validTransitions.put(OrderState.CREATED,
                Set.of(OrderState.VALIDATED, OrderState.CANCELLED));
        validTransitions.put(OrderState.VALIDATED,
                Set.of(OrderState.PAYMENT_PENDING, OrderState.CANCELLED));
        // More transitions...
    }
    
    public synchronized boolean transitionTo(OrderState targetState) {
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
    
    private void executeActions(OrderState from, OrderState to) {
        // State-specific actions
    }
}
```

### State Persistence

For long-running or mission-critical systems, persisting state information can be essential:

```java
public class PersistentStateComponent extends BaseComponent {
    private final StateRepository stateRepository;
    
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
            for (Map.Entry<String, Object> entry : 
                    state.getProperties().entrySet()) {
                setProperty(entry.getKey(), entry.getValue());
            }
            
            logger.info("State restored from repository");
        }
    }
    
    @Override
    public void setState(State newState) {
        super.setState(newState);
        
        // Persist state change
        saveState();
    }
    
    @Override
    public void setProperty(String key, Object value) {
        super.setProperty(key, value);
        
        // Persist property change
        saveState();
    }
    
    private void saveState() {
        ComponentState state = new ComponentState(
            getIdentity().getFullId(),
            getState(),
            getAllProperties(),
            Instant.now()
        );
        
        stateRepository.saveState(state);
    }
}
```

### Distributed State

In distributed systems, maintaining coherent state across multiple nodes requires special consideration:

```java
public class DistributedStateCordinator implements StateCoordinator {
    private final Map<String, Set<StateChangeListener>> listeners = 
        new ConcurrentHashMap<>();
    private final StateDistributor distributor;
    
    public DistributedStateCordinator(StateDistributor distributor) {
        this.distributor = distributor;
        
        // Register to receive state changes from the distributor
        distributor.subscribeToStateChanges(this::onDistributedStateChange);
    }
    
    @Override
    public void registerStateListener(String componentId,
                                     StateChangeListener listener) {
        listeners.computeIfAbsent(componentId, k -> ConcurrentHashMap.newKeySet())
                .add(listener);
    }
    
    @Override
    public void unregisterStateListener(String componentId,
                                       StateChangeListener listener) {
        Set<StateChangeListener> componentListeners = listeners.get(componentId);
        if (componentListeners != null) {
            componentListeners.remove(listener);
        }
    }
    
    @Override
    public void publishStateChange(String componentId, State newState) {
        // Distribute to other nodes
        distributor.distributeStateChange(componentId, newState);
    }
    
    private void onDistributedStateChange(String componentId, State newState) {
        // Notify local listeners
        Set<StateChangeListener> componentListeners = listeners.get(componentId);
        if (componentListeners != null) {
            for (StateChangeListener listener : componentListeners) {
                try {
                    listener.onStateChanged(newState);
                } catch (Exception e) {
                    logger.error("Error notifying listener for component {}", 
                              componentId, e);
                }
            }
        }
    }
}
```

## State Management Patterns

Certain patterns in state management have emerged as particularly valuable in S8r systems.

### Self-Healing State

Components that can detect and recover from failures automatically:

```java
public class SelfHealingComponent extends BaseComponent {
    private int consecutiveFailures = 0;
    private final int healingThreshold = 3;
    private Instant lastFailureTime;
    private final Duration cooldownPeriod = Duration.ofMinutes(5);
    
    @Override
    public Object process(Object input) {
        try {
            // Check if we need to attempt recovery
            if (getState() == State.DEGRADED &&
                consecutiveFailures >= healingThreshold) {
                
                attemptRecovery();
            }
            
            // Process normally if active
            if (getState() == State.ACTIVE) {
                Object result = super.process(input);
                consecutiveFailures = 0; // Reset on success
                return result;
            } else {
                // Still degraded
                consecutiveFailures++;
                lastFailureTime = Instant.now();
                return new ErrorResult("Component in degraded state");
            }
        } catch (Exception e) {
            // Handle failure
            consecutiveFailures++;
            lastFailureTime = Instant.now();
            
            if (getState() != State.DEGRADED) {
                setState(State.DEGRADED);
            }
            
            throw e;
        }
    }
    
    private void attemptRecovery() {
        logger.info("Attempting self-healing after {} consecutive failures",
                  consecutiveFailures);
        
        try {
            // Try different recovery strategies
            boolean recovered = false;
            
            // Strategy 1: Reset internal state
            recovered = resetInternalState();
            
            if (!recovered) {
                // Strategy 2: Reconnect to dependencies
                recovered = reconnectDependencies();
            }
            
            if (!recovered) {
                // Strategy 3: Restart the component
                recovered = restartComponent();
            }
            
            if (recovered) {
                logger.info("Self-healing successful");
                consecutiveFailures = 0;
                setState(State.ACTIVE);
            } else {
                logger.warn("Self-healing failed, will retry later");
            }
        } catch (Exception e) {
            logger.error("Error during self-healing", e);
        }
    }
    
    // Recovery strategy implementations
}
```

### Degraded State

Components that can operate with reduced functionality when resources are constrained:

```java
public class DegradableModeComponent extends BaseComponent {
    private OperationMode currentMode = OperationMode.FULL;
    
    private enum OperationMode {
        FULL,     // All features available
        REDUCED,  // Non-essential features disabled
        MINIMAL,  // Only critical functions
        OFFLINE   // No processing, return error
    }
    
    @Override
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
                return new DegradedResult(
                    null,
                    "System in OFFLINE mode, processing unavailable"
                );
                
            default:
                return new DegradedResult(
                    null,
                    "Unknown operation mode: " + currentMode
                );
        }
    }
    
    private void assessHealthAndUpdateMode() {
        try {
            // Check resources and dependencies
            ResourceStatus resources = checkResources();
            DependencyStatus dependencies = checkDependencies();
            
            // Determine appropriate mode
            OperationMode newMode = determineOperationMode(
                    dependencies, resources);
            
            // Update if changed
            if (newMode != currentMode) {
                logger.info("Changing operation mode from {} to {}",
                          currentMode, newMode);
                currentMode = newMode;
                
                // Update component state based on operation mode
                if (newMode == OperationMode.FULL) {
                    setState(State.ACTIVE);
                } else if (newMode == OperationMode.OFFLINE) {
                    setState(State.DEGRADED);
                } else {
                    // REDUCED or MINIMAL are still technically ACTIVE
                    // but with reduced capabilities
                    setState(State.ACTIVE);
                }
            }
        } catch (Exception e) {
            logger.error("Error assessing health", e);
            
            // Default to minimal mode on assessment error
            currentMode = OperationMode.MINIMAL;
        }
    }
    
    // Mode-specific processing methods
}
```

### State-Based Routing

Using state to direct the flow of work in the system:

```java
public class StateAwareRouter extends BaseComponent {
    private final Map<String, Component> routes = new HashMap<>();
    
    public void registerRoute(String routeName, Component destination) {
        routes.put(routeName, destination);
    }
    
    @Override
    public Object process(Object input) {
        // Choose the route based on component states
        String selectedRoute = selectRouteBasedOnState();
        
        // Get the destination
        Component destination = routes.get(selectedRoute);
        if (destination == null) {
            throw new IllegalStateException("No route found: " + selectedRoute);
        }
        
        // Forward the input
        return destination.process(input);
    }
    
    private String selectRouteBasedOnState() {
        // Find the first ACTIVE route with least load
        Component bestRoute = null;
        long lowestLoad = Long.MAX_VALUE;
        
        for (Map.Entry<String, Component> entry : routes.entrySet()) {
            Component route = entry.getValue();
            
            if (route.getState() == State.ACTIVE) {
                long currentLoad = (long)route.getProperty("currentLoad", 0L);
                
                if (currentLoad < lowestLoad) {
                    bestRoute = route;
                    lowestLoad = currentLoad;
                }
            }
        }
        
        if (bestRoute != null) {
            return bestRoute.getIdentity().getId();
        }
        
        // All routes degraded or no active routes
        // Find the first DEGRADED route as fallback
        for (Map.Entry<String, Component> entry : routes.entrySet()) {
            Component route = entry.getValue();
            
            if (route.getState() == State.DEGRADED) {
                return route.getIdentity().getId();
            }
        }
        
        // No usable routes
        throw new IllegalStateException("No active or degraded routes available");
    }
}
```

## Common Challenges and Solutions

Implementing effective state management in S8r systems comes with several common challenges:

### Challenge: State Explosion

As systems grow, the number of potential state combinations can explode, making it difficult to reason about system behavior.

**Solution: State Abstraction and Hierarchical Properties**

```java
// Example of hierarchical state management
public class HierarchicalStateComponent extends BaseComponent {
    // Use the unified State enum for primary state
    
    // Create hierarchical properties for detailed state information
    public void initializeStateProperties() {
        // Create nested property structure
        Map<String, Object> processingProps = new HashMap<>();
        processingProps.put("rate", 0.0);
        processingProps.put("count", 0);
        processingProps.put("errors", 0);
        
        Map<String, Object> resourceProps = new HashMap<>();
        resourceProps.put("memoryUsage", 0.0);
        resourceProps.put("cpuUsage", 0.0);
        resourceProps.put("connections", 0);
        
        // Set nested properties
        setProperty("processing", processingProps);
        setProperty("resources", resourceProps);
    }
    
    // Update nested properties
    public void updateProcessingRate(double rate) {
        @SuppressWarnings("unchecked")
        Map<String, Object> processingProps = 
            (Map<String, Object>)getProperty("processing");
        
        processingProps.put("rate", rate);
        setProperty("processing", processingProps);
    }
}
```

### Challenge: State Visibility Delays

In distributed systems, state changes may not be immediately visible to all components, leading to temporary inconsistencies.

**Solution: Eventually Consistent State**

```java
// Example of eventually consistent state
public class DistributedComponent extends BaseComponent {
    private final StateCoordinator coordinator;
    
    public DistributedComponent(String id, Environment env, StateCoordinator coordinator) {
        super(id, env);
        this.coordinator = coordinator;
        
        // Register for state updates from other nodes
        coordinator.registerStateListener(getId(), this::onRemoteStateChange);
    }
    
    @Override
    public void setState(State newState) {
        super.setState(newState);
        
        // Publish state change asynchronously to other nodes
        coordinator.publishStateChange(getId(), newState);
    }
    
    private void onRemoteStateChange(State remoteState) {
        // Update local state to match remote if appropriate
        if (shouldAcceptRemoteState(remoteState)) {
            getLogger().info("Synchronizing with remote state: {}", remoteState);
            super.setState(remoteState);
        }
    }
    
    private boolean shouldAcceptRemoteState(State remoteState) {
        // Logic to determine if remote state should override local state
        // based on state precedence rules
        return true; // Simplified for example
    }
}
```

---

*"The state of a component tells the story of its journey—where it has been, what it has experienced, and how it has adapted along the way."*

[← Return to Core Concepts](./core-concepts.md) | [Explore Component Patterns →](../guides/component-patterns.md)
