# Test Consolidation Plan

## Current Testing Terminology Issues

The current testing framework contains several overlapping concepts:

1. **ATL/BTL Separation**: Above The Line vs. Below The Line - this distinction is no longer needed
2. **Multiple Annotations**: Similar annotations exist in different packages (`@TubeTest`, `@SimpleTest`, etc.)
3. **BTL Tests**: These are meant to be removed entirely but some annotations and files remain
4. **Scattered Annotations**: Various test annotations exist across several packages

## Consolidation Approach

We'll consolidate to a simpler and more consistent terminology:

1. **Single Source of Truth**: Move all test annotations to a centralized location
2. **Remove BTL Entirely**: Delete all BTL-related annotations, feature tags, and references
3. **Simplify Test Categories**: Standardize on functional test categories (Unit, Flow, Component, Integration, etc.)
4. **Update Documentation**: Ensure all documentation reflects the new simplified approach

## Changes Required

### 1. Delete BTL Annotation Files

Delete the following files:
- `org.s8r.test.annotation.BTL`
- `org.samstraumr.tube.annotations.BTL`
- `org.samstraumr.tube.annotations.BelowTheLine`
- `org.samstraumr.tube.test.annotations.BTL`
- `org.samstraumr.tube.test.annotations.BelowTheLine`
- `org.test.annotations.BTL`
- `org.test.annotations.BelowTheLine`
- `org.tube.test.annotations.BTL`
- `org.tube.test.annotations.BelowTheLine`

### 2. Remove BTL from Feature Files

Remove all `@BTL` and `@BelowTheLine` tags from feature files in:
- `/tube/features/`
- `/composites/features/`
- `/test/features/`

### 3. Update Java Files

Fix `BasicCompositeTest.java` to use the correct annotations:
- Remove `@BTL` 
- Update JavaDoc to remove BTL references

### 4. Simplify Maven POM Configuration

Update Maven POM files to:
- Remove BTL test profiles completely
- Simplify ATL to just "tests" (no need for "above the line" distinction)
- Update test-related properties to remove BTL references

### 5. Centralize Test Annotations

Move all test annotations to a single package:
- Target package: `org.samstraumr.test.annotation`
- Annotations to include: Unit, Integration, Component, Flow, etc.
- Update import statements in all test files

### 6. Update Documentation

Update all documentation to:
- Remove ATL/BTL distinction
- Explain the simplified test categories
- Provide clear guidance on which annotations to use for what purposes

## Benefits

1. **Simplicity**: Clearer testing structure without unnecessary ATL/BTL distinction
2. **Consistency**: All test annotations in one place with consistent naming
3. **Maintainability**: Easier to understand, modify, and extend the testing framework
4. **Performance**: No resources wasted on managing and filtering BTL tests that aren't used

## Implementation Timeline

1. Remove BTL annotations and references
2. Consolidate test annotations to a central location
3. Update Java test files to use the consolidated annotations
4. Update Maven POM configurations
5. Update documentation

This consolidation aligns with the TDD-focused development approach and will simplify the testing infrastructure.