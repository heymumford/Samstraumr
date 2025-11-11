<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Port Interface Testing Guide

**Status:** Active  
**Last Updated:** April 9, 2025  
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

## Port Interface Test Module

The `test-port-interfaces` module contains all tests for the port interfaces in the Samstraumr framework. It follows a Behavior-Driven Development (BDD) approach using Cucumber to define test scenarios in feature files.

### Module Structure

```
test-port-interfaces/
├── pom.xml                      # Maven configuration
├── src/
│   └── test/
│       ├── java/
│       │   └── org/
│       │       └── s8r/
│       │           ├── test/
│       │           │   ├── context/   # Test contexts
│       │           │   ├── integration/ # Integration components
│       │           │   ├── mock/      # Mock adapters
│       │           │   ├── runner/    # Test runners
│       │           │   ├── runners/   # Individual test runners
│       │           │   └── steps/     # Step definitions
│       └── resources/
│           └── features/
│               ├── integration/   # Integration test features
│               └── port-interfaces/ # Port interface test features
```

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

## Testing Tools

Samstraumr provides dedicated tools for testing port interfaces:

### s8r-test-port-interfaces

A shell script for running port interface tests. It supports running all tests or filtering by specific port:

```bash
# Run all port interface tests
./s8r-test-port-interfaces all

# Run tests for a specific port
./s8r-test-port-interfaces cache
./s8r-test-port-interfaces filesystem
./s8r-test-port-interfaces configuration
./s8r-test-port-interfaces dataflow

# Run integration tests between ports
./s8r-test-port-interfaces integration

# Run tests with verbose output
./s8r-test-port-interfaces all --verbose

# Clean before running tests
./s8r-test-port-interfaces all --clean

# Generate a test report
./s8r-test-port-interfaces all --report

# Run Karate tests only
./s8r-test-port-interfaces karate
```

### s8r-port-coverage

A tool for generating test coverage reports for port interfaces:

```bash
# Generate HTML coverage report
./s8r-port-coverage

# Generate Markdown coverage report
./s8r-port-coverage --markdown

# Generate both HTML and Markdown reports
./s8r-port-coverage --all

# Skip running tests and generate report from existing data
./s8r-port-coverage --skip-tests

# Generate coverage for Karate tests
./s8r-port-coverage --karate
```

## Test Coverage Goals

- **100% Port Coverage**: Every port should have a test suite
- **90% Operation Coverage**: All primary operations of each port should be tested
- **80% Edge Case Coverage**: Most common edge cases should be tested
- **Key Integration Coverage**: Common port combinations should have integration tests

## Current Status

For the current implementation status of port interface tests, see the [Port Interface Test Report](/docs/test-reports/port-interface-test-report.md).

## Port Interface Test Implementation

Each port interface test implementation consists of the following components:

1. **Feature File**: Defines the behavior of the port interface using Gherkin syntax.
2. **Test Context**: Manages the state and operations for the tests.
3. **Mock Adapter**: Implements the port interface for testing.
4. **Step Definitions**: Implements the steps from the feature file.
5. **Test Runner**: Configures and runs the tests.

### Example: DataFlowEventPort Test Implementation

#### Feature File

```gherkin
@L2_Integration @Functional @PortInterface @DataFlow
Feature: DataFlow Event Port Interface
  As a system developer
  I want to use the DataFlowEventPort interface for component data communication
  So that I can enable components to communicate data without direct dependencies

  Background:
    Given a clean system environment
    And the DataFlowEventPort interface is properly initialized
    ...
```

#### Test Context

```java
public class DataFlowEventPortTestContext {
    private final DataFlowEventPort dataFlowEventPort;
    private final Map<String, ComponentId> componentIds;
    private final Map<String, List<ComponentDataEvent>> receivedEvents;
    
    // Methods for managing test state and operations
    ...
}
```

#### Mock Adapter

```java
public class MockDataFlowEventAdapter implements DataFlowEventPort {
    private final Map<String, Map<ComponentId, Consumer<ComponentDataEvent>>> channelSubscriptions;
    private final Map<ComponentId, Set<String>> componentSubscriptions;
    
    // Implementation of port interface methods
    ...
}
```

#### Step Definitions

```java
public class DataFlowEventPortSteps {
    private final DataFlowEventPortTestContext context;
    
    // Step definitions for the feature file
    @Given("component {string} subscribes to data channel {string}")
    public void givenComponentSubscribesToDataChannel(String componentId, String channel) {
        ...
    }
    ...
}
```

#### Test Runner

```java
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/port-interfaces/dataflow-event-port-test.feature")
@IncludeTags("DataFlow")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "org.s8r.test.steps")
public class DataFlowEventPortTests {
}
```

## Integration Testing

Integration tests verify the interaction between different port interfaces. Each integration test consists of:

1. **Feature File**: Defines the integration scenarios.
2. **Integration Components**: Classes that bridge between port interfaces.
3. **Test Context**: Manages both port interfaces and their integration.
4. **Step Definitions**: Implements the integration steps.
5. **Test Runner**: Configures and runs the integration tests.

### Example: DataFlow-Messaging Integration

The DataFlow-Messaging integration test verifies the bidirectional communication between the DataFlowEventPort and MessagingPort interfaces:

```gherkin
@L2_Integration @Functional @DataFlow @Messaging @integration
Feature: DataFlow Event and Messaging Port Integration
  As a system developer
  I want to integrate DataFlowEventPort with MessagingPort
  So that component data events can be published to messaging channels and vice versa
```

The integration is implemented using a bridge component:

```java
public class DataFlowMessagingBridge {
    private final DataFlowEventPort dataFlowEventPort;
    private final MessagingPort messagingPort;
    
    // Methods for bridging between port interfaces
    ...
}
```

## Best Practices

When implementing port interface tests, follow these best practices:

1. **Complete Port Testing**: Ensure each port interface has:
   - A feature file with comprehensive scenarios
   - A mock adapter implementation
   - A test context for managing state
   - Step definitions for all scenarios
   - A test runner

2. **Integration Testing**: Create integration tests between related port interfaces.

3. **Test Coverage**: Use `s8r-port-coverage` to monitor test coverage and identify gaps.

4. **Error Handling**: Include error scenarios to verify proper handling of edge cases.

5. **Thread Safety**: Ensure mock implementations are thread-safe for concurrent operations.

6. **Clean Architecture Principles**: Keep tests aligned with Clean Architecture principles,
   focusing on the interface contract rather than implementation details.

## Karate Testing Framework Integration

In addition to Cucumber BDD tests, Samstraumr now supports the Karate testing framework for port interface tests. Karate provides a more concise syntax for API testing and is particularly well-suited for testing interfaces.

### Karate Test Structure

Karate tests are structured as `.feature` files with a syntax similar to Cucumber, but with built-in support for HTTP, JSON, and JavaScript:

```gherkin
Feature: Configuration Port Interface Tests
  As an application developer
  I want to use a standardized Configuration Port interface
  So that I can manage application configuration properties in a consistent way

  Background:
    * def configAdapter = Java.type('org.s8r.infrastructure.config.PropertiesConfigurationAdapter').createInstance()
    * def testProperties = { 'app.name': 'Samstraumr', 'app.version': '2.7.1' }

  Scenario: Get string properties
    Given def adapter = Java.type('org.s8r.infrastructure.config.PropertiesConfigurationAdapter').createWithProperties(karate.fromJson(testProperties))
    When def appName = adapter.getString('app.name').get()
    Then assert appName == 'Samstraumr'
```

### Karate vs. Cucumber BDD Tests

Both testing frameworks serve different needs in the Samstraumr project:

| Feature | Karate | Cucumber BDD |
|---------|--------|-------------|
| Primary Focus | API and Interface Testing | Behavior Verification |
| Integration with Java | JavaScript bridge to Java | Direct Java integration |
| Syntax Complexity | Simple, concise | More verbose |
| Test Level | System (L3) | Unit/Integration (L0-L2) |
| Best For | Port implementations | Domain behavior |

### Currently Implemented Karate Tests

The following port interfaces have Karate tests implemented:

1. **Cache Port**: Testing in-memory cache operations
2. **FileSystem Port**: Testing file system operations
3. **Configuration Port**: Testing configuration property management
4. **Security-Event Integration**: Testing security event publishing
5. **Task-Notification Integration**: Testing task scheduling notifications

### Running Karate Tests

To run Karate tests:

```bash
# Run all Karate tests
./s8r-test-port-interfaces karate

# Run specific Karate tests
./s8r-test-port-interfaces karate:cache
./s8r-test-port-interfaces karate:filesystem
./s8r-test-port-interfaces karate:configuration
```

### Karate Documentation

For detailed information on writing Karate tests for Samstraumr port interfaces, see:
- [Karate Testing Guide](/docs/testing/karate-testing-guide.md)
- [Karate Syntax Reference](/docs/testing/karate-syntax-reference.md)

## Related Documentation

- [Port Interface Test Report](/docs/test-reports/port-interface-test-report.md)
- [Port Interface Testing Summary](/docs/test-reports/port-interface-testing-summary.md)
- [Clean Architecture Ports](/docs/architecture/clean/port-interfaces-summary.md)
- [Test Strategy](/docs/testing/test-strategy.md)
- [Karate Testing Guide](/docs/testing/karate-testing-guide.md)
- [Karate Syntax Reference](/docs/testing/karate-syntax-reference.md)

## Recently Implemented: Security-Event Integration

The Security-Event integration test has been implemented to verify that security events are properly published to the event system when security-related operations occur. This integration connects the SecurityPort and EventPublisherPort interfaces.

### Key Components

1. **SecurityEventBridge**: A bridge that connects SecurityPort and EventPublisherPort interfaces
2. **MockSecurityAdapter**: A mock implementation of the SecurityPort interface
3. **MockEventPublisherAdapter**: A mock implementation of the EventPublisherPort interface
4. **SecurityEventIntegrationContext**: A test context class for managing state
5. **SecurityEventIntegrationSteps**: Step definitions for the Cucumber BDD tests
6. **SecurityEventIntegrationTests**: A test runner for the integration tests

### Test Scenarios

The Security-Event integration tests cover:

1. Authentication events (success/failure)
2. Token management events (generation, validation, revocation)
3. Access control events (granted/denied)
4. Security alerts (brute force attempts)
5. User management events (role changes)

For complete details on the Security-Event integration implementation, see the [Port Interface Testing Summary](/docs/test-reports/port-interface-testing-summary.md#security-event-integration).