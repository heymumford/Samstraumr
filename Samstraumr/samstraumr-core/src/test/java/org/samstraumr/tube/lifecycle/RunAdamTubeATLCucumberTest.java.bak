package org.samstraumr.tube.lifecycle;

import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Cucumber test runner for Adam Tube concept tests. This runner focuses on critical tests for the
 * Adam Tube concept that must pass with every build.
 *
 * <p>Tags included in this runner:
 *
 * <ul>
 *   <li>@ATL - Above The Line (critical) tests
 *   <li>@AdamTube - Tests specifically for the Adam Tube concept
 * </ul>
 */
@Suite
@SuiteDisplayName("Adam Tube Concept ATL Tests")
@IncludeEngines("cucumber")
@SelectClasspathResource("tube/features")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value =
        "pretty, json:target/cucumber-reports/adam-tube-ATL.json, html:target/cucumber-reports/adam-tube-ATL.html")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@ATL and @AdamTube")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.samstraumr.tube.steps,org.samstraumr.tube.lifecycle.steps")
public class RunAdamTubeATLCucumberTest {
  // This class serves as a cucumber test runner
}
