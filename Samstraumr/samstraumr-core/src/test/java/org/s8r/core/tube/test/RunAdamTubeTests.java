/**
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * <p>This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy
 * of the MPL was not distributed with this file, You can obtain one at
 * https://github.com/heymumford/Samstraumr/blob/main/LICENSE
 *
 * <p>Test runner for Adam Tube tests in the S8r framework
 */
package org.s8r.core.tube.test;

import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Runner for Adam tube tests in the S8r framework.
 *
 * <p>This runner executes BDD tests for the Adam tube concept, which are the first-created tubes
 * (origin tubes) that become part of composites.
 *
 * <p>Feature files are located in:
 *
 * <ul>
 *   <li>src/test/resources/tube/features/L0_Tube/lifecycle/adam-tube-tests.feature
 * </ul>
 *
 * <p>Simplified usage examples:
 *
 * <ul>
 *   <li>mvn test -Dtest=RunAdamTubeTests (runs all Adam tube tests)
 *   <li>mvn test -Dtest=RunAdamTubeTests -Dcucumber.filter.tags="@AdamTube and @Positive" (runs
 *       positive Adam tube tests)
 *   <li>mvn test -Dtest=RunAdamTubeTests -Dcucumber.filter.tags="@AdamTube and @Identity" (runs
 *       Adam tube identity tests)
 * </ul>
 */
@Suite
@SuiteDisplayName("S8r Adam Tube Tests")
@IncludeEngines("cucumber")
@ConfigurationParameter(
    key = "cucumber.features",
    value = "src/test/resources/tube/features/L0_Tube/lifecycle/adam-tube-tests.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.s8r.core.tube.test.steps")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@AdamTube")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value =
        "pretty, html:target/cucumber-reports/adam-tube-cucumber.html, json:target/cucumber-reports/adam-tube-cucumber.json")
public class RunAdamTubeTests {}
