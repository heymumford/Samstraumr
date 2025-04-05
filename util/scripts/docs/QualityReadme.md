<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Quality Check Scripts

This directory contains scripts for quality checks and fixes in the Samstraumr project.

## Available Scripts

### `build-checks.sh`

Runs all quality checks configured in the project. This script has been enhanced to support running quality tools incrementally.

```bash
# Run all quality checks
./build-checks.sh

# Skip specific checks
./build-checks.sh --skip-spotless --skip-spotbugs

# Run only specific checks
./build-checks.sh --only=spotless,checkstyle

# Show help
./build-checks.sh --help
```

### `enable-quality-tools.sh`

An interactive script that helps you enable quality tools one by one, allowing you to fix issues incrementally.

```bash
# Start the interactive quality tool enablement wizard
./enable-quality-tools.sh
```

### `check-encoding.sh`

Verifies file encoding and line endings across the project.

```bash
./check-encoding.sh [options]
```

Options:
- `--verbose`: Show detailed output
- `--fix`: Automatically fix encoding and line ending issues

### `skip-quality-build.sh`

Runs the build with all quality checks skipped.

```bash
./skip-quality-build.sh
```

### `fix-pmd.sh`

Fixes PMD-related issues. This is mainly for compatibility as PMD has been replaced with SonarQube.

```bash
./fix-pmd.sh
```

## Fix Scripts Subdirectory

The `fix-scripts/` subdirectory contains specific scripts to fix various issues:

- `fix-logger.sh`: Fixes logger variables in the PatternSteps.java file
- `fix-pmd.sh`: Fixes PMD compatibility issues

## Quality Checks Overview

The Samstraumr project uses multiple quality tools:

1. **Spotless**: Code formatting using Google Java Style
2. **Checkstyle**: Style checking based on Google's style
3. **SpotBugs**: Bug detection
4. **JaCoCo**: Code coverage
5. **SonarQube**: Comprehensive static code analysis (external)

See the `quality-checks-readme.md` in the project root for more details on the quality checks configuration.
