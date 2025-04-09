# Samstraumr Script Consolidation

This directory contains consolidated scripts for the Samstraumr project, designed to reduce redundancy, improve maintainability, and ensure consistent behavior across the codebase.

## Directory Structure

The scripts are organized into functional directories:

- `/bin` - Main executables and high-level scripts
- `/bin/test-utils` - Test-related utility scripts
- `/bin/xml-utils` - XML processing utilities
- `/bin/git-utils` - Git-related utilities
- `/bin/utils` - General utility scripts
- `/bin/s8r-all` - S8r consolidated utilities
- `/bin/test-scripts` - Additional test scripts

## File Organization Standards

The Samstraumr project follows these file organization standards:

1. **Hierarchical organization by function**
   - All executables and scripts are organized in appropriate directories by function
   - Each directory should contain only files related to its specific purpose
   - Files within each directory should follow consistent naming conventions

2. **Root directory clarity**
   - The project root directory should contain only essential files (README.md, LICENSE, pom.xml)
   - All utility scripts, configuration files, and other supporting files should be in appropriate subdirectories
   - Symbolic links may be created in the root for backward compatibility, but should be phased out

3. **Proper referencing**
   - Scripts should reference other scripts/resources using relative paths that respect the directory structure
   - No hardcoded absolute paths
   - No symlinks for internal functionality

4. **Documentation**
   - Each directory must have a README.md explaining its purpose and contents
   - All scripts should have a consistent header with description, usage, and copyright information

## Consolidated Scripts

| Script                    | Description                                       | Replaces                                      |
|---------------------------|---------------------------------------------------|-----------------------------------------------|
| `s8r-version`             | Unified version management                        | `s8r-version`, `s8r-version-robust`, `s8r-version-simple` |
| `s8r-build`               | Unified build tool                                | `s8r-build`, `s8r-build-new`                  |
| `s8r-test`                | Unified test runner                               | `s8r-test`, `s8r-test-new`                    |

## Shared Library

The scripts leverage a unified common library (`util/lib/unified-common.sh`) that provides:

- Consistent output formatting and color handling
- Standardized argument parsing
- Common file and path operations
- Maven integration functions
- Validation utilities
- System and Git utilities

## Script Features

### s8r-version

```bash
./s8r-version <command> [options]
```

**Commands:**
- `get` - Show current version
- `bump [type]` - Bump version (major, minor, patch)
- `set <version>` - Set specific version
- `fix` - Fix version inconsistencies
- `verify` - Verify version consistency

**Options:**
- `--simple` - Use simplified behavior with less error recovery
- `--verify` - Verify consistency after getting version
- `--verbose` - Show detailed information

### s8r-build

```bash
./s8r-build [options] [mode]
```

**Modes:**
- `fast` - Skip tests and quality checks (default)
- `test` - Run tests during build
- `package` - Create JAR package
- `install` - Install to local Maven repository
- `compile` - Compile only
- `full` - Full build with all checks
- `docs` - Generate documentation
- `site` - Generate Maven site

**Options:**
- `-c, --clean` - Clean before building
- `-v, --verbose` - Enable verbose output
- `-p, --parallel` - Build in parallel where possible
- `--skip-quality` - Skip quality checks
- `--ci` - Run local CI checks after build

### s8r-test

```bash
./s8r-test [options] [test-type]
```

**Test types:**
- Standard: `unit`, `component`, `integration`, `system`, `all`
- Functional: `functional`, `lifecycle`, `identity`, `error-handling`, `dataflow`, etc.
- Legacy: `tube`, `composite`, `machine`, `atl`, `btl`, etc.

**Options:**
- `-v, --verbose` - Enable verbose output
- `-p, --parallel` - Run tests in parallel
- `--coverage` - Run with code coverage
- `--skip-quality` - Skip quality checks
- `-o, --output <file>` - Write output to file
- `--tags <expression>` - Run tests with specific tags
- `--verify` - Verify test suite structure
- `--fix` - Fix common test issues
- `--report` - Generate test verification report

### Specialized Test Scripts

The project includes specialized test scripts for specific test categories:

#### s8r-test-lifecycle

```bash
./s8r-test-lifecycle [options] [mode]
```

**Modes:**
- `states` - Test state-dependent behavior only (default)
- `transitions` - Test state transitions only
- `resources` - Test resource management only
- `negative` - Test error handling and exceptional conditions
- `comprehensive` - Run all lifecycle tests

**Options:**
- `-v, --verbose` - Show detailed output
- `-o, --output` - Write results to file
- `-d, --dir` - Specify custom output directory
- `-q, --quality` - Run with quality checks (slower)
- `-h, --help` - Show help

## Installation

Use the `consolidate-scripts.sh` script to install the consolidated versions:

```bash
./bin/consolidate-scripts.sh
```

This will:
1. Back up the original scripts
2. Install the consolidated versions
3. Create symlinks for backward compatibility

To see available options:

```bash
./bin/consolidate-scripts.sh --help
```

## Reverting Changes

If you need to revert to the original scripts, the consolidation script creates a timestamped backup in `backup/scripts/YYYYMMDD_HHMMSS`.

To restore from backup:

```bash
cp -r /path/to/backup/scripts/* /path/to/project
```
