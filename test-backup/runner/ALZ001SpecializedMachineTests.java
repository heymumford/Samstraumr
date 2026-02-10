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
import org.junit.jupiter.api.DisplayName;

import io.cucumber.junit.platform.engine.Cucumber;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;

/**
 * Test runner for ALZ001 Specialized Machine tests.
 *
 * <p>This class runs Cucumber tests for the specialized machine implementations
 * in the ALZ001 test suite, focused on domain-specific research areas such as
 * protein aggregation, network connectivity, temporal progression, etc.</p>
 * 
 * <p>These tests validate that the machine layer can support domain-specific
 * configurations and requirements for different areas of Alzheimer's research.</p>
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/alz001/alz001-specialized-machines.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.s8r.test.steps.alz001.machine,org.s8r.test.steps.alz001")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty,html:target/cucumber-reports/alz001-specialized-machines.html,json:target/cucumber-reports/alz001-specialized-machines.json")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@ALZ001 and @L2_Machine and @Specialized")
@DisplayName("ALZ001 Specialized Machine Tests (L2)")
@Cucumber
public class ALZ001SpecializedMachineTests {
    // Test runner class - implementation managed by JUnit and Cucumber
}