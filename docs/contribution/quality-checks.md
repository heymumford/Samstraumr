# Samstraumr Quality Checks

This document explains how to use the quality checks in the Samstraumr project.

## Available Quality Checks

The following quality checks are configured in the project:

1. **Spotless** - Code formatting using Google Java Style
2. **Checkstyle** - Style checking based on Google's style
3. **SpotBugs** - Bug detection
4. **JaCoCo** - Code coverage
5. **SonarQube** - Comprehensive static code analysis (external)

## Running Quality Checks

### Using the Quality Check Script

The recommended way to run quality checks is using the provided script:

```bash
# Run all quality checks
./util/quality/build-checks.sh

# Skip specific checks
./util/quality/build-checks.sh --skip-spotless --skip-spotbugs

# Run only specific checks
./util/quality/build-checks.sh --only=spotless,checkstyle

# Show help
./util/quality/build-checks.sh --help

This wizard allows you to incrementally add quality checks, fixing issues as you go.

### Maven Commands

You can also run checks directly with Maven:

```bash
# Run all quality checks
mvn validate -P quality-checks

# Skip quality checks during build
mvn clean install -P skip-quality-checks

# Format code with Spotless
mvn spotless:apply

# Check code formatting
mvn spotless:check

# PMD has been removed in favor of SonarQube

# Run Checkstyle
mvn checkstyle:check

# Run SpotBugs
mvn spotbugs:check

# Generate JaCoCo coverage report
mvn jacoco:report

### Encoding Issues

For encoding issues in files, ensure all files are saved with UTF-8 encoding.

### SpotBugs Issues

To see detailed information about SpotBugs issues:

```bash
mvn spotbugs:gui
