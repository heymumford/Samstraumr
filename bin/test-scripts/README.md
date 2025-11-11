# Test Scripts

This directory contains scripts used for testing specific components of the Samstraumr framework. These scripts are primarily used for development and verification purposes.

## Scripts

- `s8r-acceptance-tests.sh` - Runs acceptance tests
- `s8r-component-tests.sh` - Runs component-level tests
- `s8r-composite-tests.sh` - Runs composite-level tests
- `s8r-machine-tests.sh` - Runs machine-level tests
- `s8r-run-tests.sh` - Main test runner script
- `s8r-system-tests.sh` - Runs system-level tests
- `s8r-test-framework.sh` - Test framework utilities
- `s8r-unit-tests.sh` - Runs unit tests
- `test-cli-short.sh` - Quick CLI tests
- `test-help.sh` - Tests help system
- `test-machine-adapter.sh` - Tests machine adapter functionality
- `test-one.sh` - Runs a single test
- `verify-cli.sh` - Verifies CLI functionality

## Usage

These scripts should be used from the bin directory or via aliases set up by the `create-aliases.sh` script.

Example:
```bash
# Run component tests
./bin/test-scripts/s8r-component-tests.sh

# Run a single test
./bin/test-scripts/test-one.sh TestClass#testMethod
```
