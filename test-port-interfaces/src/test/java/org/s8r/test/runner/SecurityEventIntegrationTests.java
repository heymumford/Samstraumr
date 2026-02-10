/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.test.runner;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * Test runner for Security-Event integration tests.
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
        "org.s8r.test.steps"
    },
    tags = "@L2_Integration and @Security and @Event"
)
public class SecurityEventIntegrationTests {
    // No implementation needed
}