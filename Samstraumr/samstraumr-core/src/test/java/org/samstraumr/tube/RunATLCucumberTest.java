/*
Filename: RunATLCucumberTest.java
Purpose: Cucumber test runner for executing Above The Line (ATL) BDD tests
Goals:
  - Run only critical ATL tests that must pass with every build
  - Support filtering by the @ATL tag
  - Generate standardized reports for ATL tests
Dependencies:
  - io.cucumber: For Cucumber BDD testing infrastructure
  - org.junit.platform.suite.api: For test suite configuration
  - org.samstraumr.tube.steps: For step definitions implementing test scenarios
Assumptions:
  - Feature files have proper @ATL tags on critical scenarios
  - ATL tests are fast, reliable, and focus on core functionality
*/

package org.samstraumr.tube;

import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Cucumber test runner that only executes Above The Line (ATL) BDD tests.
 *
 * <p>ATL tests are critical tests that MUST pass with every build. They have the following
 * characteristics:
 *
 * <ul>
 *   <li>Fast - They execute quickly to provide immediate feedback
 *   <li>Reliable - They produce consistent results without flakiness
 *   <li>Critical - They verify core functionality essential to the system
 *   <li>High Priority - They block the build if failing
 * </ul>
 *
 * <p>This runner is designed to be used in the main build pipeline to provide immediate feedback on
 * critical issues.
 */
@Suite
@SuiteDisplayName("Above The Line (ATL) BDD Tests - Critical Path")
@IncludeEngines("cucumber")
@SelectClasspathResource("tube/features")
@SelectClasspathResource("composites/features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.samstraumr.tube.steps")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@ATL")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value =
        "pretty, html:target/cucumber-reports/atl-cucumber.html, json:target/cucumber-reports/atl-cucumber.json")
public class RunATLCucumberTest {}
