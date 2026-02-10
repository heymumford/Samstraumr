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

package org.s8r.adapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for domain adapters used in tests.
 *
 * <p>This class provides utility methods for handling type conversion between domain and flattened
 * component structures in tests. It complements the ComponentTypeAdapter with additional
 * test-specific functionality.
 */
public class DomainAdapterUtil {

  private static final Map<Object, Object> adaptedObjects = new HashMap<>();

  /**
   * Registers an adapted object pair.
   *
   * @param source The source object
   * @param target The adapted target object
   */
  public static void registerAdapted(Object source, Object target) {
    adaptedObjects.put(source, target);
  }

  /**
   * Gets an adapted object.
   *
   * @param <T> The target type
   * @param source The source object
   * @param targetClass The target class
   * @return The adapted object, or null if not found
   */
  @SuppressWarnings("unchecked")
  public static <T> T getAdapted(Object source, Class<T> targetClass) {
    Object target = adaptedObjects.get(source);
    if (target != null && targetClass.isInstance(target)) {
      return (T) target;
    }
    return null;
  }

  /**
   * Creates a test wrapper for a component that will pass instanceof checks.
   *
   * @param component The component to wrap
   * @return A test wrapper that appears as a domain component
   */
  public static org.s8r.domain.component.Component createTestWrapper(
      org.s8r.component.Component component) {
    org.s8r.domain.component.Component wrapper = ComponentTypeAdapter.toDomainComponent(component);
    registerAdapted(component, wrapper);
    return wrapper;
  }

  /**
   * Creates a domain component from a legacy component.
   *
   * @param component The legacy component
   * @return A new domain component
   */
  public static org.s8r.domain.component.Component createDomainComponent(
      org.s8r.component.Component component) {
    return ComponentTypeAdapter.toDomainComponent(component);
  }

  /**
   * Creates a legacy component from a domain component.
   *
   * @param domainComponent The domain component
   * @return A new legacy component
   */
  public static org.s8r.component.Component createLegacyComponent(
      org.s8r.domain.component.Component domainComponent) {
    return ComponentTypeAdapter.fromDomainComponent(domainComponent);
  }
}
