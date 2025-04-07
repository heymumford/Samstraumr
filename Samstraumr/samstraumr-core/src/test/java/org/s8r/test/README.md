<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Test Package

## Purpose

This package contains all test-related classes for the Samstraumr framework, including test runners, step definitions, and test utilities. It provides a flattened, organized structure for test code that adheres to our maximum directory depth guidelines.

## Structure

The test package is organized by component type, not by implementation details, to reduce nesting:

| Directory | Purpose |
|-----------|---------|
| `/tube` | Test classes for Tube component functionality |
| `/component` | Test classes for new Component model functionality |
| `/legacy` | Test classes for legacy component adapters |

## File Naming Conventions

To support the flattened structure, test files follow these naming conventions:

* Test Runners: `[Component]TestRunner.java`
* Step Definitions: `[Component][Feature]Steps.java`
* Test Utilities: `[Component]TestUtils.java`

## Test Organization

Rather than using deep directory nesting, we use clear class names and package structure to organize tests:

* Component-focused tests are in `org.s8r.test.component`
* Tube-focused tests are in `org.s8r.test.tube`
* Legacy adapter tests are in `org.s8r.test.legacy`

## Related Directories

* Feature files: `/src/test/resources/features/`
* Integration tests: `/src/test/java/org/s8r/test/integration/`
* Test utilities: `/src/test/java/org/s8r/test/util/`