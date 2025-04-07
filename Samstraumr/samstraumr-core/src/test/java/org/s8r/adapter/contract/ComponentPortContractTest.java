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

package org.s8r.adapter.contract;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.s8r.adapter.ComponentAdapter;
import org.s8r.domain.component.Component;
import java.util.ArrayList;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.event.ComponentDataEvent;
import org.s8r.domain.event.ComponentStateChangedEvent;
import org.s8r.domain.event.DomainEvent;
import org.s8r.domain.exception.InvalidOperationException;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;

/**
 * Contract tests for the ComponentPort interface.
 * 
 * <p>This test class verifies that any implementation of the ComponentPort
 * interface adheres to the contract defined by the interface. It tests the
 * behavior expected by the application core regardless of the specific adapter
 * implementation.</p>
 * 
 * <p>The tests cover core functionality such as identity, lifecycle management,
 * domain events, and data publishing.</p>
 */
public class ComponentPortContractTest extends PortContractTest<ComponentPort> {

    private Component domainComponent;
    
    @Override
    protected ComponentPort createPortImplementation() {
        // Create a domain component for testing
        domainComponent = Component.create("test-component");
        return ComponentAdapter.createComponentPort(domainComponent);
    }
    
    @Override
    protected void verifyNullInputHandling() {
        // This is tested in nullInputHandlingTests()
    }
    
    @Override
    protected void verifyRequiredMethods() {
        // This is tested across multiple method-specific tests
    }
    
    /**
     * Verifies that the ComponentPort implementation handles null inputs correctly.
     */
    @Test
    @DisplayName("Should handle null inputs gracefully")
    public void nullInputHandlingTests() {
        // Test null lineage entry
        portUnderTest.addToLineage(null);
        // No exception should be thrown
        
        // Test null channel in publishData
        Map<String, Object> testData = new HashMap<>();
        testData.put("key", "value");
        portUnderTest.publishData(null, testData);
        // No exception should be thrown
        
        // Test null data in publishData
        portUnderTest.publishData("test-channel", (Map<String, Object>)null);
        // No exception should be thrown
        
        // Test null key-value in publishData
        portUnderTest.publishData("test-channel", null, "value");
        portUnderTest.publishData("test-channel", "key", null);
        // No exception should be thrown
    }
    
    /**
     * Tests the identity information provided by ComponentPort implementations.
     */
    @Test
    @DisplayName("Should provide basic component identity information")
    public void basicIdentityTests() {
        // Test component identity information
        assertNotNull(portUnderTest.getId(), "Component ID should not be null");
        assertNotNull(portUnderTest.getName(), "Component name should not be null");
        assertNotNull(portUnderTest.getType(), "Component type should not be null");
        assertEquals("Component", portUnderTest.getType(), "Default component type should be 'Component'");
        
        // The state should reflect the lifecycle state
        assertEquals(portUnderTest.getLifecycleState().name(), portUnderTest.getState(), 
                "Component state should match lifecycle state name");
        
        // Creation time should be set
        assertNotNull(portUnderTest.getCreationTime(), "Creation time should not be null");
        assertTrue(portUnderTest.getCreationTime().isBefore(Instant.now()),
                "Creation time should be in the past");
    }
    
    /**
     * Tests the lifecycle management functionality of ComponentPort implementations.
     */
    @Test
    @DisplayName("Should manage component lifecycle correctly")
    public void lifecycleManagementTests() {
        // Initial state should be INITIALIZED
        assertEquals(LifecycleState.INITIALIZED, portUnderTest.getLifecycleState(), 
                "Initial state should be INITIALIZED");
        
        // Transition to READY
        portUnderTest.transitionTo(LifecycleState.READY);
        assertEquals(LifecycleState.READY, portUnderTest.getLifecycleState(), 
                "State should be READY after transition");
        
        // Activate component
        portUnderTest.activate();
        assertEquals(LifecycleState.ACTIVE, portUnderTest.getLifecycleState(), 
                "State should be ACTIVE after activation");
        
        // Deactivate component
        portUnderTest.deactivate();
        assertEquals(LifecycleState.READY, portUnderTest.getLifecycleState(), 
                "State should be READY after deactivation");
        
        // Terminate component
        portUnderTest.terminate();
        assertEquals(LifecycleState.TERMINATED, portUnderTest.getLifecycleState(), 
                "State should be TERMINATED after termination");
    }
    
    /**
     * Tests the invalid lifecycle transitions in ComponentPort implementations.
     */
    @Test
    @DisplayName("Should handle invalid lifecycle transitions correctly")
    public void invalidLifecycleTransitionsTests() {
        // Try to activate a component that's not in READY state
        assertEquals(LifecycleState.INITIALIZED, portUnderTest.getLifecycleState(), 
                "Initial state should be INITIALIZED");
        
        // This should throw an InvalidOperationException
        assertThrows(InvalidOperationException.class, () -> portUnderTest.activate(), 
                "Activating a component not in READY state should throw exception");
        
        // Transition to READY and activate
        portUnderTest.transitionTo(LifecycleState.READY);
        portUnderTest.activate();
        
        // Try to transition to INITIALIZED from ACTIVE
        assertThrows(IllegalStateException.class, 
                () -> portUnderTest.transitionTo(LifecycleState.INITIALIZED),
                "Invalid state transition should throw exception");
    }
    
    /**
     * Tests the domain event management in ComponentPort implementations.
     */
    @Test
    @DisplayName("Should manage domain events correctly")
    public void domainEventTests() {
        // Initially there should be no events
        assertTrue(portUnderTest.getDomainEvents().isEmpty(), 
                "Initial domain events should be empty");
        
        // State change should generate an event
        portUnderTest.transitionTo(LifecycleState.READY);
        
        // Verify events
        List<DomainEvent> events = portUnderTest.getDomainEvents();
        assertFalse(events.isEmpty(), "Domain events should not be empty after state change");
        
        // There should be a ComponentStateChangedEvent
        boolean foundStateChangedEvent = events.stream()
                .anyMatch(e -> e instanceof ComponentStateChangedEvent);
        assertTrue(foundStateChangedEvent, "Should contain ComponentStateChangedEvent");
        
        // Clear events
        portUnderTest.clearEvents();
        assertTrue(portUnderTest.getDomainEvents().isEmpty(), 
                "Domain events should be empty after clear");
    }
    
    /**
     * Tests the data publishing functionality of ComponentPort implementations.
     */
    @Test
    @DisplayName("Should publish data correctly")
    public void dataPublishingTests() {
        // Initially there should be no events
        assertTrue(portUnderTest.getDomainEvents().isEmpty(), 
                "Initial domain events should be empty");
        
        // Publish map data
        Map<String, Object> testData = new HashMap<>();
        testData.put("key1", "value1");
        testData.put("key2", 123);
        portUnderTest.publishData("test-channel", testData);
        
        // Publish key-value data
        portUnderTest.publishData("key-value-channel", "key3", "value3");
        
        // Verify events
        List<DomainEvent> events = portUnderTest.getDomainEvents();
        assertFalse(events.isEmpty(), "Domain events should not be empty after publishing data");
        
        // There should be a ComponentDataEvent
        boolean foundDataEvent = events.stream()
                .anyMatch(e -> e instanceof ComponentDataEvent);
        assertTrue(foundDataEvent, "Should contain ComponentDataEvent");
        
        // Check event specifics
        events.stream()
                .filter(e -> e instanceof ComponentDataEvent)
                .map(e -> (ComponentDataEvent)e)
                .forEach(e -> {
                    assertNotNull(e.getComponentId(), "ComponentId should not be null");
                    assertNotNull(e.getChannel(), "Channel should not be null");
                    assertNotNull(e.getData(), "Data should not be null");
                });
    }
    
    /**
     * Tests the lineage management in ComponentPort implementations.
     */
    @Test
    @DisplayName("Should manage lineage correctly")
    public void lineageManagementTests() {
        // Initially lineage should only contain the component's own entry
        List<String> initialLineage = portUnderTest.getLineage();
        assertNotNull(initialLineage, "Lineage should not be null");
        
        // Add lineage entries
        portUnderTest.addToLineage("ancestor-1");
        portUnderTest.addToLineage("ancestor-2");
        
        // Verify lineage
        List<String> updatedLineage = portUnderTest.getLineage();
        assertTrue(updatedLineage.size() >= initialLineage.size() + 2, 
                "Lineage should have at least 2 more entries");
        assertTrue(updatedLineage.contains("ancestor-1"), "Lineage should contain added ancestor-1");
        assertTrue(updatedLineage.contains("ancestor-2"), "Lineage should contain added ancestor-2");
    }
    
    /**
     * Tests the activity log in ComponentPort implementations.
     */
    @Test
    @DisplayName("Should maintain activity log")
    public void activityLogTests() {
        // Activity log should exist
        List<String> log = portUnderTest.getActivityLog();
        assertNotNull(log, "Activity log should not be null");
        
        // State changes should add to activity log
        int initialLogSize = log.size();
        portUnderTest.transitionTo(LifecycleState.READY);
        
        List<String> updatedLog = portUnderTest.getActivityLog();
        assertTrue(updatedLog.size() > initialLogSize, 
                "Activity log should grow after state change");
    }
}