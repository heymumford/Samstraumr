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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.s8r.domain.exception.InvalidComponentNameException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ComponentNameValidator}.
 */
@DisplayName("Component Name Validator Tests")
class ComponentNameValidatorTest {

    @Test
    @DisplayName("Valid component names should pass validation")
    void validNamesShouldPass() {
        // Valid names should not throw exceptions
        assertDoesNotThrow(() -> ComponentNameValidator.validateComponentName("TestComponent"));
        assertDoesNotThrow(() -> ComponentNameValidator.validateComponentName("test-component-123"));
        assertDoesNotThrow(() -> ComponentNameValidator.validateComponentName("component_with_underscores"));
        assertDoesNotThrow(() -> ComponentNameValidator.validateComponentName("component.with.dots"));
        assertDoesNotThrow(() -> ComponentNameValidator.validateComponentName("abc")); // Min length
        assertDoesNotThrow(() -> ComponentNameValidator.validateComponentName("a1-"));
        
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
    @DisplayName("White space only component name should throw exception")
    void whiteSpaceOnlyNameShouldThrow() {
        InvalidComponentNameException exception = assertThrows(
                InvalidComponentNameException.class,
                () -> ComponentNameValidator.validateComponentName("   ")
        );
        
        assertEquals("   ", exception.getInvalidName());
        assertEquals("Component name must contain visible characters", exception.getValidationRule());
        assertTrue(exception.getMessage().contains("cannot be empty or just whitespace"));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"ab", "a", ""})
    @DisplayName("Too short component names should throw exception")
    void tooShortNameShouldThrow(String shortName) {
        if (shortName.isEmpty()) {
            return; // Empty name is caught by a different validation
        }
        
        InvalidComponentNameException exception = assertThrows(
                InvalidComponentNameException.class,
                () -> ComponentNameValidator.validateComponentName(shortName)
        );
        
        assertEquals(shortName, exception.getInvalidName());
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
    
    @ParameterizedTest
    @ValueSource(strings = {"name with spaces", "name@invalid", "name/invalid", "name\\invalid", 
            "name+invalid", "name#invalid", "name%invalid", "name&invalid", "name*invalid"})
    @DisplayName("Component names with illegal characters should throw exception")
    void illegalCharactersShouldThrow(String invalidName) {
        InvalidComponentNameException exception = assertThrows(
                InvalidComponentNameException.class,
                () -> ComponentNameValidator.validateComponentName(invalidName)
        );
        
        assertEquals(invalidName, exception.getInvalidName());
        assertEquals("Component name can only contain letters, numbers, dots, underscores, and hyphens", 
                exception.getValidationRule());
        assertTrue(exception.getMessage().contains("illegal characters"));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"name..with..dots", "name--with--hyphens", "name__with__underscores", 
            "name//with//slashes", "name\\\\with\\\\backslashes", "name  with  spaces"})
    @DisplayName("Component names with disallowed sequences should throw exception")
    void disallowedSequencesShouldThrow(String invalidName) {
        // Skip test cases that would be caught by illegal character validation first
        if (invalidName.contains(" ") || invalidName.contains("/") || invalidName.contains("\\")) {
            return;
        }
        
        InvalidComponentNameException exception = assertThrows(
                InvalidComponentNameException.class,
                () -> ComponentNameValidator.validateComponentName(invalidName)
        );
        
        assertEquals(invalidName, exception.getInvalidName());
        assertTrue(exception.getValidationRule().contains("cannot contain the sequence"));
        assertTrue(exception.getMessage().contains("disallowed sequence"));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"system.component", "SYSTEM.component", "System.Component", 
            "admin.service", "Admin.Service", "ADMIN.service", 
            "root.component", "global.service", "internal.module"})
    @DisplayName("Component names with reserved prefixes should throw exception")
    void reservedPrefixesShouldThrow(String invalidName) {
        InvalidComponentNameException exception = assertThrows(
                InvalidComponentNameException.class,
                () -> ComponentNameValidator.validateComponentName(invalidName)
        );
        
        assertEquals(invalidName, exception.getInvalidName());
        assertTrue(exception.getValidationRule().contains("cannot start with the reserved prefix"));
        assertTrue(exception.getMessage().contains("uses reserved prefix"));
    }
}