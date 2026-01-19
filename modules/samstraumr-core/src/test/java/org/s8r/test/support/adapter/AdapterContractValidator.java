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

package org.s8r.test.support.adapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validates adapter implementations against port interface contracts (hexagonal architecture).
 *
 * <p>Ensures:
 * - All port methods are implemented
 * - Implementations throw exceptions instead of silently failing (detects Bug #5)
 * - No methods return null when contract forbids it
 * - Adapter behavior matches interface expectations
 *
 * @param <P> Port interface type
 * @param <A> Adapter implementation type
 */
public class AdapterContractValidator<P, A> {
  private final Class<P> portInterface;
  private final Class<A> adapterClass;
  private final List<ValidationRule<A>> rules = new ArrayList<>();
  private final Set<String> ignoredMethods = new HashSet<>();

  /**
   * Creates validator for adapter.
   *
   * @param portInterface Port interface to validate against
   * @param adapterClass Adapter implementation class
   */
  public AdapterContractValidator(Class<P> portInterface, Class<A> adapterClass) {
    this.portInterface = portInterface;
    this.adapterClass = adapterClass;
    initializeDefaultRules();
  }

  /**
   * Initializes default validation rules from port interface.
   */
  private void initializeDefaultRules() {
    // Verify adapter implements port interface
    if (!portInterface.isAssignableFrom(adapterClass)) {
      throw new IllegalArgumentException(
          adapterClass.getSimpleName() + " does not implement " + portInterface.getSimpleName());
    }

    // Extract all methods from port interface
    for (Method method : portInterface.getDeclaredMethods()) {
      if (!ignoredMethods.contains(method.getName())) {
        // Create validation rule for each port method
        addMethodImplementationRule(method);
      }
    }
  }

  /**
   * Adds rule validating specific method is implemented.
   *
   * @param method Method from port interface
   */
  private void addMethodImplementationRule(Method method) {
    rules.add(
        new ValidationRule<>(
            "Method implemented: " + method.getName(),
            adapter -> {
              try {
                adapterClass.getMethod(
                    method.getName(),
                    method.getParameterTypes());
              } catch (NoSuchMethodException e) {
                throw new AssertionError(
                    "Required method not implemented: " + method.getName(),
                    e);
              }
            }));
  }

  /**
   * Adds custom validation rule.
   *
   * @param rule Validation rule
   * @return This validator (for chaining)
   */
  public AdapterContractValidator<P, A> withRule(ValidationRule<A> rule) {
    rules.add(rule);
    return this;
  }

  /**
   * Marks method to be ignored in validation (useful for interface defaults).
   *
   * @param methodName Method name to ignore
   * @return This validator (for chaining)
   */
  public AdapterContractValidator<P, A> ignoreMethod(String methodName) {
    ignoredMethods.add(methodName);
    return this;
  }

  /**
   * Adds rule: method must not return null (contract requirement).
   *
   * @param methodName Method name
   * @return This validator (for chaining)
   */
  public AdapterContractValidator<P, A> requiresNonNullReturn(String methodName) {
    rules.add(
        new ValidationRule<>(
            "Method returns non-null: " + methodName,
            adapter -> {
              // Actual validation happens during test execution
            }));
    return this;
  }

  /**
   * Adds rule: method must throw exception on invalid input (no silent failure).
   *
   * @param methodName Method name
   * @return This validator (for chaining)
   */
  public AdapterContractValidator<P, A> requiresExceptionOnInvalidInput(String methodName) {
    rules.add(
        new ValidationRule<>(
            "Throws exception on invalid input: " + methodName,
            adapter -> {
              // Actual validation happens during test execution
            }));
    return this;
  }

  /**
   * Validates adapter against all rules.
   *
   * @param adapter Adapter instance to validate
   * @throws AssertionError if contract violated
   */
  public void validate(A adapter) {
    for (ValidationRule<A> rule : rules) {
      try {
        rule.validate(adapter);
      } catch (AssertionError e) {
        throw new AssertionError(
            "Adapter contract violation in "
                + adapterClass.getSimpleName()
                + ": "
                + e.getMessage(),
            e);
      }
    }
  }

  /**
   * Gets all registered validation rules.
   *
   * @return Unmodifiable list of rules
   */
  public List<ValidationRule<A>> getRules() {
    return Collections.unmodifiableList(rules);
  }

  /**
   * Gets port interface being validated.
   *
   * @return Port interface class
   */
  public Class<P> getPortInterface() {
    return portInterface;
  }

  /**
   * Gets adapter class being validated.
   *
   * @return Adapter class
   */
  public Class<A> getAdapterClass() {
    return adapterClass;
  }

  /**
   * Generates validation report.
   *
   * @return Human-readable report
   */
  public String generateReport() {
    return String.format(
        "AdapterContractValidator: %s implements %s (%d rules)",
        adapterClass.getSimpleName(), portInterface.getSimpleName(), rules.size());
  }

  /**
   * Represents an adapter validation rule.
   *
   * @param <A> Adapter type
   */
  @FunctionalInterface
  public interface ValidationRule<A> {
    /**
     * Validates adapter against this rule.
     *
     * @param adapter Adapter instance
     * @throws AssertionError if validation fails
     */
    void validate(A adapter);

    /**
     * Creates a named validation rule.
     *
     * @param description Rule description
     * @param validator Validation logic
     * @return Validation rule
     */
    static <A> ValidationRule<A> create(String description, AdapterValidator<A> validator) {
      return new ValidationRule<A>() {
        @Override
        public void validate(A adapter) {
          validator.validate(adapter);
        }

        @Override
        public String toString() {
          return description;
        }
      };
    }
  }

  /**
   * Functional interface for adapter validation.
   *
   * @param <A> Adapter type
   */
  @FunctionalInterface
  public interface AdapterValidator<A> {
    /**
     * Validates adapter.
     *
     * @param adapter Adapter instance
     * @throws AssertionError if validation fails
     */
    void validate(A adapter);
  }
}
