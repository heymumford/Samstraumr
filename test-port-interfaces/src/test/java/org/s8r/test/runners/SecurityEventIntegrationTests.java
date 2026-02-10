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

package org.s8r.test.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test runner for the Security-Event integration tests.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    plugin = {
        "pretty",
        "html:target/cucumber-reports/security-event-integration.html",
        "json:target/cucumber-reports/security-event-integration.json"
    },
    features = "src/test/resources/features/integration/security-event-integration-test.feature",
    glue = {
        "org.s8r.test.steps",
        "org.s8r.test.config"
    },
    tags = "@L2_Integration and @Security and @Event"
)
public class SecurityEventIntegrationTests {
    // No implementation needed
}