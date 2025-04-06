<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# S8r Documentation Consolidation Plan

This plan builds upon the previous consolidation efforts while focusing on the transition from Samstraumr to S8r terminology and architecture. It specifically aims to reduce documentation file count by merging similar content and updating terminology.

## 1. Core Concept Documents

### Consolidate concept documentation

- **Overlapping Files**:
  - `/docs/concepts/core-concepts.md` (S8r terminology)
  - `/docs/core/concept-overview.md` (Samstraumr terminology)
  - `/docs/concepts/composites-and-machines.md`
  - `/docs/core/concept-composites.md`
- **Plan**:
  - Keep `/docs/concepts/core-concepts.md` as the canonical version with S8r terminology
  - Update terminology in all concept documents (tube → component, etc.)
  - Merge unique content from `/docs/core/concept-composites.md` into `/docs/concepts/composites-and-machines.md`
  - Add redirects from old documents to new ones
  - Delete duplicate files after updating all references

### Identity documentation

- **Overlapping Files**:
  - `/docs/concepts/identity-addressing.md`
  - `/docs/core/concept-identity.md`
- **Plan**:
  - Merge into a single `/docs/concepts/identity-addressing.md` file
  - Update terminology to be S8r-compliant (TubeIdentity → ComponentIdentity)
  - Add examples using S8r component naming

### State management

- **Overlapping Files**:
  - `/docs/concepts/state-management.md`
  - `/docs/core/concept-state.md`
- **Plan**:
  - Consolidate into `/docs/concepts/state-management.md`
  - Update to explain the unified State model in S8r (vs dual TubeStatus/TubeLifecycleState in Samstraumr)
  - Add diagrams illustrating the state transitions in the new model

## 2. Testing Documentation

### Testing strategy

- **Overlapping Files**:
  - `/docs/testing/testing-strategy.md`
  - `/docs/testing/test-strategy.md`
  - `/docs/dev/test-strategy.md`
- **Plan**:
  - Keep `/docs/testing/testing-strategy.md` as the canonical version
  - Update terminology (tube → component, bundle → composite)
  - Add redirects from other files to the canonical version
  - Delete duplicate files after updating all references

### BDD documentation

- **Overlapping Files**:
  - `/docs/testing/bdd-documentation.md`
  - `/docs/testing/bdd-with-cucumber.md`
  - `/Samstraumr/samstraumr-core/RationaleOnUsingBDD.md`
  - `/Samstraumr/samstraumr-core/WhatIsBDD.md`
- **Plan**:
  - Keep `/docs/testing/bdd-documentation.md` as the comprehensive reference
  - Update examples to use S8r component terminology
  - Add redirects from other files to the canonical version
  - Create a new quick-start guide that focuses on S8r component testing with Cucumber

### Test annotations

- **Overlapping Files**:
  - `/docs/testing/test-tags-and-annotations.md`
  - `/docs/testing/testing-annotations.md`
  - `/docs/dev/test-annotations.md`
- **Plan**:
  - Consolidate into `/docs/testing/test-tags-and-annotations.md`
  - Update annotation names (TubeTest → ComponentTest)
  - Add mapping between old and new annotations for migration

## 3. Migration and Getting Started Guides

### Migration guides

- **Files to Update**:
  - `/docs/guides/migration/SamstraumrToS8rMigration.md` (Keep and enhance)
  - `/docs/guides/migration.md` (Redirect to SamstraumrToS8rMigration.md)
  - `/docs/guides/migration-guide.md` (Redirect to SamstraumrToS8rMigration.md)
- **New Files to Create**:
  - `/docs/guides/component-patterns.md` (to replace tube-patterns.md)

### Getting started

- **Files to Update**:
  - `/docs/guides/introduction.md` (Already updated to S8r)
  - `/docs/guides/getting-started.md` (Update with S8r examples)
  - `/docs/guides/prerequisites.md` (Update with S8r requirements)

## 4. Reference Documentation

### Standards documentation

- **Overlapping Files**:
  - Root level standards files:
    - `/docs/documentation-standards.md`
    - `/docs/java-naming-standards.md`
    - `/docs/logging-standards.md`
  - Reference directory standards:
    - `/docs/reference/standards/documentation-standards.md`
    - `/docs/reference/standards/JavaNamingStandards.md`
    - `/docs/reference/standards/LoggingStandards.md`
  - Ref directory standards:
    - `/docs/ref/standard-documentation.md`
    - `/docs/ref/standard-java-naming.md`
    - `/docs/ref/standard-logging.md`
- **Plan**:
  - Keep `/docs/reference/standards/` as the canonical location
  - Update all standards with S8r terminology
  - Add redirects from other files to the canonical versions
  - Delete duplicate files after updating all references

### API references

- **Plan**:
  - Create `/docs/reference/s8r-api-reference.md` with Component API documentation
  - Create `/docs/reference/s8r-javadoc-guide.md` with JavaDoc generation instructions for S8r

## 5. Implementation Approach

For each set of duplicate files:

1. Create a new consolidated version or identify the canonical version
2. Add cross-references and redirects for backward compatibility
3. Update all references to the old documents across the codebase
4. Run tests to ensure nothing breaks
5. Remove the duplicate files
6. Update documentation to reflect the new structure

## 6. Consolidation Order

1. **First Phase**: Migrate core concepts and guides to S8r terminology
2. **Second Phase**: Consolidate testing documentation
3. **Third Phase**: Update and consolidate reference materials
4. **Final Phase**: Clean up remaining duplicates and ensure cross-references

## 7. Metrics

- Initial file count: ~100 documentation files
- Target file count: ~60 documentation files (40% reduction)
- Criteria for keeping separate files:
  - Serves a distinct audience or purpose
  - Contains unique content that doesn't fit contextually in other documents
  - Is actively referenced from multiple locations
