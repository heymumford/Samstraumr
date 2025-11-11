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
 * Test runner for all Lifecycle tests across the codebase.
 * This is a comprehensive runner that includes all lifecycle-related feature files
 * and all step definition classes that may contain relevant implementation methods.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = {
        "classpath:features/L0_Lifecycle/",
        "classpath:features/lifecycle-unified/",
        "classpath:features/tube-lifecycle/",
        "classpath:tube/features/L0_Tube/lifecycle/"
    },
    glue = {
        "org.s8r.test.steps",
        "org.s8r.test.steps.lifecycle",
        "org.s8r.test.steps.identity",
        "org.s8r.test.steps.component",
        "org.s8r.test.steps.errorhandling",
        "org.s8r.tube.steps",
        "org.s8r.tube.lifecycle.steps"
    },
    plugin = {
        "pretty", 
        "html:target/cucumber-reports/lifecycle",
        "json:target/cucumber-reports/lifecycle.json"
    },
    tags = "@Lifecycle or @EarlyConception or @Identity or @L0_Lifecycle"
)
public class RunComprehensiveLifecycleTests {
    // No implementation needed
}