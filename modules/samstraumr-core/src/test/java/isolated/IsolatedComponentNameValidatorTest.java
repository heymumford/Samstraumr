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

package isolated;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.s8r.domain.exception.InvalidComponentNameException;
import org.s8r.domain.validation.ComponentNameValidator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ComponentNameValidator} that can run in isolation.
 */
@DisplayName("Isolated Component Name Validator Tests")
public class IsolatedComponentNameValidatorTest {

    @Test
    @DisplayName("Valid component names should pass validation")
    void validNamesShouldPass() {
        // Valid names should not throw exceptions
        assertDoesNotThrow(() -> ComponentNameValidator.validateComponentName("TestComponent"));
        assertDoesNotThrow(() -> ComponentNameValidator.validateComponentName("test-component-123"));
        assertDoesNotThrow(() -> ComponentNameValidator.validateComponentName("component_with_underscores"));
        assertDoesNotThrow(() -> ComponentNameValidator.validateComponentName("component.with.dots"));
        assertDoesNotThrow(() -> ComponentNameValidator.validateComponentName("abc")); // Min length
        
        // Boundary test: exactly 100 characters (max length)
        String exactMaxLength = "a".repeat(ComponentNameValidator.getMaxNameLength());
        assertDoesNotThrow(() -> ComponentNameValidator.validateComponentName(exactMaxLength));
    }
    
    @Test
    @DisplayName("isValidComponentName should return boolean result")
    void isValidComponentNameShouldReturnBoolean() {
        // Valid names
        assertTrue(ComponentNameValidator.isValidComponentName("ValidName"));
        assertTrue(ComponentNameValidator.isValidComponentName("valid-name-123"));
        
        // Invalid names
        assertFalse(ComponentNameValidator.isValidComponentName(null));
        assertFalse(ComponentNameValidator.isValidComponentName(""));
        assertFalse(ComponentNameValidator.isValidComponentName("ab")); // Too short
        assertFalse(ComponentNameValidator.isValidComponentName("a".repeat(101))); // Too long
        assertFalse(ComponentNameValidator.isValidComponentName("name with spaces"));
        assertFalse(ComponentNameValidator.isValidComponentName("name!with!symbols"));
    }

    @Test
    @DisplayName("Null component name should throw exception")
    void nullNameShouldThrow() {
        InvalidComponentNameException exception = assertThrows(
                InvalidComponentNameException.class,
                () -> ComponentNameValidator.validateComponentName(null)
        );
        
        assertEquals("null", exception.getInvalidName());
        assertEquals("Component name must be non-null", exception.getValidationRule());
        assertTrue(exception.getMessage().contains("cannot be null"));
    }
    
    @Test
    @DisplayName("Empty component name should throw exception")
    void emptyNameShouldThrow() {
        InvalidComponentNameException exception = assertThrows(
                InvalidComponentNameException.class,
                () -> ComponentNameValidator.validateComponentName("")
        );
        
        assertEquals("", exception.getInvalidName());
        assertEquals("Component name must contain visible characters", exception.getValidationRule());
        assertTrue(exception.getMessage().contains("cannot be empty"));
    }
    
    @Test
    @DisplayName("Too short component names should throw exception")
    void tooShortNameShouldThrow() {
        InvalidComponentNameException exception = assertThrows(
                InvalidComponentNameException.class,
                () -> ComponentNameValidator.validateComponentName("ab")
        );
        
        assertEquals("ab", exception.getInvalidName());
        assertEquals("Component name must be at least " + ComponentNameValidator.getMinNameLength() + " characters long", 
                exception.getValidationRule());
        assertTrue(exception.getMessage().contains("too short"));
    }
    
    @Test
    @DisplayName("Too long component name should throw exception")
    void tooLongNameShouldThrow() {
        String longName = "a".repeat(ComponentNameValidator.getMaxNameLength() + 1);
        
        InvalidComponentNameException exception = assertThrows(
                InvalidComponentNameException.class,
                () -> ComponentNameValidator.validateComponentName(longName)
        );
        
        assertEquals(longName, exception.getInvalidName());
        assertEquals("Component name must be at most " + ComponentNameValidator.getMaxNameLength() + " characters long", 
                exception.getValidationRule());
        assertTrue(exception.getMessage().contains("too long"));
    }
    
    @Test
    @DisplayName("Component names with illegal characters should throw exception")
    void illegalCharactersShouldThrow() {
        InvalidComponentNameException exception = assertThrows(
                InvalidComponentNameException.class,
                () -> ComponentNameValidator.validateComponentName("name with spaces")
        );
        
        assertEquals("name with spaces", exception.getInvalidName());
        assertEquals("Component name can only contain letters, numbers, dots, underscores, and hyphens", 
                exception.getValidationRule());
        assertTrue(exception.getMessage().contains("illegal characters"));
    }
    
    @Test
    @DisplayName("Component names with disallowed sequences should throw exception")
    void disallowedSequencesShouldThrow() {
        InvalidComponentNameException exception = assertThrows(
                InvalidComponentNameException.class,
                () -> ComponentNameValidator.validateComponentName("name..with..dots")
        );
        
        assertEquals("name..with..dots", exception.getInvalidName());
        assertTrue(exception.getValidationRule().contains("cannot contain the sequence"));
        assertTrue(exception.getMessage().contains("disallowed sequence"));
    }
    
    @Test
    @DisplayName("Component names with reserved prefixes should throw exception")
    void reservedPrefixesShouldThrow() {
        InvalidComponentNameException exception = assertThrows(
                InvalidComponentNameException.class,
                () -> ComponentNameValidator.validateComponentName("system.component")
        );
        
        assertEquals("system.component", exception.getInvalidName());
        assertTrue(exception.getValidationRule().contains("cannot start with the reserved prefix"));
        assertTrue(exception.getMessage().contains("uses reserved prefix"));
    }
}