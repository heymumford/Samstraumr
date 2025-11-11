# Script Updates

This document summarizes the updates to the core Samstraumr dispatcher scripts to use the unified common library.

## Overview

We have updated four core scripts to use the unified common library and follow consistent patterns:

1. **s8r**: Main dispatcher script
2. **s8r-test**: Test runner
3. **s8r-build**: Build tool
4. **s8r-version**: Version manager

Each script has been updated to follow best practices:

- Uses the unified common library for consistent formatting and functionality
- Implements graceful fallback to handle cases where the unified library isn't available
- Uses a more structured functional programming approach
- Adds comprehensive documentation and help text
- Improves error handling and exit conditions
- Provides better debugging output in verbose mode

## S8r - Main Dispatcher

The main dispatcher script has been updated to:

- Use standardized section formatting with `print_header` and `print_section` for better readability
- Implement the unified common library's output formatting functions
- Add improved help text with consistent formatting
- Include smart fallback to original libraries when unified library is not available

## S8r-Test - Test Runner

The test runner script has been updated to:

- Use the unified common library for consistent output
- Add a fully functional argument parsing system
- Improve test status reporting with standardized formatting
- Add better help text with comprehensive examples
- Include improved verbose mode with debug information
- Enhance directory and file handling

## S8r-Build - Build Tool

The build tool script has been updated to:

- Use the unified common library for consistent output
- Add improved Maven command construction using library functions
- Enhance build status reporting
- Provide better help text with descriptive explanations
- Include improved error handling and exit status

## S8r-Version - Version Manager

The version manager script has been updated to:

- Use the unified common library for consistent output
- Add semantic version validation using library functions
- Improve error handling for invalid versions
- Enhance help text with consistent formatting
- Add debug output for better troubleshooting

## Implementation Details

All updated scripts share these common features:

1. **Smart Library Loading**:
   ```bash
   # Source unified common library if available, otherwise use original
   if [ -f "${PROJECT_ROOT}/util/lib/unified-common.sh" ]; then
     source "${PROJECT_ROOT}/util/lib/unified-common.sh"
   elif [ -f "${PROJECT_ROOT}/.s8r/lib/common.sh" ]; then
     source "${PROJECT_ROOT}/.s8r/lib/common.sh"
   else
     echo "Error: Required library file not found"
     exit 1
   fi
   ```

2. **Functional Structure**:
   - Main execution separated from functions
   - Functions organized by purpose
   - Command processing in dedicated functions

3. **Consistent Help Formatting**:
   ```bash
   function show_help() {
     print_header "Script Title"
     echo
     print_section "Usage"
     echo "  ./script-name [options] [arguments]"
     echo
     print_section "Options"
     echo "  -v, --verbose           Enable verbose output"
     # ...other options...
     echo
     print_section "Examples"
     echo "  ./script-name example1      # Example 1 description"
     # ...other examples...
   }
   ```

4. **Systematic Argument Parsing**:
   ```bash
   function parse_args() {
     while [[ $# -gt 0 ]]; do
       case "$1" in
         -v|--verbose)
           VERBOSE=true
           shift
           ;;
         # ...other options...
         *)
           # Handle positional arguments
           shift
           ;;
       esac
     done
   }
   ```

## Installation

The updated scripts have been created with `-new` suffixes to allow for testing before full deployment. To install them:

1. Test each script to ensure it works correctly
2. Replace the old versions with the new ones:
   ```bash
   mv s8r-new s8r
   mv s8r-test-new s8r-test
   mv s8r-build-new s8r-build
   mv s8r-version-new s8r-version
   ```

## Next Steps

With these core scripts updated, the next steps are to:

1. Update the remaining specialized dispatcher scripts
2. Consolidate documentation scripts
3. Update utility scripts
4. Update quality check scripts
