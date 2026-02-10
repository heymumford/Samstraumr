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
 * Test runner for port interface performance tests.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    plugin = {
        "pretty",
        "html:target/cucumber-reports/port-performance.html",
        "json:target/cucumber-reports/port-performance.json"
    },
    features = "src/test/resources/features/performance",
    glue = {
        "org.s8r.test.steps"
    },
    tags = "@Performance"
)
public class PortPerformanceTests {
    // No implementation needed
}