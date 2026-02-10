/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.test.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test runner for port interface tests.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = {"classpath:features/port-interfaces"},
    glue = {"org.s8r.test.steps"},
    plugin = {
        "pretty",
        "html:target/cucumber/port-interface-tests.html",
        "json:target/cucumber/port-interface-tests.json"
    },
    tags = "@PortTest"
)
public class PortInterfaceTests {
    // This class serves as a test runner only
}