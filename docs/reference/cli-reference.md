<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Samstraumr CLI Reference

The S8r command-line interface provides a unified way to interact with the Samstraumr framework for tasks like building, testing, and managing versions.

## Command Structure

```bash
./s8r <command> [options]
```

Or use the longer form:

```bash
./util/samstraumr <command> [options]
```

## Available Commands

### Build Commands

Build the project with various configurations:

```bash
./s8r build [mode] [options]
```

Modes:
- `fast`: Fast build with quality checks skipped (default)
- `compile`: Compile only
- `test`: Compile and run tests
- `package`: Create JAR package
- `install`: Install to local repository

Options:
- `-c, --clean`: Clean before building
- `-p, --profile <profile>`: Use specific Maven profile
- `--skip-quality`: Skip quality checks

Examples:

```bash
./s8r build                   # Fast build
./s8r build test              # Run tests
./s8r build -c test           # Clean and run tests
./s8r build -p atl-tests test # Run tests with ATL profile
```

### Test Commands

Run various types of tests:

```bash
./s8r test <test-type> [options]
```

Test types:
- Industry Standard:
- `smoke` (basic system assembly and connectivity)
- `unit` (individual units in isolation)
- `component` (connected components working together)
- `integration` (interactions between different parts)
- `api` (public interfaces and contracts)
- `system` (entire system as a whole)
- `endtoend` (user perspective and requirements)
- `property` (system properties across inputs)
- Samstraumr Terminology:
- `orchestration` (basic system assembly and connectivity)
- `tube` (individual tubes in isolation)
- `composite` (or `bundle` for legacy) (connected tubes)
- `flow` (interactions between different parts)
- `machine` (public interfaces and contracts)
- `stream` (entire system as a whole)
- `acceptance` (user perspective and requirements)
- `adaptation` (system properties across inputs)
- Special test types:
- `all` (run all tests)
- `atl` (Above-The-Line critical tests)
- `adam` (Adam tube identity tests)

Options:
- `-b, --both`: Include equivalent tags (e.g., run both unit and tube tests)
- `-c, --clean`: Clean before running tests
- `-o, --output <file>`: Write test output to file
- `-p, --profile <profile>`: Use specific Maven profile
- `-v, --verbose`: Show verbose output with detailed status
- `--skip-quality`: Skip quality checks
- `--cyclename <name>`: Specify a name for the test cycle (for reporting)

Examples:

```bash
./s8r test unit                # Run unit tests
./s8r test --both unit         # Run unit and tube tests
./s8r test -c tube             # Clean and run tube tests
./s8r test -v adam             # Run Adam tube tests with verbose output
```

### Version Commands

Manage project versioning:

```bash
./s8r version <command> [options]
```

Commands:
- `get`: Show current version information
- `export`: Output only the current version (for scripts)
- `bump <type>`: Bump version (type: major, minor, patch)
- `set <version>`: Set a specific version (format: x.y.z)
- `verify`: Verify that version and tag are in sync
- `fix-tag`: Create a git tag matching the current version
- `test <type>`: Bump version, run tests, then commit and tag
- `history`: Show version history
- `diagnose`: Run diagnostics to identify and fix version issues

Examples:

```bash
./s8r version get              # Show current version
./s8r version bump patch       # Bump patch version (bug fixes)
./s8r version set 1.5.0        # Set version to 1.5.0
./s8r version test patch       # Bump patch, test, commit, tag
```

### Quality Check Commands

Validate code quality:

```bash
./s8r quality <command> [options]
```

Commands:
- `check`: Run all quality checks
- `spotless`: Run Spotless formatting check
- `checkstyle`: Run CheckStyle code style check
- `spotbugs`: Run SpotBugs bug detection
- `jacoco`: Run JaCoCo code coverage analysis
- `encoding`: Check file encodings and line endings

Options:
- `-v, --verbose`: Show detailed output
- `-f, --fix`: Fix issues automatically (where applicable)

Examples:

```bash
./s8r quality check            # Run all quality checks
./s8r quality spotless -f      # Run Spotless and fix issues
./s8r quality encoding -v      # Check encodings with verbose output
```

### Documentation Commands

Generate documentation:

```bash
./s8r docs [output-dir] [format]
```

Arguments:
- `output-dir`: Optional output directory (default: target/docs)
- `format`: Optional output format (default: pdf)

Supported formats:
- `pdf`: Adobe PDF format
- `docx`: Microsoft Word format
- `html`: HTML format for web viewing

Examples:

```bash
./s8r docs                         # Generate PDF docs in target/docs
./s8r docs ./my-docs               # Generate PDF docs in ./my-docs
./s8r docs ./my-docs docx          # Generate DOCX docs in ./my-docs  
```

## Help System

Get help on any command:

```bash
./s8r help                # General help
./s8r help <command>      # Help for specific command
```

## Environment Variables

The CLI supports several environment variables:

- `S8R_HOME`: Base directory for S8r installation
- `S8R_CONFIG`: Path to configuration file
- `S8R_LOG_LEVEL`: Logging level (debug, info, warn, error)
- `S8R_MAVEN_OPTS`: Custom Maven options
- `DOCMOSIS_KEY`: License key for documentation generation

## Configuration

The CLI uses configuration files from multiple locations:

1. `.samstraumr.config`: Project-level configuration
2. `.s8r/config.json`: User-level configuration
3. Environment variables

## Return to Main Documentation

For a complete overview of the Samstraumr framework, see the [Main README](../../README.md).
