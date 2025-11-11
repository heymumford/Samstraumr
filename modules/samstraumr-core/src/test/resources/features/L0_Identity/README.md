# Identity Tests

This directory contains BDD feature files for testing the Identity functionality in the S8r framework. These tests are categorized as L0 (Unit) level tests focusing on the component identity system.

## Test Categories

The identity tests are organized into the following features:

1. **Identity Creation**: Tests for creating and validating component identities
2. **Identity Hierarchy**: Tests for managing hierarchical relationships between identities
3. **Identity Lineage**: Tests for tracking the lineage and ancestry of components
4. **Identity Exceptions**: Tests for error handling during identity operations

## Tags

The identity tests use the following tags:

- `@L0_Unit`, `@L0_Identity`: Test pyramid level tags
- `@Identity`: Feature-specific tag
- `@Functional`, `@ATL`: Above-the-line functionality tests
- `@ErrorHandling`, `@BTL`: Below-the-line error handling tests
- `@BoundaryValue`: Boundary value tests
- `@Performance`: Performance-related tests
- `@Resilience`: Error recovery and resilience tests

## Related Files

- Step definitions: `/src/test/java/org/s8r/test/steps/identity/`
- Test runner: `/src/test/java/org/s8r/test/runner/RunComponentIdentityTests.java`
- Domain class: `/src/main/java/org/s8r/component/Identity.java`

## Running the Tests

To run just the identity tests:

```bash
./s8r-test identity
```

Or with the specific tag:

```bash
./s8r-test --tags "@L0_Identity"
```