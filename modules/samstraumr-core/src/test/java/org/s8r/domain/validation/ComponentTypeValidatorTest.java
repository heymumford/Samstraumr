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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.s8r.domain.component.ComponentType;
import org.s8r.domain.exception.InvalidComponentTypeException;
import org.s8r.domain.identity.ComponentId;

/** Test class for {@link ComponentTypeValidator}. */
@DisplayName("Component Type Validator Tests")
class ComponentTypeValidatorTest {

  @Test
  @DisplayName("Valid component types should pass validation")
  void validTypesShouldPass() {
    ComponentId componentId = ComponentId.create("TestComponent");

    // Test valid types from enum
    for (ComponentType type : ComponentType.values()) {
      assertDoesNotThrow(
          () -> ComponentTypeValidator.validateComponentType(type.getCode(), componentId));
    }
  }

  @Test
  @DisplayName("Null or empty component type should throw exception")
  void nullOrEmptyTypeShouldThrow() {
    ComponentId componentId = ComponentId.create("TestComponent");

    // Test null type
    InvalidComponentTypeException exception =
        assertThrows(
            InvalidComponentTypeException.class,
            () -> ComponentTypeValidator.validateComponentType(null, componentId));

    assertEquals("null or empty", exception.getInvalidType());
    assertEquals(componentId, exception.getComponentId());

    // Test empty type
    exception =
        assertThrows(
            InvalidComponentTypeException.class,
            () -> ComponentTypeValidator.validateComponentType("", componentId));

    assertEquals("null or empty", exception.getInvalidType());
    assertEquals(componentId, exception.getComponentId());
  }

  @ParameterizedTest
  @ValueSource(strings = {"unknown", "invalid-type", "custom", "non-existent"})
  @DisplayName("Unknown component types should throw exception")
  void unknownTypesShouldThrow(String invalidType) {
    ComponentId componentId = ComponentId.create("TestComponent");

    InvalidComponentTypeException exception =
        assertThrows(
            InvalidComponentTypeException.class,
            () -> ComponentTypeValidator.validateComponentType(invalidType, componentId));

    assertEquals(invalidType, exception.getInvalidType());
    assertEquals(componentId, exception.getComponentId());
  }

  @Test
  @DisplayName("isValidComponentType should return boolean result")
  void isValidComponentTypeShouldReturnBooleanResult() {
    // Valid types
    for (ComponentType type : ComponentType.values()) {
      assertTrue(ComponentTypeValidator.isValidComponentType(type.getCode()));
    }

    // Invalid types
    assertFalse(ComponentTypeValidator.isValidComponentType(null));
    assertFalse(ComponentTypeValidator.isValidComponentType(""));
    assertFalse(ComponentTypeValidator.isValidComponentType("unknown"));
    assertFalse(ComponentTypeValidator.isValidComponentType("invalid-type"));
  }

  @ParameterizedTest
  @CsvSource({
    "processor,PROCESS_DATA,true",
    "validator,VALIDATE_DATA,true",
    "observer,OBSERVE,true",
    "factory,CREATE_COMPONENT,true",
    "monitor,OBSERVE,true",
    "validator,PROCESS_DATA,false",
    "processor,CREATE_COMPONENT,false",
    "observer,VALIDATE_DATA,false"
  })
  @DisplayName("Component types should be correctly validated for operations")
  void componentTypesShouldBeValidatedForOperations(
      String typeCode, String operation, boolean expected) {
    assertEquals(expected, ComponentTypeValidator.isAllowedForOperation(typeCode, operation));

    ComponentId componentId = ComponentId.create("TestComponent");

    if (expected) {
      assertDoesNotThrow(
          () ->
              ComponentTypeValidator.validateComponentTypeForOperation(
                  typeCode, componentId, operation));
    } else {
      assertThrows(
          InvalidComponentTypeException.class,
          () ->
              ComponentTypeValidator.validateComponentTypeForOperation(
                  typeCode, componentId, operation));
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"SYSTEM_ADMIN", "NETWORK_CONTROL", "SECURITY_OVERRIDE"})
  @DisplayName("Restricted operations should not be allowed for any component type")
  void restrictedOperationsShouldNotBeAllowed(String restrictedOperation) {
    ComponentId componentId = ComponentId.create("TestComponent");

    // Test all component types
    for (ComponentType type : ComponentType.values()) {
      assertFalse(
          ComponentTypeValidator.isAllowedForOperation(type.getCode(), restrictedOperation));

      InvalidComponentTypeException exception =
          assertThrows(
              InvalidComponentTypeException.class,
              () ->
                  ComponentTypeValidator.validateComponentTypeForOperation(
                      type.getCode(), componentId, restrictedOperation));

      assertEquals(type.getCode(), exception.getInvalidType());
      assertEquals(componentId, exception.getComponentId());
      assertEquals(restrictedOperation + " (RESTRICTED)", exception.getOperation());
    }
  }

  @Test
  @DisplayName("Operations without specific restrictions should allow any valid component type")
  void operationsWithoutRestrictionsShouldAllowAnyType() {
    String operation = "GENERAL_OPERATION";
    ComponentId componentId = ComponentId.create("TestComponent");

    // Test all component types
    for (ComponentType type : ComponentType.values()) {
      assertTrue(ComponentTypeValidator.isAllowedForOperation(type.getCode(), operation));

      assertDoesNotThrow(
          () ->
              ComponentTypeValidator.validateComponentTypeForOperation(
                  type.getCode(), componentId, operation));
    }
  }
}
