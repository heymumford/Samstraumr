# Samstraumr Version Management Guide

This guide explains both the strategic approach and practical details of Samstraumr's version management system.

## Strategic Approach

Samstraumr follows a centralized version management strategy with these core principles:

1. **Single Source of Truth**: One canonical location for the version number
2. **Automated Synchronization**: Changes propagate to all dependent files automatically
3. **Semantic Versioning**: Structured version numbering with clear meaning
4. **Git Integration**: Versioning tightly coupled with source control

### The Version Lifecycle

When a version change occurs:

1. The source of truth (`version.properties`) is updated
2. All dependent files are synchronized automatically
3. A git commit is created with descriptive message 
4. A git tag is created for the release
5. Optionally, changes are pushed to the remote repository

## Practical Usage

### Day-to-Day Version Commands

```bash
# View the current version
./s8r version get

# View detailed version information
./s8r version get -v

# Check version history
./s8r version history

# Verify version tag alignment
./s8r version verify
```

### Making Version Changes

```bash
# Increment the patch version (1.2.3 -> 1.2.4)
./s8r version bump patch

# Increment the minor version (1.2.3 -> 1.3.0)
./s8r version bump minor

# Increment the major version (1.2.3 -> 2.0.0)
./s8r version bump major

# Set a specific version
./s8r version set 1.7.0
```

### Release Workflow Examples

#### Standard Patch Release

```bash
# 1. Ensure working directory is clean
git status

# 2. Run tests to verify everything works
./s8r test all

# 3. Bump the patch version, run tests, commit, and tag
./s8r version test patch

# 4. Push changes and tags to remote
git push origin HEAD --tags
```

#### Feature Release (Minor Version)

```bash
# 1. Merge feature branches into main
git checkout main
git merge feature/new-component

# 2. Resolve any conflicts and ensure tests pass
./s8r test all

# 3. Bump the minor version, run tests, commit, and tag
./s8r version test minor

# 4. Push changes and tags to remote
git push origin HEAD --tags
```

#### Breaking Change Release (Major Version)

```bash
# 1. Finalize all breaking changes
git checkout main
git merge breaking/api-redesign

# 2. Update documentation to reflect API changes
./s8r docs

# 3. Bump the major version, run tests, commit, and tag
./s8r version test major

# 4. Push changes and tags to remote
git push origin HEAD --tags
```

### Hotfix Workflow

```bash
# 1. Create a hotfix branch from the release tag
git checkout -b hotfix/critical-fix v1.6.7

# 2. Make the fixes and ensure tests pass
./s8r test all

# 3. Bump the patch version
./s8r version bump patch

# 4. Create a pull request or merge directly
git push origin hotfix/critical-fix

# 5. After approval, tag the hotfix
./s8r version fix-tag
```

## Versioning Strategy Guide

### When to Bump Versions

| Change Type | Version Command | Example | When to Use |
|-------------|-----------------|---------|------------|
| **Patch** | `bump patch` | 1.6.7 → 1.6.8 | Bug fixes, small improvements, documentation updates |
| **Minor** | `bump minor` | 1.6.7 → 1.7.0 | New features that don't break existing APIs |
| **Major** | `bump major` | 1.6.7 → 2.0.0 | Breaking changes, major redesigns |

### Version String Patterns

Samstraumr uses semantic versioning with the format: `MAJOR.MINOR.PATCH`

| Component | Purpose | Example |
|-----------|---------|---------|
| **MAJOR** | Incompatible API changes | 2.0.0 |
| **MINOR** | Backward-compatible new features | 1.7.0 |
| **PATCH** | Backward-compatible bug fixes | 1.6.8 |

## Technical Implementation

### Version Source of Truth

The single source of truth for versions is `/Samstraumr/version.properties`:

```properties
# Samstraumr Project Version
# This file is the single source of truth for project versioning

# Project version
samstraumr.version=1.6.7

# Last updated date
samstraumr.last.updated=April 04, 2025

# Maintainer info
samstraumr.maintainer=Eric C. Mumford (@heymumford)

# License
samstraumr.license=Mozilla Public License 2.0
```

### Synchronized Files

When the version is updated, these files are automatically synchronized:

1. `/Samstraumr/version.properties` - Source of truth
2. `/pom.xml` - Root Maven POM file
3. `/Samstraumr/pom.xml` - Module-level POM file
4. `/Samstraumr/samstraumr-core/pom.xml` - Core module POM file
5. `/README.md` - Version badge and Maven dependency
6. `/CLAUDE.md` - AI context file (if present)

### Verification and Troubleshooting

If you suspect version inconsistencies:

```bash
# Verify version tag alignment
./s8r version verify

# Test the version synchronization
./util/bin/version/test-version-sync.sh

# Fix misaligned tags
./s8r version fix-tag
```

## Version Management Architecture

The version management system consists of these components:

1. **CLI Command**: The `s8r version` command provides the user interface
2. **Version Library**: Core functionality in `util/lib/version-lib.sh`
3. **Command Handlers**: Implementation in `util/bin/version/version-manager.sh`
4. **Testing**: Verification with `util/bin/version/test-version-sync.sh`

The system is designed to be:

- **Reliable**: Backups created during updates
- **Consistent**: All files updated atomically
- **Transparent**: Detailed feedback during operations
- **Traceable**: Git integration for version history

## Advanced Usage

### Custom Version Management

For specialized scenarios, you can use these advanced options:

```bash
# Bump version without committing (for verification)
./s8r version bump patch --no-commit

# Test new version but skip tests
./s8r version test patch --skip-tests

# Bump version, commit, tag, and push (all in one)
./s8r version test patch --push
```

### Script Integration

For build or CI/CD scripts, you can use:

```bash
# Get just the version number for scripts
VERSION=$(./s8r version export)
echo "Building version ${VERSION}"

# Use in conditional logic
if [ "$VERSION" = "1.7.0" ]; then
  echo "This is the release version"
fi
```

## Version Policies and Best Practices

1. **Early and Often**: Bump patch versions frequently for small improvements
2. **Meaningful Jumps**: Minor versions should represent complete features
3. **Rare Major Updates**: Major versions indicate breaking changes and should be rare
4. **Clear Commit Messages**: Version commits should explain the reason for the change
5. **Consistent Tagging**: Always tag releases to create checkpoints for rollback
6. **Test Before Release**: Use the `test` command to ensure version changes are safe

This comprehensive versioning approach ensures that Samstraumr maintainers have clear guidelines and tools for managing the project's evolution reliably over time.