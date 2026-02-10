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

import io.cucumber.junit.platform.engine.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.jupiter.api.DisplayName;

/**
 * Enhanced integration test runner for the ALZ001 composite components.
 * 
 * <p>This test runner executes scenarios that test the integration of all five composites:
 * protein expression, neuronal networks, time series analysis, environmental factors, and 
 * predictive modeling into a unified disease modeling system.
 * 
 * <p>The tests focus on cross-composite interactions, data flow between subsystems,
 * and emergent behaviors from the integrated system.
 */
@Cucumber
@CucumberOptions(
    features = "src/test/resources/features/alz001/alz001-enhanced-system-integration.feature",
    glue = {"org.s8r.test.steps.alz001"},
    plugin = {
        "pretty",
        "html:target/cucumber-reports/alz001-enhanced-system-integration.html",
        "json:target/cucumber-reports/alz001-enhanced-system-integration.json"
    },
    tags = "@ALZ001 and @EnhancedSystemIntegration"
)
@DisplayName("ALZ001 Enhanced System Integration Tests")
public class ALZ001EnhancedSystemIntegrationTests {
    // Test runner class - implementation managed by JUnit and Cucumber
}