# Samstraumr Test Tags and Annotations

This document provides a comprehensive reference for the test tags and annotations used in Samstraumr, combining both industry-standard and domain-specific terminology.

## Table of Contents

1. [Introduction](#introduction)
2. [Dual Terminology Approach](#dual-terminology-approach)
3. [Industry-Standard Annotations](#industry-standard-annotations)
4. [Domain-Specific Annotations](#domain-specific-annotations)
5. [Criticality Annotations](#criticality-annotations)
6. [Hierarchical Structure](#hierarchical-structure)
7. [Capability Tags](#capability-tags)
8. [Lifecycle Tags](#lifecycle-tags)
9. [Pattern Tags](#pattern-tags)
10. [Non-functional Tags](#non-functional-tags)
11. [Using Tags in Combination](#using-tags-in-combination)
12. [Implementation](#implementation)
13. [Running Tests with Tags](#running-tests-with-tags)
14. [Best Practices](#best-practices)

## Introduction

Samstraumr uses a comprehensive tagging system to organize and categorize tests. This ontology is designed to align with systems theory principles and support the complete testing strategy, covering both JUnit and BDD testing approaches.

## Dual Terminology Approach

Samstraumr maintains both domain-specific terminology and industry-standard terminology to ensure expressive, domain-aligned tests while maintaining compatibility with standard testing practices.

## Industry-Standard Annotations

| Annotation | Description | Usage |
|------------|-------------|-------|
| `@UnitTest` | Tests for a single component in isolation | `@UnitTest public class UserValidatorTest` |
| `@ComponentTest` | Tests for a group of collaborating components | `@ComponentTest public class UserServiceTest` |
| `@IntegrationTest` | Tests for interactions between subsystems | `@IntegrationTest public class PaymentProcessingTest` |
| `@ApiTest` | Tests for API behavior | `@ApiTest public class RestApiTest` |
| `@SystemTest` | Tests for entire system behaviors | `@SystemTest public class EndToEndOrderTest` |
| `@EndToEndTest` | Tests that verify complete business flows | `@EndToEndTest public class UserJourneyTest` |
| `@PropertyTest` | Tests based on property verification | `@PropertyTest public class DataConsistencyTest` |
| `@SmokeTest` | Basic verification tests | `@SmokeTest public class BasicFunctionalityTest` |

## Domain-Specific Annotations

| Tag | TBD Term | TBD Acronym | Description | Test Type | Industry Equivalent |
|-----|----------|-------------|-------------|-----------|---------------------|
| `@TubeTest` | Atomic Boundary Testing | ABT | Individual tube unit tests | Tube Tests | `@UnitTest` |
| `@BundleTest` | Composite Tube Interaction Testing | CTIT | Connected tubes component tests | Bundle Tests | `@ComponentTest` |
| `@FlowTest` | Inter-Tube Feature Testing | ITFT | Single tube data flow tests | Flow Tests | `@IntegrationTest` |
| `@MachineTest` | Machine Construct Validation Testing | MCVT | End-to-end machine tests | Machine Tests | `@ApiTest` |
| `@StreamTest` | Composite Tube Interaction Testing (External) | CTIT | External integration tests | Stream Tests | `@SystemTest` |
| `@AcceptanceTest` | Machine Construct Validation Testing (User-focused) | MCVT | Business requirement validation | BDD Acceptance Tests | `@EndToEndTest` |
| `@AdaptationTest` | Inter-Tube Feature Testing (Advanced) | ITFT | Property-based adaptation tests | Adaptation Tests | `@PropertyTest` |
| `@OrchestrationTest` | Basic Assembly Testing | BAT | Basic system assembly tests | Orchestration Tests | `@SmokeTest` |

## Criticality Annotations

Tests are divided into critical and robustness categories to prioritize execution, following TBD terminology:

| Tag | TBD Term | TBD Pronunciation | Description | Example |
|-----|----------|------------------|-------------|---------|
| `@ATL` | Above The Line | "Attle" | Critical, must-pass tests that ensure Tubes don't break | Core functionality tests that must pass |
| `@BTL` | Below The Line | "Bottle" | Detailed, edge-case tests that make Tubes bulletproof | Edge cases and additional quality tests |

## Hierarchical Structure

Tests are organized in a hierarchical manner, mirroring the compositional structure of Samstraumr systems, aligned with TBD levels:

| Tag | TBD Level | TBD Primary Test Type | Description | Example |
|-----|-----------|----------------------|-------------|---------|
| `@L0_Tube` | Atomic Level | ABT | Atomic tube component tests | Testing a single tube in isolation |
| `@L1_Bundle` | Composite Level | CTIT | Bundle-level integration tests | Testing connected tubes forming a bundle |
| `@L2_Machine` | Machine Level | MCVT | Complex machine composition tests | Testing interconnected bundles forming a machine |
| `@L3_System` | Acceptance Level | MCVT (User-focused) | Full system tests | Testing complete systems with multiple machines |

## Capability Tags

Tests focusing on the fundamental capabilities of Samstraumr components:

| Tag | Description | Example |
|-----|-------------|---------|
| `@Identity` | UUID, naming, identification tests | Testing unique ID generation |
| `@Flow` | Data movement and transformation | Testing data processing pipelines |
| `@State` | State management and transitions | Testing state transitions and propagation |
| `@Awareness` | Self-monitoring and environment awareness | Testing monitoring capabilities |

## Lifecycle Tags

Tests organized by component lifecycle phase:

| Tag | Description | Example |
|-----|-------------|---------|
| `@Init` | Initialization/construction tests | Testing component creation |
| `@Runtime` | Normal operation tests | Testing standard processing operations |
| `@Termination` | Shutdown/cleanup tests | Testing proper resource cleanup |

## Pattern Tags

Tests for specific architectural patterns implemented by tubes:

| Tag | Description | Example |
|-----|-------------|---------|
| `@Observer` | Monitoring pattern tests | Testing tubes that monitor without modifying |
| `@Transformer` | Data transformation tests | Testing data transformation capabilities |
| `@Validator` | Input validation tests | Testing data validation rules |
| `@CircuitBreaker` | Fault tolerance tests | Testing isolation of failures |

## Non-functional Tags

Tests focused on non-functional quality attributes:

| Tag | Description | Example |
|-----|-------------|---------|
| `@Performance` | Speed and resource usage | Testing resource utilization and speed |
| `@Resilience` | Recovery and fault handling | Testing recovery from failures |
| `@Scale` | Load and scaling tests | Testing behavior under increased load |

## Using Tags in Combination

Tags can be combined to create more specific test subsets. Examples:

- `@L0_Tube @ATL @Identity @Init`: Critical tests for tube identity initialization
- `@L1_Bundle @BTL @Flow @Runtime`: Robustness tests for runtime bundle data flow
- `@L2_Machine @ATL @State @Transformer`: Critical tests for machine state transformation

## Implementation

All annotations are implemented as JUnit 5 tags, allowing for flexible filtering in test runs.

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("Unit")
@Tag("Tube")
public @interface UnitTest {
}
```

## Running Tests with Tags

Tests can be run using Maven profiles or custom scripts:

```bash
# Run all unit tests (using industry standard terminology)
mvn test -P unit-tests

# Run all tube tests (using Samstraumr terminology)
mvn test -P tube-tests

# Run tests with specific tag combinations
mvn test -Dcucumber.filter.tags="@L0_Tube and @Identity"

# Run critical tests
mvn test -P atl-tests

# Using the unified script
./run-tests.sh unit    # Run unit tests
./run-tests.sh tube    # Run tube tests (equivalent)
./run-tests.sh --tags="@Identity and @ATL"  # Run with tag combination
```

## Test Mapping

The framework automatically maps between terminology sets, so using either term will include tests tagged with either annotation:

| When you specify | Tests included |
|------------------|----------------|
| `unit-tests` profile | Tests with `@UnitTest` or `@TubeTest` |
| `tube-tests` profile | Tests with `@TubeTest` or `@UnitTest` |
| `component-tests` profile | Tests with `@ComponentTest` or `@BundleTest` |
| `bundle-tests` profile | Tests with `@BundleTest` or `@ComponentTest` |

## Best Practices

1. Each test should have at minimum:
   - One hierarchical tag (`@L0_Tube`, `@L1_Bundle`, etc.)
   - One critical path tag (`@ATL` or `@BTL`)
   - One capability or pattern tag

2. Tests should be executed in order of increasing complexity:
   - Start with `@L0_Tube` tests
   - Progress to `@L1_Bundle`, `@L2_Machine`, and finally `@L3_System` tests

3. Critical path tests should always be run first:
   - Always run `@ATL` tests before `@BTL` tests
   - Failures in `@ATL` tests should block further testing

4. Tag combinations should be used for targeted testing:
   - When troubleshooting identity issues: `@Identity`
   - When testing state transitions: `@State @Runtime`
   - When testing initialization: `@Init`

5. **Consistency**: Use annotations consistently across the codebase
6. **Clarity**: Choose the most descriptive annotations for each test
7. **Completeness**: Include both industry-standard and domain-specific annotations