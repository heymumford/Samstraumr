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

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * Test runner for Lifecycle tests. Runs the P1 priority test cases for Component Lifecycle
 * features.
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
      "org.s8r.test.steps.lifecycle",
      "org.s8r.tube.steps",
      "org.s8r.tube.lifecycle.steps",
      "org.s8r.test.steps"
    },
    plugin = {
      "pretty",
      "html:target/cucumber-reports/lifecycle",
      "json:target/cucumber-reports/lifecycle.json"
    },
    tags = "@L0_Lifecycle or @Lifecycle or @EarlyConception or @Identity and @Lifecycle")
public class RunLifecycleTests {
  // No implementation needed
}
