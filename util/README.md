# Samstraumr Utility Scripts

This directory contains utility scripts for the Samstraumr project. We have reorganized the scripts with a flattened directory structure and consistent naming conventions.

## Directory Structure

### Root Level Scripts (Flattened Structure)

#### Version Management
- `version.sh` - Unified version management utility

#### Build Scripts
- `build-optimal.sh` - Optimized build script with thread and memory settings
- `build-performance.sh` - Performance-focused build script
- `build-report.sh` - Generates a comprehensive build report

#### Testing Scripts
- `test-run.sh` - Comprehensive test runner for all test types
- `test-run-atl.sh` - Runner for Above The Line tests
- `test-run-all.sh` - Runner supporting multiple test terminologies
- `test-map-type.sh` - Maps between industry-standard and Samstraumr terminology

#### Setup and Environment Scripts
- `setup-java-env.sh` - Sets up Java environment variables
- `setup-java17-compat.sh` - Java 17 compatibility script
- `update-version-test.sh` - Updates version, runs tests, and creates git tag

### Utility Scripts

#### `/scripts/` directory
Contains helper scripts with consistent naming conventions:

##### Quality Scripts
- `check-build-quality.sh` - Runs all quality checks
- `check-encoding.sh` - Verifies file encoding and line endings
- `fix-pmd.sh` - Fixes PMD-related issues
- `build-skip-quality.sh` - Runs build with quality checks skipped
- `setup-quality-tools.sh` - Enables quality tools in the build

##### Maintenance Scripts
- `clean-maven.sh` - Cleans Maven repository cache
- `fix-line-endings.sh` - Corrects line endings in text files
- `setup-fast-build.sh` - Creates a fast Maven profile for development

##### Header Management
- `update-java-headers.sh` - Standardizes Java file headers
- `update-md-headers.sh` - Standardizes Markdown file headers

##### Other Utilities
- `generate-badges.sh` - Generates status badges
- `fix-logger.sh` - Fixes logger implementation issues

## Usage

Scripts are now available directly from the util/ directory with consistent naming:

```bash
# From project root
./util/version.sh bump patch                # Use the version utility 
./util/test-run.sh tube                     # Run specific tests
./util/test-run-atl.sh                      # Run Above The Line tests
./util/test-run-all.sh --both unit          # Run unit and tube tests
./util/build-optimal.sh                     # Use optimized build
./util/build-performance.sh                 # Run performance-optimized build
./util/build-report.sh                      # Generate a build report
./util/update-version-test.sh patch         # Update version, test, and tag

# Helper scripts in scripts/ directory
./util/scripts/check-build-quality.sh       # Run all quality checks
./util/scripts/check-encoding.sh            # Check file encodings
./util/scripts/fix-line-endings.sh          # Fix line endings
```

For Windows users, you can run the .bat files directly:

```cmd
util\build-performance.bat
```

### ⚠️ IMPORTANT

- Scripts should be run from the project root, using the paths shown above
- All scripts follow consistent naming with kebab-case format
- Scripts use function-based prefixes (e.g., `build-*`, `test-*`, `check-*`) for better organization
- Legacy nested paths are being phased out in favor of the flattened structure

## Version Management

The version utility provides a unified interface for managing project versions:

```bash
# Display current version information
./util/version.sh get

# Bump the patch version (0.5.8 → 0.5.9)
./util/version.sh bump patch

# Bump the minor version (0.5.8 → 0.6.0)
./util/version.sh bump minor

# Bump the major version (0.5.8 → 1.0.0)
./util/version.sh bump major

# Set a specific version
./util/version.sh set 0.7.0

# View version history
./util/version.sh history

# Update version without committing changes
./util/version.sh bump patch --no-commit

# Create git tag for the current version
./util/version.sh tag
```

The utility will automatically:
- Update all version numbers in properties files and POM files
- Update version references in documentation
- Update the "last updated" date
- Create a Git commit with appropriate message (unless `--no-commit` is specified)
- Optionally create a git tag for the version

For more help, run `./util/version.sh --help`

## Script Organization Guidelines

When adding new utility scripts:

1. Place the script directly in the `util/` directory if it's a primary function
2. Place helper scripts in the `util/scripts/` directory
3. Use kebab-case for all script names (e.g., `build-optimal.sh`, not `buildOptimal.sh`)
4. Start script names with their function category (e.g., `build-`, `test-`, `check-`)
5. Make sure it's executable (`chmod +x script.sh`)
6. Update this README.md with the script's purpose
7. Use `SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"` to ensure it works from any location