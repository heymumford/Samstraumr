<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Composition Strategies

## Table of Contents

- [Introduction](#introduction)
- [Foundational Principles](#foundational-principles)
- [Composite Patterns](#composite-patterns)
- [Flow Management](#flow-management)
- [State Management](#state-management)
- [Resilience Strategies](#resilience-strategies)
- [Evolutionary Compositions](#evolutionary-compositions)
- [Common Pitfalls](#common-pitfalls)
- [Advanced Topics](#advanced-topics)

## Introduction

Building complex systems from simple components is both an art and a science. In Samstraumr, composition is the mechanism by which individual tubes join together to create systems with emergent capabilities that exceed the sum of their parts.

## Foundational Principles

### Single Responsibility Principle

Each tube should have precisely one responsibility, enabling:
- Clear reasoning about component behavior
- Focused testing of individual responsibilities
- Easier maintenance and reusability

### Explicit Connections

Make all connections between tubes explicit and visible:

```java
// Create tubes
Tube validator = new OrderValidatorTube();
Tube inventory = new InventoryCheckTube();
Tube payment = new PaymentProcessorTube();

// Add tubes to composite
composite.addTube("validator", validator);
composite.addTube("inventory", inventory);
composite.addTube("payment", payment);

// Connect tubes explicitly
composite.connect("validator", "inventory");
composite.connect("inventory", "payment");
```

### Loose Coupling

- **Interface-Based Connections**: Connect through well-defined interfaces
- **Message-Based Communication**: Pass self-contained messages
- **Avoid Shared State**: Each tube manages its own state

### Hierarchical Organization

- **Individual Tubes**: Single-responsibility processing units
- **Composites**: Related tubes working together
- **Machines**: Orchestrated composites implementing subsystems
- **Systems**: Interconnected machines forming applications

## Composite Patterns

### Linear Pipeline

Connect tubes in a sequential processing chain:

```
[Input] → [Validator] → [Transformer] → [Enricher] → [Output]
```

**Best Used For:**
- Sequential transformations
- Data enrichment flows
- ETL operations

### Branching Pipeline

Split processing into multiple parallel paths:

```
                ┌→ [TransformerA] →┐
[Splitter] ──→ ├→ [TransformerB] →┼→ [Aggregator]
                └→ [TransformerC] →┘
```

**Best Used For:**
- Parallel processing
- Type-specific handling
- Performance optimization

### Feedback Loop

Include feedback mechanisms for iterative processing:

```
[Input] → [Processor] → [Evaluator] → [Output]
               ↑             |
               └─────────────┘
```

**Best Used For:**
- Iterative refinement
- Optimization algorithms
- Convergence calculations

### Observer Network

Distribute events to multiple interested components:

```
                ┌→ [Observer A]
[Subject] ─────→├→ [Observer B]
                └→ [Observer C]
```

**Best Used For:**
- Event distribution
- Monitoring systems
- Cross-cutting concerns

### Layered Architecture

Organize tubes into functional layers:

```
[Presentation Layer Composite]
          ↓
[Business Logic Layer Composite]
          ↓
[Data Access Layer Composite]
```

**Best Used For:**
- Complex systems
- Clear separation of concerns
- Reusable subsystems

## Flow Management

### Synchronous Flow

**Considerations:**
- Simplicity: Easier to understand and debug
- Consistency: Predictable processing order
- Throughput: May limit overall throughput

### Asynchronous Flow

**Considerations:**
- Responsiveness: Non-blocking operations
- Throughput: Higher throughput for I/O-bound operations
- Complexity: More complex error handling

### Backpressure Management

Control flow when downstream components can't keep up:

```java
public Object process(Object input) throws InterruptedException {
    semaphore.acquire();
    try {
        return composite.process(input);
    } finally {
        semaphore.release();
    }
}
```

### Priority-Based Routing

Route items based on priority or characteristics:

```java
String startTube;
if (input instanceof PriorityItem) {
    PriorityItem item = (PriorityItem) input;
    if (item.getPriority() == Priority.HIGH) {
        startTube = "highPriorityProcessor";
    } else {
        startTube = "standardProcessor";
    }
} else {
    startTube = "defaultProcessor";
}
```

## State Management

### Individual vs. Composite State

- Individual tubes manage their internal state
- Composites track state relevant to coordination
- Avoid duplicating state between levels

### State Propagation Patterns

- **Upward Propagation**: Tubes notify containing composite
- **Downward Propagation**: Composite influences constituent tubes
- **Lateral Propagation**: Changes affect sibling tubes
- **Conditional Propagation**: Only propagate under specific conditions

### Composite-Level Monitoring

Monitor health metrics across the composite:
- Health status derived from constituent tubes
- Performance metrics (throughput, latency, error rates)
- Resource utilization (memory, CPU, connections)
- Flow patterns and state distribution

## Resilience Strategies

### Redundancy Patterns

```java
public Object process(Object input) {
    try {
        // Try primary tube first
        return primaryTube.process(input);
    } catch (Exception e) {
        // Fall back to backup tube
        return backupTube.process(input);
    }
}
```

**Redundancy Strategies:**
- **Active-Passive**: Backup takes over when primary fails
- **Active-Active**: Multiple tubes process simultaneously
- **Diversified Redundancy**: Different implementations for same function

### Circuit Breaker Pattern

Protect services from cascading failures:

```java
private Tube protect(Tube tube, CircuitBreaker breaker) {
    return input -> {
        if (!breaker.isAllowed()) {
            throw new CircuitOpenException("Circuit open");
        }
        
        try {
            Object result = tube.process(input);
            breaker.recordSuccess();
            return result;
        } catch (Exception e) {
            breaker.recordFailure();
            throw e;
        }
    };
}
```

### Bulkhead Pattern

Isolate components to contain failures:

```java
private Tube isolate(Tube tube, Semaphore bulkhead) {
    return input -> {
        if (!bulkhead.tryAcquire()) {
            throw new BulkheadFullException("No capacity available");
        }
        
        try {
            return tube.process(input);
        } finally {
            bulkhead.release();
        }
    };
}
```

### Graceful Degradation

Maintain core functionality when resources are constrained:

```java
switch (currentMode) {
    case FULL:
        return processWithFullFunctionality(input);
    case REDUCED:
        return processWithReducedFunctionality(input);
    case MINIMAL:
        return processWithMinimalFunctionality(input);
    case CRITICAL_ONLY:
        if (isCriticalOperation(input)) {
            return processCriticalOnly(input);
        } else {
            return new TemporaryUnavailableResponse();
        }
}
```

## Evolutionary Compositions

### Versioned Tubes

Support multiple versions of tubes in a composition:

```java
// Register different versions
versionRouter.registerVersion("v1", new ProcessorTubeV1());
versionRouter.registerVersion("v2", new ProcessorTubeV2());

// Set active version
versionRouter.setActiveVersion("v2");
```

**Versioning Strategies:**
- Multiple active versions simultaneously
- Gradual traffic transition to new versions
- A/B testing of different versions

### Pluggable Components

Create compositions that allow plugging in different implementations:

```java
// Find all implementations
ServiceLoader<ProcessorPlugin> loader = ServiceLoader.load(ProcessorPlugin.class);

// Register plugins
for (ProcessorPlugin plugin : loader) {
    pluginRegistry.registerPlugin("processor", plugin);
}
```

### Adaptive Composition

Create compositions that adapt their structure based on conditions:

```java
private void analyzeAndAdapt() {
    // Collect metrics
    CompositeMetrics metrics = collectMetrics();
    
    // Analyze performance
    List<AdaptationAction> actions = analyzePerformance(metrics);
    
    // Apply adaptations
    for (AdaptationAction action : actions) {
        applyAdaptation(action);
    }
}
```

## Common Pitfalls

### Excessively Complex Composites

**Solution:** Hierarchical organization
- Limit composites to 7-10 tubes (cognitive limit)
- Group related tubes into sub-composites
- Create clear boundaries around functionality

### Hidden Dependencies

**Solution:** Explicit connections
- Make all connections explicit in the composite
- Avoid shared mutable state between tubes
- Pass all dependencies as messages

### Improper State Handling

**Solution:** Consistent state patterns
- Use the standard dual-state model consistently
- Make state transitions explicit
- Report state changes to listeners
- Provide clear error responses

### Overcomplex Flows

**Solution:** Simplified flows
- Prefer linear flows where possible
- Group complex routing into dedicated composites
- Use visual diagrams to illustrate flows
- Limit nesting to 2-3 levels

## Advanced Topics

### Composition Metrics and Analytics

Collect metrics at all levels:
- Individual tube metrics (processing time, error rates)
- Composite-level metrics (state distribution, throughput)
- End-to-end metrics (total processing time, success rates)

### Distributed Compositions

Design compositions that span multiple systems:
- Remote connector tubes using HTTP/REST
- Service discovery and registration
- Distributed error handling and recovery
- Cross-system monitoring

### Dynamic Recomposition

Create compositions that restructure at runtime:
- Monitor system performance and load
- Select optimal composition patterns
- Apply structural changes while preserving state
- Use pattern-based restructuring