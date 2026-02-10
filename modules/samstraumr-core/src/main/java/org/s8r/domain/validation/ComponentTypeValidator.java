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

package org.s8r.domain.validation;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.s8r.domain.component.ComponentType;
import org.s8r.domain.exception.InvalidComponentTypeException;
import org.s8r.domain.identity.ComponentId;

/**
 * Utility class for validating component types. Ensures that component types are valid and
 * appropriate for specific operations.
 */
public class ComponentTypeValidator {

  // Map of operations to allowed component types
  private static final Map<String, Set<ComponentType>> ALLOWED_TYPES_BY_OPERATION = new HashMap<>();

  // Restricted operations
  private static final Set<String> RESTRICTED_OPERATIONS =
      Collections.unmodifiableSet(
          new HashSet<>(Arrays.asList("SYSTEM_ADMIN", "NETWORK_CONTROL", "SECURITY_OVERRIDE")));

  static {
    // Initialize allowed types by operation
    ALLOWED_TYPES_BY_OPERATION.put(
        "CONNECT",
        new HashSet<>(
            Arrays.asList(
                ComponentType.CONNECTOR,
                ComponentType.COMPOSITE,
                ComponentType.MACHINE,
                ComponentType.CONTROLLER,
                ComponentType.SYSTEM)));

    ALLOWED_TYPES_BY_OPERATION.put(
        "PROCESS_DATA",
        new HashSet<>(
            Arrays.asList(
                ComponentType.PROCESSOR,
                ComponentType.TRANSFORMER,
                ComponentType.MODEL,
                ComponentType.FEATURE_EXTRACTOR,
                ComponentType.PREDICTOR,
                ComponentType.TIME_SERIES_ANALYZER,
                ComponentType.BETA_TUBE)));

    ALLOWED_TYPES_BY_OPERATION.put(
        "VALIDATE_DATA",
        new HashSet<>(Arrays.asList(ComponentType.VALIDATOR, ComponentType.PROCESSOR)));

    ALLOWED_TYPES_BY_OPERATION.put(
        "OBSERVE",
        new HashSet<>(
            Arrays.asList(
                ComponentType.OBSERVER, ComponentType.MONITOR, ComponentType.ALPHA_TUBE)));

    ALLOWED_TYPES_BY_OPERATION.put(
        "CREATE_COMPONENT",
        new HashSet<>(
            Arrays.asList(
                ComponentType.FACTORY,
                ComponentType.SYSTEM,
                ComponentType.CONTROLLER,
                ComponentType.MACHINE)));

    ALLOWED_TYPES_BY_OPERATION.put(
        "STORE_DATA",
        new HashSet<>(
            Arrays.asList(ComponentType.REPOSITORY, ComponentType.SYSTEM, ComponentType.SERVICE)));

    ALLOWED_TYPES_BY_OPERATION.put(
        "EXECUTE_ACTION",
        new HashSet<>(
            Arrays.asList(
                ComponentType.GAMMA_TUBE, ComponentType.CONTROLLER, ComponentType.SERVICE)));
  }

  /**
   * Validates if a component type is recognized by the system.
   *
   * @param typeCode The component type code to validate
   * @param componentId The ID of the component being validated
   * @throws InvalidComponentTypeException if the type is invalid
   */
  public static void validateComponentType(String typeCode, ComponentId componentId) {
    if (typeCode == null || typeCode.isEmpty()) {
      throw new InvalidComponentTypeException("null or empty", componentId);
    }

    if (!ComponentType.isValidTypeCode(typeCode)) {
      throw new InvalidComponentTypeException(typeCode, componentId);
    }
  }

  /**
   * Validates if a component type is allowed for a specific operation.
   *
   * @param typeCode The component type code to validate
   * @param componentId The ID of the component being validated
   * @param operation The operation to check
   * @throws InvalidComponentTypeException if the type is not allowed for the operation
   */
  public static void validateComponentTypeForOperation(
      String typeCode, ComponentId componentId, String operation) {

    // First validate that the type is valid at all
    validateComponentType(typeCode, componentId);

    // Check if the operation is restricted
    if (RESTRICTED_OPERATIONS.contains(operation)) {
      throw new InvalidComponentTypeException(typeCode, componentId, operation + " (RESTRICTED)");
    }

    // If the operation has specific type restrictions, check them
    if (ALLOWED_TYPES_BY_OPERATION.containsKey(operation)) {
      ComponentType componentType =
          ComponentType.fromCode(typeCode)
              .orElseThrow(() -> new InvalidComponentTypeException(typeCode, componentId));

      Set<ComponentType> allowedTypes = ALLOWED_TYPES_BY_OPERATION.get(operation);
      if (!allowedTypes.contains(componentType)) {
        throw new InvalidComponentTypeException(typeCode, componentId, operation);
      }
    }

    // If operation is not restricted and has no specific type restrictions, it's allowed
  }

  /**
   * Checks if a component type is valid without throwing an exception.
   *
   * @param typeCode The component type code to check
   * @return true if the type is valid, false otherwise
   */
  public static boolean isValidComponentType(String typeCode) {
    if (typeCode == null || typeCode.isEmpty()) {
      return false;
    }
    return ComponentType.isValidTypeCode(typeCode);
  }

  /**
   * Checks if a component type is allowed for a specific operation without throwing an exception.
   *
   * @param typeCode The component type code to check
   * @param operation The operation to check
   * @return true if the type is allowed for the operation, false otherwise
   */
  public static boolean isAllowedForOperation(String typeCode, String operation) {
    // Invalid type is never allowed
    if (!isValidComponentType(typeCode)) {
      return false;
    }

    // Restricted operations are never allowed
    if (RESTRICTED_OPERATIONS.contains(operation)) {
      return false;
    }

    // If the operation has specific type restrictions, check them
    if (ALLOWED_TYPES_BY_OPERATION.containsKey(operation)) {
      ComponentType componentType = ComponentType.fromCode(typeCode).orElse(null);
      if (componentType == null) {
        return false;
      }

      Set<ComponentType> allowedTypes = ALLOWED_TYPES_BY_OPERATION.get(operation);
      return allowedTypes.contains(componentType);
    }

    // If operation is not restricted and has no specific type restrictions, it's allowed
    return true;
  }

  /**
   * Gets the set of allowed component types for a specific operation.
   *
   * @param operation The operation
   * @return The set of allowed component types, or empty set if no restrictions
   */
  public static Set<ComponentType> getAllowedTypesForOperation(String operation) {
    return ALLOWED_TYPES_BY_OPERATION.getOrDefault(operation, Collections.emptySet());
  }

  /**
   * Gets the set of restricted operations.
   *
   * @return The set of restricted operations
   */
  public static Set<String> getRestrictedOperations() {
    return RESTRICTED_OPERATIONS;
  }
}
