/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/**
 * Common adapter initialization patterns
 * 
 * This file contains utility functions to standardize the creation
 * and initialization of port adapter instances across Karate tests.
 */

/**
 * Creates a cache adapter instance
 * @returns {Object} A new cache adapter instance
 */
function createCacheAdapter() {
  return Java.type('org.s8r.infrastructure.cache.EnhancedInMemoryCacheAdapter').createInstance();
}

/**
 * Creates a configuration adapter instance
 * @returns {Object} A new configuration adapter instance
 */
function createConfigAdapter() {
  return Java.type('org.s8r.infrastructure.config.PropertiesConfigurationAdapter').createInstance();
}

/**
 * Creates a configuration adapter initialized with the given properties
 * @param {Object} properties - The properties to initialize the adapter with
 * @returns {Object} A new configuration adapter instance initialized with the given properties
 */
function createConfigAdapterWithProps(properties) {
  return Java.type('org.s8r.infrastructure.config.PropertiesConfigurationAdapter').createWithProperties(properties);
}

/**
 * Creates a validation adapter instance
 * @returns {Object} A new validation adapter instance
 */
function createValidationAdapter() {
  return Java.type('org.s8r.infrastructure.validation.ValidationAdapter').createInstance();
}

/**
 * Creates a filesystem adapter instance
 * @returns {Object} A new filesystem adapter instance
 */
function createFileSystemAdapter() {
  return Java.type('org.s8r.infrastructure.filesystem.BufferedFileSystemAdapter').createInstance();
}

/**
 * Creates a task execution adapter instance
 * @param {number} poolSize - Optional thread pool size
 * @returns {Object} A new task execution adapter instance
 */
function createTaskExecutionAdapter(poolSize) {
  if (poolSize && typeof poolSize === 'number') {
    return Java.type('org.s8r.infrastructure.task.ThreadPoolTaskExecutionAdapter').createInstance(poolSize);
  }
  return Java.type('org.s8r.infrastructure.task.ThreadPoolTaskExecutionAdapter').createInstance();
}

/**
 * Creates a secure task adapter instance with additional security features
 * @returns {Object} A new secure task adapter instance
 */
function createSecureTaskAdapter() {
  return Java.type('org.s8r.infrastructure.task.SecureTaskExecutionAdapter').createInstance();
}

/**
 * Creates a notification adapter instance for sending notifications
 * @returns {Object} A new notification adapter instance
 */
function createNotificationAdapter() {
  return Java.type('org.s8r.infrastructure.notification.InMemoryNotificationAdapter').createInstance();
}

/**
 * Creates an event publisher adapter instance
 * @returns {Object} A new event publisher adapter instance
 */
function createEventPublisherAdapter() {
  return Java.type('org.s8r.infrastructure.event.InMemoryEventPublisherAdapter').createInstance();
}

// Export all functions for use in Karate feature files