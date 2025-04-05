<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Folder Refactoring Plan

## Issues Identified

1. **Multiple Documentation Directories**:
   - `docs/`, `docs-new/`, and `docs-reorganized/` contain similar content
   - Confusing for contributors to know which is authoritative
2. **Inconsistent Top-Level Structure**:
   - Random mix of folders, scripts, and documentation files
   - No clear organization for project artifacts
3. **Unnecessary Nesting**:
   - Test code has deep hierarchies that could be simplified
   - Utility scripts are scattered in multiple locations
4. **Unused Directories**:
   - `bin/` directory appears nearly empty
   - `temp_view/` directory seems temporary
   - `src/` directory at root is redundant with main module structure

## Refactoring Plan

### 1. consolidate documentation

- Keep only `docs/` directory
- Merge valuable content from `docs-new/` and `docs-reorganized/`
- Organize documentation with a flat structure and clear naming

### 2. reorganize top-level structure

**Keep at top level**:
- README.md
- CLAUDE.md (for AI tooling)
- pom.xml (Maven parent)
- Samstraumr/ (main code module)
- docs/ (consolidated documentation)
- util/ (all utility scripts)

**Move or remove**:
- Remove `bin/` if unused
- Remove `temp_view/`
- Remove `src/` at root level (not needed)
- Move compatibility reports to `docs/compatibility/`

### 3. simplify testing structure

- Flatten test directory where possible
- Organize by test type rather than deep hierarchies
- Consolidate similar test resources

### 4. standardize utility scripts

- All utility scripts should live in the `util/` directory
- Organize utilities by function (build, test, quality, etc.)
- Remove deprecated scripts and duplicates

## Implementation Steps

1. Consolidate documentation
2. Clean up top-level directories
3. Simplify test structure
4. Standardize utility scripts
5. Update references and paths in code
6. Run tests to ensure everything works
