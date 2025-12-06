# Samstraumr Testing Standards

This document defines the authoritative testing standards for Samstraumr. See ADR-0014 for rationale.

## Test Pyramid Targets

| Level | Type | Target % | Tags |
|-------|------|----------|------|
| L0 | Unit + Contract | 60% | `@L0_Unit`, `@ATL` |
| L1 | Component | 25% | `@L1_Component`, `@ATL` |
| L2 | Integration | 10% | `@L2_Integration`, `@ATL` |
| L3/L4 | System/E2E | <5% | `@L3_System`, `@L4_Acceptance` |

## Three Pillars

### 1. Contract Tests

Every port interface requires a contract test in `src/test/java/org/s8r/adapter/contract/`.

```java
public abstract class XxxPortContractTest extends PortContractTest<XxxPort> {
    protected abstract XxxPort createPortImplementation();

    @Test void shouldHandleNullInputGracefully() { /* ... */ }
    @Test void shouldFulfillCoreContract() { /* ... */ }
}
```

### 2. Mock Adapters

Hand-rolled mocks via `MockAdapterFactory`. No Mockito in new tests.

```java
// Usage
MockSecurityAdapter security = MockAdapterFactory.security();
security.configure(Map.of("maxFailedAttempts", 3));
// ... test ...
assertTrue(security.wasAuthenticateCalled());
```

### 3. Test Data Builders

Centralized in `TestDataFactory`.

```java
Component c = TestDataFactory.component()
    .withReason("test-processor")
    .inState(State.ACTIVE)
    .build();
```

## Test Naming

```
shouldDoSomething_whenCondition_thenExpectedOutcome
```

## Required Coverage

- Line coverage: 80% minimum (JaCoCo enforced)
- Branch coverage: 80% minimum
- Every port interface: 1 contract test + 1 mock adapter

## Commands

```bash
./s8r-test unit          # L0 tests
./s8r-test component     # L1 tests
./s8r-test integration   # L2 tests
./s8r-test all           # Full suite
mvn verify -P quality-checks  # With coverage enforcement
```

## Pre-Commit Checklist

1. `mvn spotless:apply`
2. `./s8r-test unit`
3. Verify new port has contract test and mock adapter
4. E2E test count has not increased
