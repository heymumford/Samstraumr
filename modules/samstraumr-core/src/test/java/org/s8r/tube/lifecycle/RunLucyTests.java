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
package org.s8r.tube.lifecycle;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * Test runner specifically for the Lucy tests within the tube survival strategy feature. These
 * tests focus on deeper aspects of tube identity, consciousness, and legacy.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features/tube-lifecycle/tube-survival-strategies.feature",
    glue = {"org.s8r.tube.lifecycle.steps"},
    plugin = {
      "pretty",
      "html:target/cucumber-reports/lucy-tests-report.html",
      "json:target/cucumber-reports/lucy-tests-report.json"
    },
    tags = "@Identity or @Consciousness or @Legacy or @ComprehensiveAnalysis")
public class RunLucyTests {
  // This class serves as a test runner for the Lucy test suite
}
