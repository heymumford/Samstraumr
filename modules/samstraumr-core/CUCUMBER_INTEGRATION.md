<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Cucumber Integration for Samstraumr

This document provides information about the current state of the Cucumber BDD integration in Samstraumr and how to improve it.

## Current Status

As of April 2025, the Cucumber integration is in a transitional state. The project has many feature files and step definitions, but the Cucumber JUnit 5 integration has some compatibility issues that prevent it from running properly with Java 21. 

Currently, the tests run through simple placeholder JUnit 5 tests for each test type that don't actually execute the Cucumber features.

## Implementation Challenges

The primary issues that need to be resolved:

1. **Classpath issues**: When attempting to run the Cucumber JUnit 5 platform engine, there are classpath issues with `ResourceUtils` being unavailable.

2. **Dependency alignment**: The JUnit Platform and Cucumber JUnit Platform Engine versions need precise alignment.

3. **Java 21 compatibility**: Some reflection operations in Cucumber may require additional JVM arguments with Java 21.

4. **Feature file format**: Some feature files may need to be updated for compatibility with the latest Cucumber version.

5. **Missing step definitions**: Not all steps in the feature files have corresponding step definitions implemented.

## Full Integration Guide

Follow these steps to implement a complete Cucumber integration:

### 1. Update POM dependencies

Ensure the dependencies are properly aligned:

```xml
<properties>
  <cucumber.version>7.22.0</cucumber.version>
  <junit-jupiter.version>5.10.2</junit-jupiter.version>
  <junit-platform.version>1.10.2</junit-platform.version>
</properties>

<dependencies>
  <!-- JUnit 5 -->
  <dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>${junit-jupiter.version}</version>
    <scope>test</scope>
  </dependency>
  
  <!-- JUnit Platform -->
  <dependency>
    <groupId>org.junit.platform</groupId>
    <artifactId>junit-platform-suite</artifactId>
    <version>${junit-platform.version}</version>
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>org.junit.platform</groupId>
    <artifactId>junit-platform-commons</artifactId>
    <version>${junit-platform.version}</version>
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>org.junit.platform</groupId>
    <artifactId>junit-platform-engine</artifactId>
    <version>${junit-platform.version}</version>
    <scope>test</scope>
  </dependency>
  
  <!-- Cucumber -->
  <dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-java</artifactId>
    <version>${cucumber.version}</version>
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-junit-platform-engine</artifactId>
    <version>${cucumber.version}</version>
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-picocontainer</artifactId>
    <version>${cucumber.version}</version>
    <scope>test</scope>
  </dependency>
</dependencies>
```

### 2. Configure Maven Surefire Plugin

Update the Maven Surefire plugin configuration to include necessary JVM arguments:

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-surefire-plugin</artifactId>
  <version>${maven.surefire.plugin.version}</version>
  <configuration>
    <argLine>
      --add-opens java.base/java.lang=ALL-UNNAMED
      --add-opens java.base/java.util=ALL-UNNAMED
      --add-opens java.base/java.lang.reflect=ALL-UNNAMED
    </argLine>
    <systemPropertyVariables>
      <cucumber.filter.tags>${cucumber.filter.tags}</cucumber.filter.tags>
    </systemPropertyVariables>
  </configuration>
</plugin>
```

### 3. Implement Cucumber Test Runners

Replace the placeholder test runners with proper Cucumber runners:

For general tests:
```java
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("tube/features")
@SelectClasspathResource("composites/features")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "org.s8r.tube.steps,org.s8r.tube.lifecycle.steps")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty,json:target/cucumber-reports/cucumber.json,html:target/cucumber-reports/cucumber-report.html")
public class RunCucumberTest {
    // JUnit 5 will run tests using the cucumber engine
}
```

For component/composite tests:
```java
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("tube/features/L1_Composite")
@SelectClasspathResource("composites/features/L1_Composite")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "org.s8r.tube.steps")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty,json:target/cucumber-reports/composite.json,html:target/cucumber-reports/composite-report.html")
@ConfigurationParameter(key = Constants.FILTER_TAGS_PROPERTY_NAME, value = "@L1_Composite")
public class RunCompositeTests {
    // JUnit 5 will run tests using the cucumber engine
}
```

### 4. Complete Step Definitions

Implement step definitions for all steps in the feature files. Each feature file needs corresponding step definitions in the glue code.

1. Organize step definitions by feature area:
   - `tube/steps` - Core tube functionality
   - `tube/lifecycle/steps` - Lifecycle functionality
   - `tube/steps/composite` - Composite functionality
   
2. Use dependency injection with PicoContainer:
   - Share state between steps with constructor injection
   - Use appropriate scope (scenario, feature, or test run)

### 5. Update the Feature Files

Ensure feature files follow the latest Gherkin syntax:

1. Start each file with a feature description
2. Use tags consistently (@ATL, @L1_Composite, etc.)
3. Format scenarios with Given/When/Then structure
4. Use data tables and examples tables appropriately

### 6. Update the Testing Infrastructure

Modify the test scripts to properly handle Cucumber configuration:

1. Update s8r-test to set proper system properties
2. Create JaCoCo coverage configuration compatible with Cucumber
3. Generate and analyze test reports

## Current Placeholder Solution

For now, a simplified test solution is in place to ensure basic test execution works. Each test type has a corresponding simple JUnit 5 test:

- `RunCucumberTest.java` - General test placeholder
- `RunCompositeTests.java` - Component/composite test placeholder
- `RunTubeTests.java` - Tube/unit test placeholder
- `RunMachineTests.java` - Machine/API test placeholder
- `RunATLTests.java` - ATL test placeholder

These placeholders ensure that the build and test infrastructure can continue to function while the full Cucumber integration is being implemented.

Once the integration issues are resolved, these placeholders can be replaced with the proper Cucumber runners as described above.