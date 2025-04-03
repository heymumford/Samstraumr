# State Management: The Conscious Flow of Samstraumr


## Table of Contents
- [Introduction: States of Being and Becoming](#introduction-states-of-being-and-becoming)
- [The Dual State Model](#the-dual-state-model)
    - [Design State: Fundamental Modes of Being](#design-state-fundamental-modes-of-being)
    - [Dynamic State: The Ever-Changing Now](#dynamic-state-the-ever-changing-now)
    - [The Interplay Between States](#the-interplay-between-states)
- [State at Different Levels](#state-at-different-levels)
    - [Tube State](#tube-state)
    - [Bundle State](#bundle-state)
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
    - [Design State Implementation](#design-state-implementation)
    - [Dynamic State Implementation](#dynamic-state-implementation)
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

Samstraumr brings this natural awareness to software through its unique dual state model. This approach enables components to maintain both their essential identity and respond intelligently to changing circumstances.

This document explores how state management in Samstraumr mirrors natural systems, creating software that can sense, adapt, and maintain equilibrium even amid changing requirements and environments.

## The Dual State Model

At the heart of Samstraumr's approach to state management lies the recognition that truly adaptive systems must maintain two complementary types of state:

### Design State: Fundamental Modes of Being

Design State represents the core operational mode of a component—its fundamental condition that changes relatively infrequently but has significant implications for its behavior and capabilities.

Like the phases of water (solid, liquid, gas), Design State defines the essential nature of a component at a given moment. These states are typically represented as enumerated values with clear, distinct meanings:

```java
public enum TubeState {
    FLOWING,   // Normal operation with inputs processing and outputs generating
    BLOCKED,   // Temporarily unable to process inputs due to internal or external factors
    ADAPTING,  // Actively reconfiguring to respond to changing conditions
    ERROR      // Experiencing issues that prevent normal operation
## Common Challenges and Solutions

    Implementing effective state management in Samstraumr systems comes with several common challenges:

            ### Challenge: State Explosion

    As systems grow, the number of potential state combinations can explode, making it difficult to reason about system behavior.

**Solution: State Abstraction and Hierarchical State**

            ```java
    // Example of hierarchical state management
    public class HierarchicalStateMachine {
        // Top-level states
        private enum MainState {
            INITIALIZING, OPERATING, MAINTENANCE, SHUTDOWN
        }

        // Sub-states for OPERATING
        private enum OperatingState {
            NORMAL, DEGRADED, RECOVERY
        }

        private MainState mainState = MainState.INITIALIZING;
        private OperatingState operatingState = OperatingState.NORMAL;

        public void transitionTo(MainState newMainState) {
            if (mainState != newMainState) {
                // Handle exit actions for old state
                exitMainState(mainState);

                // Update state
                mainState = newMainState;

                // Handle entry actions for new state
                enterMainState(newMainState);
            }
        }

        public void transitionOperatingState(OperatingState newState) {
            if (mainState != MainState.OPERATING) {
                throw new IllegalStateException(
                        "Cannot change operating state when not in OPERATING state"
                );
            }

            if (operatingState != newState) {
                operatingState = newState;
                // Handle sub-state specific actions
            }
        }

        // State entry/exit methods
    }

### Challenge: State Visibility Delays

In distributed systems, state changes may not be immediately visible to all components, leading to temporary inconsistencies.

**Solution: Eventually Consistent State**

```java
// Example of eventually consistent state
public class EventuallyConsistentStateTube implements Tube {
    private final StateDistributor distributor;
    private final StateReconciler reconciler;

    @Override
    public void setDesignState(TubeState newState) {
        super.setDesignState(newState);

        // Publish state change asynchronously
        distributor.publishStateChange(getIdentity().getFullID(), newState);
    }

    public void startStateReconciliation() {
        // Schedule periodic state reconciliation
        reconciler.scheduleReconciliation(
                Duration.ofSeconds(30),
                () -> {
                    // Get authoritative state
                    TubeState authoritativeState =
                            distributor.getAuthoritativeState(getIdentity().getFullID());

                    // Update if different
                    if (authoritativeState != getDesignState()) {
                        logger.info("Reconciling state from {} to {}",
                                getDesignState(), authoritativeState);
                        super.setDesignState(authoritativeState);
                    }
                }
        );
    }
}

---

*"The state of a river tells the story of its journey—where it has been, what it has encountered, and how it has adapted along the way."*

[← Return to Core Concepts](./CoreConcepts.md) | [Explore Tube Patterns →](./TubePatterns.md)

Dynamic State might include:
- Processing statistics (items processed, success rates)
- Resource utilization (memory, connections, threads)
- Queue depths and backlog information
- Error counts and patterns
- Performance metrics (latency, throughput)
- Learning progress and adaptation metrics
- Contextual information specific to the component's domain

### The Interplay Between States

The magic of Samstraumr's approach emerges in the interplay between these two state types. They exist in a continuous dialogue:

1. **Design State provides context for Dynamic State**: The interpretation of Dynamic State metrics depends on the current Design State. High latency during normal FLOWING operation might indicate a problem, but during ADAPTING, it could be expected.

2. **Dynamic State influences Design State transitions**: Persistent patterns in Dynamic State often trigger Design State changes. For example, a sustained error rate above threshold might transition a tube from FLOWING to ERROR.

3. **Dynamic State adapts to Design State**: When Design State changes, the structure and focus of Dynamic State often changes as well. A tube in ERROR state might track different metrics than one in FLOWING state.

This continuous feedback loop creates components that are both stable in their fundamental nature and highly responsive to changing conditions—much like living organisms that maintain their identity while constantly adapting to their environment.

## State at Different Levels

State management in Samstraumr operates across all levels of the system hierarchy, with each level building upon and extending the patterns established at lower levels.

### Tube State

At the tube level, state management focuses on the health and operation of individual processing units:

**Design State** for tubes defines their fundamental operational capacity:
- `FLOWING`: The tube is processing inputs and generating outputs normally
- `BLOCKED`: The tube is temporarily unable to process inputs, perhaps due to a full queue or unavailable resource
- `ADAPTING`: The tube is reconfiguring itself in response to changing conditions
- `ERROR`: The tube is experiencing issues that prevent normal operation

**Dynamic State** for tubes typically tracks:
- Processing metrics (throughput, latency)
- Error rates and patterns
- Resource utilization
- Queue depths
- Recent inputs and outputs

```java
// Example of updating tube state
public void processBatch(List<Item> items) {
    // Update design state to reflect processing
    setDesignState(TubeState.FLOWING);

    // Track metrics in dynamic state
    DynamicState.Builder stateBuilder = new DynamicState.Builder()
            .withTimestamp(Instant.now())
            .withProperty("processing", true)
            .withMetric("batchSize", items.size())
            .withMetric("queueDepth", inputQueue.size());

    try {
        // Process items
        for (Item item : items) {
            processItem(item);
            stateBuilder.withMetric("processedCount",
                    incrementProcessedCount());
        }

        // Update dynamic state with final metrics
        stateBuilder.withProperty("processing", false)
                .withMetric("successRate", calculateSuccessRate())
                .withMetric("averageLatency", calculateAverageLatency());

        setDynamicState(stateBuilder.build());
    } catch (Exception e) {
        // Update state to reflect error
        setDesignState(TubeState.ERROR);
        setDynamicState(new DynamicState.Builder()
                .withTimestamp(Instant.now())
                .withProperty("processing", false)
                .withProperty("lastError", e.getMessage())
                .withMetric("errorCount", incrementErrorCount())
                .build());
    }
}

### Machine State

At the machine level, state management coordinates the operation of multiple bundles working together to address complex domains:

**Design State** for machines reflects the system-wide operational status:
- `OPERATIONAL`: The system is functioning within expected parameters
- `PARTIAL`: Some bundles are degraded but core functions remain operational
- `REORGANIZING`: Major internal restructuring is in progress
- `IMPAIRED`: Critical capabilities are unavailable

**Dynamic State** for machines typically encompasses:
- System-wide health indicators
- Cross-bundle performance metrics
- Capability availability matrix
- Resource allocation tracking
- SLA compliance metrics

```java
// Example of machine state determination
public void determineMachineState() {
    // Check critical bundles first
    boolean criticalBundleImpaired = false;

    for (Bundle bundle : criticalBundles) {
        if (bundle.getDesignState() == BundleState.CRITICAL) {
            criticalBundleImpaired = true;
            break;
        }
    }

    // Count bundles in each state
    int flowingCount = 0;
    int degradedCount = 0;
    int adaptingCount = 0;
    int criticalCount = 0;

    for (Bundle bundle : allBundles) {
        switch (bundle.getDesignState()) {
            case FLOWING: flowingCount++; break;
            case DEGRADED: degradedCount++; break;
            case ADAPTING: adaptingCount++; break;
            case CRITICAL: criticalCount++; break;
        }
    }

    // Determine machine state
    if (criticalBundleImpaired || criticalCount > maxCriticalThreshold) {
        setDesignState(MachineState.IMPAIRED);
    } else if (adaptingCount > maxAdaptingThreshold) {
        setDesignState(MachineState.REORGANIZING);
    } else if (degradedCount > 0 || criticalCount > 0) {
        setDesignState(MachineState.PARTIAL);
    } else {
        setDesignState(MachineState.OPERATIONAL);
    }

    // Update dynamic state with capability matrix
    Map<String, Boolean> capabilityMatrix = assessCapabilityAvailability();
    setDynamicState(new DynamicState.Builder()
            .withTimestamp(Instant.now())
            .withProperty("capabilityMatrix", capabilityMatrix)
            .withMetric("availableCapabilityPercentage",
                    calculateAvailableCapabilityPercentage())
            .withMetric("systemLoadAverage", getSystemLoadAverage())
            .build());
}

### Reactive Transitions

Reactive transitions occur when a component changes its state in response to observed conditions or events:

```java
// Example of reactive state transition
public void monitorHealthAndAdapt() {
    // Gather current metrics
    HealthAssessment health = monitor.assessHealth();

    // Check for error conditions
    if (health.getErrorRate() > errorThreshold) {
        // Transition to ERROR state
        logger.warn("Error rate ({}) exceeds threshold ({})",
                health.getErrorRate(), errorThreshold);

        setDesignState(TubeState.ERROR);
        setDynamicState(new DynamicState.Builder()
                .withTimestamp(Instant.now())
                .withProperty("healthStatus", "ERROR")
                .withMetric("errorRate", health.getErrorRate())
                .withMetric("threshold", errorThreshold)
                .build());

        // Attempt recovery
        initiateRecoveryProcedure();
        return;
    }

    // Check for adaptation needs
    if (health.getPerformanceScore() < performanceThreshold) {
        // Transition to ADAPTING state
        logger.info("Performance ({}) below target ({}), adapting",
                health.getPerformanceScore(), performanceThreshold);

        setDesignState(TubeState.ADAPTING);

        // Begin adaptation
        reconfigureForBetterPerformance();
    }
}

## State Monitoring and Observability

The value of state in Samstraumr comes not just from maintaining it, but from making it observable and actionable:

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

### Vital Statistics

Vital statistics provide a focused view of a component's most important metrics, similar to how a doctor checks pulse, blood pressure, and temperature:

```java
public class VitalStats {
    private final Instant timestamp;
    private final TubeState designState;
    private final long itemsProcessed;
    private final long itemsInQueue;
    private final long errorCount;
    private final double processingRatePerSecond;
    private final double averageLatencyMs;

    // Constructor and getters
}

### System-Wide Observability

Beyond individual component state, Samstraumr emphasizes system-wide observability through:

1. **Hierarchical Rollups**: Aggregating state information up the hierarchy from tubes to bundles to machines

2. **Cross-Cutting Views**: Providing views across components by type, domain, or other dimensions

3. **State History**: Maintaining historical state information for trend analysis

4. **Anomaly Detection**: Identifying unusual state patterns that may indicate emerging issues

```java
// Example of system-wide state snapshot
public SystemStateSnapshot captureSystemState() {
    Map<String, TubeState> tubeStates = new HashMap<>();
    Map<String, BundleState> bundleStates = new HashMap<>();
    Map<String, Object> aggregateMetrics = new HashMap<>();

    // Capture tube states
    for (Tube tube : getAllTubes()) {
        tubeStates.put(tube.getIdentity().getFullID(), tube.getDesignState());
    }

    // Capture bundle states
    for (Bundle bundle : getAllBundles()) {
        bundleStates.put(bundle.getIdentity().getFullID(),
                bundle.getDesignState());
    }

    // Calculate aggregate metrics
    aggregateMetrics.put("totalTubeCount", getAllTubes().size());
    aggregateMetrics.put("flowingTubeCount",
            countTubesInState(TubeState.FLOWING));
    aggregateMetrics.put("errorTubeCount",
            countTubesInState(TubeState.ERROR));
    // Other metrics...

    return new SystemStateSnapshot(
            Instant.now(),
            machineState,
            tubeStates,
            bundleStates,
            aggregateMetrics
    );
}

### Dynamic State Implementation

1. **Use a Flexible Structure**: Dynamic state should accommodate various types of information without requiring code changes.

2. **Employ Builder Pattern**: Make dynamic state construction clear and fluent with a builder.

3. **Include Timestamps**: Always attach a timestamp to dynamic state for temporal context.

4. **Balance Detail and Performance**: Capture enough detail to be useful without excessive overhead.

5. **Consider Immutability**: Make dynamic state immutable to prevent inconsistent state.

```java
// Example of dynamic state implementation
public final class DynamicState {
    private final Instant timestamp;
    private final Map<String, Object> properties;
    private final Map<String, Number> metrics;

    private DynamicState(Instant timestamp,
                         Map<String, Object> properties,
                         Map<String, Number> metrics) {
        this.timestamp = timestamp;
        // Create defensive copies for immutability
        this.properties = Collections.unmodifiableMap(
                new HashMap<>(properties)
        );
        this.metrics = Collections.unmodifiableMap(
                new HashMap<>(metrics)
        );
    }

    // Getters with no setters for immutability

    // Builder implementation
}

## Advanced State Management

As systems grow in complexity, advanced state management techniques become necessary:

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

### Distributed State

In distributed systems, maintaining coherent state across multiple nodes requires special consideration:

```java
public class DistributedStateTube implements Tube {
    private final StateCoordinator coordinator;
    private final String tubeId;

    public DistributedStateTube(String tubeId, StateCoordinator coordinator) {
        this.tubeId = tubeId;
        this.coordinator = coordinator;

        // Register for state updates from other nodes
        coordinator.registerStateListener(tubeId, this::onRemoteStateChange);
    }

    @Override
    public void setDesignState(TubeState newState) {
        super.setDesignState(newState);

        // Distribute state change to other nodes
        coordinator.publishStateChange(tubeId, newState);
    }

    private void onRemoteStateChange(TubeState remoteState) {
        // Update local state to match remote
        if (remoteState != getDesignState()) {
            logger.info("Synchronizing with remote state: {}", remoteState);
            super.setDesignState(remoteState);
        }
    }
}

### Degraded State

Components that can operate with reduced functionality when resources are constrained:

```java
public class DegradableBundle extends BaseBundle {
    private final Set<Tube> criticalTubes = new HashSet<>();
    private final Set<Tube> nonCriticalTubes = new HashSet<>();

    @Override
    public void onTubeStateChanged(Tube tube, TubeState oldState,
                                   TubeState newState) {
        if (newState == TubeState.ERROR) {
            if (criticalTubes.contains(tube)) {
                // Critical tube failure
                setDesignState(BundleState.CRITICAL);
            } else {
                // Non-critical tube failure, enter degraded mode
                setDesignState(BundleState.DEGRADED);

                // Implement degraded mode:
                // 1. Disable non-essential features
                // 2. Reduce processing rates
                // 3. Simplify responses
            }
        }
    }
}

---

*"The state of a river tells the story of its journey—where it has been, what it has encountered, and how it has adapted along the way."*

[← Return to Core Concepts](./CoreConcepts.md) | [Explore Tube Patterns →](./TubePatterns.md)
