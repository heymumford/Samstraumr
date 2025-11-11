<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Samstraumr Testing Framework

This directory contains the testing framework for the Samstraumr project, organized according to systems theory principles and BDD best practices.

## Testing Organization

Tests are organized hierarchically following Martin Fowler's test organization principles, with tests grouped by component level complexity:

### Hierarchical Structure

- `@L0_Tube` - Atomic tube component tests
- `@L1_Bundle` - Bundle-level integration tests
- `@L2_Machine` - Complex machine composition tests
- `@L3_System` - Full system tests

### Critical Path Categorization

- `@ATL` - Above-the-line critical tests (must pass)
- `@BTL` - Below-the-line robustness tests

### Core Capabilities

- `@Identity` - UUID, naming, identification tests
- `@Flow` - Data movement and transformation
- `@State` - State management and transitions
- `@Awareness` - Self-monitoring and environment awareness

### Lifecycle Tests

- `@Init` - Initialization/construction tests
- `@Runtime` - Normal operation tests
- `@Termination` - Shutdown/cleanup tests

### Patterns

- `@Observer` - Monitoring pattern tests
- `@Transformer` - Data transformation tests
- `@Validator` - Input validation tests
- `@CircuitBreaker` - Fault tolerance tests

### Non-functional

- `@Performance` - Speed and resource usage
- `@Resilience` - Recovery and fault handling
- `@Scale` - Load and scaling tests

## Running Tests

Tests can be run using various tag combinations to target specific test types:

```bash
# Run all tests
mvn test

# Run all critical tests
mvn test -Dcucumber.filter.tags="@ATL"

# Run all tests for a specific level
mvn test -Dcucumber.filter.tags="@L0_Tube"

# Run tests for a specific capability
mvn test -Dcucumber.filter.tags="@Identity"

# Run tests for a specific lifecycle phase
mvn test -Dcucumber.filter.tags="@Init"

# Combine tags for more specific test subsets
mvn test -Dcucumber.filter.tags="@L0_Tube and @Init"
mvn test -Dcucumber.filter.tags="@ATL and @Resilience"
```

## Test Directory Structure

```
src/test/
├── java/org/samstraumr/tube/
│   ├── RunCucumberTest.java (Main test runner)
│   └── steps/
│       ├── TubeInitializationSteps.java
│       ├── BundleConnectionSteps.java
│       ├── MachineStateSteps.java
│       └── SystemResilienceSteps.java
└── resources/
    ├── cucumber.properties
    └── tube/features/
        ├── L0_Tube/
        │   └── TubeInitializationTest.feature
        ├── L1_Bundle/
        │   └── BundleConnectionTest.feature
        ├── L2_Machine/
        │   └── MachineStateTest.feature
        └── L3_System/
            └── SystemResilienceTest.feature
```

## BDD Approach

Tests follow the Behavior-Driven Development (BDD) approach with Cucumber:
- Features are written in Gherkin syntax
- Each scenario describes a specific behavior
- Tags provide organization and selective execution
- Step definitions implement the test logic

For more information on the BDD approach, see [rationale-on-using-bdd.md](../../rationale-on-using-bdd.md) and [what-is-bdd.md](../../what-is-bdd.md).
