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

package org.s8r.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.s8r.application.port.ComponentRepository;
import org.s8r.application.port.EventPublisherPort;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.exception.DuplicateComponentException;
import org.s8r.domain.identity.ComponentId;
import org.s8r.infrastructure.persistence.InMemoryComponentRepository;

/**
 * Tests for the duplicate component detection in the ComponentService.
 * 
 * <p>This test suite focuses on verifying that the ComponentService correctly detects
 * and rejects attempts to create duplicate components.
 */
class ComponentDuplicateDetectionTest {

    private LoggerPort logger;
    private EventPublisherPort eventPublisher;
    
    private InMemoryComponentRepository componentRepository;
    private ComponentService componentService;

    @BeforeEach
    void setUp() {
        // Create mocks
        logger = mock(LoggerPort.class);
        eventPublisher = mock(EventPublisherPort.class);
        
        // Use a real InMemoryComponentRepository to test the duplicate detection
        componentRepository = new InMemoryComponentRepository(logger);
        
        // Create the service with the repository and mocked dependencies
        componentService = new ComponentService(componentRepository, logger, eventPublisher);
    }

    @Test
    @DisplayName("Creating a component with same ID should throw DuplicateComponentException")
    void createDuplicateComponentThrowsException() {
        // Arrange
        // First, create a component that will be saved
        ComponentId id = ComponentId.create("test-component");
        
        // Force the next component creation to generate the same ID
        // We'll use reflection to access the private static method generateUniqueId
        // In a real-world scenario, we'd use a more sophisticated approach or dependency injection
        
        // For this test, we'll mock ComponentId.create to return the same ID
        try (var mockedStatic = mockStatic(ComponentId.class)) {
            // Configure ComponentId.create to return our predefined ID
            mockedStatic.when(() -> ComponentId.create(anyString())).thenReturn(id);
            
            // Act & Assert - First creation should succeed
            ComponentId firstId = componentService.createComponent("First component");
            assertEquals(id, firstId);
            
            // Second creation with same ID should fail
            DuplicateComponentException exception = assertThrows(
                DuplicateComponentException.class,
                () -> componentService.createComponent("Second component with same ID")
            );
            
            // Verify the exception contains the correct ID
            assertEquals(id, exception.getComponentId());
            
            // Verify error was logged
            verify(logger).error(contains("Component with ID {} already exists"), eq(id.getIdString()));
        }
    }
    
    @Test
    @DisplayName("Creating a child component with same ID should throw DuplicateComponentException")
    void createDuplicateChildComponentThrowsException() {
        // Arrange
        // First, create a parent component
        ComponentId parentId = componentService.createComponent("Parent component");
        
        // Create a predefined ID for the child that will be duplicated
        ComponentId childId = ComponentId.create("child-component");
        
        // Use static mocking for ComponentId
        try (var mockedStatic = mockStatic(ComponentId.class)) {
            // Return our original parentId when asked for parent
            mockedStatic.when(() -> ComponentId.create(eq("Parent component"))).thenReturn(parentId);
            
            // Configure ComponentId.create to return our predefined child ID for any call not matching "Parent component"
            mockedStatic.when(() -> ComponentId.create(argThat(s -> !s.equals("Parent component")))).thenReturn(childId);
            
            // Act & Assert - First child creation should succeed
            ComponentId firstChildId = componentService.createChildComponent("First child", parentId);
            assertEquals(childId, firstChildId);
            
            // Second child creation with same ID should fail
            DuplicateComponentException exception = assertThrows(
                DuplicateComponentException.class,
                () -> componentService.createChildComponent("Second child with same ID", parentId)
            );
            
            // Verify the exception contains the correct ID
            assertEquals(childId, exception.getComponentId());
            
            // Verify error was logged
            verify(logger).error(contains("Component with ID {} already exists"), eq(childId.getIdString()));
        }
    }
    
    @Test
    @DisplayName("Repository save method should throw exception when adding duplicate component")
    void repositorySaveMethodThrowsExceptionForDuplicates() {
        // Arrange
        ComponentId id = ComponentId.create("test-component");
        
        // First, save a component with the mocked repo
        ComponentPort mockComponent = mock(ComponentPort.class);
        when(mockComponent.getId()).thenReturn(id);
        
        componentRepository.save(mockComponent);
        
        // Act & Assert
        // Creating another component with the same ID should throw exception
        ComponentPort duplicateComponent = mock(ComponentPort.class);
        when(duplicateComponent.getId()).thenReturn(id);
        
        DuplicateComponentException exception = assertThrows(
            DuplicateComponentException.class,
            () -> componentRepository.save(duplicateComponent)
        );
        
        // Verify the exception contains the correct ID
        assertEquals(id, exception.getComponentId());
    }
}