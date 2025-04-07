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

package org.s8r.domain.component.port;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.s8r.adapter.ComponentAdapter;
import org.s8r.adapter.MachineAdapter;
import org.s8r.application.port.ComponentRepository;
import org.s8r.application.port.EventDispatcher;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.MachineRepository;
import org.s8r.application.service.ComponentService;
import org.s8r.application.service.DataFlowService;
import org.s8r.domain.component.Component;
import org.s8r.domain.component.composite.CompositeComponent;
import org.s8r.domain.component.composite.CompositeFactory;
import org.s8r.domain.component.composite.CompositeType;
import org.s8r.domain.event.ComponentCreated;
import org.s8r.domain.event.ComponentDataEvent;
import org.s8r.domain.event.DomainEvent;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.domain.machine.Machine;
import org.s8r.domain.machine.MachineFactory;
import org.s8r.domain.machine.MachineType;
import org.s8r.infrastructure.config.PortAdapterFactory;
import org.s8r.infrastructure.event.InMemoryEventDispatcher;
import org.s8r.infrastructure.logging.ConsoleLogger;
import org.s8r.infrastructure.persistence.InMemoryComponentRepository;
import org.s8r.infrastructure.persistence.InMemoryMachineRepository;
import org.s8r.test.annotation.UnitTest;

/**
 * Tests for the port interfaces implementation in Clean Architecture.
 * 
 * <p>This test class verifies that port interfaces work correctly with adapters,
 * repositories, and services, following the Clean Architecture pattern.
 */
@UnitTest
public class PortInterfaceTest {
    
    private LoggerPort logger;
    private ComponentRepository componentRepository;
    private MachineRepository machineRepository;
    private EventDispatcher eventDispatcher;
    private ComponentService componentService;
    private PortAdapterFactory portAdapterFactory;
    private List<DomainEvent> capturedEvents;
    
    @BeforeEach
    void setUp() {
        // Create a logger
        logger = new ConsoleLogger("PortInterfaceTest");
        
        // Create repositories
        componentRepository = new InMemoryComponentRepository(logger);
        machineRepository = new InMemoryMachineRepository(logger);
        
        // Create event dispatcher that captures events
        eventDispatcher = new InMemoryEventDispatcher(logger);
        capturedEvents = new java.util.ArrayList<>();
        eventDispatcher.registerHandler(DomainEvent.class, event -> capturedEvents.add(event));
        
        // Create the component service
        componentService = new ComponentService(componentRepository, logger, eventDispatcher);
        
        // Create port adapter factory
        portAdapterFactory = new PortAdapterFactory(logger);
    }
    
    @Test
    @DisplayName("Test creating component through port interface")
    void testCreateComponentThroughPortInterface() {
        // Create a domain component
        Component component = Component.create("Test component");
        
        // Create port interface from domain component
        ComponentPort componentPort = ComponentAdapter.createComponentPort(component);
        
        // Verify port interface
        assertNotNull(componentPort);
        assertEquals(component.getId().getIdString(), componentPort.getId().getIdString());
        
        // Save port interface to repository
        componentRepository.save(componentPort);
        
        // Retrieve from repository
        Optional<ComponentPort> retrieved = componentRepository.findById(component.getId());
        assertTrue(retrieved.isPresent());
        
        // Verify retrieved component
        ComponentPort retrievedPort = retrieved.get();
        assertEquals(component.getId().getIdString(), retrievedPort.getId().getIdString());
    }
    
    @Test
    @DisplayName("Test creating component through component service")
    void testCreateComponentThroughService() {
        // Create component through service
        ComponentId componentId = componentService.createComponent("Test service component");
        
        // Verify component was created
        assertNotNull(componentId);
        
        // Verify component was saved
        Optional<ComponentPort> retrieved = componentRepository.findById(componentId);
        assertTrue(retrieved.isPresent());
        
        // Verify events were dispatched
        assertTrue(capturedEvents.size() > 0);
        
        // Check for ComponentCreated event
        boolean foundComponentCreatedEvent = capturedEvents.stream()
            .anyMatch(event -> event instanceof ComponentCreated);
        assertTrue(foundComponentCreatedEvent, "Should have dispatched ComponentCreated event");
    }
    
    @Test
    @DisplayName("Test creating composite component with port interface")
    void testCreateCompositeComponentWithPortInterface() {
        // Create a domain composite component
        CompositeComponent composite = CompositeFactory.createComposite(CompositeType.STANDARD, "Test composite");
        
        // Create port interface from domain composite
        CompositeComponentPort compositePort = ComponentAdapter.createCompositeComponentPort(composite);
        
        // Verify port interface
        assertNotNull(compositePort);
        assertEquals(composite.getId().getIdString(), compositePort.getId().getIdString());
        
        // Save port interface to repository
        componentRepository.save(compositePort);
        
        // Retrieve from repository
        Optional<ComponentPort> retrieved = componentRepository.findById(composite.getId());
        assertTrue(retrieved.isPresent());
        assertTrue(retrieved.get() instanceof CompositeComponentPort);
        
        // Verify retrieved composite
        CompositeComponentPort retrievedPort = (CompositeComponentPort) retrieved.get();
        assertEquals(composite.getId().getIdString(), retrievedPort.getId().getIdString());
    }
    
    @Test
    @DisplayName("Test creating machine with port interface")
    void testCreateMachineWithPortInterface() {
        // Create a domain machine
        Machine machine = MachineFactory.createMachine(MachineType.STANDARD, "Test machine", "Description", "1.0.0");
        
        // Create port interface from domain machine
        MachinePort machinePort = MachineAdapter.createMachinePort(machine);
        
        // Verify port interface
        assertNotNull(machinePort);
        assertEquals(machine.getId().getIdString(), machinePort.getId().getIdString());
        
        // Save port interface to repository
        machineRepository.save(machinePort);
        
        // Retrieve from repository
        Optional<MachinePort> retrieved = machineRepository.findById(machine.getId());
        assertTrue(retrieved.isPresent());
        
        // Verify retrieved machine
        MachinePort retrievedPort = retrieved.get();
        assertEquals(machine.getId().getIdString(), retrievedPort.getId().getIdString());
        assertEquals(machine.getMachineId(), retrievedPort.getMachineId());
    }
    
    @Test
    @DisplayName("Test adding component to composite through port interfaces")
    void testAddComponentToCompositeWithPortInterfaces() {
        // Create a domain composite component
        CompositeComponent composite = CompositeFactory.createComposite(CompositeType.STANDARD, "Test composite");
        CompositeComponentPort compositePort = ComponentAdapter.createCompositeComponentPort(composite);
        
        // Create a domain component
        Component component = Component.create("Test component");
        ComponentPort componentPort = ComponentAdapter.createComponentPort(component);
        
        // Add component to composite
        boolean added = compositePort.addComponent("testComponent", componentPort);
        assertTrue(added);
        
        // Verify component was added
        ComponentPort retrievedComponent = compositePort.getComponent("testComponent");
        assertNotNull(retrievedComponent);
        assertEquals(component.getId().getIdString(), retrievedComponent.getId().getIdString());
    }
    
    @Test
    @DisplayName("Test DataFlowService with port interfaces")
    void testDataFlowServiceWithPortInterfaces() {
        // Create mocks for testing
        LoggerPort mockLogger = mock(LoggerPort.class);
        ComponentRepository repo = new InMemoryComponentRepository(mockLogger);
        EventDispatcher mockDispatcher = mock(EventDispatcher.class);
        DataFlowService dataFlowService = new DataFlowService(
                repo, null, mockDispatcher, mockLogger);
        
        // Create a component and its port interface
        Component component = Component.create("DataFlow test component");
        component.transitionTo(LifecycleState.ACTIVE);
        ComponentPort componentPort = ComponentAdapter.createComponentPort(component);
        
        // Save to repository
        repo.save(componentPort);
        
        // Publish data through service
        ComponentId componentId = component.getId();
        Map<String, Object> testData = new HashMap<>();
        testData.put("testKey", "testValue");
        dataFlowService.publishData(componentId, "testChannel", testData);
        
        // Verify event was dispatched
        verify(mockDispatcher, times(1)).dispatch(any(DomainEvent.class));
    }
    
    @Test
    @DisplayName("Test lifecycle transitions with port interfaces")
    void testLifecycleTransitionsWithPortInterfaces() {
        // Create a component and port
        Component component = Component.create("Lifecycle test component");
        ComponentPort componentPort = ComponentAdapter.createComponentPort(component);
        
        // Initial state should be INITIALIZED
        assertEquals(LifecycleState.INITIALIZED, componentPort.getLifecycleState());
        
        // Transition through states using port interface
        componentPort.transitionTo(LifecycleState.READY);
        assertEquals(LifecycleState.READY, componentPort.getLifecycleState());
        assertEquals(LifecycleState.READY, component.getLifecycleState());
        
        componentPort.activate();
        assertEquals(LifecycleState.ACTIVE, componentPort.getLifecycleState());
        assertEquals(LifecycleState.ACTIVE, component.getLifecycleState());
        
        componentPort.deactivate();
        assertEquals(LifecycleState.READY, componentPort.getLifecycleState());
        assertEquals(LifecycleState.READY, component.getLifecycleState());
        
        componentPort.terminate();
        assertEquals(LifecycleState.TERMINATED, componentPort.getLifecycleState());
        assertEquals(LifecycleState.TERMINATED, component.getLifecycleState());
    }
    
    @Test
    @DisplayName("Test event publishing with port interfaces")
    void testEventPublishingWithPortInterfaces() {
        // Create a component and port
        Component component = Component.create("Event test component");
        ComponentPort componentPort = ComponentAdapter.createComponentPort(component);
        
        // Create a mock event dispatcher
        EventDispatcher mockDispatcher = mock(EventDispatcher.class);
        
        // Create a data event through the component
        Map<String, Object> testData = new HashMap<>();
        testData.put("testKey", "testValue");
        componentPort.publishData("testChannel", testData);
        
        // Get events from the port
        List<DomainEvent> events = componentPort.getDomainEvents();
        
        // Verify events were created
        assertTrue(events.size() > 0);
        
        // Dispatch the events
        for (DomainEvent event : events) {
            mockDispatcher.dispatch(event);
        }
        
        // Verify events were dispatched
        verify(mockDispatcher, times(events.size())).dispatch(any(DomainEvent.class));
        
        // Clear events
        componentPort.clearEvents();
        assertTrue(componentPort.getDomainEvents().isEmpty());
    }
}