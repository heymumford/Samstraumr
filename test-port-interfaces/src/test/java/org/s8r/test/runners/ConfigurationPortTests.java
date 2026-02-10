/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.test.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test runner specifically for ConfigurationPort tests.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = {"classpath:features/port-interfaces/configuration-port-test.feature"},
    glue = {"org.s8r.test.steps"},
    plugin = {
        "pretty",
        "html:target/cucumber/configuration-port-tests.html",
        "json:target/cucumber/configuration-port-tests.json"
    },
    tags = "@Configuration"
)
public class ConfigurationPortTests {
    // This class serves as a test runner only
}