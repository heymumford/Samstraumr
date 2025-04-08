/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
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
 * Test runner for Integration tests.
 * 
 * <p>This suite runs all Cucumber features for integration between components,
 * including port interface tests and adapter implementations.
 * 
 * <p>To run this suite, use one of these methods:
 * <ul>
 *   <li>Maven: {@code mvn test -Dtest=IntegrationTests}</li>
 *   <li>s8r-test script: {@code ./s8r-test integration}</li>
 * </ul>
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/integration")
@SelectClasspathResource("features/port-interfaces")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "org.s8r.test.steps")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = Constants.FILTER_TAGS_PROPERTY_NAME, value = "@IntegrationTest")
public class IntegrationTests {
    // This class is intentionally empty. It's used only as a holder for the annotations.
}