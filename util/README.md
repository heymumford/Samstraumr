# Samstraumr Utility Scripts

This directory contains utility scripts for the Samstraumr project, organized into the following categories:

## Directory Structure

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
  - `update-version.sh` - Updates version numbers throughout the codebase
  - `cleanup-maven.sh` - Cleans Maven repository cache

## Usage

Scripts must be run from their locations in the util/ directory. For example:

```bash
# From project root
./util/build/build-optimal.sh

# If you frequently run these scripts, consider adding the directories to your PATH
export PATH=$PATH:$(pwd)/util/build:$(pwd)/util/quality:$(pwd)/util/maintenance
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

## Adding New Scripts

When adding new utility scripts:

1. Place the script in the appropriate subdirectory
2. Make sure it's executable (`chmod +x script.sh`)
3. Update this README.md with the script's purpose
4. Use relative paths in the script to ensure it works regardless of where it's called from