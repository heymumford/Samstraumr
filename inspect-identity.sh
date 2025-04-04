#!/bin/bash

# This script demonstrates how TubeIdentity is used in the system
# by creating and showing identity information directly

echo "==== Tube Identity Inspection Tool ===="
echo "Creating sample identities to inspect their properties..."

# Source project paths
source ./.samstraumr.config

# Create a simple identity demo that doesn't depend on external libraries
cat > SimpleIdentityDemo.java <<EOF
import java.util.UUID;
import java.time.Instant;
import java.util.*;

/**
 * A simple demonstration of TubeIdentity concepts without dependencies
 */
public class SimpleIdentityDemo {
    public static void main(String[] args) {
        System.out.println("===== Tube Identity Demonstration =====");
        
        // Demonstrate the identity model
        demonstrateAdamIdentity();
        demonstrateParentChildRelationship();
        demonstrateMultiLevelHierarchy();
    }
    
    static void demonstrateAdamIdentity() {
        System.out.println("\n--- Adam Tube Identity ---");
        
        // Create an Adam (origin) tube identity
        String id = UUID.randomUUID().toString();
        String reason = "Adam Tube Demo";
        Instant creationTime = Instant.now();
        String address = "T<" + id.substring(0, 8) + ">";
        
        // Display the identity properties
        System.out.println("Unique ID: " + id);
        System.out.println("Reason: " + reason);
        System.out.println("Creation Time: " + creationTime);
        System.out.println("Is Adam Tube: true");
        System.out.println("Hierarchical Address: " + address);
        System.out.println("Parent: null");
    }
    
    static void demonstrateParentChildRelationship() {
        System.out.println("\n--- Parent-Child Relationship ---");
        
        // Create parent identity
        String parentId = UUID.randomUUID().toString();
        String parentReason = "Parent Tube Demo";
        String parentAddress = "T<" + parentId.substring(0, 8) + ">";
        
        // Create child identity
        String childId = UUID.randomUUID().toString();
        String childReason = "Child Tube Demo";
        String childAddress = parentAddress + "." + childId.substring(0, 8);
        
        // Show parent info
        System.out.println("Parent:");
        System.out.println("  ID: " + parentId);
        System.out.println("  Reason: " + parentReason);
        System.out.println("  Address: " + parentAddress);
        System.out.println("  Is Adam Tube: true");
        
        // Show child info
        System.out.println("\nChild:");
        System.out.println("  ID: " + childId);
        System.out.println("  Reason: " + childReason);
        System.out.println("  Address: " + childAddress);
        System.out.println("  Is Adam Tube: false");
        System.out.println("  Parent ID: " + parentId);
    }
    
    static void demonstrateMultiLevelHierarchy() {
        System.out.println("\n--- Multi-Level Hierarchy ---");
        
        // Create Adam (root) identity
        String adamId = UUID.randomUUID().toString();
        String adamAddress = "T<" + adamId.substring(0, 8) + ">";
        
        // Create first generation - 2 children
        String child1Id = UUID.randomUUID().toString();
        String child1Address = adamAddress + "." + child1Id.substring(0, 8);
        
        String child2Id = UUID.randomUUID().toString();
        String child2Address = adamAddress + "." + child2Id.substring(0, 8);
        
        // Create second generation - 2 grandchildren from first child
        String grandchild1Id = UUID.randomUUID().toString();
        String grandchild1Address = child1Address + "." + grandchild1Id.substring(0, 8);
        
        String grandchild2Id = UUID.randomUUID().toString();
        String grandchild2Address = child1Address + "." + grandchild2Id.substring(0, 8);
        
        // Show the hierarchy
        System.out.println("Adam (root):");
        System.out.println("  ID: " + adamId);
        System.out.println("  Address: " + adamAddress);
        
        System.out.println("\nFirst Child:");
        System.out.println("  ID: " + child1Id);
        System.out.println("  Address: " + child1Address);
        System.out.println("  Parent: " + adamId);
        
        System.out.println("\nSecond Child:");
        System.out.println("  ID: " + child2Id);
        System.out.println("  Address: " + child2Address);
        System.out.println("  Parent: " + adamId);
        
        System.out.println("\nFirst Grandchild:");
        System.out.println("  ID: " + grandchild1Id);
        System.out.println("  Address: " + grandchild1Address);
        System.out.println("  Parent: " + child1Id);
        
        System.out.println("\nSecond Grandchild:");
        System.out.println("  ID: " + grandchild2Id);
        System.out.println("  Address: " + grandchild2Address);
        System.out.println("  Parent: " + child1Id);
        
        // Trace lineage from a grandchild
        System.out.println("\nTracing lineage from Second Grandchild:");
        System.out.println("  My ID: " + grandchild2Id);
        System.out.println("  My Parent: " + child1Id);
        System.out.println("  My Grandparent: " + adamId);
    }
}
EOF

# Compile the file
echo "Compiling..."
javac SimpleIdentityDemo.java

# Run the demo
echo "Running identity demo..."
java SimpleIdentityDemo

# Cleanup
rm SimpleIdentityDemo.java SimpleIdentityDemo.class