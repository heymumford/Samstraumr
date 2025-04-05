# Build and Test System

The Samstraumr build and test system is designed to be simple, efficient, and maintainable. It provides a straightforward interface for common build and test operations while abstracting away the complexity of the underlying Maven configuration.

## Build System

The build system is encapsulated in the `s8r-build` script, which provides a simple interface to the Maven build system.

### Usage

```bash
./s8r-build [options] [mode]
```

### Modes

- `fast`: Quick compilation with tests and quality checks skipped (default)
- `test`: Build with tests but skip quality checks
- `quality`: Build with tests and quality checks
- `package`: Build a JAR package with tests and quality checks
- `install`: Install the package to local Maven repository
- `docs`: Generate project documentation
- `site`: Generate the Maven site
- `full`: Full build including tests, quality checks, and site generation

### Options

- `-c, --clean`: Perform a clean build
- `-q, --skip-quality`: Skip quality checks (automatically included in 'fast' mode)
- `-h, --help`: Display help

### Examples

```bash
# Quick compilation without tests
./s8r-build

# Clean build with tests
./s8r-build --clean test

# Full build with documentation
./s8r-build full
```

## Test System

The test system is encapsulated in the `s8r-test` script, which provides a simple interface to run different types of tests.

### Usage

```bash
./s8r-test [options] [test-type]
```

### Test Types

- `unit`: Run unit and tube tests (fast, focused tests)
- `component`: Run component and composite tests
- `integration`: Run integration tests
- `machine`: Run machine-level tests
- `system`: Run system-level tests
- `flow`: Run data flow tests
- `adaptation`: Run adaptation tests
- `all`: Run all tests
- `acceptance`: Run acceptance tests
- `atl`: Run Above-The-Line tests
- `btl`: Run Below-The-Line tests
- `critical`: Run critical path tests
- `smoke`: Run smoke tests
- `api`: Run API tests
- `performance`: Run performance tests

### Options

- `-c, --clean`: Clean before running tests
- `-v, --verbose`: Run with verbose output
- `-f, --failfast`: Stop at first failure
- `-h, --help`: Display help

### Examples

```bash
# Run unit tests
./s8r-test unit

# Run clean build with component tests
./s8r-test --clean component

# Run all tests with verbose output
./s8r-test -v all
```

## Simplified Architecture

The build and test system has been significantly simplified from the previous implementation:

1. **Single Script Interface**: Each system is encapsulated in a single, well-documented script
2. **Convention Over Configuration**: Sensible defaults reduce the need for complex configuration
3. **Separation of Concerns**: Build and test functionality are separated
4. **Consistent Structure**: Both scripts follow similar patterns for ease of learning
5. **Minimal Dependencies**: Only depends on standard bash and Maven

This approach follows software engineering best practices including:
- Single Responsibility Principle
- Encapsulation
- Command-Query Separation
- Don't Repeat Yourself (DRY)

## Improvements Over Previous System

The new build and test system represents a significant improvement over the previous implementation:

- Reduced from 2,600+ lines across 14+ files to ~412 lines in 2 files (84% reduction)
- Simplified command interface with intuitive options
- Improved error handling and reporting
- Consistent output formatting with color-coded status messages
- Better documentation and examples
- Fixed dependency version issues in POM files

## Integration with Version Management

The build system is designed to work seamlessly with the `s8r-version` script for version management:

```bash
# Update version and build
./s8r-version bump minor
./s8r-build package

# Verify version consistency
./s8r-version fix
```

These tools together provide a comprehensive, yet simple system for building, testing, and versioning the Samstraumr project.