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

import java.util.ArrayList;
import java.util.List;

import org.s8r.core.env.Environment;
import org.s8r.core.tube.identity.Identity;
import org.s8r.domain.identity.ComponentHierarchy;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.identity.IdentityConverter;
import org.s8r.tube.TubeIdentity;

/**
 * Adapter that bridges the new ComponentId class with the legacy TubeIdentity class.
 *
 * <p>This adapter provides backward compatibility during the transition to the simplified package
 * structure, allowing existing code to continue using TubeIdentity while new code can use the
 * simplified ComponentId class.
 *
 * <p>The adapter pattern facilitates a gradual migration without breaking existing functionality.
 */
public class LegacyIdentityAdapter implements IdentityConverter {

  /**
   * Converts a legacy TubeIdentity to the new ComponentId format.
   *
   * @param oldIdentity The TubeIdentity to convert
   * @return A new ComponentId object with the same properties
   */
  public static ComponentId toComponentId(TubeIdentity oldIdentity) {
    if (oldIdentity == null) {
      return null;
    }

    return ComponentId.fromValues(
        oldIdentity.getUniqueId(),
        oldIdentity.getReason(),
        new ArrayList<>(oldIdentity.getLineage()));
  }

  /**
   * Converts a new ComponentId to the legacy TubeIdentity format.
   *
   * @param componentId The ComponentId to convert
   * @param environment The environment (required for TubeIdentity creation)
   * @return A legacy TubeIdentity object with the same properties
   */
  public static TubeIdentity toLegacyIdentity(
      ComponentId componentId, org.s8r.tube.Environment environment) {
    if (componentId == null) {
      return null;
    }

    TubeIdentity legacyIdentity;

    boolean isAdam = componentId.getParentId() == null;
    if (isAdam) {
      legacyIdentity =
          TubeIdentity.createAdamIdentity(
              componentId.getReason(),
              (environment != null) ? environment : new org.s8r.tube.Environment());
    } else {
      // For non-Adam identities, we need a parent identity
      TubeIdentity parentLegacyIdentity = null;
      if (componentId.getParentId() != null) {
        ComponentId parentId = ComponentHierarchy.getParentId(componentId);
        parentLegacyIdentity = toLegacyIdentity(parentId, environment);
      }

      legacyIdentity =
          TubeIdentity.createChildIdentity(
              componentId.getReason(),
              (environment != null) ? environment : new org.s8r.tube.Environment(),
              parentLegacyIdentity);
    }

    // Copy lineage (skipping the first entry which is already set during creation)
    List<String> lineage = componentId.getLineage();
    for (int i = 1; i < lineage.size(); i++) {
      legacyIdentity.addToLineage(lineage.get(i));
    }

    return legacyIdentity;
  }

  /**
   * Converts a legacy tube Identity to the new Identity format.
   *
   * @param oldIdentity The Identity to convert
   * @return A new Identity object with the same properties
   */
  public static Identity toNewIdentity(TubeIdentity oldIdentity) {
    if (oldIdentity == null) {
      return null;
    }

    Identity newIdentity = new Identity(oldIdentity.getUniqueId(), oldIdentity.getReason());

    // Copy over environment context
    for (String key : oldIdentity.getEnvironmentContext().keySet()) {
      newIdentity.addEnvironmentContext(key, oldIdentity.getEnvironmentContext().get(key));
    }

    // Copy lineage
    for (String entry : oldIdentity.getLineage()) {
      newIdentity.addToLineage(entry);
    }

    return newIdentity;
  }

  /**
   * Converts an Environment to a legacy Environment.
   *
   * @param newEnvironment The new Environment to convert
   * @return A legacy Environment with the same properties
   */
  public static org.s8r.tube.Environment toLegacyEnvironment(Environment newEnvironment) {
    if (newEnvironment == null) {
      return null;
    }

    org.s8r.tube.Environment legacyEnvironment = new org.s8r.tube.Environment();

    // Copy over parameters - this assumes both Environment classes have compatible APIs
    for (String key : newEnvironment.getParameterKeys()) {
      legacyEnvironment.setParameter(key, newEnvironment.getParameter(key));
    }

    return legacyEnvironment;
  }

  /**
   * Converts a legacy Environment to the new Environment format.
   *
   * @param legacyEnvironment The legacy Environment to convert
   * @return A new Environment with the same properties
   */
  public static Environment toNewEnvironment(org.s8r.tube.Environment legacyEnvironment) {
    if (legacyEnvironment == null) {
      return null;
    }

    Environment newEnvironment = new Environment();

    // Copy over parameters
    for (String key : legacyEnvironment.getParameterKeys()) {
      newEnvironment.setParameter(key, legacyEnvironment.getParameter(key));
    }

    return newEnvironment;
  }

  @Override
  public ComponentId toComponentId(
      String legacyId, String legacyReason, List<String> legacyLineage) {
    return ComponentId.fromValues(legacyId, legacyReason, new ArrayList<>(legacyLineage));
  }

  @Override
  public String toLegacyIdString(ComponentId componentId) {
    if (componentId == null) {
      return null;
    }
    return componentId.getIdString();
  }
}
