/**
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * <p>This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy
 * of the MPL was not distributed with this file, You can obtain one at
 * https://github.com/heymumford/Samstraumr/blob/main/LICENSE
 *
 * <p>Test runner specifically for Adam Tube tests
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
 * Test runner specifically for Adam Tube tests.
 *
 * <p>This runner focuses on the Adam tube concept, which represents the originating ancestor tube
 * that can create hierarchies of descendant tubes.
 *
 * <p>Feature files:
 *
 * <ul>
 *   <li>adam-tube-tests.feature: Adam tube creation and hierarchy relationships
 * </ul>
 *
 * <p>Usage examples:
 *
 * <ul>
 *   <li>mvn test -Dtest=AdamTubeTestRunner (runs all Adam tube tests)
 *   <li>mvn test -Dtest=AdamTubeTestRunner -Dcucumber.filter.tags="@Hierarchy" (runs
 *       hierarchy-related tests)
 *   <li>mvn test -Dtest=AdamTubeTestRunner -Dcucumber.filter.tags="@ATL" (runs critical Adam tube
 *       tests)
 * </ul>
 */
@Suite
@SuiteDisplayName("Adam Tube Tests")
@IncludeEngines("cucumber")
@SelectClasspathResource("tube/features/L0_Tube/lifecycle")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.s8r.core.tube.test.steps")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@AdamTube")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value =
        "pretty, html:target/cucumber-reports/adam-tube-cucumber.html, json:target/cucumber-reports/adam-tube-cucumber.json")
public class AdamTubeTestRunner {}
