<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Maven Structure Migration

*Last Updated: April 7, 2025*

This document outlines the plan and progress for migrating the Samstraumr project from the `/Samstraumr/` directory structure to a more standard `/modules/` structure.

## Motivation

The current `/Samstraumr/` directory structure has several issues:

1. It uses capitalized naming which doesn't follow Maven conventions (lowercase with hyphens)
2. It's not immediately recognizable as a Maven modules directory
3. It's inconsistent with the planned migration to "s8r" naming
4. It creates potential path issues with case-sensitive file systems

## Migration Plan

### Phase 1: Preparation ✅

1. Update documentation to reflect the new directory structure:
   - Update `docs/reference/maven-structure.md`
   - Update `docs/reference/folder-structure.md`
   - Update `docs/architecture/directory-structure.md`

2. Create the new `/modules/` directory structure:
   - Create the modules directory
   - Copy the core module structure

### Phase 2: Implementation ✅

1. Update component.core package and classes:
   - Create missing directories and packages
   - Fix package declarations and imports
   - Address build errors

2. Update Maven configuration:
   - Update the root pom.xml to reference the new directory
   - Update path references in pom.xml files
   - Update configuration file references

3. Test the build with the migrated structure:
   - Run clean install to verify the build works
   - Create finalize-modules-migration.sh script to complete the migration

### Phase 3: Documentation and Scripts 🔄

1. Update documentation to reflect the changes:
   - Update folder and package references in markdown files
   - Add migration details to KANBAN.md
   - Create maven-structure-migration.md document

2. Update scripts to use the new structure:
   - Update build scripts
   - Update test scripts
   - Update utility scripts

### Phase 4: Cleanup 📋

1. Complete verification and testing:
   - Run the full test suite with the new structure
   - Verify all functionality works as expected

2. Remove the old structure:
   - Backup important files if any remain
   - Remove the old `/Samstraumr/` directory

## Migration Status

- **Phase 1 (Preparation)**: ✅ 100% Complete
- **Phase 2 (Implementation)**: ✅ 100% Complete 
- **Phase 3 (Documentation and Scripts)**: ✅ 100% Complete
  - Documentation updates: ✅ Complete
  - Script updates: ✅ Complete
- **Phase 4 (Cleanup)**: ✅ 100% Complete
  - Backup creation: ✅ Complete
  - Directory removal: ✅ Complete
  - Build verification: ✅ Complete

## Tasks Remaining

No tasks remaining. Migration completed successfully on April 7, 2025.

## Completed Tasks

1. ✅ Updated Maven structure to use `modules/` directory
2. ✅ Created component.core classes in new location
3. ✅ Tested build with updated directory structure
4. ✅ Updated configuration references in pom.xml
5. ✅ Updated documentation to reflect new directory structure
6. ✅ Updated scripts to use new directory structure with bin/update-scripts-for-modules.sh

## Related Documents

- [folder-structure.md](/docs/reference/folder-structure.md)
- [maven-structure.md](/docs/reference/maven-structure.md)
- [directory-structure.md](/docs/architecture/directory-structure.md)
- [bin/finalize-modules-migration.sh](/bin/finalize-modules-migration.sh)