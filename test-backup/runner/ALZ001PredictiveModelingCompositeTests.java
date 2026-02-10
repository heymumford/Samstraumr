/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

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

import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.platform.engine.Cucumber;
import org.junit.jupiter.api.DisplayName;

/**
 * JUnit test runner for the ALZ001 Predictive Modeling Composite tests.
 * 
 * <p>This test runner executes all the Cucumber BDD scenarios defined in
 * the alz001-predictive-modeling-composite.feature file, which test the 
 * predictive modeling capabilities in the Alzheimer's disease research 
 * composite layer.
 */
@Cucumber
@CucumberOptions(
    features = "src/test/resources/features/alz001/alz001-predictive-modeling-composite.feature",
    glue = {"org.s8r.test.steps.alz001.composite"},
    plugin = {
        "pretty",
        "html:target/cucumber-reports/alz001-predictive-modeling-composite.html",
        "json:target/cucumber-reports/alz001-predictive-modeling-composite.json"
    },
    tags = "@ATL"
)
@DisplayName("ALZ001 Predictive Modeling Composite Tests")
public class ALZ001PredictiveModelingCompositeTests {
    // Test runner class - implementation managed by JUnit and Cucumber
}