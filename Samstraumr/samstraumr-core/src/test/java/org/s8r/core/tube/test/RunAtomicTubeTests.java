/**
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * <p>This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy
 * of the MPL was not distributed with this file, You can obtain one at
 * https://github.com/heymumford/Samstraumr/blob/main/LICENSE
 *
 * <p>Test runner for Atomic Tube tests in the S8r framework
 */
package org.s8r.core.tube.test;

import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Runner for atomic tube tests in the S8r framework.
 *
 * <p>This runner executes BDD tests for the atomic tube concept, which are standalone tubes without
 * connections to other tubes, composites, or machines.
 *
 * <p>Feature files are located in:
 *
 * <ul>
 *   <li>src/test/resources/tube/features/L0_Tube/lifecycle/atomic-tube-tests.feature
 *   <li>src/test/resources/tube/features/L0_Tube/lifecycle/adam-tube-tests.feature
 * </ul>
 *
 * <p>Simplified usage examples:
 *
 * <ul>
 *   <li>mvn test -Dtest=RunAtomicTubeTests (runs all atomic tube tests)
 *   <li>mvn test -Dtest=RunAtomicTubeTests -Dcucumber.filter.tags="@AtomicTube and @Positive" (runs
 *       positive atomic tube tests)
 *   <li>mvn test -Dtest=RunAtomicTubeTests -Dcucumber.filter.tags="@ATL and @AdamTube" (runs
 *       critical adam tube tests)
 * </ul>
 */
@Suite
@SuiteDisplayName("S8r Atomic Tube Tests")
@IncludeEngines("cucumber")
@SelectClasspathResource("tube/features/L0_Tube/lifecycle")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.s8r.core.tube.test.steps")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@AtomicTube or @AdamTube")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value =
        "pretty, html:target/cucumber-reports/atom-tube-cucumber.html, json:target/cucumber-reports/atom-tube-cucumber.json")
public class RunAtomicTubeTests {}
