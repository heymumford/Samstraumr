<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Enhanced Version Management Guide

This document provides a comprehensive guide to the enhanced version management system in Samstraumr.

## Overview

The Samstraumr version management system ensures consistent versioning across the entire codebase. It follows semantic versioning (MAJOR.MINOR.PATCH) and ensures all references to the current version are kept in sync.

Key features:
- **Robust Search and Replace**: Context-aware updates that avoid changing unrelated version strings
- **Version Consistency Checking**: Find and fix inconsistent version references
- **Backup Mechanisms**: Important files are backed up before changes are made
- **Detailed Debug Output**: Verbose mode shows exactly what changes are being made

## Single Source of Truth

The canonical source for version information is:
```
/Samstraumr/version.properties
```

When version changes are made, several files are automatically updated:
1. `/Samstraumr/version.properties` - Primary source of truth
2. `/pom.xml` - Root Maven POM file 
3. `/Samstraumr/pom.xml` - Module-level POM file
4. `/Samstraumr/samstraumr-core/pom.xml` - Core module POM file
5. `/README.md` - Version badge URL and Maven dependency example
6. `/CLAUDE.md` - Version references in the AI assistant context
7. Any Java test files that reference the version in JAR file names

## Commands

### Basic Version Operations

```bash
# Show the current version
./s8r-version get

# Export the version as plain text (for scripts)
./s8r-version export

# Bump the patch version (2.5.1 -> 2.5.2)
./s8r-version bump patch

# Bump the minor version (2.5.1 -> 2.6.0)
./s8r-version bump minor

# Bump the major version (2.5.1 -> 3.0.0)
./s8r-version bump major

# Set a specific version
./s8r-version set 2.6.0
```

### Advanced Operations

```bash
# Check for version inconsistencies
./s8r-version check

# Fix all version inconsistencies
./s8r-version fix

# List all references to the current version
./s8r-version list-refs

# Find references to a specific version
./s8r-version find 2.4.6

# Enable debug mode for any command
./s8r-version --debug bump minor
```

## Debugging Version Issues

If you encounter issues with version management:

1. Run the check command with debug output:
   ```bash
   ./s8r-version --debug check
   ```

2. Fix specific inconsistencies:
   ```bash
   ./s8r-version fix
   ```

3. If automatic fixes fail, you can manually update the files listed in the inconsistency check.

## Adding New Files to Version Management

If you add a new file that needs to reference the Samstraumr version:

1. Add the file path to the key_files array in the script
2. Add appropriate handling in the find_inconsistencies function based on the file type
3. Update the update_version function to handle the new file type if needed

## Implementation Notes

The version management tool uses:

- Context-aware grep and sed commands to ensure only relevant version strings are changed
- File backups to prevent data loss during updates
- Pattern matching that respects the structure of different file types (XML, Markdown, Java, Properties)
- Verification steps to confirm changes were made correctly

## Differences from Earlier Version System

The enhanced version management system offers several improvements over the original:

1. **Context-aware updates** - Only changes relevant version references, not dependency versions
2. **Robust consistency checking** - Finds and reports inconsistent version references
3. **Built-in debugging** - Detailed output helps diagnose version issues
4. **File backups** - Protects against data loss during updates
5. **Better error handling** - Gracefully handles edge cases and prevents unintended changes

## Best Practices

1. Always run `check` before making changes to verify consistency
2. Use debug mode when diagnosing version issues
3. Add new files to version management as they're created
4. Follow semantic versioning guidelines
   - Patch (x.y.Z): Bug fixes, documentation changes
   - Minor (x.Y.z): New features, backward compatible changes
   - Major (X.y.z): Breaking changes, major refactoring