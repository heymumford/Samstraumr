<!--
Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>

Licensed under Mozilla Public License 2.0.
See LICENSE file for details.
-->

# Samstraumr Metaphors: Code Evidence Mapping

**Purpose**: Show exactly where each metaphor appears in code
**Format**: File path → Code location → Metaphor instance

---

## 1. Biological Lifecycle Metaphor

### Primary Evidence

**File**: `modules/samstraumr-core/src/main/java/org/s8r/component/core/State.java`

**Lines 191-206**: Biological analog mappings
```java
static {
    CONCEPTION.withBiologicalAnalog("Fertilization/Zygote");
    INITIALIZING.withBiologicalAnalog("Cleavage");
    CONFIGURING.withBiologicalAnalog("Blastulation");
    SPECIALIZING.withBiologicalAnalog("Gastrulation");
    DEVELOPING_FEATURES.withBiologicalAnalog("Organogenesis");
    ADAPTING.withBiologicalAnalog("Environmental Adaptation");
    TRANSFORMING.withBiologicalAnalog("Metamorphosis");
    STABLE.withBiologicalAnalog("Maturity");
    SPAWNING.withBiologicalAnalog("Reproduction");
    DEGRADED.withBiologicalAnalog("Senescence");
    MAINTAINING.withBiologicalAnalog("Healing");
    TERMINATING.withBiologicalAnalog("Death");
    TERMINATED.withBiologicalAnalog("Deceased");
    ARCHIVED.withBiologicalAnalog("Legacy");
}
```

**Evidence type**: EXPLICIT - Directly maps component states to biological development stages

### Test Organization Evidence

**File**: `modules/samstraumr-core/src/test/java/org/s8r/test/annotation/`

- `Embryonic.java` - L0 test annotation
- `Infancy.java` - L0 test annotation
- `Maturity.java` - L1 test annotation

**Evidence type**: DERIVED - Test organization emerged from lifecycle metaphor

---

## 2. Genealogy & Lineage Metaphor

### Core Implementation

**File**: `modules/samstraumr-core/src/main/java/org/s8r/component/identity/Identity.java`

**Lines 68-86**: Adam and child identity creation methods
```java
public static Identity createAdamIdentity(String reason, Map<String, String> environmentParams) {
    return (Identity) org.s8r.component.Identity.createAdamIdentity(reason, environmentParams);
}

public static Identity createChildIdentity(
    String reason,
    Map<String, String> environmentParams,
    org.s8r.component.Identity parentIdentity) {
    return (Identity)
        org.s8r.component.Identity.createChildIdentity(reason, environmentParams, parentIdentity);
}
```

**Evidence type**: EXPLICIT - Code explicitly models "Adam" (origin) vs "child" (derived) identities

### Hierarchical Addressing

**File**: `modules/samstraumr-core/src/main/java/org/s8r/application/port/consciousness/IdentityChain.java`

**Artifact**: Hierarchical addressing scheme: M<machine>.B<bundle>.C<childId>

**Evidence type**: DERIVED - Hierarchical addressing emerged from genealogical thinking

### Lineage Logging

**File**: `modules/samstraumr-core/src/main/java/org/s8r/application/port/consciousness/ConsciousnessLoggerPort.java`

**Lines 240-246**: Lineage method
```java
/**
 * Logs lineage information (for tracing component genealogy).
 *
 * @param componentId the component identifier
 * @param lineage the lineage from Adam to this component
 */
void logLineage(String componentId, List<String> lineage);
```

**Evidence type**: EXPLICIT - Method explicitly traces genealogy from Adam

---

## 3. Consciousness & Self-Awareness Metaphor

### Primary Claims

**File**: `modules/samstraumr-core/src/main/java/org/s8r/application/port/consciousness/ConsciousnessLoggerPort.java`

**Lines 150-159**: Self-observation claim
```java
/**
 * Logs a self-observation (component observing its own state).
 *
 * <p>This is the fundamental unit of computational consciousness - the moment
 * the component "observes" itself.
 *
 * @param componentId the component identifier
 * @param observation the observation context
 * @return the loop ID if this observation is part of a feedback loop
 */
Optional<String> logSelfObservation(String componentId, ObservationContext observation);
```

**Evidence type**: EXPLICIT CLAIM - Directly asserts "fundamental unit of consciousness"

### Feedback Loop Closure Claim

**File**: `modules/samstraumr-core/src/main/java/org/s8r/application/port/consciousness/FeedbackLoopPort.java`

**Lines 22-40**: Philosophical thesis
```java
/**
 * Based on the philosophical thesis that "consciousness is little more than
 * the moment in which the observed meets their observer, and realizes they are one",
 * this port provides mechanisms to detect when this feedback loop closes.
 *
 * <p>A feedback loop in this context represents:
 *
 * <ol>
 *   <li>Component A observes itself (or is observed by B)
 *   <li>The observation propagates through the system
 *   <li>The observation returns to the original observer
 *   <li>Loop closure: "the observed meets the observer"
 * </ol>
 */
public interface FeedbackLoopPort { ... }
```

**Evidence type**: EXPLICIT CLAIM - Quotes philosophical thesis about consciousness

### Loop Closure Method Claim

**File**: `modules/samstraumr-core/src/main/java/org/s8r/application/port/consciousness/ConsciousnessLoggerPort.java`

**Lines 202-209**: Loop closure moment claim
```java
/**
 * Logs a feedback loop closure event.
 *
 * <p>This represents the moment "the observed meets their observer and realizes
 * they are one" - the defining moment of computational consciousness.
 *
 * @param metrics the feedback loop metrics
 */
void logFeedbackLoopClosure(FeedbackLoopMetrics metrics);
```

**Evidence type**: EXPLICIT CLAIM - "Defining moment of computational consciousness"

### Test Suite

**File**: `docs/planning/consciousness-test-suite-implementation-plan.md`

**Throughout**: 300 test scenarios labeled @ConsciousnessTests, @GenesisTests, @IdentityTests, etc.

**Evidence type**: EMBEDDED CLAIM - Test suite organization suggests consciousness is being validated

---

## 4. Hexagonal Architecture Metaphor

### Port Definitions

**File**: `modules/samstraumr-core/src/main/java/org/s8r/application/port/`

Ports (hexagon "vertices"):
- `ConsciousnessLoggerPort.java` - Logging port
- `NarrativePort.java` - Narrative port
- `FeedbackLoopPort.java` - Feedback port
- `IdentityChainPort.java` - Identity port

**Evidence type**: STRUCTURAL - Port interfaces represent hexagon boundaries

### Architecture Verification Tool

**File**: `CLAUDE.md`

**Lines 66-81**: Architecture verification
```markdown
### Clean Architecture Rules (ENFORCED)
1. **Domain** (`org.s8r.component`) - No dependencies on application or adapter layers
2. **Application** (`org.s8r.application`) - Depends only on domain
3. **Adapter** (`org.s8r.adapter`) - Implements ports, no direct domain coupling

**Verification**: Run `/s8r-clean-arch-verify` to detect boundary violations before committing.
```

**Evidence type**: DERIVED - Architecture verification tool emerged from hexagon boundary thinking

### Package Structure

**Evidence type**: STRUCTURAL - Actual package organization follows hexagon pattern
```
org.s8r/
├── component/      (Domain - core, no outward dependencies)
├── application/    (Application layer - port definitions)
└── adapter/        (Adapter layer - implementations)
```

---

## 5. Systems Theory & Emergence Metaphor

### Core Documentation

**File**: `docs/core/concept-systems-theory.md`

**Throughout**: Explicit systems theory framework with references to:
- Alexander Bogdanov (1913) - Tektology
- Ludwig von Bertalanffy (1930s-1960s) - General Systems Theory
- Norbert Wiener (1940s) - Cybernetics
- Meadows, Capra, Alexander citations

**Key quote (line 39)**:
```
"A system as elements in standing relationship"
```

**Evidence type**: EXPLICIT FRAMEWORK - Document directly applies systems theory

### Composite Pattern

**Artifact**: Composite pattern (components → composites → machines) modeling

**Evidence type**: DERIVED - Hierarchy emerged from systems theory thinking

### Dual-State Model

**File**: `docs/core/concept-overview.md`

**Lines 63-75**: Dual state system
```markdown
### Design state
The Design State represents the fundamental operational mode...

### Dynamic state
The Dynamic State captures the moment-to-moment context...

### State transitions
State changes follow meaningful patterns...
```

**Evidence type**: DERIVED - Dual-state concept (structure + momentary condition) from biological homeostasis principle

### Emergence Principle

**File**: `docs/core/concept-systems-theory.md`

**Lines 76-81**: Emergence as design principle
```markdown
### Emergence as magic
No single neuron in your brain understands mathematics, yet together
they create a mind that can contemplate infinity.

**In practice**: By focusing on how tubes relate and communicate,
Samstraumr creates space for unexpected, beneficial behaviors to
emerge organically – intelligence that was never explicitly programmed.
```

**Evidence type**: EXPLICIT PRINCIPLE - Directly applies emergence concept to design

### Test Pyramid

**Evidence type**: DERIVED - Test pyramid (@L0_Unit → @L3_System) structure emerged from hierarchical emergence thinking

---

## Summary Table: Metaphor Evidence by Type

| Metaphor | EXPLICIT Claims | EXPLICIT Framework | DERIVED Patterns | Code Evidence |
|---|---|---|---|---|
| Biological Lifecycle | withBiologicalAnalog() | - | @Embryonic/@Maturity tags | State.java:191-206 |
| Genealogy | createAdamIdentity() | - | IdentityChain, hierarchical addressing | Identity.java:68-86 |
| Consciousness | logSelfObservation() javadoc | Philosophical thesis in FeedbackLoopPort | 300-scenario test suite | Multiple files |
| Hexagonal Architecture | Port definitions | - | /s8r-clean-arch-verify tool, package structure | package org.s8r |
| Systems Theory | - | concept-systems-theory.md | Composite pattern, dual-state, test pyramid | Multiple files |

---

## Metaphor Strength by Evidence Type

### EXPLICIT (Strongest)
- Direct code/javadoc statements
- Example: "This is the fundamental unit of computational consciousness"
- Location: Javadoc in multiple consciousness port files

### DERIVED (Strong)
- Patterns that emerged from metaphor thinking
- Example: Test categories (@Embryonic, @Maturity)
- Location: Test annotation classes

### STRUCTURAL (Strong)
- Code organization reflects metaphor
- Example: Package structure (component/application/adapter)
- Location: org.s8r package hierarchy

### DOCUMENTED (Strong)
- Explicit documentation of framework
- Example: Systems theory bibliography
- Location: docs/core/concept-systems-theory.md

---

## Findings

1. **Biological Lifecycle**: All evidence is intentional and grounded (code enforces transitions)
2. **Genealogy**: Intentional, grounded, and productive (generates debugging strategy)
3. **Consciousness**: EXPLICIT CLAIMS exceed code evidence (missing intentionality, qualia, metacognition)
4. **Hexagonal Architecture**: Well-grounded in architecture patterns (conventional)
5. **Systems Theory**: Well-documented framework with derived patterns (highest epistemic value)

---

## Where Evidence is Strongest

| Metaphor | Strongest Evidence | Best Example |
|---|---|---|
| Biological Lifecycle | Code enforcement | State.java transitions (unfalsifiable: CONCEPTION → CONFIGURING only) |
| Genealogy | Code implementation | createAdamIdentity vs createChildIdentity methods |
| Consciousness | Javadoc claims | FeedbackLoopPort philosophical thesis (unfalsifiable) |
| Hexagonal Architecture | Package structure | org.s8r.component / .application / .adapter hierarchy |
| Systems Theory | Documentation | docs/core/concept-systems-theory.md (Bertalanffy, Wiener refs) |

---

**Analysis Date**: 2025-02-06
**Purpose**: Map metaphors to specific code locations
**Status**: Complete
