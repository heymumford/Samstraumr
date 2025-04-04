# Samstraumr Version Management Tutorial

This tutorial provides practical steps and examples for managing versions in the Samstraumr project.

## Quick Reference

| Task | Command | Description |
|------|---------|-------------|
| View current version | `./s8r version get` | Displays the current version |
| Bump patch version | `./s8r version bump patch` | Increments the patch number (z in x.y.z) |
| Bump minor version | `./s8r version bump minor` | Increments the minor number (y in x.y.z) |
| Bump major version | `./s8r version bump major` | Increments the major number (x in x.y.z) |
| Set specific version | `./s8r version set 1.7.0` | Sets version to a specific value |
| Check version history | `./s8r version history` | Shows a log of version changes |
| Verify version/tag alignment | `./s8r version verify` | Checks if git tag matches version |
| Create missing version tag | `./s8r version fix-tag` | Creates git tag if missing |
| Test, bump and tag | `./s8r version test patch` | Runs tests, bumps version, commits and tags |

## Tutorial Examples

### Example 1: Common Development Workflow

Make changes, test, and update the patch version:

```bash
# 1. Make your code changes
vim Samstraumr/samstraumr-core/src/main/java/org/s8r/component/core/Component.java

# 2. Run tests to verify changes work
./s8r test unit

# 3. Bump the patch version (automatically updates all files)
./s8r version bump patch

# 4. Push your changes and tags
git push origin main --tags
```

### Example 2: Planning for a Release

```bash
# 1. Checkout the main branch
git checkout main
git pull

# 2. Review current version
./s8r version get -v

# 3. Check test status
./s8r test all

# 4. Bump minor version for new feature release
./s8r version bump minor

# 5. Create release notes and update documentation
vim CHANGELOG.md
./s8r docs

# 6. Push release
git push origin main --tags
```

### Example 3: Working with Version-Specific Branches

```bash
# 1. Create a version-specific branch
git checkout -b release/v1.7.x

# 2. Make release-specific changes
./s8r build

# 3. Update version with a specific value
./s8r version set 1.7.0-RC1

# 4. Push release candidate
git push origin release/v1.7.x

# After testing and verification:

# 5. Set final release version
./s8r version set 1.7.0

# 6. Create a tag for the release
git tag -a v1.7.0 -m "Version 1.7.0 release"
git push origin v1.7.0
```

### Example 4: Creating a Hotfix

```bash
# 1. Checkout the release tag
git checkout v1.6.7

# 2. Create a hotfix branch
git checkout -b hotfix/issue-123

# 3. Make the bugfix
vim Samstraumr/samstraumr-core/src/main/java/org/s8r/core/tube/impl/Component.java

# 4. Test the fix
./s8r test unit

# 5. Bump the patch version
./s8r version bump patch
# This should change version to 1.6.8

# 6. Push the hotfix branch
git push origin hotfix/issue-123

# 7. Create a pull request to merge into main
# After the PR is merged:

# 8. Tag the hotfix on main
git checkout main
git pull
./s8r version fix-tag  # Ensures tag exists for the new version
git push origin --tags
```

## Understanding the Output

When you run version commands, you'll see detailed output like this:

```
Updating Version to 1.7.0
-------------------------
→ Current version: 1.6.7
→ New version:     1.7.0
→ Backup created:  /home/emumford/NativeLinuxProjects/Samstraumr/Samstraumr/version.properties.bak
✓ Updated version.properties
✓ Updated last updated date to April 04, 2025
→ Updating version in POM files:
→   Processing /home/emumford/NativeLinuxProjects/Samstraumr/pom.xml
→   Updated POM file: /home/emumford/NativeLinuxProjects/Samstraumr/pom.xml
→   Processing /home/emumford/NativeLinuxProjects/Samstraumr/Samstraumr/pom.xml
→   Updated POM file: /home/emumford/NativeLinuxProjects/Samstraumr/Samstraumr/pom.xml
→   Processing /home/emumford/NativeLinuxProjects/Samstraumr/Samstraumr/samstraumr-core/pom.xml
→   Updated POM file: /home/emumford/NativeLinuxProjects/Samstraumr/Samstraumr/samstraumr-core/pom.xml
✓ Successfully updated all POM files to version 1.7.0
✓ Updated version references in README.md
```

This shows:
- The version being changed from and to
- Backup files being created
- Individual files being processed
- Success messages for each step

## How Version Management Works Behind the Scenes

When you run a version command:

1. The current version is read from `Samstraumr/version.properties`
2. The new version is calculated or set as specified
3. Backups are created of all files that will be changed
4. The version is updated in `version.properties` (source of truth)
5. The version is synchronized across all POM files and the README
6. If requested, git commits and tags are created
7. Detailed output shows each step of the process

The system ensures all files stay in sync, eliminating inconsistencies between the declared version and what appears in various files throughout the project.

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Version files out of sync | Run `./s8r version verify` followed by appropriate fix |
| Missing git tag | Run `./s8r version fix-tag` |
| Cannot change version | Check git permissions and file write permissions |
| Need to revert a version | Use `git checkout` to previous tag |

For additional help, see the full [Version Management Reference Guide](../reference/version-management-guide.md) or run `./s8r version --help`.