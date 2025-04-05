<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Quality Tools for Samstraumr

This directory contains configuration files for various quality tools used in the Samstraumr project.

## Configuration Files

- **spotbugs-exclude.xml**: Contains exclusion patterns for SpotBugs static analysis
  - Excludes test classes and other non-production code from analysis
  - Filters out specific bug patterns that aren't relevant to this project
  - Reduces false positives in static analysis
- **checkstyle-suppressions.xml**: Contains suppressions for Checkstyle rules
  - Skips certain checks for test files where they're not appropriate
  - Allows for exceptions to standard rules in specific situations
  - Maintains code quality without being too restrictive
- **pmd-ruleset.xml**: Defines the PMD ruleset for static analysis
  - Customizes which PMD rules are enabled or disabled
  - Groups rules by category (best practices, code style, etc.)
  - Sets specific parameters for rule behavior
- **pmd-exclude.properties**: Contains file patterns to exclude from PMD violations
  - Helps avoid false positives in test code
  - Allows for exceptions to PMD rules for specific files

## Usage

These configuration files are automatically used by the Maven build plugins. You don't need to reference them directly.

To run individual quality tools:

```bash
# Run Spotless code formatter check
mvn spotless:check

# Run Checkstyle validation
mvn checkstyle:check

# Run SpotBugs analysis
mvn spotbugs:check

# Run JaCoCo code coverage (after tests)
# Note: Typically should be run with -P \!fast to override the default fast profile
mvn test jacoco:report -Djacoco.skip=false -P \!fast

# Run all quality checks
mvn verify -P \!fast
```

## Known Limitations and Workarounds

### PMD Configuration

The PMD plugin currently has compatibility issues with the Maven site skin. This results in errors when generating PMD reports.

Workaround:
- Use SpotBugs for static analysis instead of PMD
- For PMD-specific rules, consider implementing custom checkers in Checkstyle

### JaCoCo Test Coverage

Test coverage reporting requires tests to actually run. By default, the `fast` profile is active which skips tests.

Workaround:
- Use `-P \!fast` to disable the fast profile
- Set `-Djacoco.skip=false` explicitly
- Some test dependencies may be missing, so tests may not compile

### Missing Test Dependencies

The test suite requires additional dependencies that are not currently in the pom.xml:
- Mockito for mocking in tests
- TestContainers for integration tests

Workaround:
- Add these dependencies to the `pom.xml` for full test execution

## Quality Profiles

The project includes several Maven profiles for quality checks:

- `quality-checks`: Default profile with standard quality checks
- `security-checks`: Profile focused on security analysis with OWASP Dependency Check
- `doc-checks`: Profile for documentation quality with JavaDoc checks
- `coverage`: Profile for code coverage analysis with JaCoCo
- `fast`: Default profile that skips tests and some quality checks for faster builds

Example:

```bash
# Run security checks
mvn verify -P security-checks

# Run with coverage enabled and fast mode disabled
mvn verify -P coverage,\!fast
```

## Quality Thresholds

The project enforces quality thresholds:

- **Code Coverage**:
  - Line coverage: 70%
  - Branch coverage: 60%
- **Complexity Limits**:
  - Maximum class complexity: 20
  - Maximum method complexity: 10

These thresholds can be adjusted in the root `pom.xml` file.
