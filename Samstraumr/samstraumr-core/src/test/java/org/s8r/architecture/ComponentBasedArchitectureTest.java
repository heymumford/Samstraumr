package org.s8r.architecture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.s8r.component.core.Component;
import org.s8r.component.core.Environment;
import org.s8r.component.core.State;
import org.s8r.component.exception.ComponentException;
import org.s8r.component.identity.Identity;
import org.s8r.component.composite.Composite;
import org.s8r.component.machine.Machine;
import org.s8r.test.annotation.UnitTest;

/**
 * Tests for the Component-Based Architecture as described in ADR-0007.
 * 
 * <p>This test suite validates the implementation of the component model,
 * compositional patterns, interface contracts, and implementation guidelines.
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
            
            // Check initial state
            assertEquals(State.CREATED, component.getState(), "Initial state should be CREATED");
            
            // Initialize component
            component.initialize();
            assertEquals(State.READY, component.getState(), "State after initialization should be READY");
            
            // Destroy component
            component.destroy();
            assertEquals(State.DESTROYED, component.getState(), "Final state should be DESTROYED");
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
            
            // Valid transition: CREATED → READY
            component.initialize();
            assertEquals(State.READY, component.getState(), "Component should be in READY state");
            
            // Invalid transition: READY → CREATED (should throw exception)
            assertThrows(IllegalStateException.class, () -> {
                // Attempt to revert to CREATED state
                // Implementation detail: There's no public API to do this directly, 
                // so we'd need a method that attempts an invalid transition
                component.destroy();
                component.initialize(); // Can't initialize after destroy
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
            Composite composite = Composite.create("Parent Composite", environment);
            
            // Create children
            Component child1 = Component.create("Child 1", environment);
            Component child2 = Component.create("Child 2", environment);
            
            // Add children to composite
            composite.addComponent(child1);
            composite.addComponent(child2);
            
            // Verify children are managed by composite
            List<Component> children = composite.getComponents();
            assertEquals(2, children.size(), "Composite should have two children");
            assertTrue(children.contains(child1), "Composite should contain first child");
            assertTrue(children.contains(child2), "Composite should contain second child");
        }
        
        @Test
        @DisplayName("Machine should orchestrate data flow")
        void machineShouldOrchestrateDateFlow() {
            // Create machine (a special type of composite)
            Machine machine = Machine.create("Test Machine", environment);
            
            // Create components for the machine
            Component source = Component.create("Data Source", environment);
            Component processor = Component.create("Data Processor", environment);
            Component sink = Component.create("Data Sink", environment);
            
            // Add components to machine
            machine.addComponent(source);
            machine.addComponent(processor);
            machine.addComponent(sink);
            
            // Connect components in a flow
            machine.connectComponents(source, processor);
            machine.connectComponents(processor, sink);
            
            // Verify connections
            assertTrue(machine.isConnected(source, processor), "Source should be connected to processor");
            assertTrue(machine.isConnected(processor, sink), "Processor should be connected to sink");
            assertFalse(machine.isConnected(source, sink), "Source and sink should not be directly connected");
        }
        
        @Test
        @DisplayName("Lifecycle operations should propagate to children")
        void lifecycleOperationsShouldPropagateToChildren() {
            // Create a composite with children
            Composite composite = Composite.create("Lifecycle Composite", environment);
            Component child1 = Component.create("Child 1", environment);
            Component child2 = Component.create("Child 2", environment);
            
            composite.addComponent(child1);
            composite.addComponent(child2);
            
            // Initialize composite (should initialize children)
            composite.initialize();
            
            // Verify state of children
            assertEquals(State.READY, child1.getState(), "Child 1 should be initialized");
            assertEquals(State.READY, child2.getState(), "Child 2 should be initialized");
            
            // Destroy composite (should destroy children)
            composite.destroy();
            
            // Verify state of children
            assertEquals(State.DESTROYED, child1.getState(), "Child 1 should be destroyed");
            assertEquals(State.DESTROYED, child2.getState(), "Child 2 should be destroyed");
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
            assertDoesNotThrow(() -> component.initialize(), "initialize() should be defined");
            assertDoesNotThrow(() -> component.getState(), "getState() should be defined");
            assertDoesNotThrow(() -> component.getIdentity(), "getIdentity() should be defined");
            assertDoesNotThrow(() -> component.getReason(), "getReason() should be defined");
            assertDoesNotThrow(() -> component.destroy(), "destroy() should be defined");
        }
        
        @Test
        @DisplayName("Events should propagate through the system")
        void eventsShouldPropagateThroughSystem() {
            // Create a component with mock event listener
            Component component = Component.create("Event Test", environment);
            
            // Set up a test event listener
            EventListener mockListener = mock(EventListener.class);
            component.addEventHandler(mockListener);
            
            // Trigger an event (through state change)
            component.initialize();
            
            // Verify listener was called
            verify(mockListener, times(1)).onEvent(any(ComponentEvent.class));
        }
        
        @Test
        @DisplayName("Dependencies should be injectable")
        void dependenciesShouldBeInjectable() {
            // Create a component
            Component component = Component.create("Dependency Test", environment);
            
            // Define a dependency
            Logger mockLogger = mock(Logger.class);
            
            // Inject dependency
            component.setDependency("logger", mockLogger);
            
            // Verify dependency retrieval
            Object retrievedDependency = component.getDependency("logger");
            assertSame(mockLogger, retrievedDependency, "Retrieved dependency should be the same object");
            
            // Verify dependency usage (implementation would use the dependency internally)
            component.initialize(); // This would use the logger internally
            verify(mockLogger, atLeastOnce()).log(anyString(), anyString());
        }
    }
    
    // Inner interfaces for testing
    
    interface EventListener {
        void onEvent(ComponentEvent event);
    }
    
    interface ComponentEvent {
        Component getSource();
        String getType();
        Map<String, Object> getPayload();
    }
    
    interface Logger {
        void log(String level, String message);
    }
}