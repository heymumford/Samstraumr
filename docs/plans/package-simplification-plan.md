<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Package Structure Simplification Plan

## Overview

This document outlines a comprehensive plan to simplify and optimize the package structure of the Samstraumr codebase. The current structure has several issues including redundant hierarchies, overly deep nesting, and duplicate implementations across different packages. This plan aims to reduce complexity, improve maintainability, and create a more intuitive organization.

## Current Issues

1. **Redundant Package Hierarchies**
   - Parallel structures with `org.samstraumr.tube` and `org.tube` packages
   - Multiple implementations of the same functionality across packages
   - Duplicate exception classes in different locations
2. **Verbose Naming Conventions**
   - Full "Samstraumr" name used extensively in packages and artifacts
   - Redundant prefixes in class names within already namespaced packages
3. **Complex Test Organization**
   - Test annotations duplicated across multiple packages
   - Overly complicated test runner hierarchy
   - Multiple implementations of similar test steps
4. **Deep Package Nesting**
   - Some packages are 6+ levels deep (e.g., `org.samstraumr.tube.lifecycle.steps.adam`)
   - Complex navigation and import statements

## Target Structure

```
org.s8r                         (← replace org.samstraumr)
├── core                        (← core functionality)
│   ├── tube                    (← tube implementation)
│   ├── composite               (← composite implementation)
│   ├── machine                 (← machine implementation)
│   └── exception               (← centralized exceptions)
├── util                        (← common utilities)
│   ├── logging                 (← logging functionality)
│   └── identity                (← identity functionality)
└── test                        (← testing infrastructure)
    ├── annotation              (← single location for all annotations)
    ├── cucumber                (← cucumber test infrastructure)
    └── runner                  (← test runners)
```

## Implementation Phases

### Phase 1: Document and Plan (Current)

- ✅ Analyze current package structure
- ✅ Identify redundancies and problems
- ✅ Create documentation with proposed structure
- ⬜ Create a detailed migration roadmap

### Phase 2: Testing Infrastructure Consolidation

- ⬜ Create new `org.s8r.test.annotation` package
- ⬜ Migrate all test annotations to this package
- ⬜ Update annotation imports in test classes
- ⬜ Consolidate test runners
- ⬜ Establish clear guidelines for test organization

### Phase 3: Core Infrastructure Consolidation

- ⬜ Create new package structure starting with `org.s8r.core`
- ⬜ Migrate core classes to new structure
- ⬜ Centralize exception handling
- ⬜ Implement shim classes for backward compatibility

### Phase 4: Maven Artifact Simplification

- ⬜ Update artifactIDs to use s8r prefix
- ⬜ Update version properties
- ⬜ Adjust build configuration

### Phase 5: Documentation and Cleanup

- ⬜ Update all documentation to reflect new structure
- ⬜ Add deprecation annotations to old structure
- ⬜ Set timeline for removal of deprecated code
- ⬜ Update contribution guidelines with new conventions

## Guidelines for New Development

1. Use `org.s8r` as the base package for all new code
2. Follow the simplified package structure outlined above
3. Avoid redundant prefixes in class names when the package provides clear context
4. Use consistently named annotations from the central annotation package
5. Keep package nesting to a maximum of 4 levels

## Migration Strategy

To create a clean and coherent codebase, we'll adopt a complete replacement approach:

1. **Create New Structure with Complete Functionality**:
   - Establish new packages with full implementation (not just shells)
   - Focus on correctness and simplicity rather than compatibility
2. **Aggressive Replacement**:
   - Replace entire components at once rather than piecemeal migration
   - Fully replace old implementations rather than creating adapters
   - Use refactoring tools to update all references in one operation
3. **Clean Cut Over**:
   - Identify all integration points with external systems
   - Plan for complete replacement in a single change window
   - Perform thorough testing before and after replacement
4. **Removal of Legacy Code**:
   - Immediately remove old implementations after replacement
   - No periods of dual maintenance
   - No deprecated periods - clean cuts only

## Success Criteria

The project will be considered successfully migrated when:

1. All core classes follow the new package structure
2. Test organization is simplified and consolidated
3. Artifact IDs and property names use the s8r abbreviation
4. Documentation is updated to reflect new structure
5. No more than 15% of codebase uses deprecated structures
