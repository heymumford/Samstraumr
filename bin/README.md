# Samstraumr Executables Directory

This directory contains all executable scripts for the Samstraumr project, organized into categories for better maintainability and discovery.

## Setup

To properly use these executables, add this directory to your PATH:

```bash
# Add to your ~/.bashrc or ~/.zshrc:
export PATH="$PATH:/path/to/Samstraumr/bin"

# Then reload your shell:
source ~/.bashrc  # or source ~/.zshrc
```

Alternatively, for temporary usage during development:

```bash
# Temporarily add to PATH for current session:
export PATH="$PATH:$(pwd)/bin"
```

## Directory Structure

- `core/` - Core S8r framework executables (s8r, s8r-init, s8r-list)
- `build/` - Build-related executables (s8r-build, s8r-ci)
- `test/` - Testing executables (s8r-test, s8r-test-cli)
- `component/` - Component-related executables (s8r-component, s8r-composite, s8r-machine, s8r-tube)
- `dev/` - Development executables (s8r-dev)
- `migration/` - Migration-related executables (s8r-migration-monitor, s8r-architecture-verify)
- `help/` - Help documentation executables (s8r-help)
- `utils/` - Utility executables (s8r-docs, s8r-quality, s8r-version)

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

## Implementation

These are the canonical executables for the project. They are not symlinks or copies - they are the primary executables that should be used for all operations.

The scripts previously in the root directory are deprecated and will be removed in future versions. Please update any documentation, CI/CD pipelines, or personal workflows to use these bin-directory executables instead.
