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

package org.s8r.domain.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.s8r.domain.component.composite.ComponentConnection;
import org.s8r.domain.component.composite.ConnectionType;
import org.s8r.domain.exception.ConnectionCycleException;
import org.s8r.domain.exception.NonExistentComponentReferenceException;
import org.s8r.domain.identity.ComponentId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CompositeConnectionValidator}.
 */
@DisplayName("Composite Connection Validator Tests")
class CompositeConnectionValidatorTest {

    @Test
    @DisplayName("Valid connections should pass validation")
    void validConnectionsShouldPassValidation() {
        ComponentId compositeId = ComponentId.create("TestComposite");
        ComponentId sourceId = ComponentId.create("SourceComponent");
        ComponentId targetId = ComponentId.create("TargetComponent");
        
        // Mock component exists predicate that always returns true
        Predicate<ComponentId> componentExists = id -> true;
        
        // Empty list of existing connections
        List<ComponentConnection> existingConnections = new ArrayList<>();
        
        // This should not throw any exceptions
        assertDoesNotThrow(() -> CompositeConnectionValidator.validateConnection(
                compositeId, sourceId, targetId, ConnectionType.DATA_FLOW, existingConnections, componentExists));
    }
    
    @Test
    @DisplayName("Non-existent source component should throw exception")
    void nonExistentSourceComponentShouldThrow() {
        ComponentId compositeId = ComponentId.create("TestComposite");
        ComponentId sourceId = ComponentId.create("NonExistentSource");
        ComponentId targetId = ComponentId.create("TargetComponent");
        
        // Mock component exists predicate that returns false for source
        Predicate<ComponentId> componentExists = id -> !id.equals(sourceId);
        
        List<ComponentConnection> existingConnections = new ArrayList<>();
        
        NonExistentComponentReferenceException exception = assertThrows(
                NonExistentComponentReferenceException.class,
                () -> CompositeConnectionValidator.validateConnection(
                        compositeId, sourceId, targetId, ConnectionType.DATA_FLOW, existingConnections, componentExists)
        );
        
        assertEquals(compositeId, exception.getReferringComponentId());
        assertEquals(sourceId, exception.getReferencedComponentId());
        assertEquals("connect", exception.getOperationType());
    }
    
    @Test
    @DisplayName("Non-existent target component should throw exception")
    void nonExistentTargetComponentShouldThrow() {
        ComponentId compositeId = ComponentId.create("TestComposite");
        ComponentId sourceId = ComponentId.create("SourceComponent");
        ComponentId targetId = ComponentId.create("NonExistentTarget");
        
        // Mock component exists predicate that returns false for target
        Predicate<ComponentId> componentExists = id -> !id.equals(targetId);
        
        List<ComponentConnection> existingConnections = new ArrayList<>();
        
        NonExistentComponentReferenceException exception = assertThrows(
                NonExistentComponentReferenceException.class,
                () -> CompositeConnectionValidator.validateConnection(
                        compositeId, sourceId, targetId, ConnectionType.DATA_FLOW, existingConnections, componentExists)
        );
        
        assertEquals(compositeId, exception.getReferringComponentId());
        assertEquals(targetId, exception.getReferencedComponentId());
        assertEquals("connect", exception.getOperationType());
    }
    
    @Test
    @DisplayName("Connection creating a direct cycle should throw exception")
    void directCycleShouldThrow() {
        ComponentId compositeId = ComponentId.create("TestComposite");
        ComponentId sourceId = ComponentId.create("ComponentA");
        ComponentId targetId = ComponentId.create("ComponentB");
        
        // Create existing connection B -> A
        ComponentConnection existingConnection = new ComponentConnection(
                targetId, sourceId, ConnectionType.DATA_FLOW, "Existing connection");
        
        List<ComponentConnection> existingConnections = Arrays.asList(existingConnection);
        
        // Mock component exists predicate that always returns true
        Predicate<ComponentId> componentExists = id -> true;
        
        // Attempt to create connection A -> B, which would create cycle A -> B -> A
        ConnectionCycleException exception = assertThrows(
                ConnectionCycleException.class,
                () -> CompositeConnectionValidator.validateConnection(
                        compositeId, sourceId, targetId, ConnectionType.DATA_FLOW, existingConnections, componentExists)
        );
        
        assertEquals(sourceId, exception.getSourceId());
        assertEquals(targetId, exception.getTargetId());
        assertTrue(exception.getCyclePath().contains(sourceId));
        assertTrue(exception.getCyclePath().contains(targetId));
        assertTrue(exception.getMessage().contains("would create a cycle"));
    }
    
    @Test
    @DisplayName("Connection creating an indirect cycle should throw exception")
    void indirectCycleShouldThrow() {
        ComponentId compositeId = ComponentId.create("TestComposite");
        ComponentId componentA = ComponentId.create("ComponentA");
        ComponentId componentB = ComponentId.create("ComponentB");
        ComponentId componentC = ComponentId.create("ComponentC");
        ComponentId componentD = ComponentId.create("ComponentD");
        
        // Create existing connections: A -> B -> C -> D
        List<ComponentConnection> existingConnections = Arrays.asList(
                new ComponentConnection(componentA, componentB, ConnectionType.DATA_FLOW, "A to B"),
                new ComponentConnection(componentB, componentC, ConnectionType.DATA_FLOW, "B to C"),
                new ComponentConnection(componentC, componentD, ConnectionType.DATA_FLOW, "C to D")
        );
        
        // Mock component exists predicate that always returns true
        Predicate<ComponentId> componentExists = id -> true;
        
        // Attempt to create connection D -> A, which would create cycle A -> B -> C -> D -> A
        ConnectionCycleException exception = assertThrows(
                ConnectionCycleException.class,
                () -> CompositeConnectionValidator.validateConnection(
                        compositeId, componentD, componentA, ConnectionType.DATA_FLOW, existingConnections, componentExists)
        );
        
        assertEquals(componentD, exception.getSourceId());
        assertEquals(componentA, exception.getTargetId());
    }
    
    @Test
    @DisplayName("Peer connections should not cause cycle validation")
    void peerConnectionsShouldNotCauseCycleValidation() {
        ComponentId compositeId = ComponentId.create("TestComposite");
        ComponentId componentA = ComponentId.create("ComponentA");
        ComponentId componentB = ComponentId.create("ComponentB");
        
        // Create existing peer connection B -> A
        ComponentConnection existingConnection = new ComponentConnection(
                componentB, componentA, ConnectionType.PEER, "Existing peer connection");
        
        List<ComponentConnection> existingConnections = Arrays.asList(existingConnection);
        
        // Mock component exists predicate that always returns true
        Predicate<ComponentId> componentExists = id -> true;
        
        // This should not throw exception even though it would create a "cycle" with peer connections
        assertDoesNotThrow(() -> CompositeConnectionValidator.validateConnection(
                compositeId, componentA, componentB, ConnectionType.PEER, existingConnections, componentExists));
    }
    
    @Test
    @DisplayName("Multiple connections between same components should be allowed if they don't create cycles")
    void multipleConnectionsBetweenSameComponentsShouldBeAllowed() {
        ComponentId compositeId = ComponentId.create("TestComposite");
        ComponentId componentA = ComponentId.create("ComponentA");
        ComponentId componentB = ComponentId.create("ComponentB");
        
        // Create existing connection A -> B
        ComponentConnection existingConnection = new ComponentConnection(
                componentA, componentB, ConnectionType.DATA_FLOW, "First connection");
        
        List<ComponentConnection> existingConnections = Arrays.asList(existingConnection);
        
        // Mock component exists predicate that always returns true
        Predicate<ComponentId> componentExists = id -> true;
        
        // This should not throw exception - same direction connection
        assertDoesNotThrow(() -> CompositeConnectionValidator.validateConnection(
                compositeId, componentA, componentB, ConnectionType.CONTROL, existingConnections, componentExists));
        
        // But opposite direction should throw
        assertThrows(
                ConnectionCycleException.class,
                () -> CompositeConnectionValidator.validateConnection(
                        compositeId, componentB, componentA, ConnectionType.DATA_FLOW, existingConnections, componentExists)
        );
    }
}