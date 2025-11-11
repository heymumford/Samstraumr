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
 * Test runner for Error Handling tests.
 * Runs the P1 priority test cases for Error Handling features.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = {
        "classpath:features/L0_ErrorHandling/"
    },
    glue = {
        "org.s8r.test.steps.errorhandling"
    },
    plugin = {
        "pretty", 
        "html:target/cucumber-reports/error-handling",
        "json:target/cucumber-reports/error-handling.json"
    },
    tags = "@L0_ErrorHandling"
)
public class RunErrorHandlingTests {
    // No implementation needed
}