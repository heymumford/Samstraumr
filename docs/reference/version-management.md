<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Version Management

## Overview

Samstraumr uses semantic versioning (MAJOR.MINOR.PATCH) with two alternative version management systems:

1. **Original System**: Complex but feature-rich
2. **Simplified System**: Lightweight and easy to maintain

Both systems ensure consistent versioning across all project files using `version.properties` as the source of truth.

## Original Command System

```bash
# Use the original full-featured system
./s8r version <command> [options]
```

### View Commands

| Command | Description | Example |
|---------|-------------|---------|
| `get` | Show version | `./s8r version get` |
| `get -v` | Detailed info | `./s8r version get -v` |
| `export` | Just version number | `./s8r version export` |
| `history` | Version history | `./s8r version history` |
| `verify` | Check tag alignment | `./s8r version verify` |

### Modification Commands

| Command | Description | Example |
|---------|-------------|---------|
| `bump patch` | 1.2.3 → 1.2.4 | `./s8r version bump patch` |
| `bump minor` | 1.2.3 → 1.3.0 | `./s8r version bump minor` |
| `bump major` | 1.2.3 → 2.0.0 | `./s8r version bump major` |
| `set <version>` | Set specific version | `./s8r version set [VERSION]` |
| `test <type>` | Test, bump, commit, tag | `./s8r version test patch` |
| `fix-tag` | Create missing tag | `./s8r version fix-tag` |

## Simplified Command System

```bash
# Use the new lightweight system (recommended)
./s8r-version <command> [options]
```

### Commands

| Command | Description | Example |
|---------|-------------|---------|
| `get` | Show version | `./s8r-version get` |
| `bump patch` | 1.2.3 → 1.2.4 | `./s8r-version bump patch` |
| `bump minor` | 1.2.3 → 1.3.0 | `./s8r-version bump minor` |
| `bump major` | 1.2.3 → 2.0.0 | `./s8r-version bump major` |
| `set <version>` | Set specific version | `./s8r-version set [VERSION]` |
| `fix` | Fix inconsistencies | `./s8r-version fix` |

The simplified system:
- Uses less than 200 lines of code
- Has no external dependencies
- Focused on core version management tasks
- Provides clear, descriptive output
- Does not perform git operations automatically
- Can be easily extended or modified

## Files Updated

When you bump or set a version, these files are automatically synchronized:

1. `Samstraumr/version.properties` - Primary version source (source of truth)
2. `/pom.xml` - Root Maven POM file
3. `/Samstraumr/pom.xml` - Module-level POM file
4. `/Samstraumr/samstraumr-core/pom.xml` - Core module POM file
5. `/README.md` - Version badge URL and Maven dependency example
6. `CLAUDE.md` - AI context file (if it exists)
7. `docs/reference/release/changelog.md` - Changelog with new version entry

## Versioning Guidelines

- **PATCH** (1.2.3 → 1.2.4): Bug fixes and small improvements
- **MINOR** (1.2.3 → 1.3.0): New features (backward-compatible)
- **MAJOR** (1.2.3 → 2.0.0): Incompatible API changes

## Simplified System Design

The simplified system follows these design principles:

1. **Simplicity**: Single script with clear responsibilities
2. **Reliability**: Robust error handling and validation
3. **Consistency**: Ensures all version references stay synchronized
4. **Transparency**: Clear output showing what changed and where

### Example Output

```
Updating version from 1.7.1 to 1.7.2
✓ Updated version.properties
✓ Updated project version in pom.xml
✓ Updated property in pom.xml
✓ Updated project version in Samstraumr/pom.xml
✓ Updated property in Samstraumr/pom.xml
✓ Updated project version in Samstraumr/samstraumr-core/pom.xml
✓ Updated parent in Samstraumr/samstraumr-core/pom.xml
✓ Updated README.md
✓ Updated CLAUDE.md
✓ Updated changelog.md
Updated 6 files
Version successfully updated to 1.7.2
```

## Changelog Integration

The system automatically creates new entries in the changelog file whenever the version is bumped or set. It adds a template with empty sections for Added, Changed, and Fixed that developers can then edit to document the changes in that version.

## Best Practices

1. Use semantic versioning consistently
2. Run tests before finalizing version changes
3. Use `fix` command to correct any synchronization issues
4. Consider using the simplified system for most operations