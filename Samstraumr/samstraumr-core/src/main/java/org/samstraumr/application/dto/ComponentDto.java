/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * DTO for Component in the S8r framework
 */

package org.samstraumr.application.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.samstraumr.domain.component.Component;
import org.samstraumr.domain.lifecycle.LifecycleState;

/**
 * Data Transfer Object (DTO) for Component entities.
 *
 * <p>This class is used to transfer component data across architectural boundaries without exposing
 * domain entities. It follows Clean Architecture principles by keeping the domain entities isolated
 * from external concerns.
 */
public class ComponentDto {
  private final String id;
  private final String shortId;
  private final String reason;
  private final Instant creationTime;
  private final String lifecycleState;
  private final String lifecycleDescription;
  private final List<String> lineage;
  private final List<String> activityLog;

  /**
   * Creates a new ComponentDto from a domain Component entity.
   *
   * @param component The domain component entity
   */
  public ComponentDto(Component component) {
    this.id = component.getId().getIdString();
    this.shortId = component.getId().getShortId();
    this.reason = component.getId().getReason();
    this.creationTime = component.getCreationTime();

    LifecycleState state = component.getLifecycleState();
    this.lifecycleState = state.name();
    this.lifecycleDescription = state.getDescription();

    this.lineage = new ArrayList<>(component.getLineage());
    this.activityLog = new ArrayList<>(component.getActivityLog());
  }

  /**
   * Creates a new ComponentDto with the specified values.
   *
   * @param id The component ID
   * @param shortId The short component ID
   * @param reason The reason for component creation
   * @param creationTime The component creation time
   * @param lifecycleState The lifecycle state name
   * @param lifecycleDescription The lifecycle state description
   * @param lineage The component lineage
   * @param activityLog The component activity log
   */
  public ComponentDto(
      String id,
      String shortId,
      String reason,
      Instant creationTime,
      String lifecycleState,
      String lifecycleDescription,
      List<String> lineage,
      List<String> activityLog) {
    this.id = id;
    this.shortId = shortId;
    this.reason = reason;
    this.creationTime = creationTime;
    this.lifecycleState = lifecycleState;
    this.lifecycleDescription = lifecycleDescription;
    this.lineage = new ArrayList<>(lineage);
    this.activityLog = new ArrayList<>(activityLog);
  }

  // Getter methods

  /**
   * Gets the component ID.
   *
   * @return The component ID
   */
  public String getId() {
    return id;
  }

  /**
   * Gets the short component ID.
   *
   * @return The short component ID
   */
  public String getShortId() {
    return shortId;
  }

  /**
   * Gets the reason for component creation.
   *
   * @return The creation reason
   */
  public String getReason() {
    return reason;
  }

  /**
   * Gets the component creation time.
   *
   * @return The creation time
   */
  public Instant getCreationTime() {
    return creationTime;
  }

  /**
   * Gets the lifecycle state name.
   *
   * @return The lifecycle state name
   */
  public String getLifecycleState() {
    return lifecycleState;
  }

  /**
   * Gets the lifecycle state description.
   *
   * @return The lifecycle state description
   */
  public String getLifecycleDescription() {
    return lifecycleDescription;
  }

  /**
   * Gets the component lineage.
   *
   * @return The lineage
   */
  public List<String> getLineage() {
    return new ArrayList<>(lineage);
  }

  /**
   * Gets the component activity log.
   *
   * @return The activity log
   */
  public List<String> getActivityLog() {
    return new ArrayList<>(activityLog);
  }

  /**
   * Factory method to create a ComponentDto from a domain Component entity.
   *
   * @param component The domain component entity
   * @return A new ComponentDto or null if the component is null
   */
  public static ComponentDto fromDomain(Component component) {
    if (component == null) {
      return null;
    }
    return new ComponentDto(component);
  }

  @Override
  public String toString() {
    return "ComponentDto{"
        + "id='"
        + shortId
        + '\''
        + ", reason='"
        + reason
        + '\''
        + ", lifecycleState='"
        + lifecycleState
        + '\''
        + ", creationTime="
        + creationTime
        + '}';
  }
}
