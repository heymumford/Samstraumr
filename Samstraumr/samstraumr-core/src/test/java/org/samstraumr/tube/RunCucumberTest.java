package org.samstraumr.tube;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Main Cucumber test runner for executing BDD tests across all components.
 *
 * <p>This runner supports the ATL/BTL test categorization strategy, allowing for selective test
 * execution based on criticality:
 *
 * <ul>
 *   <li>ATL (Above The Line): Critical tests that must pass with every build
 *   <li>BTL (Below The Line): Important but non-blocking tests that can run separately
 * </ul>
 *
 * <p>Test execution can be filtered using the cucumber.filter.tags system property:
 *
 * <ul>
 *   <li>Run ATL tests: -Dcucumber.filter.tags="@ATL"
 *   <li>Run BTL tests: -Dcucumber.filter.tags="@BTL"
 *   <li>Run specific level: -Dcucumber.filter.tags="@L0_Tube"
 *   <li>Run combinations: -Dcucumber.filter.tags="@ATL and @L0_Tube"
 * </ul>
 *
 * <p>Feature files are organized hierarchically in the following directories:
 *
 * <ul>
 *   <li>tube/features: Core Tube features
 *   <li>composites/features: Composite Tube features
 * </ul>
 */
@Suite
@SuiteDisplayName("Samstraumr BDD Tests")
@IncludeEngines("cucumber")
@SelectClasspathResource("tube/features")
@SelectClasspathResource("composites/features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.samstraumr.tube.steps")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value =
        "pretty, html:target/cucumber-reports/cucumber.html, json:target/cucumber-reports/cucumber.json")
public class RunCucumberTest {}
