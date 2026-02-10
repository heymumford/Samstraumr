/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.test.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test runner for the TaskNotification integration tests.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    plugin = {
        "pretty",
        "html:target/cucumber-reports/task-notification-integration.html",
        "json:target/cucumber-reports/task-notification-integration.json"
    },
    features = "src/test/resources/features/integration/task-notification-integration-test.feature",
    glue = {
        "org.s8r.test.steps"
    },
    tags = "@L2_Integration and @TaskExecution and @Notification"
)
public class TaskNotificationIntegrationTests {
    // No implementation needed
}