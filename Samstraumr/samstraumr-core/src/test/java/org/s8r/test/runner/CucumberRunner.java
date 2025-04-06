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

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Base Cucumber test runner for executing BDD tests across all components.
 *
 * <p>This runner provides common configuration for all Cucumber test runners, with support for
 * environment customization. Extend this class to create specific test runners for different test
 * categories.
 *
 * <p>Key features:
 *
 * <ul>
 *   <li>Unified feature file locations
 *   <li>Consistent reporting formats
 *   <li>Standard glue package configuration
 * </ul>
 *
 * <p>This is part of the package structure simplification initiative, centralizing test runners
 * into a single package under the simplified org.s8r root package.
 */
@Suite
@SuiteDisplayName("S8r BDD Tests")
@IncludeEngines("cucumber")
@ConfigurationParameter(
    key = "cucumber.features",
    value =
        "src/test/resources/tube/features, src/test/resources/composites/features, src/test/resources/test")
@ConfigurationParameter(
    key = GLUE_PROPERTY_NAME,
    value =
        "org.s8r.tube.steps,org.s8r.tube.lifecycle.steps,org.s8r.test.steps,org.s8r.core.tube.test.steps")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value =
        "pretty, html:target/cucumber-reports/cucumber.html, json:target/cucumber-reports/cucumber.json")
public class CucumberRunner {}
