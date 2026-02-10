<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


<\!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Version Management Tutorial

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

### Standard development

```bash
# Version Management Tutorial
vim Samstraumr/samstraumr-core/src/main/java/org/s8r/component/core/Component.java

# Version Management Tutorial
./s8r test unit

# Version Management Tutorial
./s8r version bump patch

# Version Management Tutorial
git push origin main --tags
```

### Feature release

```bash
# Version Management Tutorial
git checkout main
git pull

# Version Management Tutorial
./s8r test all

# Version Management Tutorial
./s8r version bump minor

# Version Management Tutorial
vim CHANGELOG.md

# Version Management Tutorial
git push origin main --tags
```

### Release candidate

```bash
# Version Management Tutorial
git checkout -b release/v1.7.x

# Version Management Tutorial
./s8r version set [VERSION]-RC1
git push origin release/v1.7.x

# Version Management Tutorial
./s8r version set [VERSION]
git push origin v${samstraumr.version}
```

### Hotfix

```bash
# Version Management Tutorial
git checkout v${previous.version}
git checkout -b hotfix/issue-123

# Version Management Tutorial
vim Samstraumr/samstraumr-core/src/main/java/org/s8r/core/tube/impl/Component.java
./s8r test unit

# Version Management Tutorial
./s8r version bump patch

# Version Management Tutorial
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
