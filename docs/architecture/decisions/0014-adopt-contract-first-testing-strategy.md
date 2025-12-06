# 0014 Adopt Contract-First Testing Strategy

Date: 2025-12-06

## Status

Accepted

Supersedes: Partially supersedes ADR-0006 (sections on test distribution)

## Context

Samstraumr's Clean Architecture defines 23 port interfaces as boundaries between the application core and infrastructure. These interfaces are critical contracts that enable testability, modularity, and substitutability of external systems. However, our current test suite suffers from an inverted test pyramid:

**Current State (Problem)**:
- 73% E2E/System tests (should be <5%)
- 15% unit tests (should be >60%)
- Slow feedback loops, brittle tests, unclear boundaries

The Clean Architecture principle that "business rules can be tested without external dependencies" (ADR-0003) is not being honored when 73% of tests require full system assembly.

### Forces

- Port interfaces define critical boundaries (23 ports identified)
- Clean Architecture demands testability in isolation
- Mockito obscures intent and creates invisible coupling
- E2E tests should verify integration, not logic
- Test data creation is inconsistent across the codebase

## Decision

We adopt a **Contract-First Testing Strategy** that prioritizes contract verification at port boundaries using hand-rolled mock adapters and standardized test data builders.

### Target Test Distribution

| Level | Type | Target % | Tags |
|-------|------|----------|------|
| L0 | Unit + Contract | 60% | `@L0_Unit`, `@ATL` |
| L1 | Component | 25% | `@L1_Component`, `@ATL` |
| L2 | Integration | 10% | `@L2_Integration`, `@ATL` |
| L3/L4 | System/E2E | <5% | `@L3_System`, `@L4_Acceptance` |

### Core Principles

1. **Contract tests at every port boundary**
   - Each port interface gets a corresponding contract test
   - Contract tests verify interface contract, not implementation details

2. **Hand-rolled Mock Adapters over Mockito**
   - Explicit `MockXxxAdapter` classes for each port
   - Mock behavior is visible, debuggable, and self-documenting
   - Centralized via `MockAdapterFactory`

3. **TestDataFactory with Builder Pattern**
   - Centralized factory for all test fixtures
   - Builder pattern for complex domain objects
   - Immutable test data by default

4. **E2E Tests Limited to Acceptance Criteria**
   - E2E tests verify user-facing workflows only
   - Maximum 5% of total test count

### Package Organization

```
src/test/java/org/s8r/
├── adapter/contract/      # Contract tests for all ports
├── test/mock/             # Hand-rolled mock adapters + MockAdapterFactory
├── test/data/             # TestDataFactory + builders
└── test/unit/             # Unit tests for domain logic
```

## Consequences

### Positive

- Faster feedback (seconds vs minutes)
- Explicit, verified contracts for every port
- Debuggable tests with hand-rolled mocks
- Reduced flakiness from fewer E2E tests
- Living documentation via mock adapters
- Consistent fixtures via TestDataFactory

### Negative

- Initial investment creating mock adapters and contract tests
- More test code than Mockito annotations
- Learning curve for new patterns

### Mitigations

- Phase the migration; prioritize critical ports first
- Mock adapters are reusable; investment amortizes
- Contract tests fail when mocks drift from interface

## References

- ADR-0003: Adopt Clean Architecture for System Design
- ADR-0006: Implement Comprehensive Testing Pyramid Strategy (partially superseded)
