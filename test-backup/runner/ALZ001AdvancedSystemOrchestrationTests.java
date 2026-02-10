package org.s8r.test.runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import io.cucumber.junit.platform.engine.Cucumber;
import org.junit.jupiter.api.DisplayName;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;

/**
 * Test runner for the Advanced System Orchestration features in the ALZ001 test suite.
 * <p>
 * This runner executes the L3_System layer tests for coordinating multiple specialized
 * machines in a comprehensive system for Alzheimer's disease research.
 * </p>
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/alz001/alz001-advanced-system-orchestration.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.s8r.test.steps.alz001.system,org.s8r.test.steps.alz001.machine,org.s8r.test.steps.alz001")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty,html:target/cucumber-reports/alz001-advanced-system-orchestration.html,json:target/cucumber-reports/alz001-advanced-system-orchestration.json")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@ALZ001 and @L3_System")
@DisplayName("ALZ001 Advanced System Orchestration Tests (L3)")
@Cucumber
public class ALZ001AdvancedSystemOrchestrationTests {
    // Test runner class - implementation managed by JUnit and Cucumber
}