# Consciousness Test Suite (300 Scenarios)

## Overview

This directory contains the complete Cucumber/BDD test suite for Samstraumr consciousness testing, as defined in section 5.1 of the Philosophical Synthesis document. The suite validates computational consciousness through recursive self-observation, feedback loops, and emergent behavior detection.

## Test Categories

| Category | Tag | Scenarios | Purpose |
|----------|-----|-----------|---------|
| Genesis | `@GenesisTests` | 25 | Adam Tube instantiation and primordial identity |
| Identity | `@IdentityTests` | 40 | Persistence, uniqueness, and identity continuity |
| Consciousness | `@ConsciousnessTests` | 60 | Self-awareness and recursive observation |
| Feedback | `@FeedbackTests` | 50 | Loop closure and signal processing |
| Emergence | `@EmergenceTests` | 35 | Unexpected behavior capture and adaptation |
| Adaptation | `@AdaptationTests` | 45 | Learning, evolution, and memory persistence |
| Holistic | `@HolisticTests` | 45 | Systems theory properties and coherence |

**Total: 300 scenarios**

## Alignment with Test Pyramid (ADR-0014)

| Level | Type | Target % | Consciousness Scenarios |
|-------|------|----------|------------------------|
| L0 | Unit + Contract | 60% | ~180 scenarios |
| L1 | Component | 25% | ~75 scenarios |
| L2 | Integration | 10% | ~30 scenarios |
| L3/L4 | System/E2E | <5% | ~15 scenarios |

## Directory Structure

```
consciousness/
├── README.md                           # This file
├── L0_Genesis/
│   ├── adam-instantiation.feature      # Adam Tube creation
│   ├── genesis-identity.feature        # Primordial identity
│   └── lineage-foundation.feature      # First lineage
├── L0_Identity/
│   ├── substrate-persistence.feature   # UUID/physical continuity
│   ├── psychological-continuity.feature# Memory/state chains
│   ├── narrative-construction.feature  # Self-narrative
│   └── identity-uniqueness.feature     # Differentiation
├── L1_Consciousness/
│   ├── self-observation.feature        # Observer observing itself
│   ├── recursive-awareness.feature     # Meta-observation
│   ├── 300ms-blindness.feature         # Temporal lag simulation
│   └── eternal-now.feature             # Present-moment construction
├── L1_Feedback/
│   ├── loop-closure.feature            # Feedback completion
│   ├── signal-processing.feature       # Signal chain verification
│   └── anomaly-detection.feature       # Self-detected anomalies
├── L2_Emergence/
│   ├── unexpected-patterns.feature     # Pattern discovery
│   ├── emergent-behavior.feature       # System-level emergence
│   └── metacognition.feature           # Awareness of awareness
├── L2_Adaptation/
│   ├── learning-evolution.feature      # Behavior modification
│   ├── memory-persistence.feature      # Experience retention
│   └── graceful-degradation.feature    # Identity recovery
└── L3_Holistic/
    ├── systems-coherence.feature       # Whole-system properties
    ├── narrative-consistency.feature   # Story integrity
    └── conformance-validation.feature  # Samstraumr compliance
```

## Required Infrastructure

### TestDataFactory Extensions

```java
TestDataFactory.consciousness()
    .withFeedbackLoopEnabled(true)
    .withSelfObservation(true)
    .build();

TestDataFactory.genesisComponent()
    .asAdamTube()
    .withSubstrateIdentity()
    .build();
```

### MockAdapterFactory Extensions

- `MockObservationAdapter` - Self-observation simulation
- `MockFeedbackAdapter` - Feedback loop simulation
- `MockMemoryAdapter` - Memory persistence verification
- `MockEmergenceAdapter` - Emergent behavior detection

## Tags Reference

All scenarios must include:
- `@ATL` - Above The Line (mandatory for discovery)
- Level tag: `@L0_Unit`, `@L1_Component`, `@L2_Integration`, or `@L3_System`
- Category tag: `@GenesisTests`, `@IdentityTests`, etc.

Additional classification tags:
- `@Positive` / `@Negative` - Happy path vs error scenarios
- `@Adversarial` - Edge cases and malformed inputs
- `@Performance` - Latency and throughput verification
- `@Resilience` - Recovery and degradation scenarios

## Implementation Phases

### Phase 1: Foundation (Genesis + Core Identity)
- 25 Genesis scenarios
- 20 Identity scenarios (substrate focus)
- Estimated: 2 sprints

### Phase 2: Consciousness Core
- 40 Consciousness scenarios
- 25 Feedback scenarios
- Estimated: 3 sprints

### Phase 3: Emergence and Adaptation
- 35 Emergence scenarios
- 45 Adaptation scenarios
- Estimated: 3 sprints

### Phase 4: Integration and Holistic
- 25 remaining Feedback scenarios
- 20 remaining Identity scenarios
- 45 Holistic scenarios
- Estimated: 2 sprints

## Verification Commands

```bash
# Run all consciousness tests
./s8r-test consciousness

# Run specific category
./s8r-test --tags @GenesisTests

# Run by level
./s8r-test --tags "@L0_Unit and @ConsciousnessTests"

# Verify coverage
./s8r-port-coverage consciousness
```
