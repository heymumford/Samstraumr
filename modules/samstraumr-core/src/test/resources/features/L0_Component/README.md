# Component Tests

This directory contains BDD feature files for testing the core Component functionality in the S8r framework. These tests are categorized as L0 (Unit) level tests focusing on the fundamental component operations.

## Test Categories

The component tests are organized into the following features:

1. **Component Creation**: Tests for component initialization, parent-child relationships, and environment setup
2. **Component Termination**: Tests for proper component shutdown, resource cleanup, and hierarchical termination
3. **Component Exceptions**: Tests for error handling during component operations
4. **Component Status**: Tests for lifecycle state management and status tracking

## Tags

The component tests use the following tags:

- `@L0_Unit`, `@L0_Component`: Test pyramid level tags
- `@Functional`, `@ATL`: Above-the-line functionality tests
- `@ErrorHandling`, `@BTL`: Below-the-line error handling tests
- `@Lifecycle`: Component lifecycle-specific tests
- `@State`: State management tests
- `@Performance`: Performance-related tests
- `@Resilience`: Error recovery and resilience tests

## Related Files

- Step definitions: `/src/test/java/org/s8r/test/steps/component/`
- Test runner: `/src/test/java/org/s8r/test/runner/RunComponentIdentityTests.java`
- Domain class: `/src/main/java/org/s8r/component/Component.java`

## Running the Tests

To run just the component tests:

```bash
./s8r-test component
```

Or with the specific tag:

```bash
./s8r-test --tags "@L0_Component"
```