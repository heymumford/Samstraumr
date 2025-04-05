<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Test Tags and Annotations

This document provides a comprehensive reference for the test tags and annotations used in S8r, combining both industry-standard and domain-specific terminology.

## Table of Contents

1. [Introduction](#introduction)
2. [Dual Terminology Approach](#dual-terminology-approach)
3. [Industry-Standard Annotations](#industry-standard-annotations)
4. [Domain-Specific Annotations](#domain-specific-annotations)
5. [Criticality Annotations](#criticality-annotations)
6. [Hierarchical Structure](#hierarchical-structure)
7. [Biological Lifecycle Tags](#biological-lifecycle-tags)
8. [Initiative and Epic Tags](#initiative-and-epic-tags)
9. [Test Type Tags](#test-type-tags)
10. [Capability Tags](#capability-tags)
11. [Lifecycle Tags](#lifecycle-tags)
12. [Pattern Tags](#pattern-tags)
13. [Non-functional Tags](#non-functional-tags)
14. [Using Tags in Combination](#using-tags-in-combination)
15. [Implementation](#implementation)
16. [Running Tests with Tags](#running-tests-with-tags)
17. [Best Practices](#best-practices)

## Introduction

S8r uses a comprehensive tagging system to organize and categorize tests. This ontology is designed to align with systems theory principles and support the complete testing strategy, covering both JUnit and BDD testing approaches.

## Dual Terminology Approach

S8r maintains both domain-specific terminology and industry-standard terminology to ensure expressive, domain-aligned tests while maintaining compatibility with standard testing practices.

## Industry-Standard Annotations

|     Annotation     |                  Description                  |                         Usage                         |
|--------------------|-----------------------------------------------|-------------------------------------------------------|
| `@UnitTest`        | Tests for a single component in isolation     | `@UnitTest public class UserValidatorTest`            |
| `@ComponentTest`   | Tests for a group of collaborating components | `@ComponentTest public class UserServiceTest`         |
| `@IntegrationTest` | Tests for interactions between subsystems     | `@IntegrationTest public class PaymentProcessingTest` |
| `@ApiTest`         | Tests for API behavior                        | `@ApiTest public class RestApiTest`                   |
| `@SystemTest`      | Tests for entire system behaviors             | `@SystemTest public class EndToEndOrderTest`          |
| `@EndToEndTest`    | Tests that verify complete business flows     | `@EndToEndTest public class UserJourneyTest`          |
| `@PropertyTest`    | Tests based on property verification          | `@PropertyTest public class DataConsistencyTest`      |
| `@SmokeTest`       | Basic verification tests                      | `@SmokeTest public class BasicFunctionalityTest`      |

## Domain-Specific Annotations

|         Tag          |                      TBD Term                       | TBD Acronym |           Description            |      Test Type       | Industry Equivalent |
|----------------------|-----------------------------------------------------|-------------|----------------------------------|----------------------|---------------------|
| `@ComponentTest`     | Atomic Boundary Testing                             | ABT         | Individual component unit tests  | Component Tests      | `@UnitTest`         |
| `@CompositeTest`     | Composite Component Interaction Testing             | CTIT        | Connected components tests       | Composite Tests      | `@ComponentTest`    |
| `@FlowTest`          | Inter-Component Feature Testing                     | ITFT        | Single component data flow tests | Flow Tests           | `@IntegrationTest`  |
| `@MachineTest`       | Machine Construct Validation Testing                | MCVT        | End-to-end machine tests         | Machine Tests        | `@ApiTest`          |
| `@StreamTest`        | Composite Component Interaction Testing (External)  | CTIT        | External integration tests       | Stream Tests         | `@SystemTest`       |
| `@AcceptanceTest`    | Machine Construct Validation Testing (User-focused) | MCVT        | Business requirement validation  | BDD Acceptance Tests | `@EndToEndTest`     |
| `@AdaptationTest`    | Inter-Component Feature Testing (Advanced)          | ITFT        | Property-based adaptation tests  | Adaptation Tests     | `@PropertyTest`     |
| `@OrchestrationTest` | Basic Assembly Testing                              | BAT         | Basic system assembly tests      | Orchestration Tests  | `@SmokeTest`        |

## Criticality Annotations

Tests are divided into critical and robustness categories to prioritize execution, following TBD terminology:

|  Tag   |    TBD Term    | TBD Pronunciation |                         Description                          |                 Example                 |
|--------|----------------|-------------------|--------------------------------------------------------------|-----------------------------------------|
| `@ATL` | Above The Line | "Attle"           | Critical, must-pass tests that ensure Components don't break | Core functionality tests that must pass |
| `@BTL` | Below The Line | "Bottle"          | Detailed, edge-case tests that make Components bulletproof   | Edge cases and additional quality tests |

## Hierarchical Structure

Tests are organized in a hierarchical manner, mirroring the compositional structure of S8r systems, aligned with TBD levels:

|       Tag       |    TBD Level     | TBD Primary Test Type |            Description            |                       Example                       |
|-----------------|------------------|-----------------------|-----------------------------------|-----------------------------------------------------|
| `@L0_Component` | Atomic Level     | ABT                   | Atomic component tests            | Testing a single component in isolation             |
| `@L1_Composite` | Composite Level  | CTIT                  | Composite-level integration tests | Testing connected components forming a composite    |
| `@L2_Machine`   | Machine Level    | MCVT                  | Complex machine composition tests | Testing interconnected composites forming a machine |
| `@L3_System`    | Acceptance Level | MCVT (User-focused)   | Full system tests                 | Testing complete systems with multiple machines     |

## Biological Lifecycle Tags

Tests are categorized based on the biological lifecycle model for components, representing different developmental phases. This model aligns software component development with biological development stages:

|      Tag       |      Biological Phase      |        Description        |                       Test Focus                        |   Primary Initiative   |        Key Capabilities        |
|----------------|----------------------------|---------------------------|---------------------------------------------------------|------------------------|--------------------------------|
| `@Conception`  | Fertilization/Zygote       | Initial creation          | Testing component creation and initial identity         | `@SubstrateIdentity`   | `@Identity`, `@Awareness`      |
| `@Embryonic`   | Cleavage/Early Development | Basic structure formation | Testing basic structure and initialization              | `@StructuralIdentity`  | `@Structure`, `@Resilience`    |
| `@Infancy`     | Early Growth               | Initial capabilities      | Testing basic functionality and early interactions      | `@MemoryIdentity`      | `@State`, `@Learning`          |
| `@Childhood`   | Growth and Development     | Active development        | Testing developing capabilities and adaptations         | `@FunctionalIdentity`  | `@Function`, `@Flow`           |
| `@Adolescence` | Rapid Changes              | Significant evolution     | Testing changing capabilities and transitions           | `@AdaptiveIdentity`    | `@Adaptation`, `@Optimization` |
| `@Adulthood`   | Maturity/Full Function     | Complete capabilities     | Testing full functionality and integration              | `@CognitiveIdentity`   | `@Integration`, `@Awareness`   |
| `@Maturity`    | Optimization Phase         | Refined behavior          | Testing optimized performance and specialized functions | `@SpecializedIdentity` | `@Performance`, `@Efficiency`  |
| `@Senescence`  | Aging/Degradation          | Graceful degradation      | Testing adaptive failure handling                       | `@ResilienceIdentity`  | `@Resilience`, `@Recovery`     |
| `@Termination` | Death Phase                | Shutdown/cleanup          | Testing proper resource release and state archiving     | `@ClosureIdentity`     | `@Cleanup`, `@Archive`         |
| `@Legacy`      | Posthumous                 | Knowledge preservation    | Testing knowledge transfer and historical data access   | `@HeritageIdentity`    | `@Knowledge`, `@History`       |

### Implemented phases

The following phases have been fully implemented with feature files and step definitions:

1. **Conception Phase** (`@Conception`): Tests that verify proper component creation and initial identity establishment.
   - Feature file: `conception-phase-tests.feature`
   - Step definitions: `ConceptionPhaseSteps.java`
   - Primary initiative: `@SubstrateIdentity`
   - Key focus: UUID generation, creation timestamps, environmental context capture
2. **Embryonic Phase** (`@Embryonic`): Tests that verify the formation of basic component structure.
   - Feature file: `embryonic-phase-tests.feature`
   - Step definitions: `EmbryonicPhaseSteps.java`
   - Primary initiative: `@StructuralIdentity`
   - Key focus: Connection points, internal structure, structural resilience
3. **Infancy Phase** (`@Infancy`): Tests that verify early development of component capabilities.
   - Feature file: `infancy-phase-tests.feature`
   - Step definitions: `InfancyPhaseSteps.java`
   - Primary initiative: `@MemoryIdentity`
   - Key focus: State persistence, experience recording, basic learning
4. **Childhood Phase** (`@Childhood`): Tests that verify functional development during active growth.
   - Feature file: `childhood-phase-tests.feature`
   - Step definitions: `ChildhoodPhaseSteps.java`
   - Primary initiative: `@FunctionalIdentity`
   - Key focus: Data processing, state learning, error recovery

The remaining phases will be implemented incrementally as the framework matures.

## Initiative and Epic Tags

Tests are organized according to the Component Lifecycle Model initiatives and epics:

### Initiative tags

|           Tag           |                  Description                  |                        Focus Area                        |
|-------------------------|-----------------------------------------------|----------------------------------------------------------|
| `@SubstrateIdentity`    | Biological continuity analog                  | Tests for physical continuity aspects of components      |
| `@MemoryIdentity`       | Psychological continuity analog               | Tests for cognitive and memory aspects of components     |
| `@NarrativeIdentity`    | Personal narrative analog                     | Tests for purpose and self-concept aspects of components |
| `@CrossCuttingIdentity` | Identity aspects spanning multiple dimensions | Tests for identity features across categories            |
| `@S8rSpecific`          | Framework-specific verifications              | Tests specific to S8r implementation                     |

### Epic tags

|            Tag            |    Initiative     |           Description           |              Examples              |
|---------------------------|-------------------|---------------------------------|------------------------------------|
| `@UniqueIdentification`   | SubstrateIdentity | Tests for uniqueness aspects    | UUID generation, immutability      |
| `@CreationTracking`       | SubstrateIdentity | Tests for creation metadata     | Timestamps, environmental capture  |
| `@LineageManagement`      | SubstrateIdentity | Tests for ancestry tracking     | Parent-child relationships         |
| `@HierarchicalAddressing` | SubstrateIdentity | Tests for addressing            | Hierarchical IDs, resolution       |
| `@EnvironmentalContext`   | SubstrateIdentity | Tests for environment awareness | System properties, resources       |
| `@StatePersistence`       | MemoryIdentity    | Tests for state management      | State transitions, history         |
| `@ExperienceRecording`    | MemoryIdentity    | Tests for experience tracking   | Processing records, categorization |
| `@AdaptiveLearning`       | MemoryIdentity    | Tests for learning capability   | Pattern recognition, adaptation    |
| `@PerformanceAwareness`   | MemoryIdentity    | Tests for self-monitoring       | Metrics, optimizations             |
| `@PurposePreservation`    | MemoryIdentity    | Tests for purpose management    | Core function, mission alignment   |

## Test Type Tags

Tests are categorized based on their validation approach:

|     Tag     |         Description         |                             When to Use                              |
|-------------|-----------------------------|----------------------------------------------------------------------|
| `@Positive` | Tests for expected behavior | Verifying that components function correctly in normal conditions    |
| `@Negative` | Tests for error handling    | Verifying that components handle errors and edge cases appropriately |

## Capability Tags

Tests focusing on the fundamental capabilities of S8r components:

|     Tag      |                Description                |                  Example                  |
|--------------|-------------------------------------------|-------------------------------------------|
| `@Identity`  | UUID, naming, identification tests        | Testing unique ID generation              |
| `@Flow`      | Data movement and transformation          | Testing data processing pipelines         |
| `@State`     | State management and transitions          | Testing state transitions and propagation |
| `@Awareness` | Self-monitoring and environment awareness | Testing monitoring capabilities           |

## Lifecycle Tags

Tests organized by component lifecycle phase:

|      Tag       |            Description            |                Example                 |
|----------------|-----------------------------------|----------------------------------------|
| `@Init`        | Initialization/construction tests | Testing component creation             |
| `@Runtime`     | Normal operation tests            | Testing standard processing operations |
| `@Termination` | Shutdown/cleanup tests            | Testing proper resource cleanup        |

## Pattern Tags

Tests for specific architectural patterns implemented by components:

|        Tag        |        Description        |                      Example                      |
|-------------------|---------------------------|---------------------------------------------------|
| `@Observer`       | Monitoring pattern tests  | Testing components that monitor without modifying |
| `@Transformer`    | Data transformation tests | Testing data transformation capabilities          |
| `@Validator`      | Input validation tests    | Testing data validation rules                     |
| `@CircuitBreaker` | Fault tolerance tests     | Testing isolation of failures                     |

## Non-Functional Tags

Tests focused on non-functional quality attributes:

|      Tag       |         Description         |                Example                 |
|----------------|-----------------------------|----------------------------------------|
| `@Performance` | Speed and resource usage    | Testing resource utilization and speed |
| `@Resilience`  | Recovery and fault handling | Testing recovery from failures         |
| `@Scale`       | Load and scaling tests      | Testing behavior under increased load  |

## Using Tags in Combination

Tags can be combined to create more specific test subsets. Examples:

- `@L0_Component @ATL @Identity @Init`: Critical tests for component identity initialization
- `@L1_Composite @BTL @Flow @Runtime`: Robustness tests for runtime composite data flow
- `@L2_Machine @ATL @State @Transformer`: Critical tests for machine state transformation

## Implementation

All annotations are implemented as JUnit 5 tags, allowing for flexible filtering in test runs.

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("Unit")
@Tag("Component")
public @interface UnitTest {
}
```

## Running Tests with Tags

Tests can be run using Maven profiles or custom scripts. The enhanced tagging system supports more granular test selection:

```bash
# Run unit tests
mvn test -P unit-tests

# Run component tests
mvn test -P component-tests

# Run tests with specific tag combination
mvn test -Dcucumber.filter.tags="@L0_Component and @Identity"

# Run critical tests only
mvn test -P atl-tests

# Run tests by lifecycle phase
mvn test -P conception-tests
mvn test -P infancy-tests

# Run tests by identity initiative
mvn test -P substrate-identity-tests
mvn test -P memory-identity-tests

# Run tests by test type
mvn test -P positive-tests
mvn test -P negative-tests

# Run tests with complex tag combinations
mvn test -Dcucumber.filter.tags="@SubstrateIdentity and @Conception and @Positive"
mvn test -Dcucumber.filter.tags="@MemoryIdentity and @ATL and not @Negative"

# Use the CLI to run tests with tags
./s8r test --tags="@Conception"
./s8r test --tags="@SubstrateIdentity and @Positive"
./s8r test --tags="@MemoryIdentity and @Infancy"

# Map bio tags to test types
./s8r test map-tags initiative substrate  # Returns "SubstrateIdentity"
./s8r test map-tags phase conception      # Returns "Conception"
./s8r test map-tags epic uniqueidentification  # Returns "UniqueIdentification"
```

## Test Mapping

The framework maps between terminology sets, allowing flexible test selection:

### Standard terminology mapping

|      When you specify       |                 Tests included                  |
|-----------------------------|-------------------------------------------------|
| `unit-tests` profile        | Tests with `@UnitTest` or `@ComponentTest`      |
| `component-tests` profile   | Tests with `@ComponentTest` or `@UnitTest`      |
| `composite-tests` profile   | Tests with `@CompositeTest` or `@ComponentTest` |
| `integration-tests` profile | Tests with `@IntegrationTest` or `@FlowTest`    |

### Biological lifecycle mapping

|          Profile           |         Tests included          |
|----------------------------|---------------------------------|
| `conception-tests`         | Tests with `@Conception`        |
| `infancy-tests`            | Tests with `@Infancy`           |
| `substrate-identity-tests` | Tests with `@SubstrateIdentity` |
| `memory-identity-tests`    | Tests with `@MemoryIdentity`    |
| `positive-tests`           | Tests with `@Positive`          |
| `negative-tests`           | Tests with `@Negative`          |

## Best Practices

1. Each test should have these essential tag categories:
   - One hierarchical tag (`@L0_Component`, `@L1_Composite`, etc.)
   - One critical path tag (`@ATL` or `@BTL`)
   - One biological phase tag (`@Conception`, `@Infancy`, etc.)
   - One initiative tag (`@SubstrateIdentity`, `@MemoryIdentity`, etc.)
   - One test type tag (`@Positive` or `@Negative`)
   - One capability or pattern tag
2. Tests should be executed in order of biological development:
   - Start with `@Conception` phase tests
   - Progress through `@Embryonic`, `@Infancy`, etc.
   - End with `@Termination` and `@Legacy` phase tests
3. Tests should be executed in order of architectural complexity:
   - Start with `@L0_Component` tests
   - Progress to `@L1_Composite`, `@L2_Machine`, and finally `@L3_System` tests
4. Critical path tests should always be run first:
   - Always run `@ATL` tests before `@BTL` tests
   - Failures in `@ATL` tests should block further testing
5. Tag combinations should be used for targeted testing:
   - For creation phase issues: `@Conception and @SubstrateIdentity`
   - For early development memory issues: `@Infancy and @MemoryIdentity`
   - For specific capabilities: `@Identity and @SubstrateIdentity`
   - For error handling: `@Negative and @BTL`
6. **Consistency**: Use annotations consistently across the codebase
7. **Granularity**: Use specific tag combinations for precise test targeting
8. **Completeness**: Include all relevant tag categories for maximum flexibility
9. **Expressiveness**: Choose tags that clearly communicate test purpose and scope
