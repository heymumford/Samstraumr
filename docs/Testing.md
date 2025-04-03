# Testing in Samstraumr

```
Version: 0.6.1
Last updated: April 03, 2025
Author: Eric C. Mumford (@heymumford)
Contributors: Samstraumr Core Team
```

## Table of Contents
- [Introduction](#introduction)
- [Testing Philosophy](#testing-philosophy)
- [Test Types](#test-types)
- [Tag Ontology](#tag-ontology)
- [Testing Tools](#testing-tools)
- [BDD with Cucumber](#bdd-with-cucumber)
- [Testing Patterns](#testing-patterns)
- [Running Tests](#running-tests)
- [Writing New Tests](#writing-new-tests)
- [Test Organization Best Practices](#test-organization-best-practices)
- [Advanced Testing Topics](#advanced-testing-topics)
- [Quality Gates](#quality-gates)
- [Continuous Integration](#continuous-integration)

## Introduction

In Samstraumr, testing isn't an afterthought—it's an integral part of the design philosophy. Just as Samstraumr embraces the principles of living systems, our testing approach follows natural patterns of organization, adaptation, and self-awareness.

This document outlines how we approach testing in Samstraumr, from the philosophical underpinnings to practical implementation details, ensuring that your flowing systems remain robust and resilient through evolution and growth.

## Testing Philosophy

Samstraumr's testing philosophy draws from systems theory, recognizing that parts of a system must be tested both in isolation and in combination to understand emergent behaviors:

### Key Principles

1. **Hierarchical Testing**: Tests are organized in layers, mirroring the natural composition of Samstraumr systems (tubes, bundles, machines, systems).

2. **Critical Path Categorization**: Tests are prioritized into Above-the-Line (ATL) tests that must pass for basic functionality, and Below-the-Line (BTL) tests for robustness and edge cases. For a complete explanation of this strategy, see the [ATL/BTL Strategy](./ATL-BTL-Strategy.md) documentation.

3. **Capability-Based Organization**: Tests are categorized by the core capabilities they validate (identity, flow, state, awareness).

4. **Behavioral Focus**: Rather than testing implementation details, we test observable behaviors, ensuring systems function as expected regardless of internal changes.

5. **Living Documentation**: Tests serve as executable specifications that evolve alongside the system they describe, reducing documentation drift.

### From Unit Tests to Living Documentation

Traditional testing approaches fall short for adaptive systems like Samstraumr. Static unit tests struggle to capture the dynamic nature of tubes, and conventional documentation quickly becomes outdated as the system evolves.

Samstraumr adopts Behavior-Driven Development (BDD) with Cucumber, creating a unified approach to testing and documentation that aligns with our philosophical underpinnings while addressing the practical challenges of quality assurance in adaptive systems.

## Testing Tools

Samstraumr uses a variety of testing tools and frameworks to support its comprehensive testing strategy:

### JUnit 5
The foundation of Samstraumr's testing infrastructure, providing core testing capabilities:

- **Extensions**: Custom extensions for property-based testing, parameterized tests, and dynamic test generation
- **Assertions**: Rich assertion library for test verification
- **Test Lifecycle Management**: Advanced setup/teardown capabilities for test resources
- **Parallel Execution**: Multi-threaded test execution for faster feedback

```java
@DisplayName("Tube initialization tests")
class TubeInitializationTest {
    @Test
    void shouldCreateTubeWithValidParams() {
        // Test implementation
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"small", "medium", "large"})
    void shouldHandleDifferentSizes(String size) {
        // Test with different sizes
    }
}
```

### TestContainers
For system testing with external dependencies like databases and services:

- **Containerized Dependencies**: Automatically spin up Docker containers for external services
- **Database Testing**: Support for PostgreSQL, MySQL, MongoDB, and other databases
- **Messaging Systems**: Kafka, RabbitMQ, and other messaging systems
- **Custom Services**: Support for custom Docker images

```java
@Testcontainers
public class DatabaseStreamTest {
    @Container
    private static final PostgreSQLContainer<?> postgres = 
        new PostgreSQLContainer<>("postgres:14")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");
            
    @Test
    void shouldConnectToDatabase() {
        // Test database operations
    }
}
```

### Cucumber
For BDD testing at machine and acceptance levels:

- **Gherkin Syntax**: Human-readable feature files
- **Step Definitions**: Java implementations of test steps
- **Reporting**: Detailed HTML reports with test results
- **Tag Filtering**: Run specific test subsets based on tags

```gherkin
Feature: Customer Registration
  
  Scenario: Register a new customer
    Given a new customer with name "John Doe"
    When they register with email "john@example.com"
    Then they should receive a welcome email
    And their account should be created in the system
```

### Mockito
For mocking dependencies in unit and integration tests:

- **Mock Objects**: Create test doubles for external dependencies
- **Behavior Verification**: Verify interactions with dependencies
- **Argument Matching**: Flexible argument matching for verification
- **Spy Objects**: Partial mocking of real objects

```java
@Test
void shouldSendNotificationOnError() {
    // Given
    NotificationService notificationService = mock(NotificationService.class);
    MonitorTube tube = new MonitorTube(notificationService);
    
    // When
    tube.handleError("test error");
    
    // Then
    verify(notificationService).sendAlert(eq("test error"), any(ErrorLevel.class));
}
```

### Custom Testing Tools

#### TestRunner Script
Samstraumr includes a custom test runner script for simplified test execution:

- **Usage**: `./util/test/run-tests.sh [OPTIONS] [TEST_TYPE]`
- **Test Types**: tube, flow, bundle, stream, adaptation, machine, acceptance
- **Options**: parallel execution, thread count, quality check skipping

```bash
# Run tube tests in parallel
./util/test/run-tests.sh --parallel tube

# Run acceptance tests without quality checks
./util/test/run-tests.sh --skip-quality acceptance

# Run all tests with verbose output
./util/test/run-tests.sh --verbose all
```

#### Adaptation Testing Framework
Custom framework for testing adaptive behavior:

- **Resource Constraints**: Simulate memory, CPU, I/O limitations
- **Failure Injection**: Introduce controlled failures to test adaptation
- **State Transition Analysis**: Verify correct state changes during adaptation
- **Recovery Verification**: Ensure proper recovery from constrained conditions

```java
@Test
void shouldAdaptToResourceConstraints() {
    AdaptationTestHarness harness = new AdaptationTestHarness();
    harness.withConstraint(ResourceType.MEMORY, 0.2)
           .withConstraint(ResourceType.CPU, 0.3)
           .execute(tube -> {
               // Perform operations that trigger adaptation
               return tube.process("test data");
           })
           .verify(result -> {
               // Verify correct adaptation behavior
               assertTrue(result.isSuccessful());
               assertEquals(TubeState.ADAPTING, result.getTubeState());
           });
}
```

## Tag Ontology

To organize our tests in a way that reflects Samstraumr's core principles, we've developed a comprehensive tag ontology that works in conjunction with our test types:

### Test Type Tags

| Tag | Description | Test Type |
|-----|-------------|-----------|
| `@TubeTest` | Individual tube unit tests | Tube Tests |
| `@FlowTest` | Single tube data flow tests | Flow Tests |
| `@BundleTest` | Connected tubes component tests | Bundle Tests |
| `@StreamTest` | External integration tests | Stream Tests |
| `@AdaptationTest` | Property-based adaptation tests | Adaptation Tests |
| `@L2_Machine` | End-to-end machine tests | Machine Tests |
| `@Acceptance` | Business requirement validation | BDD Acceptance Tests |

### Hierarchical Structure

| Tag | Description | Example |
|-----|-------------|---------|
| `@L0_Tube` | Atomic tube component tests | Testing a single tube in isolation |
| `@L1_Bundle` | Bundle-level integration tests | Testing connected tubes forming a bundle |
| `@L2_Machine` | Complex machine composition tests | Testing interconnected bundles forming a machine |
| `@L3_System` | Full system tests | Testing complete systems with multiple machines |

### Critical Path Categorization

The ATL/BTL categorization is a key part of Samstraumr's testing strategy, dividing tests by criticality:

| Tag | Full Name | Description | Example |
|-----|-----------|-------------|---------|
| `@ATL` | Above The Line | Critical tests that MUST pass with every build | Core functionality tests that must pass |
| `@BTL` | Below The Line | Important but non-blocking tests that can run separately | Edge cases and additional quality tests |

This strategy enables faster feedback cycles while maintaining comprehensive coverage. See the complete [ATL/BTL Strategy](./ATL-BTL-Strategy.md) documentation for implementation details.

### Core Capabilities

| Tag | Description | Example |
|-----|-------------|---------|
| `@Identity` | UUID, naming, identification tests | Testing unique ID generation |
| `@Flow` | Data movement and transformation | Testing data processing pipelines |
| `@State` | State management and transitions | Testing state transitions and propagation |
| `@Awareness` | Self-monitoring and environment awareness | Testing monitoring capabilities |

### Lifecycle Tests

| Tag | Description | Example |
|-----|-------------|---------|
| `@Init` | Initialization/construction tests | Testing component creation |
| `@Runtime` | Normal operation tests | Testing standard processing operations |
| `@Termination` | Shutdown/cleanup tests | Testing proper resource cleanup |

### Patterns

| Tag | Description | Example |
|-----|-------------|---------|
| `@Observer` | Monitoring pattern tests | Testing tubes that monitor without modifying |
| `@Transformer` | Data transformation tests | Testing data transformation capabilities |
| `@Validator` | Input validation tests | Testing data validation rules |
| `@CircuitBreaker` | Fault tolerance tests | Testing isolation of failures |

### Non-functional Requirements

| Tag | Description | Example |
|-----|-------------|---------|
| `@Performance` | Speed and resource usage | Testing resource utilization and speed |
| `@Resilience` | Recovery and fault handling | Testing recovery from failures |
| `@Scale` | Load and scaling tests | Testing behavior under increased load |

## Test Types

Samstraumr's testing strategy employs a comprehensive set of test types that align with software engineering best practices while using domain-specific terminology. Each test type serves a particular purpose in verifying system functionality:

### Tube Tests
- **Focus**: Individual tubes in isolation (Unit tests)
- **Technology**: JUnit 5
- **Naming Convention**: `*TubeTest.java`
- **Purpose**: Verify the core functionality of a single tube component
- **Examples**: Tube initialization, UUID generation, state transitions, lifecycle validation
- **Command**: `./util/test/run-tests.sh tube`

```java
@Test
void shouldInitializeWithUniqueIdentifier() {
    // Given
    Environment environment = new Environment();
    
    // When
    Tube tube = Tube.create("Test tube", environment);
    
    // Then
    assertNotNull(tube.getUniqueId());
    assertTrue(tube.getUniqueId().length() > 0);
}
```

### Flow Tests
- **Focus**: Data flows through a single tube (Integration tests)
- **Technology**: JUnit 5
- **Naming Convention**: `*FlowTest.java`
- **Purpose**: Verify data transformations and internal component interactions
- **Examples**: Data processing within a tube, error handling, state changes during processing
- **Command**: `./util/test/run-tests.sh flow`

```java
@Test
void shouldTransformDataCorrectly() {
    // Given
    TransformerTube tube = new TransformerTube();
    String inputData = "test data";
    
    // When
    String result = tube.process(inputData);
    
    // Then
    assertEquals("TEST DATA", result);
    assertTrue(tube.getProcessingLog().contains("transformation applied"));
}
```

### Bundle Tests
- **Focus**: Multiple connected tubes (Component tests)
- **Technology**: JUnit 5
- **Naming Convention**: `*BundleTest.java`
- **Purpose**: Verify that tubes collaborate effectively within a bundle
- **Examples**: Data pipelines, error propagation, bundle state management
- **Command**: `./util/test/run-tests.sh bundle`

```java
@Test
void shouldProcessDataThroughEntireBundle() {
    // Given
    Bundle bundle = createTestBundle();
    String inputData = "raw input";
    
    // When
    Optional<String> result = bundle.process("entry", inputData);
    
    // Then
    assertTrue(result.isPresent());
    assertEquals("PROCESSED: RAW INPUT", result.get());
}
```

### Stream Tests
- **Focus**: External system interactions (System tests)
- **Technology**: JUnit 5 with TestContainers
- **Naming Convention**: `*StreamTest.java`
- **Purpose**: Verify integration with external dependencies
- **Examples**: Database interactions, API communication, messaging systems
- **Command**: `./util/test/run-tests.sh stream`

```java
@Test
void shouldPersistDataToDatabase() {
    // Given
    try (PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")) {
        postgres.start();
        DatabaseTube tube = new DatabaseTube(postgres.getJdbcUrl(), 
                                            postgres.getUsername(), 
                                            postgres.getPassword());
        
        // When
        tube.store("test_key", "test_value");
        
        // Then
        Optional<String> result = tube.retrieve("test_key");
        assertTrue(result.isPresent());
        assertEquals("test_value", result.get());
    }
}
```

### Adaptation Tests
- **Focus**: System behavior under changing conditions (Property tests)
- **Technology**: Custom JUnit 5 extensions
- **Naming Convention**: `*AdaptationTest.java`
- **Purpose**: Verify adaptive behavior and resilience
- **Examples**: Resource constraints handling, degraded operation modes, recovery mechanisms
- **Command**: `./util/test/run-tests.sh adaptation`

```java
@Test
void shouldAdaptToReducedMemory() {
    // Given
    AdaptiveTube tube = new AdaptiveTube();
    ResourceConstraint memoryConstraint = ResourceConstraint.memory(0.2); // 20% memory
    
    // When
    tube.applyConstraint(memoryConstraint);
    OperationResult result = tube.performOperation();
    
    // Then
    assertEquals(TubeState.ADAPTING, tube.getDesignState());
    assertTrue(result.isSuccessful());
    assertTrue(tube.getMemoryUsage() < memoryConstraint.getThreshold());
}
```

### Machine Tests
- **Focus**: End-to-end functionality (E2E tests)
- **Technology**: Cucumber BDD
- **Naming Convention**: `*.feature` files with `@L2_Machine` tag
- **Purpose**: Verify complete machine functionality across multiple bundles
- **Examples**: Business workflows, user interactions, system integrations
- **Command**: `./util/test/run-tests.sh machine`

```gherkin
@L2_Machine @ATL
Feature: Data Processing Machine
  
  Scenario: Process data through complete pipeline
    Given a data processing machine is initialized
    When raw data "customer:John Doe,order:123" is submitted
    Then the customer information should be extracted
    And the order should be processed
    And a confirmation should be generated
```

### BDD Acceptance Tests
- **Focus**: Business requirements validation (Acceptance tests)
- **Technology**: Cucumber BDD
- **Naming Convention**: `*.feature` files with `@Acceptance` tag
- **Purpose**: Verify that the system meets business requirements
- **Examples**: User stories, business scenarios, acceptance criteria
- **Command**: `./util/test/run-tests.sh acceptance`

```gherkin
@Acceptance @ATL
Feature: Order Processing
  
  Scenario: Customer places a valid order
    Given a customer with a valid account
    When they submit an order with valid payment information
    Then the order should be accepted
    And an order confirmation should be sent
    And inventory should be updated
```

## BDD with Cucumber

Samstraumr uses Behavior-Driven Development (BDD) with Cucumber for several key advantages:

### Why BDD?

1. **Natural Language Specifications**: Features and scenarios written in Gherkin provide clear, readable specifications that anyone can understand.

2. **Living Documentation**: Tests serve as executable specifications that evolve alongside the code, ensuring documentation stays current.

3. **Collaboration**: BDD bridges the gap between technical and non-technical stakeholders, fostering shared understanding.

4. **Adaptive Testing**: BDD scenarios can adapt to the changing nature of Samstraumr systems, just as the systems themselves adapt to their environment.

### Structure of a Cucumber Test

```gherkin
@ATL @L0_Tube @Init @Identity
Feature: Tube Initialization
  As a developer
  I want tubes to initialize with unique identities
  So that each tube can be uniquely identified in the system

  Background:
    Given the operating environment is ready

  Scenario: Tube initializes with a unique UUID
    When a new Tube is instantiated with reason "Test Initialization"
    Then the Tube should initialize with a unique UUID
    And the Tube should log its environment details
```

This approach combines human-readable specifications with executable tests, ensuring that your documentation always reflects the actual system behavior.

## Testing Patterns

Samstraumr implements various tube patterns, each with specific testing approaches:

### Observer Pattern Testing

The Observer pattern allows tubes to monitor events without modifying data flow.

**Testing Strategy:**
- Verify that observer tubes receive all signals they're configured to observe
- Ensure observation has minimal overhead on system performance
- Test that observers don't modify the original data being observed
- Verify proper handling of observation interruptions and recovery

**Example:**
```gherkin
@ATL @L1_Bundle @Observer
Scenario: Observer tube monitors data flow without interference
  Given a monitor tube is initialized to observe multiple signals generated at high frequency
  When the monitor tube observes them
  Then the signals should be logged accurately with 100 entries
  And no modifications should be made to the observed signals
  And the observer's overhead should be less than 10% of total processing time
```

### Transformer Pattern Testing

The Transformer pattern processes and modifies data flowing through tubes.

**Testing Strategy:**
- Verify transformations produce expected outputs for various inputs
- Test handling of invalid inputs
- Measure transformation performance under load

**Example:**
```gherkin
@ATL @L1_Bundle @Transformer
Scenario: Transformer tube correctly modifies data
  Given a transformer tube is configured with a specific transformation function
  When data "Hello" is sent through the transformer
  Then the output should be "HELLO" after transformation
  And the transformation should be logged
```

### Validator Pattern Testing

The Validator pattern ensures data meets specific criteria before processing.

**Testing Strategy:**
- Test with both valid and invalid inputs
- Verify proper rejection of invalid data
- Ensure validation rules can be dynamically updated

**Example:**
```gherkin
@ATL @L1_Bundle @Validator
Scenario: Validator tube correctly identifies invalid data
  Given a validator tube is configured with numeric validation rules
  When non-numeric data "abc" is sent for validation
  Then the data should be rejected
  And an appropriate error should be logged for the invalid input
```

### Circuit Breaker Pattern Testing

The Circuit Breaker pattern isolates failures to prevent cascading system issues.

**Testing Strategy:**
- Verify that failures are detected and isolated
- Test recovery and reset mechanisms
- Ensure proper state transitions (closed → open → half-open → closed)

**Example:**
```gherkin
@ATL @L2_Machine @CircuitBreaker
Scenario: Circuit breaker isolates failing component
  Given a machine with circuit breaker protection is running
  When a component begins failing repeatedly
  Then the circuit breaker should transition to OPEN state
  And the system should redirect flow around the failing component
```

## Running Tests

Samstraumr provides a unified test runner script that simplifies test execution while supporting the various test types:

### Using the Test Runner Script

The recommended way to run Samstraumr tests is using the test runner script:

```bash
# Run all tests
./util/test/run-tests.sh all

# Run tests by criticality
./util/test/run-tests.sh atl          # Run Above The Line tests (critical, must pass)
./util/test/run-tests.sh btl          # Run Below The Line tests (robustness, non-blocking)
./util/test/run-tests.sh critical     # Alias for atl (for backward compatibility)

# Run tests by type
./util/test/run-tests.sh tube         # Run Tube Tests (unit)
./util/test/run-tests.sh flow         # Run Flow Tests (integration)
./util/test/run-tests.sh bundle       # Run Bundle Tests (component)
./util/test/run-tests.sh stream       # Run Stream Tests (system)
./util/test/run-tests.sh adaptation   # Run Adaptation Tests (property)
./util/test/run-tests.sh machine      # Run Machine Tests (e2e)
./util/test/run-tests.sh acceptance   # Run BDD Acceptance Tests (business)
```

### Script Options

The test runner supports various options:

```bash
# Run tests in parallel
./util/test/run-tests.sh --parallel flow

# Set custom thread count
./util/test/run-tests.sh --threads 4 bundle

# Skip quality checks for faster execution
./util/test/run-tests.sh --skip-quality stream

# Enable verbose output
./util/test/run-tests.sh --verbose adaptation

# Combine options
./util/test/run-tests.sh --parallel --threads 8 --skip-quality machine
```

For help with all available options:

```bash
./util/test/run-tests.sh --help
```

### Maven Direct Execution

For more granular control, you can also run tests directly with Maven:

#### Basic Test Execution

```bash
# Run all tests
mvn test

# Run tests by criticality
mvn test -P atl-tests          # Run Above The Line tests (critical)
mvn test -P btl-tests          # Run Below The Line tests (robustness)
mvn test -Dgroups=ATL          # Alternative way to run ATL tests
mvn test -Dgroups=BTL          # Alternative way to run BTL tests

# Run tests with specific test type
mvn test -Dtest=*TubeTest       # Run all Tube Tests
mvn test -Dtest=*FlowTest       # Run all Flow Tests
mvn test -Dtest=*BundleTest     # Run all Bundle Tests
mvn test -Dtest=*StreamTest     # Run all Stream Tests
mvn test -Dtest=*AdaptationTest # Run all Adaptation Tests
```

#### Tagged Testing (Cucumber)

```bash
# Run tests with specific tags
mvn test -Dcucumber.filter.tags="@L2_Machine"    # Run Machine Tests
mvn test -Dcucumber.filter.tags="@Acceptance"    # Run Acceptance Tests

# Combine tags for specific subsets
mvn test -Dcucumber.filter.tags="@L0_Tube and @Identity"
mvn test -Dcucumber.filter.tags="@ATL and @Resilience"
```

#### Performance Optimization

```bash
# Run with custom thread count for parallelization
mvn test -T 12 -P atl-tests

# Skip quality checks during test runs
mvn test -P skip-quality-checks

# Use build performance script
./util/build/build-performance.sh test -P atl-tests
```

### Test Reports

Samstraumr generates comprehensive test reports:

#### JUnit Reports

JUnit XML reports are automatically generated in the `target/surefire-reports` directory.

#### Cucumber Reports

HTML reports for BDD tests can be found at `target/cucumber-reports/cucumber.html`.

You can also specify custom reporting options:

```bash
# Generate custom formatted HTML report
mvn test -Dcucumber.plugin="html:target/custom-reports/cucumber.html"

# Generate JSON report for external tools
mvn test -Dcucumber.plugin="json:target/custom-reports/cucumber.json"
```

## Writing New Tests

When adding new components to Samstraumr, follow these steps to create appropriate tests:

### 1. Identify the Component Level

Determine where your component fits in the hierarchy:
- Individual tube? Start with `@L0_Tube` tests
- Bundle of tubes? Add `@L1_Bundle` tests
- Machine? Include `@L2_Machine` tests
- Complete system? Create `@L3_System` tests

### 2. Start with ATL Tests

Begin by writing critical Above-the-Line tests that verify essential functionality:

```gherkin
@ATL @L0_Tube @Init @Identity
Scenario: New tube component initializes correctly
  Given the operating environment is ready
  When the new tube component is instantiated
  Then it should have a unique identifier
  And it should initialize in FLOWING state
```

### 3. Add BTL Tests for Robustness

Once ATL tests pass, enhance with Below-the-Line tests for edge cases:

```gherkin
@BTL @L0_Tube @Init @Resilience
Scenario: New tube component handles invalid initialization parameters
  Given the operating environment is ready
  When the new tube component is instantiated with invalid parameters
  Then it should fail gracefully
  And log appropriate error messages
```

### 4. Implement Step Definitions

Create corresponding step definitions in Java:

```java
@Given("the new tube component is instantiated")
public void the_new_tube_component_is_instantiated() {
    testTube = new NewTubeComponent();
    assertNotNull(testTube);
}

@Then("it should have a unique identifier")
public void it_should_have_a_unique_identifier() {
    assertNotNull(testTube.getUniqueId());
}
```

### 5. Run Tests Incrementally

Develop tests incrementally, ensuring each level passes before moving to the next:
1. Run and pass `@L0_Tube` tests
2. Implement and pass `@L1_Bundle` tests
3. Build and validate `@L2_Machine` tests
4. Finally, confirm `@L3_System` tests

## Test Organization Best Practices

To maintain a clean, manageable test suite as your Samstraumr system grows:

### 1. Follow the Tag Ontology

Every test should have at minimum:
- One hierarchical tag (`@L0_Tube`, `@L1_Bundle`, etc.)
- One critical path tag (`@ATL` or `@BTL`)
- One capability or pattern tag

### 2. Structure Test Directories Hierarchically

Organize feature files in directories that reflect the testing hierarchy:
```
src/test/resources/tube/features/
   L0_Tube/
   L1_Bundle/
   L2_Machine/
   L3_System/
```

### 3. Write Self-Documenting Scenarios

Include clear, descriptive language in your feature files:
- Feature descriptions that explain the component's purpose
- Scenario titles that clearly state what's being tested
- Comments for complex tests that explain the rationale

### 4. Maintain Step Definition Clarity

Keep step definitions:
- Focused on a single responsibility
- Well-named to reflect their purpose
- Organized by feature or component

## Advanced Testing Topics

### Testing Adaptive Behavior

Samstraumr's tubes can adapt to changing conditions. Test this with scenarios like:

```gherkin
@ATL @L0_Tube @Runtime @Awareness
Scenario: Tube adapts to resource constraints
  Given a tube is processing data at normal capacity
  When available memory drops below 20%
  Then the tube should transition to ADAPTING state
  And reduce its resource consumption
  And continue processing with adjusted parameters
```

### Testing State Transitions

Verify that state changes occur correctly and propagate appropriately:

```gherkin
@ATL @L2_Machine @State
Scenario: State changes propagate through the machine
  Given a machine with interconnected bundles is running
  When a tube in bundle B1 transitions to ERROR state
  Then bundle B1 should transition to DEGRADED state
  And the machine should report partial functionality
```

### Performance Testing

For performance-critical components, include scenarios that validate efficiency:

```gherkin
@BTL @L1_Bundle @Performance
Scenario: Bundle processes large data volumes efficiently
  Given a data processing bundle is initialized
  When 1 million records are processed through the bundle
  Then the processing should complete within 30 seconds
  And memory usage should not exceed 500MB
```

### Testing Cycles and Frequency

Samstraumr's testing approach follows natural cycles that align with development phases:

1. **Development Cycle**: During active development, focus on ATL tests for the specific components being modified.

2. **Integration Cycle**: Before merging changes, run all ATL tests across all levels to ensure no regressions.

3. **Release Cycle**: Before releases, run comprehensive ATL and BTL tests to ensure complete system integrity.

4. **Maintenance Cycle**: Periodically run performance and resilience tests to identify degradation over time.

## Quality Gates

Samstraumr enforces quality through automated checks integrated into the build process. These quality gates ensure code meets standards before being merged into the main branch.

### Spotless (Code Formatting)

Ensures consistent code style using Google Java Style guidelines.

```bash
# Check code formatting
mvn spotless:check

# Apply formatting fixes
mvn spotless:apply
```

### PMD (Static Code Analysis)

Identifies potential bugs, suboptimal code, and overly complex expressions.

```bash
# Run PMD analysis
mvn pmd:check
```

Key rules enforced:
- No unused variables or imports
- No empty catch blocks
- No overly complex methods
- Proper exception handling

### Checkstyle (Style Standards)

Enforces coding standards beyond formatting, including naming conventions and structural rules.

```bash
# Run Checkstyle
mvn checkstyle:check
```

Key standards enforced:
- Naming conventions (camelCase, PascalCase, etc.)
- Method and class length limits
- Documentation requirements
- Import order

### SpotBugs (Bug Detection)

Analyzes bytecode to find bug patterns and potential vulnerabilities.

```bash
# Run SpotBugs
mvn spotbugs:check

# View detailed reports
mvn spotbugs:gui
```

Key checks performed:
- Null pointer dereferences
- Resource leaks
- Thread synchronization issues
- Security vulnerabilities

### JaCoCo (Code Coverage)

Tracks test coverage to ensure adequate testing of all code.

```bash
# Generate coverage report
mvn jacoco:report
```

Coverage thresholds:
- Instruction coverage: 80%
- Branch coverage: 70%
- Complexity coverage: 65%

## Continuous Integration

Samstraumr uses GitHub Actions for continuous integration, automatically running tests and quality checks on every push and pull request.

### CI Pipeline Stages

1. **Build & Fast Tests**
   - Compiles the codebase
   - Runs critical ATL tests
   - Uses optimized performance settings
   - Fast feedback for developers

2. **Quality Analysis**
   - Runs all quality gates (Spotless, PMD, Checkstyle, SpotBugs)
   - Generates code coverage reports with JaCoCo
   - Fails if quality standards aren't met

3. **SonarQube Analysis**
   - Performs comprehensive code quality analysis
   - Tracks technical debt over time
   - Enforces quality gates for security vulnerabilities, code smells, and duplications

### Running the CI Pipeline Locally

You can simulate the CI pipeline locally to catch issues before pushing:

```bash
# Full CI simulation
./build-checks.sh && ./build-performance.sh test -P atl-tests
```

### SonarQube Integration

Samstraumr integrates with SonarQube for advanced code quality and security analysis.

Key metrics monitored:
- **Reliability**: Bug detection and prevention
- **Security**: Vulnerability detection
- **Maintainability**: Code smells and technical debt
- **Coverage**: Test coverage verification
- **Duplications**: Code duplication detection

When used with the GitHub workflow, SonarQube results are displayed directly in pull requests, making code review more effective and ensuring only high-quality code gets merged.

---

By embracing this comprehensive testing approach, your Samstraumr systems will grow with confidence, adapting to new requirements while maintaining the integrity and resilience that make them truly living software.

[Return to Getting Started](./GettingStarted.md) | [Explore Core Concepts](./CoreConcepts.md)
