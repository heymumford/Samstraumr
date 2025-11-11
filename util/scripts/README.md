# Samstraumr Utility Scripts

This directory contains utility scripts for the Samstraumr project, organized by functionality.

## Directory Structure

- `archived/` - Old scripts that are kept for reference but no longer actively used
- `ci/` - Continuous Integration scripts
- `docs/` - Documentation generation and validation scripts
- `java/` - Java-related utility scripts
- `test/` - Testing utility scripts

## Main Scripts

- `cleanup-root.sh` - Script for cleaning up the root directory
- `organize-s8r-scripts.sh` - Script for organizing s8r scripts into the bin directory
- `create-script-symlinks.sh` - Script for creating symbolic links to scripts
- `update-standardized-headers.sh` - Script for updating file headers
- `initialize.sh` - Repository initialization script

## Usage

### Cleanup Root Directory

To clean up the root directory by removing duplicate files and organizing symlinks:

```bash
cd /path/to/Samstraumr
./util/scripts/cleanup-root.sh
```

### Organize S8r Scripts

To organize s8r scripts into categorized directories within the bin directory:

```bash
cd /path/to/Samstraumr
./util/scripts/organize-s8r-scripts.sh
```

### Create Script Symlinks

To create symbolic links for scripts:

```bash
cd /path/to/Samstraumr
./util/scripts/create-script-symlinks.sh
```

## Script Organization

The scripts in this repository are organized in several layers:

1. **Root Directory** - Core scripts that are frequently used are kept in the root directory
2. **bin Directory** - Organized structure of all scripts categorized by function
3. **util/scripts Directory** - Source location for utility scripts

## Maintenance

When adding new scripts:

1. Place the script in the appropriate subdirectory based on its function
2. Create a symbolic link in the root directory if it's frequently used
3. Add the script to the appropriate category in the bin directory using `organize-s8r-scripts.sh`
4. Update this README.md if the script introduces new functionality

## Script Naming Conventions

- Use kebab-case for script names (e.g., `create-script-symlinks.sh`)
- Prefix s8r scripts with `s8r-` (e.g., `s8r-build`, `s8r-test`)
- Use descriptive names that indicate the script's purpose
- Add the `.sh` extension to all shell scripts

## Best Practices

- Keep scripts focused on a single responsibility
- Document script usage within the script itself using comments
- Provide meaningful error messages and exit codes
- Handle errors gracefully
- Use color-coded output for better readability
- Validate input parameters and provide usage instructions