/**
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * <p>This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy
 * of the MPL was not distributed with this file, You can obtain one at
 * https://github.com/heymumford/Samstraumr/blob/main/LICENSE
 *
 * <p>Runner for ATL (Above The Line) component tests in the S8r framework
 */
package org.s8r.component.test;

import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Runner for critical ATL (Above The Line) component tests only.
 *
 * <p>This runner executes only the tests marked with the @ATL tag, which represent critical tests
 * that must pass with every build. These tests are designed to be fast, reliable, and focused on
 * core functionality.
 *
 * <p>Feature files are organized hierarchically based on test level:
 *
 * <ul>
 *   <li>L0_Core: Core component tests
 *   <li>L1_Composite: Composite component tests
 *   <li>L2_Machine: Machine orchestration tests
 * </ul>
 *
 * <p>Simplified usage examples:
 *
 * <ul>
 *   <li>mvn test -Dtest=RunATLComponentTests (runs all ATL tests)
 *   <li>mvn test -Dtest=RunATLComponentTests -Dcucumber.filter.tags="@ATL and @L0_Core" (runs ATL
 *       core tests)
 * </ul>
 */
@Suite
@SuiteDisplayName("S8r Component ATL (Critical) Tests")
@IncludeEngines("cucumber")
@ConfigurationParameter(key = "cucumber.features", value = "src/test/resources/component/features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.s8r.component.test.steps")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@ATL")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value =
        "pretty, html:target/cucumber-reports/component-atl-cucumber.html, json:target/cucumber-reports/component-atl-cucumber.json")
public class RunATLComponentTests {}
