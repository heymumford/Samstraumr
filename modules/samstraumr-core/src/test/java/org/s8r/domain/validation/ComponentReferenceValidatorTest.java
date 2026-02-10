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

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.Function;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.s8r.domain.exception.NonExistentComponentReferenceException;
import org.s8r.domain.identity.ComponentId;

/** Tests for the ComponentReferenceValidator utility class. */
class ComponentReferenceValidatorTest {

  @Test
  @DisplayName("validateComponentReference should pass when component exists")
  void validateComponentReferencePassesWhenComponentExists() {
    // Arrange
    ComponentId referringId = ComponentId.create("ReferringComponent");
    ComponentId referencedId = ComponentId.create("ReferencedComponent");
    Function<ComponentId, Boolean> existsFunction = id -> true; // Always exists

    // Act & Assert - No exception should be thrown
    assertDoesNotThrow(
        () -> {
          ComponentReferenceValidator.validateComponentReference(
              "testOperation", referringId, referencedId, existsFunction);
        });
  }

  @Test
  @DisplayName("validateComponentReference should throw when component doesn't exist")
  void validateComponentReferenceThrowsWhenComponentDoesNotExist() {
    // Arrange
    ComponentId referringId = ComponentId.create("ReferringComponent");
    ComponentId referencedId = ComponentId.create("NonExistentComponent");
    Function<ComponentId, Boolean> existsFunction = id -> false; // Never exists

    // Act & Assert
    NonExistentComponentReferenceException exception =
        assertThrows(
            NonExistentComponentReferenceException.class,
            () -> {
              ComponentReferenceValidator.validateComponentReference(
                  "testOperation", referringId, referencedId, existsFunction);
            });

    // Verify exception properties
    assertEquals("testOperation", exception.getOperationType());
    assertEquals(referringId, exception.getReferringComponentId());
    assertEquals(referencedId, exception.getReferencedComponentId());
  }

  @Test
  @DisplayName(
      "validateComponentReference should throw IllegalArgumentException for null referringComponentId")
  void validateComponentReferenceThrowsForNullReferringId() {
    // Arrange
    ComponentId referencedId = ComponentId.create("ReferencedComponent");
    Function<ComponentId, Boolean> existsFunction = id -> true;

    // Act & Assert
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          ComponentReferenceValidator.validateComponentReference(
              "testOperation", null, referencedId, existsFunction);
        });
  }

  @Test
  @DisplayName(
      "validateComponentReference should throw IllegalArgumentException for null referencedComponentId")
  void validateComponentReferenceThrowsForNullReferencedId() {
    // Arrange
    ComponentId referringId = ComponentId.create("ReferringComponent");
    Function<ComponentId, Boolean> existsFunction = id -> true;

    // Act & Assert
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          ComponentReferenceValidator.validateComponentReference(
              "testOperation", referringId, null, existsFunction);
        });
  }

  @Test
  @DisplayName("validateComponentReferences should pass when all components exist")
  void validateComponentReferencesPassesWhenAllComponentsExist() {
    // Arrange
    ComponentId referringId = ComponentId.create("ReferringComponent");
    ComponentId referencedId1 = ComponentId.create("ReferencedComponent1");
    ComponentId referencedId2 = ComponentId.create("ReferencedComponent2");
    Function<ComponentId, Boolean> existsFunction = id -> true; // Always exists

    // Act & Assert - No exception should be thrown
    assertDoesNotThrow(
        () -> {
          ComponentReferenceValidator.validateComponentReferences(
              "testOperation", referringId, existsFunction, referencedId1, referencedId2);
        });
  }

  @Test
  @DisplayName("validateComponentReferences should throw when any component doesn't exist")
  void validateComponentReferencesThrowsWhenAnyComponentDoesNotExist() {
    // Arrange
    ComponentId referringId = ComponentId.create("ReferringComponent");
    ComponentId referencedId1 = ComponentId.create("ReferencedComponent1");
    ComponentId referencedId2 = ComponentId.create("NonExistentComponent");

    // Create a function that says id2 doesn't exist
    Function<ComponentId, Boolean> existsFunction =
        id -> !id.getIdString().equals(referencedId2.getIdString());

    // Act & Assert
    NonExistentComponentReferenceException exception =
        assertThrows(
            NonExistentComponentReferenceException.class,
            () -> {
              ComponentReferenceValidator.validateComponentReferences(
                  "testOperation", referringId, existsFunction, referencedId1, referencedId2);
            });

    // Verify exception properties
    assertEquals("testOperation", exception.getOperationType());
    assertEquals(referringId, exception.getReferringComponentId());
    assertEquals(referencedId2, exception.getReferencedComponentId());
  }

  @Test
  @DisplayName("validateComponentReferences should do nothing with empty array")
  void validateComponentReferencesHandlesEmptyArray() {
    // Arrange
    ComponentId referringId = ComponentId.create("ReferringComponent");
    Function<ComponentId, Boolean> existsFunction =
        id -> {
          fail("existsFunction should not be called");
          return false;
        };

    // Act & Assert - No exception should be thrown, and existsFunction should not be called
    assertDoesNotThrow(
        () -> {
          ComponentReferenceValidator.validateComponentReferences(
              "testOperation", referringId, existsFunction);
        });
  }

  @Test
  @DisplayName("validateComponentReferences should handle null array gracefully")
  void validateComponentReferencesHandlesNullArray() {
    // Arrange
    ComponentId referringId = ComponentId.create("ReferringComponent");
    Function<ComponentId, Boolean> existsFunction =
        id -> {
          fail("existsFunction should not be called");
          return false;
        };

    // Act & Assert - No exception should be thrown for null array
    assertDoesNotThrow(
        () -> {
          ComponentReferenceValidator.validateComponentReferences(
              "testOperation", referringId, existsFunction, (ComponentId[]) null);
        });
  }
}
