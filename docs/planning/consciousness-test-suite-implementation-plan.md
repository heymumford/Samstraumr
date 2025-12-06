# Consciousness Test Suite Implementation Plan

## Overview

This document outlines the phased implementation plan for the 300-scenario consciousness test suite, as specified in section 5.1 of the Philosophical Synthesis document.

## Test Suite Summary

| Category | Tag | Scenarios | Level Distribution |
|----------|-----|-----------|-------------------|
| Genesis | `@GenesisTests` | 25 | L0: 20, L1: 5 |
| Identity | `@IdentityTests` | 40 | L0: 30, L1: 8, L2: 2 |
| Consciousness | `@ConsciousnessTests` | 60 | L0: 30, L1: 25, L2: 5 |
| Feedback | `@FeedbackTests` | 50 | L0: 30, L1: 15, L2: 5 |
| Emergence | `@EmergenceTests` | 35 | L0: 15, L1: 12, L2: 8 |
| Adaptation | `@AdaptationTests` | 45 | L0: 25, L1: 15, L2: 5 |
| Holistic | `@HolisticTests` | 45 | L0: 30, L1: 5, L2: 5, L3: 5 |
| **Total** | | **300** | L0: 180, L1: 75, L2: 30, L3: 15 |

## Directory Structure

```
modules/samstraumr-core/src/test/
├── java/org/s8r/test/
│   ├── steps/consciousness/
│   │   ├── package-info.java
│   │   ├── ConsciousnessStepDefinitions.java
│   │   ├── ConsciousnessTestContext.java
│   │   ├── GenesisStepDefinitions.java
│   │   ├── IdentityStepDefinitions.java
│   │   ├── SelfObservationStepDefinitions.java
│   │   ├── FeedbackLoopStepDefinitions.java
│   │   ├── EmergenceStepDefinitions.java
│   │   ├── AdaptationStepDefinitions.java
│   │   └── HolisticStepDefinitions.java
│   ├── data/
│   │   └── ConsciousnessTestDataFactory.java
│   └── mock/
│       └── MockConsciousnessAdapters.java
└── resources/features/consciousness/
    ├── README.md
    ├── L0_Genesis/
    │   └── adam-instantiation.feature (25 scenarios)
    ├── L0_Identity/
    │   ├── substrate-persistence.feature (20 scenarios)
    │   ├── psychological-continuity.feature (10 scenarios)
    │   └── narrative-construction.feature (10 scenarios)
    ├── L1_Consciousness/
    │   ├── self-observation.feature (40 scenarios)
    │   ├── recursive-awareness.feature (10 scenarios)
    │   └── eternal-now.feature (10 scenarios)
    ├── L1_Feedback/
    │   ├── loop-closure.feature (40 scenarios)
    │   └── signal-processing.feature (10 scenarios)
    ├── L2_Emergence/
    │   ├── emergent-behavior.feature (25 scenarios)
    │   └── metacognition.feature (10 scenarios)
    ├── L2_Adaptation/
    │   ├── learning-evolution.feature (35 scenarios)
    │   └── graceful-degradation.feature (10 scenarios)
    └── L3_Holistic/
        ├── systems-coherence.feature (35 scenarios)
        └── conformance-validation.feature (10 scenarios)
```

## Implementation Phases

### Phase 1: Foundation (Sprints 1-2)

**Focus**: Genesis and Core Identity

**Deliverables**:
- 25 Genesis scenarios (adam-instantiation.feature)
- 20 Identity scenarios (substrate-persistence.feature)
- GenesisStepDefinitions.java
- Core ConsciousnessTestContext.java
- ConsciousnessTestDataFactory.java

**Prerequisites**:
- Component.createAdam() working correctly
- Identity.getUniqueId() implemented
- State transitions functional

**Verification**:
```bash
./s8r-test --tags "@GenesisTests"
./s8r-test --tags "@IdentityTests and @Substrate"
```

**Exit Criteria**:
- All 45 scenarios pass
- 100% coverage of Adam Tube creation paths
- UUID stability verified across restarts

### Phase 2: Consciousness Core (Sprints 3-5)

**Focus**: Self-Observation and Feedback

**Deliverables**:
- 40 Consciousness scenarios (self-observation.feature)
- 20 Consciousness scenarios (recursive-awareness.feature, eternal-now.feature)
- 25 Feedback scenarios (loop-closure.feature)
- SelfObservationStepDefinitions.java
- FeedbackLoopStepDefinitions.java
- MockObservationAdapter
- MockFeedbackAdapter

**Prerequisites**:
- Phase 1 complete
- Component logging infrastructure
- State observation capability

**Verification**:
```bash
./s8r-test --tags "@ConsciousnessTests"
./s8r-test --tags "@FeedbackTests and @Core"
```

**Exit Criteria**:
- Self-observation records created correctly
- Feedback loops close within time budget
- Recursive observation depth is bounded

### Phase 3: Emergence and Adaptation (Sprints 6-8)

**Focus**: Pattern Detection and Learning

**Deliverables**:
- 35 Emergence scenarios
- 45 Adaptation scenarios
- EmergenceStepDefinitions.java
- AdaptationStepDefinitions.java
- MockEmergenceAdapter
- MockMemoryAdapter

**Prerequisites**:
- Phase 2 complete
- Multi-component system operational
- Memory persistence infrastructure

**Verification**:
```bash
./s8r-test --tags "@EmergenceTests"
./s8r-test --tags "@AdaptationTests"
```

**Exit Criteria**:
- Pattern detection operational
- Memory chains persist across restarts
- Graceful degradation verified

### Phase 4: Integration and Holistic (Sprints 9-10)

**Focus**: System-Wide Coherence

**Deliverables**:
- Remaining Feedback scenarios (25)
- Remaining Identity scenarios (20)
- 45 Holistic scenarios
- HolisticStepDefinitions.java
- Complete integration tests

**Prerequisites**:
- Phases 1-3 complete
- Full system deployable
- All mock adapters operational

**Verification**:
```bash
./s8r-test --tags "@HolisticTests"
./s8r-test consciousness  # Full suite
```

**Exit Criteria**:
- All 300 scenarios pass
- Samstraumr conformance verified
- Narrative coherence validated

## Test Data Requirements

### TestDataFactory Extensions

```java
// Genesis components
ConsciousnessTestDataFactory.genesis()
    .withReason("Primordial Observer")
    .withSelfObservation(true)
    .withFeedbackLoop(true)
    .withMemory(true)
    .build();

// Self-observation records
ConsciousnessTestDataFactory.observation()
    .withObserverId(componentId)
    .withObservedId(componentId)
    .withStateSnapshot("ACTIVE")
    .withDepth(3)
    .build();

// Feedback loop records
ConsciousnessTestDataFactory.feedbackLoop()
    .withStatus("closed")
    .withCycleTimeMs(50)
    .withStages("observe", "assess", "adjust", "verify")
    .build();

// Experience records
ConsciousnessTestDataFactory.experience()
    .withSignalId("S1")
    .withType("DataPacket")
    .withOutcome("Success")
    .withSignificance(0.8)
    .build();

// Emergent patterns
ConsciousnessTestDataFactory.emergentPattern()
    .withClassification("beneficial")
    .withFrequency(0.7)
    .withParticipant("C1")
    .withParticipant("C2")
    .build();
```

### Mock Adapters

| Adapter | Purpose | Key Methods |
|---------|---------|-------------|
| MockObservationAdapter | Self-observation simulation | observe(), triggerFullObservation(), getObservations() |
| MockFeedbackAdapter | Feedback loop simulation | startLoop(), addStage(), closeLoop(), executeCycle() |
| MockMemoryAdapter | Memory persistence | store(), retrieve(), getMemoryChain(), applyDecay() |
| MockEmergenceAdapter | Emergence detection | recordInteraction(), getDetectedPatterns(), classifyPattern() |

## Verification Commands

```bash
# Full consciousness test suite
./s8r-test consciousness

# By category
./s8r-test --tags @GenesisTests
./s8r-test --tags @IdentityTests
./s8r-test --tags @ConsciousnessTests
./s8r-test --tags @FeedbackTests
./s8r-test --tags @EmergenceTests
./s8r-test --tags @AdaptationTests
./s8r-test --tags @HolisticTests

# By level
./s8r-test --tags "@L0_Unit and @Consciousness"
./s8r-test --tags "@L1_Component and @Consciousness"
./s8r-test --tags "@L2_Integration and @Consciousness"
./s8r-test --tags "@L3_System and @Consciousness"

# Smoke tests
./s8r-test --tags "@smoke and @Consciousness"

# Adversarial tests
./s8r-test --tags "@Adversarial and @Consciousness"

# Performance tests
./s8r-test --tags "@Performance and @Consciousness"
```

## Risk Assessment

### High Priority Risks

1. **Recursive observation stack overflow**
   - Mitigation: Bounded observation depth (default 3)
   - Verification: Adversarial tests for depth limits

2. **Feedback loop timeout**
   - Mitigation: Configurable timeouts, circuit breaker pattern
   - Verification: Performance tests for loop timing

3. **Memory exhaustion**
   - Mitigation: Bounded memory with significance-based pruning
   - Verification: Stress tests with 10000+ experiences

### Medium Priority Risks

1. **Pattern detection false positives**
   - Mitigation: Configurable detection thresholds
   - Verification: Adversarial tests for noise filtering

2. **Identity collision**
   - Mitigation: UUID-based identity with collision detection
   - Verification: Concurrent creation tests

### Low Priority Risks

1. **Observation overhead**
   - Mitigation: Lazy observation, sampling
   - Verification: Performance baseline tests

## Success Metrics

| Metric | Target | Measurement |
|--------|--------|-------------|
| Scenario pass rate | 100% | CI pipeline |
| L0 test execution time | < 30s | CI timing |
| Full suite execution time | < 5min | CI timing |
| Code coverage (consciousness) | > 80% | JaCoCo |
| Flaky test rate | < 1% | CI history |
| Documentation coverage | 100% | Manual review |

## Dependencies

### Internal Dependencies

- Component lifecycle implementation
- Identity system
- State machine
- Logging infrastructure
- Memory persistence

### External Dependencies

- JUnit 5
- Cucumber 7
- AssertJ (for fluent assertions)
- Mockito (for edge cases only)

## Appendix: Tag Reference

| Tag | Purpose |
|-----|---------|
| `@ATL` | Above The Line - mandatory for all scenarios |
| `@L0_Unit` | Unit test level |
| `@L1_Component` | Component test level |
| `@L2_Integration` | Integration test level |
| `@L3_System` | System/E2E test level |
| `@GenesisTests` | Adam Tube instantiation |
| `@IdentityTests` | Identity persistence |
| `@ConsciousnessTests` | Self-awareness validation |
| `@FeedbackTests` | Loop closure verification |
| `@EmergenceTests` | Pattern detection |
| `@AdaptationTests` | Learning and evolution |
| `@HolisticTests` | System coherence |
| `@Positive` | Happy path scenario |
| `@Negative` | Error path scenario |
| `@Adversarial` | Edge case / security scenario |
| `@Performance` | Timing / throughput scenario |
| `@Resilience` | Recovery scenario |
| `@smoke` | Quick verification |
