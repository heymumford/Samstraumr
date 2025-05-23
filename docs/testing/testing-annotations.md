<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Testing Annotations

> **⚠️ DEPRECATED**: This document has been merged into a comprehensive test tags and annotations document.
>
> Please refer to the consolidated document for the most up-to-date information:
> [Test Tags and Annotations](test-tags-and-annotations.md)

This document describes the test annotations used in Samstraumr, which follow a dual terminology approach to support both domain-specific and industry-standard testing concepts.

## Dual Terminology Approach

Samstraumr maintains both domain-specific terminology and industry-standard terminology to ensure expressive, domain-aligned tests while maintaining compatibility with standard testing practices.

### Industry-standard annotations

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

### Domain-specific annotations

|   Annotation   |         Description          | Industry Equivalent |
|----------------|------------------------------|---------------------|
| `@TubeTest`    | Tests for individual tubes   | `@UnitTest`         |
| `@BundleTest`  | Tests for bundles of tubes   | `@ComponentTest`    |
| `@MachineTest` | Tests for composite machines | `@IntegrationTest`  |
| `@SystemTest`  | Tests for entire systems     | `@SystemTest`       |
| `@FlowTest`    | Tests for end-to-end flows   | `@EndToEndTest`     |

### Criticality annotations

| Annotation |                           Description                           |
|------------|-----------------------------------------------------------------|
| `@ATL`     | Above The Line - Critical path tests that must always pass      |
| `@BTL`     | Below The Line - Robustness tests for edge cases and resilience |

### Layer-based annotations

|  Annotation   |                  Description                   |
|---------------|------------------------------------------------|
| `@L0_Tube`    | Tests focusing on individual tubes             |
| `@L1_Bundle`  | Tests focusing on bundles of tubes             |
| `@L2_Machine` | Tests focusing on machines composed of bundles |
| `@L3_System`  | Tests focusing on system-level behaviors       |

### Capability annotations

|  Annotation  |                Description                 |
|--------------|--------------------------------------------|
| `@Identity`  | Tests for identity management capabilities |
| `@Flow`      | Tests for data flow capabilities           |
| `@State`     | Tests for state management capabilities    |
| `@Awareness` | Tests for environmental awareness          |

### Lifecycle annotations

|   Annotation   |              Description              |
|----------------|---------------------------------------|
| `@Init`        | Tests for initialization behavior     |
| `@Runtime`     | Tests for runtime behavior            |
| `@Termination` | Tests for proper shutdown and cleanup |

### Pattern annotations

|    Annotation     |                   Description                    |
|-------------------|--------------------------------------------------|
| `@Observer`       | Tests for observer pattern implementation        |
| `@Transformer`    | Tests for transformation pattern implementation  |
| `@Validator`      | Tests for validation pattern implementation      |
| `@CircuitBreaker` | Tests for circuit breaker pattern implementation |

### Non-functional annotations

|   Annotation   |              Description              |
|----------------|---------------------------------------|
| `@Performance` | Tests for performance characteristics |
| `@Resilience`  | Tests for system resilience           |
| `@Scale`       | Tests for scaling behavior            |

## Annotation Implementation

All annotations are implemented as JUnit 5 tags, allowing for flexible filtering in test runs.

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("Unit")
@Tag("Tube")
public @interface UnitTest {
}
```

## Using Annotations

Annotations can be combined to precisely classify tests:

```java
@UnitTest
@TubeTest
@ATL
@Identity
@Init
public class TubeIdentityTest {
    // Test methods
}
```

## Running Tests with Annotations

Tests can be run using Maven profiles or custom scripts:

```bash
# Testing Annotations
mvn test -P unit-tests

# Testing Annotations
mvn test -P tube-tests

# Testing Annotations
mvn test -Dcucumber.filter.tags="@L0_Tube and @Identity"

# Testing Annotations
mvn test -P atl-tests

# Testing Annotations
./run-tests.sh unit    # Run unit tests
./run-tests.sh tube    # Run tube tests (equivalent)
./run-tests.sh --tags="@Identity and @ATL"  # Run with tag combination
```

## Test Mapping

The framework automatically maps between terminology sets, so using either term will include tests tagged with either annotation:

|     When you specify      |                Tests included                |
|---------------------------|----------------------------------------------|
| `unit-tests` profile      | Tests with `@UnitTest` or `@TubeTest`        |
| `tube-tests` profile      | Tests with `@TubeTest` or `@UnitTest`        |
| `component-tests` profile | Tests with `@ComponentTest` or `@BundleTest` |
| `bundle-tests` profile    | Tests with `@BundleTest` or `@ComponentTest` |

## Best Practices

1. **Consistency**: Use annotations consistently across the codebase
2. **Clarity**: Choose the most descriptive annotations for each test
3. **Completeness**: Include both industry-standard and domain-specific annotations
4. **Criticality**: Specify whether tests are ATL or BTL
