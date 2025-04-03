/*
Filename: RunCucumberTest.java
Purpose: Main test runner for coordinating and executing Cucumber BDD tests across all components.
Goals:
  - Ensure that all feature files are discoverable in classpath resources
  - Ensure that test step definitions are properly glued to feature files
  - Ensure that test reports are generated in a standardized format
Dependencies:
  - io.cucumber: For Cucumber BDD testing infrastructure
  - org.junit.platform.suite.api: For test suite configuration
  - org.samstraumr.tube.steps: For step definitions implementing test scenarios
Assumptions:
  - Feature files are organized hierarchically in tube/features and composites/features
  - Tags are utilized for selective test execution (@ATL, @BTL, @L0_Tube, etc.)
  - Maven test commands support filtering by tags for focused test runs
*/

package org.samstraumr.tube;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("tube/features")
@SelectClasspathResource("composites/features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.samstraumr.tube.steps")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "pretty, html:target/cucumber-reports/cucumber.html")
public class RunCucumberTest {}
