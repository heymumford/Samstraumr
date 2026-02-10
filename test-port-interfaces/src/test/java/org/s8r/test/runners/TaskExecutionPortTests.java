/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.test.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test runner specifically for TaskExecutionPort tests.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = {"classpath:features/port-interfaces/task-execution-port-test.feature"},
    glue = {"org.s8r.test.steps"},
    plugin = {
        "pretty",
        "html:target/cucumber/task-execution-port-tests.html",
        "json:target/cucumber/task-execution-port-tests.json"
    },
    tags = "@TaskExecution"
)
public class TaskExecutionPortTests {
    // This class serves as a test runner only
}