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

package org.s8r.component;

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
 * Represents the identity of a component based on the biological continuity model.
 *
 * <p>This class is part of the simplified package structure, moving from org.s8r.core.tube.identity
 * to the more intuitive org.s8r.component.identity package.
 *
 * <p>The Identity class encapsulates all aspects of a component's identity:
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
 * component's existence throughout its lifecycle.
 */
public class Identity {
  private final String uniqueId;
  private final Instant conceptionTime;
  private final String reason;
  private final Map<String, String> environmentContext;
  private final List<String> lineage;
  private Identity parentIdentity;
  private List<Identity> descendants;
  private String hierarchicalAddress;
  private boolean isAdamComponent;

  /**
   * Creates a new Identity with the specified properties.
   *
   * @param uniqueId The unique identifier for this component
   * @param reason The reason for creating this component
   */
  public Identity(String uniqueId, String reason) {
    this.uniqueId = uniqueId;
    this.conceptionTime = Instant.now();
    this.reason = reason;
    this.environmentContext = new HashMap<>();
    this.lineage = new CopyOnWriteArrayList<>();
    this.lineage.add(reason);
    this.descendants = new ArrayList<>();
    this.isAdamComponent = false;
    this.hierarchicalAddress = "C<" + uniqueId.substring(0, 8) + ">";
  }

  /**
   * Creates a new Identity with a randomly generated UUID.
   *
   * @param reason The reason for creating this component
   * @return A new Identity instance
   */
  public static Identity createWithRandomId(String reason) {
    String uniqueId = UUID.randomUUID().toString();
    return new Identity(uniqueId, reason);
  }

  /**
   * Gets the unique identifier for this component.
   *
   * @return The unique identifier
   */
  public String getUniqueId() {
    return uniqueId;
  }

  /**
   * Gets the conception time (creation timestamp) for this component.
   *
   * @return The conception time
   */
  public Instant getConceptionTime() {
    return conceptionTime;
  }

  /**
   * Gets the reason for creating this component.
   *
   * @return The creation reason
   */
  public String getReason() {
    return reason;
  }

  /**
   * Gets the environmental context at the time of component creation.
   *
   * @return An unmodifiable view of the environmental context
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Returning unmodifiable view")
  public Map<String, String> getEnvironmentContext() {
    return Collections.unmodifiableMap(environmentContext);
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
   * Gets the lineage information for this component.
   *
   * @return An unmodifiable view of the lineage
   */
  public List<String> getLineage() {
    return Collections.unmodifiableList(lineage);
  }

  /**
   * Adds an entry to the component's lineage.
   *
   * @param entry The lineage entry to add
   */
  public void addToLineage(String entry) {
    lineage.add(entry);
  }

  /**
   * Creates an Adam (origin) component identity without a parent reference.
   *
   * @param reason The reason for creating this Adam component
   * @param environment The environment in which to create the component
   * @return A new Adam component identity
   */
  public static Identity createAdamIdentity(String reason, Map<String, String> environmentParams) {
    Identity identity = createWithRandomId(reason);
    identity.isAdamComponent = true;
    identity.hierarchicalAddress = "C<" + identity.getUniqueId().substring(0, 8) + ">";

    // Add environmental context
    if (environmentParams != null) {
      for (Map.Entry<String, String> entry : environmentParams.entrySet()) {
        identity.addEnvironmentContext(entry.getKey(), entry.getValue());
      }
    }

    return identity;
  }

  /**
   * Creates a child component identity with a parent reference.
   *
   * @param reason The reason for creating this child component
   * @param environmentParams Environmental parameters
   * @param parentIdentity The parent component's identity
   * @return A new child component identity
   */
  public static Identity createChildIdentity(
      String reason, Map<String, String> environmentParams, Identity parentIdentity) {
    Identity identity = createWithRandomId(reason);
    identity.parentIdentity = parentIdentity;

    // Calculate hierarchical address based on parent
    if (parentIdentity != null) {
      identity.hierarchicalAddress =
          parentIdentity.getHierarchicalAddress() + "." + identity.getUniqueId().substring(0, 8);
    } else {
      identity.hierarchicalAddress = "C<" + identity.getUniqueId().substring(0, 8) + ">";
    }

    // Add environmental context
    if (environmentParams != null) {
      for (Map.Entry<String, String> entry : environmentParams.entrySet()) {
        identity.addEnvironmentContext(entry.getKey(), entry.getValue());
      }
    }

    return identity;
  }

  /**
   * Gets the parent identity of this component, if any.
   *
   * @return The parent component's identity, or null for Adam components
   */
  public Identity getParentIdentity() {
    return parentIdentity;
  }

  /**
   * Gets the parent identity of this component, if any. This is an alias for getParentIdentity()
   * for backward compatibility.
   *
   * @return The parent component's identity, or null for Adam components
   */
  public Identity getParent() {
    return getParentIdentity();
  }

  /**
   * Gets the hierarchical address of this component in the component ecosystem.
   *
   * @return The hierarchical address string
   */
  public String getHierarchicalAddress() {
    return hierarchicalAddress;
  }

  /**
   * Gets the hierarchical ID of this component. This is an alias for getHierarchicalAddress() for
   * backward compatibility.
   *
   * @return The hierarchical ID string
   */
  public String getHierarchicalId() {
    return getHierarchicalAddress();
  }

  /**
   * Determines if this is an Adam (origin) component without a parent.
   *
   * @return true if this is an Adam component, false otherwise
   */
  public boolean isAdamComponent() {
    return isAdamComponent;
  }

  /**
   * Determines if this is an Adam (origin) component without a parent. This is an alias for
   * isAdamComponent() for backward compatibility.
   *
   * @return true if this is an Adam component, false otherwise
   */
  public boolean isAdam() {
    return isAdamComponent();
  }

  /**
   * Checks if this identity has a parent.
   *
   * @return true if this identity has a parent, false otherwise
   */
  public boolean hasParent() {
    return parentIdentity != null;
  }

  /**
   * Determines if this is a root component (same as isAdam).
   *
   * @return true if this is a root component, false otherwise
   */
  public boolean isRoot() {
    return isAdamComponent();
  }

  /**
   * Gets the list of descendants of this component.
   *
   * @return An unmodifiable list of descendant identities
   */
  public List<Identity> getDescendants() {
    return Collections.unmodifiableList(descendants);
  }

  /**
   * Adds a child component to this component's descendants.
   *
   * @param childIdentity The child component's identity
   */
  public void addChild(Identity childIdentity) {
    if (childIdentity != null) {
      descendants.add(childIdentity);
    }
  }

  /**
   * Gets the siblings of this component (other components with the same parent).
   *
   * @return A list of sibling identities, or an empty list if no siblings or parent
   */
  public List<Identity> getSiblings() {
    if (parentIdentity == null) {
      return Collections.emptyList();
    }

    List<Identity> siblings = new ArrayList<>();
    for (Identity descendant : parentIdentity.getDescendants()) {
      if (descendant != this) {
        siblings.add(descendant);
      }
    }
    return siblings;
  }

  /**
   * Determines if this component is a descendant of the specified component.
   *
   * @param ancestorIdentity The potential ancestor identity
   * @return true if this component is a descendant of the specified component, false otherwise
   */
  public boolean isDescendantOf(Identity ancestorIdentity) {
    if (ancestorIdentity == null || parentIdentity == null) {
      return false;
    }

    Identity current = parentIdentity;
    while (current != null) {
      if (current.equals(ancestorIdentity)) {
        return true;
      }
      current = current.getParentIdentity();
    }
    return false;
  }

  /**
   * Determines if this component is an ancestor of the specified component.
   *
   * @param descendantIdentity The potential descendant identity
   * @return true if this component is an ancestor of the specified component, false otherwise
   */
  public boolean isAncestorOf(Identity descendantIdentity) {
    if (descendantIdentity == null) {
      return false;
    }

    return descendantIdentity.isDescendantOf(this);
  }

  /**
   * Gets the unique identifier for this identity. This is an alias for getUniqueId() needed by
   * tests.
   *
   * @return The unique identifier
   */
  public String getId() {
    return uniqueId;
  }

  /**
   * Gets the creation time of this identity. This is an alias for getConceptionTime() needed by
   * tests.
   *
   * @return The creation time
   */
  public java.time.LocalDateTime getCreationTime() {
    return java.time.LocalDateTime.ofInstant(conceptionTime, java.time.ZoneId.systemDefault());
  }

  /**
   * Gets the purpose of this identity's creation. This is an alias for getReason() needed by tests.
   *
   * @return The creation purpose
   */
  public String getPurpose() {
    return reason;
  }

  /**
   * Gets the address of this identity in the hierarchy. This is an alias for
   * getHierarchicalAddress() needed by tests.
   *
   * @return The hierarchical address
   */
  public String getAddress() {
    return hierarchicalAddress;
  }

  @Override
  public String toString() {
    return "Identity["
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
