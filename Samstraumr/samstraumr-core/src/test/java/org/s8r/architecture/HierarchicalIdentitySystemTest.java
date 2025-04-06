package org.s8r.architecture;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.s8r.component.identity.Identity;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.identity.ComponentHierarchy;
import org.s8r.test.annotation.UnitTest;

/**
 * Tests for the Hierarchical Identity System as described in ADR-0008.
 * 
 * <p>This test suite validates the implementation of the hierarchical identity system
 * for components within the Samstraumr framework.
 */
@UnitTest
@Tag("architecture")
@Tag("adr-validation")
@DisplayName("Hierarchical Identity System Tests (ADR-0008)")
public class HierarchicalIdentitySystemTest {

    private ComponentId rootId;
    private ComponentId childId1;
    private ComponentId childId2;
    private ComponentId grandchildId1;
    private ComponentId grandchildId2;
    private ComponentHierarchy hierarchy;

    @BeforeEach
    void setUp() {
        rootId = new ComponentId("root");
        childId1 = new ComponentId("child1");
        childId2 = new ComponentId("child2");
        grandchildId1 = new ComponentId("grandchild1");
        grandchildId2 = new ComponentId("grandchild2");
        
        // Create a test hierarchy
        hierarchy = new ComponentHierarchy(rootId);
        hierarchy.addChild(rootId, childId1);
        hierarchy.addChild(rootId, childId2);
        hierarchy.addChild(childId1, grandchildId1);
        hierarchy.addChild(childId2, grandchildId2);
    }

    @Nested
    @DisplayName("Identity Structure Tests")
    class IdentityStructureTests {
        
        @Test
        @DisplayName("Component ID should contain both local name and UUID")
        void componentIdShouldContainLocalNameAndUuid() {
            ComponentId id = new ComponentId("test-component");
            
            assertNotNull(id.getLocalName(), "Local name should not be null");
            assertEquals("test-component", id.getLocalName(), "Local name should match");
            assertNotNull(id.getUuid(), "UUID should not be null");
            assertTrue(id.getUuid().toString().length() > 30, "UUID should be a valid UUID string");
        }

        @Test
        @DisplayName("Component IDs with same local name should be distinct")
        void componentIdsWithSameLocalNameShouldBeDistinct() {
            ComponentId id1 = new ComponentId("same-name");
            ComponentId id2 = new ComponentId("same-name");
            
            assertNotEquals(id1, id2, "IDs with same local name should be distinct due to UUIDs");
            assertNotEquals(id1.getUuid(), id2.getUuid(), "UUIDs should be different");
        }
    }

    @Nested
    @DisplayName("Path Addressing Tests")
    class PathAddressingTests {
        
        @Test
        @DisplayName("Absolute path should start with root")
        void absolutePathShouldStartWithRoot() {
            String path = hierarchy.getAbsolutePath(grandchildId1);
            
            assertTrue(path.startsWith("/root/"), "Absolute path should start with /root/");
            assertEquals("/root/child1/grandchild1", path, "Path should reflect hierarchy");
        }

        @Test
        @DisplayName("Relative paths should be resolved correctly")
        void relativePathsShouldBeResolvedCorrectly() {
            // Test relative path from childId1 to grandchildId1
            String relativePath = "../child2/grandchild2";
            ComponentId resolved = hierarchy.resolveRelativePath(childId1, relativePath);
            
            assertEquals(grandchildId2, resolved, "Relative path should resolve correctly");
        }
        
        @Test
        @DisplayName("Path querying should find components by pattern")
        void pathQueryingShouldFindComponentsByPattern() {
            List<ComponentId> found = hierarchy.findByPathPattern("/root/*/grand*");
            
            assertEquals(2, found.size(), "Should find both grandchildren");
            assertTrue(found.contains(grandchildId1), "Should contain grandchild1");
            assertTrue(found.contains(grandchildId2), "Should contain grandchild2");
        }
    }

    @Nested
    @DisplayName("Hierarchy Management Tests")
    class HierarchyManagementTests {
        
        @Test
        @DisplayName("Moving components should update paths")
        void movingComponentsShouldUpdatePaths() {
            // Move grandchild1 from child1 to child2
            hierarchy.moveComponent(grandchildId1, childId2);
            
            String newPath = hierarchy.getAbsolutePath(grandchildId1);
            assertEquals("/root/child2/grandchild1", newPath, "Path should be updated after move");
            
            List<ComponentId> child1Children = hierarchy.getChildren(childId1);
            List<ComponentId> child2Children = hierarchy.getChildren(childId2);
            
            assertEquals(0, child1Children.size(), "child1 should have no children");
            assertEquals(2, child2Children.size(), "child2 should have two children");
            assertTrue(child2Children.contains(grandchildId1), "child2 should contain moved grandchild1");
        }
        
        @Test
        @DisplayName("Component removal should clean up hierarchy")
        void componentRemovalShouldCleanupHierarchy() {
            hierarchy.removeComponent(childId1);
            
            assertFalse(hierarchy.contains(childId1), "Removed component should not exist in hierarchy");
            assertFalse(hierarchy.contains(grandchildId1), "Children of removed component should also be removed");
            
            List<ComponentId> rootChildren = hierarchy.getChildren(rootId);
            assertEquals(1, rootChildren.size(), "Root should have only one child left");
            assertEquals(childId2, rootChildren.get(0), "Remaining child should be child2");
        }
    }

    @Nested
    @DisplayName("Identity Persistence Tests")
    class IdentityPersistenceTests {
        
        @Test
        @DisplayName("Serialization and deserialization should preserve identity")
        void serializationAndDeserializationShouldPreserveIdentity() {
            ComponentId id = new ComponentId("serialize-test");
            
            // Convert to serialized form
            String serialized = id.serialize();
            
            // Deserialize
            ComponentId deserialized = ComponentId.deserialize(serialized);
            
            assertEquals(id, deserialized, "Deserialized ID should equal original");
            assertEquals(id.getLocalName(), deserialized.getLocalName(), "Local name should be preserved");
            assertEquals(id.getUuid(), deserialized.getUuid(), "UUID should be preserved");
        }
        
        @Test
        @DisplayName("Hierarchy serialization should preserve structure")
        void hierarchySerializationShouldPreserveStructure() {
            // Serialize hierarchy
            String serialized = hierarchy.serialize();
            
            // Create new hierarchy from serialized form
            ComponentHierarchy deserialized = ComponentHierarchy.deserialize(serialized);
            
            assertTrue(deserialized.contains(rootId), "Root should exist in deserialized hierarchy");
            assertTrue(deserialized.contains(childId1), "child1 should exist in deserialized hierarchy");
            assertTrue(deserialized.contains(grandchildId1), "grandchild1 should exist in deserialized hierarchy");
            
            assertEquals(
                hierarchy.getAbsolutePath(grandchildId1),
                deserialized.getAbsolutePath(grandchildId1),
                "Paths should be preserved"
            );
        }
    }
    
    @Nested
    @DisplayName("Identity Query Tests")
    class IdentityQueryTests {
        
        @Test
        @DisplayName("Finding components by attribute should work")
        void findingComponentsByAttributeShouldWork() {
            // Add attributes to components
            hierarchy.setComponentAttribute(rootId, "type", "container");
            hierarchy.setComponentAttribute(childId1, "type", "processor");
            hierarchy.setComponentAttribute(childId2, "type", "processor");
            hierarchy.setComponentAttribute(grandchildId1, "type", "validator");
            hierarchy.setComponentAttribute(grandchildId2, "type", "transformer");
            
            // Query by type
            List<ComponentId> processors = hierarchy.findByAttribute("type", "processor");
            assertEquals(2, processors.size(), "Should find both processors");
            assertTrue(processors.contains(childId1), "Should contain childId1");
            assertTrue(processors.contains(childId2), "Should contain childId2");
            
            // Query by combination of attributes
            hierarchy.setComponentAttribute(childId1, "status", "active");
            hierarchy.setComponentAttribute(childId2, "status", "standby");
            
            List<ComponentId> activeProcessors = hierarchy.findByAttributes(
                Map.of("type", "processor", "status", "active")
            );
            
            assertEquals(1, activeProcessors.size(), "Should find one active processor");
            assertEquals(childId1, activeProcessors.get(0), "Should find childId1");
        }
        
        @Test
        @DisplayName("Finding components by capability should work")
        void findingComponentsByCapabilityShouldWork() {
            // Add capabilities to components
            hierarchy.addComponentCapability(childId1, "Serializable");
            hierarchy.addComponentCapability(childId1, "Cacheable");
            hierarchy.addComponentCapability(childId2, "Serializable");
            hierarchy.addComponentCapability(grandchildId1, "Validatable");
            
            // Query by capability
            List<ComponentId> serializables = hierarchy.findByCapability("Serializable");
            assertEquals(2, serializables.size(), "Should find two serializable components");
            assertTrue(serializables.contains(childId1), "Should contain childId1");
            assertTrue(serializables.contains(childId2), "Should contain childId2");
            
            // Query by multiple capabilities
            List<ComponentId> serializableAndCacheable = hierarchy.findByCapabilities(
                Set.of("Serializable", "Cacheable")
            );
            
            assertEquals(1, serializableAndCacheable.size(), "Should find one component with both capabilities");
            assertEquals(childId1, serializableAndCacheable.get(0), "Should find childId1");
        }
    }
}