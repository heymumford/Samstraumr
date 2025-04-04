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

> **⚠️ UPDATED**: We've developed an enhanced tag ontology based on the biological lifecycle model.
> 
> Please refer to the comprehensive [Test Tags and Annotations](./TestTagsAndAnnotations.md) documentation for the complete tagging strategy.

To organize our tests in a way that reflects Samstraumr's core principles, we've developed a comprehensive tag ontology that works in conjunction with our test types.

### Biological Lifecycle Tags

Our enhanced tag ontology is built around the biological lifecycle model, which organizes tests across different developmental phases:

| Tag | Biological Phase | Description | Test Focus | Primary Initiative |
|-----|------------------|-------------|------------|-------------------|
| `@Conception` | Fertilization/Zygote | Initial creation | Testing tube creation and initial identity | `@SubstrateIdentity` |
| `@Embryonic` | Cleavage/Early Development | Basic structure formation | Testing basic structure and initialization | `@StructuralIdentity` |
| `@Infancy` | Early Growth | Initial capabilities | Testing basic functionality and early interactions | `@MemoryIdentity` |
| `@Childhood` | Growth and Development | Active development | Testing developing capabilities and adaptations | `@FunctionalIdentity` |
| `@Adolescence` | Rapid Changes | Significant evolution | Testing changing capabilities and transitions | `@AdaptiveIdentity` |
| `@Adulthood` | Maturity/Full Function | Complete capabilities | Testing full functionality and integration | `@CognitiveIdentity` |
| `@Maturity` | Optimization Phase | Refined behavior | Testing optimized performance and specialized functions | `@SpecializedIdentity` |
| `@Senescence` | Aging/Degradation | Graceful degradation | Testing adaptive failure handling | `@ResilienceIdentity` |
| `@Termination` | Death Phase | Shutdown/cleanup | Testing proper resource release and state archiving | `@ClosureIdentity` |
| `@Legacy` | Posthumous | Knowledge preservation | Testing knowledge transfer and historical data access | `@HeritageIdentity` |

The first four phases (Conception, Embryonic, Infancy, and Childhood) have been fully implemented with feature files and step definitions.

### Initiative and Epic Tags

Tests are organized according to the Tube Lifecycle Model initiatives and epics, aligning with the biological metaphor:

#### Initiative Tags

| Tag | Description | Focus Area | Associated Lifecycle Phase |
|-----|-------------|------------|----------------------------|
| `@SubstrateIdentity` | Biological continuity analog | Tests for physical continuity aspects of tubes | Conception |
| `@StructuralIdentity` | Basic structure formation | Tests for tube structure and form | Embryonic |
| `@MemoryIdentity` | Psychological continuity analog | Tests for cognitive and memory aspects of tubes | Infancy |
| `@FunctionalIdentity` | Operational capabilities | Tests for tube's functional behavior | Childhood |
| `@AdaptiveIdentity` | Behavioral adaptation | Tests for tube's ability to adapt | Adolescence |
| `@CognitiveIdentity` | Full interoperational awareness | Tests for tube's cognitive capabilities | Adulthood |
| `@SpecializedIdentity` | Refined operation | Tests for specialized tube functions | Maturity |
| `@ResilienceIdentity` | Graceful degradation | Tests for resilience and recovery | Senescence |
| `@ClosureIdentity` | Proper termination | Tests for clean shutdown and archival | Termination |
| `@HeritageIdentity` | Knowledge preservation | Tests for knowledge transfer | Legacy |

#### Epic Tags by Initiative

| Tag | Initiative | Description | Examples |
|-----|------------|-------------|----------|
| `@UniqueIdentification` | SubstrateIdentity | Tests for uniqueness aspects | UUID generation, immutability |
| `@CreationTracking` | SubstrateIdentity | Tests for creation metadata | Timestamps, environmental capture |
| `@ConnectionPoints` | StructuralIdentity | Tests for connection framework | Input/output ports, connectivity |
| `@InternalStructure` | StructuralIdentity | Tests for tube structure | Processing mechanisms, boundaries |
| `@StatePersistence` | MemoryIdentity | Tests for state management | State transitions, history |
| `@ExperienceRecording` | MemoryIdentity | Tests for experience tracking | Processing records, categorization |
| `@DataProcessing` | FunctionalIdentity | Tests for data handling | Processing operations, transformations |
| `@StateLearning` | FunctionalIdentity | Tests for operational learning | Pattern recognition, optimization |

### Legacy Tags

For backward compatibility, we also maintain our original tag ontology:

#### Test Type Tags

| Tag | Description | Test Type |
|-----|-------------|-----------|
| `@TubeTest` | Individual tube unit tests | Tube Tests |
| `@FlowTest` | Single tube data flow tests | Flow Tests |
| `@BundleTest` | Connected tubes component tests | Bundle Tests |
| ... | ... | ... |

#### Hierarchical Structure

| Tag | Description | Example |
|-----|-------------|---------|
| `@L0_Tube` | Atomic tube component tests | Testing a single tube in isolation |
| `@L1_Bundle` | Bundle-level integration tests | Testing connected tubes forming a bundle |
| ... | ... | ... |

For a complete reference of all tags and how to use them effectively, see the [Test Tags and Annotations](./TestTagsAndAnnotations.md) document.

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

Samstraumr provides a unified test runner script that simplifies test execution while supporting various test types, including our new biological lifecycle-based tags.

### Using the Test Runner Script

The recommended way to run Samstraumr tests is using the test runner script:

```bash
# Run all tests
./util/test-run.sh all

# Run tests by criticality
./util/test-run.sh atl          # Run Above The Line tests (critical, must pass)
./util/test-run.sh btl          # Run Below The Line tests (robustness, non-blocking)

# Run tests by traditional type
./util/test-run.sh tube         # Run Tube Tests (unit)
./util/test-run.sh flow         # Run Flow Tests (integration)
./util/test-run.sh bundle       # Run Bundle Tests (component)
./util/test-run.sh stream       # Run Stream Tests (system)
./util/test-run.sh adaptation   # Run Adaptation Tests (property)
./util/test-run.sh machine      # Run Machine Tests (e2e)
./util/test-run.sh acceptance   # Run BDD Acceptance Tests (business)

# Run tests by biological lifecycle phase
./util/test-run.sh --tags="@Conception"   # Run initial creation phase tests
./util/test-run.sh --tags="@Embryonic"    # Run structural formation phase tests
./util/test-run.sh --tags="@Infancy"      # Run early capability development tests
./util/test-run.sh --tags="@Childhood"    # Run functional development tests
./util/test-run.sh --tags="@Adulthood"    # Run mature functionality tests

# Run tests by initiative 
./util/test-run.sh --tags="@SubstrateIdentity"    # Run physical continuity tests
./util/test-run.sh --tags="@StructuralIdentity"   # Run structure formation tests
./util/test-run.sh --tags="@MemoryIdentity"       # Run state/memory tests
./util/test-run.sh --tags="@FunctionalIdentity"   # Run functional capability tests

# Run by test type
./util/test-run.sh --tags="@Positive"     # Run tests for expected behavior
./util/test-run.sh --tags="@Negative"     # Run tests for error handling

For help with all available options:

```bash
./util/test-run.sh --help
```

### Maven Profile Test Execution

For more direct control, you can use Maven profiles:

```bash
# Run biological phase tests
mvn test -P conception-tests     # Run conception phase tests
mvn test -P infancy-tests        # Run infancy phase tests

# Run initiative-specific tests
mvn test -P substrate-identity-tests   # Run substrate identity tests
mvn test -P memory-identity-tests      # Run memory identity tests

# Run positive/negative tests
mvn test -P positive-tests       # Run tests for expected behavior
mvn test -P negative-tests       # Run tests for error handling
```

### Tagged Testing with Cucumber

For maximum flexibility, use cucumber tag combinations:

```bash
# Run tests with individual tags
mvn test -Dcucumber.filter.tags="@Conception"          # Run conception phase tests
mvn test -Dcucumber.filter.tags="@SubstrateIdentity"   # Run substrate identity tests

# Combine tags for precise test targeting
mvn test -Dcucumber.filter.tags="@SubstrateIdentity and @Conception"
mvn test -Dcucumber.filter.tags="@MemoryIdentity and @Infancy and @Positive"
mvn test -Dcucumber.filter.tags="@Conception and @ATL and @Identity"
mvn test -Dcucumber.filter.tags="@L0_Tube and @Positive and not @BTL"

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
