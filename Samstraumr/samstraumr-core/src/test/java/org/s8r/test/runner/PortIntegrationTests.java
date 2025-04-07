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

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test runner for port integration tests.
 * 
 * This runner executes all port integration tests that verify
 * interactions between different port interfaces in the Clean Architecture.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features/integration",
    glue = {"org.s8r.test.steps"},
    plugin = {
        "pretty",
        "html:target/cucumber-reports/port-integration.html",
        "json:target/cucumber-reports/port-integration.json"
    },
    tags = "@L3_Integration and @PortIntegration",
    monochrome = true
)
public class PortIntegrationTests {
    // This class serves as a test runner configuration only
}
