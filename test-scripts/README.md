# Samstraumr Test Scripts

This directory contains scripts for testing the Samstraumr CLI framework.

## Test Framework

The test framework consists of the following components:

- `s8r-test-framework.sh`: Core testing utilities and functions
- `s8r-run-tests.sh`: Main test runner that orchestrates all test types
- `s8r-unit-tests.sh`: Unit tests for CLI script existence, permissions, and help text
- `s8r-component-tests.sh`: Tests for component-level commands
- `s8r-composite-tests.sh`: Tests for composite-level commands  
- `s8r-machine-tests.sh`: Tests for machine-level commands
- `s8r-system-tests.sh`: End-to-end system tests
- `s8r-acceptance-tests.sh`: Acceptance tests for validation requirements

## Running Tests

Tests can be run from the project root using the symlink:

```bash
./s8r-run-tests.sh [options]
```

Available options:
- `--unit`: Run unit tests only
- `--component`: Run component tests only  
- `--composite`: Run composite tests only
- `--machine`: Run machine tests only
- `--system`: Run system tests only
- `--acceptance`: Run acceptance tests only (validation tests)
- `--all`: Run all tests (default)
- `--verbose`: Enable verbose output

## Acceptance Tests

The acceptance tests document validation requirements for the system. Many of these tests are expected to fail until the corresponding validation features are implemented. These tests serve as both documentation of requirements and verification that they've been implemented correctly.