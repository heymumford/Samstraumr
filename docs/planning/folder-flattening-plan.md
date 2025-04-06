<\!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Folder Structure Flattening Plan

## Overview

This document outlines the plan to simplify and flatten the S8r Framework folder structure. The goal is to reduce unnecessary nesting, improve discoverability, and ensure a clean, maintainable folder organization. We aim to reduce the maximum folder depth to 9 levels across the repository.

## Current Issues

1. **Excessive Nesting**: Many directories have only 1-2 files but are nested 3-4 levels deep
2. **Duplicate Purposes**: Several directories serve similar purposes (e.g., docs/planning and docs/plans)
3. **Inconsistent Structure**: Similar concerns are organized differently across the codebase
4. **Poor Discoverability**: Important files are hidden deep in the directory structure
5. **Path Depth Limit Exceeded**: Some paths exceed 9 levels deep, making navigation difficult

## Current Deep Directory Paths

Our analysis has identified several paths that exceed our target maximum of 9 levels:

### Java Test Classes (11 levels deep)
- `/Samstraumr/samstraumr-core/src/test/java/org/s8r/core/tube/test/runners/`
- `/Samstraumr/samstraumr-core/src/test/java/org/s8r/core/tube/test/steps/`

### Cucumber Feature Files (10 levels deep)
- `/Samstraumr/samstraumr-core/src/test/resources/composites/features/L1_Bundle/patterns/`
- `/Samstraumr/samstraumr-core/src/test/resources/composites/features/L1_Composite/patterns/`
- `/Samstraumr/samstraumr-core/src/test/resources/tube/features/L0_Tube/lifecycle/`

## Folder Flattening Strategy

### 1. Java Test Structure Flattening

#### Current Structure:
```
org/s8r/core/tube/test/runners/AdamTubeTestRunner.java
org/s8r/core/tube/test/steps/AdamTubeSteps.java
```

#### New Structure:
```
org/s8r/test/tube/runners/AdamTubeTestRunner.java
org/s8r/test/tube/steps/AdamTubeSteps.java
```

**Rationale**: Simplifies the package hierarchy while maintaining logical grouping. Moves test-related classes to a dedicated test package root, eliminating the `core/tube/test` path segment redundancy.

### 2. Feature Files Restructuring

#### Current Structure:
```
test/resources/composites/features/L1_Bundle/patterns/observer-tube-test.feature
test/resources/tube/features/L0_Tube/lifecycle/childhood-phase-tests.feature
```

#### New Structure:
```
test/resources/features/composite-bundle/observer-tube-test.feature
test/resources/features/tube-lifecycle/childhood-phase-test.feature
```

**Rationale**: Flattens the feature directory structure while preserving test categorization through descriptive directory names. The hierarchical relationship is preserved in the directory name rather than through nesting.

### 3. Documentation Simplification

Current structure:
```
docs/
  planning/
    active/
    archived/
    completed/
  plans/
  proposals/
  architecture/
    clean/
    event/
    monitoring/
    patterns/
  ...
```

Proposed structure:
```
docs/
  plans/           # All planning documents with clear naming conventions
  architecture/    # All architecture documents with clear file naming
  guides/          # All user and developer guides
  reference/       # Technical reference material
  standards/       # Coding and documentation standards
```

File naming convention: `[status]-[topic]-[type].md`  
Example: `active-documentation-generation-plan.md` instead of `active/documentation-generation-plan.md`

### 4. Source Code Simplification

Current structure:
```
samstraumr-core/src/main/java/org/samstraumr/
  domain/
    component/
      composite/
      pattern/
      monitoring/
    identity/
    lifecycle/
    event/
    exception/
```

Proposed structure:
```
samstraumr-core/src/main/java/org/samstraumr/
  domain/
    component/    # All component-related domain classes with clear naming
    event/        # All domain events
    exception/    # All domain exceptions
```

File naming convention: `[domain]-[entity]-[concern].java`  
Example: `component-composite-factory.java` instead of `component/composite/CompositeFactory.java`

## File Naming Strategy

To support flattening, we'll adopt consistent naming patterns:

- `[domain]-[component]-[feature].feature` for feature files
- `[Component][Type][Feature]Test.java` for test files
- `[status]-[topic]-[type].md` for documentation
- `[domain]-[entity]-[concern].java` for source code

This allows us to remove one level of nesting while maintaining clear categorization.

## Implementation Priority

1. **Highest Priority**:
   - Flatten test package structure (deepest current paths)
   - Restructure feature file directories
   - Consolidate and flatten /docs directory structure
   - Simplify test directory structure
   - Consolidate utility scripts

2. **Medium Priority**:
   - Simplify domain layer directory structure
   - Standardize adapter/port naming and structure

3. **Lower Priority**:
   - Address legacy and backup code organization
   - Optimize site generation folder structure

## Implementation Approach

For each target area:

1. Create new README.md files with clear naming conventions
2. Move files to the new locations
3. Update imports and references
4. Create redirects or guidance for common locations
5. Remove empty directories

### Phase 1: Create New Directory Structure
1. Create the new flattened directory structure
2. Update relevant README files with new structure documentation

### Phase 2: File Migration
1. Move test classes to their new locations
2. Update import statements and package declarations
3. Move feature files to their new locations

### Phase 3: Update References
1. Update test runners to reference new feature file locations
2. Update any build scripts or configuration files with new paths
3. Update documentation references

### Phase 4: Clean Up
1. Remove empty directories
2. Verify all tests still pass with the new structure
3. Update the directory structure documentation

## Consistency Guidelines

To prevent deep nesting in the future:

1. **Maximum Path Depth**: Enforce a maximum of 9 directory levels for any file
2. **Package vs. Directory**: Use Java package naming to indicate logical hierarchy rather than physical directories
3. **Descriptive Directories**: Use compound names for directories instead of creating subdirectories
4. **README Documentation**: Document logical groupings in README files rather than through directory structure

## Success Metrics

1. Reduction in total directory count by at least 30%
2. No directory with fewer than 3 files (except for organizational roots)
3. Maximum directory depth of 9 levels (including repository root)
4. All directories have documented purpose and naming conventions

## Verification Strategy

After implementation, run the following checks:

1. Execute the directory depth analysis to confirm no paths exceed 9 levels
2. Run all tests to verify functionality remains intact
3. Verify IDE navigation and code discovery still work effectively

## Timeline

- Analysis & Planning: Complete
- Implementation: 2 days
- Testing & Verification: 1 day
- Documentation Updates: 1 day
