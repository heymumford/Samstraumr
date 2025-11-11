<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Active Documentation Standardization

This document outlines the plan for standardizing all documentation in the Samstraumr project to ensure consistency, clarity, and improved maintainability.

## Metadata

- **Category**: Documentation
- **Priority**: P1
- **Status**: In Progress
- **Owner**: Claude
- **Due**: 2025-04-06
- **Issue**: N/A

## Current Status

Documentation files in the Samstraumr project currently have inconsistencies in:
- File naming conventions (mix of PascalCase, kebab-case, and UPPERCASE)
- Header styles (inconsistent capitalization and formatting)
- Organization (files in incorrect directories)
- Cross-references (mix of absolute and relative paths)
- Comment styles and formatting (inconsistent code blocks, line breaks, etc.)

## Standardization Goals

1. **Consistent file naming**: Use kebab-case for all documentation files
2. **Standardized headers**: Level 1 headers match file title case, level 2 use title case, level 3+ use sentence case
3. **Proper organization**: Files stored in the correct directories based on content type
4. **Consistent cross-references**: Use relative paths with .md extension
5. **Uniform formatting**: Consistent comment styles, code blocks, and emphasis

## Implementation Plan

### Phase 1: inventory and analysis (completed)

1. ✅ Create comprehensive inventory of all documentation files
2. ✅ Analyze current inconsistencies and patterns
3. ✅ Document standard conventions in `/docs/ref/standards-documentation.md`
4. ✅ Identify outdated planning documents for removal or archiving

### Phase 2: file naming and organization (completed)

1. ✅ Rename all documentation files to use kebab-case:
   - Update script `docs/tools/standardize-filenames.sh` to handle remaining files
   - Convert all PascalCase filenames (e.g., `GettingStarted.md` → `getting-started.md`)
   - Convert all UPPERCASE filenames (e.g., `KANBAN.md` → `kanban.md`)
   - Convert filenames with spaces and special characters (e.g., `Life Cycle Stage Comparison_.md` → `life-cycle-stage-comparison.md`)
2. ✅ Reorganize files into proper directories:
   - Move remaining files in root `/docs` to appropriate subdirectories
   - Consolidate duplicate files (e.g., `FolderStructure.md` appears in multiple locations)
   - Update cross-references to reflect new file locations

### Phase 3: content standardization (completed)

1. ✅ Update headers to follow standard conventions:
   - Level 1 headers match title case of file
   - Level 2 headers use title case
   - Level 3+ headers use sentence case
2. ✅ Standardize cross-references:
   - Convert absolute paths to relative paths
   - Ensure all links include .md extension
   - Fix broken links from file reorganization
3. ✅ Standardize code blocks and formatting:
   - Add language specifiers to all code blocks
   - Ensure consistent tab/space usage
   - Standardize emphasis formatting (bold/italic)

### Phase 4: directory simplification (completed)

1. ✅ Reduce from 20 directories to 9 core directories
2. ✅ Implement prefix-based file naming convention
3. ✅ Create README.md files for each directory with navigation guidance
4. ✅ Migrate remaining files with script to automate the process
5. ✅ Update all cross-references to match new structure

### Phase 5: automation and verification (completed)

1. ✅ Enhance existing standardization scripts:
   - Updated `standardize-filenames.sh` to handle additional cases
   - Created `update-cross-references.sh` to fix and validate cross-references
   - Created `check-documentation-standards.sh` to verify standards compliance
2. ✅ Add documentation standards check to CI pipeline:
   - Verify filename conventions
   - Check header formatting
   - Validate cross-references
   - Generate compliance reports

## Implementation Schedule

| Phase |             Task             | Start Date | Target Completion | Actual Completion |     Status     |
|-------|------------------------------|------------|-------------------|-------------------|----------------|
| 1     | Inventory and Analysis       | 2025-04-03 | 2025-04-04        | 2025-04-04        | ✅ Complete     |
| 2     | File Naming and Organization | 2025-04-04 | 2025-04-06        | 2025-04-06        | ✅ Complete     |
| 3     | Content Standardization      | 2025-04-06 | 2025-04-08        | 2025-04-06        | ✅ Complete     |
| 4     | Directory Simplification     | 2025-04-06 | 2025-04-07        | 2025-04-06        | ✅ Complete     |
| 5     | Automation and Verification  | 2025-04-06 | 2025-04-10        | 2025-04-06        | ✅ Complete     |

## Future Considerations

1. **Automated Documentation Generation**: Consider tools to generate documentation from code
2. **Documentation Testing**: Implement automated tests for documentation validity
3. **Interactive Documentation**: Explore options for more interactive documentation formats
4. **Contribution Guidelines**: Update contribution guide with documentation standards

## Success Metrics

1. **Consistency Score**: Percentage of files following all standards
2. **Link Validity**: Percentage of valid cross-references
3. **Developer Feedback**: Survey developers on documentation usability

## References

- [standard-documentation.md](../ref/standard-documentation.md)
- [standard-file-organization.md](../ref/standard-file-organization.md)
- [standard-java-naming.md](../ref/standard-java-naming.md)
