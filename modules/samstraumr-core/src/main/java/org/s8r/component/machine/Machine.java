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

package org.s8r.component.machine;

import org.s8r.component.Composite;
import org.s8r.component.Environment;

/**
 * Highest level of composition in the S8r architecture, orchestrating multiple Composites.
 *
 * <p>This class extends the base Machine functionality with additional features specific to the
 * clean architecture component.machine package. It provides a more focused implementation for the
 * Clean Architecture component layer.
 */
public class Machine extends org.s8r.component.Machine {

  /**
   * Creates a new Machine with the specified identifier in the given environment.
   *
   * @param machineId The unique identifier for this machine
   * @param environment The environment in which this machine operates
   */
  public Machine(String machineId, Environment environment) {
    super(machineId, environment);
  }

  /**
   * Adds a composite to this machine with additional validation and integration logic.
   *
   * @param name The name to reference this composite by within the machine
   * @param composite The composite to add
   * @return This machine instance for method chaining
   */
  @Override
  public Machine addComposite(String name, Composite composite) {
    logEvent("Adding composite to Clean Architecture Machine implementation: " + name);
    return (Machine) super.addComposite(name, composite);
  }
}
