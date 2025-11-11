# Test Tag Mapping

This document provides a mapping between the legacy tag system and the new standardized test tag system, helping with the transition between the two taxonomies.

## Level Mapping

| Legacy Tag       | New Standardized Tag   | Description                                        |
|------------------|------------------------|----------------------------------------------------|
| `@L0_Tube`       | `@L0_Unit`             | Unit/atomic level tests                            |
| `@TubeTest`      | `@L0_Unit`             | Individual component unit tests                    |
| `@L1_Composite`  | `@L1_Component`        | Component/composite level tests                    |
| `@CompositeTest` | `@L1_Component`        | Connected components tests                         |
| `@L2_Machine`    | `@L2_Integration`      | Integration/machine level tests                    |
| `@MachineTest`   | `@L2_Integration`      | End-to-end machine tests                           |
| `@L3_System`     | `@L3_System`           | System/end-to-end tests                            |
| `@AcceptanceTest`| `@L3_System`           | Business requirement validation                    |

## Criticality Mapping

| Legacy Tag | New Standardized Tag | Description                                         |
|------------|----------------------|-----------------------------------------------------|
| `@ATL`     | `@Functional`        | Critical, must-pass tests that ensure core functionality |
| `@BTL`     | `@ErrorHandling`     | Detailed, edge-case tests that handle unusual situations |

## Feature Area Mapping

| Legacy Tag    | New Standardized Tag | Description                                      |
|---------------|----------------------|--------------------------------------------------|
| `@Flow`       | `@DataFlow`          | Data movement and transformation                  |
| `@State`      | `@State`             | State management and transitions                  |
| `@Identity`   | `@Identity`          | UUID, naming, identification tests                |
| `@Awareness`  | `@Monitoring`        | Self-monitoring and environment awareness         |
| `@Init`       | `@Lifecycle`         | Initialization/construction tests                 |
| `@Runtime`    | `@Behavioral`        | Normal operation tests                            |
| `@Termination`| `@Lifecycle`         | Shutdown/cleanup tests                            |

## Pattern Mapping

| Legacy Tag        | New Standardized Tag | Description                                  |
|-------------------|----------------------|----------------------------------------------|
| `@Observer`       | `@Observer`          | Monitoring pattern tests                      |
| `@Transformer`    | `@Transformer`       | Data transformation tests                     |
| `@Validator`      | `@Filter`            | Input validation tests                        |
| `@CircuitBreaker` | `@CircuitBreaker`    | Fault tolerance tests                         |

## Non-Functional Mapping

| Legacy Tag       | New Standardized Tag | Description                                   |
|------------------|----------------------|-----------------------------------------------|
| `@Performance`   | `@Performance`       | Speed and resource usage                       |
| `@Resilience`    | `@Resilience`        | Recovery and fault handling                    |
| `@Scale`         | `@Performance`       | Load and scaling tests                         |
| `@Security`      | `@Security`          | Security tests                                 |

## Biological Lifecycle Phase Mapping

| Legacy Tag       | New Standardized Tag     | Description                                |
|------------------|--------------------------|------------------------------------------ |
| `@Conception`    | `@Lifecycle @Functional` | Initial creation and identity establishment |
| `@Embryonic`     | `@Lifecycle @Functional` | Basic structure formation                   |
| `@Infancy`       | `@Lifecycle @State`      | Early development of capabilities           |
| `@Childhood`     | `@Lifecycle @DataFlow`   | Functional development                      |

## Initiative Mapping

| Legacy Tag           | New Standardized Tag        | Description                             |
|----------------------|-----------------------------|----------------------------------------|
| `@SubstrateIdentity` | `@Identity @Lifecycle`      | Physical continuity aspects             |
| `@MemoryIdentity`    | `@State @Monitoring`        | Cognitive and memory aspects            |
| `@NarrativeIdentity` | `@Behavioral @Configuration`| Purpose and self-concept aspects        |

## Running Tests

### With legacy tags:

```bash
# Test Tag Mapping
./s8r test atl 
./s8r test btl

# Test Tag Mapping
./s8r test tube
./s8r test composite
./s8r test machine
./s8r test system
```

### With new standardized tags:

```bash
# Test Tag Mapping
./s8r test unit
./s8r test component
./s8r test integration
./s8r test system

# Test Tag Mapping
./s8r test functional
./s8r test error-handling
./s8r test performance

# Test Tag Mapping
./s8r test identity
./s8r test lifecycle
./s8r test state
./s8r test dataflow
```

## Tag Combinations

### Legacy tag combinations:

```
@L0_Tube @ATL @Identity @Init
@L1_Composite @BTL @Flow @Runtime
@L2_Machine @ATL @State @Performance
```

### New standardized tag combinations:

```
@L0_Unit @Functional @Identity @Lifecycle
@L1_Component @ErrorHandling @DataFlow @Behavioral
@L2_Integration @Functional @State @Performance
```

## Standardization Tools

The repository includes a tool to help migrate to the new tag system:

```bash
# Test Tag Mapping
./util/scripts/standardize-test-tags.sh

# Test Tag Mapping
./util/scripts/standardize-test-tags.sh /path/to/features
```

This tool will:
1. Create a backup of original files
2. Update feature and scenario tags to follow the new standard
3. Log all changes made
