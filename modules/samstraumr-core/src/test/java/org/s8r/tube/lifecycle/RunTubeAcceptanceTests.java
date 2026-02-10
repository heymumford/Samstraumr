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
 * Test runner for tube acceptance tests from the comprehensive acceptance test suite. Runs Cucumber
 * tests defined in the tube-acceptance-tests.feature file.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features/tube-lifecycle/tube-acceptance-tests.feature",
    glue = {"org.s8r.tube.lifecycle.steps"},
    plugin = {
      "pretty",
      "html:target/cucumber-reports/tube-acceptance-tests-report.html",
      "json:target/cucumber-reports/tube-acceptance-tests-report.json"
    },
    tags = "@TubeAcceptance")
public class RunTubeAcceptanceTests {
  // This class serves as a test runner only
}

/**
 * Test runner for tube-based business data processing acceptance tests. Runs Cucumber tests defined
 * in the acceptance-test.feature file.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/tube/features/acceptance-test.feature",
    glue = {"org.s8r.tube.steps"},
    plugin = {
      "pretty",
      "html:target/cucumber-reports/tube-business-acceptance-report.html",
      "json:target/cucumber-reports/tube-business-acceptance-report.json"
    },
    tags = "@TubeAcceptance")
class RunTubeBusinessAcceptanceTests {
  // This class serves as a test runner only
}

/**
 * Test runner for all tube acceptance tests across the system. Runs all Cucumber tests tagged with
 * TubeAcceptance.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = {
      "src/test/resources/features/tube-lifecycle/tube-acceptance-tests.feature",
      "src/test/resources/tube/features/acceptance-test.feature"
    },
    glue = {"org.s8r.tube.lifecycle.steps", "org.s8r.tube.steps"},
    plugin = {
      "pretty",
      "html:target/cucumber-reports/all-tube-acceptance-report.html",
      "json:target/cucumber-reports/all-tube-acceptance-report.json"
    },
    tags = "@TubeAcceptance")
class RunAllTubeAcceptanceTests {
  // This class serves as a test runner only
}
