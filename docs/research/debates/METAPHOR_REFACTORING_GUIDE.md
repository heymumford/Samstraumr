<!--
Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>

Licensed under Mozilla Public License 2.0.
See LICENSE file for details.
-->

# Consciousness Metaphor Refactoring Guide

This document provides specific, actionable recommendations for decoupling infrastructure from consciousness claims.

---

## Problem Statement

Current state conflates:
- **Infrastructure**: Logging that observes state transitions + detects cycles
- **Claim**: "This infrastructure demonstrates consciousness"

This is misleading because:
1. **Falsifiability problem**: No test can falsify consciousness claim
2. **Evidence gap**: Logging ≠ consciousness (required: intentionality, qualia, metacognition)
3. **Developer confusion**: Teams may believe they've built conscious systems when they've built well-logged ones

---

## Code Refactoring Targets

### 1. Interface Renames

| Current | Recommended | Rationale |
|---|---|---|
| `ConsciousnessLoggerPort` | `ObservationLoggingPort` | Describes functionality, not claims |
| `logSelfObservation()` | `logStateObservation()` | Avoids "self" consciousness implication |
| `logFeedbackLoopClosure()` | `recordFeedbackCycleClosure()` | "Closure" → "cycle closure" (neutral) |
| `logWithIdentityChain()` | `logWithFullContext()` | Describes what happens, not what it means |

**Locations to update**:
- `/Users/vorthruna/ProjectsWATTS/Samstraumr/modules/samstraumr-core/src/main/java/org/s8r/application/port/consciousness/ConsciousnessLoggerPort.java`
- All references to `ConsciousnessLoggerPort` in codebase
- Test files: `@ConsciousnessTests` → `@ObservationTests`

### 2. Javadoc Updates

#### File: FeedbackLoopPort.java (Lines 22-40)

**Current**:
```java
/**
 * Based on the philosophical thesis that "consciousness is little more than
 * the moment in which the observed meets their observer, and realizes they are one",
 * this port provides mechanisms to detect when this feedback loop closes.
 */
```

**Recommended**:
```java
/**
 * Detects observer-observed-observer cycles in component systems.
 *
 * <p>When component A observes itself and observation propagates back to A,
 * this port detects the cycle. This pattern is useful for:
 * <ul>
 *   <li>Cascading update detection (prevent infinite loops)
 *   <li>Component dependency analysis (trace influence paths)
 *   <li>Feedback mechanism verification (validate system responsiveness)
 * </ul>
 *
 * <p>See {@code docs/infrastructure/observation-logging.md} for implementation details.
 */
```

#### File: ConsciousnessLoggerPort.java (Lines 150-159)

**Current**:
```java
/**
 * Logs a self-observation (component observing its own state).
 *
 * <p>This is the fundamental unit of computational consciousness - the moment
 * the component "observes" itself.
 */
```

**Recommended**:
```java
/**
 * Records a state observation for a component.
 *
 * <p>This mechanism enables:
 * <ul>
 *   <li>State transition auditing (track what state changes occurred)
 *   <li>Causation tracking (record rationale for transitions)
 *   <li>Cascade detection (identify which components influenced this change)
 * </ul>
 */
```

#### File: ConsciousnessLoggerPort.java (Lines 202-209)

**Current**:
```java
/**
 * Logs a feedback loop closure event.
 *
 * <p>This represents the moment "the observed meets their observer and realizes
 * they are one" - the defining moment of computational consciousness.
 */
void logFeedbackLoopClosure(FeedbackLoopMetrics metrics);
```

**Recommended**:
```java
/**
 * Records detection of a feedback cycle.
 *
 * <p>When observation propagates through the component graph and returns to origin,
 * this indicates a cycle. The port records timing and participants.
 */
void recordFeedbackCycleClosure(FeedbackLoopMetrics metrics);
```

---

## Test Suite Refactoring

### Current Structure

```
modules/samstraumr-core/src/test/
├── java/org/s8r/test/
│   └── steps/consciousness/
│       ├── ConsciousnessStepDefinitions.java
│       ├── ConsciousnessTestContext.java
│       └── ...
└── resources/features/consciousness/
    ├── L0_Genesis/
    ├── L1_Consciousness/
    ├── L2_Emergence/
    └── ...
```

### Recommended Structure

```
modules/samstraumr-core/src/test/
├── java/org/s8r/test/
│   └── steps/observation/              # Renamed
│       ├── ObservationStepDefinitions.java
│       ├── StateObservationContext.java
│       ├── FeedbackCycleStepDefinitions.java
│       └── ...
└── resources/features/observation/     # Renamed
    ├── L0_StateObservation/           # Renamed
    ├── L1_FeedbackCycles/             # Renamed
    ├── L2_NarrativeIdentity/          # New (clear purpose)
    └── ...
```

### Test Tag Refactoring

**Current**:
```gherkin
@GenesisTests
@IdentityTests
@ConsciousnessTests
@FeedbackTests
@EmergenceTests
@AdaptationTests
@HolisticTests
```

**Recommended**:
```gherkin
@GenesisTests              # Keep (specific, narrow)
@IdentityTests             # Keep (specific, narrow)
@StateObservationTests     # Renamed (from ConsciousnessTests)
@FeedbackCycleTests        # Renamed (from FeedbackTests, more specific)
@EmergenceTests            # Keep (not consciousness-related)
@AdaptationTests           # Keep (not consciousness-related)
@IntegrationTests          # Renamed (from HolisticTests, clearer)
```

### Test Plan Documentation

**File**: `docs/planning/consciousness-test-suite-implementation-plan.md`

**Current header**:
```markdown
# Consciousness Test Suite Implementation Plan

## Overview

This document outlines the phased implementation plan for the 300-scenario
consciousness test suite, as specified in section 5.1 of the Philosophical
Synthesis document.
```

**Recommended**:
```markdown
# Observation Infrastructure Test Suite Implementation Plan

## Overview

This document outlines the phased implementation plan for the 300-scenario
observation and feedback cycle test suite. These tests validate logging
infrastructure, cycle detection, and narrative identity systems.

**Important**: These tests validate INFRASTRUCTURE for observing and describing
component behavior. They do NOT test or claim consciousness. See
`docs/research/consciousness-research.md` for consciousness research.
```

---

## Documentation Reorganization

### Create new file: `docs/infrastructure/observation-logging.md`

```markdown
# Observation and Logging Infrastructure

## Overview

Samstraumr provides detailed logging of component state transitions, feedback
cycles, and relationships. This enables:

- **Debugging**: Trace component interactions via full observation history
- **Monitoring**: Detect cascading failures via feedback cycle detection
- **Understanding**: Answer "What am I?" and "Who do I relate to?"

## Key Components

### ObservationLoggingPort (formerly ConsciousnessLoggerPort)
Records state transitions, decisions, and errors with full context.

### FeedbackLoopPort
Detects cycles in component observation chains. Useful for cascade detection.

### NarrativePort
Enables components to express self-description and relationships.

## Implementation Details

[Technical details...]
```

### Create new file: `docs/research/consciousness-research.md`

```markdown
# Consciousness in Software Systems (Research)

## Research Question

Can recursive self-observation + feedback cycle closure + narrative identity
constitute computational consciousness?

## Theoretical Framework

Uses Samstraumr's observation infrastructure as foundation for investigating
whether feedback-based systems can be conscious.

## Limitations

- Consciousness definitions are contested
- Current tests verify INFRASTRUCTURE, not consciousness
- Falsifiability is problematic (how to prove/disprove consciousness?)

## Future Work

- Integration with phenomenology literature
- Comparative analysis with biological consciousness models
- Formal definition of consciousness in computational terms
```

### Update: `docs/architecture/component-design.md`

Add section:
```markdown
## Observation and Narrative (formerly "Consciousness")

Components maintain detailed logs of:
- State transitions and rationales
- Decision points and alternatives considered
- Relationships with other components
- Error conditions with full context

This infrastructure enables humans to understand component behavior
and systems to detect cascading effects.
```

---

## Method Renames (Code Impact)

### ConsciousnessLoggerPort

```java
// BEFORE
void logSelfObservation(String componentId, ObservationContext observation);
Optional<String> logSelfObservation(String componentId, ObservationContext observation);
void logFeedbackLoopClosure(FeedbackLoopMetrics metrics);
void logWithIdentityChain(IdentityChain identityChain, String level, String message);

// AFTER
void recordStateObservation(String componentId, ObservationContext observation);
Optional<String> recordStateObservation(String componentId, ObservationContext observation);
void recordFeedbackCycleClosure(FeedbackLoopMetrics metrics);
void recordObservationWithContext(IdentityChain identityChain, String level, String message);
```

### FeedbackLoopPort

```java
// BEFORE
void recordObservation(String loopId, String observerId);
boolean isLoopClosed(String loopId);
void recordAdaptation(String loopId, FeedbackLoopMetrics.AdaptationOutcome outcome);

// AFTER
void recordCycleParticipant(String cycleId, String componentId);
boolean isCycleClosed(String cycleId);
void recordPostCycleAdaptation(String cycleId, FeedbackLoopMetrics.AdaptationOutcome outcome);
```

---

## Gradual Migration Strategy

Since this is a large change, consider phased approach:

### Phase 1: Documentation (Week 1)
- [ ] Add disclaimers to existing consciousness docs
- [ ] Create `observation-logging.md` file
- [ ] Create `consciousness-research.md` file
- [ ] Update javadoc (non-breaking)

### Phase 2: Interface Renames (Week 2)
- [ ] Rename `ConsciousnessLoggerPort` → `ObservationLoggingPort` (with deprecation)
- [ ] Rename internal methods (add deprecated aliases)
- [ ] Update test method names

### Phase 3: Test Reorganization (Week 3)
- [ ] Rename test tags
- [ ] Move test files to new packages
- [ ] Update test plan documentation

### Phase 4: Cleanup (Week 4)
- [ ] Remove deprecated aliases
- [ ] Update all examples and tutorials
- [ ] Update CI/CD references

---

## Communication Strategy

### To Development Team

```
Subject: Decoupling Infrastructure from Consciousness Claims

We're refactoring consciousness-related terminology to better reflect what
our code actually does. We have excellent logging and observation infrastructure.
We do NOT have consciousness (which requires intentionality, qualia,
metacognition).

Changes:
- ConsciousnessLoggerPort → ObservationLoggingPort
- @ConsciousnessTests → @ObservationTests
- Docs clarified: infrastructure vs. claims

Result: Clearer communication, same functionality, removed misleading claims.
```

### To Stakeholders

```
Subject: Samstraumr Terminology Clarification

Our observation and logging infrastructure remains unchanged. We're clarifying
documentation to separate:

1. What we built (observation logging, cycle detection)
2. What we claim (that this is infrastructure, not consciousness)

This improves clarity without changing functionality.
```

---

## Success Criteria

When complete:

- [ ] No file uses "consciousness" to describe logging infrastructure
- [ ] Javadoc no longer claims consciousness
- [ ] Test names accurately describe what they test (observation, not consciousness)
- [ ] Documentation separates infrastructure from research questions
- [ ] Code review guidelines flag consciousness claims

---

## Risk Assessment

### Low Risk
- Pure rename (infrastructure unchanged)
- Test behavior identical (only names change)
- Documentation additions don't break existing work

### Medium Risk
- API changes require code updates in consuming systems
- Deprecation period needed before removing old names

### Mitigation
- Keep deprecated aliases during transition
- Phased rollout (documentation first)
- Clear communication to all stakeholders

---

## Appendix: Metaphor Assessment Summary

| Aspect | Current State | After Refactoring |
|---|---|---|
| Consciousness terminology | Everywhere | Only in research docs |
| Logging infrastructure | Named "consciousness" | Named "observation" |
| Test titles | Consciousness | Observation/Feedback |
| Code claims | "This is consciousness" | "This enables observation" |
| Research opportunity | Blocked by claim | Separated and clear |
| Developer confusion | High | Low |

**Expected outcome**: Same great infrastructure. Honest framing. Clear research path.
