# Samstraumr Quality Checks

This document outlines the quality checks and test organization for the Samstraumr project.

## Test Organization (MECE)

Samstraumr tests follow a Mutually Exclusive, Collectively Exhaustive (MECE) organization approach:

### Hierarchical Structure
Tests are organized according to component complexity levels:

1. **Atomic Components** (`@L0_Tube`) - Unit tests for individual Tubes
2. **Component Bundles** (`@L1_Bundle`) - Tests for connected Tubes forming Bundles
3. **Machine Compositions** (`@L2_Machine`) - Tests for interconnected Bundles forming Machines
4. **Complete Systems** (`@L3_System`) - Tests for entire system operation

### Critical Path Categorization
Tests are categorized by importance to enable prioritized test execution:

1. **Above The Line** (`@ATL`) - Critical functionality tests that must pass for a valid build
2. **Below The Line** (`@BTL`) - Non-critical robustness tests for edge cases and quality

## Running Tests

### Test Selection
Run tests based on their categorization:

```bash
# Run all tests
mvn test

# Run only ATL tests (critical functionality)
mvn test -P atl-tests

# Run only BTL tests (robustness)
mvn test -P btl-tests

# Run specific component level tests
mvn test -Dcucumber.filter.tags="@L0_Tube"
mvn test -Dcucumber.filter.tags="@L1_Bundle"
mvn test -Dcucumber.filter.tags="@L2_Machine"
mvn test -Dcucumber.filter.tags="@L3_System"

# Run tests with combined tags
mvn test -Dcucumber.filter.tags="@ATL and @L0_Tube"
mvn test -Dcucumber.filter.tags="@BTL and @Performance"
```

## Quality Checks

Samstraumr implements a comprehensive quality check pipeline for ensuring code quality:

### 1. Code Formatting (Spotless)
- Enforces consistent code formatting
- Ensures proper import organization
- Validates whitespace and trailing newlines

```bash
# Check formatting
mvn spotless:check

# Apply automatic formatting
mvn spotless:apply
```

### 2. Static Code Analysis (PMD)
- Identifies potential bugs
- Enforces best practices
- Detects error-prone code patterns

```bash
# Run PMD checks
mvn pmd:check
```

### 3. Coding Standards (Checkstyle)
- Enforces Google Java Style Guide
- Ensures consistent naming conventions
- Validates documentation and Javadoc

```bash
# Run Checkstyle checks
mvn checkstyle:check
```

### 4. Bug Detection (SpotBugs)
- Static analysis to find potential bugs
- Detects null pointer dereferences
- Identifies resource leaks and concurrency issues

```bash
# Run SpotBugs checks
mvn spotbugs:check
```

### 5. Code Coverage (JaCoCo)
- Measures test coverage
- Enforces minimum coverage thresholds
- Generates detailed coverage reports

```bash
# Generate coverage report
mvn jacoco:report

# Verify coverage meets thresholds
mvn jacoco:check
```

## Running All Quality Checks

Use the provided script to run all quality checks in sequence:

```bash
./build-checks.sh
```

This script:
1. Verifies code formatting
2. Runs static code analysis
3. Checks coding standards
4. Runs bug detection
5. Executes ATL tests (critical)
6. Executes BTL tests (robustness)
7. Verifies code coverage

## Report Locations

After running the quality checks, reports are available in:

- Cucumber Test Report: `target/cucumber-reports/cucumber.html`
- JaCoCo Coverage Report: `target/site/jacoco/index.html`
- Checkstyle Report: `target/checkstyle-result.xml`
- PMD Analysis: `target/pmd.xml`
- SpotBugs Results: `target/spotbugsXml.xml`

## CI/CD Integration

These quality checks are designed to be integrated into CI/CD pipelines:

1. ATL tests should block PR merges if they fail
2. BTL tests may be run as non-blocking checks
3. Quality checks should be run before code review
4. Coverage reports should be published for review