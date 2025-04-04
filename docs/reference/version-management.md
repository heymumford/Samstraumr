<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Version Management

This guide provides comprehensive documentation for Samstraumr's version management system.

## Overview

Samstraumr uses semantic versioning (MAJOR.MINOR.PATCH) stored in a central `version.properties` file. The version management system provides commands for viewing, setting, bumping, and validating versions, as well as creating and verifying Git tags.

## Command Structure

All version management commands are accessible through the `s8r` CLI:

```bash
./s8r version <command> [options]
```

## Available Commands

### View commands

|  Command  |         Description         |         Example         |
|-----------|-----------------------------|-------------------------|
| `get`     | Display current version     | `./s8r version get`     |
| `get -v`  | Show detailed version info  | `./s8r version get -v`  |
| `export`  | Output only version number  | `./s8r version export`  |
| `history` | Show version history        | `./s8r version history` |
| `verify`  | Check version-tag alignment | `./s8r version verify`  |

### Modification commands

|     Command     |       Description       |          Example           |
|-----------------|-------------------------|----------------------------|
| `bump major`    | Increment major version | `./s8r version bump major` |
| `bump minor`    | Increment minor version | `./s8r version bump minor` |
| `bump patch`    | Increment patch version | `./s8r version bump patch` |
| `set <version>` | Set specific version    | `./s8r version set 1.2.3`  |

### Git integration commands

|          Command          |        Description         |                Example                 |
|---------------------------|----------------------------|----------------------------------------|
| `fix-tag`                 | Create missing version tag | `./s8r version fix-tag`                |
| `bump <type> --no-commit` | Bump without commit/tag    | `./s8r version bump patch --no-commit` |
| `test <type>`             | Bump, test, commit, tag    | `./s8r version test patch`             |
| `test <type> --push`      | Test with remote push      | `./s8r version test patch --push`      |

## Command Options

|      Option      |                Description                |
|------------------|-------------------------------------------|
| `-h, --help`     | Display help information                  |
| `-v, --verbose`  | Enable detailed output                    |
| `--no-commit`    | Skip Git commit/tag creation              |
| `--skip-tests`   | Skip running tests (for test command)     |
| `--skip-quality` | Skip quality checks (for test command)    |
| `--push`         | Push changes to remote (for test command) |

## Detailed Usage Guide

### Viewing the current version

To see the current version:

```bash
./s8r version get
```

For detailed information including tag status and last commit:

```bash
./s8r version get -v
```

To get only the version number (for scripts):

```bash
version=$(./s8r version export)
echo "Current version: $version"
```

### Checking version history

To view the history of version changes:

```bash
./s8r version history
```

### Bumping versions

To increment a version component:

```bash
# Version Management
./s8r version bump patch

# Version Management
./s8r version bump minor

# Version Management
./s8r version bump major
```

### Setting a specific version

To set a specific version:

```bash
./s8r version set 1.2.3
```

### Verifying version tags

To check if the Git tag matches the current version:

```bash
./s8r version verify
```

If the tag is missing, you can create it:

```bash
./s8r version fix-tag
```

### Testing after version change

For a complete workflow that bumps version, runs tests, commits, and tags:

```bash
./s8r version test patch
```

With automatic push to remote:

```bash
./s8r version test patch --push
```

Skipping tests but still performing other operations:

```bash
./s8r version test patch --skip-tests
```

### Advanced usage

Bump version without committing (useful for verification):

```bash
./s8r version bump patch --no-commit
```

## Files Updated During Version Changes

When you bump or set a version, the following files are automatically synchronized:

1. `Samstraumr/version.properties` - The primary version source (source of truth)
2. `/pom.xml` - Root Maven POM file (both project version and properties)
3. `/Samstraumr/pom.xml` - Module-level POM file (project version and properties)
4. `/Samstraumr/samstraumr-core/pom.xml` - Core module POM file (project version and parent version)
5. `/README.md` - Version badge URL and Maven dependency example
6. `CLAUDE.md` - AI context file (if it exists)

## Versioning Strategy Guidelines

### When to bump versions

- **PATCH** (1.2.3 → 1.2.4): Bug fixes and small improvements that don't add functionality or break existing APIs.
  - Patch version can roll up to 999 before automatically incrementing the minor version
  - Example: 1.2.999 → 1.3.0 (when bumping patch)
- **MINOR** (1.2.3 → 1.3.0): New features in a backward-compatible manner.
- **MAJOR** (1.2.3 → 2.0.0): Incompatible API changes or significant architectural changes.

### Git integration

All version changes are automatically committed (with descriptive commit messages) and tagged with the format `v1.2.3`. You can disable this behavior with `--no-commit`.

### Testing integration

The `test` command performs a complete workflow:
1. Bump version
2. Run tests
3. If tests pass, commit and create tag
4. If tests fail, revert version change
5. Optionally push changes to remote repository

## Configuration

The version management system uses the following configuration files:

- `.s8r/config/version.conf` - Version management settings

### Example configuration content

```bash
# Version Management
VERSION_FILE="${PROJECT_ROOT}/Samstraumr/version.properties"

# Version Management
VERSION_PROPERTY_NAME="samstraumr.version"

# Version Management
GIT_TAG_PREFIX="v"

# Version Management
COMMIT_MESSAGE_MAJOR="Bump major version from %s to %s\n\nBreaking API changes"
COMMIT_MESSAGE_MINOR="Bump minor version from %s to %s\n\nNew features without breaking changes"
COMMIT_MESSAGE_PATCH="Bump patch version from %s to %s\n\nBug fixes and improvements"

# Version Management
RUN_TESTS_AFTER_BUMP=true
TEST_COMMAND="${PROJECT_ROOT}/util/bin/test/run-tests.sh all"
```

## Architecture

The version management system uses a modular architecture with separate components:

1. `s8r` - Main CLI entry point
2. `.s8r/config/version.conf` - Configuration settings
3. `util/lib/version-lib.sh` - Core utilities and version synchronization
4. `util/bin/version/commands/` - Command modules:
   - `get-commands.sh` - Version retrieval
   - `set-commands.sh` - Version modification
   - `git-commands.sh` - Git operations
   - `test-commands.sh` - Test integration
5. `util/bin/version/version-manager-modular.sh` - Command router

This architecture provides clear separation of concerns, making the system more maintainable and extensible.

### Version Synchronization

The synchronization of version numbers across all files is implemented in the `update_version_in_files()` function in `util/lib/version-lib.sh`. This function:

1. Updates the version.properties file (source of truth)
2. Updates all POM files through carefully targeted replacements
3. Updates version references in README.md
4. Updates any version references in CLAUDE.md if it exists
5. Updates the "Last updated" date in version.properties

The synchronization process uses a backup system to ensure no changes are lost in case of an error, and provides detailed progress information during execution.

## Best Practices

1. **Use Semantic Versioning**: Follow SemVer principles for version numbering.
2. **Always Commit**: Let the system handle Git operations automatically.
3. **Run Tests**: Use the `test` command to ensure tests pass after version changes.
4. **Tag All Releases**: Ensure every version has a corresponding Git tag.
5. **Update Documentation**: Keep version numbers in documentation up-to-date.
