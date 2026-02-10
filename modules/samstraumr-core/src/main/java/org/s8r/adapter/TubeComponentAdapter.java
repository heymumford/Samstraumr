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

import org.s8r.application.port.LoggerPort;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.identity.LegacyComponentAdapterPort;
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.infrastructure.migration.feedback.IssueType;
import org.s8r.infrastructure.migration.feedback.MigrationIssueLogger;
import org.s8r.infrastructure.migration.feedback.Severity;
import org.s8r.tube.Environment;
import org.s8r.tube.Tube;
import org.s8r.tube.TubeIdentity;
import org.s8r.tube.TubeLifecycleState;
import org.s8r.tube.TubeStatus;

/**
 * A concrete implementation of LegacyComponentAdapterPort specifically for Tube components. This
 * adapter provides a direct conversion between Tube objects and Components without using
 * reflection.
 *
 * <p>This is more efficient than the reflection-based approach when we know we're working with Tube
 * objects specifically, and serves as the primary migration path for existing code.
 *
 * <p>This adapter implements the Dependency Inversion Principle by returning ComponentPort
 * interfaces instead of concrete Component implementations. This allows client code to depend on
 * abstractions rather than concrete implementations, facilitating a smooth migration to Clean
 * Architecture.
 */
public class TubeComponentAdapter implements LegacyComponentAdapterPort {

  private final LoggerPort logger;
  private final TubeLegacyIdentityConverter identityConverter;
  private final TubeLegacyEnvironmentConverter environmentConverter;
  private final MigrationIssueLogger migrationLogger;

  /**
   * Creates a new TubeComponentAdapter.
   *
   * @param logger Logger for recording operations
   * @param identityConverter Converter for identity objects
   * @param environmentConverter Converter for environment objects
   */
  public TubeComponentAdapter(
      LoggerPort logger,
      TubeLegacyIdentityConverter identityConverter,
      TubeLegacyEnvironmentConverter environmentConverter) {
    this.logger = logger;
    this.identityConverter = identityConverter;
    this.environmentConverter = environmentConverter;
    this.migrationLogger = MigrationIssueLogger.forCategory("TubeComponentAdapter");
    logger.debug("TubeComponentAdapter initialized");
  }

  @Override
  public ComponentPort wrapLegacyComponent(Object legacyComponent) {
    if (!(legacyComponent instanceof Tube)) {
      String actualType = legacyComponent != null ? legacyComponent.getClass().getName() : "null";
      migrationLogger.reportTypeMismatch("legacyComponent", actualType, "org.s8r.tube.Tube");
      throw new IllegalArgumentException("Expected Tube object, got: " + actualType);
    }

    Tube tube = (Tube) legacyComponent;
    TubeIdentity tubeIdentity = tube.getIdentity();

    // Create component ID from tube identity
    ComponentId componentId =
        identityConverter.toComponentId(
            tubeIdentity.getUniqueId(), tubeIdentity.getReason(), tubeIdentity.getLineage());

    // Map the tube state to component lifecycle state
    String tubeStateString = getTubeStateString(tube);

    // Log the state mapping for migration feedback
    migrationLogger.reportCustomIssue(
        IssueType.STATE_TRANSITION,
        Severity.INFO,
        String.format(
            "Mapped Tube state %s to Component state %s", tube.getStatus(), tubeStateString));

    // Create wrapper component
    TubeComponentWrapper wrapper =
        new TubeComponentWrapper(componentId, tube, tubeStateString, this);

    logger.debug("Wrapped Tube in Component: {}", componentId.getIdString());
    return wrapper;
  }

  @Override
  public ComponentPort createLegacyComponent(String name, String type, String reason) {
    logger.debug("Creating new Tube component: name={}, type={}, reason={}", name, type, reason);

    // Create environment
    Environment environment = new Environment();
    environment.setParameter("name", name);
    environment.setParameter("type", type);

    // Create tube
    Tube tube = Tube.create(reason, environment);

    // Wrap and return
    return wrapLegacyComponent(tube);
  }

  @Override
  public void setLegacyComponentState(Object legacyComponent, String state) {
    if (!(legacyComponent instanceof Tube)) {
      String actualType = legacyComponent != null ? legacyComponent.getClass().getName() : "null";
      migrationLogger.reportTypeMismatch("legacyComponent", actualType, "org.s8r.tube.Tube");
      throw new IllegalArgumentException("Expected Tube object, got: " + actualType);
    }

    Tube tube = (Tube) legacyComponent;
    String currentState = getTubeStateString(tube);

    // Map the component state to tube status and lifecycle state
    try {
      LifecycleState lifecycleState = LifecycleState.valueOf(state);
      TubeStatus tubeStatus = mapLifecycleStateToTubeStatus(lifecycleState);
      TubeLifecycleState tubeLifecycleState = mapLifecycleStateToTubeLifecycleState(lifecycleState);

      // Check for potentially invalid state transitions
      if (isInvalidStateTransition(tube.getStatus(), tubeStatus)) {
        migrationLogger.reportStateTransitionIssue(
            tube.getStatus().name(),
            tubeStatus.name(),
            "Potentially invalid state transition in Tube component");
      }

      // Set both states on the tube
      tube.setStatus(tubeStatus);

      // Log successful state transition
      migrationLogger.reportCustomIssue(
          IssueType.STATE_TRANSITION,
          Severity.INFO,
          String.format(
              "State transition: %s -> %s (TubeStatus: %s -> %s)",
              currentState, state, tube.getStatus(), tubeStatus));

      logger.debug("Set Tube state: status={}, lifecycleState={}", tubeStatus, tubeLifecycleState);
    } catch (IllegalArgumentException e) {
      logger.error("Invalid lifecycle state: {}", state);
      migrationLogger.reportCustomIssue(
          IssueType.STATE_TRANSITION,
          Severity.ERROR,
          String.format("Invalid lifecycle state: %s", state));
      throw new IllegalArgumentException("Invalid lifecycle state: " + state, e);
    }
  }

  /**
   * Checks if a state transition may be invalid based on the tube lifecycle model.
   *
   * @param fromStatus The current tube status
   * @param toStatus The target tube status
   * @return true if the transition might be invalid
   */
  private boolean isInvalidStateTransition(TubeStatus fromStatus, TubeStatus toStatus) {
    // Direct transition from ACTIVE to TERMINATED is invalid (should go through DEACTIVATING)
    if (fromStatus == TubeStatus.ACTIVE && toStatus == TubeStatus.TERMINATED) {
      return true;
    }

    // Other invalid transitions could be added here
    return false;
  }

  @Override
  public String getLegacyComponentState(Object legacyComponent) {
    if (!(legacyComponent instanceof Tube)) {
      String actualType = legacyComponent != null ? legacyComponent.getClass().getName() : "null";
      migrationLogger.reportTypeMismatch("legacyComponent", actualType, "org.s8r.tube.Tube");
      throw new IllegalArgumentException("Expected Tube object, got: " + actualType);
    }

    Tube tube = (Tube) legacyComponent;
    String state = getTubeStateString(tube);

    // Record state mapping
    migrationLogger.reportCustomIssue(
        IssueType.STATE_TRANSITION,
        Severity.DEBUG,
        String.format("Read component state: %s from tube status: %s", state, tube.getStatus()));

    return state;
  }

  @Override
  public void initializeLegacyComponent(Object legacyComponent) {
    if (!(legacyComponent instanceof Tube)) {
      String actualType = legacyComponent != null ? legacyComponent.getClass().getName() : "null";
      migrationLogger.reportTypeMismatch("legacyComponent", actualType, "org.s8r.tube.Tube");
      throw new IllegalArgumentException("Expected Tube object, got: " + actualType);
    }

    // Tubes are already initialized during creation, so this is a no-op
    logger.debug("No explicit initialization needed for Tube components");

    // Note the lifecycle difference
    migrationLogger.reportCustomIssue(
        IssueType.LIFECYCLE,
        Severity.INFO,
        "Tube components don't require explicit initialization unlike S8r components");
  }

  /**
   * Creates a new Tube as a child of another Tube.
   *
   * @param reason The reason for creating the child
   * @param parentTube The parent tube
   * @return A new Component wrapping the child tube
   */
  public ComponentPort createChildComponent(String reason, Tube parentTube) {
    logger.debug("Creating child Tube with reason: {}", reason);

    Environment environment = parentTube.getEnvironment();
    Tube childTube = Tube.createChildTube(reason, environment, parentTube);

    return wrapLegacyComponent(childTube);
  }

  /**
   * Gets the state string for a tube, considering both its status and lifecycle state.
   *
   * @param tube The tube
   * @return A string representation of the tube's state, compatible with the Component lifecycle
   *     state
   */
  private String getTubeStateString(Tube tube) {
    TubeStatus status = tube.getStatus();

    // Map tube status to component lifecycle state
    if (status == TubeStatus.INITIALIZING) {
      return LifecycleState.INITIALIZING.name();
    } else if (status == TubeStatus.READY) {
      return LifecycleState.READY.name();
    } else if (status == TubeStatus.ACTIVE) {
      return LifecycleState.ACTIVE.name();
    } else if (status == TubeStatus.DEACTIVATING) {
      return LifecycleState.TERMINATING.name();
    } else if (status == TubeStatus.TERMINATED) {
      return LifecycleState.TERMINATED.name();
    } else if (status == TubeStatus.ERROR || status == TubeStatus.RECOVERING) {
      return LifecycleState.DEGRADED.name();
    } else {
      logger.warn("Unknown tube status: {}, defaulting to READY", status);
      return LifecycleState.READY.name();
    }
  }

  /**
   * Maps a LifecycleState to a TubeStatus.
   *
   * @param state The lifecycle state
   * @return The corresponding TubeStatus
   */
  private TubeStatus mapLifecycleStateToTubeStatus(LifecycleState state) {
    if (state == LifecycleState.CONCEPTION) {
      return TubeStatus.INITIALIZING; // No direct equivalent, use INITIALIZING
    } else if (state == LifecycleState.INITIALIZING
        || state == LifecycleState.CONFIGURING
        || state == LifecycleState.SPECIALIZING
        || state == LifecycleState.DEVELOPING_FEATURES) {
      return TubeStatus.INITIALIZING;
    } else if (state == LifecycleState.READY) {
      return TubeStatus.READY;
    } else if (state == LifecycleState.ACTIVE) {
      return TubeStatus.ACTIVE;
    } else if (state == LifecycleState.DEGRADED) {
      return TubeStatus.ERROR; // Close match to DEGRADED
    } else if (state == LifecycleState.TERMINATING) {
      return TubeStatus.DEACTIVATING; // Equivalent to TERMINATING
    } else if (state == LifecycleState.TERMINATED) {
      return TubeStatus.TERMINATED;
    } else {
      logger.warn("Unknown lifecycle state: {}, defaulting to READY", state);
      return TubeStatus.READY;
    }
  }

  /**
   * Maps a LifecycleState to a TubeLifecycleState.
   *
   * @param state The lifecycle state
   * @return The corresponding TubeLifecycleState
   */
  private TubeLifecycleState mapLifecycleStateToTubeLifecycleState(LifecycleState state) {
    switch (state) {
      case CONCEPTION:
        return TubeLifecycleState.CONCEPTION;
      case INITIALIZING:
        return TubeLifecycleState.INITIALIZING;
      case CONFIGURING:
        return TubeLifecycleState.CONFIGURING;
      case SPECIALIZING:
        return TubeLifecycleState.SPECIALIZING;
      case DEVELOPING_FEATURES:
        return TubeLifecycleState.DEVELOPING_FEATURES;
      case READY:
      case ACTIVE:
      case DEGRADED:
        return TubeLifecycleState.READY;
      case TERMINATING:
        return TubeLifecycleState.TERMINATING;
      case TERMINATED:
        return TubeLifecycleState.TERMINATED;
      default:
        logger.warn("Unknown lifecycle state: {}, defaulting to READY", state);
        return TubeLifecycleState.READY;
    }
  }

  /**
   * Gets the TubeLegacyIdentityConverter used by this adapter.
   *
   * @return The identity converter
   */
  public TubeLegacyIdentityConverter getIdentityConverter() {
    return identityConverter;
  }

  /**
   * Gets the TubeLegacyEnvironmentConverter used by this adapter.
   *
   * @return The environment converter
   */
  public TubeLegacyEnvironmentConverter getEnvironmentConverter() {
    return environmentConverter;
  }
}
