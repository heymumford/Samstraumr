# S8r Examples

This document provides reference examples for using the Samstraumr CLI (`s8r`) for common operations.

## Version Management

### Viewing version information

```bash
# S8r Examples
./s8r version get

# S8r Examples
./s8r version get -v

# S8r Examples
./s8r version export
```

### Modifying versions

```bash
# S8r Examples
./s8r version bump patch

# S8r Examples
./s8r version bump minor

# S8r Examples
./s8r version bump major

# S8r Examples
./s8r version set 1.5.1
```

### Version workflow commands

```bash
# S8r Examples
./s8r version test patch

# S8r Examples
./s8r version test minor --push

# S8r Examples
./s8r version test patch --skip-tests --skip-quality
```

### Version verification

```bash
# S8r Examples
./s8r version verify

# S8r Examples
./s8r version fix-tag

# S8r Examples
./s8r version history
```

## Build Commands

```bash
# S8r Examples
./s8r build

# S8r Examples
./s8r build test

# S8r Examples
./s8r build -c test

# S8r Examples
./s8r build -p atl-tests test
```

## Test Commands

```bash
# S8r Examples
./s8r test unit

# S8r Examples
./s8r test --both unit

# S8r Examples
./s8r test -c tube

# S8r Examples
./s8r test -v adam

# S8r Examples
./s8r test atl
```

## Quality Check Commands

```bash
# S8r Examples
./s8r quality check

# S8r Examples
./s8r quality spotless -f

# S8r Examples
./s8r quality encoding -v
```

## Documentation Generation

```bash
# S8r Examples
./s8r docs

# S8r Examples
./s8r docs ./my-docs

# S8r Examples
./s8r docs ./my-docs docx

# S8r Examples
./s8r docs target/outputs html
```

## Complete Workflow Example

Here's a complete workflow example for making changes, running tests, updating the version, and pushing:

```bash
# S8r Examples

# S8r Examples
./s8r quality check

# S8r Examples
./s8r quality spotless -f

# S8r Examples
./s8r test unit

# S8r Examples
./s8r version test minor --push

# S8r Examples
# S8r Examples
# S8r Examples
# S8r Examples
# S8r Examples
# S8r Examples
# S8r Examples
```

## Output Examples

### Version get command

```
[0;34m[1mCurrent Version[0m
[0;34m[1m================[0m
[0;34m→ Version: 1.5.1[0m
[0;34m→ Last updated: April 04, 2025[0m
```

### Version bump command

```
[0;34m[1mBumping patch Version[0m
[0;34m[1m=====================[0m
[0;34m→ Current version: 1.5.1[0m
[0;34m→ New version: 1.5.2[0m
[0;36m[1mUpdating Version to 1.5.2[0m
[0;36m[1m-------------------------[0m
[0;34m→ Current version: 1.5.1[0m
[0;34m→ New version:     1.5.2[0m
[0;34m→ Backup created:  /path/to/version.properties.bak[0m
[0;32m✓ Updated version.properties[0m
[0;32m✓ Updated last updated date to April 04, 2025[0m
[0;34m→ Updating version in POM files:[0m
[0;34m→   Found version 1.5.1 in /path/to/pom.xml[0m
[0;34m→   Updated /path/to/pom.xml from 1.5.1 to 1.5.2[0m
[0;32m✓ Updated POM files to version 1.5.2[0m
[0;32m✓ Updated version and badge in README.md[0m
[0;32m✓ Version update complete[0m
```

### Version test command

```
[0;34m[1mBumping patch Version[0m
[0;34m[1m=====================[0m
[0;34m→ Current version: 1.5.1[0m
[0;34m→ New version: 1.5.2[0m
[0;36m[1mUpdating Version to 1.5.2[0m
[0;36m[1m-------------------------[0m
[0;34m→ Current version: 1.5.1[0m
[0;34m→ New version:     1.5.2[0m
[0;34m→ Backup created:  /path/to/version.properties.bak[0m
[0;32m✓ Updated version.properties[0m
[0;32m✓ Updated last updated date to April 04, 2025[0m
[0;34m→ Updating version in POM files:[0m
[0;34m→   Found version 1.5.1 in /path/to/pom.xml[0m
[0;34m→   Updated /path/to/pom.xml from 1.5.1 to 1.5.2[0m
[0;32m✓ Updated POM files to version 1.5.2[0m
[0;32m✓ Updated version and badge in README.md[0m
[0;32m✓ Version update complete[0m
[0;34m→ Running tests...[0m
[0;32m✓ All tests passed[0m
[0;34m→ Committing version changes...[0m
[0;32m✓ Changes committed with message: "chore: bump version to 1.5.2"[0m
[0;34m→ Creating version tag...[0m
[0;32m✓ Created tag v1.5.2[0m
[0;34m→ Pushing changes to remote...[0m
[0;32m✓ Changes pushed to remote[0m
[0;32m✓ Version test process complete[0m
```
