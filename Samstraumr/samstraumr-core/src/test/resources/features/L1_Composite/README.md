# Composite Component Tests

This directory contains BDD feature files for testing composite component functionality in the Samstraumr framework.

## Overview

Composite tests (L1_Composite) validate the behavior of composite components, which are components that contain other components in a hierarchical structure. These tests focus on creation, interaction, and design pattern implementations within composites.

## Feature Files

1. **composite-creation.feature** - Tests for creating and configuring composite components
   - Basic composite creation
   - Adding child components
   - Nested composite hierarchies
   - State management
   - Error handling for invalid parameters

2. **composite-interaction.feature** - Tests for interactions between components in a composite
   - Data flow between connected components
   - Event propagation through the composite hierarchy
   - State synchronization across related components
   - Resource sharing within the composite
   - Error handling during component interactions

3. **composite-patterns.feature** - Tests for design pattern implementations in composites
   - Observer pattern for event notification
   - Transformer pattern for data transformation
   - Filter pattern for conditional routing
   - Router pattern for content-based routing
   - Aggregator pattern for message correlation and aggregation
   - Saga pattern for distributed transactions

## Tags

These tests use the following tags for organization:

- `@L1_Component`, `@L1_Composite` - Test pyramid level tags
- `@Functional`, `@ATL` - Core functionality tests
- `@CompositeTest` - Specific test type
- `@Lifecycle`, `@State`, `@DataFlow`, `@Events`, `@ErrorHandling` - Functionality-specific tags
- `@Observer`, `@Transformer`, `@Filter`, `@Router`, `@Aggregator`, `@Saga` - Pattern-specific tags
- `@smoke` - Critical tests for smoke testing

## Running the Tests

To run the composite tests, use the s8r-test script:

```bash
# Run all composite tests
./bin/s8r-test composite

# Run specific composite tests with tags
./bin/s8r-test --tags "@L1_Composite and @Observer"
```

## Implementation

The test steps are implemented in the following Java classes:
- `CompositeBaseSteps.java` - Base class with shared functionality
- `CompositeCreationSteps.java` - Steps for composite creation
- `CompositeInteractionSteps.java` - Steps for component interactions
- `CompositePatternSteps.java` - Steps for design pattern implementations

The test runner is implemented in `RunCompositeTests.java`.