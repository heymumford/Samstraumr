/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

package org.samstraumr.tube.lifecycle;

import static io.cucumber.junit.platform.engine.Constants.*;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Cucumber test runner for comprehensive tube creation lifecycle ATL tests.
 *
 * <p>This runner specifically focuses on Above-The-Line (ATL) tests for the complete creation
 * lifecycle, from pre-existence through early development stages, implementing the biological
 * metaphor in detail.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("tube/features/L0_Tube/lifecycle")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.samstraumr.tube.lifecycle.steps")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@CreationLifecycle and @ATL")
public class RunCreationLifecycleATLCucumberTest {
  // This class serves as a test runner and doesn't need any implementation
}
