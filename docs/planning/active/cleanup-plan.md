# Repository Cleanup Plan (Completed)

This document outlines the plan for organizing files in the repository to maintain a clean and structured codebase.

## Files to Organize

### Test Files to Move to /test-scripts Directory

These files should be moved to a new `test-scripts` directory:
- s8r-acceptance-tests.sh
- s8r-component-tests.sh
- s8r-composite-tests.sh
- s8r-machine-tests.sh
- s8r-system-tests.sh
- s8r-test-framework.sh
- s8r-unit-tests.sh
- s8r-run-tests.sh
- test-cli-short.sh
- test-one.sh
- test-help.sh
- test-machine-adapter.sh
- verify-cli.sh

### Script Files to Move to /util/scripts Directory

These utility scripts should be organized in the util/scripts directory:
- flatten-component-layer.sh
- flatten-component-layer-alt.sh
- flatten-documentation-structure.sh
- flatten-domain-layer.sh
- flatten-feature-files.sh
- flatten-source-code-structure.sh
- flatten-test-structure.sh
- enhance-component-documentation.sh
- enhance-domain-documentation.sh
- fix-test-imports.sh
- fix-tests.sh
- merge-redundant-resources.sh
- standardize-todo-implementation.sh

### Documentation Files to Move to /docs Directory

These documentation files should be organized in appropriate subdirectories of /docs:
- consolidation-summary.md -> docs/planning/completed/
- directory-flattening-migration-guide.md -> docs/guides/migration/
- directory-flattening-phase5-summary.md -> docs/planning/completed/
- directory-flattening-summary.md -> docs/planning/completed/
- documentation-flattening-summary.md -> docs/planning/completed/
- phase5-implementation-summary.md -> docs/planning/completed/
- source-code-flattening-plan.md -> docs/planning/archived/
- source-code-flattening-summary.md -> docs/planning/completed/
- redundant-resources-analysis.md -> docs/planning/completed/

### Consolidation Scripts to Move to /util/bin Directory

These consolidation scripts should be moved to util/bin:
- cleanup-legacy-packages.sh
- cleanup-repo.sh
- cleanup-scripts.sh
- consolidate-documentation.sh
- consolidate-header-scripts.sh
- consolidate-repository.sh
- consolidate-temporary-files.sh
- create-component-symlinks.sh

### Temporary/Backup Directories to Remove

These directories appear to be temporary backups and should be removed:
- tag-standardization-backup-* (multiple directories)
- redundant-resources-backup-*
- feature-file-backup-*

### Version and Build Scripts to Organize

Version and build scripts should be organized:
- s8r-build -> Keep in root (primary user command)
- s8r-build-new -> Move to util/bin/build/
- s8r-test -> Keep in root (primary user command)
- s8r-test-new -> Move to util/bin/test/
- s8r-version -> Keep in root (primary user command)
- s8r-version-new -> Move to util/bin/version/
- s8r-updated -> Move to util/bin/archived/
- s8r-new -> Move to util/bin/archived/

### Special Files to Handle

- VALIDATION-TODOS.md -> Move to docs/planning/active/
- stack-trace-output.log -> Delete (temporary file)
- push -> Delete (temporary file)

## Implementation Steps

1. Create any missing target directories
2. Move files to their designated locations
3. Update any references/symlinks as needed
4. Remove temporary backup directories
5. Verify all scripts still function after reorganization