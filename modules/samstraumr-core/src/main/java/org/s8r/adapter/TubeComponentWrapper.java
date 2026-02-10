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

import java.util.Map;

import org.s8r.domain.component.Component;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.exception.ComponentException;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.tube.Tube;

/**
 * A wrapper that implements the Component interface by delegating to a Tube implementation.
 *
 * <p>This wrapper allows existing Tube objects to be used with new Component-based code, providing
 * a smooth migration path while maintaining backward compatibility.
 *
 * <p>By implementing the ComponentPort interface, this wrapper provides a standardized way for
 * adapter clients to interact with components without depending on the concrete Component
 * implementation. This follows the Dependency Inversion Principle and ensures that clients depend
 * on abstractions rather than concrete implementations.
 */
public class TubeComponentWrapper extends Component implements ComponentPort {

  private final Tube tube;
  private final TubeComponentAdapter adapter;

  /**
   * Creates a new TubeComponentWrapper.
   *
   * @param componentId The component ID
   * @param tube The tube to wrap
   * @param initialState The initial state
   * @param adapter The adapter to use
   */
  TubeComponentWrapper(
      ComponentId componentId, Tube tube, String initialState, TubeComponentAdapter adapter) {
    super(componentId);
    this.tube = tube;
    this.adapter = adapter;

    // Set the initial state by directly setting the field via reflection
    // This bypasses the state transition validation for initialization
    try {
      LifecycleState targetState = LifecycleState.valueOf(initialState);

      // Use reflection to set the state directly
      try {
        java.lang.reflect.Field stateField = Component.class.getDeclaredField("lifecycleState");
        stateField.setAccessible(true);
        stateField.set(this, targetState);
        logActivity("Initial state set to: " + targetState);
      } catch (Exception ex) {
        // If reflection fails, try to use the proper lifecycle transitions
        logActivity("Warning: Could not set initial state via reflection: " + ex.getMessage());

        // Follow the proper sequence for initialization
        try {
          super.transitionTo(LifecycleState.INITIALIZING);
          super.transitionTo(LifecycleState.CONFIGURING);
          super.transitionTo(LifecycleState.SPECIALIZING);
          super.transitionTo(LifecycleState.DEVELOPING_FEATURES);
          super.transitionTo(LifecycleState.READY);

          // Only transition to ACTIVE if needed
          if (targetState == LifecycleState.ACTIVE) {
            super.transitionTo(LifecycleState.ACTIVE);
          }
        } catch (Exception e) {
          logActivity("Warning: Could not complete state transition sequence: " + e.getMessage());
        }
      }
    } catch (IllegalArgumentException e) {
      // If the state isn't valid, default to READY using reflection
      try {
        java.lang.reflect.Field stateField = Component.class.getDeclaredField("lifecycleState");
        stateField.setAccessible(true);
        stateField.set(this, LifecycleState.READY);
        logActivity("Defaulted to READY state due to invalid state name: " + initialState);
      } catch (Exception ex) {
        logActivity("Warning: Could not set default READY state: " + ex.getMessage());
      }
    }

    // Copy tube lineage to component lineage
    for (String entry : tube.getLineage()) {
      addToLineage(entry);
    }

    // Copy tube activity log to component activity log
    for (String entry : tube.getMimirLog()) {
      logActivity("Tube: " + entry);
    }

    logActivity("TubeComponentWrapper created for tube: " + tube.getUniqueId());
  }

  @Override
  public LifecycleState getLifecycleState() {
    // Get the current state from the tube
    String tubeState = adapter.getLegacyComponentState(tube);

    try {
      return LifecycleState.valueOf(tubeState);
    } catch (IllegalArgumentException e) {
      // If the state mapping fails, return the super implementation
      return super.getLifecycleState();
    }
  }

  @Override
  public void transitionTo(LifecycleState newState) {
    // Update the Tube's state
    adapter.setLegacyComponentState(tube, newState.name());

    // Let the parent handle events and validation
    super.transitionTo(newState);
  }

  @Override
  public void activate() {
    if (tube.getStatus() != org.s8r.tube.TubeStatus.READY) {
      throw new ComponentException("Cannot activate: tube is not in READY state");
    }

    tube.setStatus(org.s8r.tube.TubeStatus.ACTIVE);
    super.transitionTo(LifecycleState.ACTIVE);
    logActivity("Tube activated");
  }

  @Override
  public void deactivate() {
    if (tube.getStatus() != org.s8r.tube.TubeStatus.ACTIVE) {
      throw new ComponentException("Cannot deactivate: tube is not in ACTIVE state");
    }

    tube.setStatus(org.s8r.tube.TubeStatus.READY);
    super.transitionTo(LifecycleState.READY);
    logActivity("Tube deactivated");
  }

  @Override
  public void terminate() {
    // Terminate the tube
    tube.terminate();

    // Let the parent handle events
    super.terminate();
  }

  @Override
  public void publishData(String channel, Map<String, Object> data) {
    // Publish the data to the component (for events)
    super.publishData(channel, data);

    // Log via reflection to avoid protected method access issue
    try {
      java.lang.reflect.Method logMethod = Tube.class.getDeclaredMethod("logToMimir", String.class);
      logMethod.setAccessible(true);
      logMethod.invoke(tube, "Data published to channel: " + channel);
    } catch (Exception e) {
      // Fallback to standard SLF4J logger if reflection fails
      org.slf4j.LoggerFactory.getLogger(Tube.class).info("Data published to channel: {}", channel);
    }
  }

  /**
   * Gets the wrapped Tube instance.
   *
   * @return The wrapped Tube
   */
  public Tube getTube() {
    return tube;
  }

  /**
   * Gets the parameters from the tube's environment.
   *
   * @return A map of environment parameters
   */
  public Map<String, String> getEnvironmentParameters() {
    return adapter
        .getEnvironmentConverter()
        .extractParametersFromLegacyEnvironment(tube.getEnvironment());
  }

  @Override
  public String toString() {
    return "TubeComponentWrapper{id="
        + getId()
        + ", tubeId="
        + tube.getUniqueId()
        + ", state="
        + getLifecycleState()
        + "}";
  }
}
