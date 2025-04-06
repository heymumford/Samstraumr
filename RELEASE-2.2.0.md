# Samstraumr 2.2.0 Release Notes

## Overview

This release includes significant improvements to the repository structure, build system, and test infrastructure. It focuses on cleaning up technical debt, improving code organization, and enhancing the testing framework.

## Key Improvements

### Enhanced Testing Infrastructure
- Dedicated test runners for different test types (component, unit, machine, ATL)
- Improved Cucumber integration documentation
- Java 21 compatibility fixes for reflection operations
- JaCoCo coverage integration for better test insights

### Repository Cleanup
- Removal of redundant backup directories
- Standardized organization following project guidelines
- Consolidated duplicate scripts into shared libraries
- Enhanced .gitignore to prevent buildup of temporary files

### Build System
- Fixed test skipping issues in Maven profiles
- Improved profile alignment between scripts and POM files
- Enhanced Maven settings for consistent test execution

### Documentation
- Added research on testing in the age of AI
- Comprehensive cleanup plan for further improvements
- Detailed documentation for Cucumber integration

### CI Pipeline
- Improved pipeline with new test runners
- Enhanced version detection using s8r-version tool
- Added component test execution to the workflow

## Installation

For development:
```bash
git clone https://github.com/heymumford/Samstraumr.git
cd Samstraumr
./s8r-build
```

## Usage

The improved test framework makes it easier to run specific test types:

```bash
# Run unit tests
./s8r-test unit

# Run component tests
./s8r-test component

# Run with coverage analysis
./s8r-test unit --coverage
```

## Future Development

The groundwork laid in this release will enable faster development and more reliable testing going forward. The consolidated script libraries and improved organization reduce technical debt and make the codebase more maintainable.

Please refer to the [full changelog](https://github.com/heymumford/Samstraumr/blob/main/docs/reference/release/changelog.md) for more details.