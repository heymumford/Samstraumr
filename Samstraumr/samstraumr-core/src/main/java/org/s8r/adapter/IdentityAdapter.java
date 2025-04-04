package org.s8r.adapter;

import org.s8r.core.env.Environment;
import org.s8r.core.tube.identity.Identity;
import org.samstraumr.tube.TubeIdentity;

/**
 * Adapter that bridges the new Identity class with the legacy TubeIdentity class.
 *
 * <p>This adapter provides backward compatibility during the transition to the simplified package
 * structure, allowing existing code to continue using TubeIdentity while new code can use the
 * simplified Identity class.
 *
 * <p>The adapter pattern facilitates a gradual migration without breaking existing functionality.
 */
public class IdentityAdapter {

  private IdentityAdapter() {
    // Private constructor to prevent instantiation
  }

  /**
   * Converts a legacy TubeIdentity to the new Identity format.
   *
   * @param oldIdentity The TubeIdentity to convert
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

    // Handle parent and children - this may require recursive conversion
    // For simplicity in this example, we skip the recursive aspects

    return newIdentity;
  }

  /**
   * Converts a new Identity to the legacy TubeIdentity format.
   *
   * @param newIdentity The Identity to convert
   * @param environment The environment (required for TubeIdentity creation)
   * @return A legacy TubeIdentity object with the same properties
   */
  public static TubeIdentity toLegacyIdentity(Identity newIdentity, org.samstraumr.tube.Environment environment) {
    if (newIdentity == null) {
      return null;
    }

    TubeIdentity legacyIdentity;
    
    if (newIdentity.isAdamComponent()) {
      legacyIdentity = TubeIdentity.createAdamIdentity(
          newIdentity.getReason(), 
          (environment != null) ? environment : new org.samstraumr.tube.Environment());
    } else {
      // For non-Adam identities, we need a parent identity
      TubeIdentity parentLegacyIdentity = null;
      if (newIdentity.getParentIdentity() != null) {
        parentLegacyIdentity = toLegacyIdentity(
            newIdentity.getParentIdentity(), 
            environment);
      }
      
      legacyIdentity = TubeIdentity.createChildIdentity(
          newIdentity.getReason(),
          (environment != null) ? environment : new org.samstraumr.tube.Environment(),
          parentLegacyIdentity);
    }

    // Copy lineage (skipping the first entry which is already set during creation)
    for (int i = 1; i < newIdentity.getLineage().size(); i++) {
      legacyIdentity.addToLineage(newIdentity.getLineage().get(i));
    }

    // Copy environment context
    for (String key : newIdentity.getEnvironmentContext().keySet()) {
      legacyIdentity.addEnvironmentContext(key, newIdentity.getEnvironmentContext().get(key));
    }

    return legacyIdentity;
  }

  /**
   * Converts an Environment to a legacy Environment.
   *
   * @param newEnvironment The new Environment to convert
   * @return A legacy Environment with the same properties
   */
  public static org.samstraumr.tube.Environment toLegacyEnvironment(Environment newEnvironment) {
    if (newEnvironment == null) {
      return null;
    }

    org.samstraumr.tube.Environment legacyEnvironment = new org.samstraumr.tube.Environment();

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
  public static Environment toNewEnvironment(org.samstraumr.tube.Environment legacyEnvironment) {
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
}