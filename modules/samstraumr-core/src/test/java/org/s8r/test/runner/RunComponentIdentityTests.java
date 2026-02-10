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
 * Test runner for Component and Identity tests. Runs the critical P0 test cases for Component and
 * Identity features.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = {"classpath:features/L0_Component/", "classpath:features/L0_Identity/"},
    glue = {"org.s8r.test.steps.component", "org.s8r.test.steps.identity"},
    plugin = {
      "pretty",
      "html:target/cucumber-reports/components-identity",
      "json:target/cucumber-reports/components-identity.json"
    },
    tags = "@L0_Component or @L0_Identity")
public class RunComponentIdentityTests {
  // No implementation needed
}
