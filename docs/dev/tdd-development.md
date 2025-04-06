<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

<\!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# TDD Development Guide

## Current Development Approach

Samstraumr is currently in a Test-Driven Development (TDD) phase, focusing on building functionality incrementally with tests guiding implementation.

## What is TDD?

Test-Driven Development is an approach where you:

1. Write a failing test that describes a desired behavior
2. Implement the minimum code needed to make the test pass
3. Refactor the code to improve design while maintaining test coverage
4. Repeat for the next feature or requirement

## Current CI Pipeline

The CI pipeline has been simplified to support rapid TDD iterations:

- Basic compilation is checked without running tests
- Project structure verification is performed
- Quality checks are disabled to allow faster development cycles
- Full test suites are not enforced to encourage incremental development

## Using Maven with TDD Settings

By default, quality checks are disabled when building the project to allow for rapid iteration:

```bash
# Default build - quality checks are skipped
mvn clean compile
```

To explicitly use the TDD profile:

```bash
mvn clean compile -Ptdd-development
```

To activate quality checks when needed:

```bash
mvn clean compile -Pquality-checks
```

## Running Tests During TDD

While developing, you can run specific tests to validate your implementation:

```bash
# Run a specific test class
mvn test -Dtest=MyComponentTest

# Run all tests in a package
mvn test -Dtest="org.samstraumr.component.*Test"
```

## Best Practices for TDD in Samstraumr

1. **Start with interfaces** - Define APIs before implementation
2. **Write minimal test cases** - Cover one aspect of behavior per test
3. **Implement in small increments** - Make a test pass before adding more functionality
4. **Refactor continuously** - Improve design as the code evolves
5. **Keep test runs fast** - Fast feedback loops improve development efficiency
6. **Commit frequently** - Small, focused commits make collaboration easier

## When to Apply Quality Checks

While quality checks are disabled by default during the TDD phase, it's recommended to periodically run them to ensure code quality:

```bash
# Run all quality checks
mvn verify -Pquality-checks

# Run just spotless formatting
mvn spotless:check

# Run just checkstyle
mvn checkstyle:check
```

## Returning to Full CI

When the development phase stabilizes, the CI pipeline will be restored to include:

- Full test suite execution
- Code quality checks
- Coverage requirements
- Comprehensive build reports

Until then, focus on writing tests and implementing functionality incrementally.
