package org.s8r.test.runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

/**
 * Test runner for the Maven structure test features.
 * <p>
 * This runner executes the Maven structure tests that validate the project's
 * Maven configuration and structure. These tests verify the three-tier Maven
 * structure, dependency management, profile configuration, and alignment with
 * Clean Architecture principles.
 * <p>
 * Run with: mvn test -Dtest=MavenStructureTests
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("tube/features/L0_Orchestration")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.s8r.test.steps")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@MavenTest")
public class MavenStructureTests {
    // This class serves as a test runner and doesn't need any implementation
}