# ATL Test Discovery System

This document explains the Above-The-Line (ATL) test discovery system in Samstraumr.

## Overview

ATL tests are critical tests that must pass for every build. They are discovered by the JUnit test runner using a combination of annotations and configuration. This system ensures that:

1. All tests annotated with `@ATL` are discovered and executed
2. ATL tests run quickly to provide immediate feedback
3. ATL test failures block the build

## Key Components

The ATL test discovery system consists of the following components:

### 1. ATL Annotation

The `@ATL` annotation is defined in:
- `src/test/java/org/s8r/test/annotation/ATL.java`

This annotation includes the JUnit `@Tag("ATL")` annotation, which allows JUnit to discover these tests.

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("ATL")
public @interface ATL {}
```

### 2. ATL Test Runner

The `RunATLTests` class is a JUnit test suite configured to discover and run all tests annotated with `@ATL`. It is defined in:
- `src/test/java/org/s8r/tube/RunATLTests.java`

```java
@Suite
@SuiteDisplayName("S8r Above-The-Line Tests")
@IncludeEngines("junit-jupiter")
@SelectPackages({
    "org.s8r.tube", 
    "org.s8r.component", 
    "org.s8r.core.tube", 
    "org.s8r.domain"
})
@IncludeTags("ATL")
// ... additional configuration ...
public class RunATLTests {}
```

### 3. Maven Configuration

The `pom.xml` includes an `atl-tests` profile that configures Maven to run tests with the `@ATL` tag:

```xml
<profile>
  <id>atl-tests</id>
  <properties>
    <cucumber.filter.tags>@ATL</cucumber.filter.tags>
  </properties>
</profile>
```

### 4. Command-Line Interface

Run ATL tests using either of these commands:

```bash
# Using the s8r-test script
./s8r-test atl

# Using the dedicated script
./util/test/run-atl-tests.sh
```

## Writing ATL Tests

To create a new ATL test:

1. Add the `@ATL` annotation to your test class:

```java
import org.s8r.test.annotation.ATL;

@ATL
public class MyImportantTest {
    @Test
    public void testCriticalFeature() {
        // Test code...
    }
}
```

2. Ensure your test follows ATL principles:
   - Fast - Executes quickly for immediate feedback
   - Reliable - Produces consistent results
   - Critical - Verifies essential functionality
   - Focused - Tests one important aspect

## ATL Test Mapping

To get a map of all ATL tests in the codebase, run:

```bash
./util/test/mapping/map-atl-tests.sh
```

This will generate a report showing all ATL tests organized by package.

## Troubleshooting

If ATL tests are not being discovered:

1. Verify the test class is properly annotated with `@ATL`
2. Ensure the package is included in the `@SelectPackages` in `RunATLTests`
3. Run the mapping script to verify the test is recognized
4. Check Maven configuration to ensure tests aren't being skipped

## Best Practices

1. Only mark truly critical tests as ATL
2. Keep ATL tests fast and reliable
3. Maintain a reasonable number of ATL tests (too many will slow the build)
4. Review the distribution of ATL tests regularly to ensure good coverage