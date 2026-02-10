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

package org.s8r.infrastructure.consciousness;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.consciousness.ComponentNarrative;
import org.s8r.application.port.consciousness.ConsciousnessLoggerPort;
import org.s8r.application.port.consciousness.DecisionPoint;
import org.s8r.application.port.consciousness.FeedbackLoopMetrics;
import org.s8r.application.port.consciousness.FeedbackLoopPort;
import org.s8r.application.port.consciousness.IdentityChain;
import org.s8r.application.port.consciousness.IdentityChainPort;
import org.s8r.application.port.consciousness.NarrativePort;
import org.s8r.application.port.consciousness.ObservationContext;

/**
 * Infrastructure adapter implementing consciousness-aware logging.
 *
 * <p>This adapter provides a concrete implementation of the {@link ConsciousnessLoggerPort}
 * interface, integrating the philosophical consciousness model with practical logging operations.
 * It follows Clean Architecture principles by implementing application layer ports in the
 * infrastructure layer.
 *
 * <p>The adapter maintains in-memory state for narratives, feedback loops, and identity chains,
 * delegating actual log output to the underlying {@link LoggerPort}.
 *
 * @see ConsciousnessLoggerPort
 * @see NarrativePort
 * @see FeedbackLoopPort
 * @see IdentityChainPort
 */
public class ConsciousnessLoggerAdapter implements ConsciousnessLoggerPort {

  private final LoggerPort baseLogger;
  private final InMemoryNarrativePort narrativePort;
  private final InMemoryFeedbackLoopPort feedbackLoopPort;
  private final InMemoryIdentityChainPort identityChainPort;
  private volatile boolean consciousnessLoggingEnabled;

  /**
   * Creates a new ConsciousnessLoggerAdapter.
   *
   * @param baseLogger the underlying logger for actual log output
   */
  public ConsciousnessLoggerAdapter(LoggerPort baseLogger) {
    this.baseLogger = baseLogger;
    this.narrativePort = new InMemoryNarrativePort();
    this.feedbackLoopPort = new InMemoryFeedbackLoopPort();
    this.identityChainPort = new InMemoryIdentityChainPort();
    this.consciousnessLoggingEnabled = true;
  }

  // ===================== Self-Observation Layer =====================

  @Override
  public void logStateTransition(String componentId, ObservationContext observation) {
    if (!consciousnessLoggingEnabled) {
      baseLogger.info(
          "[{}] State transition: {} -> {}",
          componentId,
          observation.getPreviousState(),
          observation.getCurrentState());
      return;
    }

    baseLogger.info(
        "[CONSCIOUSNESS] Component {} transitioned: {} -> {} | Rationale: {}",
        componentId,
        observation.getPreviousState(),
        observation.getCurrentState(),
        observation.getTransitionRationale().orElse("not specified"));
  }

  @Override
  public void logDecision(String componentId, DecisionPoint decision) {
    if (!consciousnessLoggingEnabled) {
      baseLogger.debug("[{}] Decision: {}", componentId, decision.getChosenPath());
      return;
    }

    baseLogger.info(
        "[CONSCIOUSNESS] Component {} at decision point {}: chose '{}' over {} | Rationale: {}",
        componentId,
        decision.getDecisionId(),
        decision.getChosenPath(),
        decision.getAlternatives(),
        decision.getRationale().orElse("not specified"));

    identityChainPort.recordDecision(componentId, decision);
  }

  @Override
  public void logErrorWithIdentityContext(
      String componentId,
      String message,
      Throwable throwable,
      Map<String, Object> additionalContext) {

    if (!consciousnessLoggingEnabled) {
      baseLogger.error("[{}] {}", componentId, message, throwable);
      return;
    }

    Optional<IdentityChain> chain = identityChainPort.getIdentityChain(componentId);
    StringBuilder contextLog = new StringBuilder();
    contextLog.append("[CONSCIOUSNESS ERROR] Component: ").append(componentId);

    chain.ifPresent(
        ic -> {
          contextLog.append(" | Lineage: ").append(ic.getLineage());
          ic.getHierarchicalAddress()
              .ifPresent(addr -> contextLog.append(" | Address: ").append(addr));
        });

    contextLog.append(" | Message: ").append(message);

    if (additionalContext != null && !additionalContext.isEmpty()) {
      contextLog.append(" | Context: ").append(additionalContext);
    }

    if (throwable != null) {
      baseLogger.error(contextLog.toString(), throwable);
    } else {
      baseLogger.error(contextLog.toString());
    }
  }

  @Override
  public Optional<String> logSelfObservation(String componentId, ObservationContext observation) {
    if (!consciousnessLoggingEnabled) {
      baseLogger.debug("[{}] Self-observation recorded", componentId);
      return Optional.empty();
    }

    String loopId = feedbackLoopPort.startLoop(componentId);
    feedbackLoopPort.recordObservation(loopId, componentId);

    baseLogger.debug(
        "[CONSCIOUSNESS] Component {} self-observation | Loop: {} | Delay: {}ms",
        componentId,
        loopId,
        observation.getReconstructionDelay().toMillis());

    if (feedbackLoopPort.isLoopClosed(loopId)) {
      return Optional.of(loopId);
    }
    return Optional.empty();
  }

  // ===================== Narrative Logging =====================

  @Override
  public void logWithNarrative(
      String componentId, String level, String message, ComponentNarrative narrative) {

    if (!consciousnessLoggingEnabled) {
      logAtLevel(level, "[{}] {}", componentId, message);
      return;
    }

    String narrativeContext =
        String.format(
            "[CONSCIOUSNESS] %s | What: %s | Why: %s | Relates to: %s | Message: %s",
            componentId,
            narrative.getWhatAmI(),
            narrative.getWhyDoIExist(),
            narrative.getWhoDoIRelateTo(),
            message);

    logAtLevel(level, narrativeContext);
  }

  @Override
  public void logNarrativeEvolution(
      String componentId,
      ComponentNarrative previousNarrative,
      ComponentNarrative newNarrative,
      String reason) {

    if (!consciousnessLoggingEnabled) {
      baseLogger.info("[{}] Narrative evolved: {}", componentId, reason);
      return;
    }

    baseLogger.info(
        "[CONSCIOUSNESS] Component {} narrative evolved | Reason: {} | "
            + "Previous: '{}' -> New: '{}'",
        componentId,
        reason,
        previousNarrative.getWhatAmI(),
        newNarrative.getWhatAmI());
  }

  @Override
  public void logRelationshipChange(
      String componentId, String relatedComponentId, String changeType, String relationshipType) {

    if (!consciousnessLoggingEnabled) {
      baseLogger.debug(
          "[{}] Relationship {}: {} ({})",
          componentId,
          changeType,
          relatedComponentId,
          relationshipType);
      return;
    }

    baseLogger.info(
        "[CONSCIOUSNESS] Component {} relationship {}: {} ({}) with {}",
        componentId,
        changeType,
        relationshipType,
        changeType,
        relatedComponentId);

    if ("added".equalsIgnoreCase(changeType)) {
      narrativePort.addRelationship(componentId, relatedComponentId, relationshipType);
    } else if ("removed".equalsIgnoreCase(changeType)) {
      narrativePort.removeRelationship(componentId, relatedComponentId);
    }
  }

  // ===================== Feedback Loop Logging =====================

  @Override
  public void logFeedbackLoopClosure(FeedbackLoopMetrics metrics) {
    if (!consciousnessLoggingEnabled) {
      baseLogger.debug("Feedback loop {} closed", metrics.getLoopId());
      return;
    }

    baseLogger.info(
        "[CONSCIOUSNESS] Feedback loop CLOSED | Loop: {} | Component: {} | "
            + "Chain: {} | Duration: {}ms | Total closures: {}",
        metrics.getLoopId(),
        metrics.getComponentId(),
        metrics.getObserverChain(),
        metrics.getClosureDuration().map(Duration::toMillis).orElse(0L),
        metrics.getTotalClosuresInSession());
  }

  @Override
  public void logAdaptation(
      String loopId, String adaptation, FeedbackLoopMetrics.AdaptationOutcome outcome) {

    if (!consciousnessLoggingEnabled) {
      baseLogger.debug("Adaptation for loop {}: {} ({})", loopId, adaptation, outcome);
      return;
    }

    baseLogger.info(
        "[CONSCIOUSNESS] Adaptation after loop {} | Action: {} | Outcome: {}",
        loopId,
        adaptation,
        outcome);

    feedbackLoopPort.recordAdaptation(loopId, outcome);
  }

  // ===================== Identity Chain Logging =====================

  @Override
  public void logWithIdentityChain(IdentityChain identityChain, String level, String message) {
    if (!consciousnessLoggingEnabled) {
      logAtLevel(level, message);
      return;
    }

    String identityContext =
        String.format(
            "[CONSCIOUSNESS] [%s] %s | Lineage: %s | Address: %s | Message: %s",
            identityChain.getShortId(),
            identityChain.getUuid(),
            identityChain.getLineage(),
            identityChain.getHierarchicalAddress().orElse("unknown"),
            message);

    logAtLevel(level, identityContext);
  }

  @Override
  public void logLineage(String componentId, List<String> lineage) {
    if (!consciousnessLoggingEnabled) {
      baseLogger.debug("[{}] Lineage: {}", componentId, lineage);
      return;
    }

    baseLogger.info(
        "[CONSCIOUSNESS] Component {} lineage from Adam: {}",
        componentId,
        String.join(" -> ", lineage));
  }

  // ===================== Utility Methods =====================

  @Override
  public LoggerPort getBaseLogger() {
    return baseLogger;
  }

  @Override
  public NarrativePort getNarrativePort() {
    return narrativePort;
  }

  @Override
  public FeedbackLoopPort getFeedbackLoopPort() {
    return feedbackLoopPort;
  }

  @Override
  public IdentityChainPort getIdentityChainPort() {
    return identityChainPort;
  }

  @Override
  public boolean isConsciousnessLoggingEnabled() {
    return consciousnessLoggingEnabled;
  }

  @Override
  public void setConsciousnessLoggingEnabled(boolean enabled) {
    this.consciousnessLoggingEnabled = enabled;
  }

  private void logAtLevel(String level, String message, Object... args) {
    switch (level.toLowerCase()) {
      case "trace":
        baseLogger.trace(message, args);
        break;
      case "debug":
        baseLogger.debug(message, args);
        break;
      case "info":
        baseLogger.info(message, args);
        break;
      case "warn":
        baseLogger.warn(message, args);
        break;
      case "error":
        baseLogger.error(message, args);
        break;
      default:
        baseLogger.info(message, args);
    }
  }

  // ===================== Inner Port Implementations =====================

  /** In-memory implementation of NarrativePort. */
  private static class InMemoryNarrativePort implements NarrativePort {
    private final Map<String, ComponentNarrative> narratives = new ConcurrentHashMap<>();
    private final Map<String, List<String>> evolutionHistories = new ConcurrentHashMap<>();

    @Override
    public Optional<ComponentNarrative> getNarrative(String componentId) {
      return Optional.ofNullable(narratives.get(componentId));
    }

    @Override
    public Optional<String> whatAmI(String componentId) {
      return getNarrative(componentId).map(ComponentNarrative::getWhatAmI);
    }

    @Override
    public Optional<String> whyDoIExist(String componentId) {
      return getNarrative(componentId).map(ComponentNarrative::getWhyDoIExist);
    }

    @Override
    public List<String> whoDoIRelateTo(String componentId) {
      return getNarrative(componentId)
          .map(ComponentNarrative::getWhoDoIRelateTo)
          .orElse(Collections.emptyList());
    }

    @Override
    public ComponentNarrative establishNarrative(
        String componentId, String whatAmI, String whyDoIExist, List<String> relationships) {
      ComponentNarrative narrative =
          ComponentNarrative.builder()
              .componentId(componentId)
              .whatAmI(whatAmI)
              .whyDoIExist(whyDoIExist)
              .whoDoIRelateTo(relationships)
              .capturedNow()
              .build();
      narratives.put(componentId, narrative);
      evolutionHistories.put(componentId, new CopyOnWriteArrayList<>());
      return narrative;
    }

    @Override
    public void updateWhatAmI(String componentId, String newDescription, String reason) {
      ComponentNarrative current = narratives.get(componentId);
      if (current != null) {
        ComponentNarrative updated =
            ComponentNarrative.builder()
                .componentId(componentId)
                .whatAmI(newDescription)
                .whyDoIExist(current.getWhyDoIExist())
                .whoDoIRelateTo(current.getWhoDoIRelateTo())
                .currentPurposeUnderstanding(current.getCurrentPurposeUnderstanding().orElse(null))
                .evolutionHistory(current.getEvolutionHistory())
                .capturedNow()
                .build();
        narratives.put(componentId, updated);
        evolutionHistories
            .computeIfAbsent(componentId, k -> new CopyOnWriteArrayList<>())
            .add("Updated 'what am I': " + reason);
      }
    }

    @Override
    public void updatePurposeUnderstanding(
        String componentId, String newUnderstanding, String reason) {
      ComponentNarrative current = narratives.get(componentId);
      if (current != null) {
        ComponentNarrative updated =
            ComponentNarrative.builder()
                .componentId(componentId)
                .whatAmI(current.getWhatAmI())
                .whyDoIExist(current.getWhyDoIExist())
                .whoDoIRelateTo(current.getWhoDoIRelateTo())
                .currentPurposeUnderstanding(newUnderstanding)
                .evolutionHistory(current.getEvolutionHistory())
                .capturedNow()
                .build();
        narratives.put(componentId, updated);
        evolutionHistories
            .computeIfAbsent(componentId, k -> new CopyOnWriteArrayList<>())
            .add("Purpose evolved: " + reason);
      }
    }

    @Override
    public void addRelationship(
        String componentId, String relatedComponentId, String relationshipType) {
      ComponentNarrative current = narratives.get(componentId);
      if (current != null) {
        List<String> newRelationships = new ArrayList<>(current.getWhoDoIRelateTo());
        newRelationships.add(relatedComponentId + ":" + relationshipType);
        ComponentNarrative updated =
            ComponentNarrative.builder()
                .componentId(componentId)
                .whatAmI(current.getWhatAmI())
                .whyDoIExist(current.getWhyDoIExist())
                .whoDoIRelateTo(newRelationships)
                .currentPurposeUnderstanding(current.getCurrentPurposeUnderstanding().orElse(null))
                .capturedNow()
                .build();
        narratives.put(componentId, updated);
      }
    }

    @Override
    public void removeRelationship(String componentId, String relatedComponentId) {
      ComponentNarrative current = narratives.get(componentId);
      if (current != null) {
        List<String> newRelationships = new ArrayList<>(current.getWhoDoIRelateTo());
        newRelationships.removeIf(r -> r.startsWith(relatedComponentId + ":"));
        ComponentNarrative updated =
            ComponentNarrative.builder()
                .componentId(componentId)
                .whatAmI(current.getWhatAmI())
                .whyDoIExist(current.getWhyDoIExist())
                .whoDoIRelateTo(newRelationships)
                .currentPurposeUnderstanding(current.getCurrentPurposeUnderstanding().orElse(null))
                .capturedNow()
                .build();
        narratives.put(componentId, updated);
      }
    }

    @Override
    public List<String> getNarrativeEvolutionHistory(String componentId) {
      return evolutionHistories.getOrDefault(componentId, Collections.emptyList());
    }

    @Override
    public boolean hasNarrative(String componentId) {
      return narratives.containsKey(componentId);
    }
  }

  /** In-memory implementation of FeedbackLoopPort. */
  private static class InMemoryFeedbackLoopPort implements FeedbackLoopPort {
    private final Map<String, LoopState> loops = new ConcurrentHashMap<>();
    private final Map<String, List<LoopClosureListener>> listeners = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> closureCounts = new ConcurrentHashMap<>();

    @Override
    public String startLoop(String componentId) {
      String loopId = "FL-" + UUID.randomUUID().toString().substring(0, 8);
      loops.put(loopId, new LoopState(componentId, Instant.now()));
      return loopId;
    }

    @Override
    public void recordObservation(String loopId, String observerId) {
      LoopState state = loops.get(loopId);
      if (state != null) {
        state.observerChain.add(observerId);
        if (state.observerChain.size() > 1 && state.observerChain.get(0).equals(observerId)) {
          state.closed = true;
          state.closureTime = Instant.now();
          closureCounts.computeIfAbsent(state.componentId, k -> new AtomicLong()).incrementAndGet();

          FeedbackLoopMetrics metrics = buildMetrics(loopId, state);
          List<LoopClosureListener> componentListeners = listeners.get(state.componentId);
          if (componentListeners != null) {
            componentListeners.forEach(l -> l.onClosure(metrics));
          }
        }
      }
    }

    @Override
    public boolean isLoopClosed(String loopId) {
      LoopState state = loops.get(loopId);
      return state != null && state.closed;
    }

    @Override
    public List<String> getObserverChain(String loopId) {
      LoopState state = loops.get(loopId);
      return state != null
          ? Collections.unmodifiableList(state.observerChain)
          : Collections.emptyList();
    }

    @Override
    public Optional<FeedbackLoopMetrics> getLoopMetrics(String loopId) {
      LoopState state = loops.get(loopId);
      return state != null ? Optional.of(buildMetrics(loopId, state)) : Optional.empty();
    }

    @Override
    public FeedbackLoopMetrics getAggregateMetrics(String componentId) {
      long totalClosures = closureCounts.getOrDefault(componentId, new AtomicLong()).get();
      return FeedbackLoopMetrics.builder()
          .loopId("aggregate-" + componentId)
          .componentId(componentId)
          .totalClosuresInSession(totalClosures)
          .build();
    }

    @Override
    public long getTotalClosures(String componentId) {
      return closureCounts.getOrDefault(componentId, new AtomicLong()).get();
    }

    @Override
    public Optional<Duration> getAverageClosureDuration(String componentId) {
      return Optional.empty(); // Simplified: would need to track durations
    }

    @Override
    public double getClosureFrequency(String componentId) {
      return 0.0; // Simplified: would need time tracking
    }

    @Override
    public void onLoopClosure(String componentId, LoopClosureListener listener) {
      listeners.computeIfAbsent(componentId, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    @Override
    public void removeClosureListener(String componentId, LoopClosureListener listener) {
      List<LoopClosureListener> componentListeners = listeners.get(componentId);
      if (componentListeners != null) {
        componentListeners.remove(listener);
      }
    }

    @Override
    public void recordAdaptation(String loopId, FeedbackLoopMetrics.AdaptationOutcome outcome) {
      LoopState state = loops.get(loopId);
      if (state != null) {
        state.adaptationOutcome = outcome;
      }
    }

    @Override
    public void endLoop(String loopId) {
      loops.remove(loopId);
    }

    private FeedbackLoopMetrics buildMetrics(String loopId, LoopState state) {
      Duration duration =
          state.closureTime != null ? Duration.between(state.startTime, state.closureTime) : null;
      return FeedbackLoopMetrics.builder()
          .loopId(loopId)
          .componentId(state.componentId)
          .loopStartTime(state.startTime)
          .loopClosureTime(state.closureTime)
          .observerChain(new ArrayList<>(state.observerChain))
          .closureDetected(state.closed)
          .closureDuration(duration)
          .totalClosuresInSession(
              closureCounts.getOrDefault(state.componentId, new AtomicLong()).get())
          .adaptationOutcome(state.adaptationOutcome)
          .build();
    }

    private static class LoopState {
      final String componentId;
      final Instant startTime;
      final List<String> observerChain = new CopyOnWriteArrayList<>();
      volatile boolean closed;
      volatile Instant closureTime;
      volatile FeedbackLoopMetrics.AdaptationOutcome adaptationOutcome;

      LoopState(String componentId, Instant startTime) {
        this.componentId = componentId;
        this.startTime = startTime;
      }
    }
  }

  /** In-memory implementation of IdentityChainPort. */
  private static class InMemoryIdentityChainPort implements IdentityChainPort {
    private final Map<String, IdentityChain> chains = new ConcurrentHashMap<>();
    private final Map<String, List<String>> lineages = new ConcurrentHashMap<>();
    private final Map<String, List<String>> decisions = new ConcurrentHashMap<>();

    @Override
    public Optional<IdentityChain> getIdentityChain(String componentId) {
      return Optional.ofNullable(chains.get(componentId));
    }

    @Override
    public IdentityChain buildIdentityChain(String componentId) {
      List<String> lineage = lineages.getOrDefault(componentId, Collections.emptyList());
      IdentityChain chain =
          IdentityChain.builder()
              .uuid(componentId + "-" + UUID.randomUUID().toString())
              .lineage(lineage)
              .conceptionTime(Instant.now())
              .build();
      chains.put(componentId, chain);
      return chain;
    }

    @Override
    public List<String> getLineage(String componentId) {
      return lineages.getOrDefault(componentId, Collections.emptyList());
    }

    @Override
    public String traceToAdam(String componentId) {
      List<String> lineage = lineages.get(componentId);
      if (lineage != null && !lineage.isEmpty()) {
        return lineage.get(0);
      }
      return componentId; // Component is its own Adam
    }

    @Override
    public boolean shareCommonAncestor(String componentId1, String componentId2) {
      String adam1 = traceToAdam(componentId1);
      String adam2 = traceToAdam(componentId2);
      return adam1.equals(adam2);
    }

    @Override
    public List<String> getDescendants(String componentId) {
      List<String> descendants = new ArrayList<>();
      for (Map.Entry<String, List<String>> entry : lineages.entrySet()) {
        if (entry.getValue().contains(componentId)) {
          descendants.add(entry.getKey());
        }
      }
      return descendants;
    }

    @Override
    public List<String> getSiblings(String componentId) {
      List<String> lineage = lineages.get(componentId);
      if (lineage == null || lineage.isEmpty()) {
        return Collections.emptyList();
      }
      String parent = lineage.get(lineage.size() - 1);
      List<String> siblings = new ArrayList<>();
      for (Map.Entry<String, List<String>> entry : lineages.entrySet()) {
        List<String> otherLineage = entry.getValue();
        if (!entry.getKey().equals(componentId)
            && !otherLineage.isEmpty()
            && otherLineage.get(otherLineage.size() - 1).equals(parent)) {
          siblings.add(entry.getKey());
        }
      }
      return siblings;
    }

    @Override
    public void updateStateNarrative(String componentId, String narrative, String rationale) {
      IdentityChain current = chains.get(componentId);
      if (current != null) {
        IdentityChain updated =
            IdentityChain.builder()
                .uuid(current.getUuid())
                .lineage(current.getLineage())
                .stateNarrative(narrative)
                .conceptionTime(current.getConceptionTime())
                .build();
        chains.put(componentId, updated);
      }
    }

    @Override
    public void recordDecision(String componentId, DecisionPoint decision) {
      decisions
          .computeIfAbsent(componentId, k -> new CopyOnWriteArrayList<>())
          .add(decision.getDecisionId() + ": " + decision.getChosenPath());
    }

    @Override
    public List<String> getRecentDecisions(String componentId, int limit) {
      List<String> componentDecisions =
          decisions.getOrDefault(componentId, Collections.emptyList());
      int size = componentDecisions.size();
      if (size <= limit) {
        return new ArrayList<>(componentDecisions);
      }
      return new ArrayList<>(componentDecisions.subList(size - limit, size));
    }

    @Override
    public void recordLearnedBehavior(
        String componentId, String behaviorKey, Object behaviorValue) {
      // Simplified: just log it
    }

    @Override
    public Optional<String> getHierarchicalAddress(String componentId) {
      IdentityChain chain = chains.get(componentId);
      return chain != null ? chain.getHierarchicalAddress() : Optional.empty();
    }
  }
}
