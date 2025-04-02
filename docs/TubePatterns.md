# Tube Patterns: Design Wisdom for Flowing Systems

```
Last updated: April 2, 2025
Author: Eric C. Mumford (@heymumford)
Contributors: Samstraumr Core Team
```

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

## Introduction: The Language of Flow

Just as nature has evolved recurring patterns that solve common problems across different ecosystems, software development benefits from recognizable patterns that address recurring challenges. In Samstraumr, these patterns speak the language of flow—describing how data, control, and state move through interconnected tubes to create resilient, adaptive systems.

This document catalogs patterns that have emerged within the Samstraumr community. Each pattern represents a tested solution to a common design challenge, building upon the foundational concepts of tubes, flows, and self-awareness. These patterns aren't rigid formulas but rather inspirations for your own flowing systems—templates to be adapted to your unique context and needs.

As you explore these patterns, you'll notice how they build upon each other, combining like tributaries flowing into a river to create systems of increasing sophistication and resilience.

## Foundational Patterns

### Validator-Transformer-Persister

**Context:** Processing input data often requires validation, transformation into a different format, and persistence.

**Pattern Structure:**
```
[Input] → [Validator Tube] → [Transformer Tube] → [Persister Tube] → [Output]
```

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
```

**Benefits:**
- Clear separation of concerns
- Focused responsibility for each tube
- Flexible composition and reuse
- Natural flow of processing with progressive refinement

**Variations:**
- Add an Enricher Tube to augment data with additional information
- Include a Normalizer Tube for standardizing inputs
- Add a Notifier Tube at the end to communicate results

### Filter-Map-Reduce

**Context:** Processing collections of data by filtering unwanted items, transforming the remaining items, and aggregating them into a result.

**Pattern Structure:**
```
[Collection] → [Filter Tube] → [Map Tube] → [Reduce Tube] → [Result]
```

**Implementation:**

```java
// Filter Tube
public class ActiveUserFilterTube implements Tube {
    private TubeProcessor createProcessor() {
        return input -> {
            List<User> users = (List<User>) input;
            List<User> activeUsers = users.stream()
                .filter(user -> user.isActive())
                .collect(Collectors.toList());

            return new FilterResult<>(activeUsers);
        };
    }
}

// Map Tube
public class UserToProfileMapperTube implements Tube {
    private TubeProcessor createProcessor() {
        return input -> {
            FilterResult<User> result = (FilterResult<User>) input;
            List<UserProfile> profiles = result.getItems().stream()
                .map(this::convertToProfile)
                .collect(Collectors.toList());

            return new MapResult<>(profiles);
        };
    }

    private UserProfile convertToProfile(User user) {
        // Conversion logic
    }
}

// Reduce Tube
public class ProfileAggregatorTube implements Tube {
    private TubeProcessor createProcessor() {
        return input -> {
            MapResult<UserProfile> result = (MapResult<UserProfile>) input;
            Map<String, List<UserProfile>> profilesByDepartment =
                result.getItems().stream()
                    .collect(Collectors.groupingBy(
                        UserProfile::getDepartment
                    ));

            return new ReduceResult<>(profilesByDepartment);
        };
    }
}
```

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
```
                  ┌→ [Observer Tube A]
[Source] → [Flow] ┼→ [Observer Tube B]
                  └→ [Flow continues]
```

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
```

**Benefits:**
- Non-intrusive monitoring
- Separation of concerns
- Multiple observers can watch the same flow
- Easy to add or remove without changing core flow

**Variations:**
- Security Observer for intrusion detection
- Performance Observer for timing operations
- Debugging Observer for development troubleshooting

### Circuit Breaker

**Context:** Need to protect systems from cascading failures when downstream services fail.

**Pattern Structure:**
```
[Source] → [Circuit Breaker Tube] → [Protected Service]
```

**Implementation:**

```java
public class CircuitBreakerTube implements Tube {
    private CircuitState circuitState = CircuitState.CLOSED;
    private int failureCount = 0;
    private Instant lastFailureTime;

    private static final int FAILURE_THRESHOLD = 5;
    private static final Duration RESET_TIMEOUT = Duration.ofMinutes(1);

    private TubeProcessor createProcessor() {
        return input -> {
            // Check circuit state
            if (circuitState == CircuitState.OPEN) {
                // Check if we should try to reset
                if (Duration.between(lastFailureTime, Instant.now())
                        .compareTo(RESET_TIMEOUT) > 0) {
                    circuitState = CircuitState.HALF_OPEN;
                } else {
                    return new CircuitBreakerResult(
                        null,
                        "Circuit open - too many failures"
                    );
                }
            }

            try {
                // Call the protected service
                Object result = callProtectedService(input);

                // On success, reset circuit if needed
                if (circuitState == CircuitState.HALF_OPEN) {
                    circuitState = CircuitState.CLOSED;
                }
                failureCount = 0;

                return new CircuitBreakerResult(result, null);
            } catch (Exception e) {
                // Handle failure
                failureCount++;
                lastFailureTime = Instant.now();

                if (failureCount >= FAILURE_THRESHOLD ||
                    circuitState == CircuitState.HALF_OPEN) {
                    circuitState = CircuitState.OPEN;
                }

                return new CircuitBreakerResult(
                    null,
                    "Service call failed: " + e.getMessage()
                );
            }
        };
    }

    private enum CircuitState {
        CLOSED, OPEN, HALF_OPEN
    }
}
```

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

### Splitter-Aggregator

**Context:** Need to divide work for parallel processing and then recombine results.

**Pattern Structure:**
```
              ┌→ [Processor A] →┐
[Splitter] →  ├→ [Processor B] →┼→ [Aggregator] → [Result]
              └→ [Processor C] →┘
```

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
```

**Benefits:**
- Enables parallel processing
- Manages complexity of dividing and recombining work
- Improves throughput for suitable workloads
- Maintains correlation between split parts

**Variations:**
- Priority-based processing for different split paths
- Dynamic splitting based on current system load
- Recursive splitting for hierarchical tasks

### Router

**Context:** Need to direct inputs to different processing paths based on content or context.

**Pattern Structure:**
```
              ┌→ [Processor A]
[Router] → ───┼→ [Processor B]
              └→ [Processor C]
```

**Implementation:**

```java
public class ContentBasedRouterTube implements Tube {
    private final Map<String, Tube> routes = new HashMap<>();

    public ContentBasedRouterTube() {
        routes.put("invoice", new InvoiceProcessorTube());
        routes.put("payment", new PaymentProcessorTube());
        routes.put("refund", new RefundProcessorTube());
    }

    private TubeProcessor createProcessor() {
        return input -> {
            Document document = (Document) input;
            String documentType = document.getType();

            Tube processor = routes.getOrDefault(
                documentType,
                routes.get("default")
            );

            if (processor == null) {
                return new RoutingResult(
                    null,
                    false,
                    "No route found for document type: " + documentType
                );
            }

            try {
                Object result = processor.process(document);
                return new RoutingResult(result, true, null);
            } catch (Exception e) {
                return new RoutingResult(
                    null,
                    false,
                    "Error processing document: " + e.getMessage()
                );
            }
        };
    }
}
```

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

### Load Balancer

**Context:** Need to distribute workload across multiple instances of the same tube for scalability.

**Pattern Structure:**
```
                 ┌→ [Processor Instance 1]
[Load Balancer] →┼→ [Processor Instance 2]
                 └→ [Processor Instance 3]
```

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
```

**Benefits:**
- Improves throughput by distributing load
- Provides horizontal scalability
- Increases resilience through redundancy
- Simplifies adding or removing capacity

**Variations:**
- Round-Robin balancing for equal distribution
- Least-Busy balancing for optimal resource use
- Consistent Hashing for related messages
- Adaptive balancing based on performance metrics

### Throttle

**Context:** Need to limit the rate of processing to prevent overload of downstream systems.

**Pattern Structure:**
```
[Source] → [Throttle Tube] → [Rate-Sensitive System]
```

**Implementation:**

```java
public class ThrottleTube implements Tube {
    private final int maxRequestsPerSecond;
    private final Queue<Long> requestTimestamps = new LinkedList<>();
    private final Object lockObject = new Object();

    public ThrottleTube(int maxRequestsPerSecond) {
        this.maxRequestsPerSecond = maxRequestsPerSecond;
    }

    private TubeProcessor createProcessor() {
        return input -> {
            synchronized (lockObject) {
                long currentTime = System.currentTimeMillis();

                // Remove timestamps older than 1 second
                while (!requestTimestamps.isEmpty() &&
                       requestTimestamps.peek() < currentTime - 1000) {
                    requestTimestamps.poll();
                }

                // Check if we're at the limit
                if (requestTimestamps.size() >= maxRequestsPerSecond) {
                    // Calculate delay needed
                    long oldestTimestamp = requestTimestamps.peek();
                    long timeToWait = 1000 - (currentTime - oldestTimestamp);

                    if (timeToWait > 0) {
                        try {
                            Thread.sleep(timeToWait);
                            currentTime = System.currentTimeMillis();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }

                // Record this request
                requestTimestamps.add(currentTime);
            }

            // Forward the request to the next tube
            return input;
        };
    }
}
```

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

### State Machine

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
```

**Benefits:**
- Makes state transitions explicit
- Prevents invalid state changes
- Centralizes transition logic
- Simplifies complex state management

**Variations:**
- Hierarchical State Machine for nested states
- Event-Driven State Machine for external triggers
- Persistent State Machine for durable transitions
- Distributed State Machine for coordinated processes

### Snapshot

**Context:** Need to capture the current state of a system for later restoration or analysis.

**Pattern Structure:**
A mechanism to capture, store, and restore the complete state of a tube or bundle.

**Implementation:**

```java
public class SnapshotCapableTube implements Tube {
    // Regular tube state
    private TubeState designState;
    private DynamicState dynamicState;
    private Map<String, Object> processingContext = new HashMap<>();

    // Snapshot capability
    public SnapshotData createSnapshot() {
        return new SnapshotData(
            Instant.now(),
            identity.getFullID(),
            designState,
            dynamicState,
            new HashMap<>(processingContext)  // Deep copy
        );
    }

    public void restoreFromSnapshot(SnapshotData snapshot) {
        // Validate snapshot belongs to this tube
        if (!identity.getFullID().equals(snapshot.getTubeId())) {
            throw new IllegalArgumentException(
                "Snapshot belongs to different tube: " +
                snapshot.getTubeId()
            );
        }

        // Restore state
        this.designState = snapshot.getDesignState();
        this.dynamicState = snapshot.getDynamicState();
        this.processingContext = new HashMap<>(snapshot.getContext());

        // Log restoration
        logger.info("Restored state from snapshot taken at {}",
                  snapshot.getTimestamp());
    }

    // Regular tube implementation
    private TubeProcessor createProcessor() {
        return input -> {
            // Update context with processing data
            processingContext.put("lastInput", input);

            // Regular processing logic
            Object result = doProcessing(input);

            processingContext.put("lastOutput", result);
            processingContext.put("lastProcessingTime", Instant.now());

            return result;
        };
    }

    // Snapshot data class
    public static class SnapshotData implements Serializable {
        private final Instant timestamp;
        private final String tubeId;
        private final TubeState designState;
        private final DynamicState dynamicState;
        private final Map<String, Object> context;

        // Constructor and getters
    }
}
```

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

### Compensating Transaction

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
```

**Benefits:**
- Enables recovery in distributed systems
- Maintains eventual consistency
- Supports reliable undo operations
- Provides failure resilience in complex workflows

**Variations:**
- Saga Pattern for coordinated transactions
- Choreography-based compensation
- Orchestration-based compensation
- Timeout-triggered compensation

### Checkpoint-Recovery

**Context:** Need to recover from failures without losing progress in long-running processes.

**Pattern Structure:**
Periodically save progress information to allow resuming from the last stable point.

**Implementation:**

```java
public class CheckpointingTube implements Tube {
    private final CheckpointStore checkpointStore;
    private final String processName;
    private int itemsProcessedSinceLastCheckpoint = 0;
    private final int checkpointFrequency = 100;

    private TubeProcessor createProcessor() {
        return input -> {
            BatchProcessingRequest request = (BatchProcessingRequest) input;
            List<Item> items = request.getItems();
            List<ProcessingResult> results = new ArrayList<>();

            // Check for existing checkpoint
            Checkpoint checkpoint = checkpointStore.getLatestCheckpoint(
                processName,
                request.getBatchId()
            );

            int startIndex = 0;
            if (checkpoint != null) {
                startIndex = checkpoint.getNextItemIndex();
                logger.info("Resuming from checkpoint at index {}", startIndex);
            }

            // Process items from the checkpoint onwards
            for (int i = startIndex; i < items.size(); i++) {
                try {
                    ProcessingResult result = processItem(items.get(i));
                    results.add(result);

                    itemsProcessedSinceLastCheckpoint++;

                    // Create checkpoint periodically
                    if (itemsProcessedSinceLastCheckpoint >= checkpointFrequency) {
                        createCheckpoint(processName, request.getBatchId(), i + 1);
                        itemsProcessedSinceLastCheckpoint = 0;
                    }
                } catch (Exception e) {
                    logger.error("Processing failed at index {}: {}",
                                i, e.getMessage());

                    // Create a final checkpoint before failing
                    createCheckpoint(processName, request.getBatchId(), i);

                    return new BatchResult(
                        false,
                        results,
                        "Processing failed at index " + i + ": " + e.getMessage()
                    );
                }
            }

            // Create final checkpoint
            createCheckpoint(processName, request.getBatchId(), items.size());

            return new BatchResult(true, results, null);
        };
    }

    private void createCheckpoint(String processName, String batchId, int nextIndex) {
        Checkpoint checkpoint = new Checkpoint(
            processName,
            batchId,
            nextIndex,
            Instant.now()
        );

        checkpointStore.saveCheckpoint(checkpoint);
        logger.info("Created checkpoint at index {}", nextIndex);
    }
}
```

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
```
[Stage 1] → [Stage 2] → [Stage 3] → ... → [Stage N]
```

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
```

**Benefits:**
- Clear representation of sequential processing
- Easy to add, remove, or reorder stages
- Each stage has a focused responsibility
- Simple to understand and trace execution flow

**Variations:**
- Conditional Pipeline with branching paths
- Parallel Pipeline for independent stages
- Dynamic Pipeline with runtime configuration
- Bidirectional Pipeline for request-response flows

### Chain of Responsibility

**Context:** Need to process a request through a series of handlers, with each deciding whether to process it or pass it along.

**Pattern Structure:**
```
[Handler 1] → [Handler 2] → [Handler 3] → ... → [Default Handler]
```

**Implementation:**

```java
public abstract class HandlerTube implements Tube {
    protected HandlerTube nextHandler;

    public void setNextHandler(HandlerTube nextHandler) {
        this.nextHandler = nextHandler;
    }

    protected abstract boolean canHandle(Request request);
    protected abstract Response doHandle(Request request);

    private TubeProcessor createProcessor() {
        return input -> {
            Request request = (Request) input;

            if (canHandle(request)) {
                return doHandle(request);
            } else if (nextHandler != null) {
                return nextHandler.process(request);
            } else {
                return new Response(
                    false,
                    "No handler found for request: " + request.getType()
                );
            }
        };
    }
}

// Concrete handlers
public class AuthenticationHandlerTube extends HandlerTube {
    protected boolean canHandle(Request request) {
        return "AUTHENTICATION".equals(request.getType());
    }

    protected Response doHandle(Request request) {
        // Handle authentication
        return new Response(true, "Authentication successful");
    }
}

public class PaymentHandlerTube extends HandlerTube {
    protected boolean canHandle(Request request) {
        return "PAYMENT".equals(request.getType());
    }

    protected Response doHandle(Request request) {
        // Handle payment
        return new Response(true, "Payment processed");
    }
}

// Usage
AuthenticationHandlerTube authHandler = new AuthenticationHandlerTube();
PaymentHandlerTube paymentHandler = new PaymentHandlerTube();
ShippingHandlerTube shippingHandler = new ShippingHandlerTube();
DefaultHandlerTube defaultHandler = new DefaultHandlerTube();

authHandler.setNextHandler(paymentHandler);
paymentHandler.setNextHandler(shippingHandler);
shippingHandler.setNextHandler(defaultHandler);

// Process request through the chain
Response response = (Response) authHandler.process(new Request("PAYMENT", data));
```

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
```
[Core Tube] → [Decorator A] → [Decorator B] → [Client]
```

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
```

**Benefits:**
- Adds behavior without modifying original tubes
- Allows combining behaviors in different ways
- Follows the Open/Closed Principle
- Creates a clean separation of concerns

**Variations:**
- Security Decorator for authorization
- Timing Decorator for performance monitoring
- Validation Decorator for input checking
- Transformation Decorator for protocol adaptation

### Composite

**Context:** Need to treat groups of tubes as a single unit while maintaining the same interface.

**Pattern Structure:**
```
[Composite Tube]
    |
    ├── [Leaf Tube A]
    ├── [Leaf Tube B]
    └── [Another Composite]
            |
            ├── [Leaf Tube C]
            └── [Leaf Tube D]
```

**Implementation:**

```java
public class CompositeTube implements Tube {
    private final List<Tube> childTubes = new ArrayList<>();
    private final BirthCertificate identity;
    private TubeState designState = TubeState.FLOWING;

    public CompositeTube(String id) {
        this.identity = new BirthCertificate.Builder()
            .withID(id)
            .withPurpose("Composite tube")
            .withCreationTime(Instant.now())
            .build();
    }

    public void addTube(Tube tube) {
        childTubes.add(tube);
    }

    public void removeTube(Tube tube) {
        childTubes.remove(tube);
    }

    @Override
    public Object process(Object input) {
        // Process through all child tubes in sequence
        Object current = input;

        for (Tube tube : childTubes) {
            if (tube.getDesignState() == TubeState.FLOWING) {
                current = tube.process(current);
            }
        }

        return current;
    }

    @Override
    public TubeState getDesignState() {
        // Derive composite state from children
        boolean anyBlocked = childTubes.stream()
            .anyMatch(t -> t.getDesignState() == TubeState.BLOCKED);

        boolean anyAdapting = childTubes.stream()
            .anyMatch(t -> t.getDesignState() == TubeState.ADAPTING);

        boolean anyError = childTubes.stream()
            .anyMatch(t -> t.getDesignState() == TubeState.ERROR);

        if (anyError) {
            return TubeState.ERROR;
        } else if (anyBlocked) {
            return TubeState.BLOCKED;
        } else if (anyAdapting) {
            return TubeState.ADAPTING;
        } else {
            return TubeState.FLOWING;
        }
    }

    // Other tube interface implementations
}

// Usage
CompositeTube orderProcessing = new CompositeTube("OrderProcessor");
orderProcessing.addTube(new ValidationTube());
orderProcessing.addTube(new PricingTube());

CompositeTube fulfillment = new CompositeTube("Fulfillment");
fulfillment.addTube(new InventoryTube());
fulfillment.addTube(new ShippingTube());

CompositeTube completeProcess = new CompositeTube("CompleteOrderProcess");
completeProcess.addTube(orderProcessing);
completeProcess.addTube(fulfillment);
completeProcess.addTube(new NotificationTube());

// Use like any other tube
Object result = completeProcess.process(order);
```

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

### Self-Healing

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
```

**Benefits:**
- Reduces need for manual intervention
- Improves system resilience
- Supports continuous operation
- Addresses transient failures automatically

**Variations:**
- Retry Pattern for temporary failures
- Fallback Pattern for alternative processing
- Circuit Breaker for protecting dependencies
- Bulkhead Pattern for isolation of failures

### Dynamic Reconfiguration

**Context:** Need tubes that can adjust their configuration at runtime based on changing conditions.

**Pattern Structure:**
A tube that monitors its performance and environmental factors to adjust its internal configuration.

**Implementation:**

```java
public class DynamicReconfigurationTube implements Tube {
    private Configuration currentConfig;
    private final ConfigurationMonitor monitor;
    private final ScheduledExecutorService scheduler =
        Executors.newSingleThreadScheduledExecutor();

    public DynamicReconfigurationTube() {
        // Initialize with default configuration
        this.currentConfig = loadDefaultConfiguration();
        this.monitor = new ConfigurationMonitor();

        // Schedule periodic configuration checks
        scheduler.scheduleAtFixedRate(
            this::evaluateAndUpdateConfiguration,
            1, 5, TimeUnit.MINUTES
        );
    }

    private TubeProcessor createProcessor() {
        return input -> {
            // Use current configuration for processing
            return processWithConfiguration(input, currentConfig);
        };
    }

    private void evaluateAndUpdateConfiguration() {
        try {
            // Gather current metrics and environment state
            PerformanceMetrics metrics = monitor.gatherMetrics();
            EnvironmentState environment = monitor.assessEnvironment();

            // Determine if configuration change is needed
            Configuration optimalConfig =
                determineOptimalConfiguration(metrics, environment);

            // Apply if different from current
            if (!optimalConfig.equals(currentConfig)) {
                logger.info("Reconfiguring from {} to {}",
                          currentConfig.getVersion(),
                          optimalConfig.getVersion());

                currentConfig = optimalConfig;

                // Notify of configuration change
                setDesignState(TubeState.ADAPTING);
                // Apply changes...
                setDesignState(TubeState.FLOWING);
            }
        } catch (Exception e) {
            logger.error("Error during configuration evaluation: {}",
                       e.getMessage());
        }
    }

    private Configuration determineOptimalConfiguration(
            PerformanceMetrics metrics,
            EnvironmentState environment) {
        // Logic to determine best configuration
        Configuration.Builder builder = new Configuration.Builder();

        // Adjust batch size based on throughput and latency
        if (metrics.getAverageLatency() > 100 && metrics.getThroughput() < 1000) {
            builder.withBatchSize(
                Math.max(10, currentConfig.getBatchSize() / 2)
            );
        } else if (metrics.getAverageLatency() < 50 &&
                  metrics.getResourceUtilization() < 0.7) {
            builder.withBatchSize(
                Math.min(1000, currentConfig.getBatchSize() * 2)
            );
        } else {
            builder.withBatchSize(currentConfig.getBatchSize());
        }

        // Adjust thread pool based on system load
        if (environment.getSystemLoad() > 0.8) {
            builder.withThreadCount(
                Math.max(2, currentConfig.getThreadCount() - 1)
            );
        } else if (environment.getSystemLoad() < 0.4 &&
                  metrics.getQueueDepth() > 100) {
            builder.withThreadCount(
                Math.min(16, currentConfig.getThreadCount() + 1)
            );
        } else {
            builder.withThreadCount(currentConfig.getThreadCount());
        }

        // Other configuration parameters...

        return builder.build();
    }
}
```

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

### Degraded Mode

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
```

**Benefits:**
- Maintains critical functionality during stress
- Gracefully handles resource constraints
- Communicates limitations clearly
- Automatically recovers when conditions improve

**Variations:**
- Priority-Based Degradation for critical transactions
- Feature Toggles for selective functionality
- Tiered Service Levels with SLA guarantees
- Cache-Only Mode for disconnected operation

### Evolutionary

**Context:** Need tubes that can evolve their behavior over time based on feedback and observed patterns.

**Pattern Structure:**
A tube that maintains multiple implementation strategies and selects the best performing ones based on historical performance.

**Implementation:**

```java
public class EvolutionaryTube implements Tube {
    private final Map<String, Strategy> strategies = new HashMap<>();
    private final Map<String, PerformanceRecord> strategyPerformance = new HashMap<>();
    private final Random random = new Random();
    private String currentBestStrategy;

    public EvolutionaryTube() {
        // Initialize with known strategies
        registerStrategy("baseline", new BaselineStrategy());
        registerStrategy("optimized", new OptimizedStrategy());
        registerStrategy("experimental", new ExperimentalStrategy());

        // Start with baseline
        currentBestStrategy = "baseline";
    }

    private TubeProcessor createProcessor() {
        return input -> {
            // Choose strategy for this request
            String selectedStrategy = selectStrategy();
            Strategy strategy = strategies.get(selectedStrategy);

            try {
                // Measure performance
                long startTime = System.nanoTime();
                Object result = strategy.execute(input);
                long endTime = System.nanoTime();

                // Update performance record
                updatePerformanceRecord(
                    selectedStrategy,
                    true,
                    endTime - startTime
                );

                // Periodically evolve strategies
                if (random.nextInt(100) < 5) { // 5% chance
                    evolveStrategies();
                }

                return result;
            } catch (Exception e) {
                // Record failure
                updatePerformanceRecord(selectedStrategy, false, 0);

                // Fall back to baseline on failure
                if (!selectedStrategy.equals("baseline")) {
                    logger.warn("Strategy {} failed, falling back to baseline",
                             selectedStrategy);
                    return strategies.get("baseline").execute(input);
                } else {
                    throw e; // Propagate if baseline fails
                }
            }
        };
    }

    private String selectStrategy() {
        // Most of the time use the best strategy
        if (random.nextInt(100) < 80) { // 80% of requests
            return currentBestStrategy;
        }

        // Sometimes try other strategies to gather performance data
        List<String> otherStrategies = strategies.keySet().stream()
            .filter(s -> !s.equals(currentBestStrategy))
            .collect(Collectors.toList());

        return otherStrategies.get(random.nextInt(otherStrategies.size()));
    }

    private void updatePerformanceRecord(
            String strategyName,
            boolean success,
            long durationNanos) {
        PerformanceRecord record = strategyPerformance.getOrDefault(
            strategyName,
            new PerformanceRecord()
        );

        record.addExecution(success, durationNanos);
        strategyPerformance.put(strategyName, record);
    }

    private void evolveStrategies() {
        // Find the best performing strategy
        String bestStrategy = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (Map.Entry<String, PerformanceRecord> entry :
             strategyPerformance.entrySet()) {
            double score = entry.getValue().calculateScore();

            if (score > bestScore) {
                bestScore = score;
                bestStrategy = entry.getKey();
            }
        }

        // Update if different
        if (!bestStrategy.equals(currentBestStrategy)) {
            logger.info("Evolved strategy from {} to {}",
                      currentBestStrategy, bestStrategy);
            currentBestStrategy = bestStrategy;
        }

        // Generate new experimental strategies periodically
        if (random.nextInt(100) < 20) { // 20% chance during evolution
            generateNewExperimentalStrategy();
        }
    }

    private void generateNewExperimentalStrategy() {
        // Create a variation of the best strategy
        Strategy base = strategies.get(currentBestStrategy);
        Strategy variant = base.createVariant();

        String newName = "experimental-" + UUID.randomUUID().toString().substring(0, 8);
        registerStrategy(newName, variant);

        logger.info("Generated new experimental strategy: {}", newName);
    }

    // Strategy interface and implementations
    private interface Strategy {
        Object execute(Object input);
        Strategy createVariant();
    }

    // Performance tracking
    private static class PerformanceRecord {
        private int totalExecutions = 0;
        private int successfulExecutions = 0;
        private long totalDurationNanos = 0;

        public void addExecution(boolean success, long durationNanos) {
            totalExecutions++;

            if (success) {
                successfulExecutions++;
                totalDurationNanos += durationNanos;
            }
        }

        public double calculateScore() {
            // No executions yet
            if (totalExecutions == 0) {
                return 0.0;
            }

            // Calculate metrics
            double successRate = (double) successfulExecutions / totalExecutions;
            double avgDuration = successfulExecutions > 0 ?
                (double) totalDurationNanos / successfulExecutions :
                Double.MAX_VALUE;

            // Combine metrics (higher is better)
            return successRate * 100 - (avgDuration / 1_000_000.0);
        }
    }
}
```

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

### Leaky Tube

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
```

**Prevention:**
- Define clear input and output contracts
- Encapsulate implementation details
- Use immutable data transfer objects at interfaces
- Think of tubes as black boxes with well-defined ports

### Monolithic Tube

**Anti-Pattern:** A tube that tries to do too much, violating the single responsibility principle.

**Symptoms:**
- Tube has complex, branching logic
- Methods are long with many parameters
- Changes for different reasons
- Difficult to test and understand

**Remedy: Tube Decomposition**

```java
// Before: Monolithic tube
public class MonolithicOrderTube implements Tube {
    private TubeProcessor createProcessor() {
        return input -> {
            // Validate order
            // Calculate pricing
            // Check inventory
            // Process payment
            // Generate shipping label
            // Send confirmation email
            // Update analytics
            return result;
        };
    }
}

// After: Decomposed tubes
public class OrderValidatorTube implements Tube { ... }
public class PricingCalculatorTube implements Tube { ... }
public class InventoryCheckerTube implements Tube { ... }
public class PaymentProcessorTube implements Tube { ... }
public class ShippingLabelTube implements Tube { ... }
public class EmailNotificationTube implements Tube { ... }
public class AnalyticsUpdateTube implements Tube { ... }

// Compose using a pipeline or bundle
Pipeline<Order, OrderResult> orderPipeline = new Pipeline<>()
    .addStage(new OrderValidatorTube())
    .addStage(new PricingCalculatorTube())
    // Add other tubes
```

**Prevention:**
- Follow the Single Responsibility Principle
- Limit tube size and complexity
- Create specialized tubes for distinct functions
- Use composition to build complex flows

### Entangled Tubes

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
```

**Prevention:**
- Focus on transforms, not connections
- Use mediators for tube coordination
- Let higher-level structures manage flow
- Exchange data, not references
- Test tubes in isolation

### Stagnant Pool

**Anti-Pattern:** Tubes that don't adapt to changing conditions or learn from experience.

**Symptoms:**
- Configuration never changes
- Same errors occur repeatedly
- Performance doesn't improve over time
- Manual intervention required frequently

**Remedy: Adaptive Mechanisms**

```java
// Before: Stagnant tube
public class StagnantTube implements Tube {
    private final int batchSize = 100; // Fixed configuration
    private final int timeout = 1000;

    private TubeProcessor createProcessor() {
        return input -> {
            // Always process the same way regardless of results
            return processWithFixedParameters(input);
        };
    }
}

// After: Adaptive tube
public class AdaptiveTube implements Tube {
    private int batchSize = 100; // Starting value
    private int timeout = 1000;
    private final PerformanceTracker tracker = new PerformanceTracker();

    private TubeProcessor createProcessor() {
        return input -> {
            // Track performance
            long startTime = System.nanoTime();
            Object result = processWithParameters(input, batchSize, timeout);
            long endTime = System.nanoTime();

            // Record and adapt
            tracker.recordExecution(endTime - startTime);
            adaptParametersBasedOnPerformance();

            return result;
        };
    }

    private void adaptParametersBasedOnPerformance() {
        // Adjust batch size based on latency trend
        if (tracker.getAverageLatency() > 500 && batchSize > 10) {
            batchSize = (int)(batchSize * 0.8); // Reduce by 20%
            logger.info("Reduced batch size to {}", batchSize);
        } else if (tracker.getAverageLatency() < 100 &&
                  tracker.getErrorRate() < 0.01) {
            batchSize = (int)(batchSize * 1.2); // Increase by 20%
            logger.info("Increased batch size to {}", batchSize);
        }

        // Similar logic for timeout and other parameters
    }
}
```

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

[← Return to Core Concepts](./CoreConcepts.md) | [Explore Bundles and Machines →](./BundlesAndMachines.md)
