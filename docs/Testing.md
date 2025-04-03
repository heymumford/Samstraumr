# Testing in Samstraumr


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

For help with all available options:

```bash
./util/test/run-tests.sh --help

#### Tagged Testing (Cucumber)

```bash
# Run tests with specific tags
mvn test -Dcucumber.filter.tags="@L2_Machine"    # Run Machine Tests
mvn test -Dcucumber.filter.tags="@Acceptance"    # Run Acceptance Tests

# Combine tags for specific subsets
mvn test -Dcucumber.filter.tags="@L0_Tube and @Identity"
mvn test -Dcucumber.filter.tags="@ATL and @Resilience"

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

### 3. Add BTL Tests for Robustness

Once ATL tests pass, enhance with Below-the-Line tests for edge cases:

```gherkin
@BTL @L0_Tube @Init @Resilience
Scenario: New tube component handles invalid initialization parameters
  Given the operating environment is ready
  When the new tube component is instantiated with invalid parameters
  Then it should fail gracefully
  And log appropriate error messages

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

### Performance Testing

For performance-critical components, include scenarios that validate efficiency:

```gherkin
@BTL @L1_Bundle @Performance
Scenario: Bundle processes large data volumes efficiently
  Given a data processing bundle is initialized
  When 1 million records are processed through the bundle
  Then the processing should complete within 30 seconds
  And memory usage should not exceed 500MB

### PMD (Static Code Analysis)

Identifies potential bugs, suboptimal code, and overly complex expressions.

```bash
# Run PMD analysis
mvn pmd:check

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
