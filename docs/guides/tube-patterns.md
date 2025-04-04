# Tube Patterns


## Table of Contents
- [Introduction: The Language of Flow](#introduction-the-language-of-flow)
- [Foundational Patterns](#foundational-patterns)
    - [Validator-Transformer-Persister](#validator-transformer-persister)
    - [Filter-Map-Reduce](#filter-map-reduce)
    - [Observer](#observer)
    - [Circuit Breaker](#circuit-breaker)
- [Flow Control Patterns](#flow-control-patterns)
    - [Splitter-Aggregator](#splitter-aggregator)
    - [Router](#router)
    - [Load Balancer](#load-balancer)
    - [Throttle](#throttle)
- [State Management Patterns](#state-management-patterns)
    - [State Machine](#state-machine)
    - [Snapshot](#snapshot)
    - [Compensating Transaction](#compensating-transaction)
    - [Checkpoint-Recovery](#checkpoint-recovery)
- [Composition Patterns](#composition-patterns)
    - [Pipeline](#pipeline)
    - [Chain of Responsibility](#chain-of-responsibility)
    - [Decorator](#decorator)
    - [Composite](#composite)
- [Adaptation Patterns](#adaptation-patterns)
    - [Self-Healing](#self-healing)
    - [Dynamic Reconfiguration](#dynamic-reconfiguration)
    - [Degraded Mode](#degraded-mode)
    - [Evolutionary](#evolutionary)
- [Anti-Patterns and Their Remedies](#anti-patterns-and-their-remedies)
    - [Leaky Tube](#leaky-tube)
    - [Monolithic Tube](#monolithic-tube)
    - [Entangled Tubes](#entangled-tubes)
    - [Stagnant Pool](#stagnant-pool)
- [Applying Patterns in Context](#applying-patterns-in-context)

## Introduction: the Language of Flow

Just as nature has evolved recurring patterns that solve common problems across different ecosystems, software development benefits from recognizable patterns that address recurring challenges. In Samstraumr, these patterns speak the language of flow—describing how data, control, and state move through interconnected tubes to create resilient, adaptive systems.

This document catalogs patterns that have emerged within the Samstraumr community. Each pattern represents a tested solution to a common design challenge, building upon the foundational concepts of tubes, flows, and self-awareness. These patterns aren't rigid formulas but rather inspirations for your own flowing systems—templates to be adapted to your unique context and needs.

As you explore these patterns, you'll notice how they build upon each other, combining like tributaries flowing into a river to create systems of increasing sophistication and resilience.

## Foundational Patterns

### Validator-transformer-persister

**Context:** Processing input data often requires validation, transformation into a different format, and persistence.

**Pattern Structure:**

**Implementation:**

```java
// Validator Tube
public class CustomerDataValidatorTube implements Tube {
    private TubeProcessor createProcessor() {
        return input -> {
            CustomerData data = (CustomerData) input;
            List<String> validationErrors = validateCustomerData(data);

            if (validationErrors.isEmpty()) {
                return ValidationResult.valid(data);
            } else {
                return ValidationResult.invalid(validationErrors);
            }
        };
    }
}

// Transformer Tube
public class CustomerEntityTransformerTube implements Tube {
    private TubeProcessor createProcessor() {
        return input -> {
            ValidationResult result = (ValidationResult) input;

            if (!result.isValid()) {
                return TransformationResult.failed(result.getErrors());
            }

            CustomerData data = (CustomerData) result.getValue();
            CustomerEntity entity = mapToEntity(data);

            return TransformationResult.success(entity);
        };
    }
}

// Persister Tube
public class CustomerRepositoryTube implements Tube {
    private final CustomerRepository repository;

    private TubeProcessor createProcessor() {
        return input -> {
            TransformationResult result = (TransformationResult) input;

            if (!result.isSuccess()) {
                return PersistenceResult.failed(result.getErrors());
            }

            CustomerEntity entity = (CustomerEntity) result.getValue();
            CustomerEntity saved = repository.save(entity);

            return PersistenceResult.success(saved);
        };
    }
}
[Collection] → [Filter Tube] → [Map Tube] → [Reduce Tube] → [Result]

**Benefits:**
- Aligns with functional programming concepts
- Cleanly handles collection processing
- Makes data transformations explicit
- Each step is independently testable

**Variations:**
- Add a Sorter Tube between steps
- Include a Limiter Tube to process only a subset
- Use a Partitioner Tube to split processing across multiple paths

### Observer

**Context:** Need to monitor data flowing through a system without altering it.

**Pattern Structure:**

**Implementation:**

```java
public class MetricsObserverTube implements Tube {
    private final MetricsService metricsService;

    private TubeProcessor createProcessor() {
        return input -> {
            // Record metrics without changing the input
            if (input instanceof Transaction) {
                Transaction tx = (Transaction) input;
                metricsService.recordTransactionValue(tx.getAmount());
                metricsService.incrementTransactionCount();
            }

            // Return input unchanged
            return input;
        };
    }
}

public class AuditLogObserverTube implements Tube {
    private final AuditLogger auditLogger;

    private TubeProcessor createProcessor() {
        return input -> {
            // Log for audit purposes
            auditLogger.logEvent(
                "PROCESSED",
                input.getClass().getSimpleName(),
                objectToJson(input)
            );

            // Return input unchanged
            return input;
        };
    }
}
[Source] → [Circuit Breaker Tube] → [Protected Service]

**Benefits:**
- Prevents cascading failures
- Allows system degradation rather than collapse
- Provides automatic recovery
- Improves system resilience

**Variations:**
- Percentage-based thresholds instead of count
- Multiple circuit levels with different thresholds
- Bulkhead pattern for isolation between different services

## Flow Control Patterns

### Splitter-aggregator

**Context:** Need to divide work for parallel processing and then recombine results.

**Pattern Structure:**

**Implementation:**

```java
// Splitter
public class WorkSplitterTube implements Tube {
    private TubeProcessor createProcessor() {
        return input -> {
            LargeWorkload workload = (LargeWorkload) input;
            List<Task> tasks = splitIntoTasks(workload);

            return new SplitResult(tasks, UUID.randomUUID());
        };
    }
}

// Parallel Processor (one instance per path)
public class TaskProcessorTube implements Tube {
    private TubeProcessor createProcessor() {
        return input -> {
            Task task = (Task) input;
            TaskResult result = processTask(task);

            return result;
        };
    }
}

// Aggregator
public class ResultAggregatorTube implements Tube {
    private final Map<UUID, List<TaskResult>> partialResults = new ConcurrentHashMap<>();

    private TubeProcessor createProcessor() {
        return input -> {
            ProcessingContext context = (ProcessingContext) input;
            UUID correlationId = context.getCorrelationId();
            TaskResult result = context.getResult();

            // Store partial result
            partialResults.computeIfAbsent(correlationId, k -> new ArrayList<>())
                .add(result);

            // Check if we have all results
            List<TaskResult> results = partialResults.get(correlationId);
            if (results.size() == context.getTotalTasks()) {
                // We have all results, aggregate them
                CompositeResult finalResult = aggregateResults(results);

                // Remove from map to free memory
                partialResults.remove(correlationId);

                return finalResult;
            } else {
                // Still waiting for more results
                return new WaitingResult(
                    correlationId,
                    results.size(),
                    context.getTotalTasks()
                );
            }
        };
    }
}
              ┌→ [Processor A]
[Router] → ───┼→ [Processor B]
              └→ [Processor C]

**Benefits:**
- Decouples routing logic from processing logic
- Allows dynamic addition of new routes
- Centralizes routing decisions
- Simplifies conditional processing

**Variations:**
- Content-Based Router examines message content
- Context-Based Router uses environmental factors
- Dynamic Router modifies routes at runtime
- Multicast Router sends to multiple destinations

### Load balancer

**Context:** Need to distribute workload across multiple instances of the same tube for scalability.

**Pattern Structure:**

**Implementation:**

```java
public class LoadBalancerTube implements Tube {
    private final List<Tube> workers = new ArrayList<>();
    private int currentIndex = 0;
    private final Object indexLock = new Object();

    public LoadBalancerTube(int workerCount) {
        // Create multiple instances of the worker tube
        for (int i = 0; i < workerCount; i++) {
            workers.add(new WorkerTube("Worker-" + i));
        }
    }

    private TubeProcessor createProcessor() {
        return input -> {
            // Select the next worker using a round-robin approach
            Tube selectedWorker;
            synchronized (indexLock) {
                selectedWorker = workers.get(currentIndex);
                currentIndex = (currentIndex + 1) % workers.size();
            }

            // Delegate processing to the selected worker
            try {
                return selectedWorker.process(input);
            } catch (Exception e) {
                // If a worker fails, try another one
                synchronized (indexLock) {
                    selectedWorker = workers.get(currentIndex);
                    currentIndex = (currentIndex + 1) % workers.size();
                }
                return selectedWorker.process(input);
            }
        };
    }
}
[Source] → [Throttle Tube] → [Rate-Sensitive System]

**Benefits:**
- Prevents overwhelming downstream systems
- Smooths out traffic spikes
- Improves overall system stability
- Enables predictable performance

**Variations:**
- Token Bucket throttling for burst handling
- Adaptive throttling based on downstream health
- Priority-based throttling for critical vs. non-critical requests
- Scheduled throttling for time-based rate changes

## State Management Patterns

### State machine

**Context:** Need to manage complex state transitions in a clear, maintainable way.

**Pattern Structure:**
A tube that maintains a current state and defines valid transitions between states.

**Implementation:**

```java
public class OrderStateMachineTube implements Tube {
    private enum OrderState {
        CREATED, PAID, SHIPPED, DELIVERED, CANCELLED
    }

    private final Map<OrderState, Set<OrderState>> validTransitions = new HashMap<>();

    public OrderStateMachineTube() {
        // Define valid state transitions
        validTransitions.put(OrderState.CREATED,
            Set.of(OrderState.PAID, OrderState.CANCELLED));
        validTransitions.put(OrderState.PAID,
            Set.of(OrderState.SHIPPED, OrderState.CANCELLED));
        validTransitions.put(OrderState.SHIPPED,
            Set.of(OrderState.DELIVERED, OrderState.CANCELLED));
        validTransitions.put(OrderState.DELIVERED,
            Set.of());  // Terminal state
        validTransitions.put(OrderState.CANCELLED,
            Set.of());  // Terminal state
    }

    private TubeProcessor createProcessor() {
        return input -> {
            StateTransitionRequest request = (StateTransitionRequest) input;
            Order order = request.getOrder();
            OrderState currentState = OrderState.valueOf(order.getStatus());
            OrderState targetState = request.getTargetState();

            // Validate transition
            if (!validTransitions.get(currentState).contains(targetState)) {
                return new StateTransitionResult(
                    false,
                    "Invalid transition from " + currentState +
                    " to " + targetState,
                    order
                );
            }

            // Perform transition actions
            try {
                performTransitionActions(order, currentState, targetState);
                order.setStatus(targetState.name());
                return new StateTransitionResult(true, null, order);
            } catch (Exception e) {
                return new StateTransitionResult(
                    false,
                    "Error during transition: " + e.getMessage(),
                    order
                );
            }
        };
    }

    private void performTransitionActions(
            Order order,
            OrderState from,
            OrderState to) {
        // Implement state-specific transition logic
        switch (to) {
            case PAID:
                processPayment(order);
                break;
            case SHIPPED:
                arrangeShipping(order);
                break;
            // Other cases
        }
    }
}

**Benefits:**
- Enables system recovery after failures
- Facilitates debugging complex states
- Allows system migration or duplication
- Supports time-travel debugging

**Variations:**
- Incremental Snapshot for efficiency
- Scheduled Snapshot for regular captures
- Distributed Snapshot for coordinated system state
- Multi-Version Snapshot for historical tracking

### Compensating transaction

**Context:** Need to undo or compensate for effects of a completed operation in a distributed system.

**Pattern Structure:**
For each operation, define a compensating operation that can undo its effects.

**Implementation:**

```java
public class CompensatingTransactionTube implements Tube {
    private final Map<String, List<CompensatingAction>> transactionLog =
        new ConcurrentHashMap<>();

    private TubeProcessor createProcessor() {
        return input -> {
            TransactionRequest request = (TransactionRequest) input;
            String transactionId = request.getTransactionId();

            // Create transaction record if it doesn't exist
            transactionLog.putIfAbsent(transactionId, new ArrayList<>());

            try {
                // Perform the requested action
                Object result = performAction(request.getAction());

                // Record compensating action
                CompensatingAction compensatingAction =
                    createCompensatingAction(request.getAction(), result);
                transactionLog.get(transactionId).add(compensatingAction);

                return new TransactionResult(true, result, null);
            } catch (Exception e) {
                // If action fails, compensate for any completed actions
                compensateTransaction(transactionId);

                return new TransactionResult(
                    false,
                    null,
                    "Transaction failed: " + e.getMessage()
                );
            }
        };
    }

    public void compensateTransaction(String transactionId) {
        List<CompensatingAction> actions = transactionLog.get(transactionId);

        if (actions == null || actions.isEmpty()) {
            return;
        }

        // Apply compensating actions in reverse order
        for (int i = actions.size() - 1; i >= 0; i--) {
            try {
                actions.get(i).compensate();
            } catch (Exception e) {
                logger.error("Compensation failed: {}", e.getMessage());
                // Continue with other compensations
            }
        }

        // Clear the log after compensation
        transactionLog.remove(transactionId);
    }

    private static interface CompensatingAction {
        void compensate() throws Exception;
    }
}

**Benefits:**
- Minimizes work lost during failures
- Enables efficient resumption
- Provides progress visibility
- Improves reliability of long-running processes

**Variations:**
- Event Sourcing for comprehensive replay
- Distributed Checkpoint for coordinated recovery
- Time-Based Checkpoint for regular intervals
- Milestone Checkpoint for logical progress points

## Composition Patterns

### Pipeline

**Context:** Need to sequence a series of processing steps with clear inputs and outputs for each stage.

**Pattern Structure:**

**Implementation:**

```java
public class Pipeline<I, O> {
    private final List<Tube> stages = new ArrayList<>();

    public Pipeline<I, O> addStage(Tube tube) {
        stages.add(tube);
        return this;
    }

    public O process(I input) {
        Object current = input;

        for (Tube stage : stages) {
            current = stage.process(current);
        }

        return (O) current;
    }
}

// Usage example
Pipeline<RawOrder, ShippingLabel> orderProcessingPipeline = new Pipeline<>()
    .addStage(new OrderValidatorTube())
    .addStage(new PaymentProcessorTube())
    .addStage(new InventoryAllocationTube())
    .addStage(new ShippingLabelGeneratorTube());

ShippingLabel label = orderProcessingPipeline.process(rawOrder);
[Handler 1] → [Handler 2] → [Handler 3] → ... → [Default Handler]

**Benefits:**
- Decouples senders from receivers
- Allows dynamic configuration of processing chain
- Each handler has single responsibility
- Easy to add new handlers without modifying existing code

**Variations:**
- Bidirectional Chain for request-response
- Branching Chain for complex decision logic
- Prioritized Chain for handler ordering
- Chain with Fallback for guaranteed handling

### Decorator

**Context:** Need to add responsibilities to tubes dynamically without modifying their code.

**Pattern Structure:**

**Implementation:**

```java
// Base decorator
public abstract class TubeDecorator implements Tube {
    protected final Tube decoratedTube;

    public TubeDecorator(Tube decoratedTube) {
        this.decoratedTube = decoratedTube;
    }

    // Forward identity, state, and other tube methods
    @Override
    public BirthCertificate getIdentity() {
        return decoratedTube.getIdentity();
    }

    @Override
    public TubeState getDesignState() {
        return decoratedTube.getDesignState();
    }

    // Add the decorator-specific functionality in subclasses
}

// Concrete decorators
public class LoggingDecorator extends TubeDecorator {
    private final Logger logger = LoggerFactory.getLogger(LoggingDecorator.class);

    public LoggingDecorator(Tube decoratedTube) {
        super(decoratedTube);
    }

    @Override
    public Object process(Object input) {
        logger.info("Before processing: {}", input);

        Object result = decoratedTube.process(input);

        logger.info("After processing: {}", result);
        return result;
    }
}

public class CachingDecorator extends TubeDecorator {
    private final Map<Object, Object> cache = new ConcurrentHashMap<>();

    public CachingDecorator(Tube decoratedTube) {
        super(decoratedTube);
    }

    @Override
    public Object process(Object input) {
        // Check cache first
        if (cache.containsKey(input)) {
            return cache.get(input);
        }

        // Process and cache result
        Object result = decoratedTube.process(input);
        cache.put(input, result);
        return result;
    }
}

// Usage
Tube baseTube = new DataProcessorTube();
Tube cachedTube = new CachingDecorator(baseTube);
Tube loggedAndCachedTube = new LoggingDecorator(cachedTube);

// Use the decorated tube
Object result = loggedAndCachedTube.process(input);
[Composite Tube]
    |
    ├── [Leaf Tube A]
    ├── [Leaf Tube B]
    └── [Another Composite]
            |
            ├── [Leaf Tube C]
            └── [Leaf Tube D]

**Benefits:**
- Treats individual tubes and compositions uniformly
- Simplifies client code
- Allows building complex nested structures
- Provides a unified view of system state

**Variations:**
- Conditional Composite for selective processing
- Parallel Composite for concurrent execution
- Weighted Composite for priority-based processing
- Self-organizing Composite for adaptive structures

## Adaptation Patterns

### Self-healing

**Context:** Need tubes that can detect and recover from failures automatically.

**Pattern Structure:**
A tube that monitors its own health and takes corrective action when problems are detected.

**Implementation:**

```java
public class SelfHealingTube implements Tube {
    private final Tube coreTube;
    private int consecutiveFailures = 0;
    private final int healingThreshold = 3;
    private Instant lastFailureTime;
    private Duration cooldownPeriod = Duration.ofMinutes(5);

    private TubeProcessor createProcessor() {
        return input -> {
            try {
                // Check if we need to attempt recovery
                if (coreTube.getDesignState() == TubeState.ERROR &&
                    consecutiveFailures >= healingThreshold) {

                    attemptRecovery();
                }

                // Process normally if flowing
                if (coreTube.getDesignState() == TubeState.FLOWING) {
                    Object result = coreTube.process(input);
                    consecutiveFailures = 0; // Reset on success
                    return result;
                } else {
                    // Still in error state
                    consecutiveFailures++;
                    lastFailureTime = Instant.now();
                    return new ErrorResult("Tube in error state: " +
                                        coreTube.getDesignState());
                }
            } catch (Exception e) {
                // Handle failure
                consecutiveFailures++;
                lastFailureTime = Instant.now();

                return new ErrorResult("Processing failed: " + e.getMessage());
            }
        };
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
                // Strategy 3: Restart the tube
                recovered = restartTube();
            }

            if (recovered) {
                logger.info("Self-healing successful");
                consecutiveFailures = 0;
            } else {
                logger.warn("Self-healing failed, will retry later");
            }
        } catch (Exception e) {
            logger.error("Error during self-healing: {}", e.getMessage());
        }
    }

    // Recovery strategies
    private boolean resetInternalState() {
        // Implementation of state reset logic
        return false; // Indicate success or failure
    }

    private boolean reconnectDependencies() {
        // Implementation of dependency reconnection
        return false;
    }

    private boolean restartTube() {
        // Implementation of tube restart
        return false;
    }
}

**Benefits:**
- Adapts to changing workloads
- Optimizes resource usage
- Improves performance over time
- Reduces manual tuning

**Variations:**
- Policy-Based Reconfiguration for rule-driven changes
- Learning-Based Reconfiguration using historical data
- Collaborative Reconfiguration coordinating with other tubes
- Profile-Based Reconfiguration for predefined scenarios

### Degraded mode

**Context:** Need tubes that can continue operating with reduced functionality when resources are constrained or dependencies are unavailable.

**Pattern Structure:**
A tube that detects resource constraints or dependency failures and switches to a simplified operation mode.

**Implementation:**

```java
public class DegradedModeTube implements Tube {
    private OperationMode currentMode = OperationMode.FULL;
    private final DependencyMonitor dependencyMonitor;
    private final ResourceMonitor resourceMonitor;

    private TubeProcessor createProcessor() {
        return input -> {
            // Check health before processing
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
        };
    }

    private void assessHealthAndUpdateMode() {
        try {
            // Check dependencies
            DependencyStatus dependencies = dependencyMonitor.checkDependencies();

            // Check resources
            ResourceStatus resources = resourceMonitor.checkResources();

            // Determine appropriate mode
            OperationMode newMode = determineOperationMode(dependencies, resources);

            // Update if changed
            if (newMode != currentMode) {
                logger.info("Changing operation mode from {} to {}",
                          currentMode, newMode);
                currentMode = newMode;

                if (newMode == OperationMode.FULL) {
                    setDesignState(TubeState.FLOWING);
                } else {
                    setDesignState(TubeState.ADAPTING);
                }
            }
        } catch (Exception e) {
            logger.error("Error assessing health: {}", e.getMessage());

            // Default to minimal mode on assessment error
            currentMode = OperationMode.MINIMAL;
        }
    }

    private OperationMode determineOperationMode(
            DependencyStatus dependencies,
            ResourceStatus resources) {
        // Critical dependency unavailable
        if (!dependencies.isCriticalDatabaseAvailable() ||
            !dependencies.isAuthServiceAvailable()) {
            return OperationMode.OFFLINE;
        }

        // Severe resource constraint
        if (resources.getMemoryAvailable() < 100_000_000) {
            return OperationMode.MINIMAL;
        }

        // Secondary dependencies unavailable or moderate resource constraints
        if (!dependencies.isEnrichmentServiceAvailable() ||
            resources.getCpuUtilization() > 0.8) {
            return OperationMode.REDUCED;
        }

        // Everything good
        return OperationMode.FULL;
    }

    private Object processWithFullFunctionality(Object input) {
        // Full processing with all features
        return null; // Implementation
    }

    private Object processWithReducedFunctionality(Object input) {
        // Processing with non-essential features disabled
        return null; // Implementation
    }

    private Object processWithMinimalFunctionality(Object input) {
        // Bare minimum processing to maintain critical functions
        return null; // Implementation
    }

    private enum OperationMode {
        FULL,     // All features available
        REDUCED,  // Non-essential features disabled
        MINIMAL,  // Only critical functions
        OFFLINE   // No processing, return error
    }
}

**Benefits:**
- Continuously improves over time
- Adapts to changing workload patterns
- Self-optimizes for performance
- Discovers unexpected solutions

**Variations:**
- Genetic Algorithm for strategy evolution
- A/B Testing for controlled experiments
- Multi-armed Bandit for exploration-exploitation balance
- Reinforcement Learning for complex adaptation

## Anti-Patterns and Their Remedies

### Leaky tube

**Anti-Pattern:** A tube that allows implementation details to leak through its interface, coupling it tightly to other tubes.

**Symptoms:**
- Changes to one tube ripple through multiple tubes
- Tubes make assumptions about the internal state of other tubes
- Types or constants from one tube are directly referenced in others

**Remedy: Interface Isolation**

```java
// Before: Leaky implementation
public class LeakyTube implements Tube {
    public static final int MAX_BATCH_SIZE = 100;
    public InternalState getInternalState() { ... }

    // Other methods exposing implementation details
}

// After: Clean interface
public class CleanTube implements Tube {
    private static final int MAX_BATCH_SIZE = 100;

    @Override
    public Object process(Object input) {
        // Process using internal state, returning only what's needed
        return result;
    }

    // No leaky methods exposing implementation details
}

**Prevention:**
- Follow the Single Responsibility Principle
- Limit tube size and complexity
- Create specialized tubes for distinct functions
- Use composition to build complex flows

### Entangled tubes

**Anti-Pattern:** Tubes that are tightly coupled and directly call each other, creating a tangled web of dependencies.

**Symptoms:**
- Circular dependencies between tubes
- Changes to one tube require changes to many others
- Testing one tube requires setting up many others
- Control flow is difficult to follow

**Remedy: Flow-Based Communication**

```java
// Before: Entangled tubes
public class EntangledTubeA implements Tube {
    private final EntangledTubeB tubeB;
    private final EntangledTubeC tubeC;

    private TubeProcessor createProcessor() {
        return input -> {
            // Direct calls to other tubes
            Object resultB = tubeB.process(input);
            Object resultC = tubeC.process(resultB);
            return resultC;
        };
    }
}

// After: Flow-based communication
public class DecoupledTubeA implements Tube {
    private TubeProcessor createProcessor() {
        return input -> {
            // Process without knowledge of other tubes
            return transformInput(input);
        };
    }
}

// External flow coordination
Pipeline<Object, Object> pipeline = new Pipeline<>()
    .addStage(new DecoupledTubeA())
    .addStage(new DecoupledTubeB())
    .addStage(new DecoupledTubeC());

**Prevention:**
- Implement feedback mechanisms
- Use dynamic configuration
- Track performance metrics
- Create learning loops
- Establish adaptation policies

## Applying Patterns in Context

These patterns aren't meant to be applied in isolation but combined thoughtfully to address specific requirements and constraints. Here are some guidelines for applying them effectively:

1. **Start with Foundational Patterns**: Begin with simple, well-understood patterns like Validator-Transformer-Persister or Pipeline before moving to more complex ones.

2. **Combine Patterns Judiciously**: Patterns can be composed, with each addressing a specific aspect of your system:
    - Use Observer for monitoring
    - Add Circuit Breaker for resilience
    - Apply Throttle for resource management
    - Include Self-Healing for recovery

3. **Let Requirements Drive Selection**: Choose patterns based on what your system needs:
    - For high availability, focus on Circuit Breaker, Degraded Mode, and Self-Healing
    - For performance, consider Load Balancer, Dynamic Reconfiguration, and Throttle
    - For maintainability, use Composite, Decorator, and Pipeline patterns

4. **Watch for Anti-Patterns**: Regularly review your code for signs of Leaky Tube, Monolithic Tube, Entangled Tubes, or Stagnant Pool.

5. **Evolve Your Pattern Usage**: As your system matures, you may need different patterns:
    - Early stage: Simpler patterns focusing on functionality
    - Growth stage: Add resilience and scalability patterns
    - Mature stage: Incorporate adaptation and optimization patterns

6. **Document Pattern Usage**: Create a pattern language for your team that describes how patterns are applied in your specific context.

Remember that patterns are tools, not rules. Apply them thoughtfully, adapt them to your needs, and don't be afraid to create new patterns as your system evolves.

---

*"The true nature of software, like water, is to flow, adapt, and find the most efficient path forward."*

[← Return to Core Concepts](./core-concepts.md) | [Explore Bundles and Machines →](./bundles-and-machines.md)
