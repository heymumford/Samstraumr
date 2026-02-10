/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.test.runner;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/** Test runner for port interface tests. */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features/port-interfaces",
    glue = {"org.s8r.test.steps"},
    plugin = {"pretty", "json:target/cucumber-reports/port-tests.json"},
    tags = "@port")
public class PortTests {
  // This class is intentionally empty. It's used only as a holder for the annotations.
}
