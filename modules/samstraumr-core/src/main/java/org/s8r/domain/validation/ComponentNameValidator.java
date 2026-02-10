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

import java.util.regex.Pattern;

import org.s8r.domain.exception.InvalidComponentNameException;

/**
 * Utility class for validating component names. Ensures that component names meet specific criteria
 * for length and character content.
 */
public class ComponentNameValidator {
  // Minimum and maximum allowed length for component names
  private static final int MIN_NAME_LENGTH = 3;
  private static final int MAX_NAME_LENGTH = 100;

  // Pattern for valid component name characters
  private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\-\\.]+$");

  // Disallowed sequences in component names
  private static final String[] DISALLOWED_SEQUENCES = {"..", "--", "__", "//", "\\\\", "  "};

  // Reserved prefixes that cannot be used in component names
  private static final String[] RESERVED_PREFIXES = {
    "system.", "admin.", "root.", "global.", "internal."
  };

  /**
   * Validates a component name, checking for length, allowed characters, and disallowed sequences.
   * Throws an exception if the name is invalid.
   *
   * @param name The component name to validate
   * @throws InvalidComponentNameException if the name fails validation
   */
  public static void validateComponentName(String name) {
    // Check for null or empty name
    if (name == null) {
      throw new InvalidComponentNameException(
          "Component name cannot be null", "null", "Component name must be non-null");
    }

    if (name.trim().isEmpty()) {
      throw new InvalidComponentNameException(
          "Component name cannot be empty or just whitespace",
          name,
          "Component name must contain visible characters");
    }

    // Check length constraints
    if (name.length() < MIN_NAME_LENGTH) {
      throw new InvalidComponentNameException(
          "Component name is too short: " + name,
          name,
          "Component name must be at least " + MIN_NAME_LENGTH + " characters long");
    }

    if (name.length() > MAX_NAME_LENGTH) {
      throw new InvalidComponentNameException(
          "Component name is too long: " + name,
          name,
          "Component name must be at most " + MAX_NAME_LENGTH + " characters long");
    }

    // Check for valid characters using regex pattern
    if (!VALID_NAME_PATTERN.matcher(name).matches()) {
      throw new InvalidComponentNameException(
          "Component name contains illegal characters: " + name,
          name,
          "Component name can only contain letters, numbers, dots, underscores, and hyphens");
    }

    // Check for disallowed character sequences
    for (String disallowed : DISALLOWED_SEQUENCES) {
      if (name.contains(disallowed)) {
        throw new InvalidComponentNameException(
            "Component name contains disallowed sequence '" + disallowed + "': " + name,
            name,
            "Component name cannot contain the sequence '" + disallowed + "'");
      }
    }

    // Check for reserved prefixes
    for (String prefix : RESERVED_PREFIXES) {
      if (name.toLowerCase().startsWith(prefix.toLowerCase())) {
        throw new InvalidComponentNameException(
            "Component name uses reserved prefix '" + prefix + "': " + name,
            name,
            "Component name cannot start with the reserved prefix '" + prefix + "'");
      }
    }
  }

  /**
   * Checks if a component name is valid without throwing an exception.
   *
   * @param name The component name to validate
   * @return true if the name is valid, false otherwise
   */
  public static boolean isValidComponentName(String name) {
    try {
      validateComponentName(name);
      return true;
    } catch (InvalidComponentNameException e) {
      return false;
    }
  }

  /**
   * Gets the minimum allowed length for component names.
   *
   * @return The minimum allowed length
   */
  public static int getMinNameLength() {
    return MIN_NAME_LENGTH;
  }

  /**
   * Gets the maximum allowed length for component names.
   *
   * @return The maximum allowed length
   */
  public static int getMaxNameLength() {
    return MAX_NAME_LENGTH;
  }
}
