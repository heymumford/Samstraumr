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

/**
 * Exception thrown when a component name contains illegal characters, is too short or too long,
 * or otherwise fails to meet the component name validation requirements.
 */
public class InvalidComponentNameException extends ComponentException {
  private static final long serialVersionUID = 1L;
  
  private final String invalidName;
  private final String validationRule;
  
  /**
   * Creates a new InvalidComponentNameException with details about the validation failure.
   *
   * @param message The error message
   * @param invalidName The invalid component name that caused the exception
   * @param validationRule The specific validation rule that was violated
   */
  public InvalidComponentNameException(String message, String invalidName, String validationRule) {
    super(message);
    this.invalidName = invalidName;
    this.validationRule = validationRule;
  }
  
  /**
   * Returns the invalid component name that caused this exception.
   *
   * @return The invalid component name
   */
  public String getInvalidName() {
    return invalidName;
  }
  
  /**
   * Returns the specific validation rule that was violated.
   *
   * @return The validation rule description
   */
  public String getValidationRule() {
    return validationRule;
  }
}