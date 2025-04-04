# Version Management Strategy

## Version Script Consolidation

The version management functionality has been consolidated into a single, unified script:

- `./util/version` - Consolidated version management utility

### Legacy Scripts (Deprecated)

The following scripts are now deprecated and will be removed in a future release.
They all redirect to the main `util/version` script:

- `./util/version.sh` - Deprecated version management wrapper
- `./bin/update-version.sh` - Deprecated redirect script
- `./util/update-version-test.sh` - Deprecated version bump and test script

## Usage

### Basic Version Management

```bash
# Show current version information
./util/version get

# Output only the version number (for scripts and CI)
./util/version export

# Verify version and tag alignment
./util/version verify

# Create a git tag for the current version (if missing)
./util/version fix-tag

# Show version history
./util/version history
```

### Version Bumping

```bash
# Bump patch version (1.2.3 → 1.2.4)
./util/version bump patch

# Bump minor version (1.2.3 → 1.3.0)
./util/version bump minor

# Bump major version (1.2.3 → 2.0.0)
./util/version bump major

# Set a specific version
./util/version set 1.5.0
```

### Bump, Test and Commit

The `test` command combines version bumping, running tests, and creating a commit and tag:

```bash
# Bump patch version, run tests, create commit and tag
./util/version test patch

# Bump minor version, skip tests
./util/version test minor --skip-tests

# Bump major version, skip quality checks, push to remote
./util/version test major --skip-quality --push
```

### Options

- `--no-commit`: Don't automatically commit the version change
- `--skip-tests`: Skip running tests (for `test` command only)
- `--skip-quality`: Skip quality checks (for `test` command only)
- `--push`: Push changes to remote (for `test` command only)
- `--help`: Show help message

## Version/Tag Synchronization

The utility now ensures that git tags always match the version number:

1. When bumping a version, it verifies if the current version has a matching tag
2. If not, it warns and asks for confirmation before proceeding
3. After bumping the version, it automatically creates a git tag
4. The `verify` command can check if version and tag are aligned
5. The `fix-tag` command can create a missing tag for the current version
