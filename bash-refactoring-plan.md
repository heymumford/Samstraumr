# Bash Script Reorganization and Refactoring Plan

## Current Issues

1. Too many scripts scattered throughout the codebase
2. Inconsistent organization and naming conventions
3. Missing or incomplete documentation
4. Non-functional programming style
5. Hardcoded paths and values
6. Duplicate functionality

## Reorganization Plan

### New Directory Structure

```
/util
├── bin         # Consolidated executable scripts
│   ├── build/  # Build-related scripts
│   ├── test/   # Testing-related scripts
│   ├── quality/# Quality check scripts
│   ├── version/# Version management scripts
│   └── utils/  # Utility scripts
└── lib         # Shared bash libraries and functions
    ├── common.sh      # Common utility functions
    ├── build-lib.sh   # Build-related functions
    ├── test-lib.sh    # Test-related functions
    ├── quality-lib.sh # Quality-related functions
    ├── version-lib.sh # Version-related functions
    └── ui-lib.sh      # UI/output formatting functions
```

### Script Template

Each script will follow this template:

```bash
#!/bin/bash
#==============================================================================
# Filename: script-name.sh
# Description: Clear description of the script's purpose
# Author: Original author (and Claude)
# Created: YYYY-MM-DD
# Updated: YYYY-MM-DD
#==============================================================================
# Usage: ./script-name.sh [options] <args>
#
# Options:
#   -h, --help    Display this help message
#   -v, --verbose Enable verbose output
#
# Examples:
#   ./script-name.sh arg1
#   ./script-name.sh --option value
#==============================================================================

# Determine script directory and load library
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"

# Source shared libraries
source "${PROJECT_ROOT}/util/lib/common.sh"

#------------------------------------------------------------------------------
# Functions
#------------------------------------------------------------------------------

function do_something() {
    local param1="$1"
    # Function body
}

function main() {
    # Parse arguments
    parse_arguments "$@"
    
    # Main logic as a series of function calls
    do_something
    another_function
}

#------------------------------------------------------------------------------
# Main
#------------------------------------------------------------------------------
main "$@"
```

## Implementation Plan

1. Create the new directory structure
2. Create shared library files
3. Identify categories for scripts based on functionality
4. Refactor scripts one by one, following functional programming principles
5. Update paths in scripts to use the configuration file
6. Test each script after refactoring
7. Update documentation and help messages
8. Create symlinks in the old locations to ensure backward compatibility

## Script Categories

1. **Build Scripts**: scripts related to building the project
2. **Test Scripts**: scripts related to running tests
3. **Quality Scripts**: scripts related to code quality checks
4. **Version Scripts**: scripts related to version management
5. **Utility Scripts**: miscellaneous utility scripts

## Testing Process

1. Run each script with `-h` or `--help` to verify help message
2. Run each script with valid arguments to verify functionality
3. Run each script with invalid arguments to verify error handling
4. Verify that no hardcoded paths are used
5. Verify that the script follows functional programming principles
