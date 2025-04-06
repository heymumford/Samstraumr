<\!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Samstraumr Version Management Tutorial

## Command Summary

| Task | Command |
|------|---------|
| View version | `./s8r version get` |
| Bump patch | `./s8r version bump patch` |
| Bump minor | `./s8r version bump minor` |
| Bump major | `./s8r version bump major` |
| Set version | `./s8r version set [VERSION]` |
| View history | `./s8r version history` |
| Verify tags | `./s8r version verify` |
| Fix tags | `./s8r version fix-tag` |
| Test & bump | `./s8r version test patch` |

## Common Workflows

### Standard Development

```bash
# Fix bug in code
vim Samstraumr/samstraumr-core/src/main/java/org/s8r/component/core/Component.java

# Test your fix
./s8r test unit

# Bump patch version (auto-updates all files)
./s8r version bump patch

# Push changes
git push origin main --tags
```

### Feature Release

```bash
# Get latest changes
git checkout main
git pull

# Run tests
./s8r test all

# Bump minor version
./s8r version bump minor

# Update documentation
vim CHANGELOG.md

# Push release
git push origin main --tags
```

### Release Candidate

```bash
# Create version branch
git checkout -b release/v1.7.x

# Set RC version
./s8r version set [VERSION]-RC1
git push origin release/v1.7.x

# After testing, set final version
./s8r version set [VERSION]
git push origin v${samstraumr.version}
```

### Hotfix

```bash
# Create hotfix branch from tag
git checkout v${previous.version}
git checkout -b hotfix/issue-123

# Fix code and test
vim Samstraumr/samstraumr-core/src/main/java/org/s8r/core/tube/impl/Component.java
./s8r test unit

# Bump patch (${previous.version} → ${previous.version})
./s8r version bump patch

# Submit PR and after merge:
git checkout main
git pull
./s8r version fix-tag
git push origin --tags
```

## How It Works

The version system:
1. Reads from `Samstraumr/version.properties` (source of truth)
2. Creates backups of files to be changed
3. Updates version in all synchronized files:
   - version.properties
   - All POM files
   - README.md (badge and examples)
   - CLAUDE.md (if present)
4. Creates git commits and tags if requested

## Troubleshooting

| Issue | Fix |
|-------|-----|
| Inconsistent files | `./s8r version verify` |
| Missing tag | `./s8r version fix-tag` |
| Permission issues | Check file permissions |
| Revert to previous | `git checkout v1.x.y` |

See [Version Management Guide](../reference/version-management-guide.md) for details.
