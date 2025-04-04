package org.s8r.test.runner;

import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.s8r.test.annotation.ATL;
import org.s8r.test.annotation.OrchestrationTest;

/**
 * Orchestration tests runner for the basic integration and system assembly tests.
 *
 * <p>Orchestration tests are the highest level of ATL tests, verifying the basic integration and
 * wiring of the system. They serve as the essential baseline for a valid build, ensuring that the
 * core components function correctly.
 *
 * <p>These tests focus on:
 * <ul>
 *   <li>System assembly and connectivity
 *   <li>Basic component interaction
 *   <li>Core system flows
 * </ul>
 * 
 * <p>This runner is part of the package structure simplification initiative, centralizing test
 * runners into the org.s8r.test.runner package.
 */
@Suite
@SuiteDisplayName("Orchestration Tests")
@IncludeEngines("cucumber")
@ConfigurationParameter(
    key = "cucumber.features",
    value = "src/test/resources/tube/features, src/test/resources/test")
@ConfigurationParameter(
    key = GLUE_PROPERTY_NAME,
    value = "org.samstraumr.tube.steps,org.samstraumr.tube.orchestration.steps,org.s8r.test.steps")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@OrchestrationTest")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "pretty, html:target/cucumber-reports/orchestration-report.html")
@OrchestrationTest
@ATL
public class OrchestrationTestRunner {}