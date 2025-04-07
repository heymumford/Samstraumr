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
import org.s8r.test.annotation.ComponentTest;
import org.s8r.test.annotation.IdentityTest;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;

/**
 * Test runner for atomic component identity tests.
 * This runs all the atomic component identity feature tests.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/identity/atomic-component")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.s8r.test.steps")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports/atomic-component-identity.html")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@ComponentIdentity")
@ComponentTest
@IdentityTest
public class AtomicComponentIdentityTests {
    // This class is a test runner, so no implementation needed
}