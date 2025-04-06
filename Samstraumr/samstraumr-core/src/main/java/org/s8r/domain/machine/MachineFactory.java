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

package org.s8r.domain.machine;

import org.s8r.domain.identity.ComponentId;

/**
 * Factory for creating machines with predefined configurations.
 *
 * <p>This factory provides methods for creating different types of machines with appropriate
 * configurations. It follows Clean Architecture principles with no dependencies on infrastructure
 * or frameworks.
 */
public class MachineFactory {

  /**
   * Creates a data processor machine.
   *
   * @param name The machine name
   * @param description The machine description
   * @return A new data processor machine
   */
  public static Machine createDataProcessor(String name, String description) {
    ComponentId id = ComponentId.create("DataProcessor: " + name);
    return Machine.create(id, MachineType.DATA_PROCESSOR, name, description, "1.0.0");
  }

  /**
   * Creates an analytics machine.
   *
   * @param name The machine name
   * @param description The machine description
   * @return A new analytics machine
   */
  public static Machine createAnalytics(String name, String description) {
    ComponentId id = ComponentId.create("Analytics: " + name);
    return Machine.create(id, MachineType.ANALYTICS, name, description, "1.0.0");
  }

  /**
   * Creates an integration machine.
   *
   * @param name The machine name
   * @param description The machine description
   * @return A new integration machine
   */
  public static Machine createIntegration(String name, String description) {
    ComponentId id = ComponentId.create("Integration: " + name);
    return Machine.create(id, MachineType.INTEGRATION, name, description, "1.0.0");
  }

  /**
   * Creates a workflow machine.
   *
   * @param name The machine name
   * @param description The machine description
   * @return A new workflow machine
   */
  public static Machine createWorkflow(String name, String description) {
    ComponentId id = ComponentId.create("Workflow: " + name);
    return Machine.create(id, MachineType.WORKFLOW, name, description, "1.0.0");
  }

  /**
   * Creates a monitoring machine.
   *
   * @param name The machine name
   * @param description The machine description
   * @return A new monitoring machine
   */
  public static Machine createMonitoring(String name, String description) {
    ComponentId id = ComponentId.create("Monitoring: " + name);
    return Machine.create(id, MachineType.MONITORING, name, description, "1.0.0");
  }

  /**
   * Creates a machine of the specified type.
   *
   * @param type The machine type
   * @param name The machine name
   * @param description The machine description
   * @param version The machine version
   * @return A new machine of the specified type
   */
  public static Machine createMachine(
      MachineType type, String name, String description, String version) {
    ComponentId id = ComponentId.create(type.name() + ": " + name);
    return Machine.create(id, type, name, description, version);
  }
}
