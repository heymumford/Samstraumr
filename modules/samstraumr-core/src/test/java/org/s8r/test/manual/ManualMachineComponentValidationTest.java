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

package org.s8r.test.manual;

import org.s8r.domain.component.Component;
import org.s8r.domain.component.ComponentType;
import org.s8r.domain.component.composite.CompositeComponent;
import org.s8r.domain.exception.ComponentNotFoundException;
import org.s8r.domain.exception.InvalidCompositeTypeException;
import org.s8r.domain.exception.InvalidOperationException;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.domain.machine.Machine;
import org.s8r.domain.machine.MachineType;

/**
 * Manual test for machine component validation.
 *
 * <p>This class demonstrates the validation of components in machines, including the prevention of
 * adding non-existent or non-composite components.
 *
 * <p>Run this class manually with:
 *
 * <pre>
 * java -cp target/classes:target/test-classes org.s8r.test.manual.ManualMachineComponentValidationTest
 * </pre>
 */
public class ManualMachineComponentValidationTest {

  /**
   * Main method to run the test.
   *
   * @param args Command line arguments (not used)
   */
  public static void main(String[] args) {
    System.out.println("Starting Machine Component Validation Test");
    System.out.println("==========================================");

    try {
      testValidComponentAddition();
      testInvalidComponentAddition();
      testComponentRemoval();
      testComponentOperations();
      testInvalidStateOperations();

      System.out.println("\nAll tests completed successfully!");
    } catch (Exception e) {
      System.err.println("\nTest failed with exception: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /** Tests the addition of a valid composite component to a machine. */
  private static void testValidComponentAddition() {
    System.out.println("\nTest: Adding Valid Component");
    System.out.println("---------------------------");

    // Create a machine
    ComponentId machineId = ComponentId.createNew();
    Machine machine =
        Machine.create(
            machineId,
            MachineType.PROCESSING,
            "Test Machine",
            "For testing component validation",
            "1.0.0");
    System.out.println("Created machine: " + machine);

    // Create a valid composite component (using a mock/anonymous implementation)
    ComponentId componentId = ComponentId.createNew();
    CompositeComponent component =
        new CompositeComponent() {
          @Override
          public ComponentId getId() {
            return componentId;
          }

          @Override
          public ComponentType getType() {
            return ComponentType.COMPOSITE;
          }

          @Override
          public String getName() {
            return "Test Component";
          }

          @Override
          public LifecycleState getLifecycleState() {
            return LifecycleState.READY;
          }

          @Override
          public void activate() {
            // Mock implementation
          }

          @Override
          public void deactivate() {
            // Mock implementation
          }

          @Override
          public void terminate() {
            // Mock implementation
          }
        };
    System.out.println("Created component: " + component.getId().getShortId());

    // Add the component to the machine
    try {
      machine.addComponent(component);
      System.out.println("Successfully added component to machine");

      // Verify it was added
      boolean found = machine.getComponent(componentId).isPresent();
      System.out.println("Component found in machine: " + found);
    } catch (Exception e) {
      System.err.println("Error adding component: " + e.getMessage());
      throw e;
    }
  }

  /** Tests the validation when adding invalid components to a machine. */
  private static void testInvalidComponentAddition() {
    System.out.println("\nTest: Adding Invalid Components");
    System.out.println("-------------------------------");

    // Create a machine
    ComponentId machineId = ComponentId.createNew();
    Machine machine =
        Machine.create(
            machineId,
            MachineType.PROCESSING,
            "Test Machine",
            "For testing component validation",
            "1.0.0");
    System.out.println("Created machine: " + machine);

    // Test null component
    try {
      System.out.println("Attempting to add null component...");
      machine.addComponent(null);
      System.err.println("ERROR: Expected exception but none was thrown");
    } catch (IllegalArgumentException e) {
      System.out.println("Correctly threw exception for null component: " + e.getMessage());
    }

    // Test non-composite component (we'll use a mock component that's not a composite)
    try {
      System.out.println("Attempting to add non-composite component...");

      // Create a simple atomic component (not a composite)
      Component atomicComponent =
          new Component() {
            private final ComponentId id = ComponentId.createNew();

            @Override
            public ComponentId getId() {
              return id;
            }

            @Override
            public ComponentType getType() {
              return ComponentType.ATOMIC;
            }

            @Override
            public String getName() {
              return "Mock Atomic Component";
            }

            @Override
            public ComponentDescription getDescription() {
              return new ComponentDescription("Mock", "Not a composite");
            }
          };

      // This should fail type validation since we're casting but it's not actually a composite
      machine.addComponent((CompositeComponent) atomicComponent);
      System.err.println("ERROR: Expected exception but none was thrown");
    } catch (InvalidCompositeTypeException e) {
      System.out.println(
          "Correctly threw exception for non-composite component: " + e.getMessage());
    } catch (ClassCastException e) {
      System.out.println(
          "Correctly threw exception for non-composite component (cast exception): "
              + e.getMessage());
    }
  }

  /** Tests the removal of components from a machine. */
  private static void testComponentRemoval() {
    System.out.println("\nTest: Component Removal");
    System.out.println("-----------------------");

    // Create a machine
    ComponentId machineId = ComponentId.createNew();
    Machine machine =
        Machine.create(
            machineId,
            MachineType.PROCESSING,
            "Test Machine",
            "For testing component validation",
            "1.0.0");
    System.out.println("Created machine: " + machine);

    // Create a valid composite component (using a mock/anonymous implementation)
    ComponentId componentId = ComponentId.createNew();
    CompositeComponent component =
        new CompositeComponent() {
          @Override
          public ComponentId getId() {
            return componentId;
          }

          @Override
          public ComponentType getType() {
            return ComponentType.COMPOSITE;
          }

          @Override
          public String getName() {
            return "Test Component";
          }

          @Override
          public LifecycleState getLifecycleState() {
            return LifecycleState.READY;
          }

          @Override
          public void activate() {
            // Mock implementation
          }

          @Override
          public void deactivate() {
            // Mock implementation
          }

          @Override
          public void terminate() {
            // Mock implementation
          }
        };
    System.out.println("Created component: " + component.getId().getShortId());

    // Add the component to the machine
    machine.addComponent(component);
    System.out.println("Added component to machine");

    // Remove the component
    try {
      machine.removeComponent(componentId);
      System.out.println("Successfully removed component from machine");

      // Verify it was removed
      boolean found = machine.getComponent(componentId).isPresent();
      System.out.println("Component still in machine: " + found + " (should be false)");
    } catch (Exception e) {
      System.err.println("Error removing component: " + e.getMessage());
      throw e;
    }

    // Try to remove a non-existent component
    try {
      System.out.println("Attempting to remove non-existent component...");
      machine.removeComponent(ComponentId.createNew());
      System.err.println("ERROR: Expected exception but none was thrown");
    } catch (ComponentNotFoundException e) {
      System.out.println("Correctly threw exception for non-existent component: " + e.getMessage());
    }
  }

  /** Tests component validation during machine operations. */
  private static void testComponentOperations() {
    System.out.println("\nTest: Component Validation During Operations");
    System.out.println("-----------------------------------------");

    // Create a machine
    ComponentId machineId = ComponentId.createNew();
    Machine machine =
        Machine.create(
            machineId,
            MachineType.PROCESSING,
            "Test Machine",
            "For testing component validation",
            "1.0.0");
    System.out.println("Created machine: " + machine);

    // Create a valid composite component (using a mock/anonymous implementation)
    ComponentId componentId = ComponentId.createNew();
    CompositeComponent component =
        new CompositeComponent() {
          @Override
          public ComponentId getId() {
            return componentId;
          }

          @Override
          public ComponentType getType() {
            return ComponentType.COMPOSITE;
          }

          @Override
          public String getName() {
            return "Test Component";
          }

          @Override
          public LifecycleState getLifecycleState() {
            return LifecycleState.READY;
          }

          @Override
          public void activate() {
            // Mock implementation
          }

          @Override
          public void deactivate() {
            // Mock implementation
          }

          @Override
          public void terminate() {
            // Mock implementation
          }
        };
    System.out.println("Created component: " + component.getId().getShortId());

    // Add the component to the machine
    machine.addComponent(component);
    System.out.println("Added component to machine");

    // Run through the lifecycle operations
    try {
      machine.initialize();
      System.out.println("Machine initialized: " + machine.getState());

      machine.start();
      System.out.println("Machine started: " + machine.getState());

      machine.pause();
      System.out.println("Machine paused: " + machine.getState());

      machine.resume();
      System.out.println("Machine resumed: " + machine.getState());

      machine.stop();
      System.out.println("Machine stopped: " + machine.getState());

      machine.destroy();
      System.out.println("Machine destroyed: " + machine.getState());
    } catch (Exception e) {
      System.err.println("Error during operations: " + e.getMessage());
      throw e;
    }
  }

  /** Tests validation of operations in invalid states. */
  private static void testInvalidStateOperations() {
    System.out.println("\nTest: Operations in Invalid States");
    System.out.println("----------------------------------");

    // Create a machine
    ComponentId machineId = ComponentId.createNew();
    Machine machine =
        Machine.create(
            machineId,
            MachineType.PROCESSING,
            "Test Machine",
            "For testing component validation",
            "1.0.0");
    System.out.println("Created machine: " + machine);

    // Create a valid composite component (using a mock/anonymous implementation)
    ComponentId componentId = ComponentId.createNew();
    CompositeComponent component =
        new CompositeComponent() {
          @Override
          public ComponentId getId() {
            return componentId;
          }

          @Override
          public ComponentType getType() {
            return ComponentType.COMPOSITE;
          }

          @Override
          public String getName() {
            return "Test Component";
          }

          @Override
          public LifecycleState getLifecycleState() {
            return LifecycleState.READY;
          }

          @Override
          public void activate() {
            // Mock implementation
          }

          @Override
          public void deactivate() {
            // Mock implementation
          }

          @Override
          public void terminate() {
            // Mock implementation
          }
        };

    // Initialize and destroy the machine
    machine.initialize();
    machine.destroy();
    System.out.println("Machine destroyed: " + machine.getState());

    // Try operations in DESTROYED state
    try {
      System.out.println("Attempting to add component to destroyed machine...");
      machine.addComponent(component);
      System.err.println("ERROR: Expected exception but none was thrown");
    } catch (InvalidOperationException e) {
      System.out.println("Correctly threw exception: " + e.getMessage());
    }

    try {
      System.out.println("Attempting to start destroyed machine...");
      machine.start();
      System.err.println("ERROR: Expected exception but none was thrown");
    } catch (InvalidOperationException e) {
      System.out.println("Correctly threw exception: " + e.getMessage());
    }
  }
}
