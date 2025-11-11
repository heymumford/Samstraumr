<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Script Reorganization Plan

This document outlines the plan to reorganize scripts in the Samstraumr repository to maintain a clean and structured directory layout.

## Current Issues

1. Several scripts are located in the root directory that should be in appropriate utility folders
2. Some scripts have duplicate functionality but different names
3. Missing symbolical links for backward compatibility

## Scripts to Relocate

### Structure and maven scripts

| Current Location | New Location | Purpose |
|------------------|--------------|---------|
| `/maven-structure-test.sh` | `/util/scripts/test/maven-structure-test.sh` | Tests Maven structure compliance |
| `/maven-profile-test.sh` | `/util/scripts/test/maven-profile-test.sh` | Tests Maven profile functionality |
| `/test-maven-structure.sh` | `/util/scripts/test/maven-structure-test.sh` (with symlink) | Tests Maven structure compliance (duplicate) |

### Architecture test scripts

| Current Location | New Location | Purpose |
|------------------|--------------|---------|
| `/run-architecture-tests.sh` | `/util/scripts/test/run-architecture-tests.sh` | Runs architecture verification tests |

### Initialization and workflow scripts

| Current Location | New Location | Purpose |
|------------------|--------------|---------|
| `/run-initializer.sh` | `/util/scripts/initialize.sh` (with symlink) | Initializes the project |
| `/trigger-workflow.sh` | `/util/scripts/ci/trigger-workflow.sh` | Triggers CI workflows |

### Utility scripts

| Current Location | New Location | Purpose |
|------------------|--------------|---------|
| `/update-standardized-headers.sh` | `/util/scripts/update-standardized-headers.sh` | Updates file headers |
| `/use-java21.sh` | `/util/scripts/java/use-java21.sh` (with symlink) | Sets up Java 21 environment |

## Implementation Steps

1. Create necessary subdirectories
2. Move each script to its new location
3. Create symbolic links for backward compatibility
4. Update documentation references
5. Test that all scripts still work correctly

## Backward Compatibility

For each relocated script, we will create a symbolic link from the original location to the new location to ensure backward compatibility. This will prevent breaking existing tooling or documentation.

## Documentation Updates

After relocation, the following documents will need to be updated:

1. `/README.md` - Update references to root scripts
2. `/CLAUDE.md` - Update script locations for the AI assistant context
3. `/docs/reference/cli-reference.md` - Update command reference

## Expected Benefits

1. **Cleaner Root Directory**: Reduces clutter in the root directory
2. **Better Organization**: Groups related scripts together
3. **Improved Discoverability**: Makes it easier to find scripts by function
