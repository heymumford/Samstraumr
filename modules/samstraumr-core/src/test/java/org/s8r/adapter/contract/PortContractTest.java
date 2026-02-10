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
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.mozilla.org/en-US/MPL/2.0/
 */

package org.s8r.adapter.contract;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.s8r.application.port.LoggerPort;
import org.s8r.infrastructure.logging.ConsoleLogger;
import org.s8r.test.annotation.UnitTest;

/**
 * Base abstract class for all Port Contract Tests.
 *
 * <p>This class provides common setup and utilities for testing adapter implementations. Contract
 * tests verify that adapters correctly implement the port interfaces as expected by the application
 * core.
 *
 * <p>All port contract tests should extend this class and implement the specific test methods
 * relevant to the port interface being tested.
 */
@UnitTest
@Tag("L0_Unit")
@Tag("Contract")
@Tag("PortInterface")
public abstract class PortContractTest<T> {

  protected LoggerPort logger;
  protected T portUnderTest;

  /** Common setup for all port contract tests. */
  @BeforeEach
  public void baseSetUp() {
    logger = new ConsoleLogger(this.getClass());
    portUnderTest = createPortImplementation();
  }

  /**
   * Creates a specific implementation of the port interface to be tested. Subclasses must implement
   * this method to provide the port implementation.
   *
   * @return The port implementation to be tested
   */
  protected abstract T createPortImplementation();

  /**
   * Verifies that the implementation handles null inputs correctly. Subclasses should implement
   * this method to verify null handling behavior.
   */
  protected abstract void verifyNullInputHandling();

  /**
   * Verifies that all required methods of the port interface are correctly implemented according to
   * the interface contract.
   */
  protected abstract void verifyRequiredMethods();
}
