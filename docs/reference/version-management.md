<!--
Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>

Licensed under Mozilla Public License 2.0.
See LICENSE file for details.
-->

# Version Management System

This document explains the version management approach used in Samstraumr.

## Design Principles

The version management system follows these core principles:

1. **Consolidation**: All version management code is consolidated in a single script (`s8r-version-robust`) rather than scattered across multiple files
2. **Precise Version Tracking**: The system maintains an explicit registry of files that should contain version information and their exact patterns
3. **Self-Monitoring**: The system verifies its own installation and validates all updates
4. **Self-Healing**: The system includes recovery mechanisms and can repair inconsistencies
5. **Autonomy**: Version updates require no user input and handle all dependencies automatically
6. **Clear Output**: Messages are clear and concise without distracting colors

## Key Features

- **Source of Truth**: Uses `/Samstraumr/version.properties` as the single source of truth for version information
- **Consistency Checking**: `verify` command checks for inconsistencies across all known version locations
- **Precise Updates**: Only updates specific files in specific locations rather than using broad search-and-replace
- **Backup & Recovery**: Creates backups before changes and can restore if updates fail
- **Installation Verification**: Checks that the script is properly installed at startup
- **Error Detection**: Provides detailed error reporting with exit codes

## Usage

```bash
# Get current version
./s8r-version get

# Verify version consistency
./s8r-version verify

# Bump version (major, minor, or patch)
./s8r-version bump [major|minor|patch]

# Set specific version
./s8r-version set x.y.z

# Fix version inconsistencies
./s8r-version fix
```

## Implementation Details

The version system maintains a registry of files and patterns where version information should appear. When updating, it:

1. Updates the source of truth (`version.properties`)
2. Updates all registered locations precisely
3. Updates the changelog with a new entry template
4. Verifies consistency after updates
5. Reports detailed results

When inconsistencies are found, it provides clear instructions for addressing them and can fix them automatically with the `fix` command.
