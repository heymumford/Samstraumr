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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.function.Function;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.s8r.domain.component.Component;
import org.s8r.domain.component.ComponentType;
import org.s8r.domain.component.composite.CompositeComponent;
import org.s8r.domain.exception.InvalidCompositeTypeException;
import org.s8r.domain.exception.NonExistentComponentReferenceException;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.machine.Machine;
import org.s8r.domain.machine.MachineType;

@DisplayName("Machine Component Validator Tests")
class MachineComponentValidatorTest {

    @Test
    @DisplayName("validateComponentExists should throw exception for non-existent component")
    void validateComponentExistsThrowsExceptionForNonExistentComponent() {
        // Given
        ComponentId machineId = ComponentId.createNew();
        ComponentId componentId = ComponentId.createNew();
        Function<ComponentId, Boolean> existsFunction = id -> false;
        
        // When / Then
        NonExistentComponentReferenceException exception = assertThrows(
                NonExistentComponentReferenceException.class,
                () -> MachineComponentValidator.validateComponentExists(machineId, componentId, existsFunction));
        
        assertEquals("addComponent", exception.getOperationType());
        assertEquals(machineId, exception.getReferringComponentId());
        assertEquals(componentId, exception.getReferencedComponentId());
    }
    
    @Test
    @DisplayName("validateComponentExists should not throw exception for existing component")
    void validateComponentExistsDoesNotThrowForExistingComponent() {
        // Given
        ComponentId machineId = ComponentId.createNew();
        ComponentId componentId = ComponentId.createNew();
        Function<ComponentId, Boolean> existsFunction = id -> true;
        
        // When / Then
        assertDoesNotThrow(
                () -> MachineComponentValidator.validateComponentExists(machineId, componentId, existsFunction));
    }
    
    @Test
    @DisplayName("validateIsCompositeComponent should throw exception for non-composite component")
    void validateIsCompositeComponentThrowsExceptionForNonCompositeComponent() {
        // Given
        ComponentId machineId = ComponentId.createNew();
        Component component = mock(Component.class);
        
        // When / Then
        InvalidCompositeTypeException exception = assertThrows(
                InvalidCompositeTypeException.class,
                () -> MachineComponentValidator.validateIsCompositeComponent(machineId, component));
        
        assertEquals(machineId, exception.getMachineId());
        assertEquals(component.getClass().getName(), exception.getActualType());
    }
    
    @Test
    @DisplayName("validateIsCompositeComponent should not throw exception for composite component")
    void validateIsCompositeComponentDoesNotThrowForCompositeComponent() {
        // Given
        ComponentId machineId = ComponentId.createNew();
        CompositeComponent component = mock(CompositeComponent.class);
        
        // When / Then
        assertDoesNotThrow(
                () -> MachineComponentValidator.validateIsCompositeComponent(machineId, component));
    }
    
    @Test
    @DisplayName("validateIsCompositeComponent should throw exception for null component")
    void validateIsCompositeComponentThrowsExceptionForNullComponent() {
        // Given
        ComponentId machineId = ComponentId.createNew();
        
        // When / Then
        assertThrows(
                IllegalArgumentException.class,
                () -> MachineComponentValidator.validateIsCompositeComponent(machineId, null));
    }
    
    @Test
    @DisplayName("validateMachineComponent should not throw exception for valid component")
    void validateMachineComponentDoesNotThrowForValidComponent() {
        // Given
        Machine machine = Machine.create(
                ComponentId.createNew(), 
                MachineType.PROCESSING, 
                "Test Machine", 
                "Test Description", 
                "1.0.0");
        
        CompositeComponent component = mock(CompositeComponent.class);
        when(component.getId()).thenReturn(ComponentId.createNew());
        
        // When / Then
        assertDoesNotThrow(() -> MachineComponentValidator.validateMachineComponent(machine, component));
    }
    
    @Test
    @DisplayName("validateMachineComponent should throw exception for null component")
    void validateMachineComponentThrowsExceptionForNullComponent() {
        // Given
        Machine machine = Machine.create(
                ComponentId.createNew(), 
                MachineType.PROCESSING, 
                "Test Machine", 
                "Test Description", 
                "1.0.0");
        
        // When / Then
        assertThrows(
                IllegalArgumentException.class,
                () -> MachineComponentValidator.validateMachineComponent(machine, null));
    }
    
    @Test
    @DisplayName("validateMachineComponents should validate multiple components")
    void validateMachineComponentsValidatesMultipleComponents() {
        // Given
        Machine machine = Machine.create(
                ComponentId.createNew(), 
                MachineType.PROCESSING, 
                "Test Machine", 
                "Test Description", 
                "1.0.0");
        
        CompositeComponent component1 = mock(CompositeComponent.class);
        when(component1.getId()).thenReturn(ComponentId.createNew());
        
        CompositeComponent component2 = mock(CompositeComponent.class);
        when(component2.getId()).thenReturn(ComponentId.createNew());
        
        // When / Then
        assertDoesNotThrow(
                () -> MachineComponentValidator.validateMachineComponents(
                        machine, component1, component2));
    }
    
    @Test
    @DisplayName("validateComponentReference should validate component existence and type")
    void validateComponentReferenceValidatesExistenceAndType() {
        // Given
        Machine machine = Machine.create(
                ComponentId.createNew(), 
                MachineType.PROCESSING, 
                "Test Machine", 
                "Test Description", 
                "1.0.0");
        
        ComponentId componentId = ComponentId.createNew();
        CompositeComponent component = mock(CompositeComponent.class);
        
        Function<ComponentId, Boolean> existsFunction = id -> true;
        Function<ComponentId, Object> retrieveFunction = id -> component;
        
        // When / Then
        assertDoesNotThrow(
                () -> MachineComponentValidator.validateComponentReference(
                        machine, componentId, existsFunction, retrieveFunction));
    }
    
    @Test
    @DisplayName("validateComponentReference should throw exception for non-existent component")
    void validateComponentReferenceThrowsExceptionForNonExistentComponent() {
        // Given
        Machine machine = Machine.create(
                ComponentId.createNew(), 
                MachineType.PROCESSING, 
                "Test Machine", 
                "Test Description", 
                "1.0.0");
        
        ComponentId componentId = ComponentId.createNew();
        
        Function<ComponentId, Boolean> existsFunction = id -> false;
        Function<ComponentId, Object> retrieveFunction = id -> null;
        
        // When / Then
        assertThrows(
                NonExistentComponentReferenceException.class,
                () -> MachineComponentValidator.validateComponentReference(
                        machine, componentId, existsFunction, retrieveFunction));
    }
    
    @Test
    @DisplayName("validateComponentReference should throw exception for non-composite component")
    void validateComponentReferenceThrowsExceptionForNonCompositeComponent() {
        // Given
        Machine machine = Machine.create(
                ComponentId.createNew(), 
                MachineType.PROCESSING, 
                "Test Machine", 
                "Test Description", 
                "1.0.0");
        
        ComponentId componentId = ComponentId.createNew();
        Component component = mock(Component.class);
        when(component.getType()).thenReturn(ComponentType.ATOMIC);
        
        Function<ComponentId, Boolean> existsFunction = id -> true;
        Function<ComponentId, Object> retrieveFunction = id -> component;
        
        // When / Then
        assertThrows(
                InvalidCompositeTypeException.class,
                () -> MachineComponentValidator.validateComponentReference(
                        machine, componentId, existsFunction, retrieveFunction));
    }
}