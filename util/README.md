# Samstraumr Utility Scripts

This directory contains utility scripts for the Samstraumr project, organized into the following categories:

## Directory Structure

- Root Level
  - `version` - **NEW**: Unified version management utility
  - `test/` - Testing utilities
    - `run-tests.sh` - Comprehensive test runner for all test types
    - `run-atl-tests.sh` - Runner for Above The Line tests
    - `run-all-tests.sh` - Runner supporting multiple test terminologies
    - `mapping/` - Test mapping utilities
      - `map-test-type.sh` - Maps between industry-standard and Samstraumr terminology

- `build/` - Scripts for building the project with different configurations
  - `build-optimal.sh` - Optimized build script with thread and memory settings
  - `build-performance.sh` - Performance-focused build script
  - `build-performance.bat` - Windows version of performance build script
  - `java17-compat.sh` - Java 17 compatibility script

- `quality/` - Scripts for quality checks and fixes
  - `build-checks.sh` - Runs all quality checks
  - `check-encoding.sh` - Verifies file encoding and line endings
  - `fix-pmd.sh` - Fixes PMD-related issues
  - `skip-quality-build.sh` - Runs build with quality checks skipped

- `maintenance/` - Scripts for project maintenance
  - `update-version.sh` - *(Deprecated)* Use the new `version` utility instead
  - `cleanup-maven.sh` - Cleans Maven repository cache
  - `setup-fast.sh` - Creates a fast Maven profile for development
  - `headers/` - Header standardization utilities
    - `update-java-headers.sh` - Standardizes Java file headers
    - `update-md-headers.sh` - Standardizes Markdown file headers

## Usage

Scripts must be run from their locations in the util/ directory. For example:

```bash
# From project root
./util/version bump patch                 # Use the new version utility 
./util/test/run-tests.sh tube             # Run specific tests
./util/test/run-atl-tests.sh              # Run Above The Line tests
./util/test/run-all-tests.sh --both unit  # Run unit and tube tests
./util/build/build-optimal.sh             # Use optimized build
./util/maintenance/headers/update-md-headers.sh # Standardize Markdown headers

# If you frequently run these scripts, consider adding the directories to your PATH
export PATH=$PATH:$(pwd)/util:$(pwd)/util/test:$(pwd)/util/build:$(pwd)/util/quality:$(pwd)/util/maintenance
version bump patch
run-atl-tests.sh
build-optimal.sh
```

For Windows users, you can run the .bat files directly:

```cmd
util\build\build-performance.bat
```

### ⚠️ IMPORTANT

Scripts are no longer available in the project root. All scripts must be run from their 
specific locations in the util directory structure. Running `./build-optimal.sh` 
from the project root will fail.

## Version Management

The new `version` utility provides a unified interface for managing project versions:

```bash
# Display current version information
./util/version get

# Bump the patch version (0.5.8 → 0.5.9)
./util/version bump patch

# Bump the minor version (0.5.8 → 0.6.0)
./util/version bump minor

# Bump the major version (0.5.8 → 1.0.0)
./util/version bump major

# Set a specific version
./util/version set 0.7.0

# View version history
./util/version history

# Update version without committing changes
./util/version bump patch --no-commit
```

The utility will automatically:
- Update all version numbers in properties files and POM files
- Update version references in documentation
- Update the "last updated" date
- Create a Git commit with appropriate message (unless `--no-commit` is specified)

For more help, run `./util/version --help`

## Adding New Scripts

When adding new utility scripts:

1. Place the script in the appropriate subdirectory
2. Make sure it's executable (`chmod +x script.sh`)
3. Update this README.md with the script's purpose
4. Use relative paths in the script to ensure it works regardless of where it's called from