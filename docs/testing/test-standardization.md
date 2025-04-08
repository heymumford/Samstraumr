<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Samstraumr Test Standardization Plan

## Test Structure Analysis (April 7, 2025)

After migrating to the new modules structure and cleaning up test files, here's the current test organization:

### Test Count by Category
- Architecture: 13 tests
- Adapter: 11 tests
- Application: 10 tests
- Domain: 4 tests
- Infrastructure: 10 tests
- Migration: 1 test
- Tube: 7 tests
- Test Runners: 4 classes

### Test Types
We have several test annotation types:
- `@UnitTest` - Basic unit tests
- `@TubeTest` - Tests for individual tubes (domain-specific unit tests)
- `@ComponentTest` - Tests for S8r components
- `@CompositeTest` - Tests for composite components
- `@MachineTest` - Tests for machines (higher-level components)
- `@IntegrationTest` - Tests for integration between components
- `@OrchestrationTest` - Tests for system orchestration

### Test Resources
- BDD feature files organized by component type
- Multiple test fixtures and resources

## Test Execution

The `s8r-test` script provides a standardized way to run tests:

```bash
# Run all tests
./s8r-test all

# Run specific test categories
./s8r-test unit
./s8r-test component
./s8r-test composite
./s8r-test machine
./s8r-test architecture
./s8r-test port
```

## Next Steps

1. **Consolidate Test Resources**
   - Move all feature files to a single structure under `modules/samstraumr-core/src/test/resources/features`
   - Ensure consistent naming and organization

2. **Standard Test Annotations**
   - Keep all test annotations in the `org.s8r.test.annotation` package
   - Avoid duplicate annotations

3. **Test Runner Consolidation**
   - Use JUnit 5 test suites for grouping tests
   - Standardize on Cucumber for BDD tests

4. **Test Naming Conventions**
   - [Class]Test.java for unit tests
   - [Feature]Tests.java for test suites

5. **Test Layers**
   - Maintain clear separation between test layers:
     - Unit/Component tests
     - Integration tests
     - BDD tests
     - Architecture tests

6. **CI Integration**
   - Ensure all tests can be run in CI pipeline
   - Add test reports and test coverage metrics

## Current Test File Structure

The test files are organized in the following package structure:
```
org.s8r.architecture - Architecture validation tests
org.s8r.adapter - Adapter implementation tests
org.s8r.application - Application service tests
org.s8r.domain - Domain model tests
org.s8r.infrastructure - Infrastructure implementation tests
org.s8r.migration - Migration utility tests
org.s8r.tube - Tube component tests
org.s8r.test - Test annotations and utilities
```

Each of these package trees contains multiple test classes focused on specific functionality within that layer.
