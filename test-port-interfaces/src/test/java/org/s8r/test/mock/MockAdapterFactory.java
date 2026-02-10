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

package org.s8r.test.mock;

import java.util.Map;

/**
 * Centralized factory for all mock adapters.
 *
 * <p>Provides lazy singleton instances of mock adapters for consistent test setup.
 * Use {@link #resetAll()} between tests to ensure isolation.
 *
 * <p>Usage:
 * <pre>{@code
 * MockSecurityAdapter security = MockAdapterFactory.security();
 * security.configure(Map.of("maxFailedAttempts", 3));
 * // ... run tests ...
 * MockAdapterFactory.resetAll();
 * }</pre>
 *
 * @see <a href="docs/architecture/decisions/0014-adopt-contract-first-testing-strategy.md">ADR-0014</a>
 */
public final class MockAdapterFactory {

  // Singleton instances (lazy initialized)
  private static MockSecurityAdapter security;
  private static MockNotificationAdapter notification;
  private static MockEventPublisherAdapter eventPublisher;
  private static MockConfigurationAdapter configuration;
  private static MockLoggerAdapter logger;
  private static MockPersistenceAdapter persistence;
  private static MockMessagingAdapter messaging;
  private static MockTaskExecutionAdapter taskExecution;
  private static MockValidationAdapter validation;
  private static MockDataFlowEventAdapter dataFlowEvent;

  private MockAdapterFactory() {
    // Utility class - no instantiation
  }

  // =========================================================================
  // Factory Methods (Singleton)
  // =========================================================================

  /** Returns singleton MockSecurityAdapter instance. */
  public static synchronized MockSecurityAdapter security() {
    if (security == null) {
      security = MockSecurityAdapter.createInstance();
    }
    return security;
  }

  /** Returns singleton MockNotificationAdapter instance. */
  public static synchronized MockNotificationAdapter notification() {
    if (notification == null) {
      notification = MockNotificationAdapter.createInstance();
    }
    return notification;
  }

  /** Returns singleton MockEventPublisherAdapter instance. */
  public static synchronized MockEventPublisherAdapter eventPublisher() {
    if (eventPublisher == null) {
      eventPublisher = new MockEventPublisherAdapter();
    }
    return eventPublisher;
  }

  /** Returns singleton MockConfigurationAdapter instance. */
  public static synchronized MockConfigurationAdapter configuration() {
    if (configuration == null) {
      configuration = new MockConfigurationAdapter();
    }
    return configuration;
  }

  /** Returns singleton MockLoggerAdapter instance. */
  public static synchronized MockLoggerAdapter logger() {
    if (logger == null) {
      logger = new MockLoggerAdapter();
    }
    return logger;
  }

  /** Returns singleton MockPersistenceAdapter instance. */
  public static synchronized MockPersistenceAdapter persistence() {
    if (persistence == null) {
      persistence = new MockPersistenceAdapter(logger());
    }
    return persistence;
  }

  /** Returns singleton MockMessagingAdapter instance. */
  public static synchronized MockMessagingAdapter messaging() {
    if (messaging == null) {
      messaging = new MockMessagingAdapter(logger());
    }
    return messaging;
  }

  /** Returns singleton MockTaskExecutionAdapter instance. */
  public static synchronized MockTaskExecutionAdapter taskExecution() {
    if (taskExecution == null) {
      taskExecution = new MockTaskExecutionAdapter();
    }
    return taskExecution;
  }

  /** Returns singleton MockValidationAdapter instance. */
  public static synchronized MockValidationAdapter validation() {
    if (validation == null) {
      validation = new MockValidationAdapter(logger());
    }
    return validation;
  }

  /** Returns singleton MockDataFlowEventAdapter instance. */
  public static synchronized MockDataFlowEventAdapter dataFlowEvent() {
    if (dataFlowEvent == null) {
      dataFlowEvent = new MockDataFlowEventAdapter();
    }
    return dataFlowEvent;
  }

  // =========================================================================
  // Non-Singleton Creators (for isolated tests)
  // =========================================================================

  /** Creates a new isolated MockLoggerAdapter instance. */
  public static MockLoggerAdapter createLogger() {
    return new MockLoggerAdapter();
  }

  /** Creates a new isolated MockPersistenceAdapter instance. */
  public static MockPersistenceAdapter createPersistence() {
    return new MockPersistenceAdapter(createLogger());
  }

  /** Creates a new isolated MockMessagingAdapter instance. */
  public static MockMessagingAdapter createMessaging() {
    return new MockMessagingAdapter(createLogger());
  }

  /** Creates a new isolated MockValidationAdapter instance. */
  public static MockValidationAdapter createValidation() {
    return new MockValidationAdapter(createLogger());
  }

  // =========================================================================
  // Lifecycle Management
  // =========================================================================

  /**
   * Configures all adapters that support configuration.
   *
   * @param settings Configuration map applied to supporting adapters
   */
  public static synchronized void configureAll(Map<String, Object> settings) {
    if (security != null) {
      security.configure(settings);
    }
    if (notification != null) {
      notification.configure(settings);
    }
    // Add other configurable adapters as needed
  }

  /**
   * Resets all singleton instances. Call between tests for isolation.
   *
   * <p>After calling this method, the next call to any factory method
   * will create a fresh instance.
   */
  public static synchronized void resetAll() {
    security = null;
    notification = null;
    eventPublisher = null;
    configuration = null;
    logger = null;
    persistence = null;
    messaging = null;
    taskExecution = null;
    validation = null;
    dataFlowEvent = null;
  }

  /**
   * Clears internal state of all adapters without destroying instances.
   *
   * <p>Use when you want to preserve adapter configuration but clear
   * recorded invocations and state.
   */
  public static synchronized void clearAll() {
    if (logger != null) {
      logger.clearLogEntries();
    }
    if (notification != null) {
      notification.clearNotifications();
    }
    if (configuration != null) {
      configuration.reset();
    }
    if (eventPublisher != null) {
      eventPublisher.clearEvents();
    }
    // Add other clearable adapters as needed
  }
}
