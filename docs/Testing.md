# Testing in Samstraumr

```
Last updated: April 2, 2025
Author: Eric C. Mumford (@heymumford)
Contributors: Samstraumr Core Team
```

## Table of Contents
- [Introduction](#introduction)
- [Testing Philosophy](#testing-philosophy)
- [Tag Ontology](#tag-ontology)
- [Hierarchical Testing Structure](#hierarchical-testing-structure)
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

2. **Critical Path Categorization**: Tests are prioritized into Above-the-Line (ATL) tests that must pass for basic functionality, and Below-the-Line (BTL) tests for robustness and edge cases.

3. **Capability-Based Organization**: Tests are categorized by the core capabilities they validate (identity, flow, state, awareness).

4. **Behavioral Focus**: Rather than testing implementation details, we test observable behaviors, ensuring systems function as expected regardless of internal changes.

5. **Living Documentation**: Tests serve as executable specifications that evolve alongside the system they describe, reducing documentation drift.

### From Unit Tests to Living Documentation

Traditional testing approaches fall short for adaptive systems like Samstraumr. Static unit tests struggle to capture the dynamic nature of tubes, and conventional documentation quickly becomes outdated as the system evolves.

Samstraumr adopts Behavior-Driven Development (BDD) with Cucumber, creating a unified approach to testing and documentation that aligns with our philosophical underpinnings while addressing the practical challenges of quality assurance in adaptive systems.

## Tag Ontology

To organize our tests in a way that reflects Samstraumr's core principles, we've developed a comprehensive tag ontology:

### Hierarchical Structure

| Tag | Description | Example |
|-----|-------------|---------|
| `@L0_Tube` | Atomic tube component tests | Testing a single tube in isolation |
| `@L1_Bundle` | Bundle-level integration tests | Testing connected tubes forming a bundle |
| `@L2_Machine` | Complex machine composition tests | Testing interconnected bundles forming a machine |
| `@L3_System` | Full system tests | Testing complete systems with multiple machines |

### Critical Path Categorization

| Tag | Description | Example |
|-----|-------------|---------|
| `@ATL` | Above-the-line critical tests (must pass) | Core functionality tests that must pass |
| `@BTL` | Below-the-line robustness tests | Edge cases and additional quality tests |

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

## Hierarchical Testing Structure

Our testing approach follows a natural hierarchy that mirrors the compositional structure of Samstraumr systems:

### L0: Atomic Boundary Tests (ABT)
- **Focus**: Individual tubes in isolation
- **Examples**: UUID generation, state management, basic input/output
- **Purpose**: Ensure each tube functions correctly as a standalone component
- **Conceptual Foundation**: These tests verify the foundational properties of tubes as self-aware components with clear identities and boundaries.

```gherkin
@ATL @L0_Tube @Identity
Scenario: Tube initializes with a unique ID and logs environment details
  Given the operating environment is ready
  When a new Tube is instantiated with reason "Test Initialization"
  Then the Tube should initialize with a unique UUID
  And the Tube should log its environment details
```

### L1: Inter-Tube Feature Tests (ITFT)
- **Focus**: Connected tubes forming bundles
- **Examples**: Data transformation pipelines, validation flows
- **Purpose**: Verify that tubes can communicate and collaborate effectively
- **Conceptual Foundation**: These tests ensure that tubes maintain their integrity while participating in collective behavior, similar to how cells in tissues work together while maintaining their individual functions.

```gherkin
@ATL @L1_Bundle @Flow @Transformer
Scenario: Basic tubes connect into a data transformation bundle
  Given tubes are instantiated for a simple transformation bundle
  When the tubes are connected in a linear sequence
  Then data should flow from the source tube through the transformer tube to the sink tube
```

### L2: Composite Tube Interaction Tests (CTIT)
- **Focus**: Bundles forming machines
- **Examples**: State propagation, machine-level adaptations
- **Purpose**: Test how bundles interact within a larger machine context
- **Conceptual Foundation**: These tests verify emergent properties that arise when bundles interact, similar to how organ systems in a body create higher-level behaviors that aren't present in individual organs.

```gherkin
@ATL @L2_Machine @State
Scenario: Machine initializes with proper state hierarchy
  Given a machine with multiple bundles is instantiated
  When the machine completes initialization
  Then each bundle should have its own state
  And the machine should have a unified state view
```

### L3: Machine Construct Validation Tests (MCVT)
- **Focus**: Complete system behavior
- **Examples**: System resilience, performance under load
- **Purpose**: Validate end-to-end system functionality and non-functional requirements
- **Conceptual Foundation**: These tests focus on the highest level of system organization, where multiple machines interact with each other and the external environment, demonstrating holistic emergent properties.

```gherkin
@ATL @L3_System @Resilience
Scenario: System recovers from component failures
  Given a complete system with redundant components is running
  When a critical component fails
  Then the system should detect the failure
  And circuit breakers should isolate the failure
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

**Example:**
```gherkin
@ATL @L1_Bundle @Observer
Scenario: Observer tube monitors data flow without interference
  Given a monitor tube is initialized to observe multiple signals
  When 100 data packets flow through the observed tube
  Then the observer tube should log all 100 packets
  And the observer's overhead should be less than 25% of total processing time
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

Samstraumr tests are designed to be flexible and targeted, allowing you to focus on specific aspects of the system:

### Basic Test Execution

```bash
# Run all tests
mvn test

# Run all critical tests
mvn test -Dcucumber.filter.tags="@ATL"

# Run all tests for a specific level
mvn test -Dcucumber.filter.tags="@L0_Tube"
```

### Targeted Testing

```bash
# Run tests for a specific capability
mvn test -Dcucumber.filter.tags="@Identity"

# Run tests for a specific lifecycle phase
mvn test -Dcucumber.filter.tags="@Init"

# Combine tags for more specific test subsets
mvn test -Dcucumber.filter.tags="@L0_Tube and @Init"
mvn test -Dcucumber.filter.tags="@ATL and @Resilience"
```

### Performance-Optimized Testing

For faster test execution, especially in CI/CD pipelines:

```bash
# Run optimized tests with the performance script
./build-performance.sh test -P atl-tests

# Run with custom thread count for parallelization
mvn test -T 12 -P atl-tests

# Skip quality checks during test runs
mvn test -P skip-quality-checks
```

### Test Reports

Cucumber generates comprehensive reports that provide insights into test results:

```bash
# Generate HTML report
mvn test -Dcucumber.plugin="html:target/cucumber-reports/cucumber.html"

# Generate JSON report
mvn test -Dcucumber.plugin="json:target/cucumber-reports/cucumber.json"
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