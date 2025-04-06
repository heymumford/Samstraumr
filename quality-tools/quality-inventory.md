<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Quality Tools Inventory and Analysis

This document provides a comprehensive inventory and analysis of all quality-related tools currently used in the Samstraumr project. It serves as the foundation for the Unified Quality Checks implementation.

## Current Quality Tools

### 1. Spotless

**Purpose**: Code formatting and style enforcement

**Configuration Files**:
- Maven POM configuration in `Samstraumr/pom.xml`

**Implementation Status**: Working correctly

**Usage**:
```bash
mvn spotless:check      # Check code formatting
mvn spotless:apply      # Apply code formatting fixes
```

**Features**:
- License header management
- Java code formatting
- XML and Markdown formatting
- Tab/space normalization

**Issues/Limitations**:
- No significant issues identified

### 2. Checkstyle

**Purpose**: Code style checking based on a defined style guide

**Configuration Files**:
- `Samstraumr/checkstyle.xml`
- `quality-tools/checkstyle-suppressions.xml`

**Implementation Status**: Working correctly

**Usage**:
```bash
mvn checkstyle:check    # Check code style
```

**Features**:
- Enforces Google Java Style (customized)
- File header verification
- Import order checks
- Naming conventions
- File-specific suppressions via checkstyle-suppressions.xml

**Issues/Limitations**:
- Configuration distributed between two files in different locations

### 3. SpotBugs

**Purpose**: Static analysis to find potential bugs

**Configuration Files**:
- `quality-tools/spotbugs-exclude.xml`

**Implementation Status**: Working correctly

**Usage**:
```bash
mvn spotbugs:check      # Run SpotBugs analysis
```

**Features**:
- Bug pattern detection
- FindSecBugs integration for security analysis
- Custom exclusion filters
- XML report generation

**Issues/Limitations**:
- No HTML report configuration found

### 4. PMD

**Purpose**: Additional static analysis with focus on code quality

**Configuration Files**:
- `quality-tools/pmd-ruleset.xml`
- `quality-tools/pmd-exclude.properties`

**Implementation Status**: Partially working - compatibility issues with Maven site plugin

**Usage**:
```bash
mvn pmd:check           # Run PMD analysis
```

**Features**:
- Custom ruleset with categorized rules
- File exclusion patterns
- Multiple report formats

**Issues/Limitations**:
- Compatibility issues with Maven site plugin
- Currently recommended to skip and rely on SpotBugs

### 5. JaCoCo

**Purpose**: Code coverage analysis

**Configuration Files**:
- Maven POM configuration in `Samstraumr/samstraumr-core/pom.xml`

**Implementation Status**: Partially working - requires disabling the 'fast' profile

**Usage**:
```bash
mvn test jacoco:report -P \!fast -Djacoco.skip=false     # Generate coverage report
```

**Features**:
- Line coverage (70% threshold)
- Branch coverage (60% threshold)
- Complexity rules:
  - Maximum class complexity: 20
  - Maximum method complexity: 10
- HTML report generation

**Issues/Limitations**:
- Requires disabling the 'fast' profile
- Missing consistent configuration across modules

### 6. OWASP Dependency Check

**Purpose**: Security vulnerability scanning in dependencies

**Configuration Files**:
- Maven POM configuration in `Samstraumr/pom.xml`

**Implementation Status**: Working correctly (in security-checks profile)

**Usage**:
```bash
mvn verify -P security-checks     # Run security vulnerability scan
```

**Features**:
- Scans for known vulnerabilities in dependencies
- Configured to fail on CVSS score 7 or higher
- HTML and XML report generation

**Issues/Limitations**:
- Only available in a specific profile, not part of standard checks

### 7. JavaDoc

**Purpose**: Documentation quality verification

**Configuration Files**:
- Maven POM configuration in `Samstraumr/pom.xml`

**Implementation Status**: Working correctly (in doc-checks profile)

**Usage**:
```bash
mvn javadoc:javadoc -P doc-checks     # Generate and check JavaDoc
```

**Features**:
- Validates JavaDoc completeness
- Enforces documentation standards
- HTML report generation

**Issues/Limitations**:
- Only available in a specific profile, not part of standard checks

### 8. File Encoding Check

**Purpose**: Ensures consistent file encodings and line endings

**Configuration Files**:
- Script-based implementation in `util/scripts/check-encoding.sh`

**Implementation Status**: Working correctly

**Usage**:
```bash
./util/scripts/check-encoding.sh      # Check file encodings
./util/scripts/check-encoding.sh --fix    # Fix encoding issues
```

**Features**:
- Verifies UTF-8 encoding
- Checks for appropriate line endings (LF vs CRLF)
- Can automatically fix issues

**Issues/Limitations**:
- Not integrated into Maven build

### 9. ArchUnit

**Purpose**: Architecture testing and enforcement

**Configuration Files**:
- Test classes (not yet implemented)

**Implementation Status**: Ready for implementing tests

**Usage**:
- Via JUnit tests

**Features**:
- Architecture rule verification
- Dependency enforcement
- Package structure validation

**Issues/Limitations**:
- No tests implemented yet

## Script Inventory

### Current Quality-Related Scripts

1. **check-build-quality.sh**
   - Location: `util/scripts/check-build-quality.sh`
   - Purpose: Runs all quality checks and tests
   - Features:
     - Supports selectively skipping tools
     - Command-line arguments for customization
     - Detailed output with colored formatting
   - Limitations:
     - Some quality checks are hardcoded
     - No unified reporting

2. **quality-lib.sh**
   - Location: `util/lib/quality-lib.sh`
   - Purpose: Shared functions for quality scripts
   - Features:
     - Functions for running different quality tools
     - Code fix functions for common issues
     - Header update utilities
   - Limitations:
     - Not consistently used by all quality scripts

3. **check-encoding.sh**
   - Location: `util/scripts/check-encoding.sh`
   - Purpose: Checks file encodings and line endings
   - Features:
     - Verifies UTF-8 encoding
     - Checks for appropriate line endings (LF vs CRLF)
     - Can automatically fix issues
   - Limitations:
     - Not integrated into Maven build

4. **update-java-headers.sh**
   - Location: `util/scripts/update-java-headers.sh`
   - Purpose: Updates headers in Java files
   - Features:
     - Applies consistent license headers
     - Batch processing of files
   - Limitations:
     - Duplicated functionality with quality-lib.sh

5. **fix-pmd.sh**
   - Location: `util/scripts/fix-pmd.sh`
   - Purpose: Fixes PMD issues
   - Features:
     - Adds proper PMD suppression annotations
     - Converts comment-based suppressions to annotations
   - Limitations:
     - Duplicated functionality with quality-lib.sh

## Maven Profile Inventory

The project uses the following Maven profiles for quality checks:

1. **quality-checks** (default)
   - Standard quality checks
   - Includes Spotless, Checkstyle, SpotBugs

2. **security-checks**
   - Security analysis with OWASP Dependency Check
   - Not part of the default build

3. **doc-checks**
   - JavaDoc generation and validation
   - Not part of the default build

4. **coverage**
   - JaCoCo code coverage analysis
   - Not part of the default build

5. **fast** (default)
   - Skips tests and some quality checks for faster builds
   - Interferes with proper quality verification

## Analysis of Current Issues

1. **Fragmentation**:
   - Quality tools are configured in different locations
   - Some tools are only activated in specific profiles
   - Duplicate functionality between scripts

2. **Configuration Inconsistencies**:
   - Different configuration patterns for similar tools
   - Missing or incomplete configuration for some modules

3. **Reporting**:
   - No unified reporting across tools
   - Different report formats and locations
   - Difficult to get an overall quality picture

4. **Command Line Interface**:
   - Multiple commands required to run all checks
   - Inconsistent command-line arguments between scripts
   - No unified entry point

5. **Integration**:
   - Limited integration with CI/CD
   - No quality gates defined for automated builds
   - Manual review required for most quality issues

## Recommendations

1. **Unified Tool Configuration**:
   - Consolidate all configuration files in `quality-tools` directory
   - Use consistent configuration patterns across all tools
   - Add comprehensive documentation for each tool's purpose and usage

2. **Unified Script Structure**:
   - Create a single entry point script (`check-quality.sh`)
   - Modularize functionality into separate libraries
   - Implement consistent command-line arguments

3. **Unified Reporting**:
   - Create a consolidated HTML report
   - Include summary metrics and detailed findings
   - Generate badge-friendly metrics

4. **Maven Integration**:
   - Add a unified Maven profile for all quality checks
   - Ensure all tools can be run via Maven
   - Define quality gates for CI/CD

5. **CI/CD Integration**:
   - Add GitHub Actions workflow for quality checks
   - Configure automatic PR feedback
   - Implement quality badges for README

## Implementation Plan

Based on this inventory and analysis, the implementation plan will:

1. Consolidate all quality tool configurations in a single location
2. Implement a unified script structure with a single entry point
3. Create a consolidated reporting mechanism
4. Integrate with Maven and CI/CD
5. Document the unified quality check system

See `docs/plans/active-unified-quality-checks.md` for the detailed implementation plan.