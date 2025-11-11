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

package isolated;

import java.util.ArrayList;
import java.util.List;

import org.s8r.domain.component.composite.ComponentConnection;
import org.s8r.domain.component.composite.ConnectionType;
import org.s8r.domain.exception.ConnectionCycleException;
import org.s8r.domain.exception.NonExistentComponentReferenceException;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.validation.CompositeConnectionValidator;

/**
 * Simple manual test for {@link CompositeConnectionValidator} that runs without JUnit.
 */
public class ManualCompositeConnectionTest {

    public static void main(String[] args) {
        System.out.println("Running manual composite connection validation tests...\n");
        
        // Create test component IDs
        ComponentId compositeId = ComponentId.create("TestComposite");
        ComponentId componentA = ComponentId.create("ComponentA");
        ComponentId componentB = ComponentId.create("ComponentB");
        ComponentId componentC = ComponentId.create("ComponentC");
        ComponentId nonExistentId = ComponentId.create("NonExistent");
        
        List<ComponentConnection> connections = new ArrayList<>();
        
        // 1. Test component existence validation
        System.out.println("Testing component existence validation:");
        
        try {
            CompositeConnectionValidator.validateConnection(
                    compositeId, 
                    componentA, 
                    componentB, 
                    ConnectionType.DATA_FLOW, 
                    connections, 
                    id -> !id.equals(nonExistentId));
            
            System.out.println("✓ Valid components accepted");
        } catch (Exception e) {
            System.out.println("✗ Unexpected exception: " + e.getMessage());
        }
        
        try {
            CompositeConnectionValidator.validateConnection(
                    compositeId,
                    nonExistentId, 
                    componentB, 
                    ConnectionType.DATA_FLOW, 
                    connections, 
                    id -> !id.equals(nonExistentId));
            
            System.out.println("✗ Failed to catch non-existent source component");
        } catch (NonExistentComponentReferenceException e) {
            System.out.println("✓ Correctly caught non-existent source component: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ Wrong exception type: " + e.getClass().getSimpleName());
        }
        
        // 2. Test cycle detection
        System.out.println("\nTesting cycle detection:");
        
        // Add A -> B connection
        connections.add(new ComponentConnection(componentA, componentB, ConnectionType.DATA_FLOW, "Test A->B"));
        
        try {
            CompositeConnectionValidator.validateConnection(
                    compositeId,
                    componentB, 
                    componentC, 
                    ConnectionType.DATA_FLOW, 
                    connections, 
                    id -> true);
            
            System.out.println("✓ Non-cycle connection accepted (B->C)");
            
            // Add B -> C connection
            connections.add(new ComponentConnection(componentB, componentC, ConnectionType.DATA_FLOW, "Test B->C"));
        } catch (Exception e) {
            System.out.println("✗ Unexpected exception: " + e.getMessage());
        }
        
        try {
            CompositeConnectionValidator.validateConnection(
                    compositeId,
                    componentC, 
                    componentA, 
                    ConnectionType.DATA_FLOW, 
                    connections, 
                    id -> true);
            
            System.out.println("✗ Failed to catch cycle (A->B->C->A)");
        } catch (ConnectionCycleException e) {
            System.out.println("✓ Correctly caught cycle (A->B->C->A): " + e.getMessage());
            System.out.println("  Cycle path: " + e.getCyclePathString());
        } catch (Exception e) {
            System.out.println("✗ Wrong exception type: " + e.getClass().getSimpleName());
        }
        
        // 3. Test peer connections (should not create cycles)
        System.out.println("\nTesting peer connections:");
        
        List<ComponentConnection> peerConnections = new ArrayList<>();
        peerConnections.add(new ComponentConnection(componentA, componentB, ConnectionType.PEER, "A->B peer"));
        
        try {
            CompositeConnectionValidator.validateConnection(
                    compositeId,
                    componentB, 
                    componentA, 
                    ConnectionType.PEER, 
                    peerConnections, 
                    id -> true);
            
            System.out.println("✓ Peer connection in opposite direction accepted");
        } catch (Exception e) {
            System.out.println("✗ Unexpected exception on peer connection: " + e.getMessage());
        }
        
        System.out.println("\nAll tests completed.");
    }
}