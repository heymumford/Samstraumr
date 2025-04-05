# Samstraumr Library Files

This directory contains the shared library code used by the s8r command-line interface. These libraries provide common functionality used across different commands and operations.

## Library Files

- **common.sh**: Core functions and utilities used by all commands (output formatting, error handling, etc.)
- **docmosis.sh**: Docmosis integration for document generation and report creation

## Usage in Commands

These library files are designed to be sourced by the main `s8r` command and its subcommands. Each library provides a specific set of functions that can be used by multiple commands, promoting code reuse and maintaining a consistent interface.

```bash
# Example: Using a library in a command
source "${PROJECT_ROOT}/.s8r/lib/common.sh"
source "${PROJECT_ROOT}/.s8r/lib/docmosis.sh"

# Now you can use the library functions
info "Starting process..."
load_docmosis_config
```

## Library Structure

Each library file follows a consistent structure:

1. File header with description
2. Constants/configuration
3. Public functions 
4. Private helper functions (prefixed with underscore)

Functions in specialized libraries are prefixed with the library name to avoid naming conflicts (e.g., `docmosis_info` instead of just `info`).

## Adding New Libraries

When adding a new library:

1. Create a file named `<feature>-lib.sh` in this directory
2. Add a file header with description
3. If using common functions, source common.sh
4. Add documentation for each function
5. Use a specific prefix for function names to avoid conflicts 
6. Update this README to document the new library

## Testing

Library functions should be designed to be testable. Consider adding test functions that can verify the behavior of library code.