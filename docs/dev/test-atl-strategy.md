<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# A T L B T L Strategy

This document outlines the Above the Line (ATL) and Below the Line (BTL) testing strategy implemented in Samstraumr, providing guidelines for categorizing tests and ensuring efficient test execution.

## Table of Contents

- [Introduction](#introduction)
- [Key Concepts](#key-concepts)
- [Implementation](#implementation)
- [Running Tests](#running-tests)
- [Test Categorization Guidelines](#test-categorization-guidelines)
- [Continuous Integration](#continuous-integration)
- [Best Practices](#best-practices)

## Introduction

The ATL/BTL (Above the Line/Below the Line) testing strategy divides tests into two distinct categories based on criticality:

1. **Above The Line (ATL)**: Critical tests that MUST pass with every build
2. **Below The Line (BTL)**: Important but non-blocking tests that can run separately

This division enables faster feedback cycles while maintaining comprehensive test coverage by prioritizing the most critical tests during development.

## Key Concepts

### Above the line (atl) tests

ATL tests have the following characteristics:
- **Fast**: Execute quickly to provide immediate feedback
- **Reliable**: Produce consistent results without flakiness
- **Critical**: Verify core functionality essential to the system
- **High Priority**: Block the build if failing

Examples include:
- Core tube functionality tests
- Critical business flows
- Key user journeys
- Identity and initialization tests

### Below the line (btl) tests

BTL tests have the following characteristics:
- **May be slower**: May take longer to execute
- **More complex**: Often test edge cases or rare scenarios
- **Lower priority**: Don't block the build if failing
- **Comprehensive**: Provide broader coverage of the system

Examples include:
- Edge cases and boundary tests
- Performance and stress tests
- Rare user scenarios
- Resource-intensive tests

## Implementation

Samstraumr implements the ATL/BTL strategy across both JUnit and Cucumber tests:

### Junit implementation

- **Annotations**:
  - `@ATL`: Marks tests as critical (preferred)
  - `@BTL`: Marks tests as important but non-blocking (preferred)
  - `@AboveTheLine`: Marks tests as critical (deprecated)
  - `@BelowTheLine`: Marks tests as important but non-blocking (deprecated)
- **Test Runners**:
  - `ATLTestRunner`: Executes only Above The Line tests
  - `BTLTestRunner`: Executes only Below The Line tests

### Cucumber implementation

- **Tags**:
  - `@ATL`: Marks scenarios as critical
  - `@BTL`: Marks scenarios as important but non-blocking
- **Runners**:
  - `RunATLCucumberTest`: Executes only Above The Line scenarios
  - `RunBTLCucumberTest`: Executes only Below The Line scenarios

### Maven profiles

- `atl-tests`: Runs only Above The Line tests
- `btl-tests`: Runs only Below The Line tests

## Running Tests

### Using the test runner script

```bash
# A T L B T L Strategy
./util/test/run-tests.sh atl

# A T L B T L Strategy
./util/test/run-tests.sh btl

# A T L B T L Strategy
./util/test/run-tests.sh --parallel atl

# A T L B T L Strategy
./util/test/run-tests.sh --skip-quality atl

## Test Categorization Guidelines

When categorizing tests as ATL or BTL, consider the following guidelines:

### Above the line (atl) tests should:

1. **Verify Core Functionality**: Test essential behaviors without which the system cannot function
2. **Run Quickly**: Complete in seconds rather than minutes
3. **Be Stable**: Rarely or never produce false failures
4. **Focus on Happy Paths**: Cover primary user flows and expected behavior
5. **Have Minimal Dependencies**: Avoid reliance on external systems

### Below the line (btl) tests should:

1. **Cover Edge Cases**: Test boundary conditions and unusual scenarios
2. **Be Comprehensive**: Fill coverage gaps left by ATL tests
3. **Test Performance**: Verify system behavior under stress or load
4. **Cover Complex Scenarios**: Test multi-step user journeys
5. **Test Integrations**: Verify interactions with external systems

## Continuous Integration

The ATL/BTL strategy is designed to optimize continuous integration:

- **Main Pipeline**: Runs ATL tests on every push and pull request
  - Fast feedback on critical issues
  - Blocks merges if critical tests fail

- **Secondary Pipeline**: Runs BTL tests in a separate process
  - Nightly builds or parallel jobs
  - Provides comprehensive coverage without blocking development
  - Reports issues that don't block the build

## Best Practices

1. **Start with ATL Tests**: Begin by writing critical ATL tests before BTL tests
2. **Maintain Balance**: Aim for a reasonable distribution between ATL and BTL tests
   - Too many ATL tests: Slow feedback loop
   - Too few ATL tests: Critical issues may be missed
3. **Review Categorization**: Periodically review test categorization to ensure it remains appropriate
4. **Document Reasoning**: When categorizing a test, document why it's ATL or BTL
5. **Fix Flaky Tests**: Address flaky tests promptly, especially in the ATL category
6. **Monitor Test Duration**: Keep ATL test suite execution time under 5 minutes for efficient CI
7. **Apply Across Levels**: Use ATL/BTL categorization at all testing levels (unit, integration, system)
8. **Dual Tagging**: Tests should have both a type tag (Tube, Bundle, etc.) and a criticality tag (ATL, BTL)

---

By following this ATL/BTL testing strategy, Samstraumr achieves a balance between fast feedback for critical functionality and comprehensive coverage for the entire system.

```
