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

package org.s8r.test.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test runner for ValidationPort interface tests.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = {"classpath:features/port-interfaces/validation-port-test.feature"},
    glue = {"org.s8r.test.steps"},
    plugin = {
        "pretty",
        "html:target/cucumber/validation-port-tests.html",
        "json:target/cucumber/validation-port-tests.json"
    },
    tags = "@Validation"
)
public class ValidationPortTests {
    // This class serves as a test runner only
}