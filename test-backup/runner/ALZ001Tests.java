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
 * Test runner for ALZ001 tests.
 * 
 * <p>This runner executes all ALZ001 tagged scenarios, which focus on modeling
 * Alzheimer's disease using combinations of tubes, composites, and machines.
 * 
 * <p>Configuration is loaded from the cucumber-alz001.properties file at:
 * /home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/resources/cucumber-alz001.properties
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/alz001")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "org.s8r.test.steps.alz001")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = Constants.FILTER_TAGS_PROPERTY_NAME, value = "@ALZ001")
@ConfigurationParameter(key = Constants.CONFIG_LOCATION_PROPERTY_NAME, value = "classpath:cucumber-alz001.properties")
public class ALZ001Tests {
  // This class is intentionally empty. It's used only as a holder for the annotations.
}