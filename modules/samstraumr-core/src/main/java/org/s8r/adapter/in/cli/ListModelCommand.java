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

package org.s8r.adapter.in.cli;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.s8r.application.ServiceLocator;
import org.s8r.application.dto.ComponentDto;
import org.s8r.application.dto.CompositeComponentDto;
import org.s8r.application.dto.MachineDto;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.service.ComponentService;
import org.s8r.application.service.MachineService;

/**
 * Command-line adapter for listing S8r models in ASCII visualization.
 *
 * <p>This class provides the entry point for visualizing an S8r model structure from the command
 * line. It follows the Adapter pattern from Clean Architecture, connecting external interfaces
 * (CLI) to the application layer.
 */
public class ListModelCommand {

  /**
   * Main entry point for the command-line model listing.
   *
   * @param args Command line arguments. args[0] should be the project path (optional).
   */
  public static void main(String[] args) {
    // Get the project path from args or use current directory
    String projectPath = args.length > 0 ? args[0] : System.getProperty("user.dir");

    // Get services from the ServiceLocator
    LoggerPort logger = ServiceLocator.getServiceFactory().getLogger(ListModelCommand.class);
    MachineService machineService =
        ServiceLocator.getServiceFactory().getService(MachineService.class);
    ComponentService componentService =
        ServiceLocator.getServiceFactory().getService(ComponentService.class);

    // Verify this is a valid S8r project
    Path s8rDir = Paths.get(projectPath, ".s8r");
    if (!Files.exists(s8rDir) || !Files.isDirectory(s8rDir)) {
      System.err.println("Not a valid S8r project");
      System.exit(1);
    }

    // Generate ASCII visualization
    try {
      // Get all machines in the model
      List<MachineDto> machines = machineService.getAllMachines();

      // Print header
      System.out.println("S8r Model Visualization");
      System.out.println("======================\n");

      if (machines.isEmpty()) {
        System.out.println("No machines defined in this model.");
        return;
      }

      // Print each machine and its composites
      for (MachineDto machine : machines) {
        printMachine(machine, componentService);
        System.out.println();
      }

    } catch (Exception e) {
      logger.error("Failed to list model", e);
      System.err.println("Error listing model: " + e.getMessage());
      System.exit(1);
    }
  }

  /**
   * Prints a machine and its components in ASCII format.
   *
   * @param machine the machine to print
   * @param componentService the component service for retrieving component details
   */
  private static void printMachine(MachineDto machine, ComponentService componentService) {
    System.out.println("Machine: " + machine.getId());
    System.out.println("  State: " + (machine.isActive() ? "ACTIVE" : "INACTIVE"));
    System.out.println();

    // Print composites
    Map<String, CompositeComponentDto> composites = machine.getComposites();
    if (composites.isEmpty()) {
      System.out.println("  No composites defined in this machine.");
      return;
    }

    System.out.println("Composites:");
    for (Map.Entry<String, CompositeComponentDto> entry : composites.entrySet()) {
      String name = entry.getKey();
      CompositeComponentDto composite = entry.getValue();
      System.out.println("  [" + name + ": " + getCompositeType(composite) + "]");

      // Print components in this composite
      Map<String, ComponentDto> components = composite.getComponents();
      if (!components.isEmpty()) {
        System.out.println("    Components:");
        for (Map.Entry<String, ComponentDto> compEntry : components.entrySet()) {
          System.out.println(
              "      - " + compEntry.getKey() + " (" + compEntry.getValue().getId() + ")");
        }
      }
    }

    // Print connections
    Map<String, List<String>> connections = machine.getConnections();
    if (!connections.isEmpty()) {
      System.out.println("\nConnections:");
      for (Map.Entry<String, List<String>> entry : connections.entrySet()) {
        String source = entry.getKey();
        for (String target : entry.getValue()) {
          System.out.println("  [" + source + "] --> [" + target + "]");
        }
      }
    }
  }

  /**
   * Gets the type of a composite based on its environment parameters.
   *
   * @param composite the composite
   * @return the composite type, or "standard" if not specified
   */
  private static String getCompositeType(CompositeComponentDto composite) {
    Map<String, Object> env = composite.getEnvironment();
    if (env.containsKey("type")) {
      return env.get("type").toString();
    }
    return "standard";
  }
}
