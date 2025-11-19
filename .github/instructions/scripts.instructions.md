---
applyTo:
  - "bin/**"
  - "util/**"
  - "*.sh"
  - "**/*.sh"
excludeAgent: []
---

# Scripts and Utilities Instructions

These instructions apply to all shell scripts, build utilities, and automation tools in the Samstraumr repository.

## Script Philosophy

- Scripts should be maintainable and self-documenting
- Follow Unix philosophy: do one thing well
- Provide clear error messages and exit codes
- Make scripts idempotent where possible
- Test scripts before committing

## Shell Script Standards

### Script Structure

Every script should follow this structure:

```bash
#!/bin/bash
# Script description and purpose
#
# Usage: script-name [options] [arguments]
#
# Copyright (c) 2025 Eric C. Mumford (@heymumford)
# Licensed under the Mozilla Public License 2.0

set -euo pipefail  # Exit on error, undefined variables, pipe failures

# Constants and configuration
readonly SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
readonly PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

# Functions
function show_usage() {
    cat << EOF
Usage: $(basename "$0") [options] [arguments]

Description of what the script does.

Options:
    -h, --help      Show this help message
    -v, --verbose   Enable verbose output
    
Examples:
    $(basename "$0") --help
    $(basename "$0") argument
EOF
}

function main() {
    # Main script logic
    echo "Running script..."
}

# Script execution
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
```

### Bash Options

Always use these at the start of scripts:
- `set -e`: Exit on error
- `set -u`: Exit on undefined variable
- `set -o pipefail`: Exit on pipe failure
- `set -x`: Enable debug mode (optional, for troubleshooting)

### Naming Conventions

- **Script names**: Use `kebab-case` with descriptive names
- **Functions**: Use `snake_case` for function names
- **Variables**: Use `UPPER_SNAKE_CASE` for constants, `snake_case` for variables
- **Prefix**: Use `s8r-` prefix for project-specific utilities

Examples:
- `s8r-test` - Main test runner
- `s8r-build` - Build orchestrator
- `s8r-version` - Version management

## Error Handling

### Exit Codes

Use standard exit codes:
- `0`: Success
- `1`: General error
- `2`: Misuse of command
- `126`: Command cannot execute
- `127`: Command not found
- `130`: Script terminated by Ctrl+C

### Error Messages

```bash
function error() {
    echo "ERROR: $*" >&2
    return 1
}

function fatal() {
    echo "FATAL: $*" >&2
    exit 1
}

function warning() {
    echo "WARNING: $*" >&2
}

function info() {
    echo "INFO: $*"
}
```

### Validation

Always validate inputs and prerequisites:

```bash
function validate_requirements() {
    if [[ ! -f "pom.xml" ]]; then
        fatal "pom.xml not found. Run from project root."
    fi
    
    if ! command -v java &> /dev/null; then
        fatal "Java not found. Please install Java 17+"
    fi
    
    if ! command -v mvn &> /dev/null; then
        fatal "Maven not found. Please install Maven 3.9+"
    fi
}
```

## CLI Tools Best Practices

### Help Documentation

Every CLI tool should provide:
- `-h` or `--help` flag for usage information
- Clear description of what the tool does
- List of all options and arguments
- Usage examples
- Exit codes documentation

### Argument Parsing

Use consistent patterns for argument parsing:

```bash
function parse_arguments() {
    while [[ $# -gt 0 ]]; do
        case "$1" in
            -h|--help)
                show_usage
                exit 0
                ;;
            -v|--verbose)
                VERBOSE=true
                shift
                ;;
            -*)
                error "Unknown option: $1"
                show_usage
                exit 2
                ;;
            *)
                POSITIONAL_ARGS+=("$1")
                shift
                ;;
        esac
    done
}
```

### Output Formatting

- Use colors for better readability (with fallback for non-color terminals)
- Provide progress indicators for long operations
- Use consistent formatting for success/error messages

```bash
# Color codes
readonly RED='\033[0;31m'
readonly GREEN='\033[0;32m'
readonly YELLOW='\033[1;33m'
readonly NC='\033[0m' # No Color

function print_success() {
    echo -e "${GREEN}✓${NC} $*"
}

function print_error() {
    echo -e "${RED}✗${NC} $*" >&2
}

function print_warning() {
    echo -e "${YELLOW}⚠${NC} $*"
}
```

## Build Scripts

### Build Orchestration

- Use `s8r-build` as the main build orchestrator
- Support different build modes: fast, full, with/without tests
- Clean up before building
- Provide clear build progress indicators

### Build Targets

Common build targets:
- `compile`: Compile source code
- `test`: Run tests
- `package`: Create distribution packages
- `verify`: Run quality checks
- `clean`: Remove build artifacts

## Test Scripts

### Test Execution

- Use `s8r-test` as the main test runner
- Support test type filtering: unit, component, integration
- Support coverage reporting
- Provide test result summaries

### Test Isolation

- Clean test environment before running
- Don't modify source code during tests
- Capture test output for debugging
- Support running individual tests

## Utility Scripts

### Path Management

Always use absolute paths or properly resolved relative paths:

```bash
# Get script directory
readonly SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Get project root
readonly PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

# Change to project root
cd "${PROJECT_ROOT}" || fatal "Cannot change to project root"
```

### Temporary Files

Use proper temporary file handling:

```bash
# Create temporary file
readonly TEMP_FILE="$(mktemp)"
trap 'rm -f "${TEMP_FILE}"' EXIT

# Create temporary directory
readonly TEMP_DIR="$(mktemp -d)"
trap 'rm -rf "${TEMP_DIR}"' EXIT
```

### File Operations

- Check if files/directories exist before operations
- Use atomic operations when possible
- Provide clear error messages
- Handle spaces in filenames properly

```bash
# Check file exists
if [[ ! -f "${CONFIG_FILE}" ]]; then
    fatal "Configuration file not found: ${CONFIG_FILE}"
fi

# Safe file operations
cp -f "${SOURCE}" "${DEST}" || error "Failed to copy file"

# Handle spaces in filenames
find . -name "*.java" -print0 | while IFS= read -r -d '' file; do
    process_file "${file}"
done
```

## Script Testing

### Testing Approach

- Test scripts with different inputs
- Test error conditions
- Test on clean environment
- Document tested scenarios

### Manual Testing Checklist

Before committing script changes:
- [ ] Run with valid inputs
- [ ] Run with invalid inputs
- [ ] Test all command-line options
- [ ] Test error handling
- [ ] Verify help message
- [ ] Check exit codes
- [ ] Test on clean environment

## Documentation

### Inline Comments

- Explain why, not what
- Document non-obvious behavior
- Keep comments up to date
- Use TODO/FIXME markers for known issues

```bash
# TODO: Add support for custom test categories
# FIXME: Handle case where Maven is not in PATH
# NOTE: This assumes Java 17+ is installed
```

### Script Headers

Every script should have a header explaining:
- Purpose and functionality
- Usage syntax
- Required dependencies
- Examples
- Author and license information

## Performance Considerations

- Avoid unnecessary subprocess spawning
- Use shell built-ins when possible
- Cache expensive operations
- Provide progress indicators for long operations

```bash
# Bad: Spawns unnecessary processes
for file in $(ls *.txt); do
    process_file "$file"
done

# Good: Uses shell globbing
for file in *.txt; do
    [[ -f "$file" ]] || continue
    process_file "$file"
done
```

## Security Considerations

- Never log sensitive information
- Validate and sanitize all inputs
- Use proper quoting to prevent injection
- Set proper file permissions
- Don't execute untrusted code

```bash
# Always quote variables
rm -f "${USER_INPUT}"  # Safe
rm -f $USER_INPUT      # Unsafe - can be exploited

# Validate input
if [[ ! "${USER_INPUT}" =~ ^[a-zA-Z0-9_-]+$ ]]; then
    fatal "Invalid input format"
fi
```

## Common Script Patterns

### Configuration Loading

```bash
function load_config() {
    local config_file="${1:-.s8r.config}"
    
    if [[ -f "${config_file}" ]]; then
        # shellcheck source=/dev/null
        source "${config_file}"
    else
        warning "Configuration file not found: ${config_file}"
    fi
}
```

### Dependency Checking

```bash
function check_dependencies() {
    local missing_deps=()
    
    for cmd in java mvn git; do
        if ! command -v "${cmd}" &> /dev/null; then
            missing_deps+=("${cmd}")
        fi
    done
    
    if [[ ${#missing_deps[@]} -gt 0 ]]; then
        fatal "Missing dependencies: ${missing_deps[*]}"
    fi
}
```

### Logging

```bash
function setup_logging() {
    local log_file="${1:-${PROJECT_ROOT}/logs/build.log}"
    local log_dir="$(dirname "${log_file}")"
    
    mkdir -p "${log_dir}"
    
    # Log to both file and console
    exec > >(tee -a "${log_file}")
    exec 2>&1
}
```

## Script Anti-Patterns to Avoid

- ❌ Using `cd` without error checking
- ❌ Unquoted variables that could contain spaces
- ❌ Parsing `ls` output
- ❌ Using `eval` with user input
- ❌ Hard-coding absolute paths
- ❌ Ignoring errors (not using `set -e`)
- ❌ Using deprecated commands (e.g., backticks instead of `$()`)
- ❌ Not providing help/usage information
