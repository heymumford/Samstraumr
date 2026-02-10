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
 * Test runner for ALZ001 tests focused on Alzheimer's disease modeling.
 * 
 * <p>This test runner executes the Cucumber feature tests for verifying
 * the ALZ001 scientific modeling capabilities in the Samstraumr project. 
 * It uses the ALZ001 tag to filter for the relevant feature files.
 * 
 * <p>The tests validate:
 * <ul>
 *   <li>Protein expression modeling</li>
 *   <li>Neuronal network simulation</li>
 *   <li>Time series analysis</li>
 *   <li>Environmental factor integration</li>
 *   <li>Predictive disease modeling</li>
 * </ul>
 * 
 * <p>The ALZ001 test suite covers the full spectrum of the testing pyramid,
 * from individual components to complete system integration.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/alz001")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "org.s8r.test.steps.alz001")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = Constants.FILTER_TAGS_PROPERTY_NAME, value = "@ALZ001")
public class ALZ001Tests {
  // This class is intentionally empty. It's used only as a holder for the annotations.
}