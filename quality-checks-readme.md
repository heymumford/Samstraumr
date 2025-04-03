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

### All Quality Checks

To run all quality checks:

```bash
mvn validate -P quality-checks
```

### Skip Quality Checks

To skip quality checks during build:

```bash
mvn clean install -P skip-quality-checks
```

### Individual Checks

To run individual quality checks:

```bash
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
```

## Fixing Common Issues

### Spotless Issues

If you have formatting issues, run:

```bash
mvn spotless:apply
```

### Encoding Issues

For encoding issues in files, ensure all files are saved with UTF-8 encoding.

### SpotBugs Issues

To see detailed information about SpotBugs issues:

```bash
mvn spotbugs:gui
```

## Configuration Files

- Checkstyle: `Samstraumr/checkstyle.xml`
- SonarQube: Configuration resides on the SonarQube server

## CI/CD Integration

Quality checks are integrated into the CI/CD pipeline to ensure code quality. Pull requests that fail quality checks will not be merged until the issues are fixed.

## SonarQube Integration

SonarQube provides comprehensive static code analysis for the project. It runs as part of the CI/CD pipeline and also can be accessed through:

- SonarQube Dashboard (refer to your team's SonarQube instance URL)
- SonarLint IDE plugin for local analysis

For more information on SonarQube and why we migrated from PMD, see `docs/quality-changes.md`.
