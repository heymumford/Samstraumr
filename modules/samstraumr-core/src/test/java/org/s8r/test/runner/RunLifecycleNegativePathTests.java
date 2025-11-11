/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools 
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights 
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0
 */
package org.s8r.test.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test runner specifically for lifecycle negative path tests.
 * This provides a focused runner for testing error handling and exceptional conditions
 * in component lifecycle.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features/L0_Lifecycle/lifecycle-negative-paths.feature",
    glue = {"org.s8r.test.steps.lifecycle"},
    plugin = {
        "pretty", 
        "html:target/cucumber-reports/lifecycle-negative-paths",
        "json:target/cucumber-reports/lifecycle-negative-paths.json"
    },
    tags = "@L0_Lifecycle and @ErrorHandling or @BTL",
    monochrome = true
)
public class RunLifecycleNegativePathTests {
    // This class is empty as it's just a runner
}