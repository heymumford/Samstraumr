# Test Pyramid Tags

This document defines the standardized test pyramid structure and tagging conventions for Samstraumr tests, ensuring consistent organization and execution across all test levels.

## Test Pyramid Structure

The Samstraumr test pyramid consists of four distinct levels, each with specific attributes and purposes:

| Level | Tag          | Description                                       | Primary Focus                             |
|-------|--------------|---------------------------------------------------|-------------------------------------------|
| L0    | `@UnitTest`  | Tests for individual components in isolation      | Component structure, behavior, exceptions |
| L1    | `@Component` | Tests for connected components forming composites | Component interactions, integration       |
| L2    | `@Integration` | Tests for machine-level functionality           | End-to-end flows, machine state           |
| L3    | `@System`    | Tests for complete system behavior                | Multi-machine interactions, resilience    |

## Primary Tag Categories

Each test should use tags from the following categories to enable precise test selection:

### 1. pyramid level tags

These tags define the level in the test pyramid:

| Tag             | Level | Description                                          |
|-----------------|-------|------------------------------------------------------|
| `@L0_Unit`      | L0    | Unit/atomic level tests                              |
| `@L1_Component` | L1    | Component/composite level tests                      |
| `@L2_Integration` | L2  | Integration/machine level tests                      |
| `@L3_System`    | L3    | System/end-to-end tests                              |

### 2. test type tags 

These tags define the type of test being performed:

| Tag           | Description                                         | Example                                |
|---------------|-----------------------------------------------------|----------------------------------------|
| `@Functional` | Tests of core functionality                         | Basic component operation              |
| `@Behavioral` | Tests of behavioral aspects                         | State transitions, lifecycle behavior  |
| `@DataFlow`   | Tests focused on data movement and transformation   | Transformation, validation, routing    |
| `@ErrorHandling` | Tests focused on error and exception handling    | Invalid input handling, fault recovery |
| `@Performance` | Tests of timing, throughput, and resource usage    | Load tests, throughput measurement     |
| `@Security`   | Tests of security aspects                           | Authorization, data protection         |

### 3. feature area tags

These tags identify the specific feature area being tested:

| Tag             | Description                                  | Example                                 |
|-----------------|----------------------------------------------|------------------------------------------|
| `@Identity`     | Component identity and identification        | UUID generation, naming, addressing      |
| `@Lifecycle`    | Component lifecycle management               | Creation, initialization, termination    |
| `@State`        | State management and transitions             | State persistence, transitions, history  |
| `@Connection`   | Component interconnection and communication  | Connecting components, message passing   |
| `@Configuration`| System and component configuration           | Settings, parameters, profiles           |
| `@Monitoring`   | Observability and metrics                    | Health checks, performance monitoring    |
| `@Resilience`   | Fault tolerance and recovery                 | Error handling, failover, circuit breaking |

### 4. pattern tags

These tags identify architectural patterns being tested:

| Tag             | Description                             | Example                                    |
|-----------------|-----------------------------------------|--------------------------------------------|
| `@Pipeline`     | Linear processing pipeline pattern      | Sequential data processing                 |
| `@Transformer`  | Data transformation pattern             | Content modification                       |
| `@Filter`       | Content filtering pattern               | Selective data processing                  |
| `@Aggregator`   | Data combination pattern                | Merging data from multiple sources         |
| `@Router`       | Conditional routing pattern             | Content-based routing, dynamic destination |
| `@Observer`     | Event notification pattern              | Monitoring without modifying               |
| `@CircuitBreaker` | Fault isolation pattern              | Failure containment                        |
| `@Splitter`     | Message decomposition pattern           | Breaking composite messages                |

## Tag Combination Best Practices

Each test should be tagged with at minimum:

1. One pyramid level tag (e.g., `@L1_Component`)
2. One test type tag (e.g., `@Functional`)
3. One feature area tag (e.g., `@Identity`)

Example combined tags:

- `@L0_Unit @Functional @Identity`: Unit tests for component identity functionality
- `@L1_Component @DataFlow @Connection`: Component tests for data flow between connected components
- `@L2_Integration @Behavioral @State`: Integration tests for state behavior in machines
- `@L3_System @Performance @Resilience`: System tests for performance under failure conditions

## Running Tests by Tag

Tests can be selected and executed by tag combination using Maven or the CLI:

```bash
# Test Pyramid Tags
./s8r test unit

# Test Pyramid Tags
./s8r test component

# Test Pyramid Tags
./s8r test integration

# Test Pyramid Tags
./s8r test system

# Test Pyramid Tags
./s8r test --tags="@L1_Component and @Identity"
./s8r test --tags="@L2_Integration and @DataFlow"
./s8r test --tags="@Resilience and @CircuitBreaker"
```

## Migration Plan

1. Update all existing feature files with the new standardized tags
2. Update test runners to use the new tag structure
3. Update documentation and build scripts to reference the new tags
4. Create tag mapping between legacy tags and new standardized tags

## Tag Mapping

For backward compatibility, the following mappings are established:

| Legacy Tag       | New Standardized Tag   |
|------------------|------------------------|
| `@L0_Tube`       | `@L0_Unit`             |
| `@L1_Composite`  | `@L1_Component`        |
| `@L2_Machine`    | `@L2_Integration`      |
| `@L3_System`     | `@L3_System`           |
| `@ATL`           | `@Functional`          |
| `@BTL`           | `@ErrorHandling`       |
| `@Lifecycle`     | `@Lifecycle`           |
| `@Flow`          | `@DataFlow`            |
| `@State`         | `@State`               |
| `@Awareness`     | `@Monitoring`          |

## Implementation in Code

```java
// Example Java annotation for an L1 Component test
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("L1_Component")
@Tag("Functional")
@Tag("Identity")
public @interface ComponentIdentityTest {}

// Example feature file tags
@L1_Component @Functional @Connection @Pipeline
Feature: Component Connection Pipeline
  As a system designer
  I want to connect components in a linear pipeline
  So that data can flow sequentially through processing stages
```

## Conclusion

