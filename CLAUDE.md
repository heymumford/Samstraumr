# Samstraumr Context for Claude

## Version Management System

Version management in the Samstraumr project now follows a consistent approach:

- **Single Source of Truth**: `/Samstraumr/version.properties` is the canonical source for the current version
- **Synchronized Files**: When the version is updated, all related files are automatically kept in sync
- **Reliable Consistency**: No more manual updates or out-of-sync files

### Files Automatically Synchronized

1. `/Samstraumr/version.properties` - Primary source of truth
2. `/pom.xml` - Root Maven POM file 
3. `/Samstraumr/pom.xml` - Module-level POM file
4. `/Samstraumr/samstraumr-core/pom.xml` - Core module POM file
5. `/README.md` - Version badge URL and Maven dependency example 
6. `CLAUDE.md` - Version references in the AI assistant context

### Command-Line Interface

Version management happens through the `s8r` CLI:

```bash
# Get the current version
./s8r version get

# Bump the patch version
./s8r version bump patch

# Set a specific version
./s8r version set 1.6.7

# Test, bump and tag
./s8r version test patch
```

### Implementation Details

The version management system is implemented with the following components:

1. `util/lib/version-lib.sh` - Core library with the `update_version_in_files()` function
2. `util/bin/version/version-manager.sh` - Primary CLI for version management
3. `util/bin/version/version-manager-modular.sh` - Modular version of the tool
4. `util/bin/version/test-version-sync.sh` - Test script to verify synchronization

## Current Version

The current version of Samstraumr is: 1.6.7 (as of April 04, 2025)