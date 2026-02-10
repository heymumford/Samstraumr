/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

package org.s8r.architecture.util;

import java.util.*;
import java.util.function.Supplier;

import org.s8r.domain.component.Component;
import org.s8r.domain.exception.*;
import org.s8r.domain.identity.ComponentId;

/**
 * Utility class for testing error handling patterns and strategies. Provides helper methods for
 * simulating errors and verifying proper error handling.
 */
public class ErrorHandlingTestUtils {

  /**
   * Creates a component that throws a specific exception when activated.
   *
   * @param name Component name
   * @param exceptionSupplier Supplier for the exception to throw
   * @return A component that will throw the specified exception when activated
   */
  public static Component createFailingComponent(
      String name, Supplier<RuntimeException> exceptionSupplier) {
    // Create normal component
    Component component = Component.create(ComponentId.create(name));

    // Intercept the activation to throw an exception
    return new ExceptionThrowingComponent(name, exceptionSupplier);
  }

  /**
   * Executes an operation with retry logic and returns the result.
   *
   * @param <T> The type of result expected
   * @param operation The operation to execute
   * @param maxRetries Maximum number of retries
   * @param retryDelay Delay between retries in milliseconds
   * @return The result of the operation if successful
   * @throws Exception if the operation fails after all retries
   */
  public static <T> T executeWithRetry(Supplier<T> operation, int maxRetries, long retryDelay)
      throws Exception {
    Exception lastException = null;

    for (int attempt = 0; attempt <= maxRetries; attempt++) {
      try {
        return operation.get();
      } catch (Exception e) {
        lastException = e;
        if (attempt < maxRetries) {
          Thread.sleep(retryDelay);
        }
      }
    }

    throw lastException;
  }

  /**
   * Creates an exception hierarchy for testing error classification.
   *
   * @return Map of error categories to lists of exception types
   */
  public static Map<String, List<Class<? extends Exception>>> createErrorClassification() {
    Map<String, List<Class<? extends Exception>>> classification = new HashMap<>();

    // Infrastructure errors
    classification.put(
        "infrastructure",
        Arrays.asList(
            java.io.IOException.class,
            java.net.ConnectException.class,
            java.sql.SQLException.class));

    // Domain errors
    classification.put(
        "domain",
        Arrays.asList(
            ComponentException.class,
            ComponentInitializationException.class,
            ComponentNotFoundException.class,
            DuplicateComponentException.class,
            InvalidOperationException.class,
            InvalidStateTransitionException.class));

    // Application errors
    classification.put(
        "application",
        Arrays.asList(
            IllegalArgumentException.class,
            IllegalStateException.class,
            UnsupportedOperationException.class));

    return classification;
  }

  /**
   * A component implementation that throws exceptions on certain operations. We implement Component
   * directly rather than delegating to avoid wrapper issues.
   */
  private static class ExceptionThrowingComponent extends Component {
    private final Supplier<RuntimeException> exceptionSupplier;

    public ExceptionThrowingComponent(String name, Supplier<RuntimeException> exceptionSupplier) {
      super(ComponentId.create(name));
      this.exceptionSupplier = exceptionSupplier;
    }

    @Override
    public void activate() {
      throw exceptionSupplier.get();
    }

    @Override
    public void deactivate() {
      throw exceptionSupplier.get();
    }

    @Override
    public void terminate() {
      throw exceptionSupplier.get();
    }

    @Override
    public void publishData(String channel, Map<String, Object> data) {
      throw exceptionSupplier.get();
    }

    @Override
    public void publishData(String channel, String key, Object value) {
      throw exceptionSupplier.get();
    }
  }
}
