<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

<\!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# JUnit 5 and Cucumber Upgrade Guide

This document describes the changes made to upgrade, streamline, and simplify the JUnit and Cucumber test orchestration in the Samstraumr/S8r framework.

## Summary of Changes

1. **Removed JUnit 4 Dependencies**
   - Removed direct JUnit 4 dependency
   - Removed junit-vintage-engine to prevent JUnit 4 compatibility layer
2. **Modern Test Runner Architecture**
   - Created standardized test runners using JUnit 5 Jupiter Platform
   - Implemented test runners with clear responsibility separation
   - Used `@SelectClasspathResource` instead of string paths for better resource discovery
3. **Standardized Runner Structure**
   - `S8rTestRunner` - Main entry point for all S8r tests
   - `CriticalTestRunner` - Focused on ATL tests
   - `AtomicTubeTestRunner` - Specific to atomic tube tests
   - `AdamTubeTestRunner` - Specific to Adam tube tests
4. **Simplified Configuration**
   - Consolidated test discovery mechanisms
   - Standardized report generation paths
   - Made tag filtering consistent across runners

## Configuration Changes

### POM.xml Changes

```xml
<dependency>
  <groupId>org.junit.vintage</groupId>
  <artifactId>junit-vintage-engine</artifactId>
  <version>${junit-jupiter.version}</version>
  <scope>test</scope>
</dependency>
```

### Test Runner Changes

From:

```java
@Suite
@SuiteDisplayName("S8r Atomic Tube Tests")
@IncludeEngines("cucumber")
@ConfigurationParameter(key = "cucumber.features", value = "src/test/resources/tube/features/L0_Tube/lifecycle")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.s8r.core.tube.test.steps")
```

To:

```java
@Suite
@SuiteDisplayName("S8r Atomic Tube Tests")
@IncludeEngines("cucumber")
@SelectClasspathResource("tube/features/L0_Tube/lifecycle")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.s8r.core.tube.test.steps")
```

## Maven Test Execution

Tests can be executed using the following pattern:

```
mvn test -Dtest=RunnerClassName -Dcucumber.filter.tags="@TagName"
```

Examples:

```
mvn test -Dtest=S8rTestRunner                             # Run all tests
mvn test -Dtest=CriticalTestRunner                        # Run ATL tests
mvn test -Dtest=AtomicTubeTestRunner                      # Run atomic tube tests
mvn test -Dtest=AdamTubeTestRunner -P adam-tube-atl-tests # Run Adam ATL tests with profile
```

## Cucumber Report Generation

Reports are generated in the `target/cucumber-reports/` directory:

- HTML reports: `*-cucumber.html`
- JSON reports: `*-cucumber.json`

## Migration Recommendations

1. Use the new test runners for all test execution
2. Prefer Maven profiles for filtering when possible
3. Use the specific test runners for focused test execution
4. Rely on JUnit 5 assertions in test code
5. Remove any remaining JUnit 4 imports or usage
