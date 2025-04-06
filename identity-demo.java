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


import java.util.UUID;
import java.time.Instant;
import java.util.*;

class IdentityDemo {
    public static void main(String[] args) {
        System.out.println("=== Tube Identity Demonstration ===\n");
        
        // Create sample identities to show what they'd contain in memory
        showAdamTubeIdentity();
        showParentChildRelationship();
        showMultiLevelHierarchy();
    }
    
    static void showAdamTubeIdentity() {
        System.out.println("--- Adam Tube Identity ---");
        
        // In a real implementation, this would be a TubeIdentity object
        // Here we just show what it would contain
        String id = UUID.randomUUID().toString();
        String reason = "Demo Adam Tube";
        Instant creationTime = Instant.now();
        String address = "T<" + id.substring(0, 8) + ">";
        
        System.out.println("Unique ID: " + id);
        System.out.println("Reason: " + reason);
        System.out.println("Creation Time: " + creationTime);
        System.out.println("Is Adam Tube: true");
        System.out.println("Hierarchical Address: " + address);
        System.out.println("Parent: null");
        System.out.println();
    }
    
    static void showParentChildRelationship() {
        System.out.println("--- Parent-Child Relationship ---");
        
        // Create parent
        String parentId = UUID.randomUUID().toString();
        String parentReason = "Demo Parent Tube";
        String parentAddress = "T<" + parentId.substring(0, 8) + ">";
        
        // Create child
        String childId = UUID.randomUUID().toString();
        String childReason = "Demo Child Tube";
        String childAddress = parentAddress + "." + childId.substring(0, 8);
        
        // Show parent info
        System.out.println("Parent:");
        System.out.println("  ID: " + parentId);
        System.out.println("  Reason: " + parentReason);
        System.out.println("  Address: " + parentAddress);
        System.out.println("  Is Adam Tube: true");
        System.out.println("  Children: 1");
        
        // Show child info
        System.out.println("\nChild:");
        System.out.println("  ID: " + childId);
        System.out.println("  Reason: " + childReason);
        System.out.println("  Address: " + childAddress);
        System.out.println("  Is Adam Tube: false");
        System.out.println("  Parent ID: " + parentId);
        System.out.println();
    }
    
    static void showMultiLevelHierarchy() {
        System.out.println("--- Multi-Level Hierarchy ---");
        
        // Create Adam (root)
        String adamId = UUID.randomUUID().toString();
        String adamAddress = "T<" + adamId.substring(0, 8) + ">";
        
        // Create two children
        String child1Id = UUID.randomUUID().toString();
        String child1Address = adamAddress + "." + child1Id.substring(0, 8);
        
        String child2Id = UUID.randomUUID().toString();
        String child2Address = adamAddress + "." + child2Id.substring(0, 8);
        
        // Create grandchildren
        String grandchild1Id = UUID.randomUUID().toString();
        String grandchild1Address = child1Address + "." + grandchild1Id.substring(0, 8);
        
        String grandchild2Id = UUID.randomUUID().toString();
        String grandchild2Address = child1Address + "." + grandchild2Id.substring(0, 8);
        
        // Print the hierarchy
        System.out.println("Adam (root):");
        System.out.println("  ID: " + adamId);
        System.out.println("  Address: " + adamAddress);
        System.out.println("  Children: 2");
        
        System.out.println("\nFirst Child:");
        System.out.println("  ID: " + child1Id);
        System.out.println("  Address: " + child1Address);
        System.out.println("  Parent: " + adamId);
        System.out.println("  Children: 2");
        
        System.out.println("\nSecond Child:");
        System.out.println("  ID: " + child2Id);
        System.out.println("  Address: " + child2Address);
        System.out.println("  Parent: " + adamId);
        System.out.println("  Children: 0");
        
        System.out.println("\nFirst Grandchild:");
        System.out.println("  ID: " + grandchild1Id);
        System.out.println("  Address: " + grandchild1Address);
        System.out.println("  Parent: " + child1Id);
        
        System.out.println("\nSecond Grandchild:");
        System.out.println("  ID: " + grandchild2Id);
        System.out.println("  Address: " + grandchild2Address);
        System.out.println("  Parent: " + child1Id);
        
        // Show tracing lineage up from a grandchild
        System.out.println("\nTracing lineage from Second Grandchild:");
        System.out.println("  My ID: " + grandchild2Id);
        System.out.println("  My Parent: " + child1Id);
        System.out.println("  My Grandparent: " + adamId);
        System.out.println();
    }
}