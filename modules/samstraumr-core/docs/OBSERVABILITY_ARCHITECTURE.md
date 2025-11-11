# Samstraumr Observability Architecture Plan

## 1. Conceptual Framework

### Core Concepts

Observability in Samstraumr follows these principles:

1. **Intrinsic Observability**: Every Tube/Component/Machine has built-in self-monitoring capabilities
2. **Extrinsic Observability**: External observation through standardized interfaces
3. **Recursive Observation**: Observers can observe other observers, creating a monitoring hierarchy
4. **Defensive Monitoring**: Resource usage self-regulation to prevent system degradation
5. **Transparency Gradient**: Configurable levels of visibility for different consumers

In Samstraumr, a tube is both the observer and the observed - it can monitor itself and attempt to make sense of a situation given the environment and various complications it finds itself in. It can also ask itself questions, explore possibilities, and track its own experimental results using the Mimir integration.

### System Design

```
┌─────────────────────────────────────────┐
│              ObservabilityPort          │
├─────────────────────────────────────────┤
│ - emitMetric(name, value, tags)         │
│ - emitEvent(name, severity, data)       │
│ - emitTrace(span, parent, data)         │
│ - emitState(state, metadata)            │
│ - registerHealthCheck(name, check)      │
│ - configureAlertThreshold(metric, value)│
└─────────────────────────────────────────┘
             ▲                  ▲
             │                  │
┌────────────┴───────┐ ┌────────┴───────────┐
│ SelfObserver       │ │ ExternalObserver   │
├────────────────────┤ ├────────────────────┤
│ - monitorMemory()  │ │ - attach(observed) │
│ - monitorCycles()  │ │ - detach()         │
│ - detectRecursion()│ │ - configure()      │
└────────────────────┘ └────────────────────┘
```

## 2. Detailed Design

### 2.1 Core Interfaces

```java
/**
 * Core interface for all observable entities in Samstraumr
 */
public interface Observable {
    /**
     * Gets the observability port for this entity
     */
    ObservabilityPort getObservabilityPort();
    
    /**
     * Accepts an observer for extrinsic monitoring
     */
    void acceptObserver(Observer observer);
    
    /**
     * Removes an observer
     */
    void removeObserver(Observer observer);
}

/**
 * Core interface for entities that can observe others
 */
public interface Observer {
    /**
     * Called when an observed entity emits a metric
     */
    void onMetric(String name, double value, Map<String, String> tags);
    
    /**
     * Called when an observed entity emits an event
     */
    void onEvent(String name, String severity, Map<String, Object> data);
    
    /**
     * Called when an observed entity changes state
     */
    void onStateChange(String oldState, String newState, Map<String, Object> metadata);
}
```

### 2.2 Observability Port

```java
/**
 * Standard interface for emitting observability data
 */
public interface ObservabilityPort {
    /**
     * Emits a metric with optional tags
     */
    void emitMetric(String name, double value, Map<String, String> tags);
    
    /**
     * Emits an event with severity and data
     */
    void emitEvent(String name, String severity, Map<String, Object> data);
    
    /**
     * Emits tracing information
     */
    void emitTrace(String spanName, String parentSpan, Map<String, Object> data);
    
    /**
     * Emits state information
     */
    void emitState(String state, Map<String, Object> metadata);
    
    /**
     * Registers a health check function
     */
    void registerHealthCheck(String name, Supplier<HealthStatus> check);
    
    /**
     * Configures an alert threshold for a metric
     */
    void configureAlertThreshold(String metricName, double threshold, String operator);
}
```

### 2.3 Self-Observer Implementation

```java
/**
 * Self-monitoring implementation for Tubes/Components/Machines
 */
public class SelfObserver implements Observer {
    private final Observable owner;
    private final Map<String, Double> thresholds = new HashMap<>();
    private final ScheduledExecutorService scheduler;
    private final AtomicLong memoryBaseline = new AtomicLong(0);
    private final AtomicInteger recursionDepth = new AtomicInteger(0);
    private final AtomicLong lastMemoryCheck = new AtomicLong(0);
    
    public SelfObserver(Observable owner) {
        this.owner = owner;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        initializeMonitoring();
    }
    
    private void initializeMonitoring() {
        // Schedule periodic self-checks
        scheduler.scheduleAtFixedRate(this::monitorMemory, 1, 1, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::monitorRecursion, 1, 1, TimeUnit.SECONDS);
        
        // Set default thresholds
        thresholds.put("memory_growth_pct", 20.0); // 20% growth triggers warning
        thresholds.put("max_recursion_depth", 100.0);
        thresholds.put("max_execution_time_ms", 5000.0);
    }
    
    public void monitorMemory() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        
        if (memoryBaseline.get() == 0) {
            memoryBaseline.set(usedMemory);
            return;
        }
        
        long baselineMemory = memoryBaseline.get();
        double growthPct = (usedMemory - baselineMemory) * 100.0 / baselineMemory;
        
        if (growthPct > thresholds.get("memory_growth_pct")) {
            Map<String, Object> data = new HashMap<>();
            data.put("baseline_memory", baselineMemory);
            data.put("current_memory", usedMemory);
            data.put("growth_pct", growthPct);
            
            owner.getObservabilityPort().emitEvent(
                "memory_growth_warning", 
                "WARNING", 
                data
            );
        }
        
        // Update baseline periodically to avoid false positives from normal growth
        if (System.currentTimeMillis() - lastMemoryCheck.get() > 60000) {
            memoryBaseline.set(usedMemory);
            lastMemoryCheck.set(System.currentTimeMillis());
        }
    }
    
    public void monitorRecursion() {
        if (recursionDepth.get() > thresholds.get("max_recursion_depth")) {
            Map<String, Object> data = new HashMap<>();
            data.put("current_depth", recursionDepth.get());
            data.put("max_allowed", thresholds.get("max_recursion_depth"));
            
            owner.getObservabilityPort().emitEvent(
                "excessive_recursion", 
                "ERROR", 
                data
            );
        }
    }
    
    public void enterRecursion() {
        recursionDepth.incrementAndGet();
    }
    
    public void exitRecursion() {
        recursionDepth.decrementAndGet();
    }
}
```

### 2.4 Observable Implementation for Tubes

```java
/**
 * Implementation of Observable for Tube entities
 */
public abstract class ObservableTube implements Observable {
    private final ObservabilityPort observabilityPort;
    private final SelfObserver selfObserver;
    private final Set<Observer> externalObservers = new CopyOnWriteArraySet<>();
    private String currentState = "INITIALIZED";
    
    public ObservableTube() {
        this.observabilityPort = new TubeObservabilityPort(this);
        this.selfObserver = new SelfObserver(this);
    }
    
    @Override
    public ObservabilityPort getObservabilityPort() {
        return observabilityPort;
    }
    
    @Override
    public void acceptObserver(Observer observer) {
        externalObservers.add(observer);
    }
    
    @Override
    public void removeObserver(Observer observer) {
        externalObservers.remove(observer);
    }
    
    protected void setState(String newState) {
        String oldState = this.currentState;
        this.currentState = newState;
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("transition_time", System.currentTimeMillis());
        
        // Notify all external observers
        for (Observer observer : externalObservers) {
            observer.onStateChange(oldState, newState, metadata);
        }
        
        // Log state change
        observabilityPort.emitState(newState, metadata);
    }
    
    /**
     * Inner class implementing ObservabilityPort for this tube
     */
    private class TubeObservabilityPort implements ObservabilityPort {
        private final ObservableTube tube;
        
        public TubeObservabilityPort(ObservableTube tube) {
            this.tube = tube;
        }
        
        @Override
        public void emitMetric(String name, double value, Map<String, String> tags) {
            for (Observer observer : externalObservers) {
                observer.onMetric(name, value, tags);
            }
        }
        
        @Override
        public void emitEvent(String name, String severity, Map<String, Object> data) {
            for (Observer observer : externalObservers) {
                observer.onEvent(name, severity, data);
            }
        }
        
        @Override
        public void emitTrace(String spanName, String parentSpan, Map<String, Object> data) {
            // Implementation details...
        }
        
        @Override
        public void emitState(String state, Map<String, Object> metadata) {
            // Implementation details...
        }
        
        @Override
        public void registerHealthCheck(String name, Supplier<HealthStatus> check) {
            // Implementation details...
        }
        
        @Override
        public void configureAlertThreshold(String metricName, double threshold, String operator) {
            // Implementation details...
        }
    }
}
```

### 2.5 Observability for Composites

```java
/**
 * Specialized observer for monitoring composite structures
 */
public class CompositeObserver implements Observer {
    private final Map<String, Observable> componentMap = new HashMap<>();
    private final ObservabilityPort aggregationPort;
    
    public CompositeObserver(Observable owningComposite) {
        this.aggregationPort = owningComposite.getObservabilityPort();
    }
    
    public void addComponent(String id, Observable component) {
        componentMap.put(id, component);
        component.acceptObserver(this);
    }
    
    public void removeComponent(String id) {
        Observable component = componentMap.remove(id);
        if (component != null) {
            component.removeObserver(this);
        }
    }
    
    @Override
    public void onMetric(String name, double value, Map<String, String> tags) {
        // Aggregate metrics from components
        Map<String, String> enrichedTags = new HashMap<>(tags);
        enrichedTags.put("source", "component");
        aggregationPort.emitMetric("component." + name, value, enrichedTags);
    }
    
    @Override
    public void onEvent(String name, String severity, Map<String, Object> data) {
        // Forward component events with enrichment
        Map<String, Object> enrichedData = new HashMap<>(data);
        enrichedData.put("source", "component");
        aggregationPort.emitEvent("component." + name, severity, enrichedData);
    }
    
    @Override
    public void onStateChange(String oldState, String newState, Map<String, Object> metadata) {
        // Track component state changes
        Map<String, Object> enrichedMetadata = new HashMap<>(metadata);
        enrichedMetadata.put("old_state", oldState);
        enrichedMetadata.put("source", "component");
        aggregationPort.emitState("component_state_change", enrichedMetadata);
    }
    
    public Map<String, String> getComponentStates() {
        Map<String, String> states = new HashMap<>();
        for (Map.Entry<String, Observable> entry : componentMap.entrySet()) {
            if (entry.getValue() instanceof ObservableTube) {
                ObservableTube tube = (ObservableTube) entry.getValue();
                states.put(entry.getKey(), tube.getCurrentState());
            }
        }
        return states;
    }
}
```

### 2.6 Mimir Integration

```java
/**
 * Integration with Mimir for self-reflection capabilities
 */
public class MimirObservabilityAdapter {
    private final Observable observed;
    private final Map<String, Object> observationHistory = new ConcurrentHashMap<>();
    
    public MimirObservabilityAdapter(Observable observed) {
        this.observed = observed;
        initializeMimirObservation();
    }
    
    private void initializeMimirObservation() {
        observed.acceptObserver(new MimirObserver());
    }
    
    public Map<String, Object> queryObservationHistory(String pattern) {
        // Query the observation history using pattern matching
        Map<String, Object> results = new HashMap<>();
        for (Map.Entry<String, Object> entry : observationHistory.entrySet()) {
            if (entry.getKey().matches(pattern)) {
                results.put(entry.getKey(), entry.getValue());
            }
        }
        return results;
    }
    
    public void runExperiment(String experimentName, Runnable action, String metricToObserve) {
        // Record baseline
        double baselineValue = getMetricValue(metricToObserve);
        
        // Run the experiment
        action.run();
        
        // Measure impact
        double newValue = getMetricValue(metricToObserve);
        double change = newValue - baselineValue;
        
        // Record results
        Map<String, Object> results = new HashMap<>();
        results.put("baseline", baselineValue);
        results.put("result", newValue);
        results.put("change", change);
        results.put("timestamp", System.currentTimeMillis());
        
        observationHistory.put("experiment." + experimentName, results);
    }
    
    private double getMetricValue(String metricName) {
        // Get current value of a metric
        Object value = observationHistory.getOrDefault("metric." + metricName, 0.0);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }
    
    /**
     * Observer that records data for Mimir self-reflection
     */
    private class MimirObserver implements Observer {
        @Override
        public void onMetric(String name, double value, Map<String, String> tags) {
            observationHistory.put("metric." + name, value);
        }
        
        @Override
        public void onEvent(String name, String severity, Map<String, Object> data) {
            observationHistory.put("event." + name, data);
        }
        
        @Override
        public void onStateChange(String oldState, String newState, Map<String, Object> metadata) {
            Map<String, Object> stateInfo = new HashMap<>();
            stateInfo.put("old", oldState);
            stateInfo.put("new", newState);
            stateInfo.put("metadata", metadata);
            stateInfo.put("timestamp", System.currentTimeMillis());
            
            observationHistory.put("state_change." + System.currentTimeMillis(), stateInfo);
        }
    }
}
```

## 3. Implementation Strategy

### 3.1 For Tubes

1. **Basic Observability Tube**: A tube that only implements self-observation
   - Memory usage monitoring
   - State tracking
   - Recursion detection
   - Basic health checks

2. **Advanced Observability Tube**: Self-observation + ability to observe other tubes
   - External observation capability
   - Metric aggregation
   - Event forwarding
   - Health status propagation

3. **Mimir-Enabled Tube**: Full observability with self-experimentation capabilities
   - Self-experimentation
   - A/B testing of different operational parameters
   - Learning from past behavior
   - Adaptation based on observations

### 3.2 For Composites

1. **Component Registry**: Track all components within the composite
   - Maintain registry of all child components
   - Track creation/destruction of components
   - Monitor component relationships

2. **Aggregation Logic**: Combine metrics and events from multiple components
   - Statistical aggregation (sum, avg, min, max, percentiles)
   - Component-level filtering
   - Anomaly detection across components

3. **Dependency Monitoring**: Track health and relationships between components
   - Identify dependency chains
   - Track health propagation
   - Detect cascading failures
   - Measure dependency satisfaction

### 3.3 For Machines

1. **Resource Monitoring**: Track CPU, memory, and IO usage
   - CPU time tracking
   - Memory allocation monitoring
   - IO operation tracking
   - Throughput and latency measurements

2. **State Management**: Monitor state transitions and execution times
   - State transition tracking
   - Execution time profiling
   - Suspension/resumption monitoring
   - Deadlock detection

3. **Exception Handling**: Track errors and recovery attempts
   - Exception tracking
   - Recovery attempt monitoring
   - Failure rate calculation
   - Circuit breaker pattern implementation

### 3.4 External Systems Integration

1. **Prometheus Adapter**: Export metrics in Prometheus format
   - Implement /metrics endpoint
   - Support metric types (counter, gauge, histogram)
   - Configure metric retention
   - Support metric scraping

2. **OpenTelemetry Integration**: Support for distributed tracing
   - Trace context propagation
   - Span creation and management
   - Attribute handling
   - Trace exporting

3. **Logging System**: Integration with structured logging systems
   - Structured log format
   - Log level control
   - Context enrichment
   - Log aggregation

4. **Alerting System**: Threshold-based alerts and notifications
   - Alert rule definition
   - Notification channels
   - Alert grouping
   - Alert state management

## 4. Test-Driven Development Approach

### 4.1 Positive Test Cases

```java
@Test
public void testTubeEmitsMetricsSuccessfully() {
    // Given
    TestObserver observer = new TestObserver();
    ObservableTube tube = new BasicObservableTube("test-tube");
    tube.acceptObserver(observer);
    
    // When
    tube.getObservabilityPort().emitMetric("cpu_usage", 45.2, Map.of("instance", "test"));
    
    // Then
    assertEquals(1, observer.getMetricCount());
    assertEquals(45.2, observer.getLastMetricValue("cpu_usage"), 0.001);
    assertEquals("test", observer.getLastMetricTags("cpu_usage").get("instance"));
}

@Test
public void testTubeStateChangeNotification() {
    // Given
    TestObserver observer = new TestObserver();
    ObservableTube tube = new BasicObservableTube("test-tube");
    tube.acceptObserver(observer);
    
    // When
    tube.setState("RUNNING");
    
    // Then
    assertEquals(1, observer.getStateChangeCount());
    assertEquals("INITIALIZED", observer.getLastStateChange().getOldState());
    assertEquals("RUNNING", observer.getLastStateChange().getNewState());
}

@Test
public void testRecursiveObservationWorks() {
    // Given
    ObservableTube primaryTube = new BasicObservableTube("primary");
    ObservableTube observerTube = new ObserverTube("observer");
    TestObserver topObserver = new TestObserver();
    
    // When
    primaryTube.acceptObserver((Observer)observerTube);
    observerTube.acceptObserver(topObserver);
    primaryTube.getObservabilityPort().emitEvent("important_event", "INFO", Map.of("data", "test"));
    
    // Then
    assertEquals(1, topObserver.getEventCount());
    assertEquals("observed.important_event", topObserver.getLastEventName());
}

@Test
public void testCompositeAggregatesComponentMetrics() {
    // Given
    ObservableComposite composite = new BasicObservableComposite("test-composite");
    ObservableTube component1 = new BasicObservableTube("component1");
    ObservableTube component2 = new BasicObservableTube("component2");
    TestObserver observer = new TestObserver();
    
    // When
    composite.addComponent("c1", component1);
    composite.addComponent("c2", component2);
    composite.acceptObserver(observer);
    component1.getObservabilityPort().emitMetric("response_time", 150, Map.of());
    component2.getObservabilityPort().emitMetric("response_time", 250, Map.of());
    
    // Then
    assertEquals(2, observer.getMetricCount());
    Map<String, Double> metrics = observer.getMetricsByName("component.response_time");
    assertEquals(2, metrics.size());
}

@Test
public void testSelfMonitoringDetectsMemoryGrowth() {
    // Given
    MemoryLeakingTube tube = new MemoryLeakingTube("leaky-tube");
    TestObserver observer = new TestObserver();
    tube.acceptObserver(observer);
    
    // When
    tube.simulateMemoryLeak();
    
    // Then - should detect abnormal memory usage and emit a warning
    assertTrue(observer.receivedEventWithName("memory_growth_warning"));
}

@Test
public void testMimirExperimentTracking() {
    // Given
    ObservableTube tube = new BasicObservableTube("test-tube");
    MimirObservabilityAdapter mimir = new MimirObservabilityAdapter(tube);
    
    // When
    mimir.runExperiment("throughput_test", () -> {
        // Simulate an experiment
        tube.getObservabilityPort().emitMetric("throughput", 100, Map.of());
        tube.getObservabilityPort().emitMetric("throughput", 200, Map.of());
    }, "throughput");
    
    // Then
    Map<String, Object> results = mimir.queryObservationHistory("experiment.throughput_test");
    assertNotNull(results);
    assertEquals(100.0, ((Map<String, Object>)results.get("experiment.throughput_test")).get("change"));
}
```

### 4.2 Negative Test Cases

```java
@Test
public void testObserverRejectedByObservable() {
    // Given
    ObservableTube tube = new RestrictedObservableTube("restricted");
    TestObserver observer = new TestObserver();
    
    // When/Then
    assertThrows(SecurityException.class, () -> {
        tube.acceptObserver(observer);
    });
}

@Test
public void testRecursionLimitExceeded() {
    // Given
    RecursiveTube tube = new RecursiveTube("recursive");
    TestObserver observer = new TestObserver();
    tube.acceptObserver(observer);
    
    // When
    tube.triggerRecursion(150); // Exceeds default threshold of 100
    
    // Then
    assertTrue(observer.receivedEventWithName("excessive_recursion"));
    assertEquals("ERROR", observer.getLastEventSeverity("excessive_recursion"));
}

@Test
public void testInvalidMetricRejected() {
    // Given
    ObservableTube tube = new ValidatingObservableTube("validator");
    TestObserver observer = new TestObserver();
    tube.acceptObserver(observer);
    
    // When/Then
    assertThrows(IllegalArgumentException.class, () -> {
        tube.getObservabilityPort().emitMetric(null, 100, Map.of());
    });
    
    assertThrows(IllegalArgumentException.class, () -> {
        tube.getObservabilityPort().emitMetric("invalid.name!", 100, Map.of());
    });
}

@Test
public void testObserverFailureHandling() {
    // Given
    ObservableTube tube = new RobustObservableTube("robust");
    FailingObserver failingObserver = new FailingObserver();
    TestObserver monitorObserver = new TestObserver();
    tube.acceptObserver(failingObserver);
    tube.acceptObserver(monitorObserver);
    
    // When
    tube.getObservabilityPort().emitMetric("test", 100, Map.of());
    
    // Then
    assertTrue(monitorObserver.receivedEventWithName("observer_failure"));
    // Should still receive the metric even though one observer failed
    assertEquals(100, monitorObserver.getLastMetricValue("test"));
}

@Test
public void testCircularObservationDetection() {
    // Given
    ObservableTube tube1 = new BasicObservableTube("tube1");
    ObservableTube tube2 = new BasicObservableTube("tube2");
    TestObserver observer = new TestObserver();
    tube1.acceptObserver(observer);
    
    // When
    tube1.acceptObserver((Observer)tube2);
    
    // Then
    assertThrows(IllegalStateException.class, () -> {
        tube2.acceptObserver((Observer)tube1); // This would create a circular reference
    });
}

@Test
public void testThrottlingExcessiveEvents() {
    // Given
    ThrottlingObservableTube tube = new ThrottlingObservableTube("throttled");
    TestObserver observer = new TestObserver();
    tube.acceptObserver(observer);
    tube.setEventThrottleLimit(5, Duration.ofSeconds(1));
    
    // When
    for (int i = 0; i < 20; i++) {
        tube.getObservabilityPort().emitEvent("spam_event", "INFO", Map.of("count", i));
    }
    
    // Then
    assertEquals(5, observer.getEventCount()); // Only the first 5 should get through
    assertTrue(observer.receivedEventWithName("events_throttled"));
}
```

## 5. Implementation Roadmap

### Phase 1: Core Interfaces (Week 1)
- Define Observable and Observer interfaces
- Create ObservabilityPort interface
- Implement basic SelfObserver for memory and resource monitoring
- Develop unit tests for core interfaces

### Phase 2: Basic Component Implementation (Week 2)
- Implement ObservableTube
- Implement ObservableComposite
- Implement ObservableMachine
- Create integration tests between components

### Phase 3: Advanced Features (Week 3)
- Implement Mimir integration for self-experimentation
- Create specialized observers for different use cases
- Implement resource safety mechanisms
- Develop tests for advanced features

### Phase 4: External Integration (Week 4)
- Create adapters for monitoring systems (Prometheus, etc.)
- Implement logging and alerting integrations
- Create dashboards and visualization examples
- Develop end-to-end tests with external systems

### Phase 5: Testing and Documentation (Week 5)
- Complete test suite with positive and negative cases
- Create comprehensive documentation
- Build examples and tutorials
- Perform final review and refinement

## 6. Conclusion

The Samstraumr Observability framework provides a unified approach to monitoring and introspection that perfectly aligns with the tube-composite-machine philosophy. By making observability a first-class concept, we enable components to be both self-aware and externally observable, creating a rich ecosystem for monitoring, debugging, and self-optimization.

This design also ensures that systems can scale safely, with built-in guards against resource exhaustion and recursive loops. The integration with Mimir provides powerful self-experimentation capabilities, allowing tubes to truly understand their own behavior and evolve accordingly.

The key innovation of this approach is the unification of intrinsic and extrinsic observability, allowing a tube to be both the observer and the observed. This creates a powerful framework for building resilient, self-aware systems that can monitor their own health and performance, detect anomalies, and even experiment with different operational parameters to optimize their behavior.