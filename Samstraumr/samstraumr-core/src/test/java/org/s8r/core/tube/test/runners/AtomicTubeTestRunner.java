/**
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * <p>This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy
 * of the MPL was not distributed with this file, You can obtain one at
 * https://github.com/heymumford/Samstraumr/blob/main/LICENSE
 *
 * <p>Test runner specifically for Atomic Tube tests
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
 * Test runner specifically for Atomic Tube tests.
 *
 * <p>This runner focuses on the core atomic tube concept, executing tests related to standalone
 * tube components without connections to other components.
 *
 * <p>Feature files:
 *
 * <ul>
 *   <li>atomic-tube-tests.feature: Basic atomic tube functionality
 *   <li>adam-tube-tests.feature: Adam tube creation and hierarchy
 * </ul>
 *
 * <p>Usage examples:
 *
 * <ul>
 *   <li>mvn test -Dtest=AtomicTubeTestRunner (runs all atomic tube tests)
 *   <li>mvn test -Dtest=AtomicTubeTestRunner -Dcucumber.filter.tags="@Identity" (runs
 *       identity-related tests)
 *   <li>mvn test -Dtest=AtomicTubeTestRunner -Dcucumber.filter.tags="@Positive" (runs positive test
 *       cases)
 * </ul>
 */
@Suite
@SuiteDisplayName("Atomic Tube Tests")
@IncludeEngines("cucumber")
@SelectClasspathResource("tube/features/L0_Tube/lifecycle")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.s8r.core.tube.test.steps")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@AtomicTube or @AdamTube")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value =
        "pretty, html:target/cucumber-reports/atomic-tube-cucumber.html, json:target/cucumber-reports/atomic-tube-cucumber.json")
public class AtomicTubeTestRunner {}
