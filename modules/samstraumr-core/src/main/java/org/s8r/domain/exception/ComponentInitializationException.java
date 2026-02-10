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

package org.s8r.domain.exception;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.s8r.domain.identity.ComponentId;

/**
 * Exception thrown when a component cannot be properly initialized.
 *
 * <p>This exception is used for errors that occur during the component's initialization process,
 * such as configuration errors or resource allocation failures.
 */
public class ComponentInitializationException extends ComponentException {
  private static final long serialVersionUID = 1L;

  private final ComponentId componentId;
  private final Map<String, Object> details;

  /**
   * Creates a new ComponentInitializationException with the specified component ID and error
   * message.
   *
   * @param componentId The ID of the component that failed to initialize
   * @param message The error message
   */
  public ComponentInitializationException(ComponentId componentId, String message) {
    super(message);
    this.componentId = componentId;
    this.details = new HashMap<>();
  }

  /**
   * Creates a new ComponentInitializationException with the specified component ID, error message,
   * and cause.
   *
   * @param componentId The ID of the component that failed to initialize
   * @param message The error message
   * @param cause The cause of the exception
   */
  public ComponentInitializationException(
      ComponentId componentId, String message, Throwable cause) {
    super(message, cause);
    this.componentId = componentId;
    this.details = new HashMap<>();
  }

  /**
   * Creates a new ComponentInitializationException with the specified error message. This
   * constructor is used for general initialization errors not tied to a specific component.
   *
   * @param message The error message
   */
  public ComponentInitializationException(String message) {
    super(message);
    this.componentId = null;
    this.details = new HashMap<>();
  }

  /**
   * Creates a new ComponentInitializationException with the specified error message and cause. This
   * constructor is used for general initialization errors not tied to a specific component.
   *
   * @param message The error message
   * @param cause The cause of the exception
   */
  public ComponentInitializationException(String message, Throwable cause) {
    super(message, cause);
    this.componentId = null;
    this.details = new HashMap<>();
  }

  /**
   * Adds a detail to this exception to provide more context about the initialization failure.
   *
   * @param key The detail key
   * @param value The detail value
   * @return This exception, for method chaining
   */
  public ComponentInitializationException addDetail(String key, Object value) {
    if (key != null) {
      details.put(key, value);
    }
    return this;
  }

  /**
   * Gets the ID of the component that failed to initialize.
   *
   * @return The component ID
   */
  public ComponentId getComponentId() {
    return componentId;
  }

  /**
   * Gets all the details associated with this initialization failure.
   *
   * @return An unmodifiable map of detail keys to values
   */
  public Map<String, Object> getDetails() {
    return Collections.unmodifiableMap(details);
  }

  /**
   * Gets a specific detail by key.
   *
   * @param key The detail key
   * @return The detail value, or null if the key doesn't exist
   */
  public Object getDetail(String key) {
    return details.get(key);
  }

  @Override
  public String getMessage() {
    StringBuilder message = new StringBuilder(super.getMessage());

    if (!details.isEmpty()) {
      message.append("\nDetails:");
      for (Map.Entry<String, Object> entry : details.entrySet()) {
        message.append("\n - ").append(entry.getKey()).append(": ").append(entry.getValue());
      }
    }

    return message.toString();
  }
}
