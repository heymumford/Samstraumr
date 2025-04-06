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

package org.s8r.app;

import java.util.List;
import java.util.Scanner;

import org.s8r.Samstraumr;
import org.s8r.application.dto.ComponentDto;
import org.s8r.application.dto.MachineDto;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.machine.MachineType;
import org.s8r.infrastructure.logging.LoggerFactory;

/**
 * A simple CLI application that demonstrates the S8r framework.
 *
 * <p>This application provides a command-line interface for interacting with the framework's
 * functionality, showing how to use the Samstraumr facade.
 */
public class CliApplication {
  private static final LoggerPort logger = LoggerFactory.getLogger(CliApplication.class);
  private static final Samstraumr framework = Samstraumr.getInstance();

  /**
   * Main method.
   *
   * @param args Command-line arguments
   */
  public static void main(String[] args) {
    logger.info("Starting Samstraumr CLI application");

    printWelcome();

    try (Scanner scanner = new Scanner(System.in)) {
      boolean running = true;

      while (running) {
        printMenu();
        String choice = scanner.nextLine().trim();

        switch (choice) {
          case "1":
            createComponent(scanner);
            break;
          case "2":
            listComponents();
            break;
          case "3":
            createMachine(scanner);
            break;
          case "4":
            listMachines();
            break;
          case "5":
            startMachine(scanner);
            break;
          case "6":
            stopMachine(scanner);
            break;
          case "0":
            running = false;
            break;
          default:
            System.out.println("Invalid choice. Please try again.");
        }
      }
    }

    logger.info("Samstraumr CLI application exiting");
    System.out.println("Goodbye!");
  }

  private static void printWelcome() {
    System.out.println("===================================");
    System.out.println("Samstraumr Clean Architecture Demo");
    System.out.println("===================================");
  }

  private static void printMenu() {
    System.out.println("\nPlease select an option:");
    System.out.println("1. Create Component");
    System.out.println("2. List Components");
    System.out.println("3. Create Machine");
    System.out.println("4. List Machines");
    System.out.println("5. Start Machine");
    System.out.println("6. Stop Machine");
    System.out.println("0. Exit");
    System.out.print("> ");
  }

  private static void createComponent(Scanner scanner) {
    System.out.print("Enter reason for component creation: ");
    String reason = scanner.nextLine().trim();

    try {
      ComponentId id = framework.createComponent(reason);
      System.out.println("Component created successfully: " + id.getShortId());
    } catch (Exception e) {
      System.out.println("Error creating component: " + e.getMessage());
      logger.error("Error creating component", e);
    }
  }

  private static void listComponents() {
    try {
      List<ComponentDto> components = framework.getAllComponents();

      if (components.isEmpty()) {
        System.out.println("No components found");
        return;
      }

      System.out.println("\nAll Components:");
      System.out.println("--------------");

      for (ComponentDto component : components) {
        System.out.printf(
            "ID: %s | Reason: %s | State: %s%n",
            component.getShortId(), component.getReason(), component.getLifecycleState());
      }
    } catch (Exception e) {
      System.out.println("Error listing components: " + e.getMessage());
      logger.error("Error listing components", e);
    }
  }

  private static void createMachine(Scanner scanner) {
    System.out.print("Enter machine name: ");
    String name = scanner.nextLine().trim();

    System.out.print("Enter machine description: ");
    String description = scanner.nextLine().trim();

    System.out.println("Available machine types:");
    for (MachineType type : MachineType.values()) {
      System.out.printf("- %s: %s%n", type.name(), type.getDescription());
    }

    System.out.print("Enter machine type: ");
    String typeStr = scanner.nextLine().trim();

    try {
      MachineDto machine = framework.createMachine(typeStr, name, description);
      System.out.println("Machine created successfully: " + machine.getShortId());
    } catch (Exception e) {
      System.out.println("Error creating machine: " + e.getMessage());
      logger.error("Error creating machine", e);
    }
  }

  private static void listMachines() {
    try {
      List<MachineDto> machines = framework.getAllMachines();

      if (machines.isEmpty()) {
        System.out.println("No machines found");
        return;
      }

      System.out.println("\nAll Machines:");
      System.out.println("-------------");

      for (MachineDto machine : machines) {
        System.out.printf(
            "ID: %s | Name: %s | Type: %s | State: %s%n",
            machine.getShortId(), machine.getName(), machine.getType(), machine.getState());
      }
    } catch (Exception e) {
      System.out.println("Error listing machines: " + e.getMessage());
      logger.error("Error listing machines", e);
    }
  }

  private static void startMachine(Scanner scanner) {
    System.out.print("Enter machine ID to start: ");
    String idStr = scanner.nextLine().trim();

    try {
      ComponentId id = ComponentId.fromString(idStr, "Start");
      framework.startMachine(id);
      System.out.println("Machine started successfully");
    } catch (Exception e) {
      System.out.println("Error starting machine: " + e.getMessage());
      logger.error("Error starting machine", e);
    }
  }

  private static void stopMachine(Scanner scanner) {
    System.out.print("Enter machine ID to stop: ");
    String idStr = scanner.nextLine().trim();

    try {
      ComponentId id = ComponentId.fromString(idStr, "Stop");
      framework.stopMachine(id);
      System.out.println("Machine stopped successfully");
    } catch (Exception e) {
      System.out.println("Error stopping machine: " + e.getMessage());
      logger.error("Error stopping machine", e);
    }
  }
}
