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
 * Licensed under the Mozilla Public License 2.0
 */
package org.s8r.test.util;

import org.s8r.component.Environment;

/**
 * Utility class to help with migration between different Environment API versions.
 *
 * <p>This utility helps bridge compatibility between the old getValue/setValue API and the new
 * getParameter/setParameter API in Environment.
 */
public class EnvironmentCompatUtil {

  /**
   * Gets a value from the environment, working with both old and new APIs.
   *
   * @param environment The environment to get the value from
   * @param key The key to get the value for
   * @return The value, or null if not found
   */
  public static String getValue(Environment environment, String key) {
    return environment.getParameter(key);
  }

  /**
   * Sets a value in the environment, working with both old and new APIs.
   *
   * @param environment The environment to set the value in
   * @param key The key to set
   * @param value The value to set
   */
  public static void setValue(Environment environment, String key, String value) {
    environment.setParameter(key, value);
  }

  /**
   * Tests if an environment has a particular value.
   *
   * @param environment The environment to test
   * @param key The key to test for
   * @param expectedValue The expected value
   * @return true if the environment has the expected value for the key
   */
  public static boolean hasValue(Environment environment, String key, String expectedValue) {
    String actualValue = getValue(environment, key);
    return expectedValue.equals(actualValue);
  }

  /**
   * Converts a value to a string safely.
   *
   * @param value The value to convert
   * @return The string representation, or null if value is null
   */
  public static String toString(Object value) {
    return value != null ? value.toString() : null;
  }
}
