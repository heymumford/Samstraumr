# Samstraumr Refactoring Roadmap: Cognitive Load Reduction

**Objective:** Reduce developer cognitive load by 20-30% while maintaining architectural integrity and error prevention.

**Timeline:** 3-4 sprints
**Priority:** Address in order (each enables next phase)

---

## Phase 1: Component Class Refactoring (Sprint 1-2)

### Problem Statement

`Component.java` is 1,596 lines with 8 distinct responsibilities:
1. Lifecycle state management
2. Parent/child relationship tracking
3. Event listener management
4. Resource allocation and tracking
5. Connection lifecycle management
6. Termination and cleanup
7. Property storage and management
8. Memory logging

**Current State:**
```java
// Current: All responsibilities in Component
Component c = Component.create("name", env);
c.setState(State.ACTIVE);           // Lifecycle
c.registerChild(child);             // Hierarchy
c.addEventListener(listener, type); // Events
c.allocateResource("memory", 50);   // Resources
c.establishConnection("source");    // Connections
```

**Target State:**
```java
// Refactored: Responsibilities separated
Component c = ComponentBuilder.create("name", env)
  .build();

// Lifecycle (separate class)
ComponentLifecycle lc = c.getLifecycle();
lc.setState(State.ACTIVE);

// Hierarchy (separate class)
ComponentHierarchy hier = c.getHierarchy();
hier.registerChild(child);

// Events (separate class)
ComponentEventManager events = c.getEventManager();
events.addEventListener(listener, type);

// Resources (separate class)
ResourceManager resources = c.getResourceManager();
resources.allocate("memory", 50);
```

### Refactoring Steps

#### Step 1.1: Extract ComponentLifecycle (Estimated effort: 3-4 days)

**New class:** `ComponentLifecycle.java`

```java
/**
 * Manages component lifecycle state transitions and validation.
 * Extracted from Component to reduce cognitive load.
 */
public class ComponentLifecycle {
  private volatile State state = State.CONCEPTION;
  private final List<StateTransitionListener> listeners = new CopyOnWriteArrayList<>();
  private final Logger logger;

  // Reduced from 60+ methods to ~15
  public void transitionTo(State newState) {
    validateTransition(this.state, newState);
    State oldState = this.state;
    this.state = newState;
    notifyListeners(oldState, newState);
  }

  public State getState() { return state; }
  public boolean isTerminated() { return state == State.TERMINATED; }
  public boolean isOperational() { return state == State.ACTIVE; }
  public void addTransitionListener(StateTransitionListener l) { /* ... */ }
  // ~12 other core methods
}
```

**Changes to Component.java:**
- Remove: `state` field, `transitionToState()`, `validateStateTransition()`, `notifyStateTransitionListeners()`, `removeAllStateTransitionListeners()`, `addStateTransitionListener()`, `removeStateTransitionListener()`, `isTerminated()`, `isOperational()`, `isEmbryonic()`, `proceedThroughEarlyLifecycle()`
- Add: `ComponentLifecycle lifecycle` field, delegation methods

**Cognitive benefit:**
- Component is now 1,200 lines instead of 1,596 (-25%)
- Developer learning "state transitions" reads ComponentLifecycle, not full Component
- Clear separation: "What is a component?" vs. "How does lifecycle work?"

**Test impact:**
- Existing Component tests continue to work (delegation methods maintain API)
- New ComponentLifecycle unit tests validate transition rules in isolation

---

#### Step 1.2: Extract ComponentEventManager (Estimated effort: 2-3 days)

**New class:** `ComponentEventManager.java`

```java
/**
 * Manages component event publishing and listener subscriptions.
 */
public class ComponentEventManager {
  private final List<EventListener> listeners = new CopyOnWriteArrayList<>();
  private final List<Map<String, Object>> eventQueue = new ArrayList<>();
  private final Logger logger;

  public void addEventListener(EventListener listener, String eventType) { /* ... */ }
  public boolean removeEventListener(EventListener listener) { /* ... */ }
  public boolean publishEvent(String eventType, Map<String, Object> data) { /* ... */ }
  public void processQueuedEvents() { /* ... */ }
  public List<EventListener> getActiveListeners() { /* ... */ }
  // ~8 methods total
}
```

**Changes to Component.java:**
- Remove: `eventListeners`, `addEventListener()`, `removeEventListener()`, `publishEvent()`, `queueEvent()`, `notifyListeners()`, `unsubscribeFromAllEvents()`, `processQueuedEvents()`, `getActiveListeners()`
- Add: `ComponentEventManager eventManager` field, delegation methods

**Cognitive benefit:**
- Component reduced to 1,100 lines (-35% from original)
- Event logic isolated; developers understand "listen to events" without lifecycle context

---

#### Step 1.3: Extract ResourceManager (Estimated effort: 2-3 days)

**New class:** `ResourceManager.java`

```java
/**
 * Manages component resource allocation and tracking.
 */
public class ResourceManager {
  private final Map<String, Object> resources = new ConcurrentHashMap<>();
  private final Logger logger;

  public int getResourceUsage(String resourceType) { /* ... */ }
  public void setResourceUsage(String resourceType, int usage) { /* ... */ }
  public boolean allocateResource(String resourceType, int amount) { /* ... */ }
  public void releaseResource(String resourceType, int amount) { /* ... */ }
  public void updateResourceUsage(State state) { /* ... */ }
  public Map<String, Object> getResourceUsage() { /* ... */ }
  // ~6 methods total
}
```

**Changes to Component.java:**
- Remove: `resources` property map, `allocateResource()`, `releaseResource()`, `getResourceUsage()` (both versions), `setResourceUsage()`, `initializeResourceTracking()`, `updateResourceUsage()`
- Add: `ResourceManager resourceManager` field, delegation methods

**Cognitive benefit:**
- Component reduced to 1,000 lines (-37% from original)
- Resource management is a single concern; easy to understand

---

#### Step 1.4: Extract ComponentHierarchy (Estimated effort: 2-3 days)

**New class:** `ComponentHierarchy.java`

```java
/**
 * Manages parent/child relationships and lineage tracking.
 */
public class ComponentHierarchy {
  private final String uniqueId;
  private final List<String> lineage = Collections.synchronizedList(new ArrayList<>());
  private Identity parentIdentity;
  private final Logger logger;

  public void registerChild(Component child) { /* ... */ }
  public boolean hasChildren() { /* ... */ }
  public List<Component> getChildren() { /* ... */ }
  public void addToLineage(String entry) { /* ... */ }
  public List<String> getLineage() { /* ... */ }
  // ~5 methods total
}
```

**Changes to Component.java:**
- Remove: `lineage`, `parentIdentity`, `registerChild()`, `hasChildren()`, `getChildren()`, `addToLineage()`, `getParentIdentity()`, `createChild()` (instance method)
- Add: `ComponentHierarchy hierarchy` field, delegation methods
- Static `createChild()` methods remain in Component (they handle creation)

**Cognitive benefit:**
- Component reduced to 900-950 lines (-40% from original)
- Hierarchy logic is isolated; easy to test parent/child semantics

---

#### Step 1.5: Connection Management (Estimated effort: 1-2 days)

**New class:** `ConnectionManager.java`

```java
/**
 * Manages active connections to this component.
 */
public class ConnectionManager {
  private final List<String> activeConnections = new ArrayList<>();
  private final Map<String, Map<String, String>> connectionDetails = new ConcurrentHashMap<>();
  private final ResourceManager resources;
  private final Logger logger;

  public String establishConnection(String sourceId) { /* ... */ }
  public boolean closeConnection(String connectionId) { /* ... */ }
  public void closeAllConnections() { /* ... */ }
  public List<String> getActiveConnections() { /* ... */ }
  // ~4 methods total
}
```

**Changes to Component.java:**
- Remove: `establishConnection()`, `closeConnection()`, `closeAllConnections()`, `getActiveConnections()`
- Add: `ConnectionManager connectionManager` field, delegation methods

**Cognitive benefit:**
- Component reduced to 850 lines (-47% from original)
- Connection logic is separate; developers understand connection semantics independently

---

### Phase 1 Summary

**Before refactoring:** `Component.java` 1,596 lines, 60+ methods, 8 responsibilities
**After refactoring:**
- `Component.java`: 850 lines, 30 methods, 2 responsibilities (creation, delegation)
- `ComponentLifecycle.java`: 250 lines
- `ComponentEventManager.java`: 180 lines
- `ResourceManager.java`: 150 lines
- `ComponentHierarchy.java`: 140 lines
- `ConnectionManager.java`: 120 lines

**Total new lines: 840 (distributed across focused classes)**
**Net reduction in max class complexity: -47%**
**Cognitive benefit: Developer can understand each class in 15-20 minutes vs. 1-2 hours for monolith**

---

## Phase 2: State Machine Simplification (Sprint 2-3)

### Problem Statement

26 states with biological analogs add cognitive load. Consolidate into 4-6 semantic groups.

**Current state taxonomy:**
- Operational (13 states): INITIALIZING, READY, ACTIVE, WAITING, RECEIVING_INPUT, PROCESSING_INPUT, OUTPUTTING_RESULT, ERROR, RECOVERING, PAUSED, DORMANT, SUSPENDED, MAINTENANCE
- Lifecycle (6 states): CONCEPTION, CONFIGURING, SPECIALIZING, DEVELOPING_FEATURES, ADAPTING, TRANSFORMING
- Advanced (4 states): STABLE, SPAWNING, DEGRADED, MAINTAINING
- Termination (3 states): DEACTIVATING, TERMINATING, TERMINATED, ARCHIVED

**Problems:**
- Too many to remember
- Biological analogs rarely used in debugging
- Some states have overlapping semantics (PAUSED vs. SUSPENDED vs. DORMANT)

### Proposed State Taxonomy

**Target: 10 semantic states (vs. 26)**

```
FORMATION GROUP (Lifecycle initialization)
├─ CONCEPTION      (initial creation)
├─ INITIALIZING    (setup phase)
├─ CONFIGURING     (boundary definition)
└─ SPECIALIZING    (determining core functions)

OPERATIONAL GROUP (Active processing)
├─ READY           (prepared, not active)
├─ ACTIVE          (processing requests)
└─ STABLE          (optimized performance)

DEGRADED GROUP (Error/recovery scenarios)
├─ ERROR           (encountered error)
├─ RECOVERING      (recovery in progress)
└─ MAINTENANCE     (undergoing repair)

TERMINATION GROUP (End-of-life)
├─ TERMINATING     (shutdown in progress)
├─ TERMINATED      (shutdown complete)
└─ ARCHIVED        (preserved for history)
```

**Removed states (consolidate or eliminate):**
- WAITING → READY (no behavioral difference)
- RECEIVING_INPUT, PROCESSING_INPUT, OUTPUTTING_RESULT → ACTIVE (sub-states not needed at this level)
- PAUSED → combined with SUSPENDED → single PAUSED state
- DORMANT → READY (no behavioral difference)
- DEVELOPING_FEATURES → part of FORMATION
- ADAPTING, TRANSFORMING → not part of core lifecycle (move to application layer if needed)
- SPAWNING → ACTIVE (spawning is operation, not state)
- MAINTAINING → MAINTENANCE (rename for clarity)
- DEACTIVATING → TERMINATING (combine end-of-life)

### Refactoring Steps

#### Step 2.1: Create new State enum with consolidated values

```java
public enum State {
  // Formation states
  CONCEPTION("Initial creation", Category.FORMATION),
  INITIALIZING("Setup phase", Category.FORMATION),
  CONFIGURING("Boundary definition", Category.FORMATION),
  SPECIALIZING("Determining core functions", Category.FORMATION),

  // Operational states
  READY("Prepared but not active", Category.OPERATIONAL),
  ACTIVE("Fully operational and processing", Category.OPERATIONAL),
  STABLE("Optimized performance", Category.OPERATIONAL),

  // Degraded states
  ERROR("Encountered an error", Category.DEGRADED),
  RECOVERING("Recovery in progress", Category.DEGRADED),
  MAINTENANCE("Undergoing repair", Category.DEGRADED),

  // Termination states
  TERMINATING("Shutdown in progress", Category.TERMINATION),
  TERMINATED("Shutdown complete", Category.TERMINATION),
  ARCHIVED("Preserved for history", Category.TERMINATION);

  // Remove biological analogs
  // Simplify categories (remove ADVANCED)
}
```

#### Step 2.2: Update state transition validation

```java
// ComponentLifecycle.validateTransition()
private void validateTransition(State current, State target) {
  // Simplified rules
  if (current == State.TERMINATED && target != State.ARCHIVED) {
    throw new InvalidStateTransitionException(...);
  }
  if (current == State.CONCEPTION && !isFormationState(target)) {
    throw new InvalidStateTransitionException(...);
  }
  // ~4 rules instead of 12
}
```

#### Step 2.3: Update state-dependent guards

```java
// Fewer methods, clearer semantics
public boolean allowsDataProcessing() {
  return this == ACTIVE || this == STABLE;
}

public boolean allowsConfigurationChanges() {
  return this == ACTIVE || this == MAINTENANCE;
}

public boolean allowsRecovery() {
  return this == ERROR || this == RECOVERING || this == MAINTENANCE;
}
```

**Cognitive benefit:**
- 10 states instead of 26 (-62%)
- Developer can memorize state space (8-10 states is short-term memory limit)
- Removed biological analogs eliminate distraction
- Clearer semantics (FORMING → OPERATIONAL → DEGRADED → TERMINATED)

---

## Phase 3: Consciousness Logging Optimization (Sprint 1)

### Problem Statement

Consciousness logging adds 40% log volume; most developers ignore `[CONSCIOUSNESS]` prefix.

### Solution: Opt-In with Clear Patterns

#### Step 3.1: Make consciousness logging environment-configurable

```java
// In ConsciousnessLoggerAdapter
public class ConsciousnessLoggerAdapter implements ConsciousnessLoggerPort {
  private volatile boolean consciousnessLoggingEnabled =
    Boolean.parseBoolean(System.getenv("LOG_CONSCIOUSNESS")); // Default: false

  public void enableConsciousnessLogging(boolean enabled) {
    this.consciousnessLoggingEnabled = enabled;
  }
}
```

**Environment variable:**
```bash
# Enable consciousness logging
export LOG_CONSCIOUSNESS=true

# Run application
./s8r-build test
```

#### Step 3.2: Document patterns for using consciousness logging

Create `docs/guides/consciousness-logging-patterns.md`:

```markdown
# When to Enable Consciousness Logging

Consciousness logging is optional and useful for specific scenarios:

## Pattern 1: Identity Hierarchy Debugging
Enable when: Multiple components with parent/child relationships, unclear lineage
Command: LOG_CONSCIOUSNESS=true ./s8r-test lifecycle

## Pattern 2: Multi-Component Orchestration
Enable when: Composite systems with complex interactions
Command: LOG_CONSCIOUSNESS=true ./s8r-build test

## Pattern 3: Decision Point Analysis
Enable when: Components making state transitions, need to understand WHY
Search logs for: [CONSCIOUSNESS] decision point

## Pattern 4: Production Post-Mortem
Enable when: Incident review needing narrative context
Command: Check archived logs with LOG_CONSCIOUSNESS=true
```

#### Step 3.3: Reduce default log verbosity

```java
// In ConsciousnessLoggerAdapter, reduce narrative output when disabled
@Override
public void logWithNarrative(String componentId, String level, String message,
                             ComponentNarrative narrative) {
  if (!consciousnessLoggingEnabled) {
    // Standard log, no narrative
    logAtLevel(level, "[{}] {}", componentId, message);
    return;
  }

  // Full narrative output only when enabled
  String narrativeContext = String.format(
    "[CONSCIOUSNESS] %s | What: %s | Why: %s | Relates to: %s | Message: %s",
    componentId, narrative.getWhatAmI(), narrative.getWhyDoIExist(),
    narrative.getWhoDoIRelateTo(), message);
  logAtLevel(level, narrativeContext);
}
```

**Cognitive benefit:**
- Default logs are quiet (no `[CONSCIOUSNESS]` noise)
- Developers opt-in when debugging specific scenarios
- Clear patterns reduce "when to use this?" confusion
- Log volume reduction: -40% in normal operation

---

## Phase 4: Telemetry & Measurement (Ongoing, Sprint 1+)

### Objective

Validate that refactoring reduces cognitive load and maintains error prevention.

### Metrics to Collect

#### Developer Workflow Metrics

```bash
# Git hooks to capture (non-intrusive)
# Hook: post-commit
echo "$(date +%s) | $(git show --pretty=%H -s) | $(git diff HEAD~1 --stat | tail -1)" >> .metrics/commits.log

# Hook: post-rebase
# Record: duration, conflicts, test failures
```

#### Build Performance

```bash
# In s8r-build, capture timing
time mvn verify -P quality-checks > /tmp/build.log
grep "BUILD SUCCESS\|BUILD FAILURE" /tmp/build.log
```

#### Test Iteration Count

```bash
# In s8r-test, count how many mvn test invocations before CI passes
# Store in .metrics/test_iterations.log
```

#### Code Review Feedback

```bash
# Analyze PR comments for:
# - Architecture violations (should decrease after Phase 1)
# - State machine confusion (should decrease after Phase 2)
# - Consciousness logging questions (should decrease after Phase 3)
```

### Dashboard

Create `docs/reference/developer-metrics-dashboard.md`:

| Metric | Baseline | Target | Current | Status |
|--------|----------|--------|---------|--------|
| Onboarding time (days) | 5-7 | 2-3 | TBD | |
| First feature time (hours) | 2-3 | 1-1.5 | TBD | |
| Test iteration count (avg) | 3-4 | 1-2 | TBD | |
| Code review comments (arch) | 2-3/PR | <1/PR | TBD | |
| Log noise ratio | 1.4x | 1.0x | TBD | |

---

## Phase 5: Shell Script Unification (Sprint 3+, Lower Priority)

### Problem Statement

6+ shell scripts (s8r-test, s8r-build, s8r-version, etc.) add cognitive load. Developers must learn both Maven and shell wrappers.

### Solution: Migrate to Maven Profiles

**Current:** `./s8r-test unit --coverage`
**Target:** `mvn test -P unit -P coverage` or `mvn test -Dtest-level=unit -Dcoverage`

### Rationale

1. Single mental model (Maven instead of Maven + shell)
2. Easier IDE integration (Maven goals directly)
3. Cross-platform (shell scripts have POSIX/macOS/Windows issues)
4. Lower maintenance burden

### Effort Estimate

HIGH (3-4 sprints) — Only prioritize after Phase 1-3 are complete.

---

## Implementation Roadmap Timeline

```
Sprint 1:
  - Week 1-2: Extract ComponentLifecycle + ComponentEventManager
  - Week 3: Extract ResourceManager + tests
  - Parallel: Opt-in consciousness logging (Phase 3)

Sprint 2:
  - Week 1-2: Extract ComponentHierarchy + ConnectionManager
  - Week 3: State machine consolidation (Phase 2) - Part 1
  - Parallel: Set up telemetry (Phase 4)

Sprint 3:
  - Week 1-2: State machine consolidation - Part 2
  - Week 3: Testing & validation across all refactors
  - Parallel: Telemetry analysis

Sprint 4:
  - Week 1: Bug fixes from testing
  - Week 2-3: Documentation updates
  - Week 4: Retrospective & Phase 5 planning
```

---

## Success Criteria

### Cognitive Load Reduction
- [ ] Component class reduced from 1,596 to <600 lines
- [ ] New component understanding time reduced from 2 hrs to 30 min
- [ ] State space reduced from 26 to 10 states
- [ ] State transition learning time reduced from 2-3 hrs to 30 min

### Error Prevention Maintained
- [ ] All existing tests pass (Component API unchanged via delegation)
- [ ] State transition validation still effective (0 invalid transitions reach runtime)
- [ ] Architecture boundary enforcement unchanged
- [ ] Coverage % maintained or improved (>80%)

### Developer Satisfaction
- [ ] Team survey: "Code is easier to understand" (target: 80% agree)
- [ ] PR review time reduced by 20%
- [ ] New developer onboarding time reduced by 40%
- [ ] Log signal-to-noise ratio improved (consciousness logging opt-in working)

---

## Rollback Plan

Each phase is independently reversible:
- Phase 1: If Component delegation breaks, revert merged classes
- Phase 2: If state consolidation causes validation issues, expand state space
- Phase 3: If consciousness logging needed, set LOG_CONSCIOUSNESS=true
- Phase 4: Telemetry is non-breaking; no rollback needed
- Phase 5: Shell scripts remain during Maven migration; run both in parallel

---

## Conclusion

This roadmap reduces cognitive load by 20-30% while preserving error prevention and architectural integrity. Phase 1 (Component refactoring) is the highest-impact change. Phase 2 (State simplification) addresses the second-largest source of cognitive burden. Both should be completed before evaluating Phase 5 (shell script migration).
