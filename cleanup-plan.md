# Samstraumr Repository Cleanup Plan

This document outlines the steps for cleaning up the Samstraumr repository to reduce technical debt, eliminate redundancy, and streamline the codebase.

## 1. Removal of Backup and Temporary Directories

We've identified the following backup/temporary directories that can be safely removed:

- `/backup-before-migration-20250404220413/` - This appears to be a backup created during the package migration. Since the migration has been completed and tested, this backup can be safely removed.
- `/backup-legacy/` - An older backup of legacy code that has been migrated to the new package structure.
- `/.script_backups/` - Contains empty directories and is no longer needed.

## 2. Cleanup of Duplicate Code Patterns

Based on the document at `/duplicate-cleanup-plan/component-duplicates.md`, we should consolidate duplicate classes following the Clean Architecture model:

1. **Component Classes** - Consolidate into `org.s8r.domain.component.Component`
2. **Machine Classes** - Consolidate into `org.s8r.domain.machine.Machine`
3. **Composite Classes** - Consolidate into `org.s8r.domain.component.composite` package
4. **Environment Classes** - Consolidate to a single implementation in domain layer
5. **Logger Classes** - Consolidate interfaces and implementations

The consolidation should follow these steps for each duplicate:
- Keep the implementation in the appropriate layer according to Clean Architecture
- Provide adapters/compatibility classes where needed
- Update all references to use the canonical implementation
- Add deprecation annotations to legacy versions

## 3. Script Consolidation

Redundant and duplicate scripts should be consolidated:

- Many similar scripts exist in `/util/scripts/`, `/util/bin/`, and the project root
- Create a unified script library in `/util/lib/` with common functions
- Create simplified wrapper scripts that use the common library

Specifically, we should:
- Consolidate all header-related scripts into a single script
- Merge testing scripts into a unified test library
- Create a "tools" directory for simplified user-facing scripts
- Remove or mark as deprecated any redundant scripts

## 4. Target and Build Directory Cleanup

- Remove or add to `.gitignore`: `/Samstraumr/samstraumr-core/target/`
- Ensure all `*.class` files are ignored
- Remove any other build artifacts that have been committed

## 5. Documentation Consolidation

- Eliminate duplicated documentation
- Consolidate strategy documents
- Create clearer pointers from deprecated to canonical documentation

## 6. Other Cleanups

- Remove redundant Maven configuration
- Consolidate utility classes
- Clean up and remove any temp files (`*.tmp`, `*.bak`, etc.)

## Implementation Plan

1. First, create git tags to mark the current state for safety
2. Remove backup directories
3. Implement duplicate code consolidation in phases:
   - Phase 1: Core components (Component, Machine)
   - Phase 2: Supporting components (Composite, Environment)
   - Phase 3: Legacy tube cleanup
4. Consolidate scripts
5. Update documentation
6. Run full test suite after each phase

## Testing Strategy

- After each step, run the full test suite (`./s8r test all`)
- Verify that the build still works (`./s8r build`)
- Ensure documentation matches the implementation

## Expected Outcome

- Reduced repository size
- Cleaner code organization
- Elimination of redundant code
- Better adherence to Clean Architecture principles
- Simplified maintenance and onboarding