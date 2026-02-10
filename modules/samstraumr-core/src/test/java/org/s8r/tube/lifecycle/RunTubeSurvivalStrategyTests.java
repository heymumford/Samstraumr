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
 * Licensed under the Mozilla Public License 2.0
 */
package org.s8r.tube.lifecycle;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * Test runner for tube survival strategy tests. Runs Cucumber tests defined in the
 * tube-survival-strategies.feature file.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features/tube-lifecycle/tube-survival-strategies.feature",
    glue = {"org.s8r.tube.lifecycle.steps"},
    plugin = {
      "pretty",
      "html:target/cucumber-reports/tube-survival-strategy-report.html",
      "json:target/cucumber-reports/tube-survival-strategy-report.json"
    },
    tags = "@SurvivalStrategy")
public class RunTubeSurvivalStrategyTests {
  // This class serves as a test runner only
}
