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

package org.s8r.domain.identity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.s8r.domain.exception.ComponentNotFoundException;
import org.s8r.test.annotation.UnitTest;

/**
 * Tests for the ComponentHierarchy class which manages the hierarchical organization
 * of components in the Samstraumr framework.
 * 
 * <p>The component hierarchy is a crucial domain concept that represents the
 * parent-child relationships between components.
 */
@UnitTest
@DisplayName("Component Hierarchy Tests")
class ComponentHierarchyTest {

    private ComponentHierarchy hierarchy;
    private ComponentId rootComponentId;
    private ComponentId childComponentId1;
    private ComponentId childComponentId2;
    private ComponentId grandchildComponentId;
    
    @BeforeEach
    void setUp() {
        // Create test ComponentIds
        rootComponentId = ComponentId.create("Root Component");
        
        // Create lineage with the root component
        List<String> childLineage = Collections.singletonList(rootComponentId.getIdString());
        childComponentId1 = ComponentId.create("Child Component 1", childLineage);
        childComponentId2 = ComponentId.create("Child Component 2", childLineage);
        
        // Create lineage with root and first child
        List<String> grandchildLineage = Arrays.asList(
                rootComponentId.getIdString(),
                childComponentId1.getIdString()
        );
        grandchildComponentId = ComponentId.create("Grandchild Component", grandchildLineage);
        
        // Create the hierarchy
        hierarchy = new ComponentHierarchy();
        
        // Register components in the hierarchy
        hierarchy.registerComponent(rootComponentId);
        hierarchy.registerComponent(childComponentId1);
        hierarchy.registerComponent(childComponentId2);
        hierarchy.registerComponent(grandchildComponentId);
    }
    
    @Nested
    @DisplayName("Component Registration and Retrieval Tests")
    class RegistrationAndRetrievalTests {
        
        @Test
        @DisplayName("registerComponent() should add component to the hierarchy")
        void registerComponentShouldAddComponentToHierarchy() {
            // Given
            ComponentId newComponentId = ComponentId.create("New Component");
            
            // When
            hierarchy.registerComponent(newComponentId);
            
            // Then
            Optional<ComponentId> retrieved = hierarchy.findComponentById(newComponentId.getIdString());
            assertTrue(retrieved.isPresent(), "Component should be found after registration");
            assertEquals(newComponentId, retrieved.get(), "Retrieved component should match registered one");
        }
        
        @Test
        @DisplayName("findComponentById() should return component by ID")
        void findComponentByIdShouldReturnComponentById() {
            // When
            Optional<ComponentId> foundComponent = hierarchy.findComponentById(childComponentId1.getIdString());
            
            // Then
            assertTrue(foundComponent.isPresent(), "Component should be found by ID");
            assertEquals(childComponentId1, foundComponent.get(), "Found component should match expected");
        }
        
        @Test
        @DisplayName("findComponentById() should return empty for unknown ID")
        void findComponentByIdShouldReturnEmptyForUnknownId() {
            // Given
            String unknownId = "unknown-id";
            
            // When
            Optional<ComponentId> foundComponent = hierarchy.findComponentById(unknownId);
            
            // Then
            assertFalse(foundComponent.isPresent(), "Component should not be found for unknown ID");
        }
        
        @Test
        @DisplayName("getComponentById() should return component by ID")
        void getComponentByIdShouldReturnComponentById() {
            // When
            ComponentId foundComponent = hierarchy.getComponentById(childComponentId1.getIdString());
            
            // Then
            assertNotNull(foundComponent, "Component should be found by ID");
            assertEquals(childComponentId1, foundComponent, "Found component should match expected");
        }
        
        @Test
        @DisplayName("getComponentById() should throw exception for unknown ID")
        void getComponentByIdShouldThrowExceptionForUnknownId() {
            // Given
            String unknownId = "unknown-id";
            
            // When/Then
            assertThrows(ComponentNotFoundException.class,
                    () -> hierarchy.getComponentById(unknownId),
                    "Should throw ComponentNotFoundException for unknown ID");
        }
    }
    
    @Nested
    @DisplayName("Parent-Child Relationship Tests")
    class ParentChildRelationshipTests {
        
        @Test
        @DisplayName("getChildComponents() should return direct children of a component")
        void getChildComponentsShouldReturnDirectChildren() {
            // When
            List<ComponentId> children = hierarchy.getChildComponents(rootComponentId);
            
            // Then
            assertEquals(2, children.size(), "Root component should have 2 children");
            assertTrue(children.contains(childComponentId1), "Children should include child 1");
            assertTrue(children.contains(childComponentId2), "Children should include child 2");
            assertFalse(children.contains(grandchildComponentId), "Children should not include grandchild");
        }
        
        @Test
        @DisplayName("getChildComponents() should return empty list for leaf component")
        void getChildComponentsShouldReturnEmptyListForLeafComponent() {
            // When
            List<ComponentId> children = hierarchy.getChildComponents(grandchildComponentId);
            
            // Then
            assertTrue(children.isEmpty(), "Leaf component should have no children");
        }
        
        @Test
        @DisplayName("getParentComponent() should return parent of a component")
        void getParentComponentShouldReturnParentOfComponent() {
            // When
            Optional<ComponentId> parent = hierarchy.getParentComponent(childComponentId1);
            
            // Then
            assertTrue(parent.isPresent(), "Child component should have a parent");
            assertEquals(rootComponentId, parent.get(), "Parent should be the root component");
        }
        
        @Test
        @DisplayName("getParentComponent() should return empty for root component")
        void getParentComponentShouldReturnEmptyForRootComponent() {
            // When
            Optional<ComponentId> parent = hierarchy.getParentComponent(rootComponentId);
            
            // Then
            assertFalse(parent.isPresent(), "Root component should have no parent");
        }
    }
    
    @Nested
    @DisplayName("Hierarchy Navigation Tests")
    class HierarchyNavigationTests {
        
        @Test
        @DisplayName("getAncestors() should return all ancestors in order")
        void getAncestorsShouldReturnAllAncestorsInOrder() {
            // When
            List<ComponentId> ancestors = hierarchy.getAncestors(grandchildComponentId);
            
            // Then
            assertEquals(2, ancestors.size(), "Grandchild should have 2 ancestors");
            assertEquals(childComponentId1, ancestors.get(0), "First ancestor should be parent");
            assertEquals(rootComponentId, ancestors.get(1), "Second ancestor should be grandparent");
        }
        
        @Test
        @DisplayName("getAncestors() should return empty list for root component")
        void getAncestorsShouldReturnEmptyListForRootComponent() {
            // When
            List<ComponentId> ancestors = hierarchy.getAncestors(rootComponentId);
            
            // Then
            assertTrue(ancestors.isEmpty(), "Root component should have no ancestors");
        }
        
        @Test
        @DisplayName("getDescendants() should return all descendants")
        void getDescendantsShouldReturnAllDescendants() {
            // When
            List<ComponentId> descendants = hierarchy.getDescendants(rootComponentId);
            
            // Then
            assertEquals(3, descendants.size(), "Root should have 3 descendants");
            assertTrue(descendants.contains(childComponentId1), "Descendants should include child 1");
            assertTrue(descendants.contains(childComponentId2), "Descendants should include child 2");
            assertTrue(descendants.contains(grandchildComponentId), "Descendants should include grandchild");
        }
        
        @Test
        @DisplayName("getDescendants() should return empty list for leaf component")
        void getDescendantsShouldReturnEmptyListForLeafComponent() {
            // When
            List<ComponentId> descendants = hierarchy.getDescendants(grandchildComponentId);
            
            // Then
            assertTrue(descendants.isEmpty(), "Leaf component should have no descendants");
        }
        
        @Test
        @DisplayName("isAncestorOf() should return true for ancestor-descendant relationship")
        void isAncestorOfShouldReturnTrueForAncestorDescendantRelationship() {
            // Then
            assertTrue(hierarchy.isAncestorOf(rootComponentId, childComponentId1), 
                    "Root should be ancestor of child");
            assertTrue(hierarchy.isAncestorOf(rootComponentId, grandchildComponentId), 
                    "Root should be ancestor of grandchild");
            assertTrue(hierarchy.isAncestorOf(childComponentId1, grandchildComponentId), 
                    "Child should be ancestor of grandchild");
        }
        
        @Test
        @DisplayName("isAncestorOf() should return false for non-ancestor relationship")
        void isAncestorOfShouldReturnFalseForNonAncestorRelationship() {
            // Then
            assertFalse(hierarchy.isAncestorOf(childComponentId1, childComponentId2), 
                    "Siblings should not be ancestors of each other");
            assertFalse(hierarchy.isAncestorOf(grandchildComponentId, rootComponentId), 
                    "Descendant should not be ancestor of ancestor");
        }
    }
    
    /**
     * Simple implementation of ComponentHierarchy for testing purposes.
     * 
     * <p>In a real implementation, this would be more sophisticated with caching,
     * indexing, and optimized traversal algorithms.
     */
    private static class ComponentHierarchy {
        private List<ComponentId> components = new java.util.ArrayList<>();
        
        public void registerComponent(ComponentId component) {
            components.add(component);
        }
        
        public Optional<ComponentId> findComponentById(String id) {
            return components.stream()
                    .filter(c -> c.getIdString().equals(id))
                    .findFirst();
        }
        
        public ComponentId getComponentById(String id) {
            return findComponentById(id)
                    .orElseThrow(() -> new ComponentNotFoundException("Component not found: " + id));
        }
        
        public List<ComponentId> getChildComponents(ComponentId parent) {
            return components.stream()
                    .filter(c -> {
                        if (c.getParentId() == null) return false;
                        return c.getParentId().equals(parent.getValue());
                    })
                    .collect(java.util.stream.Collectors.toList());
        }
        
        public Optional<ComponentId> getParentComponent(ComponentId child) {
            if (child.getParentId() == null) {
                return Optional.empty();
            }
            
            return components.stream()
                    .filter(c -> c.getValue().equals(child.getParentId()))
                    .findFirst();
        }
        
        public List<ComponentId> getAncestors(ComponentId component) {
            List<ComponentId> ancestors = new java.util.ArrayList<>();
            Optional<ComponentId> parent = getParentComponent(component);
            
            while (parent.isPresent()) {
                ancestors.add(parent.get());
                parent = getParentComponent(parent.get());
            }
            
            return ancestors;
        }
        
        public List<ComponentId> getDescendants(ComponentId component) {
            List<ComponentId> descendants = new java.util.ArrayList<>();
            List<ComponentId> directChildren = getChildComponents(component);
            
            descendants.addAll(directChildren);
            
            for (ComponentId child : directChildren) {
                descendants.addAll(getDescendants(child));
            }
            
            return descendants;
        }
        
        public boolean isAncestorOf(ComponentId possibleAncestor, ComponentId possibleDescendant) {
            return getAncestors(possibleDescendant).contains(possibleAncestor);
        }
    }
}