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

package org.s8r.domain.exception;

import org.s8r.domain.identity.ComponentId;

/**
 * Exception thrown when an operation refers to a component that doesn't exist in the current
 * context.
 *
 * <p>This exception is typically thrown during validation to prevent operations that reference
 * non-existent components, such as adding a non-existent component to a machine or creating a
 * connection to a non-existent component.
 */
public class NonExistentComponentReferenceException extends ComponentException {
  private static final long serialVersionUID = 1L;

  private final ComponentId referringComponentId;
  private final ComponentId referencedComponentId;
  private final String operationType;

  /**
   * Creates a new NonExistentComponentReferenceException.
   *
   * @param operationType The type of operation that attempted to reference the non-existent
   *     component
   * @param referringComponentId The ID of the component making the reference
   * @param referencedComponentId The ID of the referenced component that doesn't exist
   */
  public NonExistentComponentReferenceException(
      String operationType, ComponentId referringComponentId, ComponentId referencedComponentId) {
    super(
        String.format(
            "Operation '%s' failed: Component %s attempted to reference non-existent component %s",
            operationType, referringComponentId.getShortId(), referencedComponentId.getShortId()));
    this.operationType = operationType;
    this.referringComponentId = referringComponentId;
    this.referencedComponentId = referencedComponentId;
  }

  /**
   * Creates a new NonExistentComponentReferenceException with a custom message.
   *
   * @param message The custom error message
   * @param operationType The type of operation that attempted to reference the non-existent
   *     component
   * @param referringComponentId The ID of the component making the reference
   * @param referencedComponentId The ID of the referenced component that doesn't exist
   */
  public NonExistentComponentReferenceException(
      String message,
      String operationType,
      ComponentId referringComponentId,
      ComponentId referencedComponentId) {
    super(message);
    this.operationType = operationType;
    this.referringComponentId = referringComponentId;
    this.referencedComponentId = referencedComponentId;
  }

  /**
   * Creates a new NonExistentComponentReferenceException with a cause.
   *
   * @param operationType The type of operation that attempted to reference the non-existent
   *     component
   * @param referringComponentId The ID of the component making the reference
   * @param referencedComponentId The ID of the referenced component that doesn't exist
   * @param cause The cause of the exception
   */
  public NonExistentComponentReferenceException(
      String operationType,
      ComponentId referringComponentId,
      ComponentId referencedComponentId,
      Throwable cause) {
    super(
        String.format(
            "Operation '%s' failed: Component %s attempted to reference non-existent component %s",
            operationType, referringComponentId.getShortId(), referencedComponentId.getShortId()),
        cause);
    this.operationType = operationType;
    this.referringComponentId = referringComponentId;
    this.referencedComponentId = referencedComponentId;
  }

  /**
   * Gets the ID of the component that attempted to reference a non-existent component.
   *
   * @return The referring component's ID
   */
  public ComponentId getReferringComponentId() {
    return referringComponentId;
  }

  /**
   * Gets the ID of the referenced component that doesn't exist.
   *
   * @return The referenced component's ID
   */
  public ComponentId getReferencedComponentId() {
    return referencedComponentId;
  }

  /**
   * Gets the type of operation that attempted to reference the non-existent component.
   *
   * @return The operation type
   */
  public String getOperationType() {
    return operationType;
  }
}
