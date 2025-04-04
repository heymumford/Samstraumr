# Biological Lifecycle Testing

This directory contains feature files that test Tube components according to the biological lifecycle model. This approach organizes tests around the developmental phases that tubes go through, from conception to legacy.

## Lifecycle Phases

Tests are organized according to the following biological phases:

1. **Conception** (`@Conception`): Tests that verify proper tube creation and initial identity establishment.
   - Primary Initiative: `@SubstrateIdentity`
   - Key Focus: UUID generation, creation timestamps, environmental context capture
2. **Embryonic** (`@Embryonic`): Tests that verify the formation of basic tube structure.
   - Primary Initiative: `@StructuralIdentity`
   - Key Focus: Connection points, internal structure, structural resilience
3. **Infancy** (`@Infancy`): Tests that verify early development of tube capabilities.
   - Primary Initiative: `@MemoryIdentity`
   - Key Focus: State persistence, experience recording, basic learning
4. **Childhood** (`@Childhood`): Tests that verify functional development during active growth.
   - Primary Initiative: `@FunctionalIdentity`
   - Key Focus: Data processing, state learning, error recovery
5. **Adolescence** (`@Adolescence`): Tests that verify significant evolution and transitions.
   - Primary Initiative: `@AdaptiveIdentity`
   - Key Focus: Adaptation, optimization, changing capabilities (Not yet implemented)
6. **Adulthood** (`@Adulthood`): Tests that verify complete operational capabilities.
   - Primary Initiative: `@CognitiveIdentity`
   - Key Focus: Integration, full awareness, decision making (Not yet implemented)
7. **Maturity** (`@Maturity`): Tests that verify optimized and specialized behavior.
   - Primary Initiative: `@SpecializedIdentity`
   - Key Focus: Performance optimization, specialized functions (Not yet implemented)
8. **Senescence** (`@Senescence`): Tests that verify graceful degradation and failure handling.
   - Primary Initiative: `@ResilienceIdentity`
   - Key Focus: Resilience, recovery, failure adaptation (Not yet implemented)
9. **Termination** (`@Termination`): Tests that verify proper shutdown and cleanup processes.
   - Primary Initiative: `@ClosureIdentity`
   - Key Focus: Resource release, state archiving, clean termination (Not yet implemented)
10. **Legacy** (`@Legacy`): Tests that verify knowledge preservation and transfer.
    - Primary Initiative: `@HeritageIdentity`
    - Key Focus: Knowledge transfer, historical data access (Not yet implemented)

## Running Tests

Use the dedicated test runner script to execute biological lifecycle tests:

```bash
# Run tests for a specific phase
./util/test-bio-lifecycle.sh conception

# Run tests for a specific initiative within a phase
./util/test-bio-lifecycle.sh infancy --initiative=memory

# Run only positive or negative tests
./util/test-bio-lifecycle.sh childhood --type=positive

# Run critical (ATL) tests for all implemented phases
./util/test-bio-lifecycle.sh all --atl

# List available phases
./util/test-bio-lifecycle.sh list

# Get help
./util/test-bio-lifecycle.sh --help
```

For more information about the testing strategy, see the [Test Tags and Annotations](/docs/testing/TestTagsAndAnnotations.md) documentation.

## Implementation Status

The first four phases (Conception, Embryonic, Infancy, and Childhood) have been fully implemented with feature files and step definitions. The remaining phases will be implemented incrementally as the framework matures.

## Test Organization

Within each feature file, scenarios are categorized by tags that enable fine-grained test selection:

- **Phase Tags**: `@Conception`, `@Embryonic`, etc.
- **Initiative Tags**: `@SubstrateIdentity`, `@StructuralIdentity`, etc.
- **Epic Tags**: `@UniqueIdentification`, `@ConnectionPoints`, etc.
- **Test Type Tags**: `@Positive`, `@Negative`
- **Criticality Tags**: `@ATL`, `@BTL`
- **Layer Tags**: `@L0_Tube`
- **Capability Tags**: `@Identity`, `@Structure`, `@State`, etc.

## Step Definition Organization

Step definitions for the biological lifecycle tests are organized in classes that correspond to lifecycle phases:

- `ConceptionPhaseSteps.java`
- `EmbryonicPhaseSteps.java`
- `InfancyPhaseSteps.java`
- `ChildhoodPhaseSteps.java`

Each step definition class is responsible for implementing the steps defined in its corresponding feature file.
