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
 * Basic test runner for Lifecycle tests.
 * This runner excludes ALZ001 and other advanced test cases that have compilation issues.
 * Use this for basic verification until the more complex test modules are fixed.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = {
        "classpath:features/L0_Lifecycle/lifecycle-states.feature"
    },
    glue = {
        "org.s8r.test.steps.lifecycle",
        "org.s8r.test.steps"
    },
    plugin = {
        "pretty", 
        "html:target/cucumber-reports/basic-lifecycle",
        "json:target/cucumber-reports/basic-lifecycle.json"
    },
    monochrome = true
)
public class RunBasicLifecycleTests {
    // No implementation needed
}