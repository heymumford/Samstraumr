/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.test.runner;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Test runner for Maven structure tests.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/L0_Orchestration")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "org.s8r.test.steps")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = Constants.FILTER_TAGS_PROPERTY_NAME, value = "@maven-structure")
public class MavenStructureTests {
    // This class is intentionally empty. It's used only as a holder for the annotations.
}