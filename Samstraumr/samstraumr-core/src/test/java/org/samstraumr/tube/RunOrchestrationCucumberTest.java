package org.samstraumr.tube;

import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.Suite;
import org.test.annotations.AboveTheLine;
import org.test.annotations.OrchestrationTest;

/**
 * Orchestration tests are the highest level of ATL tests.
 *
 * <p>They verify the basic integration and wiring of the system, ensuring that the core components
 * function correctly. They serve as the essential baseline for a valid build.
 */
@Suite
@IncludeEngines("cucumber")
@ConfigurationParameter(key = "cucumber.features", value = "src/test/resources/test")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "pretty, html:target/cucumber-reports/orchestration-report.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.test.steps")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@Orchestration")
@OrchestrationTest
@AboveTheLine
public class RunOrchestrationCucumberTest {
  // This class serves as a runner for orchestration Cucumber tests
  // No additional code needed
}
