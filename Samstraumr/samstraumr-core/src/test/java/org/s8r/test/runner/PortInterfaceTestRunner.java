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

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;

/**
 * Cucumber test runner for Port Interface tests.
 * 
 * <p>This runner executes all BDD feature tests for the port interfaces,
 * focusing on integration tests that verify clean architecture implementation.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/port-interfaces")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.s8r.test.steps")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports/port-interfaces-report.html")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@PortInterface")
public class PortInterfaceTestRunner {
    // The runner class doesn't need any code - configuration is done with annotations
}