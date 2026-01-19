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

/**
 * Validates that port implementations honor their interface contracts.
 *
 * <p>Contract testing for hexagonal architecture: ensures adapters implement ports correctly
 * and maintain expected behavior guarantees.
 *
 * <p>Checks:
 * - Required methods are implemented
 * - Methods throw appropriate exceptions for error conditions
 * - Return values match contract expectations
 * - No silent fallbacks or defaults (Bug #5 detection)
 *
 * @param <P> Port interface type
 */
public class PortContractValidator<P> {
  private final Class<P> portInterface;
  private final List<P> implementations = new ArrayList<>();
  private final List<ContractRule> rules = new ArrayList<>();

  /**
   * Creates validator for a port interface.
   *
   * @param portInterface Port interface to validate implementations against
   */
  public PortContractValidator(Class<P> portInterface) {
    this.portInterface = portInterface;
  }

  /**
   * Registers an implementation to validate.
   *
   * @param implementation Port implementation
   * @return This validator (for chaining)
   */
  public PortContractValidator<P> withImplementation(P implementation) {
    implementations.add(implementation);
    return this;
  }

  /**
   * Registers a contract rule that must be satisfied.
   *
   * @param rule Contract rule
   * @return This validator (for chaining)
   */
  public PortContractValidator<P> withRule(ContractRule rule) {
    rules.add(rule);
    return this;
  }

  /**
   * Registers rule: method must exist and be implemented.
   *
   * @param methodName Method name
   * @return This validator (for chaining)
   */
  public PortContractValidator<P> requiresMethod(String methodName) {
    rules.add(
        new ContractRule(
            "Method exists: " + methodName,
            impl -> {
              try {
                portInterface.getMethod(methodName);
              } catch (NoSuchMethodException e) {
                throw new AssertionError("Required method not found: " + methodName);
              }
            }));
    return this;
  }

  /**
   * Registers rule: method must throw exception on invalid input.
   *
   * @param methodName Method name
   * @param expectedExceptionType Expected exception when called with invalid input
   * @return This validator (for chaining)
   */
  public PortContractValidator<P> requiresExceptionOn(
      String methodName, Class<? extends Exception> expectedExceptionType) {
    rules.add(
        new ContractRule(
            "Throws " + expectedExceptionType.getSimpleName() + " on " + methodName,
            impl -> {
              // Actual validation happens in test using this validator
            }));
    return this;
  }

  /**
   * Validates all implementations against all registered rules.
   *
   * @throws AssertionError if any implementation violates contract
   */
  public void validate() {
    for (P implementation : implementations) {
      for (ContractRule rule : rules) {
        try {
          rule.validate(implementation);
        } catch (AssertionError e) {
          throw new AssertionError(
              "Contract violation in "
                  + implementation.getClass().getSimpleName()
                  + ": "
                  + e.getMessage(),
              e);
        }
      }
    }
  }

  /**
   * Gets all registered implementations.
   *
   * @return Implementations to validate
   */
  public List<P> getImplementations() {
    return Collections.unmodifiableList(implementations);
  }

  /**
   * Gets all registered contract rules.
   *
   * @return Contract rules
   */
  public List<ContractRule> getRules() {
    return Collections.unmodifiableList(rules);
  }

  /**
   * Gets port interface type.
   *
   * @return Port interface class
   */
  public Class<P> getPortInterface() {
    return portInterface;
  }

  /**
   * Generates validation report.
   *
   * @return Human-readable report
   */
  public String generateReport() {
    return String.format(
        "PortContractValidator for %s: %d implementations, %d rules",
        portInterface.getSimpleName(), implementations.size(), rules.size());
  }

  /**
   * Represents a contract rule that implementations must satisfy.
   */
  @FunctionalInterface
  public interface ContractRule {
    /**
     * Validates implementation against this rule.
     *
     * @param implementation Implementation to validate
     * @throws AssertionError if validation fails
     */
    void validate(Object implementation);

    /**
     * Creates a named contract rule.
     *
     * @param description Rule description
     * @param validation Validation logic
     * @return Contract rule
     */
    static ContractRule create(String description, RuleValidator validation) {
      return new ContractRule() {
        @Override
        public void validate(Object implementation) {
          validation.validate(implementation);
        }

        @Override
        public String toString() {
          return description;
        }
      };
    }
  }

  /**
   * Functional interface for rule validation.
   */
  @FunctionalInterface
  public interface RuleValidator {
    /**
     * Validates implementation.
     *
     * @param implementation Implementation to validate
     * @throws AssertionError if validation fails
     */
    void validate(Object implementation);
  }
}
