/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0
 */

package org.s8r.test.data;

import java.util.HashMap;
import java.util.Map;

import org.s8r.component.Component;
import org.s8r.component.Environment;
import org.s8r.component.State;
import org.s8r.component.identity.Identity;

/**
 * Centralized factory for test data creation.
 *
 * <p>Provides builder instances for creating domain objects in tests with sensible defaults. All
 * builders use fluent API and produce immutable objects.
 *
 * <p>Usage:
 *
 * <pre>{@code
 * Component component = TestDataFactory.component()
 *     .withReason("test-processor")
 *     .inState(State.ACTIVE)
 *     .build();
 *
 * Environment env = TestDataFactory.environment()
 *     .withParameter("mode", "test")
 *     .build();
 * }</pre>
 *
 * @see <a
 *     href="docs/architecture/decisions/0014-adopt-contract-first-testing-strategy.md">ADR-0014</a>
 */
public final class TestDataFactory {

  private TestDataFactory() {
    // Utility class - no instantiation
  }

  // =========================================================================
  // Builder Entry Points
  // =========================================================================

  /** Returns a new ComponentBuilder with default settings. */
  public static ComponentBuilder component() {
    return new ComponentBuilder();
  }

  /** Returns a new EnvironmentBuilder with default settings. */
  public static EnvironmentBuilder environment() {
    return new EnvironmentBuilder();
  }

  /** Returns a new IdentityBuilder with default settings. */
  public static IdentityBuilder identity() {
    return new IdentityBuilder();
  }

  // =========================================================================
  // Convenience Methods (Pre-configured objects)
  // =========================================================================

  /** Creates an active Adam component with default settings. */
  public static Component anActiveAdamComponent() {
    return component().asAdam().inState(State.ACTIVE).build();
  }

  /** Creates a component in CONCEPTION state. */
  public static Component aNewComponent() {
    return component().withReason("new-component").build();
  }

  /** Creates a test environment with standard test parameters. */
  public static Environment aTestEnvironment() {
    return environment()
        .withParameter("test.mode", "true")
        .withParameter("test.isolation", "enabled")
        .build();
  }

  /** Creates an Adam identity with default reason. */
  public static Identity anAdamIdentity() {
    return identity().asAdam().withReason("adam-test-identity").build();
  }

  // =========================================================================
  // Builder Classes
  // =========================================================================

  /** Builder for Component test instances. */
  public static class ComponentBuilder {
    private String reason = "test-component";
    private Environment environment;
    private State targetState = State.CONCEPTION;
    private Component parentComponent = null;
    private boolean isAdam = true;

    ComponentBuilder() {
      this.environment = new Environment();
    }

    public ComponentBuilder withReason(String reason) {
      this.reason = reason;
      return this;
    }

    public ComponentBuilder withEnvironment(Environment environment) {
      this.environment = environment;
      return this;
    }

    public ComponentBuilder inState(State state) {
      this.targetState = state;
      return this;
    }

    public ComponentBuilder asAdam() {
      this.isAdam = true;
      this.parentComponent = null;
      return this;
    }

    public ComponentBuilder asChildOf(Component parent) {
      this.isAdam = false;
      this.parentComponent = parent;
      return this;
    }

    public ComponentBuilder withProperty(String key, String value) {
      this.environment.setParameter(key, value);
      return this;
    }

    public Component build() {
      Component component;
      if (isAdam) {
        component = Component.createAdam(reason);
      } else if (parentComponent != null) {
        component = Component.createChild(reason, environment, parentComponent);
      } else {
        component = Component.create(reason, environment);
      }

      // Transition to target state if not CONCEPTION
      transitionToState(component, targetState);

      return component;
    }

    private void transitionToState(Component component, State target) {
      // State transitions follow: CONCEPTION -> INITIALIZING -> CONFIGURING -> SPECIALIZING ->
      // ACTIVE
      State[] orderedStates = {
        State.CONCEPTION, State.INITIALIZING, State.CONFIGURING, State.SPECIALIZING, State.ACTIVE
      };

      int currentIndex = indexOf(orderedStates, component.getState());
      int targetIndex = indexOf(orderedStates, target);

      if (targetIndex > currentIndex) {
        for (int i = currentIndex + 1; i <= targetIndex; i++) {
          component.setState(orderedStates[i]);
        }
      }
    }

    private int indexOf(State[] states, State state) {
      for (int i = 0; i < states.length; i++) {
        if (states[i] == state) return i;
      }
      return 0;
    }
  }

  /** Builder for Environment test instances. */
  public static class EnvironmentBuilder {
    private final Environment environment;

    EnvironmentBuilder() {
      this.environment = new Environment();
    }

    public EnvironmentBuilder withParameter(String key, String value) {
      this.environment.setParameter(key, value);
      return this;
    }

    public EnvironmentBuilder asTestEnvironment() {
      this.environment.setParameter("test.mode", "true");
      return this;
    }

    public Environment build() {
      return environment;
    }
  }

  /** Builder for Identity test instances. */
  public static class IdentityBuilder {
    private String reason = "test-identity";
    private Map<String, String> environmentParams = new HashMap<>();
    private boolean isAdam = true;

    IdentityBuilder() {}

    public IdentityBuilder withReason(String reason) {
      this.reason = reason;
      return this;
    }

    public IdentityBuilder asAdam() {
      this.isAdam = true;
      return this;
    }

    public IdentityBuilder withEnvironmentParam(String key, String value) {
      this.environmentParams.put(key, value);
      return this;
    }

    public Identity build() {
      if (isAdam) {
        return Identity.createAdamIdentity(reason, environmentParams);
      } else {
        return Identity.createWithRandomId(reason);
      }
    }
  }
}
