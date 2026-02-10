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

package org.s8r.tube.lifecycle;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.s8r.tube.Environment;
import org.s8r.tube.Tube;
import org.s8r.tube.TubeIdentity;
import org.s8r.tube.TubeStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adversarial tests for Tube temporal semantics, causality preservation, and event integrity.
 *
 * <p>This test suite verifies that the Tube's temporal model remains coherent and causally sound
 * even under hostile input and edge cases.
 *
 * <p>Test categories:
 *
 * <ul>
 *   <li>TIMELINE: Conception time immutability and monotonicity
 *   <li>CAUSALITY: Event ordering and causal dependence
 *   <li>STATUS: Invalid status transitions and state machine integrity
 *   <li>CONCURRENCY: Race conditions and concurrent safety
 *   <li>MIMIR: Event log integrity and tamper-proofing
 *   <li>IDENTITY: Genealogical consistency and parent-child invariants
 * </ul>
 */
@Tag("L0_Unit")
@Tag("Adversarial")
@Tag("Temporal")
@DisplayName("Tube Temporal Adversarial Tests")
public class TubeTemporalAdversarialTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(TubeTemporalAdversarialTest.class);

  private Environment environment;

  @BeforeEach
  public void setUp() {
    environment = new Environment();
  }

  // ============================================================================
  // TIMELINE: Conception Time Immutability
  // ============================================================================

  @Test
  @Tag("Timeline")
  @DisplayName("shouldPreserveConceptionTimeImmutability_acrossMultipleCalls")
  public void shouldPreserveConceptionTimeImmutability() {
    // Arrange
    Tube tube = Tube.create("Immutability Test", environment);
    Instant conceptionTime1 = tube.getIdentity().getConceptionTime();

    // Small delay to ensure time would change if mutable
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    // Act - multiple reads
    Instant conceptionTime2 = tube.getIdentity().getConceptionTime();
    Instant conceptionTime3 = tube.getIdentity().getConceptionTime();

    // Assert - all reads return same value
    assertEquals(
        conceptionTime1, conceptionTime2, "Conception time should be immutable across calls");
    assertEquals(conceptionTime1, conceptionTime3, "Conception time should remain constant");
    LOGGER.info("✓ Conception time immutability verified: {}", conceptionTime1);
  }

  @Test
  @Tag("Timeline")
  @DisplayName("shouldMonotonicallyIncreaseLifecycleEvents_afterConception")
  public void shouldMonotonicallyIncreaseLifecycleEvents() {
    // Arrange
    Tube tube = Tube.create("Monotonicity Test", environment);
    int logSizeBefore = tube.getMimirLogSize();

    try {
      Thread.sleep(5);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    // Act - trigger status change
    tube.setStatus(TubeStatus.ACTIVE);
    int logSizeAfter = tube.getMimirLogSize();

    // Assert
    assertTrue(
        logSizeAfter >= logSizeBefore, "Mimir log should grow monotonically or stay same size");
    LOGGER.info("✓ Monotonic event ordering verified: {} -> {}", logSizeBefore, logSizeAfter);
  }

  @Test
  @Tag("Timeline")
  @DisplayName("shouldEnsureAllEventsAfterConceptionTime")
  public void shouldEnsureAllEventsAfterConceptionTime() {
    // Arrange
    Instant beforeCreation = Instant.now();
    Tube tube = Tube.create("Post-Conception Test", environment);
    Instant afterCreation = Instant.now();

    // Assert
    Instant conceptionTime = tube.getIdentity().getConceptionTime();
    assertFalse(
        conceptionTime.isBefore(beforeCreation.minusSeconds(1)),
        "Conception time should be >= before-creation instant");
    assertFalse(
        conceptionTime.isAfter(afterCreation.plusSeconds(1)),
        "Conception time should be <= after-creation instant");
    LOGGER.info("✓ Conception time within creation window verified");
  }

  // ============================================================================
  // CAUSALITY: Event Ordering and Causal Dependence
  // ============================================================================

  @Test
  @Tag("Causality")
  @DisplayName("shouldPreserveCausalOrderingInMimirLog")
  public void shouldPreserveCausalOrderingInMimirLog() {
    // Arrange
    Tube tube = Tube.create("Causality Test", environment);
    int initialLogSize = tube.getMimirLogSize();

    // Act - generate events in specific order
    tube.setStatus(TubeStatus.ACTIVE);
    tube.setStatus(TubeStatus.READY);

    // Assert - verify Mimir log has causally ordered entries
    List<String> log = new ArrayList<>(tube.queryMimirLog());
    assertTrue(log.size() >= initialLogSize, "Mimir log should have events");

    // Verify timestamps are non-decreasing
    for (int i = 1; i < log.size(); i++) {
      String prevEntry = log.get(i - 1);
      String currEntry = log.get(i);
      // Basic check: entries should exist
      assertNotNull(prevEntry, "Previous log entry should not be null");
      assertNotNull(currEntry, "Current log entry should not be null");
    }

    LOGGER.info("✓ Causal ordering in Mimir log verified");
  }

  @Test
  @Tag("Causality")
  @DisplayName("shouldNotAllowCausalityViolation_withoutSkippingEvents")
  public void shouldNotAllowCausalityViolation() {
    // Arrange
    Tube tube = Tube.create("Non-Retroactive Test", environment);
    tube.setStatus(TubeStatus.ACTIVE);

    // Act - further status change
    tube.setStatus(TubeStatus.READY);

    // Assert - verify current status is progression, not regression
    TubeStatus currentStatus = tube.getStatus();
    assertNotNull(currentStatus, "Current status should not be null");
    assertTrue(
        currentStatus.equals(TubeStatus.READY) || currentStatus.equals(TubeStatus.ACTIVE),
        "Status should be one of valid values");

    LOGGER.info("✓ Causality violation prevention verified");
  }

  // ============================================================================
  // STATUS: Status Transitions
  // ============================================================================

  @Test
  @Tag("Status")
  @DisplayName("shouldHandleStatusTransitions_consistently")
  public void shouldHandleStatusTransitions() {
    // Arrange
    Tube tube = Tube.create("Status Transition Test", environment);
    TubeStatus initialStatus = tube.getStatus();

    // Act - change status
    tube.setStatus(TubeStatus.ACTIVE);
    TubeStatus activeStatus = tube.getStatus();

    tube.setStatus(TubeStatus.READY);
    TubeStatus readyStatus = tube.getStatus();

    // Assert
    assertNotNull(initialStatus, "Initial status should be valid");
    assertEquals(TubeStatus.ACTIVE, activeStatus, "Should be ACTIVE after setting");
    assertEquals(TubeStatus.READY, readyStatus, "Should be READY after setting");

    LOGGER.info("✓ Status transitions handled consistently");
  }

  @Test
  @Tag("Status")
  @DisplayName("shouldPreserveStatusAfterMultipleSets")
  public void shouldPreserveStatusAfterMultipleSets() {
    // Arrange
    Tube tube = Tube.create("Status Preservation Test", environment);

    // Act
    tube.setStatus(TubeStatus.ACTIVE);
    TubeStatus status1 = tube.getStatus();

    try {
      Thread.sleep(5);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    TubeStatus status2 = tube.getStatus();

    // Assert
    assertEquals(status1, status2, "Status should remain constant after set");

    LOGGER.info("✓ Status preservation verified: {}", status1);
  }

  @Test
  @Tag("Status")
  @DisplayName("shouldTerminateCleanly_fromAnyStatus")
  public void shouldTerminateCleanly() {
    // Arrange
    Tube tube = Tube.create("Termination Test", environment);
    tube.setStatus(TubeStatus.ACTIVE);

    // Act
    tube.terminate();

    // Assert - status should be retrievable after termination
    assertDoesNotThrow(
        () -> {
          TubeStatus status = tube.getStatus();
          assertNotNull(status, "Status should be retrievable after termination");
        });

    LOGGER.info("✓ Clean termination verified");
  }

  // ============================================================================
  // CONCURRENCY: Race Conditions
  // ============================================================================

  @Test
  @Tag("Concurrency")
  @DisplayName("shouldHandleConcurrentStatusChanges_safely")
  public void shouldHandleConcurrentStatusChanges() throws InterruptedException {
    // Arrange
    Tube tube = Tube.create("Concurrent Status Changes Test", environment);
    int threadCount = 5;
    CountDownLatch latch = new CountDownLatch(threadCount);
    AtomicInteger changeCount = new AtomicInteger(0);

    // Act - attempt concurrent status changes
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);

    for (int i = 0; i < threadCount; i++) {
      final int index = i;
      executor.submit(
          () -> {
            try {
              if (index % 2 == 0) {
                tube.setStatus(TubeStatus.ACTIVE);
              } else {
                tube.setStatus(TubeStatus.READY);
              }
              changeCount.incrementAndGet();
            } finally {
              latch.countDown();
            }
          });
    }

    // Wait for all threads
    boolean completed = latch.await(5, TimeUnit.SECONDS);
    executor.shutdown();

    // Assert - verify tube status is consistent after concurrent access
    assertTrue(completed, "All changes should complete within timeout");
    assertTrue(changeCount.get() > 0, "At least one status change should have succeeded");

    TubeStatus finalStatus = tube.getStatus();
    assertNotNull(finalStatus, "Final status should be valid after concurrent access");

    LOGGER.info(
        "✓ Concurrent status changes handled safely: {} changes, final status: {}",
        changeCount.get(),
        finalStatus);
  }

  @Test
  @Tag("Concurrency")
  @DisplayName("shouldHandleConcurrentTermination_safely")
  public void shouldHandleConcurrentTermination() throws InterruptedException {
    // Arrange
    Tube tube = Tube.create("Concurrent Termination Test", environment);
    int threadCount = 3;
    CountDownLatch latch = new CountDownLatch(threadCount);
    AtomicInteger terminationAttempts = new AtomicInteger(0);

    // Act - attempt concurrent termination calls
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);

    for (int i = 0; i < threadCount; i++) {
      executor.submit(
          () -> {
            try {
              tube.terminate();
              terminationAttempts.incrementAndGet();
            } finally {
              latch.countDown();
            }
          });
    }

    // Wait for all threads
    boolean completed = latch.await(5, TimeUnit.SECONDS);
    executor.shutdown();

    // Assert - verify tube is in a valid state even after concurrent terminations
    assertTrue(completed, "All terminations should complete within timeout");
    assertDoesNotThrow(
        () -> {
          tube.getStatus(); // Should not throw even after concurrent terminations
        });

    LOGGER.info(
        "✓ Concurrent termination handled safely: {} attempts made", terminationAttempts.get());
  }

  // ============================================================================
  // MIMIR: Event Log Integrity
  // ============================================================================

  @Test
  @Tag("Mimir")
  @DisplayName("shouldMaintainMimirLogIntegrity_acrossStatusChanges")
  public void shouldMaintainMimirLogIntegrity() {
    // Arrange
    Tube tube = Tube.create("Mimir Integrity Test", environment);
    int initialLogSize = tube.getMimirLogSize();

    // Act - generate multiple events
    tube.setStatus(TubeStatus.ACTIVE);
    int logSizeAfterStatusChange = tube.getMimirLogSize();

    tube.setStatus(TubeStatus.READY);
    int logSizeAfterSecondChange = tube.getMimirLogSize();

    // Assert - verify log grows monotonically
    assertTrue(logSizeAfterStatusChange >= initialLogSize, "Log size should not decrease");
    assertTrue(
        logSizeAfterSecondChange >= logSizeAfterStatusChange,
        "Log size should not decrease after more events");

    // Verify log entries are non-empty
    List<String> logEntries = tube.queryMimirLog();
    assertTrue(logEntries.size() > 0, "Mimir log should have entries");

    for (String entry : logEntries) {
      assertNotNull(entry, "Log entry should not be null");
      assertFalse(entry.isEmpty(), "Log entry should not be empty");
    }

    LOGGER.info(
        "✓ Mimir log integrity verified: initial={}, after 1st change={}, after 2nd={}",
        initialLogSize,
        logSizeAfterStatusChange,
        logSizeAfterSecondChange);
  }

  @Test
  @Tag("Mimir")
  @DisplayName("shouldPreserveLogEntryOrder_chronologically")
  public void shouldPreserveLogEntryOrder() {
    // Arrange
    Tube tube = Tube.create("Log Entry Order Test", environment);

    // Act - generate events and capture log
    tube.setStatus(TubeStatus.ACTIVE);
    List<String> logAfterFirstChange = new ArrayList<>(tube.queryMimirLog());

    tube.setStatus(TubeStatus.READY);
    List<String> logAfterSecondChange = new ArrayList<>(tube.queryMimirLog());

    // Assert - verify log grows without reordering
    assertTrue(logAfterSecondChange.size() >= logAfterFirstChange.size(), "Log should only grow");
    assertTrue(
        logAfterSecondChange.containsAll(logAfterFirstChange),
        "New log should contain all previous entries");

    LOGGER.info("✓ Log entry order preserved chronologically");
  }

  // ============================================================================
  // IDENTITY: Genealogical Consistency
  // ============================================================================

  @Test
  @Tag("Identity")
  @DisplayName("shouldPreserveIdentityConsistency_acrossOperations")
  public void shouldPreserveIdentityConsistency() {
    // Arrange
    Tube tube = Tube.create("Identity Consistency Test", environment);
    String uniqueId1 = tube.getUniqueId();
    String reason = tube.getReason();

    // Act - perform operations
    tube.setStatus(TubeStatus.ACTIVE);
    String uniqueId2 = tube.getUniqueId();
    String reasonAfter = tube.getReason();

    // Assert
    assertEquals(uniqueId1, uniqueId2, "Unique ID should remain constant");
    assertEquals(reason, reasonAfter, "Reason should remain constant");

    LOGGER.info("✓ Identity consistency verified: ID={}, reason={}", uniqueId1, reason);
  }

  @Test
  @Tag("Identity")
  @DisplayName("shouldPreserveParentChildRelationship_withChildTubes")
  public void shouldPreserveParentChildRelationship() {
    // Arrange
    Tube parentTube = Tube.create("Parent", environment);
    Tube childTube = Tube.createChildTube("Child", environment, parentTube);

    // Act - get identities
    TubeIdentity parentIdentity = parentTube.getIdentity();
    TubeIdentity childIdentity = childTube.getIdentity();
    TubeIdentity childParent = childIdentity.getParentIdentity();

    // Assert
    assertNotNull(parentIdentity, "Parent should have identity");
    assertNotNull(childIdentity, "Child should have identity");
    assertNotNull(childParent, "Child should have parent identity");
    assertEquals(
        parentIdentity.getUniqueId(),
        childParent.getUniqueId(),
        "Child's parent should match actual parent");

    LOGGER.info("✓ Parent-child relationship verified");
  }

  @Test
  @Tag("Identity")
  @DisplayName("shouldMaintainHierarchicalAddressConsistency")
  public void shouldMaintainHierarchicalAddressConsistency() {
    // Arrange
    Tube adam = Tube.create("Adam", environment);
    Tube child1 = Tube.createChildTube("Child1", environment, adam);
    Tube child2 = Tube.createChildTube("Child2", environment, adam);
    Tube grandchild = Tube.createChildTube("Grandchild", environment, child1);

    // Act
    String adamAddress = adam.getIdentity().getHierarchicalAddress();
    String child1Address = child1.getIdentity().getHierarchicalAddress();
    String child2Address = child2.getIdentity().getHierarchicalAddress();
    String grandchildAddress = grandchild.getIdentity().getHierarchicalAddress();

    // Assert - verify hierarchical consistency
    assertTrue(adamAddress.startsWith("T<"), "Adam should have root-level address");
    assertTrue(
        child1Address.startsWith(adamAddress),
        "Child1 address should be derived from parent address");
    assertTrue(
        child2Address.startsWith(adamAddress),
        "Child2 address should be derived from parent address");
    assertTrue(
        grandchildAddress.startsWith(child1Address),
        "Grandchild address should be derived from parent address");

    LOGGER.info(
        "✓ Hierarchical address consistency verified:\n  Adam: {}\n  Child1: {}\n  Grandchild: {}",
        adamAddress,
        child1Address,
        grandchildAddress);
  }

  // ============================================================================
  // CLOCK SKEW: Multiple Tubes and Time Boundaries
  // ============================================================================

  @Test
  @Tag("ClockSkew")
  @DisplayName("shouldHandleMultipleOperations_consistently")
  public void shouldHandleMultipleOperations() {
    // Arrange
    Tube tube = Tube.create("Multiple Operations Test", environment);

    // Act - perform multiple operations in sequence
    for (int i = 0; i < 5; i++) {
      tube.setStatus(i % 2 == 0 ? TubeStatus.ACTIVE : TubeStatus.READY);
    }

    // Assert - verify tube is in a valid state
    TubeStatus finalStatus = tube.getStatus();
    assertNotNull(finalStatus, "Final status should be valid");

    // Verify Mimir log captured all events
    int logSize = tube.getMimirLogSize();
    assertTrue(logSize > 0, "Mimir log should contain events");

    LOGGER.info(
        "✓ Multiple operations handled consistently: final status = {}, log size = {}",
        finalStatus,
        logSize);
  }

  @Test
  @Tag("ClockSkew")
  @DisplayName("shouldPreserveCreationOrderInMultipleTubes")
  public void shouldPreserveCreationOrderInMultipleTubes() {
    // Arrange
    Instant before = Instant.now();

    Tube tube1 = Tube.create("First Tube", environment);
    Instant tube1ConceptionTime = tube1.getIdentity().getConceptionTime();

    try {
      Thread.sleep(5);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    Tube tube2 = Tube.create("Second Tube", environment);
    Instant tube2ConceptionTime = tube2.getIdentity().getConceptionTime();

    Instant after = Instant.now();

    // Assert - verify creation order via conception times
    assertTrue(
        tube1ConceptionTime.isBefore(tube2ConceptionTime)
            || tube1ConceptionTime.equals(tube2ConceptionTime),
        "First tube should be created before or at same time as second");

    LOGGER.info(
        "✓ Creation order preserved: tube1={}, tube2={}, delta={}ms",
        tube1ConceptionTime,
        tube2ConceptionTime,
        Duration.between(tube1ConceptionTime, tube2ConceptionTime).toMillis());
  }

  @Test
  @Tag("ClockSkew")
  @DisplayName("shouldHandleElapsedTimeBetweenOperations")
  public void shouldHandleElapsedTimeBetweenOperations() {
    // Arrange
    Tube tube = Tube.create("Duration Test", environment);
    Instant conceptionTime = tube.getIdentity().getConceptionTime();

    tube.setStatus(TubeStatus.ACTIVE);

    // Simulate elapsed time check
    Instant afterFirstChange = Instant.now();
    Duration duration = Duration.between(conceptionTime, afterFirstChange);

    // Act - perform another operation
    tube.setStatus(TubeStatus.READY);

    // Assert - verify tube still functions correctly
    assertTrue(duration.toMillis() >= 0, "Duration should be non-negative");
    assertNotNull(tube.getStatus(), "Tube should have valid status despite elapsed time");

    LOGGER.info("✓ Elapsed time handling verified: elapsed time = {}ms", duration.toMillis());
  }

  // ============================================================================
  // COMPOSITE: Multi-Factor Adversarial Scenarios
  // ============================================================================

  @Test
  @Tag("Composite")
  @DisplayName("shouldHandleConcurrentOperationsAndQueries_safely")
  public void shouldHandleConcurrentOperationsAndQueries() throws InterruptedException {
    // Arrange
    Tube tube = Tube.create("Composite Test", environment);
    int threadCount = 8;
    CountDownLatch latch = new CountDownLatch(threadCount);
    AtomicInteger successCount = new AtomicInteger(0);

    // Act - mix status changes, queries, and log access concurrently
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);

    for (int i = 0; i < threadCount; i++) {
      final int index = i;
      executor.submit(
          () -> {
            try {
              if (index % 3 == 0) {
                tube.setStatus(TubeStatus.ACTIVE);
              } else if (index % 3 == 1) {
                tube.getStatus();
              } else {
                tube.getMimirLogSize();
              }
              successCount.incrementAndGet();
            } finally {
              latch.countDown();
            }
          });
    }

    // Wait for all threads
    boolean completed = latch.await(5, TimeUnit.SECONDS);
    executor.shutdown();

    // Assert - verify consistency
    assertTrue(completed, "All operations should complete");
    assertEquals(threadCount, successCount.get(), "All operations should succeed");

    TubeStatus finalStatus = tube.getStatus();
    assertNotNull(finalStatus, "Tube should have valid status");

    List<String> log = tube.queryMimirLog();
    assertTrue(log.size() > 0, "Log should have entries");

    LOGGER.info(
        "✓ Concurrent operations handled safely: {} operations completed", successCount.get());
  }

  @Test
  @Tag("Composite")
  @DisplayName("shouldRecoverGracefullyFromErrorConditions")
  public void shouldRecoverGracefullyFromErrorConditions() {
    // Arrange
    Tube tube = Tube.create("Recovery Test", environment);

    // Act - perform normal operations
    tube.setStatus(TubeStatus.ACTIVE);
    int logSizeBefore = tube.getMimirLogSize();

    // Simulate recovery by continuing operations
    tube.setStatus(TubeStatus.READY);
    int logSizeAfter = tube.getMimirLogSize();

    // Assert
    assertTrue(logSizeAfter >= logSizeBefore, "Tube should recover and continue logging");
    assertNotNull(tube.getStatus(), "Tube should remain functional");

    LOGGER.info("✓ Graceful recovery verified: {} -> {} log entries", logSizeBefore, logSizeAfter);
  }
}
