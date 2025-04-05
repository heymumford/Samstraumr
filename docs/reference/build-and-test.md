# Build and Test System

The Samstraumr build and test system is designed to be simple, efficient, and maintainable. It provides a straightforward interface for common build and test operations while abstracting away the complexity of the underlying Maven configuration.

## Unified Command Interface

Samstraumr provides a single entry point for all operations through the `s8r` script.

### Usage

```bash
./s8r <command> [options] [arguments]
```

### Core Commands

- `build`: Build the project with various modes
- `test`: Run tests of different types
- `version`: Manage version information
- `coverage`: Run and manage code coverage
- `clean`: Clean build artifacts

### Global Options

- `--watch, -w`: Watch mode (continuous execution)
- `--parallel, -p`: Execute in parallel where supported
- `--help, -h`: Show help information

### Command Chaining

The system supports command chaining for common operations:

```bash
# Build and test in one command
./s8r build-test unit
```

## Build System

The build system is accessible through the `s8r build` command, providing a simple interface to the Maven build system.

### Usage

```bash
./s8r build [options] [mode]
```

### Modes

- `fast`: Quick compilation with tests and quality checks skipped (default)
- `test`: Build with tests but skip quality checks
- `package`: Build a JAR package with tests and quality checks
- `install`: Install the package to local Maven repository
- `compile`: Compile only
- `full`: Full build including tests, quality checks, and verification
- `docs`: Generate project documentation
- `site`: Generate the Maven site

### Options

- `-c, --clean`: Perform a clean build
- `-p, --parallel`: Build in parallel where possible
- `-v, --verbose`: Enable verbose output
- `--skip-quality`: Skip quality checks (automatically included in 'fast' mode)
- `-h, --help`: Display help

### Examples

```bash
# Quick compilation without tests
./s8r build

# Clean build with tests
./s8r build --clean test

# Parallel build for faster execution
./s8r build --parallel full

# Watch mode for continuous compilation during development
./s8r build --watch
```

## Test System

The test system is accessible through the `s8r test` command, providing a simple interface to run different types of tests.

### Usage

```bash
./s8r test [options] [test-type]
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
- `adam`: Run Adam Tube tests
- `orchestration`: Run orchestration tests

### Options

- `-v, --verbose`: Run with verbose output
- `-p, --parallel`: Run tests in parallel (where supported)
- `--coverage`: Run tests with code coverage analysis
- `--skip-quality`: Skip quality checks
- `-o, --output <file>`: Write test output to file
- `-h, --help`: Display help

### Examples

```bash
# Run unit tests
./s8r test unit

# Run component tests in parallel
./s8r test --parallel component

# Run tests with code coverage analysis
./s8r test --coverage all

# Continuous testing with watch mode
./s8r test --watch unit
```

## Coverage System

The coverage system is accessible through the `s8r coverage` command, providing tools for code coverage analysis.

### Usage

```bash
./s8r coverage <command> [options]
```

### Commands

- `report`: Generate coverage report (default)
- `run [test-type]`: Run tests with coverage enabled

### Examples

```bash
# Generate coverage report
./s8r coverage

# Run unit tests with coverage
./s8r coverage run unit
```

## Simplified Architecture

The build and test system has been significantly simplified from the previous implementation:

1. **Unified Command Interface**: Single entry point (`s8r`) for all operations
2. **Command Composability**: Commands can be naturally combined in sequences
3. **Convention Over Configuration**: Sensible defaults reduce the need for complex configuration
4. **Progressive Disclosure**: Simple commands for common tasks, advanced options when needed
5. **Modern Features**: Watch mode, parallel execution, and code coverage integration

This approach follows software engineering best practices including:
- Single Responsibility Principle
- Encapsulation
- Command-Query Separation
- Don't Repeat Yourself (DRY)

## Improvements Over Previous System

The new build and test system represents a significant improvement over the previous implementation:

- **Code Reduction**: Reduced from 2,600+ lines across 14+ files to ~412 lines in 2 files (84% reduction)
- **Unified Interface**: Single `s8r` entry point for all operations
- **Enhanced Features**:
  - Parallel build and test execution
  - Code coverage analysis and reporting
  - Watch mode for continuous development
  - Command chaining for common workflows
- **Improved Usability**:
  - Consistent command structure
  - Color-coded status messages
  - Execution timing information
  - Better error handling and reporting
- **Comprehensive Documentation**: Clear examples and detailed explanations

## Integration with Version Management

The system provides seamless integration between build, test, and version management:

```bash
# Update version and build
./s8r version bump minor
./s8r build package

# Verify version consistency
./s8r version fix

# Combined operations
./s8r build-test unit
./s8r coverage run integration
```

These features together provide a comprehensive, yet simple system for building, testing, and versioning the Samstraumr project.