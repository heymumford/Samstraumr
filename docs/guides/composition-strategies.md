# Composition Strategies: Building Complex Systems from Simple Tubes

## Table of Contents
- [Introduction: The Art of Composition](#introduction-the-art-of-composition)
- [Foundational Principles](#foundational-principles)
- [Composite Patterns](#composite-patterns)
- [Flow Management Strategies](#flow-management-strategies)
- [State Management in Compositions](#state-management-in-compositions)
- [Resilience Through Composition](#resilience-through-composition)
- [Evolutionary Compositions](#evolutionary-compositions)
- [Practical Examples](#practical-examples)
- [Common Pitfalls and Solutions](#common-pitfalls-and-solutions)
- [Advanced Topics](#advanced-topics)

## Introduction: The Art of Composition

Building complex systems from simple components is both an art and a science. In Samstraumr, composition is the mechanism by which individual tubes—each with their own focused responsibility—join together to create systems with emergent capabilities that exceed the sum of their parts.

This guide explores strategies for effective composition, helping you create systems that are not only functional but also maintainable, extensible, and resilient. Like a master composer arranging musical notes into a symphony, you'll learn to arrange tubes into harmonious flows that respond intelligently to their environment.

## Foundational Principles

### Single Responsibility Principle

Each tube should have precisely one responsibility—one reason to exist and change. This enables:

- **Clear Reasoning**: Understanding what each component does
- **Focused Testing**: Testing each responsibility in isolation
- **Easier Maintenance**: Changing one aspect without affecting others
- **Reusability**: Combining tubes in different contexts

### Explicit Connections

Make all connections between tubes explicit and visible:

```java
public class OrderProcessingComposite {
    public OrderProcessingComposite() {
        // Create tubes
        Tube validator = new OrderValidatorTube();
        Tube inventory = new InventoryCheckTube();
        Tube payment = new PaymentProcessorTube();
        Tube fulfillment = new OrderFulfillmentTube();
        Tube notification = new CustomerNotificationTube();
        
        // Add tubes to composite
        composite.addTube("validator", validator);
        composite.addTube("inventory", inventory);
        composite.addTube("payment", payment);
        composite.addTube("fulfillment", fulfillment);
        composite.addTube("notification", notification);
        
        // Connect tubes explicitly
        composite.connect("validator", "inventory");
        composite.connect("inventory", "payment");
        composite.connect("payment", "fulfillment");
        composite.connect("fulfillment", "notification");
    }
}
```

### Loose Coupling

Keep tubes independent of implementation details in other tubes:

- **Interface-Based Connections**: Connect through well-defined interfaces
- **Message-Based Communication**: Pass self-contained messages
- **Avoid Shared State**: Each tube manages its own state
- **Configuration Injection**: External configuration over hardcoded dependencies

### Hierarchical Organization

Organize tubes in a meaningful hierarchy:

1. **Individual Tubes**: Single-responsibility processing units
2. **Composites**: Related tubes working together
3. **Machines**: Orchestrated composites implementing subsystems
4. **Systems**: Interconnected machines forming complete applications

## Composite Patterns

### Linear Pipeline

Connect tubes in a sequential processing chain:

```
[Input] → [Validator] → [Transformer] → [Enricher] → [Persister] → [Output]
```

**Implementation:**

```java
public class LinearPipelineComposite {
    private final Composite composite = new Composite("OrderPipeline");
    
    public LinearPipelineComposite() {
        // Create tubes
        Tube validator = new DataValidatorTube();
        Tube transformer = new DataTransformerTube();
        Tube enricher = new DataEnricherTube();
        Tube persister = new DataPersisterTube();
        
        // Add tubes to composite
        composite.addTube("validator", validator);
        composite.addTube("transformer", transformer);
        composite.addTube("enricher", enricher);
        composite.addTube("persister", persister);
        
        // Connect in sequence
        composite.connect("validator", "transformer");
        composite.connect("transformer", "enricher");
        composite.connect("enricher", "persister");
    }
    
    public Object process(Object input) {
        // Start processing at the first tube in pipeline
        return composite.process("validator", input);
    }
}
```

**Best Used For:**
- Sequential transformations
- Data enrichment flows
- Processing with clear stages
- ETL operations

### Branching Pipeline

Split processing into multiple parallel paths:

```
                ┌→ [TransformerA] →┐
[Splitter] ──→ ├→ [TransformerB] →┼→ [Aggregator]
                └→ [TransformerC] →┘
```

**Implementation:**

```java
public class BranchingPipelineComposite {
    private final Composite composite = new Composite("BranchingPipeline");
    
    public BranchingPipelineComposite() {
        // Create tubes
        Tube splitter = new DataSplitterTube();
        Tube transformerA = new TypeATransformerTube();
        Tube transformerB = new TypeBTransformerTube();
        Tube transformerC = new TypeCTransformerTube();
        Tube aggregator = new ResultAggregatorTube();
        
        // Add tubes to composite
        composite.addTube("splitter", splitter);
        composite.addTube("transformerA", transformerA);
        composite.addTube("transformerB", transformerB);
        composite.addTube("transformerC", transformerC);
        composite.addTube("aggregator", aggregator);
        
        // Connect branches
        composite.connect("splitter", "transformerA");
        composite.connect("splitter", "transformerB");
        composite.connect("splitter", "transformerC");
        composite.connect("transformerA", "aggregator");
        composite.connect("transformerB", "aggregator");
        composite.connect("transformerC", "aggregator");
    }
    
    public Object process(Object input) {
        // Start processing at the splitter
        return composite.process("splitter", input);
    }
}
```

**Best Used For:**
- Parallel processing
- Type-specific handling
- Different processing requirements
- Performance optimization

### Feedback Loop

Include feedback mechanisms for iterative processing:

```
[Input] → [Processor] → [Evaluator] → [Output]
               ↑             |
               └─────────────┘
```

**Implementation:**

```java
public class FeedbackLoopComposite {
    private final Composite composite = new Composite("FeedbackLoop");
    
    public FeedbackLoopComposite() {
        // Create tubes
        Tube processor = new DataProcessorTube();
        Tube evaluator = new ResultEvaluatorTube();
        
        // Add tubes to composite
        composite.addTube("processor", processor);
        composite.addTube("evaluator", evaluator);
        
        // Connect with feedback
        composite.connect("processor", "evaluator");
        composite.connect("evaluator", "processor");
    }
    
    public Object process(Object input) {
        // Handle the feedback loop
        Object result = composite.process("processor", input);
        
        // Extract final result
        return ((LoopResult)result).getFinalOutput();
    }
}
```

**Best Used For:**
- Iterative refinement
- Optimization algorithms
- Machine learning processes
- Convergence calculations

### Observer Network

Distribute events to multiple interested components:

```
                ┌→ [Observer A]
[Subject] ─────→├→ [Observer B]
                └→ [Observer C]
```

**Implementation:**

```java
public class ObserverNetworkComposite {
    private final Composite composite = new Composite("ObserverNetwork");
    
    public ObserverNetworkComposite() {
        // Create tubes
        Tube subject = new EventSourceTube();
        Tube observerA = new MetricsObserverTube();
        Tube observerB = new LoggingObserverTube();
        Tube observerC = new NotificationObserverTube();
        
        // Add tubes to composite
        composite.addTube("subject", subject);
        composite.addTube("metrics", observerA);
        composite.addTube("logging", observerB);
        composite.addTube("notification", observerC);
        
        // Connect subject to observers
        composite.connect("subject", "metrics");
        composite.connect("subject", "logging");
        composite.connect("subject", "notification");
    }
    
    public void publishEvent(Event event) {
        composite.process("subject", event);
    }
}
```

**Best Used For:**
- Event distribution
- Monitoring systems
- Cross-cutting concerns
- Decoupled notification

### Layered Architecture

Organize tubes into functional layers:

```
[Presentation Layer Composite]
          ↓
[Business Logic Layer Composite]
          ↓
[Data Access Layer Composite]
```

**Implementation:**

```java
public class LayeredSystemBuilder {
    public Machine buildLayeredSystem() {
        // Build data access layer
        Composite dataLayer = buildDataAccessLayer();
        
        // Build business logic layer
        Composite businessLayer = buildBusinessLogicLayer(dataLayer);
        
        // Build presentation layer
        Composite presentationLayer = buildPresentationLayer(businessLayer);
        
        // Create machine that coordinates all layers
        Machine system = new Machine("LayeredSystem");
        system.addComposite("data", dataLayer);
        system.addComposite("business", businessLayer);
        system.addComposite("presentation", presentationLayer);
        
        return system;
    }
    
    private Composite buildDataAccessLayer() {
        Composite dataLayer = new Composite("DataLayer");
        // Add data access tubes
        return dataLayer;
    }
    
    private Composite buildBusinessLogicLayer(Composite dataLayer) {
        Composite businessLayer = new Composite("BusinessLayer");
        // Add business logic tubes
        // Connect to data layer
        return businessLayer;
    }
    
    private Composite buildPresentationLayer(Composite businessLayer) {
        Composite presentationLayer = new Composite("PresentationLayer");
        // Add presentation tubes
        // Connect to business layer
        return presentationLayer;
    }
}
```

**Best Used For:**
- Complex systems
- Clear separation of concerns
- Organized dependencies
- Reusable subsystems

## Flow Management Strategies

### Synchronous Flow

Process each input completely before handling the next:

```java
public class SynchronousComposite {
    private final Composite composite;
    
    public Object process(Object input) {
        // Each process call completes fully before returning
        return composite.process(input);
    }
}
```

**Considerations:**
- **Simplicity**: Easier to understand and debug
- **Consistency**: Predictable processing order
- **Throughput**: May limit overall throughput
- **Resource Utilization**: May not utilize resources optimally

### Asynchronous Flow

Decouple processing stages with asynchronous execution:

```java
public class AsynchronousComposite {
    private final Composite composite;
    private final ExecutorService executor;
    
    public CompletableFuture<Object> process(Object input) {
        return CompletableFuture.supplyAsync(
            () -> composite.process(input),
            executor
        );
    }
}
```

**Considerations:**
- **Responsiveness**: Non-blocking operations
- **Throughput**: Higher throughput for I/O-bound operations
- **Complexity**: More complex error handling and state management
- **Resource Efficiency**: Better utilization of resources

### Backpressure Management

Control flow when downstream components can't keep up:

```java
public class BackpressureComposite {
    private final Composite composite;
    private final Semaphore semaphore;
    
    public BackpressureComposite(int maxConcurrent) {
        this.semaphore = new Semaphore(maxConcurrent);
    }
    
    public Object process(Object input) throws InterruptedException {
        semaphore.acquire();
        try {
            return composite.process(input);
        } finally {
            semaphore.release();
        }
    }
}
```

**Considerations:**
- **System Protection**: Prevents overwhelming downstream components
- **Resource Management**: Controls resource consumption
- **Stability**: Maintains system stability under load
- **Predictability**: More predictable performance

### Priority-Based Routing

Route items based on priority or characteristics:

```java
public class PriorityRoutingComposite {
    private final Composite composite;
    
    public Object process(Object input) {
        // Determine processing path based on priority
        String startTube;
        if (input instanceof PriorityItem) {
            PriorityItem item = (PriorityItem) input;
            if (item.getPriority() == Priority.HIGH) {
                startTube = "highPriorityProcessor";
            } else if (item.getPriority() == Priority.MEDIUM) {
                startTube = "mediumPriorityProcessor";
            } else {
                startTube = "lowPriorityProcessor";
            }
        } else {
            startTube = "defaultProcessor";
        }
        
        return composite.process(startTube, input);
    }
}
```

**Considerations:**
- **Service Differentiation**: Different handling for different priorities
- **Resource Allocation**: Appropriate resource allocation
- **Fairness**: Ensures high-priority items get processed promptly
- **Quality of Service**: Maintains SLAs for different classes of work

## State Management in Compositions

### Individual vs. Composite State

Balance between individual tube state and composite-level state:

```java
public class CompositeStateExample {
    public void demonstrateStateManagement() {
        // Individual tube manages its own state
        Tube processorTube = new ProcessorTube();
        // Get individual tube state
        TubeState tubeState = processorTube.getDesignState();
        DynamicState tubeDynamicState = processorTube.getDynamicState();
        
        // Composite may track consolidated state
        Composite composite = new Composite("OrderProcessor");
        composite.addTube("processor", processorTube);
        
        // Get composite-level state
        CompositeState compositeState = composite.getState();
        
        // Composite state may be derived from constituent tubes
        boolean allTubesFlowing = composite.getTubes().stream()
            .allMatch(tube -> tube.getDesignState() == TubeState.FLOWING);
        
        // Or it may have its own independent state
        composite.setCompositeProperty("processingMode", "batch");
    }
}
```

**Guidelines:**
- Individual tubes should manage their internal state
- Composites should track state relevant to coordination
- Avoid duplicating state between levels
- Provide mechanisms for propagating state changes

### State Propagation

Propagate state changes appropriately through the composition:

```java
public class StatePropagationExample {
    public void demonstrateStatePropagation() {
        Composite composite = new Composite("PaymentProcessor");
        
        // Register state change listener
        composite.addStateChangeListener((tube, oldState, newState) -> {
            // When a constituent tube changes state
            if (newState == TubeState.ERROR) {
                // Take composite-level action
                handleTubeError(tube);
            }
        });
        
        // Propagate state changes upward
        if (areAllTubesBlocked(composite)) {
            // Change composite state
            composite.setDesignState(TubeState.BLOCKED);
            
            // Potentially propagate further up the hierarchy
            notifyParentMachine(composite);
        }
    }
    
    private void handleTubeError(Tube tube) {
        // Implement error handling strategy
    }
    
    private boolean areAllTubesBlocked(Composite composite) {
        return composite.getTubes().stream()
            .allMatch(tube -> tube.getDesignState() == TubeState.BLOCKED);
    }
    
    private void notifyParentMachine(Composite composite) {
        // Notify parent machine of composite state change
    }
}
```

**Propagation Patterns:**
- **Upward Propagation**: Tubes notify containing composite
- **Downward Propagation**: Composite influences constituent tubes
- **Lateral Propagation**: Changes affect sibling tubes
- **Conditional Propagation**: Only propagate under specific conditions

### Composite-Level Monitoring

Implement monitoring at the composite level:

```java
public class CompositeMonitoring {
    private final Composite composite;
    private final ScheduledExecutorService scheduler;
    
    public CompositeMonitoring(Composite composite) {
        this.composite = composite;
        this.scheduler = Executors.newScheduledThreadPool(1);
        
        // Start periodic health check
        scheduler.scheduleAtFixedRate(
            this::checkCompositeHealth,
            0, 30, TimeUnit.SECONDS
        );
    }
    
    private void checkCompositeHealth() {
        try {
            // Check design state of all tubes
            Map<String, TubeState> tubeStates = new HashMap<>();
            for (Map.Entry<String, Tube> entry : composite.getTubes().entrySet()) {
                tubeStates.put(entry.getKey(), entry.getValue().getDesignState());
            }
            
            // Check for error tubes
            List<String> errorTubes = tubeStates.entrySet().stream()
                .filter(entry -> entry.getValue() == TubeState.ERROR)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
            
            if (!errorTubes.isEmpty()) {
                // Attempt recovery
                for (String tubeId : errorTubes) {
                    attemptTubeRecovery(tubeId);
                }
            }
            
            // Check resource usage
            Map<String, Map<String, Object>> resourceUsage = collectResourceUsage();
            if (isResourceUsageExcessive(resourceUsage)) {
                implementResourceOptimization();
            }
            
            // Log consolidated state
            logCompositeHealth(tubeStates, resourceUsage);
            
        } catch (Exception e) {
            logger.error("Error during composite health check", e);
        }
    }
    
    private void attemptTubeRecovery(String tubeId) {
        // Attempt to recover the tube
    }
    
    private Map<String, Map<String, Object>> collectResourceUsage() {
        // Collect resource usage from all tubes
        return new HashMap<>();
    }
    
    private boolean isResourceUsageExcessive(Map<String, Map<String, Object>> resourceUsage) {
        // Determine if resource usage is excessive
        return false;
    }
    
    private void implementResourceOptimization() {
        // Implement resource optimization strategies
    }
    
    private void logCompositeHealth(
            Map<String, TubeState> tubeStates,
            Map<String, Map<String, Object>> resourceUsage) {
        // Log consolidated health information
    }
}
```

**Monitoring Aspects:**
- **Health Status**: Overall health derived from constituent tubes
- **Performance Metrics**: Throughput, latency, error rates
- **Resource Utilization**: Memory, CPU, connections
- **Flow Patterns**: Input rates, processing patterns
- **State Distribution**: Distribution of tubes across states

## Resilience Through Composition

### Redundancy Patterns

Implement redundancy to improve reliability:

```java
public class RedundantComposite {
    private final Tube primaryTube;
    private final Tube backupTube;
    
    public Object process(Object input) {
        try {
            // Try primary tube first
            return primaryTube.process(input);
        } catch (Exception e) {
            logger.warn("Primary tube failed, using backup", e);
            
            // Fall back to backup tube
            return backupTube.process(input);
        }
    }
}
```

**Redundancy Strategies:**
- **Active-Passive**: Backup takes over when primary fails
- **Active-Active**: Multiple tubes process simultaneously
- **N+1 Redundancy**: One more tube than minimum required
- **Diversified Redundancy**: Different implementations for same function

### Circuit Breaker Composition

Protect services with circuit breakers:

```java
public class CircuitBreakerComposite {
    private final Composite composite;
    private final Map<String, CircuitBreaker> circuitBreakers = new HashMap<>();
    
    public CircuitBreakerComposite() {
        // Create tubes
        Tube userService = new UserServiceTube();
        Tube orderService = new OrderServiceTube();
        Tube paymentService = new PaymentServiceTube();
        
        // Create circuit breakers for each service
        circuitBreakers.put("user", new CircuitBreaker("user", 5, Duration.ofMinutes(1)));
        circuitBreakers.put("order", new CircuitBreaker("order", 3, Duration.ofMinutes(2)));
        circuitBreakers.put("payment", new CircuitBreaker("payment", 2, Duration.ofMinutes(5)));
        
        // Add tubes with circuit breaker protection
        composite.addTube("user", protect(userService, circuitBreakers.get("user")));
        composite.addTube("order", protect(orderService, circuitBreakers.get("order")));
        composite.addTube("payment", protect(paymentService, circuitBreakers.get("payment")));
        
        // Connect tubes
        composite.connect("user", "order");
        composite.connect("order", "payment");
    }
    
    private Tube protect(Tube tube, CircuitBreaker breaker) {
        return input -> {
            if (!breaker.isAllowed()) {
                throw new CircuitOpenException(
                    "Circuit open for " + breaker.getName()
                );
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
}
```

**Circuit Breaker Considerations:**
- **Failure Thresholds**: How many failures before opening
- **Reset Timeouts**: How long before retry
- **Half-Open State**: Gradual recovery testing
- **Fallback Mechanisms**: What to do when circuit is open

### Bulkhead Pattern

Isolate components to contain failures:

```java
public class BulkheadComposite {
    private final Map<String, Semaphore> bulkheads = new HashMap<>();
    private final Composite composite;
    
    public BulkheadComposite() {
        // Create tubes
        Tube criticalService = new CriticalServiceTube();
        Tube standardService = new StandardServiceTube();
        Tube nonCriticalService = new NonCriticalServiceTube();
        
        // Create bulkheads with different capacities
        bulkheads.put("critical", new Semaphore(10));  // More capacity
        bulkheads.put("standard", new Semaphore(5));   // Medium capacity
        bulkheads.put("nonCritical", new Semaphore(2)); // Limited capacity
        
        // Add tubes with bulkhead protection
        composite.addTube("critical", isolate(criticalService, bulkheads.get("critical")));
        composite.addTube("standard", isolate(standardService, bulkheads.get("standard")));
        composite.addTube("nonCritical", isolate(nonCriticalService, bulkheads.get("nonCritical")));
    }
    
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
}
```

**Bulkhead Strategies:**
- **Thread Pool Isolation**: Separate thread pools
- **Semaphore Isolation**: Limit concurrent calls
- **Client Isolation**: Separate clients for different services
- **Server Isolation**: Deploy services on separate infrastructure

### Graceful Degradation

Maintain core functionality when resources are constrained:

```java
public class GracefulDegradationComposite {
    private final Composite composite;
    private OperatingMode currentMode = OperatingMode.FULL;
    
    public Object process(Object input) {
        // Check available resources
        ResourceStatus status = checkResources();
        
        // Update operating mode if needed
        updateOperatingMode(status);
        
        // Process based on current mode
        switch (currentMode) {
            case FULL:
                return processWithFullFunctionality(input);
                
            case REDUCED:
                return processWithReducedFunctionality(input);
                
            case MINIMAL:
                return processWithMinimalFunctionality(input);
                
            case CRITICAL_ONLY:
                return processOnlyCriticalFunctions(input);
                
            default:
                throw new IllegalStateException("Unknown operating mode");
        }
    }
    
    private ResourceStatus checkResources() {
        // Check system resources
        return new ResourceStatus();
    }
    
    private void updateOperatingMode(ResourceStatus status) {
        if (status.getCpuUtilization() > 90 || status.getMemoryUtilization() > 90) {
            currentMode = OperatingMode.CRITICAL_ONLY;
        } else if (status.getCpuUtilization() > 70 || status.getMemoryUtilization() > 70) {
            currentMode = OperatingMode.MINIMAL;
        } else if (status.getCpuUtilization() > 50 || status.getMemoryUtilization() > 50) {
            currentMode = OperatingMode.REDUCED;
        } else {
            currentMode = OperatingMode.FULL;
        }
    }
    
    private Object processWithFullFunctionality(Object input) {
        // Use all tubes with all features
        return composite.process(input);
    }
    
    private Object processWithReducedFunctionality(Object input) {
        // Skip non-essential tubes
        return composite.processReducedPath(input);
    }
    
    private Object processWithMinimalFunctionality(Object input) {
        // Use only core tubes with minimal features
        return composite.processMinimalPath(input);
    }
    
    private Object processOnlyCriticalFunctions(Object input) {
        // Process only critical operations
        if (isCriticalOperation(input)) {
            return composite.processCriticalPath(input);
        } else {
            return new TemporaryUnavailableResponse(
                "System is in critical-only mode. Please try again later."
            );
        }
    }
    
    private boolean isCriticalOperation(Object input) {
        // Determine if this is a critical operation
        return false;
    }
    
    private enum OperatingMode {
        FULL,           // All features available
        REDUCED,        // Non-essential features disabled
        MINIMAL,        // Only basic functionality
        CRITICAL_ONLY   // Only critical operations
    }
}
```

**Degradation Strategies:**
- **Feature Reduction**: Disable non-critical features
- **Quality Reduction**: Reduce quality of service
- **Caching**: Use cached results instead of processing
- **Simplified Processing**: Use simpler algorithms
- **Admission Control**: Reject non-critical requests

## Evolutionary Compositions

### Versioned Tubes

Support multiple versions of tubes in a composition:

```java
public class VersionedComposite {
    private final Composite composite;
    private final VersionRouter versionRouter;
    
    public VersionedComposite() {
        // Create router
        versionRouter = new VersionRouter();
        
        // Register different versions
        versionRouter.registerVersion("v1", new ProcessorTubeV1());
        versionRouter.registerVersion("v2", new ProcessorTubeV2());
        versionRouter.registerVersion("v3", new ProcessorTubeV3());
        
        // Set active version
        versionRouter.setActiveVersion("v2");
        
        // Add router to composite
        composite.addTube("processor", versionRouter);
    }
    
    public Object process(Object input) {
        return composite.process(input);
    }
    
    // Router that selects appropriate version
    private class VersionRouter implements Tube {
        private final Map<String, Tube> versions = new HashMap<>();
        private String activeVersion;
        
        public void registerVersion(String version, Tube tube) {
            versions.put(version, tube);
        }
        
        public void setActiveVersion(String version) {
            if (!versions.containsKey(version)) {
                throw new IllegalArgumentException("Unknown version: " + version);
            }
            this.activeVersion = version;
        }
        
        @Override
        public Object process(Object input) {
            // Check for version override in input
            String versionToUse = activeVersion;
            if (input instanceof VersionedRequest) {
                String requestedVersion = ((VersionedRequest) input).getVersion();
                if (versions.containsKey(requestedVersion)) {
                    versionToUse = requestedVersion;
                }
            }
            
            // Process with selected version
            return versions.get(versionToUse).process(input);
        }
    }
}
```

**Versioning Strategies:**
- **Multiple Active Versions**: Support several versions simultaneously
- **Gradual Transition**: Shift traffic slowly to new versions
- **A/B Testing**: Compare performance of different versions
- **Compatibility Layers**: Provide adapters between versions

### Pluggable Components

Create compositions that allow plugging in different implementations:

```java
public class PluggableComposite {
    private final Composite composite;
    private final PluginRegistry pluginRegistry;
    
    public PluggableComposite() {
        composite = new Composite("PluggableSystem");
        pluginRegistry = new PluginRegistry();
        
        // Register core tubes
        composite.addTube("input", new InputHandlerTube());
        composite.addTube("output", new OutputHandlerTube());
        
        // Add plugin point
        PluginPoint processorPlugin = new PluginPoint("processor");
        composite.addTube("processor", processorPlugin);
        
        // Connect tubes
        composite.connect("input", "processor");
        composite.connect("processor", "output");
        
        // Discover and register plugins
        discoverPlugins();
    }
    
    private void discoverPlugins() {
        // Find all implementations of ProcessorPlugin
        ServiceLoader<ProcessorPlugin> loader = ServiceLoader.load(ProcessorPlugin.class);
        
        for (ProcessorPlugin plugin : loader) {
            pluginRegistry.registerPlugin("processor", plugin);
        }
    }
    
    public void activatePlugin(String pluginId) {
        // Get plugin point
        PluginPoint processorPlugin = (PluginPoint) composite.getTube("processor");
        
        // Set active plugin
        processorPlugin.setActivePlugin(pluginRegistry.getPlugin("processor", pluginId));
    }
    
    private class PluginPoint implements Tube {
        private final String pointId;
        private Tube activePlugin;
        
        public PluginPoint(String pointId) {
            this.pointId = pointId;
        }
        
        public void setActivePlugin(Tube plugin) {
            this.activePlugin = plugin;
        }
        
        @Override
        public Object process(Object input) {
            if (activePlugin == null) {
                throw new IllegalStateException("No active plugin for " + pointId);
            }
            return activePlugin.process(input);
        }
    }
}
```

**Plugin Approaches:**
- **Service Provider Interface**: Discover implementations at runtime
- **Configuration-Based**: Load components based on configuration
- **Dynamic Loading**: Load plugins on demand
- **Hot Swap**: Replace components without stopping system

### Adaptive Composition

Create compositions that adapt their structure based on conditions:

```java
public class AdaptiveComposite {
    private final Composite composite;
    private final AdaptationEngine adaptationEngine;
    
    public AdaptiveComposite() {
        composite = new Composite("AdaptiveSystem");
        adaptationEngine = new AdaptationEngine(composite);
        
        // Setup initial configuration
        setupInitialConfiguration();
        
        // Start monitoring and adaptation
        adaptationEngine.startMonitoring();
    }
    
    private void setupInitialConfiguration() {
        // Add basic tubes
        composite.addTube("input", new InputHandlerTube());
        composite.addTube("processor", new ProcessorTube());
        composite.addTube("output", new OutputHandlerTube());
        
        // Connect tubes
        composite.connect("input", "processor");
        composite.connect("processor", "output");
    }
    
    private class AdaptationEngine {
        private final Composite composite;
        private ScheduledExecutorService scheduler;
        
        public AdaptationEngine(Composite composite) {
            this.composite = composite;
        }
        
        public void startMonitoring() {
            scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(
                this::analyzeAndAdapt,
                0, 1, TimeUnit.MINUTES
            );
        }
        
        private void analyzeAndAdapt() {
            try {
                // Collect metrics
                CompositeMetrics metrics = collectMetrics();
                
                // Analyze performance
                List<AdaptationAction> actions = analyzePerformance(metrics);
                
                // Apply adaptations
                if (!actions.isEmpty()) {
                    logger.info("Applying {} adaptations", actions.size());
                    for (AdaptationAction action : actions) {
                        applyAdaptation(action);
                    }
                }
            } catch (Exception e) {
                logger.error("Error during adaptation", e);
            }
        }
        
        private void applyAdaptation(AdaptationAction action) {
            switch (action.getType()) {
                case ADD_TUBE:
                    composite.addTube(action.getTubeId(), action.getTube());
                    break;
                    
                case REMOVE_TUBE:
                    composite.removeTube(action.getTubeId());
                    break;
                    
                case REPLACE_TUBE:
                    composite.replaceTube(action.getTubeId(), action.getTube());
                    break;
                    
                case CHANGE_CONNECTION:
                    composite.disconnect(action.getSourceId(), action.getTargetId());
                    composite.connect(action.getSourceId(), action.getNewTargetId());
                    break;
                    
                case UPDATE_CONFIGURATION:
                    Tube tube = composite.getTube(action.getTubeId());
                    if (tube instanceof ConfigurableTube) {
                        ((ConfigurableTube)tube).updateConfiguration(action.getConfiguration());
                    }
                    break;
                    
                default:
                    logger.warn("Unknown adaptation action: {}", action.getType());
            }
        }
    }
}
```

**Adaptation Triggers:**
- **Performance Metrics**: Response time, throughput, error rates
- **Resource Utilization**: CPU, memory, I/O usage
- **Workload Patterns**: Request types, volumes, patterns
- **Time-Based**: Time of day, day of week, seasonal patterns
- **External Events**: System events, business events

## Practical Examples

### E-Commerce Order Processing

Build a complete order processing system with composites:

```java
public class OrderProcessingSystem {
    public Machine buildOrderProcessingSystem() {
        Machine system = new Machine("OrderProcessing");
        
        // Add customer management composite
        system.addComposite("customer", buildCustomerComposite());
        
        // Add inventory management composite
        system.addComposite("inventory", buildInventoryComposite());
        
        // Add payment processing composite
        system.addComposite("payment", buildPaymentComposite());
        
        // Add fulfillment composite
        system.addComposite("fulfillment", buildFulfillmentComposite());
        
        // Connect composites
        system.connect("customer", "inventory");
        system.connect("inventory", "payment");
        system.connect("payment", "fulfillment");
        
        return system;
    }
    
    private Composite buildCustomerComposite() {
        Composite composite = new Composite("CustomerManagement");
        
        // Add tubes for customer management
        composite.addTube("validator", new CustomerValidatorTube());
        composite.addTube("enricher", new CustomerInfoEnricherTube());
        composite.addTube("preferences", new CustomerPreferencesTube());
        
        // Connect tubes
        composite.connect("validator", "enricher");
        composite.connect("enricher", "preferences");
        
        return composite;
    }
    
    private Composite buildInventoryComposite() {
        Composite composite = new Composite("InventoryManagement");
        
        // Add tubes for inventory management
        composite.addTube("checker", new StockCheckerTube());
        composite.addTube("allocator", new InventoryAllocatorTube());
        composite.addTube("reserver", new StockReservationTube());
        
        // Connect tubes
        composite.connect("checker", "allocator");
        composite.connect("allocator", "reserver");
        
        return composite;
    }
    
    private Composite buildPaymentComposite() {
        Composite composite = new Composite("PaymentProcessing");
        
        // Add tubes for payment processing
        composite.addTube("calculator", new TotalCalculatorTube());
        composite.addTube("authorizer", new PaymentAuthorizerTube());
        composite.addTube("processor", new PaymentProcessorTube());
        composite.addTube("receipt", new ReceiptGeneratorTube());
        
        // Connect tubes
        composite.connect("calculator", "authorizer");
        composite.connect("authorizer", "processor");
        composite.connect("processor", "receipt");
        
        return composite;
    }
    
    private Composite buildFulfillmentComposite() {
        Composite composite = new Composite("Fulfillment");
        
        // Add tubes for fulfillment
        composite.addTube("packager", new OrderPackagerTube());
        composite.addTube("shipper", new ShippingArrangerTube());
        composite.addTube("tracker", new TrackingInfoTube());
        composite.addTube("notifier", new ShippingNotificationTube());
        
        // Connect tubes
        composite.connect("packager", "shipper");
        composite.connect("shipper", "tracker");
        composite.connect("tracker", "notifier");
        
        return composite;
    }
}
```

### Data Processing Pipeline

Create a resilient data processing pipeline:

```java
public class DataProcessingPipeline {
    public Composite buildProcessingPipeline() {
        Composite pipeline = new Composite("DataPipeline");
        
        // Add ingestion stage
        Composite ingestion = new Composite("DataIngestion");
        ingestion.addTube("receiver", new DataReceiverTube());
        ingestion.addTube("validator", new SchemaValidatorTube());
        ingestion.addTube("normalizer", new DataNormalizerTube());
        ingestion.connect("receiver", "validator");
        ingestion.connect("validator", "normalizer");
        
        // Add transformation stage
        Composite transformation = new Composite("DataTransformation");
        transformation.addTube("enricher", new DataEnricherTube());
        transformation.addTube("transformer", new DataTransformerTube());
        transformation.addTube("aggregator", new DataAggregatorTube());
        transformation.connect("enricher", "transformer");
        transformation.connect("transformer", "aggregator");
        
        // Add analysis stage
        Composite analysis = new Composite("DataAnalysis");
        analysis.addTube("analyzer", new DataAnalyzerTube());
        analysis.addTube("scorer", new ScoreCalculatorTube());
        analysis.addTube("classifier", new DataClassifierTube());
        analysis.connect("analyzer", "scorer");
        analysis.connect("scorer", "classifier");
        
        // Add output stage
        Composite output = new Composite("DataOutput");
        output.addTube("formatter", new OutputFormatterTube());
        output.addTube("writer", new DataWriterTube());
        output.addTube("notifier", new ProcessingCompleteTube());
        output.connect("formatter", "writer");
        output.connect("writer", "notifier");
        
        // Add stages to pipeline
        pipeline.addTube("ingestion", ingestion);
        pipeline.addTube("transformation", transformation);
        pipeline.addTube("analysis", analysis);
        pipeline.addTube("output", output);
        
        // Connect stages
        pipeline.connect("ingestion", "transformation");
        pipeline.connect("transformation", "analysis");
        pipeline.connect("analysis", "output");
        
        // Add resilience
        applyResilience(pipeline);
        
        return pipeline;
    }
    
    private void applyResilience(Composite pipeline) {
        // Add circuit breakers
        addCircuitBreakers(pipeline);
        
        // Add retry mechanisms
        addRetryMechanisms(pipeline);
        
        // Add persistent checkpoints
        addCheckpoints(pipeline);
        
        // Add monitoring
        addMonitoring(pipeline);
    }
    
    private void addCircuitBreakers(Composite pipeline) {
        // Add circuit breakers to external dependencies
    }
    
    private void addRetryMechanisms(Composite pipeline) {
        // Add retry logic for transient failures
    }
    
    private void addCheckpoints(Composite pipeline) {
        // Add checkpoints for recovery
    }
    
    private void addMonitoring(Composite pipeline) {
        // Add monitoring for the pipeline
    }
}
```

## Common Pitfalls and Solutions

### Excessively Complex Composites

**Problem:** Composites with too many tubes become difficult to understand and maintain.

**Solution: Component Boundaries**

```java
// Before: One massive composite
public class MonolithicComposite {
    public Composite buildComposite() {
        Composite composite = new Composite("Monolith");
        
        // 30+ tubes added directly
        composite.addTube("input", new InputTube());
        composite.addTube("validate", new ValidationTube());
        // Many more tubes...
        composite.addTube("output", new OutputTube());
        
        // Complex connection pattern
        // Many connections...
        
        return composite;
    }
}

// After: Hierarchical organization
public class HierarchicalComposite {
    public Composite buildComposite() {
        Composite root = new Composite("Root");
        
        // Add logically grouped sub-composites
        root.addTube("input", buildInputComposite());
        root.addTube("processing", buildProcessingComposite());
        root.addTube("output", buildOutputComposite());
        
        // Simple connections at this level
        root.connect("input", "processing");
        root.connect("processing", "output");
        
        return root;
    }
    
    private Composite buildInputComposite() {
        Composite input = new Composite("Input");
        // 5-10 logically related tubes
        return input;
    }
    
    private Composite buildProcessingComposite() {
        Composite processing = new Composite("Processing");
        // 5-10 logically related tubes
        return processing;
    }
    
    private Composite buildOutputComposite() {
        Composite output = new Composite("Output");
        // 5-10 logically related tubes
        return output;
    }
}
```

**Recommendations:**
- Limit composites to 7-10 tubes (cognitive limit)
- Group related tubes into sub-composites
- Create clear boundaries around functionality
- Document the purpose of each composite

### Hidden Dependencies

**Problem:** Dependencies between tubes are not explicit, leading to unexpected interactions.

**Solution: Explicit Connections**

```java
// Before: Hidden dependencies
public class HiddenDependencyComposite {
    public Composite buildComposite() {
        Composite composite = new Composite("Hidden");
        
        // Tubes share state through static variables or external resources
        composite.addTube("producer", new ProducerTube());
        composite.addTube("consumer", new ConsumerTube());
        
        // No explicit connection
        // ProducerTube writes to SharedState.data
        // ConsumerTube reads from SharedState.data
        
        return composite;
    }
}

// After: Explicit connections
public class ExplicitConnectionComposite {
    public Composite buildComposite() {
        Composite composite = new Composite("Explicit");
        
        // Tubes communicate through explicit connections
        composite.addTube("producer", new ProducerTube());
        composite.addTube("consumer", new ConsumerTube());
        
        // Explicit connection
        composite.connect("producer", "consumer");
        
        return composite;
    }
}
```

**Recommendations:**
- Make all connections explicit in the composite
- Avoid shared mutable state between tubes
- Pass all dependencies as messages
- Document any external dependencies

### Improper State Handling

**Problem:** Inconsistent state management leads to unpredictable behavior.

**Solution: Consistent State Patterns**

```java
// Before: Inconsistent state handling
public class InconsistentStateTube implements Tube {
    private boolean active = true;
    private int errorCount = 0;
    
    @Override
    public Object process(Object input) {
        if (!active) {
            return null; // Silent failure
        }
        
        try {
            Object result = doProcessing(input);
            errorCount = 0; // Reset on success
            return result;
        } catch (Exception e) {
            errorCount++;
            if (errorCount > 5) {
                active = false; // Silently deactivate
            }
            return null; // No error indication
        }
    }
}

// After: Proper state management
public class ProperStateTube implements Tube {
    private TubeState designState = TubeState.FLOWING;
    private final Map<String, Object> dynamicState = new HashMap<>();
    
    public ProperStateTube() {
        dynamicState.put("errorCount", 0);
        dynamicState.put("lastProcessingTime", 0L);
    }
    
    @Override
    public Object process(Object input) {
        if (designState != TubeState.FLOWING) {
            return new ErrorResponse(
                "Tube not in FLOWING state, current state: " + designState
            );
        }
        
        try {
            long startTime = System.nanoTime();
            Object result = doProcessing(input);
            long processingTime = System.nanoTime() - startTime;
            
            // Update dynamic state
            updateDynamicState("errorCount", 0);
            updateDynamicState("lastProcessingTime", processingTime);
            updateDynamicState("lastSuccessTime", System.currentTimeMillis());
            
            return result;
        } catch (Exception e) {
            // Update error metrics
            int errorCount = (Integer)dynamicState.get("errorCount") + 1;
            updateDynamicState("errorCount", errorCount);
            updateDynamicState("lastError", e.getMessage());
            updateDynamicState("lastErrorTime", System.currentTimeMillis());
            
            // Update design state if needed
            if (errorCount > 5) {
                setDesignState(TubeState.ERROR);
            }
            
            return new ErrorResponse(
                "Processing error: " + e.getMessage()
            );
        }
    }
    
    private void updateDynamicState(String key, Object value) {
        dynamicState.put(key, value);
        // Notify state listeners
        for (StateChangeListener listener : stateListeners) {
            listener.onDynamicStateChanged(this, key, value);
        }
    }
}
```

**Recommendations:**
- Use the standard dual-state model consistently
- Make state transitions explicit
- Report state changes to listeners
- Provide clear error responses
- Document state transition rules

### Overcomplex Flows

**Problem:** Overly complex flow patterns are difficult to understand and debug.

**Solution: Simplified Flows**

```java
// Before: Complex flow with many branches
public class ComplexFlowComposite {
    public Composite buildComposite() {
        Composite composite = new Composite("Complex");
        
        // Add many tubes
        composite.addTube("input", new InputTube());
        composite.addTube("validate", new ValidationTube());
        composite.addTube("route", new RoutingTube());
        composite.addTube("processA", new ProcessATube());
        composite.addTube("processB", new ProcessBTube());
        composite.addTube("processC", new ProcessCTube());
        composite.addTube("transform", new TransformationTube());
        composite.addTube("enrich", new EnrichmentTube());
        composite.addTube("aggregate", new AggregationTube());
        composite.addTube("format", new FormattingTube());
        composite.addTube("output", new OutputTube());
        
        // Complex connections
        composite.connect("input", "validate");
        composite.connect("validate", "route");
        composite.connect("route", "processA");
        composite.connect("route", "processB");
        composite.connect("route", "processC");
        composite.connect("processA", "transform");
        composite.connect("processB", "transform");
        composite.connect("processC", "enrich");
        composite.connect("transform", "aggregate");
        composite.connect("enrich", "aggregate");
        composite.connect("aggregate", "format");
        composite.connect("format", "output");
        
        return composite;
    }
}

// After: Simplified flow with sub-composites
public class SimplifiedFlowComposite {
    public Composite buildComposite() {
        Composite root = new Composite("Root");
        
        // Create sub-composites for main stages
        Composite inputStage = new Composite("InputStage");
        Composite processingStage = new Composite("ProcessingStage");
        Composite outputStage = new Composite("OutputStage");
        
        // Setup input stage
        inputStage.addTube("input", new InputTube());
        inputStage.addTube("validate", new ValidationTube());
        inputStage.connect("input", "validate");
        
        // Setup processing stage (encapsulates complex routing)
        setupProcessingStage(processingStage);
        
        // Setup output stage
        outputStage.addTube("format", new FormattingTube());
        outputStage.addTube("output", new OutputTube());
        outputStage.connect("format", "output");
        
        // Add stages to root
        root.addTube("input", inputStage);
        root.addTube("processing", processingStage);
        root.addTube("output", outputStage);
        
        // Simple, linear connections at root level
        root.connect("input", "processing");
        root.connect("processing", "output");
        
        return root;
    }
    
    private void setupProcessingStage(Composite composite) {
        // Encapsulate complex processing logic
        // ...
    }
}
```

**Recommendations:**
- Prefer linear flows where possible
- Group complex routing into dedicated composites
- Create clear documentation for flow patterns
- Use visual diagrams to illustrate flows
- Limit nesting to 2-3 levels

## Advanced Topics

### Composition Metrics and Analytics

Collect and analyze metrics for compositions:

```java
public class CompositeAnalytics {
    private final Composite composite;
    private final MetricsRegistry registry;
    
    public CompositeAnalytics(Composite composite) {
        this.composite = composite;
        this.registry = new MetricsRegistry();
        
        // Register metrics for each tube
        for (String tubeId : composite.getTubeIds()) {
            registerTubeMetrics(tubeId, composite.getTube(tubeId));
        }
        
        // Register composite-level metrics
        registerCompositeMetrics();
        
        // Start collection
        startCollection();
    }
    
    private void registerTubeMetrics(String tubeId, Tube tube) {
        // Register basic metrics
        registry.registerCounter(tubeId + ".processCount", "Number of items processed");
        registry.registerCounter(tubeId + ".errorCount", "Number of processing errors");
        registry.registerTimer(tubeId + ".processingTime", "Time to process items");
        
        // Register state metrics
        registry.registerGauge(tubeId + ".state", "Current design state", 
            () -> tube.getDesignState().ordinal());
        
        // Register custom metrics based on tube type
        if (tube instanceof MeasurableTube) {
            for (MetricDefinition metric : ((MeasurableTube)tube).getMetrics()) {
                registry.registerMetric(tubeId + "." + metric.getName(), 
                                     metric.getDescription(),
                                     metric.getType(),
                                     metric.getValueSupplier());
            }
        }
    }
    
    private void registerCompositeMetrics() {
        // Register composite-level metrics
        registry.registerGauge("flowingTubeCount", "Number of tubes in FLOWING state",
            () -> countTubesInState(TubeState.FLOWING));
        registry.registerGauge("blockedTubeCount", "Number of tubes in BLOCKED state",
            () -> countTubesInState(TubeState.BLOCKED));
        registry.registerGauge("adaptingTubeCount", "Number of tubes in ADAPTING state",
            () -> countTubesInState(TubeState.ADAPTING));
        registry.registerGauge("errorTubeCount", "Number of tubes in ERROR state",
            () -> countTubesInState(TubeState.ERROR));
        
        // Flow metrics
        registry.registerTimer("endToEndProcessingTime", "Total processing time");
        registry.registerCounter("totalInputCount", "Total inputs processed");
        registry.registerCounter("successfulOutputCount", "Successful outputs produced");
        registry.registerCounter("failedOutputCount", "Failed outputs produced");
    }
    
    private long countTubesInState(TubeState state) {
        return composite.getTubes().values().stream()
            .filter(tube -> tube.getDesignState() == state)
            .count();
    }
    
    public void instrumentProcessing(String startTubeId, String endTubeId) {
        // Track end-to-end performance
        Tube startTube = composite.getTube(startTubeId);
        Tube endTube = composite.getTube(endTubeId);
        
        // Wrap start tube to track inputs
        composite.replaceTube(startTubeId, new TubeWrapper(startTube, 
            (input, output) -> {
                registry.incrementCounter("totalInputCount");
                return output;
            }));
        
        // Wrap end tube to track outputs
        composite.replaceTube(endTubeId, new TubeWrapper(endTube,
            (input, output) -> {
                if (output instanceof ErrorResponse) {
                    registry.incrementCounter("failedOutputCount");
                } else {
                    registry.incrementCounter("successfulOutputCount");
                }
                return output;
            }));
    }
    
    private class TubeWrapper implements Tube {
        private final Tube wrapped;
        private final BiFunction<Object, Object, Object> interceptor;
        
        public TubeWrapper(Tube wrapped, BiFunction<Object, Object, Object> interceptor) {
            this.wrapped = wrapped;
            this.interceptor = interceptor;
        }
        
        @Override
        public Object process(Object input) {
            Object result = wrapped.process(input);
            return interceptor.apply(input, result);
        }
        
        // Delegate other methods to wrapped tube
    }
}
```

### Distributed Compositions

Design compositions that span multiple systems:

```java
public class DistributedCompositionExample {
    public void setupDistributedComposition() {
        // Create local composite
        Composite localComposite = new Composite("LocalSystem");
        
        // Add local tubes
        localComposite.addTube("input", new InputHandlerTube());
        localComposite.addTube("validator", new ValidationTube());
        
        // Add remote connector tube
        RemoteConnectorTube connector = new RemoteConnectorTube(
            "http://remote-service/api/processor"
        );
        localComposite.addTube("remoteConnector", connector);
        
        // Add result handler
        localComposite.addTube("resultHandler", new ResultHandlerTube());
        
        // Connect tubes
        localComposite.connect("input", "validator");
        localComposite.connect("validator", "remoteConnector");
        localComposite.connect("remoteConnector", "resultHandler");
    }
    
    // Remote connector implementation
    private class RemoteConnectorTube implements Tube {
        private final String endpoint;
        private final HttpClient client;
        
        public RemoteConnectorTube(String endpoint) {
            this.endpoint = endpoint;
            this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        }
        
        @Override
        public Object process(Object input) {
            try {
                // Convert input to JSON
                String json = objectToJson(input);
                
                // Create request
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
                
                // Send request
                HttpResponse<String> response = client.send(
                    request, 
                    HttpResponse.BodyHandlers.ofString()
                );
                
                // Process response
                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                    // Success, convert JSON to object
                    return jsonToObject(response.body());
                } else {
                    // Error response
                    return new ErrorResponse(
                        "Remote service error: " + response.statusCode() + 
                        " - " + response.body()
                    );
                }
            } catch (Exception e) {
                // Handle communication errors
                return new ErrorResponse(
                    "Remote communication error: " + e.getMessage()
                );
            }
        }
    }
}
```

### Dynamic Recomposition

Create compositions that can restructure themselves at runtime:

```java
public class DynamicRecompositionExample {
    private final Composite composite;
    private final RecompositionEngine engine;
    
    public DynamicRecompositionExample() {
        composite = new Composite("DynamicSystem");
        engine = new RecompositionEngine(composite);
        
        // Setup initial structure
        setupInitialStructure();
        
        // Start monitoring
        engine.startMonitoring();
    }
    
    private void setupInitialStructure() {
        // Add base tubes
        composite.addTube("input", new InputHandlerTube());
        composite.addTube("processor", new ProcessorTube());
        composite.addTube("output", new OutputHandlerTube());
        
        // Connect tubes
        composite.connect("input", "processor");
        composite.connect("processor", "output");
    }
    
    private class RecompositionEngine {
        private final Composite composite;
        private final Map<String, CompositionPattern> patterns;
        
        public RecompositionEngine(Composite composite) {
            this.composite = composite;
            this.patterns = new HashMap<>();
            
            // Register patterns
            patterns.put("highLoad", new HighLoadPattern());
            patterns.put("lowResourceSystem", new LowResourcePattern());
            patterns.put("highReliability", new HighReliabilityPattern());
            patterns.put("complexData", new ComplexDataPattern());
        }
        
        public void startMonitoring() {
            // Start periodic pattern evaluation
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(
                this::evaluatePatterns,
                0, 1, TimeUnit.MINUTES
            );
        }
        
        private void evaluatePatterns() {
            // Collect system metrics
            SystemMetrics metrics = collectSystemMetrics();
            
            // Find best matching pattern
            String bestPattern = findBestMatchingPattern(metrics);
            
            // If we need to change pattern
            if (bestPattern != null) {
                logger.info("Changing to {} pattern", bestPattern);
                
                // Apply pattern
                applyPattern(bestPattern);
            }
        }
        
        private String findBestMatchingPattern(SystemMetrics metrics) {
            String bestPattern = null;
            double bestScore = 0.0;
            
            for (Map.Entry<String, CompositionPattern> entry : patterns.entrySet()) {
                double score = entry.getValue().evaluateFit(metrics);
                if (score > bestScore) {
                    bestScore = score;
                    bestPattern = entry.getKey();
                }
            }
            
            // Only recommend change if score is significantly better
            return bestScore > 0.7 ? bestPattern : null;
        }
        
        private void applyPattern(String patternName) {
            CompositionPattern pattern = patterns.get(patternName);
            
            // Save current state
            Map<String, Object> state = captureCurrentState();
            
            // Restructure composite
            pattern.applyTo(composite);
            
            // Restore state
            restoreState(state);
        }
        
        private Map<String, Object> captureCurrentState() {
            // Capture relevant state from all tubes
            return new HashMap<>();
        }
        
        private void restoreState(Map<String, Object> state) {
            // Restore state to all tubes
        }
    }
    
    private interface CompositionPattern {
        double evaluateFit(SystemMetrics metrics);
        void applyTo(Composite composite);
    }
    
    private class HighLoadPattern implements CompositionPattern {
        @Override
        public double evaluateFit(SystemMetrics metrics) {
            return 0; // Evaluation logic
        }
        
        @Override
        public void applyTo(Composite composite) {
            // Restructure for high load
            // Add parallel processing, buffering, etc.
        }
    }
    
    // Additional patterns...
}
```

---

*"Composition is the art of arranging simple elements in ways that create emergent intelligence—where the whole becomes greater than the sum of its parts."*