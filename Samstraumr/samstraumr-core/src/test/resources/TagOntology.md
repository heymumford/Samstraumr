# Samstraumr Test Tag Ontology

This document defines the tag ontology for organizing and categorizing Samstraumr tests. This ontology is designed to align with systems theory principles and support the complete testing strategy for Samstraumr, covering both JUnit and BDD testing approaches.

## Test Type Tags

Tags that identify the primary test type and corresponding testing tool:

| Tag | Description | Test Type | Technology |
|-----|-------------|-----------|------------|
| `@TubeTest` | Individual tube unit tests | Tube Tests | JUnit 5 |
| `@FlowTest` | Single tube data flow tests | Flow Tests | JUnit 5 |
| `@BundleTest` | Connected tubes component tests | Bundle Tests | JUnit 5 |
| `@StreamTest` | External integration tests | Stream Tests | JUnit 5 + TestContainers |
| `@AdaptationTest` | Property-based adaptation tests | Adaptation Tests | JUnit 5 (custom) |
| `@L2_Machine` | End-to-end machine tests | Machine Tests | Cucumber |
| `@Acceptance` | Business requirement validation | BDD Acceptance Tests | Cucumber |

## Hierarchical Structure

Tests are organized in a hierarchical manner, mirroring the compositional structure of Samstraumr systems:

| Tag | Description | Example |
|-----|-------------|---------|
| `@L0_Tube` | Atomic tube component tests | Testing a single tube in isolation |
| `@L1_Bundle` | Bundle-level integration tests | Testing connected tubes forming a bundle |
| `@L2_Machine` | Complex machine composition tests | Testing interconnected bundles forming a machine |
| `@L3_System` | Full system tests | Testing complete systems with multiple machines |

## Critical Path Categorization

Tests are divided into critical and robustness categories to prioritize execution:

| Tag | Description | Example |
|-----|-------------|---------|
| `@ATL` | Above-the-line critical tests (must pass) | Core functionality tests that must pass |
| `@BTL` | Below-the-line robustness tests | Edge cases and additional quality tests |

## Core Capabilities

Tests focusing on the fundamental capabilities of Samstraumr components:

| Tag | Description | Example |
|-----|-------------|---------|
| `@Identity` | UUID, naming, identification tests | Testing unique ID generation |
| `@Flow` | Data movement and transformation | Testing data processing pipelines |
| `@State` | State management and transitions | Testing state transitions and propagation |
| `@Awareness` | Self-monitoring and environment awareness | Testing monitoring capabilities |

## Lifecycle Tests

Tests organized by component lifecycle phase:

| Tag | Description | Example |
|-----|-------------|---------|
| `@Init` | Initialization/construction tests | Testing component creation |
| `@Runtime` | Normal operation tests | Testing standard processing operations |
| `@Termination` | Shutdown/cleanup tests | Testing proper resource cleanup |

## Patterns

Tests for specific architectural patterns implemented by tubes:

| Tag | Description | Example |
|-----|-------------|---------|
| `@Observer` | Monitoring pattern tests | Testing tubes that monitor without modifying |
| `@Transformer` | Data transformation tests | Testing data transformation capabilities |
| `@Validator` | Input validation tests | Testing data validation rules |
| `@CircuitBreaker` | Fault tolerance tests | Testing isolation of failures |

## Non-functional Requirements

Tests focused on non-functional quality attributes:

| Tag | Description | Example |
|-----|-------------|---------|
| `@Performance` | Speed and resource usage | Testing resource utilization and speed |
| `@Resilience` | Recovery and fault handling | Testing recovery from failures |
| `@Scale` | Load and scaling tests | Testing behavior under increased load |

## Tag Combinations

Tags can be combined to create more specific test subsets. Examples:

```
@L0_Tube @Identity           # All identity tests for atomic tubes
@ATL @Init                   # All critical initialization tests
@L2_Machine @State @Runtime  # Runtime state tests for machines
@BTL @Resilience             # All robustness resilience tests
```

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
