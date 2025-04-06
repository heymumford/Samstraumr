<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Unified Quality Checks Plan

This document outlines the plan to implement a unified quality check system for the Samstraumr project.

## Metadata

- **Category**: Build/CI
- **Priority**: P1
- **Status**: Planned
- **Owner**: Development Team
- **Due**: 2025-04-20
- **Issue**: N/A

## Overview

Currently, quality checks are scattered across multiple scripts and configuration files, making it difficult to maintain and ensure consistent quality throughout the codebase. This plan aims to consolidate all quality checks into a unified system that can be run consistently in various environments.

## Current Issues

1. Quality check scripts are fragmented across multiple locations
2. Configuration for quality tools is inconsistent
3. No unified reporting for quality issues
4. Difficult to run all checks with a single command
5. No clear metrics for code quality

## Standardization Goals

1. **Consolidated scripts**: Create a unified script to run all quality checks
2. **Consistent configuration**: Standardize configuration for all quality tools
3. **Unified reporting**: Create a consolidated report for all quality issues
4. **Single command**: Allow running all quality checks with a single command
5. **Quality metrics**: Define clear metrics to measure code quality

## Implementation Plan

### Phase 1: Inventory and Analysis (Completed)

1. ✅ Inventory all existing quality check tools:
   - Checkstyle
   - SpotBugs
   - PMD
   - JaCoCo
   - Spotless
   - OWASP Dependency Check
   - JavaDoc
   - File Encoding Check
   - ArchUnit
2. ✅ Analyze current configuration for each tool:
   - Documented current settings in `quality-tools/quality-inventory.md`
   - Identified inconsistencies in configuration patterns
   - Determined optimal configuration approach
3. ✅ Map current scripts to functions:
   - Identified duplicate functionality between scripts
   - Documented common patterns across quality scripts
   - Planned unified structure for consolidation

### Phase 2: Tool Configuration Standardization (In Progress)

1. ✅ Create standardized configuration files:
   - Checkstyle: `quality-tools/checkstyle/checkstyle.xml`
   - SpotBugs: `quality-tools/spotbugs/spotbugs-exclude.xml`
   - PMD: `quality-tools/pmd/pmd-ruleset.xml`
   - JaCoCo: `quality-tools/jacoco/jacoco.xml`
2. ✅ Define quality thresholds:
   - Code coverage: Line coverage > 70%, branch coverage > 60%
   - Complexity: Class complexity < 20, method complexity < 10
   - Duplication: < 3% duplicated code
   - Documented in `quality-tools/quality-thresholds.xml`
3. ✅ Create profiles for different quality levels:
   - Standard: Basic quality checks for development
   - Strict: Thorough checks for releases
   - Security: Focused on security issues
   - Defined in `quality-tools/quality-thresholds.xml`

### Phase 3: Script Implementation (Completed)

1. ✅ Create a unified script structure:
   - `util/bin/quality/check-quality.sh`: Main entry point
   - Created module directory for tool-specific functions
2. ✅ Implement script functions:
   - Created modules for each tool:
     - `modules/spotless.sh`: Code formatting checks
     - `modules/checkstyle.sh`: Style checks
     - `modules/spotbugs.sh`: Bug detection
     - `modules/pmd.sh`: Static analysis
     - `modules/jacoco.sh`: Coverage checks
     - `modules/owasp.sh`: Security checks
     - `modules/javadoc.sh`: Documentation checks
     - `modules/encoding.sh`: File encoding checks
3. ✅ Add reporting functionality:
   - HTML report generation with summary
   - Consolidated output with colored formatting
   - Tool-specific reports linked from main report

### Phase 4: Integration (Completed)

1. ✅ Integrate with s8r CLI:
   - Added s8r-quality symbolic link
   - Updated s8r CLI to use the unified quality checker
   - Added comprehensive help output for the new quality command
2. ✅ Integrate with CI pipeline:
   - Added profiles for different quality levels (standard, strict, security)
   - Configured HTML report generation
   - Set up reporting directory structure
3. ✅ Add pre-commit options:
   - Added --fix option to automatically fix issues
   - Added report-only mode for non-blocking checks
   - Added tool-specific selection and skipping

### Phase 5: Documentation and Training (Completed)

1. ✅ Create documentation:
   - Tool configuration documentation
   - Script usage examples
   - Quality metrics and thresholds
   - Created comprehensive reference in `docs/reference/quality-checks.md`
2. ✅ Update CLI and guides:
   - Updated s8r CLI help output
   - Added tool module documentation
   - Created README for quality scripts

## Implementation Schedule

| Phase |                Task                | Target Start | Target Completion | Actual Completion |    Status    |
|-------|------------------------------------|--------------|-------------------|-------------------|--------------|
| 1     | Inventory and Analysis             | 2025-04-10   | 2025-04-12        | 2025-04-06        | ✅ Completed |
| 2     | Tool Configuration Standardization | 2025-04-12   | 2025-04-15        | 2025-04-06        | ✅ Completed |
| 3     | Script Implementation              | 2025-04-15   | 2025-04-17        | 2025-04-06        | ✅ Completed |
| 4     | Integration                        | 2025-04-17   | 2025-04-19        | 2025-04-06        | ✅ Completed |
| 5     | Documentation and Training         | 2025-04-19   | 2025-04-20        | 2025-04-06        | ✅ Completed |

## Success Criteria

1. **Usability**: All quality checks can be run with a single command
2. **Visibility**: Consolidated quality report available
3. **Integration**: Quality checks integrated with CI pipeline
4. **Coverage**: All major quality aspects (style, bugs, security, coverage) addressed
5. **Consistency**: Consistent results across local and CI environments

## References

- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [SonarQube Quality Gates](https://docs.sonarqube.org/latest/user-guide/quality-gates/)
