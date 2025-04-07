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

package org.s8r.test.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test runner for Atomic Tube Identity tests.
 * 
 * This runner executes all tests related to the identity aspects of Atomic Tubes,
 * including substrate identity, memory identity, narrative identity, etc.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features/identity/atomic-tube",
    glue = {"org.s8r.test.steps.identity"},
    plugin = {
        "pretty",
        "html:target/cucumber-reports/atomic-tube-identity.html",
        "json:target/cucumber-reports/atomic-tube-identity.json"
    },
    tags = "@Tube and @Identity and @AtomicTube",
    monochrome = true
)
public class AtomicTubeIdentityTests {
    // This class serves as a test runner configuration only
}