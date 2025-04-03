# Script Reorganization Summary

## Overview

This document summarizes the reorganization of utility scripts to improve maintainability and consistency across the Samstraumr project. The reorganization involved:

1. **Flattening the directory structure** - Moving most scripts to the top-level `util/` directory
2. **Standardizing naming conventions** - Implementing consistent kebab-case names with function-based prefixes
3. **Clean transition approach** - Removing old script locations entirely for a clean break
4. **Updating documentation** - Revising README files to reflect the new organization

## New Directory Structure

### Root Level Scripts (Primary Functions)

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

### Helper Scripts (Secondary Functions)

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

## Script Mapping (Old to New)

The following table shows the mapping from old script locations to new ones. The old script locations have been completely removed for a clean break and to avoid ambiguity:

| Old Location | New Location |
|--------------|--------------|
| **Build Scripts** | |
| `util/build/build-optimal.sh` | `util/build-optimal.sh` |
| `util/build/build-performance.sh` | `util/build-performance.sh` |
| `util/build/generate-build-report.sh` | `util/build-report.sh` |
| `util/build/java-env-setup.sh` | `util/setup-java-env.sh` |
| `util/build/java17-compat.sh` | `util/setup-java17-compat.sh` |
| **Quality Scripts** | |
| `util/quality/build-checks.sh` | `util/scripts/check-build-quality.sh` |
| `util/quality/check-encoding.sh` | `util/scripts/check-encoding.sh` |
| `util/quality/fix-pmd.sh` | `util/scripts/fix-pmd.sh` |
| `util/quality/skip-quality-build.sh` | `util/scripts/build-skip-quality.sh` |
| `util/quality/enable-quality-tools.sh` | `util/scripts/setup-quality-tools.sh` |
| **Maintenance Scripts** | |
| `util/maintenance/update-version.sh` | `util/version.sh` |
| `util/maintenance/cleanup-maven.sh` | `util/scripts/clean-maven.sh` |
| `util/maintenance/fix-line-endings.sh` | `util/scripts/fix-line-endings.sh` |
| `util/maintenance/setup-fast.sh` | `util/scripts/setup-fast-build.sh` |
| `util/maintenance/update-and-test.sh` | `util/update-version-test.sh` |
| **Test Scripts** | |
| `util/test/run-tests.sh` | `util/test-run.sh` |
| `util/test/run-atl-tests.sh` | `util/test-run-atl.sh` |
| `util/test/run-all-tests.sh` | `util/test-run-all.sh` |
| `util/test/mapping/map-test-type.sh` | `util/test-map-type.sh` |

## Naming Conventions

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

## Documentation Updates

The util/README.md file has been updated to reflect the new organization, with:

1. Clear description of the new directory structure
2. Updated usage examples
3. Explanation of naming conventions
4. Guidelines for adding new scripts

## Transition Plan

We've opted for a clean break approach:

1. **Immediate Actions**
   - Old script locations completely removed
   - Documentation fully updated to reference new locations
   - CI/CD scripts updated to use new locations
   - References in other scripts updated

2. **Benefits of Clean Break**
   - No ambiguity about which script to use
   - Forces immediate updates to references
   - Cleaner codebase without duplicated functionality
   - Simpler maintenance without transition code

3. **Mitigation for Disruption**
   - Clear documentation of all renamed scripts
   - Error messages in CI will explicitly point to missing files
   - Comprehensive mapping table in this document
   - README.md in util/ directory updated with new locations

4. **Going Forward**
   - All new scripts will follow the standardized naming conventions
   - Scripts will be placed in appropriate locations in the flattened structure
   - Root level for primary functions, scripts/ directory for helpers