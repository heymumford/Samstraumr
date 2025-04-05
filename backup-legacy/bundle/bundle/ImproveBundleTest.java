/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

package org.samstraumr.tube.bundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.samstraumr.tube.Environment;
import org.samstraumr.tube.composite.Composite;
import org.samstraumr.tube.test.annotations.BundleTest;

/**
 * Bundle Tests focus on connected tubes forming bundles. These tests verify component interactions
 * and data flow through connected tubes in a bundle.
 */
@BundleTest
@ExtendWith(MockitoExtension.class)
public class ImproveBundleTest {

  private static final String BUNDLE_ID = "test-bundle";

  @Mock private Environment mockEnvironment;

  private Bundle bundle;

  @BeforeEach
  void setUp() {
    // Configure mock environment using lenient() to avoid unused stubbing issues
    lenient()
        .when(mockEnvironment.getParameters())
        .thenReturn("{\"hostname\":\"bundle-test-host\"}");
    lenient().when(mockEnvironment.getEnvironmentHash()).thenReturn("bundle-test-hash");

    // Create bundle for testing
    bundle = new Bundle(BUNDLE_ID, mockEnvironment);
  }

  @Test
  @DisplayName("Bundle should correctly connect tubes")
  void shouldCorrectlyConnectTubes() {
    // Given - Create tubes
    bundle
        .createTube("source", "Source Tube")
        .createTube("processor1", "First Processor")
        .createTube("processor2", "Second Processor")
        .createTube("sink", "Sink Tube");

    // When - Connect tubes in a chain
    bundle
        .connect("source", "processor1")
        .connect("processor1", "processor2")
        .connect("processor2", "sink");

    // Then - Verify connections
    Map<String, List<String>> connections = bundle.getConnections();

    assertEquals(3, connections.size());
    assertEquals("processor1", connections.get("source").get(0));
    assertEquals("processor2", connections.get("processor1").get(0));
    assertEquals("sink", connections.get("processor2").get(0));
  }

  @Test
  @DisplayName("Bundle should process data through connected tubes")
  void shouldProcessDataThroughConnectedTubes() {
    // Given - Create and connect tubes
    bundle
        .createTube("source", "Source Tube")
        .createTube("uppercase", "Uppercase Processor")
        .createTube("reverse", "Reverse Processor")
        .createTube("sink", "Sink Tube");

    bundle
        .connect("source", "uppercase")
        .connect("uppercase", "reverse")
        .connect("reverse", "sink");

    // Add transformers
    bundle.addTransformer("uppercase", (Function<String, String>) String::toUpperCase);
    bundle.addTransformer(
        "reverse", (Function<String, String>) s -> new StringBuilder(s).reverse().toString());

    // When - Process data through bundle
    String inputData = "hello world";
    Optional<String> result = bundle.process("source", inputData);

    // Then - Verify result
    assertTrue(result.isPresent());
    assertEquals("DLROW OLLEH", result.get()); // uppercase then reversed
  }

  @Test
  @DisplayName("Bundle should activate and deactivate correctly")
  void shouldActivateAndDeactivateCorrectly() {
    // Given - Create and connect tubes
    bundle.createTube("source", "Source Tube").createTube("sink", "Sink Tube");

    bundle.connect("source", "sink");

    // When/Then - Test initial state
    assertTrue(bundle.isActive());

    // When - Deactivate the bundle
    bundle.deactivate();

    // Then - Bundle should be inactive
    assertFalse(bundle.isActive());

    // When - Process data in inactive bundle
    Optional<String> result = bundle.process("source", "test data");

    // Then - Processing should fail in inactive bundle
    assertFalse(result.isPresent());

    // When - Reactivate the bundle
    bundle.activate();

    // Then - Bundle should be active again
    assertTrue(bundle.isActive());

    // When - Process data in active bundle
    result = bundle.process("source", "test data");

    // Then - Processing should succeed
    assertTrue(result.isPresent());
  }

  @Test
  @DisplayName("Bundle should handle validation failures")
  void shouldHandleValidationFailures() {
    // Given - Create tubes
    bundle
        .createTube("source", "Source Tube")
        .createTube("validator", "Validation Tube")
        .createTube("sink", "Sink Tube");

    bundle.connect("source", "validator").connect("validator", "sink");

    // Add a validator that only accepts strings longer than 5 characters
    bundle.addValidator("validator", (Function<String, Boolean>) s -> s.length() > 5);

    // When/Then - Test valid data
    Optional<String> validResult = bundle.process("source", "valid long data");
    assertTrue(validResult.isPresent());
    assertEquals("valid long data", validResult.get());

    // When/Then - Test invalid data
    Optional<String> invalidResult = bundle.process("source", "short");
    assertFalse(invalidResult.isPresent());
  }

  @Test
  @DisplayName("Bundle should track events correctly")
  void shouldTrackEventsCorrectly() {
    // Given - Create tube
    bundle.createTube("test", "Test Tube");

    // When - Generate some events
    bundle.logEvent("Test event 1");
    bundle.logEvent("Test event 2");
    bundle.process("test", "test data");

    // Then - Verify events were logged
    List<Bundle.BundleEvent> events = bundle.getEventLog();
    assertTrue(events.size() >= 4); // Init event + 2 test events + processing events

    // Verify event content
    assertTrue(events.stream().anyMatch(e -> e.getDescription().contains("Test event 1")));
    assertTrue(events.stream().anyMatch(e -> e.getDescription().contains("Test event 2")));
  }

  @Test
  @DisplayName("Bundle circuit breaker should open after failures")
  void circuitBreakerShouldOpenAfterFailures() {
    // Given - Create tube with circuit breaker
    bundle.createTube("processor", "Processor Tube");
    bundle.enableCircuitBreaker("processor", 2, 1000);

    // Add a transformer that fails for specific inputs
    bundle.addTransformer(
        "processor",
        (Function<String, String>)
            input -> {
              if (input.contains("fail")) {
                throw new RuntimeException("Simulated failure");
              }
              return input.toUpperCase();
            });

    // When/Then - First successful processing
    Optional<String> successResult = bundle.process("processor", "success");
    assertTrue(successResult.isPresent());
    assertEquals("SUCCESS", successResult.get());

    // When - Trigger failures to trip the circuit breaker
    bundle.process("processor", "fail1");
    bundle.process("processor", "fail2");

    // Then - Get the delegate composite circuit breakers
    Map<String, Composite.CircuitBreaker> circuitBreakers = bundle.getCircuitBreakers();

    // Verify the circuit breaker is open
    Composite.CircuitBreaker processorBreaker = circuitBreakers.get("processor");
    assertTrue(processorBreaker.isOpen());

    // When - Try processing valid data with open circuit
    Optional<String> rejectedResult = bundle.process("processor", "should be rejected");

    // Then - Should be rejected due to open circuit
    assertFalse(rejectedResult.isPresent());

    // When - Wait for circuit breaker to reset (mock reset by directly resetting)
    processorBreaker.reset();

    // Then - Circuit should be closed
    assertFalse(processorBreaker.isOpen());

    // When - Process again after reset
    Optional<String> afterResetResult = bundle.process("processor", "after reset");

    // Then - Should succeed
    assertTrue(afterResetResult.isPresent());
    assertEquals("AFTER RESET", afterResetResult.get());
  }

  @Test
  @DisplayName("Bundle should handle parallel processing")
  void shouldHandleParallelProcessing() throws InterruptedException {
    // Given - Create tubes
    bundle.createTube("source", "Source Tube").createTube("sink", "Sink Tube");

    bundle.connect("source", "sink");

    // Add a processor with a delay to simulate work
    bundle.addTransformer(
        "sink",
        (Function<String, String>)
            input -> {
              try {
                Thread.sleep(100); // Simulate processing work
                return input.toUpperCase();
              } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Processing interrupted", e);
              }
            });

    // When - Process multiple inputs in parallel
    final int THREAD_COUNT = 10;
    CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

    for (int i = 0; i < THREAD_COUNT; i++) {
      final int index = i;
      new Thread(
              () -> {
                try {
                  bundle.process("source", "thread-" + index);
                } finally {
                  latch.countDown();
                }
              })
          .start();
    }

    // Then - All threads should complete without exceptions
    boolean allCompleted = latch.await(5, TimeUnit.SECONDS);
    assertTrue(allCompleted, "All parallel processing threads should complete");

    // Verify events were logged for all threads
    List<Bundle.BundleEvent> events = bundle.getEventLog();
    assertTrue(
        events.size() >= THREAD_COUNT * 2); // At least start and complete events for each thread
  }
}
