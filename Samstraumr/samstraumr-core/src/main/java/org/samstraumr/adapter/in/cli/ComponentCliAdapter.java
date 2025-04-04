/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * CLI adapter for demonstrating the Clean Architecture implementation
 */

package org.samstraumr.adapter.in.cli;

import java.util.Optional;
import java.util.Scanner;

import org.samstraumr.adapter.out.InMemoryComponentRepository;
import org.samstraumr.application.port.ComponentRepository;
import org.samstraumr.application.port.LoggerPort;
import org.samstraumr.application.service.ComponentService;
import org.samstraumr.domain.component.Component;
import org.samstraumr.domain.identity.ComponentId;
import org.samstraumr.infrastructure.logging.Slf4jLogger;

/**
 * Command-line interface adapter for demonstrating the Clean Architecture implementation.
 *
 * <p>This adapter provides a simple CLI for interacting with the application layer,
 * following Clean Architecture principles by depending only on application services
 * and using dependency injection for infrastructure.
 */
public class ComponentCliAdapter {
    private final ComponentService componentService;
    private final LoggerPort logger;
    
    /**
     * Creates a new ComponentCliAdapter with default implementations.
     */
    public ComponentCliAdapter() {
        // Create infrastructure implementations
        ComponentRepository repository = new InMemoryComponentRepository();
        this.logger = new Slf4jLogger(ComponentCliAdapter.class);
        
        // Create application service with dependencies injected
        this.componentService = new ComponentService(repository, logger);
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
    
    /**
     * Runs the CLI application.
     */
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
        } catch (Exception e) {
            System.out.println("Error activating component: " + e.getMessage());
            logger.error("Error activating component", e);
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
            Optional<Component> component = componentService.getComponent(id);
            
            if (component.isPresent()) {
                Component c = component.get();
                System.out.println("\nComponent Details:");
                System.out.println("------------------");
                System.out.println("ID: " + c.getId().getIdString());
                System.out.println("Reason: " + c.getId().getReason());
                System.out.println("Creation Time: " + c.getCreationTime());
                System.out.println("Lifecycle State: " + c.getLifecycleState());
                System.out.println("Lineage: " + c.getLineage());
                
                System.out.println("\nActivity Log:");
                c.getActivityLog().forEach(System.out::println);
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
            
            for (Component c : components) {
                System.out.printf("ID: %s | Reason: %s | State: %s%n",
                    c.getId().getShortId(),
                    c.getId().getReason(),
                    c.getLifecycleState());
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