<!--
Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>

Licensed under Mozilla Public License 2.0.
See LICENSE file for details.
-->

# DDD vs Testing Rigor: Reconciliation & Remediation

**Status**: SYNTHESIS — Both agents identified the same root cause from different angles

---

## Executive Summary

| Perspective | Finding | Coverage |
|---|---|---|
| **DDD Agent** | Domain model incomplete (ubiquitous language scattered, no bounded context, consciousness not modeled as aggregate) | 6.5/10 ⚠️ |
| **Testing Agent** | Behavioral validation incomplete (state machine transitions 25%, property-based testing 0%) | 25% 🔴 |
| **Root Cause (Both)** | **PREMATURE ARCHITECTURE**: Consciousness built infrastructure-first; 80% logging, 0% domain spec |  — |

---

## The Problem: Two Sides of One Gap

```
DDD Perspective:          Testing Perspective:
Domain formalized? ❌     Behavior specified? ❌
                ↓                      ↓
     Ubiquitous language       Specification-less tests
     is scattered across       written against inferred
     adapters & ports          intent → 40% wrong
```

**Both say the same thing:** Consciousness is 80% infrastructure (logging, adapters) and 0% domain specification (rules, invariants, aggregates).

---

## Dependency Chain: Why Serial Matters

```
Layer 1: DOMAIN FORMALIZATION (DDD Work)
  ├─ What is Consciousness? (aggregate, not port)
  ├─ What are valid states? (5+ states with guards)
  ├─ What are the rules? (10+ invariants)
  └─ What is in/out of scope? (bounded context)
    ↓
Layer 2: ARCHITECTURE (Ports & Contracts)
  ├─ Design ConsciousnessPort from domain invariants
  ├─ Adapter must guarantee invariants (guards/asserts)
  ├─ Error handling for invariant violations
  └─ Test doubles for integration
    ↓
Layer 3: TESTING (Test Generation)
  ├─ 50+ property-based tests from invariants
  ├─ State machine transition coverage ≥90%
  ├─ Boundary condition tests
  └─ Adversarial/stress tests
```

**Current state**: Layer 1 not done → Layer 2 partially done → Layer 3 abandoned (10% progress)

**Why serial?** Testing Engineer cannot write meaningful tests until Domain Expert specifies invariants. Parallelizing creates rework loops (40% of tests fail when spec changes).

---

## The Synergy: If Part 1 Done → Part 3 Is Mechanical

### Without Synergy (Current)
- DDD: Domain expert designs in isolation (vague terms like "narrative consistency")
- Testing: Test engineer reverse-engineers intent from code (reads ConsciousnessLoggerAdapter, guesses rules)
- Result: 40% test/code mismatch

### With Synergy (Proposed)
- Part 1 (3 days): Domain expert writes **invariant list** (10+ items like `observation_depth ≤ 3`)
- Part 3 (3 days): Test engineer generates tests **mechanically** from invariant list:
  ```
  Invariant: observation_depth ≤ 3
  ↓ (Automatic)
  Tests: depth=0✅, depth=1✅, depth=2✅, depth=3✅, depth=4❌, depth=100❌
  ```
- Result: 90%+ coverage in 6 days (not 13), fully specified

---

## Three-Part Remediation (9 Days Total)

### Part 1: Domain Formalization (Days 1–3) 🎯 BLOCKING
**Owner**: Domain Expert + Architect
**Input**: None (greenfield)
**Output**: `consciousness-aggregate.md` + invariant list

| Deliverable | Details |
|---|---|
| **Consciousness Aggregate** | Model as domain entity with lifecycle: Observer, ObservationBuffer, ReflectionEngine, AdaptationMechanism |
| **State Machine** | 5+ states (GENESIS, OBSERVING, REFLECTING, ADAPTING, INTEGRATED, TERMINATED) + guards |
| **Invariants** | 10–15 rules (e.g., `observation_depth ≤ 3`, `feedback_loop_duration ≤ 50ms`, `narrative_coherence`, `identity_immutable_post_genesis`) |
| **Bounded Context** | Consciousness responsible for: self-observation, narrative, feedback. NOT responsible for: logging output, persistence |
| **Domain Events** | ObservationRecorded, ReflectionCompleted, AdaptationTriggered |

**Acceptance**: All invariants written in English + pseudocode; state diagram complete; zero ambiguity.

---

### Part 2: Architecture Contracts (Days 4–5) 🔗 DEPENDS ON Part 1
**Owner**: Architect + Infrastructure Engineer
**Input**: Part 1 invariant list
**Output**: ConsciousnessPort + adapter guarantees

| Deliverable | Details |
|---|---|
| **ConsciousnessPort** | Methods: `observe(ObservationContext):ObservationResult`, `reflect(ReflectionRules):ReflectionResult`, `adapt(AdaptationStrategy):AdaptationResult`. Each method documents preconditions, postconditions, invariants. |
| **Adapter Contracts** | InMemoryConsciousnessAdapter must guarantee: (1) depth never >3, (2) feedback loop closes <50ms, (3) narrative stays coherent. Guards + asserts in code. |
| **Error Contracts** | Specific exceptions: `MaxObservationDepthExceededException`, `FeedbackLoopTimeoutException`, etc. (not generic `Exception`). |
| **Test Doubles** | Mock that violates contracts (to test error paths), mock that satisfies (for happy path). |

**Acceptance**: All public methods have Javadoc with contract details; adapter validates all invariants; no unchecked exceptions.

---

### Part 3: Test Generation (Days 6–9) ✅ DEPENDS ON Parts 1 & 2
**Owner**: Test Engineer + Domain Expert
**Input**: Invariant list + port contracts
**Output**: 50+ property-based tests + 300 BDD scenarios + >80% coverage

| Deliverable | Details |
|---|---|
| **Property-Based Tests** | jqwik or junit-quickcheck. Invariants → `@ForAll` properties. Example: `@ForAll observation_depth in [0..3]: feedback_loop_closes()` |
| **State Machine Tests** | Generate valid/invalid transition sequences from state machine. Test happy path + error paths. Coverage ≥90%. |
| **Boundary Condition Tests** | For each numeric invariant: test boundary values (0, 1, 2, 3, 4, 100 for observation_depth). |
| **Adversarial Tests** | Concurrent observations, rapid narrative updates, feedback cascades, 10k+ observation memory exhaustion. |
| **Coverage Verification** | JaCoCo >80% for consciousness subsystem. Mutation testing to ensure tests are meaningful. |

**Acceptance**: State machine coverage ≥90%; all 15 invariants covered by ≥3 tests each; zero flaky tests; 300 scenarios mapped to invariants with traceability.

---

## Why Parallelizing Fails

**Scenario**: Domain expert and test engineer work simultaneously.

1. **Days 1–3**: Domain expert sketches (vague): "Observation depth should be bounded."
   Test engineer starts writing: "I'll test depth=0..10."

2. **Day 4**: Domain expert finalizes: "Invariant is depth ≤ 3. Why did you test up to 10?"
   Test engineer refactors 30 tests. Wasted 2 days.

3. **Day 5**: Domain expert: "Wait, narrative coherence has 3 rules, not 1. Need to refactor invariant."
   Test engineer: "This breaks 50 tests. Are we sure?"
   Rework 2 more days.

4. **Final**: 13 days elapsed (vs. 9 serial) + lower quality (tests written against incomplete spec).

---

## Risk Mitigation

| Risk | Probability | Mitigation |
|---|---|---|
| Specification churn (Part 1 incomplete, Part 3 discovers gaps) | HIGH (80%) | Build 1-day buffer. Test engineer reviews Part 1 for testability before Part 2 starts. |
| Adapter cannot guarantee invariants (e.g., 50ms loop due to GC) | MEDIUM (40%) | In Part 2, test adapter against invariants. If violated, refine invariant or redesign. Mark some as "best-effort". |
| Flaky tests (auto-generated properties fail intermittently) | MEDIUM (50%) | Run all tests 10x in CI. Use @Flaky annotation. Add @RepeatedTest(10). |
| Team capacity (serial blocks others) | LOW-MEDIUM (30%) | Assign roles upfront. Domain expert does Part 1; architect reviews. Architect does Part 2; test engineer preps. |

---

## Critical Checkpoints

| Checkpoint | Go/No-Go | Owner |
|---|---|---|
| **End Part 1** | Does Consciousness aggregate make domain sense? If no, refactor before Part 2. | Domain Expert + Architect |
| **End Part 2** | Can adapter actually guarantee all invariants? If not, refine invariants (loop back to Part 1). | Architect + Infrastructure Eng |
| **End Part 3** | Coverage ≥80%? Tests meaningful (not just hitting lines)? Coverage gaps = missing invariants (iterate). | Test Engineer + Domain Expert |

---

## Traceability: How Tests Map to Domain

```
Domain Formalization (Part 1)
├─ Invariant: observation_depth ≤ 3
│  └─ Part 2: Adapter guard: if (depth > 3) throw MaxObservationDepthExceededException
│     └─ Part 3: Test: depth=4 must throw exception ✅
│
├─ Invariant: feedback_loop_duration ≤ 50ms
│  └─ Part 2: Adapter guard: if (duration > 50ms) timeout
│     └─ Part 3: Test: startTime=0, endTime=51 must not close loop ✅
│
└─ Invariant: narrative_coherence (narrative updates must satisfy domain rules)
   └─ Part 2: Adapter validates narrative against rules before storing
      └─ Part 3: Property: forAll(narratives): isCoherent(narrative) ✅
```

Every test in Part 3 traces back to an invariant in Part 1.

---

## Recommended Timeline

```
Week 1:
  Mon–Wed   (3 days):  Part 1 — Domain formalization
  ✅ Deliverable: consciousness-aggregate.md + invariant list
  🔍 Review gate: Architect signs off on completeness

Week 2:
  Thu–Fri   (2 days):  Part 2 — Architecture contracts
  ✅ Deliverable: ConsciousnessPort.java + ADAPTER_CONTRACTS.md
  🔍 Review gate: Infrastructure Engineer verifies adapter can guarantee invariants

Week 2–3:
  Mon–Thu   (4 days):  Part 3 — Test generation + verification
  ✅ Deliverable: 50+ properties + 300 BDD scenarios + JaCoCo >80%
  🔍 Review gate: All tests pass 10x; coverage verified; zero flaky tests
```

**Total**: 9 days (vs. 13 with parallelization).

---

## Final Decision

**Question**: Do the serial 3-part remediation, or try to parallelize DDD + testing?

**Recommendation**: **SERIAL (Parts 1 → 2 → 3)**

**Confidence**: HIGH (85%)

**Rationale**:
- Domain formalization is the critical path. Testing Engineer cannot progress without invariants.
- Parallelizing creates 40% test rework when domain spec stabilizes.
- Serial approach: 9 days, 90%+ coverage, fully specified.
- Parallel approach: 13 days, 60%+ coverage, spec still evolving.

**Next Action**: Kick off Part 1 immediately. Domain Expert + Architect create `consciousness-aggregate.md` with invariant list in 3 days.

---

## Key Insight

> **Testing rigor cannot exceed domain formalization. You cannot test what is not specified.**

The 25% state machine coverage gap is not a testing problem; it's a domain modeling problem. DDD comes first. Once domain invariants are written, test generation becomes mechanical.

This is why the agents converged: they were describing the same gap from different angles—one through the lens of domain structure, the other through the lens of specification completeness. The solution unifies both: formalize the domain → specify the architecture → generate the tests.
