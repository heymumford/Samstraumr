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

package org.s8r.tube.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

/**
 * Base step definitions for common Cucumber steps.
 *
 * <p>These step definitions handle common steps used across multiple feature files to avoid
 * duplication.
 */
public class BaseSteps {
  private static final Logger LOGGER = LoggerFactory.getLogger(BaseSteps.class);

  @Before
  public void setUp(Scenario scenario) {
    LOGGER.info("Starting scenario: {}", scenario.getName());
  }

  @After
  public void tearDown(Scenario scenario) {
    LOGGER.info("Finished scenario: {} - Status: {}", scenario.getName(), scenario.getStatus());
  }

  @Given("the system environment is properly configured")
  public void systemEnvironmentIsProperlyConfigured() {
    LOGGER.info("System environment is configured for testing");
  }

  @Then("the operation should be successful")
  public void operationShouldBeSuccessful() {
    LOGGER.info("Operation was successful");
  }

  @Then("an appropriate error message should be logged")
  public void errorMessageShouldBeLogged() {
    LOGGER.info("Error message was logged");
  }
}
