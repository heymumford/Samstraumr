<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Script Reorganization

## Overview

This document summarizes the reorganization and refactoring of utility scripts to improve maintainability and consistency across the Samstraumr project. It covers both the initial reorganization and subsequent enhancements.

## Initial Reorganization

The initial reorganization involved:

1. **Flattening the directory structure** - Moving most scripts to the top-level `util/` directory
2. **Standardizing naming conventions** - Implementing consistent kebab-case names with function-based prefixes
3. **Clean transition approach** - Removing old script locations entirely for a clean break
4. **Updating documentation** - Revising README files to reflect the new organization

### New directory structure

#### Root level scripts (primary functions)

```
util/
├── build-optimal.sh          # Optimized build script
├── build-performance.sh      # Performance-focused build script
├── build-report.sh           # Build report generator
├── setup-java-env.sh         # Java environment setup
├── setup-java17-compat.sh    # Java 17 compatibility script
├── test-map-type.sh          # Test type mapping utility
├── test-run-all.sh           # Multi-terminology test runner
├── test-run-atl.sh           # Above-the-line test runner
├── test-run.sh               # Main test runner
├── update-version-test.sh    # Version + test script
├── version.sh                # Unified version management
```

#### Helper scripts (secondary functions)

```
util/scripts/
├── build-skip-quality.sh     # Build with quality checks skipped
├── check-build-quality.sh    # Run all quality checks
├── check-encoding.sh         # Verify file encodings
├── clean-maven.sh            # Clean Maven repository
├── fix-line-endings.sh       # Fix line ending issues
├── fix-logger.sh             # Fix logger implementations
├── fix-pmd.sh                # Fix PMD-related issues
├── generate-badges.sh        # Generate status badges
├── setup-fast-build.sh       # Fast build profile setup
├── setup-quality-tools.sh    # Enable quality tools
├── update-java-headers.sh    # Java header standardization
└── update-md-headers.sh      # Markdown header standardization
```

### Script mapping (old to new)

The following table shows the mapping from old script locations to new ones. The old script locations have been completely removed for a clean break and to avoid ambiguity:

|              Old Location              |             New Location              |
|----------------------------------------|---------------------------------------|
| **Build Scripts**                      |                                       |
| `util/build/build-optimal.sh`          | `util/build-optimal.sh`               |
| `util/build/build-performance.sh`      | `util/build-performance.sh`           |
| `util/build/generate-build-report.sh`  | `util/build-report.sh`                |
| `util/build/java-env-setup.sh`         | `util/setup-java-env.sh`              |
| `util/build/java17-compat.sh`          | `util/setup-java17-compat.sh`         |
| **Quality Scripts**                    |                                       |
| `util/quality/build-checks.sh`         | `util/scripts/check-build-quality.sh` |
| `util/quality/check-encoding.sh`       | `util/scripts/check-encoding.sh`      |
| `util/quality/fix-pmd.sh`              | `util/scripts/fix-pmd.sh`             |
| `util/quality/skip-quality-build.sh`   | `util/scripts/build-skip-quality.sh`  |
| `util/quality/enable-quality-tools.sh` | `util/scripts/setup-quality-tools.sh` |
| **Maintenance Scripts**                |                                       |
| `util/maintenance/update-version.sh`   | `util/version.sh`                     |
| `util/maintenance/cleanup-maven.sh`    | `util/scripts/clean-maven.sh`         |
| `util/maintenance/fix-line-endings.sh` | `util/scripts/fix-line-endings.sh`    |
| `util/maintenance/setup-fast.sh`       | `util/scripts/setup-fast-build.sh`    |
| `util/maintenance/update-and-test.sh`  | `util/update-version-test.sh`         |
| **Test Scripts**                       |                                       |
| `util/test/run-tests.sh`               | `util/test-run.sh`                    |
| `util/test/run-atl-tests.sh`           | `util/test-run-atl.sh`                |
| `util/test/run-all-tests.sh`           | `util/test-run-all.sh`                |
| `util/test/mapping/map-test-type.sh`   | `util/test-map-type.sh`               |

### Naming conventions

The new naming convention follows these principles:

1. **All scripts use kebab-case** - Words separated by hyphens (e.g., `build-optimal.sh`, not `buildOptimal.sh`)
2. **Function-based prefixes** - Script names start with their primary function:
   - `build-*` - Build-related scripts
   - `test-*` - Testing-related scripts
   - `setup-*` - Environment and configuration setup
   - `check-*` - Verification and validation
   - `fix-*` - Issue remediation
   - `update-*` - Content updating
3. **Consistent verb-noun order** - Action comes first (e.g., `clean-maven.sh`, not `maven-clean.sh`)

## Enhanced Hierarchical Structure

Following initial reorganization, scripts were further reorganized into a more structured hierarchical system:

```
/util
├── bin/              # Consolidated executable scripts
│   ├── build/        # Build-related scripts
│   ├── test/         # Testing-related scripts
│   ├── quality/      # Quality check scripts
│   ├── version/      # Version management scripts
│   ├── utils/        # Utility scripts
│   └── demo/         # Demonstration scripts
└── lib/              # Shared bash libraries and functions
    ├── common.sh     # Common utility functions
    ├── build-lib.sh  # Build-related functions
    ├── test-lib.sh   # Test-related functions
    ├── quality-lib.sh# Quality-related functions
    └── version-lib.sh# Version-related functions
```

## Additional Demo Command Infrastructure

As part of ongoing refactoring efforts, we added:

1. **Demo Command Infrastructure**
   - Created a new `demo` command in the `s8r` CLI for running demonstration scripts
   - Added `show_demo_help()` and `handle_demo_command()` functions to handle demo operations
   - Updated the main help to include the demo command in examples
2. **Demo Scripts Directory**
   - Created a new directory: `/util/bin/demo/` for demonstration scripts
   - Moved `inspect-identity.sh` from project root to `/util/bin/demo/tube-identity-demo.sh`
   - Renamed the script with a clearer, more descriptive name following naming conventions
3. **Script Cleanup**
   - Removed scripts from the project root directory
   - Ensured all scripts are properly executable (`chmod +x`)
   - Maintained compatibility with existing functionality

### Demo command usage examples

```bash
# Script Reorganization
./s8r demo list

# Script Reorganization
./s8r demo identity

# Script Reorganization
./s8r help demo
```

## Documentation Updates

The util/README.md file has been updated to reflect the new organization, with:

1. Clear description of the new directory structure
2. Updated usage examples
3. Explanation of naming conventions
4. Guidelines for adding new scripts

## Benefits

1. **Improved Organization**
   - Project root directory is now cleaner
   - Scripts are logically organized by purpose
   - Standardized naming makes scripts easier to find
2. **Enhanced Maintainability**
   - Centralized location for all scripts by type
   - Consistent CLI interface via the `s8r` command
   - Better separation of concerns
   - Shared library functions reduce duplication
3. **User Experience**
   - Users can discover functionality via help commands
   - Consistent interface for all operations
   - Improved discoverability through logical organization

## Future Work

1. **Additional Demos**
   - Add more demonstration scripts for key concepts
   - Consider adding documentation-specific demos
   - Add demos for new features as they are developed
2. **Demo Categories**
   - Organize demos into categories for better discovery
   - Add support for filtering demos by category
   - Allow listing demos with descriptions
3. **Integration with Documentation**
   - Link demos to relevant documentation
   - Add explanation comments in demo scripts
   - Consider adding step-by-step tutorials alongside demos
