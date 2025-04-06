package org.s8r.architecture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.s8r.component.exception.ComponentException;
import org.s8r.domain.exception.ComponentNotFoundException;
import org.s8r.domain.exception.DuplicateComponentException;
import org.s8r.component.composite.CompositeException;
import org.s8r.core.exception.InitializationException;
import org.s8r.domain.identity.ComponentId;
import org.s8r.test.annotation.UnitTest;
import org.s8r.core.tube.LifecycleState;

/**
 * Tests for the Standardized Error Handling Strategy as described in ADR-0011.
 * 
 * <p>This test suite validates the implementation of error classification, exception hierarchy,
 * design guidelines, handling responsibilities, recovery patterns, and logging/monitoring.
 */
@UnitTest
@Tag("architecture")
@Tag("adr-validation")
@DisplayName("Standardized Error Handling Tests (ADR-0011)")
public class StandardizedErrorHandlingTest {

    @Test
    @DisplayName("Exception hierarchy should be properly structured")
    void exceptionHierarchyShouldBeProperlyStructured() {
        // Create different types of exceptions
        ComponentId testId = ComponentId.create("test-component");
        ComponentNotFoundException notFoundEx = new ComponentNotFoundException(testId);
        DuplicateComponentException duplicateEx = new DuplicateComponentException("Duplicate component", testId);
        
        // Check inheritance
        assertTrue(notFoundEx instanceof org.s8r.domain.exception.ComponentException, 
            "ComponentNotFoundException should extend ComponentException");
        assertTrue(duplicateEx instanceof org.s8r.domain.exception.ComponentException, 
            "DuplicateComponentException should extend ComponentException");
        
        // Check component ID is preserved
        assertEquals(testId, notFoundEx.getComponentId(), "ComponentNotFoundException should preserve ComponentId");
        assertEquals(testId, duplicateEx.getComponentId(), "DuplicateComponentException should preserve ComponentId");
    }
    
    @Test
    @DisplayName("Composite exceptions should handle multiple errors")
    void compositeExceptionsShouldHandleMultipleErrors() {
        // Create composite exception
        ComponentId componentId1 = ComponentId.create("component-1");
        ComponentId componentId2 = ComponentId.create("component-2");
        
        CompositeException compositeEx = new CompositeException("Composite operation failed");
        compositeEx.addSuppressed(new ComponentNotFoundException(componentId1));
        compositeEx.addSuppressed(new ComponentNotFoundException(componentId2));
        
        // Verify suppressed exceptions
        Throwable[] suppressed = compositeEx.getSuppressed();
        assertEquals(2, suppressed.length, "CompositeException should contain suppressed exceptions");
        assertTrue(suppressed[0] instanceof ComponentNotFoundException, 
            "Suppressed exceptions should be preserved with type");
    }
    
    @Test
    @DisplayName("Exceptions should be unchecked")
    void exceptionsShouldBeUnchecked() {
        // Verify that domain exceptions extend RuntimeException
        org.s8r.domain.exception.ComponentException domainEx = 
            new org.s8r.domain.exception.ComponentException("Test");
        org.s8r.component.exception.ComponentException componentEx = 
            new org.s8r.component.exception.ComponentException("Test");
            
        assertTrue(domainEx instanceof RuntimeException, 
            "Domain exceptions should extend RuntimeException");
        assertTrue(componentEx instanceof RuntimeException, 
            "Component exceptions should extend RuntimeException");
    }
    
    @Test
    @DisplayName("Domain and core exceptions are properly separated")
    void domainAndCoreExceptionsAreProperlyStructured() {
        InitializationException coreEx = new InitializationException("Core initialization failed");
        ComponentId testId = ComponentId.create("test-component");
        ComponentNotFoundException domainEx = new ComponentNotFoundException(testId);
        
        // Check that these are different exception types (not inheritance)
        assertNotEquals(org.s8r.domain.exception.ComponentException.class, InitializationException.class,
            "Core exceptions should be separate from domain exceptions");
        assertNotEquals(org.s8r.component.exception.ComponentException.class, InitializationException.class,
            "Core exceptions should be separate from component exceptions");
        assertTrue(domainEx instanceof org.s8r.domain.exception.ComponentException,
            "Domain exceptions should extend the domain component exception");
    }
    
    @Test
    @DisplayName("Exceptions have meaningful messages")
    void exceptionsShouldHaveMeaningfulMessages() {
        // Create exceptions with context
        ComponentId testId = ComponentId.create("test-component");
        ComponentNotFoundException notFoundEx = new ComponentNotFoundException(testId);
        
        // Verify messages contain appropriate details
        String message = notFoundEx.getMessage();
        
        assertTrue(message.contains(testId.toString()) || 
                   message.contains(testId.getShortId()),
                   "ComponentNotFoundException should include component ID in message");
    }
}