# File Consolidation Plan

Based on the analysis of duplicate and overlapping files in the codebase, here's a plan to consolidate them effectively.

## 1. Documentation Files with Clear Redundancy

### Folder Structure Documentation
- **Duplicate Files**: 
  - `/docs/FolderStructure.md`
  - `/docs/reference/FolderStructure.md`
- **Plan**: 
  - Keep `/docs/reference/FolderStructure.md` as the canonical version
  - Remove `/docs/FolderStructure.md`
  - Create a symbolic link from old to new location (not possible with git, but can add a redirect notice)

### Java Naming Standards
- **Duplicate Files**: 
  - `/docs/JavaNamingStandards.md`
  - `/docs/reference/standards/JavaNamingStandards.md`
- **Plan**: 
  - Keep `/docs/reference/standards/JavaNamingStandards.md` as the canonical version (it has the newer content with acronym exceptions)
  - Remove `/docs/JavaNamingStandards.md`
  - Add a notice to the README.md pointing to the new location

### Documentation Standards
- **Duplicate Files**: 
  - `/docs/DocumentationStandards.md`
  - `/docs/reference/standards/DocumentationStandards.md`
- **Plan**: 
  - Keep `/docs/reference/standards/DocumentationStandards.md` as the canonical version
  - Remove `/docs/DocumentationStandards.md`
  - Add a notice to the README.md pointing to the new location

### Logging Standards
- **Duplicate Files**: 
  - `/docs/LoggingStandards.md`
  - `/docs/reference/standards/LoggingStandards.md`
- **Plan**: 
  - Keep `/docs/reference/standards/LoggingStandards.md` as the canonical version
  - Remove `/docs/LoggingStandards.md`
  - Add a notice to the README.md pointing to the new location

## 2. Test Documentation with Significant Overlap

### Testing Strategy
- **Overlapping Files**: 
  - `/docs/testing/TestingStrategy.md` (comprehensive)
  - `/docs/testing/TestStrategy.md` (concise)
  - `/docs/proposals/SamstraumrTestingStrategy.md` (early draft)
- **Plan**: 
  - Create a merged document at `/docs/testing/TestingStrategy.md` incorporating the best content from all three
  - Remove `/docs/testing/TestStrategy.md` and `/docs/proposals/SamstraumrTestingStrategy.md`
  - Add clear heading and TOC to help navigation of the comprehensive document

### BDD Documentation
- **Overlapping Files**: 
  - `/Samstraumr/samstraumr-core/RationaleOnUsingBDD.md` (conceptual rationale)
  - `/Samstraumr/samstraumr-core/WhatIsBDD.md` (introduction and concepts)
  - `/docs/testing/BddWithCucumber.md` (practical implementation)
- **Plan**: 
  - Create a new comprehensive BDD document at `/docs/testing/BddDocumentation.md` with clear sections:
    1. What is BDD? (from WhatIsBDD.md)
    2. Why Samstraumr uses BDD (from RationaleOnUsingBDD.md)
    3. Implementing BDD with Cucumber (from BddWithCucumber.md)
  - Keep all original documents for now but add deprecation notices
  - Add references in README.md to the new consolidated version

### Test Annotations
- **Overlapping Files**: 
  - `/docs/testing/TestingAnnotations.md` (comprehensive)
  - `/Samstraumr/samstraumr-core/src/test/resources/TagOntology.md` (focused on tags)
- **Plan**: 
  - Merge unique content from TagOntology.md into TestingAnnotations.md
  - Rename `/docs/testing/TestingAnnotations.md` to `/docs/testing/TestTagsAndAnnotations.md`
  - Add appropriate cross-references
  - Keep TagOntology.md in its test resources location as a quick reference for test developers, but add reference to the main document

## 3. Implementation Approach

For each set of duplicate files:

1. Create a new consolidated version or identify the canonical version
2. Add cross-references and redirects for backward compatibility
3. Update all references to the old documents across the codebase
4. Run tests to ensure nothing breaks
5. Remove the duplicate files
6. Update documentation to reflect the new structure

## 4. Testing Strategy

For each consolidation:

1. Make a single change at a time
2. Run appropriate tests after each change
3. Verify navigation between documents works correctly
4. Ensure cross-references are updated
5. Confirm all content is preserved