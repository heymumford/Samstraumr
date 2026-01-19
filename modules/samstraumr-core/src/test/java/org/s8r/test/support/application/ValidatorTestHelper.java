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

package org.s8r.test.support.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Helper for testing validators without implementation coupling.
 *
 * <p>Provides contract testing utilities for validators:
 * - Test that validators accept valid input
 * - Test that validators reject invalid input with proper exceptions
 * - Test that validators don't silently fail (Bug #5 detection)
 * - Detect missing validation rules
 *
 * <p>Uses function-based validation to avoid coupling to concrete validator implementations.
 *
 * @param <T> Input type to validate
 * @param <R> Result type from validator
 */
public class ValidatorTestHelper<T, R> {
  private final Function<T, R> validator;
  private final List<T> validCases = new ArrayList<>();
  private final List<T> invalidCases = new ArrayList<>();

  /**
   * Creates validator test helper.
   *
   * @param validator Function implementing validation logic
   */
  public ValidatorTestHelper(Function<T, R> validator) {
    this.validator = validator;
  }

  /**
   * Registers a case that should validate successfully.
   *
   * @param validCase Input that should pass validation
   * @return This helper (for chaining)
   */
  public ValidatorTestHelper<T, R> withValidCase(T validCase) {
    validCases.add(validCase);
    return this;
  }

  /**
   * Registers multiple valid cases.
   *
   * @param cases Inputs that should pass validation
   * @return This helper (for chaining)
   */
  public ValidatorTestHelper<T, R> withValidCases(T... cases) {
    for (T c : cases) {
      validCases.add(c);
    }
    return this;
  }

  /**
   * Registers a case that should fail validation.
   *
   * @param invalidCase Input that should fail validation
   * @return This helper (for chaining)
   */
  public ValidatorTestHelper<T, R> withInvalidCase(T invalidCase) {
    invalidCases.add(invalidCase);
    return this;
  }

  /**
   * Registers multiple invalid cases.
   *
   * @param cases Inputs that should fail validation
   * @return This helper (for chaining)
   */
  public ValidatorTestHelper<T, R> withInvalidCases(T... cases) {
    for (T c : cases) {
      invalidCases.add(c);
    }
    return this;
  }

  /**
   * Tests that all valid cases pass validation.
   *
   * @throws AssertionError if any valid case fails validation
   */
  public void assertValidCasesPass() {
    for (T validCase : validCases) {
      try {
        R result = validator.apply(validCase);
        // Validation succeeded - expected
        if (result == null) {
          throw new AssertionError("Validator returned null for valid case: " + validCase);
        }
      } catch (Exception e) {
        throw new AssertionError("Valid case failed validation: " + validCase, e);
      }
    }
  }

  /**
   * Tests that all invalid cases fail validation with exception.
   *
   * @throws AssertionError if any invalid case passes validation (detects missing validation)
   */
  public void assertInvalidCasesFail() {
    for (T invalidCase : invalidCases) {
      try {
        R result = validator.apply(invalidCase);
        // If we get here, validation didn't throw exception (silent failure - Bug #5)
        throw new AssertionError(
            "Invalid case did not fail validation (silent failure detected): "
                + invalidCase
                + " returned: "
                + result);
      } catch (Exception e) {
        // Expected: validation should throw exception
        if (e instanceof AssertionError) {
          throw e; // Re-throw our assertion errors
        }
        // Other exceptions are expected validation failures
      }
    }
  }

  /**
   * Tests that validator rejects invalid cases with specific exception type.
   *
   * @param expectedExceptionType Expected exception class
   * @throws AssertionError if invalid cases pass or throw different exception
   */
  public void assertInvalidCasesFailWith(Class<? extends Exception> expectedExceptionType) {
    for (T invalidCase : invalidCases) {
      try {
        validator.apply(invalidCase);
        // No exception thrown - silent failure
        throw new AssertionError(
            "Invalid case did not fail (silent failure detected): " + invalidCase);
      } catch (Exception e) {
        if (!expectedExceptionType.isInstance(e)) {
          throw new AssertionError(
              "Expected exception "
                  + expectedExceptionType.getSimpleName()
                  + " but got "
                  + e.getClass().getSimpleName()
                  + " for case: "
                  + invalidCase,
              e);
        }
      }
    }
  }

  /**
   * Tests that validator allows custom validation logic on each result.
   *
   * @param validator Custom validator for results
   * @throws AssertionError if any result fails custom validation
   */
  public void assertResultsMatch(Consumer<R> validator) {
    for (T validCase : validCases) {
      R result = this.validator.apply(validCase);
      try {
        validator.accept(result);
      } catch (Exception e) {
        throw new AssertionError("Result validation failed for: " + validCase, e);
      }
    }
  }

  /**
   * Gets unmodifiable list of registered valid cases.
   *
   * @return Valid cases
   */
  public List<T> getValidCases() {
    return Collections.unmodifiableList(validCases);
  }

  /**
   * Gets unmodifiable list of registered invalid cases.
   *
   * @return Invalid cases
   */
  public List<T> getInvalidCases() {
    return Collections.unmodifiableList(invalidCases);
  }

  /**
   * Returns validator coverage summary.
   *
   * @return Human-readable test coverage report
   */
  public String getCoverageSummary() {
    return String.format(
        "ValidatorTestHelper: %d valid cases, %d invalid cases", validCases.size(), invalidCases.size());
  }

  /**
   * Full validation test: valid cases pass, invalid cases fail.
   *
   * @throws AssertionError if validation contract violated
   */
  public void validate() {
    assertValidCasesPass();
    assertInvalidCasesFail();
  }
}
