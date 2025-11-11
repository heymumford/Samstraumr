<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Samstraumr Build Output Standard

This document defines the standardized output format for all build and test scripts in the Samstraumr project. Maintaining a consistent output format helps users quickly understand the status of operations and improves the user experience.

## Core Principles

1. **Clarity**: Output should clearly indicate what is happening
2. **Consistency**: All scripts should follow the same output pattern
3. **Progress Indication**: Users should know when steps start and complete
4. **Error Visibility**: Errors should be immediately apparent

## Standard Output Pattern

All operations should follow a three-phase output pattern:

1. **Going to do**: Indicate what operation is about to start
2. **Doing**: Show progress during long-running operations
3. **Done**: Indicate successful completion

### Format Specification

#### Headers (Section Titles)
```
============================
  Section Title Here
============================
```

#### Information Messages (Going to do)
```
→ About to perform operation X
→ Starting process Y
```

#### Success Messages (Done)
```
✓ Operation X completed successfully
✓ Process Y finished
```

#### Warning Messages
```
! Warning: Potential issue detected
! Caution: Configuration may need adjustment
```

#### Error Messages
```
✗ Error: Operation failed
✗ Failed: Unable to complete process
```

#### Timing Information
```
Execution time: 45s
Execution time: 2m 15s
```

## Color Coding

All scripts should use the following ANSI color codes:

| Element | Color | ANSI Code |
|---------|-------|-----------|
| Headers | Blue, Bold | `\033[1;34m` |
| Info Messages | Blue | `\033[0;34m` |
| Success Messages | Green | `\033[0;32m` |
| Warning Messages | Yellow | `\033[0;33m` |
| Error Messages | Red | `\033[0;31m` |
| Timing Information | Yellow | `\033[0;33m` |
| Normal Text | Default | `\033[0m` |

## Example Output

Below is an example of the expected output format for a build operation:

```
============================
  Building Samstraumr
============================
→ Mode: test
→ Clean build enabled
→ Starting Maven build process...
  ... Maven output here (may be verbose) ...
✓ Build completed successfully
Execution time: 45s

============================
  Running Tests
============================
→ Test type: component-identity
→ Running Maven test process...
  ... Maven output here (may be verbose) ...
✓ Tests completed successfully
Execution time: 32s

============================
  Quality Checks
============================
→ Running checkstyle analysis...
✓ Checkstyle checks passed
→ Running PMD analysis...
! Warning: 3 PMD issues found (below threshold)
→ Running SpotBugs analysis...
✓ SpotBugs checks passed
Execution time: 28s
```

## Implementation Guide

To ensure consistent output across all scripts, use the following bash functions:

```bash
# Terminal colors
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
BOLD='\033[1m'
RESET='\033[0m'

# Functions for prettier output
print_header() {
  echo -e "${BLUE}${BOLD}============================${RESET}"
  echo -e "${BLUE}${BOLD}  $1${RESET}"
  echo -e "${BLUE}${BOLD}============================${RESET}"
}

print_info() { 
  echo -e "${BLUE}→ $1${RESET}"
}

print_success() { 
  echo -e "${GREEN}✓ $1${RESET}"
}

print_warning() { 
  echo -e "${YELLOW}! $1${RESET}" >&2
}

print_error() { 
  echo -e "${RED}✗ $1${RESET}" >&2
  exit 1
}

print_timing() {
  local duration=$1
  if [ $duration -gt 60 ]; then
    local minutes=$((duration / 60))
    local seconds=$((duration % 60))
    echo -e "${YELLOW}Execution time: ${minutes}m ${seconds}s${RESET}"
  else
    echo -e "${YELLOW}Execution time: ${duration}s${RESET}"
  fi
}

time_exec() {
  local start_time=$(date +%s)
  "$@"
  local exit_code=$?
  local end_time=$(date +%s)
  local duration=$((end_time - start_time))
  
  print_timing $duration
  
  return $exit_code
}
```

## Integration with Existing Scripts

All build and test scripts should source the unified common library at:
`/util/lib/unified-common.sh` which contains these standardized output functions.

## Benefits

Standardizing the output format across all scripts provides several benefits:

1. **Improved User Experience**: Users know exactly what to expect
2. **Faster Troubleshooting**: Issues are more readily apparent
3. **Easier Automation**: Standardized output is easier to parse for automation
4. **Consistent Documentation**: Screenshots and examples remain consistent
5. **Professional Appearance**: Polished look enhances project quality perception

## Compliance Checking

A script is available to check if other scripts comply with the output standard:
`bin/check-output-standard.sh`

Run this periodically to ensure all scripts maintain the standardized output format.