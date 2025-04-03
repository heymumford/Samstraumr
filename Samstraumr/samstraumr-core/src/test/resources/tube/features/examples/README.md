# TBD (Tube-Based Development) Testing Examples

This directory contains example feature files demonstrating how to implement the Tube-Based Development (TBD) testing framework in Samstraumr. These examples are designed to help you understand the different types of TBD tests and how to write them effectively.

## TBD Testing Framework Overview

The TBD testing framework organizes tests into four main categories:

1. **Atomic Boundary Testing (ABT)** - Tests for individual tubes in isolation
2. **Inter-Tube Feature Testing (ITFT)** - Tests for feature interactions within a tube
3. **Composite Tube Interaction Testing (CTIT)** - Tests for interactions between tubes
4. **Machine Construct Validation Testing (MCVT)** - Tests for complete machines

Each test category is further classified as either:
- **Above The Line (ATL/Attle)** - Critical tests that must pass
- **Below The Line (BTL/Bottle)** - Robust tests for edge cases and resilience

## Example Files

### 1. TBD-AtomicBoundaryTest-Example.feature

This example demonstrates how to write proper Atomic Boundary Tests (ABT) for individual tubes. Key aspects:
- Testing unique identity boundaries
- Verifying state containment
- Validating input/output constraints
- Enforcing tube boundaries

### 2. TBD-InterTubeFeatureTest-Example.feature

This example shows how to test feature interactions within a single tube. Key aspects:
- Testing how memory and processing features collaborate
- Verifying state tracking and reporting features
- Testing resource monitoring and adaptation
- Validating identity and configuration features

### 3. TBD-CompositeTubeInteractionTest-Example.feature

This example illustrates testing interactions between connected tubes. Key aspects:
- Testing data flow between tubes
- Verifying state propagation
- Testing error isolation
- Validating resource awareness across tubes

### 4. TBD-MachineConstructValidationTest-Example.feature

This example demonstrates end-to-end testing of complete machines. Key aspects:
- Testing full data processing workflows
- Verifying coordinated state management
- Testing system-wide fault tolerance
- Validating business requirement fulfillment

## How to Use These Examples

1. **Reference for Test Structure**: Use these examples as reference when creating new tests
2. **Tag Ontology**: Follow the TBD tag structure (@ABT, @ITFT, @CTIT, @MCVT)
3. **Naming Conventions**: Adopt consistent naming for test files and scenarios
4. **Commenting Style**: Include detailed comments explaining test purpose and expectations

## TBD Testing Best Practices

1. **Start with ABT**: Begin testing at the atomic level before moving to more complex tests
2. **Focus on Boundaries**: Pay special attention to boundaries between components
3. **Test Adaptation**: Always include tests for adaptive behavior and resource response
4. **Dual State Testing**: Test both design state (configuration) and runtime state (behavior)
5. **Clear Scenarios**: Write scenarios that clearly express intent and expectations
6. **Consistent Tagging**: Use the full tag ontology to properly classify tests

## Running TBD Tests

Use the test runner with specific TBD categories:

```bash
# Run all ABT tests
./util/test/run-tests.sh --tags "@ABT"

# Run ATL CTIT tests
./util/test/run-tests.sh --tags "@ATL and @CTIT"

# Run all MCVT tests
./util/test/run-tests.sh --tags "@MCVT"
```

## Migration Notes

When migrating existing tests to the TBD framework:

1. Add appropriate TBD tags (@ABT, @ITFT, @CTIT, @MCVT)
2. Update scenario descriptions to focus on boundaries and interactions
3. Enhance tests to include adaptation and dual state testing where missing
4. Keep existing tags (@L0_Tube, @ATL, etc.) alongside new TBD tags for compatibility

For more information, refer to the full [Testing-Strategy-TBD.md](/docs/Testing-Strategy-TBD.md) document.