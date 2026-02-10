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

/**
 * Test utility adapter to handle type conversion between domain and flattened component structures.
 *
 * <p>This class provides static conversion methods that can be used in tests to resolve type
 * incompatibilities between the legacy component packages and domain component packages.
 */
public class ComponentTypeAdapter {

  /**
   * Converts a domain component to a flattened component.
   *
   * @param domainComponent The domain component to convert
   * @return A flattened component
   */
  public static org.s8r.component.Component fromDomainComponent(
      org.s8r.domain.component.Component domainComponent) {
    if (domainComponent == null) {
      return null;
    }

    // Create a new component with the domain component's ID
    org.s8r.component.Environment env = new org.s8r.component.Environment();
    return org.s8r.component.Component.create(domainComponent.getId().getReason(), env);
  }

  /**
   * Converts a component port to a flattened component.
   *
   * @param componentPort The component port to convert
   * @return A flattened component
   */
  public static org.s8r.component.Component fromDomainComponent(
      org.s8r.domain.component.port.ComponentPort componentPort) {
    if (componentPort == null) {
      return null;
    }

    // Create a new component with the component port's ID
    org.s8r.component.Environment env = new org.s8r.component.Environment();
    return org.s8r.component.Component.create(componentPort.getId().getReason(), env);
  }

  public static org.s8r.component.Component fromComponentPort(
      org.s8r.domain.component.port.ComponentPort componentPort) {
    return fromDomainComponent(componentPort);
  }

  /**
   * Converts a flattened component to a domain component.
   *
   * @param component The flattened component to convert
   * @return A domain component
   */
  public static org.s8r.domain.component.Component toDomainComponent(
      org.s8r.component.Component component) {
    if (component == null) {
      return null;
    }

    // Create a domain component with the component's reason
    return org.s8r.domain.component.Component.create(
        org.s8r.domain.identity.ComponentId.create(component.getReason()));
  }

  /**
   * Wraps a flattened component for use in tests that expect a domain component.
   *
   * <p>This method provides a minimal wrapper with essential functionality to pass tests, but may
   * not implement all domain component functionality.
   *
   * @param component The flattened component to wrap
   * @return A domain component wrapper
   */
  public static org.s8r.domain.component.Component wrap(org.s8r.component.Component component) {
    return toDomainComponent(component);
  }

  /**
   * Unwraps a domain component to a flattened component for use in tests.
   *
   * @param domainComponent The domain component to unwrap
   * @return A flattened component
   */
  public static org.s8r.component.Component unwrap(
      org.s8r.domain.component.Component domainComponent) {
    return fromDomainComponent(domainComponent);
  }
}
