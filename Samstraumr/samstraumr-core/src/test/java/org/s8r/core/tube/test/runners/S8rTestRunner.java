/**
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * <p>This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy
 * of the MPL was not distributed with this file, You can obtain one at
 * https://github.com/heymumford/Samstraumr/blob/main/LICENSE
 *
 * <p>Base test runner for S8r framework tests
 */
package org.s8r.core.tube.test.runners;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Base test runner for S8r framework tests.
 *
 * <p>This runner serves as a unified entry point for all S8r core tests using JUnit 5 and Cucumber.
 * It detects and runs tests in the specified feature directories with appropriate glue code.
 *
 * <p>Feature files are organized hierarchically by test level:
 *
 * <ul>
 *   <li>L0_Tube: Core tube functionality (atomic tubes, lifecycle, identity)
 *   <li>L1_Composite: Composite tube structures and connections
 *   <li>L2_Machine: Machine orchestration and component interactions
 *   <li>L3_System: System-wide behavior and resilience
 * </ul>
 *
 * <p>Usage examples:
 *
 * <ul>
 *   <li>mvn test -Dtest=S8rTestRunner (runs all tests)
 *   <li>mvn test -Dtest=S8rTestRunner -Dcucumber.filter.tags="@ATL" (runs ATL tests only)
 *   <li>mvn test -Dtest=S8rTestRunner -Dcucumber.filter.tags="@AtomicTube" (runs atomic tube tests
 *       only)
 * </ul>
 */
@Suite
@SuiteDisplayName("S8r Framework Tests")
@IncludeEngines("cucumber")
@SelectClasspathResource("tube/features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.s8r.core.tube.test.steps")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value =
        "pretty, html:target/cucumber-reports/s8r-cucumber.html, json:target/cucumber-reports/s8r-cucumber.json")
public class S8rTestRunner {}
