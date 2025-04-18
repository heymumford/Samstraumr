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

package org.s8r.component.test;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Main Cucumber test runner for executing BDD tests across all component implementations.
 *
 * <p>This runner supports the test categorization strategy with multiple levels, allowing for
 * selective test execution:
 *
 * <ul>
 *   <li>L0_Core: Core component tests
 *   <li>L1_Composite: Composite component tests
 *   <li>L2_Machine: Machine orchestration tests
 * </ul>
 *
 * <p>Test execution can be filtered using the cucumber.filter.tags system property:
 *
 * <ul>
 *   <li>Run ATL tests: -Dcucumber.filter.tags="@ATL"
 *   <li>Run core tests: -Dcucumber.filter.tags="@L0_Core"
 *   <li>Run composite tests: -Dcucumber.filter.tags="@L1_Composite"
 *   <li>Run machine tests: -Dcucumber.filter.tags="@L2_Machine"
 *   <li>Run combinations: -Dcucumber.filter.tags="@ATL and @L0_Core"
 * </ul>
 *
 * <p>Feature files are organized hierarchically in the following directories:
 *
 * <ul>
 *   <li>component/features/L0_Core: Core component features
 *   <li>component/features/L1_Composite: Composite component features
 *   <li>component/features/L2_Machine: Machine orchestration features
 * </ul>
 *
 * <p>Simplified usage examples:
 *
 * <ul>
 *   <li>mvn test -Dtest=RunComponentTests -Dcucumber.filter.tags="@ATL and @L0_Core"
 *   <li>mvn test -Dtest=RunComponentTests -Dcucumber.filter.tags="@Lifecycle"
 *   <li>mvn test -Dtest=RunComponentTests -Dcucumber.filter.tags="@ATL and @L1_Composite"
 * </ul>
 */
@Suite
@SuiteDisplayName("S8r Component BDD Tests")
@IncludeEngines("cucumber")
@ConfigurationParameter(key = "cucumber.features", value = "src/test/resources/component/features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.s8r.component.test.steps")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value =
        "pretty, html:target/cucumber-reports/component-cucumber.html, json:target/cucumber-reports/component-cucumber.json")
public class RunComponentTests {}
