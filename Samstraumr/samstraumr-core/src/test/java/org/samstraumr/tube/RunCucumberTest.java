package org.samstraumr.tube;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Main test runner for Samstraumr Tube tests. Organized hierarchically according to Martin Fowler's
 * test organization principles.
 *
 * <p>Test hierarchy: - L0_Tube: Atomic tube component tests - L1_Bundle: Bundle-level integration
 * tests - L2_Machine: Complex machine composition tests - L3_System: Full system tests
 *
 * <p>Run with specific tags: - mvn test -Dcucumber.filter.tags="@ATL" (Above-the-line critical
 * tests) - mvn test -Dcucumber.filter.tags="@L0_Tube" (Only atomic tube tests) - mvn test
 * -Dcucumber.filter.tags="@Identity" (Only identity-related tests)
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("tube/features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.samstraumr.tube.steps")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "pretty, html:target/cucumber-reports/cucumber.html")
public class RunCucumberTest {}
