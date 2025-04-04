# Samstraumr CLI Examples

This document provides reference examples for using the Samstraumr CLI (`s8r`) for common operations.

## Version Management

### Viewing Version Information

```bash
# Show current version info
./s8r version get

# Show detailed version info with tag status
./s8r version get -v

# Output only the current version (for scripts)
./s8r version export
```

### Modifying Versions

```bash
# Bump patch version (bug fixes)
./s8r version bump patch

# Bump minor version (new features)
./s8r version bump minor

# Bump major version (breaking changes)
./s8r version bump major

# Set to a specific version
./s8r version set 1.5.1
```

### Version Workflow Commands

```bash
# Bump patch version, run tests, commit and tag
./s8r version test patch

# Bump minor version, run tests, commit and tag, and push to remote
./s8r version test minor --push

# Bump patch version with commit but skip tests and quality checks
./s8r version test patch --skip-tests --skip-quality
```

### Version Verification

```bash
# Verify that version and tag are in sync
./s8r version verify

# Create a git tag matching the current version
./s8r version fix-tag

# Show version history from git tags
./s8r version history
```

## Build Commands

```bash
# Fast build (default)
./s8r build

# Run tests
./s8r build test

# Clean and run tests
./s8r build -c test

# Run tests with ATL profile
./s8r build -p atl-tests test
```

## Test Commands

```bash
# Run unit tests
./s8r test unit

# Run unit and tube tests
./s8r test --both unit

# Clean and run tube tests
./s8r test -c tube

# Run Adam tube tests with verbose output
./s8r test -v adam

# Run critical tests
./s8r test atl
```

## Quality Check Commands

```bash
# Run all quality checks
./s8r quality check

# Run Spotless and fix issues
./s8r quality spotless -f

# Check encodings with verbose output
./s8r quality encoding -v
```

## Documentation Generation

```bash
# Generate PDF docs in target/docs
./s8r docs

# Generate PDF docs in custom directory
./s8r docs ./my-docs

# Generate DOCX docs in custom directory
./s8r docs ./my-docs docx

# Generate HTML docs in target/outputs
./s8r docs target/outputs html
```

## Complete Workflow Example

Here's a complete workflow example for making changes, running tests, updating the version, and pushing:

```bash
# 1. Make code changes

# 2. Run quality checks
./s8r quality check

# 3. Fix any quality issues
./s8r quality spotless -f

# 4. Run tests
./s8r test unit

# 5. Bump version, run tests again, commit, tag, and push
./s8r version test minor --push

# Alternatively, do these steps separately:
# ./s8r version bump minor  # Bump version
# ./s8r test all            # Run all tests
# git add .                 # Add changes
# git commit -m "..."       # Commit changes
# ./s8r version fix-tag     # Create version tag
# git push && git push --tags  # Push changes
```

## Output Examples

### Version Get Command

```
[0;34m[1mCurrent Version[0m
[0;34m[1m================[0m
[0;34m→ Version: 1.5.1[0m
[0;34m→ Last updated: April 04, 2025[0m
```

### Version Bump Command

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

### Version Test Command

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