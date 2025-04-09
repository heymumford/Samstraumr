# Port Interface Testing Strategy

**Status:** Active  
**Last Updated:** April 8, 2025  
**Author:** Eric C. Mumford (@heymumford)

## Overview

This document outlines the strategy for testing port interfaces in the Samstraumr project. Port interfaces form a critical boundary in Clean Architecture between the application core and external infrastructure components. Properly testing these interfaces ensures that the system maintains proper separation of concerns and allows for infrastructure components to be swapped without affecting application behavior.

## Key Concepts

### Port Interfaces

In Clean Architecture, ports define the boundary between:
- **Application Core**: Contains business logic and use cases
- **Infrastructure**: Contains technical implementations and external services

Port interfaces:
- Are defined by the application core
- Are implemented by adapters in the infrastructure layer
- Allow the application to remain decoupled from implementation details

### Test Approach

Port interface tests verify:
1. The **contract** defined by the port is sufficient for the application's needs
2. **Adapters** correctly implement the port interface contract
3. Different adapters for the same port are **interchangeable**
4. Integration between multiple ports works as expected

## BDD Testing with Cucumber

Samstraumr uses Behavior-Driven Development (BDD) with Cucumber to test port interfaces:

1. **Feature Files**: Define the expected behavior of the port
2. **Step Definitions**: Implement the test steps using the port interface
3. **Test Runners**: Configure and execute the tests

### Feature File Structure

Port interface feature files follow this structure:

```gherkin
@L0_Unit @port
Feature: [Port Name] Port Interface
  As an application developer
  I want to use a standardized [Port Name] Port interface
  So that I can [primary purpose of the port]

  Background:
    Given a [port name] port implementation is available

  @smoke
  Scenario: Basic port operation
    When I [perform basic operation]
    Then the operation should complete successfully
    And [expected result should be verified]

  # Additional scenarios for other operations
```

### Integration Test Structure

Port integration tests follow this structure:

```gherkin
@L2_Integration @integration
Feature: [Port A]-[Port B] Integration
  As an application developer
  I want to integrate [Port A] with [Port B]
  So that I can [purpose of integration]

  Background:
    Given [Port A] and [Port B] implementations are available

  @smoke
  Scenario: Basic integration operation
    When I [perform integration operation]
    Then the operation should complete successfully
    And [expected integrated behavior is observed]
```

## Directory Structure

Port interface tests are organized as follows:

- **Feature Files**:
  - `modules/samstraumr-core/src/test/resources/features/port-interfaces/` - Port interface tests
  - `modules/samstraumr-core/src/test/resources/features/integration/` - Integration tests

- **Step Definitions**:
  - `modules/samstraumr-core/src/test/java/org/s8r/test/steps/` - Port step definitions

- **Test Runners**:
  - `modules/samstraumr-core/src/test/java/org/s8r/test/runner/PortIntegrationTests.java` - Port tests
  - `modules/samstraumr-core/src/test/java/org/s8r/test/runner/IntegrationTests.java` - Integration tests

## Port Interface Test Types

Each port interface should be tested for:

1. **Basic Operations**: Core functionality of the port
2. **Error Handling**: How errors are propagated through the port
3. **Edge Cases**: Boundary conditions and special cases
4. **Thread Safety**: Behavior under concurrent access (where applicable)
5. **Resource Management**: Proper allocation and cleanup of resources

## Test Implementation Guideline

### Creating a New Port Interface Test

1. Create a new feature file in `modules/samstraumr-core/src/test/resources/features/port-interfaces/`
   - Name: `[port-name]-port-test.feature`
   - Add tags: `@port @L1`

2. Implement step definitions in `modules/samstraumr-core/src/test/java/org/s8r/test/steps/`
   - Name: `[PortName]PortSteps.java`
   - Implement each step from the feature file

3. Update the test runner if needed
   - The `PortIntegrationTests.java` runner will automatically include the test if it has the `@port` tag

### Creating a Port Integration Test

1. Create a new feature file in `modules/samstraumr-core/src/test/resources/features/integration/`
   - Name: `[port-a]-[port-b]-integration-test.feature`
   - Add tags: `@integration @L2`

2. Implement step definitions in `modules/samstraumr-core/src/test/java/org/s8r/test/steps/`
   - Name: `[PortA][PortB]IntegrationSteps.java`
   - Implement each step from the feature file

3. Update the test runner if needed
   - The `IntegrationTests.java` runner will automatically include the test if it has the `@integration` tag

## Running Port Interface Tests

Use the dedicated script to run port interface tests:

```bash
# Run all port interface tests
./s8r-test-port-interfaces

# Run tests for a specific port
./s8r-test-port-interfaces cache
./s8r-test-port-interfaces notification

# Run all port integration tests
./s8r-test-port-interfaces --integration all

# Run specific integration tests
./s8r-test-port-interfaces --integration cache-fs

# Generate a report on port interface test status
./s8r-test-port-interfaces --verify --report
```

## Test Coverage Goals

- **100% Port Coverage**: Every port should have a test suite
- **90% Operation Coverage**: All primary operations of each port should be tested
- **80% Edge Case Coverage**: Most common edge cases should be tested
- **Key Integration Coverage**: Common port combinations should have integration tests

## Current Status

For the current implementation status of port interface tests, see the [Port Interface Test Report](/docs/test-reports/port-interface-test-report.md).

## Related Documentation

- [Clean Architecture in Samstraumr](/docs/architecture/clean-architecture-migration.md)
- [Adapter Pattern Implementation](/docs/architecture/clean/adapter-pattern-implementation.md)
- [Port Interfaces Summary](/docs/architecture/clean/port-interfaces-summary.md)
- [Integration Testing Strategy](/docs/testing/testing-strategy.md#integration-testing)