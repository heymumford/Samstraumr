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

package org.s8r.domain.component.composite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.s8r.domain.component.Component;
import org.s8r.domain.exception.ConnectionCycleException;
import org.s8r.domain.exception.NonExistentComponentReferenceException;
import org.s8r.domain.identity.ComponentId;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for composite connection validation.
 */
@DisplayName("Composite Connection Validation Integration Tests")
class CompositeConnectionValidationTest {
    
    private CompositeComponent composite;
    private Component componentA;
    private Component componentB;
    private Component componentC;
    private ComponentId nonExistentId;
    
    @BeforeEach
    void setUp() {
        // Create a composite and components
        composite = CompositeComponent.create(
                ComponentId.create("TestComposite"), 
                CompositeType.STANDARD);
        
        componentA = Component.create(ComponentId.create("ComponentA"));
        componentB = Component.create(ComponentId.create("ComponentB"));
        componentC = Component.create(ComponentId.create("ComponentC"));
        
        // Add components to the composite
        composite.addComponent(componentA);
        composite.addComponent(componentB);
        composite.addComponent(componentC);
        
        // Create a non-existent component ID
        nonExistentId = ComponentId.create("NonExistentComponent");
    }
    
    @Test
    @DisplayName("Valid connections should be created successfully")
    void validConnectionsShouldBeCreatedSuccessfully() {
        // Create a valid connection
        ComponentConnection connection = composite.connect(
                componentA.getId(), 
                componentB.getId(), 
                ConnectionType.DATA_FLOW, 
                "Test connection");
        
        // Verify the connection was created
        assertNotNull(connection);
        assertEquals(componentA.getId(), connection.getSourceId());
        assertEquals(componentB.getId(), connection.getTargetId());
        assertEquals(ConnectionType.DATA_FLOW, connection.getType());
        
        // Should be in the list of connections
        assertTrue(composite.getConnections().contains(connection));
    }
    
    @Test
    @DisplayName("Connection with non-existent source component should throw exception")
    void connectionWithNonExistentSourceShouldThrow() {
        NonExistentComponentReferenceException exception = assertThrows(
                NonExistentComponentReferenceException.class,
                () -> composite.connect(
                        nonExistentId, 
                        componentB.getId(), 
                        ConnectionType.DATA_FLOW, 
                        "Invalid connection")
        );
        
        assertEquals(composite.getId(), exception.getReferringComponentId());
        assertEquals(nonExistentId, exception.getReferencedComponentId());
    }
    
    @Test
    @DisplayName("Connection with non-existent target component should throw exception")
    void connectionWithNonExistentTargetShouldThrow() {
        NonExistentComponentReferenceException exception = assertThrows(
                NonExistentComponentReferenceException.class,
                () -> composite.connect(
                        componentA.getId(), 
                        nonExistentId, 
                        ConnectionType.DATA_FLOW, 
                        "Invalid connection")
        );
        
        assertEquals(composite.getId(), exception.getReferringComponentId());
        assertEquals(nonExistentId, exception.getReferencedComponentId());
    }
    
    @Test
    @DisplayName("Connection creating a direct cycle should throw exception")
    void connectionCreatingDirectCycleShouldThrow() {
        // Create initial A -> B connection
        composite.connect(
                componentA.getId(), 
                componentB.getId(), 
                ConnectionType.DATA_FLOW, 
                "A to B");
        
        // Attempt to create B -> A, which would form a cycle
        ConnectionCycleException exception = assertThrows(
                ConnectionCycleException.class,
                () -> composite.connect(
                        componentB.getId(), 
                        componentA.getId(), 
                        ConnectionType.DATA_FLOW, 
                        "B to A")
        );
        
        assertEquals(componentB.getId(), exception.getSourceId());
        assertEquals(componentA.getId(), exception.getTargetId());
        assertTrue(exception.getCyclePath().contains(componentA.getId()));
        assertTrue(exception.getCyclePath().contains(componentB.getId()));
    }
    
    @Test
    @DisplayName("Connection creating an indirect cycle should throw exception")
    void connectionCreatingIndirectCycleShouldThrow() {
        // Create connections to form a chain: A -> B -> C
        composite.connect(
                componentA.getId(), 
                componentB.getId(), 
                ConnectionType.DATA_FLOW, 
                "A to B");
        
        composite.connect(
                componentB.getId(), 
                componentC.getId(), 
                ConnectionType.DATA_FLOW, 
                "B to C");
        
        // Attempt to create C -> A, which would form a cycle A -> B -> C -> A
        ConnectionCycleException exception = assertThrows(
                ConnectionCycleException.class,
                () -> composite.connect(
                        componentC.getId(), 
                        componentA.getId(), 
                        ConnectionType.DATA_FLOW, 
                        "C to A")
        );
        
        assertEquals(componentC.getId(), exception.getSourceId());
        assertEquals(componentA.getId(), exception.getTargetId());
        
        // Verify the cycle path contains all components in the cycle
        assertTrue(exception.getCyclePath().contains(componentA.getId()));
        assertTrue(exception.getCyclePath().contains(componentB.getId()));
        assertTrue(exception.getCyclePath().contains(componentC.getId()));
    }
    
    @Test
    @DisplayName("Peer connections should not create cycles")
    void peerConnectionsShouldNotCreateCycles() {
        // Create initial A -> B peer connection
        composite.connect(
                componentA.getId(), 
                componentB.getId(), 
                ConnectionType.PEER, 
                "A to B peer");
        
        // Create B -> A peer connection - should not throw exception
        ComponentConnection connection = composite.connect(
                componentB.getId(), 
                componentA.getId(), 
                ConnectionType.PEER, 
                "B to A peer");
        
        assertNotNull(connection);
        assertEquals(ConnectionType.PEER, connection.getType());
    }
}