# Folder Structure Flattening Plan

## Overview

This document outlines the plan to simplify and flatten the S8r Framework folder structure. The goal is to reduce unnecessary nesting, improve discoverability, and ensure a clean, maintainable folder organization.

## Current Issues

1. **Excessive Nesting**: Many directories have only 1-2 files but are nested 3-4 levels deep
2. **Duplicate Purposes**: Several directories serve similar purposes (e.g., docs/planning and docs/plans)
3. **Inconsistent Structure**: Similar concerns are organized differently across the codebase
4. **Poor Discoverability**: Important files are hidden deep in the directory structure

## Folder Flattening Strategy

### 1. Documentation Simplification

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

### 2. Source Code Simplification

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

## Implementation Priority

1. **Highest Priority**:
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

## Success Metrics

1. Reduction in total directory count by at least 30%
2. No directory with fewer than 3 files (except for organizational roots)
3. Maximum directory depth of 4 levels from repository root
4. All directories have documented purpose and naming conventions