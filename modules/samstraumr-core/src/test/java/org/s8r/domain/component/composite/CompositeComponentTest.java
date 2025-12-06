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

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.s8r.domain.component.Component;
import org.s8r.domain.event.ComponentConnectionEvent;
import org.s8r.domain.event.DomainEvent;
import org.s8r.domain.exception.ComponentNotFoundException;
import org.s8r.domain.exception.DuplicateComponentException;
import org.s8r.domain.exception.InvalidOperationException;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.test.annotation.UnitTest;

/**
 * Tests for the CompositeComponent domain entity.
 *
 * <p>These tests validate the behavior of CompositeComponent, including component hierarchy
 * management, connections, and lifecycle propagation.
 */
@UnitTest
@DisplayName("CompositeComponent Domain Entity Tests")
class CompositeComponentTest {

  private CompositeComponent composite;
  private ComponentId compositeId;
  private Component child1;
  private Component child2;
  private ComponentId child1Id;
  private ComponentId child2Id;

  @BeforeEach
  void setUp() {
    compositeId = ComponentId.create("Test Composite");
    composite = CompositeComponent.create(compositeId, CompositeType.STANDARD);

    child1Id = ComponentId.create("Child Component 1");
    child1 = Component.create(child1Id);

    child2Id = ComponentId.create("Child Component 2");
    child2 = Component.create(child2Id);

    // Clear events that may have been generated during creation
    composite.clearEvents();

    // Advance components to READY state for lifecycle tests
    if (composite.getLifecycleState() == LifecycleState.CONCEPTION) {
      composite.transitionTo(LifecycleState.INITIALIZING);
      composite.transitionTo(LifecycleState.CONFIGURING);
      composite.transitionTo(LifecycleState.SPECIALIZING);
      composite.transitionTo(LifecycleState.DEVELOPING_FEATURES);
      composite.transitionTo(LifecycleState.READY);
    }

    if (child1.getLifecycleState() == LifecycleState.CONCEPTION) {
      child1.transitionTo(LifecycleState.INITIALIZING);
      child1.transitionTo(LifecycleState.CONFIGURING);
      child1.transitionTo(LifecycleState.SPECIALIZING);
      child1.transitionTo(LifecycleState.DEVELOPING_FEATURES);
      child1.transitionTo(LifecycleState.READY);
    }

    if (child2.getLifecycleState() == LifecycleState.CONCEPTION) {
      child2.transitionTo(LifecycleState.INITIALIZING);
      child2.transitionTo(LifecycleState.CONFIGURING);
      child2.transitionTo(LifecycleState.SPECIALIZING);
      child2.transitionTo(LifecycleState.DEVELOPING_FEATURES);
      child2.transitionTo(LifecycleState.READY);
    }
  }

  @Nested
  @DisplayName("Component Management Tests")
  class ComponentManagementTests {

    @Test
    @DisplayName("addComponent() should add a child component correctly")
    void addComponentShouldAddChildComponent() {
      // When
      composite.addComponent(child1);

      // Then
      assertTrue(composite.containsComponent(child1Id), "Composite should contain the added child");
      assertEquals(1, composite.getChildrenCount(), "Child count should be 1");

      Optional<Component> retrievedChild = composite.getComponent(child1Id);
      assertTrue(retrievedChild.isPresent(), "Should find child by ID");
      assertEquals(child1, retrievedChild.get(), "Retrieved child should match added child");

      // Verify the COMPOSITION connection was created
      List<ComponentConnection> connections =
          composite.getConnectionsByType(ConnectionType.COMPOSITION);
      assertEquals(1, connections.size(), "Should have one COMPOSITION connection");
      assertEquals(
          compositeId, connections.get(0).getSourceId(), "Connection source should be composite");
      assertEquals(child1Id, connections.get(0).getTargetId(), "Connection target should be child");
    }

    @Test
    @DisplayName("addComponent() should throw DuplicateComponentException for duplicate component")
    void addComponentShouldThrowExceptionForDuplicateComponent() {
      // Given
      composite.addComponent(child1);

      // Then
      assertThrows(
          DuplicateComponentException.class,
          () -> composite.addComponent(child1),
          "Should throw DuplicateComponentException when adding same component again");
    }

    @Test
    @DisplayName("getComponents() should return all child components")
    void getComponentsShouldReturnAllChildren() {
      // Given
      composite.addComponent(child1);
      composite.addComponent(child2);

      // When
      List<Component> children = composite.getComponents();

      // Then
      assertEquals(2, children.size(), "Should return 2 children");
      assertTrue(children.contains(child1), "Should contain first child");
      assertTrue(children.contains(child2), "Should contain second child");
    }

    @Test
    @DisplayName("removeComponent() should remove a component and its connections")
    void removeComponentShouldRemoveComponentAndConnections() {
      // Given
      composite.addComponent(child1);
      composite.addComponent(child2);
      composite.connect(child1Id, child2Id, ConnectionType.DATA_FLOW, "Test connection");

      // When
      composite.removeComponent(child1Id);

      // Then
      assertFalse(composite.containsComponent(child1Id), "Should not contain removed component");
      assertEquals(1, composite.getChildrenCount(), "Should have 1 child remaining");

      // The DATA_FLOW connection should also be removed
      List<ComponentConnection> dataFlowConnections =
          composite.getConnectionsByType(ConnectionType.DATA_FLOW);
      assertEquals(0, dataFlowConnections.size(), "DATA_FLOW connection should be removed");

      // One COMPOSITION connection should remain (to child2)
      List<ComponentConnection> compositionConnections =
          composite.getConnectionsByType(ConnectionType.COMPOSITION);
      assertEquals(
          1, compositionConnections.size(), "Should have 1 COMPOSITION connection remaining");
      assertEquals(
          child2Id,
          compositionConnections.get(0).getTargetId(),
          "Remaining connection should point to child2");
    }

    @Test
    @DisplayName(
        "removeComponent() should throw ComponentNotFoundException for non-existent component")
    void removeComponentShouldThrowExceptionForNonExistentComponent() {
      // Then
      assertThrows(
          ComponentNotFoundException.class,
          () -> composite.removeComponent(ComponentId.create("NonExistent")),
          "Should throw ComponentNotFoundException for non-existent component");
    }
  }

  @Nested
  @DisplayName("Connection Management Tests")
  class ConnectionManagementTests {

    @Test
    @DisplayName("connect() should create a connection between components")
    void connectShouldCreateConnection() {
      // Given
      composite.addComponent(child1);
      composite.addComponent(child2);

      // When
      ComponentConnection connection =
          composite.connect(child1Id, child2Id, ConnectionType.DATA_FLOW, "Test data flow");

      // Then
      assertNotNull(connection, "Connection should be created");
      assertEquals(ConnectionType.DATA_FLOW, connection.getType(), "Connection type should match");
      assertEquals(child1Id, connection.getSourceId(), "Source should match");
      assertEquals(child2Id, connection.getTargetId(), "Target should match");
      assertEquals("Test data flow", connection.getDescription(), "Description should match");
      assertTrue(connection.isActive(), "Connection should be active by default");

      // Verify connections are retrievable
      List<ComponentConnection> connections = composite.getConnections();
      assertEquals(
          3, connections.size(), "Should have 3 connections (2 COMPOSITION + 1 DATA_FLOW)");

      List<ComponentConnection> dataFlowConnections =
          composite.getConnectionsByType(ConnectionType.DATA_FLOW);
      assertEquals(1, dataFlowConnections.size(), "Should have 1 DATA_FLOW connection");

      // Event should be raised
      List<DomainEvent> events = composite.getDomainEvents();
      assertTrue(
          events.stream().anyMatch(e -> e instanceof ComponentConnectionEvent),
          "Should raise ComponentConnectionEvent");
    }

    @Test
    @DisplayName("connect() should throw ComponentNotFoundException for non-existent source")
    void connectShouldThrowExceptionForNonExistentSource() {
      // Given
      ComponentId nonExistentId = ComponentId.create("NonExistent");
      composite.addComponent(child2);

      // Then
      assertThrows(
          ComponentNotFoundException.class,
          () -> composite.connect(nonExistentId, child2Id, ConnectionType.CONTROL, "Test"),
          "Should throw ComponentNotFoundException for non-existent source");
    }

    @Test
    @DisplayName("connect() should throw ComponentNotFoundException for non-existent target")
    void connectShouldThrowExceptionForNonExistentTarget() {
      // Given
      ComponentId nonExistentId = ComponentId.create("NonExistent");
      composite.addComponent(child1);

      // Then
      assertThrows(
          ComponentNotFoundException.class,
          () -> composite.connect(child1Id, nonExistentId, ConnectionType.CONTROL, "Test"),
          "Should throw ComponentNotFoundException for non-existent target");
    }

    @Test
    @DisplayName("disconnect() should remove a connection")
    void disconnectShouldRemoveConnection() {
      // Given
      composite.addComponent(child1);
      composite.addComponent(child2);
      ComponentConnection connection =
          composite.connect(child1Id, child2Id, ConnectionType.EVENT, "Test event");

      // When
      composite.disconnect(connection.getConnectionId());

      // Then
      List<ComponentConnection> eventConnections =
          composite.getConnectionsByType(ConnectionType.EVENT);
      assertEquals(0, eventConnections.size(), "Should have no EVENT connections after disconnect");

      // COMPOSITION connections should remain
      List<ComponentConnection> compositionConnections =
          composite.getConnectionsByType(ConnectionType.COMPOSITION);
      assertEquals(2, compositionConnections.size(), "Should still have 2 COMPOSITION connections");
    }

    @Test
    @DisplayName("getConnectionsForComponent() should return all connections for a component")
    void getConnectionsForComponentShouldReturnAllConnectionsForComponent() {
      // Given
      composite.addComponent(child1);
      composite.addComponent(child2);
      composite.connect(child1Id, child2Id, ConnectionType.DATA_FLOW, "Data connection");
      composite.connect(child2Id, child1Id, ConnectionType.EVENT, "Event connection");

      // When
      List<ComponentConnection> child1Connections = composite.getConnectionsForComponent(child1Id);

      // Then
      assertEquals(
          3,
          child1Connections.size(),
          "Should have 3 connections for child1 (1 COMPOSITION + 2 others)");
    }
  }

  @Nested
  @DisplayName("Lifecycle Management Tests")
  class LifecycleManagementTests {

    @Test
    @DisplayName("activate() should activate the composite and its eligible children")
    void activateShouldActivateCompositeAndChildren() {
      // Given
      composite.addComponent(child1);
      composite.addComponent(child2);

      // When
      composite.activate();

      // Then
      assertEquals(
          LifecycleState.ACTIVE, composite.getLifecycleState(), "Composite should be active");
      assertEquals(LifecycleState.ACTIVE, child1.getLifecycleState(), "Child1 should be active");
      assertEquals(LifecycleState.ACTIVE, child2.getLifecycleState(), "Child2 should be active");

      // All connections should be active
      List<ComponentConnection> connections = composite.getConnections();
      for (ComponentConnection connection : connections) {
        assertTrue(connection.isActive(), "Connection should be active");
      }
    }

    @Test
    @DisplayName("deactivate() should deactivate the composite and its active children")
    void deactivateShouldDeactivateCompositeAndChildren() {
      // Given
      composite.addComponent(child1);
      composite.addComponent(child2);
      composite.activate();

      // When
      composite.deactivate();

      // Then
      assertEquals(
          LifecycleState.READY, composite.getLifecycleState(), "Composite should be ready");
      assertEquals(LifecycleState.READY, child1.getLifecycleState(), "Child1 should be ready");
      assertEquals(LifecycleState.READY, child2.getLifecycleState(), "Child2 should be ready");

      // All connections should be inactive
      List<ComponentConnection> connections = composite.getConnections();
      for (ComponentConnection connection : connections) {
        assertFalse(connection.isActive(), "Connection should be inactive");
      }
    }

    @Test
    @DisplayName("terminate() should terminate the composite and its children")
    void terminateShouldTerminateCompositeAndChildren() {
      // Given
      composite.addComponent(child1);
      composite.addComponent(child2);

      // When
      composite.terminate();

      // Then
      assertEquals(
          LifecycleState.TERMINATED,
          composite.getLifecycleState(),
          "Composite should be terminated");
      assertEquals(
          LifecycleState.TERMINATED, child1.getLifecycleState(), "Child1 should be terminated");
      assertEquals(
          LifecycleState.TERMINATED, child2.getLifecycleState(), "Child2 should be terminated");
    }
  }

  @Nested
  @DisplayName("Edge Case Tests")
  class EdgeCaseTests {

    @Test
    @DisplayName("adding components to active composite should throw InvalidOperationException")
    void addingComponentsToActiveCompositeShouldThrowException() {
      // Given
      composite.activate();

      // Then
      assertThrows(
          InvalidOperationException.class,
          () -> composite.addComponent(child1),
          "Should throw InvalidOperationException when adding component to active composite");
    }

    @Test
    @DisplayName("connecting components in active composite should throw InvalidOperationException")
    void connectingComponentsInActiveCompositeShouldThrowException() {
      // Given
      composite.addComponent(child1);
      composite.addComponent(child2);
      composite.activate();

      // Then
      assertThrows(
          InvalidOperationException.class,
          () -> composite.connect(child1Id, child2Id, ConnectionType.DATA_FLOW, "Test"),
          "Should throw InvalidOperationException when connecting components in active composite");
    }

    @Test
    @DisplayName("removing components from active composite should throw InvalidOperationException")
    void removingComponentsFromActiveCompositeShouldThrowException() {
      // Given
      composite.addComponent(child1);
      composite.activate();

      // Then
      assertThrows(
          InvalidOperationException.class,
          () -> composite.removeComponent(child1Id),
          "Should throw InvalidOperationException when removing component from active composite");
    }

    @Test
    @DisplayName("disconnecting in active composite should throw InvalidOperationException")
    void disconnectingInActiveCompositeShouldThrowException() {
      // Given
      composite.addComponent(child1);
      composite.addComponent(child2);
      ComponentConnection connection =
          composite.connect(child1Id, child2Id, ConnectionType.DATA_FLOW, "Test");
      composite.activate();

      // Then
      assertThrows(
          InvalidOperationException.class,
          () -> composite.disconnect(connection.getConnectionId()),
          "Should throw InvalidOperationException when disconnecting in active composite");
    }
  }

  @Nested
  @DisplayName("Utility Method Tests")
  class UtilityMethodTests {

    @Test
    @DisplayName("getChildrenCount() should return correct number of children")
    void getChildrenCountShouldReturnCorrectCount() {
      // Given
      assertEquals(0, composite.getChildrenCount(), "Initial child count should be 0");

      // When
      composite.addComponent(child1);

      // Then
      assertEquals(
          1, composite.getChildrenCount(), "Child count should be 1 after adding one child");

      // When
      composite.addComponent(child2);

      // Then
      assertEquals(
          2, composite.getChildrenCount(), "Child count should be 2 after adding second child");

      // When
      composite.removeComponent(child1Id);

      // Then
      assertEquals(1, composite.getChildrenCount(), "Child count should be 1 after removing child");
    }

    @Test
    @DisplayName("getConnectionCount() should return correct number of connections")
    void getConnectionCountShouldReturnCorrectCount() {
      // Given - empty composite
      assertEquals(0, composite.getConnectionCount(), "Initial connection count should be 0");

      // When - adding children creates composition connections
      composite.addComponent(child1);
      composite.addComponent(child2);

      // Then
      assertEquals(2, composite.getConnectionCount(), "Should have 2 COMPOSITION connections");

      // When - adding explicit connections
      composite.connect(child1Id, child2Id, ConnectionType.DATA_FLOW, "Data flow");

      // Then
      assertEquals(3, composite.getConnectionCount(), "Should have 3 connections total");
    }
  }
}
