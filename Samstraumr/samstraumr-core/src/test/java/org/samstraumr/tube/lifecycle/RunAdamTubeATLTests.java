package org.samstraumr.tube.lifecycle;

import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * JUnit 5 Cucumber test runner for Above The Line (critical) Adam Tube concept tests.
 *
 * <p>This runner is specifically designed to run critical tests related to the Adam Tube concept,
 * which represents tubes that serve as origin points in the tube hierarchy.
 */
@Suite
@SelectClasspathResource("test/features/adam-tube-tests.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.test.steps")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value =
        "pretty, html:target/cucumber-reports/adam-tube-atl.html, json:target/cucumber-reports/adam-tube-atl.json")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@ATL and @AdamTube")
public class RunAdamTubeATLTests {
  // This class serves as a JUnit 5 cucumber test runner for ATL tests
}
