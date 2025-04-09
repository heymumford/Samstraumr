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
 * Test runner for Port Interface tests.
 * 
 * <p>This test runner executes the Cucumber feature tests that verify
 * the port interfaces in the Samstraumr project. It uses the @port tag
 * to filter for the relevant feature files in the port-interfaces directory.
 * 
 * <p>The following port interfaces are tested:
 * <ul>
 *   <li>Cache Port - For data caching operations</li>
 *   <li>Configuration Port - For configuration management</li>
 *   <li>Event Publisher Port - For publishing and subscribing to events</li>
 *   <li>Filesystem Port - For file system operations</li>
 *   <li>Notification Port - For sending notifications</li>
 *   <li>Task Execution Port - For asynchronous task execution</li>
 * </ul>
 * 
 * <p>These tests ensure that port interfaces adhere to clean architecture
 * principles and properly separate domains from implementation details.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/port-interfaces")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "org.s8r.test.steps")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = Constants.FILTER_TAGS_PROPERTY_NAME, value = "@port")
public class PortTests {
  // This class is intentionally empty. It's used only as a holder for the annotations.
}