/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

package org.s8r.architecture;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.s8r.domain.identity.ComponentId;
import org.s8r.test.annotation.UnitTest;

/**
 * Tests for the Hierarchical Identity System as described in ADR-0008.
 *
 * <p>This test suite validates the implementation of the hierarchical identity system for
 * components within the Samstraumr framework.
 */
@UnitTest
@Tag("architecture")
@Tag("adr-validation")
@DisplayName("Hierarchical Identity System Tests (ADR-0008)")
public class HierarchicalIdentitySystemTest {

  private ComponentId rootId;
  private ComponentId childId1;
  private ComponentId childId2;
  private List<ComponentId> componentIds;

  @BeforeEach
  void setUp() {
    // Create component identifiers using the factory method
    rootId = ComponentId.create("root");
    childId1 = ComponentId.create("child1");
    childId2 = ComponentId.create("child2");

    // Store components for testing
    componentIds = new ArrayList<>();
    componentIds.add(rootId);
    componentIds.add(childId1);
    componentIds.add(childId2);
  }

  @Nested
  @DisplayName("Identity Structure Tests")
  class IdentityStructureTests {

    @Test
    @DisplayName("Component ID should contain UUID and reason")
    void componentIdShouldContainUuidAndReason() {
      ComponentId id = ComponentId.create("test-component");

      assertNotNull(id.getValue(), "UUID should not be null");
      assertNotNull(id.getReason(), "Reason should not be null");
      assertEquals("test-component", id.getReason(), "Reason should match");
      assertTrue(id.getIdString().length() > 30, "ID should be a valid UUID string");
    }

    @Test
    @DisplayName("Component IDs with same reason should be distinct")
    void componentIdsWithSameReasonShouldBeDistinct() {
      ComponentId id1 = ComponentId.create("same-reason");
      ComponentId id2 = ComponentId.create("same-reason");

      assertNotEquals(id1, id2, "IDs with same reason should be distinct due to UUIDs");
      assertNotEquals(id1.getValue(), id2.getValue(), "UUIDs should be different");
      assertEquals("same-reason", id1.getReason(), "Reason should match input");
      assertEquals("same-reason", id2.getReason(), "Reason should match input");
    }

    @Test
    @DisplayName("Component IDs should have creation time")
    void componentIdsShouldHaveCreationTime() {
      ComponentId id = ComponentId.create("test-component");
      assertNotNull(id.getCreationTime(), "Creation time should not be null");
      assertFalse(
          id.getCreationTime().isAfter(Instant.now()), "Creation time should be in the past");
      assertTrue(
          id.getCreationTime().isBefore(Instant.now().plusSeconds(1)),
          "Creation time should be recent");
    }

    @Test
    @DisplayName("Components can be created from existing UUIDs")
    void componentsCanBeCreatedFromUuids() {
      String uuidStr = UUID.randomUUID().toString();
      ComponentId id = ComponentId.fromString(uuidStr, "test-reason");

      assertEquals(uuidStr, id.getIdString(), "UUID string should match input");
      assertEquals("test-reason", id.getReason(), "Reason should match input");
    }
  }

  @Nested
  @DisplayName("Component Hierarchy Tests")
  class ComponentHierarchyTests {

    @Test
    @DisplayName("Components can have hierarchical addressing")
    void componentsCanHaveHierarchicalAddressing() {
      // Create root hierarchy
      org.s8r.domain.identity.ComponentHierarchy rootHierarchy =
          org.s8r.domain.identity.ComponentHierarchy.createRoot(rootId);

      assertNotNull(rootHierarchy, "Root hierarchy should be created");
      assertTrue(rootHierarchy.isRoot(), "Should be identified as root");
      assertEquals(rootId, rootHierarchy.getId(), "Component ID should match");
      assertNull(rootHierarchy.getParentId(), "Root should have no parent");
      assertTrue(rootHierarchy.getChildrenIds().isEmpty(), "New hierarchy should have no children");

      // Create and add child hierarchy
      org.s8r.domain.identity.ComponentHierarchy childHierarchy =
          org.s8r.domain.identity.ComponentHierarchy.createChild(childId1, rootHierarchy);

      assertFalse(childHierarchy.isRoot(), "Child hierarchy should not be root");
      assertEquals(rootId, childHierarchy.getParentId(), "Parent ID should match root ID");
      assertTrue(
          childHierarchy.getHierarchicalAddress().contains(rootId.getShortId()),
          "Child address should contain root ID");

      // Add child to root and verify
      rootHierarchy.addChild(childId1);
      assertEquals(1, rootHierarchy.getChildrenIds().size(), "Root should have one child");
      assertTrue(
          rootHierarchy.getChildrenIds().contains(childId1), "Root children should contain child1");
    }

    @Test
    @DisplayName("Hierarchy should maintain parent-child relationships")
    void hierarchyShouldMaintainParentChildRelationships() {
      // Create root hierarchy
      org.s8r.domain.identity.ComponentHierarchy rootHierarchy =
          org.s8r.domain.identity.ComponentHierarchy.createRoot(rootId);

      // Add children
      rootHierarchy.addChild(childId1);
      rootHierarchy.addChild(childId2);

      assertEquals(2, rootHierarchy.getChildrenIds().size(), "Root should have two children");
      assertTrue(
          rootHierarchy.getChildrenIds().contains(childId1), "Root children should contain child1");
      assertTrue(
          rootHierarchy.getChildrenIds().contains(childId2), "Root children should contain child2");

      // Create child hierarchies
      org.s8r.domain.identity.ComponentHierarchy child1Hierarchy =
          org.s8r.domain.identity.ComponentHierarchy.createChild(childId1, rootHierarchy);
      org.s8r.domain.identity.ComponentHierarchy child2Hierarchy =
          org.s8r.domain.identity.ComponentHierarchy.createChild(childId2, rootHierarchy);

      // Verify hierarchical addresses
      assertTrue(
          child1Hierarchy
              .getHierarchicalAddress()
              .startsWith(rootHierarchy.getHierarchicalAddress()),
          "Child address should start with parent address");
      assertTrue(
          child2Hierarchy
              .getHierarchicalAddress()
              .startsWith(rootHierarchy.getHierarchicalAddress()),
          "Child address should start with parent address");
      assertNotEquals(
          child1Hierarchy.getHierarchicalAddress(),
          child2Hierarchy.getHierarchicalAddress(),
          "Child addresses should be distinct");
    }
  }

  @Nested
  @DisplayName("Component Identity Tests")
  class ComponentIdentityTests {

    @Test
    @DisplayName("Component identities should be unique")
    void componentIdentitiesShouldBeUnique() {
      // Create multiple components with the same reason
      ComponentId id1 = ComponentId.create("component");
      ComponentId id2 = ComponentId.create("component");
      ComponentId id3 = ComponentId.create("component");

      // Verify they are all unique
      assertNotEquals(id1, id2, "Components should have unique identities");
      assertNotEquals(id2, id3, "Components should have unique identities");
      assertNotEquals(id1, id3, "Components should have unique identities");

      // Verify they all have the same reason
      assertEquals(id1.getReason(), id2.getReason(), "Reasons should be the same");
      assertEquals(id2.getReason(), id3.getReason(), "Reasons should be the same");
    }

    @Test
    @DisplayName("Component IDs have short representations")
    void componentIdsHaveShortRepresentations() {
      ComponentId id = ComponentId.create("test-component");

      assertNotNull(id.getShortId(), "Short ID should not be null");
      assertTrue(
          id.getShortId().length() < id.getIdString().length(),
          "Short ID should be shorter than full ID");
      assertEquals(8, id.getShortId().length(), "Short ID should be 8 characters");
    }

    @Test
    @DisplayName("Components have address representations")
    void componentsHaveAddressRepresentations() {
      ComponentId id = ComponentId.create("test-component");

      String address = id.toAddress();
      assertNotNull(address, "Component address should not be null");
      assertTrue(address.contains(id.getShortId()), "Address should contain component short ID");
      assertTrue(address.startsWith("CO<"), "Address should start with component indicator");
    }
  }
}
