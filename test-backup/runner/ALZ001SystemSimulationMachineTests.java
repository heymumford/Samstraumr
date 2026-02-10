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
 * Test runner for ALZ001 System Simulation Machine tests.
 *
 * <p>This class runs Cucumber tests for the SystemSimulationMachine 
 * which coordinates the interactions of all five composites in the ALZ001 test suite.
 * It targets the L2_Machine tier of the testing pyramid.</p>
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/alz001/alz001-system-simulation-machine.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.s8r.test.steps.alz001.machine,org.s8r.test.steps.alz001")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty,html:target/cucumber-reports/alz001-system-simulation-machine.html,json:target/cucumber-reports/alz001-system-simulation-machine.json")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@ALZ001 and @L2_Machine")
@DisplayName("ALZ001 System Simulation Machine Tests (L2)")
@Cucumber
public class ALZ001SystemSimulationMachineTests {
    // Test runner class - implementation managed by JUnit and Cucumber
}