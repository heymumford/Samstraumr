/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Domain model for machine types in the S8r framework
 */

package org.samstraumr.domain.machine;

/**
 * Defines the types of machines in the S8r framework.
 *
 * <p>This enum represents the different categories and purposes of machines, which are higher-level
 * abstractions of component compositions.
 */
public enum MachineType {
  /** Data processing machine for ETL operations. */
  DATA_PROCESSOR("Processes and transforms data", Category.DATA),

  /** Analytics machine for data analysis and insights. */
  ANALYTICS("Analyzes data and provides insights", Category.DATA),

  /** Integration machine for connecting different systems. */
  INTEGRATION("Connects different systems and services", Category.INTEGRATION),

  /** Workflow machine for orchestrating processes. */
  WORKFLOW("Orchestrates complex processes and workflows", Category.PROCESS),

  /** Monitoring machine for system observation. */
  MONITORING("Observes and reports on system health", Category.OPERATIONS),

  /** Decision machine for making automated decisions. */
  DECISION("Makes automated decisions based on rules", Category.INTELLIGENCE),

  /** Learning machine for adaptive behavior. */
  LEARNING("Adapts behavior based on experience", Category.INTELLIGENCE),

  /** Communication machine for messaging and events. */
  COMMUNICATION("Handles messaging and event distribution", Category.INTEGRATION),

  /** Validation machine for ensuring data quality. */
  VALIDATION("Ensures data quality and consistency", Category.DATA),

  /** Security machine for protection and compliance. */
  SECURITY("Provides protection and ensures compliance", Category.OPERATIONS);

  /** Machine category classification. */
  public enum Category {
    /** Data-focused machines. */
    DATA("Data processing and management"),

    /** Integration-focused machines. */
    INTEGRATION("System integration and connectivity"),

    /** Process-focused machines. */
    PROCESS("Process management and orchestration"),

    /** Operations-focused machines. */
    OPERATIONS("System operations and monitoring"),

    /** Intelligence-focused machines. */
    INTELLIGENCE("Decision making and learning");

    private final String description;

    /**
     * Constructs a Category with a description.
     *
     * @param description A description of the category
     */
    Category(String description) {
      this.description = description;
    }

    /**
     * Gets the description of this category.
     *
     * @return The description
     */
    public String getDescription() {
      return description;
    }
  }

  private final String description;
  private final Category category;

  /**
   * Constructs a MachineType with a description and category.
   *
   * @param description A description of the machine type
   * @param category The machine category
   */
  MachineType(String description, Category category) {
    this.description = description;
    this.category = category;
  }

  /**
   * Gets the description of this machine type.
   *
   * @return The description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the category of this machine type.
   *
   * @return The category
   */
  public Category getCategory() {
    return category;
  }

  /**
   * Checks if this machine type is data-focused.
   *
   * @return true if this is a data-focused machine, false otherwise
   */
  public boolean isDataFocused() {
    return category == Category.DATA;
  }

  /**
   * Checks if this machine type is integration-focused.
   *
   * @return true if this is an integration-focused machine, false otherwise
   */
  public boolean isIntegrationFocused() {
    return category == Category.INTEGRATION;
  }

  /**
   * Checks if this machine type is process-focused.
   *
   * @return true if this is a process-focused machine, false otherwise
   */
  public boolean isProcessFocused() {
    return category == Category.PROCESS;
  }

  /**
   * Checks if this machine type is operations-focused.
   *
   * @return true if this is an operations-focused machine, false otherwise
   */
  public boolean isOperationsFocused() {
    return category == Category.OPERATIONS;
  }

  /**
   * Checks if this machine type is intelligence-focused.
   *
   * @return true if this is an intelligence-focused machine, false otherwise
   */
  public boolean isIntelligenceFocused() {
    return category == Category.INTELLIGENCE;
  }

  @Override
  public String toString() {
    return name() + " (" + description + ", Category: " + category + ")";
  }
}
