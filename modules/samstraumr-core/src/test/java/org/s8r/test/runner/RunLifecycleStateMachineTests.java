/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools 
 * including Claude 3.7 Sonnet, Claude Code, and GitHub Copilot Pro,
 * which were used as paid services. All intellectual property rights 
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0
 */
package org.s8r.test.runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;

/**
 * Test runner for Component lifecycle state machine tests.
 * 
 * <p>This runner executes the feature scenarios that verify the Component state transitions
 * and state-dependent behavior.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/L0_Lifecycle/lifecycle-state-machine.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.s8r.test.steps.lifecycle")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, json:target/cucumber-reports/lifecycle-state-machine.json")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@L0_Lifecycle and @StateMachine")
public class RunLifecycleStateMachineTests {
    // This class serves as a test runner for the Cucumber JUnit Platform Engine.
    // No additional code is needed here.
}