# Samstraumr Version Management Guide

## Core Strategy

Samstraumr uses a centralized version management approach with:

- **Single Source**: `version.properties` as the canonical location
- **Auto-Sync**: Changes propagate to all files automatically 
- **SemVer**: MAJOR.MINOR.PATCH versioning format
- **Git Integration**: Built-in commit/tag creation

## Quick Command Reference

```bash
# View versions
./s8r version get             # Current version
./s8r version get -v          # Detailed info
./s8r version history         # Version history  
./s8r version verify          # Check tag alignment

# Change versions
./s8r version bump patch      # 1.2.3 → 1.2.4
./s8r version bump minor      # 1.2.3 → 1.3.0
./s8r version bump major      # 1.2.3 → 2.0.0
./s8r version set [VERSION]       # Set specific version
./s8r version test patch      # Test, bump, commit, tag
```

## Workflow Examples

### Patch Release

```bash
git status                   # Ensure clean working directory
./s8r test all               # Verify everything works
./s8r version test patch     # Bump, test, commit, tag  
git push origin HEAD --tags  # Push changes
```

### Feature Release (Minor Version)

```bash
git checkout main
git merge feature/new-component
./s8r test all
./s8r version test minor
git push origin HEAD --tags
```

### Breaking Change (Major Version)

```bash
git checkout main
git merge breaking/api-redesign
./s8r docs                   # Update documentation
./s8r version test major
git push origin HEAD --tags
```

### Hotfix

```bash
git checkout -b hotfix/critical-fix v${previous.version}
# Make fixes...
./s8r test all
./s8r version bump patch
git push origin hotfix/critical-fix
# After approval:
./s8r version fix-tag
```

## When to Bump Versions

| Change Type | Command | Example | Use Case |
|-------------|---------|---------|----------|
| **Patch** | `bump patch` | ${previous.version}→${previous.version} | Bug fixes, small improvements |
| **Minor** | `bump minor` | ${previous.version}→${samstraumr.version} | New features (backward compatible) |
| **Major** | `bump major` | ${previous.version}→2.0.0 | Breaking changes |

## Technical Details

### Files Synchronized

When a version changes, these files update automatically:

1. `/Samstraumr/version.properties` - Source of truth
2. `/pom.xml` - Root POM
3. `/Samstraumr/pom.xml` - Module POM  
4. `/Samstraumr/samstraumr-core/pom.xml` - Core POM
5. `/README.md` - Version badge and Maven example
6. `/CLAUDE.md` - AI context (if present)

### Troubleshooting

```bash
./s8r version verify             # Check version/tag alignment
./util/bin/version/test-version-sync.sh  # Test synchronization
./s8r version fix-tag            # Fix misaligned tags
```

### Advanced Options

```bash
# Skip committing
./s8r version bump patch --no-commit

# Skip tests 
./s8r version test patch --skip-tests

# All-in-one
./s8r version test patch --push

# Script integration
VERSION=$(./s8r version export)
```

## Best Practices

1. Use **patch** for bug fixes and minor improvements
2. Use **minor** for complete new features
3. Reserve **major** for breaking changes
4. Always run tests before version changes
5. Use descriptive commit messages
6. Tag all releases for clear versioning history