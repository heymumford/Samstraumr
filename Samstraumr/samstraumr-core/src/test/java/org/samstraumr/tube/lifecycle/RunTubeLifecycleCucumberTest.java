package org.samstraumr.tube.lifecycle;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Cucumber test runner for Tube Lifecycle tests.
 *
 * <p>This runner focuses specifically on the lifecycle aspects of Tubes based on the
 * biological development model. It executes the tests defined in the lifecycle feature files.
 *
 * <p>Test execution can be filtered using the cucumber.filter.tags system property:
 *
 * <ul>
 *   <li>Run all lifecycle tests: -Dcucumber.filter.tags="@Lifecycle"
 *   <li>Run creation phase tests: -Dcucumber.filter.tags="@Lifecycle and @Creation"
 *   <li>Run specific tests: -Dcucumber.filter.tags="@Lifecycle and @ATL"
 * </ul>
 */
@Suite
@SuiteDisplayName("Tube Lifecycle Tests")
@IncludeEngines("cucumber")
@SelectClasspathResource("tube/features/L0_Tube/lifecycle")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.samstraumr.tube.lifecycle.steps")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value =
        "pretty, html:target/cucumber-reports/lifecycle/cucumber.html, json:target/cucumber-reports/lifecycle/cucumber.json")
public class RunTubeLifecycleCucumberTest {}