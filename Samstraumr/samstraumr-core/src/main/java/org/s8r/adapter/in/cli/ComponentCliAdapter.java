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

import java.util.Optional;
import java.util.Scanner;

import org.s8r.infrastructure.persistence.InMemoryComponentRepository;
import org.s8r.application.port.ComponentRepository;
import org.s8r.application.port.EventPublisherPort;
import org.s8r.application.port.LoggerFactory;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.service.ComponentService;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.exception.ComponentException;
import org.s8r.domain.exception.ComponentNotFoundException;
import org.s8r.domain.exception.InvalidOperationException;
import org.s8r.domain.identity.ComponentId;
import org.s8r.infrastructure.event.EventPublisherAdapter;

/**
 * Command-line interface adapter for demonstrating the Clean Architecture implementation.
 *
 * <p>This adapter provides a simple CLI for interacting with the application layer, following Clean
 * Architecture principles by depending only on application services and using dependency injection
 * for infrastructure.
 */
public class ComponentCliAdapter {
  private final ComponentService componentService;
  private final LoggerPort logger;

  /** Creates a new ComponentCliAdapter with default implementations. */
  public ComponentCliAdapter() {
    // Get a LoggerFactory through dependency injection first
    // In a real application, this would be injected from outside
    LoggerFactory loggerFactory = org.s8r.infrastructure.config.DependencyContainer.getInstance().get(LoggerFactory.class);
    this.logger = loggerFactory.getLogger(ComponentCliAdapter.class);
    
    // Create infrastructure implementations with the logger
    ComponentRepository repository = new InMemoryComponentRepository(logger);

    // Create a simple in-memory event dispatcher
    org.s8r.infrastructure.event.InMemoryEventDispatcher eventDispatcher =
        new org.s8r.infrastructure.event.InMemoryEventDispatcher(logger);
        
    // Create the event publisher adapter using the dispatcher
    EventPublisherPort eventPublisher = new EventPublisherAdapter(eventDispatcher, repository, logger);

    // Create application service with dependencies injected
    this.componentService = new ComponentService(repository, logger, eventPublisher);
  }

  /**
   * Creates a new ComponentCliAdapter with the specified dependencies.
   *
   * @param componentService The component service to use
   * @param logger The logger to use
   */
  public ComponentCliAdapter(ComponentService componentService, LoggerPort logger) {
    this.componentService = componentService;
    this.logger = logger;
  }

  /** Runs the CLI application. */
  public void run() {
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
            createChildComponent(scanner);
            break;
          case "3":
            activateComponent(scanner);
            break;
          case "4":
            deactivateComponent(scanner);
            break;
          case "5":
            terminateComponent(scanner);
            break;
          case "6":
            viewComponent(scanner);
            break;
          case "7":
            listAllComponents();
            break;
          case "8":
            addToLineage(scanner);
            break;
          case "0":
            running = false;
            break;
          default:
            System.out.println("Invalid choice. Please try again.");
        }
      }
    }

    System.out.println("Goodbye!");
  }

  private void printWelcome() {
    System.out.println("===================================");
    System.out.println("S8r Clean Architecture Demo");
    System.out.println("===================================");
  }

  private void printMenu() {
    System.out.println("\nPlease select an option:");
    System.out.println("1. Create Component");
    System.out.println("2. Create Child Component");
    System.out.println("3. Activate Component");
    System.out.println("4. Deactivate Component");
    System.out.println("5. Terminate Component");
    System.out.println("6. View Component");
    System.out.println("7. List All Components");
    System.out.println("8. Add to Lineage");
    System.out.println("0. Exit");
    System.out.print("> ");
  }

  private void createComponent(Scanner scanner) {
    System.out.print("Enter reason for component creation: ");
    String reason = scanner.nextLine().trim();

    try {
      ComponentId id = componentService.createComponent(reason);
      System.out.println("Component created successfully: " + id.getIdString());
    } catch (Exception e) {
      System.out.println("Error creating component: " + e.getMessage());
      logger.error("Error creating component", e);
    }
  }

  private void createChildComponent(Scanner scanner) {
    System.out.print("Enter parent component ID: ");
    String parentIdStr = scanner.nextLine().trim();

    System.out.print("Enter reason for child component creation: ");
    String reason = scanner.nextLine().trim();

    try {
      ComponentId parentId = ComponentId.fromString(parentIdStr, "Parent");
      ComponentId childId = componentService.createChildComponent(reason, parentId);
      System.out.println("Child component created successfully: " + childId.getIdString());
    } catch (Exception e) {
      System.out.println("Error creating child component: " + e.getMessage());
      logger.error("Error creating child component", e);
    }
  }

  private void activateComponent(Scanner scanner) {
    System.out.print("Enter component ID to activate: ");
    String idStr = scanner.nextLine().trim();

    try {
      ComponentId id = ComponentId.fromString(idStr, "Activation");
      componentService.activateComponent(id);
      System.out.println("Component activated successfully");
    } catch (ComponentNotFoundException e) {
      System.out.println("Component not found: " + e.getComponentId().getShortId());
      logger.error("Component not found during activation", e);
    } catch (InvalidOperationException e) {
      System.out.println("Cannot activate component in state: " + e.getCurrentState());
      System.out.println("The component must be in READY state to be activated.");
      logger.error("Invalid operation during activation", e);
    } catch (ComponentException e) {
      System.out.println("Domain error during activation: " + e.getMessage());
      logger.error("Domain error during activation", e);
    } catch (Exception e) {
      System.out.println("Unexpected error activating component: " + e.getMessage());
      logger.error("Unexpected error during activation", e);
    }
  }

  private void deactivateComponent(Scanner scanner) {
    System.out.print("Enter component ID to deactivate: ");
    String idStr = scanner.nextLine().trim();

    try {
      ComponentId id = ComponentId.fromString(idStr, "Deactivation");
      componentService.deactivateComponent(id);
      System.out.println("Component deactivated successfully");
    } catch (Exception e) {
      System.out.println("Error deactivating component: " + e.getMessage());
      logger.error("Error deactivating component", e);
    }
  }

  private void terminateComponent(Scanner scanner) {
    System.out.print("Enter component ID to terminate: ");
    String idStr = scanner.nextLine().trim();

    try {
      ComponentId id = ComponentId.fromString(idStr, "Termination");
      componentService.terminateComponent(id);
      System.out.println("Component terminated successfully");
    } catch (Exception e) {
      System.out.println("Error terminating component: " + e.getMessage());
      logger.error("Error terminating component", e);
    }
  }

  private void viewComponent(Scanner scanner) {
    System.out.print("Enter component ID to view: ");
    String idStr = scanner.nextLine().trim();

    try {
      ComponentId id = ComponentId.fromString(idStr, "View");
      Optional<ComponentPort> component = componentService.getComponent(id);

      if (component.isPresent()) {
        ComponentPort c = component.get();
        System.out.println("\nComponent Details:");
        System.out.println("------------------");
        System.out.println("ID: " + c.getId().getIdString());
        System.out.println("Lifecycle State: " + c.getLifecycleState());
        System.out.println("Lineage: " + String.join(" > ", c.getLineage()));
        
        if (c instanceof org.s8r.domain.component.port.CompositeComponentPort) {
          org.s8r.domain.component.port.CompositeComponentPort composite = 
              (org.s8r.domain.component.port.CompositeComponentPort) c;
          System.out.println("Type: Composite");
          System.out.println("Components: " + composite.getComponents().size());
          
          for (String name : composite.getComponents().keySet()) {
            System.out.println("  - " + name);
          }
        }
      } else {
        System.out.println("Component not found");
      }
    } catch (Exception e) {
      System.out.println("Error viewing component: " + e.getMessage());
      logger.error("Error viewing component", e);
    }
  }

  private void listAllComponents() {
    try {
      var components = componentService.getAllComponents();

      if (components.isEmpty()) {
        System.out.println("No components found");
        return;
      }

      System.out.println("\nAll Components:");
      System.out.println("--------------");

      for (ComponentPort c : components) {
        String type = c instanceof org.s8r.domain.component.port.CompositeComponentPort ? "Composite" : "Component";
        System.out.printf(
            "ID: %s | Type: %s | State: %s%n",
            c.getId().getShortId(), type, c.getLifecycleState());
      }
    } catch (Exception e) {
      System.out.println("Error listing components: " + e.getMessage());
      logger.error("Error listing components", e);
    }
  }

  private void addToLineage(Scanner scanner) {
    System.out.print("Enter component ID: ");
    String idStr = scanner.nextLine().trim();

    System.out.print("Enter lineage entry: ");
    String entry = scanner.nextLine().trim();

    try {
      ComponentId id = ComponentId.fromString(idStr, "Lineage");
      componentService.addToLineage(id, entry);
      System.out.println("Lineage entry added successfully");
    } catch (Exception e) {
      System.out.println("Error adding to lineage: " + e.getMessage());
      logger.error("Error adding to lineage", e);
    }
  }

  /**
   * Application entry point.
   *
   * @param args Command-line arguments (not used)
   */
  public static void main(String[] args) {
    new ComponentCliAdapter().run();
  }
}
