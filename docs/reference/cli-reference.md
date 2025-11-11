<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Cli Reference

The S8r command-line interface provides a unified way to interact with the Samstraumr framework for tasks like building, testing, and managing versions.

## Command Structure

```bash
./s8r <command> [options]
```

Or use the longer form:

```bash
./util/scripts/samstraumr <command> [options]
```

## Available Commands

### Model management commands

#### Initialize a new model

```bash
./s8r init [options] [target-directory]
```

Options:
- `-p, --package <n>`: Specify a custom package name (default: org.example)
- `-n, --name <n>`: Specify a model name
- `-v, --verbose`: Enable verbose output
- `-d, --debug`: Enable debug output (additional details)

Examples:
```bash
./s8r init                                  # Initialize in current directory
./s8r init ~/new-model                      # Initialize in ~/new-model
./s8r init -p com.example.model             # Use custom package name
./s8r init -n "Customer Data Model" -p com.example.customer  # Set model name and package
```

#### Visualize model structure

```bash
./s8r list [options] [directory]
```

Options:
- `-f, --format <format>`: Output format: ascii, tree, json (default: ascii)
- `-d, --detailed`: Show detailed component information
- `-v, --verbose`: Enable verbose output

Examples:
```bash
./s8r list                  # List model in current directory
./s8r list ~/my-model       # List model in specified directory
./s8r list -f tree          # Display as tree structure
./s8r list -d               # Show detailed information
```

#### Component management

```bash
./s8r component <subcommand> [arguments]
```

Subcommands:
- `create`: Create a new component
- `list`: List all components
- `info`: Show details about a component
- `delete`: Delete a component

Examples:
```bash
./s8r component create --type transformer DataProcessor
./s8r component list --format json
```

#### Composite management

```bash
./s8r composite <subcommand> [arguments]
```

Subcommands:
- `create`: Create a new composite
- `list`: List all composites
- `add`: Add a component to a composite
- `connect`: Connect components within a composite
- `info`: Show details about a composite
- `delete`: Delete a composite

Examples:
```bash
./s8r composite create --type processing DataFlow
./s8r composite add --component DataProcessor --composite DataFlow
```

#### Machine management

```bash
./s8r machine <subcommand> [arguments]
```

Subcommands:
- `create`: Create a new machine
- `list`: List all machines
- `add`: Add a composite to a machine
- `connect`: Connect composites within a machine
- `info`: Show details about a machine
- `delete`: Delete a machine

Examples:
```bash
./s8r machine create --template flow DataPipeline
./s8r machine add --composite DataFlow --machine DataPipeline
```

### Build commands

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
- `--ci`: Run local CI checks after build
- `-v, --verbose`: Enable verbose output
- `-p, --parallel`: Build in parallel where possible

Examples:

```bash
./s8r build                   # Fast build
./s8r build test              # Run tests
./s8r build -c test           # Clean and run tests
./s8r build -p atl-tests test # Run tests with ATL profile
./s8r build --ci              # Fast build with local CI check
./s8r build -v -p test        # Run tests in parallel with verbose output
```

### Test commands

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
- `--cyclename <n>`: Specify a name for the test cycle (for reporting)

Examples:

```bash
./s8r test unit                # Run unit tests
./s8r test --both unit         # Run unit and tube tests
./s8r test -c tube             # Clean and run tube tests
./s8r test -v adam             # Run Adam tube tests with verbose output
```

### Version commands

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

### Quality check commands

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

### Ci commands

Run GitHub Actions workflows locally:

```bash
./s8r-ci [options]
```

Options:
- `-w, --workflow FILE`: Workflow file to run (default: samstraumr-pipeline.yml)
- `-j, --job JOB_ID`: Specific job to run (default: all jobs)
- `-e, --event EVENT`: Event type to trigger (default: push)
- `-d, --dry-run`: Show what would be run without executing
- `-v, --verbose`: Enable verbose output

Examples:

```bash
./s8r-ci                        # Run the default CI pipeline
./s8r-ci -j basic-verification  # Run only the basic verification job
./s8r-ci -w smoke-test.yml      # Run the smoke test workflow
./s8r-ci -e pull_request        # Trigger as pull_request event
./s8r-ci --dry-run              # Show what would be run without executing
```

The CI tool uses [nektos/act](https://github.com/nektos/act) to run GitHub Actions workflows locally. This enables you to:
- Test CI pipelines before pushing to GitHub
- Debug workflow issues locally
- Validate workflow configuration changes
- Speed up CI development cycle

You can also run CI checks after building:

```bash
./s8r build fast --ci           # Fast build followed by local CI check
```

### Documentation commands

#### Document generation

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

#### Documentation integrity

Check and maintain documentation integrity:

```bash
./s8r-docs <command>
```

Commands:
- `check`: Run documentation integrity check (interactive)
- `fix`: Automatically fix documentation issues
- `report`: Generate documentation integrity report

Examples:

```bash
./s8r-docs check        # Check documentation integrity
./s8r-docs fix          # Automatically fix documentation issues
./s8r-docs report       # Generate documentation integrity report only
```

This tool performs several checks on documentation files:
- Verifies all links in markdown files reference existing files
- Ensures consistency in section references
- Validates that key sections and document structures are consistent
- Checks for package reference consistency
- Validates code examples use correct package names
- Ensures consistent header format with copyright notices
- Checks for kebab-case filenames in markdown files
- Validates header hierarchy and conventions

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

## Script Organization

Starting from version 2.7.1, the CLI scripts are organized into a proper bin directory structure for better maintainability and adherence to standard practices.

### Bin Directory Organization

All executables are organized by functionality in the `bin` directory:

```bash
bin/
   core/           # Core framework executables (s8r, s8r-init, s8r-list)
   build/          # Build-related executables (s8r-build, s8r-ci)
   test/           # Testing executables (s8r-test, s8r-test-cli)
   component/      # Component-related executables (s8r-component, s8r-composite, s8r-machine, s8r-tube)
   dev/            # Development executables (s8r-dev, s8r-dev-help-build)
   migration/      # Migration tools (s8r-migration-monitor, s8r-architecture-verify)
   help/           # Help executables (s8r-help, s8r-help-build, s8r-help-component)
   utils/          # Utility executables (s8r-docs, s8r-quality, s8r-version)
   ai/             # AI-related executables (s8r-ai-test)
   setup-path.sh   # Script to add bin directory to PATH
   create-aliases.sh # Script to create shell aliases for executables
   remove-root-scripts.sh # Script to safely remove root directory scripts
   setup-bin-directory.sh # Script to set up the bin directory structure
   s8r-scripts-version # Script to manage script versions (original vs. new)
   README.md       # Documentation for bin directory structure and usage
```

These are the canonical executables for the project. They are not symlinks or copies of files in the root directory - they ARE the primary executables that should be used for all operations.

### Setting Up Your Environment

You have three options for using the Samstraumr executables:

#### Option 1: Add the bin directory to your PATH (Recommended)

```bash
# Run the setup script
./bin/setup-path.sh

# Then activate in current session
source ~/.bashrc   # or ~/.zshrc, etc. depending on your shell
```

This will add the bin directory to your shell configuration and provide instructions for activating it in your current session.

#### Option 2: Create shell aliases

```bash
# Run the aliases script
./bin/create-aliases.sh

# Then activate in current session
source ~/.bashrc   # or ~/.zshrc, etc. depending on your shell
```

This will create aliases for all scripts in your shell configuration, making them accessible from anywhere.

#### Option 3: Use full paths

```bash
# You can always use the full path to run a script
/path/to/Samstraumr/bin/core/s8r
/path/to/Samstraumr/bin/build/s8r-build
```

### Removing Root Directory Scripts

For a cleaner repository structure, after setting up your PATH or aliases, you should remove the deprecated scripts from the root directory:

```bash
# Run the removal script
bin/remove-root-scripts.sh
```

The script will:
1. Verify that the bin directory is in your PATH or aliases are set up
2. Check that all scripts have corresponding executables in the bin directory
3. Safely remove the root directory scripts

### Script Version Management

Some scripts have both original and new versions (e.g., s8r-build and s8r-build-new). To manage these versions, use:

```bash
# List available script versions
bin/s8r-scripts-version list

# Switch to new versions of scripts
bin/s8r-scripts-version use-new

# Switch back to original versions
bin/s8r-scripts-version use-old
```

### Utility Scripts

Development-specific utility scripts are located in the `util/scripts` directory:

```bash
util/scripts/
   archived/       # Archived scripts that are no longer actively used
   docs/           # Documentation about scripts
   ...other utility scripts
```

### Bin Directory Setup

If you need to recreate or update the bin directory structure:

```bash
# Setup or update the bin directory structure
bin/setup-bin-directory.sh
```

This script:
1. Creates all necessary subdirectories
2. Copies scripts from the root directory to appropriate subdirectories
3. Creates management scripts (setup-path.sh, create-aliases.sh, etc.)
4. Updates the bin directory README

### Legacy Script Support (Deprecated)

The scripts previously located in the root directory are deprecated and will be removed in a future version. Please update your workflows to use the bin directory executables instead.

All documentation, CI/CD pipelines, and personal workflows should be updated to reference the bin directory executables rather than the root directory scripts.

## Return to Main Documentation

For a complete overview of the Samstraumr framework, see the [Main README](../../readme.md).