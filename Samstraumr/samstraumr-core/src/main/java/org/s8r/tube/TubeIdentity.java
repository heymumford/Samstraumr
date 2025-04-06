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

package org.s8r.tube;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Represents the identity of a Tube based on the biological continuity model.
 *
 * <p>The TubeIdentity class encapsulates all aspects of a tube's substrate identity:
 *
 * <ul>
 *   <li>Universally Unique Identifier (UUID)
 *   <li>Creation timestamp (conception time)
 *   <li>Lineage information (parent-child relationships)
 *   <li>Environmental context at creation
 *   <li>Creation reason and metadata
 * </ul>
 *
 * <p>Once created, the core identity properties are immutable, ensuring a stable foundation for a
 * tube's existence throughout its lifecycle.
 */
public class TubeIdentity {
  private final String uniqueId;
  private final Instant conceptionTime;
  private final String reason;
  private final Map<String, String> environmentContext;
  private final List<String> lineage;
  private TubeIdentity parentIdentity;
  private List<TubeIdentity> descendants;
  private String hierarchicalAddress;
  private boolean isAdamTube;

  /**
   * Creates a new TubeIdentity with the specified properties.
   *
   * @param uniqueId The unique identifier for this tube
   * @param reason The reason for creating this tube
   */
  public TubeIdentity(String uniqueId, String reason) {
    this.uniqueId = uniqueId;
    this.conceptionTime = Instant.now();
    this.reason = reason;
    this.environmentContext = new HashMap<>();
    this.lineage = new CopyOnWriteArrayList<>();
    this.lineage.add(reason);
    this.descendants = new ArrayList<>();
    this.isAdamTube = false;
    this.hierarchicalAddress = "T<" + uniqueId.substring(0, 8) + ">";
  }

  /**
   * Creates a new TubeIdentity with a randomly generated UUID.
   *
   * @param reason The reason for creating this tube
   * @return A new TubeIdentity instance
   */
  public static TubeIdentity createWithRandomId(String reason) {
    String uniqueId = UUID.randomUUID().toString();
    return new TubeIdentity(uniqueId, reason);
  }

  /**
   * Gets the unique identifier for this tube.
   *
   * @return The unique identifier
   */
  public String getUniqueId() {
    return uniqueId;
  }

  /**
   * Gets the conception time (creation timestamp) for this tube.
   *
   * @return The conception time
   */
  public Instant getConceptionTime() {
    return conceptionTime;
  }

  /**
   * Gets the reason for creating this tube.
   *
   * @return The creation reason
   */
  public String getReason() {
    return reason;
  }

  /**
   * Gets the environmental context at the time of tube creation.
   *
   * @return An unmodifiable view of the environmental context
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Returning unmodifiable view")
  public Map<String, String> getEnvironmentContext() {
    return Collections.unmodifiableMap(environmentContext);
  }

  /**
   * Gets the environmental context at the time of tube creation. This is an alias for
   * getEnvironmentContext() for backward compatibility.
   *
   * @return An unmodifiable view of the environmental context
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Returning unmodifiable view")
  public Map<String, String> getEnvironmentalContext() {
    return getEnvironmentContext();
  }

  /**
   * Adds environmental context information.
   *
   * @param key The context key
   * @param value The context value
   */
  public void addEnvironmentContext(String key, String value) {
    environmentContext.put(key, value);
  }

  /**
   * Gets the lineage information for this tube.
   *
   * @return An unmodifiable view of the lineage
   */
  public List<String> getLineage() {
    return Collections.unmodifiableList(lineage);
  }

  /**
   * Adds an entry to the tube's lineage.
   *
   * @param entry The lineage entry to add
   */
  public void addToLineage(String entry) {
    lineage.add(entry);
  }

  /**
   * Creates an Adam (origin) tube identity without a parent reference.
   *
   * @param reason The reason for creating this Adam tube
   * @param environment The environment in which to create the tube
   * @return A new Adam tube identity
   */
  public static TubeIdentity createAdamIdentity(String reason, Environment environment) {
    TubeIdentity identity = createWithRandomId(reason);
    identity.isAdamTube = true;
    identity.hierarchicalAddress = "T<" + identity.getUniqueId().substring(0, 8) + ">";

    // Add environmental context
    if (environment != null) {
      for (String key : environment.getParameterKeys()) {
        identity.addEnvironmentContext(key, environment.getParameter(key));
      }
    }

    return identity;
  }

  /**
   * Creates a child tube identity with a parent reference.
   *
   * @param reason The reason for creating this child tube
   * @param environment The environment in which to create the tube
   * @param parentIdentity The parent tube's identity
   * @return A new child tube identity
   */
  public static TubeIdentity createChildIdentity(
      String reason, Environment environment, TubeIdentity parentIdentity) {
    TubeIdentity identity = createWithRandomId(reason);
    identity.parentIdentity = parentIdentity;

    // Calculate hierarchical address based on parent
    if (parentIdentity != null) {
      identity.hierarchicalAddress =
          parentIdentity.getHierarchicalAddress() + "." + identity.getUniqueId().substring(0, 8);
    } else {
      identity.hierarchicalAddress = "T<" + identity.getUniqueId().substring(0, 8) + ">";
    }

    // Add environmental context
    if (environment != null) {
      for (String key : environment.getParameterKeys()) {
        identity.addEnvironmentContext(key, environment.getParameter(key));
      }
    }

    return identity;
  }

  /**
   * Gets the parent identity of this tube, if any.
   *
   * @return The parent tube's identity, or null for Adam tubes
   */
  public TubeIdentity getParentIdentity() {
    return parentIdentity;
  }

  /**
   * Gets the hierarchical address of this tube in the tube ecosystem.
   *
   * @return The hierarchical address string
   */
  public String getHierarchicalAddress() {
    return hierarchicalAddress;
  }

  /**
   * Determines if this is an Adam (origin) tube without a parent.
   *
   * @return true if this is an Adam tube, false otherwise
   */
  public boolean isAdamTube() {
    return isAdamTube;
  }

  /**
   * Gets the list of descendants of this tube.
   *
   * @return An unmodifiable list of descendant identities
   */
  public List<TubeIdentity> getDescendants() {
    return Collections.unmodifiableList(descendants);
  }

  /**
   * Adds a child tube to this tube's descendants.
   *
   * @param childIdentity The child tube's identity
   */
  public void addChild(TubeIdentity childIdentity) {
    if (childIdentity != null) {
      descendants.add(childIdentity);
    }
  }

  @Override
  public String toString() {
    return "TubeIdentity["
        + "id="
        + uniqueId
        + ", conception="
        + conceptionTime
        + ", reason='"
        + reason
        + "'"
        + "]";
  }
}
