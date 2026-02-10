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
 * Master test runner for all ALZ001 tests across all layers.
 *
 * <p>This class runs Cucumber tests for the complete ALZ001 test suite,
 * including all composites (L1), the system simulation machine (L2),
 * and the integrated system tests (L3).</p>
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/alz001")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.s8r.test.steps.alz001,org.s8r.test.steps.alz001.system,org.s8r.test.steps.alz001.machine,org.s8r.test.steps.alz001.composite")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty,html:target/cucumber-reports/alz001-all-tests.html,json:target/cucumber-reports/alz001-all-tests.json")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@ALZ001")
@DisplayName("ALZ001 - Complete Test Suite (L1, L2, L3)")
@Cucumber
public class ALZ001AllTests {
    // Test runner class - implementation managed by JUnit and Cucumber
}