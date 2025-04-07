<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Directory Flattening Migration Guide

This guide helps developers transition to the new flattened directory structure implemented as part of the directory flattening initiative.

## Overview

The directory flattening initiative has created a new, more intuitive structure with reduced nesting. The implementation strategy for Phase 4 (test structure) and Phase 5 (source code) was to create the new structure while preserving the old structure for backward compatibility.

This approach allows for a gradual migration rather than a disruptive "big bang" change. Eventually, the old structure will be deprecated and removed, but for now, both structures are maintained.

## Test Structure Changes

### Java Test Classes

**Old Structure**:
```
org/s8r/tube/lifecycle/steps/adam/AdamTubeSteps.java
org/s8r/core/tube/test/steps/TubeSteps.java
org/s8r/core/tube/test/runners/TubeTestRunner.java
org/s8r/tube/legacy/test/steps/LegacySteps.java
```

**New Structure**:
```
org/s8r/test/tube/lifecycle/AdamTubeSteps.java
org/s8r/test/tube/TubeSteps.java
org/s8r/test/tube/TubeTestRunner.java
org/s8r/test/legacy/LegacySteps.java
```

### Feature Files

**Old Structure**:
```
test/resources/tube/features/L0_Tube/lifecycle/childhood-phase-tests.feature
test/resources/composites/features/L1_Bundle/patterns/observer-tube-test.feature
test/resources/tube/features/L2_Machine/machine-data-flow-test.feature
test/resources/tube/features/L3_System/system-end-to-end-test.feature
```

**New Structure**:
```
test/resources/features/tube-lifecycle/childhood-phase-tests.feature
test/resources/features/composite-patterns/observer-tube-test.feature
test/resources/features/machine/machine-data-flow-test.feature
test/resources/features/system/system-end-to-end-test.feature
```

## Migration Steps for Developers

### For Test Classes

1. **Update Imports in New Code**:
   - Use the new package structure in all new imports:
     ```java
     // Old
     import org.s8r.tube.lifecycle.steps.adam.AdamTubeSteps;
     
     // New
     import org.s8r.test.tube.lifecycle.AdamTubeSteps;
     ```

2. **Gradually Refactor Existing Code**:
   - When working on a file, update its imports to use the new structure
   - Both structures work, so this can be done incrementally

3. **Update Test Runners**:
   - Update test runners to use the new package structure
   - Add the new package to the `@ComponentScan` or `glue` paths if needed

### For Feature Files

1. **Create New Feature Files**:
   - Place new feature files in the new structure:
     ```
     /src/test/resources/features/{domain}/feature-name.feature
     ```

2. **Reference Feature Files Correctly**:
   - Update step definitions to reference features in the new location
   - Both locations work with current runners, but prefer the new location

3. **Run Tests from New Structure**:
   - Use the new structure when running specific tests:
     ```
     ./s8r-test feature ./features/tube-lifecycle/childhood-phase-tests.feature
     ```

## Verification

After making changes:

1. **Run Tests**: Verify all tests still pass
2. **Check Imports**: Use the linter to verify imports are using new structure
3. **Update Documentation**: Update any documentation that references old paths

## Timeline for Full Migration

- **Current Phase**: Dual structure support
- **Q2 2025**: Begin deprecation warning for old structure
- **Q3 2025**: Add compilation warnings for old structure imports
- **Q4 2025**: Remove old structure completely

## IDE Configuration

Configure your IDE to prefer imports from the new structure:

### IntelliJ IDEA
1. Go to Settings > Editor > Code Style > Java > Imports
2. Add the new packages to the "Packages to Use Import with '*'" section
3. Move them above the old packages

### Eclipse
1. Go to Preferences > Java > Code Style > Organize Imports
2. Add the new packages to the import order
3. Place them above the old packages

## Need Help?

If you encounter any issues while migrating to the new structure, please reach out to the architecture team.

## References

- [Directory Flattening Plan](./docs/planning/folder-flattening-plan.md)
- [Directory Flattening Summary](./directory-flattening-summary.md)