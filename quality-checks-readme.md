# Samstraumr Quality Checks

This document explains how to use the quality checks in the Samstraumr project.

## Available Quality Checks

The following quality checks are configured in the project:

1. **Spotless** - Code formatting using Google Java Style
2. **PMD** - Static code analysis
3. **Checkstyle** - Style checking based on Google's style
4. **SpotBugs** - Bug detection
5. **JaCoCo** - Code coverage

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

# Run PMD analysis
mvn pmd:check

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
- PMD: `Samstraumr/pmd-ruleset.xml`

## CI/CD Integration

Quality checks are integrated into the CI/CD pipeline to ensure code quality. Pull requests that fail quality checks will not be merged until the issues are fixed.
