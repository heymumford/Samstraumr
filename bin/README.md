# Samstraumr Script Consolidation

This directory contains consolidated scripts for the Samstraumr project, designed to reduce redundancy, improve maintainability, and ensure consistent behavior across the codebase.

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
- Functional: `functional`, `error-handling`, `dataflow`, `state`, etc.
- Legacy: `tube`, `composite`, `machine`, `atl`, `btl`, etc.

**Options:**
- `-v, --verbose` - Enable verbose output
- `-p, --parallel` - Run tests in parallel
- `--coverage` - Run with code coverage
- `--skip-quality` - Skip quality checks
- `-o, --output <file>` - Write output to file
- `--tags <expression>` - Run tests with specific tags

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
