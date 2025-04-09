<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Samstraumr Build and Test Process

This document provides a comprehensive guide to the build and test process for the Samstraumr project. It outlines the standardized approach to building, testing, and quality checking the codebase.

## Build and Test Sequence

The Samstraumr build and test process follows this standardized sequence:

1. **Build**: Compile the code and create artifacts
2. **Test**: Run tests across different categories and levels
3. **Quality Checks**: Perform static analysis and quality verification
4. **Coverage**: Analyze and report test coverage

Each of these steps provides clear "going to do / doing / done" feedback to maintain visibility of the process.

## Core Scripts

All build and test operations are performed using scripts in the `bin/` directory:

| Script | Purpose |
|--------|---------|
| `bin/s8r-build` | Main build script with various modes |
| `bin/s8r-test` | Unified test runner for different test types |
| `bin/s8r-quality-enhanced` | Run enhanced quality checks |
| `bin/s8r-test-coverage` | Analyze and report test coverage |
| `bin/s8r-version` | Manage version information |

## Standard Build Process

### Step 1: Building the Project

```bash
# Fast build (skips tests and quality checks)
bin/s8r-build fast

# Build with tests
bin/s8r-build test

# Build for packaging
bin/s8r-build package

# Clean build (removes previous build artifacts first)
bin/s8r-build -c test

# Parallel build for faster execution
bin/s8r-build -p test

# Skip quality checks
bin/s8r-build test --skip-quality

# Full build (includes all tests and quality checks)
bin/s8r-build full
```

For each build command, you'll see standardized output indicating:
- Start of build process
- Current build mode and options
- Progress updates
- Completion status with timing information

### Step 2: Running Tests

```bash
# Run all tests
bin/s8r-test all

# Run specific test types
bin/s8r-test unit           # Unit level tests
bin/s8r-test component      # Component level tests
bin/s8r-test identity       # Identity-specific tests
bin/s8r-test component-identity  # Component and Identity tests (P0)
bin/s8r-test lifecycle      # Lifecycle tests (P1)
bin/s8r-test error-handling # Error handling tests (P1)
bin/s8r-test integration    # Integration tests

# Run with specialized test scripts
bin/s8r-test-lifecycle             # Run basic lifecycle state tests
bin/s8r-test-lifecycle negative    # Run lifecycle error handling tests
bin/s8r-test-lifecycle resources   # Run lifecycle resource tests
bin/s8r-test-lifecycle transitions # Run state transition tests
bin/s8r-test-lifecycle comprehensive # Run all lifecycle tests

# Run tests with specific options
bin/s8r-test all --coverage  # Include coverage analysis
bin/s8r-test all -p          # Run tests in parallel
bin/s8r-test all -v          # Verbose output
bin/s8r-test --verify        # Verify test suite structure
bin/s8r-test --verify --fix  # Fix common test issues
```

The test output follows a consistent format:
- Announcement of test type being run
- Test execution progress
- Summary of test results
- Timing information

### Step 3: Quality Checks

```bash
# Run enhanced quality checks
bin/s8r-quality-enhanced

# Fix issues automatically where possible
bin/s8r-quality-enhanced --fix

# Run quality checks for specific modules
bin/s8r-quality-enhanced --modules core,identity
```

Quality check output includes:
- What checks are being performed
- Progress for each check type
- Issues found (if any)
- Summary of quality status

### Step 4: Coverage Analysis

```bash
# Run coverage analysis
bin/s8r-test-coverage

# Run tests with coverage and analyze
bin/s8r-test all --coverage
```

Coverage output includes:
- Test count by category 
- Coverage percentages
- Gaps in test coverage
- Recommendations for improvement

## Polyglot Build Support

The build process supports multiple technologies:

1. **Java**: Primary codebase built with Maven
2. **Shell Scripts**: Build and test automation
3. **JavaScript/TypeScript**: For documentation and web interfaces 
4. **Python**: For build tools and utilities

The unified build scripts ensure consistent behavior across these different technologies.

## Output Consistency

All scripts follow these output standards:

1. **Headers**: Blue, bold text for major sections
2. **Info**: Blue arrows → for "going to do" notifications
3. **Success**: Green checkmarks ✓ for "done" notifications
4. **Warnings**: Yellow exclamation marks ! for cautions
5. **Errors**: Red X marks ✗ for failures
6. **Timing**: Yellow text for execution time information

Example output:

```
============================
  Building Samstraumr
============================
→ Mode: test
→ Clean build enabled
→ Starting Maven build process...
  ...build output...
✓ Build completed successfully
Execution time: 45s

============================
  Running Samstraumr Tests
============================
→ Test type: component-identity
→ Tags: @L0_Component or @L0_Identity
→ Running Maven test process...
  ...test output...
✓ Tests completed successfully
Execution time: 32s
```

## Complete Build Example

A complete build and test cycle with all validations would look like:

```bash
# 1. Build with tests
bin/s8r-build test

# 2. Run specific test suites
bin/s8r-test component-identity
bin/s8r-test lifecycle
bin/s8r-test error-handling

# 3. Run quality checks  
bin/s8r-quality-enhanced

# 4. Analyze test coverage
bin/s8r-test-coverage
```

## Automated CI Process

The continuous integration process uses the same scripts to ensure consistency between local development and CI pipelines:

```bash
# Run full build process with local CI validation
bin/s8r-build full --ci
```

## Troubleshooting

If issues occur during the build or test process:

1. Check the logs for specific error messages
2. Use verbose mode (`-v` or `--verbose`) for more detailed output
3. Try cleaning the build (`-c` or `--clean`) to remove stale artifacts
4. Check the Maven configuration in `pom.xml` files
5. Verify that test dependencies are correctly configured

## Specialized Test Scripts

### Lifecycle Tests

The project provides specialized scripts for testing different aspects of component lifecycle management:

```bash
bin/s8r-test-lifecycle [options] [mode]
```

**Modes:**
- `states` - Test state-dependent behavior only (default)
- `transitions` - Test state transitions only
- `resources` - Test resource management only
- `negative` - Test error handling and exceptional conditions
- `comprehensive` - Run all lifecycle tests

**Options:**
- `-v, --verbose` - Show detailed output
- `-o, --output` - Write results to file
- `-d, --dir` - Specify custom output directory
- `-q, --quality` - Run with quality checks (slower)

### Test Verification

The test verification system helps ensure test quality and completeness:

```bash
bin/s8r-test --verify
```

This command performs comprehensive checks of:
- Feature files with proper tags
- Step definitions for all features
- Test runners for each category
- Consistent organization

You can also automatically fix common issues:

```bash
bin/s8r-test --verify --fix
```

And generate a detailed verification report:

```bash
bin/s8r-test --verify --report
```

## Java Compatibility

Samstraumr now supports Java 21, which requires special JVM options for reflection-based code:

```
--add-opens java.base/java.lang=ALL-UNNAMED
--add-opens java.base/java.util=ALL-UNNAMED
--add-opens java.base/java.lang.reflect=ALL-UNNAMED
```

These options are automatically set when using the project scripts. You can switch Java versions with:

```bash
# Switch to Java 17
java-switch 17

# Switch to Java 21 (OpenJDK)
java-switch 21
```

## Further Reading

- [Maven Structure Guide](maven-structure.md)
- [Test Strategy](../testing/test-strategy.md)
- [Quality Checks](quality-checks.md)
- [Version Management](version-management.md)
- [Test Suite Implementation Report](../test-reports/test-suite-implementation-report.md)