<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Script Simplification and Reorganization Plan

## Overview

This document consolidates our plan for reorganizing and simplifying bash scripts in the Samstraumr project. It includes both the structural reorganization and functional simplification aspects.

## Current Issues

### Organization Issues

1. Too many scripts scattered throughout the codebase
2. Inconsistent organization and naming conventions
3. Missing or incomplete documentation
4. Non-functional programming style
5. Hardcoded paths and values
6. Duplicate functionality

### Implementation Issues

1. **Excessive Duplication**:
   - Significant code duplication between `run-tests.sh` and `run-atl-tests.sh`
   - Duplicated argument parsing logic
   - Duplicated test execution functions
   - Duplicated output handling code
2. **Scattered Color Definitions**:
   - Color codes defined independently in multiple files
   - Inconsistent naming (`COLOR_RED` vs `STATUS_RED`)
   - Multiple implementations of similar formatting utilities
3. **Inconsistent Command-Line Parsing**:
   - Different patterns used across scripts
   - Some use `case` statements, others use custom logic
   - Common argument parsing functions in `common.sh` aren't consistently used
4. **Test Type Mapping Redundancy**:
   - Test type mapping implemented in:
     - `s8r` (lines 410-479)
     - `test-lib.sh` (lines 17-98)
     - `run-tests.sh` (lines 102-240)
5. **Duplicated Maven Command Building**:
   - Maven command construction duplicated in multiple files
   - Similar logic with slight variations
   - No centralized utility for command generation

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

## Script Categories

1. **Build Scripts**: scripts related to building the project
2. **Test Scripts**: scripts related to running tests
3. **Quality Scripts**: scripts related to code quality checks
4. **Version Scripts**: scripts related to version management
5. **Utility Scripts**: miscellaneous utility scripts

## Simplification Plan

### Phase 1: Enhance Common Libraries

1. **Centralize Color and Formatting Logic**:
   - Use only `common.sh` for color definitions
   - Create standardized print functions for consistent output
   - Remove duplicate color definitions from other scripts
2. **Add Standardized Argument Parsing**:
   - Create a generalized argument parsing function in `common.sh`
   - Support common flag patterns (-v/--verbose, etc.)
   - Use associative arrays for configuration
3. **Create Maven Command Builder**:
   - Implement a unified function for Maven command construction
   - Support profiles, goals, and other common parameters
   - Handle clean, skip tests, and other flags consistently
4. **Implement Script Initialization Helper**:
   - Create a standard initialization function for all scripts
   - Handle path determination and config loading
   - Set up error handling and default options

### Phase 2: Consolidate Test Scripts

1. **Merge Test Execution Scripts**:
   - Combine `run-tests.sh` and `run-atl-tests.sh` into a single script
   - Use a parameterized approach for different test types
   - Centralize test reporting and result handling
2. **Centralize Test Type Mapping**:
   - Move all test type mapping logic to `test-lib.sh`
   - Create utility functions for test type conversion
   - Ensure consistent handling of test types across the codebase
3. **Simplify Test Output and Reporting**:
   - Create a unified test reporting function in `test-lib.sh`
   - Standardize test result formatting and display
   - Improve verbose mode output for better debugging

### Phase 3: Update Main CLI (s8r)

1. **Refactor Command Handling**:
   - Use the command pattern for cleaner organization
   - Leverage the enhanced libraries for consistent behavior
   - Reduce the main script size by modularizing functionality
2. **Improve Help Documentation**:
   - Move help text to separate files
   - Implement dynamic help generation for commands
   - Ensure consistent help format across all commands

### Phase 4: Implement Modern Shell Features

1. **Use Associative Arrays** for configuration and argument storage
2. **Leverage Parameter Expansion** for cleaner variable defaults
3. **Implement Process Substitution** for more elegant command chaining
4. **Use Here Strings** for more readable command inputs
5. **Enforce Local Variables** to prevent scope leakage

### Phase 5: Testing and Documentation

1. **Develop Test Cases** for the simplified scripts
2. **Document New Patterns** for future script development
3. **Update CLI Documentation** to reflect changes
4. **Create Migration Guide** for updating existing scripts

## Implementation Process

1. Create the new directory structure
2. Create shared library files
3. Identify categories for scripts based on functionality
4. Refactor scripts one by one, following functional programming principles
5. Update paths in scripts to use the configuration file
6. Test each script after refactoring
7. Update documentation and help messages
8. Create symlinks in the old locations to ensure backward compatibility

## Testing Process

1. Run each script with `-h` or `--help` to verify help message
2. Run each script with valid arguments to verify functionality
3. Run each script with invalid arguments to verify error handling
4. Verify that no hardcoded paths are used
5. Verify that the script follows functional programming principles

## Implementation Order

1. Enhance common libraries (`common.sh`, `test-lib.sh`)
2. Implement the unified test execution script
3. Update the main CLI (`s8r`)
4. Test all changes thoroughly
5. Update documentation
