# Error Handling Tests

This directory contains BDD feature files for testing error handling in the S8r framework. These tests are categorized as L0 (Unit) level tests focusing on various aspects of exception handling, parameter validation, and resource management during error conditions.

## Test Categories

The error handling tests are organized into the following features:

1. **Parameter Validation** (`parameter-validation.feature`): Tests for robust validation of method parameters, including null checks, empty string validation, and length constraints.

2. **State Transition Exceptions** (`state-transition-exceptions.feature`): Tests for proper exception handling during invalid state transitions, including error messages, state preservation, and logging.

3. **Resource Cleanup During Errors** (`resource-cleanup-during-errors.feature`): Tests for proper resource management when errors occur, ensuring no leaks even during exceptional conditions.

4. **Operation Exception Handling** (`operation-exception-handling.feature`): Tests for comprehensive exception handling during component operations, including proper exception types, contextual information, and recovery strategies.

## Tags

The error handling tests use the following tags:

- `@L0_Unit`, `@L0_ErrorHandling`: Test pyramid level tags
- `@ErrorHandling`, `@BTL`: Below-the-line error handling tests
- `@State`: State-related error handling tests
- `@Resilience`: Error recovery and resilience tests

## Related Files

- Step definitions: `/src/test/java/org/s8r/test/steps/errorhandling/`
- Test runner: `/src/test/java/org/s8r/test/runner/RunErrorHandlingTests.java`
- Domain class: `/src/main/java/org/s8r/component/Component.java`

## Running the Tests

To run just the error handling tests:

```bash
./s8r-test error-handling
```

Or with the specific tag:

```bash
./s8r-test --tags "@L0_ErrorHandling"
```