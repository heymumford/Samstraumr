/*
Filename: RunBTLCucumberTest.java
Purpose: Cucumber test runner for executing Below The Line (BTL) BDD tests
Goals:
  - Run only BTL tests that are important but non-blocking
  - Support filtering by the @BTL tag
  - Generate standardized reports for BTL tests
Dependencies:
  - io.cucumber: For Cucumber BDD testing infrastructure
  - org.junit.platform.suite.api: For test suite configuration
  - org.samstraumr.tube.steps: For step definitions implementing test scenarios
Assumptions:
  - Feature files have proper @BTL tags on non-critical scenarios
  - BTL tests may be slower, more complex, and focus on edge cases
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
 * Cucumber test runner that only executes Below The Line (BTL) BDD tests.
 *
 * <p>BTL tests are important but non-blocking tests that can run separately. They have the
 * following characteristics:
 *
 * <ul>
 *   <li>May be slower - They may take longer to execute
 *   <li>More complex - They often test edge cases or rare scenarios
 *   <li>Lower priority - They don't block the build if failing
 *   <li>Comprehensive - They provide broader coverage of the system
 * </ul>
 *
 * <p>This runner is designed to be used in separate processes (nightly builds, separate CI jobs) to
 * provide comprehensive coverage without blocking development.
 */
@Suite
@SuiteDisplayName("Below The Line (BTL) BDD Tests - Robustness Path")
@IncludeEngines("cucumber")
@SelectClasspathResource("tube/features")
@SelectClasspathResource("composites/features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.samstraumr.tube.steps")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@BTL")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value =
        "pretty, html:target/cucumber-reports/btl-cucumber.html, json:target/cucumber-reports/btl-cucumber.json")
public class RunBTLCucumberTest {}
