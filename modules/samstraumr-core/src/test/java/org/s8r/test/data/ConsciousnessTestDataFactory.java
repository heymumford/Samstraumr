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

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.s8r.component.Component;
import org.s8r.component.Environment;
import org.s8r.component.State;

/**
 * Extended test data factory for consciousness testing.
 *
 * <p>Provides specialized builders and fixtures for the 300-scenario consciousness test suite.
 * Extends the base TestDataFactory with consciousness-specific test data creation.
 *
 * <p>Categories supported:
 *
 * <ul>
 *   <li>Genesis - Adam Tube creation with primordial identity
 *   <li>Identity - Substrate, psychological, and narrative identity
 *   <li>Consciousness - Self-observation and recursive awareness
 *   <li>Feedback - Loop closure and signal processing
 *   <li>Emergence - Pattern detection and metacognition
 *   <li>Adaptation - Learning, memory, and evolution
 *   <li>Holistic - System-wide coherence and conformance
 * </ul>
 *
 * @see TestDataFactory
 * @see <a href="docs/concepts/philosophical-synthesis-identity-time-consciousness.md">Philosophical
 *     Synthesis</a>
 */
public final class ConsciousnessTestDataFactory {

  private ConsciousnessTestDataFactory() {
    // Utility class - no instantiation
  }

  // =========================================================================
  // Builder Entry Points
  // =========================================================================

  /** Returns a new ConsciousnessComponentBuilder with default settings. */
  public static ConsciousnessComponentBuilder consciousness() {
    return new ConsciousnessComponentBuilder();
  }

  /** Returns a new GenesisComponentBuilder for Adam Tube creation. */
  public static GenesisComponentBuilder genesis() {
    return new GenesisComponentBuilder();
  }

  /** Returns a new ObservationBuilder for self-observation records. */
  public static ObservationBuilder observation() {
    return new ObservationBuilder();
  }

  /** Returns a new FeedbackLoopBuilder for feedback loop testing. */
  public static FeedbackLoopBuilder feedbackLoop() {
    return new FeedbackLoopBuilder();
  }

  /** Returns a new ExperienceBuilder for memory and learning tests. */
  public static ExperienceBuilder experience() {
    return new ExperienceBuilder();
  }

  /** Returns a new EmergentPatternBuilder for emergence detection tests. */
  public static EmergentPatternBuilder emergentPattern() {
    return new EmergentPatternBuilder();
  }

  // =========================================================================
  // Convenience Methods
  // =========================================================================

  /** Creates an Adam Tube with full consciousness capabilities. */
  public static Component aConsciousAdamTube() {
    return genesis()
        .withReason("Conscious Adam Tube")
        .withSelfObservation(true)
        .withFeedbackLoop(true)
        .withMemory(true)
        .build();
  }

  /** Creates an active conscious component. */
  public static Component anActiveConsciousComponent() {
    return consciousness()
        .withReason("Active Conscious Component")
        .inState(State.ACTIVE)
        .withSelfObservation(true)
        .build();
  }

  /** Creates a self-observation record. */
  public static Map<String, Object> aSelfObservation(String componentId) {
    return observation()
        .withObserverId(componentId)
        .withObservedId(componentId)
        .withStateSnapshot("ACTIVE")
        .build();
  }

  /** Creates a closed feedback loop record. */
  public static FeedbackLoopRecord aClosedFeedbackLoop() {
    return feedbackLoop()
        .withStatus("closed")
        .withCycleTimeMs(50)
        .withStages("observe", "assess", "adjust", "verify")
        .build();
  }

  /** Creates an experience record. */
  public static Map<String, Object> anExperience(String signalId, String type, String outcome) {
    return experience().withSignalId(signalId).withType(type).withOutcome(outcome).build();
  }

  // =========================================================================
  // Builder Classes
  // =========================================================================

  /** Builder for consciousness-enabled components. */
  public static class ConsciousnessComponentBuilder {
    private String reason = "consciousness-test-component";
    private Environment environment;
    private State targetState = State.CONCEPTION;
    private boolean selfObservation = false;
    private boolean feedbackLoop = false;
    private boolean memory = false;
    private int observationDepth = 3;
    private Component parent = null;

    ConsciousnessComponentBuilder() {
      this.environment =
          TestDataFactory.environment().withParameter("consciousness.enabled", "true").build();
    }

    public ConsciousnessComponentBuilder withReason(String reason) {
      this.reason = reason;
      return this;
    }

    public ConsciousnessComponentBuilder withEnvironment(Environment environment) {
      this.environment = environment;
      return this;
    }

    public ConsciousnessComponentBuilder inState(State state) {
      this.targetState = state;
      return this;
    }

    public ConsciousnessComponentBuilder withSelfObservation(boolean enabled) {
      this.selfObservation = enabled;
      if (enabled) {
        environment.setParameter("consciousness.self_observation", "true");
      }
      return this;
    }

    public ConsciousnessComponentBuilder withFeedbackLoop(boolean enabled) {
      this.feedbackLoop = enabled;
      if (enabled) {
        environment.setParameter("consciousness.feedback_loop", "true");
      }
      return this;
    }

    public ConsciousnessComponentBuilder withMemory(boolean enabled) {
      this.memory = enabled;
      if (enabled) {
        environment.setParameter("consciousness.memory", "true");
      }
      return this;
    }

    public ConsciousnessComponentBuilder withObservationDepth(int depth) {
      this.observationDepth = depth;
      environment.setParameter("consciousness.observation.depth", String.valueOf(depth));
      return this;
    }

    public ConsciousnessComponentBuilder asChildOf(Component parent) {
      this.parent = parent;
      return this;
    }

    public Component build() {
      Component component;
      if (parent == null) {
        component = Component.createAdam(reason);
      } else {
        component = Component.createChild(reason, environment, parent);
      }

      // Transition to target state
      transitionToState(component, targetState);

      return component;
    }

    private void transitionToState(Component component, State target) {
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

  /** Builder for Adam Tube (genesis) components. */
  public static class GenesisComponentBuilder {
    private String reason = "genesis-adam-tube";
    private Environment environment;
    private boolean selfObservation = true;
    private boolean feedbackLoop = true;
    private boolean memory = true;
    private Map<String, String> environmentalFactors;

    GenesisComponentBuilder() {
      this.environment =
          TestDataFactory.environment()
              .withParameter("genesis.timestamp", Instant.now().toString())
              .withParameter("genesis.type", "adam")
              .build();
      this.environmentalFactors = new HashMap<>();
      captureEnvironmentalFactors();
    }

    private void captureEnvironmentalFactors() {
      environmentalFactors.put("os.name", System.getProperty("os.name", "unknown"));
      environmentalFactors.put("os.version", System.getProperty("os.version", "unknown"));
      environmentalFactors.put("java.version", System.getProperty("java.version", "unknown"));
      environmentalFactors.put(
          "available.processors", String.valueOf(Runtime.getRuntime().availableProcessors()));
      environmentalFactors.put("max.memory", String.valueOf(Runtime.getRuntime().maxMemory()));
    }

    public GenesisComponentBuilder withReason(String reason) {
      this.reason = reason;
      return this;
    }

    public GenesisComponentBuilder withSelfObservation(boolean enabled) {
      this.selfObservation = enabled;
      return this;
    }

    public GenesisComponentBuilder withFeedbackLoop(boolean enabled) {
      this.feedbackLoop = enabled;
      return this;
    }

    public GenesisComponentBuilder withMemory(boolean enabled) {
      this.memory = enabled;
      return this;
    }

    public GenesisComponentBuilder withEnvironmentalFactor(String key, String value) {
      this.environmentalFactors.put(key, value);
      return this;
    }

    public Component build() {
      // Apply environmental factors
      for (Map.Entry<String, String> factor : environmentalFactors.entrySet()) {
        environment.setParameter("env." + factor.getKey(), factor.getValue());
      }

      // Apply consciousness settings
      environment.setParameter("consciousness.self_observation", String.valueOf(selfObservation));
      environment.setParameter("consciousness.feedback_loop", String.valueOf(feedbackLoop));
      environment.setParameter("consciousness.memory", String.valueOf(memory));

      return Component.createAdam(reason);
    }
  }

  /** Builder for observation records. */
  public static class ObservationBuilder {
    private String observerId;
    private String observedId;
    private String stateSnapshot;
    private Instant timestamp;
    private int depth = 1;
    private Map<String, Object> additionalData;

    ObservationBuilder() {
      this.timestamp = Instant.now();
      this.additionalData = new HashMap<>();
    }

    public ObservationBuilder withObserverId(String id) {
      this.observerId = id;
      return this;
    }

    public ObservationBuilder withObservedId(String id) {
      this.observedId = id;
      return this;
    }

    public ObservationBuilder withStateSnapshot(String state) {
      this.stateSnapshot = state;
      return this;
    }

    public ObservationBuilder withTimestamp(Instant timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public ObservationBuilder withDepth(int depth) {
      this.depth = depth;
      return this;
    }

    public ObservationBuilder withData(String key, Object value) {
      this.additionalData.put(key, value);
      return this;
    }

    public Map<String, Object> build() {
      Map<String, Object> observation = new HashMap<>();
      observation.put("observer_id", observerId);
      observation.put("observed_id", observedId);
      observation.put("state_snapshot", stateSnapshot);
      observation.put("observation_ts", timestamp.toString());
      observation.put("depth", depth);
      observation.put("is_self_observation", observerId != null && observerId.equals(observedId));
      observation.putAll(additionalData);
      return observation;
    }
  }

  /** Builder for feedback loop records. */
  public static class FeedbackLoopBuilder {
    private String id;
    private String status = "open";
    private long cycleTimeMs = 0;
    private List<String> stages;
    private Instant startTime;
    private Instant endTime;

    FeedbackLoopBuilder() {
      this.id = UUID.randomUUID().toString();
      this.stages = new ArrayList<>();
      this.startTime = Instant.now();
    }

    public FeedbackLoopBuilder withId(String id) {
      this.id = id;
      return this;
    }

    public FeedbackLoopBuilder withStatus(String status) {
      this.status = status;
      if ("closed".equals(status)) {
        this.endTime = Instant.now();
      }
      return this;
    }

    public FeedbackLoopBuilder withCycleTimeMs(long ms) {
      this.cycleTimeMs = ms;
      return this;
    }

    public FeedbackLoopBuilder withStages(String... stages) {
      this.stages.clear();
      for (String stage : stages) {
        this.stages.add(stage);
      }
      return this;
    }

    public FeedbackLoopBuilder addStage(String stage) {
      this.stages.add(stage);
      return this;
    }

    public FeedbackLoopRecord build() {
      return new FeedbackLoopRecord(id, status, cycleTimeMs, stages, startTime, endTime);
    }
  }

  /** Immutable feedback loop record. */
  public static class FeedbackLoopRecord {
    private final String id;
    private final String status;
    private final long cycleTimeMs;
    private final List<String> stages;
    private final Instant startTime;
    private final Instant endTime;

    public FeedbackLoopRecord(
        String id,
        String status,
        long cycleTimeMs,
        List<String> stages,
        Instant startTime,
        Instant endTime) {
      this.id = id;
      this.status = status;
      this.cycleTimeMs = cycleTimeMs;
      this.stages = new ArrayList<>(stages);
      this.startTime = startTime;
      this.endTime = endTime;
    }

    public String getId() {
      return id;
    }

    public String getStatus() {
      return status;
    }

    public long getCycleTimeMs() {
      return cycleTimeMs;
    }

    public List<String> getStages() {
      return new ArrayList<>(stages);
    }

    public Instant getStartTime() {
      return startTime;
    }

    public Instant getEndTime() {
      return endTime;
    }

    public boolean isClosed() {
      return "closed".equals(status);
    }
  }

  /** Builder for experience records. */
  public static class ExperienceBuilder {
    private String signalId;
    private String type;
    private String outcome;
    private Instant timestamp;
    private String componentId;
    private double significance = 1.0;
    private Map<String, Object> metadata;

    ExperienceBuilder() {
      this.signalId = UUID.randomUUID().toString();
      this.timestamp = Instant.now();
      this.metadata = new HashMap<>();
    }

    public ExperienceBuilder withSignalId(String id) {
      this.signalId = id;
      return this;
    }

    public ExperienceBuilder withType(String type) {
      this.type = type;
      return this;
    }

    public ExperienceBuilder withOutcome(String outcome) {
      this.outcome = outcome;
      return this;
    }

    public ExperienceBuilder withTimestamp(Instant timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public ExperienceBuilder withComponentId(String id) {
      this.componentId = id;
      return this;
    }

    public ExperienceBuilder withSignificance(double significance) {
      this.significance = significance;
      return this;
    }

    public ExperienceBuilder withMetadata(String key, Object value) {
      this.metadata.put(key, value);
      return this;
    }

    public Map<String, Object> build() {
      Map<String, Object> experience = new HashMap<>();
      experience.put("signalId", signalId);
      experience.put("type", type);
      experience.put("outcome", outcome);
      experience.put("timestamp", timestamp.toString());
      experience.put("componentId", componentId);
      experience.put("significance", significance);
      experience.putAll(metadata);
      return experience;
    }
  }

  /** Builder for emergent pattern records. */
  public static class EmergentPatternBuilder {
    private String patternId;
    private String classification = "emergent";
    private Instant discoveryTime;
    private double frequency = 0.0;
    private List<String> participatingComponents;
    private Map<String, Object> characteristics;

    EmergentPatternBuilder() {
      this.patternId = "PATTERN_" + UUID.randomUUID().toString().substring(0, 8);
      this.discoveryTime = Instant.now();
      this.participatingComponents = new ArrayList<>();
      this.characteristics = new HashMap<>();
    }

    public EmergentPatternBuilder withPatternId(String id) {
      this.patternId = id;
      return this;
    }

    public EmergentPatternBuilder withClassification(String classification) {
      this.classification = classification;
      return this;
    }

    public EmergentPatternBuilder withFrequency(double frequency) {
      this.frequency = frequency;
      return this;
    }

    public EmergentPatternBuilder withParticipant(String componentId) {
      this.participatingComponents.add(componentId);
      return this;
    }

    public EmergentPatternBuilder withCharacteristic(String key, Object value) {
      this.characteristics.put(key, value);
      return this;
    }

    public Map<String, Object> build() {
      Map<String, Object> pattern = new HashMap<>();
      pattern.put("pattern_id", patternId);
      pattern.put("classification", classification);
      pattern.put("discovery_time", discoveryTime.toString());
      pattern.put("frequency", frequency);
      pattern.put("participating_components", new ArrayList<>(participatingComponents));
      pattern.putAll(characteristics);
      return pattern;
    }
  }
}
