/**
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * <p>This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy
 * of the MPL was not distributed with this file, You can obtain one at
 * https://github.com/heymumford/Samstraumr/blob/main/LICENSE
 *
 * <p>Test runner for critical (ATL) tests
 */
package org.s8r.core.tube.test.runners;

import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Test runner for critical (ATL) tests.
 *
 * <p>This runner focuses on the Above The Line (ATL) tests, which are critical tests that must pass
 * for every build. ATL tests form the core safety net for the codebase.
 *
 * <p>Feature directories included:
 *
 * <ul>
 *   <li>tube/features: All tube feature files
 * </ul>
 *
 * <p>Usage examples:
 *
 * <ul>
 *   <li>mvn test -Dtest=CriticalTestRunner (runs all ATL tests)
 *   <li>mvn test -Dtest=CriticalTestRunner -Dcucumber.filter.tags="@ATL and @L0_Tube" (runs
 *       critical L0 tests)
 *   <li>mvn test -Dtest=CriticalTestRunner -Dcucumber.filter.tags="@ATL and @Positive" (runs
 *       critical positive tests)
 * </ul>
 */
@Suite
@SuiteDisplayName("S8r Critical Tests (ATL)")
@IncludeEngines("cucumber")
@SelectClasspathResource("tube/features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.s8r.core.tube.test.steps")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@ATL")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value =
        "pretty, html:target/cucumber-reports/critical-cucumber.html, json:target/cucumber-reports/critical-cucumber.json")
public class CriticalTestRunner {}
