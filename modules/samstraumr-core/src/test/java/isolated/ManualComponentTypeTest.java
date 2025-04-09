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

import org.s8r.domain.component.ComponentType;
import org.s8r.domain.exception.InvalidComponentTypeException;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.validation.ComponentTypeValidator;

/**
 * Simple manual test for {@link ComponentTypeValidator} and {@link ComponentType} that runs without JUnit.
 */
public class ManualComponentTypeTest {

    public static void main(String[] args) {
        System.out.println("Running manual component type tests...");
        
        // Create a test component ID
        ComponentId testId = ComponentId.create("TestComponent");
        
        System.out.println("\nTesting ComponentType enum values:");
        for (ComponentType type : ComponentType.values()) {
            System.out.println("✓ " + type.name() + " (" + type.getCode() + "): " + type.getDescription());
        }
        
        System.out.println("\nTesting component type validation:");
        
        // Test valid types
        try {
            ComponentTypeValidator.validateComponentType("standard", testId);
            System.out.println("✓ standard is a valid component type");
        } catch (InvalidComponentTypeException e) {
            System.out.println("✗ standard was rejected but should be valid: " + e.getMessage());
        }
        
        try {
            ComponentTypeValidator.validateComponentType("processor", testId);
            System.out.println("✓ processor is a valid component type");
        } catch (InvalidComponentTypeException e) {
            System.out.println("✗ processor was rejected but should be valid: " + e.getMessage());
        }
        
        // Test invalid types
        try {
            ComponentTypeValidator.validateComponentType("invalid", testId);
            System.out.println("✗ invalid was accepted but should be rejected");
        } catch (InvalidComponentTypeException e) {
            System.out.println("✓ invalid was correctly rejected: " + e.getMessage());
        }
        
        try {
            ComponentTypeValidator.validateComponentType(null, testId);
            System.out.println("✗ null was accepted but should be rejected");
        } catch (InvalidComponentTypeException e) {
            System.out.println("✓ null was correctly rejected: " + e.getMessage());
        }
        
        // Test operation validation
        System.out.println("\nTesting operation validation:");
        
        boolean allowed = ComponentTypeValidator.isAllowedForOperation("processor", "PROCESS_DATA");
        System.out.println("processor for PROCESS_DATA: " + (allowed ? "Allowed ✓" : "Not allowed ✗"));
        
        allowed = ComponentTypeValidator.isAllowedForOperation("validator", "VALIDATE_DATA");
        System.out.println("validator for VALIDATE_DATA: " + (allowed ? "Allowed ✓" : "Not allowed ✗"));
        
        allowed = ComponentTypeValidator.isAllowedForOperation("processor", "VALIDATE_DATA");
        System.out.println("processor for VALIDATE_DATA: " + (allowed ? "Allowed ✗" : "Not allowed ✓"));
        
        allowed = ComponentTypeValidator.isAllowedForOperation("standard", "SYSTEM_ADMIN");
        System.out.println("standard for SYSTEM_ADMIN: " + (allowed ? "Allowed ✗" : "Not allowed ✓ (restricted)"));
        
        System.out.println("\nAll tests completed!");
    }
}