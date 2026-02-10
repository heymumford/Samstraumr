/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.mozilla.org/en-US/MPL/2.0/
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
 *   <li>DataFlow Event Port - For component data flow communication</li>
 *   <li>Event Publisher Port - For publishing and subscribing to events</li>
 *   <li>Filesystem Port - For file system operations</li>
 *   <li>Messaging Port - For messaging operations</li>
 *   <li>Notification Port - For sending notifications</li>
 *   <li>Persistence Port - For data persistence operations</li>
 *   <li>Security Port - For security and authorization operations</li>
 *   <li>Task Execution Port - For asynchronous task execution</li>
 *   <li>Validation Port - For data validation operations</li>
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