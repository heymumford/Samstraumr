<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Component Patterns

## Table of Contents

- [Introduction](#introduction)
- [Foundational Patterns](#foundational-patterns)
- [Flow Control Patterns](#flow-control-patterns)
- [State Management Patterns](#state-management-patterns)
- [Composition Patterns](#composition-patterns)
- [Adaptation Patterns](#adaptation-patterns)
- [Anti-Patterns](#anti-patterns)
- [Applying Patterns](#applying-patterns)

## Introduction

S8r component patterns speak the language of flow—describing how data, control, and state move through interconnected components to create resilient, adaptive systems. Each pattern represents a tested solution to a common design challenge.

## Foundational Patterns

### Validator-transformer-persister

**Context:** Processing input data requires validation, transformation, and persistence.

**Implementation:**

```java
// Validator Component
public class CustomerDataValidatorComponent implements Component {
    private ComponentProcessor createProcessor() {
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

// Transformer Component
public class CustomerEntityTransformerComponent implements Component {
    private ComponentProcessor createProcessor() {
        return input -> {
            ValidationResult result = (ValidationResult) input;
            if (!result.isValid()) {
                return TransformationResult.failed(result.getErrors());
            }

            CustomerEntity entity = mapToEntity((CustomerData)result.getValue());
            return TransformationResult.success(entity);
        };
    }
}
```

### Observer

**Context:** Monitor data flow without altering it.

**Implementation:**

```java
public class MetricsObserverComponent implements Component {
    private final MetricsService metricsService;

    private ComponentProcessor createProcessor() {
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
```

### Circuit breaker

**Context:** Prevent cascading failures when calling potentially failing services.

**Implementation:**

```java
public class CircuitBreakerComponent implements Component {
    private State state = State.CLOSED;
    private int failureCount = 0;
    private final int threshold = 5;
    private Instant lastStateChange = Instant.now();
    private final Duration resetTimeout = Duration.ofMinutes(1);
    
    private ComponentProcessor createProcessor() {
        return input -> {
            if (state == State.OPEN) {
                if (Duration.between(lastStateChange, Instant.now()).compareTo(resetTimeout) > 0) {
                    // Try half-open
                    state = State.HALF_OPEN;
                    lastStateChange = Instant.now();
                } else {
                    throw new CircuitBreakerOpenException("Circuit breaker is open");
                }
            }
            
            try {
                Object result = callProtectedService(input);
                
                // Success - reset failure count
                failureCount = 0;
                if (state == State.HALF_OPEN) {
                    state = State.CLOSED;
                    lastStateChange = Instant.now();
                }
                
                return result;
            } catch (Exception e) {
                failureCount++;
                
                if (state == State.HALF_OPEN || failureCount >= threshold) {
                    state = State.OPEN;
                    lastStateChange = Instant.now();
                }
                
                throw e;
            }
        };
    }
    
    private enum State {
        CLOSED, OPEN, HALF_OPEN
    }
}
```

## Flow Control Patterns

### Splitter-aggregator

**Context:** Divide work for parallel processing and recombine results.

**Implementation:**

```java
// Splitter
public class WorkSplitterComponent implements Component {
    private ComponentProcessor createProcessor() {
        return input -> {
            LargeWorkload workload = (LargeWorkload) input;
            List<Task> tasks = splitIntoTasks(workload);
            return new SplitResult(tasks, UUID.randomUUID());
        };
    }
}

// Aggregator
public class ResultAggregatorComponent implements Component {
    private final Map<UUID, List<TaskResult>> partialResults = new ConcurrentHashMap<>();

    private ComponentProcessor createProcessor() {
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
                CompositeResult finalResult = aggregateResults(results);
                partialResults.remove(correlationId);
                return finalResult;
            } else {
                return new WaitingResult(correlationId, results.size(), context.getTotalTasks());
            }
        };
    }
}
```

### Router

**Context:** Direct input to different processors based on content or context.

**Implementation:**

```java
public class ContentBasedRouter implements Component {
    private final Map<String, Component> routes = new HashMap<>();
    
    public void addRoute(String routeType, Component destination) {
        routes.put(routeType, destination);
    }
    
    private ComponentProcessor createProcessor() {
        return input -> {
            // Determine route based on content
            String routeType = determineRouteType(input);
            
            Component destination = routes.get(routeType);
            if (destination == null) {
                destination = routes.get("default");
                if (destination == null) {
                    throw new UnknownRouteException("No route for type: " + routeType);
                }
            }
            
            return destination.process(input);
        };
    }
}
```

### Load balancer

**Context:** Distribute workload across multiple instances for scalability.

**Implementation:**

```java
public class LoadBalancerComponent implements Component {
    private final List<Component> workers = new ArrayList<>();
    private int currentIndex = 0;
    private final Object indexLock = new Object();
    
    private ComponentProcessor createProcessor() {
        return input -> {
            // Select the next worker using round-robin
            Component selectedWorker;
            synchronized (indexLock) {
                selectedWorker = workers.get(currentIndex);
                currentIndex = (currentIndex + 1) % workers.size();
            }
            
            // Delegate processing
            try {
                return selectedWorker.process(input);
            } catch (Exception e) {
                // If worker fails, try another
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

### Throttle

**Context:** Limit processing rate to protect downstream systems.

**Implementation:**

```java
public class ThrottleComponent implements Component {
    private final RateLimiter rateLimiter;
    private final Component target;
    
    public ThrottleComponent(Component target, double ratePerSecond) {
        this.target = target;
        this.rateLimiter = RateLimiter.create(ratePerSecond);
    }
    
    private ComponentProcessor createProcessor() {
        return input -> {
            // Wait for permission
            rateLimiter.acquire();
            
            // Process when allowed
            return target.process(input);
        };
    }
}
```

## State Management Patterns

### State machine

**Context:** Manage complex state transitions clearly.

**Implementation:**

```java
public class OrderStateMachineComponent implements Component {
    private enum OrderState {
        CREATED, PAID, SHIPPED, DELIVERED, CANCELLED
    }

    private final Map<OrderState, Set<OrderState>> validTransitions = new HashMap<>();
    
    public OrderStateMachineComponent() {
        // Define valid transitions
        validTransitions.put(OrderState.CREATED, 
            Set.of(OrderState.PAID, OrderState.CANCELLED));
        validTransitions.put(OrderState.PAID,
            Set.of(OrderState.SHIPPED, OrderState.CANCELLED));
        validTransitions.put(OrderState.SHIPPED,
            Set.of(OrderState.DELIVERED, OrderState.CANCELLED));
        validTransitions.put(OrderState.DELIVERED, Set.of());
        validTransitions.put(OrderState.CANCELLED, Set.of());
    }

    private ComponentProcessor createProcessor() {
        return input -> {
            StateTransitionRequest request = (StateTransitionRequest) input;
            OrderState currentState = OrderState.valueOf(request.getOrder().getStatus());
            OrderState targetState = request.getTargetState();
            
            // Validate transition
            if (!validTransitions.get(currentState).contains(targetState)) {
                return new StateTransitionResult(
                    false, 
                    "Invalid transition from " + currentState + " to " + targetState,
                    request.getOrder()
                );
            }
            
            // Perform transition
            try {
                performTransitionActions(request.getOrder(), currentState, targetState);
                request.getOrder().setStatus(targetState.name());
                return new StateTransitionResult(true, null, request.getOrder());
            } catch (Exception e) {
                return new StateTransitionResult(
                    false,
                    "Error during transition: " + e.getMessage(),
                    request.getOrder()
                );
            }
        };
    }
}
```

### Compensating transaction

**Context:** Undo the effects of a completed operation in a distributed system.

**Implementation:**

```java
public class CompensatingTransactionComponent implements Component {
    private final Map<String, List<CompensatingAction>> transactionLog = new ConcurrentHashMap<>();

    private ComponentProcessor createProcessor() {
        return input -> {
            TransactionRequest request = (TransactionRequest) input;
            String transactionId = request.getTransactionId();
            
            // Create transaction record
            transactionLog.putIfAbsent(transactionId, new ArrayList<>());
            
            try {
                // Perform action
                Object result = performAction(request.getAction());
                
                // Record compensating action
                CompensatingAction compensatingAction = 
                    createCompensatingAction(request.getAction(), result);
                transactionLog.get(transactionId).add(compensatingAction);
                
                return new TransactionResult(true, result, null);
            } catch (Exception e) {
                // Compensate for any completed actions
                compensateTransaction(transactionId);
                return new TransactionResult(false, null, e.getMessage());
            }
        };
    }
    
    public void compensateTransaction(String transactionId) {
        List<CompensatingAction> actions = transactionLog.get(transactionId);
        
        // Apply compensating actions in reverse order
        for (int i = actions.size() - 1; i >= 0; i--) {
            actions.get(i).compensate();
        }
        
        // Clear the log
        transactionLog.remove(transactionId);
    }
}
```

## Composition Patterns

### Pipeline

**Context:** Sequence processing steps with clear inputs/outputs.

**Implementation:**

```java
public class Pipeline<I, O> {
    private final List<Component> stages = new ArrayList<>();

    public Pipeline<I, O> addStage(Component component) {
        stages.add(component);
        return this;
    }

    public O process(I input) {
        Object current = input;
        for (Component stage : stages) {
            current = stage.process(current);
        }
        return (O) current;
    }
}

// Usage
Pipeline<RawOrder, ShippingLabel> orderPipeline = new Pipeline<>()
    .addStage(new OrderValidatorComponent())
    .addStage(new PaymentProcessorComponent())
    .addStage(new ShippingLabelGeneratorComponent());
```

### Decorator

**Context:** Add responsibilities to components dynamically.

**Implementation:**

```java
// Base decorator
public abstract class ComponentDecorator implements Component {
    protected final Component decoratedComponent;

    public ComponentDecorator(Component decoratedComponent) {
        this.decoratedComponent = decoratedComponent;
    }

    // Forward identity, state methods
    @Override
    public Identity getIdentity() {
        return decoratedComponent.getIdentity();
    }
}

// Concrete decorator
public class LoggingDecorator extends ComponentDecorator {
    public LoggingDecorator(Component decoratedComponent) {
        super(decoratedComponent);
    }

    @Override
    public Object process(Object input) {
        logger.info("Before processing: {}", input);
        Object result = decoratedComponent.process(input);
        logger.info("After processing: {}", result);
        return result;
    }
}

// Usage
Component component = new LoggingDecorator(
    new CachingDecorator(new DataProcessorComponent()));
```

## Adaptation Patterns

### Self-healing

**Context:** Detect and recover from failures automatically.

**Implementation:**

```java
public class SelfHealingComponent implements Component {
    private final Component coreComponent;
    private int consecutiveFailures = 0;
    private final int healingThreshold = 3;
    
    private ComponentProcessor createProcessor() {
        return input -> {
            try {
                // Check if recovery needed
                if (coreComponent.getState() == State.DEGRADED && 
                    consecutiveFailures >= healingThreshold) {
                    attemptRecovery();
                }
                
                // Process if active
                if (coreComponent.getState() == State.ACTIVE) {
                    Object result = coreComponent.process(input);
                    consecutiveFailures = 0; // Reset on success
                    return result;
                } else {
                    // Still degraded
                    consecutiveFailures++;
                    return new ErrorResult("Component degraded");
                }
            } catch (Exception e) {
                consecutiveFailures++;
                return new ErrorResult(e.getMessage());
            }
        };
    }
    
    private void attemptRecovery() {
        // Try recovery strategies in sequence
        boolean recovered = resetInternalState() || 
                           reconnectDependencies() || 
                           restartComponent();
        
        if (recovered) {
            consecutiveFailures = 0;
            logger.info("Self-healing successful");
        }
    }
}
```

### Degraded mode

**Context:** Continue with reduced functionality when resources are constrained.

**Implementation:**

```java
public class DegradedModeComponent implements Component {
    private OperationMode currentMode = OperationMode.FULL;
    
    private enum OperationMode {
        FULL,     // All features
        REDUCED,  // Non-essential features disabled
        MINIMAL,  // Only critical functions
        OFFLINE   // No processing
    }
    
    private ComponentProcessor createProcessor() {
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
                    return new DegradedResult(null, "System offline");
                default:
                    return new DegradedResult(null, "Unknown mode");
            }
        };
    }
    
    private void assessHealthAndUpdateMode() {
        DependencyStatus dependencies = checkDependencies();
        ResourceStatus resources = checkResources();
        
        // Update mode based on health assessment
        OperationMode newMode = determineOperationMode(dependencies, resources);
        if (newMode != currentMode) {
            currentMode = newMode;
            setState(newMode == OperationMode.FULL ? State.ACTIVE : State.DEGRADED);
        }
    }
}
```

## Anti-Patterns

### Leaky component

**Anti-Pattern:** Implementation details leak through the interface.

**Remedy:** Interface Isolation

```java
// Before: Leaky
public class LeakyComponent implements Component {
    public static final int MAX_BATCH_SIZE = 100;
    public InternalState getInternalState() { ... }
}

// After: Clean interface
public class CleanComponent implements Component {
    private static final int MAX_BATCH_SIZE = 100;
    
    @Override
    public Object process(Object input) {
        // No implementation details exposed
        return result;
    }
}
```

### Entangled components

**Anti-Pattern:** Tightly coupled components directly calling each other.

**Remedy:** Flow-Based Communication

```java
// Before: Entangled
public class EntangledComponentA implements Component {
    private final EntangledComponentB componentB;
    private final EntangledComponentC componentC;
    
    private ComponentProcessor createProcessor() {
        return input -> {
            Object resultB = componentB.process(input);
            Object resultC = componentC.process(resultB);
            return resultC;
        };
    }
}

// After: Flow-based
public class DecoupledComponentA implements Component {
    private ComponentProcessor createProcessor() {
        return input -> transformInput(input);
    }
}

// External flow coordination
Pipeline<Object, Object> pipeline = new Pipeline<>()
    .addStage(new DecoupledComponentA())
    .addStage(new DecoupledComponentB())
    .addStage(new DecoupledComponentC());
```

## Applying Patterns

1. **Start with Foundational Patterns**: Begin with simple patterns before complex ones.

2. **Combine Patterns**: Address different concerns with pattern combinations:
   - Observer for monitoring
   - Circuit Breaker for resilience
   - Throttle for resource management

3. **Let Requirements Drive Selection**:
   - For high availability: Circuit Breaker, Degraded Mode, Self-Healing
   - For performance: Load Balancer, Dynamic Reconfiguration
   - For maintainability: Composite, Decorator, Pipeline

4. **Watch for Anti-Patterns**: Review for Leaky Components and other issues.

5. **Evolve Pattern Usage**: Adapt patterns as your system matures:
   - Early stage: Simple patterns focusing on functionality
   - Growth stage: Add resilience and scalability patterns
   - Mature stage: Incorporate adaptation patterns

Remember that patterns are tools, not rules. Apply them thoughtfully and adapt them to your specific needs.

