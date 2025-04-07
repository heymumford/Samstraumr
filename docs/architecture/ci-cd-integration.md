# Ci Cd Integration

*Date: 2025-04-07*

## Overview

This document describes how Clean Architecture compliance is verified as part of the CI/CD pipeline in the Samstraumr project. The verification ensures that all code changes adhere to the architectural principles defined in [ADR-0003: Adopt Clean Architecture for System Design](./decisions/0003-adopt-clean-architecture-for-system-design.md).

## Implementation

The Clean Architecture verification is integrated into the CI/CD pipeline through the following components:

1. **Architecture Verification Scripts**
   - `s8r-architecture-verify`: A standalone script that runs architecture compliance tests
   - `run-architecture-tests.sh`: An existing script for running architecture tests locally

2. **CI/CD Workflow Integration**
   - `.github/workflows/samstraumr-pipeline.yml`: The main CI/CD pipeline
   - `.github/workflows/local-pipeline.yml`: A simplified pipeline for local testing

3. **Architecture Test Classes**
   - `CleanArchitectureComplianceTest.java`: Core tests for Clean Architecture compliance
   - `AcyclicDependencyTest.java`: Tests for verifying absence of circular dependencies
   - Additional architecture test classes for specific architectural decisions

## Architecture Tests

The architecture tests verify compliance with several key principles:

1. **Dependency Rule** - Inner layers don't depend on outer layers
   - Domain layer must not import from application, infrastructure, or adapter layers
   - Application layer must not import from infrastructure or adapter layers

2. **Layer Structure** - Required layers must exist and follow expected structure
   - Domain, application, infrastructure, and adapter layers must exist
   - Each layer must have expected packages and components

3. **Component Responsibility** - Components must be in correct layers
   - Entities must be in domain layer
   - Use cases must be in application layer
   - Interface adapters must be in infrastructure layer

4. **Dependency Inversion** - Interfaces defined in inner layers, implemented in outer layers
   - Port interfaces must be defined in application layer
   - Adapters implementing ports must be in infrastructure layer

## Usage

### In ci/cd pipeline

The Clean Architecture verification is automatically run as part of the CI/CD pipeline. The verification process:

1. Runs after basic verification (compilation, linting, unit tests)
2. Executes core architecture compliance tests
3. Generates a report with results and recommendations
4. Fails the pipeline if compliance issues are detected

### Local verification

Developers can run the verification locally using:

```bash
# Ci Cd Integration
./s8r-architecture-verify

# Ci Cd Integration
./s8r-architecture-verify --quick

# Ci Cd Integration
./s8r-ci --arch
```

## Report Generation

Each verification run generates a report (`architecture-report.md`) that includes:

- Summary of compliance status (pass/fail)
- List of principles verified
- Recommendations for resolving any issues
- Test execution details

The report is uploaded as an artifact in the CI/CD workflow, making it accessible for review after pipeline execution.

## Benefits

Integration of Clean Architecture verification into the CI/CD pipeline provides:

1. **Early Detection** - Architecture violations are caught before merge
2. **Consistent Enforcement** - All PRs are verified against the same standards
3. **Documentation** - Generated reports provide clear guidance on issues
4. **Quality Assurance** - Prevents architectural erosion over time

## Future Enhancements

Planned enhancements to the architecture verification process:

1. Add more detailed metrics on architecture compliance
2. Implement visual diagram generation from code analysis
3. Support for custom rule definitions through configuration files
