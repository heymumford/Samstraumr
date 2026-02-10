/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

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

package org.s8r.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.exception.DuplicateComponentException;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;

class InMemoryComponentRepositoryTest {

  private InMemoryComponentRepository repository;
  private LoggerPort loggerMock;

  @BeforeEach
  void setUp() {
    // Create a mock for the LoggerPort
    loggerMock = mock(LoggerPort.class);

    // Initialize the repository with the mock
    repository = new InMemoryComponentRepository(loggerMock);
  }

  @Test
  @DisplayName("Save component should store the component successfully")
  void saveComponentStoresSuccessfully() {
    // Arrange
    ComponentPort mockComponent = createMockComponent("test-component");

    // Act
    repository.save(mockComponent);

    // Assert
    Optional<ComponentPort> retrieved = repository.findById(mockComponent.getId());
    assertTrue(retrieved.isPresent());
    assertEquals(mockComponent, retrieved.get());

    // Verify logging
    verify(loggerMock).debug(contains("Saved component"), eq(mockComponent.getId().getIdString()));
  }

  @Test
  @DisplayName("Save duplicate component should throw DuplicateComponentException")
  void saveDuplicateComponentThrowsException() {
    // Arrange
    ComponentId id = ComponentId.create("test-component");
    ComponentPort mockComponent1 = createMockComponent(id);
    ComponentPort mockComponent2 = createMockComponent(id); // Same ID as first component

    // Save the first component
    repository.save(mockComponent1);

    // Act & Assert
    assertThrows(
        DuplicateComponentException.class,
        () -> {
          repository.save(mockComponent2);
        });

    // Verify error was logged
    verify(loggerMock).error(contains("duplicate component"), eq(id.getIdString()));
  }

  @Test
  @DisplayName("Check exists returns true for existing component")
  void existsReturnsTrueForExistingComponent() {
    // Arrange
    ComponentPort mockComponent = createMockComponent("test-component");
    repository.save(mockComponent);

    // Act
    boolean exists = repository.exists(mockComponent.getId());

    // Assert
    assertTrue(exists);
  }

  @Test
  @DisplayName("Check exists returns false for non-existing component")
  void existsReturnsFalseForNonExistingComponent() {
    // Arrange
    ComponentId id = ComponentId.create("non-existent");

    // Act
    boolean exists = repository.exists(id);

    // Assert
    assertFalse(exists);
  }

  @Test
  @DisplayName("Find all returns all stored components")
  void findAllReturnsAllComponents() {
    // Arrange
    ComponentPort component1 = createMockComponent("component1");
    ComponentPort component2 = createMockComponent("component2");
    ComponentPort component3 = createMockComponent("component3");

    repository.save(component1);
    repository.save(component2);
    repository.save(component3);

    // Act
    List<ComponentPort> allComponents = repository.findAll();

    // Assert
    assertEquals(3, allComponents.size());
    assertTrue(allComponents.contains(component1));
    assertTrue(allComponents.contains(component2));
    assertTrue(allComponents.contains(component3));
  }

  @Test
  @DisplayName("Update existing component works correctly")
  void updateExistingComponentWorks() {
    // Arrange
    ComponentId id = ComponentId.create("test-component");
    ComponentPort originalComponent = createMockComponent(id);
    repository.save(originalComponent);

    // Create a new component with the same ID but different state
    ComponentPort updatedComponent = createMockComponent(id, LifecycleState.ACTIVE);

    // Act
    repository.update(updatedComponent);

    // Assert
    Optional<ComponentPort> retrieved = repository.findById(id);
    assertTrue(retrieved.isPresent());
    assertEquals(updatedComponent, retrieved.get());
    assertEquals(LifecycleState.ACTIVE, retrieved.get().getLifecycleState());

    // Verify logging
    verify(loggerMock).debug(contains("Updated component"), eq(id.getIdString()));
  }

  @Test
  @DisplayName("Update non-existent component throws ComponentNotFoundException")
  void updateNonExistentComponentThrows() {
    // Arrange
    ComponentId id = ComponentId.create("non-existent");
    ComponentPort component = createMockComponent(id);

    // Act & Assert
    assertThrows(
        org.s8r.domain.exception.ComponentNotFoundException.class,
        () -> {
          repository.update(component);
        });
  }

  @Test
  @DisplayName("Delete component removes it from repository")
  void deleteComponentRemovesIt() {
    // Arrange
    ComponentPort component = createMockComponent("test-component");
    repository.save(component);

    // Act
    repository.delete(component.getId());

    // Assert
    Optional<ComponentPort> retrieved = repository.findById(component.getId());
    assertFalse(retrieved.isPresent());
  }

  @Test
  @DisplayName("Clear repository removes all components")
  void clearRepositoryRemovesAllComponents() {
    // Arrange
    ComponentPort component1 = createMockComponent("component1");
    ComponentPort component2 = createMockComponent("component2");

    repository.save(component1);
    repository.save(component2);
    assertEquals(2, repository.size());

    // Act
    repository.clear();

    // Assert
    assertEquals(0, repository.size());
    assertTrue(repository.findAll().isEmpty());
  }

  @Test
  @DisplayName("Find by state returns components with matching state")
  void findByStateReturnsMatchingComponents() {
    // Arrange
    ComponentPort activeComponent = createMockComponent("active-component", LifecycleState.ACTIVE);
    ComponentPort readyComponent = createMockComponent("ready-component", LifecycleState.READY);
    ComponentPort anotherActiveComponent =
        createMockComponent("another-active", LifecycleState.ACTIVE);

    repository.save(activeComponent);
    repository.save(readyComponent);
    repository.save(anotherActiveComponent);

    // Act
    List<ComponentPort> activeComponents = repository.findByState(LifecycleState.ACTIVE);
    List<ComponentPort> readyComponents = repository.findByState(LifecycleState.READY);
    List<ComponentPort> terminatedComponents = repository.findByState(LifecycleState.TERMINATED);

    // Assert
    assertEquals(2, activeComponents.size());
    assertTrue(activeComponents.contains(activeComponent));
    assertTrue(activeComponents.contains(anotherActiveComponent));

    assertEquals(1, readyComponents.size());
    assertTrue(readyComponents.contains(readyComponent));

    assertTrue(terminatedComponents.isEmpty());
  }

  // Helper method to create a mock ComponentPort with a specific reason
  private ComponentPort createMockComponent(String reason) {
    ComponentId id = ComponentId.create(reason);
    return createMockComponent(id);
  }

  // Helper method to create a mock ComponentPort with a specific ID
  private ComponentPort createMockComponent(ComponentId id) {
    return createMockComponent(id, LifecycleState.READY);
  }

  // Helper method to create a mock ComponentPort with a specific ID and state
  private ComponentPort createMockComponent(String reason, LifecycleState state) {
    ComponentId id = ComponentId.create(reason);
    return createMockComponent(id, state);
  }

  // Helper method to create a mock ComponentPort with a specific ID and state
  private ComponentPort createMockComponent(ComponentId id, LifecycleState state) {
    ComponentPort mockComponent = mock(ComponentPort.class);

    // Configure the mock to return the provided ID
    when(mockComponent.getId()).thenReturn(id);

    // Configure other necessary methods
    when(mockComponent.getLifecycleState()).thenReturn(state);
    when(mockComponent.getLineage()).thenReturn(Collections.emptyList());
    when(mockComponent.getActivityLog()).thenReturn(Collections.emptyList());
    when(mockComponent.getCreationTime()).thenReturn(Instant.now());
    when(mockComponent.getDomainEvents()).thenReturn(Collections.emptyList());

    return mockComponent;
  }
}
