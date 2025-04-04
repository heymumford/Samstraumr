package org.samstraumr.tube.lifecycle;

import static io.cucumber.junit.platform.engine.Constants.*;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Cucumber test runner for Early Lifecycle ATL tests.
 *
 * <p>This runner specifically focuses on Above-The-Line (ATL) tests for the very earliest stages of
 * the tube lifecycle, including pre-conception and early conception phases.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("tube/features/L0_Tube/lifecycle")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.samstraumr.tube.lifecycle.steps")
@ConfigurationParameter(
    key = FILTER_TAGS_PROPERTY_NAME,
    value = "@PreConception or @EarlyConception and @ATL")
public class RunEarlyLifecycleATLCucumberTest {
  // This class serves as a test runner and doesn't need any implementation
}
