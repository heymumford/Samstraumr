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

package org.s8r.test.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test runner for ValidationPort and PersistencePort integration tests.
 *
 * <p>This runner configures Cucumber to run the integration tests between
 * ValidationPort and PersistencePort with appropriate plugins and tags.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features/integration/validation-persistence-integration-test.feature",
    glue = {"org.s8r.test.steps"},
    plugin = {
        "pretty",
        "html:target/cucumber-reports/validation-persistence-integration-tests.html",
        "json:target/cucumber-reports/validation-persistence-integration-tests.json"
    },
    tags = "@integration"
)
public class ValidationPersistenceIntegrationTests {
    // No implementation needed - JUnit will run the tests using Cucumber
}