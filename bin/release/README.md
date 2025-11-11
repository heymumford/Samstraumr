# Samstraumr Release Tools

This directory contains tools for preparing, verifying, and publishing Samstraumr releases.

## Key Requirements

### Scientific Accessibility

S8r is designed to be used by scientists with varying levels of computer science knowledge. All documentation (especially documentation around modifying S8r for new and innovative uses) must be accessible to:

1. Non-technical scientists and engineers new to this complex module model
2. Domain experts with deep knowledge in their field but limited programming experience
3. Scientists with very little computer science knowledge

The release process includes verification of documentation against scientific accessibility standards. See [scientific-documentation-standards.md](../../docs/reference/standards/scientific-documentation-standards.md) for details.

## Tools

### prepare-release

Comprehensive release preparation script that:
- Verifies all requirements are met before tagging a new release
- Checks version consistency across files
- Validates test coverage meets thresholds
- Ensures documentation is up to date
- Verifies build succeeds without warnings
- Creates release notes and changelog entries if missing

```bash
# Basic usage
./prepare-release 2.5.0

# Fix issues automatically
./prepare-release --fix 2.5.0

# Dry run (no changes made)
./prepare-release --dry-run 2.5.0
```

### publish-release

Release publishing script that:
- Tags and pushes a new release
- Deploys Maven artifacts
- Creates GitHub releases
- Updates documentation sites

```bash
# Basic usage
./publish-release 2.5.0

# Dry run (no changes made)
./publish-release --dry-run 2.5.0

# With GitHub token for release creation
./publish-release --token <github-token> 2.5.0
```

## Release Process

1. **Prepare the Release**:
   ```bash
   # Verify readiness
   ./prepare-release 2.5.0
   
   # Fix any issues
   ./prepare-release --fix 2.5.0
   ```

2. **Publish the Release**:
   ```bash
   # Deploy artifacts and create GitHub release
   ./publish-release 2.5.0
   ```

3. **Verify Published Release**:
   - Check Maven repositories
   - Verify GitHub release page
   - Ensure documentation site is updated

## Configuration

These tools respect the following environment variables:
- `GITHUB_TOKEN` / `GH_TOKEN`: GitHub API token for releases
- `PROJECT_ROOT`: Root directory of the project

## Related Documentation

See the [RELEASE-CHECKLIST.md](./RELEASE-CHECKLIST.md) file for a comprehensive release checklist.