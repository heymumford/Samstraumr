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

package org.s8r.core.composite;

/**
 * Defines the different types of composites available in the S8r framework.
 *
 * <p>Each composite type represents a different pattern or purpose for organizing components. The
 * type influences how components within the composite interact and communicate with each other.
 */
public enum CompositeType {
  /**
   * Observer pattern: Components within the composite observe and respond to events from an
   * observed source component.
   */
  OBSERVER("Observer"),

  /**
   * Transformer pattern: Components are chained in a data transformation sequence, each processing
   * and transforming the data before passing it to the next component.
   */
  TRANSFORMER("Transformer"),

  /**
   * Validator pattern: Components perform validation operations, each checking a specific aspect of
   * the data.
   */
  VALIDATOR("Validator"),

  /**
   * Processor pattern: Components work together to process data through a series of processing
   * steps.
   */
  PROCESSOR("Processor"),

  /**
   * Service pattern: Components provide various services as part of a cohesive service offering.
   */
  SERVICE("Service"),

  /**
   * Controller pattern: Components are managed by a controller component that determines execution
   * flow.
   */
  CONTROLLER("Controller"),

  /** Adapter pattern: Components adapt between different interfaces or protocols. */
  ADAPTER("Adapter"),

  /** Generic composite: A general-purpose composite without a specific pattern. */
  GENERIC("Generic");

  private final String displayName;

  /**
   * Creates a new CompositeType.
   *
   * @param displayName The human-readable name of the composite type
   */
  CompositeType(String displayName) {
    this.displayName = displayName;
  }

  /**
   * Gets the display name of this composite type.
   *
   * @return The display name
   */
  public String getDisplayName() {
    return displayName;
  }
}
