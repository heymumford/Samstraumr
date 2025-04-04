package org.s8r.test.runner;

import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.s8r.test.annotation.ATL;

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
 *
 * <p>This runner is part of the package structure simplification initiative, centralizing test
 * runners into the org.s8r.test.runner package.
 */
@Suite
@SuiteDisplayName("Critical BDD Tests")
@IncludeEngines("cucumber")
@ConfigurationParameter(
    key = "cucumber.features",
    value = "src/test/resources/tube/features, src/test/resources/composites/features")
@ConfigurationParameter(
    key = GLUE_PROPERTY_NAME,
    value = "org.samstraumr.tube.steps,org.samstraumr.tube.lifecycle.steps,org.s8r.test.steps")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@ATL")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value =
        "pretty, html:target/cucumber-reports/atl-cucumber.html, json:target/cucumber-reports/atl-cucumber.json")
@ATL
public class CriticalTestRunner {}
