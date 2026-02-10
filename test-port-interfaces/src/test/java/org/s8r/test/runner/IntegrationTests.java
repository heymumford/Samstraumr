/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.test.runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import io.cucumber.junit.platform.engine.Constants;

/**
 * Test runner for Integration tests.
 * 
 * <p>This test runner executes the Cucumber feature tests that verify
 * the integration of different port interfaces in the Samstraumr project.
 * It uses the @integration tag to filter for the relevant feature files 
 * in the integration directory.
 * 
 * <p>The following integration scenarios are tested:
 * <ul>
 *   <li>Cache and FileSystem Integration - For persistent caching functionality</li>
 *   <li>Event and Notification Integration - For events triggering notifications</li>
 *   <li>Validation and Persistence Integration - For data validation before storage</li>
 *   <li>DataFlow and Messaging Integration - For connecting component data flows with messaging systems</li>
 * </ul>
 * 
 * <p>These integration tests ensure that the port interfaces can work together
 * effectively to provide higher-level functionality in the application.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/integration")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "org.s8r.test.steps")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = Constants.FILTER_TAGS_PROPERTY_NAME, value = "@integration")
public class IntegrationTests {
  // This class is intentionally empty. It's used only as a holder for the annotations.
}