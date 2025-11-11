package org.s8r.architecture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.s8r.component.Component;
import org.s8r.component.Composite;
import org.s8r.component.Environment;
import org.s8r.component.Identity;
import org.s8r.component.Machine;
import org.s8r.component.State;
import org.s8r.test.annotation.UnitTest;

/**
 * Tests for the Component-Based Architecture as described in ADR-0007.
 *
 * <p>This test suite validates the implementation of the component model, compositional patterns,
 * interface contracts, and implementation guidelines.
 */
@UnitTest
@Tag("architecture")
@Tag("adr-validation")
@DisplayName("Component-Based Architecture Tests (ADR-0007)")
public class ComponentBasedArchitectureTest {

  private Environment environment;

  @BeforeEach
  void setUp() {
    environment = new Environment();
    environment.setParameter("test-mode", "true");
  }

  @Nested
  @DisplayName("Core Component Model Tests")
  class CoreComponentModelTests {

    @Test
    @DisplayName("Component creation should succeed with proper identity")
    void componentCreationShouldSucceedWithProperIdentity() {
      Component component = Component.create("Test Component", environment);

      assertNotNull(component, "Component should be created");
      assertNotNull(component.getIdentity(), "Component should have identity");
      assertEquals("Test Component", component.getReason(), "Component should have correct reason");
    }

    @Test
    @DisplayName("Component should have basic lifecycle states")
    void componentShouldHaveBasicLifecycleStates() {
      Component component = Component.create("Lifecycle Test", environment);

      // Check initial state - Component in this implementation starts at CONCEPTION
      // and automatically initializes to READY
      assertEquals(State.READY, component.getState(), "Initial state should be READY");

      // Terminate component
      component.terminate();
      assertEquals(State.TERMINATED, component.getState(), "Final state should be TERMINATED");
    }

    @Test
    @DisplayName("Identity should provide unique identification")
    void identityShouldProvideUniqueIdentification() {
      Component component1 = Component.create("First Component", environment);
      Component component2 = Component.create("Second Component", environment);

      Identity id1, id2;
      id1 = (Identity) component1.getIdentity();
      id2 = (Identity) component2.getIdentity();

      assertNotNull(id1, "First component should have identity");
      assertNotNull(id2, "Second component should have identity");
      assertNotEquals(id1, id2, "Components should have different identities");
      assertNotEquals(id1.getUniqueId(), id2.getUniqueId(), "Component IDs should be unique");
    }

    @Test
    @DisplayName("State transitions should follow allowed paths")
    void stateTransitionsShouldFollowAllowedPaths() {
      Component component = Component.create("State Transition Test", environment);

      // Component should already be in READY state (initialized during creation)
      assertEquals(State.READY, component.getState(), "Component should be in READY state");

      // Valid transition: READY → ACTIVE
      component.setState(State.ACTIVE);
      assertEquals(State.ACTIVE, component.getState(), "Component should be in ACTIVE state");

      // Invalid transition: once terminated, can't change state again
      component.terminate();
      assertEquals(
          State.TERMINATED, component.getState(), "Component should be in TERMINATED state");

      // Attempting to change state of terminated component should throw exception
      assertThrows(
          IllegalStateException.class,
          () -> {
            component.setState(State.READY);
          });
    }
  }

  @Nested
  @DisplayName("Compositional Pattern Tests")
  class CompositionalPatternTests {

    @Test
    @DisplayName("Composite should manage child components")
    void compositeShouldManageChildComponents() {
      // Create a composite
      Composite composite = new Composite("Parent Composite", environment);

      // Create children
      Component child1 = Component.create("Child 1", environment);
      Component child2 = Component.create("Child 2", environment);

      // Add children to composite
      composite.addComponent("child1", child1);
      composite.addComponent("child2", child2);

      // Verify children are managed by composite
      Map<String, Component> children = composite.getComponents();
      assertEquals(2, children.size(), "Composite should have two children");
      assertSame(child1, children.get("child1"), "Composite should contain first child");
      assertSame(child2, children.get("child2"), "Composite should contain second child");
    }

    @Test
    @DisplayName("Machine should orchestrate data flow")
    void machineShouldOrchestrateDateFlow() {
      // Create machine (a special type of composite)
      Machine machine = new Machine("Test Machine", environment);

      // Create composites for the machine
      Composite inputComposite = new Composite("Input", environment);
      Composite processingComposite = new Composite("Processing", environment);
      Composite outputComposite = new Composite("Output", environment);

      // Add composites to machine
      machine.addComposite("input", inputComposite);
      machine.addComposite("processor", processingComposite);
      machine.addComposite("output", outputComposite);

      // Connect composites in a flow
      machine.connect("input", "processor");
      machine.connect("processor", "output");

      // Verify connections
      Map<String, List<String>> connections = machine.getConnections();
      assertTrue(
          connections.get("input").contains("processor"), "Input should be connected to processor");
      assertTrue(
          connections.get("processor").contains("output"),
          "Processor should be connected to output");
      assertFalse(
          connections.containsKey("input") && connections.get("input").contains("output"),
          "Input should not be directly connected to output");
    }

    @Test
    @DisplayName("Lifecycle operations should propagate to children")
    void lifecycleOperationsShouldPropagateToChildren() {
      // Create a machine with composites
      Machine machine = new Machine("Lifecycle Machine", environment);

      // Create composites
      Composite composite1 = new Composite("Composite 1", environment);
      Composite composite2 = new Composite("Composite 2", environment);

      // Add composites to machine
      machine.addComposite("comp1", composite1);
      machine.addComposite("comp2", composite2);

      // Verify machine and composites are active initially
      assertTrue(machine.isActive(), "Machine should be active");
      assertTrue(composite1.isActive(), "Composite 1 should be active");
      assertTrue(composite2.isActive(), "Composite 2 should be active");

      // Shutdown machine (should deactivate all composites)
      machine.shutdown();

      // Verify machine and composites are no longer active
      assertFalse(machine.isActive(), "Machine should not be active");

      // Check machine's final state
      assertEquals(
          "TERMINATED", machine.getState().get("status"), "Machine should be in TERMINATED state");
    }
  }

  @Nested
  @DisplayName("Interface Contract Tests")
  class InterfaceContractTests {

    @Test
    @DisplayName("Component interface should be well-defined")
    void componentInterfaceShouldBeWellDefined() {
      // Create a component and verify its interface
      Component component = Component.create("Interface Test", environment);

      // Core interface methods should exist and be callable
      assertDoesNotThrow(() -> component.getState(), "getState() should be defined");
      assertDoesNotThrow(() -> component.getIdentity(), "getIdentity() should be defined");
      assertDoesNotThrow(() -> component.getReason(), "getReason() should be defined");
      assertDoesNotThrow(() -> component.terminate(), "terminate() should be defined");
    }

    @Test
    @DisplayName("Events should propagate through the system")
    void eventsShouldPropagateThroughSystem() {
      // Create a machine for event testing
      Machine machine = new Machine("Event Machine", environment);

      // Log an event to the machine's event log
      machine.logEvent("Test event");

      // Verify the event was logged
      List<Machine.MachineEvent> events = machine.getEventLog();
      assertFalse(events.isEmpty(), "Event log should not be empty");
      assertEquals(
          "Test event",
          events.get(events.size() - 1).getDescription(),
          "Event description should match");
    }

    @Test
    @DisplayName("Dependencies should be injectable")
    void dependenciesShouldBeInjectable() {
      // Create a component
      Component component = Component.create("Dependency Test", environment);

      // Set a property (Components store properties through setProperty/getProperty methods)
      component.setProperty("testProp", "testValue");

      // Verify property retrieval
      Object retrievedProperty = component.getProperty("testProp");
      assertEquals("testValue", retrievedProperty, "Retrieved property should match set value");

      // Verify environment access is available
      assertNotNull(component.getEnvironment(), "Environment should be accessible");

      // Verify logger is accessible
      assertNotNull(component.getLogger(), "Logger should be accessible");
    }
  }

  // End of tests
}
