# Lifecycle Tests

This directory contains BDD feature files for testing component lifecycle management in the S8r framework. These tests are categorized as L0 (Unit) level tests focusing on lifecycle state transitions, state-dependent behavior, and resource management.

## Test Categories

The lifecycle tests are organized into the following features:

1. **Lifecycle Transitions** (`lifecycle-transitions.feature`): Tests for progression through lifecycle states, valid and invalid transitions, and event notification.

2. **Lifecycle States** (`lifecycle-states.feature`): Tests for state-dependent behavior and operation availability based on component state.

3. **Lifecycle Resources** (`lifecycle-resources.feature`): Tests for resource allocation and release throughout the component lifecycle.

4. **Lifecycle Negative Paths** (`lifecycle-negative-paths.feature`): Tests for error handling during lifecycle operations, invalid transitions, and operation attempts in inappropriate states.

## Tags

The lifecycle tests use the following tags:

- `@L0_Unit`, `@L0_Lifecycle`: Test pyramid level tags
- `@Lifecycle`: Component lifecycle-specific tests
- `@Functional`, `@ATL`: Above-the-line functionality tests
- `@ErrorHandling`, `@BTL`: Below-the-line error handling tests
- `@Resilience`: Error recovery and resilience tests
- `@Performance`: Performance-related tests

## Related Files

- Step definitions:
  - `org.s8r.test.steps.lifecycle.LifecycleTransitionSteps`: State transition tests
  - `org.s8r.test.steps.lifecycle.LifecycleNegativePathSteps`: Error handling tests
  - `org.s8r.test.steps.lifecycle.LifecycleOperationsSteps`: State-dependent operations

- Test runners:
  - `RunLifecycleTests.java`: Comprehensive runner for all lifecycle tests
  - `RunBasicLifecycleStateTests.java`: Focused runner for state tests
  - `RunLifecycleNegativePathTests.java`: Error handling tests
  - `RunLifecycleResourceTests.java`: Resource management tests

- Support classes:
  - `ComponentTestUtil.java`: Helper for creating test components
  - `ListenerFactory.java`: Factory for test listeners

- Domain classes:
  - `Component.java`: Main component implementation
  - `State.java`: Component lifecycle states
  - `ComponentTerminatedException.java`: Exception for terminated component operations
  - `InvalidStateTransitionException.java`: Exception for invalid state transitions

## Running the Tests

The project now includes a specialized script for lifecycle tests with different modes:

```bash
# Run basic state tests (default)
./bin/s8r-test-lifecycle

# Run specific test categories
./bin/s8r-test-lifecycle states     # State-dependent behavior
./bin/s8r-test-lifecycle transitions # State transitions 
./bin/s8r-test-lifecycle resources   # Resource management
./bin/s8r-test-lifecycle negative    # Error handling

# Run all lifecycle tests
./bin/s8r-test-lifecycle comprehensive

# Run with options
./bin/s8r-test-lifecycle states --verbose     # Detailed output
./bin/s8r-test-lifecycle resources --output   # Save results to file
```

Or use the main test script:

```bash
./s8r-test lifecycle
```