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
 * Licensed under the Mozilla Public License 2.0
 */

package org.s8r.test.mock;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Mock adapters for consciousness testing.
 *
 * <p>Provides hand-rolled mock implementations for the consciousness testing infrastructure,
 * supporting the 300-scenario consciousness test suite.
 *
 * <p>Available mock adapters:
 *
 * <ul>
 *   <li>MockObservationAdapter - Self-observation simulation
 *   <li>MockFeedbackAdapter - Feedback loop simulation
 *   <li>MockMemoryAdapter - Memory persistence verification
 *   <li>MockEmergenceAdapter - Emergent behavior detection
 * </ul>
 *
 * @see <a
 *     href="docs/architecture/decisions/0014-adopt-contract-first-testing-strategy.md">ADR-0014</a>
 */
public final class MockConsciousnessAdapters {

  private MockConsciousnessAdapters() {
    // Utility class - no instantiation
  }

  // =========================================================================
  // Factory Methods
  // =========================================================================

  private static MockObservationAdapter observationAdapter;
  private static MockFeedbackAdapter feedbackAdapter;
  private static MockMemoryAdapter memoryAdapter;
  private static MockEmergenceAdapter emergenceAdapter;

  /** Returns singleton MockObservationAdapter instance. */
  public static synchronized MockObservationAdapter observation() {
    if (observationAdapter == null) {
      observationAdapter = new MockObservationAdapter();
    }
    return observationAdapter;
  }

  /** Returns singleton MockFeedbackAdapter instance. */
  public static synchronized MockFeedbackAdapter feedback() {
    if (feedbackAdapter == null) {
      feedbackAdapter = new MockFeedbackAdapter();
    }
    return feedbackAdapter;
  }

  /** Returns singleton MockMemoryAdapter instance. */
  public static synchronized MockMemoryAdapter memory() {
    if (memoryAdapter == null) {
      memoryAdapter = new MockMemoryAdapter();
    }
    return memoryAdapter;
  }

  /** Returns singleton MockEmergenceAdapter instance. */
  public static synchronized MockEmergenceAdapter emergence() {
    if (emergenceAdapter == null) {
      emergenceAdapter = new MockEmergenceAdapter();
    }
    return emergenceAdapter;
  }

  /** Resets all singleton instances. Call between tests for isolation. */
  public static synchronized void resetAll() {
    observationAdapter = null;
    feedbackAdapter = null;
    memoryAdapter = null;
    emergenceAdapter = null;
  }

  // =========================================================================
  // MockObservationAdapter
  // =========================================================================

  /**
   * Mock adapter for self-observation simulation.
   *
   * <p>Simulates the consciousness self-observation subsystem, capturing observation records,
   * tracking recursion depth, and managing observation history.
   */
  public static class MockObservationAdapter {
    private final List<Map<String, Object>> observations;
    private final Map<String, List<Map<String, Object>>> componentObservations;
    private int maxDepth = 3;
    private boolean enabled = true;
    private final AtomicInteger observationCount = new AtomicInteger(0);

    public MockObservationAdapter() {
      this.observations = new ArrayList<>();
      this.componentObservations = new ConcurrentHashMap<>();
    }

    /**
     * Records a self-observation for a component.
     *
     * @param observerId The ID of the observing component
     * @param observedId The ID of the observed component (same for self-observation)
     * @param stateSnapshot The state at time of observation
     * @param depth The recursion depth of this observation
     * @return The observation record
     */
    public Map<String, Object> observe(
        String observerId, String observedId, String stateSnapshot, int depth) {
      if (!enabled) {
        return null;
      }

      if (depth > maxDepth) {
        return null; // Prevent infinite recursion
      }

      Map<String, Object> observation = new HashMap<>();
      observation.put("observation_id", UUID.randomUUID().toString());
      observation.put("observer_id", observerId);
      observation.put("observed_id", observedId);
      observation.put("state_snapshot", stateSnapshot);
      observation.put("observation_ts", Instant.now().toString());
      observation.put("depth", depth);
      observation.put("is_self", observerId.equals(observedId));
      observation.put("sequence", observationCount.incrementAndGet());

      observations.add(observation);
      componentObservations.computeIfAbsent(observerId, k -> new ArrayList<>()).add(observation);

      return observation;
    }

    /**
     * Triggers a full self-observation cycle with recursive meta-observation.
     *
     * @param componentId The component to observe
     * @param state The component's current state
     * @return List of observations at each depth level
     */
    public List<Map<String, Object>> triggerFullObservation(String componentId, String state) {
      List<Map<String, Object>> depthObservations = new ArrayList<>();

      for (int depth = 1; depth <= maxDepth; depth++) {
        String description = getDepthDescription(depth);
        Map<String, Object> obs =
            observe(componentId, componentId, state + " - " + description, depth);
        if (obs != null) {
          depthObservations.add(obs);
        }
      }

      return depthObservations;
    }

    private String getDepthDescription(int depth) {
      return switch (depth) {
        case 1 -> "observing state";
        case 2 -> "observing the act of observing";
        case 3 -> "aware of awareness of observation";
        default -> "meta-observation level " + depth;
      };
    }

    public List<Map<String, Object>> getObservations() {
      return new ArrayList<>(observations);
    }

    public List<Map<String, Object>> getObservationsFor(String componentId) {
      return new ArrayList<>(componentObservations.getOrDefault(componentId, new ArrayList<>()));
    }

    public int getObservationCount() {
      return observationCount.get();
    }

    public void setMaxDepth(int maxDepth) {
      this.maxDepth = maxDepth;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public boolean isEnabled() {
      return enabled;
    }

    public void clear() {
      observations.clear();
      componentObservations.clear();
      observationCount.set(0);
    }
  }

  // =========================================================================
  // MockFeedbackAdapter
  // =========================================================================

  /**
   * Mock adapter for feedback loop simulation.
   *
   * <p>Simulates the consciousness feedback loop subsystem, managing loop creation, stage tracking,
   * closure detection, and timing measurement.
   */
  public static class MockFeedbackAdapter {
    private final Map<String, FeedbackLoop> activeLoops;
    private final List<FeedbackLoop> completedLoops;
    private boolean enabled = true;
    private long defaultTimeout = 1000; // ms

    public MockFeedbackAdapter() {
      this.activeLoops = new ConcurrentHashMap<>();
      this.completedLoops = new ArrayList<>();
    }

    /**
     * Starts a new feedback loop.
     *
     * @param componentId The component starting the loop
     * @return The loop ID
     */
    public String startLoop(String componentId) {
      if (!enabled) {
        return null;
      }

      String loopId = UUID.randomUUID().toString();
      FeedbackLoop loop = new FeedbackLoop(loopId, componentId);
      activeLoops.put(loopId, loop);
      return loopId;
    }

    /**
     * Adds a stage to an active loop.
     *
     * @param loopId The loop ID
     * @param stage The stage name
     * @param result The stage result (optional)
     */
    public void addStage(String loopId, String stage, Object result) {
      FeedbackLoop loop = activeLoops.get(loopId);
      if (loop != null) {
        loop.addStage(stage, result);
      }
    }

    /**
     * Closes a feedback loop.
     *
     * @param loopId The loop ID
     * @return The completed loop record
     */
    public FeedbackLoop closeLoop(String loopId) {
      FeedbackLoop loop = activeLoops.remove(loopId);
      if (loop != null) {
        loop.close();
        completedLoops.add(loop);
      }
      return loop;
    }

    /**
     * Executes a complete feedback cycle.
     *
     * @param componentId The component ID
     * @param stages The stages to execute
     * @return The completed loop
     */
    public FeedbackLoop executeCycle(String componentId, String... stages) {
      String loopId = startLoop(componentId);
      for (String stage : stages) {
        addStage(loopId, stage, null);
      }
      return closeLoop(loopId);
    }

    public FeedbackLoop getActiveLoop(String loopId) {
      return activeLoops.get(loopId);
    }

    public List<FeedbackLoop> getCompletedLoops() {
      return new ArrayList<>(completedLoops);
    }

    public int getActiveLoopCount() {
      return activeLoops.size();
    }

    public int getCompletedLoopCount() {
      return completedLoops.size();
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public void clear() {
      activeLoops.clear();
      completedLoops.clear();
    }

    /** Feedback loop record. */
    public static class FeedbackLoop {
      private final String id;
      private final String componentId;
      private final long startTime;
      private long endTime;
      private String status;
      private final List<Stage> stages;

      public FeedbackLoop(String id, String componentId) {
        this.id = id;
        this.componentId = componentId;
        this.startTime = System.currentTimeMillis();
        this.status = "open";
        this.stages = new ArrayList<>();
      }

      public void addStage(String name, Object result) {
        stages.add(new Stage(name, result, System.currentTimeMillis()));
      }

      public void close() {
        this.endTime = System.currentTimeMillis();
        this.status = "closed";
      }

      public String getId() {
        return id;
      }

      public String getComponentId() {
        return componentId;
      }

      public String getStatus() {
        return status;
      }

      public long getCycleTimeMs() {
        return endTime - startTime;
      }

      public List<Stage> getStages() {
        return new ArrayList<>(stages);
      }

      public boolean isClosed() {
        return "closed".equals(status);
      }
    }

    /** Stage record within a feedback loop. */
    public static class Stage {
      private final String name;
      private final Object result;
      private final long timestamp;

      public Stage(String name, Object result, long timestamp) {
        this.name = name;
        this.result = result;
        this.timestamp = timestamp;
      }

      public String getName() {
        return name;
      }

      public Object getResult() {
        return result;
      }

      public long getTimestamp() {
        return timestamp;
      }
    }
  }

  // =========================================================================
  // MockMemoryAdapter
  // =========================================================================

  /**
   * Mock adapter for memory persistence verification.
   *
   * <p>Simulates the consciousness memory subsystem, storing experiences, managing memory chains,
   * implementing significance decay, and supporting memory queries.
   */
  public static class MockMemoryAdapter {
    private final Map<String, Map<String, Object>> experiences;
    private final Map<String, List<String>> memoryChains;
    private final Map<String, Double> significanceScores;
    private int maxMemorySize = 1000;
    private boolean enabled = true;

    public MockMemoryAdapter() {
      this.experiences = new ConcurrentHashMap<>();
      this.memoryChains = new ConcurrentHashMap<>();
      this.significanceScores = new ConcurrentHashMap<>();
    }

    /**
     * Stores an experience in memory.
     *
     * @param experienceId The experience ID
     * @param experience The experience data
     * @param componentId The component that had the experience
     * @param significance The significance score (0.0 to 1.0)
     */
    public void store(
        String experienceId,
        Map<String, Object> experience,
        String componentId,
        double significance) {
      if (!enabled) {
        return;
      }

      experiences.put(experienceId, experience);
      significanceScores.put(experienceId, significance);

      // Add to memory chain
      memoryChains.computeIfAbsent(componentId, k -> new ArrayList<>()).add(experienceId);

      // Prune if over capacity
      if (experiences.size() > maxMemorySize) {
        pruneLowestSignificance();
      }
    }

    private void pruneLowestSignificance() {
      String lowestId = null;
      double lowestScore = Double.MAX_VALUE;

      for (Map.Entry<String, Double> entry : significanceScores.entrySet()) {
        if (entry.getValue() < lowestScore) {
          lowestScore = entry.getValue();
          lowestId = entry.getKey();
        }
      }

      if (lowestId != null) {
        experiences.remove(lowestId);
        significanceScores.remove(lowestId);
      }
    }

    /**
     * Retrieves an experience by ID.
     *
     * @param experienceId The experience ID
     * @return The experience data, or null if not found
     */
    public Map<String, Object> retrieve(String experienceId) {
      return experiences.get(experienceId);
    }

    /**
     * Gets the memory chain for a component.
     *
     * @param componentId The component ID
     * @return List of experience IDs in chronological order
     */
    public List<String> getMemoryChain(String componentId) {
      return new ArrayList<>(memoryChains.getOrDefault(componentId, new ArrayList<>()));
    }

    /**
     * Applies significance decay to all memories.
     *
     * @param decayRate The decay rate (0.0 to 1.0)
     */
    public void applyDecay(double decayRate) {
      for (Map.Entry<String, Double> entry : significanceScores.entrySet()) {
        double newScore = entry.getValue() * (1.0 - decayRate);
        entry.setValue(newScore);
      }
    }

    /**
     * Queries memories by significance threshold.
     *
     * @param minSignificance The minimum significance score
     * @return List of experiences meeting the threshold
     */
    public List<Map<String, Object>> queryBySignificance(double minSignificance) {
      List<Map<String, Object>> result = new ArrayList<>();
      for (Map.Entry<String, Double> entry : significanceScores.entrySet()) {
        if (entry.getValue() >= minSignificance) {
          Map<String, Object> exp = experiences.get(entry.getKey());
          if (exp != null) {
            result.add(exp);
          }
        }
      }
      return result;
    }

    public int getMemoryCount() {
      return experiences.size();
    }

    public double getSignificance(String experienceId) {
      return significanceScores.getOrDefault(experienceId, 0.0);
    }

    public void setMaxMemorySize(int size) {
      this.maxMemorySize = size;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public void clear() {
      experiences.clear();
      memoryChains.clear();
      significanceScores.clear();
    }
  }

  // =========================================================================
  // MockEmergenceAdapter
  // =========================================================================

  /**
   * Mock adapter for emergent behavior detection.
   *
   * <p>Simulates the consciousness emergence detection subsystem, capturing patterns, classifying
   * emergent behaviors, and tracking pattern evolution.
   */
  public static class MockEmergenceAdapter {
    private final List<Pattern> detectedPatterns;
    private final Map<String, List<Interaction>> interactionHistory;
    private boolean enabled = true;
    private int patternThreshold = 3; // Minimum occurrences to detect pattern

    public MockEmergenceAdapter() {
      this.detectedPatterns = new ArrayList<>();
      this.interactionHistory = new ConcurrentHashMap<>();
    }

    /**
     * Records an interaction between components.
     *
     * @param sourceId Source component ID
     * @param targetId Target component ID
     * @param interactionType Type of interaction
     */
    public void recordInteraction(String sourceId, String targetId, String interactionType) {
      if (!enabled) {
        return;
      }

      String key = sourceId + "->" + targetId;
      Interaction interaction = new Interaction(sourceId, targetId, interactionType);

      interactionHistory.computeIfAbsent(key, k -> new ArrayList<>()).add(interaction);

      // Check for pattern emergence
      List<Interaction> history = interactionHistory.get(key);
      if (history.size() >= patternThreshold) {
        checkForPattern(key, history);
      }
    }

    private void checkForPattern(String key, List<Interaction> history) {
      // Simple pattern detection: same interaction type repeated
      Map<String, Integer> typeCounts = new HashMap<>();
      for (Interaction interaction : history) {
        typeCounts.merge(interaction.type, 1, Integer::sum);
      }

      for (Map.Entry<String, Integer> entry : typeCounts.entrySet()) {
        if (entry.getValue() >= patternThreshold) {
          Pattern pattern =
              new Pattern(
                  "PATTERN_" + UUID.randomUUID().toString().substring(0, 8),
                  "emergent",
                  key,
                  entry.getKey(),
                  entry.getValue());
          detectedPatterns.add(pattern);
        }
      }
    }

    /**
     * Gets all detected patterns.
     *
     * @return List of detected patterns
     */
    public List<Pattern> getDetectedPatterns() {
      return new ArrayList<>(detectedPatterns);
    }

    /**
     * Classifies a pattern.
     *
     * @param patternId The pattern ID
     * @param classification The classification (beneficial, harmful, neutral)
     */
    public void classifyPattern(String patternId, String classification) {
      for (Pattern pattern : detectedPatterns) {
        if (pattern.id.equals(patternId)) {
          pattern.classification = classification;
          break;
        }
      }
    }

    /**
     * Simulates the detection of an emergent pattern.
     *
     * @param classification The pattern classification
     * @return The detected pattern
     */
    public Pattern simulatePatternDetection(String classification) {
      Pattern pattern =
          new Pattern(
              "PATTERN_" + UUID.randomUUID().toString().substring(0, 8),
              classification,
              "simulated",
              "simulated",
              patternThreshold);
      detectedPatterns.add(pattern);
      return pattern;
    }

    public int getPatternCount() {
      return detectedPatterns.size();
    }

    public void setPatternThreshold(int threshold) {
      this.patternThreshold = threshold;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public void clear() {
      detectedPatterns.clear();
      interactionHistory.clear();
    }

    /** Interaction record. */
    public static class Interaction {
      public final String sourceId;
      public final String targetId;
      public final String type;
      public final long timestamp;

      public Interaction(String sourceId, String targetId, String type) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
      }
    }

    /** Pattern record. */
    public static class Pattern {
      public final String id;
      public String classification;
      public final String interactionKey;
      public final String patternType;
      public final int frequency;
      public final long discoveryTime;

      public Pattern(
          String id,
          String classification,
          String interactionKey,
          String patternType,
          int frequency) {
        this.id = id;
        this.classification = classification;
        this.interactionKey = interactionKey;
        this.patternType = patternType;
        this.frequency = frequency;
        this.discoveryTime = System.currentTimeMillis();
      }
    }
  }
}
