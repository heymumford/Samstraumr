# Samstraumr Executables Directory

This directory contains all executable scripts for the Samstraumr project, organized into categories for better maintainability and discovery.

## Setup Instructions

To properly use these executables, you have three options:

### Option 1: Add this directory to your PATH

```bash
# Run the setup script
./bin/setup-path.sh

# This will add the bin directory to your shell configuration
# and provide instructions for activating it in your current session
```

### Option 2: Create shell aliases (recommended)

```bash
# Run the aliases script
./bin/create-aliases.sh

# This will create aliases for all scripts in your shell configuration
# and provide instructions for activating them in your current session
```

### Option 3: Use the scripts directly with full paths

```bash
# You can always use the full path to run a script
/path/to/Samstraumr/bin/core/s8r
/path/to/Samstraumr/bin/build/s8r-build
```

## Directory Structure

Scripts are organized by functionality:

- `core/` - Core S8r framework executables (s8r, s8r-init, s8r-list)
- `build/` - Build-related executables (s8r-build, s8r-ci)
- `test/` - Testing executables (s8r-test, s8r-test-cli)
- `component/` - Component-related executables (s8r-component, s8r-composite, s8r-machine, s8r-tube)
- `dev/` - Development executables (s8r-dev)
- `migration/` - Migration-related executables (s8r-migration-monitor, s8r-architecture-verify)
- `help/` - Help documentation executables (s8r-help)
- `utils/` - Utility executables (s8r-docs, s8r-quality, s8r-version)
- `ai/` - AI-related executables (s8r-ai-test)
- `s8r-all/` - Backup copies of all scripts

## Management Scripts

This directory also contains management scripts:

- `setup-path.sh` - Adds the bin directory to your PATH
- `create-aliases.sh` - Creates shell aliases for all scripts
- `remove-root-scripts.sh` - Safely removes scripts from the root directory
- `setup-bin-directory.sh` - Sets up the entire bin directory structure
- `s8r-scripts-version` - Manages script versions

## Script Versions

Some scripts have both original and new versions. To manage these versions, use:

```bash
# List available script versions
s8r-scripts-version list

# Switch to new versions of scripts
s8r-scripts-version use-new

# Switch back to original versions
s8r-scripts-version use-old
```

## Implementation Notes

These are the canonical executables for the project. They are actual files (not symlinks) that should be used for all operations.

The scripts previously in the root directory are deprecated and have been removed. If you need to restore them, you can find backup copies in the `s8r-all` directory, but it's strongly recommended to use the bin-directory executables instead.

When updating scripts, please update them directly in their appropriate subdirectory. The `setup-bin-directory.sh` script will automatically create backups in the `s8r-all` directory.
