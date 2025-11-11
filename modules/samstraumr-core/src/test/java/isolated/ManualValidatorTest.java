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

import org.s8r.domain.exception.InvalidComponentNameException;
import org.s8r.domain.validation.ComponentNameValidator;

/**
 * Simple manual test for {@link ComponentNameValidator} that runs without JUnit.
 */
public class ManualValidatorTest {

    public static void main(String[] args) {
        System.out.println("Running manual component name validator tests...");
        
        // Test valid names
        testValidNames();
        
        // Test invalid names
        testInvalidNames();
        
        // Test boolean validation method
        testBooleanValidation();
        
        System.out.println("\nAll tests completed successfully!");
    }
    
    private static void testValidNames() {
        System.out.println("\nTesting valid component names:");
        
        tryValidate("TestComponent", true);
        tryValidate("test-component-123", true);
        tryValidate("component_with_underscores", true);
        tryValidate("component.with.dots", true);
        tryValidate("abc", true); // Min length
    }
    
    private static void testInvalidNames() {
        System.out.println("\nTesting invalid component names:");
        
        // Null
        tryValidate(null, false);
        
        // Empty
        tryValidate("", false);
        
        // Too short
        tryValidate("ab", false);
        
        // Too long
        String longName = "a".repeat(ComponentNameValidator.getMaxNameLength() + 1);
        tryValidate(longName, false);
        
        // Invalid characters
        tryValidate("name with spaces", false);
        tryValidate("name@invalid", false);
        
        // Disallowed sequences
        tryValidate("name..with..dots", false);
        tryValidate("name--with--hyphens", false);
        
        // Reserved prefixes
        tryValidate("system.component", false);
        tryValidate("admin.service", false);
    }
    
    private static void testBooleanValidation() {
        System.out.println("\nTesting isValidComponentName method:");
        
        boolean validResult = ComponentNameValidator.isValidComponentName("ValidName");
        System.out.println("ValidName: " + (validResult ? "Valid ✓" : "Invalid ✗"));
        
        boolean invalidResult = ComponentNameValidator.isValidComponentName("ab");
        System.out.println("ab (too short): " + (invalidResult ? "Valid ✗" : "Invalid ✓"));
    }
    
    private static void tryValidate(String name, boolean shouldBeValid) {
        try {
            ComponentNameValidator.validateComponentName(name);
            if (shouldBeValid) {
                System.out.println("✓ " + name + " is valid (as expected)");
            } else {
                System.out.println("✗ " + name + " was accepted but should have been rejected");
            }
        } catch (InvalidComponentNameException e) {
            if (shouldBeValid) {
                System.out.println("✗ " + name + " was rejected but should have been valid");
                System.out.println("  Error: " + e.getMessage());
            } else {
                System.out.println("✓ " + name + " correctly rejected: " + e.getValidationRule());
            }
        }
    }
}