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

package org.s8r.domain.component;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Enumeration of supported component types in the S8r framework. Component types define the
 * behavior, capabilities, and purpose of components.
 */
public enum ComponentType {
  // Basic types
  STANDARD("standard", "Basic component with default behavior"),
  ADAPTER("adapter", "Component that adapts between different interfaces"),
  CONNECTOR("connector", "Component that connects multiple other components"),
  PROCESSOR("processor", "Component that processes data"),
  VALIDATOR("validator", "Component that validates data"),
  TRANSFORMER("transformer", "Component that transforms data"),
  OBSERVER("observer", "Component that observes other components"),

  // Special types
  CONTROLLER("controller", "Component that controls other components"),
  SERVICE("service", "Component that provides services to other components"),
  REPOSITORY("repository", "Component that stores data"),
  FACTORY("factory", "Component that creates other components"),

  // System-level types
  SYSTEM("system", "System-level component"),
  MONITOR("monitor", "Component that monitors system state"),
  GATEWAY("gateway", "Component that serves as gateway to external systems"),

  // Machine and tube types
  MACHINE("machine", "Component that contains and coordinates composites"),
  COMPOSITE("composite", "Component that contains other components"),
  TUBE("tube", "Generalized component type that can be used in machines"),
  ALPHA_TUBE("alpha-tube", "Alpha category tube - primary sensing"),
  BETA_TUBE("beta-tube", "Beta category tube - processing"),
  GAMMA_TUBE("gamma-tube", "Gamma category tube - output/action"),

  // AI/ML specific types
  MODEL("model", "Component that represents a machine learning model"),
  FEATURE_EXTRACTOR("feature-extractor", "Component that extracts features from data"),
  PREDICTOR("predictor", "Component that makes predictions"),

  // Domain-specific types (ALZ001 example)
  NEURONAL_NETWORK("neuronal-network", "Component that simulates neuronal networks"),
  PROTEIN_EXPRESSION("protein-expression", "Component that models protein expression"),
  ENVIRONMENTAL_FACTORS("environmental-factors", "Component that models environmental factors"),
  TIME_SERIES_ANALYZER("time-series-analyzer", "Component that analyzes time series data");

  private final String code;
  private final String description;

  // Maps for lookups
  private static final Map<String, ComponentType> BY_CODE = new HashMap<>();
  private static final Set<String> VALID_TYPES = new HashSet<>();

  // Sets of related types for compatibility and checking
  private static final Set<ComponentType> SYSTEM_TYPES =
      Collections.unmodifiableSet(
          new HashSet<>(Arrays.asList(SYSTEM, MONITOR, GATEWAY, CONTROLLER, MACHINE)));

  private static final Set<ComponentType> DATA_PROCESSING_TYPES =
      Collections.unmodifiableSet(
          new HashSet<>(Arrays.asList(PROCESSOR, TRANSFORMER, VALIDATOR, FEATURE_EXTRACTOR)));

  private static final Set<ComponentType> TUBE_TYPES =
      Collections.unmodifiableSet(
          new HashSet<>(Arrays.asList(TUBE, ALPHA_TUBE, BETA_TUBE, GAMMA_TUBE)));

  private static final Set<ComponentType> AI_ML_TYPES =
      Collections.unmodifiableSet(
          new HashSet<>(Arrays.asList(MODEL, FEATURE_EXTRACTOR, PREDICTOR)));

  private static final Set<ComponentType> ALZ001_TYPES =
      Collections.unmodifiableSet(
          new HashSet<>(
              Arrays.asList(
                  NEURONAL_NETWORK,
                  PROTEIN_EXPRESSION,
                  ENVIRONMENTAL_FACTORS,
                  TIME_SERIES_ANALYZER)));

  static {
    // Initialize lookup maps
    for (ComponentType type : values()) {
      BY_CODE.put(type.code, type);
      VALID_TYPES.add(type.code);
    }
  }

  ComponentType(String code, String description) {
    this.code = code;
    this.description = description;
  }

  /**
   * Gets the type code used in configuration.
   *
   * @return The type code
   */
  public String getCode() {
    return code;
  }

  /**
   * Gets the description of this component type.
   *
   * @return The type description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Finds a component type by its code.
   *
   * @param code The type code to look up
   * @return An Optional containing the component type if found, or empty if not found
   */
  public static Optional<ComponentType> fromCode(String code) {
    return Optional.ofNullable(BY_CODE.get(code));
  }

  /**
   * Checks if a type code is valid.
   *
   * @param code The type code to check
   * @return true if the code is valid, false otherwise
   */
  public static boolean isValidTypeCode(String code) {
    return VALID_TYPES.contains(code);
  }

  /**
   * Gets all valid type codes.
   *
   * @return Set of valid type codes
   */
  public static Set<String> getAllValidTypeCodes() {
    return Collections.unmodifiableSet(VALID_TYPES);
  }

  /**
   * Checks if this component type is a system-level type.
   *
   * @return true if this is a system type, false otherwise
   */
  public boolean isSystemType() {
    return SYSTEM_TYPES.contains(this);
  }

  /**
   * Checks if this component type is a data processing type.
   *
   * @return true if this is a data processing type, false otherwise
   */
  public boolean isDataProcessingType() {
    return DATA_PROCESSING_TYPES.contains(this);
  }

  /**
   * Checks if this component type is a tube type.
   *
   * @return true if this is a tube type, false otherwise
   */
  public boolean isTubeType() {
    return TUBE_TYPES.contains(this);
  }

  /**
   * Checks if this component type is an AI/ML type.
   *
   * @return true if this is an AI/ML type, false otherwise
   */
  public boolean isAIMLType() {
    return AI_ML_TYPES.contains(this);
  }

  /**
   * Checks if this component type is an ALZ001-specific type.
   *
   * @return true if this is an ALZ001 type, false otherwise
   */
  public boolean isALZ001Type() {
    return ALZ001_TYPES.contains(this);
  }

  /**
   * Gets all system-level component types.
   *
   * @return Set of system-level component types
   */
  public static Set<ComponentType> getSystemTypes() {
    return SYSTEM_TYPES;
  }

  /**
   * Gets all data processing component types.
   *
   * @return Set of data processing component types
   */
  public static Set<ComponentType> getDataProcessingTypes() {
    return DATA_PROCESSING_TYPES;
  }

  /**
   * Gets all tube component types.
   *
   * @return Set of tube component types
   */
  public static Set<ComponentType> getTubeTypes() {
    return TUBE_TYPES;
  }

  /**
   * Gets all AI/ML component types.
   *
   * @return Set of AI/ML component types
   */
  public static Set<ComponentType> getAIMLTypes() {
    return AI_ML_TYPES;
  }

  /**
   * Gets all ALZ001-specific component types.
   *
   * @return Set of ALZ001-specific component types
   */
  public static Set<ComponentType> getALZ001Types() {
    return ALZ001_TYPES;
  }
}
