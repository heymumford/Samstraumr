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
 * Master test runner for all ALZ001 composite tests in the test suite.
 * 
 * <p>This test runner executes the BDD scenarios for all five ALZ001 composites:
 * <ul>
 *   <li>Protein Expression Composite</li>
 *   <li>Neuronal Network Composite</li>
 *   <li>Time Series Analysis Composite</li>
 *   <li>Environmental Factors Composite</li>
 *   <li>Predictive Modeling Composite</li>
 * </ul>
 * 
 * <p>This provides a unified way to run all composite tests to ensure complete
 * coverage of the ALZ001 system.
 */
@Cucumber
@CucumberOptions(
    features = {
        "src/test/resources/features/alz001/alz001-protein-expression-composite.feature",
        "src/test/resources/features/alz001/alz001-neuronal-network-composite.feature",
        "src/test/resources/features/alz001/alz001-time-series-composite.feature",
        "src/test/resources/features/alz001/alz001-environmental-factors-composite.feature",
        "src/test/resources/features/alz001/alz001-predictive-modeling-composite.feature"
    },
    glue = {"org.s8r.test.steps.alz001.composite"},
    plugin = {
        "pretty",
        "html:target/cucumber-reports/alz001-all-composites.html",
        "json:target/cucumber-reports/alz001-all-composites.json"
    },
    tags = "@ALZ001 and @L1_Composite or @L2_Composite"
)
@DisplayName("ALZ001 - All Composite Tests")
public class ALZ001AllCompositeTests {
    // Test runner class - implementation managed by JUnit and Cucumber
}