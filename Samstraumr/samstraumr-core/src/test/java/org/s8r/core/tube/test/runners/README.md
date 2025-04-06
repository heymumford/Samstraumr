<\!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# S8r Test Runners

This package contains JUnit 5 test runners for the S8r framework, providing a modern and flexible approach to test orchestration and execution. These runners leverage JUnit 5 and the latest Cucumber version for Behavior-Driven Development (BDD) testing.

## Runner Architecture

The test runners are organized in a hierarchical structure:

1. **S8rTestRunner** - The main entry point for all S8r framework tests
2. **CriticalTestRunner** - Runs only critical (Above The Line) tests
3. **AtomicTubeTestRunner** - Focused on atomic tube tests
4. **AdamTubeTestRunner** - Focused on Adam tube tests

## Key Features

- Exclusive use of JUnit 5 (Jupiter) platform for modern testing
- Latest Cucumber integration with JUnit 5 platform engine
- Feature file discovery using path-based resource selection
- Tag-based filtering for selective test execution
- HTML and JSON reports generated automatically

## Usage Examples

Run all S8r tests:

```
mvn test -Dtest=S8rTestRunner
```

Run only critical tests:

```
mvn test -Dtest=CriticalTestRunner
```

Run atomic tube tests with specific tags:

```
mvn test -Dtest=AtomicTubeTestRunner -Dcucumber.filter.tags="@Identity and @Positive"
```

Run profile-specific tests:

```
mvn test -P atl-tests -Dtest=S8rTestRunner
```

## Maven Profiles

S8r's test runners work alongside Maven profiles defined in pom.xml for additional flexibility:

- `atl-tests`: Critical tests only
- `adam-tube-tests`: Adam tube tests only
- `atomic-tube-tests`: Atomic tube tests only

## Report Generation

All runners generate both HTML and JSON reports in the `target/cucumber-reports/` directory.
