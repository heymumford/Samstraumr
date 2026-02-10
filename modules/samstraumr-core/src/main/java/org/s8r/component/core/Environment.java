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

package org.s8r.component.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple environment implementation for the core component layer. This is a lightweight version of
 * the full Environment class in org.s8r.component.
 */
public class Environment {

  private final Map<String, String> parameters;

  /** Creates a new Environment with empty parameters. */
  public Environment() {
    this.parameters = new ConcurrentHashMap<>();
  }

  /**
   * Creates a new Environment with the specified parameters.
   *
   * @param parameters The initial parameters
   */
  public Environment(Map<String, String> parameters) {
    this.parameters = new ConcurrentHashMap<>();
    if (parameters != null) {
      this.parameters.putAll(parameters);
    }
  }

  /**
   * Gets all parameter keys in this environment.
   *
   * @return A set of parameter keys
   */
  public Set<String> getParameterKeys() {
    return parameters.keySet();
  }

  /**
   * Gets a specific parameter value.
   *
   * @param key The parameter key
   * @return The parameter value, or null if not found
   */
  public String getParameter(String key) {
    return parameters.get(key);
  }

  /**
   * Sets a parameter value in the environment.
   *
   * @param key The parameter key
   * @param value The parameter value
   */
  public void setParameter(String key, String value) {
    if (key != null && value != null) {
      parameters.put(key, value);
    }
  }

  /**
   * Creates a copy of this environment.
   *
   * @return A new Environment with the same parameters
   */
  public Environment copy() {
    return new Environment(new HashMap<>(parameters));
  }

  /**
   * Gets all parameters as a map.
   *
   * @return An unmodifiable view of the parameters
   */
  public Map<String, String> getParameters() {
    return Map.copyOf(parameters);
  }

  @Override
  public String toString() {
    return "Environment{parameters=" + parameters + "}";
  }
}
