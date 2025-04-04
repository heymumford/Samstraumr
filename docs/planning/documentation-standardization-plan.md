# Documentation Standardization Plan

This document outlines the plan for standardizing all documentation in the Samstraumr project to ensure consistency, clarity, and improved maintainability.

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
3. ✅ Document standard conventions in `/docs/reference/standards/documentation-standards.md`
4. ✅ Identify outdated planning documents for removal or archiving

### Phase 2: file naming and organization (in progress)

1. ⬜ Rename all documentation files to use kebab-case:
   - Update script `docs/scripts/standardize-md-filenames.sh` to handle remaining files
   - Convert all PascalCase filenames (e.g., `GettingStarted.md` → `getting-started.md`)
   - Convert all UPPERCASE filenames (e.g., `KANBAN.md` → `kanban.md`)
   - Convert filenames with spaces and special characters (e.g., `Life Cycle Stage Comparison_.md` → `life-cycle-stage-comparison.md`)
2. ⬜ Reorganize files into proper directories:
   - Move remaining files in root `/docs` to appropriate subdirectories
   - Consolidate duplicate files (e.g., `FolderStructure.md` appears in multiple locations)
   - Update cross-references to reflect new file locations

### Phase 3: content standardization

1. ⬜ Update headers to follow standard conventions:
   - Level 1 headers match title case of file
   - Level 2 headers use title case
   - Level 3+ headers use sentence case
2. ⬜ Standardize cross-references:
   - Convert absolute paths to relative paths
   - Ensure all links include .md extension
   - Fix broken links from file reorganization
3. ⬜ Standardize code blocks and formatting:
   - Add language specifiers to all code blocks
   - Ensure consistent tab/space usage
   - Standardize emphasis formatting (bold/italic)

### Phase 4: planning document cleanup

1. ⬜ Review and update current planning documents:
   - Archive completed plans with "ARCHIVED-" prefix
   - Update ongoing plans to reflect current status
   - Remove obsolete plans that no longer apply
   - Consolidate duplicate planning information
2. ⬜ Ensure all planning documents follow the new standards:
   - Rename to kebab-case
   - Update headers to follow conventions
   - Fix cross-references
   - Standardize formatting

### Phase 5: automation and verification

1. ⬜ Enhance existing standardization scripts:
   - Update `standardize-md-filenames.sh` to handle additional cases
   - Create script to check and update header formatting
   - Create script to validate cross-references
2. ⬜ Add documentation standards check to CI pipeline:
   - Verify filename conventions
   - Check header formatting
   - Validate cross-references

## Implementation Schedule

| Phase |             Task             | Start Date | Target Completion |     Status     |
|-------|------------------------------|------------|-------------------|----------------|
| 1     | Inventory and Analysis       | 2025-04-03 | 2025-04-04        | ✅ Complete     |
| 2     | File Naming and Organization | 2025-04-04 | 2025-04-06        | 🔄 In Progress |
| 3     | Content Standardization      | 2025-04-06 | 2025-04-08        | ⬜ Not Started  |
| 4     | Planning Document Cleanup    | 2025-04-08 | 2025-04-09        | ⬜ Not Started  |
| 5     | Automation and Verification  | 2025-04-09 | 2025-04-10        | ⬜ Not Started  |

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

- [Documentation Standards](../reference/standards/documentation-standards.md)
- [File Organization Standards](../reference/standards/file-organization.md)
