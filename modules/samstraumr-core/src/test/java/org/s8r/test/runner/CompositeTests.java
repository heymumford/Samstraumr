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
 * Test runner for Composite tests.
 * 
 * <p>This suite runs all Cucumber features related to Composite components.
 * It includes the composite-patterns directory and also the patterns directory
 * which includes pattern implementations for composites.
 * 
 * <p>To run this suite, use one of these methods:
 * <ul>
 *   <li>Maven: {@code mvn test -Dtest=CompositeTests}</li>
 *   <li>s8r-test script: {@code ./s8r-test composite}</li>
 * </ul>
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/composite-patterns")
@SelectClasspathResource("features/patterns")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "org.s8r.test.steps,org.s8r.tube.steps")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = Constants.FILTER_TAGS_PROPERTY_NAME, value = "@CompositeTest")
public class CompositeTests {
    // This class is intentionally empty. It's used only as a holder for the annotations.
}