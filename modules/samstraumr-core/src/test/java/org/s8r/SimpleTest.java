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

package org.s8r;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Simple test to verify test execution works correctly. */
@DisplayName("Simple Verification Test")
public class SimpleTest {

  @Test
  @DisplayName("Test basic assertions")
  public void testBasicAssertions() {
    // Given
    String message = "Test execution works!";

    // When
    String result = message;

    // Then
    assertEquals(message, result, "Strings should be equal");
    assertTrue(result.contains("works"), "Result should contain 'works'");
    assertFalse(result.isEmpty(), "Result should not be empty");

    System.out.println("Simple test executed successfully!");
  }

  @Test
  @DisplayName("Test string operations")
  public void testStringOperations() {
    // Given
    String str1 = "Hello";
    String str2 = "World";

    // When
    String result = str1 + " " + str2;

    // Then
    assertEquals("Hello World", result, "Concatenation should work");
    assertEquals(11, result.length(), "Length should be 11");

    System.out.println("String test executed successfully!");
  }

  @Test
  @DisplayName("Test numeric operations")
  public void testNumericOperations() {
    // Given
    int a = 5;
    int b = 7;

    // When
    int sum = a + b;
    int product = a * b;

    // Then
    assertEquals(12, sum, "Addition should work");
    assertEquals(35, product, "Multiplication should work");

    System.out.println("Numeric test executed successfully!");
  }
}
