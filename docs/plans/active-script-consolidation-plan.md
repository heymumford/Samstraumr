# Script Consolidation Plan

## Overview

This document outlines a concrete implementation plan for consolidating and standardizing bash scripts in the Samstraumr project, building on the existing script simplification plan.

## Completed Tasks

1. **Unified Common Library**: Created a consolidated utility library
   - Implemented in `/util/lib/unified-common.sh`
   - Consolidates functions from multiple common.sh files
   - Maintains backward compatibility with legacy function names
   - Includes comprehensive documentation for all functions

2. **Documentation Library**: Created a specialized documentation utility library
   - Implemented in `/util/lib/doc-lib.sh`
   - Consolidates functions from documentation scripts
   - Provides specialized functions for markdown processing
   - Standardizes documentation validation and verification

3. **Improved C4 Diagram Generation**:
   - Updated `/bin/generate-diagrams.sh` to use the unified library
   - Enhanced help formatting and error handling
   - Added smart fallback to original libraries if unified version not found

4. **Core Script Updates**:
   - Updated `/s8r` main dispatcher script to use the unified library
   - Updated `/s8r-test` test runner to use the unified library
   - Updated `/s8r-build` build tool to use the unified library
   - Updated `/s8r-version` version manager to use the unified library
   - Added consistent output formatting and error handling
   - Improved help documentation and examples

5. **Documentation Script Updates**:
   - Updated `/docs/tools/doc-integrity-check-auto.sh` to use the doc-lib library
   - Added graceful fallback to legacy methods
   - Updated documentation in `/util/lib/README.md`

## Remaining Implementation Tasks

### Phase 1: Migrate Core Scripts to Unified Library

1. **Dispatcher Scripts** (✅ Completed):
   - ✅ Update `/s8r` dispatcher to use the unified library
   - ✅ Update module-specific dispatchers (s8r-build, s8r-test, s8r-version)
   - ✅ Standardize command parsing and help formatting

2. **Documentation Scripts** (🔄 In Progress):
   - ✅ Create dedicated `doc-lib.sh` library for shared documentation functions
   - ✅ Update README in utility library directory
   - ✅ Update `/docs/tools/doc-integrity-check-auto.sh` to use doc-lib.sh
   - ✅ Update `/docs/scripts/standardize-kebab-case.sh` to use doc-lib.sh 
   - ✅ Update `/docs/scripts/extract-todos.sh` to use doc-lib.sh
   - ✅ Update `/docs/scripts/update-cross-references.sh` to use doc-lib.sh
   - ✅ Update `/docs/scripts/check-todo-format.sh` to use doc-lib.sh
   - ✅ Update `/docs/scripts/standardize-todos.sh` to use doc-lib.sh
   - ✅ Update `/docs/tools/doc-integrity-check.sh` to use doc-lib.sh
   - ✅ Update `/docs/tools/generate-changelog.sh` to use doc-lib.sh
   - ✅ Update `/docs/tools/update-readme.sh` to use doc-lib.sh
   - ✅ Update `/docs/tools/generate-javadoc.sh` to use doc-lib.sh
   - ✅ Update `/docs/scripts/standardize-feature-filenames.sh` to use doc-lib.sh
   - ✅ Update `/docs/scripts/standardize-md-filenames.sh` to use doc-lib.sh
   - ✅ Update `/docs/scripts/standardize-planning-filenames.sh` to use doc-lib.sh
   - ✅ Update `/docs/scripts/convert-to-markdown.sh` to use doc-lib.sh
   - ✅ Update `/docs/scripts/fix-markdown-links.sh` to use doc-lib.sh
   - ✅ Update `/docs/scripts/update-markdown-headers.sh` to use doc-lib.sh
   - 🔄 Continue updating remaining scripts in `/docs/scripts/`

3. **Utility Scripts**:
   - Consolidate duplicate functions in `/util/scripts/`
   - Update scripts to use the unified library
   - Add standardized logging and error handling

### Phase 2: Organize Scripts by Function

1. **Create Functional Categories**:
   - **Build Scripts**: `/util/bin/build/`
   - **Test Scripts**: `/util/bin/test/`
   - **Quality Scripts**: `/util/bin/quality/`
   - **Version Scripts**: `/util/bin/version/`
   - **Utility Scripts**: `/util/bin/utils/`
   - **Documentation Scripts**: `/util/bin/docs/`

2. **Move Scripts to New Locations**:
   - Move scripts to appropriate category directories
   - Create symlinks at original locations for backward compatibility
   - Update READMEs in each category directory

3. **Standardize Script Headers**:
   - Ensure all scripts have standard headers following template
   - Add copyright and license information
   - Standardize function documentation

### Phase 3: Specialized Library Consolidation

1. **Test Library**:
   - Consolidate test-related functions from multiple scripts
   - Create centralized test type mapping
   - Standardize test result reporting

2. **Build Library**:
   - Consolidate Maven command building functions
   - Standardize build options and parameters
   - Create consistent progress reporting

3. **Version Library**:
   - Consolidate version management functions
   - Standardize version file handling
   - Create consistent version bumping utilities

4. **Quality Library**:
   - Consolidate code quality check functions
   - Standardize reporting format
   - Create unified quality command interface

### Phase 4: Script Verification and Testing

1. **Create Verification Tool**:
   - Implement tool to check script compliance with standards
   - Validate header format, function documentation, and error handling
   - Generate compliance reports

2. **Testing Framework**:
   - Create test cases for critical script functions
   - Implement automated testing for library functions
   - Add tests to CI/CD pipeline

### Phase 5: Documentation and Training

1. **Update Script Documentation**:
   - Create comprehensive documentation for script libraries
   - Document script organization and conventions
   - Create examples of common usage patterns

2. **Development Guidelines**:
   - Update developer documentation with script standards
   - Create templates for new scripts
   - Document script development best practices

## Implementation Roadmap

| Phase | Tasks | Timeline | Priority |
|-------|-------|----------|----------|
| Phase 1 | Migrate Core Scripts | 1-2 weeks | High |
| Phase 2 | Organize by Function | 1-2 weeks | High |
| Phase 3 | Specialized Libraries | 2-3 weeks | Medium |
| Phase 4 | Verification & Testing | 1-2 weeks | Medium |
| Phase 5 | Documentation | 1 week | Low |

## Implementation Guidelines

1. **Backward Compatibility**:
   - Maintain function signatures for existing APIs
   - Add deprecation warnings for functions that will be removed
   - Create compatibility layer for legacy scripts

2. **Testing Approach**:
   - Test each script before and after changes
   - Verify all options and arguments work correctly
   - Check help output and error handling

3. **Documentation Standards**:
   - Document all functions with purpose, arguments, and return values
   - Include examples for complex functions
   - Add inline comments for non-obvious logic

4. **Error Handling**:
   - Use consistent error messages
   - Return appropriate exit codes
   - Log errors to appropriate channels
   - Provide helpful error recovery suggestions

## Completion Criteria

A script is considered fully migrated when:

1. It sources the unified common library
2. It follows the standardized header format
3. It uses standard output and error handling functions
4. It has comprehensive help documentation
5. It has been tested with all supported options
6. It is located in the appropriate category directory

The consolidation project is complete when:

1. All scripts have been migrated
2. All duplicate functions have been consolidated
3. All specialized libraries are in place
4. Verification tests pass for all scripts
5. Documentation is complete and accurate