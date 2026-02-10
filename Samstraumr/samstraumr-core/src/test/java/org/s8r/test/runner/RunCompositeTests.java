/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025 Eric C. Mumford
 *
 * Licensed under the Mozilla Public License 2.0
 */
package org.s8r.test.runner;

import io.cucumber.junit.platform.engine.Cucumber;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test runner for composite component tests.
 * Executes all L1_Composite level tests.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features/L1_Composite",
    glue = {"org.s8r.test.steps.composite"},
    plugin = {
        "pretty",
        "html:target/cucumber-reports/composite-tests.html",
        "json:target/cucumber-reports/composite-tests.json"
    },
    tags = "@L1_Composite"
)
public class RunCompositeTests {
    // This class serves as an entry point for running composite tests
}