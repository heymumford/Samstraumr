# Samstraumr Test Coverage Guide

This guide explains how to measure, analyze and improve test coverage in the Samstraumr project.

## Understanding Code Coverage

Code coverage is a measurement of how much of your code is executed during tests. It helps identify untested code paths and potential gaps in your test suite.

### Types of Coverage Metrics

1. **Line Coverage**: Percentage of lines of code executed during tests
2. **Branch Coverage**: Percentage of branching points (if/else, switch) covered
3. **Method Coverage**: Percentage of methods called during tests
4. **Class Coverage**: Percentage of classes used during tests

Samstraumr uses JaCoCo for measuring code coverage with default targets set to:

```xml
<jacoco.line.coverage>0.75</jacoco.line.coverage>
<jacoco.branch.coverage>0.75</jacoco.branch.coverage>
```

This means we aim for at least 75% line and branch coverage in production code.

## Running Tests with Coverage

### Option 1: Using s8r-test with Coverage

The simplest way to run tests with coverage is:

```bash
# Run all tests with coverage
./s8r-test all --coverage

# Run specific tests with coverage
./s8r-test unit --coverage
./s8r-test component --coverage
```

This will run the tests and generate a JaCoCo coverage report.

### Option 2: Maven with JaCoCo Plugin

You can also run tests with coverage directly using Maven:

```bash
# Run tests with JaCoCo coverage
mvn clean test jacoco:report

# Activate coverage profile for threshold checking
mvn clean verify -Pcoverage
```

## Viewing Coverage Reports

### JaCoCo HTML Report

JaCoCo generates an HTML report that provides detailed coverage information:

```bash
# Open the JaCoCo HTML report in your browser
firefox ./modules/samstraumr-core/target/site/jacoco/index.html
```

This HTML report shows:
- Overall project coverage percentages
- Package-level coverage
- Class-level coverage with color-coded source code highlighting
- Missed branches and lines

### Coverage Analysis Tool

The `s8r-test-coverage` tool provides a comprehensive overview of test coverage:

```bash
./s8r-test-coverage
```

This tool:
1. Counts test files by category (domain, application, infrastructure, etc.)
2. Displays feature file count by functionality
3. Shows JaCoCo coverage metrics (if available)
4. Analyzes coverage gaps against targets
5. Provides specific recommendations for improvement

## Coverage Profiles and Configuration

### TDD Development Profile

By default, the project uses the `tdd-development` profile, which sets coverage thresholds to 0:

```xml
<profile>
  <id>tdd-development</id>
  <activation>
    <activeByDefault>true</activeByDefault>
  </activation>
  <properties>
    <jacoco.line.coverage>0.0</jacoco.line.coverage>
    <jacoco.branch.coverage>0.0</jacoco.branch.coverage>
  </properties>
</profile>
```

This allows development to proceed without strict coverage enforcement while the system is in early development.

### Coverage Profile

For build validation, use the coverage profile:

```bash
mvn clean verify -Pcoverage
```

This enforces the coverage thresholds defined in the main properties:

```xml
<jacoco.line.coverage>0.75</jacoco.line.coverage>
<jacoco.branch.coverage>0.75</jacoco.branch.coverage>
```

## JaCoCo Configuration

JaCoCo excludes certain files from coverage calculation:

```xml
<excludes>
  <exclude>**/*Test.class</exclude>
  <exclude>**/*Steps.class</exclude>
  <exclude>**/*Exception.class</exclude>
</excludes>
```

This focuses coverage metrics on production code rather than test code.

## Improving Test Coverage

### Methodology

1. **Run the coverage analysis**:
   ```bash
   ./s8r-test-coverage
   ```

2. **Identify gaps**:
   - Look for areas with low coverage
   - Focus on domain and application layers first
   - Prioritize critical functionality

3. **Write targeted tests**:
   - Add missing unit tests for domain models and business logic
   - Add component tests for integration points
   - Add BDD tests for key functionality

4. **Use the coverage report**:
   - Examine missed branches in the JaCoCo report
   - Address complex conditionals with multiple test cases

5. **Follow test patterns**:
   - Use the examples in `docs/testing/examples/` as templates
   - Focus on both happy path and error scenarios

## CI/CD Integration

The coverage report and JaCoCo analysis can be integrated into your CI/CD pipeline:

```yaml
- name: Run tests with coverage
  run: ./s8r-test all --coverage

- name: Check coverage thresholds
  run: mvn verify -Pcoverage
```

This ensures that coverage standards are maintained as the codebase evolves.