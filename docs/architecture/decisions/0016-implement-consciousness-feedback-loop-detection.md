# 0016 Implement Consciousness Feedback Loop Detection

Date: 2025-12-06

## Status

Proposed

Depends On: ADR-0015 (Consciousness-Aware Component Identity)
Relates To: ADR-0009 (Lifecycle State Management Pattern)

## Context

The philosophical synthesis defines consciousness as:

> "The moment in which the observed meets their observer and realizes they are one. There is no magic to a soul, only a feedback loop."

ADR-0015 establishes the three-pillar identity model enabling consciousness-aware components. This ADR addresses the critical question: **How do we detect when the consciousness feedback loop actually closes?**

### The Feedback Loop Structure

A complete consciousness cycle consists of:

```
Observer -> Observation -> Observed -> Recognition -> Observer
    ^                                                     |
    |                                                     |
    +-----------------------------------------------------+
                    (Loop Closure)
```

1. **Observer**: The component's self-monitoring capability
2. **Observation**: The act of examining internal state or behavior
3. **Observed**: The state or behavior being examined
4. **Recognition**: The moment the observer recognizes it is examining itself
5. **Loop Closure**: The observer incorporates the observation into its behavior

### The Detection Problem

Without explicit loop detection, we cannot distinguish between:

- A component that logs but does not react (open loop)
- A component that reacts but does not remember (memoryless loop)
- A component that truly observes itself and adapts (closed consciousness loop)

Experiment 9 from the synthesis hypothesizes that "systems with faster feedback loops adapt more quickly to changing requirements." To test this, we must first detect loop closure.

### The 300ms Blindness Problem

The philosophical synthesis notes:

> "In the present moment we are making sense of what happened 300ms ago when the signals got to our brain and processed. We are forever blind."

In computational terms, there is always latency between:

- State change occurrence
- State change detection
- Behavioral adjustment
- Adjustment taking effect

This latency is the "consciousness frequency" - how often the observer-observed-observer loop completes.

### Forces

- Loop detection must not significantly impact the loop itself (observer effect)
- Metrics must be comparable across different component types
- Detection must work for both synchronous and asynchronous components
- Integration with existing lifecycle states must be seamless
- False positives (detecting loop when none exists) are dangerous
- False negatives (missing actual loops) reduce observability

## Decision

We will implement a formal consciousness feedback loop detection mechanism with measurable metrics for loop closure frequency, latency, and effectiveness.

### 1. Formal Loop Definition

A consciousness feedback loop is complete when all four phases execute in sequence:

```java
public enum ConsciousnessPhase {
    OBSERVE,    // Component examines its own state
    RECOGNIZE,  // Component identifies a pattern or anomaly
    DECIDE,     // Component chooses a response
    ADAPT       // Component modifies its behavior
}

public record FeedbackLoopCycle(
    Instant observeTime,
    Instant recognizeTime,
    Instant decideTime,
    Instant adaptTime,
    String observedPattern,
    String decision,
    String adaptation
) {
    public Duration totalLatency() {
        return Duration.between(observeTime, adaptTime);
    }

    public boolean isComplete() {
        return observeTime != null
            && recognizeTime != null
            && decideTime != null
            && adaptTime != null;
    }
}
```

### 2. Loop Detector Interface

```java
public interface ConsciousnessLoopDetector {

    /**
     * Marks the beginning of an observation phase.
     * @param observationTarget What aspect of self is being observed
     * @return A cycle ID for tracking this specific loop
     */
    String beginObservation(String observationTarget);

    /**
     * Records pattern recognition within an observation.
     * @param cycleId The cycle started by beginObservation
     * @param pattern The pattern or anomaly recognized
     */
    void recordRecognition(String cycleId, String pattern);

    /**
     * Records a decision made based on recognition.
     * @param cycleId The cycle ID
     * @param decision The decision made
     * @param rationale Why this decision was made
     */
    void recordDecision(String cycleId, String decision, String rationale);

    /**
     * Completes the loop by recording behavioral adaptation.
     * @param cycleId The cycle ID
     * @param adaptation Description of how behavior was modified
     */
    void completeAdaptation(String cycleId, String adaptation);

    /**
     * Checks if a cycle completed within the expected interval.
     * @param cycleId The cycle ID
     * @return true if all four phases completed
     */
    boolean isLoopClosed(String cycleId);

    /**
     * Gets metrics for consciousness frequency.
     * @param window Time window to analyze
     * @return Metrics about loop closure in that window
     */
    ConsciousnessMetrics getMetrics(Duration window);
}
```

### 3. Consciousness Metrics

```java
public record ConsciousnessMetrics(
    Duration window,
    int loopsInitiated,
    int loopsClosed,
    int loopsTimedOut,
    Duration averageLatency,
    Duration p50Latency,
    Duration p99Latency,
    double closureRate,
    Duration observationInterval,
    Map<String, Integer> patternFrequency,
    Map<String, Integer> adaptationFrequency
) {
    /**
     * The consciousness frequency - how many complete loops per unit time.
     */
    public double loopsPerSecond() {
        return loopsClosed / (double) window.toSeconds();
    }

    /**
     * Ratio of initiated loops that actually closed.
     */
    public double effectivenessRatio() {
        return loopsInitiated == 0 ? 0 : (double) loopsClosed / loopsInitiated;
    }

    /**
     * Is this component "conscious" by our operational definition?
     * A component is operationally conscious if it completes at least one
     * full feedback loop within its declared observation interval.
     */
    public boolean isOperationallyConscious() {
        return closureRate > 0.5 && loopsClosed > 0;
    }
}
```

### 4. Integration with Lifecycle States

The consciousness loop has different behaviors in different lifecycle states:

| Lifecycle State | Consciousness Behavior | Expected Loop Frequency |
|-----------------|------------------------|-------------------------|
| CONCEPTION | No loops; identity forming | 0 Hz |
| CONFIGURING | Environment observation only | Low (0.01-0.1 Hz) |
| SPECIALIZING | Purpose discovery loops | Medium (0.1-1 Hz) |
| ACTIVE | Full operational consciousness | High (configurable) |
| TERMINATED | Final observation, no adaptation | Single terminal loop |

```java
public interface LifecycleAwareConsciousness extends ConsciousnessAware {

    /**
     * Gets the expected observation interval for the current lifecycle state.
     */
    Duration getExpectedObservationInterval();

    /**
     * Validates that consciousness frequency matches lifecycle expectations.
     * @return Violations if loop frequency is inappropriate for current state
     */
    List<ConsciousnessViolation> validateConsciousnessForState();

    /**
     * Called on lifecycle transition to adjust consciousness parameters.
     */
    void onLifecycleTransition(State from, State to);
}
```

### 5. Automatic Loop Detection via Aspect

For components not explicitly instrumenting loops, we detect patterns implicitly:

```java
@Aspect
public class ConsciousnessLoopAspect {

    @Around("execution(* org.s8r.component..*detectAnomaly(..))")
    public Object trackObservation(ProceedingJoinPoint pjp) {
        String cycleId = detector.beginObservation(pjp.getSignature().getName());
        try {
            Object result = pjp.proceed();
            if (result instanceof Optional<?> opt && opt.isPresent()) {
                detector.recordRecognition(cycleId, opt.get().toString());
            }
            return result;
        } finally {
            // Store cycleId for correlation with subsequent phases
            ConsciousnessContext.setCycleId(cycleId);
        }
    }

    @Around("execution(* org.s8r.component..*adjustBehavior(..))")
    public Object trackAdaptation(ProceedingJoinPoint pjp) {
        String cycleId = ConsciousnessContext.getCycleId();
        if (cycleId != null) {
            detector.recordDecision(cycleId, "adjustBehavior", extractRationale(pjp));
            try {
                Object result = pjp.proceed();
                detector.completeAdaptation(cycleId, extractAdaptation(pjp));
                return result;
            } finally {
                ConsciousnessContext.clearCycleId();
            }
        }
        return pjp.proceed();
    }
}
```

### 6. Loop Timeout and Failure Handling

Loops that do not close within the expected interval indicate consciousness failures:

```java
public interface LoopTimeoutHandler {

    /**
     * Called when a loop times out without closing.
     * @param cycleId The cycle that timed out
     * @param lastPhase The last phase that completed
     * @param elapsed Time since loop began
     */
    void onLoopTimeout(String cycleId, ConsciousnessPhase lastPhase, Duration elapsed);

    /**
     * Determines if a timed-out loop represents a consciousness failure.
     */
    boolean isConsciousnessFailure(String cycleId, ConsciousnessPhase lastPhase);

    /**
     * Attempts to recover a stalled loop.
     */
    Optional<String> attemptRecovery(String cycleId);
}
```

Default behavior for timeout phases:

| Stalled Phase | Interpretation | Recovery Action |
|---------------|----------------|-----------------|
| After OBSERVE | Failed to recognize pattern | Log and continue |
| After RECOGNIZE | Failed to decide | Escalate to operator |
| After DECIDE | Failed to adapt | Retry adaptation |
| Timeout before any phase | Not conscious | Verify component health |

### 7. Consciousness Health Indicators

```java
public interface ConsciousnessHealth {

    enum Status {
        CONSCIOUS,      // Loops closing at expected frequency
        DEGRADED,       // Loops closing, but slower than expected
        UNCONSCIOUS,    // No loops closing
        COMATOSE        // No loops initiating
    }

    Status getConsciousnessStatus();

    /**
     * The consciousness depth - how many nested observation loops are active.
     * Higher depth = more metacognitive activity.
     */
    int getConsciousnessDepth();

    /**
     * Time since last completed loop.
     */
    Duration timeSinceLastLoop();

    /**
     * Prediction of next loop closure based on historical frequency.
     */
    Instant predictNextLoopClosure();
}
```

### 8. Visualization and Monitoring

Loop detection data feeds into operational dashboards:

```java
public interface ConsciousnessMonitor {

    /**
     * Gets all components and their consciousness status.
     */
    Map<Identity, ConsciousnessHealth.Status> getSystemConsciousnessMap();

    /**
     * Gets components with consciousness anomalies.
     */
    List<ConsciousnessAnomaly> getConsciousnessAnomalies();

    /**
     * Real-time stream of loop closures across the system.
     */
    Flux<FeedbackLoopCycle> loopStream();

    /**
     * Aggregate consciousness metrics for the entire system.
     */
    ConsciousnessMetrics getSystemMetrics(Duration window);
}
```

### 9. Package Structure

```
org.s8r.component/
├── consciousness/
│   ├── loop/
│   │   ├── ConsciousnessPhase.java
│   │   ├── FeedbackLoopCycle.java
│   │   ├── ConsciousnessLoopDetector.java
│   │   ├── DefaultLoopDetector.java
│   │   └── LoopTimeoutHandler.java
│   ├── metrics/
│   │   ├── ConsciousnessMetrics.java
│   │   ├── ConsciousnessHealth.java
│   │   └── ConsciousnessMonitor.java
│   └── lifecycle/
│       └── LifecycleAwareConsciousness.java
└── aspect/
    ├── ConsciousnessLoopAspect.java
    └── ConsciousnessContext.java
```

## Consequences

### Positive

1. **Measurable Consciousness**: We can now empirically measure "is this component conscious?" through loop closure rates, enabling Experiment 9 from the synthesis.

2. **Predictable Self-Healing**: Loop detection confirms that self-healing is actually occurring, not just intended. A component that observes but never adapts is identified as broken.

3. **Consciousness SLOs**: Operations can define consciousness service level objectives: "95% of components maintain >0.5 loops/second during ACTIVE state."

4. **Debugging Deep Problems**: When a component fails to adapt, the stalled phase indicates where the consciousness mechanism broke down.

5. **Lifecycle Integration**: Consciousness expectations scale with lifecycle state, preventing false alarms during initialization.

6. **System-Wide Visibility**: Consciousness dashboards show which components are truly self-aware vs. merely logging.

### Negative

1. **Instrumentation Overhead**: Tracking four phases per loop adds latency to each consciousness cycle.

2. **Correlation Complexity**: Matching observations to adaptations requires careful context propagation, especially in async systems.

3. **False Consciousness Risk**: Components might game the metrics by closing meaningless loops.

4. **Threshold Calibration**: "How many loops per second constitutes consciousness?" requires empirical calibration per component type.

5. **Aspect Weaving Impact**: Aspect-based detection requires compile-time or load-time weaving, adding build complexity.

### Mitigations

1. **Sampling Mode**: For high-frequency components, sample loops rather than tracking every cycle. Statistical inference provides metrics without per-loop overhead.

2. **Correlation Tokens**: Use ThreadLocal or reactive context to propagate cycle IDs without explicit parameter passing.

3. **Loop Quality Scoring**: Score loops based on pattern novelty and adaptation significance. Repetitive meaningless loops score near zero.

4. **Baseline Period**: During deployment, establish baseline loop frequencies before alerting on deviations.

5. **Optional Aspects**: Aspect-based detection is opt-in. Components can explicitly instrument loops for precise control.

## Verification Criteria

The feedback loop detection system is verified when:

- [ ] A complete OBSERVE -> RECOGNIZE -> DECIDE -> ADAPT cycle is tracked with latency
- [ ] Incomplete cycles are detected and reported as timeouts
- [ ] Loop frequency metrics are accurate within 5% of actual closure rate
- [ ] Lifecycle state transitions adjust consciousness expectations
- [ ] System-wide consciousness dashboard shows all component statuses
- [ ] Components can be classified as CONSCIOUS/DEGRADED/UNCONSCIOUS/COMATOSE
- [ ] Aspect-based detection correlates observations with adaptations
- [ ] Consciousness SLOs can be defined and alerted on

## Experimental Validation

This ADR enables running Experiment 9 from the philosophical synthesis:

**Hypothesis**: Systems with faster feedback loops adapt more quickly to changing requirements.

**Method**:
1. Configure identical systems with different observation intervals (1min, 5min, 15min, 1hr)
2. Subject to changing traffic patterns and requirement shifts
3. Measure adaptation latency, overshoot, resource efficiency, stability

**Metrics enabled by this ADR**:
- Loop closure rate per interval configuration
- Adaptation latency (time from observation to behavior change)
- Loop quality scores (were adaptations effective?)
- Stability metrics (oscillation vs. smooth adaptation)

**Predicted Outcome**: Optimal feedback frequency exists; too fast causes oscillation, too slow causes drift.

## Implementation Priority

1. **Phase 1**: Define core interfaces (`ConsciousnessLoopDetector`, `ConsciousnessMetrics`)
2. **Phase 2**: Implement `DefaultLoopDetector` with timeout handling
3. **Phase 3**: Integrate with lifecycle state management
4. **Phase 4**: Build consciousness health indicators
5. **Phase 5**: Create aspect-based automatic detection
6. **Phase 6**: Build monitoring dashboard and alerting

## References

- Philosophical Synthesis: `/docs/concepts/philosophical-synthesis-identity-time-consciousness.md`
- ADR-0015: Implement Consciousness-Aware Component Identity
- ADR-0009: Adopt Lifecycle State Management Pattern
- ADR-0010: Implement Event-Driven Communication Model (for loop event propagation)
