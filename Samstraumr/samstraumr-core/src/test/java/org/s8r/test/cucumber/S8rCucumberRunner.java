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

package org.s8r.test.cucumber;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Cucumber test runner specifically for the new S8r framework.
 *
 * <p>This runner provides configuration for running BDD tests for the new S8r component model,
 * separate from the legacy tube model. It focuses on testing the new org.s8r.core package structure
 * and its components.
 *
 * <p>Key features:
 *
 * <ul>
 *   <li>Unified feature file locations
 *   <li>Consistent reporting formats
 *   <li>Specific glue package configuration for S8r components
 * </ul>
 *
 * <p>This runner is specifically designed to work with the new S8r component model and
 * will run all BDD tests that test org.s8r.core classes.
 */
@Suite
@SuiteDisplayName("S8r Framework BDD Tests")
@IncludeEngines("cucumber")
@ConfigurationParameter(
    key = "cucumber.features",
    value = "src/test/resources/core/features")
@ConfigurationParameter(
    key = GLUE_PROPERTY_NAME,
    value = "org.s8r.core.test.steps,org.s8r.test.cucumber.steps")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value = "pretty, html:target/cucumber-reports/s8r-core.html, json:target/cucumber-reports/s8r-core.json")
public class S8rCucumberRunner {}