# BTL Tests Removal Plan

## Overview

This plan outlines the steps to remove the BTL (Below The Line) test implementations while preserving the BTL testing infrastructure for future use. BTL tests have been identified as problematic and not properly functioning in the current architecture.

## Files to Modify

### 1. Test Runners to Remove

The following test runners should be removed as they specifically run BTL tests:

```
Samstraumr/samstraumr-core/src/test/java/org/samstraumr/tube/lifecycle/RunLifecycleBTLCucumberTest.java
Samstraumr/samstraumr-core/src/test/java/org/samstraumr/tube/RunBTLCucumberTest.java
```

### 2. Feature Files to Modify

The following feature files contain BTL-tagged scenarios that should be removed or commented out:

```
src/test/resources/composites/features/L1_Bundle/patterns/validator-tube-test.feature
src/test/resources/composites/features/L1_Bundle/patterns/observer-tube-test.feature
src/test/resources/composites/features/L1_Bundle/patterns/transformer-tube-test.feature
src/test/resources/tube/features/L3_System/system-resilience-test.feature
src/test/resources/tube/features/examples/TBD-machine-construct-validation-test-example.feature
src/test/resources/tube/features/examples/TBD-composite-tube-interaction-test-example.feature
src/test/resources/tube/features/examples/TBD-atomic-boundary-test-example.feature
src/test/resources/tube/features/examples/TBD-inter-tube-feature-test-example.feature
src/test/resources/tube/features/L1_Bundle/bundle-connection-test.feature
src/test/resources/tube/features/L2_Machine/machine-state-test.feature
src/test/resources/tube/features/L0_Tube/lifecycle/embryonic-phase-tests.feature
src/test/resources/tube/features/L0_Tube/lifecycle/childhood-phase-tests.feature
src/test/resources/tube/features/L0_Tube/lifecycle/conception-phase-tests.feature
src/test/resources/test/features/embryonic-phase-tests.feature
src/test/resources/test/features/childhood-phase-tests.feature
src/test/resources/test/features/conception-phase-tests.feature
```

These files need to be examined to remove BTL-tagged scenarios while preserving non-BTL scenarios.

### 3. Maven Configuration

Update the pom.xml files to remove BTL profile configuration:

1. Find and modify the `btl-tests` profile in Samstraumr/pom.xml and Samstraumr/samstraumr-core/pom.xml
2. Remove or comment out the profile, but keep it as documentation for future reference

### 4. Test Scripts to Modify

Update the test scripts to remove BTL test execution but preserve the BTL infrastructure:

1. `util/bin/test/run-tests.sh`: Update to remove BTL-specific paths but keep BTL tag definition
2. `util/bin/quality/check-build-quality.sh`: Update to remove BTL execution but preserve structure
3. `.github/workflows/samstraumr-pipeline.yml`: Update to remove BTL job execution but keep job definition

## Files to Preserve

### 1. BTL Annotations

Keep these annotations as part of the testing infrastructure:

- `org.samstraumr.tube.annotations.BTL.java` 
- `org.samstraumr.tube.annotations.BelowTheLine.java`

### 2. BTL Testing Documentation

Preserve the following documentation files describing the BTL strategy:

- `docs/testing/ATLBTLStrategy.md`
- Any other relevant documentation explaining the BTL testing approach

## Implementation Steps

1. Create backup of all BTL test code to `/tmp/btl-backup` (completed)
2. Remove BTL test runners
3. Modify feature files to remove BTL-tagged scenarios (or comment them out)
4. Update Maven profiles to disable BTL test execution
5. Update test scripts to remove BTL execution
6. Update CI/CD pipeline to remove BTL job execution
7. Add comments in preserved files documenting why BTL tests were removed
8. Commit changes with appropriate message

## Future Considerations

- Consider reimplementing proper BTL tests in the future when architecture stabilizes
- Use the preserved BTL infrastructure as a foundation for future implementation
- Document the removed code to make future reimplementation easier