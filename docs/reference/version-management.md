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

### View Commands

| Command | Description | Example |
|---------|-------------|---------|
| `get` | Display current version | `./s8r version get` |
| `get -v` | Show detailed version info | `./s8r version get -v` |
| `export` | Output only version number | `./s8r version export` |
| `history` | Show version history | `./s8r version history` |
| `verify` | Check version-tag alignment | `./s8r version verify` |

### Modification Commands

| Command | Description | Example |
|---------|-------------|---------|
| `bump major` | Increment major version | `./s8r version bump major` |
| `bump minor` | Increment minor version | `./s8r version bump minor` |
| `bump patch` | Increment patch version | `./s8r version bump patch` |
| `set <version>` | Set specific version | `./s8r version set 1.2.3` |

### Git Integration Commands

| Command | Description | Example |
|---------|-------------|---------|
| `fix-tag` | Create missing version tag | `./s8r version fix-tag` |
| `bump <type> --no-commit` | Bump without commit/tag | `./s8r version bump patch --no-commit` |
| `test <type>` | Bump, test, commit, tag | `./s8r version test patch` |
| `test <type> --push` | Test with remote push | `./s8r version test patch --push` |

## Command Options

| Option | Description |
|--------|-------------|
| `-h, --help` | Display help information |
| `-v, --verbose` | Enable detailed output |
| `--no-commit` | Skip Git commit/tag creation |
| `--skip-tests` | Skip running tests (for test command) |
| `--skip-quality` | Skip quality checks (for test command) |
| `--push` | Push changes to remote (for test command) |

## Detailed Usage Guide

### Viewing the Current Version

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

### Checking Version History

To view the history of version changes:

```bash
./s8r version history
```

### Bumping Versions

To increment a version component:

```bash
# Patch: 1.2.3 → 1.2.4 (for bug fixes)
./s8r version bump patch

# Minor: 1.2.3 → 1.3.0 (for new features)
./s8r version bump minor

# Major: 1.2.3 → 2.0.0 (for breaking changes)
./s8r version bump major
```

### Setting a Specific Version

To set a specific version:

```bash
./s8r version set 1.2.3
```

### Verifying Version Tags

To check if the Git tag matches the current version:

```bash
./s8r version verify
```

If the tag is missing, you can create it:

```bash
./s8r version fix-tag
```

### Testing After Version Change

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

### Advanced Usage

Bump version without committing (useful for verification):

```bash
./s8r version bump patch --no-commit
```

## Files Updated During Version Changes

When you bump or set a version, the following files are updated:

1. `Samstraumr/version.properties` - The primary version source
2. Maven POM files (if present)
3. README.md version badge (if present)

## Versioning Strategy Guidelines

### When to Bump Versions

- **PATCH** (1.2.3 → 1.2.4): Bug fixes and small improvements that don't add functionality or break existing APIs.
- **MINOR** (1.2.3 → 1.3.0): New features in a backward-compatible manner.
- **MAJOR** (1.2.3 → 2.0.0): Incompatible API changes or significant architectural changes.

### Git Integration

All version changes are automatically committed (with descriptive commit messages) and tagged with the format `v1.2.3`. You can disable this behavior with `--no-commit`.

### Testing Integration

The `test` command performs a complete workflow:
1. Bump version
2. Run tests
3. If tests pass, commit and create tag
4. If tests fail, revert version change
5. Optionally push changes to remote repository

## Configuration

The version management system uses the following configuration files:

- `.s8r/config/version.conf` - Version management settings

### Example Configuration Content

```bash
# Version file path
VERSION_FILE="${PROJECT_ROOT}/Samstraumr/version.properties"

# Version property name in the properties file
VERSION_PROPERTY_NAME="samstraumr.version"

# Git tag settings
GIT_TAG_PREFIX="v"

# Default message templates
COMMIT_MESSAGE_MAJOR="Bump major version from %s to %s\n\nBreaking API changes"
COMMIT_MESSAGE_MINOR="Bump minor version from %s to %s\n\nNew features without breaking changes"
COMMIT_MESSAGE_PATCH="Bump patch version from %s to %s\n\nBug fixes and improvements"

# Test settings
RUN_TESTS_AFTER_BUMP=true
TEST_COMMAND="${PROJECT_ROOT}/util/bin/test/run-tests.sh all"
```

## Architecture

The version management system uses a modular architecture with separate components:

1. `s8r` - Main CLI entry point
2. `.s8r/config/version.conf` - Configuration settings
3. `util/lib/version-lib.sh` - Core utilities
4. `util/bin/version/commands/` - Command modules:
   - `get-commands.sh` - Version retrieval
   - `set-commands.sh` - Version modification
   - `git-commands.sh` - Git operations
   - `test-commands.sh` - Test integration
5. `util/bin/version/version-manager-modular.sh` - Command router

This architecture provides clear separation of concerns, making the system more maintainable and extensible.

## Best Practices

1. **Use Semantic Versioning**: Follow SemVer principles for version numbering.
2. **Always Commit**: Let the system handle Git operations automatically.
3. **Run Tests**: Use the `test` command to ensure tests pass after version changes.
4. **Tag All Releases**: Ensure every version has a corresponding Git tag.
5. **Update Documentation**: Keep version numbers in documentation up-to-date.
6. **Document Changes**: Maintain a changelog of what changed between versions.